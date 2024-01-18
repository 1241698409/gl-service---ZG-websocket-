package com.baosight.gl.service.ht;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baosight.gl.mapper.db2.HtMapper;
import com.baosight.gl.service.ht.constant.GridsPointsConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("all")
public class HtServiceImpl implements HtService {

//	@Autowired
	@Autowired(required = false)
	HtMapper htMapper;

	@Autowired
	InverseDistanceService inverseDistanceService;

	SimpleDateFormat Format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat Format2 = new SimpleDateFormat("yyyyMMddHHmmss");

	@Override
	public List<HashMap> queryOreCoke(HashMap paramsMap) {
		return htMapper.queryOreCoke(paramsMap);
	}

	@Override
	public HashMap queryCurrentAirFlow(HashMap paramsMap) {
		return htMapper.queryCurrentAirFlow(paramsMap);
	}

	@Override
	public void insertAirFlowIndex(HashMap paramsMap) {
		htMapper.insertAirFlowIndex(paramsMap);
	}

	@Override
	public List<HashMap> queryAirFlowByTimes(HashMap paramsMap) {
		return htMapper.queryAirFlowByTimes(paramsMap);
	}

	@Override
	public void dealAirFlow(int typeKey, String airFlowName) throws Exception {
		// 获取开始时间
		long startMoment = System.currentTimeMillis();
		// 设置参数
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("type", typeKey);
		// 查询当前气流场数据库文件名
		HashMap currentAirFlowMap = queryCurrentAirFlow(paramsMap);
		// 判断currentAirFlowMap是否为null
		currentAirFlowMap = currentAirFlowMap == null ? new HashMap<>() : currentAirFlowMap;
		// 获取数据库当前原始文件名
		Object currentOriginalFileName = currentAirFlowMap.get("ORIGINAL_FILE_NAME");
		// 获取当前气流场海仿提供文件名
		String currentHfFileName = getCurrentHfFileName();
		// 判断数据库和海仿提供文件名是否一致
		if (!currentHfFileName.equals(currentOriginalFileName)) {
			// 如果不一致，开始通过反距离加权法计算仿真数据
			HashMap InverseDistanceMap = inverseDistanceService.countInverseDistance(currentHfFileName, airFlowName);
			// 提取气流场海仿提供文件名中时间字符
			String currentHfFileTime = currentHfFileName.substring(6, 20);
			// 提供时间字符年份
			String year = currentHfFileTime.substring(0, 4);
			// 提取时间字符月份
			String month = currentHfFileTime.substring(0, 6);
			// 提取时间字符日期
			String day = currentHfFileTime.substring(0, 8);
			// 声明separator
			String separator = File.separator;
			// 拼接路径后缀
			StringBuffer sbPath = new StringBuffer();
			sbPath.append(year);
			sbPath.append(separator);
			sbPath.append(month);
			sbPath.append(separator);
			sbPath.append(day);
			sbPath.append(separator);
			sbPath.append(currentHfFileTime);
			sbPath.append(".json");
			// 声明sourcePath
			String sourcePath = "";
			// 声明destPath
			String destPath = "";
			// 判断airFlowName
			switch (airFlowName) {
			case "u":
				sourcePath = GridsPointsConstant.FILE_PATH_COUNT_U;
				destPath = GridsPointsConstant.FILE_PATH_COPY_U + sbPath.toString();
				break;
			case "p":
				sourcePath = GridsPointsConstant.FILE_PATH_COUNT_P;
				destPath = GridsPointsConstant.FILE_PATH_COPY_P + sbPath.toString();
				break;
			case "t":
				sourcePath = GridsPointsConstant.FILE_PATH_COUNT_T;
				destPath = GridsPointsConstant.FILE_PATH_COPY_T + sbPath.toString();
				break;
			}
			// copy文件
			copyFileUsingStream(sourcePath, destPath);
			// 获取结束时间
			long endMoment = System.currentTimeMillis();
			// 格式化countStartTime、countEndTime、countTime
			String countStartTime = Format1.format(startMoment);
			String countEndTime = Format1.format(endMoment);
			String countTime = String.valueOf(endMoment - startMoment);
			// 设置参数
			HashMap airParamsMap = new HashMap<>();
			airParamsMap.put("originalFileName", currentHfFileName);
			airParamsMap.put("originalFileTime", convertTimeFormat(currentHfFileTime));
			airParamsMap.put("targetFilePath", destPath);
			airParamsMap.put("type", typeKey);
			airParamsMap.put("countStartTime", countStartTime);
			airParamsMap.put("countEndTime", countEndTime);
			airParamsMap.put("countTime", countTime);
			airParamsMap.put("originalMinValue", InverseDistanceMap.get("originalMinValue"));
			airParamsMap.put("originalMaxValue", InverseDistanceMap.get("originalMaxValue"));
			airParamsMap.put("countMinValue", InverseDistanceMap.get("countMinValue"));
			airParamsMap.put("countMaxValue", InverseDistanceMap.get("countMaxValue"));
			// 处理最新的海仿数据到数据库
			insertAirFlowIndex(airParamsMap);
		}
	}

	@Override
	public String getCurrentHfFileName() throws Exception {
		String path = GridsPointsConstant.FILE_PATH_UPT;
		List list = Files.walk(Paths.get(path)).filter(Files::isRegularFile).collect(Collectors.toList());
		String filePath = list.get(list.size() - 1).toString();
		File tempFile = new File(filePath.trim());
		String fileName = tempFile.getName();
		return fileName;
	}

	@Override
	public String readAirFlowFile(String airFlowPath) throws Exception {
		// 声明返回值
		String retAirFlow = "";
		// 声明FileInputStream、InputStreamReader、BufferedReader
		FileInputStream fileInputStream = new FileInputStream(airFlowPath);
		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		// 声明lineTxt
		String lineTxt = null;
		// 遍历读取文件
		while ((lineTxt = bufferedReader.readLine()) != null) {
			retAirFlow = lineTxt;
		}
		// 关闭全部文件流
		fileInputStream.close();
		inputStreamReader.close();
		bufferedReader.close();
		// 返回
		return retAirFlow;
	}

	@Override
	public void copyFileUsingStream(String source, String dest) throws Exception {
		// 声明destFile
		File destFile = new File(dest);
		// 判断destFile路径是否存在
		if (!destFile.getParentFile().exists()) {
			// 不存在则创建路径
			destFile.getParentFile().mkdirs();
		}
		// copy文件
		InputStream is = new FileInputStream(source);
		OutputStream os = new FileOutputStream(dest);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) > 0) {
			os.write(buffer, 0, length);
		}
		is.close();
		os.close();
	}

	public String convertTimeFormat(String formatTime) throws Exception {
		Date formatDate = Format2.parse(formatTime);
		String retTime = Format1.format(formatDate);
		return retTime;
	}
}