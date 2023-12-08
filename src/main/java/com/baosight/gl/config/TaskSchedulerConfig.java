package com.baosight.gl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@SuppressWarnings("all")
public class TaskSchedulerConfig {

	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler taskExecutor = new ThreadPoolTaskScheduler();
		taskExecutor.setPoolSize(50);
		return taskExecutor;
	}
}
