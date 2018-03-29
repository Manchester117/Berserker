package com.bushmaster.architecture.domain.entity;

import java.util.Date;

public class ScenarioResultInfo {
    private Integer id;
    private Integer numThreads;
    private Integer rampUp;
    private Integer duration;
    private Date runTime;
    private Integer scenarioId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(Integer numThreads) {
        this.numThreads = numThreads;
    }

    public Integer getRampUp() {
        return rampUp;
    }

    public void setRampUp(Integer rampUp) {
        this.rampUp = rampUp;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Date getRunTime() {
        return runTime;
    }

    public void setRunTime(Date runTime) {
        this.runTime = runTime;
    }

    public Integer getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Integer scenarioId) {
        this.scenarioId = scenarioId;
    }

    @Override
    public String toString() {
        return "ScenarioResultInfo{" +
                "id=" + id +
                ", numThreads=" + numThreads +
                ", rampUp=" + rampUp +
                ", duration=" + duration +
                ", runTime=" + runTime +
                ", scenarioId=" + scenarioId +
                '}';
    }
}
