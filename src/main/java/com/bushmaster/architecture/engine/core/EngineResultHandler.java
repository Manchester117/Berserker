package com.bushmaster.architecture.engine.core;

import com.bushmaster.architecture.engine.collect.EngineSampleCollector;
import com.bushmaster.architecture.engine.collect.EngineSampleRealOuter;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.util.JMeterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class EngineResultHandler {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private EngineSampleRealOuter engineSampleRealOuter;

    private EngineSampleCollector engineSampleCollector;

    private BoundListOperations<String, String> runningSampleResultList;

    /**
     * @description     添加结果收集器
     * @return          返回结果收集器列表
     */
    public List<ResultCollector> resultCollect(Integer scenarioId) {
        // 结果收集
        Summariser summary = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summary = new Summariser(summariserName);
            // 设置summary打印间隔,没起作用
//            summary.setProperty("summariser.interval", "1");
        }
        // 定义存入Redis的结果Key
        String runningScenarioKey = StringUtils.join("scenario_", scenarioId);
        runningSampleResultList = stringRedisTemplate.boundListOps(runningScenarioKey);

        // 定义结果收集器的List
        List<ResultCollector> resultCollectorList = new ArrayList<>();

        // 自定义结果收集.继承自ResultCollector.runningSampleResultList,将SampleResult的数据进行缓存
        engineSampleCollector = new EngineSampleCollector(summary, runningSampleResultList);
        // 这两个方法都是继承自ResultCollector的方法
        engineSampleCollector.setName("自定义结果收集");
        engineSampleCollector.setEnabled(Boolean.TRUE);
//        engineSampleCollector.setFilename(reportFilePath);
        // 添加刚才定义的结果收集
        resultCollectorList.add(engineSampleCollector);

        // 原生的结果收集器
//        ResultCollector csvCollector = new ResultCollector(summary);
//        csvCollector.setName("自定义结果记录");
//        csvCollector.setFilename("D:\\zbc.csv");
//        resultCollectorList.add(csvCollector);

        return resultCollectorList;
    }

    public void resultRealOuter() {
        engineSampleRealOuter.setRunningSampleResultList(runningSampleResultList);
        engineSampleRealOuter.sampleRealOuter();
    }

    /**
     * @description     重置结果收集器
     */
    public void clearCalculator() {
        if (Objects.nonNull(engineSampleCollector))
            engineSampleCollector.clearCalculator();
    }

    /**
     * @description     获取当前的SamplerResult列表(为测试完成后,存储测试结果做准备)
     * @return
     */
    public BoundListOperations<String, String> getRunningSampleResultList() {
        return runningSampleResultList;
    }
}
