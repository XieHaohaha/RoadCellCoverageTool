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

	final static int gridSize = 400;

	public static void main(String[] args) {
		// Step1: 加载 线路配置

		// Step1.1：解析多边形的经纬度，存于List容器中

		// Step1.2：以左上右下的点组成的矩形

		long startTime = System.currentTimeMillis();// 获取当前时间

		final Map<Point, List<Road>> gridMap = new HashMap<>();

		try {
			FileReader.readFiles("D:\\tb.txt", new LineHandler() {

				@Override
				public void handle(String line) {

					String[] words = line.split("\t");

					Road road = new Road(words);

					setGridMap(road, gridMap);
				}
			});

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
					List<Road> roadList = findRoadList(longitude, latitude, gridMap);
					if (roadList != null) {
						for (int i = 0; i < roadList.size(); i++) {
							Road road = roadList.get(i);
							List<Point> vertexes = road.polygonPoints;

							if (PolygonUtil.isPointInOrOnPolygon(longitude, latitude, vertexes)) {
								int subId = road.subId;
								int time = locationItem.itime;
								int eci = locationItem.eci;
								RoadCellCoverage roadCellCoverage = new RoadCellCoverage(subId, time, time, eci);

								String subIdEci = "" + road.subId + "__" + locationItem.eci;
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

			for (RoadCellCoverage value : map.values()) {
				System.out.println(value.subId + "\t" + value.stime + "\t"
						+ value.etime + "\t" + value.eci + "\t" + value.num);
			}

			long endTime = System.currentTimeMillis();
			System.out.println("程序运行时间：" + (endTime - startTime) + "ms");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 筛选道路配置
	private static List<Road> findRoadList(double longitude, double latitude,
			Map<Point, List<Road>> gridMap) {

		int lngLeftLowerGrid = ((int) (longitude * 100000)) / gridSize
				* gridSize;
		int latLeftLowerGrid = ((int) (latitude * 100000)) / gridSize
				* gridSize;

		return gridMap.get(new Point(lngLeftLowerGrid, latLeftLowerGrid));
	}

	// 设置栅格Map
	private static void setGridMap(Road road, Map<Point, List<Road>> gridMap) {

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
				List<Road> roadList = gridMap.get(subLeftLowerGrid);
				if (roadList == null) {
					List<Road> list = new ArrayList<>();
					list.add(road);
					gridMap.put(subLeftLowerGrid, list);
				} else {
					roadList.add(road);
					gridMap.put(subLeftLowerGrid, roadList);
				}
				j = j + 400;
			}
			i = i + 400;
		}

	}
}
