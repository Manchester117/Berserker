package com.bushmaster.architecture.engine;

import org.apache.jmeter.engine.StandardJMeterEngine;

public class EngineController {
    private static final EngineController INSTANCE = new EngineController();
    private StandardJMeterEngine engine = new StandardJMeterEngine();

    private EngineController() {}

    public static EngineController getInstance() {
        return INSTANCE;
    }

    public void engineListenerPerform() {

    }

    public void engineParamLoaderPerform() {

    }

    public void engineResultCollectorPerform() {

    }

    public void engineScriptSetterPerform() {

    }

    public void engineScenarioRunner() {

    }
}
