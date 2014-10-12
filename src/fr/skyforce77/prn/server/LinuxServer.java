package fr.skyforce77.prn.server;

import fr.skyforce77.prn.rss.FeedItemInfo;

public class LinuxServer extends PRNServer {
	
	public LinuxServer(String title, String[] args) {
		super(title, args);
	}
	
	@Override
	public void show(FeedItemInfo itemi) {
		System.out.println("["+itemi.getFeed().getHeader().getName()+"] "+
				itemi.getItem().getTitle()+"\n"+itemi.getItem().getDescriptionAsText());
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
