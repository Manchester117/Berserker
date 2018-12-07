package com.berserker.architecture.engine.reader;

import com.berserker.architecture.utils.ScriptUtil;
import com.berserker.architecture.domain.entity.ScenarioInfo;
import com.berserker.architecture.mapper.ScenarioInfoMapper;
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

    /**
     * @description         场景信息的读取(ID,场景名称,测试计划,并发数量,攀升时间,持续时间)
     * @param scenarioId    场景ID
     * @return              返回场景的Map<String, Object>结构
     */
    public Map<String, Object> testPlanReader(Integer scenarioId) {
        ScenarioInfo scenarioInfo = scenarioInfoMapper.getScenarioInfo(scenarioId);
        File scriptFileObject = scriptUtil.getScriptFileByScenarioId(scenarioId);

        Map<String, Object> scenarioRunInfo = new HashMap<>();
        scenarioRunInfo.put("scenarioId", scenarioInfo.getId());
        scenarioRunInfo.put("scenarioName", scenarioInfo.getScenarioName());
        scenarioRunInfo.put("scriptFile", scriptFileObject);
        scenarioRunInfo.put("scriptAbsolutePath", scriptFileObject.getAbsolutePath());
        scenarioRunInfo.put("numThreads", scenarioInfo.getNumThreads());
        scenarioRunInfo.put("rampUp", scenarioInfo.getRampUp());
        scenarioRunInfo.put("duration", scenarioInfo.getDuration());

        return scenarioRunInfo;
    }
}
