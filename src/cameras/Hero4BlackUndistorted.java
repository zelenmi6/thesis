package cameras;

import cameras.Hero4Black.Hero4BlackFieldOfView;

public class Hero4BlackUndistorted extends AbstractCamera {
	public enum Hero4BlackUndistortedFieldOfView implements FieldOfViewInterface {
		WIDE_4x3, MEDIUM_4X3, NARROW_4X3, WIDE_17X9, MEDIUM_17X9, NARROW_1_X9, WIDE_16X9, MEDIUM_16X9, NARROW_16X9;
	}
	
	public Hero4BlackUndistorted(double initialFovVertical, double initialFovHorizontal, int initialFps) {
		super(initialFovVertical, initialFovHorizontal, initialFps);
		this.cameraName = "HERO4+ Black Edition (Undistorted image)";
	}
	
	public Hero4BlackUndistorted(FieldOfViewInterface initialFieldOfView, int initialFps) throws Exception {
		super(initialFieldOfView, initialFps);
		this.cameraName = "HERO4+ Black Edition (Undistorted image)";
	}
	
	@Override
	public void setFieldOfView(FieldOfViewInterface fieldOfView) throws Exception {
		if (fieldOfView instanceof Hero4BlackUndistortedFieldOfView == false) {
			throw new Exception("Enum needs to be of type Hero4PlusBlackFieldOfView");
		}
		Hero4BlackUndistortedFieldOfView fov = (Hero4BlackUndistortedFieldOfView)fieldOfView;
		if (fov == Hero4BlackUndistortedFieldOfView.WIDE_16X9) {
			this.fovVertical = Math.toRadians(63.4);
			this.fovHorizontal = Math.toRadians(95.4);
		} else {
			throw new Exception("Not implemented yet.");
		}
	}

}
