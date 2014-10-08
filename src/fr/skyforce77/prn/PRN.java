package fr.skyforce77.prn;

import it.sauronsoftware.feed4j.FeedParser;
import it.sauronsoftware.feed4j.bean.Feed;
import it.sauronsoftware.feed4j.bean.FeedItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
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
	private final static ArrayList<FeedItemInfo> torender = new ArrayList<>();

	public static void main(final String[] args) {
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
		
		frame = new SettingsFrame();

		new Thread("GTK-Main") {
			public void run() {
				Gtk.init(args);
				Notify.init(title);
				icon = new StatusIcon();
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
						frame.setVisible(true);
					}
				});
				try {
					File f = new File(getDirectory(), "ressources/textures/rss.png");
					icon.setFromPixbuf(new Pixbuf(f.getPath()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				Gtk.main();
			};
		}.start();

		new Thread("RSS-Update") {
			@SuppressWarnings("unchecked")
			public void run() {
				long wait = 0;
				while(!Thread.interrupted()) {
					for(RSSEntry entry : (CopyOnWriteArrayList<RSSEntry>)DataBase.getValue("feeds")) {
						try {
							URL url = new URL(entry.getURL());
							Feed feed = FeedParser.parse(url);
							Long lastupdate = (Long)(DataBase.getValue(entry.getURL()));
							System.out.println("Updating "+feed.getHeader().getTitle()+"...");
							int items = feed.getItemCount();
							for (int i = 0; i < items; i++) {
								FeedItem item = feed.getItem(i);
								FeedItemInfo itemi = new FeedItemInfo(feed, item);
								if(lastupdate > itemi.getPubDate().getTime()) {
									if(lastupdate > itemi.getModDate().getTime()) {
										i = items;
									} else {
										itemi.setUpdate(true);
										torender.add(itemi);
										System.out.println("Found item update: "+item.getTitle());
									}
								} else {
									torender.add(itemi);
									System.out.println("Found new item: "+item.getTitle());
								}
							}
							DataBase.setValue(entry.getURL(), new Date().getTime());
							DataBase.save();
							System.out.println("Up to date");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					try {
						while(wait < frame.panel.updatetime.getValue()*60000) {
							Thread.sleep(10l);
							wait+=10;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
		
		new Thread("Notify-Timed") {
			public void run() {
				while(!Thread.interrupted()) {
					if(torender.size() > 0 && torender.get(0) != null) {
						PRN.notify(torender.get(0));
						torender.remove(0);
						try {
							Thread.sleep(10000l);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(100l);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
	}
	
	public static void notify(FeedItemInfo itemi) {
		Notification not = null;
		if(itemi.isUpdate()) {
			not = new Notification(itemi.getFeed().getHeader().getTitle(), "<b>[Item update]</b>\n\n"+itemi.getItem().getTitle()+"\n\n"+itemi.getItem().getLink(), "");
		} else {
			not = new Notification(itemi.getFeed().getHeader().getTitle(), "<b>"+itemi.getItem().getTitle()+"</b>\n\n"+itemi.getItem().getDescriptionAsText()+"\n\n"+itemi.getItem().getLink(), "");
		}
		not.setIcon(icon.getPixbuf());
		not.show();
		System.out.println("Displayed item: "+itemi.getItem().getTitle());
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
	
	public static String getOS() {
		String OS = System.getProperty("os.name").toUpperCase();
		if (OS.contains("WIN"))
			return "windows";
		else if (OS.contains("MAC"))
			return "mac";
		else if (OS.contains("NUX"))
			return "linux";
		else
			return "wtf";
	}
	
}
