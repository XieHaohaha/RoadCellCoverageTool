import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.mastercom.rcc.model.LocationItem;
import com.mastercom.rcc.model.Point;
import com.mastercom.rcc.model.Road;
import com.mastercom.rcc.model.RoadCellCoverage;
import com.mastercom.rcc.util.FileReader;
import com.mastercom.rcc.util.FileReader.LineHandler;
import com.mastercom.rcc.util.PolygonUtil;

public class Main {
	
	static long startTime = 0;

	public static void main(String[] args) {
		// Step1: 加载 线路配置

		// Step1.1：解析多边形的经纬度，存于List容器中

		// Step1.2：以左上右下的点组成的矩形

		startTime = System.currentTimeMillis();// 获取当前时间

		// 结果写入文件
		// try {
		// PrintStream ps = new PrintStream("D:/log.txt");
		// System.setOut(ps);
		// } catch (FileNotFoundException e1) {
		// e1.printStackTrace();
		// }

		// 道路配置集合
		final List<Road> roadList = new ArrayList<>();

		final TreeMap<Double, Road> lngMinMap = new TreeMap<>();// 经度最小值

		final TreeMap<Double, Road> lngMaxMap = new TreeMap<>();// 经度最大值

		final TreeMap<Double, Road> latMinMap = new TreeMap<>();// 纬度最小值

		final TreeMap<Double, Road> latMaxMap = new TreeMap<>();// 纬度最大值

		try {
			FileReader.readFiles("D:\\tb.txt", new LineHandler() {

				@Override
				public void handle(String line) {

					String[] words = line.split("\t");

					Road road = new Road(words);
					roadList.add(road);
					setMapsFromPolygonPoints(road, lngMinMap, lngMaxMap,
							latMinMap, latMaxMap);
				}
			});
			
//			System.out.println(lngMinMap.size());
//			System.out.println(latMaxMap.size());
//			
//			Road road1 = roadList.get(1);
//			List<Point> polygonPoints = road1.polygonPoints;
//			for (int i = 0; i < polygonPoints.size(); i++) {
//				System.out.println(polygonPoints.get(i).x + "\t" + polygonPoints.get(i).y);
//			}
//			System.out.println(road1.RectanglePoints.get(0).x + "\t" + road1.RectanglePoints.get(0).y);
//			System.out.println(road1.RectanglePoints.get(1).x + "\t" + road1.RectanglePoints.get(1).y);

			final Map<String, RoadCellCoverage> map = new HashMap<>();

			// 加载数据

			FileReader.readFile("D:\\loc.dat", new LineHandler() {

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
					
					System.out.println("求交集前：" + (System.currentTimeMillis() - startTime) + "ms");
					Collection<Road> possibleRoad = getPossibleRoad(longitude, latitude, lngMinMap, lngMaxMap, latMinMap, latMaxMap);
					System.out.println("求交集后：" + (System.currentTimeMillis() - startTime) + "ms");
					Iterator<Road> iter = possibleRoad.iterator();  
			        //通过循环迭代  
			        while(iter.hasNext()){  
			        	Road road = (Road)iter.next();
			            List<Point> polygonPoints = road.polygonPoints;
			            
			            if (PolygonUtil.isPointInOrOnPolygon(longitude, latitude, polygonPoints)) {
							int subId = road.subId;
							int time = locationItem.itime;
							int eci = locationItem.eci;
							RoadCellCoverage roadCellCoverage = new RoadCellCoverage(
									subId, time, time, eci);

							String subIdEci = "" + road.subId
									+ locationItem.eci;
							if (map.keySet().contains(subIdEci)) {
								RoadCellCoverage coverage = map.get(subIdEci);
								if (time > coverage.stime
										&& time < coverage.etime) {
									return;
								}
							} else {
								map.put(subIdEci, roadCellCoverage);
							}

						}
			        }  
				}
			});

			for (RoadCellCoverage value : map.values()) {
				System.out.println(value.subId + "\t" + value.stime + "\t"
						+ value.etime + "\t" + value.eci);
			}

			long endTime = System.currentTimeMillis();
			System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static Collection<Road> getPossibleRoad(double longitude,
			double latitude, TreeMap<Double, Road> lngMinMap,
			TreeMap<Double, Road> lngMaxMap, TreeMap<Double, Road> latMinMap,
			TreeMap<Double, Road> latMaxMap) {
		
		System.out.println("headtail前：" + (System.currentTimeMillis() - startTime) + "ms");
		Collection<Road> collection1 = lngMinMap.headMap(longitude).values();
		Collection<Road> collection2 = lngMaxMap.tailMap(longitude).values();
		Collection<Road> collection3 = latMinMap.headMap(latitude).values();
		Collection<Road> collection4 = latMaxMap.tailMap(latitude).values();
		System.out.println("headtail后：" + (System.currentTimeMillis() - startTime) + "ms");
		
		Collection<Road> intersection = intersection(collection1, collection2, collection3, collection4);
		
		return intersection;
	}

	private static void setMapsFromPolygonPoints(Road road,
			TreeMap<Double, Road> lngMinMap, TreeMap<Double, Road> lngMaxMap,
			TreeMap<Double, Road> latMinMap, TreeMap<Double, Road> latMaxMap) {
		List<Point> rectanglePoints = road.RectanglePoints;
		lngMinMap.put(rectanglePoints.get(0).x, road);
		lngMaxMap.put(rectanglePoints.get(1).x, road);
		latMinMap.put(rectanglePoints.get(0).y, road);
		latMaxMap.put(rectanglePoints.get(1).y, road);

//		System.out.println(lngMinMap.size());
	}
	
	//求交集
	public static <T> Collection<T> intersection(Collection<T> collection1,
            Collection<T> collection2,Collection<T> collection3,Collection<T> collection4) {
        Collection<T> collection = new ArrayList<T>(collection1); // How would it be??
       /* for (T t : collection1) {
            if (collection2.contains(t)&&collection3.contains(t)&&collection4.contains(t)) {
                collection.add(t);
            }
        }*/
        collection.retainAll(collection2);
        collection.retainAll(collection3);
        collection.retainAll(collection4);
        return collection;
    }

	// 点在多边形内算法优化
	public static boolean isPointInOrOnRectangle(double x, double y,
			List<Point> points) {
		int intResult = checkPointInOrOnRectangle(x, y, points);
		return intResult != 0;
	}

	public static int checkPointInOrOnRectangle(double x, double y,
			List<Point> points) {
		int intResult = 0;// 0在矩形外，1在矩形里面，-1在矩形边界上
		Point leftTop = points.get(0);
		Point rightLower = points.get(1);

		if (x > leftTop.x && x < rightLower.x && y > leftTop.y
				&& y < rightLower.y) {
			intResult = 1;
		} else if (x == leftTop.x && x == rightLower.x && y == leftTop.y
				&& y == rightLower.y) {
			intResult = -1;
		}

		return intResult;
	}

}
