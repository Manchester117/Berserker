package com.bushmaster.architecture.engine.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SampleResultReceiver {
    private static final Logger log = LoggerFactory.getLogger(SampleResultReceiver.class);

    public void receiveSampleResult(String sampleResult) {
        log.info("Received <\"" + sampleResult + "\">");
    }
}
