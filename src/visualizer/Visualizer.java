package visualizer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import constants.CameraTesting;
import visualizer.CameraPolygon.CameraParameter;
import visualizer.CameraPolygon.Option;

public class Visualizer extends JPanel implements MouseWheelListener {
	
	protected final int WINDOW_WIDTH = 1024;
	protected final int WINDOW_HEIGHT = 768;
	
	protected SliderSet zoomSlider;
	protected CameraPolygon cameraPolygon;
	
	
	public Visualizer() {
		cameraPolygon = new CameraPolygon();
		cameraPolygon.addMouseWheelListener(this);
		setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		this.setLayout(new BorderLayout());
		this.add(cameraPolygon, BorderLayout.CENTER);
		zoomSlider = new SliderSet(CameraParameter.ZOOM, CameraTesting.ZOOM, -100, 100, SwingConstants.VERTICAL, cameraPolygon);
		this.add(zoomSlider, BorderLayout.WEST);
		this.add(addRollAltitudePanel(cameraPolygon), BorderLayout.EAST);
		this.add(addHeadingPitchPanel(cameraPolygon), BorderLayout.SOUTH);
		this.add(addOptionsBar(cameraPolygon), BorderLayout.NORTH);
	}
	
	protected JPanel addRollAltitudePanel(CameraPolygon cameraPolygon) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		panel.add(new SliderSet(CameraParameter.ALTITUDE, CameraTesting.ALTITUDE, 0, 100, SwingConstants.VERTICAL, cameraPolygon));
		panel.add(new SliderSet(CameraParameter.ROLL, CameraTesting.ROLL, -180, 180, SwingConstants.VERTICAL, cameraPolygon));
		return panel;
	}
	
	protected JPanel addHeadingPitchPanel(CameraPolygon cameraPolygon) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1));
		panel.add(new SliderSet(CameraParameter.HEADING, CameraTesting.HEADING, -180, 180, SwingConstants.HORIZONTAL, cameraPolygon));
		panel.add(new SliderSet(CameraParameter.PITCH, CameraTesting.PITCH, -180, 180, SwingConstants.HORIZONTAL, cameraPolygon));
		return panel;
	}
	
	protected JPanel addOptionsBar(CameraPolygon cameraPolygon) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		panel.add(new OptionsItem(Option.BOUNDING_POLYGON, cameraPolygon));
		panel.add(new OptionsItem(Option.ORIGIN_1, cameraPolygon));
		panel.add(new OptionsItem(Option.ORIGIN_2, cameraPolygon));
		panel.add(new OptionsItem(Option.ORIGIN_3, cameraPolygon));
		panel.add(new OptionsItem(Option.ORIGIN_4, cameraPolygon));
		
		return panel;
	}
	
	private void addOptions() {
		
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		zoomSlider.sliderAdjusted(e.getWheelRotation());
	}
	

}
















