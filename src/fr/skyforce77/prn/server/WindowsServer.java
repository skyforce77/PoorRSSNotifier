package fr.skyforce77.prn.server;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import fr.skyforce77.prn.PRN;
import fr.skyforce77.prn.notify.Notification;

public class WindowsServer extends PRNServer{

	private TrayIcon icon;
	
	public WindowsServer(String title, String[] args) {
		super(title, args);
		
		if(!SystemTray.isSupported()) {
			JOptionPane.showMessageDialog(null, "SystemTray not supported\n"+title+" can't start", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Image i = new ImageIcon(PRN.class.getResource("/resources/textures/rss.png")).getImage();
		
		PopupMenu menu = new PopupMenu(title);
		MenuItem quit = new MenuItem("Quit");
		MenuItem settings = new MenuItem("Settings");
		
		quit.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		settings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PRN.frame.setVisible(true);
			}
		});
		
		menu.add(settings);
		menu.add(quit);
		
		icon = new TrayIcon(i, title, menu);
		icon.setImageAutoSize(true);
		
		try {
			SystemTray.getSystemTray().add(icon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setIcon(String ico) {
		if(!new File(PRN.getDirectory(), "resources/textures/"+ico+".png").exists()) {
			try {
				File tdir = new File(PRN.getDirectory(), "resources/textures/");
				tdir.mkdirs();
				File ic = new File(tdir, ico+".png");
				ic.createNewFile();
				InputStream in = PRN.class.getResourceAsStream("/resources/textures/"+ico+".png");
				OutputStream out = new FileOutputStream(ic);
				byte[] buffer = new byte[1024];
				int len = in.read(buffer);
				while (len != -1) {
				    out.write(buffer, 0, len);
				    len = in.read(buffer);
				}
				in.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		icon.setImage(new ImageIcon(new File(PRN.getDirectory(), "resources/textures/"+ico+".png").getPath()).getImage());
	}
	
	@Override
	public void show(Notification notif) {
		//TODO
	}
	
}
