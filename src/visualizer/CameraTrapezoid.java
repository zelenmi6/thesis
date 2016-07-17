package visualizer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;
import javax.vecmath.Vector3d;

import constants.CameraTesting;
import geometry.CameraCalculator;

public class CameraTrapezoid extends JPanel implements MouseMotionListener {
	
	public enum CameraParameter {
		HEADING, ROLL, PITCH, ALTITUDE, ZOOM;
	}
	
	public enum Option {
		BOUNDING_POLYGON
	}
	
	private int mouseX, mouseY;
	private double heading = CameraTesting.HEADING;
	private double roll = CameraTesting.ROLL;
	private double pitch = CameraTesting.PITCH; 
	private double altitude = CameraTesting.ALTITUDE;
	private double zoom = CameraTesting.ZOOM;
	private double xOffset = 0;
	private double yOffset = 0;
	
	private Point mousePt;
	
	
	public CameraTrapezoid() {
		addMouseMotionListener(this);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mousePt = e.getPoint();
			}
		});
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		setBackground(Color.WHITE);
		g2.drawString("X: " + (translateXToCenter(mouseX) / zoom - xOffset) + " , Y: " + (translateYToCenter(mouseY) / zoom - yOffset), mouseX, mouseY);
		drawAxes(g2);
		drawTrapezoid(g2);
	}
	
	public void valueChanged(CameraParameter parameter, int value) {
		if (parameter == CameraParameter.HEADING) {
			heading = adjustAngle(value);
		} else if (parameter == CameraParameter.ROLL) {
			roll = adjustAngle(value);
		} else if (parameter == CameraParameter.PITCH) {
			pitch = adjustAngle(value);
		} if (parameter == CameraParameter.ALTITUDE) {
			altitude = value;
		} if (parameter == CameraParameter.ZOOM) {
			adjustZoom(value);
		}
		repaint();
	}
	
	private void adjustZoom(int value) {
		if (value < 0) {
			zoom = -1 * 1. / value;
		} else if (value == 0) {
			zoom = 1;
		} else {
			zoom = value;
		}
	}
	
	private double adjustAngle(int angle) {
		if (angle < 0)
			angle += 360;
		return Math.toRadians(angle);
	}

	public void mouseDragged(MouseEvent e) {
		int dx = e.getX() - mousePt.x;
		int dy = e.getY() - mousePt.y;
		xOffset += dx / zoom;
		yOffset += dy / zoom;
		mousePt = e.getPoint();
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		repaint();
	}
	
	private double translateXToCenter(double x) {
		double windowWidth = this.getSize().getWidth();
		return x - windowWidth / 2;
	}
	
	private double translateYToCenter(double y) {
		double windowHeight = this.getSize().getHeight();
		return y - windowHeight / 2;
	}
	
	private double translateCenterToX(double x) {
		double windowWidth = this.getSize().getWidth();
		return x + windowWidth / 2;
	}
	
	private double translateCenterToY(double y) {
		double windowHeight = this.getSize().getHeight();
		return y + windowHeight / 2;
	}
	
	private void drawAxes(Graphics2D g2) {
		double windowWidth = this.getSize().getWidth();
		double windowHeight = this.getSize().getHeight();
		g2.draw(new Line2D.Double(windowWidth/2 + xOffset*zoom, 0, windowWidth/2 + xOffset*zoom, windowHeight));
		g2.draw(new Line2D.Double(0, windowHeight/2 + yOffset*zoom, windowWidth, windowHeight/2 + yOffset*zoom));
	}
	
	private void drawTrapezoid(Graphics2D g2) {
		Vector3d [] corners = CameraCalculator.getBoundingPolygon(CameraTesting.FOVh, CameraTesting.FOVv, altitude,
				roll, pitch, heading);
		offsetPoints(corners, xOffset, yOffset);
		
		
		drawPoints(corners, g2);
		drawLine(g2, corners[0], corners[1]);
		drawLine(g2, corners[1], corners[2]);
		drawLine(g2, corners[2], corners[3]);
		drawLine(g2, corners[3], corners[0]);
		
		
	}
	private void drawLine(Graphics2D g2, Vector3d pointA, Vector3d pointB) {
		g2.draw(new Line2D.Double(pointA.x, pointA.y,
				pointB.x, pointB.y));
	}
	
	private void offsetPoints(Vector3d [] points, double xOffset, double yOffset) {
		for (Vector3d point : points) {
			point.x += xOffset;
			point.y += yOffset;
			
			point.x = translateCenterToX(point.x * zoom);
			point.y = translateCenterToY(point.y * zoom);
		}
	}
	
	private void drawPoints(Vector3d [] corners, Graphics2D g2) {
		int counter = 1;
		for (Vector3d point : corners) {
			Ellipse2D.Double pointToDraw = new Ellipse2D.Double(point.x - CameraTesting.POINT_SIZE/2,
					point.y - CameraTesting.POINT_SIZE/2, CameraTesting.POINT_SIZE, CameraTesting.POINT_SIZE);
			g2.setColor(Color.RED);
			g2.fill(pointToDraw);
			g2.setColor(Color.BLACK);
			g2.drawString(Integer.toString(counter), (int)(point.x + CameraTesting.POINT_SIZE), 
					(int)(point.y + CameraTesting.POINT_SIZE));
			counter ++;
		}
	}
	
	public void optionChanged(Option option) {
		System.out.println(option.toString());
	}
}
















