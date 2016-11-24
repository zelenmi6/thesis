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
import constants.CameraTesting;
import database.VideoPicturesDao;
import geometry.Calculations;
import geometry.CameraCalculator;
import geometry.ConvexHull;
import geometry.GeoLocation;
import loaders.VideoLoader;
import video.FrameGrabber;
import video.OpenCVGrabber;
import video.TransformEstimate;
import videoVisualizer.VideoVisualizer;
import visualizer.DataSetVisualizer;
import visualizer.Visualizer;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
//		GeoLocation cameraPosition = GeoLocation.fromDegrees(50.086213, 14.434772);
//		GeoLocation[] geoLocation = cameraPosition.boundingCoordinates(CameraTesting.MAX_DISTANCE / 1000, 6371.01);
//		System.out.println("Waiting");
		
//		Calculations.getFramesWithPoint(-13.294068549350916, 3.6692342889354252, 4.);
		
//		MatSerializer.printCalibrationMatrixValues("resources/camera/cameraMatrix_gopro_0.23.json");
		
//		AbstractCamera camera = new Hero4Black(Hero4BlackFieldOfView.WIDE_16X9, 25);
//		TelemetryHomographyComparator comparator = new TelemetryHomographyComparator(
//				"C:\\Users\\Milan\\Desktop\\26.8.16 data\\fisheye\\GOPR3989_cropped.avi",
//				"C:\\Users\\Milan\\Desktop\\26.8.16 data\\parsed_logs\\2016_08_26_11_57_56.csv",
//				camera);
////		long time = 122000-60000;
////		long offset = 1160*2;
//		long time = 122000-60000+1160;
//		long offset = 1160;
//		comparator.compareTelemetryAndHomography(time, time + offset, true);
		
//		VideoVisualizer vv = new VideoVisualizer();
		
//		CameraCalibration calib = new CameraCalibration();
//		calib.runCalibration(null);
		
//		
//		VideoAnalyzer va = new VideoAnalyzer();
//		va.setRotationMatrixAndTranslationVector(14.1209483, 50.070355, 0);
//		va.testConversion(new Vector3d(14.120762, 50.070322, 4));
//		va.printDataSetInformation(30);
//		va.printGpsOfCamera(47145); // frame 1800
//		va.printGpsOfCamera(20345); // start of telemetry
		
		
//		CameraCalculator.getBoundingPolygon(CameraTesting.FOVh, CameraTesting.FOVv, 10, CameraTesting.ROLL,
//				CameraTesting.PITCH, CameraTesting.HEADING);
		
//		DataSetVisualizer v = new DataSetVisualizer();
//		JFrame frame = new JFrame("Visualizer");
//		frame.setContentPane(v);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.pack();
//		frame.setLocationRelativeTo(null);
//		frame.setVisible(true);
////		v.visualizeDataSet(30, 1000, 48); // original data
//		v.visualizeDataSet(34, 40, 0);
		
		
//		va.testConversion(new Vector3d(50.06843579, 14.34322337, 0.5776692915));
//		va.testConversion(new Vector3d(14.34322337, 50.06843579, 0.5776692915));
//		va.testConversion(new Vector3d(14.34309495, 50.06843546, 0));
//		va.testConversion(new Vector3d(14.34292243, 50.06844189, 0)); // destnik 1
//		va.testConversion(new Vector3d(14.3429035, 50.06854956, 0));
		//!TODO nejspise nekde prehozene latitude / longitude
		
//		Visualizer v = new Visualizer();
//		JFrame frame = new JFrame("Visualizer");
//		frame.setContentPane(v);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.pack();
//		frame.setLocationRelativeTo(null);
//		frame.setVisible(true);
		
//		AbstractCamera camera = new Hero4PlusBlack(Hero3PlusBlackFieldOfView.WIDE_16X9, 25);
//		VideoLoader vl = new VideoLoader("nothing yet", "C:/Users/Milan/Desktop/26.8.16 data/parsed_logs/2016_08_26_11_57_56.csv",
//				"louka2", camera, 0);
//		(1212/camera.getFps() * 1000)

		
//		OpenCVGrabber grabber = new OpenCVGrabber("C:\\Users\\Milan\\Desktop\\droneVideo\\video\\1_converted.mp4",
//				"C:/JavaPrograms/thesis/resources/output/");
		
//		FrameGrabber grabber = new FrameGrabber("C:\\Users\\Milan\\Desktop\\droneVideo\\video\\1_converted.mp4");
//		grabber.showNthFrame(5000);
//		Thread.sleep(5000);
//		grabber.showNthFrame(1805);
//		Thread.sleep(5000);
//		grabber.showNthFrame(47145);
		//48 vterin
		
//		FrameGrabber grabber = new FrameGrabber("C:\\Users\\Milan\\Desktop\\calibration\\GOPR4141.MP4");
//		FrameGrabber grabber = new FrameGrabber("C:\\Users\\Milan\\Desktop\\telemetry_test\\GOPR4142.avi");
//		for (int i = 1; i <= 2581; i += 1) {
//			grabber.saveNthFrame(i, "C:\\Users\\Milan\\Desktop\\telemetry_test\\frames\\");
//		}
//		grabber.saveNthFrame(3050, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\");
//		grabber.saveNthFrame(3055, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\");
//		grabber.saveNthFrame(25*11, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\");
//		grabber.saveNthFrame(75, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\");
//		grabber.saveNthFrame(100, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\");
//		grabber.saveNthFrame(125, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\");
//		grabber.saveNthFrame(150, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\");
//		grabber.saveNthFrame(350, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\");
		
//		int pictureNum = 2581/25;
//		int pictureNum = 3;
//		int[] chain = new int[]{350, 375};
//		int[] indices = new int[pictureNum];
//		for (int i = 1; i <= pictureNum; i ++) {
//			indices[i-1] = i*25;
//		}
//		int[][] pairs = new int[][]{{1, 25},{55, 88}};
		int[][] pairs = new int[][]{{498, 560}, {498, 618}, {618, 677}, {618, 762}, {677, 762}};
		
//		OdometricAngles oa = new OdometricAngles("C:\\Users\\Milan\\Desktop\\telemetry_test\\frames\\", "jpg");
//		try {
//			oa.runCalculationChain(chain);
//			oa.runCalculationPairs(pairs);
//		} catch (Exception e) {
//			// nothing
//		} finally {
//			oa.serializeResults("pairResultTest.ser");
//		}
			
		OdometryTelemetryComparator odometryComparator = 
				new OdometryTelemetryComparator("C:\\Users\\Milan\\Desktop\\telemetry_test\\frames\\pairResultTest.ser",
						pairs, 25);
		odometryComparator.loadTelemetry("C:\\Users\\Milan\\Desktop\\telemetry_test\\apmLog\\2016_11_24_15_50_07.csv");
		odometryComparator.compareTelemetryOdometry();
		
//		OdometryAnalyzer odometryAnalyzer = 
//				new OdometryAnalyzer("C:\\Users\\Milan\\Desktop\\telemetry_test\\frames\\results_every25_frame.ser", 1000, 25);
//		odometryAnalyzer.printRollForWolfram();
//		odometryAnalyzer.printRollComulativeForWolfram();
//		odometryAnalyzer.printRollDebug();
			
		
		
//		List<double[]> rotations = oa.deserializeResults("testResults.ser");
//		System.out.println("Ready");
		
//		TransformEstimate te = new TransformEstimate("C:\\Users\\Milan\\Desktop\\telemetry_test\\frames\\521.jpg",
//				"C:\\Users\\Milan\\Desktop\\telemetry_test\\frames\\586.jpg");
		
//		TransformEstimate te = new TransformEstimate("C:\\JavaPrograms\\thesis\\resources\\output\\frames\\old_data\\roll2\\1.png",
//				"C:\\JavaPrograms\\thesis\\resources\\output\\frames\\old_data\\roll2\\25.png");
		
//		TransformEstimate te = new TransformEstimate("C:\\JavaPrograms\\thesis\\resources\\output\\frames\\3050.jpg",
//				"C:\\JavaPrograms\\thesis\\resources\\output\\frames\\3055.jpg");

//		System.out.println("Program has finished");
	}

}
