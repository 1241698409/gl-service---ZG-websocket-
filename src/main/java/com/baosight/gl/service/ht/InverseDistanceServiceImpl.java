package com.baosight.gl.service.ht;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baosight.gl.service.ht.constant.GridsPointsConstant;
import com.baosight.gl.service.ht.domain.Point;
import com.baosight.gl.service.ht.thread.CountUTask;
import com.baosight.gl.utils.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("all")
public class InverseDistanceServiceImpl implements InverseDistanceService {

	/**
	 * @description 使用反距离加权法计算仿真数据
	 * 计算压力、温度仿真数据结果
	 */
	@Override
	public HashMap countInverseDistance(String uptFileName, String airFlowName) throws Exception {
		// 声明inverseDistanceRetMap集合
		HashMap inverseDistanceRetMap = new HashMap<>();
		/**
		 * 获取计算气流场基础数据项
		 */
		// 声明UPT文件路径
		String filePathUPT = GridsPointsConstant.FILE_PATH_UPT + uptFileName;
		// 获取UPT文件数据
		Map<String, double[]> uptMap = readFileUPT(filePathUPT);
		// 获取速度、压力、温度数组
		double[] uArrays = uptMap.get("uArrays");
		double[] pArrays = uptMap.get("pArrays");
		double[] tArrays = uptMap.get("tArrays");
		// 声明originalExtremumValueMap集合
		HashMap<String, String> originalExtremumValueMap = new HashMap<>();
		// 声明rResult集合
		List<Double> rResult = new ArrayList<>();
		// 声明UTP_COUNT_PATH
		String UPT_COUNT_PATH = "";
		// 判断airFlowName
		switch (airFlowName) {
		case "u":
			// 根据反距插值算法：计算速度场数据项
			rResult = InverseDistanceU(uArrays);
			// 根据airFlowName、uArrays计算：最小值、最大值
			originalExtremumValueMap = countExtremum(airFlowName, uArrays);
			// 赋值UPT_COUNT_PATH：速度场计算文件路径
			UPT_COUNT_PATH = GridsPointsConstant.FILE_PATH_COUNT_U;
			break;
		case "p":
			// 根据反距插值算法：计算压力场数据项
			rResult = InverseDistancePT(airFlowName, pArrays);
			// 根据airFlowName、pArrays计算：最小值、最大值
			originalExtremumValueMap = countExtremum(airFlowName, pArrays);
			// 赋值UPT_COUNT_PATH：压力场计算文件路径
			UPT_COUNT_PATH = GridsPointsConstant.FILE_PATH_COUNT_P;
			break;
		case "t":
			// 根据反距插值算法：计算温度场数据项
			rResult = InverseDistancePT(airFlowName, tArrays);
			// 根据airFlowName、tArrays计算：最小值、最大值
			originalExtremumValueMap = countExtremum(airFlowName, tArrays);
			// 赋值UPT_COUNT_PATH：温度场计算文件路径
			UPT_COUNT_PATH = GridsPointsConstant.FILE_PATH_COUNT_T;
			break;
		}
		// 写rResult到UPT_COUNT_PATH路径文件
		writeFileUPT(rResult, UPT_COUNT_PATH);
		// 处理气流场最小值、最大值到inverseDistanceRetMap集合
		inverseDistanceRetMap.put("originalMinValue", originalExtremumValueMap.get("minValue"));
		inverseDistanceRetMap.put("originalMaxValue", originalExtremumValueMap.get("maxValue"));
		// 获取二次计算气流场数据项：最小值、最大值 --可注释
		Double[] DataArraysU = rResult.toArray(new Double[rResult.size()]);
		double[] DataArraysL = Stream.of(DataArraysU).mapToDouble(Double::doubleValue).toArray();
		HashMap<String, String> ExtremumValueMap = countExtremum(airFlowName, DataArraysL);
		inverseDistanceRetMap.put("countMinValue", ExtremumValueMap.get("minValue"));
		inverseDistanceRetMap.put("countMaxValue", ExtremumValueMap.get("maxValue"));
		// 返回
		return inverseDistanceRetMap;
	}

	@Override
	public List<Double> InverseDistancePT(String airFlowName, double[] ptArrays) throws Exception {
		// 声明grids_xzy.json文件路径
		String filePathGridsJsonXzy = GridsPointsConstant.FILE_PATH_GRIDS_XZY_JSON;
		// 获取grids_xzy.json文件数据
		double[] gridsJsonXzyArrays = readFileGridsPoints(filePathGridsJsonXzy);
		// 声明potins_xzy.json文件路径
		String filePathPointsJsonXzy = GridsPointsConstant.FILE_PATH_POINTS_XZY_JSON;
		// 获取potins_xzy.json文件数据
		double[] pointsJsonXzyArrays = readFileGridsPoints(filePathPointsJsonXzy);
		// 声明Point
		Point point = new Point();
		// 声明变量：tall、width、height、radius
		int tall = 100;
		int width = 100;
		int height = 100;
		double radius = 1.3;
		// 赋值standPoints、valuePoints
		point.standPoints = gridsJsonXzyArrays;
		point.valuePoints = ptArrays;
		/**
		 * 根据反距插值算法：二次计算气流场数据项
		 */
		// 声明rResult集合
		List<Double> rResult = new ArrayList<>();
		// 获取pointsJsonArrays长度
		int pointsJsonArraysLength = pointsJsonXzyArrays.length / 3;
		// 循环遍历pointsJsonArrays
		for (int i = 0; i < pointsJsonArraysLength; i++) {
			// 获取x、y、z坐标
			double x = pointsJsonXzyArrays[i * 3];
			double y = pointsJsonXzyArrays[i * 3 + 1];
			double z = pointsJsonXzyArrays[i * 3 + 2];
			// 调用getZValue()方法
			double[] valueArrays = point.getZValue(x, y, z, radius, 7, airFlowName);
			// 处理valueArrays数组到rResult集合
			for (int j = 0; j < valueArrays.length; j++) {
				rResult.add(valueArrays[j]);
			}
		}
		// 返回
		return rResult;
	}

	@Override
	public List<Double> InverseDistanceU(double[] uArrays) throws Exception {
		// 声明grids_xyz.json文件路径
		String filePathGridsJsonXyz = GridsPointsConstant.FILE_PATH_GRIDS_XYZ_JSON;
		// 获取grids_xyz.json文件数据
		double[] gridsJsonXyzArrays = readFileGridsPoints(filePathGridsJsonXyz);
		// 声明变量：tall、width、height、radius
		/*int tall = 40;
		int width = 40;
		int height = 40;*/
		int tall = 100;
		int width = 100;
		int height = 100;
		double radius = 0.5;
		// 获取{ minX, maxX, minY, maxY, minZ, maxZ };
		double[] rang3Arrays = range3(gridsJsonXyzArrays);
		double minX = rang3Arrays[0];
		double maxX = rang3Arrays[1];
		double minY = rang3Arrays[2];
		double maxY = rang3Arrays[3];
		double minZ = rang3Arrays[4];
		double maxZ = rang3Arrays[5];
		// 赋值xStart、yStart、zStart
		double xStart = minX;
		double yStart = minY;
		double zStart = minZ;
		// 赋值stepX、stepY、stepZ
		double stepX = (maxX - minX) / (width);
		double stepY = (maxY - minY) / (tall);
		double stepZ = (maxZ - minZ) / (height);
		/**
		 * 根据反距插值算法：二次计算气流场数据项
		 */
		// 声明rResult集合
		List<Double> rResult = new ArrayList<>();
		// 线程数:10
		int RunSize = tall / 10;
		// 声明executor、countDownLatch
		ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(RunSize);
		CountDownLatch countDownLatch = new CountDownLatch(RunSize);
		// 声明rResultList集合
		List<List<Double>> rResultList = new ArrayList<>();
		// 遍历RunSize
		for (int i = 0; i < RunSize; i++) {
			List<Double> rResultValue = new ArrayList<>();
			rResultList.add(rResultValue);
		}
		// 遍历线程size
		for (int i = 0; i < RunSize; i++) {
			CountUTask countUTask = new CountUTask();
			// 声明Point
			Point point = new Point();
			// 赋值standPoints、valuePoints
			point.standPoints = gridsJsonXyzArrays;
			point.valuePoints = uArrays;
			countUTask.countDownLatch = countDownLatch;
			countUTask.startIndex = i * 10;
			countUTask.endIndex = (i + 1) * 10;
			countUTask.width = width;
			countUTask.height = height;
			countUTask.xStart = xStart;
			countUTask.yStart = yStart;
			countUTask.zStart = zStart;
			countUTask.stepX = stepX;
			countUTask.stepY = stepY;
			countUTask.stepZ = stepZ;
			countUTask.radius = radius;
			countUTask.point = point;
			countUTask.rResult = rResultList.get(i);
			executor.execute(countUTask);
		}
		countDownLatch.await();
		// 遍历rResultList
		for (int i = 0; i < rResultList.size(); i++) {
			List<Double> rResultValue = rResultList.get(i);
			rResult.addAll(rResultValue);
		}
		// 返回
		return rResult;
	}

	@Override
	public HashMap<String, String> countExtremum(String airFlowName, double[] valueArrays) {
		// 声明extremumList集合
		List<Double> extremumList = new ArrayList<>();
		// 声明最小值
		double minValue;
		// 声明最大值
		double maxValue;
		// 判断airFlowName
		switch (airFlowName) {
		case "u":
			// 根据公式：Math.sqrt(x*x + y*y + z* z)：计算速度场最大值，最小值
			for (int i = 0; i < valueArrays.length; i += 3) {
				// 获取x、z、y
				double x = valueArrays[i];
				double z = valueArrays[i + 1];
				double y = valueArrays[i + 2];
				// 求平方根
				double sqrtValue = Math.sqrt(x * x + y * y + z * z);
				// 添加sqrtValue到extremumList集合
				extremumList.add(sqrtValue);
			}
			break;
		case "p":
		case "t":
			// 遍历valueArrays数组，处理到extremumList集合
			for (int i = 0; i < valueArrays.length; i++) {
				extremumList.add(valueArrays[i]);
			}
			break;
		}
		// 排序
		Collections.sort(extremumList);
		// 获取最小值
		minValue = extremumList.get(0);
		// 获取最大值
		maxValue = extremumList.get(extremumList.size() - 1);
		// 声明ExtremumValueMap
		HashMap<String, String> ExtremumValueMap = new HashMap<>();
		// 处理minValue、maxValue到ExtremumValueMap集合
		ExtremumValueMap.put("minValue", String.valueOf(minValue));
		ExtremumValueMap.put("maxValue", String.valueOf(maxValue));
		// 返回
		return ExtremumValueMap;
	}

	/**
	 * @description 获取每帧upt文件数据
	 * @param upt文件路径
	 */
	@Override
	public Map<String, double[]> readFileUPT(String filePath) throws Exception {
		// 声明返回值
		Map<String, double[]> retMap = new HashMap<String, double[]>();
		// U-P-T文件：声明FileInputStream
		FileInputStream fileInputStream = new FileInputStream(filePath);
		// U-P-T文件：声明InputStreamReader
		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
		// U-P-T文件：声明BufferedReader
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		// 声明lineTxt
		String lineTxt = null;
		// 声明uList
		List<Double> uList = new ArrayList<>();
		// 声明pList
		List<Double> pList = new ArrayList<>();
		// 声明tList
		List<Double> tList = new ArrayList<>();
		// 遍历读取文件
		while ((lineTxt = bufferedReader.readLine()) != null) {
			double[] utpArrays = CollectionUtils.toDoubleArrays(lineTxt, ",");
			// 获取速度u：x
			double ux = utpArrays[0];
			// 获取速度u：y
			double uy = utpArrays[1];
			// 获取速度u：z
			double uz = utpArrays[2];
			// 处理u：x、z、y到uList集合
			uList.add(ux);
			uList.add(uy);
			uList.add(uz);
			// 获取压力p
			double p = utpArrays[3];
			pList.add(p);
			// 获取温度t
			double t = utpArrays[4];
			tList.add(t);
		}
		// 关闭全部文件流
		fileInputStream.close();
		inputStreamReader.close();
		bufferedReader.close();
		// 处理uList集合
		Double[] uArrays = uList.toArray(new Double[uList.size()]);
		double[] uArraysD = Stream.of(uArrays).mapToDouble(Double::doubleValue).toArray();
		// 处理pList集合
		Double[] pArrays = pList.toArray(new Double[pList.size()]);
		double[] pArraysD = Stream.of(pArrays).mapToDouble(Double::doubleValue).toArray();
		// 处理tList集合
		Double[] tArrays = tList.toArray(new Double[tList.size()]);
		double[] tArraysD = Stream.of(tArrays).mapToDouble(Double::doubleValue).toArray();
		// 处理uArrays、pArrays、tArrays数组
		retMap.put("uArrays", uArraysD);
		retMap.put("pArrays", pArraysD);
		retMap.put("tArrays", tArraysD);
		// 返回
		return retMap;
	}

	/**
	 * @description 获取grids.json、points.json文件
	 * @param grids、points文件路径
	 */
	@Override
	public double[] readFileGridsPoints(String filePath) throws Exception {
		// 声明返回值
		double[] retArrays = {};
		// grids、points文件：声明FileInputStream
		FileInputStream fileInputStream = new FileInputStream(filePath);
		// grids、points文件：声明InputStreamReader
		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
		// grids、points文件：声明BufferedReader
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		// 声明lineTxt
		String lineTxt = null;
		// 遍历读取文件
		while ((lineTxt = bufferedReader.readLine()) != null) {
			retArrays = CollectionUtils.toDoubleArrays(lineTxt, ",");
		}
		// 关闭全部文件流
		fileInputStream.close();
		inputStreamReader.close();
		bufferedReader.close();
		// 返回
		return retArrays;
	}

	/**
	 * @description 写u、p、t集合到对应文件
	 * @param1 u、p、t集合
	 * @param2 u.json、p.json、t.json文件路径
	 */
	@Override
	public void writeFileUPT(List<Double> lineRow, String filePath) throws Exception {
		// 声明fileWriter
		FileWriter fileWriter = new FileWriter(filePath);
		// 声明BufferedWriter
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		// 写入数据
		bufferedWriter.write(JSON.toJSONString(lineRow));
		// 刷新
		bufferedWriter.flush();
		// 关闭BufferedWriter
		bufferedWriter.close();
	}

	/**
	 * @description 求中心坐标：x,y,z：最大值，最小值
	 * @param data 数组参数：grids中心坐标
	 */
	@Override
	public double[] range3(double[] data) {
		// 声明listX、listY、listZ集合
		List<Double> listX = new ArrayList<>();
		List<Double> listY = new ArrayList<>();
		List<Double> listZ = new ArrayList<>();
		// 遍历数组
		for (int i = 0; i < data.length; i++) {
			if (i % 3 == 0) {
				listX.add(data[i]);
			} else if (i % 3 == 1) {
				listY.add(data[i]);
			} else {
				listZ.add(data[i]);
			}
		}
		// 获取listX：最小值和最大值
		double minX = listX.stream().reduce(Double::min).get();
		double maxX = listX.stream().reduce(Double::max).get();
		// 获取listY：最小值和最大值
		double minY = listY.stream().reduce(Double::min).get();
		double maxY = listY.stream().reduce(Double::max).get();
		// 获取listZ：最小值和最大值
		double minZ = listZ.stream().reduce(Double::min).get();
		double maxZ = listZ.stream().reduce(Double::max).get();
		// 赋值
		double[] ret = { minX, maxX, minY, maxY, minZ, maxZ };
		// 返回
		return ret;
	}

	/**
	 * @description 求速度场：x，y，z平方根
	 * @param data 数组参数：U速度场文件
	 */
	@Override
	public List<Double> sqrt(Double[] data) {
		// 声明retList集合
		List<Double> retList = new ArrayList<>();
		// 获取length
		int length = data.length / 3;
		// 根据length：遍历数组
		for (int i = 0; i < length; i++) {
			// 获取坐标x值
			double valueX = data[3 * i];
			// 获取坐标y值
			double valueY = data[3 * i + 1];
			// 获取坐标z值
			double valueZ = data[3 * i + 2];
			// 求平方根
			double valueSqrt = Math.sqrt(valueX * valueX + valueY * valueY + valueZ * valueZ);
			// 处理平方根到retList集合
			retList.add(valueSqrt);
		}
		// 返回
		return retList;
	}
}