package com.baosight.gl.utils;

import java.util.UUID;

public class UuidUtils {

	public static String UUID32() {
		// UUID生成32位数
		String uuid32 = UUID.randomUUID().toString().replace("-", "");
		// 返回
		return uuid32;
	}

	public static String UUID16() {
		// UUID生成32位数
		String uuid32 = UUID.randomUUID().toString().replace("-", "");
		// 然后截取前面或后面16位
		String uuid16 = uuid32.substring(0, 16);
		// 返回
		return uuid16;
	}
}
