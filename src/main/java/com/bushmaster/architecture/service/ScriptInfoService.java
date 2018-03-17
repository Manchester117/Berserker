package com.bushmaster.architecture.service;

import com.bushmaster.architecture.domain.entity.ScriptFileInfo;
import org.apache.jorphan.collections.HashTree;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ScriptInfoService {
    ScriptFileInfo getScriptFileInfo(Integer id);

    HashTree getTestPlanTreeByScenarioId(Integer scenarioId);

    ScriptFileInfo getScriptFileInfoByScenarioId(Integer scenarioId);

    Map<String, Object> getScriptDataStructure(Integer scenarioId);

    List<Map<String, Object>> getCsvDataSetSlotList(Integer scenarioId);

    Map<String, Object> addScriptInfo(MultipartFile scriptFile, Integer addScenarioId);

    Map<String, Object> modScriptInfo(MultipartFile scriptFileUpload, Integer modScenarioId);

    Map<String, Object> delScriptInfo(ScriptFileInfo scriptFileInfo);

    Map<String, Object> delScriptInfoByScenarioId(Integer scenarioId);
}
