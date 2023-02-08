package com.robin.magic_realm.components.attribute;

import java.awt.Color;

import com.robin.magic_realm.components.MagicRealmColor;

public class ChatStyle {
	
	public static ChatStyle[] styles = {
		new ChatStyle("blue",Color.blue),
		new ChatStyle("fgreen",MagicRealmColor.FORESTGREEN),
		new ChatStyle("brown",MagicRealmColor.BROWN),
		new ChatStyle("red",Color.red),
		new ChatStyle("darkgray",MagicRealmColor.DARKGRAY),
		new ChatStyle("orange",MagicRealmColor.ORANGE),
		new ChatStyle("purple",MagicRealmColor.PURPLE),
		new ChatStyle("green",MagicRealmColor.GREEN),
		new ChatStyle("black",Color.black),
	};
	
	private String styleName;
	private Color color;
	
	public ChatStyle(String styleName,Color color) {
		this.styleName = styleName;
		this.color = color;
	}
	public String getStyleName() {
		return styleName;
	}
	public Color getColor() {
		return color;
	}
}