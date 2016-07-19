package visualizer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import visualizer.CameraPolygon.CameraParameter;

public class SliderSet extends JPanel {
	
	private JLabel nameLabel, valueLabel;
	private CameraPolygon cameraPolygon;
	private CameraParameter name;
	
	private JSlider slider;
	
	
	public SliderSet(CameraParameter parameterType, double defaultValue, int minValue, int maxValue,
			int orientation, CameraPolygon cameraPolygonParam) {
		int sliderInitialValue = (int)defaultValue;
		if (parameterType == CameraParameter.HEADING || 
				parameterType == CameraParameter.ROLL || 
				parameterType == CameraParameter.PITCH) {
					sliderInitialValue = (int)Math.round(Math.toDegrees(defaultValue));
				}
		
		this.name = parameterType;
		this.cameraPolygon = cameraPolygonParam;
		setLayout(new GridBagLayout());
		
		nameLabel = new JLabel(name.toString());
		valueLabel = new JLabel(Integer.toString(sliderInitialValue));
		
		slider = new JSlider(orientation, minValue, maxValue, sliderInitialValue);
		slider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JSlider slider = (JSlider)e.getSource();
				valueLabel.setText(Integer.toString(slider.getValue()));
				cameraPolygon.valueChanged(name, slider.getValue());
			}
			
		});
		
		if (orientation == SwingConstants.VERTICAL) {
			nameLabel.setUI(new VerticalLabelUI(true));
			GridBagConstraints c = new GridBagConstraints();
			
			nameLabel.setVerticalAlignment(SwingConstants.TOP);
			
//			c.fill = GridBagConstraints.VERTICAL;
			c.insets = new Insets(10, 10, 10, 10);
			c.gridx = 0;
			c.gridy = 0;
			c.weighty = 20;
			c.weightx = 1;
			add(nameLabel, c);
			
			c.gridx = 0;
			c.gridy = 1;
			c.weighty = 70;
			c.weightx = 1;
			c.fill = GridBagConstraints.VERTICAL;
			add(slider, c);
			
			c.gridx = 0;
			c.gridy = 2;
			c.weighty = 10;
			c.weightx = 1;
			add(valueLabel, c);
		} else {
			GridBagConstraints c = new GridBagConstraints();
//			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(10, 10, 10, 10);
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 20;
			add(nameLabel, c);
			
			c.gridx = 1;
			c.gridy = 0;
			c.weightx = 70;
			c.fill = GridBagConstraints.HORIZONTAL;
			add(slider, c);
			
			c.gridx = 2;
			c.gridy = 0;
			c.weightx = 10;
			add(valueLabel, c);
		}
		
	}
	public void sliderAdjusted(int value) {
		slider.setValue(slider.getValue() - value);
	}
}

















