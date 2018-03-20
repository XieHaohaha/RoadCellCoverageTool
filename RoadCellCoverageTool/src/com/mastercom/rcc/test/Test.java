package com.mastercom.rcc.test;

import java.util.HashMap;
import java.util.Map;

import com.mastercom.rcc.model.Point;

public class Test {

	public static void main(String[] args) {
		Point p1 = new Point(1.11, 2.22);
		Point p2 = new Point(1.11, 2.22);
		Point p3 = new Point(2.22, 2.22);
		
		Map<Point, Integer> map = new HashMap<Point, Integer>();
		
		map.put(p1, 11);
		map.put(p2, 22);
		map.put(p3, 33);
		
		System.out.println(map.get(p1));
		
	}
	
}
