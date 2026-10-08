package com.robin.magic_realm.components.attribute;

public class ChatLineGlobal implements ChatLine {
	private String senderName;
	private String text;
	public ChatLineGlobal(String senderName, String text) {
		this.senderName = senderName;
		this.text = text;
	}
	public String getHeader() {
		return senderName;
	}
	public String getHeaderStyleName() {
		return ChatStyle.BOLD_PREFIX+"black";
	}
	public String getText() {
		return text;
	}
	public String getTextStyleName() {
		return "black";
	}
	public boolean isValid() {
		return senderName!=null && text.trim().length()>0;
	}
}