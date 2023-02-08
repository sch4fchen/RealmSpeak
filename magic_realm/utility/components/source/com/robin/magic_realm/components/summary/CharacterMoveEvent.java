package com.robin.magic_realm.components.summary;

import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class CharacterMoveEvent extends CharacterActionEvent {
	
	public static final String KEY = "CME";

	public CharacterMoveEvent(CharacterWrapper character) {
		super(character);
	}
	public CharacterMoveEvent(String val) {
		super(val);
	}
	protected String getKey() {
		return KEY;
	}
}