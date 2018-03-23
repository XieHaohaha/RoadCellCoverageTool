package com.mastercom.rcc.model;

import java.util.ArrayList;
import java.util.List;

public class PointSquare {

	public Point center;
	public Point LeftLower;
	public Point LeftTop;
	public Point RightLower;
	public Point RightTop;
	
	public List<Point> FourPointList;
	
	public PointSquare(Point center, double expansion) {
		super();
		this.center = center;
		
		double longitude = center.x;
		double latitude = center.y;
		
		double lngLeft = (longitude * 100000 - expansion) / 100000;
		double lngRight = (longitude * 100000 + expansion) / 100000;
		double latLower = (latitude * 100000 - expansion) / 100000;
		double latTop = (latitude * 100000 + expansion) / 100000;
		
		LeftLower = new Point(lngLeft, latLower);
		LeftTop = new Point(lngLeft, latTop);
		RightLower = new Point(lngRight, latLower);
		RightTop = new Point(lngRight, latTop);
		
		FourPointList = new ArrayList<>();
		FourPointList.add(LeftLower);
		FourPointList.add(LeftTop);
		FourPointList.add(RightLower);
		FourPointList.add(RightTop);
	}
}
