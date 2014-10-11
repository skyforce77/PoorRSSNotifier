package fr.skyforce77.prn;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import fr.skyforce77.prn.notify.Notification;
import fr.skyforce77.prn.rss.FeedItemInfo;
import fr.skyforce77.prn.save.DataBase;
import fr.skyforce77.prn.save.RSSEntry;
import fr.skyforce77.prn.server.GnomeServer;
import fr.skyforce77.prn.server.LibnotifyServer;
import fr.skyforce77.prn.server.LinuxServer;
import fr.skyforce77.prn.server.PRNServer;
import fr.skyforce77.prn.server.WindowsServer;
import fr.skyforce77.prn.swing.SettingsFrame;

public class PRN {

	public static String title = "PoorRSSNotifier";
	public static PRNServer server;
	public static SettingsFrame frame;

	public static void main(final String[] args) {
		if(!getDirectory().exists()) {
			getDirectory().mkdirs();
		}
		
		DataBase.load();
		
		if(DataBase.getValue("feeds") == null) {
			CopyOnWriteArrayList<RSSEntry> entries = new CopyOnWriteArrayList<RSSEntry>();
			DataBase.setValue("feeds", entries);
		}

		initServer(args);
		frame = new SettingsFrame();
	}

	public static void initServer(String[] args) {
		String command = Arrays.toString(args).toLowerCase();
		if(getOS().equals("linux")) {
			if(command.contains("--gtk") || command.contains("-g")) {
				server = new GnomeServer(PRN.title, args);
			} else if(command.contains("--libnotify") || command.contains("-l")) {
				server = new LibnotifyServer(PRN.title, args);
			} else if(command.contains("--windows") || command.contains("-w")) {
				server = new WindowsServer(PRN.title, args);
			} else {
				server = new LinuxServer(PRN.title, args);
			}
		} else {
			server = new WindowsServer(PRN.title, args);
		}
		server.startService();
	}

	public static void notify(FeedItemInfo itemi) {
		Notification not = null;
		if(itemi.isUpdate()) {
			not = new Notification(itemi.getFeed().getHeader().getTitle(), "<b>[Item update]</b>\n\n"+itemi.getItem().getTitle()+"\n\n"+itemi.getItem().getLink());
		} else {
			not = new Notification(itemi.getFeed().getHeader().getTitle(), "<b>"+itemi.getItem().getTitle()+"</b>\n\n"+itemi.getItem().getDescriptionAsText()+"\n\n"+itemi.getItem().getLink());
		}
		not.setIcon(new File(getDirectory(), "/resources/textures/rss.png").getPath());
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
