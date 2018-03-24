package com.bushmaster.architecture;

import com.bushmaster.architecture.domain.entity.SampleResultInfo;
import com.bushmaster.architecture.engine.cache.RedisSampleSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {
    @Bean
    public CacheManager cacheManager(RedisTemplate<?, ?> redisTemplate) {
        return new RedisCacheManager(redisTemplate);
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    @ConditionalOnMissingBean(name = {"redisTemplate"})
    public RedisTemplate<String, SampleResultInfo> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, SampleResultInfo> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new RedisSampleSerializer());
        return template;
    }
}
