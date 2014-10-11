package fr.skyforce77.prn.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import org.gnome.gtk.Gtk;
import org.gnome.gtk.StatusIcon;
import org.gnome.notify.Notify;

import fr.skyforce77.prn.PRN;

public class GnomeServer extends LibnotifyServer{

	private StatusIcon icon;
	
	public GnomeServer(final String title, String[] args) {
		super(title, args);
		
		Gtk.init(args);
		Notify.init(title);
		icon = new StatusIcon();
		
		new Thread("GTK-Main") {
			public void run() {
				icon.setTooltipText(title);
				
				icon.connect(new StatusIcon.PopupMenu() {
					@Override
					public void onPopupMenu(StatusIcon arg0, int arg1, int arg2) {
						int i = JOptionPane.showConfirmDialog(null, "Do you want to quit "+title+"?", "Quit", JOptionPane.YES_NO_OPTION);
						if(i == JOptionPane.YES_OPTION)
							System.exit(0);
					}
				});

				icon.connect(new StatusIcon.Activate() {
					@Override
					public void onActivate(StatusIcon arg0) {
						PRN.frame.setVisible(true);
					}
				});
				Gtk.setProgramName(title);
				Gtk.main();
			};
		}.start();
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
		icon.setFromFile(new File(PRN.getDirectory(), "resources/textures/"+ico+".png").getPath());
	}
}
