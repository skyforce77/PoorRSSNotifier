package fr.skyforce77.prn.server;

import fr.skyforce77.prn.notify.Notification;

public class LinuxServer extends PRNServer {
	
	public LinuxServer(String title, String[] args) {
		super(title, args);
	}
	
	@Override
	public void show(Notification notif) {
		System.out.println("[Item: "+notif.getTitle()+"] "+notif.getText());
	}
	
	@Override
	public void setIcon(String ico) {
		String s = "[State] ";
		switch (ico) {
		case "rss":
			s = s + "Default";
			break;
		case "rss-update":
			s = s + "Updating feeds";
			break;
		case "rss-notify":
			s = s + "Notifying updates";
			break;
		default:
			break;
		}
		System.out.println(s);
	}
	
}
