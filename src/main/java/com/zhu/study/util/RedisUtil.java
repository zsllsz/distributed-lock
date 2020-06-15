package com.zhu.study.util;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;


/**
 * redis工具类
 * @author zhusl 
 * @date 2020年6月11日  下午6:30:54
 *
 */
@Component
@SuppressWarnings("unchecked")
public class RedisUtil {
	
	private RedisUtil() {}

	@SuppressWarnings("rawtypes")
	private static RedisTemplate redisTemplate;
	
	/**
	 * 设置key-value，过期时间为timeout秒
	 * @param key
	 * @param value
	 * @param timeout
	 */
	public static void setString(String key, String value, Long timeout) {
		redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
	}
	
	/**
	 * 设置key-value
	 * @param key
	 * @param value
	 */
	public static void setString(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}
	
	/**
	 * 获取key-value
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		return (String) redisTemplate.opsForValue().get(key);
	}
	
	/**
	 * 判断key是否存在
	 * @param key
	 * @return
	 */
	public static boolean isExist(String key) {
		return redisTemplate.hasKey(key);
	}
	
	/**
	 * 删除key
	 * @param key
	 * @return
	 */
	public static boolean delKey(String key) {
		return redisTemplate.delete(key);
	}
	
	/**
	 * key不存在时就设置，返回true，key已存在就返回false
	 * @param key
	 * @param value
	 * @param timeout
	 * @return
	 */
	public static boolean setIfAbsent(String key, String value, Long timeout) {
		return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
	}

	

	@Autowired
	private void setRedisTemplate(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate) {
		redisTemplate.setKeySerializer(new StringRedisSerializer());
        //设置序列化Value的实例化对象
		redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		RedisUtil.redisTemplate = redisTemplate;
	}
}
