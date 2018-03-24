package com.bushmaster.architecture.engine.cache;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.Objects;

public class RedisSampleSerializer implements RedisSerializer<Object>{
    private Converter<Object, byte[]> serializer = new SerializingConverter();
    private Converter<byte[], Object> deserializer = new DeserializingConverter();

    private static final byte [] EMPTY_ARRAY = new byte[0];

    private boolean isEmpty(byte [] data) {
        return (Objects.isNull(data) || Objects.equals(data.length, 0));
    }

    /**
     * @description                         对象序列化
     * @param object                        待序列化的对象
     * @return                              字节数组
     * @throws SerializationException       序列化异常
     */
    @Override
    public byte[] serialize(Object object) throws SerializationException {
        if (Objects.isNull(object)) {
            return EMPTY_ARRAY;
        }
        return serializer.convert(object);
    }

    /**
     * @description                         对象反序列化
     * @param bytes                         对象字节数组
     * @return                              对象
     * @throws SerializationException       序列化异常
     */
    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (this.isEmpty(bytes))
            return null;
        return deserializer.convert(bytes);
    }
}
