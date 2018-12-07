package com.berserker.architecture.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class SampleResultInfo implements Serializable {
    private Timestamp timeStamp;
    private String samplerLabel;
    private Long samplerCount;
    private Double meanTime;
    private Long minTime;
    private Long maxTime;
    private Double standardDeviation;
    private Double errorPercentage;
    private Double requestRate;
    private Double receiveKBPerSecond;
    private Double sentKBPerSecond;
    private Double avgPageBytes;
    private Integer threadCount;
    private Integer resultId;

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSamplerLabel() {
        return samplerLabel;
    }

    public void setSamplerLabel(String samplerLabel) {
        this.samplerLabel = samplerLabel;
    }

    public Long getSamplerCount() {
        return samplerCount;
    }

    public void setSamplerCount(Long samplerCount) {
        this.samplerCount = samplerCount;
    }

    public Double getMeanTime() {
        return meanTime;
    }

    public void setMeanTime(Double meanTime) {
        if (Double.isFinite(meanTime)) {
            BigDecimal bigDecimal = new BigDecimal(meanTime.toString());
            this.meanTime = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else {
            this.meanTime = 0.0;
        }
    }

    public Long getMinTime() {
        return minTime;
    }

    public void setMinTime(Long minTime) {
        this.minTime = minTime;
    }

    public Long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Long maxTime) {
        this.maxTime = maxTime;
    }

    public Double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(Double standardDeviation) {
        if (Double.isFinite(standardDeviation)) {
            BigDecimal bigDecimal = new BigDecimal(standardDeviation.toString());
            this.standardDeviation = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();        // 保留2位小数
        } else {
            this.standardDeviation = 0.0;
        }
    }

    public Double getErrorPercentage() {
        return errorPercentage;
    }

    public void setErrorPercentage(Double errorPercentage) {
        this.errorPercentage = errorPercentage;
    }

    public Double getRequestRate() {
        return requestRate;
    }

    public void setRequestRate(Double requestRate) {
        if (Double.isFinite(requestRate)) {                                     // 这里使用了isFinite方法,判断是有效数据,避免计数器返回无限大的数据,导致数据解析失败.
            BigDecimal bigDecimal = new BigDecimal(requestRate.toString());
            this.requestRate = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else {
            this.requestRate = 0.0;
        }
    }

    public Double getReceiveKBPerSecond() {
        return receiveKBPerSecond;
    }

    public void setReceiveKBPerSecond(Double receiveKBPerSecond) {
        if (Double.isFinite(receiveKBPerSecond)) {
            BigDecimal bigDecimal = new BigDecimal(receiveKBPerSecond);
            this.receiveKBPerSecond = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else {
            this.receiveKBPerSecond = 0.0;
        }
    }

    public Double getSentKBPerSecond() {
        return sentKBPerSecond;
    }

    public void setSentKBPerSecond(Double sentKBPerSecond) {
        if (Double.isFinite(sentKBPerSecond)) {
            BigDecimal bigDecimal = new BigDecimal(sentKBPerSecond.toString());
            this.sentKBPerSecond = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else {
            this.sentKBPerSecond = 0.0;
        }
    }

    public Double getAvgPageBytes() {
        return avgPageBytes;
    }

    public void setAvgPageBytes(Double avgPageBytes) {
        if (Double.isFinite(avgPageBytes)) {
            BigDecimal bigDecimal = new BigDecimal(avgPageBytes.toString());
            this.avgPageBytes = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else {
            this.avgPageBytes = 0.0;
        }
    }

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    public Integer getResultId() {
        return resultId;
    }

    public void setResultId(Integer resultId) {
        this.resultId = resultId;
    }

    @Override
    public String toString() {
        return "SampleResultInfo{" +
                "timeStamp=" + timeStamp +
                ", samplerLabel='" + samplerLabel + '\'' +
                ", samplerCount=" + samplerCount +
                ", meanTime=" + meanTime +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                ", standardDeviation=" + standardDeviation +
                ", errorPercentage=" + errorPercentage +
                ", requestRate=" + requestRate +
                ", receiveKBPerSecond=" + receiveKBPerSecond +
                ", sentKBPerSecond=" + sentKBPerSecond +
                ", avgPageBytes=" + avgPageBytes +
                ", threadCount=" + threadCount +
                ", resultId=" + resultId +
                '}';
    }
}
