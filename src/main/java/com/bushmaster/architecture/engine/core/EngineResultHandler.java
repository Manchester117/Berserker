package com.bushmaster.architecture.engine.core;

import com.bushmaster.architecture.domain.entity.SampleResultInfo;
import com.bushmaster.architecture.engine.collect.EngineSamplerCollector;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.util.JMeterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class EngineResultHandler {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static EngineSamplerCollector engineSamplerCollector;

    /**
     * @description     添加结果收集器
     * @return          返回结果收集器列表
     */
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

        // 自定义结果收集.继承自ResultCollector.传入stringRedisTemplate,将SampleResult的数据进行缓存
        engineSamplerCollector = new EngineSamplerCollector(summary, stringRedisTemplate);
        engineSamplerCollector.setName("自定义结果收集");
        engineSamplerCollector.setEnabled(Boolean.TRUE);
//        engineSamplerCollector.setFilename(reportFilePath);
        // 添加刚才定义的结果收集
        resultCollectorList.add(engineSamplerCollector);

        ResultCollector csvCollector = new ResultCollector(summary);
        csvCollector.setName("自定义结果记录");
        csvCollector.setFilename("D:\\zbc.csv");
        resultCollectorList.add(csvCollector);

        return resultCollectorList;
    }

    /**
     * @description     重置结果收集器
     */
    public void clearCalculator() {
        if (Objects.nonNull(engineSamplerCollector))
            engineSamplerCollector.clearCalculator();
    }
}
