import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;

import com.mastercom.rcc.model.LocationItem;
import com.mastercom.rcc.model.Point;
import com.mastercom.rcc.model.Road;
import com.mastercom.rcc.model.RoadCellCoverage;
import com.mastercom.rcc.util.FileReader;
import com.mastercom.rcc.util.FileReader.LineHandler;
import com.mastercom.rcc.util.FileWriter;
import com.mastercom.rcc.util.FileWriter.LineGetter;
import com.mastercom.rcc.util.PolygonUtil;

public class Main {

	final static int gridSize = 400;
	
	public static void main(String[] args) {
		// Step1: 加载 线路配置

		// Step1.1：解析多边形的经纬度，存于List容器中

		// Step1.2：以左上右下的点组成的矩形
		
		if (args.length < 3) {
			System.out.println("usage:tbFilePath, dataFilePath, outputPath, HDFSPath" + "\n");
			System.exit(0);
		}
		
		String tbFilePath = args[0];
		String dataFilePath = args[1];
		String outputPath = args[2];

		long startTime = System.currentTimeMillis();// 获取当前时间

		final Map<Point, List<Road>> gridMap = new HashMap<>();

		try {
			Configuration conf = new Configuration();
			
			if (args.length > 3) {
				String HDFSPath = args[3];
				conf.set("fs.defaultFS", HDFSPath);//"hdfs://192.168.1.31:9000"
			}
			
			FileReader.readFiles(conf, tbFilePath, new LineHandler() {  //"/tmp/tb.txt"
			
				@Override
				public void handle(String line) {

					String[] words = line.split("\t");
					
					Road road = new Road(words);

					setGridMap(road, gridMap);
				}
			});

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
			
			FileWriter.writeToFile(conf, outputPath, new LineGetter() {  //"D:\\result.txt"
				
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
				j = j + gridSize;
			}
			i = i + gridSize;
		}

	}
}
