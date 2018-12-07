package com.berserker.architecture.domain.entity;

import java.util.Date;

public class ScriptFileInfo {
    private Integer id;
    private String scriptFileName;
    private String scriptFilePath;
    private Date uploadTime;
    private Integer scenarioId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getScriptFileName() {
        return scriptFileName;
    }

    public void setScriptFileName(String scriptFileName) {
        this.scriptFileName = scriptFileName;
    }

    public String getScriptFilePath() {
        return scriptFilePath;
    }

    public void setScriptFilePath(String scriptFilePath) {
        this.scriptFilePath = scriptFilePath;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Integer getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(Integer scenarioId) {
        this.scenarioId = scenarioId;
    }

    @Override
    public String toString() {
        return "ScriptFileInfo{" +
                "id=" + id +
                ", scriptFileName='" + scriptFileName + '\'' +
                ", scriptFilePath='" + scriptFilePath + '\'' +
                ", uploadTime=" + uploadTime +
                ", scenarioId=" + scenarioId +
                '}';
    }
}
