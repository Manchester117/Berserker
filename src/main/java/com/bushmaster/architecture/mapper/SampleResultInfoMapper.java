package com.bushmaster.architecture.mapper;

import com.bushmaster.architecture.domain.entity.SampleResultInfo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SampleResultInfoMapper {
    @Select("SELECT * FROM sampleresultinfo WHERE id = #{id}")
    SampleResultInfo getSampleResultInfo(Integer id);

    @Select("SELECT * FROM sampleresultinfo WHERE resultId = #{resultId} ORDER BY timeStamp")
    List<SampleResultInfo> getSampleResultInfoListByResultId(Integer resultId);

//    @Insert("INSERT INTO sampleresultinfo(timeStamp, samplerLabel, meanTime, minTime, maxTime, standardDeviation, errorPercentage, requestRate, receiveKBPerSecond, sentKBPerSecond, avgPageBytes, resultId)" +
//            "VALUES(#{timeStamp}, #{samplerLabel}, #{meanTime}, #{minTime}, #{maxTime}, #{standardDeviation}, #{errorPercentage}, #{requestRate}, #{receiveKBPerSecond}, #{sentKBPerSecond}, #{avgPageBytes}, #{resultId})")
    @InsertProvider(type = SampleResultInfoProvider.class, method = "insertBatchSampleResult")
    int insertSampleResultInfoList(@Param("sampleResultInfoList") List<SampleResultInfo> SampleResultInfoList);

    @Delete("DELETE FROM sampleresultinfo WHERE resultId = #{resultId}")
    int deleteSampleResultInfoByResultId(Integer resultId);
}
