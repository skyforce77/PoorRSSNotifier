package fr.skyforce77.prn.server;

import java.io.IOException;

import fr.skyforce77.prn.notify.Notification;

public class LibnotifyServer extends LinuxServer {
	
	public LibnotifyServer(String title, String[] args) {
		super(title, args);
	}
	
	@Override
	public void show(Notification notif) {
		try {
			Runtime.getRuntime().exec(new String[]{"notify-send","--icon="+notif.getIcon(),notif.getTitle(),notif.getText()});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
