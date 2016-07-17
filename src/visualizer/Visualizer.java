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
import visualizer.CameraTrapezoid.CameraParameter;
import visualizer.CameraTrapezoid.Option;

public class Visualizer extends JPanel implements MouseWheelListener {
	
	private final int WINDOW_WIDTH = 1024;
	private final int WINDOW_HEIGHT = 768;
	
	private SliderSet zoomSlider;
	
	
	public Visualizer() {
		CameraTrapezoid cameraTrapezoid = new CameraTrapezoid();
		cameraTrapezoid.addMouseWheelListener(this);
		setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		this.setLayout(new BorderLayout());
		this.add(cameraTrapezoid, BorderLayout.CENTER);
		zoomSlider = new SliderSet(CameraParameter.ZOOM, CameraTesting.ZOOM, -100, 100, SwingConstants.VERTICAL, cameraTrapezoid);
		this.add(zoomSlider, BorderLayout.WEST);
		this.add(addRollAltitudePanel(cameraTrapezoid), BorderLayout.EAST);
		this.add(addHeadingPitchPanel(cameraTrapezoid), BorderLayout.SOUTH);
		this.add(addOptionsBar(cameraTrapezoid), BorderLayout.NORTH);
	}
	
	private JPanel addRollAltitudePanel(CameraTrapezoid cameraTrapezoid) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 2));
		panel.add(new SliderSet(CameraParameter.ALTITUDE, CameraTesting.ALTITUDE, 0, 100, SwingConstants.VERTICAL, cameraTrapezoid));
		panel.add(new SliderSet(CameraParameter.ROLL, CameraTesting.ROLL, -180, 180, SwingConstants.VERTICAL, cameraTrapezoid));
		return panel;
	}
	
	private JPanel addHeadingPitchPanel(CameraTrapezoid cameraTrapezoid) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 1));
		panel.add(new SliderSet(CameraParameter.HEADING, CameraTesting.HEADING, -180, 180, SwingConstants.HORIZONTAL, cameraTrapezoid));
		panel.add(new SliderSet(CameraParameter.PITCH, CameraTesting.PITCH, -180, 180, SwingConstants.HORIZONTAL, cameraTrapezoid));
		return panel;
	}
	
	private JPanel addOptionsBar(CameraTrapezoid cameraTrapezoid) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		panel.add(new OptionsItem(Option.BOUNDING_POLYGON, cameraTrapezoid));
		
		return panel;
	}
	
	private void addOptions() {
		
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		zoomSlider.sliderAdjusted(e.getWheelRotation());
	}
	

}
















