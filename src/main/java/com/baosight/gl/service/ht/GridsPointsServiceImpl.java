package com.baosight.gl.service.ht;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baosight.gl.service.ht.constant.GridsPointsConstant;
import com.baosight.gl.utils.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("all")
public class GridsPointsServiceImpl implements GridsPointsService {

	/**
	 * 处理GridsPoints文件数据项
	 */
	@Override
	public void dealGridsPoints() throws Exception {
		// 获取gridsHashMap集合
		HashMap<String, List<Double>> gridsHashMap = getGrids();
		// 获取gridsList_xyz集合
		List<Double> gridsList_xyz = gridsHashMap.get("gridsList_xyz");
		// 获取gridsList_xzy集合
		List<Double> gridsList_xzy = gridsHashMap.get("gridsList_xzy");
		// 获取pointsHashMap集合
		HashMap<String, List<Double>> pointsHashMap = getPoints();
		// 获取pointsList_xyz集合
		List<Double> pointsList_xyz = pointsHashMap.get("pointsList_xyz");
		// 获取pointsList_xzy集合
		List<Double> pointsList_xzy = pointsHashMap.get("pointsList_xzy");
		// 处理gridsList_xyz、、gridsList_xzy、pointsList集合到文件中
		writeFileGridsPoints(gridsList_xyz, GridsPointsConstant.FILE_PATH_GRIDS_XYZ_JSON);
		writeFileGridsPoints(gridsList_xzy, GridsPointsConstant.FILE_PATH_GRIDS_XZY_JSON);
		writeFileGridsPoints(pointsList_xyz, GridsPointsConstant.FILE_PATH_POINTS_XYZ_JSON);
		writeFileGridsPoints(pointsList_xzy, GridsPointsConstant.FILE_PATH_POINTS_XZY_JSON);
		// 验证grids、points集合正确性：grids：988893,points：61591*3=184773
		/*System.out.println("pointsList_xyz.size()：" + pointsList_xyz.size());
		System.out.println("pointsList_xyz1：" + pointsList_xyz.get(0));
		System.out.println("pointsList_xyz2：" + pointsList_xyz.get(1));
		System.out.println("pointsList_xyz3：" + pointsList_xyz.get(2));*/
	}

	/**
	 * 读取Cx、Cy、Cz文件，组织成list集合
	 */
	@Override
	public HashMap<String, List<Double>> getGrids() throws Exception {
		// 声明gridsHashMap集合
		HashMap<String, List<Double>> gridsHashMap = new HashMap<>();
		// 声明grids坐标：Cx、Cy、Cz文件路径
		String filePathX = GridsPointsConstant.FILE_PATH_X;
		String filePathY = GridsPointsConstant.FILE_PATH_Y;
		String filePathZ = GridsPointsConstant.FILE_PATH_Z;
		// 获取grids坐标：x坐标
		List<Double> listX = readFileGridsPoints(filePathX, "grids");
		// 获取grids坐标：y坐标
		List<Double> listY = readFileGridsPoints(filePathY, "grids");
		// 获取grids坐标：z坐标
		List<Double> listZ = readFileGridsPoints(filePathZ, "grids");
		// 获取gridsList集合长度
		Integer gridsArraysLength = listX.size() + listY.size() + listZ.size();
		// 声明gridsList_xyz集合
		List<Double> gridsList_xyz = new ArrayList<>();
		// 声明gridsList_xzy集合
		List<Double> gridsList_xzy = new ArrayList<>();
		// 遍历listX、listY、listZ
		for (int k = 0, i = 0; i < gridsArraysLength; k++, i += 3) {
			// 处理listX、listY、listZ：[x1,y1,z1,x2,y2,z2,...]
			gridsList_xyz.add(i, listX.get(k));
			gridsList_xyz.add(i + 1, listY.get(k));
			gridsList_xyz.add(i + 2, listZ.get(k));
			// 处理listX、listY、listZ：[x1,z1,y1,x2,z2,y2,...]
			gridsList_xzy.add(i, listX.get(k));
			gridsList_xzy.add(i + 1, listZ.get(k));
			gridsList_xzy.add(i + 2, listY.get(k));
		}
		// 处理gridsList_xyz、gridsList_xzy到gridsHashMap集合
		gridsHashMap.put("gridsList_xyz", gridsList_xyz);
		gridsHashMap.put("gridsList_xzy", gridsList_xzy);
		// 返回
		return gridsHashMap;
	}

	/**
	 * 读取points文件，组织成list集合
	 */
	@Override
	public HashMap<String, List<Double>> getPoints() throws Exception {
		// 声明pointsHashMap集合
		HashMap<String, List<Double>> pointsHashMap = new HashMap<>();
		// 声明points坐标：文件坐标
		String filePathPoints = GridsPointsConstant.FILE_PATH_POINTS;
		// 获取pointsList_xyz集合
		List<Double> pointsList_xyz = readFileGridsPoints(filePathPoints, "points");
		// 声明pointsList_xzy集合
		List<Double> pointsList_xzy = new ArrayList<>();
		// 遍历pointsList_xyz集合：获取pointsList_xzy集合
		for (int i = 0; i < pointsList_xyz.size(); i += 3) {
			pointsList_xzy.add(pointsList_xyz.get(i));
			pointsList_xzy.add(pointsList_xyz.get(i + 2));
			pointsList_xzy.add(pointsList_xyz.get(i + 1));
		}
		// 处理pointsList_xyz、pointsList_xzy到pointsHashMap集合
		pointsHashMap.put("pointsList_xyz", pointsList_xyz);
		pointsHashMap.put("pointsList_xzy", pointsList_xzy);
		// 返回
		return pointsHashMap;
	}

	/**
	 * 根据文件路径，读取文件内容
	 */
	@Override
	public List<Double> readFileGridsPoints(String filePath, String type) throws Exception {
		// 声明FileInputStream
		FileInputStream fileInputStream = new FileInputStream(filePath);
		// 声明InputStreamReader
		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
		// 声明BufferedReader
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		// 声明listJson
		String listJson = null;
		// 声明lineTxt
		String strTmp = null;
		// 声明行数索引值
		Integer lineIndex = 1;
		// 声明数据索引值
		Integer strIndex = 1;
		// 声明fList
		List<Double> fList = new ArrayList<>();
		// switch判断：grids、points
		switch (type) {
		case "grids":
			// 声明grids文件：默认结束行数:0
			Integer gridsEndLine = 0;
			// 遍历读取grids文件
			while ((strTmp = bufferedReader.readLine()) != null) {
				// 判断grids文件结束行数
				if (strIndex == 21) {
					gridsEndLine = Integer.valueOf(strTmp) + 23;
				}
				// 根据结束行数，读取grids文件
				if (strIndex > 22 && strIndex < gridsEndLine) {
					fList.add(Double.parseDouble(strTmp));
				}
				strIndex++;
			}
			break;
		case "points":
			// 声明points文件：默认结束行数:0
			Integer pointsEndLine = 0;
			// 遍历读取points文件
			while ((strTmp = bufferedReader.readLine()) != null) {
				// 判断points文件结束行数
				if (strIndex == 19) {
					pointsEndLine = Integer.valueOf(strTmp) + 21;
				}
				// 根据结束行数，读取points文件
				if (strIndex > 20 && strIndex < pointsEndLine) {
					// 转points字符坐标为数组
					double[] pointsArrays = CollectionUtils.toDoubleArrays(strTmp, " ");
					// 处理points的x坐标
					fList.add(pointsArrays[0]);
					// 处理points的y坐标
					fList.add(pointsArrays[1]);
					// 处理points的z坐标
					fList.add(pointsArrays[2]);
				}
				strIndex++;
			}
			break;
		}
		// 关闭全部文件流
		fileInputStream.close();
		inputStreamReader.close();
		bufferedReader.close();
		return fList;
	}

	/**
	 * 根据文件路径，写入文件内容
	 */
	@Override
	public void writeFileGridsPoints(List<Double> lineRow, String filePath) throws Exception {
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
}