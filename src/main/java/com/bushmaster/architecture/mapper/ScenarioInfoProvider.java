package com.bushmaster.architecture.mapper;

import com.bushmaster.architecture.domain.entity.ScenarioInfo;
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
            String statusSQL = null;
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
                if (Objects.nonNull(params.get("status"))) {
                    String status = params.get("status").toString();
                    if (StringUtils.isNotEmpty(status)) {
                        conditionStatus.append(" status = ");
                        conditionStatus.append(params.get("status"));
                    }
                }
                statusSQL = conditionStatus.toString();

                // 拼凑SQL语句
                SELECT("id, scenarioName, scenarioDescription, createTime, numThreads, rampUp, duration, status");
                FROM("scenarioInfo");
                if (!Strings.isNullOrEmpty(scenarioNameSQL) && Strings.isNullOrEmpty(statusSQL)) {
                    WHERE(scenarioNameSQL);
                } else if (Strings.isNullOrEmpty(scenarioNameSQL) && !Strings.isNullOrEmpty(statusSQL)) {
                    WHERE(statusSQL);
                } else if (!Strings.isNullOrEmpty(scenarioNameSQL) && !Strings.isNullOrEmpty(statusSQL)) {
                    WHERE(scenarioNameSQL, statusSQL);
                }
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
                        else if (columnValue instanceof Boolean) {
                            if (Objects.equals(columnValue, Boolean.TRUE))
                                column.append(1);
                            else
                                column.append(0);
                        }
                        sqlColumn.VALUES(field.getName(), column.toString());
                    }
                    if (Objects.equals(statementCTRL, "UPDATE")) {
                        if (columnValue instanceof Integer)
                            column.append(field.getName()).append(" = ").append(columnValue);
                        else if (columnValue instanceof Date)
                            column.append(field.getName()).append(" = '").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) columnValue)).append("'");
                        else if (columnValue instanceof String)
                            column.append(field.getName()).append(" = '").append(columnValue).append("'");
                        else if (columnValue instanceof Boolean)
                            if (Objects.equals(columnValue, Boolean.TRUE))
                                column.append(field.getName()).append(" = ").append(1);
                            else
                                column.append(field.getName()).append(" = ").append(0);
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
