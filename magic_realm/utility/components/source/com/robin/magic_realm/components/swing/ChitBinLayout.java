package com.robin.magic_realm.components.swing;

import java.awt.Point;
import java.util.*;

import com.robin.general.util.HashLists;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.ChitComponent;
import com.robin.magic_realm.components.StateChitComponent;

public class ChitBinLayout {
	public static final int INNER_CELL_SPACE = 3;
	private static final String[] GROUP = {
		"FLY",
		"OTHER",
		"M/F",
		"MOVE",
		"FIGHT",
		"MAGIC",
	};
	
	private ArrayList<String> groups;
	private ArrayList<ChitBin> chitBins;
	private HashLists<String,ChitBin> hashLists;
	
	public ChitBinLayout(ArrayList<StateChitComponent> chits) {
		Collections.sort(chits);
		groups = new ArrayList<>();
		chitBins = new ArrayList<>();
		hashLists = new HashLists<>();
		for (ChitComponent chit : chits) {
			if (chit.isActionChit()) {
				CharacterActionChitComponent achit = (CharacterActionChitComponent)chit;
				if (achit.isMoveFight()) {
					addChit("M/F",achit);
				}
				else if (achit.isMove()) {
					addChit("MOVE",achit);
				}
				else if (achit.isFight() || achit.isFightAlert()) {
					addChit("FIGHT",achit);
				}
				else if (achit.isFly()) {
					addChit("FLY",achit);
				}
				else if (achit.isMagic()) {
					addChit("MAGIC",achit);
				}
				else {
					addChit("OTHER",achit);
				}
			}
			else {
				// Spell Fly chit
				addChit("FLY",null);
			}
		}
		
		ArrayList<String> sorted = new ArrayList<>(Arrays.asList(GROUP));
		sorted.retainAll(groups);
		groups = sorted;
	}
	public ArrayList<String> getGroups() {
		return groups;
	}
	public ArrayList<ChitBin> getBins(String group) {
		return hashLists.getList(group);
	}
	public ChitComponent getChit(int index) {
		if (index<0 || index>=chitBins.size()) {
			throw new IllegalStateException("No chit bin at position " + index);
		}
		ChitBin bin = chitBins.get(index);
		return bin.getChit();
	}
	public void setChit(int index,ChitComponent chit) {
		if (index<0 || index>=chitBins.size()) {
			throw new IllegalStateException("No chit bin at position " + index);
		}
		ChitBin bin = chitBins.get(index);
		bin.setChit(chit);
	}
	public ArrayList<ChitComponent> getAllChits() {
		ArrayList<ChitComponent> list = new ArrayList<>();
		for (ChitBin bin : chitBins) {
			ChitComponent chit = bin.getChit();
			if (chit!=null) {
				list.add(chit);
			}
		}
		return list;
	}
	private void addChit(String type,CharacterActionChitComponent chit) {
		ChitBin bin = new ChitBin();
		if (chit!=null && chit.isMagic()) {
			bin.setColorMagic(chit.getEnchantedColorMagic());
		}
		hashLists.put(type, bin);
		chitBins.add(bin);
		if (!groups.contains(type)) {
			groups.add(type);
		}
	}
	public void reset() {
		for (ChitBin bin : chitBins) {
			bin.setChit(null);
		}
	}
	public ChitComponent getChitAt(Point p) {
		for (ChitBin bin : chitBins) {
			if (bin.getRectangle().contains(p)) {
				return bin.getChit();
			}
		}
		return null;
	}
	public int getChitIndex(ChitComponent chit) {
		for (int i=0;i<chitBins.size();i++) {
			ChitBin bin = chitBins.get(i);
			if (chit==bin.getChit()) { // testing pointer equality is good enough for here
				return i;
			}
		}
		return -1;
	}
}