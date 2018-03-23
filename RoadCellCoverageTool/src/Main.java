import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;

import com.mastercom.rcc.model.LocationItem;
import com.mastercom.rcc.model.Point;
import com.mastercom.rcc.model.PointSquare;
import com.mastercom.rcc.model.Road;
import com.mastercom.rcc.model.RoadCellCoverage;
import com.mastercom.rcc.util.FileReader;
import com.mastercom.rcc.util.FileReader.LineHandler;
import com.mastercom.rcc.util.FileWriter;
import com.mastercom.rcc.util.FileWriter.LineGetter;
import com.mastercom.rcc.util.PolygonUtil;
import com.mastercom.rcc.util.TwoSidesIntersecting;

public class Main {

	final static int gridSize = 400; //栅格大小
	final static int expansion = 20; //多边形周围20米
	
	public static void main(String[] args) {
		
		if (args.length < 3) {
			System.out.println("usage:tbFilePath, dataFilePath, outputPath, HDFSPath" + "\n");
			System.exit(0);
		}
		
		String tbFilePath = args[0];  //"/tmp/tb.txt"
		String dataFilePath = args[1];//"/seq/loc/20171025/loc.dat"
		String outputPath = args[2];  //"D://result.txt"

		long startTime = System.currentTimeMillis();// 获取当前时间

		final Map<Point, Set<Road>> gridMap = new HashMap<>();

		try {
			Configuration conf = new Configuration();
			
			if (args.length > 3) {
				String HDFSPath = args[3];  //"hdfs://192.168.1.31:9000"
				conf.set("fs.defaultFS", HDFSPath);
			}
			
			//加载道路配置
			FileReader.readFiles(conf, tbFilePath, new LineHandler() {
			
				@Override
				public void handle(String line) {

					String[] words = line.split("\t");
					
					Road road = new Road(words);

					setGridMap(road, gridMap);
				}
			});

			//subID+eci,输出对象
			final Map<String, RoadCellCoverage> map = new HashMap<>();

			// 加载数据
			FileReader.readFile(conf, dataFilePath, new LineHandler() {
			
				@Override
				public void handle(String line) {

					String[] words = line.split("\\|");

					LocationItem locationItem = new LocationItem();
					if (!line.contains("|")) {
						return;
					}
					locationItem.fillData(words, 0);

					double longitude = locationItem.longitude;
					double latitude = locationItem.latitude;
					Set<Road> roadSet = findRoadSet(longitude, latitude, gridMap);
					if (roadSet != null) {
						for (Road road : roadSet) {
							List<Point> vertexes = road.polygonPoints;

							//点本身在多边形内，或者多边形的边与点对应的正方形的四条边相交，或者多边形的点在正方形，或者多边形的点在正方形内
							if (PolygonUtil.isPointInOrOnPolygon(longitude, latitude, vertexes) || isIntersect(longitude, latitude, vertexes) || isPolygonPointInOrOnSquare(longitude, latitude, vertexes)) {
								int subId = road.subId;
								int time = locationItem.locTime;
								int eci = locationItem.eci;
								RoadCellCoverage roadCellCoverage = new RoadCellCoverage(subId, time, time, eci);

								String subIdEci = "" + subId + "__" + eci;
								if (map.keySet().contains(subIdEci)) {
									RoadCellCoverage coverage = map.get(subIdEci);
									coverage.num++;
									if (time < coverage.stime) {
										coverage.stime = time;
									}else if(time > coverage.etime){
										coverage.etime = time;
									}
									map.put(subIdEci, coverage);
								} else {
									roadCellCoverage.num = 1;
									map.put(subIdEci, roadCellCoverage);
								}
							}
						}
					}
				}
			});

			final Collection<RoadCellCoverage> roadCellCoverageCollection = map.values();
			
			FileWriter.writeToFile(conf, outputPath, new LineGetter() {
				
				Iterator<RoadCellCoverage> iterator = roadCellCoverageCollection.iterator();
				
				StringBuilder sb = new StringBuilder();
				
				@Override
				public String next() {
					RoadCellCoverage next = iterator.next();
					if (sb.length() > 0) {
						sb.delete(0, sb.length());
					}
					sb.append(next.subId).append("\t").append(next.stime).append("\t").append(next.etime).append("\t").append(next.eci).append("\t").append(next.num);
					return sb.toString();
				}
				
				@Override
				public boolean hasNext() {
					return iterator.hasNext();
				}
			});

			long endTime = System.currentTimeMillis();
			System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//判断多边形的边与点对应的正方形的四条边是否相交
	protected static boolean isIntersect(double longitude, double latitude, List<Point> vertexes) {
		
		PointSquare ps = new PointSquare(new Point(longitude, latitude), expansion);
		
		for (int i = 0; i < vertexes.size(); i++) {
			Point p1 = ps.LeftLower;
			Point p2 = ps.RightLower;
			Point p3 = null;
			Point p4 = null;
			if (i == vertexes.size() - 1) {
				p3 = vertexes.get(i);
				p4 = vertexes.get(0);
			}else{
				p3 = vertexes.get(i);
				p4 = vertexes.get(i + 1);
			}
			
			if (TwoSidesIntersecting.segmentsIntersect(p1, p2, p3, p4)) {
				return true;
			}
			
			p1 = ps.LeftLower;
			p2 = ps.LeftTop;
			if (TwoSidesIntersecting.segmentsIntersect(p1, p2, p3, p4)) {
				return true;
			}
			
			p1 = ps.RightTop;
			p2 = ps.LeftTop;
			if (TwoSidesIntersecting.segmentsIntersect(p1, p2, p3, p4)) {
				return true;
			}
			
			p1 = ps.RightTop;
			p2 = ps.RightLower;
			if (TwoSidesIntersecting.segmentsIntersect(p1, p2, p3, p4)) {
				return true;
			}
		}
		
		return false;
	}

	//判断多边形的点是否在正方形内
	protected static boolean isPolygonPointInOrOnSquare(double longitude, double latitude, List<Point> vertexes) {
		
		double lngLeft = (longitude * 100000 - expansion) / 100000;
		double lngRight = (longitude * 100000 + expansion) / 100000;
		double latLower = (latitude * 100000 - expansion) / 100000;
		double latTop = (latitude * 100000 + expansion) / 100000;
		
		for (Point point : vertexes) {
			if (point.x >= lngLeft && point.x <= lngRight && point.y >= latLower && point.y <= latTop) {
				return true;
			}
		}
		
		return false;
	}
	
	//判断点对应的正方形的四个点是否有任意一个点在多边形内
	protected static boolean isFourPointInOrOnPolygon(double longitude,
			double latitude, List<Point> vertexes) {
		
		double lngLeft = (longitude * 100000 - expansion) / 100000;
		double lngRight = (longitude * 100000 + expansion) / 100000;
		double latLower = (latitude * 100000 - expansion) / 100000;
		double latTop = (latitude * 100000 + expansion) / 100000;
		
		if (PolygonUtil.isPointInOrOnPolygon(lngLeft, latLower, vertexes)) {
			return true;
		}
		if (PolygonUtil.isPointInOrOnPolygon(lngLeft, latTop, vertexes)) {
			return true;
		}
		if (PolygonUtil.isPointInOrOnPolygon(lngRight, latLower, vertexes)) {
			return true;
		}
		if (PolygonUtil.isPointInOrOnPolygon(lngRight, latTop, vertexes)) {
			return true;
		}
		
		return false;
		
	}

	// 筛选道路配置
	private static Set<Road> findRoadSet(double longitude, double latitude,
			Map<Point, Set<Road>> gridMap) {

		int lngLeft = ((int) (longitude * 100000 - expansion)) / gridSize
				* gridSize;
		int lngRight = ((int) (longitude * 100000 + expansion)) / gridSize
				* gridSize;
		int latLower = ((int) (latitude * 100000 - expansion)) / gridSize
				* gridSize;
		int latTop = ((int) (latitude * 100000 + expansion)) / gridSize
				* gridSize;
		
		Set<Road> roadSet = new HashSet<>();
		Point point = new Point(lngLeft, latLower);  //临时变量
		Set<Road> set = gridMap.get(point);  //临时变量
		if (set != null) {
			roadSet.addAll(set);
		}
		
		if (lngLeft != lngRight) {
			point = new Point(lngRight, latLower);  
			set = gridMap.get(point);
			if (set != null) {
				roadSet.addAll(set);
			}
		}
		if (latLower != latTop) {
			point = new Point(lngLeft, latTop);  
			set = gridMap.get(point);
			if (set != null) {
				roadSet.addAll(set);
			}
			
			point = new Point(lngRight, latTop);  
			set = gridMap.get(point);
			if (set != null) {
				roadSet.addAll(set);
			}
		}

		return roadSet;
		
	}

	// 设置栅格Map
	private static void setGridMap(Road road, Map<Point, Set<Road>> gridMap) {

		Point leftLowerPoint = road.leftLowerPoint;
		Point rightTopPoint = road.rightTopPoint;

		// 栅格左下角和右上角边界的经纬度
		int lngLeftLowerGrid = ((int) (leftLowerPoint.x * 100000)) / gridSize
				* gridSize;
		int latLeftLowerGrid = ((int) (leftLowerPoint.y * 100000)) / gridSize
				* gridSize;
		int lngrightTopGrid = ((int) (rightTopPoint.x * 100000)) / gridSize
				* gridSize;
		int latrightTopGrid = ((int) (rightTopPoint.y * 100000)) / gridSize
				* gridSize;

		for (int i = lngLeftLowerGrid; i <= lngrightTopGrid;) {
			for (int j = latLeftLowerGrid; j <= latrightTopGrid;) {
				Point subLeftLowerGrid = new Point(i, j);
				Set<Road> roadSet = gridMap.get(subLeftLowerGrid);
				if (roadSet == null) {
					Set<Road> set = new HashSet<>();
					set.add(road);
					gridMap.put(subLeftLowerGrid, set);
				} else {
					roadSet.add(road);
					gridMap.put(subLeftLowerGrid, roadSet);
				}
				j = j + gridSize;
			}
			i = i + gridSize;
		}

	}
}
