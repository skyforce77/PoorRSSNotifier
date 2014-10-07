package fr.skyforce77.prn;

import it.sauronsoftware.feed4j.bean.Feed;
import it.sauronsoftware.feed4j.bean.FeedItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FeedItemInfo {
	
	private FeedItem item;
	private Feed feed;
	private boolean update = false;
	
	public FeedItemInfo(Feed feed, FeedItem item) {
		this.feed = feed;
		this.item = item;
	}
	
	public Feed getFeed() {
		return feed;
	}
	
	public FeedItem getItem() {
		return item;
	}
	
	public void setUpdate(boolean update) {
		this.update = update;
	}
	
	public boolean isUpdate() {
		return update;
	}
	
	public Date getPubDate() {
		if(item.getElement("", "pubDate") == null) {
			return new Date(0L);
		}
		
		String date = item.getElement("", "pubDate").getValue();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Date getModDate() {
		if(item.getElement("", "modDate") == null) {
			return new Date(0L);
		}
		
		String date = item.getElement("", "modDate").getValue();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
