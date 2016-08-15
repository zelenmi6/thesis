package cameras;

public abstract class AbstractCamera {
	
	protected String cameraName;
	protected double fovVertical;
	protected double fovHorizontal;
	private int fps;
	
	public AbstractCamera(double initialFovVertical, double initialFovHorizontal, int initialFps) {
		fovVertical = initialFovVertical;
		fovHorizontal = initialFovHorizontal;
		fps = initialFps;
	}
	
	public AbstractCamera(FieldOfViewInterface initialFieldOfView, int initialFps) throws Exception {
		setFieldOfView(initialFieldOfView);
		fps = initialFps;
	}
	
	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public String getCameraName() {
		return cameraName;
	}

	public double getFovVertical() {
		return fovVertical;
	}

	public double getFovHorizontal() {
		return fovHorizontal;
	}
	
	public void setFovVertical(double fovVertical) {
		this.fovVertical = fovVertical;
	}

	public void setFovHorizontal(double fovHorizontal) {
		this.fovHorizontal = fovHorizontal;
	}

	public abstract void setFieldOfView(FieldOfViewInterface fieldOfView) throws Exception;
	
}
