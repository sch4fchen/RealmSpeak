package com.robin.magic_realm.components.attribute;

import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class ChatLine {
	
	public enum HeaderMode {
		CharacterName,
		PlayerName,
		Both,
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
	private String text;
	public ChatLine(CharacterWrapper character,String text) {
		this.character = character;
		this.text = text;
	}
	public String getHeader() {
		switch(headerMode) {
			case CharacterName:
				return character.getName();
			case PlayerName:
				return character.getPlayerName();
			case Both:
				break;
		}
		return character.getName()+" ("+character.getPlayerName()+")";
	}
	public String getHeaderStyleName() {
		return BOLD_PREFIX+character.getChatStyle();
	}
	public String getText() {
		return text;
	}
	public String getTextStyleName() {
		return character.getChatStyle();
	}
	public boolean isValid() {
		return character!=null && text.trim().length()>0;
	}
}