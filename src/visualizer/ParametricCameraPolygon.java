package visualizer;

import java.awt.Graphics2D;

import javax.vecmath.Vector3d;

import geometry.Calculations;

public class ParametricCameraPolygon extends CameraPolygon {
	private Vector3d [] corners = null;
	
	@Override
	protected void drawPolygon(Graphics2D g2) {
		if (corners == null) {
			return;
		}
		boolean cameraWithinPolygon = Calculations.polygonContainsPoint(cameraPosition, corners);
		Integer leftOutermost = null;
		Integer rightOutermost = null;
		if (!cameraWithinPolygon) {
			try {
				leftOutermost = Calculations.findOutermostLeftPoint(corners, cameraPosition);
				rightOutermost = Calculations.findOutermostRightPoint(corners, cameraPosition);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		offsetPoints(corners, xOffset, yOffset);
		
		// If the camera is not within the polygon on the ground
		if(!cameraWithinPolygon && drawBoundingPolygon) {
			drawBoundingPolygon(g2, corners, leftOutermost, rightOutermost);
		}
		
		// Draw points vector-plane intersection points
		drawPoints(corners, g2);
		
		// Draw vectors connecting the camera and the vector-plane intersection points
		drawOriginPointLine(g2, corners);
		
		// Draw polygon on the ground
		drawLine(g2, corners[0], corners[1]);
		drawLine(g2, corners[1], corners[2]);
		drawLine(g2, corners[2], corners[3]);
		drawLine(g2, corners[3], corners[0]);
	}
	
	public void setCorners(Vector3d [] corners) {
		this.corners = corners;
		repaint();
	}
}
