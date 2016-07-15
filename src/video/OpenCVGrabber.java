package video;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

public class OpenCVGrabber {
	static{ System.loadLibrary("opencv_java300"); }
	
	VideoCapture cap = new VideoCapture();
	Mat matFrame = new Mat();
	
	public OpenCVGrabber(String videoPath, String outputDirectory) {
		cap.open(videoPath);
		
		if (cap.isOpened()) {
			boolean keepProcessing = true;
			Imshow ims = new Imshow("From video source ... ");
			int counter = 0;
			while (keepProcessing) {
				counter ++;
				cap.grab();
				cap.retrieve(matFrame);
				if (counter % 25 == 0)
					Imgcodecs.imwrite(outputDirectory + counter + ".png", matFrame);
				if (!(matFrame.empty())) {
					ims.showImage(matFrame);
//					try {
//						Thread.sleep(40);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
				} else {
					keepProcessing = false;
				}
			}
			
			
		} else {
			System.out.println("error cannot open any capture source - exiting");
		}
		cap.release();
	}
	

}
