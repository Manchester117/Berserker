package com.berserker.architecture.engine.core;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jorphan.collections.HashTree;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EngineTestRunner {
    /**
     * @description             场景运行方法
     * @param engine            Jmeter引擎
     * @param testPlanTree      测试计划的HashTree
     */
    public void scenarioRun(StandardJMeterEngine engine, HashTree testPlanTree) {
        if (Objects.nonNull(testPlanTree)) {
            engine.configure(testPlanTree);         // 载入测试计划
            engine.run();                           // 运行测试
        }
    }
}
