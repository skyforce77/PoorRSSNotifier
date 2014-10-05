package fr.skyforce77.prn.save;

import it.sauronsoftware.feed4j.FeedParser;
import it.sauronsoftware.feed4j.bean.Feed;

import java.io.Serializable;
import java.net.URL;

public class RSSEntry implements Serializable{
	
	private static final long serialVersionUID = 5653713732743244463L;
	
	private String name, url;
	
	public RSSEntry(String url) {
		this.url = url;
		this.name = "Unknown";
		try {
			URL u = new URL(url);
			Feed feed = FeedParser.parse(u);
			this.name = feed.getHeader().getTitle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getURL() {
		return url;
	}

}
