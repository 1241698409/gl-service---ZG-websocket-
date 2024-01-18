package com.baosight.gl.mapper.db2;

import java.util.HashMap;
import java.util.List;

import com.baosight.gl.domain.CastIronItemValue;

@SuppressWarnings("all")
public interface HtMapper {



	List<HashMap> queryOreCoke(HashMap paramsMap);

	HashMap queryCurrentAirFlow(HashMap paramsMap);

	void insertAirFlowIndex(HashMap paramsMap);

	List<HashMap> queryAirFlowByTimes(HashMap paramsMap);
	void setFilePath(HashMap<String,Object> paramsMap);
	HashMap JsonReader(HashMap paramsMap);
	List<HashMap> queryCutoffResult(HashMap paramsMap);
	void insertID(HashMap paramsMap);
}