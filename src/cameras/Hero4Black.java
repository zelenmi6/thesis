package cameras;

public class Hero4Black extends AbstractCamera {

	public enum Hero4BlackFieldOfView implements FieldOfViewInterface {
		WIDE_4x3, MEDIUM_4X3, NARROW_4X3, WIDE_17X9, MEDIUM_17X9, NARROW_1_X9, WIDE_16X9, MEDIUM_16X9, NARROW_16X9;
	}
	
	public Hero4Black(double initialFovVertical, double initialFovHorizontal, int initialFps) {
		super(initialFovVertical, initialFovHorizontal, initialFps);
		this.cameraName = "HERO4+ Black Edition";
	}
	
	public Hero4Black(FieldOfViewInterface initialFieldOfView, int initialFps) throws Exception {
		super(initialFieldOfView, initialFps);
		this.cameraName = "HERO4+ Black Edition";
	}

	@Override
	public void setFieldOfView(FieldOfViewInterface fieldOfView) throws Exception {
		if (fieldOfView instanceof Hero4BlackFieldOfView == false) {
			throw new Exception("Enum needs to be of type Hero4PlusBlackFieldOfView");
		}
		Hero4BlackFieldOfView fov = (Hero4BlackFieldOfView)fieldOfView;
		if (fov == Hero4BlackFieldOfView.WIDE_16X9) {
			this.fovVertical = Math.toRadians(69.5);
			this.fovHorizontal = Math.toRadians(118.2);
		} else {
			throw new Exception("Not implemented yet.");
		}
	}
	
}
