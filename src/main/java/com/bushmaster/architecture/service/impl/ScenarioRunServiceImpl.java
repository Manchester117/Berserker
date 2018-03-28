package com.bushmaster.architecture.service.impl;

import com.bushmaster.architecture.engine.core.EngineController;
import com.bushmaster.architecture.service.ScenarioRunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ScenarioRunServiceImpl implements ScenarioRunService{
    @Autowired
    private EngineController engineController;

    @Async
    @Override
    public void scenarioStartRun(Integer scenarioId) {
        engineController.engineScenarioRunner(scenarioId);
    }

    @Override
    public Map<String, Object> scenarioStopRun() {
        return engineController.stopEngine();
    }

    @Override
    public Boolean getEngineIsActive() {
        return engineController.getEngineStatus();
    }

    @Override
    public Integer getRunningScenarioId() {
        return engineController.getRunningScenarioId();
    }

    @Override
    public String getRunningScenarioName() {
        return engineController.getRunningScenarioName();
    }

    @Async
    @Override
    public void scenarioSampleResultRealOuter() {
        engineController.engineScenarioRealOuter();
    }
}
