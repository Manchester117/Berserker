package com.bushmaster.architecture.service;

import com.bushmaster.architecture.domain.entity.ScenarioInfo;
import com.bushmaster.architecture.domain.entity.ScriptFileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ScenarioInfoService {
    ScenarioInfo getScenarioInfo(Integer id);

    Map<String, Object> getScenarioInfoByPageList(Integer offset, Integer limit, String scenarioName, Boolean status);

    Map<String, Object> addScenarioInfo(ScenarioInfo scenarioInfo, MultipartFile scriptFile);

    ScriptFileInfo getScriptFileInfoByScenarioId(Integer scenarioId);

    List<Map<String, Object>> getCsvDataSetSlotList(Integer scenarioId);

    Map<String, Object> getScriptDataStructure(Integer scenarioId);

    Map<String, Object> addScenarioParamFiles(Integer scenarioId, List<Map<String, Object>> csvDataParamFileList);

    Map<String, Object> modScenarioInfo(ScenarioInfo scenarioInfo, MultipartFile scriptFile);

    Map<String, Object> delScenarioInfo(Integer id);
}
