package com.berserker.architecture.domain.entity;

import java.util.Date;

public class ParamFileInfo {
    private Integer id;
    private String paramFileName;
    private String paramFilePath;
    private Date uploadTime;
    private Integer scenarioId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getParamFileName() {
        return paramFileName;
    }

    public void setParamFileName(String paramFileName) {
        this.paramFileName = paramFileName;
    }

    public String getParamFilePath() {
        return paramFilePath;
    }

    public void setParamFilePath(String paramFilePath) {
        this.paramFilePath = paramFilePath;
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
        return "ParamFileInfo{" +
                "id=" + id +
                ", paramFileName='" + paramFileName + '\'' +
                ", paramFilePath='" + paramFilePath + '\'' +
                ", uploadTime=" + uploadTime +
                ", scenarioId=" + scenarioId +
                '}';
    }
}
