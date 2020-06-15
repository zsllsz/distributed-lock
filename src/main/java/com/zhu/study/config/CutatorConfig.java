package com.zhu.study.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhusl
 * @date 2020年6月12日 下午5:49:24
 *
 */
@Configuration
public class CutatorConfig {

	@Value("${curator.retryCount}")
	private Integer retryCount;

	@Value("${curator.retryTimeInterval}")
	private Integer retryTimeInterval;

	@Value("${curator.url}")
	private String url;

	@Value("${curator.sessionTimeout}")
	private Integer sessionTimeout;

	@Value("${curator.connectionTimeout}")
	private Integer connectionTimeout;

	@Bean(name = "curatorFramework")
	public CuratorFramework curatorFramework() {
		return CuratorFrameworkFactory.newClient(url, sessionTimeout, connectionTimeout,
				new RetryNTimes(retryCount, retryTimeInterval));
	}

}
