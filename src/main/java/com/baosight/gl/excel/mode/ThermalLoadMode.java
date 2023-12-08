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
public class ThermalLoadMode extends BaseRowModel {

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
}