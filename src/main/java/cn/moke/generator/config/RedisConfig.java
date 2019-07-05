package cn.moke.generator.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring整合Jedis
 * @author moke
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * CacheManager中key的生成制定（返回类型名（包含泛型） + 全限定类名 + 方法名 + 各种参数的名（包含泛型） + 各种参数的值）
     */
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return (Object target, Method method, Object... params) -> {
            String returnName = method.getGenericReturnType().getTypeName();
            String clazzName = target.getClass().getName();
            String methodName = method.getName();
            Type[] parameterTypes = method.getGenericParameterTypes();

            StringBuilder sb = new StringBuilder();
            sb.append(returnName).append(clazzName).append(methodName);
            for (int i = 0; i < params.length; i++ ) {
                sb.append(parameterTypes[i]).append(params[i].toString());
            }
            return sb.toString();
        };
    }

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory factory){
        // RedisTemplate key和value默认采用的是JDK的序列化策略
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 连接工厂
        template.setConnectionFactory(factory);
        // string序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 设置key的序列化器（string操作）
        template.setKeySerializer(stringRedisSerializer);
        // json-对象 序列化器
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = buildJackson2JsonRedisSerializer();
        // 设置value的序列化器（string）
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // 设置value的序列化器（hash-key）
        template.setHashKeySerializer(stringRedisSerializer);
        // 设置value的序列化器（hash-value）
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        return template;
    }

    /**
     * RedisCacheManager 2.0之前配置
     */
//    @Bean
//    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
//        //初始化一个RedisCacheWriter
//        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
//        //设置CacheManager的值序列化方式为JdkSerializationRedisSerializer,但其实RedisCacheConfiguration默认就是使用StringRedisSerializer序列化key，JdkSerializationRedisSerializer序列化value,所以以下注释代码为默认实现
//        //ClassLoader loader = this.getClass().getClassLoader();
//        //JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(loader);
//        //RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(jdkSerializer);
//        //RedisCacheConfiguration defaultCacheConfig=RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);
//        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
//        //设置默认超过期时间是30秒
//        defaultCacheConfig.entryTtl(Duration.ofSeconds(30));
//        //初始化RedisCacheManager
//        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
//    }


    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> initialRedisCacheConfigurationMap = new HashMap<>();
        initialRedisCacheConfigurationMap.put("RolePermission", RedisCacheConfiguration.defaultCacheConfig());
        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5)))
                .withInitialCacheConfigurations(initialRedisCacheConfigurationMap)
                .transactionAware()
                .build();
    }

    private Jackson2JsonRedisSerializer buildJackson2JsonRedisSerializer(){
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }
}