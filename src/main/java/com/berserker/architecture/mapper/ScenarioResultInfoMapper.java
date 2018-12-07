package com.berserker.architecture.mapper;

import com.berserker.architecture.domain.entity.ScenarioResultInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ScenarioResultInfoMapper {
    @Select("SELECT * FROM scenarioresultinfo WHERE id = #{id}")
    ScenarioResultInfo getScenarioResultInfo(@Param("id") Integer id);

    @Select("SELECT * FROM scenarioresultinfo WHERE scenarioId = #{scenarioId} ORDER BY runTime DESC")
    List<ScenarioResultInfo> getScenarioResultInfoListByScenarioId(@Param("scenarioId") Integer scenarioId);

    @Select("SELECT * FROM scenarioresultinfo ORDER BY runTime DESC")
    List<ScenarioResultInfo> getScenarioResultInfoList();

    @Insert("INSERT INTO scenarioresultinfo(scenarioName, numThreads, rampUp, duration, runTime, scenarioId)" +
            "VALUES(#{scenarioName}, #{numThreads}, #{rampUp}, #{duration}, #{runTime}, #{scenarioId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertScenarioResultInfo(ScenarioResultInfo scenarioResultInfo);

    @Delete("DELETE FROM scenarioresultinfo WHERE id = #{id}")
    int deleteScenarioResultInfo(@Param("id") Integer id);

    @Delete("DELETE FROM scenarioresultinfo WHERE scenarioId = #{scenarioId}")
    int deleteScenarioResultInfoByScenarioId(@Param("scenarioId") Integer scenarioId);
}
