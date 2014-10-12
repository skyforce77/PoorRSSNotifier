package fr.skyforce77.prn.server;

import java.io.File;
import java.io.IOException;

import fr.skyforce77.prn.PRN;
import fr.skyforce77.prn.rss.FeedItemInfo;

public class LibnotifyServer extends LinuxServer {
	
	public LibnotifyServer(String title, String[] args) {
		super(title, args);
	}
	
	@Override
	public void show(FeedItemInfo itemi) {
		String title = itemi.getFeed().getHeader().getTitle();
		String text = "<b>"+itemi.getItem().getTitle()+"</b>\n\n"+itemi.getItem().getDescriptionAsText()+"\n\n"+itemi.getItem().getLink();
		File i = new File(PRN.getDirectory(), "/resources/textures/rss.png");
		String icon = i.exists() ? i.getPath() : "dialog-information";
		try {
			Runtime.getRuntime().exec(new String[]{"notify-send","--icon="+icon,title,text});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
