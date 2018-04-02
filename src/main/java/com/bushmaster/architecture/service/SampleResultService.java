package com.bushmaster.architecture.service;

import com.bushmaster.architecture.domain.entity.SampleResultInfo;
import org.springframework.data.redis.core.BoundListOperations;

import java.util.Map;

public interface SampleResultService {
    SampleResultInfo getSampleResultInfo(Integer id);

    Map<String, Object> getSampleResultInfoListByResultId(Integer offset, Integer limit, Integer resultId);

    void addSampleResultToDB(Integer runningResultId, BoundListOperations<String, String> runningSampleResultList);

    Map<String, Object> delSampleResultInfoByResultId(Integer resultId);
}
