package visualizer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import constants.CameraTesting;
import geometry.Calculations;

public class ParametricCameraPolygon extends CameraPolygon {
	private Vector3d [] corners = null;
	private Point2d dronePosition = new Point2d(0, 0);
	private double droneHeadingRad = 0;
	
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
	
	@Override
	protected void drawPoint(Graphics2D g2) {
		///////////////////// OLD DATA SET /////////////////////
//		Point2d towel = new Point2d(-9.170993343994464, 0.038963166074696076);
//		towel.x += xOffset;
//		towel.y += yOffset;
//		towel.x = translateCenterToX(towel.x * zoom);
//		towel.y = translateCenterToY(towel.y * zoom);
		
//		Point2d umbrella1 = new Point2d(-21.479352051090228, -0.6751105523027792);
//		umbrella1.x += xOffset;
//		umbrella1.y += yOffset;
//		umbrella1.x = translateCenterToX(umbrella1.x * zoom);
//		umbrella1.y = translateCenterToY(umbrella1.y * zoom);
//		
//		Point2d umbrella2 = new Point2d(-22.827191865440792, -12.644058580926762);
//		umbrella2.x += xOffset;
//		umbrella2.y += yOffset;
//		umbrella2.x = translateCenterToX(umbrella2.x * zoom);
//		umbrella2.y = translateCenterToY(umbrella2.y * zoom);
		///////////////////// OLD DATA SET END /////////////////////
		
//		Point2d target = new Point2d(-5.153951485404552, -9.721579921488562);
//		target.x += xOffset;
//		target.y += yOffset;
//		target.x = translateCenterToX(target.x * zoom);
//		target.y = translateCenterToY(target.y * zoom);
//		
//		
//		Ellipse2D.Double pointToDraw = new Ellipse2D.Double(target.x - CameraTesting.POINT_SIZE/2,
//				target.y - CameraTesting.POINT_SIZE/2, CameraTesting.POINT_SIZE*2, CameraTesting.POINT_SIZE*2);
//		g2.setColor(Color.BLUE);
//		g2.fill(pointToDraw);
//		g2.setColor(Color.BLACK);
		
		
//		drawPoint(new Point2d(-13.294068549350916, 3.6692342889354252), g2, Color.GREEN);
		
		drawPoint(new Point2d(-5.153951485404552, -9.721579921488562), g2, Color.BLUE);
		drawPoint(dronePosition, g2, Color.DARK_GRAY);
		drawHeading(dronePosition, droneHeadingRad, 40, g2);
		
//		Ellipse2D.Double pointToDraw2 = new Ellipse2D.Double(umbrella1.x - CameraTesting.POINT_SIZE/2,
//				umbrella1.y - CameraTesting.POINT_SIZE/2, CameraTesting.POINT_SIZE*2, CameraTesting.POINT_SIZE*2);
//		g2.setColor(Color.BLUE);
//		g2.fill(pointToDraw2);
//		g2.setColor(Color.BLACK);
//		
//		Ellipse2D.Double pointToDraw3 = new Ellipse2D.Double(umbrella2.x - CameraTesting.POINT_SIZE/2,
//				umbrella2.y - CameraTesting.POINT_SIZE/2, CameraTesting.POINT_SIZE*2, CameraTesting.POINT_SIZE*2);
//		g2.setColor(Color.BLUE);
//		g2.fill(pointToDraw3);
//		g2.setColor(Color.BLACK);
	}
	
	private void drawPoint(Point2d point, Graphics2D g2, java.awt.Color color) {
		point.x += xOffset;
		point.y += yOffset;
		point.x = translateCenterToX(point.x * zoom);
		point.y = translateCenterToY(point.y * zoom);
		
		Ellipse2D.Double pointToDraw = new Ellipse2D.Double(point.x - CameraTesting.POINT_SIZE/2,
				point.y - CameraTesting.POINT_SIZE/2, CameraTesting.POINT_SIZE*2, CameraTesting.POINT_SIZE*2);
		g2.setColor(color);
		g2.fill(pointToDraw);
		g2.setColor(Color.BLACK);
	}
	
	private void drawHeading(Point2d dronePos, double direction, double magnitude, Graphics2D g2/*, java.awt.Color color*/) {
		double startX = dronePos.x;
		double startY = dronePos.y;
//		startY += yOffset;
//		startX += xOffset;
//		startY = translateCenterToY(startY * zoom);
//		startX = translateCenterToX(startX * zoom);
		
		double endY = Math.sin(direction) * magnitude + dronePos.y;
		double endX = Math.cos(direction) * magnitude + dronePos.x;
//		endY += yOffset;
//		endX += xOffset;
//		endY = translateCenterToY(endY * zoom);
//		endX = translateCenterToX(endX * zoom);
		
		g2.draw(new Line2D.Double(startX, startY,
				endX, endY));
	}
	
	public void setCorners(Vector3d [] corners) {
		this.corners = corners;
		repaint();
	}
	
	public void setDronePosition (double x, double y, double headingRad) {
		this.dronePosition = new Point2d(x, y);
		this.droneHeadingRad = headingRad;
	}
}

























