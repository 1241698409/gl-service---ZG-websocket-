package com.baosight.gl.service.ht.domain;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class Point {

	public Integer[] stands = { 1, 50, 10, 30, 30, 40, 25, 25, 50, 50, 50, 10, 1, 1, 10 };

	public double[] standPoints;

	public double[] valuePoints;

	public double[] valueTempate;

	public Integer beta;

	public double distance;

	public List<Integer> inCirclePointsIndex = new ArrayList<>();

	public double x;

	public double y;

	public double z;

	/**
	 * @description 初始化、赋值数据
	 * 
	 * @param t
	 * @param n
	 * @param z
	 * @param i
	 * @param s
	 */
	public double[] getZValue(double t, double n, double z, double i, int s, String type) {
		// 初始化inCirclePointsIndex
		this.inCirclePointsIndex = new ArrayList<>();
		// 初始化valueTempate
		if (type.equals("u")) {
			this.valueTempate = new double[3];
			this.valueTempate[0] = 0d;
			this.valueTempate[1] = 0d;
			this.valueTempate[2] = 0d;
		} else {
			this.valueTempate = new double[1];
			this.valueTempate[0] = 0d;
		}
		// 赋值x、y、z、distance、beta
		this.x = t;
		this.y = n;
		this.z = z;
		this.distance = i;
		this.beta = s;
		// 调用_getZValue()方法
		return this._getZValue();
	}

	/**
	 * @description 初始化、赋值数据
	 */
	public double[] _getZValue() {
		// 计算坐标差的平方根
		List<Double> disArr = this.getdisArr();
		// 累加disArr集合
		Double deno = this.getDenominator(disArr);
		// 根据disArr和Denominator处理数据
		List<Double> weigArr = this.getWeigArr(disArr, deno);
		// 赋值s
		double[] s = this.valueTempate;
		// 获取weigArr长度
		int length = weigArr.size();
		// 获取valueTempate长度
		int valueLength = this.valueTempate.length;
		// 循环遍历weigArr集合
		for (int j = 0; j < length; j++) {
			// 获取index
			int index = this.inCirclePointsIndex.get(j) / 3;
			// 循环遍历累加s[k]
			for (int k = 0; k < valueLength; k++) {
				s[k] += this.valuePoints[index * valueLength + k] * weigArr.get(j);
			}
		}
		// 返回
		return s;
	}

	/**
	 * @description 计算坐标差的平方根
	 */
	public List<Double> getdisArr() {
		// 声明返回值：tRet集合
		List<Double> tRet = new ArrayList<>();
		// 获取standPoints数组长度
		int index = this.standPoints.length;
		// 遍历standPoints数组
		for (int i = 0; i < index; i += 3) {
			// 获取x、y、z
			double s = this.standPoints[i];
			double r = this.standPoints[i + 1];
			double o = this.standPoints[i + 2];
			// 求x、y、z差值
			double e = s - this.x;
			double a = r - this.y;
			double a1 = o - this.z;
			// 相加e、a、a1平方，在平方根
			double h = Math.sqrt(Math.pow(e, 2) + Math.pow(a, 2) + Math.pow(a1, 2));
			// 判断计算值和系数
			if (h < this.distance) {
				tRet.add(h);
				this.inCirclePointsIndex.add(i);
			}
		}
		// 返回
		return tRet;
	}

	/**
	 * @description 累加disArr集合
	 * 
	 * @param disArr
	 */
	public Double getDenominator(List<Double> disArr) {
		// 声明返回值：nRet
		Double nRet = 0d;
		// 遍历disArr集合
		for (int i = 0; i < disArr.size(); i++) {
			// 二元表达式，判断disArr
			Double r = 0 != disArr.get(i) ? disArr.get(i) : 1d;
			// 累加1 / r的beta次方
			nRet += Math.pow(1 / r, this.beta);
		}
		// 返回
		return nRet;
	}

	/**
	 * @description 根据disArr和Denominator处理数据
	 * 
	 * @param disArr
	 * @param Denominator
	 */
	public List<Double> getWeigArr(List<Double> disArr, Double Denominator) {
		// 声明返回值：weigArr集合
		List<Double> weigArr = new ArrayList<>();
		// 遍历disArr集合
		for (int i = 0; i < this.inCirclePointsIndex.size(); i++) {
			// 二元表达式，判断disArr
			Double o = 0 != disArr.get(i) ? disArr.get(i) : 1d;
			// 求1 / o的beta次方 / Denominator
			Double e = Math.pow(1 / o, this.beta) / Denominator;
			// 处理e到weigArr集合
			weigArr.add(e);
		}
		// 返回
		return weigArr;
	}
}