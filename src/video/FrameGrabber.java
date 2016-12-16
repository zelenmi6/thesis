package video;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

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
		cap.read(matFrame);
		cap.retrieve(matFrame);
//		ims.showImage(matFrame);
		boolean gray = false;
		if (gray) {
			Mat matFrameGray = new Mat(matFrame.height(), matFrame.width(), CvType.CV_8UC1);
			org.opencv.imgproc.Imgproc.cvtColor(matFrame, matFrameGray, org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY);
			matFrame = matFrameGray;
		}
		Imgcodecs.imwrite(outputDirectory + idx + ".jpg", matFrame);
	}
	
	public void saveNthFrameRectified(int idx, String outputDirectory) {
		cap.set(1, idx); // CV_CAP_PROP_POS_FRAMES == 1
		cap.read(matFrame);
		cap.retrieve(matFrame);
		Mat matFrameGray = new Mat(matFrame.height(), matFrame.width(), CvType.CV_8UC3);
		org.opencv.imgproc.Imgproc.cvtColor(matFrame, matFrameGray, org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY);
		Mat undistorted = undistortImage(matFrameGray);
		Imgcodecs.imwrite(outputDirectory + idx + ".jpg", undistorted);
	}
	
	private Mat getImage(String path) throws IOException {
		BufferedImage image = ImageIO.read(new File(path));
		Mat matFrame = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		matFrame.put(0, 0, pixels);
		
		Mat matFrameGray = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		org.opencv.imgproc.Imgproc.cvtColor(matFrame, matFrameGray, org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY);
		return matFrameGray;
	}
	
	private Mat undistortImage(Mat distorted) {
		Mat distortionCoefficients = new Mat(1, 5, CvType.CV_32F);
		
		distortionCoefficients.put(0, 0, -0.25722);
		distortionCoefficients.put(0, 1, 0.09022);
		distortionCoefficients.put(0, 2, -0.00060);
		distortionCoefficients.put(0, 3, 0.00009);
		distortionCoefficients.put(0, 4, -0.01662);
		
		Mat undistorted = new Mat(distorted.height(), distorted.width(), CvType.CV_8UC3);
		
		Mat cameraMat = new Mat(3, 3, 6);
		cameraMat.put(0, 0, 582.18394);
		cameraMat.put(0, 2, 663.50655);
		cameraMat.put(1, 1, 582.52915);
		cameraMat.put(1, 2, 378.74541);
		cameraMat.put(2, 2, 1.);

		Imgproc.undistort(distorted, undistorted, cameraMat, distortionCoefficients);
		return undistorted;
		
	}

}
























