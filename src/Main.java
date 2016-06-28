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
		
		final double altitude = 30;
		final double angleX = Math.toRadians(40);
		final double angleY = Math.toRadians(50);
		final double FOVWidth = Math.toRadians(64.4);
		final double FOVHeight = Math.toRadians(37.2);
		
		System.out.println("Bottom: " + CameraCalculator.droneToBottomDist(altitude, FOVWidth, angleX));
		System.out.println("Top: " + CameraCalculator.droneToTopDist(altitude, FOVWidth, angleX));
		
		System.out.println("Left: " + CameraCalculator.droneToLeftDist(altitude, FOVHeight, angleY));
		System.out.println("Right: " + CameraCalculator.droneToRightDist(altitude, FOVHeight, angleY));
		
//		Vector2d pivot = new Vector2d(0, 0);
//		Vector2d point = new Vector2d(5, 0);
//		
//		System.out.println(CameraCalculator.rotatePoint(point, Math.toRadians(0), pivot));
//		System.out.println(CameraCalculator.rotatePoint(point, Math.toRadians(90), pivot));
//		System.out.println(CameraCalculator.rotatePoint(point, Math.toRadians(180), pivot));
//		System.out.println(CameraCalculator.rotatePoint(point, Math.toRadians(270), pivot));
		
		
		
		System.out.println("Program has finished");
	}

}
