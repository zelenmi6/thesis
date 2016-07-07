import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import database.PictureTelemetryDao;
import geometry.CameraCalculator;
import loaders.DirectoryPicturesLoader;
import loaders.PictureTelemetry;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("Running program");
		
//		DirectoryPicturesLoader parser = new DirectoryPicturesLoader("Dvur, ", "C:/Users/Milan/Google Drive/Škola/Magistr/Diplomka2/bkpImages/dvurSmall");
//		DirectoryPicturesLoader parser = new DirectoryPicturesLoader("Invalidovna, ", "C:/Users/Milan/Google Drive/Škola/Magistr/Diplomka2/bkpImages/invalidovna");
//		PictureTelemetryDao dao = PictureTelemetryDao.getInstance();
		
		final double altitude = 20;
		final double angleX = Math.toRadians(0);
		final double angleY = Math.toRadians(0);
		final double FOVWidth = Math.toRadians(64.4);
//		final double FOVWidth = Math.toRadians(80);
		final double FOVHeight = Math.toRadians(37.2);
		
		Vector3d ray1 = CameraCalculator.ray1(FOVWidth, FOVHeight);
		Vector3d ray2 = CameraCalculator.ray2(FOVWidth, FOVHeight);
		Vector3d ray3 = CameraCalculator.ray3(FOVWidth, FOVHeight);
		Vector3d ray4 = CameraCalculator.ray4(FOVWidth, FOVHeight);
		CameraCalculator.printDirections(ray1, ray2, ray3, ray4);
		
		Vector3d [] rotatedVectors = CameraCalculator.rotateRays(
				ray1, ray2, ray3, ray4, Math.toRadians(0), Math.toRadians(0), Math.toRadians(0));
		ray1 = rotatedVectors[0];
		ray2 = rotatedVectors[1];
		ray3 = rotatedVectors[2];
		ray4 = rotatedVectors[3];
		
		Vector3d intersection1 = CameraCalculator.findVectorGroundIntersection(ray1, new Vector3d(0, 0, 20));
		Vector3d intersection2 = CameraCalculator.findVectorGroundIntersection(ray2, new Vector3d(0, 0, 20));
		Vector3d intersection3 = CameraCalculator.findVectorGroundIntersection(ray3, new Vector3d(0, 0, 20));
		Vector3d intersection4 = CameraCalculator.findVectorGroundIntersection(ray4, new Vector3d(0, 0, 20));
		System.out.println("Intersecting: " + intersection1.x + ", " + intersection1.y + ", " + intersection1.z);
		System.out.println("Intersecting: " + intersection2.x + ", " + intersection2.y + ", " + intersection2.z);
		System.out.println("Intersecting: " + intersection3.x + ", " + intersection3.y + ", " + intersection3.z);
		System.out.println("Intersecting: " + intersection4.x + ", " + intersection4.y + ", " + intersection4.z);
		
//		System.out.println("Bottom: " + CameraCalculator.droneToBottomDist(altitude, FOVWidth, angleX));
//		System.out.println("Top: " + CameraCalculator.droneToTopDist(altitude, FOVWidth, angleX));
//		
//		System.out.println("Left: " + CameraCalculator.droneToLeftDist(altitude, FOVHeight, angleY));
//		System.out.println("Right: " + CameraCalculator.droneToRightDist(altitude, FOVHeight, angleY));
		
		
		System.out.println("Program has finished");
	}

}
























