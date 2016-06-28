package geometry;

import javax.vecmath.Vector2d;

public class CameraCalculator {
	
	
	public static Trapezoid getTrapezoid(double altitude, double FOVWidth, double FOVHeight, double heading, double roll, double pitch) {
		double bottom = droneToBottomDist(altitude, FOVWidth, pitch);
		double top = droneToTopDist(altitude, FOVWidth, pitch);
		double left = droneToLeftDist(altitude, FOVHeight, roll);
		double right = droneToRightDist(altitude, FOVHeight, roll);
		
		return null;
	}
	
	public static double droneToBottomDist(double altitude, double FOVWidth, double pitch) {
		double angle = limitAngle(pitch - FOVWidth/2);
		double debugDegrees = Math.toDegrees(pitch - FOVWidth/2);
		double debugDegrees2 = Math.toDegrees(angle);
		return altitude * Math.tan(angle);
	}
	
	public static double droneToTopDist(double altitude, double FOVWidth, double pitch) {
		double angle = limitAngle(pitch + FOVWidth/2);
		double debugDegrees = Math.toDegrees(pitch + FOVWidth/2);
		double debugDegrees2 = Math.toDegrees(angle);
		return altitude * Math.tan(angle);
	}
	
	public static double droneToLeftDist(double altitude, double FOVHeight, double roll) {
		double angle = limitAngle(roll - FOVHeight/2);
		double debugDegrees = Math.toDegrees(roll - FOVHeight/2);
		double debugDegrees2 = Math.toDegrees(angle);
		return altitude * Math.tan(angle);
	}
	
	public static double droneToRightDist(double altitude, double FOVHeight, double roll) {
		double angle = limitAngle(roll + FOVHeight/2);
		double debugDegrees = Math.toDegrees(roll + FOVHeight/2);
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















