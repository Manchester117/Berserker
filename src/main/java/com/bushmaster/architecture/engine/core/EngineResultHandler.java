package com.bushmaster.architecture.engine.core;

import com.bushmaster.architecture.engine.collect.EngineSamplerCollector;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.util.JMeterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EngineResultHandler {
    @Autowired
    private SimpMessagingTemplate template;       // 因为WebSocket的信息发送需要注入,所以需要在Component中定义,传给Sampler的收集器

    public List<ResultCollector> resultCollect() {
        // 结果收集
        Summariser summary = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summary = new Summariser(summariserName);
            // 设置summary打印间隔
            summary.setProperty("summariser.interval", "1");
        }
        // 定义结果收集器的List
        List<ResultCollector> resultCollectorList = new ArrayList<>();

        // 自定义结果收集.继承自ResultCollector
        EngineSamplerCollector engineSamplerCollector = new EngineSamplerCollector(template);
        engineSamplerCollector.setName("自定义结果收集");
        engineSamplerCollector.setEnabled(Boolean.TRUE);
//        engineSamplerCollector.setFilename(reportFilePath);
        // 添加刚才定义的结果收集
        resultCollectorList.add(engineSamplerCollector);

        ResultCollector csvCollector = new ResultCollector(summary);
        csvCollector.setName("自定义结果记录");
        csvCollector.setFilename("D:\\abc.csv");
        resultCollectorList.add(csvCollector);

        return resultCollectorList;
    }
}
