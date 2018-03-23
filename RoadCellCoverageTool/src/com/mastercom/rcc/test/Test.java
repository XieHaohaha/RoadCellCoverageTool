package com.mastercom.rcc.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;



import org.apache.hadoop.fs.FileSystem;

import com.mastercom.rcc.model.Point;
import com.mastercom.rcc.util.FileReader;
import com.mastercom.rcc.util.FileReader.LineHandler;

public class Test {

	public static void main1(String[] args) {
		Point p1 = new Point(1.11, 2.22);
		Point p2 = new Point(1.11, 2.22);
		Point p3 = new Point(2.22, 2.22);
		
		Map<Point, Integer> map = new HashMap<Point, Integer>();
		
		map.put(p1, 11);
		map.put(p2, 22);
		map.put(p3, 33);
		
		System.out.println(map.get(p1));
		
	}
	
	public static void main2(String[] args) throws Exception
	{
		
		org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
		conf.set("fs.defaultFS", "hdfs://192.168.1.31:9000");
		
		org.apache.hadoop.fs.FileSystem fs = org.apache.hadoop.fs.FileSystem.get(conf);
		
		System.out.println(fs.toString());
	}
	
	public static void main(String[] args) throws Exception
	{
		
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://192.168.1.31:9000");
		
		FileSystem fs = FileSystem.get(conf);
		
		FileReader.readFile(conf, "/tmp/tb.txt", new LineHandler() {
			
			@Override
			public void handle(String line) {
				System.out.println(line);
			}
		});
	}

	
}
