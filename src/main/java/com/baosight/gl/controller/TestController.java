package com.baosight.gl.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baosight.gl.mapper.db1.GlMapper;
import com.baosight.gl.mapper.db1.ProcessMapper;
import com.baosight.gl.service.gl.GlService;
import com.baosight.gl.service.gl.ProcessService;
import com.baosight.gl.service.ht.HtService;
import com.baosight.gl.service.ht.InverseDistanceService;
import com.baosight.gl.service.ht.constant.GridsPointsConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@SuppressWarnings("all")
public class TestController {

	@Autowired
	HtService htService;

	@Autowired
	InverseDistanceService inverseDistanceService;

	@Autowired
	ProcessService processService;

	@Autowired
	ProcessMapper processMapper;

	@Autowired
	GlService glService;

	@Autowired
	GlMapper glMapper;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@CrossOrigin
	@PostMapping("/test123")
	public String test123() throws Exception {
		/*htService.dealAirFlow(1, "u");*/
		// 设置参数
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("type", 1);
		// 查询AirFlowMap集合
		HashMap AirFlowMap = htService.queryCurrentAirFlow(paramsMap);
		String FilePath = "C:\\Users\\SY\\Desktop\\u.json";
		System.out.println("读取完毕！");
		return htService.readAirFlowFile(FilePath);
	}

	@CrossOrigin
	@PostMapping("/queryMinMax")
	public void queryMinMax(String uptFileName) throws Exception {
		// 声明UPT文件路径
		String filePathUPT = GridsPointsConstant.FILE_PATH_UPT + uptFileName;
		// 获取UPT文件数据
		Map<String, double[]> uptMap = inverseDistanceService.readFileUPT(filePathUPT);
		// 获取速度、压力、温度数组
		double[] uArrays = uptMap.get("uArrays");
		double[] pArrays = uptMap.get("pArrays");
		double[] tArrays = uptMap.get("tArrays");
		// 声明extremumListP、extremumListT
		List<Double> extremumListP = new ArrayList<>();
		List<Double> extremumListT = new ArrayList<>();
		// 遍历pArrays
		for (int i = 0; i < pArrays.length; i++) {
			extremumListP.add(pArrays[i]);
		}
		// 遍历tArrays
		for (int i = 0; i < tArrays.length; i++) {
			extremumListT.add(tArrays[i]);
		}
		// 排序
		Collections.sort(extremumListP);
		Collections.sort(extremumListT);
		// 获取极值
		double minP = extremumListP.get(0);
		double maxP = extremumListP.get(extremumListP.size() - 1);
		double minT = extremumListT.get(0);
		double maxT = extremumListT.get(extremumListT.size() - 1);
		System.out.println("123");
	}

	@CrossOrigin
	@PostMapping("/countPressure")
	public void countPressure() throws Exception {
		// 两天数据量
		int rows = 1440;
		// 设置参数
		HashMap paramsMap = new HashMap<>();
		paramsMap.put("rows", rows);
		// 查询炉身静压数据项：T_CUTOFF_RESULT_AVG_1
		List<HashMap> PressureList_1 = glService.queryStaticPressure(paramsMap);
		// 查询热风压力数据项：T_CUTOFF_RESULT_AVG_9
		List<HashMap> PressureList_4 = glService.queryHotPressure(paramsMap);
		// 查询炉顶压力数据项：T_CUTOFF_RESULT_AVG_1
		List<HashMap> PressureList_5 = glService.queryTopPressure(paramsMap);
		// 声明PressureList集合
		List<HashMap> PressureList = new ArrayList<>();
		// 遍历压力数据项
		for (int i = 0; i < rows; i++) {
			// 获取压力数据项
			HashMap PressureMap_1 = PressureList_1.get(i);
			HashMap PressureMap_4 = PressureList_4.get(i);
			HashMap PressureMap_5 = PressureList_5.get(i);
			// 声明PressureMap集合
			HashMap PressureMap = new HashMap<>();
			// 处理压力数据项到PressureMap集合
			PressureMap.putAll(PressureMap_1);
			PressureMap.putAll(PressureMap_4);
			PressureMap.putAll(PressureMap_5);
			PressureMap.remove("RESULTID");
			// 处理PressureMap集合到PressureList集合
			PressureList.add(PressureMap);
		}
		// 处理PressureList数据
		List<Double> dealList = new ArrayList<>();
		for (int i = 0; i < PressureList.size(); i++) {
			Map valueMap = PressureList.get(i);
			//
			for (Object key : valueMap.keySet()) {
				Object value = valueMap.get(key);
				dealList.add(Double.parseDouble(value.toString()));
			}
		}
		//
		System.out.println(dealList.size());
		// 排序
		Collections.sort(dealList);
		// 获取最小值
		double minValue = dealList.get(0);
		// 获取最大值
		double maxValue = dealList.get(dealList.size() - 1);
		// 打印
		System.out.println("minValue:" + minValue);
		System.out.println("maxValue:" + maxValue);
	}
}