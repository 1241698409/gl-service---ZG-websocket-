package com.baosight.gl.utils;

public class NumberFormatUtils {

	public static Double formatDouble(Double formatNumber) {
		// 四舍五入后，保留两位小数
		return (double) Math.round(formatNumber * 100) / 100;
	}
}