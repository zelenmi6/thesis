package loaders;

import javax.vecmath.Vector3d;

public class PictureTelemetry {
	private Vector3d coordinates;
	private double heading;
	private double roll;
	private double pitch;
	
	public PictureTelemetry(Vector3d coordinates, double heading, double roll, double pitch) {
		super();
		this.coordinates = coordinates;
		this.heading = heading;
		this.roll = roll;
		this.pitch = pitch;
	}

	public Vector3d getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Vector3d coordinates) {
		this.coordinates = coordinates;
	}

	public double getHeading() {
		return heading;
	}

	public void setHeading(double heading) {
		this.heading = heading;
	}

	public double getRoll() {
		return roll;
	}

	public void setRoll(double roll) {
		this.roll = roll;
	}

	public double getPitch() {
		return pitch;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}
	
	
}
