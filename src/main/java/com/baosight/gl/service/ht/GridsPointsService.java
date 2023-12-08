package com.baosight.gl.service.ht;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;

@SuppressWarnings("all")
public interface GridsPointsService {

	public void dealGridsPoints() throws Exception;

	public HashMap<String, List<Double>> getGrids() throws Exception;

	public HashMap<String, List<Double>> getPoints() throws Exception;

	public List<Double> readFileGridsPoints(String filePath, String type) throws Exception;

	public void writeFileGridsPoints(List<Double> lineRow, String filePath) throws Exception;

}