package com.baosight.gl.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.baosight.gl.service.ht.HtService;
import com.baosight.gl.utils.SystemUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 高炉定时任务
 * 
 * @author SY
 */
@Slf4j
@Configuration
@EnableScheduling
@SuppressWarnings(value = "all")
public class HtScheduler {

	@Autowired
	HtService htService;

	/**
	 * 定时计算气流场：速度
	 * 时间间隔：5s
	 */
	@Scheduled(cron = "*/5 * * * * *")
	public void countSpeedTask() throws Exception {
		// 判断系统
		if (SystemUtils.isLinux()) {
			htService.dealAirFlow(1, "u");
		}
	}

	/**
	 * 定时计算气流场：压力
	 * 时间间隔：5s
	 */
	@Scheduled(cron = "*/5 * * * * *")
	public void countPressureTask() throws Exception {
		// 判断系统
		if (SystemUtils.isLinux()) {
			htService.dealAirFlow(2, "p");
		}
	}

	/**
	 * 定时计算气流场：温度
	 * 时间间隔：5s
	 */
	@Scheduled(cron = "*/5 * * * * *")
	public void countTemperatureTask() throws Exception {
		// 判断系统
		if (SystemUtils.isLinux()) {
			htService.dealAirFlow(3, "t");
		}
	}
}