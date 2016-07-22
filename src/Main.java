import javax.swing.JFrame;
import javax.vecmath.Vector3d;

import geometry.ConvexHull;
import visualizer.Visualizer;

public class Main {
	
	public static void main(String[] args) throws InterruptedException {
		
		Visualizer v = new Visualizer();
		JFrame frame = new JFrame("Visualizer");
		frame.setContentPane(v);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
//		Vector3d [] pointArray = new Vector3d[]{new Vector3d(0, 0, 0), new Vector3d(10, 0, 0), new Vector3d(10, 10, 0), new Vector3d(0, 10, 0), new Vector3d(5, 5, 0),
//				 new Vector3d(11, 11, 0)};
//		Vector3d [] polygon = ConvexHull.convex_hull(pointArray);
//		for (Vector3d point: polygon) {
//			System.out.println(point);
//		}
		
//		OpenCVGrabber grabber = new OpenCVGrabber("C:/JavaPrograms/thesis/resources/input/SampleVideo_1280x720_5mb.flv",
//				"C:/JavaPrograms/thesis/resources/output/");

		// PictureTelemetryDao dao = PictureTelemetryDao.getInstance();
		// try {
		// dao.addPicture(141, "dummyPath2", new Vector3d[]{a, b, c, d});
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// DirectoryPicturesLoader parser = new DirectoryPicturesLoader("Dvur,
		// ",
		// "C:/Users/Milan/Google
		// Drive/�kola/Magistr/Diplomka2/bkpImages/dvurSmall", FOVv, FOVh);
		// DirectoryPicturesLoader parser = new
		// DirectoryPicturesLoader("Invalidovna, ", "C:/Users/Milan/Google
		// Drive/�kola/Magistr/Diplomka2/bkpImages/invalidovna");
		// PictureTelemetryDao dao = PictureTelemetryDao.getInstance();
		//


//		System.out.println("Program has finished");
	}

}
