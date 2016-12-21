package geometry;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import cameras.AbstractCamera;
import cameras.Hero4Black;
import cameras.Hero4Black.Hero4BlackFieldOfView;
import cameras.Hero4BlackUndistorted;
import cameras.Hero4BlackUndistorted.Hero4BlackUndistortedFieldOfView;
import constants.CameraTesting;
import database.VideoPicturesDao;

public class Calculations {
	
	/**
	 * Obsolete and no longer used. Use getFramesWithPoint that specifies the data set to search in.
	 * @param x
	 * @param y
	 * @param z
	 * @throws Exception
	 */
	public static void getFramesWithPoint(double x, double y, double z, int fps) throws Exception {
		VideoPicturesDao dao = VideoPicturesDao.getInstance();
		List<int[]> frames2d = dao.getFramesContainingPoint2d(x, y);
		AbstractCamera camera = new Hero4BlackUndistorted(Hero4BlackUndistortedFieldOfView.WIDE_16X9, 25);
		List<int[]> framesWithPoint = new ArrayList<>();
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
				System.out.println("Inside, frame id: " + frame[0] + " frame num: " + frame[1] + " time: " + frameToTime(frame[1], fps));
			} else {
				System.out.println("Outside, frame id: " + frame[0] + " frame num: " + frame[1] + " time: " + frameToTime(frame[1], fps));
			}
		}
	}
	
	/**
	 * Finds frames containing a point given by Cartesian coordinates in a data set.
	 * Used for purposes of testing when we know the Cartesian coordinates.
	 * In a real scenario, we would use GPS coordinates, find corresponding
	 * Monitored Areas, use their origin for GPS-cartesian conversion and then
	 * use this or a similar method to find the frames.
	 * @param x Cartesian x coordinate of the point of interest
	 * @param y Cartesian y coordinate of the point of interest
	 * @param z Cartesian z coordinate of the point of interest
	 * @param dataSetId Id of the data set to search in
	 * @throws Exception
	 */
	public static void getFramesWithPoint(double x, double y, double z, int dataSetId, int fps) throws Exception {
		VideoPicturesDao dao = VideoPicturesDao.getInstance();
		List<int[]> frames2d = dao.getFramesContainingPoint2dFromDataSet(x, y, dataSetId);
		AbstractCamera camera = new Hero4BlackUndistorted(Hero4BlackUndistortedFieldOfView.WIDE_16X9, 25);
//		List<double[]> intervals = geometry.Calculations.getContinuousIntervalsInSortedList(frames);
//		for (double [] interval : intervals) {
//			System.out.println("Interval: " + interval[0] + ", " + interval[1]);
//		}
		List<int[]> framesWithPoint = new ArrayList<>();
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
				System.out.println("Inside, frame id: " + frame[0] + " frame num: " + frame[1] + " time: " + frameToTime(frame[1], fps));
			} else {
				System.out.println("Outside, frame id: " + frame[0] + " frame num: " + frame[1] + " time: " + frameToTime(frame[1], fps));
			}
		}
	}
	
	private static int frameToMinutes(int frameNum, int fps) {
		return frameNum / fps / 60;
	}
	
	private static int frameToSeconds(int frameNum, int fps) {
		return frameNum / fps % 60;
	}
	
	private static String frameToTime(int frameNum, int fps) {
		return frameToMinutes(frameNum, fps) + ":" + frameToSeconds(frameNum, fps);
	}
	
	/**
	 * Finds all continuous intervals of numbers in a sorted list.
	 * @param numbers Sorted list of array numbers. 
	 * Uses the second arrays' elements to find intervals.
	 * @return List of arrays containing new intervals.
	 */
	public static List<double[]> getContinuousIntervalsInSortedList(List<int[]> numbers) {
		List<double[]> intervals = new ArrayList<>();
		int startOfInterval = numbers.get(0)[1];
		int lastNum = numbers.get(0)[1];
		
		for (int i = 1; i < numbers.size(); i ++) {
			if (numbers.get(i)[1] - lastNum != 1) {
				intervals.add(new double[]{startOfInterval, lastNum});
				startOfInterval = numbers.get(i)[1];
			}
			lastNum = numbers.get(i)[1];
		}
		return intervals;
	}
	
	// Not tested
	public static Vector4d getEquationOfAPlane(Vector3d origin, Vector3d point) {
		// Find the equation of the plane ax + by + cz - d == 0;
		Vector3d normVector = new Vector3d(point.x - origin.x, point.y - origin.y, point.z - origin.z);
		double d = point.x * normVector.x + point.y * normVector.y + point.z * normVector.z;
		return new Vector4d(normVector.x, normVector.y, normVector.z, -d);
	}
	
	/**
	 * Finds an equation of a plane given by three points
	 * @param points Vector of three points
	 * @return Equation of a plane in format {ax, by, cz, -d}
	 * @throws Exception
	 */
	public static Vector4d getEquationOfAPlane(Vector3d [] points) throws Exception {
		if (points.length < 3)
			throw new Exception("Not enough points in the array. A plane needs at least 3 points to be constructed.");
		return getEquationOfAPlane(points[0], points[1], points[2]);
	}
	
	/**
	 * Finds an equation of a plane given by three points
	 * @param a First point
	 * @param b Second point
	 * @param c Third point
	 * @return Equation of a plane in format {ax, by, cz, -d}
	 */
	public static Vector4d getEquationOfAPlane(Vector3d a, Vector3d b, Vector3d c) {
		// Find the equation of the plane ax + by + cz - d == 0;
		Vector3d normVector = getPlaneNormVector(a, b, c);
		double d = a.x * normVector.x + a.y * normVector.y + a.z * normVector.z;
		return new Vector4d(normVector.x, normVector.y, normVector.z, -d);
	}
	
	/**
	 * Returns plane's norm vector given by the plane's three points
	 * @param points Plane's three points
	 * @return Plane's norm vector
	 * @throws Exception
	 */
	public static Vector3d getPlaneNormVector(Vector3d [] points) throws Exception {
		if (points.length < 3)
			throw new Exception("Not enough points in the array. A plane needs at least 3 points to be constructed.");
		return getPlaneNormVector(points[0], points[1], points[2]);
	}
	
	/**
	 * Returns plane's norm vector given by the plane's three points
	 * @param a First plane's point
	 * @param b Second plane's point
	 * @param c Third plane's point
	 * @return Plane's norm vector
	 */
	public static Vector3d getPlaneNormVector(Vector3d a, Vector3d b, Vector3d c) {
		Vector3d planeVector1 = new Vector3d(b.x - a.x, b.y - a.y, b.z - a.z);
		Vector3d planeVector2 = new Vector3d(c.x - a.x, c.y - a.y, c.z - a.z);
		Vector3d normVector = new Vector3d();
		normVector.cross(planeVector1, planeVector2);
		normVector.normalize();
		return normVector;
	}
	
	public static double getPointPlaneDistance(Vector3d planeNormVector, Vector3d pointInPlane, Vector3d point) {
		Vector3d newPoint = new Vector3d(pointInPlane.x - point.x, pointInPlane.y - point.y, pointInPlane.z - point.z);
		return planeNormVector.dot(newPoint);
	}
	
	public static double getPointPlaneDistance(Vector4d plane, Vector3d point) {
		return (plane.x * point.x + plane.y * point.y + plane.z * point.z + plane.w) 
				/ Math.sqrt(plane.x * plane.x + plane.y * plane.y + plane.z * plane.z);
	}
	
	/**
	 * Calculates the intersection point of a plane and a vector
	 * @param plane Plane ax + by + cz - d = 0
	 * @param vector Vector ax + by + cz = 0
	 * @param origin Origin of the vector
	 * @return Point of intersection
	 */
	public static Vector3d findPlaneVectorIntersection(Vector4d plane, Vector3d vector, Vector3d origin) {
		// Parametric form of an equation
		// P = origin + vector * t
		// Vectors can be preallocated but code readability is decreased.
		Vector2d x = new Vector2d(origin.x, vector.x);
		Vector2d y = new Vector2d(origin.y, vector.y);
		Vector2d z = new Vector2d(origin.z, vector.z);
		// Solve equation for t
		// plane.x * x.x + plane.x * x.y * t + plane.y * y.x * plane.y * y.y * t + plane.z * z.x + plane.z * z.y * t = d;
		double expressionWithoutT = plane.x * x.x + plane.y * y.x + plane.z * z.x;
		double expressionWithT = plane.x * x.y + plane.y * y.y + plane.z * z.y;
		// different sign from the original approach because d is on the other side of the equation
		double t = (expressionWithoutT + plane.w) / -expressionWithT;
		return new Vector3d(x.x + x.y * t, y.x + y.y * t, z.x + z.y * t);
	}
	
	/**
	 * Finds point along a vector at a distance given by a constant
	 * Assumes the starting point of the vector is at (0, 0, 0) and the vector is normalized
	 * http://math.stackexchange.com/questions/175896/finding-a-point-along-a-line-a-certain-distance-away-from-another-point
	 * @param vector Vector along which we are looking for a point
	 * @return Coordinates of a point
	 */
	public static Vector3d findPointAlongVectorAtDistance(Vector3d vector, double distance) {
		return new Vector3d(vector.x * distance, vector.y * distance, vector.z * distance);
	}
	
	/**
	 * Checks whether a point lies within a polygon. Takes into account only the x and z axes.
	 * @param point Point to check whether it lies with the given polygon.
	 * @param polygon Given polygon.
	 * @return True if the point lies within the polygon. False otherwise.
	 */
	public static boolean polygonContainsPoint(Vector2d point, Vector3d [] polygon) {
//		int i;
//		int j;
//		boolean result = false;
//		for (i = 0, j = polygon.length - 1; i < polygon.length; j = i++) {
//			if ((polygon[i].y > point.y) != (polygon[j].y > point.y) &&
//					(point.x < (polygon[j].x - polygon[i].x) * (point.y - polygon[i].y) / (polygon[j].y-polygon[i].y) + polygon[i].x)) {
//				result = !result;
//			}
//		}
//		return result;
		return polygonContainsPoint(point.x, point.y, polygon);
	}
	
	public static boolean polygonContainsPoint(Vector2d point, Vector2d [] polygon) {
		return polygonContainsPoint(point.x, point.y, polygon);
	}
	
	/**
	 * Checks whether a point lies within a polygon in 3d. Assumes the polygon is convex
	 * and all its points lie on the same plane as the point. That way we project the given
	 * polygon with the point to the axis with the longest component and reduce it to a 2d
	 * problem.
	 * @param point Point to check whether it lies with the given polygon.
	 * @param polygon Given polygon.
	 * @param plane Plane where the polygon and and point lie.
	 * @return True if the point lies within the polygon. False otherwise.
	 */
	public static boolean polygonContainsPoint(Vector3d point, Vector3d [] polygon, Vector4d plane) {
		int longestComponentIdx = getPlanesNormVectorLongestComponentIndex(plane);
		if (longestComponentIdx == 0) {
			// project to x
			return polygonContainsPoint(new Vector2d(point.y, point.z), projectPolygonToXAxis(polygon));
		} else if (longestComponentIdx == 1) {
			// project to y
			return polygonContainsPoint(new Vector2d(point.x, point.z), projectPolygonToYAxis(polygon));
		} else {
			//project to z
			return polygonContainsPoint(new Vector2d(point.x, point.y), projectPolygonToZAxis(polygon));
		}
	}
	
	/**
	 * Checks whether a point lies within a polygon. Takes into account only the x and y axes.
	 * Assumes the polygon is convex.
	 * @param x X coordinate of the point
	 * @param y Y coordinate of the point
	 * @param polygon Polygon given by a set of points.
	 * @return
	 */
	public static boolean polygonContainsPoint(double x, double y, Vector3d [] polygon) {
		int i;
		int j;
		boolean result = false;
		for (i = 0, j = polygon.length - 1; i < polygon.length; j = i++) {
			if ((polygon[i].y > y) != (polygon[j].y > y) &&
					(x < (polygon[j].x - polygon[i].x) * (y - polygon[i].y) / (polygon[j].y-polygon[i].y) + polygon[i].x)) {
				result = !result;
			}
		}
		return result;
	}
	
	/**
	 * Checks whether a point lies within a polygon. Assumes the polygon is convex.
	 * @param x X coordinate of the point
	 * @param y Y coordinate of the point
	 * @param polygon Polygon given by a set of points.
	 * @return
	 */
	public static boolean polygonContainsPoint(double x, double y, Vector2d [] polygon) {
		int i;
		int j;
		boolean result = false;
		for (i = 0, j = polygon.length - 1; i < polygon.length; j = i++) {
			if ((polygon[i].y > y) != (polygon[j].y > y) &&
					(x < (polygon[j].x - polygon[i].x) * (y - polygon[i].y) / (polygon[j].y-polygon[i].y) + polygon[i].x)) {
				result = !result;
			}
		}
		return result;
	}
	
	private static Vector2d[] projectPolygonToXAxis(Vector3d[] polygon) {
		Vector2d [] projected = new Vector2d[polygon.length];
		for (int i = 0; i < polygon.length; i ++) {
			projected[i] = new Vector2d(polygon[i].y, polygon[i].z);
		}
		return projected;
	}
	
	private static Vector2d[] projectPolygonToYAxis(Vector3d[] polygon) {
		Vector2d [] projected = new Vector2d[polygon.length];
		for (int i = 0; i < polygon.length; i ++) {
			projected[i] = new Vector2d(polygon[i].x, polygon[i].z);
		}
		return projected;
	}
	
	private static Vector2d[] projectPolygonToZAxis(Vector3d[] polygon) {
		Vector2d [] projected = new Vector2d[polygon.length];
		for (int i = 0; i < polygon.length; i ++) {
			projected[i] = new Vector2d(polygon[i].x, polygon[i].y);
		}
		return projected;
	}
	
	private static int getPlanesNormVectorLongestComponentIndex(Vector4d plane) {
		double longest = Math.abs(plane.x);
		int idx = 0;
		if (Math.abs(plane.y) > longest) {
			longest = Math.abs(plane.y);
			idx = 1;
		}
		if (Math.abs(plane.z) > longest) {
			longest = Math.abs(plane.z);
			idx = 2;
		}
		return idx;
	}
	
	/**
	 * Checks whether a point lies inside a triangle
	 * //http://stackoverflow.com/questions/13300904/determine-whether-point-lies-inside-triangle
	 * @param point Point to check
	 * @param v1 Triangle vertex1
	 * @param v2 Triangle vertex2
	 * @param v3 Triangle vertex3
	 * @return Returns true if the triangle contains the point. Otherwise returns false;
	 */
	public static boolean triangleContainsPoint(Vector3d point, Vector3d v1, Vector3d v2, Vector3d v3) {
		double alpha = ((v2.y - v3.y)*(point.x - v3.x) + (v3.x - v2.x)*(point.y - v3.y)) /
		        ((v2.y - v3.y)*(v1.x - v3.x) + (v3.x - v2.x)*(v1.y - v3.y));
		double beta = ((v3.y - v1.y)*(point.x - v3.x) + (v1.x - v3.x)*(point.y - v3.y)) /
		       ((v2.y - v3.y)*(v1.x - v3.x) + (v3.x - v2.x)*(v1.y - v3.y));
		double gamma = 1.0d - alpha - beta;
		
		if (alpha > 0 && beta > 0 && gamma >0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Tests on which side of a line a point lies
	 * @param origin first point forming the line
	 * @param target second point forming the line
	 * @param point point to test
	 * @return If value > 0, point is on the left side, if value == 0, point lies on the same line,
	 * if value < 0, point lies on the right side.
	 */
	public static double getPointSide(Vector2d origin, Vector3d target, Vector3d point) {
		return (target.x - origin.x) * (point.y - origin.y) - (point.x - origin.x) * (target.y - origin.y);
	}
	
	/**
	 * Returns the index of the point that is on the outer most right side of a polygon
	 * from the direction camera -> polygon
	 * @param points polygon
	 * @param origin camera position
	 * @return point index
	 * @throws Exception
	 */
	public static int findOutermostLeftPoint(Vector3d [] points, Vector2d origin) throws Exception {
		int j = 0;
		for (int i = 0; i < points.length; i ++) {
			for (j = 0; j < points.length; j ++) {
				if (i == j) continue; // do not test the same point
				double side = getPointSide(origin, points[i], points[j]);
				if (side > 0) {
					// another point is on the left, let's try again with a different point
					j = 0; // reset for the next loop
					break;
				}
			}
			if (j != 0) {
				// we didn't find a point that is to the left of the line
				return i;
			}
		}
		throw new Exception("The algorithm is incorrect");
	}
	
	/**
	 * Returns the index of the point that is on the outer most left side of a polygon
	 * from the direction camera -> polygon
	 * @param points polygon
	 * @param origin camera position
	 * @return point index
	 * @throws Exception
	 */
	public static int findOutermostRightPoint(Vector3d [] points, Vector2d origin) throws Exception {
		int j = 0;
		for (int i = 0; i < points.length; i ++) {
			for (j = 0; j < points.length; j ++) {
				if (i == j) continue; // do not test the same point
				double side = getPointSide(origin, points[i], points[j]);
				if (side < 0) {
					// another point is on the left, let's try again with a different point
					j = 0; // reset for the next loop
					break;
				}
			}
			if (j != 0) {
				// we didn't find a point that is to the left of the line
				return i;
			}
		}
		throw new Exception("The algorithm is incorrect");
	}
	
	/**
	 * Ignores the z-axis of the second parameter
	 * @param a first point
	 * @param b second point
	 * @return distance between points
	 */
	public static Double distance3dPointsIn2d(Vector2d a, Vector3d b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}
	
	public static Double distance2dPoints(Vector2d a, Vector2d b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}
	
	public static Double distance3dPoints(Vector3d a, Vector3d b) {
		return Math.sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y) + (b.z - a.z) * (b.z - a.z));
	}
	
	/**
	 * !TODO test
	 * @param points
	 * @return
	 * @throws Exception
	 */
	public static int[] threeBiggestValuesIndexes(double [] points) throws Exception {
		if (points.length != 4) {
			throw new Exception("Function defined only for parameters with 4 points.");
		}
		
		int [] result = new int[3];
		int count = 0;
		int smallestIndex = 0;
		for (int i = 1; i < points.length; i ++) {
			if (points[i] < points[smallestIndex]) {
				smallestIndex = i;
			}
		}
		for (int i = 0; i < points.length; i ++) {
			if (i != smallestIndex) {
				result[count++] = i;
			}
		}
		
		return result;
	}
	
	/**
	 * !TODO test
	 * @param origin
	 * @param polygon
	 * @return
	 * @throws Exception
	 */
	public static int [] threeFurthestPointsIndexes(Vector2d origin, Vector2d [] polygon) throws Exception {
		if (polygon.length != 4) {
			throw new Exception("Parameter is not a trapezoid");
		}
		
		double oa = distance2dPoints(origin, polygon[0]);
		double ob = distance2dPoints(origin, polygon[1]);
		double oc = distance2dPoints(origin, polygon[2]);
		double od = distance2dPoints(origin, polygon[3]);
		
		return threeBiggestValuesIndexes(new double[]{oa, ob, oc, od});
	}
	
	public static int [] threeFurthestPointsIndexes(Vector2d origin, Vector3d [] polygon) throws Exception {
		Vector2d [] polygon2d = new Vector2d[polygon.length];
		for (int i = 0; i < polygon.length; i ++) {
			polygon2d[i] = new Vector2d(polygon[i].x, polygon[i].y);
//			polygon2d[i].x = polygon[i].x;
//			polygon2d[i].y = polygon[i].y;
		}
		return threeFurthestPointsIndexes(origin, polygon2d);
	}
	
	public static List<Integer> twoNearestPointsIndexes(Vector2d origin, Vector3d [] polygon) {
		double oa = distance3dPointsIn2d(origin, polygon[0]);
		double ob = distance3dPointsIn2d(origin, polygon[1]);
		double oc = distance3dPointsIn2d(origin, polygon[2]);
		double od = distance3dPointsIn2d(origin, polygon[3]);
		
		double [] distanceArray = new double[]{oa, ob, oc, od};
		int min1 = 0;
		int min2 = 1;
		if (distanceArray[1] < distanceArray[0]) {
			min1 = 1;
			min2 = 0;
		}
		
		for (int i = 2; i < distanceArray.length; i ++) {
			if (distanceArray[i] < distanceArray[min1]) {
				min2 = min1;
				min1 = i;
			} else if (distanceArray[i] < distanceArray[min2]) {
				min2 = i;
			}
		}
		
		List<Integer> nearestIndexes = new ArrayList<Integer>();
		nearestIndexes.add(min1);
		nearestIndexes.add(min2);
		
		return nearestIndexes;
	}
	
	public static Vector2d createTranslatedPoint(double x, double y, double translationX, double translationY) {
		return new Vector2d(x + translationX, y + translationY);
	}
	
	public static Vector2d createTranslatedPoint(Vector2d original, double translationX, double translationY) {
		return new Vector2d(original.x + translationX, original.y + translationY);
	}
	
	/**
	 * Translates the passed parameter into new coordinates.
	 * @param point Point to be translated
	 * @param translationX Translation x
	 * @param translationY Translation y
	 */
	public static void  translatePoint(Vector2d point, double translationX, double translationY) {
		point.x += translationX;
		point.y += translationY;
	}
	
	
	public static void translate3dPointXYonly(Vector3d point, double translationX, double translationY) {
		point.x += translationX;
		point.y += translationY;
	}
	
	/**
	 * Moves point on the ground ignoring the z-coordinate.
	 * @param point Point to translate
	 * @param translation Translation of the point
	 */
	public static void translate3dPointXYonly(Vector3d point, Vector3d translation) {
		translate3dPointXYonly(point, translation.x, translation.y);
	}
	
}






















