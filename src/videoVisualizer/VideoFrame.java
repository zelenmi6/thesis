package videoVisualizer;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import video.Imshow;

public class VideoFrame implements Runnable {
static{ System.loadLibrary("opencv_java300"); }
	
	VideoCapture cap;
	Mat matFrame;
	Imshow ims;
	double frameCount;
	boolean keepPlaying = true;
	int currentFrame = 1;
	
	public VideoFrame(String videoName, String videoPath) {
		ims = new Imshow(videoName);
		matFrame = new Mat();
		cap = new VideoCapture(videoPath);
		if (!cap.isOpened()) {
			System.out.println("Cannot open video file");
			return;
		}
		frameCount = cap.get(Videoio.CAP_PROP_FRAME_COUNT);
		
		cap.grab();
		cap.retrieve(matFrame);
		ims.showImage(matFrame);
	}
	
	public void moveToFrame(int idx) {
		currentFrame = idx;
		cap.set(1, idx); // CV_CAP_PROP_POS_FRAMES == 1
		cap.read(matFrame);
		ims.showImage(matFrame);
	}
	
	public void moveToTime(int seconds) {
		
	}
	
	public void startPlaying() {
		keepPlaying = true;
		synchronized (VideoVisualizer.LOCK) {
			VideoVisualizer.LOCK.notify();
		}
	}
	
	public void stopPlaying() {
		keepPlaying = false;
	}

	@Override
	public void run() {
		while(true) {
			if (!keepPlaying) {
				synchronized(VideoVisualizer.LOCK) {
					try {
						VideoVisualizer.LOCK.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
			cap.grab();
			currentFrame ++;
			cap.retrieve(matFrame);
			if (!(matFrame.empty())) {
				ims.showImage(matFrame);
			} else {
				System.out.println("Nothing to process");
			}
		}
	}
}
