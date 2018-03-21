package com.bushmaster.architecture.engine.core;

import com.bushmaster.architecture.engine.reader.EngineScenarioReader;
import com.bushmaster.architecture.utils.FileStorageUtil;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jorphan.collections.HashTree;
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


    private static final EngineController INSTANCE = new EngineController();
    private StandardJMeterEngine engine = new StandardJMeterEngine();

    private EngineController() {}

    public StandardJMeterEngine getEngine() {
        return engine;
    }

    public static EngineController getInstance() {
        return INSTANCE;
    }

    public Boolean getEngineStatus() {
        return engine.isActive();
    }

    public Map<String, Object> stopEngine() {
        Map<String, Object> stopRunResult = new HashMap<>();
        if (engine.isActive()) {
            engine.stopTest(Boolean.TRUE);
            stopRunResult.put("info", "Success");
            stopRunResult.put("message", "场景成功停止运行");
        } else {
            stopRunResult.put("info", "Info");
            stopRunResult.put("message", "没有场景处于运行状态");
        }
        return stopRunResult;
    }

    public void engineScenarioRunner(Integer scenarioId) {
        // 设置引擎参数
        paramLoader.setEngineParam();
        // 添加场景参数,获得新的测试计划树
        Map<String, Object> scenarioRunInfo = scenarioReader.testPlanReader(scenarioId);
        List<ResultCollector> resultCollectorList = resultHandler.resultCollect();
        HashTree testPlanTree = testPlanSetter.testPlanSetting(scenarioRunInfo, resultCollectorList);
        // 运行测试
        testRunner.scenarioRun(engine, testPlanTree);
    }
}
