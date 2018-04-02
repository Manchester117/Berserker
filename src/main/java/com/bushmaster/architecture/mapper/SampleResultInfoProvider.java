package com.bushmaster.architecture.mapper;

import com.bushmaster.architecture.domain.entity.SampleResultInfo;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class SampleResultInfoProvider {
    @SuppressWarnings("unchecked")
    public String insertBatchSampleResult(Map<String, List<SampleResultInfo>> params) {
        List<SampleResultInfo> sampleResultInfoList = params.get("sampleResultInfoList");
        MessageFormat messageFormat = new MessageFormat(
            "(#'{'sampleResultInfoList[{0}].timeStamp'}', " +
                    "#'{'sampleResultInfoList[{0}].samplerLabel'}', " +
                    "#'{'sampleResultInfoList[{0}].meanTime'}', " +
                    "#'{'sampleResultInfoList[{0}].minTime'}', " +
                    "#'{'sampleResultInfoList[{0}].maxTime'}', " +
                    "#'{'sampleResultInfoList[{0}].standardDeviation'}', " +
                    "#'{'sampleResultInfoList[{0}].errorPercentage'}', " +
                    "#'{'sampleResultInfoList[{0}].requestRate'}', " +
                    "#'{'sampleResultInfoList[{0}].receiveKBPerSecond'}', " +
                    "#'{'sampleResultInfoList[{0}].sentKBPerSecond'}', " +
                    "#'{'sampleResultInfoList[{0}].avgPageBytes'}', " +
                    "#'{'sampleResultInfoList[{0}].threadCount'}', " +
                    "#'{'sampleResultInfoList[{0}].resultId'}')"
        );
        StringBuilder batchInsertSQL = new StringBuilder();
        batchInsertSQL.append("INSERT INTO sampleresultinfo");
        batchInsertSQL.append("(timeStamp, samplerLabel, meanTime, minTime, maxTime, standardDeviation, errorPercentage, requestRate, receiveKBPerSecond, sentKBPerSecond, avgPageBytes, threadCount, resultId) ");
        batchInsertSQL.append("VALUES");
        for (int index = 0; index < sampleResultInfoList.size(); ++index) {
            // 这里有一个坑,不能直接使用new Object[]{index},否则MessageFormat会将下标以科学计数法的方式输出,导致DB无法存储
            batchInsertSQL.append(messageFormat.format(new Object[]{String.valueOf(index)}));
            if (index < sampleResultInfoList.size() - 1)
                batchInsertSQL.append(",");
        }
        return batchInsertSQL.toString();
    }
}
