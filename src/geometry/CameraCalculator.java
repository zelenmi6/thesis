package geometry;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import Jama.Matrix;
import constants.CameraTesting;

public class CameraCalculator {
	
	public final static Vector3d AXES_ORIGIN = new Vector3d(0, 0, 0);
	
	/**
	 * Get corners of the polygon captured by the camera. 
	 * The points are not yet translated to camera's X-Y coordinates.
	 * @param FOVh Horizontal field of view in radians
	 * @param FOVv Vertical field of view in radians
	 * @param altitude Altitude of the camera in meters
	 * @param heading Heading of the camera (z axis) in radians
	 * @param roll Roll of the camera (x axis) in radians
	 * @param pitch Pitch of the camera (y axis) in radians
	 * @return Array with 4 points defining a polygon
	 */
	public static Vector3d [] getBoundingPolygon(double FOVh, double FOVv, double altitude,
			double roll, double pitch, double heading) {
		Vector3d ray1 = CameraCalculator.ray1(FOVh, FOVv);
		Vector3d ray2 = CameraCalculator.ray2(FOVh, FOVv);
		Vector3d ray3 = CameraCalculator.ray3(FOVh, FOVv);
		Vector3d ray4 = CameraCalculator.ray4(FOVh, FOVv);
		
		Vector3d [] rotatedVectors = CameraCalculator.rotateRays(
				ray1, ray2, ray3, ray4, roll, pitch, heading);
		
		Vector3d origin = new Vector3d(0, 0, altitude);
		Vector3d[] intersections = getRayGroundIntersections(rotatedVectors, origin);
		limitRange(intersections, rotatedVectors, altitude, origin);
		
		
//		pointIsInsidePyramid(rotatedVectors, origin, new Vector3d(1, 0, 0));
//		if (pointIsInsidePyramid(rotatedVectors, origin, new Vector3d(1, 0, 0)))
//			System.out.println("Inside");
//		else
//			System.out.println("Outside");
		
		return intersections;
	}
	
	public static boolean pointIsInsidePyramid(Vector3d [] rays, Vector3d cameraPosition, Vector3d point) {
		Vector3d [] normVectors = new Vector3d[rays.length];
		Vector4d [] planes = new Vector4d[rays.length];
		for (int i = 0; i < rays.length; i ++) {
			normVectors[i] = Calculations.getPlaneNormVector(translatePointFromAxesOriginToCamera(rays[i], cameraPosition),
					translatePointFromAxesOriginToCamera(rays[(i+1)%rays.length], cameraPosition), cameraPosition);
//			planes[i] = Calculations.getEquationOfAPlane(translatePointFromAxesOriginToCamera(rays[i], cameraPosition),
//					translatePointFromAxesOriginToCamera(rays[(i+1)%rays.length], cameraPosition), cameraPosition);
//			printPlaneForWolfram(planes[i], "f" + i);
		}
//		Vector3d cameraCenter = new Vector3d(planes[0].x + planes[1].x + planes[2].x + planes[3].x,
//				planes[0].y + planes[1].y + planes[2].y + planes[3].y,
//				planes[0].z + planes[1].z + planes[2].z + planes[3].z);
//		System.out.println("Camera center: \n" + cameraCenter.toString());
		
		for (int i = 0; i < normVectors.length; i ++) {
			if (Calculations.getPointPlaneDistance(normVectors[i], cameraPosition, point) > 0) {
				return false;
			}
		}
		return true;
	}
	
	private static void printPlaneForWolfram(Vector4d plane, String functionName) {
		StringBuilder sb = new StringBuilder();
		sb.append(functionName).append("[x_, y_] := 1/").append(plane.z).append("(").append(plane.x).append("x + ")
		.append(plane.y).append("y - ").append(plane.w).append(");");
		System.out.println(sb.toString());
	}
	
	private static Vector3d [] getRayGroundIntersections(Vector3d [] rays, Vector3d origin) {
		Vector3d [] intersections = new Vector3d[rays.length];
		for (int i = 0; i < rays.length; i ++) {
			intersections[i] = CameraCalculator.findRayGroundIntersection(rays[i], origin);
		}
		return intersections;
	}
	
	private static boolean quadrantChanged(Vector3d vector, Vector3d intersection) {
		if (vector.x < 0 && intersection.x > 0 || vector.x > 0 && intersection.x < 0)
			return true;
		if (vector.y < 0 && intersection.y > 0 || vector.y > 0 && intersection.y < 0)
			return true;
		return false;
	}
	
	private static void limitRange(Vector3d[] intersections, Vector3d [] rays, double altitude, Vector3d origin) {
		for (int i = 0; i < intersections.length; i ++) {
			if (quadrantChanged(rays[i], intersections[i])){
				// if the ray is aiming over the horizon, the quadrant of intersections changes
				// that way we know we have to limit our range of view
				limitDistanceOfView(intersections[i], rays[i]);
			} else if (Calculations.distance3dPoints(origin, intersections[i]) > CameraTesting.MAX_DISTANCE) {
				// if the range of view is too big we need to limit it
				// !TODO Muzu tohle udelat? Beru souradnice pouze vektoru v urcite jeho vzdalenosti.
				limitDistanceOfView(intersections[i], rays[i]);
			}
		}
	}
	
	private static void limitDistanceOfView(Vector3d intersection, Vector3d rotatedVector) {
		intersection.x = rotatedVector.x * CameraTesting.MAX_DISTANCE;
		intersection.y = rotatedVector.y * CameraTesting.MAX_DISTANCE;
	}
	
	public static Vector3d ray1(double FOVh, double FOVv) {
		Vector3d ray = new Vector3d(Math.tan(FOVv/2), Math.tan(FOVh/2), -1);
		ray.normalize();
		return ray;
	}
	
	public static Vector3d ray2(double FOVh, double FOVv) {
		Vector3d ray = new Vector3d(Math.tan(FOVv/2), -Math.tan(FOVh/2), -1);
		ray.normalize();
		return ray;
	}
	
	public static Vector3d ray3(double FOVh, double FOVv) {
		Vector3d ray = new Vector3d(-Math.tan(FOVv/2), -Math.tan(FOVh/2), -1);
		ray.normalize();
		return ray;
	}
	
	public static Vector3d ray4(double FOVh, double FOVv) {
		Vector3d ray = new Vector3d(-Math.tan(FOVv/2), Math.tan(FOVh/2), -1);
		ray.normalize();
		return ray;
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
		double sinAlpha = Math.sin(yaw);
		double sinBeta = Math.sin(pitch);
		double sinGamma = Math.sin(roll);
		double cosAlpha = Math.cos(yaw);
		double cosBeta = Math.cos(pitch);
		double cosGamma = Math.cos(roll);
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
//		printRotationDegrees(rotationMatrix);
		Matrix ray1Matrix = new Matrix(new double[][]{{ray1.x}, {ray1.y}, {ray1.z}});
		Matrix ray2Matrix = new Matrix(new double[][]{{ray2.x}, {ray2.y}, {ray2.z}});
		Matrix ray3Matrix = new Matrix(new double[][]{{ray3.x}, {ray3.y}, {ray3.z}});
		Matrix ray4Matrix = new Matrix(new double[][]{{ray4.x}, {ray4.y}, {ray4.z}});
		
		Matrix res1 = rotationMatrix.times(ray1Matrix);
		Matrix res2 = rotationMatrix.times(ray2Matrix);
		Matrix res3 = rotationMatrix.times(ray3Matrix);
		Matrix res4 = rotationMatrix.times(ray4Matrix);
		
		Vector3d rotatedRay1 = new Vector3d(res1.get(0, 0), res1.get(1, 0), res1.get(2, 0));
		Vector3d rotatedRay2 = new Vector3d(res2.get(0, 0), res2.get(1, 0), res2.get(2, 0));
		Vector3d rotatedRay3 = new Vector3d(res3.get(0, 0), res3.get(1, 0), res3.get(2, 0));
		Vector3d rotatedRay4 = new Vector3d(res4.get(0, 0), res4.get(1, 0), res4.get(2, 0));
		Vector3d[] rayArray = new Vector3d[]{rotatedRay1, rotatedRay2, rotatedRay3, rotatedRay4};
		
		return rayArray;
	}
	
	private static void printRotationDegrees(Matrix rotationMatrix) {
		double x, y, z;
		x = Math.atan2(rotationMatrix.get(2, 1), rotationMatrix.get(2, 2)); //roll
		y = Math.atan2(-rotationMatrix.get(2, 0), Math.sqrt(rotationMatrix.get(2, 1) * rotationMatrix.get(2, 1) + 
				rotationMatrix.get(2, 2) * rotationMatrix.get(2, 2) )); //pitch
		z = Math.atan2(rotationMatrix.get(1, 0), rotationMatrix.get(0, 0)); //yaw
		System.out.println("Roll: " + Math.toDegrees(x));
		System.out.println("Pitch: " + Math.toDegrees(y));
		System.out.println("Yaw: " + Math.toDegrees(z));
	}
	
	public static Vector3d findRayGroundIntersection(Vector3d ray, Vector3d origin) {
		
		// Parametric form of an equation
		// P = origin + vector * t
		Vector2d x = new Vector2d(origin.x,ray.x);
		Vector2d y = new Vector2d(origin.y,ray.y);
		Vector2d z = new Vector2d(origin.z,ray.z);
		
		// Equation of the horizontal plane (ground)
		// -z = 0
		
		// Calculate t by substituting z
		double t = - (z.x / z.y);
		
		// Substitute t in the original parametric equations to get points of intersection
		return new Vector3d(x.x + x.y * t, y.x + y.y * t, z.x + z.y * t);
	}
	
	/**
	 * Calculates points of intersection of camera rays and a plane perpendicular to vector (origin, pointOfInterest)
	 * passing through pointOfInterest.
	 * @param rays Camera rays
	 * @param origin Camera position
	 * @param pointOfInterest Point that we are querying whether it lies in the camera's field of view
	 * @return Array containing points of intersection of camera rays and a plane perpendicular to vector (origin, pointOfInterest)
	 * passing through pointOfInterest.
	 */
	public static Vector3d[] findRaysVerticalPlaneIntersection(Vector3d[] rays, Vector3d origin, Vector3d pointOfInterest) {
		Vector3d [] intersections = new Vector3d[rays.length];
		// Find the equation of the plane ax + by + cz = d;
		// a, b and c are equal to the components of the norm vector
		// d is computed as a multiplication of each of the norm vector components and the point the plane passes through
		//   e.g norm vector v = (1, 7, -2), point P = (3, 1, 6)
		//   (1, 7, -2) * (x, y, z) = (1, 7, -2) * (3, 1, 6) => x + 7y -2z = -2
		
		// It is also necessary to translate the origin to (0, 0, 0) and all other points accordingly in order to be able
		// to detect that a camera ray intersects the given plane "from behind" or in other words - the opposite direction. 
		// That is done by checking if the point of intersection lies within the same octant as the corresponding vector.
		// Afterwards it is necessary to translate the points of intersection back accordingly
		Vector3d pointOfInterestTranslated = translatePointToAxesOrigin(pointOfInterest, origin);
		
		// ax + by + cz - d = 0;
		Vector4d plane = new Vector4d(pointOfInterestTranslated.x, pointOfInterestTranslated.y, pointOfInterestTranslated.z, 0);
		plane.w = (pointOfInterestTranslated.x * plane.x + 
				pointOfInterestTranslated.y * plane.y + pointOfInterestTranslated.z * plane.z) * (-1); // * (-1) to make d -> -d
		
		// Get intersections of rays and the plane with the point of interest
		// !TODO moc neotestovano
		for (int i = 0; i < rays.length; i ++) {
			intersections[i] = Calculations.findPlaneVectorIntersection(plane, rays[i], AXES_ORIGIN);
		}
		
		for (int i = 0; i < intersections.length; i ++) {
			if (octantChanged(rays[i], intersections[i])) {
				System.out.print("Octant changed, ");
				Vector3d middle = getRayInTheMiddle(rays);
				Vector3d pointAlongMiddle = Calculations.findPointAlongVectorAtDistance(middle, CameraTesting.MAX_DISTANCE);
				//!TODO zkontrolovat, zdali je ok davat jako origin vzdy (0,0,0)
				Vector4d viewLimitingPlane = Calculations.getEquationOfAPlane(AXES_ORIGIN, pointAlongMiddle);
				Vector3d pointOnRay = Calculations.findPlaneVectorIntersection(viewLimitingPlane, rays[i], AXES_ORIGIN);
				//!TODO projekce na plosinu
				System.out.print("");
			}
			System.out.println("x: " + intersections[i].x + ", y: " + intersections[i].y + ", z:" + intersections[i].z);
		}
		System.out.println("-------------------------------------");
		
		for (int i = 0; i < intersections.length; i ++) {
			intersections[i] = translatePointFromAxesOriginToCamera(intersections[i], origin);
		}
		
		return intersections;
	}
	
	private static Vector3d getRayInTheMiddle(Vector3d [] rays) {
		if (rays.length == 0) {
			return null;
		}
		
		Vector3d middle = new Vector3d();
		for (int i = 0; i < rays.length; i ++) {
			middle.x += rays[i].x;
			middle.y += rays[i].y;
			middle.z += rays[i].z;
		}
		return middle;
	}
	
	
	private static boolean octantChanged(Vector3d ray, Vector3d intersection) {
		if (ray.x > 0 && intersection.x < 0 || ray.x < 0 && intersection.x > 0)
			return true;
		if (ray.y > 0 && intersection.y < 0 || ray.y < 0 && intersection.y > 0)
			return true;
		if (ray.z > 0 && intersection.z < 0 || ray.z < 0 && intersection.z > 0)
			return true;
		
		
		return false;
	}
	
	/**
	 * Translates a point according to camera being translated to (0, 0, 0)
	 * @param pointToTranslate Point which we want to translate
	 * @param cameraPosition Real camera position
	 * @return Point translated according to camera being translated to (0, 0, 0)
	 */
	private static Vector3d translatePointToAxesOrigin(Vector3d pointToTranslate, Vector3d cameraPosition) {
		return new Vector3d(pointToTranslate.x - cameraPosition.x, 
				pointToTranslate.y - cameraPosition.y, 
				pointToTranslate.z - cameraPosition.z);
	}
	
	/**
	 * Translates a point according to camera being translated from (0, 0, 0) to its original position
	 * @param pointToTranslate Point which we want to translate
	 * @param cameraPosition Real camera position
	 * @return Point translated according to camera being translated from (0, 0, 0) to its original position
	 */
	private static Vector3d translatePointFromAxesOriginToCamera(Vector3d pointToTranslate, Vector3d cameraPosition) {
		return new Vector3d(pointToTranslate.x + cameraPosition.x, 
				pointToTranslate.y + cameraPosition.y, 
				pointToTranslate.z + cameraPosition.z);
	}
	
	public static void findVectorVerticalPlaneIntersection(Vector3d vector, Vector3d origin) {
		// Parametric form of an equation
		// P = origin + vector * t
		Vector2d x = new Vector2d(origin.x,vector.x);
		Vector2d y = new Vector2d(origin.y,vector.y);
		Vector2d z = new Vector2d(origin.z,vector.z);
		
		// Equation of the vertical plane perpendicular to the ground at distance given by a constant MAX_DISTANCE
		// x - MAX_DISTANCE = 0
		
		// Calculate t by substituting x
		double t = (CameraTesting.MAX_DISTANCE - x.x) / x.y;
		System.out.println("x: " +( x.x + x.y * t )+ ", y:" +( y.x + y.y * t )+ ", z:" +( z.x + z.y * t));
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















