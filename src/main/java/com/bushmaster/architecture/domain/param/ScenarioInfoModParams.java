package com.bushmaster.architecture.domain.param;

import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class ScenarioInfoModParams {
    @Pattern(regexp = "^\\+?[1-9][0-9]*$", message = "场景ID必须是非零正整数")
    private String scenarioId;
    @NotBlank(message = "测试场景名称不能为空")
    private String scenarioName;
    private String scenarioDescription;
//    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}\\s[0-9]{2}:[0-9]{2}:[0-9]{2}$", message="场景创建时间格式不正确")
//    private String createTime;
    private String numThreads;
    private String rampUp;
    private String duration;
    private String status;

    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }

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

//    public String getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(String createTime) {
//        this.createTime = createTime;
//    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
