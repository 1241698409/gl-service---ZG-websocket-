package com.baosight.gl.service.gl;

import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.baosight.gl.controller.GlController;
import com.baosight.gl.mapper.db2.HtMapper;
import com.baosight.gl.mapper.db3.LGMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baosight.gl.constant.FormatConstant;
import com.baosight.gl.excel.mode.BlastFurnaceMode;
import com.baosight.gl.excel.utils.ReadExcelUtils;
import com.baosight.gl.mapper.db1.ProcessMapper;
import com.baosight.gl.utils.CollectionUtils;
import com.baosight.gl.utils.NumberFormatUtils;
import com.baosight.gl.utils.TimeUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("all")
public class ProcessServiceImpl implements ProcessService {

	@Autowired
	GlService glService;
	@Autowired
	GlController GlController;
	@Autowired
	ProcessMapper processMapper;
	@Autowired
	LGMapper lgMapper;
//	@Autowired
	@Autowired(required = false)
	HtMapper htMapper;
	@Value("${json.file.path}") // 从配置文件中获取文件路径
	private String jsonFilePath;
	@Override
	public void handleData(String getKey, String putKey, Map getMap, Map putMap) {
		Object value = getMap.get(getKey);
		value = (value == null || " ".equals(value)) ? "" : value;
		putMap.put(putKey, value);
	}

	@Override
	public List<BlastFurnaceMode> readBlastFurnaceExcel(String fileName) {
		InputStream is = this.getClass().getResourceAsStream("/templateExcel/" + fileName);
		List<BlastFurnaceMode> BlastFurnaceModeList = ReadExcelUtils.readExcel(is, BlastFurnaceMode.class);
		return BlastFurnaceModeList;
	}

	@Override
	public void getFieldStr(List<BlastFurnaceMode> BlastFurnaceModeList, HashMap paramsMap, String flag) {
		List<String> fieldList = new ArrayList<>();
		switch (flag) {
		case "field":
//			对流中的每个元素进行转换
			fieldList = BlastFurnaceModeList.stream().map(BlastFurnaceMode::getField).collect(Collectors.toList());
			break;
		case "average":
			fieldList = BlastFurnaceModeList.stream().map(BlastFurnaceMode::getAverage).collect(Collectors.toList());
			break;
		}
		// 处理fieldList集合：去重
		List disFieldList = fieldList.stream().distinct().collect(Collectors.toList());
		// 处理disFieldList集合：disFieldStr字符串
		String disFieldStr = disFieldList.toString().replace("[", "").replace("]", "");
		// 处理disFieldStr字符串到paramsMap集合
		paramsMap.put("fieldStr", disFieldStr);
	}

	@Override
	public List<HashMap> formatBlastFurnaceData(HashMap BlastFurnaceMap, List<BlastFurnaceMode> BlastFurnaceList,String resultId) {
		//添加累加器用于获取故障的点附近的数据
		int count = 0;
		// 声明BlastFurnaceRetList集合
		List<HashMap> BlastFurnaceRetList = new ArrayList<>();
		// BlastFurnaceList集合根据key分组    第二个参数 LinkedHashMap::new 指定了生成的 Map 使用 LinkedHashMap 来保持插入顺序。
		Map<Double, List<BlastFurnaceMode>> BlastFurnaceValueMap = BlastFurnaceList.stream().collect(Collectors.groupingBy(BlastFurnaceMode::getHeight, LinkedHashMap::new, Collectors.toList()));
		// 遍历BlastFurnaceValueMap集合
		for (Double key : BlastFurnaceValueMap.keySet()) {
			// 声明BlastFurnaceDealList集合
			List<HashMap> BlastFurnaceDealList = new ArrayList<>();
			// 获取BlastFurnaceValueList集合
			List<BlastFurnaceMode> BlastFurnaceValueList = BlastFurnaceValueMap.get(key);
			// 遍历BlastFurnaceValueList集合
			for (BlastFurnaceMode blastFurnaceMode : BlastFurnaceValueList) {
				// 获取标高
				Double height = blastFurnaceMode.getHeight();
				// 获取角度
				Double angle = blastFurnaceMode.getAngle();
				// 获取起始角度用于绘制热负荷
				Double angle2 = blastFurnaceMode.getAngle2();
				// 获取插入深度
				Double depth = blastFurnaceMode.getDepth();
				// 获取字段
				String field = blastFurnaceMode.getField();
				// 获取每块大小
				Double size = blastFurnaceMode.getSize();
				//平均值2（热电偶判断异常）
				String average = blastFurnaceMode.getAverage2();
				// 根据字段获取数值
				Object valueO = BlastFurnaceMap.get(field);
				// 判断获取的数值是否为空
				Double valueD = (valueO == null || " ".equals(valueO)) ? 0d : Double.valueOf(valueO.toString());
				// 声明BlastFurnaceDealMap集合
				HashMap BlastFurnaceDealMap = new HashMap<>();
				// 处理获取的数据到BlastFurnaceDealMap集合
				BlastFurnaceDealMap.put("field", field);
				BlastFurnaceDealMap.put("height", height);
				BlastFurnaceDealMap.put("angle", angle);
				if (depth != null) {
					BlastFurnaceDealMap.put("depth", depth);
				}
				if (size != null) {
					BlastFurnaceDealMap.put("size", size);
				}
				if (angle2 != null) {
					BlastFurnaceDealMap.put("angle2", angle2);
				}
				if (average != null && (valueD > Double.valueOf(average) || valueD < 0)) {
					// 获取角度的上层以及下层小于该角度和大于该角度的4个点的平均值
					double avg = calculateAverageValue(BlastFurnaceMap,BlastFurnaceValueMap.keySet(), BlastFurnaceValueMap,height,angle);
					BlastFurnaceDealMap.put("value", avg);
					BlastFurnaceDealMap.put("value2", 1);
				} else {
					BlastFurnaceDealMap.put("value", valueD);
				}
				// 处理BlastFurnaceDealMap到BlastFurnaceDealList集合
				BlastFurnaceDealList.add(BlastFurnaceDealMap);
			}
			// 声明BlastFurnaceRetMap集合
			HashMap BlastFurnaceRetMap = new HashMap<>();
			// 获取desc描述
			String desc = BlastFurnaceValueList.get(0).getDesc();
			// 获取height标高
			Double height = BlastFurnaceValueList.get(0).getHeight();
			// 处理desc到BlastFurnaceRetMap集合
			BlastFurnaceRetMap.put("name", desc);
			// 处理height到BlastFurnaceRetMap集合
			BlastFurnaceRetMap.put("height", height);
			// 处理BlastFurnaceDealList到BlastFurnaceRetMap集合
			BlastFurnaceRetMap.put("info", BlastFurnaceDealList);
			//加时间为了显示历史回放的时候时间标签
			if(count==0){
				// 根据time：查询T_CUTOFF_RESULT表时间
				Long timeStamp = null;
				try {
					timeStamp = queryResultTimeStamp(1, resultId,"resultId");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				BlastFurnaceRetMap.put("timeStamp", timeStamp);
			}
			// 处理BlastFurnaceRetMap到BlastFurnaceRetList集合
			BlastFurnaceRetList.add(BlastFurnaceRetMap);
			count++;
		}
		// 返回
		return BlastFurnaceRetList;
	}
	@Override
	public List<HashMap> formatBlastFurnaceData(HashMap BlastFurnaceMap, List<BlastFurnaceMode> BlastFurnaceList,String resultId,String salg) {
		//添加累加器用于获取故障的点附近的数据
		int count = 0;
		// 声明BlastFurnaceRetList集合
		List<HashMap> BlastFurnaceRetList = new ArrayList<>();
		// BlastFurnaceList集合根据key分组
		Map<Double, List<BlastFurnaceMode>> BlastFurnaceValueMap = BlastFurnaceList.stream().collect(Collectors.groupingBy(BlastFurnaceMode::getHeight, LinkedHashMap::new, Collectors.toList()));
		// 遍历BlastFurnaceValueMap集合
		for (Double key : BlastFurnaceValueMap.keySet()) {
			// 声明BlastFurnaceDealList集合
			List<HashMap> BlastFurnaceDealList = new ArrayList<>();
			// 获取BlastFurnaceValueList集合
			List<BlastFurnaceMode> BlastFurnaceValueList = BlastFurnaceValueMap.get(key);
			// 遍历BlastFurnaceValueList集合
			for (BlastFurnaceMode blastFurnaceMode : BlastFurnaceValueList) {
				// 获取标高
				Double height = blastFurnaceMode.getHeight();
				// 获取角度
				Double angle = blastFurnaceMode.getAngle();
				// 获取插入深度
				Double depth = blastFurnaceMode.getDepth();
				// 获取字段
				String field = blastFurnaceMode.getField();
				// 获取每块大小
				Double size = blastFurnaceMode.getSize();
				//平均值2（热电偶判断异常）
				String average = blastFurnaceMode.getAverage2();
				// 根据字段获取数值
				Object valueO = BlastFurnaceMap.get(field);
				// 判断获取的数值是否为空
				Double valueD = (valueO == null || " ".equals(valueO)) ? 0d : Double.valueOf(valueO.toString());
				// 声明BlastFurnaceDealMap集合
				HashMap BlastFurnaceDealMap = new HashMap<>();
				// 处理获取的数据到BlastFurnaceDealMap集合
				BlastFurnaceDealMap.put("field", field);
				BlastFurnaceDealMap.put("height", height);
				BlastFurnaceDealMap.put("angle", angle);
				if (depth != null) {
					BlastFurnaceDealMap.put("depth", depth);
				}
				if (size != null) {
					BlastFurnaceDealMap.put("size", size);
				}
					if (salg != "heatMap"&average != null && (valueD > Double.valueOf(average) || valueD < 0)) {
						// 获取角度的上层以及下层小于该角度和大于该角度的4个点的平均值
						double avg = calculateAverageValue(BlastFurnaceMap, BlastFurnaceValueMap.keySet(), BlastFurnaceValueMap, height, angle);
						BlastFurnaceDealMap.put("value", avg);
						BlastFurnaceDealMap.put("value2", 1);
					} else {
						BlastFurnaceDealMap.put("value", valueD);
					}
					// 处理BlastFurnaceDealMap到BlastFurnaceDealList集合
					BlastFurnaceDealList.add(BlastFurnaceDealMap);
			}
			// 声明BlastFurnaceRetMap集合
			HashMap BlastFurnaceRetMap = new HashMap<>();
			// 获取desc描述
			String desc = BlastFurnaceValueList.get(0).getDesc();
			// 获取height标高
			Double height = BlastFurnaceValueList.get(0).getHeight();
			// 处理desc到BlastFurnaceRetMap集合
			BlastFurnaceRetMap.put("name", desc);
			// 处理height到BlastFurnaceRetMap集合
			BlastFurnaceRetMap.put("height", height);
			// 处理BlastFurnaceDealList到BlastFurnaceRetMap集合
			BlastFurnaceRetMap.put("info", BlastFurnaceDealList);
			if(count==0){
				// 根据time：查询T_CUTOFF_RESULT表时间
				Long timeStamp = null;
				try {
					timeStamp = queryResultTimeStamp(1, resultId,"resultId");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				BlastFurnaceRetMap.put("timeStamp", timeStamp);
			}
			// 处理BlastFurnaceRetMap到BlastFurnaceRetList集合
			BlastFurnaceRetList.add(BlastFurnaceRetMap);
			count++;
		}
		// 返回
		return BlastFurnaceRetList;
	}
	private double calculateAverageValue(HashMap BlastFurnaceMap, Set<Double> keySet, Map<Double, List<BlastFurnaceMode>>BlastFurnaceValueMap,Double nowheight,Double nowangle) {
		// 获取上层和下层
		List<Double> upperHeight = new ArrayList<>();
		List<Double> lowerHeight = new ArrayList<>();

		for (Double key : keySet) {
			if (key < nowheight) {
				lowerHeight.add(key);
			} else if (key > nowheight) {
				upperHeight.add(key);
			}
		}

		// 对上层和下层排序（升序）
		Collections.sort(upperHeight);
		Collections.sort(lowerHeight);

		// 获取上层和下层的每个角度对应的数值
		List<Double> upperValues = new ArrayList<>();
		List<Double> lowerValues = new ArrayList<>();
		//需要先按照角度降序排序
		List<BlastFurnaceMode> upperBlastFurnaceValueList = BlastFurnaceValueMap.get(upperHeight.get(0)).stream()
				.sorted(Comparator.comparingDouble(BlastFurnaceMode::getAngle))
				.collect(Collectors.toList());;
		List<BlastFurnaceMode> lowerBlastFurnaceValueList = BlastFurnaceValueMap.get(lowerHeight.get(lowerHeight.size()-1)).stream()
				.sorted(Comparator.comparingDouble(BlastFurnaceMode::getAngle))
				.collect(Collectors.toList());;;
		for (BlastFurnaceMode blastFurnaceMode : upperBlastFurnaceValueList) {
			Double angle = blastFurnaceMode.getAngle();
			if (angle>nowangle) {
				Object valueO = BlastFurnaceMap.get(blastFurnaceMode.getField());
				Double valueD = (valueO == null || " ".equals(valueO)) ? 0d : Double.valueOf(valueO.toString());
				upperValues.add(valueD);
				break;
			}
		}
		int uppercount=0;
		//没有处理0度的数据点
		for (BlastFurnaceMode blastFurnaceMode : upperBlastFurnaceValueList) {
			Double angle = blastFurnaceMode.getAngle();
			if (angle<nowangle) {
				uppercount++;
			}else {
				Object valueO = BlastFurnaceMap.get(upperBlastFurnaceValueList.get(uppercount-1).getField());
				Double valueD = (valueO == null || " ".equals(valueO)) ? 0d : Double.valueOf(valueO.toString());
				upperValues.add(valueD);
				break;
			}
		}
		for (BlastFurnaceMode blastFurnaceMode : lowerBlastFurnaceValueList) {
			Double angle = blastFurnaceMode.getAngle();
			if (angle>nowangle) {
				Object valueO = BlastFurnaceMap.get(blastFurnaceMode.getField());
				Double valueD = (valueO == null || " ".equals(valueO)) ? 0d : Double.valueOf(valueO.toString());
				lowerValues.add(valueD);
				break;
			}
		}
		int lowercount=0;
		for (BlastFurnaceMode blastFurnaceMode : lowerBlastFurnaceValueList) {
			Double angle = blastFurnaceMode.getAngle();
			if (angle<nowangle) {
				lowercount++;
			}else {
				Object valueO = BlastFurnaceMap.get(lowerBlastFurnaceValueList.get(lowercount-1).getField());
				Double valueD = (valueO == null || " ".equals(valueO)) ? 0d : Double.valueOf(valueO.toString());
				lowerValues.add(valueD);
				break;
			}
		}

		// 计算上层和下层的平均值
		double upperAverage = calculateAverage(upperValues);
		double lowerAverage = calculateAverage(lowerValues);

		// 返回平均值
		return (upperAverage + lowerAverage) / 2;
	}

	private double calculateAverage(List<Double> values) {
		if (values.isEmpty()) {
			return 0d;
		}

		double sum = 0;
		for (Double value : values) {
			sum += value;
		}

		return sum / values.size();
	}
	@Override
	public void dealFormatData(List<HashMap> FormatList, LinkedHashMap ValueHashMap, String flag) {
		for (int i = 0; i < FormatList.size(); i++) {
			HashMap FormatThermalLoadTempHashMap = FormatList.get(i);
			List<HashMap> infoList = (List<HashMap>) FormatThermalLoadTempHashMap.get("info");
			for (int j = 0; j < infoList.size(); j++) {
				HashMap infoHashMap = infoList.get(j);
				String angle = String.valueOf(infoHashMap.get("angle"));
				String height = String.valueOf(infoHashMap.get("height"));
				String field = String.valueOf(infoHashMap.get("field"));
				switch (flag) {
				case "put1":
					Double value1 = Double.parseDouble(String.valueOf(infoHashMap.get("value")));
					ValueHashMap.put(height + "@" + angle, value1);
					break;
				case "get1":
					if(ValueHashMap.get(height + "@" + angle)!=null) {
						Double value2 = Double.parseDouble(ValueHashMap.get(height + "@" + angle).toString());
						infoHashMap.put("value2", value2);
					}
					break;
				case "get2":
					Double value3 = Double.parseDouble(ValueHashMap.get(field).toString());
					infoHashMap.put("value2", value3);
					break;
				}

			}
		}
	}

	@Override
	public List<HashMap> getHeatMapValueDiffByList(List<HashMap> valueList) {
		// 声明retValueList集合
		List<HashMap> retValueList = new ArrayList<>();
		// 获取索引值
		int index = valueList.size() / 2;
		// 根据索引值，遍历retValueList集合
		for (int i = 0; i < index; i++) {
			// 获取valueMap1集合
			HashMap valueMap1 = valueList.get(i);
			// 获取valueMap2集合
			HashMap valueMap2 = valueList.get(i + index);
			// 计算valueMap1、valueMap2集合差值绝对值
			HashMap valueMap = getHeatMapValueDiffByMap(valueMap1, valueMap2);
			// 处理valueMap到retValueList集合
			retValueList.add(valueMap);
		}
		// 返回
		return retValueList;
	}

	@Override
	public HashMap getHeatMapValueDiffByMap(HashMap valueMap1, HashMap valueMap2) {
		// 声明retValueHashMap集合
		HashMap retValueHashMap = new HashMap<>();
		// 遍历valueMap1集合
		for (Object key : valueMap1.keySet()) {
			// 获取value1值
			Double value1 = Double.parseDouble(valueMap1.get(key).toString());
			// 获取value2值
			Double value2 = Double.parseDouble(valueMap2.get(key).toString());
			// 计算value1、value2差值绝对值
			// Double diffValue = NumberFormatUtils.formatDouble(Math.abs(value1 - value2));
			Double diffValue = NumberFormatUtils.formatDouble(value1 - value2);
			// 处理key、diffValue到retValueHashMap集合
			retValueHashMap.put(key, diffValue);
		}
		// 获取resultId
		/*int resultId = Integer.parseInt(valueMap1.get("RESULTID").toString());
		// 处理resultId到retValueHashMap集合
		retValueHashMap.put("RESULTID", resultId);*/
		// 返回
		return retValueHashMap;
	}

	@Override
	public HashMap getTapNoByResultId(int resultId, int number) throws Exception {
		// 声明retHashMap集合
		HashMap retHashMap = new HashMap<>();
		// 声明diffClockMap集合
		Map<String, Long> diffClockMap = new HashMap<>();
		// 声明sortDiffClockMap集合
		Map<String, Long> sortDiffClockMap = new LinkedHashMap();
		// 设置参数
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("cutoffId", 1);
		paramsMap.put("number", number);
		paramsMap.put("resultId", resultId);
		paramsMap.put("orderFetch", "order by resultId asc");
		// 根据resultId：查询cutoffResultList集合
		List<HashMap> cutoffResultList = processMapper.queryCutoffResult(paramsMap);
		// 获取时间数据项
		String time = cutoffResultList.get(0).get("CLOCK").toString();
		// 转换时间数据项为时间戳
		Long clock1 = FormatConstant.FORMAT1.parse(time).getTime();
		// 查询TapNoList集合
		List<HashMap> TapNoList = processMapper.queryTapNoByTimes(paramsMap);
		// 遍历TapNoList集合
		for (int i = 0; i < TapNoList.size(); i++) {
			// 获取tsTapMap集合
			HashMap TapNoMap = TapNoList.get(i);
			// 获取tapNo
			String tapNo = TapNoMap.get("TAPNO").toString();
			// 获取endTime
			String endTime = TapNoMap.get("ENDTIME").toString();
			// 转换endTime为时间戳
			Long clock2 = FormatConstant.FORMAT1.parse(endTime).getTime();
			// 求两个时间戳时间差绝对值
			Long diffClock = Math.abs(clock1 - clock2);
			// 处理tapNo、diffClock到diffClockMap集合
			diffClockMap.put(tapNo, diffClock);
		}
		// 对diffClockMap排序，处理到sortDiffClockMap集合
		diffClockMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEachOrdered(x -> sortDiffClockMap.put(x.getKey(), x.getValue()));
		// 获取sortDiffClockMap集合第一个key
		String retTapNo = CollectionUtils.getFirstKeyOrNull(sortDiffClockMap);
		// 处理retTapNo到retHashMap集合
		retHashMap.put("tapNo", retTapNo);
		// 返回
		return retHashMap;
	}

	@Override
	public HashMap getCutOff3ResultIdByResultId(int resultId, int number) throws Exception {
		// 声明retHashMap集合
		HashMap retHashMap = new HashMap<>();
		// 声明diffClockMap集合
		Map<String, Long> diffClockMap = new HashMap<>();
		// 声明sortDiffClockMap集合
		Map<String, Long> sortDiffClockMap = new LinkedHashMap();
		// 设置参数
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("cutoffId", 1);
		paramsMap.put("number", number);
		paramsMap.put("resultId", resultId);
		paramsMap.put("orderFetch", "order by resultId asc");
		// 根据resultId：查询cutoffResultList集合
		List<HashMap> cutoffResultList = processMapper.queryCutoffResult(paramsMap);
		// 获取时间数据项
		String time = cutoffResultList.get(0).get("CLOCK").toString();
		// 转换时间数据项为时间戳
		Long clock1 = FormatConstant.FORMAT1.parse(time).getTime();
		//根据根据resultId 查询对应的时间范围，根据时间范围查出对应的CutOff3ResultIdList集合
		// 查询CutOff3ResultIdList集合
		List<HashMap> CutOff3ResultIdList = processMapper.queryCutOff3ResultIdByTimes(paramsMap);
		// 遍历CutOff3ResultIdList集合
		for (int i = 0; i < CutOff3ResultIdList.size(); i++) {
			// 获取CutOff3ResultIdMap集合
			HashMap CutOff3ResultIdMap = CutOff3ResultIdList.get(i);
			// 获取cutOff3ResultId
			String cutOff3ResultId = CutOff3ResultIdMap.get("RESULTID").toString();
			// 获取clock
			String clock = CutOff3ResultIdMap.get("CLOCK").toString();
			// 转换clock为时间戳
			Long clock2 = FormatConstant.FORMAT1.parse(clock).getTime();
			// 求两个时间戳时间差绝对值
			Long diffClock = Math.abs(clock1 - clock2);
			// 处理cutOff3ResultId、diffClock到diffClockMap集合
			diffClockMap.put(cutOff3ResultId, diffClock);
		}
		// 对diffClockMap排序，处理到sortDiffClockMap集合
        //按照 diffClockMap 对象的值进行排序。这意味着根据时间差的大小，对键值对进行升序排序取第一个即为理当前ResultId数据最近的 CutOff3ResultId数据
		//forEachOrdered() 是流式操作中的终端操作之一。它会按照流的遍历顺序对流中的元素依次执行给定的操作。
        //与 forEach() 方法不同的是，forEachOrdered() 会保持流中元素的原始顺序，无论流是否是并行处理的。
		/*使用流排序和Collections.sort排序的区别
		1. 使用流进行排序时，可以通过调用sorted() 方法直接对流中的元素进行排序。而使用Collections.sort()
		需要传入一个Comparator来指定排序规则，并对List进行排序。
		2. 使用流进行排序可以通过链式调用其他操作，例如过滤、映射等，以便根据具体需求进行灵活的数据处理。
		而使用Collections.sort() 仅仅是对List进行排序，无法直接进行其他操作。
		3. 对于较小的数据集，两种方式的性能差异可能不明显。但是对于大型数据集，使用流进行排序可以使用并行处理（parallel）
		提高排序的效率，因为流操作可以自动将操作并行化，充分利用多核处理器的优势。而使用Collections.sort() 只能在单线程中进行排序。*/
		diffClockMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEachOrdered(x -> sortDiffClockMap.put(x.getKey(), x.getValue()));
		// 获取sortDiffClockMap集合第一个key
		String retCutOff3ResultId = CollectionUtils.getFirstKeyOrNull(sortDiffClockMap);
		// 处理resultId、cutOff3ResultId到retHashMap集合
		retHashMap.put("resultId", resultId);
		retHashMap.put("cutOff3ResultId", retCutOff3ResultId);
		// 返回
		return retHashMap;
	}

	@Override
	public HashMap getLayerClockByResultId(int resultId, int number) throws Exception {
		// 声明retHashMap集合
		HashMap retHashMap = new HashMap<>();
		// 声明diffClockMap集合
		Map<String, Long> diffClockMap = new HashMap<>();
		// 声明sortDiffClockMap集合
		Map<String, Long> sortDiffClockMap = new LinkedHashMap();
		// 设置参数
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("cutoffId", 1);
		paramsMap.put("number", number);
		paramsMap.put("resultId", resultId);
		paramsMap.put("orderFetch", "order by resultId asc");
		// 根据resultId：查询cutoffResultList集合
		List<HashMap> cutoffResultList = processMapper.queryCutoffResult(paramsMap);
		// 获取时间数据项
		String time = cutoffResultList.get(0).get("CLOCK").toString();
		// 转换时间数据项为时间戳
		Long clock1 = FormatConstant.FORMAT1.parse(time).getTime();
		// 查询layerClockList集合
		List<HashMap> layerClockList = processMapper.queryLayerClockByTimes(paramsMap);
		// 遍历layerClockList集合
		for (int i = 0; i < layerClockList.size(); i++) {
			// 获取layerClockMap集合
			HashMap layerClockMap = layerClockList.get(i);
			// 获取CLOCK
			String clock = layerClockMap.get("CLOCK").toString();
			// 转换clock为时间戳
			Long clock2 = FormatConstant.FORMAT1.parse(clock).getTime();
			// 求两个时间戳时间差绝对值
			Long diffClock = Math.abs(clock1 - clock2);
			// 处理clock、diffClock到diffClockMap集合
			diffClockMap.put(clock, diffClock);
		}
		// 对diffClockMap排序，处理到sortDiffClockMap集合,
		diffClockMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEachOrdered(x -> sortDiffClockMap.put(x.getKey(), x.getValue()));
		// 获取sortDiffClockMap集合第一个key
		String retLayerClock = CollectionUtils.getFirstKeyOrNull(sortDiffClockMap);
		// 处理retLayerClock到retHashMap集合
		retHashMap.put("clock", retLayerClock);
		// 返回
		return retHashMap;
	}

	@Override
	public HashMap getErodeSolidByResultId(int resultId, int number) throws Exception {
		// 声明retHashMap集合
		HashMap retHashMap = new HashMap<>();
		// 声明diffClockMap集合
		Map<String, Long> diffClockMap = new HashMap<>();
		// 声明sortDiffClockMap集合
		Map<String, Long> sortDiffClockMap = new LinkedHashMap();
		// 设置参数
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("cutoffId", 1);
		paramsMap.put("number", number);
		paramsMap.put("resultId", resultId);
		paramsMap.put("orderFetch", "order by resultId asc");
		// 根据resultId：查询cutoffResultList集合
		List<HashMap> cutoffResultList = processMapper.queryCutoffResult(paramsMap);
		// 获取时间数据项
		String time = cutoffResultList.get(0).get("CLOCK").toString();
		// 转换时间数据项为时间戳
		Long clock1 = FormatConstant.FORMAT1.parse(time).getTime();
		// 查询erodeSolidList集合  取了前后各7天的侵蚀时间来判断距离现在时间最近的一个时间就是侵蚀数据的时间
		List<HashMap> erodeSolidList = lgMapper.queryErodeSolidByTimes(paramsMap);
		// 遍历erodeSolidList集合
		for (int i = 0; i < erodeSolidList.size(); i++) {
			// 获取erodeSolidMap集合
			HashMap erodeSolidMap = erodeSolidList.get(i);
			// 获取STARTDATE
			String startTime = erodeSolidMap.get("STARTDATE").toString();
			// 转换startTime为时间戳p arse(),按照给定的SimpleDateFormat 对象的格式化存储来解析字符串
			Long clock2 = FormatConstant.FORMAT1.parse(startTime).getTime();
			// 求两个时间戳时间差绝对值
			Long diffClock = Math.abs(clock1 - clock2);
			// 处理startTime、diffClock到diffClockMap集合
			diffClockMap.put(startTime, diffClock);
		}
		// 对diffClockMap排序，处理到sortDiffClockMap集合
		diffClockMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEachOrdered(x -> sortDiffClockMap.put(x.getKey(), x.getValue()));
		// 获取sortDiffClockMap集合第一个key
		String retStartTime = CollectionUtils.getFirstKeyOrNull(sortDiffClockMap);
		// 处理retStartTime到retHashMap集合
		retHashMap.put("startTime", retStartTime);
		// 返回
		return retHashMap;
	}

	@Override
	public Long queryResultTimeStamp(int cutoffId, String time) throws Exception {
		// 设置时间参数
		HashMap timesParamsMap = new HashMap<>();
		timesParamsMap.put("cutoffId", cutoffId);
		timesParamsMap.put("endTime", time);
		timesParamsMap.put("orderFetch", "order by resultId desc fetch first 1 rows only");
		// 获取cutoffResultList集合
		List<HashMap> cutoffResultList = processMapper.queryCutoffResult(timesParamsMap);
		// 获取cutoffResultMap集合
		Map cutoffResultMap = cutoffResultList.get(0);
		// 获取clock时间
		String clock = cutoffResultMap.get("CLOCK").toString();
		// 转换clock时间成时间戳
		Long timeStamp = FormatConstant.FORMAT1.parse(clock).getTime();
		// 返回
		return timeStamp;
	}
	@Override
	public Long queryResultTimeStamp(int cutoffId, String str, String flag) throws Exception {
		// 设置时间参数
		HashMap timesParamsMap = new HashMap<>();
		timesParamsMap.put("cutoffId", cutoffId);
		if(flag=="resultId"){
			timesParamsMap.put("resultId", str);
		}else {
			timesParamsMap.put("endTime", str);
		}
		timesParamsMap.put("orderFetch", "order by resultId desc fetch first 1 rows only");
		// 获取cutoffResultList集合
		List<HashMap> cutoffResultList = processMapper.queryCutoffResult(timesParamsMap);
		// 获取cutoffResultMap集合
		Map cutoffResultMap = cutoffResultList.get(0);
		// 获取clock时间
		String clock = cutoffResultMap.get("CLOCK").toString();
		// 转换clock时间成时间戳
		Long timeStamp = FormatConstant.FORMAT1.parse(clock).getTime();
		// 返回
		return timeStamp;
	}
	@Override
	public LinkedHashMap dealBlastFurnaceHistory(HashMap blastFurnaceParamsMap) {
		// 声明BlastFurnaceDealMap集合
		LinkedHashMap<Object, Object> BlastFurnaceDealMap = new LinkedHashMap<>();
		// 获取参数
		List<BlastFurnaceMode> blastFurnaceList = (List<BlastFurnaceMode>) blastFurnaceParamsMap.get("blastFurnaceList");
		List<HashMap> dataList = (List<HashMap>) blastFurnaceParamsMap.get("dataList");
		int rows = (int) blastFurnaceParamsMap.get("rows");
		int type = (int) blastFurnaceParamsMap.get("type");
		// 遍历blastFurnaceList集合
		for (BlastFurnaceMode blastFurnaceMode : blastFurnaceList) {
			// 获取field字段
			String field = blastFurnaceMode.getField();
			// 获取height字段
			Double height = blastFurnaceMode.getHeight();
			// 获取角度
			Double angle = blastFurnaceMode.getAngle();
			// 判断type类型
			switch (type) {
			case 1:
				BlastFurnaceDealMap.put(field, new ArrayList<>());
				break;
			case 2:
				BlastFurnaceDealMap.put(field + "@" + height, new ArrayList<>());
				break;
			case 3:
				BlastFurnaceDealMap.put(field + "@" + height + "@" + angle, new ArrayList<>());
				break;
			}
		}
		// 倒叙遍历历史数据项
		for (int i = rows - 1; i >= 0; i--) {
			// 获取BlastFurnaceMap集合
			HashMap BlastFurnaceMap = dataList.get(i);
			// 遍历BlastFurnaceDealMap集合
			for (Object BlastFurnaceDealKey : BlastFurnaceDealMap.keySet()) {
				// 获取BlastFurnaceDealList集合
				List<Object> BlastFurnaceDealList = (List<Object>) BlastFurnaceDealMap.get(BlastFurnaceDealKey);
//				// 声明BlastFurnaceDealValue
				Object BlastFurnaceDealValue;
				//如果有时间则是查询历史60条，把时间格式传入
				if(BlastFurnaceMap.get("CLOCK")!=null){
					ArrayList linshi =new ArrayList();
					switch (type) {
						case 1:
							linshi.add(BlastFurnaceMap.get("CLOCK"));
							linshi.add(BlastFurnaceMap.get(BlastFurnaceDealKey));
							BlastFurnaceDealValue=linshi;
							break;
						case 2:
							linshi.add(BlastFurnaceMap.get(BlastFurnaceDealKey.toString().split("@")[0]));
							BlastFurnaceDealValue=linshi;
							break;
						case 3:
							linshi.add(BlastFurnaceMap.get(BlastFurnaceDealKey.toString().split("@")[0]));
							BlastFurnaceDealValue=linshi;
							break;
						default:
							BlastFurnaceDealValue=linshi;
					}
				}else{
					 BlastFurnaceDealValue = null;
					switch (type) {
						case 1:
							BlastFurnaceDealValue = BlastFurnaceMap.get(BlastFurnaceDealKey);
							break;
						case 2:
							BlastFurnaceDealValue = BlastFurnaceMap.get(BlastFurnaceDealKey.toString().split("@")[0]);
							break;
						case 3:
							BlastFurnaceDealValue = BlastFurnaceMap.get(BlastFurnaceDealKey.toString().split("@")[0]);
							break;
					}
				}
				// 判断type类型

				// 处理BlastFurnaceDealValue到BlastFurnaceDealList集合
				BlastFurnaceDealList.add(BlastFurnaceDealValue);
			}
		}
		// 返回
		return BlastFurnaceDealMap;
	}

	@Override
	public HashMap<String, Object> getHeatMapResultId(HashMap heatMapParamsMap) throws Exception {
		// 声明HeatMapResultIdHashMap集合
		HashMap<String, Object> HeatMapResultIdHashMap = new HashMap<>();
		// 声明HeatMapResultIdList集合
		List<Integer> HeatMapResultIdList = new ArrayList();
		// 获取resultId
		Object resultId = heatMapParamsMap.get("resultId");
		// 获取time
		Object time = heatMapParamsMap.get("time");
		// 获取row
		Integer rows = Integer.parseInt(heatMapParamsMap.get("rows").toString());
		// 设置rows
		rows = rows * 60;
		//Integer rows = 1500;
		// 设置参数
		HashMap paramsHashMap = new HashMap<>();
		paramsHashMap.put("cutoffId", 1);
		paramsHashMap.put("resultId", resultId);
		paramsHashMap.put("endTime", time);
		paramsHashMap.put("orderFetch", "order by resultId desc fetch first 1 rows only");
		// 根据resultId获取对应时间
		List<HashMap> cutoffResultList = processMapper.queryCutoffResult(paramsHashMap);
		// 获取当前时间
		String clock = String.valueOf(cutoffResultList.get(0).get("FORMATCLOCK"));
		// 根据clock：设置参数
		paramsHashMap.remove("resultId");
		paramsHashMap.put("endTime", clock);
		paramsHashMap.put("orderFetch", "order by resultId desc fetch first " + rows + " rows only");
		// 查询当前时间一个小时内：resultId集合
		cutoffResultList = processMapper.queryCutoffResult(paramsHashMap);
		// 处理cutoffResultList集合到HeatMapResultIdList集合
		for (int i = 0; i < cutoffResultList.size(); i++) {
			HeatMapResultIdList.add(Integer.parseInt(cutoffResultList.get(i).get("RESULTID").toString()));
		}
		// 判断HeatMapResultIdList长度
		if (HeatMapResultIdList.size() != rows) {
			return null;
		}
		// 处理HeatMapResultIdList集合
		String HeatMapResultIdStr = HeatMapResultIdList.toString().replace("[", "(").replace("]", ")");
		// 处理HeatMapResultIdStr、HeatMapResultIdBeforeStr到HeatMapResultIdHashMap集合
		HeatMapResultIdHashMap.put("HeatMapResultIdStr", HeatMapResultIdStr);
		// 处理HeatMapResultId、HeatMapResultIdBefore到HeatMapResultIdHashMap集合
		HeatMapResultIdHashMap.put("HeatMapResultId", HeatMapResultIdList);
		// 返回
		return HeatMapResultIdHashMap;
	}

	@Override
	public String getHeatMapResultIdHistory(HashMap heatMapParamsMap) throws Exception {
		// 声明HeatMapResultIdList集合
		List<Integer> HeatMapResultIdList = new ArrayList<>();
		// 声明HeatMapResultId
		Integer HeatMapResultId;
		// 获取rows
		Integer rows = Integer.parseInt(heatMapParamsMap.get("rows").toString()) * 60;
		// 获取time
		Object time = heatMapParamsMap.get("time");
		// 设置参数
		heatMapParamsMap.put("cutoffId", 1);
		heatMapParamsMap.put("endTime", time);
		heatMapParamsMap.put("orderFetch", "order by resultId desc fetch first" + rows + "rows only");
		// 根据rows、time,查询最新的resultIdList集合
		List<HashMap> resultIdList = processMapper.queryCutoffResult(heatMapParamsMap);
		// 遍历resultIdList集合
		for (int i = 0; i < resultIdList.size(); i += 60) {
			// 获取索引"j"
			int j = i == 0 ? i : i - 1;
			// 根据索引"j",获取resultIdHashMap集合
			HashMap resultIdHashMap = resultIdList.get(j);
			// 获取HeatMapResultId
			HeatMapResultId = Integer.parseInt(resultIdHashMap.get("RESULTID").toString());
			// 处理HeatMapResultId到HeatMapResultIdList集合
			HeatMapResultIdList.add(HeatMapResultId);
			// 获取clock时间
			String clock = resultIdHashMap.get("FORMATCLOCK").toString();
			// 根据clock时间，获取24小时前时间
			String beforeClock = TimeUtils.beforeHour(clock);
			// 重新赋值heatMapParamsMap参数集合
			heatMapParamsMap.put("endTime", beforeClock);
			heatMapParamsMap.put("orderFetch", "order by resultId desc fetch first 1 rows only");
			// 获取beforeResultIdList集合
			List<HashMap> beforeResultIdList = processMapper.queryCutoffResult(heatMapParamsMap);
			if (beforeResultIdList.size() == 0) {
				return null;
			}
			// 获取HeatMapResultId
			HeatMapResultId = Integer.parseInt(beforeResultIdList.get(0).get("RESULTID").toString());
			// 处理HeatMapResultId到HeatMapResultIdList集合
			HeatMapResultIdList.add(HeatMapResultId);
		}
		// 判断HeatMapResultIdList长度
		if (HeatMapResultIdList.size() < rows / 60 * 2) {
			return null;
		}
		// 根据HeatMapResultIdList处理字符串
		String HeatMapResultIdStr = HeatMapResultIdList.toString().replace("[", "(").replace("]", ")");
		// 返回
		return HeatMapResultIdStr;
	}
	@Override
	public String JsonFileService(String stringJson) throws Exception {
		String fileName = "json_" + LocalDate.now() + ".json"; // 根据当前日期生成文件名
		Path filePath = Paths.get(jsonFilePath, fileName); // 拼接文件路径
		FileWriter fileWriter = new FileWriter(filePath.toString());
		fileWriter.write(stringJson);
		fileWriter.close();
		HashMap HashMapParam =new HashMap();
		//注意时间和后台时间的格式对应问题，不对应会出现数据格式转换异常  这样写，sql不能加TIMESTAMP
		HashMapParam.put("creatTime", LocalDate.now()+" 00:00:00");
//		或者下面这么坐
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		String createTimeStr = LocalDateTime.now().format(formatter);
//		HashMapParam.put("creatTime", createTimeStr);
//		HashMapParam.put("creatTime", LocalDate.now());
		HashMapParam.put("filePath", filePath.toString());
		htMapper.setFilePath(HashMapParam);
		return "1";
	}
	@Override
		public String JsonFileService(String stringJson,Integer resultid) throws Exception {
		String fileName = "json_" + resultid+ ".json"; // 根据当前resultid生成文件名
		Path filePath = Paths.get(jsonFilePath, fileName); // 拼接文件路径
		FileWriter fileWriter = new FileWriter(filePath.toString());
		fileWriter.write(stringJson);
		fileWriter.close();
		HashMap HashMapParam =new HashMap();
		//注意时间和后台时间的格式对应问题，不对应会出现数据格式转换异常  这样写，sql不能加TIMESTAMP
		HashMapParam.put("creatTime", LocalDate.now()+" 00:00:00");
		HashMapParam.put("resultid", resultid);
//		或者下面这么坐
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		String createTimeStr = LocalDateTime.now().format(formatter);
//		HashMapParam.put("creatTime", createTimeStr);
//		HashMapParam.put("creatTime", LocalDate.now());
		HashMapParam.put("filePath", filePath.toString());
		htMapper.setFilePath(HashMapParam);
		return "1";
	}
	@Override
	public String JsonFileService(String stringJson,Integer resultid,HashMap resulthashMap) throws Exception {
		String CLOCK=resulthashMap.get("CLOCK").toString();
		//时间转换不熟悉
		long timestampLong = Long.parseLong(CLOCK);
		Timestamp timestamp = new Timestamp(timestampLong);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String formattedTimestamp = sdf.format(timestamp);
		String fileName = "json_" + resultid+"-"+formattedTimestamp+".json"; // 根据当前resultid生成文件名
		Path filePath = Paths.get(jsonFilePath, fileName); // 拼接文件路径
		FileWriter fileWriter = new FileWriter(filePath.toString());
		fileWriter.write(stringJson);
		fileWriter.close();
		HashMap HashMapParam =new HashMap();
		//注意时间和后台时间的格式对应问题，不对应会出现数据格式转换异常  这样写，sql不能加TIMESTAMP
		HashMapParam.put("creatTime", LocalDate.now()+" 00:00:00");
		HashMapParam.put("CLOCK", timestamp);
		HashMapParam.put("resultid", resultid);
//		或者下面这么坐
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		String createTimeStr = LocalDateTime.now().format(formatter);
//		HashMapParam.put("creatTime", createTimeStr);
//		HashMapParam.put("creatTime", LocalDate.now());
		HashMapParam.put("filePath", filePath.toString());
		htMapper.setFilePath(HashMapParam);
		return "1";
	}
	@Override
	public String JsonReader (String time) throws Exception {
		HashMap HashMapParam =new HashMap();
		//注意时间和后台时间的格式对应问题，不对应会出现数据格式转换异常
		HashMapParam.put("creatTime", LocalDate.now()+" 00:00:00");
		HashMapParam.put("resultid", "");
		HashMap JSONParam=htMapper.JsonReader(HashMapParam);
		String jsonString = new String(Files.readAllBytes(Paths.get(JSONParam.get("FILEPATH").toString())));
		return jsonString;
	}
	@Override
	public String JsonReader (String time,Integer resultid) throws Exception {
		HashMap HashMapParam =new HashMap();
		//注意时间和后台时间的格式对应问题，不对应会出现数据格式转换异常
		HashMapParam.put("creatTime", LocalDate.now()+" 00:00:00");
		HashMapParam.put("resultid", resultid);
		HashMap JSONParam=htMapper.JsonReader(HashMapParam);
		String jsonString = new String(Files.readAllBytes(Paths.get(JSONParam.get("FILEPATH").toString())));
		return jsonString;
	}
	@Override
	public void differenceresultid() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		//查询数据库中所有的ResultId(用来查询等值图的id)
		String resultid=GlController.queryResultIdbyTimes("1","","");
		List<Integer> resultidValue = objectMapper.convertValue(
				objectMapper.readTree(resultid).get("resultIds"),
				new TypeReference<List<Integer>>() {}
		);
		//查询本地历史回放数据数据库
		String localresultid=GlController.queryResultIdbyLocal("1","","");
		List<Integer> localresultidValue = objectMapper.convertValue(
				objectMapper.readTree(localresultid).get("resultIds"),
				new TypeReference<List<Integer>>() {}
		);
		//将list转为HashSet使用时间复杂度更低的对比值
		HashSet<Integer> set = new HashSet<>(localresultidValue);
		List<Integer> difference = new ArrayList<>();
		for (Integer num : resultidValue) {
			if (!set.contains(num)) {
				difference.add(num);
				//将本地数据库中没有的数据插入
				JsonFileService(GlController.queryThermocouple(num.toString()),num);
			}
		}
	}
}