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
import org.springframework.data.redis.core.BoundListOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class EngineSampleCollector extends ResultCollector{
    private static final Logger log = LoggerFactory.getLogger(EngineSampleCollector.class);
    private static Map<String, Calculator> calculatorContainer = new HashMap<>();       // 针对不同请求的计数器集合

    private BoundListOperations<String, String> runningSampleResultList;                // 将SampleResult存入Redis的模板

    public EngineSampleCollector(Summariser summer, BoundListOperations<String, String> runningSampleResultList) {
        super(summer);
        this.runningSampleResultList = runningSampleResultList;
    }

//    @Override
//    public void sampleOccurred(SampleEvent event) {
//        super.sampleOccurred(event);
//        SampleResult result = event.getResult();
//
//        calculator.addSample(result);
//
////        log.info("Sampler Label: " + result.getSampleLabel() +
////                "\t\tSampler Count: " + calculator.getCount() +
////                "\t\tResponse Mean Time: " + calculator.getMeanAsNumber() +
////                "\t\tResponse Min Time: " + calculator.getMin() +
////                "\t\tResponse Max Time: " + calculator.getMax() +
////                "\t\tResponse Standard Deviation: " + calculator.getStandardDeviation() +
////                "\t\tError Percentage: " + calculator.getErrorPercentage() +
////                "\t\tThroughput: " + calculator.getRate() +
////                "\t\tThroughput bytes Per Second: " + calculator.getKBPerSecond() +
////                "\t\tSent Throughput KB Per Second: " + calculator.getSentKBPerSecond() +
////                "\t\tAvg Page Bytes: " + calculator.getAvgPageBytes() +
////                "\t\tThread Count: " + result.getAllThreads()
////        );
//
//        SampleResultInfo sampleResultInfo = new SampleResultInfo();
//        sampleResultInfo.setTimeStamp(result.getTimeStamp());                            // 时间戳
//        sampleResultInfo.setSamplerLabel(result.getSampleLabel());                       // 请求名称
//        sampleResultInfo.setSamplerCount(calculator.getCount());                         // SamplerCount
//        sampleResultInfo.setMeanTime(calculator.getMeanAsNumber());                      // 平均响应时间
//        sampleResultInfo.setMinTime(calculator.getMin());                                // 最小响应时间
//        sampleResultInfo.setMaxTime(calculator.getMax());                                // 最大响应时间
//        sampleResultInfo.setStandardDeviation(calculator.getStandardDeviation());        // 标准方差
//        sampleResultInfo.setErrorPercentage(calculator.getErrorPercentage());            // 错误率
//        sampleResultInfo.setRequestRate(calculator.getRate());                           // 每秒请求处理能力
//        sampleResultInfo.setReceiveKBPerSecond(calculator.getKBPerSecond());             // 每秒Receive的数据量
//        sampleResultInfo.setSentKBPerSecond(calculator.getSentKBPerSecond());            // 每秒Send的数据量
//        sampleResultInfo.setAvgPageBytes(calculator.getAvgPageBytes());                  // 平均页面大小
//        sampleResultInfo.setThreadCount(result.getAllThreads());                         // 线程数量
//
//        // 将信息以列表形式存入Redis,以时间戳为Key
//        String sampleResultJson = JSON.toJSONString(sampleResultInfo);
//        // 使用右侧插入,在实时显示的时候可以以正确顺序显示
//        runningSampleResultList.rightPush(sampleResultJson);
//    }

//    /**
//     * @description     在测试完成后,清理结果收集器的数据
//     */
//    public void clearCalculator() {
//        calculator.clear();
//    }

    @Override
    public void sampleOccurred(SampleEvent event) {
        super.sampleOccurred(event);
        SampleResult result = event.getResult();
        String sampleLabel = result.getSampleLabel();

        // 必须要针对不同的请求分别实例化计数器.如果直接使用计数器,则区分不出来请求的数据.
        if (Objects.isNull(calculatorContainer.get(sampleLabel))){
            calculatorContainer.put(sampleLabel, new Calculator());
            calculatorContainer.get(sampleLabel).addSample(result);
        } else {
            calculatorContainer.get(sampleLabel).addSample(result);
        }

        Calculator calculator = calculatorContainer.get(sampleLabel);

        log.info("Sampler Label: " + result.getSampleLabel() +
                "\t\tSampler Count: " + calculator.getCount() +
                "\t\tResponse Mean Time: " + calculator.getMeanAsNumber() +
                "\t\tResponse Min Time: " + calculator.getMin() +
                "\t\tResponse Max Time: " + calculator.getMax() +
                "\t\tResponse Standard Deviation: " + calculator.getStandardDeviation() +
                "\t\tError Percentage: " + calculator.getErrorPercentage() +
                "\t\tThroughput: " + calculator.getRate() +
                "\t\tThroughput bytes Per Second: " + calculator.getKBPerSecond() +
                "\t\tSent Throughput KB Per Second: " + calculator.getSentKBPerSecond() +
                "\t\tAvg Page Bytes: " + calculator.getAvgPageBytes() +
                "\t\tThread Count: " + result.getAllThreads()
        );

        SampleResultInfo sampleResultInfo = new SampleResultInfo();
        sampleResultInfo.setTimeStamp(result.getTimeStamp());                            // 时间戳
        sampleResultInfo.setSamplerLabel(result.getSampleLabel());                       // 请求名称
        sampleResultInfo.setSamplerCount(calculator.getCount());                         // SamplerCount
        sampleResultInfo.setMeanTime(calculator.getMeanAsNumber());                      // 平均响应时间
        sampleResultInfo.setMinTime(calculator.getMin());                                // 最小响应时间
        sampleResultInfo.setMaxTime(calculator.getMax());                                // 最大响应时间
        sampleResultInfo.setStandardDeviation(calculator.getStandardDeviation());        // 标准方差
        sampleResultInfo.setErrorPercentage(calculator.getErrorPercentage());            // 错误率
        sampleResultInfo.setRequestRate(calculator.getRate());                           // 每秒请求处理能力
        sampleResultInfo.setReceiveKBPerSecond(calculator.getKBPerSecond());             // 每秒Receive的数据量
        sampleResultInfo.setSentKBPerSecond(calculator.getSentKBPerSecond());            // 每秒Send的数据量
        sampleResultInfo.setAvgPageBytes(calculator.getAvgPageBytes());                  // 平均页面大小
        sampleResultInfo.setThreadCount(result.getAllThreads());                         // 线程数量

        // 将信息以列表形式存入Redis,以时间戳为Key
        String sampleResultJson = JSON.toJSONString(sampleResultInfo);
        // 使用右侧插入,在实时显示的时候可以以正确顺序显示
        runningSampleResultList.rightPush(sampleResultJson);
    }

    /**
     * @description     在测试完成后,清理结果收集器的数据
     */
    public void clearCalculator() {
        calculatorContainer.clear();
    }
}
