package fr.skyforce77.prn.swing;

import java.awt.Dimension;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import fr.skyforce77.prn.PRN;

public class SettingsFrame extends JFrame{

	private static final long serialVersionUID = -8346087794455011856L;
	
	public final RSSPanel rsspanel = new RSSPanel();
	public final SettingsPanel settingspanel = new SettingsPanel();

	public SettingsFrame() {
		JTabbedPane tabbedpane = new JTabbedPane();
		
		setTitle("Settings");
		setLocationRelativeTo(null);
		setIconImage(new ImageIcon(new File(PRN.getDirectory(), "/resources/textures/rss.png").getPath()).getImage());
		setMinimumSize(new Dimension(300, 200));
		setSize(new Dimension(400, 500));
		setEnabled(true);
		
		tabbedpane.addTab("RSS", rsspanel);
		tabbedpane.addTab("Global", settingspanel);
		
		add(tabbedpane);
	}
}
