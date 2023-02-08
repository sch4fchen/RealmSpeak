package com.robin.magic_realm.components.summary;

import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public abstract class CharacterActionEvent extends SummaryEvent {
	
	private String characterName;
	
	public CharacterActionEvent(CharacterWrapper character) {
		characterName = character.getGameObject().getName();
	}
	public CharacterActionEvent(String dataString) {
	}
	public String getCharacterName() {
		return characterName;
	}
	protected String getDataString() {
		return "";
	}
}