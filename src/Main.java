import java.util.List;

import javax.swing.JFrame;
import javax.vecmath.Vector3d;

import org.postgis.LineString;
import org.postgis.LinearRing;
import org.postgis.Point;
import org.postgis.Polygon;

import analyzers.VideoAnalyzer;
import cameras.AbstractCamera;
import cameras.Hero3PlusBlack;
import cameras.Hero3PlusBlack.Hero3PlusBlackFieldOfView;
import database.VideoPicturesDao;
import geometry.Calculations;
import geometry.CameraCalculator;
import geometry.ConvexHull;
import loaders.VideoLoader;
import video.FrameGrabber;
import video.OpenCVGrabber;
import video.TransformEstimate;
import visualizer.DataSetVisualizer;
import visualizer.Visualizer;

public class Main {
	
	public static void main(String[] args) throws Exception {
//		
//		VideoAnalyzer va = new VideoAnalyzer();
//		va.setRotationMatrixAndTranslationVector(14.34322337, 50.06843579, 0.5776692915);
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
		
		TransformEstimate te = new TransformEstimate("C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\resized1.jpg",
				"C:\\Users\\Milan\\Google Drive\\Škola\\Magistr\\Diplomka2\\photos\\resized2.jpg");
		
//		DataSetVisualizer v = new DataSetVisualizer();
//		JFrame frame = new JFrame("Visualizer");
//		frame.setContentPane(v);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.pack();
//		frame.setLocationRelativeTo(null);
//		frame.setVisible(true);
//		v.visualizeDataSet(30, 1000, 48);
		
		
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
//		VideoLoader vl = new VideoLoader("nothing yet", "C:\\Users\\Milan\\Desktop\\droneVideo\\parsed\\2016_07_25_11_28_14.csv",
//				"louka", camera, (1212/camera.getFps() * 1000));

		
//		OpenCVGrabber grabber = new OpenCVGrabber("C:\\Users\\Milan\\Desktop\\droneVideo\\video\\1_converted.mp4",
//				"C:/JavaPrograms/thesis/resources/output/");
		
//		FrameGrabber grabber = new FrameGrabber("C:\\Users\\Milan\\Desktop\\droneVideo\\video\\1_converted.mp4");
//		grabber.showNthFrame(5000);
//		grabber.showNthFrame(1805);
//		grabber.showNthFrame(47145);
		//48 vterin


//		System.out.println("Program has finished");
	}

}
