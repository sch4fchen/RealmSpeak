package com.robin.magic_realm.components.table;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.general.swing.DieRoller;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.PathDetail;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.swing.CenteredMapView;
import com.robin.magic_realm.components.swing.TileLocationChooser;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.DieRollBuilder;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class OffroadTravel extends Search {
	
	public static final String KEY = "Offroad";
	private static final String[] RESULT_MOUNTAINS = {
			"Choice/Mark Path",
			"Choose Clearing",
			"Random Clearing",
			"Same Clearing",
			"Avalanche (1d6 wounds)",
			"Lost",
	};
	private static final String[] RESULT_CAVES = {
			"Choice/Mark Path",
			"Choose Clearing",
			"Random Clearing",
			"Lost (1 wound)",
			"Lost (1 wound)",
			"Same Clearing",
	};
	private static final String[] RESULT_OTHERS = {
			"Choice/Mark Path",
			"Choose Clearing",
			"Choose Clearing",
			"Random Clearing",
			"Lost",
			"Lost",
	};
	private TileLocation current;
	
	public OffroadTravel(JFrame frame, TileLocation current) {
		super(frame,null);
		this.current = current;
	}
	public String getTableName(boolean longDescription) {
		return "Offroad";
	}
	public String getTableKey() {
		return KEY;
	}
	
	public String applyOne(CharacterWrapper character) {
		if (current.hasClearing() && current.clearing.isMountain()) {
			choiceAndMarkPath(character);
			return RESULT_MOUNTAINS[0];
		}
		if (current.hasClearing() && current.clearing.isCave()) {
			choiceAndMarkPath(character);
			return RESULT_CAVES[0];
		}
		choiceAndMarkPath(character);
		return RESULT_OTHERS[0];
	}

	public String applyTwo(CharacterWrapper character) {
		if (current.hasClearing() && current.clearing.isMountain()) {
			chooseClearing(character);
			return RESULT_MOUNTAINS[1];
		}
		if (current.hasClearing() && current.clearing.isCave()) {
			chooseClearing(character);
			return RESULT_CAVES[1];
		}
		chooseClearing(character);
		return RESULT_OTHERS[1];
	}

	public String applyThree(CharacterWrapper character) {
		if (current.hasClearing() && current.clearing.isMountain()) {
			randomClearing(character);
			return RESULT_MOUNTAINS[2];
		}
		if (current.hasClearing() && current.clearing.isCave()) {
			randomClearing(character);
			return RESULT_CAVES[2];
		}
		chooseClearing(character);
		return RESULT_OTHERS[2];
	}

	public String applyFour(CharacterWrapper character) {
		if (current.hasClearing() && current.clearing.isMountain()) {
			sameClearing(character);
			return RESULT_MOUNTAINS[3];
		}
		if (current.hasClearing() && current.clearing.isCave()) {
			lost(character,1);
			return RESULT_CAVES[3];
		}
		randomClearing(character);
		return RESULT_OTHERS[3];
	}

	public String applyFive(CharacterWrapper character) {
		if (current.hasClearing() && current.clearing.isMountain()) {
			avalanche(character);
			return RESULT_MOUNTAINS[4];
		}
		if (current.hasClearing() && current.clearing.isCave()) {
			lost(character,1);
			return RESULT_CAVES[4];
		}
		lost(character,0);
		return RESULT_OTHERS[4];
	}

	public String applySix(CharacterWrapper character) {
		if (current.hasClearing() && current.clearing.isMountain()) {
			lost(character,0);
			return RESULT_MOUNTAINS[5];
		}
		if (current.hasClearing() && current.clearing.isCave()) {
			sameClearing(character);
			return RESULT_CAVES[5];
		}
		lost(character,0);
		return RESULT_OTHERS[5];
	}
	
	private ArrayList<String> getClearingsOfSameSide() {
		int side = 0;
		int currentClearingNum = current.clearing.getNum();
		boolean sideExists = false;
		while(true) {
			ArrayList<String> list = current.tile.getGameObject().getThisAttributeList(Constants.OFFROAD_TRAVEL_SIDES+"_"+side);
			if (list!=null && !list.isEmpty()) {
				for (int i = 0; i<=list.size(); i++) {
					if (list.get(i).matches(String.valueOf(currentClearingNum))) {
						side = i;
						break;
					}
				}
			}
			if (sideExists) break;
		}
		if (sideExists) return current.tile.getGameObject().getThisAttributeList(Constants.OFFROAD_TRAVEL_SIDES+"_"+side);
		return null;
	}
	private void choiceAndMarkPath(CharacterWrapper character) {
		ArrayList<PathDetail> list = getAllUndiscoveredPaths(character);
		for (PathDetail path:list) {
			character.addHiddenPathDiscovery(path.getFullPathKey());
		}
		chooseClearing(character);
	}
	private void chooseClearing(CharacterWrapper character) {
		CenteredMapView.getSingleton().setMarkClearingAlertText("Travel to which clearing?");
		if (current.hasClearing() && current.tile.getGameObject().hasThisAttribute(Constants.OFFROAD_TRAVEL_SIDES)) {
			for (String num : getClearingsOfSameSide()) {
				CenteredMapView.getSingleton().markClearing(current.tile.getClearing(num), true);
			}
		} else {
			for (ClearingDetail cl : current.tile.getClearings()) {
				CenteredMapView.getSingleton().markClearing(cl, true);
			}
		}
		TileLocationChooser chooser = new TileLocationChooser(getParentFrame(),CenteredMapView.getSingleton(),current);
		chooser.setVisible(true);
		CenteredMapView.getSingleton().markAllClearings(false);
		character.setOffroadTravelClearing(chooser.getSelectedLocation().clearing.getNum());
		character.setOffroadTravelLost(false);
	}
	private void randomClearing(CharacterWrapper character) {
		ClearingDetail selectedClearing = null;
		if (current.tile.getGameObject().hasThisAttribute(Constants.OFFROAD_TRAVEL_SIDES)) {
			ArrayList<String> availableClearings = getClearingsOfSameSide();
			int selectedClearingNum = Integer.parseInt(availableClearings.get(availableClearings.size()));
			selectedClearing = current.tile.getClearings().get(RandomNumber.getRandom(selectedClearingNum));
		} else {
			selectedClearing = current.tile.getClearings().get(RandomNumber.getRandom(current.tile.getClearingCount()));
		}
		character.setOffroadTravelClearing(selectedClearing.getNum());
		character.setOffroadTravelLost(false);
	}
	private static void sameClearing(CharacterWrapper character) {
		character.removeOffroadTravelClearing();
		character.setOffroadTravelLost(false);
	}
	private static void lost(CharacterWrapper character, int wounds) {
		if (wounds!=0) {
			character.setExtraWounds(wounds);
		}
		character.setOffroadTravelLost(true);
	}
	private static void avalanche(CharacterWrapper character) {
		DieRoller dieRoller = DieRollBuilder.getDieRollBuilder(null,character).createRoller("Avalance",1);
		character.setExtraWounds(dieRoller.getTotal());
		character.setOffroadTravelLost(true);
	}
}