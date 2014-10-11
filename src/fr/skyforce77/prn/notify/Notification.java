package fr.skyforce77.prn.notify;

import fr.skyforce77.prn.PRN;

public class Notification {

	private String title, text;
	private String icon = "dialog-information";
	
	public Notification(String title, String text) {
		this.title = title;
		this.text = text;
	}
	
	public Notification(String title, String text, String icon) {
		this(title, text);
		this.icon = icon;
	}

	public String getIcon() {
		return icon;
	}
	
	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public void show() {
		PRN.server.show(this);
	}
	
}
