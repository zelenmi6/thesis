import java.util.List;

import javax.swing.JFrame;
import javax.vecmath.Vector3d;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.postgis.LineString;
import org.postgis.LinearRing;
import org.postgis.Point;
import org.postgis.Polygon;

import analyzers.OdometricAngles;
import analyzers.OdometryAnalyzer;
import analyzers.OdometryTelemetryComparator;
import analyzers.TelemetryHomographyComparator;
import analyzers.VideoAnalyzer;
import camera_calibration.CameraCalibration;
import camera_calibration.MatSerializer;
import cameras.AbstractCamera;
import cameras.Hero4Black;
import cameras.Hero4Black.Hero4BlackFieldOfView;
import cameras.Hero4BlackUndistorted;
import cameras.Hero4BlackUndistorted.Hero4BlackUndistortedFieldOfView;
import constants.CameraTesting;
import database.VideoPicturesDao;
import geometry.Calculations;
import geometry.CameraCalculator;
import geometry.ConvexHull;
import geometry.GeoLocation;
import loaders.Telemetry;
import loaders.VideoLoader;
import tests.AltitudeErrorTest;
import tests.VisualTelemetryTest;
import video.FrameGrabber;
import video.OpenCVGrabber;
import video.TransformEstimate;
import videoVisualizer.VideoVisualizer;
import visualizer.DataSetVisualizer;
import visualizer.Visualizer;

public class Main {
	
	public static void main(String[] args) throws Exception {	
		
//		VisualTelemetryTest vtt = new VisualTelemetryTest();
		
//		GeoLocation cameraPosition = GeoLocation.fromDegrees(50.086213, 14.434772);
//		GeoLocation[] geoLocation = cameraPosition.boundingCoordinates(CameraTesting.MAX_DISTANCE / 1000, 6371.01);
//		System.out.println("Waiting");
		
//		Calculations.getFramesWithPoint(-13.294068549350916, 3.6692342889354252, 4., 88);

		
//		MatSerializer.printCalibrationMatrixValues("resources/camera/cameraMatrix_gopro_0.23.json");
		
		
//		VideoVisualizer vv = new VideoVisualizer();
		
//		CameraCalibration calib = new CameraCalibration();
//		calib.runCalibration(null);
		
//		Test 1
//		VideoAnalyzer va = new VideoAnalyzer();
//		va.setRotationMatrixAndTranslationVector(14.1209483, 50.070355, 0);
//		va.testConversion(new Vector3d(14.120762, 50.070322, 4));
//		AltitudeErrorTest aet = new AltitudeErrorTest();
//		aet.polygonsContainingPoint(-13.294068549350916, 3.6692342889354252, 4., 88, 91, 90);
		
		//Test 2
//		VideoAnalyzer va = new VideoAnalyzer();
//		va.setRotationMatrixAndTranslationVector(14.1208351, 50.0702896, 0.16);
//		va.testConversion(new Vector3d(14.120763, 50.070377, 4));
//		va.testConversion(new Vector3d(14.12075735, 50.07039369, 4));
//		Calculations.getFramesWithPoint(-5.153951485404552, -9.721579921488562, 3.8387163984930255, 126);
//		Calculations.getFramesWithPoint(-5.547175031803819, -11.581271490751064, 3.8387163984930255, 126);
//		AltitudeErrorTest aet = new AltitudeErrorTest();
//		aet.polygonsContainingPoint(-5.153951485404552, -9.721579921488562, 3.8387163984930255, 131, 132, 129);
		
//		va.printDataSetInformation(30);
//		va.printGpsOfCamera(47145); // frame 1800
//		va.printGpsOfCamera(20345); // start of telemetry
		

		
		DataSetVisualizer v = new DataSetVisualizer();
		JFrame frame = new JFrame("Visualizer");
		frame.setContentPane(v);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		v.visualizeDataSet(133, 40, 0);
		
		
//		Visualizer v = new Visualizer();
//		JFrame frame = new JFrame("Visualizer");
//		frame.setContentPane(v);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.pack();
//		frame.setLocationRelativeTo(null);
//		frame.setVisible(true);
		
//		AbstractCamera camera = new Hero4Black(Hero4BlackFieldOfView.WIDE_16X9, 25);
//		AbstractCamera cameraUndistorted = new Hero4BlackUndistorted(Hero4BlackUndistortedFieldOfView.WIDE_16X9, 25);
//		VideoLoader vl = new VideoLoader("nothing yet", "C:/Users/Milan/Desktop/6.12.12 data/2016_12_06_heli/2016_12_06_13_37_19.csv",
//				"New data, Higher altitude undistorted", cameraUndistorted, 0, true);
//		VideoLoader v2 = new VideoLoader("nothing yet", "C:/Users/Milan/Desktop/26.8.16 data/parsed_logs/2016_08_26_11_57_56.csv",
//				"Higher Altitude, distorted", camera, 0);
		
//		VisualTelemetryTest vtt = new VisualTelemetryTest();

		
//		OpenCVGrabber grabber = new OpenCVGrabber("C:\\Users\\Milan\\Desktop\\droneVideo\\video\\1_converted.mp4",
//				"C:/JavaPrograms/thesis/resources/output/");
		
//		FrameGrabber grabber = new FrameGrabber("C:\\Users\\Milan\\Desktop\\droneVideo\\video\\1_converted.mp4");
//		grabber.showNthFrame(5000);
//		Thread.sleep(5000);
//		grabber.showNthFrame(1805);
//		Thread.sleep(5000);
//		grabber.showNthFrame(47145);
		//48 vterin
		
//		FrameGrabber grabber = new FrameGrabber("C:\\Users\\Milan\\Desktop\\26.8.16 data\\fisheye\\movie_maker_cropped.mp4");
//		FrameGrabber grabber = new FrameGrabber("C:\\Users\\Milan\\Desktop\\telemetry_test\\newYawTestCropped.mp4");
//		for (int i = 0; i <= 2000; i ++) {
//			grabber.saveNthFrame(i, "C:\\Users\\Milan\\Desktop\\telemetry_test\\newYawTest\\");
//			grabber.saveNthFrame(i, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\videoFramesRectified\\");
//			grabber.saveNthFrameRectified(i, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\newRectified\\");
//		}

//		System.out.println("Program has finished");
//		System.exit(0);
	}

}
