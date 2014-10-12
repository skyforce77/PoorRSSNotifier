package fr.skyforce77.prn.server;

import it.sauronsoftware.feed4j.FeedParser;
import it.sauronsoftware.feed4j.bean.Feed;
import it.sauronsoftware.feed4j.bean.FeedItem;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import fr.skyforce77.prn.PRN;
import fr.skyforce77.prn.rss.FeedItemInfo;
import fr.skyforce77.prn.save.DataBase;
import fr.skyforce77.prn.save.RSSEntry;

public class PRNServer {
	
	private final ArrayList<FeedItemInfo> torender = new ArrayList<>();
	
	public PRNServer(String title, String[] args) {}
	
	public void startService() {
		new Thread("RSS-Update") {
			@SuppressWarnings("unchecked")
			public void run() {
				while(!Thread.interrupted()) {
					long wait = 0;
					setIcon("rss-update");
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
									if(PRN.dev)
										torender.add(itemi);
									i = items;
								} else {
									torender.add(itemi);
									System.out.println("Found new item: "+item.getTitle());
								}
							}
							DataBase.setValue(entry.getURL(), new Date().getTime());
							DataBase.save();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					setIcon("rss");
					try {
						while(wait < PRN.frame.settingspanel.updatetime.getValue()*60000) {
							Thread.sleep(1000l);
							wait+=1000;
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
						setIcon("rss-notify");
						PRN.server.show(torender.get(0));
						torender.remove(0);
						if(!(torender.size() > 0))
							setIcon("rss");
						try {
							Thread.sleep(10000l);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(10000l);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
	}
	public void setIcon(String ico) {}
	
	public void show(FeedItemInfo itemi) {}
	
}
