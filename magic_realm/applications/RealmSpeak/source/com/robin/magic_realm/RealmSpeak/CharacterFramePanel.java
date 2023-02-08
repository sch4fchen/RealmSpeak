package com.robin.magic_realm.RealmSpeak;

import javax.swing.JPanel;

import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.*;

public abstract class CharacterFramePanel extends JPanel {
	
	private CharacterFrame parent;
	
	public abstract void updatePanel();
	
	protected CharacterFramePanel(CharacterFrame parent) {
		this.parent = parent;
	}
	public CharacterFrame getCharacterFrame() {
		return parent;
	}
	public CharacterWrapper getCharacter() {
		return parent.character;
	}
	public RealmComponent getRealmComponent() {
		return RealmComponent.getRealmComponent(parent.character.getGameObject());
	}
	public RealmGameHandler getGameHandler() {
		return parent.gameHandler;
	}
	public RealmSpeakFrame getMainFrame() {
		return parent.gameHandler.getMainFrame();
	}
	public HostPrefWrapper getHostPrefs() {
		return parent.hostPrefs;
	}
	public GameWrapper getGame() {
		return parent.gameHandler.getGame();
	}
}