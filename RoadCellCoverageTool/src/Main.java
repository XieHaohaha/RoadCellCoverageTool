import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mastercom.rcc.model.LocationItem;
import com.mastercom.rcc.model.Point;
import com.mastercom.rcc.model.Road;
import com.mastercom.rcc.model.RoadCellCoverage;
import com.mastercom.rcc.util.FileReader;
import com.mastercom.rcc.util.FileReader.LineHandler;
import com.mastercom.rcc.util.PolygonUtil;

public class Main {

	public static void main(String[] args) {
		// Step1: 加载 线路配置

		// Step1.1：解析多边形的经纬度，存于List容器中

		// Step1.2：以左上右下的点组成的矩形
		
		long startTime = System.currentTimeMillis();//获取当前时间

		// 道路配置集合
		final List<Road> roadList = new ArrayList<>();

		try {
			FileReader.readFiles("D:\\tb.txt", new LineHandler() {

				@Override
				public void handle(String line) {

					String[] words = line.split("\t");

					Road road = new Road(words);
					roadList.add(road);
				}
			});
			
//			Road road1 = roadList.get(0);
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
					for (int i = 0; i < roadList.size(); i++) {
						Road road = roadList.get(i);
						List<Point> vertexes = road.polygonPoints;
						List<Point> rectanglePoints = road.RectanglePoints;

						// 判断是否在多边形内之前，先判断是否在对应矩形内
						if (isPointInOrOnRectangle(longitude, latitude,
								rectanglePoints)) {

							if (PolygonUtil.isPointInOrOnPolygon(longitude,
									latitude, vertexes)) {

								int subId = road.subId;
								int time = locationItem.itime;
								int eci = locationItem.eci;
								RoadCellCoverage roadCellCoverage = new RoadCellCoverage(
										subId, time, time, eci);

								String subIdEci = "" + road.subId
										+ locationItem.eci;
								if (map.keySet().contains(subIdEci)) {
									RoadCellCoverage coverage = map
											.get(subIdEci);
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
					// System.out.println(locationList);
				}
			});

			for (RoadCellCoverage value : map.values()) {
				System.out.println(value.subId + "\t" + value.stime + "\t"
						+ value.etime + "\t" + value.eci);
			}
			
			long endTime = System.currentTimeMillis();
			System.out.println("程序运行时间："+(endTime-startTime)+"ms");

		} catch (Exception e) {
			e.printStackTrace();
		}

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
