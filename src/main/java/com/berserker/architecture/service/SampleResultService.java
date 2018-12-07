package com.berserker.architecture.service;

import com.berserker.architecture.domain.entity.SampleResultInfo;
import org.springframework.data.redis.core.BoundListOperations;

import java.util.List;
import java.util.Map;

public interface SampleResultService {
    SampleResultInfo getSampleResultInfo(Integer id);

    Map<String, List<List<Number>>> getSampleResultDataListByResultId(Integer resultId, String dataType);

    List<SampleResultInfo> getSampleResultData(Integer resultId);

    void addSampleResultToDB(Integer runningResultId, BoundListOperations<String, String> runningSampleResultList);

    Map<String, Object> delSampleResultInfoByResultId(Integer resultId);
}
