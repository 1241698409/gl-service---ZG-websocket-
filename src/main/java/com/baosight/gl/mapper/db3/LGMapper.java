package com.baosight.gl.mapper.db3;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public interface LGMapper {

	List<HashMap> queryCutoffResult(HashMap paramsMap);
	List<HashMap> queryCutoffResultMap(HashMap paramsMap);
	List<HashMap> queryTapNoByTimes(HashMap paramsMap);

	List<HashMap> queryCutOff3ResultIdByTimes(HashMap paramsMap);

	List<HashMap> queryLayerClockByTimes(HashMap paramsMap);

	List<HashMap> queryErodeSolidByTimes(HashMap paramsMap);

	List<Integer> queryHeatMapResultId(HashMap paramsMap);
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