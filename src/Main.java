import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import video.Imshow;
import video.OpenCVGrabber;

public class Main {
	
	public static void main(String[] args) throws InterruptedException {


		
		OpenCVGrabber grabber = new OpenCVGrabber("C:/JavaPrograms/thesis/resources/input/SampleVideo_1280x720_5mb.flv",
				"C:/JavaPrograms/thesis/resources/output/");

		// PictureTelemetryDao dao = PictureTelemetryDao.getInstance();
		// Vector3d a = new Vector3d(0, 0, 0);
		// Vector3d b = new Vector3d(-10, -5, 0);
		// Vector3d c = new Vector3d(-4, -4, 0);
		// Vector3d d = new Vector3d(-2, -3, 0);
		//
		// try {
		// dao.addPicture(141, "dummyPath2", new Vector3d[]{a, b, c, d});
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// Vector3d [] polygon =
		// CameraCalculator.getPolygon(Math.toRadians(64.4),
		// Math.toRadians(37.2), 8.04, 0, 0, 0);

		// final double FOVh = Math.toRadians(64.4);
		// final double FOVv = Math.toRadians(37.2);
		// DirectoryPicturesLoader parser = new DirectoryPicturesLoader("Dvur,
		// ",
		// "C:/Users/Milan/Google
		// Drive/Škola/Magistr/Diplomka2/bkpImages/dvurSmall", FOVv, FOVh);
		// DirectoryPicturesLoader parser = new
		// DirectoryPicturesLoader("Invalidovna, ", "C:/Users/Milan/Google
		// Drive/Škola/Magistr/Diplomka2/bkpImages/invalidovna");
		// PictureTelemetryDao dao = PictureTelemetryDao.getInstance();
		//
		// final double altitude = 8;
		// final double angleX = Math.toRadians(0);
		// final double angleY = Math.toRadians(0);
		// final double FOVWidth = Math.toRadians(64.4);
		// final double FOVHeight = Math.toRadians(37.2);
		//
		// Vector3d ray1 = CameraCalculator.ray1(FOVWidth, FOVHeight);
		// Vector3d ray2 = CameraCalculator.ray2(FOVWidth, FOVHeight);
		// Vector3d ray3 = CameraCalculator.ray3(FOVWidth, FOVHeight);
		// Vector3d ray4 = CameraCalculator.ray4(FOVWidth, FOVHeight);
		//
		// CameraCalculator.printDirections(ray1, ray2, ray3, ray4);
		// ray1.normalize();ray2.normalize();ray3.normalize();ray4.normalize();
		// CameraCalculator.printDirections(ray1, ray2, ray3, ray4);
		//
		// Vector3d [] rotatedVectors = CameraCalculator.rotateRays(
		// ray1, ray2, ray3, ray4, Math.toRadians(0), Math.toRadians(0),
		// Math.toRadians(0));
		// ray1 = rotatedVectors[0];
		// ray2 = rotatedVectors[1];
		// ray3 = rotatedVectors[2];
		// ray4 = rotatedVectors[3];
		//
		// Vector3d intersection1 =
		// CameraCalculator.findVectorGroundIntersection(ray1, new Vector3d(0,
		// 0, altitude));
		// Vector3d intersection2 =
		// CameraCalculator.findVectorGroundIntersection(ray2, new Vector3d(0,
		// 0, altitude));
		// Vector3d intersection3 =
		// CameraCalculator.findVectorGroundIntersection(ray3, new Vector3d(0,
		// 0, altitude));
		// Vector3d intersection4 =
		// CameraCalculator.findVectorGroundIntersection(ray4, new Vector3d(0,
		// 0, altitude));
		// System.out.println("Intersecting: " + intersection1.x + ", " +
		// intersection1.y + ", " + intersection1.z);
		// System.out.println("Intersecting: " + intersection2.x + ", " +
		// intersection2.y + ", " + intersection2.z);
		// System.out.println("Intersecting: " + intersection3.x + ", " +
		// intersection3.y + ", " + intersection3.z);
		// System.out.println("Intersecting: " + intersection4.x + ", " +
		// intersection4.y + ", " + intersection4.z);
		// System.out.println("Intersecting: " + intersection1.x + ", " +
		// intersection1.z + ", " + intersection1.y);
		// System.out.println("Intersecting: " + intersection2.x + ", " +
		// intersection2.z + ", " + intersection2.y);
		// System.out.println("Intersecting: " + intersection3.x + ", " +
		// intersection3.z + ", " + intersection3.y);
		// System.out.println("Intersecting: " + intersection4.x + ", " +
		// intersection4.z + ", " + intersection4.y);
		//
		// System.out.println("Bottom: " +
		// CameraCalculator.droneToBottomDist(altitude, FOVWidth, angleX));
		// System.out.println("Top: " +
		// CameraCalculator.droneToTopDist(altitude, FOVWidth, angleX));
		//
		// System.out.println("Left: " +
		// CameraCalculator.droneToLeftDist(altitude, FOVHeight, angleY));
		// System.out.println("Right: " +
		// CameraCalculator.droneToRightDist(altitude, FOVHeight, angleY));

		System.out.println("Program has finished");
	}

}
