package database;

import javax.vecmath.Vector2d;

import loaders.PictureTelemetry;

public class PostGISStringBuilder {
	
	private StringBuilder sb = new StringBuilder();
	
	public PostGISStringBuilder() {
		
	}
	
	public String pointGeometry3d(double x, double y, double z) {
		sb.setLength(0);
		sb.append("POINT(").append(x).append(" ").append(y).append(" ").append(z).append(")");
		return sb.toString();
	}
	
	public String pointGeometry3D(PictureTelemetry telemetry) {
		return pointGeometry3d(telemetry.coordinates.x, telemetry.coordinates.y, telemetry.coordinates.z);
	}
	
	public String pointGeometry2d(Vector2d point) {
		return pointGeometry2d(point.x, point.y);
	}
	
	public String pointGeometry2d(double x, double y) {
		sb.setLength(0);
		sb.append("POINT(").append(x).append(" ").append(y).append(")");
		return sb.toString();
	}
	
	public String trapezoidGeometry2d(Vector2d [] trapezoid) {
		sb.setLength(0);
		sb.append("POLYGON((").append(trapezoid[0].x).append(" ").append(trapezoid[0].y).append(", ")
		.append(trapezoid[1].x).append(" ").append(trapezoid[1].y).append(", ")
		.append(trapezoid[2].x).append(" ").append(trapezoid[2].y).append(", ")
		.append(trapezoid[3].x).append(" ").append(trapezoid[3].y).append(", ")
		.append(trapezoid[0].x).append(" ").append(trapezoid[0].y)
		.append("))");
		return sb.toString();
	}
	
	public String triangleGeometry2d(Vector2d [] triangle) {
		sb.setLength(0);
		sb.append("POLYGON((").append(triangle[0].x).append(" ").append(triangle[0].y).append(", ")
		.append(triangle[1].x).append(" ").append(triangle[1].y).append(", ")
		.append(triangle[2].x).append(" ").append(triangle[2].y).append(", ")
		.append(triangle[0].x).append(" ").append(triangle[0].y)
		.append("))");
		return sb.toString();
	}
}

























