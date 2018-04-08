package com.bushmaster.architecture.service.impl;

import com.bushmaster.architecture.domain.entity.ScenarioInfo;
import com.bushmaster.architecture.domain.entity.ScenarioResultInfo;
import com.bushmaster.architecture.engine.core.EngineController;
import com.bushmaster.architecture.service.SampleResultService;
import com.bushmaster.architecture.service.ScenarioInfoService;
import com.bushmaster.architecture.service.ScenarioResultService;
import com.bushmaster.architecture.service.ScenarioRunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
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
    @Autowired
    private SampleResultService sampleResultService;

    /**
     * @description             启动场景测试的方法,在启动测试后生成一条测试结果记录,存入ScenarioResultInfo表中.
     * @param scenarioId        场景ID
     */
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
        scenarioResultInfo.setScenarioName(scenarioInfo.getScenarioName());
        scenarioResultInfo.setScenarioId(scenarioInfo.getId());
        // 在库中添加结果信息
        Map<String, Object> addResult = scenarioResultService.addScenarioResultInfo(scenarioResultInfo);
        // 保留结果ID,将结果ID放置到EngineController中,方便后面的结果写入DB
        Integer resultId = Integer.parseInt(addResult.get("resultId").toString());
        // 启动场景测试
        engineController.engineScenarioRunner(scenarioId);
        // 获取运行完成之后SampleResultList
        BoundListOperations<String, String> runningSampleResultList = engineController.getRunningSampleResultList();
        // 将SamplerResultList中的内容写入到DB中
        sampleResultService.addSampleResultToDB(resultId, runningSampleResultList);
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

    @Override
    public void scenarioSampleResultRealOuter() {
        engineController.engineScenarioRealOuter();
    }
}
