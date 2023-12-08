package com.baosight.gl.service.ht.constant;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.baosight.gl.utils.SystemUtils;

@SuppressWarnings("all")
public class GridsPointsConstant {

	public static String FILE_PATH_UPT = "";
	public static String FILE_PATH_X = "";
	public static String FILE_PATH_Y = "";
	public static String FILE_PATH_Z = "";
	public static String FILE_PATH_POINTS = "";
	public static String FILE_PATH_GRIDS_XYZ_JSON = "";
	public static String FILE_PATH_GRIDS_XZY_JSON = "";
	public static String FILE_PATH_POINTS_XYZ_JSON = "";
	public static String FILE_PATH_POINTS_XZY_JSON = "";
	public static String FILE_PATH_COUNT_U = "";
	public static String FILE_PATH_COUNT_P = "";
	public static String FILE_PATH_COUNT_T = "";
	public static String FILE_PATH_COPY_U = "";
	public static String FILE_PATH_COPY_P = "";
	public static String FILE_PATH_COPY_T = "";

	static {
		if (SystemUtils.isWindow()) {
			// 声明path文件路径
			String path = "D:\\项目文档\\太钢六高炉仿真数据文档\\海仿模拟数据文件\\";
			// 声明海仿文件路径
			FILE_PATH_UPT = path + "hfFile\\";
			// 声明Cx、Cy、Cz、points文件路径
			FILE_PATH_X = path + "hfFile\\polyMesh\\Cx";
			FILE_PATH_Y = path + "hfFile\\polyMesh\\Cy";
			FILE_PATH_Z = path + "hfFile\\polyMesh\\Cz";
			FILE_PATH_POINTS = path + "hfFile\\polyMesh\\points";
			// 处理Cx、Cy、Cz：grids_xyz.json、grids_xzy.json
			FILE_PATH_GRIDS_XYZ_JSON = path + "countResult\\grids_xyz.json";
			FILE_PATH_GRIDS_XZY_JSON = path + "countResult\\grids_xzy.json";
			// 处理points：points_xyz.json、points_xzy.json
			FILE_PATH_POINTS_XYZ_JSON = path + "countResult\\points_xyz.json";
			FILE_PATH_POINTS_XZY_JSON = path + "countResult\\points_xzy.json";
			// 声明计算后的u.json、p.json、t.json文件路径
			FILE_PATH_COUNT_U = path + "countResult\\u.json";
			FILE_PATH_COUNT_P = path + "countResult\\p.json";
			FILE_PATH_COUNT_T = path + "countResult\\t.json";
			// 声明复制后的u.json、p.json、t.json文件路径
			FILE_PATH_COPY_U = path + "u-json-file\\";
			FILE_PATH_COPY_P = path + "p-json-file\\";
			FILE_PATH_COPY_T = path + "t-json-file\\";
		} else {
			// 声明海仿文件路径
			FILE_PATH_UPT = "/gl-service/hfFile/";
			// 声明Cx、Cy、Cz、points文件路径
			FILE_PATH_X = "/gl-service/hfFile/polyMesh/Cx";
			FILE_PATH_Y = "/gl-service/hfFile/polyMesh/Cy";
			FILE_PATH_Z = "/gl-service/hfFile/polyMesh/Cz";
			FILE_PATH_POINTS = "/gl-service/hfFile/polyMesh/points";
			// 处理Cx、Cy、Cz：grids_xyz.json、grids_xzy.json
			FILE_PATH_GRIDS_XYZ_JSON = "/gl-service/countResult/grids_xyz.json";
			FILE_PATH_GRIDS_XZY_JSON = "/gl-service/countResult/grids_xzy.json";
			// 处理points：points_xyz.json、points_xzy.json
			FILE_PATH_POINTS_XYZ_JSON = "/gl-service/countResult/points_xyz.json";
			FILE_PATH_POINTS_XZY_JSON = "/gl-service/countResult/points_xzy.json";
			// 声明计算后的u.json、p.json、t.json文件路径
			FILE_PATH_COUNT_U = "/gl-service/countResult/u.json";
			FILE_PATH_COUNT_P = "/gl-service/countResult/p.json";
			FILE_PATH_COUNT_T = "/gl-service/countResult/t.json";
			// 声明复制后的u.json、p.json、t.json文件路径
			FILE_PATH_COPY_U = "/home/gl-file/u-json-file/";
			FILE_PATH_COPY_P = "/home/gl-file/p-json-file/";
			FILE_PATH_COPY_T = "/home/gl-file/t-json-file/";
		}
	}
}