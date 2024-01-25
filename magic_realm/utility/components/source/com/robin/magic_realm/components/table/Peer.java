package com.robin.magic_realm.components.table;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.PathDetail;
import com.robin.magic_realm.components.swing.PathIcon;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class Peer extends Search {
	public Peer(JFrame frame) {
		this(frame,null);
	}
	public Peer(JFrame frame,ClearingDetail clearing) {
		super(frame,clearing);
	}
	public String getTableName(boolean longDescription) {
		return "Peer"+(longDescription?"\n(Hidden Paths, Hidden Enemies)":"");
	}
	public String getTableKey() {
		return "Peer";
	}

	public String applyOne(CharacterWrapper character) {
		// Choice
		return doChoice(character);
	}

	public String applyTwo(CharacterWrapper character) {
		// Clues, Paths
		doClues(character);
		String pathRes = doPaths(character);
		if (pathRes!=null) {
			return "Clues and "+pathRes;
		}
		return "Clues";
	}

	public String applyThree(CharacterWrapper character) {
		// Hidden Enemies, Paths
		doHiddenEnemies(character);
		String pathRes = doPaths(character);
		if (pathRes!=null) {
			return "Found hidden enemies and "+pathRes;
		}
		return "Found hidden enemies";
	}

	public String applyFour(CharacterWrapper character) {
		// Found Hidden Enemies
		return doHiddenEnemies(character);
	}

	public String applyFive(CharacterWrapper character) {
		if (character.affectedByKey(Constants.ADVENTURE_GUIDE)) {
			doPassages(character);
		}
		if (character.affectedByKey(Constants.TRAVELERS_GUIDE)) {
			doPaths(character);
		}
		// Clues
		doClues(character);
		return "Clues";
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
		return list;
	}
}