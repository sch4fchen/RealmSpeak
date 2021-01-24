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

import com.robin.general.swing.ButtonOptionDialog;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.swing.PathIcon;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class Search1ed extends Search {
	public Search1ed(JFrame frame) {
		this(frame,null);
	}
	public Search1ed(JFrame frame,ClearingDetail clearing) {
		super(frame,clearing);
	}
	public String getTableName(boolean longDescription) {
		return "Search"+(longDescription?"\n(Secret Passages, Hidden Paths, Hidden Enemies, Treasure Locations)":"");
	}
	public String getTableKey() {
		return "Search1ed";
	}
	public String applyOne(CharacterWrapper character) {
		return doCoiceSearch(character);
	}

	public String applyTwo(CharacterWrapper character) {
		return doGlimpse(character);
	}

	public String applyThree(CharacterWrapper character) {
		return doPassages(character);
	}

	public String applyFour(CharacterWrapper character) {
		return doPaths(character);
	}

	public String applyFive(CharacterWrapper character) {
		return doHiddenEnemies(character);
	}

	public String applySix(CharacterWrapper character) {
		return "Nothing";
	}
	
	private String doCoiceSearch(CharacterWrapper character) {
		String glimpse = "Glimpse counters";
		String secretPassages = "Secret passages";
		String hiddenPaths = "Hidden paths";
		String hiddenEnemies = "Hidden enemies";
		
		ImageIcon chits = getIconFromList(convertRealmComponentToImageIcon(getAllDiscoverableChits(character,true)));
		ImageIcon passages = getIconFromList(convertPathDetailToImageIcon(getAllUndiscoveredPassages(character)));
		ImageIcon paths = getIconFromList(convertPathDetailToImageIcon(getAllUndiscoveredPaths(character)));
		
		ButtonOptionDialog chooseSearch = new ButtonOptionDialog(getParentFrame(), null, "Choice:", "", false);
		chooseSearch.addSelectionObject(glimpse);
		chooseSearch.setSelectionObjectIcon(glimpse,chits);
		chooseSearch.addSelectionObject(secretPassages);
		chooseSearch.setSelectionObjectIcon(secretPassages,passages);
		chooseSearch.addSelectionObject(hiddenPaths);
		chooseSearch.setSelectionObjectIcon(hiddenPaths,paths);
		chooseSearch.addSelectionObject(hiddenEnemies);
		chooseSearch.setVisible(true);
		String choice = (String)chooseSearch.getSelectedObject();
		if (choice.equals(glimpse)) {
			return "Choice - "+doGlimpse(character);
		}
		else if (choice.equals(secretPassages)) {
			return "Choice - "+doPassages(character);
		}
		else if (choice.equals(hiddenPaths)) {
			return "Choice - "+doPaths(character);
		}
		else if (choice.equals(hiddenEnemies)) {
			return "Choice - "+doHiddenEnemies(character);
		}
		return null;
	}
	
	private String doGlimpse(CharacterWrapper character) {
		character.getGameObject().setThisAttribute(Constants.GLIMPSED_COUNTERS+character.getCurrentLocation().tile.getName(), character.getCurrentDayKey());
		doClues(character);
		return "Glimpse counters";
	}
	
	@Override
	protected ArrayList<ImageIcon> getHintIcons(CharacterWrapper character) {
		ArrayList<ImageIcon> list = new ArrayList<>();
		for(PathDetail path:getAllUndiscoveredPassages(character)) {
			list.add(new PathIcon(path));
		}
		for(PathDetail path:getAllUndiscoveredPaths(character)) {
			list.add(new PathIcon(path));
		}
		for(RealmComponent rc:getAllDiscoverableChits(character,true)) {
			list.add(getIconForSearch(rc));
		}
		return list;
	}
}