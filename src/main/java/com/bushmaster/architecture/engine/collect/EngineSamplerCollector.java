package com.bushmaster.architecture.engine.collect;

import com.bushmaster.architecture.domain.entity.SamplerInfo;
import com.bushmaster.architecture.engine.queue.SamplerQueue;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.util.Calculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineSamplerCollector extends ResultCollector{
    private static final Logger log = LoggerFactory.getLogger(EngineSamplerCollector.class);
    private static volatile Calculator calculator = new Calculator();

    public EngineSamplerCollector(Summariser summer) {
        super(summer);
    }

    @Override
    public void sampleOccurred(SampleEvent event) {
        super.sampleOccurred(event);
        SampleResult result = event.getResult();
        calculator.addSample(result);

        log.info("Response Number Time: " + calculator.getMeanAsNumber() +
                "\t\tMin Response Time: " + calculator.getMin() +
                "\t\tMax Response Time: " + calculator.getMax() +
                "\t\tSampler Count: " + calculator.getCount() +
                "\t\tSend throughput bytes Per Second: " + calculator.getSentBytesPerSecond() +
                "\t\tThroughput bytes Per Second: " + calculator.getBytesPerSecond() +
                "\t\tErrorPercentage: " + calculator.getErrorPercentage() +
                "\t\tThread Count: " + result.getAllThreads()
        );

        SamplerInfo samplerInfo = new SamplerInfo();
        samplerInfo.setMeanTime(calculator.getMean());
        samplerInfo.setMinTime(calculator.getMin());
        samplerInfo.setMaxTime(calculator.getMax());
        samplerInfo.setSamplerCount(calculator.getCount());
        samplerInfo.setSendBytesPerSecond(calculator.getSentBytesPerSecond());
        samplerInfo.setTotalBytesPerSecond(calculator.getBytesPerSecond());
        samplerInfo.setErrorPercentage(calculator.getErrorPercentage());
        samplerInfo.setThreadCount(result.getAllThreads());

        SamplerQueue samplerQueue = SamplerQueue.getInstance();
        samplerQueue.push(samplerInfo);
    }
}
