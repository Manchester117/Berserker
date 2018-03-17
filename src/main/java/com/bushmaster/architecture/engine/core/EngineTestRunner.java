package com.bushmaster.architecture.engine.core;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jorphan.collections.HashTree;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EngineTestRunner {
    public void scenarioRun(StandardJMeterEngine engine, HashTree testPlanTree) {
        if (Objects.nonNull(testPlanTree)) {
            engine.configure(testPlanTree);         // 载入测试计划
            engine.run();                           // 运行测试
        }
    }
}
