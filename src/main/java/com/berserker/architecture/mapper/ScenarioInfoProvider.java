package com.berserker.architecture.mapper;

import com.berserker.architecture.domain.entity.ScenarioInfo;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class ScenarioInfoProvider {
    public String selectScenarioByCondition(Map<String, Object> params) {
        return new SQL() {
            StringBuffer conditionScenarioName = new StringBuffer();
            StringBuffer conditionStatus = new StringBuffer();
            String scenarioNameSQL = null;
            {
                if (Objects.nonNull(params.get("scenarioName"))) {
                    String scenarioName = params.get("scenarioName").toString();
                    if (StringUtils.isNotEmpty(scenarioName)) {
                        conditionScenarioName.append("scenarioName LIKE '%");
                        conditionScenarioName.append(params.get("scenarioName"));
                        conditionScenarioName.append("%' ");
                    }
                }
                scenarioNameSQL = conditionScenarioName.toString();

                // 拼凑SQL语句
                SELECT("id, scenarioName, scenarioDescription, createTime, numThreads, rampUp, duration");
                FROM("scenarioInfo");
                if (!Strings.isNullOrEmpty(scenarioNameSQL))
                    WHERE(scenarioNameSQL);
                ORDER_BY("createTime");
            }
        }.toString();
    }

    private SQL assembleColumnSQL(SQL sqlColumn, ScenarioInfo scenarioInfo, String statementCTRL) {
        StringBuilder column = null;
        for (Field field : scenarioInfo.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (Objects.nonNull(field.get(scenarioInfo)) && StringUtils.isNotEmpty(field.get(scenarioInfo).toString())) {
                    column = new StringBuilder();
                    Object columnValue = field.get(scenarioInfo);
                    if (Objects.equals(statementCTRL, "INSERT")) {
                        if (columnValue instanceof Integer)
                            column.append(((Integer) columnValue).toString());
                        else if (columnValue instanceof Date)
                            column.append("'").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) columnValue)).append("'");
                        else if (columnValue instanceof String)
                            column.append("'").append(columnValue).append("'");
                        sqlColumn.VALUES(field.getName(), column.toString());
                    }
                    if (Objects.equals(statementCTRL, "UPDATE")) {
                        if (columnValue instanceof Integer)
                            column.append(field.getName()).append(" = ").append(columnValue);
                        else if (columnValue instanceof Date)
                            column.append(field.getName()).append(" = '").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) columnValue)).append("'");
                        else if (columnValue instanceof String)
                            column.append(field.getName()).append(" = '").append(columnValue).append("'");
                        sqlColumn.SET(column.toString());
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return sqlColumn;
    }

    public String insertScenario(ScenarioInfo scenarioInfo) {
        return new SQL() {
            {
                INSERT_INTO("scenarioInfo");
                assembleColumnSQL(this, scenarioInfo, "INSERT");
            }
        }.toString();
    }

    public String updateScenario(ScenarioInfo scenarioInfo) {
        return new SQL() {
            {
                UPDATE("scenarioInfo");
                assembleColumnSQL(this, scenarioInfo, "UPDATE");
                if (Objects.nonNull(scenarioInfo.getId()) && StringUtils.isNotEmpty(scenarioInfo.getId().toString()))
                    WHERE("id = " + scenarioInfo.getId().toString());
            }
        }.toString();
    }

    public String deleteScenario(Integer id) {
        return new SQL() {
            {
                DELETE_FROM("scenarioInfo");
                WHERE("id = " + id);
            }
        }.toString();
    }
}
