package com.baosight.gl.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.baosight.gl.domain.CenterGrid;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@SuppressWarnings("all")
public class GrController {

	/**
	 * 计算仿真数据：中心点坐标位置
	 */
	@RequestMapping("/countGrid")
	public String countGrid() {
		// 声明返回提示
		String returnMsg = null;
		// 声明仿真数据路径
		String path = "D:\\SimulationData\\constant\\polyMesh\\";
		// 声明FileInputStream
		FileInputStream fileInputStream;
		// 声明InputStreamReader
		InputStreamReader inputStreamReader;
		// 声明BufferedReader
		BufferedReader bufferedReader;
		// 声明读取字符串
		String strTmp;
		// 声明读取索引值
		Integer strIndex;
		try {
			// 拼接仿真owner文件路径
			String ownerPath = path + "owner";
			// owner文件：声明FileInputStream
			fileInputStream = new FileInputStream(ownerPath);
			// owner文件：声明InputStreamReader
			inputStreamReader = new InputStreamReader(fileInputStream);
			// owner文件：声明BufferedReader
			bufferedReader = new BufferedReader(inputStreamReader);
			// owner文件：声明读取字符串
			strTmp = "";
			// owner文件：声明读取索引值
			strIndex = 1;
			// owner文件：声明ownerList集合
			List<String> ownerList = new ArrayList<>();
			// 读取owner文件
			while ((strTmp = bufferedReader.readLine()) != null) {
				// 处理owner数据到list集合
				if (strIndex > 21 && strIndex < 697909) {
					ownerList.add(strTmp);
				}
				strIndex++;
			}
			// 打印处理后的ownerList数据长度
			System.out.println("ownerList长度：" + ownerList.size());
			// 获取去重后的ownerList
			List<String> ownerListDis = ownerList.stream().distinct().collect(Collectors.toList());
			// 打印去重后的ownerList数据长度
			System.out.println("去重后ownerList长度：" + ownerListDis.size());

			// 拼接仿真faces文件路径
			String facesPath = path + "faces";
			// faces文件：声明FileInputStream
			fileInputStream = new FileInputStream(facesPath);
			// faces文件：声明InputStreamReader
			inputStreamReader = new InputStreamReader(fileInputStream);
			// faces文件：声明BufferedReader
			bufferedReader = new BufferedReader(inputStreamReader);
			// faces文件：声明读取字符串
			strTmp = "";
			// faces文件：声明读取索引值
			strIndex = 1;
			// faces文件：声明facesList集合
			List<String> facesList = new ArrayList<>();
			// 读取faces文件
			while ((strTmp = bufferedReader.readLine()) != null) {
				// 处理faces数据到list集合
				if (strIndex > 20 && strIndex < 697908) {
					strTmp = strTmp.substring(2, strTmp.length() - 1);
					facesList.add(strTmp);
				}
				strIndex++;
			}
			// 打印处理后的facesList数据长度
			System.out.println("facesList长度：" + facesList.size());

			// 声明CenterGridList集合
			List<CenterGrid> centerGridList = new ArrayList<>();
			// 遍历ownerList集合
			for (int i = 0; i < ownerList.size(); i++) {
				CenterGrid centerGrid = new CenterGrid();
				Integer owner = Integer.valueOf(ownerList.get(i));
				String faces = facesList.get(i);
				centerGrid.setOwner(owner);
				centerGrid.setFaces(faces);
				centerGridList.add(centerGrid);
			}

			// 声明centerGridPointsList集合
			List<CenterGrid> centerGridPointsList = new ArrayList<>();
			// 根据owner分组
			Map<Integer, List<CenterGrid>> ownerFacesMap = centerGridList.stream().collect(Collectors.groupingBy(CenterGrid::getOwner));
			// 遍历ownerFacesMap集合
			for (Integer owner : ownerFacesMap.keySet()) {
				// 获取centerGridValueList
				List<CenterGrid> centerGridValueList = ownerFacesMap.get(owner);
				// 声明pointsSet集合
				Set<String> pointsSet = new HashSet<>();
				// 遍历centerGridValueList集合
				for (CenterGrid centerGrid : centerGridValueList) {
					// 获取centerGrid中faces
					String faces = centerGrid.getFaces();
					// 根据空格分割faces，提取points坐标数组
					String[] pointsArrays = faces.split(" ");
					// 遍历point坐标数组
					for (String points : pointsArrays) {
						pointsSet.add(points);
					}
				}
				//
				CenterGrid centerGrid = new CenterGrid();
				centerGrid.setOwner(owner);
				centerGrid.setPointsSet(pointsSet);
				centerGridPointsList.add(centerGrid);
			}
			// 打印处理后的centerGridPointsList数据长度
			System.out.println("centerGridPointsList长度：" + centerGridPointsList.size());
			// centerGridPointsList根据owner排序
			Collections.sort(centerGridPointsList, Comparator.comparing(CenterGrid::getOwner));

			// 拼接仿真points文件路径
			String pointsPath = path + "points";
			// points文件：声明FileInputStream
			fileInputStream = new FileInputStream(pointsPath);
			// points文件：声明InputStreamReader
			inputStreamReader = new InputStreamReader(fileInputStream);
			// points文件：声明BufferedReader
			bufferedReader = new BufferedReader(inputStreamReader);
			// points文件：声明读取字符串
			strTmp = "";
			// points文件：声明读取索引值
			strIndex = 1;
			// points文件：声明pointsList集合
			List<String> pointsList = new ArrayList<>();
			// 读取points文件
			while ((strTmp = bufferedReader.readLine()) != null) {
				// 处理points数据到list集合
				if (strIndex > 20 && strIndex < 63895) {
					strTmp = strTmp.substring(1, strTmp.length() - 1);
					pointsList.add(strTmp);
				}
				strIndex++;
			}
			// 打印处理后的pointsList数据长度
			System.out.println("pointsList长度：" + pointsList.size());

			// 声明centerGridAvgPointsList集合
			List<CenterGrid> centerGridAvgPointsList = new ArrayList<>();
			// 遍历centerGridPointsList集合，计算中心坐标值
			for (int i = 0; i < centerGridPointsList.size(); i++) {
				// 声明坐标轴求和值
				Double xPointsSum = 0d;
				Double yPointsSum = 0d;
				Double zPointsSum = 0d;
				// 获取centerGridPoints
				CenterGrid centerGridPoints = centerGridPointsList.get(i);
				// 获取pointsSet
				Set<String> pointsSet = centerGridPoints.getPointsSet();
				// 遍历pointsSet
				for (String points : pointsSet) {
					// faces有0坐标，则points第一条数据对应faces中的0坐标 --待验证
					Integer pointsInt = Integer.valueOf(points);
					// 获取坐标
					String pointsValue = pointsList.get(pointsInt);
					// 根据空格分割pointsValue，提取pointsValue中：x，y，z
					String[] pointsValueArrays = pointsValue.split(" ");
					// 获取x轴坐标值
					String xPoints = pointsValueArrays[0];
					// 转换类型（注：e-代表10^,例：1.2e-12 = 1.2*10的-12次方，数值过小忽略不计，赋值0）
					Double D_xPoints = xPoints.contains("e-") ? 0 : Double.valueOf(xPoints);
					// 对x轴坐标求和
					xPointsSum = xPointsSum + D_xPoints;
					// 获取y轴坐标值
					String yPoints = pointsValueArrays[1];
					// 转换类型（注：e-代表10^,例：1.2e-12 = 1.2*10的-12次方，数值过小忽略不计，赋值0）
					Double D_yPoints = yPoints.contains("e-") ? 0 : Double.valueOf(yPoints);
					// 对y轴坐标求和
					yPointsSum = yPointsSum + D_yPoints;
					// 获取z轴坐标值
					String zPoints = pointsValueArrays[2];
					// 转换类型（注：e-代表10^,例：1.2e-12 = 1.2*10的-12次方，数值过小忽略不计，赋值0）
					Double D_zPoints = zPoints.contains("e-") ? 0 : Double.valueOf(zPoints);
					// 对z轴坐标求和
					zPointsSum = zPointsSum + D_zPoints;
				}
				// 求x，y，z坐标的平均值
				Double xPointsAvg = xPointsSum / pointsSet.size();
				Double yPointsAvg = yPointsSum / pointsSet.size();
				Double zPointsAvg = zPointsSum / pointsSet.size();
				// 获取onwer
				Integer owner = centerGridPoints.getOwner();
				// 声明centerGridAvgPoints
				CenterGrid centerGridAvgPoints = new CenterGrid();
				// 赋值centerGridAvgPoints
				centerGridAvgPoints.setOwner(owner);
				centerGridAvgPoints.setxPointsAvg(xPointsAvg);
				centerGridAvgPoints.setyPointsAvg(yPointsAvg);
				centerGridAvgPoints.setzPointsAvg(zPointsAvg);
				//
				centerGridAvgPointsList.add(centerGridAvgPoints);
			}
			// 打印中心坐标集合
			System.out.println("centerGridAvgPointsList长度：" + centerGridAvgPointsList.size());
			// 打印值：验证计算程序是否正确
			System.out.println(JSON.toJSONString(centerGridAvgPointsList.get(0)));
			System.out.println(JSON.toJSONString(centerGridAvgPointsList.get(centerGridAvgPointsList.size() - 1)));

			// 关闭全部文件流
			fileInputStream.close();
			inputStreamReader.close();
			bufferedReader.close();
			// 赋值返回值
			returnMsg = "计算中心点坐标成功！";
		} catch (Exception e) {
			// 赋值返回值
			returnMsg = "计算中心点坐标失败：" + e.toString();
			// 打印错误日志
			log.error(returnMsg);
		}
		return returnMsg;
	}

}