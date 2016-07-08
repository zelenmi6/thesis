package geometry;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import Jama.Matrix;

public class CameraCalculator {
	
	/**
	 * Get corners of the polygon captured by the camera
	 * !TODO verify results. it seems that the diagonal angles between vectors do not match
	 * the camera's specifications. 
	 * @param FOVh Horizontal field of view in radians
	 * @param FOVv Vertical field of view in radians
	 * @param altitude Altitude of the camera
	 * @param heading Heading of the camera (z axis) in radians
	 * @param roll Roll of the camera (x axis) in radians
	 * @param pitch Pitch of the camera (y axis) in radians
	 * @return Array with 4 points defining a polygon
	 */
	public static Vector3d [] getBoundingPolygon(double FOVh, double FOVv, double altitude,
			double heading, double roll, double pitch) {
		Vector3d ray1 = CameraCalculator.ray1(FOVh, FOVv);
		Vector3d ray2 = CameraCalculator.ray2(FOVh, FOVv);
		Vector3d ray3 = CameraCalculator.ray3(FOVh, FOVv);
		Vector3d ray4 = CameraCalculator.ray4(FOVh, FOVv);
		ray1.normalize();
		ray2.normalize();
		ray3.normalize();
		ray4.normalize();
		
		Vector3d [] rotatedVectors = CameraCalculator.rotateRays(
				ray1, ray2, ray3, ray4, roll, pitch, heading);
		Vector3d origin = new Vector3d(0, 0, altitude);
		Vector3d intersection1 = CameraCalculator.findVectorGroundIntersection(rotatedVectors[0], origin);
		Vector3d intersection2 = CameraCalculator.findVectorGroundIntersection(rotatedVectors[1], origin);
		Vector3d intersection3 = CameraCalculator.findVectorGroundIntersection(rotatedVectors[2], origin);
		Vector3d intersection4 = CameraCalculator.findVectorGroundIntersection(rotatedVectors[3], origin);
		
		return new Vector3d[]{intersection1, intersection2, intersection3, intersection4};
	}
	
//	public static Vector3d ray1(double FOVh, double FOVv) {
//		return new Vector3d(1, Math.tan(FOVh/2), Math.tan(FOVv/2));
//	}
//	
//	public static Vector3d ray2(double FOVh, double FOVv) {
//		return new Vector3d(1, -Math.tan(FOVh/2), Math.tan(FOVv/2));
//	}
//	
//	public static Vector3d ray3(double FOVh, double FOVv) {
//		return new Vector3d(1, Math.tan(FOVh/2), -Math.tan(FOVv/2));
//	}
//	
//	public static Vector3d ray4(double FOVh, double FOVv) {
//		return new Vector3d(-Math.tan(FOVh/2), -Math.tan(FOVv/2), -1);
//	}
	
	public static Vector3d ray1(double FOVh, double FOVv) {
		return new Vector3d(Math.tan(FOVv/2), Math.tan(FOVh/2), -1);
	}
	
	public static Vector3d ray2(double FOVh, double FOVv) {
		return new Vector3d(-Math.tan(FOVv/2), Math.tan(FOVh/2), -1);
	}
	
	public static Vector3d ray3(double FOVh, double FOVv) {
		return new Vector3d(Math.tan(FOVv/2), -Math.tan(FOVh/2), -1);
	}
	
	public static Vector3d ray4(double FOVh, double FOVv) {
		return new Vector3d(-Math.tan(FOVv/2), -Math.tan(FOVh/2), -1);
	}
	
	public static void printDirections(Vector3d ray1,Vector3d ray2, Vector3d ray3, Vector3d ray4) {
		System.out.println("(" + ray1.x + ", " + ray1.y + ", " + ray1.z + ")");
		System.out.println("(" + ray2.x + ", " + ray2.y + ", " + ray2.z + ")");
		System.out.println("(" + ray3.x + ", " + ray3.y + ", " + ray3.z + ")");
		System.out.println("(" + ray4.x + ", " + ray4.y + ", " + ray4.z + ")");
	}
	
	public static void printDirections2(Vector3d ray1,Vector3d ray2, Vector3d ray3, Vector3d ray4) {
		System.out.println("(" + ray1.x + ", " + ray1.z + ", " + ray1.y + ")");
		System.out.println("(" + ray2.x + ", " + ray2.z + ", " + ray2.y + ")");
		System.out.println("(" + ray3.x + ", " + ray3.z + ", " + ray3.y + ")");
		System.out.println("(" + ray4.x + ", " + ray4.z + ", " + ray4.y + ")");
	}
	
	public static Vector3d[] rotateRays(Vector3d ray1,Vector3d ray2, Vector3d ray3, Vector3d ray4, double roll, double pitch, double yaw) {
		double sinAlpha = Math.sin(roll);
		double sinBeta = Math.sin(pitch);
		double sinGamma = Math.sin(yaw);
		double cosAlpha = Math.cos(roll);
		double cosBeta = Math.cos(pitch);
		double cosGamma = Math.cos(yaw);
		double m00 = cosAlpha * cosBeta;
		double m01 = cosAlpha * sinBeta * sinGamma - sinAlpha * cosGamma;
		double m02 = cosAlpha * sinBeta * cosGamma + sinAlpha * sinGamma;
		double m10 = sinAlpha * cosBeta;
		double m11 = sinAlpha * sinBeta * sinGamma + cosAlpha * cosGamma;
		double m12 = sinAlpha * sinBeta * cosGamma - cosAlpha * sinGamma;
		double m20 = -sinBeta;
		double m21 = cosBeta * sinGamma;
		double m22 = cosBeta * cosGamma;
		
		// Using an external library to rotate vectors
		Matrix rotationMatrix = new Matrix(new double[][]{{m00, m01, m02}, {m10, m11, m12}, {m20, m21, m22}});
		Matrix ray1Matrix = new Matrix(new double[][]{{ray1.x}, {ray1.y}, {ray1.z}});
		Matrix ray2Matrix = new Matrix(new double[][]{{ray2.x}, {ray2.y}, {ray2.z}});
		Matrix ray3Matrix = new Matrix(new double[][]{{ray3.x}, {ray3.y}, {ray3.z}});
		Matrix ray4Matrix = new Matrix(new double[][]{{ray4.x}, {ray4.y}, {ray4.z}});
		
		Matrix res1 = rotationMatrix.times(ray1Matrix);
		Matrix res2 = rotationMatrix.times(ray2Matrix);
		Matrix res3 = rotationMatrix.times(ray3Matrix);
		Matrix res4 = rotationMatrix.times(ray4Matrix);
		
		System.out.println();
		// x, y, z
//		System.out.println("(" + res1.get(0, 0) + ", " + res1.get(1, 0) + ", " + res1.get(2, 0) + ")");
//		System.out.println("(" + res2.get(0, 0) + ", " + res2.get(1, 0) + ", " + res2.get(2, 0) + ")");
//		System.out.println("(" + res3.get(0, 0) + ", " + res3.get(1, 0) + ", " + res3.get(2, 0) + ")");
//		System.out.println("(" + res4.get(0, 0) + ", " + res4.get(1, 0) + ", " + res4.get(2, 0) + ")");
//		System.out.println();
		// x, z, y
//		System.out.println("(" + res1.get(0, 0) + ", " + res1.get(2, 0) + ", " + res1.get(1, 0) + ")");
//		System.out.println("(" + res2.get(0, 0) + ", " + res2.get(2, 0) + ", " + res2.get(1, 0) + ")");
//		System.out.println("(" + res3.get(0, 0) + ", " + res3.get(2, 0) + ", " + res3.get(1, 0) + ")");
//		System.out.println("(" + res4.get(0, 0) + ", " + res4.get(2, 0) + ", " + res4.get(1, 0) + ")");
		
		return new Vector3d[]{new Vector3d(res1.get(0, 0), res1.get(1, 0), res1.get(2, 0)),
				new Vector3d(res2.get(0, 0), res2.get(1, 0), res2.get(2, 0)),
				new Vector3d(res3.get(0, 0), res3.get(1, 0), res3.get(2, 0)),
				new Vector3d(res4.get(0, 0), res4.get(1, 0), res4.get(2, 0))};
	}
	
	public static Vector3d findVectorGroundIntersection(Vector3d vector, Vector3d origin) {
		
		// Parametric form of an equation
		// P = origin + vector * t
		Vector2d x = new Vector2d(origin.x,vector.x);
		Vector2d y = new Vector2d(origin.y,vector.y);
		Vector2d z = new Vector2d(origin.z,vector.z);
		
		// Equation of the horizontal plane (ground)
		// -z = 0
		
		// Calculate t by substituting z
		double t = - (z.x / z.y);
		
		// Substitute t in the original parametric equations to get points of intersection
		return new Vector3d(x.x + x.y * t, y.x + y.y * t, z.x + z.y * t);
	}
	
	public static Trapezoid getTrapezoid(double altitude, double FOVh, double FOVv, double heading, double roll, double pitch) {
		double bottom = droneToBottomDist(altitude, FOVh, pitch);
		double top = droneToTopDist(altitude, FOVh, pitch);
		double left = droneToLeftDist(altitude, FOVv, roll);
		double right = droneToRightDist(altitude, FOVv, roll);
		
		return null;
	}
	
	public static double droneToBottomDist(double altitude, double FOVh, double pitch) {
		double angle = limitAngle(pitch - FOVh/2);
		double debugDegrees = Math.toDegrees(pitch - FOVh/2);
		double debugDegrees2 = Math.toDegrees(angle);
		return altitude * Math.tan(angle);
	}
	
	public static double droneToTopDist(double altitude, double FOVh, double pitch) {
		double angle = limitAngle(pitch + FOVh/2);
		double debugDegrees = Math.toDegrees(pitch + FOVh/2);
		double debugDegrees2 = Math.toDegrees(angle);
		return altitude * Math.tan(angle);
	}
	
	public static double droneToLeftDist(double altitude, double FOVv, double roll) {
		double angle = limitAngle(roll - FOVv/2);
		double debugDegrees = Math.toDegrees(roll - FOVv/2);
		double debugDegrees2 = Math.toDegrees(angle);
		return altitude * Math.tan(angle);
	}
	
	public static double droneToRightDist(double altitude, double FOVv, double roll) {
		double angle = limitAngle(roll + FOVv/2);
		double debugDegrees = Math.toDegrees(roll + FOVv/2);
		double debugDegrees2 = Math.toDegrees(angle);
		return altitude * Math.tan(angle);
	}
	
	// avoid problems with tangent
	private static double limitAngle(double angle) {
		if (angle >= 90 * Math.PI / 180) {
			return 89.9 * Math.PI / 180;
		} else if (angle <= -90 * Math.PI / 180) {
			return -89.9 * Math.PI / 180;
		} else {
			return angle;
		}
	}
		
	public static Vector2d translatePoint(double x, double y, double translationX, double translationY) {
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
	
	
	public static void translate3dPointOnGround(Vector3d point, double translationX, double translationY) {
		point.x += translationX;
		point.y += translationY;
	}
	
	/**
	 * Moves point on the ground ignoring the z-coordinate.
	 * @param point Point to translate
	 * @param translation Translation of the point
	 */
	public static void translate3dPointOnGround(Vector3d point, Vector3d translation) {
		translate3dPointOnGround(point, translation.x, translation.y);
	}
	
//	public static Vector2d translatePoint(Vector2d point, Vector2d ) {
//		return new Vector2d(point.x + translationX, point.y + translationY);
//	}
	
	public static Vector2d rotatePoint(double x, double y, double angle, double pivotX, double pivotY) {
		double s = Math.sin(angle);
		double c = Math.cos(angle);

		// translate point back to origin:
		x -= pivotX;
		y -= pivotY;

		// rotate point
		double xnew = x * c + y * s;
		double ynew = -x * s + y * c;

		// translate point back:
		x = xnew + pivotX;
		y = ynew + pivotY;
		return new Vector2d(x, y);
	}
	
	public static Vector2d rotatePoint(Vector2d point, double angle, double pivotX, double pivotY) {
		return rotatePoint(point.x, point.y, angle, pivotX, pivotY);
	}
	
	public static Vector2d rotatePoint(Vector2d point, double angle, Vector2d pivot) {
		return rotatePoint(point.x, point.y, angle, pivot.x, pivot.y);
	}
}















