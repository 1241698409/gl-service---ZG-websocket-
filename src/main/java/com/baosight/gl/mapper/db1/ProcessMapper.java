package com.baosight.gl.mapper.db1;

import java.util.HashMap;
import java.util.List;

import com.baosight.gl.domain.CastIronItemValue;

@SuppressWarnings("all")
public interface ProcessMapper {

	List<HashMap> queryCutoffResult(HashMap paramsMap);

	List<HashMap> queryTapNoByTimes(HashMap paramsMap);

	List<HashMap> queryCutOff3ResultIdByTimes(HashMap paramsMap);

	List<HashMap> queryLayerClockByTimes(HashMap paramsMap);

	List<HashMap> queryErodeSolidByTimes(HashMap paramsMap);

	List<Integer> queryHeatMapResultId(HashMap paramsMap);

}