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

    @Select("SELECT * FROM sampleresultinfo WHERE resultId = #{resultId} ORDER BY timeStamp DESC")
    List<SampleResultInfo> getSampleResultInfoListByResultId(Integer resultId);

    @InsertProvider(type = SampleResultInfoProvider.class, method = "insertBatchSampleResult")
    void insertSampleResultInfoList(@Param("sampleResultInfoList") List<SampleResultInfo> SampleResultInfoList);

    @Delete("DELETE FROM sampleresultinfo WHERE resultId = #{resultId}")
    int deleteSampleResultInfoByResultId(Integer resultId);

    @Select("SELECT samplerLabel FROM sampleresultinfo WHERE resultId = #{resultId} GROUP BY samplerLabel")
    List<String> getSamplerLabelByResultId(Integer resultId);

    @Select("SELECT * FROM sampleresultinfo WHERE resultId = #{resultId} AND samplerLabel = #{samplerLabel} ORDER BY id DESC LIMIT 1")
    SampleResultInfo getSampleResultData(@Param("resultId") Integer resultId, @Param("samplerLabel") String samplerLabel);
}
