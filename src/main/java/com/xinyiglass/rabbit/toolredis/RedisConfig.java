package com.xinyiglass.rabbit.toolredis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        //下面注释的代码，在测试hincr接口时报错，而且在使用lleftPush和lleftPop时也有问题
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisConnectionFactory);
//        //配置序列化方式: Key序列化为String，Value序列化为JSON（默认使用Jackson）
//        template.setKeySerializer(RedisSerializer.string());
//        template.setValueSerializer(RedisSerializer.json());
//        template.setHashKeySerializer(RedisSerializer.string());
//        template.setHashValueSerializer(RedisSerializer.json());
//        return template;

        RedisTemplate<String, Object> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        StringRedisSerializer stringRedisSerializer=new StringRedisSerializer();
        MyStringRedisSerializer myStringRedisSerializer=new MyStringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(myStringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(myStringRedisSerializer);

        return template;
    }

}
