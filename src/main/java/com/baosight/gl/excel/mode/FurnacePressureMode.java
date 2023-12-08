package com.baosight.gl.excel.mode;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

/**
 * 炉身静压数据项
 * 
 * @author SY
 *
 */
@SuppressWarnings("all")
public class FurnacePressureMode extends BaseRowModel {

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
}