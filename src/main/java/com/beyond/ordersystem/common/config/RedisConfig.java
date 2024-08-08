package com.beyond.ordersystem.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    public String host;
    @Value("${spring.redis.port}")
    public int port;

    @Bean
    @Qualifier("2") // 여기서는 숫자 1부터 시작
    public RedisConnectionFactory redisConnectionFactory(){
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(host);
        redisConfiguration.setPort(port);
//        1번 DB 사용한다.
        redisConfiguration.setDatabase(1); // 여기는 0부터 시작
        return new LettuceConnectionFactory(redisConfiguration);
    }

    @Bean
    @Qualifier("2")
    public RedisTemplate<String, Object> redisTemplateExec(@Qualifier("2") RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }



    @Bean
    @Qualifier("3")
    public RedisConnectionFactory stockFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        // 2번 db 사용 - redis에서 select 2로 확인
        configuration.setDatabase(2);
        return new LettuceConnectionFactory(configuration);

    }

    @Bean(name = "redisTemplate")
    @Qualifier("3")
    public RedisTemplate<String, Object> stockRedisTemplate(@Qualifier("3") RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // string 형태를 직렬화 시키게따 (java에 string으로 들어가게)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 제이슨 직렬화 툴 세팅
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 주입받은 connection을 넣어줌
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }


//    @Bean
//    @Qualifier("3")
//    public RedisConnectionFactory redisStockFactory(){
//        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
//        redisConfiguration.setHostName(host);
//        redisConfiguration.setPort(port);
//        redisConfiguration.setDatabase(3);
//        return new LettuceConnectionFactory(redisConfiguration);
//    }
//
//    @Bean(name = "stockRedis")
//    @Qualifier("3")
//    public RedisTemplate<String, Object> stockRedisTemplateExec(@Qualifier("3") RedisConnectionFactory redisStockFactory){
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        redisTemplate.setConnectionFactory(redisStockFactory);
//        return redisTemplate;
//    }

}
