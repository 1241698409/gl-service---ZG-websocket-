package com.baosight.gl.domain;

import java.util.Set;

public class CenterGrid {

	public Integer owner;

	public String faces;

	public Double xPointsAvg;

	public Double yPointsAvg;

	public Double zPointsAvg;

	public Set<String> pointsSet;

	public Integer getOwner() {
		return owner;
	}

	public void setOwner(Integer owner) {
		this.owner = owner;
	}

	public String getFaces() {
		return faces;
	}

	public void setFaces(String faces) {
		this.faces = faces;
	}

	public Double getxPointsAvg() {
		return xPointsAvg;
	}

	public void setxPointsAvg(Double xPointsAvg) {
		this.xPointsAvg = xPointsAvg;
	}

	public Double getyPointsAvg() {
		return yPointsAvg;
	}

	public void setyPointsAvg(Double yPointsAvg) {
		this.yPointsAvg = yPointsAvg;
	}

	public Double getzPointsAvg() {
		return zPointsAvg;
	}

	public void setzPointsAvg(Double zPointsAvg) {
		this.zPointsAvg = zPointsAvg;
	}

	public Set<String> getPointsSet() {
		return pointsSet;
	}

	public void setPointsSet(Set<String> pointsSet) {
		this.pointsSet = pointsSet;
	}
}