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
import visualizer.DataSetVisualizer;
import visualizer.Visualizer;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		VideoAnalyzer va = new VideoAnalyzer();
		va.setRotationMatrixAndTranslationVector(14.34322337, 50.06843579, 0.5776692915);
		va.printDataSetInformation(30);
//		va.printGpsOfCamera(47145); // frame 1800
//		va.printGpsOfCamera(20345); // start of telemetry
		
//		DataSetVisualizer v = new DataSetVisualizer();
//		JFrame frame = new JFrame("Visualizer");
//		frame.setContentPane(v);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.pack();
//		frame.setLocationRelativeTo(null);
//		frame.setVisible(true);
//		v.visualizeDataSet(30, 1000);
		
		
//		va.testConversion(new Vector3d(50.06843579, 14.34322337, 0.5776692915));
//		va.testConversion(new Vector3d(14.34322337, 50.06843579, 0.5776692915));
//		va.testConversion(new Vector3d(14.34309495, 50.06843546, 0));
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
		
		FrameGrabber grabber = new FrameGrabber("C:\\Users\\Milan\\Desktop\\droneVideo\\video\\1_converted.mp4");
		grabber.showNthFrame(5000);
//		grabber.showNthFrame(1805);
//		grabber.showNthFrame(47145);
		//48 vterin


//		System.out.println("Program has finished");
	}

}
