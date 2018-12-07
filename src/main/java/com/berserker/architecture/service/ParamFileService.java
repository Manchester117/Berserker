package com.berserker.architecture.service;

import com.berserker.architecture.domain.entity.ParamFileInfo;

import java.util.List;
import java.util.Map;

public interface ParamFileService {
    ParamFileInfo getParamFileInfo(Integer id);

    List<ParamFileInfo> getParamFileInfoListByScenarioId(Integer scenarioId);

    Map<String, Object> addParamFileInfo(Integer scenarioId, List<Map<String, Object>> csvDataParamFileList);

    Map<String, Object> delParamFileInfo(ParamFileInfo paramFileInfo);

    List<Map<String, Object>> delParamFileInfoByScenarioId(Integer scenarioId);
}
