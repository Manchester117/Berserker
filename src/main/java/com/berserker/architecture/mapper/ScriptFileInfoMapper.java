package com.berserker.architecture.mapper;

import com.berserker.architecture.domain.entity.ScriptFileInfo;
import org.apache.ibatis.annotations.*;

public interface ScriptFileInfoMapper {
    @Select("SELECT COUNT(*) FROM scriptfileinfo")
    Integer getScriptFileInfoCount();

    @Select("SELECT * FROM scriptfileinfo WHERE id = #{id}")
    ScriptFileInfo getScriptFileInfo(@Param("id") Integer id);

    @Select("SELECT * FROM scriptfileinfo WHERE scenarioId = #{scenarioId}")
    ScriptFileInfo getScriptFileInfoByScenarioId(@Param("scenarioId") Integer scenarioId);

    @Insert("INSERT INTO scriptfileinfo(scriptFileName, scriptFilePath, uploadTime, scenarioId)" +
            "VALUES(#{scriptFileName}, #{scriptFilePath}, #{uploadTime}, #{scenarioId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertScriptFileInfo(ScriptFileInfo scriptFileInfo);

    @Update("UPDATE scriptfileinfo " +
            "SET scriptFileName = #{scriptFileName}, scriptFilePath = #{scriptFilePath}, uploadTime = #{uploadTime}, scenarioId = #{scenarioId} " +
            "WHERE id = #{id}")
    int modifyScriptFileInfo(ScriptFileInfo scriptFileInfo);

    @Delete("DELETE FROM scriptfileinfo WHERE id = #{id}")
    int deleteScriptFileInfo(@Param("id") Integer id);

    @Delete("DELETE FROM scriptfileinfo WHERE scenarioId = #{scenarioId}")
    int deleteScriptFileInfoByScenarioId(@Param("scenarioId") Integer scenarioId);
}
