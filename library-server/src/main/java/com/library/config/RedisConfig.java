package com.library.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis 配置类
 * <p>
 * 功能：
 * 1. 自定义 RedisTemplate —— 使用 JSON 序列化，方便在 Redis 客户端查看数据
 * 2. 配置 CacheManager —— Spring Cache 注解（@Cacheable、@CacheEvict）使用 Redis
 * 3. 设置默认缓存过期时间为 30 分钟
 * </p>
 */
@Configuration
public class RedisConfig {

    /**
     * 自定义 RedisTemplate
     * <p>
     * Key 使用 String 序列化；
     * Value 使用 Jackson JSON 序列化（可读性好，且支持复杂对象）。
     * </p>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // JSON 序列化器
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = createJacksonSerializer();

        // Key 用 String 序列化
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value 用 JSON 序列化
        template.setValueSerializer(jacksonSerializer);
        template.setHashValueSerializer(jacksonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置 Spring Cache 使用 Redis 缓存
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<Object> jacksonSerializer = createJacksonSerializer();

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 默认过期时间 30 分钟
                .entryTtl(Duration.ofMinutes(30))
                // 缓存 null 值（防止缓存穿透）
                // .disableCachingNullValues()  // 如不需要可取消注释
                // Key 序列化
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                // Value 序列化
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jacksonSerializer)
                )
                // 缓存 key 前缀（便于区分不同业务）
                .prefixCacheNameWith("library:");

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                // 可以为不同缓存区域设置不同过期时间
                // .withCacheConfiguration("hotBooks",
                //         RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(2)))
                .build();
    }

    /**
     * 创建 Jackson JSON 序列化器
     * <p>
     * 配置 ObjectMapper 以支持多态类型和私有字段序列化。
     * </p>
     */
    private Jackson2JsonRedisSerializer<Object> createJacksonSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        // 注册 Java 8 日期时间模块（LocalDateTime 等类型的序列化支持）
        mapper.registerModule(new JavaTimeModule());
        // 禁用将日期写为时间戳，改用 ISO-8601 字符串
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 所有字段可见（包括 private）
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 启用多态类型支持
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL
        );
        return new Jackson2JsonRedisSerializer<>(mapper, Object.class);
    }
}
