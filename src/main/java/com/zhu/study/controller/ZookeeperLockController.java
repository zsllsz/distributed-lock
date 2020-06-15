package com.zhu.study.controller;

import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zhu.study.util.ZookeeperUtil;

/**
 * @author zhusl 
 * @date 2020年6月15日  下午2:40:56
 *
 */
@RestController
@RequestMapping("/zookeeper-lock")
public class ZookeeperLockController {
	
	@GetMapping("/testLock")
	public String testLock() {
		// 获取锁
		boolean lockResult = ZookeeperUtil.interProcessMutex("testLock");
		if (lockResult) {
			try {
				// 模拟执行业务逻辑
				TimeUnit.MINUTES.sleep(1L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 释放锁
			ZookeeperUtil.releaseLock("testLock");
			return "success";
		} else {
			return "fail";
		}
	}

}
