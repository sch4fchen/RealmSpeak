package com.robin.magic_realm.components.store;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.swing.ButtonOptionDialog;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public abstract class GuildStore extends Store {
	
	protected static String FREE_ADVANCEMENT = "Advance to next level by giving item to guild: ";
	
	protected CharacterWrapper character;
	protected ArrayList<GameObject> advancementObjects; 
	
	protected abstract void setupGuildSpecific();
	protected abstract String doGuildService(JFrame frame,int level);

	public GuildStore(GuildChitComponent guild,CharacterWrapper character) {
		super(guild);
		this.character = character;
		setupStore();
	}

	private void setupStore() {
		advancementObjects = new ArrayList<GameObject>();
		for (GameObject go:character.getInventory()) {
			if (go.hasThisAttribute(Constants.ADVANCEMENT)) {
				advancementObjects.add(go);
			}
		}
		setupGuildSpecific();
	}
	
	protected boolean isFreeAdvancement(String selected) {
		if (selected.startsWith(FREE_ADVANCEMENT)) {
			String item = selected.substring(FREE_ADVANCEMENT.length());
			for (GameObject go:advancementObjects) {
				if (item.equals(go.getName())) {
					go.getHeldBy().remove(go); // Make object disappear
					return true;
				}
			}
		}
		return false;
	}

	protected void updateButtonChooser(ButtonOptionDialog chooser,int level) {
		if (level>=3) return;
		for (GameObject go:advancementObjects) {
			chooser.addSelectionObject(FREE_ADVANCEMENT+go.getName());
		}
	}
	
	public String doService(JFrame frame) {
		return doGuildService(frame,character.getCurrentGuildLevel());
	}
	protected GameObject getNewCharacterChit() {
		GameObject go = character.getGameData().createNewObject();
		go.setThisAttribute("character_chit");
		go.setThisAttribute(Constants.CHIT_EARNED);
		CharacterActionChitComponent first = character.getAllChits().get(0);
		go.setThisAttribute("icon_folder",first.getGameObject().getThisAttribute("icon_folder"));
		go.setThisAttribute("icon_type",first.getGameObject().getThisAttribute("icon_type"));
		character.getGameObject().add(go);
		CharacterActionChitComponent chit = (CharacterActionChitComponent)RealmComponent.getRealmComponent(go);
		chit.makeActive();
		return go;
	}
	
	protected void chooseFriendlinessGain(JFrame frame) {
		ArrayList<String> list = trader.getGameObject().getThisAttributeList("allies");
		
		GamePool pool = new GamePool(trader.getGameObject().getGameData().getGameObjects());
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,"The guild advancement includes one friendliness level for one of the following groups:",false);
		for (String groupName : list) {
			GameObject leader = pool.findFirst("rank=HQ,native="+groupName);
			int rel = character.getRelationship(leader);
			String oldR = RealmUtility.getRelationshipNameFor(rel);
			String newR = RealmUtility.getRelationshipNameFor(rel+1);
			chooser.addGameObject(leader,oldR+" -> "+newR);
		}
		chooser.setVisible(true);
		String selected = chooser.getSelectedText();
		if (selected!=null) {
			GameObject leader = chooser.getFirstSelectedComponent().getGameObject();
			character.changeRelationship(leader,1);
			RealmLogging.logMessage(character.getGameObject().getName(),"+1 friendliness with the "+leader.getThisAttribute("native"));
		}
	}
}