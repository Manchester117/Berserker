package com.bushmaster.architecture.service;

import com.alibaba.fastjson.JSONObject;
import com.bushmaster.architecture.domain.entity.SampleResultInfo;
import org.springframework.data.redis.core.BoundListOperations;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface SampleResultService {
    SampleResultInfo getSampleResultInfo(Integer id);

    Map<String, List<Map<Timestamp, Object>>> getSampleResultDataListByResultId(Integer resultId, String dataType);

//    JSONObject getSampleResultDataListByResultId(Integer resultId, String dataType);

    void addSampleResultToDB(Integer runningResultId, BoundListOperations<String, String> runningSampleResultList);

    Map<String, Object> delSampleResultInfoByResultId(Integer resultId);
}
