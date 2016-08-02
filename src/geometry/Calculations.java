package geometry;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

public class Calculations {
	
	public static Vector4d getEquationOfAPlane(Vector3d origin, Vector3d point) {
		// Find the equation of the plane ax + by + cz - d == 0;
		Vector3d normVector = new Vector3d(point.x - origin.x, point.y - origin.y, point.z - origin.z);
		double d = point.x * normVector.x + point.y * normVector.y + point.z * normVector.z;
		return new Vector4d(normVector.x, normVector.y, normVector.z, -d);
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
	
}






















