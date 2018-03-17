package com.bushmaster.architecture.engine.reader;

import com.bushmaster.architecture.domain.entity.ScenarioInfo;
import com.bushmaster.architecture.mapper.ScenarioInfoMapper;
import com.bushmaster.architecture.utils.ScriptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Component
public class EngineScenarioReader {
    @Autowired
    private ScenarioInfoMapper scenarioInfoMapper;
    @Autowired
    private ScriptUtil scriptUtil;

    public Map<String, Object> testPlanReader(Integer scenarioId) {
        ScenarioInfo scenarioInfo = scenarioInfoMapper.getScenarioInfo(scenarioId);
        File scriptFileObject = scriptUtil.getScriptFileByScenarioId(scenarioId);

        Map<String, Object> scenarioRunInfo = new HashMap<>();
        scenarioRunInfo.put("scriptFile", scriptFileObject);
        scenarioRunInfo.put("numThreads", scenarioInfo.getNumThreads());
        scenarioRunInfo.put("rampUp", scenarioInfo.getRampUp());
        scenarioRunInfo.put("duration", scenarioInfo.getDuration());

        return scenarioRunInfo;
    }
}
