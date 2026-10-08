package com.robin.magic_realm.components.attribute;

import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class ChatLine {
	
	public enum HeaderMode {
		CharacterName,
		PlayerName,
		Both,
		SenderName,
	}
	
	public static String BOLD_PREFIX = "b_";
	private static HeaderMode headerMode = HeaderMode.CharacterName;
	public static HeaderMode getHeaderMode() {
		return headerMode;
	}
	public static void setHeaderMode(HeaderMode mode) {
		headerMode = mode;
	}
	
	private CharacterWrapper character;
	private String senderName;
	private String text;
	public ChatLine(CharacterWrapper character,String text) {
		this.character = character;
		this.text = text;
	}
	public ChatLine(String senderName, String text) {
		this.senderName = senderName;
		this.text = text;
	}
	public String getHeader() {
		if (senderName != null) return senderName;
		switch(headerMode) {
			case CharacterName:
				return character.getName();
			case PlayerName:
				return character.getPlayerName();
			case Both:
				break;
			case SenderName:
				return senderName;
		}
		return character.getName()+" ("+character.getPlayerName()+")";
	}
	public String getHeaderStyleName() {
		if (senderName != null) return BOLD_PREFIX+"black";
		return BOLD_PREFIX+character.getChatStyle();
	}
	public String getText() {
		return text;
	}
	public String getTextStyleName() {
		if (senderName != null) return "black";
		return character.getChatStyle();
	}
	public boolean isValid() {
		if (senderName != null) return text.trim().length() > 0;
		return character != null && text.trim().length() > 0;
	}
}