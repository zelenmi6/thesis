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

/**
 * This class serves as a comparator between three different data sets
 * that are identical except for the altitude used for their computations.
 * The purpose is to find out which altitude is the most correct for
 * the purposes of video mapping. The original idea is to compare the 
 * real (altitude from sensors) altitude to a lower and a higher altitude.
 * @author Milan Zelenka
 *
 */
public class AltitudeErrorTest {
	
	VideoPicturesDao dao = VideoPicturesDao.getInstance();
	AbstractCamera camera;
	
	/**
	 * Constructor
	 * @param camera Camera model used
	 * @throws Exception
	 */
	public AltitudeErrorTest(AbstractCamera camera) throws Exception {
		this.camera = camera;
	}
	
	/**
	 * 
	 * @param x Cartesian x coordinate of the object of interest
	 * @param y Cartesian y coordinate of the object of interest
	 * @param z Cartesian z coordinate of the object of interest
	 * @param dataSetLowerId Id of a data set with its altitude lower than the altitude from sensors
	 * @param realDataSetId Id of a data set with the altitude from sensors
	 * @param dataSetHigherId Id of a data set with its altitude higher than the altitude from sensors
	 * @param intervalsOnScreen a 2d array of frame intervals when the object of interest is visible
	 * @throws Exception
	 */
	public void polygonsContainingPoint(double x, double y, double z,
			int dataSetLowerId, int realDataSetId, int dataSetHigherId, int[][] intervalsOnScreen) throws Exception {
		List<int[]> frames2dSmaller = dao.getFramesContainingPoint2dFromDataSet(x, y, dataSetLowerId);
		List<int[]> frames2dReal = dao.getFramesContainingPoint2dFromDataSet(x, y, realDataSetId);
		List<int[]> frames2dBigger = dao.getFramesContainingPoint2dFromDataSet(x, y, dataSetHigherId);
		
		List<int[]> framesSmallerContaining = getFramesContainingPoint3d(x, y, z, frames2dSmaller, camera);
		List<int[]> framesRealContaining = getFramesContainingPoint3d(x, y, z, frames2dReal, camera);
		List<int[]> framesBiggerContaining = getFramesContainingPoint3d(x, y, z, frames2dBigger, camera);
//		printResults(framesSmallerContaining, framesBiggerContaining);
		printResultsForWolfram(framesSmallerContaining, framesRealContaining, framesBiggerContaining, realDataSetId, intervalsOnScreen);
	}
	
	/**
	 * Computes all frames containing a point of interest
	 * @param x Cartesian x coordinate of the object of interest
	 * @param y Cartesian y coordinate of the object of interest
	 * @param z Cartesian z coordinate of the object of interest
	 * @param frames2d List of 2-element int arrays representing frames in the database. 
	 * The first element of the array contains the id of the frame, the second one its frame number in the vido.
	 * @param camera Camera model used.
	 * @return
	 * @throws SQLException
	 */
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
	
	// Prints output lists for Wolfram Mathematica charts
	private void printResultsForWolfram(List<int[]> framesSmallerContaining, List<int[]> framesRealContaining,
			List<int[]> framesBiggerContaining, int realDataSetId, int [][] intervalsOnScreen)
			throws Exception {
		int lastFrameNum = framesBiggerContaining.get(framesBiggerContaining.size()-1)[1];
		List<double[]> dsInfo = dao.getFrameIdsAndFrameNumbersAndAltitudesFromDataSet(realDataSetId);
		
		StringBuilder altitudeSeries = new StringBuilder(1000);
		StringBuilder innerContainingSeries = new StringBuilder(1000);
		StringBuilder realContainingSeries = new StringBuilder(1000);
		StringBuilder outerContainingSeries = new StringBuilder(1000);
		StringBuilder onScreen = new StringBuilder(1000);
		
		String[] names = new String[]{"altitude", "inner", "real", "outer", "onscreen"};
		
		altitudeSeries.append(names[0]).append(" = {");
		innerContainingSeries.append(names[1]).append(" = {");
		realContainingSeries.append(names[2]).append(" = {");
		outerContainingSeries.append(names[3]).append(" = {");
		onScreen.append(names[4]).append(" = {");
		
				
		for (int i = 0; i < lastFrameNum; i ++) {
			altitudeSeries.append(getWolframPair(i, dsInfo.get(i)[2])).append(", ");
			if (list2DContains(framesSmallerContaining, 1, i)) {
				innerContainingSeries.append(getWolframPair(i, 1.3)).append(", ");
			} else {
				innerContainingSeries.append("{").append(i).append(", None}, ");
			}
			if (list2DContains(framesBiggerContaining, 1, i)) {
				outerContainingSeries.append(getWolframPair(i, 1.7)).append(", ");
			} else {
				outerContainingSeries.append("{").append(i).append(", None}, ");
			}
			if (list2DContains(framesRealContaining, 1, i)) {
				realContainingSeries.append(getWolframPair(i, 1.5)).append(", ");
			} else {
				realContainingSeries.append("{").append(i).append(", None}, ");
			}
			if (intervalsContain(intervalsOnScreen, i)) {
				onScreen.append("{").append(i).append(", 2}, ");
			} else {
				onScreen.append("{").append(i).append(", 1}, ");
			}
		}
		altitudeSeries.deleteCharAt(altitudeSeries.length()-2).append("};");
		innerContainingSeries.deleteCharAt(innerContainingSeries.length()-2).append("};");
		realContainingSeries.deleteCharAt(realContainingSeries.length()-2).append("};");
		outerContainingSeries.deleteCharAt(outerContainingSeries.length()-2).append("};");
		onScreen.deleteCharAt(onScreen.length()-2).append("};");
		
//		System.out.println(generateSeries(altitudeSeries));
		System.out.println(generateSeries(innerContainingSeries));
		System.out.println(generateSeries(realContainingSeries));
		System.out.println(generateSeries(outerContainingSeries));
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

























