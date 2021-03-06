package visualizer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.List;

import javax.swing.JPanel;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import constants.CameraTesting;
import geometry.Calculations;
import geometry.CameraCalculator;
import geometry.ConvexHull;

public class CameraPolygon extends JPanel implements MouseMotionListener {
	
	public enum CameraParameter {
		HEADING, ROLL, PITCH, ALTITUDE, ZOOM;
	}
	
	public enum Option {
		BOUNDING_POLYGON, ORIGIN_1, ORIGIN_2, ORIGIN_3, ORIGIN_4;
	}
	
	private int mouseX, mouseY;
	private double heading = CameraTesting.HEADING;
	private double roll = CameraTesting.ROLL;
	private double pitch = CameraTesting.PITCH; 
	private double altitude = CameraTesting.ALTITUDE;
	protected double zoom = CameraTesting.ZOOM;
	protected double xOffset = 0;
	protected double yOffset = 0;
	protected boolean drawBoundingPolygon = false;
	private boolean [] lineOriginPoint = new boolean[4]; // = false
	
	private Point mousePt;
	protected final Vector2d cameraPosition = new Vector2d(0, 0);
	private Vector3d origin = new Vector3d(); // pre-alocate
	
	
	public CameraPolygon() {
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
		drawPolygon(g2);
		drawPoint(g2);
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
	
	protected double translateCenterToX(double x) {
		double windowWidth = this.getSize().getWidth();
		return x + windowWidth / 2;
	}
	
	protected double translateCenterToY(double y) {
		double windowHeight = this.getSize().getHeight();
		return y + windowHeight / 2;
	}
	
	private void drawAxes(Graphics2D g2) {
		double windowWidth = this.getSize().getWidth();
		double windowHeight = this.getSize().getHeight();
		g2.draw(new Line2D.Double(windowWidth/2 + xOffset*zoom, 0, windowWidth/2 + xOffset*zoom, windowHeight));
		g2.draw(new Line2D.Double(0, windowHeight/2 + yOffset*zoom, windowWidth, windowHeight/2 + yOffset*zoom));
	}
	
	protected void drawPolygon(Graphics2D g2) {
		Vector3d [] corners = CameraCalculator.getBoundingPolygon(CameraTesting.FOVh, CameraTesting.FOVv, altitude,
				roll, pitch, heading);
		
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
		// We have to add the are with potentional objects in the air
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
	protected void drawLine(Graphics2D g2, Vector3d pointA, Vector3d pointB) {
		g2.draw(new Line2D.Double(pointA.x, pointA.y,
				pointB.x, pointB.y));
	}
	
	protected void offsetPoints(Vector3d [] points, double xOffset, double yOffset) {
		for (Vector3d point : points) {
			point.x += xOffset;
			point.y += yOffset;
			
			point.x = translateCenterToX(point.x * zoom);
			point.y = translateCenterToY(point.y * zoom);
		}
	}
	
	protected void drawPoints(Vector3d [] corners, Graphics2D g2) {
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
	
	protected void drawPoint(Graphics2D g2) {
		
	}
	
	public void optionChanged(Option option) {
		if (option == Option.BOUNDING_POLYGON) {
			drawBoundingPolygon = !drawBoundingPolygon;
		} else if (option == Option.ORIGIN_1) {
			lineOriginPoint[0] = !lineOriginPoint[0];
		}  else if (option == Option.ORIGIN_2) {
			lineOriginPoint[1] = !lineOriginPoint[1];
		}  else if (option == Option.ORIGIN_3) {
			lineOriginPoint[2] = !lineOriginPoint[2];
		}  else if (option == Option.ORIGIN_4) {
			lineOriginPoint[3] = !lineOriginPoint[3];
		}
		repaint();
	}
	
	protected void drawOriginPointLine(Graphics2D g2, Vector3d[] points) {
		centerOrigin();
		
		Stroke origStroke = g2.getStroke();
		Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
		g2.setColor(Color.BLUE);
		g2.setStroke(dashed);
		for (int i = 0; i < lineOriginPoint.length; i ++) {
			if (lineOriginPoint[i]) {
				drawLine(g2, origin, points[i]);
			}
		}
		g2.setColor(Color.BLACK);
		g2.setStroke(origStroke);
	}
	
	protected void drawBoundingPolygon(Graphics2D g2, Vector3d[] points, int leftOutermostIdx, int rightOutermostIdx) {
		try {
			centerOrigin();
			
			Vector3d[] pointArray = copyArrayAndPoint(points, origin); 
			pointArray = ConvexHull.convex_hull(pointArray);
//			for (Vector3d point: pointArray)
//				System.out.println(point);
//			System.out.println("---------------------");
			
			g2.setColor(Color.RED);
			Stroke origStroke = g2.getStroke();
			g2.setStroke(new BasicStroke(5));
			
			for (int i = 0; i < pointArray.length; i ++) {
				drawLine(g2, pointArray[i], pointArray[(i+1)%pointArray.length]);
			}
			
			g2.setColor(Color.BLACK);
			g2.setStroke(origStroke);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Copies the contents of one array along with another vector into a new array
	 * @param array Array to be copied
	 * @param point Vector to be added to the new array
	 * @return New array with elements from the original array and the new one
	 */
	private Vector3d [] copyArrayAndPoint(Vector3d [] array, Vector3d point) {
		Vector3d [] newArray = new Vector3d[array.length+1];
		for (int i = 0; i < array.length; i ++) {
			newArray[i] = new Vector3d(array[i]);
		}
		newArray[array.length] = new Vector3d(point);
		return newArray;
	}
	
	/**
	 * Sets the coordinates of the origin object to where the x and y axes should cross
	 */
	private void centerOrigin() {
		double windowWidth = this.getSize().getWidth();
		double windowHeight = this.getSize().getHeight();
		double originX = windowWidth/2 + xOffset*zoom;
		double originY = windowHeight/2 + yOffset*zoom;
		origin.x = originX;
		origin.y = originY;
	}
	
}
















