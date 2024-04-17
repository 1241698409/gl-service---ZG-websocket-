package com.baosight.gl.service.gl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.baosight.gl.mapper.db3.LGMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baosight.gl.domain.CastIronItemValue;
import com.baosight.gl.mapper.db1.GlMapper;
import com.baosight.gl.mapper.db1.ProcessMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("all")
public class GlServiceImpl implements GlService {

	@Autowired
	GlMapper glMapper;

	@Autowired
	ProcessMapper processMapper;
	@Autowired
	LGMapper lgMapper;
	@Override
	public List<CastIronItemValue> queryCastIron(HashMap paramsMap) {
		return glMapper.queryCastIron(paramsMap);
	}

	@Override
	public List<HashMap> queryCastIronTag() {
		return glMapper.queryCastIronTag();
	}
	@Override
	public List<HashMap> queryCastIronTag2() {
		return glMapper.queryCastIronTag2();
	}
	@Override
	public List<HashMap> queryTuyereTag() {
		return glMapper.queryTuyereTag();
	}

	@Override
	public HashMap queryFurnaceMaterial1(HashMap paramsMap) {
		return glMapper.queryFurnaceMaterial1(paramsMap);
	}

	@Override
	public HashMap queryFurnaceMaterial2(HashMap paramsMap) {
		return glMapper.queryFurnaceMaterial2(paramsMap);
	}

	@Override
	public HashMap queryFurnaceMaterial3(HashMap paramsMap) {
		return glMapper.queryFurnaceMaterial3(paramsMap);
	}

	@Override
	public HashMap queryFurnaceMaterial4(HashMap paramsMap) {
		return glMapper.queryFurnaceMaterial4(paramsMap);
	}

	@Override
	public HashMap queryFurnaceMaterial5(HashMap paramsMap) {
		return glMapper.queryFurnaceMaterial5(paramsMap);
	}

	@Override
	public HashMap queryFurnaceMaterial6(HashMap paramsMap) {
		return glMapper.queryFurnaceMaterial6(paramsMap);
	}

	@Override
	public HashMap queryFurnaceMaterial7(HashMap paramsMap) {
		return glMapper.queryFurnaceMaterial7(paramsMap);
	}

	@Override
	public HashMap queryFurnaceMaterial8(HashMap paramsMap) {
		return glMapper.queryFurnaceMaterial8(paramsMap);
	}
	@Override
	public HashMap queryFurnaceMaterial9(HashMap paramsMap) {
		return glMapper.queryFurnaceMaterial9(paramsMap);
	}
	@Override
	public HashMap queryFurnaceMaterial10(HashMap paramsMap) {
		return glMapper.queryFurnaceMaterial10(paramsMap);
	}
	@Override
	public List<HashMap> queryThermalLoad(HashMap paramsMap) {
		return glMapper.queryThermalLoad(paramsMap);
	}
	@Override
	public List<HashMap> queryThermalLoadHis(HashMap paramsMap) {
		return glMapper.queryThermalLoadHis(paramsMap);
	}


	@Override
	public List<HashMap> queryThermalLoadTemp(HashMap paramsMap) {
		return glMapper.queryThermalLoadTemp(paramsMap);
	}


	@Override
	public List<HashMap> queryThermocouple(HashMap paramsMap) {
		return glMapper.queryThermocouple(paramsMap);
	}
	public List<HashMap> queryThermocoupleHis(HashMap paramsMap) {
		return glMapper.queryThermocoupleHis(paramsMap);
	}

	@Override
	public List<HashMap> queryHeatMapHistory(HashMap paramsMap) {
		return glMapper.queryHeatMapHistory(paramsMap);
	}

	@Override
	public List<HashMap> queryStaticPressure(HashMap paramsMap) {
		return glMapper.queryStaticPressure(paramsMap);
	}

	@Override
	public List<HashMap> querySupplyPressure1(HashMap paramsMap) {
		return glMapper.querySupplyPressure1(paramsMap);
	}

	@Override
	public List<HashMap> querySupplyPressure2(HashMap paramsMap) {
		return glMapper.querySupplyPressure2(paramsMap);
	}

	@Override
	public List<HashMap> queryHotPressure(HashMap paramsMap) {
		return glMapper.queryHotPressure(paramsMap);
	}

	@Override
	public List<HashMap> queryTopPressure(HashMap paramsMap) {
		return glMapper.queryTopPressure(paramsMap);
	}

	@Override
	public List<HashMap> queryMaterialLayer(HashMap paramsMap) {
		return glMapper.queryMaterialLayer(paramsMap);
	}
	
	@Override
	public List<HashMap> queryMaterialLayerData() {
		return lgMapper.queryMaterialLayerData();
	}

	@Override
	public List<HashMap> queryErodeSolidAngle() {
		return lgMapper.queryErodeSolidAngle();
	}

	@Override
	public HashMap queryErodeSolidCalcId(HashMap paramsMap) {
		return lgMapper.queryErodeSolidCalcId(paramsMap);
	}

	@Override
	public List<HashMap> queryErodeDataItem(HashMap paramsMap) {
		return lgMapper.queryErodeDataItem(paramsMap);
	}

	@Override
	public List<HashMap> querySolidDataItem(HashMap paramsMap) {
		return lgMapper.querySolidDataItem(paramsMap);
	}

	@Override
	public List<HashMap> queryElevation() {
		return lgMapper.queryElevation();
	}

	@Override
	public List<HashMap> queryOtherAnglesLine(HashMap paramsMap) {
		return glMapper.queryOtherAnglesLine(paramsMap);
	}

	@Override
	public List<HashMap> queryResidualThickness(HashMap paramsMap) {
		return lgMapper.queryResidualThickness(paramsMap);
	}
	@Override
	public List<HashMap> queryResidualThicknessHis(HashMap paramsMap) {
		return glMapper.queryResidualThicknessHis(paramsMap);
	}
	@Override
	public List<HashMap> queryMdWrPointValue() {
		return glMapper.queryMdWrPointValue();
	}

	@Override
	public List<HashMap> queryMdErValueGroupTimes(HashMap paramsMap) {
		return glMapper.queryMdErValueGroupTimes(paramsMap);
	}

	@Override
	public List<HashMap> queryMdErPolygon(HashMap paramsMap) {
		return glMapper.queryMdErPolygon(paramsMap);
	}

	@Override
	public List<HashMap> queryRefractoryMaterial(HashMap paramsMap) {
		return glMapper.queryRefractoryMaterial(paramsMap);
	}

	/**
	 * @Description 查询模型：其余角度侵蚀线/凝固线数据项
	 * 
	 * @param itemId
	 * @version 2.0
	 */
	@Override
	public HashMap<Double, List<HashMap>> dealOtherAnglesLine(HashMap paramsMap) {
		// 获取emptyAngleList集合
		List<Double> emptyAngleList = (List<Double>) paramsMap.get("emptyAngleList");
		// 声明otherAnglesLineRetMap集合
		HashMap<Double, List<HashMap>> otherAnglesLineRetMap = new HashMap<>();
		// 获取侵蚀线/凝固线
		List<HashMap> OtherAnglesLineList = queryOtherAnglesLine(paramsMap);
		// 根据角度分组
		for (int i = 0; i < OtherAnglesLineList.size(); i++) {
			HashMap OtherAnglesLineHashMap = OtherAnglesLineList.get(i);
			// 获取角度
			Double angle = Double.valueOf(OtherAnglesLineHashMap.get("angle").toString());
			// 获取半径
			Object radius = OtherAnglesLineHashMap.get("radius");
			// 获取标高
			Object height = OtherAnglesLineHashMap.get("height");
			//
			List<HashMap> valueList = otherAnglesLineRetMap.get(angle);
			//
			valueList = valueList == null ? new ArrayList<>() : valueList;
			HashMap valueHashMap = new HashMap<>();
			valueHashMap.put("R", radius);
			valueHashMap.put("Z", height);
			valueList.add(valueHashMap);
			// 处理valueList到otherAnglesLineRetMap集合
			otherAnglesLineRetMap.put(angle, valueList);
		}
		// 根据emptyAngleList处理otherAnglesLineRetMap集合  排除原来的角度数据
		for (int i = 0; i < emptyAngleList.size(); i++) {
			otherAnglesLineRetMap.remove(emptyAngleList.get(i));
		}
		// 返回
		return otherAnglesLineRetMap;
	}

	/**
	 * @Description 查询模型：其余角度侵蚀线/凝固线数据项
	 * 
	 * @param itemId
	 * @version 1.0
	 */
	/*@Override
	public HashMap<Double, List<HashMap>> dealOtherAnglesLine(HashMap paramsMap) {
		// 声明otherAnglesLineRetMap集合
		HashMap<Double, List<HashMap>> otherAnglesLineRetMap = new HashMap<>();
		// 查询标高数据项
		List<HashMap> elevationList = queryElevation();
		// 遍历elevationList集合
		for (HashMap elevationMap : elevationList) {
			// 获取标高
			Object high = elevationMap.get("HIGH");
			// 设置标高
			paramsMap.put("high", high);
			// 根据标高获取侵蚀线/凝固线
			List<HashMap> OtherAnglesLineList = queryOtherAnglesLine(paramsMap);
			// 遍历OtherAnglesLineList集合
			for (HashMap OtherAnglesLineMap : OtherAnglesLineList) {
				// 获取角度
				Double angle = Double.valueOf(OtherAnglesLineMap.get("angle").toString());
				// 获取半径
				Object radius = OtherAnglesLineMap.get("radius");
				// 获取标高
				Object height = OtherAnglesLineMap.get("height");
				// 根据角度获取valueList集合
				List valueList = otherAnglesLineRetMap.get(angle);
				valueList = valueList == null ? new ArrayList<>() : valueList;
				// 声明valueMap集合
				HashMap valueMap = new HashMap<>();
				valueMap.put("R", radius);
				valueMap.put("Z", height);
				// 处理valueMap到valueList集合
				valueList.add(valueMap);
				// 处理valueList到otherAnglesLineRetMap集合
				otherAnglesLineRetMap.put(angle, valueList);
			}
		}
		System.out.println(JSON.toJSONString(otherAnglesLineRetMap));
		// 返回
		return otherAnglesLineRetMap;
	}*/

	/**
	 * @Description 查询模型：其余角度侵蚀线/凝固线数据项
	 * 
	 * @param itemId
	 */
	@Override
	public HashMap<Double, List<HashMap>> dealResidualThickness(HashMap paramsMap) {
		// 声明otherAnglesLineRetMap集合
		HashMap<Double, List<HashMap>> otherAnglesLineRetMap = new HashMap<>();
		// 查询标高数据项
		List<HashMap> elevationList = queryElevation();
		for (HashMap elevationMap : elevationList) {
			// 获取标高
			Object high = elevationMap.get("HIGH");
			// 设置标高
			paramsMap.put("high", high);
			// 根据标高获取侵蚀线/凝固线
			List<HashMap> OtherAnglesLineList = queryResidualThickness(paramsMap);
			// 遍历OtherAnglesLineList集合
			for (HashMap OtherAnglesLineMap : OtherAnglesLineList) {
				// 获取角度
				Double angle = Double.valueOf(OtherAnglesLineMap.get("angle").toString());
				// 获取半径
				Object radius = OtherAnglesLineMap.get("radius");
				// 获取标高
				Object height = OtherAnglesLineMap.get("height");
				// 根据角度获取valueList集合
				List valueList = otherAnglesLineRetMap.get(angle);
				valueList = valueList == null ? new ArrayList<>() : valueList;
				// 声明valueMap集合
				HashMap valueMap = new HashMap<>();
				valueMap.put("R", radius);
				valueMap.put("Z", height);
				// 处理valueMap到valueList集合
				valueList.add(valueMap);
				// 处理valueList到otherAnglesLineRetMap集合
				otherAnglesLineRetMap.put(angle, valueList);
			}
		}
		// 返回
		return otherAnglesLineRetMap;
	}
	@Override
	public HashMap<Object, List<HashMap>> dealResidualThicknessHis(HashMap paramsMap) {
		// 声明otherAnglesLineRetMap集合
		HashMap<Object, List<HashMap>> otherAnglesLineRetMap = new HashMap<>();
		List<HashMap> elevationList;
		if(paramsMap.get("high")!=null&&paramsMap.get("angle")!=null){
			 elevationList= new ArrayList<>();
			HashMap elevationMap=new HashMap<>();
			elevationMap.put("high", paramsMap.get("high"));
			elevationMap.put("angle", paramsMap.get("angle"));
			elevationList.add(elevationMap);
		}else {
			// 查询标高数据项
			elevationList = queryElevation();
		}
			for (HashMap elevationMap : elevationList) {
//				 获取标高(在标高角度为空时出错)
				Object high = elevationMap.get("high");
				// 设置标高
				paramsMap.put("high", high);
				// 根据标高获取侵蚀线/凝固线历史数据
				List<HashMap> OtherAnglesLineList = queryResidualThicknessHis(paramsMap);
				// 遍历OtherAnglesLineList集合
				for (HashMap OtherAnglesLineMap : OtherAnglesLineList) {
					// 获取角度
					Double angle = Double.valueOf(OtherAnglesLineMap.get("angle").toString());
					// 获取半径
					Object radius = OtherAnglesLineMap.get("radius");
					// 获取标高
					Object height = OtherAnglesLineMap.get("height");
//					// 获取时间
//					Object time = OtherAnglesLineMap.get("STARTDATE").toString();
					// 测试获取时间转json后变成时间戳字符串
					Object time = OtherAnglesLineMap.get("STARTDATE");
					// 根据角度获取valueList集合
					List valueList = otherAnglesLineRetMap.get(height+"@"+angle+"@"+time);
					valueList = valueList == null ? new ArrayList<>() : valueList;
					// 声明valueMap集合
					HashMap valueMap = new HashMap<>();
					if(paramsMap.get("slag")=="all"){
						if(Integer.parseInt(paramsMap.get("itemId").toString())==65){
							valueMap.put("ErosionR", radius);
							valueMap.put("Z", height);
							valueMap.put("time", time);
						}else{
							valueMap.put("SolidR", radius);
							valueMap.put("Z", height);
							valueMap.put("time", time);
						}

					}else {
						valueMap.put("R", radius);
						valueMap.put("Z", height);
						valueMap.put("time", time);
					}
					// 处理valueMap到valueList集合
					valueList.add(valueMap);
					// 处理valueList到otherAnglesLineRetMap集合
					otherAnglesLineRetMap.put(height+"@"+angle+"@"+time, valueList);
				}
			}

		// 返回
		return otherAnglesLineRetMap;
	}
	/**
	 * 处理用户提供的侵蚀线数据项：标高大于16，而且测量周期很长
	 */
	public HashMap<Double, List<HashMap>> dealMdWrPointValue() {
		// 声明MdWrPointValueRetMap集合
		HashMap<Double, List<HashMap>> MdWrPointValueRetMap = new HashMap<>();
		//
		List<HashMap> MdWrPointValueList = queryMdWrPointValue();
		//
		for (int i = 0; i < MdWrPointValueList.size(); i++) {
			HashMap MdWrPointValueMap = MdWrPointValueList.get(i);
			// 获取角度
			Double angle = Double.parseDouble(MdWrPointValueMap.get("angle").toString());
			// 获取半径
			Object radius = MdWrPointValueMap.get("radius");
			// 获取标高
			Object height = MdWrPointValueMap.get("height");
			// 根据角度获取valueList集合
			List valueList = MdWrPointValueRetMap.get(angle);
			valueList = valueList == null ? new ArrayList<>() : valueList;
			// 声明valueMap集合
			HashMap valueMap = new HashMap<>();
			valueMap.put("R", radius);
			valueMap.put("Z", height);
			// 处理valueMap到valueList集合
			valueList.add(valueMap);
			// 处理valueList到MdWrPointValueRetMap集合
			MdWrPointValueRetMap.put(angle, valueList);
		}
		return MdWrPointValueRetMap;
	}
}