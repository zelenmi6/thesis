package visualizer;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import visualizer.CameraPolygon.Option;

public class OptionsItem extends JPanel implements ItemListener {
	
	Option type;
	CameraPolygon cameraPolygon;
	
	public OptionsItem(Option optionType, CameraPolygon cameraPolygon) {
		setLayout(new GridLayout(1, 2));
		type = optionType;
		this.cameraPolygon = cameraPolygon;
		JLabel label = new JLabel(type.toString());
		JCheckBox checkBox = new JCheckBox();
		checkBox.addItemListener(this);
		
		this.add(label);
		this.add(checkBox);
	}

	public void itemStateChanged(ItemEvent e) {
		cameraPolygon.optionChanged(type);
	}

}
