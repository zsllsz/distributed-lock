package com.zhu.study;


import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.zhu.study.util.ZookeeperUtil;


@SpringBootTest(classes = {DistributedLockApplication.class})
@RunWith(SpringRunner.class)
public class DistributedLockApplicationTests {
	
	@Autowired
	private CuratorFramework curatorFramework;
	

	@Test
	public void contextLoads() {
		curatorFramework.start();
		try {
			curatorFramework.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/zhusl", "test".getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testZookeeperLock() {
		ZookeeperUtil.interProcessMutex("lock2");
	}
	
}
