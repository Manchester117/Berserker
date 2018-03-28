package com.bushmaster.architecture.service;

import java.util.Map;

public interface ScenarioRunService {
    void scenarioStartRun(Integer scenarioId);

    Map<String, Object> scenarioStopRun();

    Boolean getEngineIsActive();

    Integer getRunningScenarioId();

    String getRunningScenarioName();

    void scenarioSampleResultRealOuter();
}
