import javax.swing.JFrame;
import javax.vecmath.Vector3d;

import analyzers.VideoAnalyzer;
import cameras.AbstractCamera;
import cameras.Hero3PlusBlack;
import cameras.Hero3PlusBlack.Hero3PlusBlackFieldOfView;
import geometry.Calculations;
import geometry.CameraCalculator;
import geometry.ConvexHull;
import loaders.VideoLoader;
import video.FrameGrabber;
import video.OpenCVGrabber;
import visualizer.Visualizer;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		VideoAnalyzer va = new VideoAnalyzer();
		va.setRotationMatrixAndTranslationVector(14.34322337, 50.06843579, 0.5776692915);
//		va.printDataSetInformation(11);
//		va.printGpsOfCamera(20369); // frame 1800
//		va.printGpsOfCamera(20345); // start of telemetry
		
//		va.testConversion(new Vector3d(50.06843579, 14.34322337, 0.5776692915));
		va.testConversion(new Vector3d(14.34322337, 50.06843579, 0.5776692915));
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
//				"test", camera, (1212/camera.getFps() * 1000));
		
//		System.out.println(Calculations.getEquationOfAPlane(new Vector3d(1, 1, 1), new Vector3d(-1, 1, 0), new Vector3d(2, 0, 3)).toString());
		
//		Vector3d translation = CameraCalculator.translatePointToAxesOrigin(new Vector3d(5, 10, 15), new Vector3d(-3, 5, 8));
//		System.out.println(translation);
//		translation = CameraCalculator.translatePointFromAxesOriginToCamera(translation, new Vector3d(-3, 5, 8));
//		System.out.println(translation);
		
//		CameraCalculator.findRaysVerticalPlaneIntersection(new Vector3d[]{new Vector3d(0, 0, 0)}, new Vector3d(2, -6, 8) ,new Vector3d(3, 1, 6));
		
//		Vector3d [] pointArray = new Vector3d[]{new Vector3d(0, 0, 0), new Vector3d(10, 0, 0), new Vector3d(10, 10, 0), new Vector3d(0, 10, 0), new Vector3d(5, 5, 0),
//				 new Vector3d(11, 11, 0)};
//		Vector3d [] polygon = ConvexHull.convex_hull(pointArray);
//		for (Vector3d point: polygon) {
//			System.out.println(point);
//		}
		
//		OpenCVGrabber grabber = new OpenCVGrabber("C:\\Users\\Milan\\Desktop\\droneVideo\\video\\1_converted.mp4",
//				"C:/JavaPrograms/thesis/resources/output/");
		
		FrameGrabber grabber = new FrameGrabber("C:\\Users\\Milan\\Desktop\\droneVideo\\video\\1_converted.mp4");
//		grabber.showNthFrame(1212);
//		grabber.showNthFrame(1805);
//		grabber.showNthFrame(6575);
		//48 vterin


//		System.out.println("Program has finished");
	}

}
