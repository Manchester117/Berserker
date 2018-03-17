package com.bushmaster.architecture.engine.core;

import com.bushmaster.architecture.engine.collect.EngineResultCollector;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.util.JMeterUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EngineResultHandler {
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
        EngineResultCollector engineResultCollector = new EngineResultCollector(summary);
        engineResultCollector.setName("自定义结果收集");
//        engineResultCollector.setFilename(reportFilePath);
        resultCollectorList.add(engineResultCollector);

        return resultCollectorList;
    }
}
