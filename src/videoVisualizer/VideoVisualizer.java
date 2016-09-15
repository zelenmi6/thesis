package videoVisualizer;

public class VideoVisualizer {
	private VideoFrame videoFrame;
	public static final Object LOCK = new Object();
	
	public VideoVisualizer() throws InterruptedException {
		videoFrame = new VideoFrame("New video", "C:\\Users\\Milan\\Desktop\\26.8.16 data\\fisheye\\GOPR3989_cropped.avi");
		Thread t = new Thread(videoFrame);
		t.start();
		Thread.sleep(3000);
		videoFrame.stopPlaying();
		Thread.sleep(3000);
		videoFrame.startPlaying();		
	}
}
