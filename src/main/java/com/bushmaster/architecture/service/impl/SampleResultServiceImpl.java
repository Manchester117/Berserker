package com.bushmaster.architecture.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bushmaster.architecture.domain.entity.SampleResultInfo;
import com.bushmaster.architecture.mapper.SampleResultInfoMapper;
import com.bushmaster.architecture.service.SampleResultService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SampleResultServiceImpl implements SampleResultService{
    @Autowired
    private SampleResultInfoMapper sampleMapper;

    @Override
    public SampleResultInfo getSampleResultInfo(Integer id) {
        SampleResultInfo sampleResultInfo = sampleMapper.getSampleResultInfo(id);
        if (Objects.nonNull(sampleResultInfo))
            return sampleResultInfo;
        else
            return null;
    }

    @Override
    public Map<String, Object> getSampleResultInfoListByResultId(Integer offset, Integer limit, Integer resultId) {
        Map<String, Object> result = new HashMap<>();
        Page<Object> page = PageHelper.offsetPage(offset, limit);
        List<SampleResultInfo> sampleResultInfoList = sampleMapper.getSampleResultInfoListByResultId(resultId);
        result.put("total", page.getTotal());
        result.put("rows", sampleResultInfoList);
        return result;
    }

    @Async
    @Override
    public void addSampleResultToDB(Integer runningResultId, BoundListOperations<String, String> runningSampleResultList) {
        List<SampleResultInfo> sampleResultInfoList = new ArrayList<>();
        while (runningSampleResultList.size() > 0) {
            // 从Redis中获取每个SampleResult的字符串
            String sampleResultString = runningSampleResultList.leftPop();
            // 将获取的JSON字符串转换成SampleResult对象
            SampleResultInfo sampleResultInfo = JSONObject.parseObject(sampleResultString, SampleResultInfo.class);
            sampleResultInfo.setResultId(runningResultId);
            sampleResultInfoList.add(sampleResultInfo);
        }
        // 将采样结果写入到DB
        Integer resultNum = sampleMapper.insertSampleResultInfoList(sampleResultInfoList);
        System.out.println("XTX: " + resultNum);
    }

    @Override
    public Map<String, Object> delSampleResultInfoByResultId(Integer resultId) {
        Map<String, Object> result = new HashMap<>();
        Integer deleteFlag = sampleMapper.deleteSampleResultInfoByResultId(resultId);
        if (deleteFlag > 0) {
            result.put("status", "Success");
            result.put("message", "采样结果删除成功");
        } else {
            result.put("status", "Fail");
            result.put("message", "采样结果删除失败");
        }
        return result;
    }
}
