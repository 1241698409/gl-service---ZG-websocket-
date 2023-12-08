package com.baosight.gl.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baosight.gl.constant.FormatConstant;

public class TimeUtils {

	/**
	 * @Description:获取当前时间一周前、一周后时间
	 * 
	 * @Return 
	 * 1.weekBefore:一周前时间
	 * 2.weekAfter:一周后时间
	 * 
	 */
	public static Map<String, String> weekRangeTime() {
		// 声明返回值
		Map<String, String> weekRangeTimeMap = new HashMap<>();
		// 声明format格式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 获取一周前时间
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(new Date());
		calendar1.add(Calendar.DATE, -7);
		Date weekBefore = calendar1.getTime();
		weekRangeTimeMap.put("weekBefore", sdf.format(weekBefore));
		// 获取一周后时间
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(new Date());
		calendar2.add(Calendar.DATE, +7);
		Date weekAfter = calendar2.getTime();
		weekRangeTimeMap.put("weekAfter", sdf.format(weekAfter));
		return weekRangeTimeMap;
	}

	/**
	 * @Description:获取当前时间、昨日时间
	 * 
	 * @Return 
	 * 1.currentDate:当前时间
	 * 2.yesterdayDate:昨日时间
	 * 
	 */
	public static Map<String, String> currentYesterDate() {
		// 声明返回值
		Map<String, String> currentYesterDateMap = new HashMap<>();
		// 声明format格式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 获取当前时间
		currentYesterDateMap.put("currentDate", sdf.format(new Date()));
		// 获取昨日时间
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(new Date());
		calendar1.add(Calendar.DATE, -1);
		Date yesterdayDate = calendar1.getTime();
		currentYesterDateMap.put("yesterdayDate", sdf.format(yesterdayDate));
		return currentYesterDateMap;
	}

	/**
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 * @throws Exception
	 */
	public static int compareTimes(String time1, String time2) throws Exception {
		// 将两个时间转换为Date用作比较
		Date startDate = FormatConstant.FORMAT1.parse(time1);
		Date endDate = FormatConstant.FORMAT1.parse(time2);
		// 比较两个时间
		int result = startDate.compareTo(endDate);
		// 返回
		return result;
	}

	public static String beforeHour(String time) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(FormatConstant.FORMAT1.parse(time));
		calendar.add(Calendar.HOUR_OF_DAY, -24);
		return FormatConstant.FORMAT1.format(calendar.getTime());
	}
}