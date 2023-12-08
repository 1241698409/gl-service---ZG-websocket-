package com.baosight.gl.mapper.db1;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.baosight.gl.domain.CastIronItemValue;

@SuppressWarnings("all")
public interface GlMapper {

	/**
	 * 查询出铁数据项
	 */
	List<CastIronItemValue> queryCastIron(HashMap paramsMap);

	/**
	 * 查询出铁信号点
	 */
	List<HashMap> queryCastIronTag();

	/**
	 * 查询风口信号点
	 */
	List<HashMap> queryTuyereTag();

	/**
	 * 2d面板：产量、焦比、煤比、燃料比、料速
	 */
	HashMap queryFurnaceMaterial1(HashMap paramsMap);

	/**
	 * 2d面板：风量、热风温度、平均料线、偏尺
	 */
	HashMap queryFurnaceMaterial2(HashMap paramsMap);

	/**
	 * 2d面板：富氧量、鼓风湿度
	 */
	HashMap queryFurnaceMaterial3(HashMap paramsMap);

	/**
	 * 2d面板：热风压力
	 */
	HashMap queryFurnaceMaterial4(HashMap paramsMap);

	/**
	 * 2d面板：炉顶压力、ηCO、CO、CO2
	 */
	HashMap queryFurnaceMaterial5(HashMap paramsMap);

	/**
	 * 2d面板：△P
	 */
	HashMap queryFurnaceMaterial6(HashMap paramsMap);

	/**
	 * 2d面板：K、炉腹煤气量、顶温-平均值、边缘四点温度、CCT、CCT2、鼓风动能、理论燃烧温度
	 */
	HashMap queryFurnaceMaterial7(HashMap paramsMap);

	/**
	 * 2d面板：喷煤
	 */
	HashMap queryFurnaceMaterial8(HashMap paramsMap);

	/**
	 * 热负荷数据项
	 */
	List<HashMap> queryThermalLoad(HashMap paramsMap);
	/**
	 * 热负荷历史数据项
	 */
	List<HashMap> queryThermalLoadHis(HashMap paramsMap);


	/**
	 * 热负荷温度数据项
	 */
	List<HashMap> queryThermalLoadTemp(HashMap paramsMap);

	/**
	 * 热电偶数据项
	 */
	List<HashMap> queryThermocouple(HashMap paramsMap);
	//热电偶历史60条
	List<HashMap> queryThermocoupleHis(HashMap paramsMap);

	List<HashMap> queryHeatMapHistory(HashMap paramsMap);

	/**
	 * 炉身静压数据项
	 */
	List<HashMap> queryStaticPressure(HashMap paramsMap);

	/**
	 * 冷却板供水压力数据项1
	 */
	List<HashMap> querySupplyPressure1(HashMap paramsMap);

	/**
	 * 冷却板供水压力数据项2
	 */
	List<HashMap> querySupplyPressure2(HashMap paramsMap);

	/**
	 * 热风压力数据项
	 */
	List<HashMap> queryHotPressure(HashMap paramsMap);

	/**
	 * 炉顶压力数据项
	 */
	List<HashMap> queryTopPressure(HashMap paramsMap);

	/**
	 * 料层数据项
	 */
	List<HashMap> queryMaterialLayer(HashMap paramsMap);
	
	/**
	 * 料层数据项_1
	 */
	List<HashMap> queryMaterialLayerData();

	/**
	 * 查询侵蚀线、凝固线：角度数据项
	 */
	List<HashMap> queryErodeSolidAngle();

	/**
	 * 查询侵蚀线、凝固线：CalcId数据项
	 */
	HashMap queryErodeSolidCalcId(HashMap paramsMap);

	/**
	 * 查询侵蚀线数据项，注：r-半径、z-高度、angle-角度
	 */
	List<HashMap> queryErodeDataItem(HashMap paramsMap);

	/**
	 * 查询凝固线数据项，注：r-半径、z-高度、angle-角度
	 */
	List<HashMap> querySolidDataItem(HashMap paramsMap);

	List<HashMap> queryElevation();

	List<HashMap> queryOtherAnglesLine(HashMap paramsMap);

	List<HashMap> queryResidualThickness(HashMap paramsMap);
	List<HashMap> queryResidualThicknessHis(HashMap paramsMap);
	List<HashMap> queryMdWrPointValue();

	List<HashMap> queryMdErValueGroupTimes(HashMap paramsMap);

	List<HashMap> queryMdErPolygon(HashMap paramsMap);
	
	List<HashMap> queryRefractoryMaterial(HashMap paramsMap);
}