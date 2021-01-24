/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
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