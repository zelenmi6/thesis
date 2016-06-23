package database;

import loaders.PictureTelemetry;

public class PostGISStringBuilder {
	
	private StringBuilder sb = new StringBuilder();
	
	public PostGISStringBuilder() {
		
	}
	
	public String pointGeometry3D(double x, double y, double z) {
		sb.setLength(0);
		sb.append("POINT(").append(x).append(" ").append(y).append(" ").append(z).append(")");
		return sb.toString();
	}
	
	public String pointGeometry3D(PictureTelemetry telemetry) {
		return pointGeometry3D(telemetry.coordinates.x, telemetry.coordinates.y, telemetry.coordinates.z);
	}
}
