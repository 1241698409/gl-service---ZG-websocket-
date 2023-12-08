package com.baosight.gl.service.ht;

import java.io.File;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public interface HtService {

	List<HashMap> queryOreCoke(HashMap paramsMap);
	
	HashMap queryCurrentAirFlow(HashMap paramsMap);

	void insertAirFlowIndex(HashMap paramsMap);

	List<HashMap> queryAirFlowByTimes(HashMap paramsMap);

	void dealAirFlow(int typeKey, String airFlowName) throws Exception;

	String getCurrentHfFileName() throws Exception;

	String readAirFlowFile(String airFlowPath) throws Exception;

	void copyFileUsingStream(String source, String dest) throws Exception;

	String convertTimeFormat(String formatTime) throws Exception;
	
}