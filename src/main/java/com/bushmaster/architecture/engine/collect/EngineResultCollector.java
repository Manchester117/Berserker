package com.bushmaster.architecture.engine.collect;

import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.util.Calculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineResultCollector extends ResultCollector{
    private static final Logger log = LoggerFactory.getLogger(EngineResultCollector.class);
    private static volatile Calculator calculator = new Calculator();

    public EngineResultCollector(Summariser summer) {
        super(summer);
    }

    @Override
    public void sampleOccurred(SampleEvent event) {
        super.sampleOccurred(event);
        SampleResult result = event.getResult();
        calculator.addSample(result);
//        System.out.println("Response Time: " + calculator.getMean() +
//                "\t\tMin Response Time: " + calculator.getMin() +
//                "\t\tMax Response Time: " + calculator.getMax() +
//                "\t\tThroughput Per Second: " + calculator.getSentBytesPerSecond() +
//                "\t\tSampler Count: " + calculator.getCount() +
//                "\t\tThread Count: " + result.getAllThreads()
//        );

        log.info("Response Time: " + calculator.getMean() +
                "\t\tMin Response Time: " + calculator.getMin() +
                "\t\tMax Response Time: " + calculator.getMax() +
                "\t\tThroughput Per Second: " + calculator.getSentBytesPerSecond() +
                "\t\tSampler Count: " + calculator.getCount() +
                "\t\tThread Count: " + result.getAllThreads()
        );
    }
}
