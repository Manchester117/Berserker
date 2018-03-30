package com.bushmaster.architecture.domain.param;

import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class ScenarioResultListParams {
    @NotEmpty(message="分页页码不能为空")
    private String offset;
    @NotEmpty(message="记录个数不能为空")
    private String limit;
    @Pattern(regexp = "^\\+?[1-9][0-9]*$", message = "场景ID必须是非零正整数")
    private String scenarioId;

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
    }
}
