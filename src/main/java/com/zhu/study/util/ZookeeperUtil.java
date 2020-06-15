package com.zhu.study.util;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMultiLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhusl
 * @date 2020年6月15日 上午9:51:39
 *
 */
@Component
@Slf4j
public class ZookeeperUtil {

	private static CuratorFramework curatorFramework;
	
	private static InterProcessLock lock;

	/** 持久节点 */
	private final static String ROOT_PATH = "/lock/";
	
	/** 可重入共享锁 */
	private static InterProcessMutex interProcessMutex;
	/** 不可重入共享锁 */
	private static InterProcessSemaphoreMutex interProcessSemaphoreMutex;
	/** 可重入读写锁 */
	private static InterProcessReadWriteLock interProcessReadWriteLock;
	/** 多共享锁(将多把锁当成一把来用) */
	private static InterProcessMultiLock interProcessMultiLock;

	@Autowired
	private void setCuratorFramework(CuratorFramework curatorFramework) {
		ZookeeperUtil.curatorFramework = curatorFramework;
		ZookeeperUtil.curatorFramework.start();
	}

	/**
	 * 获取可重入排他锁
	 * 
	 * @param lockName
	 * @return
	 */
	public static boolean interProcessMutex(String lockName) {
		interProcessMutex = new InterProcessMutex(curatorFramework, ROOT_PATH + lockName);
		lock = interProcessMutex;
		return acquireLock(lockName, lock);
	}

	/**
	 * 获取不可重入排他锁
	 * 
	 * @param lockName
	 * @return
	 */
	public static boolean interProcessSemaphoreMutex(String lockName) {
		interProcessSemaphoreMutex = new InterProcessSemaphoreMutex(curatorFramework, ROOT_PATH + lockName);
		lock = interProcessSemaphoreMutex;
		return acquireLock(lockName, lock);
	}

	/**
	 * 获取可重入读锁
	 * 
	 * @param lockName
	 * @return
	 */
	public static boolean interProcessReadLock(String lockName) {
		interProcessReadWriteLock = new InterProcessReadWriteLock(curatorFramework, ROOT_PATH + lockName);
		lock = interProcessReadWriteLock.readLock();
		return acquireLock(lockName, lock);
	}

	/**
	 * 获取可重入写锁
	 * 
	 * @param lockName
	 * @return
	 */
	public static boolean interProcessWriteLock(String lockName) {
		interProcessReadWriteLock = new InterProcessReadWriteLock(curatorFramework, ROOT_PATH + lockName);
		lock = interProcessReadWriteLock.writeLock();
		return acquireLock(lockName, lock);
	}

	/**
	 * 获取联锁(多把锁当成一把来用)
	 * @param lockNames
	 * @return
	 */
	public static boolean interProcessMultiLock(List<String> lockNames) {
		if (lockNames == null || lockNames.isEmpty()) {
			log.error("no lockNames found");
			return false;
		}
		interProcessMultiLock = new InterProcessMultiLock(curatorFramework, lockNames);
		try {
			if (!interProcessMultiLock.acquire(10, TimeUnit.SECONDS)) {
				log.info("Thread:" + Thread.currentThread().getId() + " acquire distributed lock fail");
				return false;
			} else {
				log.info("Thread:" + Thread.currentThread().getId() + " acquire distributed lock success");
				return true;
			}
		} catch (Exception e) {
			log.info("Thread:" + Thread.currentThread().getId() + " release lock occured an exception = " + e);
			return false;
		}
	}

	/**
	 * 释放锁
	 * 
	 * @param lockName
	 */
	public static void releaseLock(String lockName) {
		try {
			if (lock != null && lock.isAcquiredInThisProcess()) {
				lock.release();
				curatorFramework.delete().inBackground().forPath(ROOT_PATH + lockName);
				log.info("Thread:" + Thread.currentThread().getId() + " release lock success");
			}
		} catch (Exception e) {
			log.info("Thread:" + Thread.currentThread().getId() + " release lock occured an exception = " + e);
		}
	}
	
	/**
	 * 释放联锁
	 */
	public static void releaseMultiLock(List<String> lockNames) {
		try {
			if (lockNames == null || lockNames.isEmpty()) {
				log.error("no no lockNames found to release");
				return;
			}
			if (interProcessMultiLock != null && interProcessMultiLock.isAcquiredInThisProcess()) {
				interProcessMultiLock.release();
				for (String lockName : lockNames) {
					curatorFramework.delete().inBackground().forPath(ROOT_PATH + lockName);
				}
				log.info("Thread:" + Thread.currentThread().getId() + " release lock success");
			}
		} catch (Exception e) {
			log.info("Thread:" + Thread.currentThread().getId() + " release lock occured an exception = " + e);
		}
	}
	

	/**
	 * 获取锁
	 * 
	 * @param lockName
	 * @param interProcessLock
	 * @return
	 */
	private static boolean acquireLock(String lockName, InterProcessLock interProcessLock) {
		int flag = 0;
		try {
			while (!interProcessLock.acquire(2, TimeUnit.SECONDS)) {
				flag++;
				if (flag > 1) {
					break;
				}
			}
		} catch (Exception e) {
			log.error("acquire lock occured an exception = " + e);
			return false;
		}
		if (flag > 1) {
			log.info("Thread:" + Thread.currentThread().getId() + " acquire distributed lock fail");
			return false;
		} else {
			log.info("Thread:" + Thread.currentThread().getId() + " acquire distributed lock success");
			return true;
		}
	}
}
