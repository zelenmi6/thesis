package cameras;

public class Hero3PlusBlack extends AbstractCamera {

	public enum Hero3PlusBlackFieldOfView implements FieldOfViewInterface {
		WIDE_4x3, MEDIUM_4X3, NARROW_4X3, WIDE_17X9, MEDIUM_17X9, NARROW_1_X9, WIDE_16X9, MEDIUM_16X9, NARROW_16X9;
	}
	
	public Hero3PlusBlack(double initialFovVertical, double initialFovHorizontal, int initialFps) {
		super(initialFovVertical, initialFovHorizontal, initialFps);
		this.cameraName = "HERO3+ Black Edition";
	}
	
	public Hero3PlusBlack(FieldOfViewInterface initialFieldOfView, int initialFps) throws Exception {
		super(initialFieldOfView, initialFps);
		this.cameraName = "HERO3+ Black Edition";
	}

	@Override
	public void setFieldOfView(FieldOfViewInterface fieldOfView) throws Exception {
		if (fieldOfView instanceof Hero3PlusBlackFieldOfView == false) {
			throw new Exception("Enum needs to be of type Hero3PlusBlackFieldOfView");
		}
		Hero3PlusBlackFieldOfView fov = (Hero3PlusBlackFieldOfView)fieldOfView;
		if (fov == Hero3PlusBlackFieldOfView.WIDE_16X9) {
			this.fovVertical = Math.toRadians(69.5);
			this.fovHorizontal = Math.toRadians(118.2);
		} else {
			throw new Exception("Not implemented yet.");
		}
	}
	
}
