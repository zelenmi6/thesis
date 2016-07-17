package visualizer;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import visualizer.CameraTrapezoid.Option;

public class OptionsItem extends JPanel implements ItemListener {
	
	Option type;
	CameraTrapezoid cameraTrapezoid;
	
	public OptionsItem(Option optionType, CameraTrapezoid cameraTrapezoid) {
		setLayout(new GridLayout(1, 2));
		type = optionType;
		this.cameraTrapezoid = cameraTrapezoid;
		JLabel label = new JLabel(type.toString());
		JCheckBox checkBox = new JCheckBox();
		checkBox.addItemListener(this);
		
		this.add(label);
		this.add(checkBox);
	}

	public void itemStateChanged(ItemEvent e) {
		cameraTrapezoid.optionChanged(type);
	}

}
