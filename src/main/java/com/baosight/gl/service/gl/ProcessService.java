package com.baosight.gl.service.gl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.baosight.gl.excel.mode.BlastFurnaceMode;

@SuppressWarnings("all")
public interface ProcessService {

	/**
	 * 处理集合数据项
	 * 
	 * @param getKey
	 * @param putKey
	 * @param getMap
	 * @param putMap
	 */
	void handleData(String getKey, String putKey, Map getMap, Map putMap);

	/**
	 * 根据文件名称读取excel文件
	 * 
	 * @param fileName 文件名称
	 */
	List<BlastFurnaceMode> readBlastFurnaceExcel(String fileName);

	/**
	 * 
	 * @param BlastFurnaceModeList
	 * @param flag
	 */
	void getFieldStr(List<BlastFurnaceMode> BlastFurnaceModeList, HashMap paramsMap, String flag);

	/**
	 * 根据：数据库数据项、excel数据项，处理数据格式
	 * 
	 * @param BlastFurnaceMap  高炉数据库模型数据项
	 * @param BlastFurnaceList 高炉excel模型数据项
	 */
	List<HashMap> formatBlastFurnaceData(HashMap BlastFurnaceMap, List<BlastFurnaceMode> BlastFurnaceList,String resultId);
	List<HashMap> formatBlastFurnaceData(HashMap BlastFurnaceMap, List<BlastFurnaceMode> BlastFurnaceList,String resultId,String slag);



    /**
	 * 
	 * @param FormatList
	 * @param ValueHashMap
	 */
	void dealFormatData(List<HashMap> FormatList, LinkedHashMap ValueHashMap, String flag);

	/**
	 * 求传入的list集合的差值绝对值
	 * 
	 * @param valueList
	 */
	List<HashMap> getHeatMapValueDiffByList(List<HashMap> valueList);

	/**
	 * 求传入两个map集合的差值绝对值，并保留两位小数
	 * 
	 * @param valueMap1
	 * @param valueMap2
	 */
	HashMap getHeatMapValueDiffByMap(HashMap valueMap1, HashMap valueMap2);

	/**
	 * 
	 * 
	 * @remark1：根据resultId查询CUTOFFID=1的T_CUTOFF_RESULT表CLOCK时间
	 * @remark2：处理查询时间为：(查询时间-$(number)h 至 查询时间+$(number)h)时间区间
	 * @remark3：出铁信号表T_TS_TAP：每隔两个小时(特殊情况为四个小时)会新增一条数据，则number > 4即可
	 * @remark4：设置number默认值为：6h,后续可根据T_TS_TAP表间隔时间，进行修改
	 * @remark5：根据处理的查询时间区间，和T_TS_TAP表ENDTIME时间字段，查询ENDTIME时间集合
	 * @remark6：遍历ENDTIME时间集合：和CUTOFFID=1的T_CUTOFF_RESULT表CLOCK时间求差值绝对值
	 * @remark7：获取差值绝对值最小对应的TapNo
	 * 
	 * @param resultId
	 * @param number
	 */
	public HashMap getTapNoByResultId(int resultId, int number) throws Exception;

	/**
	 * 
	 * 
	 * @remark1：根据resultId查询CUTOFFID=1的T_CUTOFF_RESULT表CLOCK时间
	 * @remark2：处理查询时间为：(查询时间-$(number)h 至 查询时间+$(number)h)时间区间
	 * @remark3：2d面板CUTOFFID=3的T_CUTOFF_RESULT表，每隔一个小时会新增一条数据，则number > 1即可
	 * @remark4：设置number默认值为：2h,后续可根据CUTOFFID=3的T_CUTOFF_RESULT表间隔时间，进行修改
	 * @remark5：根据处理的查询时间区间，和T_CUTOFF_RESULT表CLOCK时间字段，查询CLOCK时间集合
	 * @remark6：遍历CLOCK时间集合：和CUTOFFID=1的T_CUTOFF_RESULT表CLOCK时间求差值绝对值
	 * @remark7：获取差值绝对值最小对应的CUTOFFID=3的resultId
	 * 
	 * @param resultId
	 * @param number
	 */
	public HashMap getCutOff3ResultIdByResultId(int resultId, int number) throws Exception;

	public HashMap getLayerClockByResultId(int resultId, int number) throws Exception;

	public HashMap getErodeSolidByResultId(int resultId, int number) throws Exception;

	public Long queryResultTimeStamp(int cutoffId, String time) throws Exception;

	public Long queryResultTimeStamp(int cutoffId, String time, String flag) throws Exception;

	public LinkedHashMap dealBlastFurnaceHistory(HashMap blastFurnaceParamsMap);

	public HashMap<String, Object> getHeatMapResultId(HashMap heatMapParamsMap) throws Exception;

	public String getHeatMapResultIdHistory(HashMap heatMapParamsMap) throws Exception;
	public String JsonFileService (String stringJson) throws Exception;
	public String JsonFileService (String stringJson,Integer resultid) throws Exception;
	public String JsonFileService (String stringJson,Integer resultid,HashMap hashMap) throws Exception;
	public String JsonReader  (String time) throws Exception;
	public String JsonReader  (String time,Integer resultid) throws Exception;

	void differenceresultid() throws Exception;
}