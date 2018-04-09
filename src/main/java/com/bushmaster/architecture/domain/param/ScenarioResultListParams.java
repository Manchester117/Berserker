package com.bushmaster.architecture.domain.param;

import org.hibernate.validator.constraints.NotEmpty;

public class ScenarioResultListParams {
    @NotEmpty(message="分页页码不能为空")
    private String offset;
    @NotEmpty(message="记录个数不能为空")
    private String limit;

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
}
