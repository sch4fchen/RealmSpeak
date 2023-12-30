package com.robin.magic_realm.components.table;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class Peer1ed extends Search {
	public Peer1ed(JFrame frame) {
		this(frame,null);
	}
	public Peer1ed(JFrame frame,ClearingDetail clearing) {
		super(frame,clearing);
	}
	public String getTableName(boolean longDescription) {
		return "Peer"+(longDescription?"\n(Glimpse, Hidden Enemies)":"");
	}
	public String getTableKey() {
		return "Peer";
	}
	
	public String applyOne(CharacterWrapper character) {
		// Find chits
		return doDiscoverChits(character);
	}

	public String applyTwo(CharacterWrapper character) {
		// Glimpse
		return doGlimpse(character);
	}

	public String applyThree(CharacterWrapper character) {
		// Hidden Enemies
		return doHiddenEnemies(character);
	}

	public String applyFour(CharacterWrapper character) {
		// Hidden Enemies
		return doHiddenEnemies(character);
	}

	public String applyFive(CharacterWrapper character) {
		if (character.affectedByKey(Constants.ADVENTURE_GUIDE)) {
			doPassages(character);
		}
		// Glimpse
		return doGlimpse(character);
	}

	public String applySix(CharacterWrapper character) {
		// Nothing
		return "Nothing";
	}

	@Override
	protected ArrayList<ImageIcon> getHintIcons(CharacterWrapper character) {
		ArrayList<ImageIcon> list = new ArrayList<>();
		for(RealmComponent rc:getAllDiscoverableChits(character,true)) {
			list.add(getIconForSearch(rc));
		}
		return list;
	}
}