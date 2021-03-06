package com.berserker.architecture.service.impl;

import com.berserker.architecture.mapper.SampleResultInfoMapper;
import com.berserker.architecture.domain.entity.ScenarioResultInfo;
import com.berserker.architecture.mapper.ScenarioResultInfoMapper;
import com.berserker.architecture.service.ScenarioResultService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScenarioResultServiceImpl implements ScenarioResultService{
    @Autowired
    private ScenarioResultInfoMapper resultMapper;
    @Autowired
    private SampleResultInfoMapper sampleMapper;

    @Override
    public ScenarioResultInfo getScenarioResultInfo(Integer id) {
        ScenarioResultInfo scenarioResultInfo = resultMapper.getScenarioResultInfo(id);
        if (Objects.nonNull(scenarioResultInfo))
            return scenarioResultInfo;
        else
            return null;
    }

    @Override
    public Map<String, Object> getScenarioResultInfoByScenarioId(Integer offset, Integer limit, Integer scenarioId) {
        Map<String, Object> result = new HashMap<>();
        Page<Object> page = PageHelper.offsetPage(offset, limit);
        List<ScenarioResultInfo> scenarioResultInfoList = resultMapper.getScenarioResultInfoListByScenarioId(scenarioId);
        result.put("total", page.getTotal());
        result.put("rows", scenarioResultInfoList);
        return result;
    }

    @Override
    public Map<String, Object> getScenarioResultInfoList(Integer offset, Integer limit) {
        Map<String, Object> result = new HashMap<>();
        Page<Object> page = PageHelper.offsetPage(offset, limit);
        List<ScenarioResultInfo> scenarioResultInfoList = resultMapper.getScenarioResultInfoList();
        result.put("total", page.getTotal());
        result.put("rows", scenarioResultInfoList);
        return result;
    }

    @Override
    public Map<String, Object> addScenarioResultInfo(ScenarioResultInfo scenarioResultInfo) {
        Map<String, Object> result = new HashMap<>();
        int insertFlag = resultMapper.insertScenarioResultInfo(scenarioResultInfo);
        if (insertFlag > 0) {
            result.put("status", "Success");
            result.put("message", "结果集建立成功");
            result.put("resultId", scenarioResultInfo.getId());
        } else {
            result.put("status", "Fail");
            result.put("message", "结果集建立失败");
        }
        return result;
    }

    @Override
    public Map<String, Object> delScenarioResultInfoByResultId(Integer resultId) {
        Map<String, Object> result = new HashMap<>();
        int deleteSampleResultFlag = sampleMapper.deleteSampleResultInfoByResultId(resultId);
        if (deleteSampleResultFlag >= 0) {
            int deleteFlag = resultMapper.deleteScenarioResultInfo(resultId);
            if (deleteFlag >= 0) {
                result.put("status", "Success");
                result.put("message", "结果集删除成功");
            } else {
                result.put("status", "Fail");
                result.put("message", "结果集删除失败");
            }
        } else {
            result.put("status", "Fail");
            result.put("message", "结果详情删除失败");
        }
        return result;
    }

    @Override
    public Map<String, Object> delScenarioResultInfoByScenarioId(Integer scenarioId) {
        Integer successCount = 0;
        Integer failCount = 0;
        Map<String, Object> result = new HashMap<>();
        List<ScenarioResultInfo> ScenarioResultInfoList = resultMapper.getScenarioResultInfoListByScenarioId(scenarioId);
        for (ScenarioResultInfo resultInfo: ScenarioResultInfoList) {
            Map<String, Object> delScenarioResult = delScenarioResultInfoByResultId(resultInfo.getId());
            String status = delScenarioResult.get("status").toString();
            if (Objects.equals(status, "Success")) {
                result.put("successCount", ++successCount);
            } else {
                result.put("failCount", ++failCount);
            }
        }
        return result;
    }
}
