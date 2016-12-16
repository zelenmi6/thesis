package tests;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector3d;

import cameras.AbstractCamera;
import cameras.Hero4Black;
import cameras.Hero4Black.Hero4BlackFieldOfView;
import cameras.Hero4BlackUndistorted;
import cameras.Hero4BlackUndistorted.Hero4BlackUndistortedFieldOfView;
import database.VideoPicturesDao;
import geometry.CameraCalculator;

public class AltitudeErrorTest {
	
	VideoPicturesDao dao = VideoPicturesDao.getInstance();
	
	
	public AltitudeErrorTest() throws Exception {

	}
	
	public void polygonsContainingPoint(double x, double y, double z, 
			int dataSetLowerId, int dataSetHigherId, int realDataSetId) throws Exception {
		AbstractCamera undistortedCamera = new Hero4BlackUndistorted(Hero4BlackUndistortedFieldOfView.WIDE_16X9, 25);
		AbstractCamera distortedCamera = new Hero4Black(Hero4BlackFieldOfView.WIDE_16X9, 25);
		List<int[]> frames2dSmaller = dao.getFramesContainingPoint2dFromDataSet(x, y, dataSetLowerId); 
		List<int[]> frames2dBigger = dao.getFramesContainingPoint2dFromDataSet(x, y, dataSetHigherId);
		
		List<int[]> framesSmallerContaining = getFramesContainingPoint3d(x, y, z, frames2dSmaller, undistortedCamera);
		List<int[]> framesBiggerContaining = getFramesContainingPoint3d(x, y, z, frames2dBigger, undistortedCamera);
		printResults(framesSmallerContaining, framesBiggerContaining);
//		printResultsForWolfram(framesSmallerContaining, framesBiggerContaining, realDataSetId);
	}
	
	private List<int[]> getFramesContainingPoint3d(double x, double y, double z,
													List<int[]> frames2d, AbstractCamera camera) throws SQLException {
		List<int[]> framesContainingPoint = new ArrayList<>();
		for (int[] frame : frames2d) {
			double [] coordinates, angles;
			coordinates = dao.getCameraCoordinates(frame[0]);
			angles = dao.getCameraAngles(frame[0]);

			Vector3d ray1 = CameraCalculator.ray1(camera.getFovHorizontal(), camera.getFovVertical());
			Vector3d ray2 = CameraCalculator.ray2(camera.getFovHorizontal(), camera.getFovVertical());
			Vector3d ray3 = CameraCalculator.ray3(camera.getFovHorizontal(), camera.getFovVertical());
			Vector3d ray4 = CameraCalculator.ray4(camera.getFovHorizontal(), camera.getFovVertical());
			
			Vector3d [] rotatedVectors = CameraCalculator.rotateRays(
					ray1, ray2, ray3, ray4, angles[0], angles[1], angles[2]);
			boolean inside = CameraCalculator.pointIsInsidePyramid(
					rotatedVectors, new Vector3d(coordinates[0], coordinates[1], coordinates[2]),
					new Vector3d(x, y, z));
			if (inside) {
				framesContainingPoint.add(new int[]{frame[0], frame[1]});
			}
//			if (inside) {
//				System.out.println("Inside, frame id: " + frame[0] + " frame num: " + frame[1] + " time: " + frameToTime(frame[1]));
//			} else {
//				System.out.println("Outside, frame id: " + frame[0] + " frame num: " + frame[1] + " time: " + frameToTime(frame[1]));
//			}
		}
		return framesContainingPoint;
	}
	
	private void printResults(List<int[]> framesSmallerContaining, List<int[]> framesBiggerContaining) {
		int lastFrameOutput = 1;
		for (int[] frame : framesBiggerContaining) {
			if (frame[1]-1 != lastFrameOutput) {
				System.out.println("---------------------------------");
			}
			System.out.print(frame[1]);
			if (list2DContains(framesSmallerContaining, 1, frame[1])) {
				System.out.println(" inner");
			} else {
				System.out.println(" outer");
			}
			lastFrameOutput = frame[1];
		}
	}
	
	private void printResultsForWolfram(List<int[]> framesSmallerContaining, 
			List<int[]> framesBiggerContaining, int realDataSetId)
			throws Exception {
		int firstFrameNum = framesBiggerContaining.get(0)[1];
		int lastFrameNum = framesBiggerContaining.get(framesBiggerContaining.size()-1)[1];
//		List<Integer> ids = dao.getDataSetFrameIds(dataSetId);
//		dao.printDataSetInformation(88);
		List<double[]> dsInfo = dao.getFrameIdsAndFrameNumbersAndAltitudesFromDataSet(realDataSetId);
		
		StringBuilder altitudeSeries = new StringBuilder(1000);
		StringBuilder innerContainingSeries = new StringBuilder(1000);
		StringBuilder outerContainingSeries = new StringBuilder(1000);
		StringBuilder onScreen = new StringBuilder(1000);
		
		String[] names = new String[]{"altitude", "inner", "outer", "onscreen"};
		
		altitudeSeries.append(names[0]).append(" = {");
		innerContainingSeries.append(names[1]).append(" = {");
		outerContainingSeries.append(names[2]).append(" = {");
		onScreen.append(names[3]).append(" = {");
		
		int[][] intervalsOnScreen = new int[][]
				{{1390, 1601}, {1661, 2011}, {2291, 2421}, {2541, 2611}, {2701, 2751}, {2831, 2881}, {2891, 2951}};
		int[][] intervalsOnScreenRectified = new int[][]
				{{1401, 1601}, {1661, 1791}, {1801, 2011}, {2301, 2311}, {2391, 2411}, {2541, 2551}, {2571, 2601}, {2701, 2751}, {2831, 2871}, {2901, 2941}};
		
		for (int i = 0; i < lastFrameNum; i ++) {
			altitudeSeries.append(getWolframPair(i, dsInfo.get(i)[2])).append(", ");
			if (list2DContains(framesSmallerContaining, 1, i)) {
				innerContainingSeries.append(getWolframPair(i, 14.8)).append(", ");
			} else {
				innerContainingSeries.append("{").append(i).append(", None}, ");
			}
			if (list2DContains(framesBiggerContaining, 1, i)) {
				outerContainingSeries.append(getWolframPair(i, 15)).append(", ");
			} else {
				outerContainingSeries.append("{").append(i).append(", None}, ");
			}
			if (intervalsContain(intervalsOnScreenRectified, i)) {
				onScreen.append("{").append(i).append(", 14}, ");
			} else {
				onScreen.append("{").append(i).append(", 13}, ");
			}
		}
//		altitudeSeries.deleteCharAt(altitudeSeries.length()-2).append("};");
//		innerContainingSeries.deleteCharAt(innerContainingSeries.length()-2).append("};");
//		outerContainingSeries.deleteCharAt(outerContainingSeries.length()-2).append("};");
		onScreen.deleteCharAt(onScreen.length()-2).append("};");
		
//		System.out.println(generateSeries(altitudeSeries));
//		System.out.println(generateSeries(innerContainingSeries));
//		System.out.println(generateSeries(outerContainingSeries));
		System.out.println(generateSeries(onScreen));
//		printSeries(names);
	}
	
	private String generateSeries(StringBuilder sb) {
		sb.deleteCharAt(sb.length()-2).append("};");
		return sb.toString();
	}
	
	private void printSeries(String[] names) {
		for (String name : names) {
			System.out.println("ListLinePlot[" + name + "]");
		}
	}
	
	private String getWolframPair(double first, double second) {
		return "{" + first + ", " + second + "}";
	}
	
	private boolean list2DContains(List<int[]> list, int idx, int element) {
		for (int[] array : list) {
			if (array[idx] == element) {
				return true;
			}
		}
		return false;
	}
	
	private boolean intervalsContain(int[][]intervals, int element) {
		for (int[] interval : intervals) {
			if (element >= interval[0] && element <= interval[1]) {
				return true;
			}
		}
		return false;
	}
}

























