package com.baosight.gl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * RestConfig配置
 * 
 * @author SY
 */
@Slf4j
@Configuration
@SuppressWarnings("all")
public class RestConfig {

	/**
	 * 创建restTemplate配置Bean
	 */
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpRequestFactory.setConnectionRequestTimeout(30 * 1000);
		httpRequestFactory.setConnectTimeout(30 * 3000);
		httpRequestFactory.setReadTimeout(30 * 3000);
		restTemplate.setRequestFactory(httpRequestFactory);
		return restTemplate;
	}
}