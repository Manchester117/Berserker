package com.bushmaster.architecture.engine.core;

import com.bushmaster.architecture.engine.reader.EngineScenarioReader;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jorphan.collections.HashTree;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EngineController {
    @Autowired
    private EngineParamLoader paramLoader;
    @Autowired
    private EngineScenarioReader scenarioReader;
    @Autowired
    private EngineResultHandler resultHandler;
    @Autowired
    private EngineTestPlanSetter testPlanSetter;
    @Autowired
    private EngineTestRunner testRunner;

    private StandardJMeterEngine engine = new StandardJMeterEngine();

    private Integer runningScenarioId;

    private String runningScenarioName;

    private Integer runningResultId;

    private BoundListOperations<String, String> runningSampleResultList;

    public StandardJMeterEngine getEngine() {
        return engine;
    }

    public Boolean getEngineStatus() {
        return engine.isActive();
    }

    public Integer getRunningScenarioId() {
        return runningScenarioId;
    }

    public String getRunningScenarioName(){
        return runningScenarioName;
    }

    public Integer getRunningResultId() {
        return runningResultId;
    }

    public void setRunningResultId(Integer runningResultId) {
        this.runningResultId = runningResultId;
    }

    public void setRunningSampleResultList(BoundListOperations<String, String> runningSampleResultList) {
        this.runningSampleResultList = runningSampleResultList;
    }

    public BoundListOperations<String, String> getRunningSampleResultList() {
        return runningSampleResultList;
    }

    public Map<String, Object> stopEngine() {
        Map<String, Object> stopRunResult = new HashMap<>();
        if (engine.isActive()) {
            engine.stopTest(Boolean.TRUE);
            stopRunResult.put("status", "True");
            stopRunResult.put("message", "场景成功停止运行");
        } else {
            stopRunResult.put("status", "False");
            stopRunResult.put("message", "没有场景处于运行状态");
        }
        return stopRunResult;
    }

    /**
     * @description             场景运行器
     * @param scenarioId        场景ID
     */
    public void engineScenarioRunner(Integer scenarioId) {
        // 设置引擎参数
        paramLoader.setEngineParam();
        // 添加场景参数,获得新的测试计划树
        Map<String, Object> scenarioRunInfo = scenarioReader.testPlanReader(scenarioId);
        // 获取运行场景ID,为检查是否有场景运行做准备
        runningScenarioId = Integer.parseInt(scenarioRunInfo.get("scenarioId").toString());
        // 获取运行场景名称,为检查是否有场景运行做准备
        runningScenarioName = scenarioRunInfo.get("scenarioName").toString();
        // 添加场景的结果收集
        List<ResultCollector> resultCollectorList = resultHandler.resultCollect(scenarioId);
        HashTree testPlanTree = testPlanSetter.testPlanSetting(scenarioRunInfo, resultCollectorList);
        // 运行测试
        testRunner.scenarioRun(engine, testPlanTree);
        // 运行完场景之后,重置结果收集器
        resultHandler.clearCalculator();
        // 运行完场景之后,将SamplerResultList赋给EngineController中的runningSampleResultList
        runningSampleResultList = resultHandler.getRunningSampleResultList();
    }

    public void engineScenarioRealOuter() {
        resultHandler.resultRealTimeOuter();
    }
}
