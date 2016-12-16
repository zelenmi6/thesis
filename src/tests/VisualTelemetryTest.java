package tests;

import java.io.IOException;

import analyzers.OdometricAngles;
import analyzers.OdometryTelemetryComparator;

public class VisualTelemetryTest {
	
	public VisualTelemetryTest() throws Exception {
		test2();
	}
	
	private void test2() throws Exception {
		int[][] yawPairs = new int[][] {{1, 277}, {277, 370}, {370, 277}, {433, 509}, 
			{581, 662}, {622, 820}, {820, 1097}, {1097, 1170}, {1170, 1205}};
//		int[][] yawPairs = new int[][] {{1, 200}};
		OdometricAngles oa = new OdometricAngles("C:\\Users\\Milan\\Desktop\\telemetry_test\\newYawTest\\", "jpg");
		try {
			oa.runCalculationPairs(yawPairs);
		} catch (Exception e) {
//			// nothing
		} finally {
			oa.serializeResults("newYawPairResultTest.ser");
		}
		
		OdometryTelemetryComparator odometryComparator = 
				new OdometryTelemetryComparator("C:\\Users\\Milan\\Desktop\\telemetry_test\\newYawTest\\newYawPairResultTest.ser",
						yawPairs, 25);
		odometryComparator.loadTelemetry("C:\\Users\\Milan\\Desktop\\telemetry_test\\apmLog\\2016_12_06_21_49_43.csv");
		odometryComparator.compareTelemetryOdometry();
	}
	
	private void test1() throws Exception {
		int pictureNum = 2581/25;
//		int pictureNum = 3;
//		int[] chain = new int[]{350, 375};
//		int[] indices = new int[pictureNum];
//		for (int i = 1; i <= pictureNum; i ++) {
//			indices[i-1] = i*25;
//		}
//		int[][] pairs = new int[][]{{1, 25},{55, 88}};
//		int[][] pairs = new int[][]{{498, 560}, {498, 618}, {618, 677}, {618, 762}, {677, 762}};
//		int[][] rollPairs = new int[][]{/*{471, 504}, {471, 545}, {545, 613}, {545, 701}, {613, 701},*/ /*{1617, 1671},*/ {1617, 1753}}; //roll
//		int[][] pitchPairs = new int[][]{/*{832, 863},*/ /* {832, 886}*/ /*{1041, 1054}, {1783, 1794}, {1794, 1803} {1840, 1853}*/};
		int[][] yawPairs = new int[][]{{1477, 1574}, {1916, 1933}};
//		int[][] allPairs = new int[][]{{471, 504}, {471, 545}, {545, 613}, {545, 701}, {613, 701},{1617, 1671}, {1617, 1753},
//										{832, 863}, {832, 886}, {1041, 1054}, {1783, 1794}, {1794, 1803}, {1840, 1853},
//										{1477, 1574}, {1477, 1671}, {1916, 1933}};
//		
//		
		OdometricAngles oa = new OdometricAngles("C:\\Users\\Milan\\Desktop\\telemetry_test\\frames_after_beep\\", "jpg");
//		try {
////			oa.runCalculationChain(chain);
			oa.runCalculationPairs(yawPairs);
//		} catch (Exception e) {
////			 nothing
//		} finally {
			oa.serializeResults("yawPairResultTest.ser");
//		}
//			
		OdometryTelemetryComparator odometryComparator = 
				new OdometryTelemetryComparator("C:\\Users\\Milan\\Desktop\\telemetry_test\\frames_after_beep\\yawPairResultTest.ser",
						yawPairs, 25);
		odometryComparator.loadTelemetry("C:\\Users\\Milan\\Desktop\\telemetry_test\\apmLog\\2016_11_24_15_50_07.csv");
		odometryComparator.compareTelemetryOdometry();
		
//		OdometryAnalyzer odometryAnalyzer = 
//				new OdometryAnalyzer("C:\\Users\\Milan\\Desktop\\telemetry_test\\frames\\results_every25_frame.ser", 1000, 25);
//		odometryAnalyzer.printRollForWolfram();
//		odometryAnalyzer.printRollComulativeForWolfram();
//		odometryAnalyzer.printRollDebug();
	}

}
