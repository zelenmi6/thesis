package video;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.imgcodecs.Imgcodecs;

public class FrameGrabber {
	static{ System.loadLibrary("libopencv_java310"); }
	
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
	
	public void saveNthFrame(int idx, String outputDirectory) {
		cap.set(1, idx); // CV_CAP_PROP_POS_FRAMES == 1
		cap.read(matFrame);// ?
		cap.retrieve(matFrame);// ?
//		ims.showImage(matFrame);
		boolean gray = false;
		if (gray) {
			Mat matFrameGray = new Mat(matFrame.height(), matFrame.width(), CvType.CV_8UC1);
			org.opencv.imgproc.Imgproc.cvtColor(matFrame, matFrameGray, org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY);
			matFrame = matFrameGray;
		}
		Imgcodecs.imwrite(outputDirectory + idx + ".jpg", matFrame);
	}

}
