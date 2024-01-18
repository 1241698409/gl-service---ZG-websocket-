package com.baosight.gl.excel.utils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.baosight.gl.utils.CollectionUtils;

@SuppressWarnings("all")
public class ReadExcelUtils {

	public static <T> List<T> readExcel(InputStream is, Class<? extends BaseRowModel> excelMode) {
		// 声明modeList
		List<T> modeList = new ArrayList<>();
		try {
			// 获取第一个sheet
			Sheet sheet = new Sheet(1, 2, excelMode);
			// 读取sheet的第三行开始数据
			List<Object> sheetList = EasyExcelFactory.read(is, sheet);
			List<Object> list=new ArrayList<>();
			// 转换excelMode类型
			Class<T> desiredClass = (Class<T>) excelMode;
			// 转换List<Object>为List<T>
			for (Object rowData : sheetList) {
				if (rowData instanceof Object) {
					// 判断是否为空行
					if (!isAllFieldsNull(rowData)) {
						// 将非空行加入到 list 中
					list.add(rowData);
					}
				}
			}
//			将集合（Collection）转换为指定类型的列表（List）
			modeList = CollectionUtils.toList(list, desiredClass);
//			modeList = CollectionUtils.toList(sheetList, desiredClass);
		} catch (Exception e) {
			
		}
		// 返回
		return modeList;
	}
	// 判断当前行是否为空行
	private static boolean isEmptyRow(List<Object> rowData) {
		for (Object cellData : rowData) {
			if (cellData != null && cellData.toString().trim().length() > 0) {
				return false;
			}
		}
		return true;
	}
	public static Map<String, Object> listToMap(List<Object> list) {
		Map<String, Object> map = new HashMap<>();
		for (int i = 0; i < list.size(); i++) {
			map.put(String.valueOf(i), list.get(i));
		}
		return map;
	}
	public static boolean isAllFieldsNull(Object object) {
		// 获取对象的 Class 对象
		Class<?> clazz = object.getClass();

		// 获取对象的所有属性用于获取类或接口的所有属性（包括私有属性），但不包括其父类继承来的属性
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			// 设置访问权限，使得private属性可访问
			field.setAccessible(true);
			try {
				// 获取属性的值
				Object value = field.get(object);

				// 如果属性值不为空，则直接返回false
				if (value != null) {
					return false;
				}
			} catch (IllegalAccessException e) {
				// 异常处理
			}
		}
		// 所有属性值都为空，返回true
		return true;
	}
}
