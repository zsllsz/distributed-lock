package com.zhu.study.controller;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhusl
 * @date 2020年6月12日 下午3:34:06
 *
 */
@RestController
@RequestMapping("/redisson-lock")
@Slf4j
public class RedissonLockController {

	@Autowired
	private RedissonClient redisson;

	/**
	 * 未设置过期时间，没获取到就会一直阻塞着
	 * @return
	 */
	@GetMapping("/testLock")
	public String testLock() {
		log.info("进入testLock方法，开始获取锁");
		String key = "testLock";
		RLock lock = redisson.getLock(key);
		lock.lock();
		log.info("获取锁成功，开始执行业务逻辑……");
		try {TimeUnit.SECONDS.sleep(10L); } catch (Exception e) { e.printStackTrace();};
		log.info("执行完业务逻辑，释放锁");
		lock.unlock();
		return "success";
	}
	
	/**
	 * 尝试获取锁，没获取到就直接失败，不会阻塞
	 * @return
	 */
	@GetMapping("/testTryLock")
	public String testTryLock() {
		log.info("进入testTryLock方法，开始获取锁");
		String key = "testTryLock";
		RLock lock = redisson.getLock(key);
		boolean res = lock.tryLock();
		if (!res) {
			log.error("尝试获取锁失败");
			return "fail";
		} else {
			log.info("获取锁成功，开始执行业务逻辑……");
			try {TimeUnit.SECONDS.sleep(30L); } catch (Exception e) { e.printStackTrace();};
			log.info("执行完业务逻辑，释放锁");
			lock.unlock();
			return "success";
		}
	}
	
	/**
	 * 锁设置了过期时间，即使最后面的unlock失败，20秒后也会自动释放锁
	 * @return
	 */
	@GetMapping("/testLockTimeout")
	public String testLockTimeout() {
		log.info("进入testLockTimeout方法，开始获取锁");
		String key = "testLockTimeout";
		RLock lock = redisson.getLock(key);
		// 20秒后自动释放锁
		lock.lock(20, TimeUnit.SECONDS);
		log.info("获取锁成功，开始执行业务逻辑……");
		try {TimeUnit.SECONDS.sleep(10L); } catch (Exception e) { e.printStackTrace();};
		lock.unlock();
		return "success";
	}
	
	/**
	 * 尝试获取锁，15秒还没获取到就获取锁失败；获取到了会持有20秒，20秒后自动释放锁
	 * @return
	 */
	@GetMapping("/testTryLockTimeout")
	public String testTryLockTimeout() {
		log.info("进入testTryLockTimeout方法，开始获取锁");
		String key = "testTryLockTimeout";
		RLock lock = redisson.getLock(key);
		boolean res = false;
		try {
			res = lock.tryLock(15, 20, TimeUnit.SECONDS);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if (!res) {
			log.error("尝试获取锁失败");
			return "fail";
		} else {
			log.info("获取锁成功，开始执行业务逻辑……");
			try {TimeUnit.SECONDS.sleep(10L); } catch (Exception e) { e.printStackTrace();};
			log.info("执行完业务逻辑，释放锁");
			lock.unlock();
			return "success";
		}
	}
}
