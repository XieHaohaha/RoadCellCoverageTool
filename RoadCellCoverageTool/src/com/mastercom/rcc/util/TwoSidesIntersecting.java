package com.mastercom.rcc.util;

import com.mastercom.rcc.model.Point;

public class TwoSidesIntersecting {
	
	//判断线段p1p2和p3p4是否相交
	public static boolean segmentsIntersect(Point p1, Point p2, Point p3,Point p4) {
	    double d1 = direction(p1, p2, p3);
	    double d2 = direction(p1, p2, p4);
	    double d3 = direction(p3, p4, p1);
	    double d4 = direction(p3, p4, p2);

	    if (d1 > 0 && d2 < 0 || d1 < 0 || d2>0 || d3>0 && d4 < 0 || d3 < 0 && d4>0)
	        return true;
	    if (Math.abs(d1) <= 1e-9 && onSegment(p1, p2, p3)) return true;
	    if (Math.abs(d2) <= 1e-9 && onSegment(p1, p2, p4)) return true;
	    if (Math.abs(d3) <= 1e-9 && onSegment(p3, p4, p1)) return true;
	    if (Math.abs(d4) <= 1e-9 && onSegment(p3, p4, p2)) return true;
	    return false;
	}

	//这是判断p3是在线段p1p2的哪一侧
	public static double direction(Point p1, Point p2, Point p3) {
	    return (p2.x - p1.x)*(p3.y - p2.y) - (p3.x - p2.x)*(p2.y - p1.y);
	}

	//这是判断点p3是否在以p1p2为对角线的矩形内
	public static boolean onSegment(Point p1, Point p2, Point p3) {
	    if (p3.x >= Math.min(p1.x, p2.x) && p3.x <= Math.max(p1.x, p2.x) &&
	        p3.y >= Math.min(p1.y, p2.y) && p3.y <= Math.max(p1.y, p2.y))
	        return true;
	    return false;
	}

}
