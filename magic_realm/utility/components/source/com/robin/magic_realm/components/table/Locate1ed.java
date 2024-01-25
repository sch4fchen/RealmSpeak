package com.robin.magic_realm.components.table;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.swing.PathIcon;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class Locate1ed extends Search {
	public Locate1ed(JFrame frame) {
		this(frame,null);
	}
	public Locate1ed(JFrame frame,ClearingDetail clearing) {
		super(frame,clearing);
	}
	public String getTableName(boolean longDescription) {
		return "Locate"+(longDescription?"\n(Hidden Paths, Treasure Locations)":"");
	}
	public String getTableKey() {
		return "Locate";
	}
	public String applyOne(CharacterWrapper character) {
		// Choice
		return doChoice1ed(character);
	}

	public String applyTwo(CharacterWrapper character) {
		// Choice
		return doChoice1ed(character);
	}

	public String applyThree(CharacterWrapper character) {
		// Hidden paths
		return doPaths(character);
	}

	public String applyFour(CharacterWrapper character) {
		// Glimpse to find
		return applyFive(character);
	}

	public String applyFive(CharacterWrapper character) {
		if (character.affectedByKey(Constants.ADVENTURE_GUIDE)) {
			doPassages(character);
		}
		if (character.affectedByKey(Constants.TRAVELERS_GUIDE)) {
			doPaths(character);
		}
		// Glimpse to find
		if (character.getGameObject().getThisAttribute(Constants.GLIMPSED_COUNTERS+character.getCurrentLocation().tile.getName()).matches(character.getCurrentDayKey())) {
			return doDiscoverChits(character);
		}
		return "Nothing";
	}

	public String applySix(CharacterWrapper character) {
		// Nothing
		return "Nothing";
	}
	
	@Override
	protected ArrayList<ImageIcon> getHintIcons(CharacterWrapper character) {
		ArrayList<ImageIcon> list = new ArrayList<>();
		for(PathDetail path:getAllUndiscoveredPaths(character)) {
			list.add(new PathIcon(path));
		}
		for(RealmComponent rc:getAllDiscoverableChits(character,true)) {
			list.add(getIconForSearch(rc));
		}
		return list;
	}
}