package com.robin.magic_realm.components.table;

import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.event.ChangeListener;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.swing.ButtonOptionDialog;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.quest.SearchResultType;
import com.robin.magic_realm.components.quest.requirement.QuestRequirementParams;
import com.robin.magic_realm.components.swing.PathIcon;
import com.robin.magic_realm.components.utility.ClearingUtility;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public abstract class Search extends RealmTable {
	protected ClearingDetail targetClearing;
	
	public Search(JFrame frame) {
		this(frame,null);
	}
	public Search(JFrame frame,ClearingDetail clearing) {
		super(frame,null);
		targetClearing = clearing;
	}
	protected ClearingDetail getCurrentClearing(CharacterWrapper character) {
		return targetClearing==null?character.getCurrentLocation().clearing:targetClearing;
	}
	protected String doChoice(CharacterWrapper character) {
		String peer2String = "Clues and Paths";
		String peer3String = "Hidden Enemies and Paths";
		String locate2String = "Clues and Passages";
		String locate4String = "Discover Chits";
		
		ImageIcon paths = getIconFromList(convertPathDetailToImageIcon(getAllUndiscoveredPaths(character)));
		ImageIcon passages = getIconFromList(convertPathDetailToImageIcon(getAllUndiscoveredPassages(character)));
		ImageIcon chits = getIconFromList(convertRealmComponentToImageIcon(getAllDiscoverableChits(character,true)));
		
		ButtonOptionDialog chooseSearch = new ButtonOptionDialog(getParentFrame(), null, "Choice:", "", false);
		chooseSearch.addSelectionObject(peer2String);
		chooseSearch.setSelectionObjectIcon(peer2String,paths);
		chooseSearch.addSelectionObject(peer3String);
		chooseSearch.setSelectionObjectIcon(peer3String,paths);
		chooseSearch.addSelectionObject(locate2String);
		chooseSearch.setSelectionObjectIcon(locate2String,passages);
		chooseSearch.addSelectionObject(locate4String);
		chooseSearch.setSelectionObjectIcon(locate4String,chits);
		chooseSearch.setVisible(true);
		String choice = (String)chooseSearch.getSelectedObject();
		if (choice.equals(peer2String)) {
			return "Choice - "+RealmTable.peer(getParentFrame(),targetClearing).applyTwo(character);
		}
		else if (choice.equals(peer3String)) {
			return "Choice - "+RealmTable.peer(getParentFrame(),targetClearing).applyThree(character);
		}
		else if (choice.equals(locate2String)) {
			return "Choice - "+RealmTable.locate(getParentFrame(),targetClearing).applyTwo(character);
		}
		else if (choice.equals(locate4String)) {
			return "Choice - "+RealmTable.locate(getParentFrame(),targetClearing).applyFour(character);
		}
		return null;
	}
	protected void doClues(CharacterWrapper character) {
		ClearingDetail currentClearing = getCurrentClearing(character);
		String note = ClearingUtility.showTileChits(getParentFrame(),currentClearing,"Clues");
		if (note!=null) {
			character.addNote(currentClearing.getParent(),"Clues",note);
		}
		
		QuestRequirementParams qp = new QuestRequirementParams();
		qp.actionName = getTableKey();
		qp.actionType = CharacterActionType.SearchTable;
		qp.searchType = SearchResultType.Clues;
		qp.searchHadAnEffect = note!=null;
		character.testQuestRequirements(getParentFrame(),qp);
	}
	protected ArrayList<ImageIcon> convertPathDetailToImageIcon(ArrayList<PathDetail> paths) {
		ArrayList<ImageIcon> list = new ArrayList<>();
		if (paths!=null) {
			for (PathDetail path:paths) {
				list.add(new PathIcon(path));
			}
		}
		return list;
	}
	protected ArrayList<ImageIcon> convertRealmComponentToImageIcon(ArrayList<RealmComponent> chits) {
		ArrayList<ImageIcon> list = new ArrayList<>();
		if (chits!=null) {
			for (RealmComponent rc:chits) {
				list.add(getIconForSearch(rc));
			}
		}
		return list;
	}
	protected ArrayList<PathDetail> getAllUndiscoveredPaths(CharacterWrapper character) {
		ArrayList<PathDetail> list = new ArrayList<>();
		ClearingDetail currentClearing = getCurrentClearing(character);
		ArrayList<PathDetail> passages = currentClearing.getConnectedPaths();
		if (passages==null) return list;
		for (PathDetail path : passages) {
			if (path.isHidden()) {
				if (!character.hasHiddenPathDiscovery(path.getFullPathKey())) {
					list.add(path);
				}
			}
		}
		return list;
	}
	protected ArrayList<PathDetail> getAllUndiscoveredPassages(CharacterWrapper character) {
		ArrayList<PathDetail> list = new ArrayList<>();
		ClearingDetail currentClearing = getCurrentClearing(character);
		ArrayList<PathDetail> passages = currentClearing.getConnectedPaths();
		if (passages==null) return list;
		for (PathDetail path : passages) {
			if (path.isSecret()) {
				if (!character.hasSecretPassageDiscovery(path.getFullPathKey())) {
					list.add(path);
				}
			}
		}
		return list;
	}
	protected ArrayList<RealmComponent> getAllDiscoverableChits(CharacterWrapper character,boolean onlyUndiscovered) {
		ArrayList<RealmComponent> list = new ArrayList<>();
		ClearingDetail currentClearing = getCurrentClearing(character);
		for (RealmComponent rc:currentClearing.getClearingComponents()) {
			if (rc.getGameObject().hasThisAttribute("chit")
					&& rc.getGameObject().hasThisAttribute("seen")) { // Only "seen" chits should be added as hints
				if (rc.getGameObject().hasThisAttribute("discovery")) {
					if (!onlyUndiscovered || !character.hasTreasureLocationDiscovery(rc.getGameObject().getName())) {
						if (rc.getGameObject().getHoldCount()>0) { // no point in hinting when the treasure location is empty!
							list.add(rc);
						}
					}
				}
				else if (rc.getGameObject().hasThisAttribute("gold")) {
					list.add(rc);
				}
				else if (rc.getGameObject().hasThisAttribute("minor_tl")) {
					list.add(rc);
				}
				else if (rc.isGate() || rc.isGuild()) {
					if (!onlyUndiscovered || !character.hasOtherChitDiscovery(rc.getGameObject().getName())) {
						list.add(rc);
					}
				}
			}
		}
		return list;
	}
	protected String doDiscoverChits(CharacterWrapper character) {
		doClues(character);
		QuestRequirementParams qp = new QuestRequirementParams();
		qp.actionName = getTableKey();
		qp.actionType = CharacterActionType.SearchTable;
		qp.searchType = SearchResultType.DiscoverChits;
		// Discover treasure locations in current clearing
		String message = "Discover chit(s) - Found ";
		ClearingDetail currentClearing = getCurrentClearing(character);
		Collection<RealmComponent> allChits = currentClearing.getClearingComponents();
		int count=0;
		for (RealmComponent rc : allChits) {
			// only discover discovery chits
			if (rc.getGameObject().hasThisAttribute("chit")) {
				boolean foundSomething = discoverChit(getParentFrame(),character,currentClearing,rc,qp,getListener());
				if (foundSomething) {
					if (count>0) {
						message = message + ",";
					}
					message = message + rc.getGameObject().getName();
					count++;
				}
			}
		}
		if (count==0) {
			message = "Discover chit(s) - none to discover";
		}
		qp.searchHadAnEffect = count>0;
		character.testQuestRequirements(getParentFrame(),qp);
		return message;
	}
	public static boolean discoverChit(JFrame frame,CharacterWrapper character,ClearingDetail currentClearing,RealmComponent rc,QuestRequirementParams qp,ChangeListener listener) {
		String discoveryName = rc.getGameObject().getName();
		if (rc.getGameObject().hasThisAttribute("discovery")) {
			if (!character.hasTreasureLocationDiscovery(rc.getGameObject().getName())) {
				character.addTreasureLocationDiscovery(rc.getGameObject().getName());
				qp.objectList.add(rc.getGameObject());
				return true;
			}
		}
		else if (rc.getGameObject().hasThisAttribute("gold")) {
			int gold = rc.getGameObject().getThisInt("gold");
			character.addGold(gold);
			currentClearing.remove(rc.getGameObject());
			return true;
		}
		else if (rc.getGameObject().hasThisAttribute("minor_tl")) {
			GameObject thing = rc.getGameObject().getHold().get(0); // better be one thing there!
			if (thing.hasThisAttribute("spell")) {
				if (character.canLearn(thing)) {
					discoveryName = discoveryName + " (learned "+thing.getName()+")";
					character.recordNewSpell(frame,thing);
					currentClearing.remove(rc.getGameObject());
					qp.objectList.add(rc.getGameObject());
				}
				else {
					discoveryName = discoveryName + " (cannot learn spell)";
				}
				return true;
			}
			discoveryName = discoveryName + " ("+thing.getName()+")";
			Loot.addItemToCharacter(frame,listener,character,thing);
			currentClearing.remove(rc.getGameObject());
			qp.objectList.add(rc.getGameObject());
			return true;
		}
		else if (rc.isGate() || rc.isGuild() || rc.isRedSpecial()) {
			if (!character.hasOtherChitDiscovery(rc.getGameObject().getName())) {
				character.addOtherChitDiscovery(rc.getGameObject().getName());
				qp.objectList.add(rc.getGameObject());
				
				if (rc.isGate()) {
					GamePool pool = new GamePool(character.getGameData().getGameObjects());
					ArrayList<GameObject> characters = pool.find(CharacterWrapper.NAME_KEY);
					for (GameObject otherCharacterGo : characters) {
						CharacterWrapper otherCharacter = new CharacterWrapper(otherCharacterGo);
						if (otherCharacter.hasActiveInventoryThisKey(Constants.GATE_MASTER)) {
							otherCharacter.addOtherChitDiscovery(rc.getGameObject().getName());
						}
					}
				}
				return true;
			}
		}
		return false;
	}
	protected String doPaths(CharacterWrapper character) {
		ArrayList<PathDetail> list = getAllUndiscoveredPaths(character);
		for (PathDetail path:list) {
			character.addHiddenPathDiscovery(path.getFullPathKey());
		}
		QuestRequirementParams qp = new QuestRequirementParams();
		qp.actionName = getTableKey();
		qp.actionType = CharacterActionType.SearchTable;
		qp.searchType = SearchResultType.Paths;
		String ret = "Path(s)";
		if (list.size() > 0) {
			ret = "Found " + list.size() + " path(s)";
			qp.searchHadAnEffect = true;
		}
		character.testQuestRequirements(getParentFrame(),qp);
		return ret;
	}
	protected String doPassages(CharacterWrapper character) {
		ArrayList<PathDetail> list = getAllUndiscoveredPassages(character);
		for (PathDetail path:list) {
			character.addSecretPassageDiscovery(path.getFullPathKey());
		}
		
		QuestRequirementParams qp = new QuestRequirementParams();
		qp.actionName = getTableKey();
		qp.actionType = CharacterActionType.SearchTable;
		qp.searchType = SearchResultType.Passages;
		
		String ret = "Passage(s)";
		if (list.size()>0) {
			ret = "Found "+list.size()+" passage(s)";
			qp.searchHadAnEffect = true;
		}
		character.testQuestRequirements(getParentFrame(),qp);
		return ret;
	}
	protected String doHiddenEnemies(CharacterWrapper character) {
		QuestRequirementParams qp = new QuestRequirementParams();
		qp.actionName = getTableKey();
		qp.actionType = CharacterActionType.SearchTable;
		qp.searchType = SearchResultType.HiddenEnemies;
		qp.searchHadAnEffect = !character.foundHiddenEnemies();
		character.testQuestRequirements(getParentFrame(),qp);
		
		character.setFoundHiddenEnemies(true);
		return "Found hidden enemies";
	}
	protected String doGlimpse(CharacterWrapper character) {
		character.getGameObject().setThisAttribute(Constants.GLIMPSED_COUNTERS+character.getCurrentLocation().tile.getName(), character.getCurrentDayKey());
		doClues(character);
		return "Glimpse counters";
	}
	
	protected String doChoice1ed(CharacterWrapper character) {
		String hiddenEnemies = "Hidden enemies";
		String hiddenPaths = "Hidden paths";
		String secretPassages = "Secret passages";
		String findCounters = "Find counters";
		
		ImageIcon paths = getIconFromList(convertPathDetailToImageIcon(getAllUndiscoveredPaths(character)));
		ImageIcon passages = getIconFromList(convertPathDetailToImageIcon(getAllUndiscoveredPassages(character)));
		ImageIcon chits = getIconFromList(convertRealmComponentToImageIcon(getAllDiscoverableChits(character,true)));
		
		ButtonOptionDialog chooseSearch = new ButtonOptionDialog(getParentFrame(), null, "Choice:", "", false);

		chooseSearch.addSelectionObject(hiddenEnemies);
		chooseSearch.addSelectionObject(hiddenPaths);
		chooseSearch.setSelectionObjectIcon(hiddenPaths,paths);
		chooseSearch.addSelectionObject(secretPassages);
		chooseSearch.setSelectionObjectIcon(secretPassages,passages);
		chooseSearch.addSelectionObject(findCounters);
		chooseSearch.setSelectionObjectIcon(findCounters,chits);
		chooseSearch.setVisible(true);
		String choice = (String)chooseSearch.getSelectedObject();
		if (choice.equals(hiddenEnemies)) {
			return "Choice - "+doHiddenEnemies(character);
		}
		if (choice.equals(hiddenPaths)) {
			return "Choice - "+doPaths(character);
		}
		if (choice.equals(secretPassages)) {
			return "Choice - "+doPassages(character);
		}
		if (choice.equals(findCounters)) {
			return "Choice - "+doDiscoverChits(character);
		}

		return null;
	}
}