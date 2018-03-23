package com.bushmaster.architecture;

import com.bushmaster.architecture.engine.queue.SampleResultReceiver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 订阅一个叫message的通道
        container.addMessageListener(listenerAdapter, new PatternTopic("message"));
        // container中可以添加多个messageListener
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(SampleResultReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveSampleResult");
    }

//    @Bean
//    StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
//        return new StringRedisTemplate(connectionFactory);
//    }
}
