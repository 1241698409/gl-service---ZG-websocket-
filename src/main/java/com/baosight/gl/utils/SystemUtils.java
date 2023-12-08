package com.baosight.gl.utils;

/**
 * 
 * @author SY
 * @description 判断系统为：linux、window
 *
 */
public class SystemUtils {

	static String system = System.getProperty("os.name");

	public static Boolean isLinux() {
		return system.toLowerCase().startsWith("win") ? false : true;
	}

	public static boolean isWindow() {
		return system.toLowerCase().startsWith("win") ? true : false;
	}
}
