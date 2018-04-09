package com.bushmaster.architecture.service.impl;

import com.bushmaster.architecture.domain.entity.ScenarioInfo;
import com.bushmaster.architecture.domain.entity.ScenarioResultInfo;
import com.bushmaster.architecture.engine.core.EngineController;
import com.bushmaster.architecture.engine.core.EngineResultHandler;
import com.bushmaster.architecture.service.SampleResultService;
import com.bushmaster.architecture.service.ScenarioInfoService;
import com.bushmaster.architecture.service.ScenarioResultService;
import com.bushmaster.architecture.service.ScenarioRunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Service
public class ScenarioRunServiceImpl implements ScenarioRunService{
    @Autowired
    private EngineController engineController;
    @Autowired
    private EngineResultHandler resultHandler;
    @Autowired
    private ScenarioInfoService scenarioInfoService;
    @Autowired
    private ScenarioResultService scenarioResultService;
    @Autowired
    private SampleResultService sampleResultService;

    /**
     * @description             启动场景测试的方法,在启动测试后生成一条测试结果记录,存入ScenarioResultInfo表中.
     * @param scenarioId        场景ID
     */
    @Async
    @Override
    public void scenarioStartRun(Integer scenarioId) {
        // 启动场景前先获取场景信息
        ScenarioInfo scenarioInfo = scenarioInfoService.getScenarioInfo(scenarioId);
        // 初始化结果信息
        ScenarioResultInfo scenarioResultInfo = new ScenarioResultInfo();
        scenarioResultInfo.setRunTime(new Date());
        scenarioResultInfo.setNumThreads(scenarioInfo.getNumThreads());
        scenarioResultInfo.setRampUp(scenarioInfo.getRampUp());
        scenarioResultInfo.setDuration(scenarioInfo.getDuration());
        scenarioResultInfo.setScenarioName(scenarioInfo.getScenarioName());
        scenarioResultInfo.setScenarioId(scenarioInfo.getId());
        // 在库中添加结果信息
        Map<String, Object> addResult = scenarioResultService.addScenarioResultInfo(scenarioResultInfo);
        // 保留结果ID,将结果ID放置到EngineController中,方便后面的结果写入DB
        Integer resultId = Integer.parseInt(addResult.get("resultId").toString());
        // 在EngineController中保存结果ID
        engineController.setRunningResultId(resultId);
        // 启动场景测试
        engineController.engineScenarioRunner(scenarioId);
        // 获取运行完成之后SampleResultList
        BoundListOperations<String, String> runningSampleResultList = engineController.getRunningSampleResultList();
        // 将SamplerResultList中的内容写入到DB中
        sampleResultService.addSampleResultToDB(resultId, runningSampleResultList);
    }

    /**
     * @description             手动终止场景
     * @return                  返回终止信息
     */
    @Override
    public Map<String, Object> scenarioStopRun() {
        // 停止场景运行
        Map<String, Object> result = engineController.stopEngine();
        if (Objects.equals(result.get("status"), "True")) {
            // 清空当前的计数器
            resultHandler.clearCalculator();
            // 获取当前的SampleResultList,交给EngineController中的runningSampleResultList.
            // 由于场景是手动中止的,不能直接从EngineController中直接获取,要从EngineResultHandler中获取.
            // 从EngineResultHandler中获取的是已经存入数据队列
            engineController.setRunningSampleResultList(resultHandler.getRunningSampleResultList());
            // 获取运行完成之后SampleResultList
            BoundListOperations<String, String> runningSampleResultList = engineController.getRunningSampleResultList();
            // 从EngineController中获取结果ID
            Integer resultId = engineController.getRunningResultId();
            // 将SamplerResultList中的内容写入到DB中
            sampleResultService.addSampleResultToDB(resultId, runningSampleResultList);
        }
        return result;
    }

    /**
     * @description             获取当前测试状态
     * @return                  是否正在运行
     */
    @Override
    public Boolean getEngineIsActive() {
        return engineController.getEngineStatus();
    }

    /**
     * @description             获取当前场景ID
     * @return                  场景ID
     */
    @Override
    public Integer getRunningScenarioId() {
        return engineController.getRunningScenarioId();
    }

    /**
     * @description             获取当前场景的名称
     * @return                  场景名称
     */
    @Override
    public String getRunningScenarioName() {
        return engineController.getRunningScenarioName();
    }

    /**
     * @description             SampleResult的实时输出
     */
    @Override
    public void scenarioSampleResultRealOuter() {
        engineController.engineScenarioRealOuter();
    }
}
