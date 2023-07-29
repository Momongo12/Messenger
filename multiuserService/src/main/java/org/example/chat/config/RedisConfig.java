package org.example.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;


@Configuration
public class RedisConfig implements RedisConfiguration {

    @Value("${redis.database:-1}")
    private Integer redisDatabase;

    @Value("${redis.password:}")
    private String password;

    @Value("${redis.host:localhost}")
    private String host;

    @Value("${redis.port:6379}")
    private Integer port;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);

        if (!password.trim().isEmpty()) {
            redisStandaloneConfiguration.setPassword(password);
        }

        if (redisDatabase > 0) {
            redisStandaloneConfiguration.setDatabase(redisDatabase);
        }

        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public <F, S> RedisTemplate<F, S> redisTemplate() {

        final RedisTemplate<F, S> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Bean
    public <F, S> HashOperations<F, String, S> hashOperations(RedisTemplate<F, S> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    @Bean
    public <F, S> SetOperations<F, S> setOperations(RedisTemplate<F, S> redisTemplate) {
        return redisTemplate.opsForSet();
    }
}
