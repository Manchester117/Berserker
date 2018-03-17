package com.bushmaster.architecture.engine.core;

import com.bushmaster.architecture.engine.reader.EngineScenarioReader;
import com.bushmaster.architecture.service.ScriptInfoService;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jorphan.collections.HashTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class EngineController {
    @Autowired
    private ScriptInfoService scriptInfoService;
    @Autowired
    private EngineListener listener;
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

    public void engineScenarioRunner(Integer scenarioId) {
        // 建立引擎的监听
        listener.engineControlListener(engine);
        // 设置引擎参数
        paramLoader.setEngineParam();
        // 添加场景参数
        Map<String, Object> scenarioRunInfo = scenarioReader.testPlanReader(scenarioId);
        List<ResultCollector> resultCollectorList = resultHandler.resultCollect();
        testPlanSetter.testPlanSetting(scenarioRunInfo, resultCollectorList);
        // 通过场景ID获取测试计划的HashTree
        HashTree testPlanTree = scriptInfoService.getTestPlanTreeByScenarioId(scenarioId);
        // 运行测试
        testRunner.scenarioRun(engine, testPlanTree);
    }
}
