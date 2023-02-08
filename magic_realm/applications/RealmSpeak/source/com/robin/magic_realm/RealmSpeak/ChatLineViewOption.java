package com.robin.magic_realm.RealmSpeak;

import javax.swing.JRadioButton;

public class ChatLineViewOption extends JRadioButton {
	private int lines;
	public ChatLineViewOption(String text,int lines) {
		super(text);
		this.lines = lines;
	}
	public int getLines() {
		return lines;
	}
	
	public static ChatLineViewOption[] generateOptions() {
		return new ChatLineViewOption[] {
			new ChatLineViewOption("10 Lines of Chat",10),
			new	ChatLineViewOption("5 Lines of Chat",5),
			new ChatLineViewOption("3 Lines of Chat",3),
			new ChatLineViewOption("1 Lines of Chat",1),
			new ChatLineViewOption("Chat OFF",0),
		};
	}
}