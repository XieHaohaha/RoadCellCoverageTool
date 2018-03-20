package com.mastercom.rcc.model;

import java.util.ArrayList;
import java.util.List;

import com.mastercom.rcc.util.BitConverter;

public class Road {

	public final int id;

	public final int subId;

	public final String name;

	public final int endpointId1;

	public final int endpointId2;

	public final double length;

	public final byte[] linePointBytes;

	public final byte[] polygonPointBytes;

	public final String type;

	public List<Point> linePoints;

	public List<Point> polygonPoints;

	public List<Point> RectanglePoints;

	public Road(String[] args) {
		if (args.length != 9) {
			throw new IllegalArgumentException();
		}

		id = Integer.parseInt(args[0]);
		subId = Integer.parseInt(args[1]);
		name = args[2];
		endpointId1 = Integer.parseInt(args[3]);
		endpointId2 = Integer.parseInt(args[4]);
		length = Double.parseDouble(args[5]);
		linePointBytes = BitConverter.toBytes(args[6]);
		polygonPointBytes = BitConverter.toBytes(args[7]);
		type = args[8];

		polygonPoints = getPointsFromBytes(polygonPointBytes);

		// 点在多边形内算法优化
		RectanglePoints = getPointsFromPolygonPoints(polygonPoints);
	}

	private List<Point> getPointsFromBytes(byte[] borderBytes) {
		List<Point> lstPoints = new ArrayList<Point>();
		if (borderBytes == null) {
			return lstPoints;
		}
		int pos = 0;
		while (pos < borderBytes.length) {
			int iLng = BitConverter.toInt(borderBytes, pos);
			double lng = 1d * iLng / 10000000;
			pos += 4;

			int iLat = BitConverter.toInt(borderBytes, pos);
			double lat = 1d * iLat / 10000000;
			pos += 4;

			lstPoints.add(new Point(lng, lat));
		}
		return lstPoints;
	}

	private List<Point> getPointsFromPolygonPoints(List<Point> points) {
		List<Point> lstPoints = new ArrayList<Point>();
		double lngMin = 0;//经度最小值
		double lngMax = 0;//经度最大值
		double latMin = 0;//纬度最小值
		double latMax = 0;//纬度最大值
		
		//计算经度最小值
		for (int i = 0; i < points.size(); i++) {
			if (points.get(i).x < lngMin) {
				lngMin = points.get(i).x;
			}
			if (points.get(i).x > lngMax) {
				lngMax = points.get(i).x;
			}
			if (points.get(i).y < latMin) {
				latMin = points.get(i).y;
			}
			if (points.get(i).y > latMax) {
				latMax = points.get(i).y;
			}
		}
		lstPoints.add(new Point(lngMin, latMin));
		lstPoints.add(new Point(lngMax, latMax));
		return lstPoints;
	}

}
