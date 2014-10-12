package fr.skyforce77.prn;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

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
	public static boolean dev = false;

	public static void main(final String[] args) {
		if(!getDirectory().exists()) {
			getDirectory().mkdirs();
		}

		DataBase.load();

		if(DataBase.getValue("feeds") == null) {
			CopyOnWriteArrayList<RSSEntry> entries = new CopyOnWriteArrayList<RSSEntry>();
			DataBase.setValue("feeds", entries);
		}

		String command = Arrays.toString(args).toLowerCase();
		initServer(command, args);
		frame = new SettingsFrame();
		
		if(command.contains("--settings") || command.contains("-s")) {
			frame.setVisible(true);
		}
	}

	public static void initServer(String command, String[] args) {
		if(command.contains("--help") || command.contains("-h")) {
			BufferedReader in = new BufferedReader(new InputStreamReader(PRN.class.getResourceAsStream("/resources/help.txt")));
			try {
				String input;
				while ((input = in.readLine()) != null)
					System.out.println(input);
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
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
