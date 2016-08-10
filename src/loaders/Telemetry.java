package loaders;

import javax.vecmath.Vector3d;

public class Telemetry {
	public long milliseconds;
	public Vector3d coordinates;
	public double heading;
	public double roll;
	public double pitch;
	
	public Telemetry(long milliseconds, Vector3d coordinates, double heading, double roll, double pitch) {
		super();
		this.milliseconds = milliseconds;
		this.coordinates = coordinates;
		this.heading = heading;
		this.roll = roll;
		this.pitch = pitch;
	}
}
