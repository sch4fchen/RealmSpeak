package com.robin.magic_realm.components.table;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.robin.general.swing.ButtonOptionDialog;
import com.robin.general.swing.DieRoller;
import com.robin.general.swing.IconFactory;
import com.robin.general.util.OrderedHashtable;
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
			return choiceOrMarkPath(character);
		}
		if (current.hasClearing() && current.clearing.isCave()) {
			return choiceOrMarkPath(character);
		}
		return choiceOrMarkPath(character);
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
		String sideString = current.tile.getStatSide();
		boolean sideExists = false;
		ArrayList<String> list = null;
		for (int i = 0; i<=5; i++) {
			list = (ArrayList<String>) current.tile.getGameObject().getAttributeBlock(sideString).getOrDefault(Constants.OFFROAD_TRAVEL_SIDE+"_"+i,null);
			if (list!=null && !list.isEmpty()) {
				for (int j=0; j<list.size(); j++) {
					if (list.get(j).matches(String.valueOf(currentClearingNum))) {
						sideExists = true;
						break;
					}
				}
			}
			if (sideExists) break;
		}
		if (sideExists) {
			return list;
		}
		return null;
	}
	private String choiceOrMarkPath(CharacterWrapper character) {
		String pathsString = "Mark Paths";
		ImageIcon paths = getIconFromList(convertPathDetailToImageIcon(getAllUndiscoveredPaths(character)));
		String chooseString = "Choose Clearing";
		String sameString = "Same Clearing";
		String randomString = "Random Clearing";
		String lostString = "Lost";
		String lostWoundString = "Lost (1 wound)";
		String avalancheString = "Avalanche (1d6 wounds)";
		
		ButtonOptionDialog chooseSearch = new ButtonOptionDialog(getParentFrame(), null, "Choice:", "Offroad Travel", false);
		chooseSearch.addSelectionObject(pathsString);
		chooseSearch.setSelectionObjectIcon(pathsString,paths);
		chooseSearch.addSelectionObject(chooseString);
		chooseSearch.setSelectionObjectIcon(chooseString,IconFactory.findIcon("images/interface/build.gif"));
		chooseSearch.addSelectionObject(randomString);
		chooseSearch.setSelectionObjectIcon(randomString,IconFactory.findIcon("images/interface/build.gif"));
		if (current.hasClearing() && (current.clearing.isMountain() || current.clearing.isCave())) {
			chooseSearch.addSelectionObject(sameString);
			chooseSearch.setSelectionObjectIcon(sameString,IconFactory.findIcon("images/interface/build.gif"));
		}
		if (current.hasClearing() && (current.clearing.isMountain())) {
			chooseSearch.addSelectionObject(avalancheString);
			chooseSearch.setSelectionObjectIcon(avalancheString,IconFactory.findIcon("images/phases/mountain.gif"));
		}
		if (current.hasClearing() && !current.clearing.isCave()) {
			chooseSearch.addSelectionObject(lostString);
			chooseSearch.setSelectionObjectIcon(lostString,IconFactory.findIcon("images/phases/normal.gif"));
		}
		if (current.hasClearing() && current.clearing.isCave()) {
			chooseSearch.addSelectionObject(lostWoundString);
			chooseSearch.setSelectionObjectIcon(lostWoundString,IconFactory.findIcon("images/phases/cave.gif"));
		}
		chooseSearch.setVisible(true);
		String choice = (String)chooseSearch.getSelectedObject();
		if (choice.equals(pathsString)) {
			sameClearing(character);
			return "Choice - "+pathsString;
		}
		else if (choice.equals(chooseString)) {
			chooseClearing(character);
			return "Choice - "+chooseString;
		}
		else if (choice.equals(sameString)) {
			sameClearing(character);
			return "Choice - "+sameString;
		}
		else if (choice.equals(randomString)) {
			randomClearing(character);
			return "Choice - "+randomString;
		}
		else if (choice.equals(avalancheString)) {
			avalanche(character);
			return "Choice - "+avalancheString;
		}
		else if (choice.equals(lostString)) {
			lost(character,0);
			return "Choice - "+lostString;
		}
		else if (choice.equals(lostWoundString)) {
			lost(character,1);
			return "Choice - "+lostWoundString;
		}
		return null;
	}
	private void markPaths(CharacterWrapper character) {
		ArrayList<PathDetail> list = getAllUndiscoveredPaths(character);
		for (PathDetail path:list) {
			character.addHiddenPathDiscovery(path.getFullPathKey());
		}
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
			String selectedClearingNum = availableClearings.get(RandomNumber.getRandom(availableClearings.size()));
			selectedClearing = current.tile.getClearing(selectedClearingNum);
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
	private void lost(CharacterWrapper character, int wounds) {
		String text = "You got Lost!";
		if (wounds!=0) {
			character.setExtraWounds(wounds);
			text = text + " You received "+wounds+" wounds while travelling offroad.";
		}
		character.setOffroadTravelLost(true);
		JOptionPane.showMessageDialog(getParentFrame(),text,"Lost",JOptionPane.INFORMATION_MESSAGE);
	}
	private void avalanche(CharacterWrapper character) {
		DieRoller dieRoller = DieRollBuilder.getDieRollBuilder(null,character).createRoller("Avalance",1);
		int wounds = dieRoller.getTotal();
		character.setExtraWounds(wounds);
		character.setOffroadTravelLost(true);
		JOptionPane.showMessageDialog(getParentFrame(),"An avalanche hit you and you received "+wounds+" wounds. You are now lost!","Avalanche",JOptionPane.INFORMATION_MESSAGE);
	}
}