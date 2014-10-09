package fr.skyforce77.prn.swing;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.skyforce77.prn.save.DataBase;

public class SettingsPanel extends JPanel{

	private static final long serialVersionUID = 3861075847304452437L;

	public SettingsPanel instance;
	public GroupLayout layout;
	public JSlider updatetime;
	public JLabel updatetimevalue;
	public JPanel updatepanel;
	public GroupLayout updatepanellayout;
	
	public SettingsPanel() {
		instance = this;
		layout = new GroupLayout(this);
		setLayout(layout);
		
		updatepanel = new JPanel();
		updatepanellayout = new GroupLayout(updatepanel);
		updatepanel.setLayout(updatepanellayout);
		updatepanel.setBorder(BorderFactory.createTitledBorder("Refresh time"));
		
		updatetimevalue = new JLabel("Actual: "+(int)DataBase.getValue("updatetime", 60)+" minutes");
		
		updatetime = new JSlider(0, 360, (int)DataBase.getValue("updatetime", 60));
		updatetime.setMinorTickSpacing(30);
        updatetime.setMajorTickSpacing(60);
        updatetime.setPaintTicks(true);
        updatetime.setPaintLabels(true);
        updatetime.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updatetimevalue.setText("Actual: "+updatetime.getValue()+" minutes");
				DataBase.setValue("updatetime", updatetime.getValue());
				DataBase.save();
			}
		});
		
        updatepanellayout.setVerticalGroup(
        		updatepanellayout.createSequentialGroup()
				.addComponent(updatetimevalue)
				.addComponent(updatetime));

		updatepanellayout.setHorizontalGroup(
				updatepanellayout.createParallelGroup()
				.addComponent(updatetimevalue)
				.addComponent(updatetime));
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(updatepanel));

		layout.setHorizontalGroup(
				layout.createParallelGroup()
				.addComponent(updatepanel));
	}
}
