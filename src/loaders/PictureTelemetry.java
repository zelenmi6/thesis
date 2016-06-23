package loaders;

import javax.vecmath.Vector3d;

public class PictureTelemetry {
	public Vector3d coordinates;
	public double heading;
	public double roll;
	public double pitch;
	
	public PictureTelemetry(Vector3d coordinates, double heading, double roll, double pitch) {
		super();
		this.coordinates = coordinates;
		this.heading = heading;
		this.roll = roll;
		this.pitch = pitch;
	}
}
