package com.zhu.study.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhu.study.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhusl 
 * @date 2020年6月11日  下午7:06:20
 *
 */
@RestController
@RequestMapping("/redis-lock")
@Slf4j
public class RedisLockController {
	
	@GetMapping("/hello")
	public String hello() {
		// 方法名当作key
		String key = "hello";
		String value = "hellolock";
		if (RedisUtil.setIfAbsent(key, value, 60 * 2L)) {
			log.info("成功获取到锁，开始执行业务逻辑……");
			// 假如执行业务逻辑需要1分钟
			try {TimeUnit.MINUTES.sleep(1L); } catch (Exception e) { e.printStackTrace();};
			// 释放锁先校验value，避免释放错
			if (value.equals(RedisUtil.getString(key))) {
				RedisUtil.delKey(key);
				log.info("执行完业务逻辑，释放锁成功");
			}
			return "success";
		} else {
			log.error("锁被别的线程占有，获取锁失败");
			return "acquire lock failed";
		}
	}

}
