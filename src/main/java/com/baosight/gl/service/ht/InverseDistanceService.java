package com.baosight.gl.service.ht;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public interface InverseDistanceService {

	public HashMap countInverseDistance(String uptFileName, String airFlowName) throws Exception;

	public List<Double> InverseDistanceU(double[] uArrays) throws Exception;

	public List<Double> InverseDistancePT(String airFlowName, double[] ptArrays) throws Exception;

	public Map<String, double[]> readFileUPT(String filePath) throws Exception;

	public double[] readFileGridsPoints(String filePath) throws Exception;

	public void writeFileUPT(List<Double> lineRow, String filePath) throws Exception;

	public double[] range3(double[] data);

	public List<Double> sqrt(Double[] data);

	public HashMap countExtremum(String airFlowName, double[] valueArrays);

}