package com.berserker.architecture.domain.param;

import org.hibernate.validator.constraints.NotBlank;

public class ScenarioInfoAddParams {
    @NotBlank(message = "测试场景名称不能为空")
    private String scenarioName;
    private String numThreads;
    private String rampUp;
    private String duration;
    private String scenarioDescription;

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public String getScenarioDescription() {
        return scenarioDescription;
    }

    public void setScenarioDescription(String scenarioDescription) {
        this.scenarioDescription = scenarioDescription;
    }

    public String getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(String numThreads) {
        this.numThreads = numThreads;
    }

    public String getRampUp() {
        return rampUp;
    }

    public void setRampUp(String rampUp) {
        this.rampUp = rampUp;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
