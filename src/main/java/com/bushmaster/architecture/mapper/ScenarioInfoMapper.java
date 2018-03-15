package com.bushmaster.architecture.mapper;

import com.bushmaster.architecture.domain.entity.ScenarioInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ScenarioInfoMapper {
    @Select("SELECT COUNT(*) FROM scenarioInfo")
    Integer getScenarioCount();

    @Select("SELECT * FROM scenarioInfo WHERE id = #{id}")
    ScenarioInfo getScenarioInfo(@Param("id") Integer id);

    @SelectProvider(type = ScenarioInfoProvider.class, method = "selectScenarioByCondition")
    List<ScenarioInfo> getScenarioInfoList(@Param("scenarioName") String scenarioName, @Param("status") Boolean status);

    @InsertProvider(type = ScenarioInfoProvider.class, method = "insertScenario")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertScenarioInfo(ScenarioInfo scenarioInfo);

    @UpdateProvider(type = ScenarioInfoProvider.class, method = "updateScenario")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int updateScenarioInfo(ScenarioInfo scenarioInfo);

    @DeleteProvider(type = ScenarioInfoProvider.class, method = "deleteScenario")
    int deleteScenarioInfo(Integer id);
}
