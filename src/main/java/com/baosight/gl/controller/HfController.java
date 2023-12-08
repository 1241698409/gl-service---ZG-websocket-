package com.baosight.gl.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.baosight.gl.service.ht.GridsPointsService;
import com.baosight.gl.service.ht.HtService;
import com.baosight.gl.service.ht.InverseDistanceService;
import com.baosight.gl.service.ht.constant.GridsPointsConstant;
import com.baosight.gl.service.ht.domain.Point;

import lombok.extern.slf4j.Slf4j;

/**
 * @author SY
 * 
 * @description 海仿U-P-T文件数据
 *
 */
@Slf4j
@RestController
@SuppressWarnings("all")
public class HfController {

	@Autowired
	GridsPointsService gridsPointsService;

	@Autowired
	HtService htService;

	@CrossOrigin
	@PostMapping("/queryOreCoke")
	public String queryOreCoke(Integer type) {
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("type", type);
		List<HashMap> oreCokeList = htService.queryOreCoke(paramsMap);
		List<List<String>> oreCokeValueList = new ArrayList<>();
		for (int i = 0; i < oreCokeList.size(); i++) {
			HashMap oreCokeHashMap = oreCokeList.get(i);
			List<String> valueList = new ArrayList<>();
			for (int j = 0; j <= 525; j++) {
				String value = String.valueOf(oreCokeHashMap.get("R" + j));
				valueList.add(value);
			}
			oreCokeValueList.add(valueList);
		}
		return JSON.toJSONString(oreCokeValueList);
	}

	/**
	 * 查询气流场速度文件数据项
	 * 传参id：查询对应id数据项，不传参id：查询最新的数据项
	 * 
	 * @param id
	 */
	@CrossOrigin
	@PostMapping("/queryAirFlowU")
	public String queryAirFlowU(Integer id) {
		try {
			// 设置参数
			HashMap paramsMap = new HashMap<>();
			paramsMap.put("type", 1);
			paramsMap.put("id", id);
			// 查询AirFlowMap集合
			HashMap AirFlowMap = htService.queryCurrentAirFlow(paramsMap);
			// 获取路径
			String FilePath = AirFlowMap.get("TARGET_FILE_PATH").toString();
			// 返回
			return htService.readAirFlowFile(FilePath);
		} catch (Exception e) {
			// 返回错误码
			return "500";
		}
	}

	/**
	 * 查询气流场压力文件数据项
	 * 传参id：查询对应id数据项，不传参id：查询最新的数据项
	 * 
	 * @param id
	 */
	@CrossOrigin
	@PostMapping("/queryAirFlowP")
	public String queryAirFlowP(Integer id) {
		try {
			// 设置参数
			HashMap paramsMap = new HashMap<>();
			paramsMap.put("type", 2);
			paramsMap.put("id", id);
			// 查询AirFlowMap集合
			HashMap AirFlowMap = htService.queryCurrentAirFlow(paramsMap);
			// 获取路径
			String FilePath = AirFlowMap.get("TARGET_FILE_PATH").toString();
			// 返回
			return htService.readAirFlowFile(FilePath);
		} catch (Exception e) {
			// 返回错误码
			return "500";
		}
	}

	/**
	 * 查询气流场温度文件数据项
	 * 传参id：查询对应id数据项，不传参id：查询最新的数据项
	 * 
	 * @param id
	 */
	@CrossOrigin
	@PostMapping("/queryAirFlowT")
	public String queryAirFlowT(Integer id) {
		try {
			// 设置参数
			HashMap paramsMap = new HashMap<>();
			paramsMap.put("type", 3);
			paramsMap.put("id", id);
			// 查询AirFlowMap集合
			HashMap AirFlowMap = htService.queryCurrentAirFlow(paramsMap);
			// 获取路径
			String FilePath = AirFlowMap.get("TARGET_FILE_PATH").toString();
			// 返回
			return htService.readAirFlowFile(FilePath);
		} catch (Exception e) {
			// 返回错误码
			return "500";
		}
	}

	/**
	 * 查询气流场原始数据项：最小值、最大值
	 * 
	 * @param id
	 * @param type：1速度，2压力，3温度
	 */
	@CrossOrigin
	@PostMapping("/queryAirFlowExtremum")
	public String queryAirFlowExtremum(Integer id, Integer type) {
		try {
			// 设置参数
			HashMap paramsMap = new HashMap<>();
			paramsMap.put("type", type);
			paramsMap.put("id", id);
			// 查询AirFlowMap集合
			HashMap AirFlowMap = htService.queryCurrentAirFlow(paramsMap);
			// 获取最小值
			String minValue = AirFlowMap.get("ORIGINAL_MIN_VALUE").toString();
			BigDecimal minBigDecimal = new BigDecimal(minValue);
			// 判断最小值为负数时，处理成0
			if (minBigDecimal.compareTo(BigDecimal.ZERO) == -1) {
				minBigDecimal = new BigDecimal(0);
			}
			// 获取最大值
			String maxValue = AirFlowMap.get("ORIGINAL_MAX_VALUE").toString();
			BigDecimal maxBigDecimal = new BigDecimal(maxValue);
			// 处理minValue、maxValue到ExtremumMap集合
			Map ExtremumMap = new HashMap<>();
			ExtremumMap.put("minValue", minBigDecimal.toPlainString());
			ExtremumMap.put("maxValue", maxBigDecimal.toPlainString());
			// 返回
			return JSON.toJSONString(ExtremumMap);
		} catch (Exception e) {
			// 返回错误码
			return "500";
		}
	}

	/**
	 * 查询开始时间-结束时间：id集合
	 * 
	 * @param type：1速度，2压力，3温度
	 * @param startTime：开始时间
	 * @param endTime：结束时间
	 */
	@CrossOrigin
	@PostMapping("/queryAirFlowByTimes")
	public String queryAirFlowByTimes(String type, String startTime, String endTime) {
		try {
			// 声明retMap集合
			Map retMap = new HashMap<>();
			// 声明resultIdRetList集合
			List<Integer> resultIdRetList = new ArrayList<>();
			// 设置参数
			HashMap paramsMap = new HashMap<>();
			paramsMap.put("type", type);
			paramsMap.put("startTime", startTime);
			paramsMap.put("endTime", endTime);
			// 查询AirFlowByTimesList集合
			List<HashMap> AirFlowByTimesList = htService.queryAirFlowByTimes(paramsMap);
			// 遍历AirFlowByTimesList集合
			for (int i = 0; i < AirFlowByTimesList.size(); i++) {
				Integer resultId = Integer.valueOf(AirFlowByTimesList.get(i).get("ID").toString());
				resultIdRetList.add(resultId);
			}
			// 获取开始时间
			Object startTimeValue = AirFlowByTimesList.get(0).get("ORIGINAL_FILE_TIME");
			// 获取结束时间
			Object endTimeValue = AirFlowByTimesList.get(AirFlowByTimesList.size() - 1).get("ORIGINAL_FILE_TIME");
			// 处理resultIdRetList、startTimeValue、endTimeValue到retMap集合
			retMap.put("resultIds", resultIdRetList);
			retMap.put("startTime", startTimeValue);
			retMap.put("endTime", endTimeValue);
			retMap.put("interval", "3min");
			// 返回
			return JSON.toJSONString(retMap);
		} catch (Exception e) {
			// 返回错误码
			return "500";
		}
	}

	/**
	 * 处理GridsPoints文件数据
	 */
	@RequestMapping("/dealGridsPoints")
	public void dealGridsPoints() throws Exception {
		// 获取开始时间
		long startMoment = System.currentTimeMillis();
		// 处理GridsPoints文件数据
		gridsPointsService.dealGridsPoints();
		// 获取结束时间
		long endMoment = System.currentTimeMillis();
		// 打印后台处理GridsPoints文件使用时间
		log.error("### 处理GridsPoints文件使用时间:" + (endMoment - startMoment) + "ms");
	}
}