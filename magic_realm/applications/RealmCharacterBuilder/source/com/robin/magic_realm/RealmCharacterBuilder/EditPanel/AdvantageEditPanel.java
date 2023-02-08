package com.robin.magic_realm.RealmCharacterBuilder.EditPanel;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public abstract class AdvantageEditPanel extends JPanel {
	
	protected GameObject character;
	private String levelKey;
	
	public abstract String toString();
	protected abstract void applyAdvantage();
	public abstract boolean isCurrent();
	
	protected AdvantageEditPanel(CharacterWrapper pChar,String levelKey) {
		this.levelKey = levelKey;
		character = pChar.getGameObject();
		setBorder(BorderFactory.createTitledBorder(toString()));
	}
	public boolean equals(Object o) {
		return o!=null && toString().equals(o.toString());
	}
	protected GameData getGameData() {
		return character.getGameData();
	}
	protected String getAttribute(String key) {
		return character.getAttribute(levelKey,key);
	}
	protected boolean hasAttribute(String key) {
		return character.hasAttribute(levelKey,key);
	}
	protected void setAttribute(String key) {
		character.setAttribute(levelKey,key);
	}
	protected void setAttribute(String key,String val) {
		character.setAttribute(levelKey,key,val);
	}
	protected void removeAttribute(String key) {
		character.removeAttribute(levelKey,key);
	}
	protected void setAttributeList(String key,ArrayList<String> list) {
		character.setAttributeList(levelKey,key,list);
	}
	protected ArrayList<String> getAttributeList(String key) {
		if (character.getAttributeBlock(levelKey).get(key) instanceof ArrayList) {
			return character.getAttributeList(levelKey,key);
		}
		return null;
	}
	protected void addAttributeListItem(String key,String val) {
		character.addAttributeListItem(levelKey,key,val);
	}
	protected String getCharacterName() {
		return character.getName();
	}
	public void apply() {
		applyAdvantage();
	}
	public String getSuggestedDescription() {
		return null;
	}
	public String getLevelKey() {
		return levelKey;
	}
}