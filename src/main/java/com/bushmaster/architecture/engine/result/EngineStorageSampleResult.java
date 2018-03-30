package com.bushmaster.architecture.engine.result;

import com.alibaba.fastjson.JSONObject;
import com.bushmaster.architecture.domain.entity.SampleResultInfo;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.stereotype.Component;

@Component
public class EngineStorageSampleResult {

    public void SampleResultToDB(Integer runningResultId, BoundListOperations<String, String> runningSampleResultList) {
        while (runningSampleResultList.size() > 0) {
            // 从Redis中获取每个SampleResult的字符串
            String sampleResultString = runningSampleResultList.leftPop();
            // 将获取的JSON字符串转换成SampleResult对象
            SampleResultInfo sampleResult = JSONObject.parseObject(sampleResultString, SampleResultInfo.class);
        }
    }
}
