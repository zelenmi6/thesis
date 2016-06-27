package geometry;

public class CameraCalculator {
	
	public static Trapezoid getTrapezoid(double altitude, double FOVhor, double FOVvert, double roll, double pitch) {
		return null;
	}
	
	public static double droneToBottomDist(double altitude, double FOVhor, double yAngle) {
		return altitude * Math.tan(Math.toRadians(yAngle - FOVhor/2));
	}
	
	public static double droneToTopDist(double altitude, double FOVhor, double yAngle) {
		return altitude * Math.tan(Math.toRadians(yAngle + FOVhor/2));
	}
}
