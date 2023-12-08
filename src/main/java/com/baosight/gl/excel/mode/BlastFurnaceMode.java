package com.baosight.gl.excel.mode;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

/**
 * 热电偶数据项
 * 
 * @author SY
 *
 */
@SuppressWarnings("all")
public class BlastFurnaceMode extends BaseRowModel {

	/**
	 * 描述
	 */
	@ExcelProperty(index = 0)
	private String desc;

	/**
	 * 字段名
	 */
	@ExcelProperty(index = 1)
	private String field;

	/**
	 * 标高
	 */
	@ExcelProperty(index = 2)
	private Double height;

	/**
	 * 角度
	 */
	@ExcelProperty(index = 3)
	private Double angle;

	/**
	 * 插入深度
	 */
	@ExcelProperty(index = 4)
	private Double depth;

	/**
	 * 表名
	 */
	@ExcelProperty(index = 5)
	private String table;

	/**
	 * 平均值2
	 */
	@ExcelProperty(index = 6)
	private String average2;
	/**
	 * 每块大小
	 */
	@ExcelProperty(index = 7)
	private Double size;
	/**
	 * 平均值
	 */
	@ExcelProperty(index = 8)
	private String average;
	/**
	 * 角度范围
	 */
	@ExcelProperty(index = 9)
	private Double angle2;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getAngle() {
		return angle;
	}

	public void setAngle(Double angle) {
		this.angle = angle;
	}

	public Double getDepth() {
		return depth;
	}

	public void setDepth(Double depth) {
		this.depth = depth;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getAverage() {
		return average;
	}

	public void setAverage(String average) {
		this.average = average;
	}
	public String getAverage2() {
		return average2;
	}

	public void setAverage2(String average2) {
		this.average2 = average2;
	}
	public Double getSize() {
		return size;
	}

	public void setSize(Double size) {
		this.size = size;
	}
	public Double getAngle2() {
		return angle2;
	}

	public void setAngle2(Double angle2) {
		this.angle2 = angle2;
	}
}