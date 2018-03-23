package com.bushmaster.architecture.engine.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SampleResultPublisher {
    @Autowired
    private StringRedisTemplate template;

    public void publishSampleResult() {
        template.convertAndSend("message", "Are you OK?!");
    }
}
