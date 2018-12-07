package com.berserker.architecture.mapper;

import com.berserker.architecture.domain.entity.ParamFileInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ParamFileInfoMapper {
    @Select("SELECT COUNT(*) FROM paramfileinfo")
    Integer getParamFileInfoCount();

    @Select("SELECT * FROM paramfileinfo WHERE id = #{id}")
    ParamFileInfo getParamFileInfo(@Param("id") Integer id);

    @Select("SELECT * FROM paramfileinfo WHERE scenarioId = #{scenarioId}")
    List<ParamFileInfo> getParamFileInfoListByScenarioId(@Param("scenarioId") Integer scenarioId);

    @Insert("INSERT INTO paramfileinfo(paramFileName, paramFilePath, uploadTime, scenarioId)" +
            "VALUES(#{paramFileName}, #{paramFilePath}, #{uploadTime}, #{scenarioId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertParamFileInfo(ParamFileInfo paramFileInfo);

    @Update("UPDATE paramfileinfo " +
            "SET paramFileName = #{paramFileName}, paramFilePath = #{paramFilePath}, uploadTime = #{uploadTime}, scenarioId = #{scenarioId}" +
            "WHERE id = #{id}")
    int modifyParamFileInfo(ParamFileInfo paramFileInfo);

    @Delete("DELETE FROM paramfileinfo WHERE id = #{id}")
    int deleteParamFileInfo(@Param("id") Integer id);

    @Delete("DELETE FROM paramfileinfo WHERE scenarioId = #{scenarioId}")
    int deleteParamFileInfoByScenarioId(@Param("scenarioId") Integer scenarioId);
}
