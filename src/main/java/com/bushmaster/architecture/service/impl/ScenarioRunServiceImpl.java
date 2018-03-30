package com.bushmaster.architecture.service.impl;

import com.bushmaster.architecture.domain.entity.ScenarioInfo;
import com.bushmaster.architecture.domain.entity.ScenarioResultInfo;
import com.bushmaster.architecture.engine.core.EngineController;
import com.bushmaster.architecture.service.ScenarioInfoService;
import com.bushmaster.architecture.service.ScenarioResultService;
import com.bushmaster.architecture.service.ScenarioRunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class ScenarioRunServiceImpl implements ScenarioRunService{
    @Autowired
    private EngineController engineController;
    @Autowired
    private ScenarioInfoService scenarioInfoService;
    @Autowired
    private ScenarioResultService scenarioResultService;

    @Async
    @Override
    public void scenarioStartRun(Integer scenarioId) {
        // 启动场景前先获取场景信息
        ScenarioInfo scenarioInfo = scenarioInfoService.getScenarioInfo(scenarioId);
        // 初始化结果信息
        ScenarioResultInfo scenarioResultInfo = new ScenarioResultInfo();
        scenarioResultInfo.setRunTime(new Date());
        scenarioResultInfo.setNumThreads(scenarioInfo.getNumThreads());
        scenarioResultInfo.setRampUp(scenarioInfo.getRampUp());
        scenarioResultInfo.setDuration(scenarioInfo.getDuration());
        scenarioResultInfo.setScenarioId(scenarioInfo.getId());
        // 在库中添加结果信息
        Map<String, Object> addResult = scenarioResultService.addScenarioResultInfo(scenarioResultInfo);
        // 保留结果ID,将结果ID放置到EngineController中,方便后面的结果写入DB
        Integer resultId = Integer.parseInt(addResult.get("resultId").toString());
        engineController.setRunningResultId(resultId);
        // 启动场景测试
        engineController.engineScenarioRunner(scenarioId);
    }

    @Override
    public Map<String, Object> scenarioStopRun() {
        return engineController.stopEngine();
    }

    @Override
    public Boolean getEngineIsActive() {
        return engineController.getEngineStatus();
    }

    @Override
    public Integer getRunningScenarioId() {
        return engineController.getRunningScenarioId();
    }

    @Override
    public String getRunningScenarioName() {
        return engineController.getRunningScenarioName();
    }

    @Async
    @Override
    public void scenarioSampleResultRealOuter() {
        engineController.engineScenarioRealOuter();
    }
}
