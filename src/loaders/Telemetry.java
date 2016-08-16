package loaders;

import java.sql.Timestamp;

import javax.vecmath.Vector3d;

public class Telemetry {
	public Timestamp timestamp;
	public Vector3d coordinates;
	public double heading;
	public double roll;
	public double pitch;
	
	public Telemetry() {
		
	}
	
	public Telemetry(Timestamp timestamp, Vector3d coordinates, double roll, double pitch, double heading) {
		super();
		this.timestamp = timestamp;
		this.coordinates = coordinates;
		this.heading = heading;
		this.roll = roll;
		this.pitch = pitch;
	}
	
//	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Timestamp: ").append(timestamp.getTime()).append(", coordinates: ").append(coordinates.toString())
		.append(", heading: ").append(heading).append(", roll: ").append(roll).append(", pitch: ").append(pitch);
		return sb.toString();
	}
}
