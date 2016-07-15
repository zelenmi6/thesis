package geometry;

import javax.vecmath.Vector2d;

public class Calculations {
	
	public static Double distance2dPoints(Vector2d a, Vector2d b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
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
	 * @param trapezoid
	 * @return
	 * @throws Exception
	 */
	public static int [] threeFurthestPointsIndexes(Vector2d origin, Vector2d [] trapezoid) throws Exception {
		if (trapezoid.length != 4) {
			throw new Exception("Parameter is not a trapezoid");
		}
		
		double oa = distance2dPoints(origin, trapezoid[0]);
		double ob = distance2dPoints(origin, trapezoid[1]);
		double oc = distance2dPoints(origin, trapezoid[2]);
		double od = distance2dPoints(origin, trapezoid[3]);
		
		return threeBiggestValuesIndexes(new double[]{oa, ob, oc, od});
	}
	
}
