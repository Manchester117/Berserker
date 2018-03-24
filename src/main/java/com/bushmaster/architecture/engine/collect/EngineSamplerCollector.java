package com.bushmaster.architecture.engine.collect;

import com.alibaba.fastjson.JSON;
import com.bushmaster.architecture.domain.entity.SampleResultInfo;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.util.Calculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;;


public class EngineSamplerCollector extends ResultCollector{
    private static final Logger log = LoggerFactory.getLogger(EngineSamplerCollector.class);
    private static volatile Calculator calculator = new Calculator();   // 性能计数器

    private StringRedisTemplate stringRedisTemplate;                    // 将SampleResult存入Redis的模板
    private String nowTime;                                             // 将SampleResult存入缓存的时间戳字符串

    public EngineSamplerCollector(Summariser summer, StringRedisTemplate stringRedisTemplate) {
        super(summer);
        this.stringRedisTemplate = stringRedisTemplate;
        this.nowTime = String.valueOf(System.currentTimeMillis());      // 使用当前时间
    }

    @Override
    public void sampleOccurred(SampleEvent event) {
        super.sampleOccurred(event);
        SampleResult result = event.getResult();

        calculator.addSample(result);

//        log.info("Sampler Label: " + result.getSampleLabel() +
//                "\t\tSampler Count: " + calculator.getCount() +
//                "\t\tResponse Mean Time: " + calculator.getMeanAsNumber() +
//                "\t\tResponse Min Time: " + calculator.getMin() +
//                "\t\tResponse Max Time: " + calculator.getMax() +
//                "\t\tResponse Standard Deviation: " + calculator.getStandardDeviation() +
//                "\t\tError Percentage: " + calculator.getErrorPercentage() +
//                "\t\tThroughput: " + calculator.getRate() +
//                "\t\tThroughput bytes Per Second: " + calculator.getKBPerSecond() +
//                "\t\tSent Throughput KB Per Second: " + calculator.getSentKBPerSecond() +
//                "\t\tAvg Page Bytes: " + calculator.getAvgPageBytes() +
//                "\t\tThread Count: " + result.getAllThreads()
//        );

        SampleResultInfo sampleResultInfo = new SampleResultInfo();
        sampleResultInfo.setTimeStamp(result.getTimeStamp());                            // 时间戳
        sampleResultInfo.setSamplerCount(calculator.getCount());                         // SamplerCount
        sampleResultInfo.setMeanTime(calculator.getMeanAsNumber());                      // 平均响应时间
        sampleResultInfo.setMinTime(calculator.getMin());                                // 最小响应时间
        sampleResultInfo.setMaxTime(calculator.getMax());                                // 最大响应时间
        sampleResultInfo.setStandardDeviation(calculator.getStandardDeviation());        // 标准方差
        sampleResultInfo.setErrorPercentage(calculator.getErrorPercentage());            // 错误率
        sampleResultInfo.setRequestRate(calculator.getRate());                           // 每秒请求处理能力
        sampleResultInfo.setReceiveKBPerSecond(calculator.getKBPerSecond());             // 每秒Receive的数据量
        sampleResultInfo.setSentKBPerSecond(calculator.getSentKBPerSecond());            // 每秒Send的数据量
        sampleResultInfo.setAvgPageBytes(calculator.getAvgPageBytes());                  // 平均收发数据量
        sampleResultInfo.setThreadCount(result.getAllThreads());                         // 线程数量

        // 将信息以列表形式存入Redis,以时间戳为Key
        String sampleResultJson = JSON.toJSONString(sampleResultInfo);
        ListOperations<String, String> sampleRedisList = this.stringRedisTemplate.opsForList();
        sampleRedisList.leftPush(nowTime, sampleResultJson);

        sampleRedisList.range(nowTime, 0, sampleRedisList.size(nowTime));
    }

    /**
     * @description     在测试完成后,清理结果收集器的数据
     */
    public void clearCalculator() {
        calculator.clear();
    }
}
