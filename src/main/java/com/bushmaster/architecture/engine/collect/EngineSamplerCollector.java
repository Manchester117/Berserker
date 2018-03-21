package com.bushmaster.architecture.engine.collect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bushmaster.architecture.domain.entity.SamplerInfo;
import com.bushmaster.architecture.engine.core.EngineController;
import com.bushmaster.architecture.engine.queue.SamplerQueue;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.util.Calculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;


public class EngineSamplerCollector extends ResultCollector{
    private static final Logger log = LoggerFactory.getLogger(EngineSamplerCollector.class);
    private static volatile Calculator calculator = new Calculator();
    private SimpMessagingTemplate template;                 // 通过构造函数获取WebSocket信息发送机制

//    public EngineSamplerCollector(Summariser summer, SimpMessagingTemplate template) {
//        super(summer);
//        this.template = template;
//    }

    public EngineSamplerCollector(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void sampleOccurred(SampleEvent event) {
        super.sampleOccurred(event);
        SampleResult result = event.getResult();
        calculator.addSample(result);

        log.info("Response Mean Time: " + calculator.getMeanAsNumber() +
                "\t\tMin Response Time: " + calculator.getMin() +
                "\t\tMax Response Time: " + calculator.getMax() +
                "\t\tSampler Count: " + calculator.getCount() +
                "\t\tRequest Throughput Per Second: " + calculator.getRate() +
                "\t\tSent Throughput bytes Per Second: " + calculator.getSentBytesPerSecond() +
                "\t\tThroughput bytes Per Second: " + calculator.getBytesPerSecond() +
                "\t\tErrorPercentage: " + calculator.getErrorPercentage() +
                "\t\tThread Count: " + result.getAllThreads()
        );

        SamplerInfo samplerInfo = new SamplerInfo();
        samplerInfo.setMeanTime(calculator.getMeanAsNumber());
        samplerInfo.setMinTime(calculator.getMin());
        samplerInfo.setMaxTime(calculator.getMax());
        samplerInfo.setSamplerCount(calculator.getCount());
        samplerInfo.setRequestRate(calculator.getRate());
        samplerInfo.setSentBytesPerSecond(calculator.getSentBytesPerSecond());
        samplerInfo.setTotalBytesPerSecond(calculator.getBytesPerSecond());
        samplerInfo.setErrorPercentage(calculator.getErrorPercentage());
        samplerInfo.setThreadCount(result.getAllThreads());

        SamplerQueue samplerQueue = SamplerQueue.getInstance();
        samplerQueue.push(samplerInfo);

        String samplerInfoJson = JSON.toJSONString(samplerInfo);

        // 使用WebSocket把信息发送到客户端
        template.convertAndSend("/topic/notice", samplerInfoJson);
    }
}
