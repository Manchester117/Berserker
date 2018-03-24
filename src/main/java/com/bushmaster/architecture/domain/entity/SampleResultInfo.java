package com.bushmaster.architecture.domain.entity;

import java.io.Serializable;

public class SampleResultInfo implements Serializable {
    private Long timeStamp;
    private int samplerCount;
    private Number meanTime;
    private Long minTime;
    private Long maxTime;
    private Double standardDeviation;
    private Double errorPercentage;
    private Double requestRate;
    private Double receiveKBPerSecond;
    private Double sentKBPerSecond;
    private Double avgPageBytes;
    private Integer threadCount;

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getSamplerCount() {
        return samplerCount;
    }

    public void setSamplerCount(int samplerCount) {
        this.samplerCount = samplerCount;
    }

    public Number getMeanTime() {
        return meanTime;
    }

    public void setMeanTime(Number meanTime) {
        this.meanTime = meanTime;
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
        this.standardDeviation = standardDeviation;
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
        this.requestRate = requestRate;
    }

    public Double getReceiveKBPerSecond() {
        return receiveKBPerSecond;
    }

    public void setReceiveKBPerSecond(Double receiveKBPerSecond) {
        this.receiveKBPerSecond = receiveKBPerSecond;
    }

    public Double getSentKBPerSecond() {
        return sentKBPerSecond;
    }

    public void setSentKBPerSecond(Double sentKBPerSecond) {
        this.sentKBPerSecond = sentKBPerSecond;
    }

    public Double getAvgPageBytes() {
        return avgPageBytes;
    }

    public void setAvgPageBytes(Double avgPageBytes) {
        this.avgPageBytes = avgPageBytes;
    }

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public String toString() {
        return "SampleResultInfo{" +
                "timeStamp=" + timeStamp +
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
                '}';
    }
}
