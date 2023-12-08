package com.baosight.gl.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

@SuppressWarnings("all")
public class CollectionUtils {

	public static List getListValue(List value) {
		return value == null ? new ArrayList<>() : value;
	}

	public static <T> List<T> toList(List<Object> object, Class<T> desiredClass) {
		List<T> transformedList = new ArrayList<>();
		if (object != null) {
			for (Object result : object) {
				String json = new Gson().toJson(result);
				T model = new Gson().fromJson(json, desiredClass);
				transformedList.add(model);
			}
		}
		return transformedList;
	}

	/**
	 * 转换数组字符串为数组
	 */
	public static double[] toDoubleArrays(String source, String separator) {
		String[] sourceArr = source.substring(1, source.length() - 1).split(separator);
		/*Double[] retArrays = (Double[]) ConvertUtils.convert(sourceArr, Double.class);
		return retArrays;*/
		return Arrays.stream(sourceArr).mapToDouble(Double::parseDouble).toArray();
	}

	/**
	 * 获取map中第一个Key
	 *
	 * @param map 数据源
	 * @return 
	 */
	public static String getFirstKeyOrNull(Map<String, Long> map) {
		String obj = null;
		for (Entry<String, Long> entry : map.entrySet()) {
			obj = entry.getKey();
			if (obj != null) {
				break;
			}
		}
		return obj;
	}
	public static <T> T toBean(Map<String, Object> data, Class<T> desiredClass) {
		try {
			T obj = desiredClass.getDeclaredConstructor().newInstance(); // 实例化 desiredClass 对象
			for (Field field : desiredClass.getDeclaredFields()) {
				String fieldName = field.getName();
				if (data.containsKey(fieldName)) {
					Object value = data.get(fieldName);
					field.setAccessible(true);
					field.set(obj, value); // 将 value 赋值给 obj 的字段
				}
			}
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
