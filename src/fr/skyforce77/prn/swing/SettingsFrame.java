package fr.skyforce77.prn.swing;

import java.awt.Dimension;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import fr.skyforce77.prn.PRN;

public class SettingsFrame extends JFrame{

	private static final long serialVersionUID = -8346087794455011856L;

	public SettingsFrame() {
		setTitle("Options");
		setLocationRelativeTo(null);
		setIconImage(new ImageIcon(new File(PRN.getDirectory(), "/ressources/textures/rss.png").getPath()).getImage());
		setMinimumSize(new Dimension(200, 100));
		setSize(new Dimension(300, 200));
		setEnabled(true);
		
		add(new SettingsPanel());
	}
}
