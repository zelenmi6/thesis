package video;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class FrameGrabber {
	static{ System.loadLibrary("opencv_java300"); }
	
	VideoCapture cap;
	Mat matFrame = new Mat();
	Imshow ims = new Imshow("From video source ... ");
	double frameCount;
	
	public FrameGrabber(String videoPath) {
		cap = new VideoCapture(videoPath);
		if (!cap.isOpened()) {
			System.out.println("Cannot open video file");
			return;
		}
		
		frameCount = cap.get(Videoio.CAP_PROP_FRAME_COUNT);
		
	}
	
	public void showNthFrame(int idx) {
		cap.set(1, idx); // CV_CAP_PROP_POS_FRAMES == 1
		cap.read(matFrame);
		ims.showImage(matFrame);
	}

}
