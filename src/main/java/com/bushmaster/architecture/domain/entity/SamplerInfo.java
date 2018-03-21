package com.bushmaster.architecture.domain.entity;

public class SamplerInfo {
    private Number meanTime;
    private Long minTime;
    private Long maxTime;
    private int samplerCount;
    private Double requestRate;
    private Double sentBytesPerSecond;
    private Double totalBytesPerSecond;
    private Double errorPercentage;
    private Integer threadCount;

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

    public int getSamplerCount() {
        return samplerCount;
    }

    public void setSamplerCount(int samplerCount) {
        this.samplerCount = samplerCount;
    }

    public Double getRequestRate() {
        return requestRate;
    }

    public void setRequestRate(Double requestRate) {
        this.requestRate = requestRate;
    }

    public Double getSentBytesPerSecond() {
        return sentBytesPerSecond;
    }

    public void setSentBytesPerSecond(Double sentBytesPerSecond) {
        this.sentBytesPerSecond = sentBytesPerSecond;
    }

    public Double getTotalBytesPerSecond() {
        return totalBytesPerSecond;
    }

    public void setTotalBytesPerSecond(Double totalBytesPerSecond) {
        this.totalBytesPerSecond = totalBytesPerSecond;
    }

    public Double getErrorPercentage() {
        return errorPercentage;
    }

    public void setErrorPercentage(Double errorPercentage) {
        this.errorPercentage = errorPercentage;
    }

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public String toString() {
        return "SamplerInfo{" +
                "meanTime=" + meanTime +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                ", samplerCount=" + samplerCount +
                ", requestRate=" + requestRate +
                ", totalBytesPerSecond=" + totalBytesPerSecond +
                ", errorPercentage=" + errorPercentage +
                ", threadCount=" + threadCount +
                '}';
    }
}
