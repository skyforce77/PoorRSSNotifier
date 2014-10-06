package fr.skyforce77.prn;

import it.sauronsoftware.feed4j.FeedParser;
import it.sauronsoftware.feed4j.bean.Feed;
import it.sauronsoftware.feed4j.bean.FeedItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JOptionPane;

import org.gnome.gdk.Pixbuf;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.StatusIcon;
import org.gnome.notify.Notification;
import org.gnome.notify.Notify;

import fr.skyforce77.prn.save.DataBase;
import fr.skyforce77.prn.save.RSSEntry;
import fr.skyforce77.prn.swing.SettingsFrame;

public class PRN {

	public static StatusIcon icon;
	public static String title = "PoorRSSNotifier";
	public static SettingsFrame frame;

	public static void main(String[] args) {
		if(!getDirectory().exists()) {
			getDirectory().mkdirs();
		}
		DataBase.load();
		if(DataBase.getValue("feeds") == null) {
			CopyOnWriteArrayList<RSSEntry> entries = new CopyOnWriteArrayList<RSSEntry>();
			DataBase.setValue("feeds", entries);
		}
		if(!new File(getDirectory(), "ressources/textures/").exists()) {
			try {
				File tdir = new File(getDirectory(), "ressources/textures/");
				tdir.mkdirs();
				File ic = new File(tdir, "rss.png");
				ic.createNewFile();
				InputStream in = PRN.class.getResourceAsStream("/ressources/textures/rss.png");
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
		Gtk.init(args);
		Notify.init(title);
		icon = new StatusIcon();
		frame = new SettingsFrame();

		icon.connect(new StatusIcon.PopupMenu() {
			@Override
			public void onPopupMenu(StatusIcon arg0, int arg1, int arg2) {
				int i = JOptionPane.showConfirmDialog(null, "Voulez vous vraiment quitter "+title+"?", "Quitter", JOptionPane.YES_NO_OPTION);
				if(i == JOptionPane.YES_OPTION)
					System.exit(0);
			}
		});

		icon.connect(new StatusIcon.Activate() {
			@Override
			public void onActivate(StatusIcon arg0) {
				frame.setVisible(true);
			}
		});

		new Thread("GTK-Main") {
			public void run() {
				try {
					File f = new File(getDirectory(), "ressources/textures/rss.png");
					icon.setFromPixbuf(new Pixbuf(f.getPath()));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				icon.setTooltipText(title);
				Gtk.main();
			};
		}.start();

		new Thread("Notify-Main") {
			@SuppressWarnings("unchecked")
			public void run() {
				while(!Thread.interrupted()) {
					for(RSSEntry entry : (CopyOnWriteArrayList<RSSEntry>)DataBase.getValue("feeds")) {
						try {
							URL url = new URL(entry.getURL());
							Feed feed = FeedParser.parse(url);
							System.out.println("Updating "+feed.getHeader().getTitle()+"...");
							int items = feed.getItemCount();
							for (int i = 0; i < items; i++) {
								FeedItem item = feed.getItem(i);
								if((DataBase.getValue(entry.getURL()) != null && DataBase.getValue(entry.getURL()).equals(item.getGUID()))
										|| DataBase.hasPermission(item.getGUID())) {
									i = items;
								} else {
									PRN.notify(feed, item);
									try {
										Thread.sleep(10000l);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
							DataBase.setValue(entry.getURL(), feed.getItem(0).getGUID());
							DataBase.save();
							System.out.println("Updated");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(600000l);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}
	
	public static void notify(Feed feed, FeedItem item) {
		Notification not = new Notification(feed.getHeader().getTitle(), "<b>"+item.getTitle()+"</b>\n\n"+item.getDescriptionAsText()+"\n\n"+item.getLink(), "");
		not.setIcon(icon.getPixbuf());
		not.setTimeout(Notification.NOTIFY_EXPIRES_NEVER);
		not.show();
	}

	public static File getDirectory() {
		String OS = System.getProperty("os.name").toUpperCase();
		if (OS.contains("WIN"))
			return new File(System.getenv("APPDATA"), "/.PoorRSSNotifier");
		else if (OS.contains("MAC"))
			return new File(System.getProperty("user.home") + "/Library/Application "
					+ "Support", "/.PoorRSSNotifier");
		else if (OS.contains("NUX"))
			return new File(System.getProperty("user.home"), "/.PoorRSSNotifier");
		return new File(System.getProperty("user.dir"), "/.PoorRSSNotifier");
	}
}
