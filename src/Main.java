import java.util.List;

import javax.swing.JFrame;
import javax.vecmath.Vector3d;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.postgis.LineString;
import org.postgis.LinearRing;
import org.postgis.Point;
import org.postgis.Polygon;

import analyzers.TelemetryHomographyComparator;
import analyzers.VideoAnalyzer;
import camera_calibration.CameraCalibration;
import camera_calibration.MatSerializer;
import cameras.AbstractCamera;
import cameras.Hero4PlusBlack;
import cameras.Hero4PlusBlack.Hero3PlusBlackFieldOfView;
import constants.CameraTesting;
import database.VideoPicturesDao;
import geometry.Calculations;
import geometry.CameraCalculator;
import geometry.ConvexHull;
import loaders.VideoLoader;
import video.FrameGrabber;
import video.OpenCVGrabber;
import video.TransformEstimate;
import videoVisualizer.VideoVisualizer;
import visualizer.DataSetVisualizer;
import visualizer.Visualizer;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		MatSerializer.printCalibrationMatrixValues("resources/camera/cameraMatrix_gopro_0.23.json");
		
//		AbstractCamera camera = new Hero3PlusBlack(Hero3PlusBlackFieldOfView.WIDE_16X9, 25);
//		TelemetryHomographyComparator comparator = new TelemetryHomographyComparator(
//				"C:\\Users\\Milan\\Desktop\\26.8.16 data\\fisheye\\GOPR3989_cropped.avi",
//				"C:\\Users\\Milan\\Desktop\\26.8.16 data\\parsed_logs\\2016_08_26_11_57_56.csv",
//				camera);
//		long time = 122000-60000;
//		long offset = 1160;
//		comparator.compareTelemetryAndHomography(time, time + offset, false);
		
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
		
//		TransformEstimate te = new TransformEstimate("C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\20160817_143831.jpg",
//				"C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\20160817_143837.jpg");
		
//		TransformEstimate te = new TransformEstimate("C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\20160817_150515.jpg",
//		"C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\20160817_150510.jpg"); //posunute v malovani
		
//		TransformEstimate te = new TransformEstimate("C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\20160817_150808.jpg",
//				"C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\20160817_150804.jpg");
		
//		TransformEstimate te = new TransformEstimate("C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\GVExn.png",
//				"C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\To7eN.png"); // fungujici priklad, posunute
		
//		TransformEstimate te = new TransformEstimate("C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\gray1.jpg",
//				"C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\gray2.jpg");
		
//		TransformEstimate te = new TransformEstimate("C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\2gray.jpg",
//				"C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\2_2gray.jpg");
		
//		TransformEstimate te = new TransformEstimate("C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\malyposun.jpg",
//				"C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\malyposun2.jpg");
		
//		TransformEstimate te = new TransformEstimate("C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\pokus1.jpg",
//				"C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\pokus2.jpg");
		
//		TransformEstimate te = new TransformEstimate("C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\resized1.jpg",
//				"C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\resized2.jpg");
		
//		TransformEstimate te = new TransformEstimate("C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\malyposun.jpg",
//				"C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\malyposun - copy.jpg");
		
//		TransformEstimate te = new TransformEstimate("C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\multipleRot.jpg",
//				"C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\multipleRot2.jpg");
		
//		TransformEstimate te = new TransformEstimate("C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\wall1.jpg",
//				"C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\wall2.jpg");
		
//		TransformEstimate te = new TransformEstimate("C:\\JavaPrograms\\thesis\\resources\\output\\frames\\3060.png",
//				"C:\\JavaPrograms\\thesis\\resources\\output\\frames\\3075.png");
		
//		TransformEstimate te = new TransformEstimate("C:\\JavaPrograms\\thesis\\resources\\output\\frames\\1.png",
//				"C:\\JavaPrograms\\thesis\\resources\\output\\frames\\72.png");
		
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
//		v.visualizeDataSet(33, 40, 0);
		
		
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
		
//		AbstractCamera camera = new Hero3PlusBlack(Hero3PlusBlackFieldOfView.WIDE_16X9, 25);
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
		
//		FrameGrabber grabber = new FrameGrabber("C:\\Users\\Milan\\Desktop\\calibration\\GOPR4098.MP4");
//		FrameGrabber grabber = new FrameGrabber("C:\\Users\\Milan\\Desktop\\26.8.16 data\\no_fisheye\\GOPR4034.avi");
//		for (int i = 1; i < 27750; i += 7) {
//			grabber.saveNthFrame(i, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\calibFrames\\");
//		}
//		grabber.saveNthFrame(36, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\");
//		grabber.saveNthFrame(92, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\");
//		grabber.saveNthFrame(125, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\");
//		grabber.saveNthFrame(175, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\");
//		grabber.saveNthFrame(125, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\");
//		grabber.saveNthFrame(175, "C:\\JavaPrograms\\thesis\\resources\\output\\frames\\");
		
		
//		TransformEstimate te = new TransformEstimate("C:\\JavaPrograms\\thesis\\resources\\output\\frames\\roll2\\1.png",
//				"C:\\JavaPrograms\\thesis\\resources\\output\\frames\\roll2\\25.png");
		
//		TransformEstimate te = new TransformEstimate("C:\\JavaPrograms\\thesis\\resources\\output\\frames\\36.jpg",
//				"C:\\JavaPrograms\\thesis\\resources\\output\\frames\\92.jpg");

//		System.out.println("Program has finished");
	}

}
