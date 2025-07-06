package com.robin.magic_realm.components.quest;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.*;
import com.robin.general.swing.ButtonOptionDialog;
import com.robin.general.swing.ImageCache;
import com.robin.general.util.*;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.map.Tile;

public class QuestLocation extends GameObjectWrapper {
	private static String TAG_FRONT = "<";
	private static String TAG_END = ">";
	
	private static final String TYPE = "_t";
	private static final String LOCK_ADDRESS = "_la";
	private static final String CHOICE_ADDRESSES = "_ca";
	private static final String HIDE_NOTIFICATION = "_hn";
	
	private static final String SAME_TILE = "_st";
	private static final String LOC_CLEARING_TYPE = "_ct";
	private static final String LOC_TILE_SIDE_TYPE = "_tst";
	
	private static final String LOC_FOR_CLONED_QUESTS = "_cloned_quests";
	
	public QuestLocation(GameObject go) {
		super(go);
	}
	public String getDescription() {
		ArrayList<String> list = getChoiceAddresses();
		String locList = list == null ? "" : StringUtilities.collectionToString(list, ",");
		LocationType type = getLocationType();
		LocationClearingType lc = getLocationClearingType();
		LocationTileSideType ts = getLocationTileSideType();
		
		StringBuilder sb = new StringBuilder();
		sb.append(type.getDescriptionPrefix());
		sb.append(" ");
		if (ts != LocationTileSideType.Any) {
			sb.append(ts.toString().toLowerCase());
			sb.append(" ");
		}
		if (lc != LocationClearingType.Any) {
			sb.append(lc.toString().toLowerCase());
			sb.append(" ");
		}
		sb.append("clearing");
		if (locList.trim().length()>0) {
			if (isSameTile()) {
				sb.append(" in the same tile as ");
			}
			else {
				sb.append(" containing ");
			}
			sb.append(locList);
		}
		sb.append(".");
		return sb.toString();
	}
	private Quest getParentQuest() {
		GameObject quest = getGameObject().getHeldBy();
		return new Quest(quest);
	}
	private String getQuestName() {
		GameObject quest = getGameObject().getHeldBy();
		return quest.getName();
	}
	private ArrayList<String> getValidAddresses() {
		ArrayList<String> addresses = new ArrayList<>();
		String lock = getLockAddress();
		if (lock!=null) {
			addresses.add(lock);
		}
		else {
			ArrayList<String> choices = getChoiceAddresses();
			if (choices!=null) {
				addresses.addAll(choices); // the only time this should happen, is with "Any" or "Lock"
			}
		}
		return addresses;
	}
	public RealmComponent[] allPiecesForLocationClearings(JFrame frame,CharacterWrapper character) {
		if (needsResolution()) {
			if (getLocationType()==LocationType.Lock) {
				RealmLogging.logMessage(QuestConstants.QUEST_ERROR,"Can't fetch chits for a LOCK type of location without requiring the character to first visit that location.");
				return null;
			}
			resolveStepStart(frame,character);
		}
		
		ArrayList<RealmComponent> allPieces = new ArrayList<>();
		
		ArrayList<String> addresses = getValidAddresses();
		if (addresses.isEmpty()) {
			ArrayList<TileLocation> validLocations = fetchAllLocations(getGameData());
			for (TileLocation tl : validLocations) {
				allPieces.addAll(tl.tile.getAllClearingComponents());
			}
			return allPieces.toArray(new RealmComponent[0]);
		}
		
		LocationTileSideType tileSideType = getLocationTileSideType();
		LocationClearingType clearingType = getLocationClearingType();
		
		for(String address:addresses) {
			ArrayList<RealmComponent> pieces = fetchPieces(getGameData(),address,false);
			if (pieces!=null) {
				for (RealmComponent rc : pieces) {
					TileLocation location = rc.getCurrentLocation();					
					if (location != null && tileSideType.matches(location.tile) && clearingType.matches(location.clearing)) {
						allPieces.add(rc);
					}
				}
			}
			else {
				TileLocation tl = fetchTileLocation(getGameData(),address);
				ArrayList<TileLocation> validLocations = getAllAllowedClearingsForTileLocation(tl);
				if (validLocations != null) {
					for (TileLocation validLocation : validLocations) {	
						for(RealmComponent rc : validLocation.clearing.getClearingComponents()) {
							if (rc.isChit()) {
								allPieces.add(rc);
							}
						}
					}
				}
			}
		}
		return allPieces.toArray(new RealmComponent[0]);
	}
	/**
	 * This will test to see if the character is at a location supported by this object.  If the location type is "Lock" then any success will also lock the address.
	 * 
	 * Note that if specificObject is used, then ONLY that component is considered (like when doing a search)
	 * 
	 * @return		true if the character is at "this" location
	 */
	public boolean locationMatchAddress(JFrame frame,CharacterWrapper character) {
		return locationMatchAddress(frame,character,null);
	}
	public boolean locationMatchAddress(JFrame frame,CharacterWrapper character,GameObject specificObject) {
		LocationType type = getLocationType();
		if (type!=LocationType.Lock && needsResolution()) { 
			// If this is not a "Lock" type, and still needs resolution (random or choice), then resolve that first.
			resolveStepStart(frame,character);
		}
		
		ArrayList<String> addressesToTest = getValidAddresses();
		
		TileLocation current = character.getCurrentLocation();
		
		LocationClearingType clearingType = getLocationClearingType();
		LocationTileSideType tileSideType = getLocationTileSideType();
		
		if (clearingType!=LocationClearingType.Any && !clearingType.matches(current.clearing)) return false;
		if (tileSideType!=LocationTileSideType.Any && !tileSideType.matches(current.tile)) return false;
		
		if (addressesToTest.isEmpty()) return true; // If there are NO addresses, then anything is allowed
		
		ArrayList<RealmComponent> clearingComponents;
		if (specificObject!=null) {
			clearingComponents= new ArrayList<>();
			clearingComponents.add(RealmComponent.getRealmComponent(specificObject));
		}
		else {
			if (isSameTile()) {
				clearingComponents = current.tile.getAllClearingComponents();
				clearingComponents.addAll(current.tile.getOffroadRealmComponents());
			}
			else {
				clearingComponents = current.tile.getOffroadRealmComponents();
				if (current.clearing != null) {
					clearingComponents.addAll(current.clearing.getClearingComponents());
				}
			}
			for(GameObject go:character.getInventory()) {
				clearingComponents.add(RealmComponent.getRealmComponent(go));
			}
			for(RealmComponent rc:character.getFollowingHirelings()) {
				clearingComponents.add(RealmComponent.getRealmComponent(rc.getGameObject()));
			}
		}
		String matchingAddress = null;
		for(String address:addressesToTest) {
			TileLocation tl = fetchTileLocation(getGameData(),address);
			if (tl!=null) {
				if ((isSameTile() && tl.tile.equals(current.tile))
					|| tl.equals(current)) {
						matchingAddress = address;
						break;
				}
			}
			ArrayList<RealmComponent> pieces = fetchPieces(getGameData(),address,true);
			if (CollectionUtility.containsAny(clearingComponents,pieces)) {
				matchingAddress = address;
				break;
			}
		}
		if (matchingAddress!=null) {
			if (type==LocationType.Lock && getLockAddress()==null && addressesToTest.size()>1) {
				// Lock address down
				setLockAddress(matchingAddress);
				String message = getTagName()+" is at the "+matchingAddress;
				character.addNote(getGameObject(),getQuestName(),message);
				if (locationForClonedQuests()) {
					setLocationAddressForClonedQuests(message);
				}
			}
			return true;
		}
		
		return false;
	}
	public boolean locationMatchAddressForRealmComponent(JFrame frame,CharacterWrapper character, RealmComponent rc) {
		LocationType type = getLocationType();
		if (needsResolution() && frame != null && character != null) {
			if (type==LocationType.Lock) {
				RealmLogging.logMessage(QuestConstants.QUEST_ERROR,"Can't fetch locations for a LOCK type of location without requiring the character to first visit that location.");
				return false;
			}
			resolveStepStart(frame,character);
		}
		TileLocation loc = rc.getCurrentLocation();
		if (loc == null) {
			return false;
		}
		
		ArrayList<String> addressesToTest = getValidAddresses();
		LocationClearingType clearingType = getLocationClearingType();
		LocationTileSideType tileSideType = getLocationTileSideType();
		
		if (clearingType!=LocationClearingType.Any && !clearingType.matches(loc.clearing)) return false;
		if (tileSideType!=LocationTileSideType.Any && !tileSideType.matches(loc.tile)) return false;
		if (addressesToTest.isEmpty()) return true; // If there are NO addresses, then anything is allowed
		
		ArrayList<RealmComponent> clearingComponents;
		if (isSameTile()) {
			clearingComponents = loc.tile.getAllClearingComponents(); 
		}
		else {
			clearingComponents = loc.tile.getOffroadRealmComponents(); // state chits without a clearing
			clearingComponents.addAll(loc.clearing.getClearingComponents());
		}
		String matchingAddress = null;
		for(String address:addressesToTest) {
			TileLocation tl = fetchTileLocation(getGameData(),address);
			if (tl!=null) {
				if ((isSameTile() && tl.tile.equals(loc.tile))
					|| tl.equals(loc)) {
						matchingAddress = address;
						break;
				}
			}
			ArrayList<RealmComponent> pieces = fetchPieces(getGameData(),address,true);
			if (CollectionUtility.containsAny(clearingComponents,pieces)) {
				matchingAddress = address;
				break;
			}
		}
		if (matchingAddress!=null) {
			return true;
		}
		
		return false;
	}
	public void resolveQuestStart(JFrame frame,CharacterWrapper character) {
		if (locationForClonedQuests()) {
			if (setLocationAddressByClonedQuest()) return;
		}
		
		ArrayList<String> choices = getChoiceAddresses();
		if (choices==null || choices.size()==0) return;
		if (choices.size()==1) {
			// This is easy
			setLockAddress(choices.get(0));
			if (locationForClonedQuests()) {
				setLocationAddressByClonedQuest();
			}
			return;
		}
		
		String message = null;
		// More than one choice...
		LocationType type = getLocationType();
		if (type==LocationType.QuestRandom) {
			int r = RandomNumber.getRandom(choices.size());
			setLockAddress(choices.get(r));
			message = getTagName()+" is at the "+getLockAddress().toUpperCase();
			character.addNote(getGameObject(),getQuestName(),message);
			if (!hideNotification()) {
				Quest.showQuestMessage(frame,getParentQuest(),message,getGameObject().getHeldBy().getName());
			}
		}
		else if (type==LocationType.QuestChoice) {
			// Allow the player to pick from the list
			forcePlayerPick(frame,choices);
			message = getTagName()+" is at the "+getLockAddress().toUpperCase();
			character.addNote(getGameObject(),getQuestName(),message);
		}
		// All others are ignored at this point
		
		if (message!=null && locationForClonedQuests()) {
			setLocationAddressForClonedQuests(message);
		}
	}
	public void resolveStepStart(JFrame frame,CharacterWrapper character) {
		if (!needsResolution()) return;
		
		if (locationForClonedQuests()) {
			if (setLocationAddressByClonedQuest()) return;
		}
		
		String message = null;
		ArrayList<String> choices = getChoiceAddresses();
		LocationType type = getLocationType();
		if (type==LocationType.StepRandom) {
			int r = RandomNumber.getRandom(choices.size());
			setLockAddress(choices.get(r));
			message = getTagName()+" is at the "+getLockAddress().toUpperCase();
			character.addNote(getGameObject(),getQuestName(),message);
			if (!hideNotification()) {
				Quest.showQuestMessage(frame,getParentQuest(),message,getGameObject().getHeldBy().getName());
			}
		}
		else if (type==LocationType.StepChoice) {
			// Allow the player to pick from the list
			forcePlayerPick(frame,choices);
			message = getTagName()+" is at the "+getLockAddress().toUpperCase();
			character.addNote(getGameObject(),getQuestName(),message);
		}
		if (message!=null && locationForClonedQuests()) {
			setLocationAddressForClonedQuests(message);
		}
	}
	private void forcePlayerPick(JFrame frame,ArrayList<String> choices) {
		// assume that the location type was already verified (needs to be QuestChoice or StepChoice) ...
		GameObject quest = getGameObject().getHeldBy();
		ButtonOptionDialog chooser = new ButtonOptionDialog(frame,ImageCache.getIcon("quests/token"),"Which location would you like to choose for "+getTagName()+"?",quest.getName(),false);
		for(String choice:choices) {
			chooser.addSelectionObject(choice);
		}
		chooser.setVisible(true);
		String selected = chooser.getSelectedObject().toString();
		setLockAddress(selected);
	}
	public boolean needsResolution() {
		return getLockAddress()==null && getLocationType()!=LocationType.Any;
	}
	public String toString() {
		return getName();
	}
	public void init() {
		getGameObject().setThisAttribute(Quest.QUEST_LOCATION);
	}
	public String getBlockName() {
		return Quest.QUEST_BLOCK;
	}
	
	public String getTagName() {
		return TAG_FRONT + getName() + TAG_END;
	}
	
	public void setLocationType(LocationType type) {
		setString(TYPE,type.toString());
	}
	
	public LocationType getLocationType() {
		String val = getString(TYPE);
		return LocationType.valueOf(val);
	}
	
	public void setLockAddress(String val) {
		setString(LOCK_ADDRESS,val);
	}
	
	public String getLockAddress() {
		return getString(LOCK_ADDRESS);
	}
	
	public void clearLockAddress() {
		removeAttribute(LOCK_ADDRESS);
	}
	
	public void addChoiceAddresses(String val) {
		addListItem(CHOICE_ADDRESSES,val);
	}
	
	public void clearChoiceAddresses() {
		clear(CHOICE_ADDRESSES);
	}
	
	public boolean hideNotification() {
		return getBoolean(HIDE_NOTIFICATION);
	}
	
	public boolean locationForClonedQuests() {
		return getBoolean(LOC_FOR_CLONED_QUESTS);
	}
	
	public void setHideNotification(boolean val) {
		setBoolean(HIDE_NOTIFICATION,val);
	}
	
	public void setLocationForClonedQuests(boolean val) {
		setBoolean(LOC_FOR_CLONED_QUESTS,val);
	}
	
	public ArrayList<String> getChoiceAddresses() {
		return getList(CHOICE_ADDRESSES);
	}
	
	public void setSameTile(boolean val) {
		setBoolean(SAME_TILE,val);
	}
	
	public boolean isSameTile() {
		return getBoolean(SAME_TILE);
	}
	
	public void setLocationClearingType(LocationClearingType lt) {
		setString(LOC_CLEARING_TYPE,lt.toString());
	}
	
	public LocationClearingType getLocationClearingType() {
		String val = getString(LOC_CLEARING_TYPE);
		return val==null?LocationClearingType.Any:LocationClearingType.valueOf(val);
	}
	
	public void setLocationTileSideType(LocationTileSideType le) {
		setString(LOC_TILE_SIDE_TYPE,le.toString());
	}
	
	public LocationTileSideType getLocationTileSideType() {
		String val = getString(LOC_TILE_SIDE_TYPE);
		return val==null?LocationTileSideType.Any:LocationTileSideType.valueOf(val);
	}
	
	public ArrayList<TileLocation> getAllAllowedClearingsForTileLocation(TileLocation location) {
		LocationTileSideType tileSideType = getLocationTileSideType();
		LocationClearingType clearingType = getLocationClearingType();
		ArrayList<TileLocation> locations = new ArrayList<>();
		ArrayList<ClearingDetail> clearingsToCheck = new ArrayList<>();
		if (location == null) {
			return null;
		}
		if (location.clearing == null) {
			for(ClearingDetail cl : location.tile.getClearings()) {
				clearingsToCheck.add(cl);
			}
		}
		else {
			clearingsToCheck.add(location.clearing);
		}
		
		for (ClearingDetail cl : clearingsToCheck) {
			if (tileSideType.matches(location.tile) && clearingType.matches(cl)) {
				TileLocation validLocation = new TileLocation(location.tile, cl, false);
				locations.add(validLocation);
			}
		}
		
		return locations;
	}
	
	public static TileLocation fetchTileLocation(GameData gameData,String val) {
		return fetchTileLocation(gameData,val,true);
	}
	
	public static TileLocation fetchTileLocation(GameData gameData,String val,boolean tileMustBePlaced) {
		// Tile coordinate (like AV2)
		try {
			return TileLocation.parseTileLocationNoPartway(gameData,val.toUpperCase());
		}
		catch(Exception ex) {
			// ignore exception - this just means its NOT a tile coordinate
		}
		
		// Tile name and clearing (like Awful Valley 2)
		Pattern pattern = Pattern.compile("([a-zA-Z\\s]+)(\\d*)");
		Matcher match = pattern.matcher(val);
		if (match.matches()) {
			String tileName = match.group(1).trim();
			String clearingNumString = match.group(2).trim();
			int clearingNum = clearingNumString.length()>0 ? Integer.parseInt(clearingNumString) : -1;
			GameObject go = gameData.getGameObjectByNameIgnoreCase(tileName);
			if (go!=null) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (rc != null && rc.isTile() && (!tileMustBePlaced || go.getAttribute(Tile.MAP_GRID, Tile.MAP_POSITION) != null)) {
					TileComponent tile = (TileComponent)rc;
					ClearingDetail clearing = clearingNum>0?tile.getClearing(clearingNum):null;
					return new TileLocation(tile,clearing,false);
				}
			}
		}
		//Tile name alone
		pattern = Pattern.compile("([a-zA-Z\\s]+)");
		match = pattern.matcher(val);
		if (match.matches()) {
			String tileName = match.group(1).trim();
			GameObject go = gameData.getGameObjectByNameIgnoreCase(tileName);
			if (go!=null) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (rc != null && rc.isTile() && (!tileMustBePlaced || go.getAttribute(Tile.MAP_GRID, Tile.MAP_POSITION) != null)) {
					TileComponent tile = (TileComponent)rc;
					return new TileLocation(tile,false);
				}
			}
		}
		
		return null;
	}

	private static ArrayList<RealmComponent> fetchPieces(GameData gameData, String val,boolean onlySeen) {
		ArrayList<GameObject> gos = gameData.getGameObjectsByNameIgnoreCase(val);
		if (gos.isEmpty()) return null;
		ArrayList<RealmComponent> ret = new ArrayList<>();
		for (GameObject go : gos) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc==null) continue;
			if (rc.isStateChit() || rc.isDwelling() || rc.isMonster() || rc.isNative() || rc.isItem() || rc.isGoldSpecial() || rc.isTreasureLocation() || rc.isGuild() || rc.isGate() || rc.isVisitor() || rc.isTraveler()) {
				if (!onlySeen || !rc.isStateChit() || rc.getGameObject().hasThisAttribute("seen")) {
					ret.add(rc);
				}
			}
		}
		return ret.isEmpty() ? null : ret;
	}
	
	public ArrayList<TileLocation> fetchAllLocations(JFrame frame, CharacterWrapper character, GameData gameData) {
		if (needsResolution() && frame != null && character != null) {
			if (getLocationType()==LocationType.Lock) {
				RealmLogging.logMessage(QuestConstants.QUEST_ERROR,"Can't fetch locations for a LOCK type of location without requiring the character to first visit that location.");
				return null;
			}
			resolveStepStart(frame,character);
		}
		return fetchAllLocations(gameData);
	}
	
	public ArrayList<TileLocation> fetchAllLocations(GameData gameData) {
		ArrayList<String> addresses = getValidAddresses();
		ArrayList<TileLocation> allTileLocations = new ArrayList<>();
		
		if (addresses.isEmpty()) {
			ArrayList<GameObject> gameObjects = getGameObject().getGameData().getGameObjects();
			for (GameObject go : gameObjects) {
				if(go.hasThisAttribute("tile")) {
					TileComponent tc = (TileComponent) RealmComponent.getRealmComponent(go);
					TileLocation tileLocation = new TileLocation(tc);
					ArrayList<TileLocation> validLocations = this.getAllAllowedClearingsForTileLocation(tileLocation);
					if (validLocations != null) {
						allTileLocations.addAll(this.getAllAllowedClearingsForTileLocation(tileLocation));
					}
				}
			}
		}
		
		for (String address : addresses) {
			TileLocation location = fetchTileLocation(gameData, address);
			ArrayList<TileLocation> validLocations = this.getAllAllowedClearingsForTileLocation(location);
			if (validLocations != null) {
				allTileLocations.addAll(validLocations);
			}
			ArrayList<GameObject> gos = gameData.getGameObjectsByNameIgnoreCase(address);
			if (!gos.isEmpty()) {
				for (GameObject go : gos) {
					RealmComponent rc = RealmComponent.getRealmComponent(go);
					if (rc==null) continue;
					ArrayList<TileLocation> validPieceLocations = this.getAllAllowedClearingsForTileLocation(rc.getCurrentLocation());
					if (validPieceLocations != null) {
						allTileLocations.addAll(validPieceLocations);
					}
				}
			}
		}
		
		return allTileLocations;
	}
	
	public static boolean validLocation(GameData gameData,String val) {
		return fetchTileLocation(gameData,val,false)!=null || fetchPieces(gameData,val,false)!=null;
	}
	
	private boolean setLocationAddressByClonedQuest() {
		GameObject questGo = getGameObject().getHeldBy();
		Quest quest = new Quest(questGo);
		for (GameObject clonedQuestGo : quest.findClones(getGameData().getGameObjects())) {
			Quest clonedQuest = new Quest(clonedQuestGo);
			for (QuestLocation loc : clonedQuest.getLocations()) {
				if (loc.getTagName().matches(this.getTagName())) {
					if (loc.getLockAddress()!=null&&!loc.getLockAddress().isEmpty()) {
						this.setLockAddress(loc.getLockAddress());
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private void setLocationAddressForClonedQuests(String message) {
		if (this.getLockAddress()==null || this.getLockAddress().isEmpty()) return;
		GameObject questGo = getGameObject().getHeldBy();
		Quest quest = new Quest(questGo);
		for (GameObject clonedQuestGo : quest.findClones(getGameData().getGameObjects())) {
			Quest clonedQuest = new Quest(clonedQuestGo);
			for (QuestLocation loc : clonedQuest.getLocations()) {
				if (loc.getTagName().matches(this.getTagName())) {
					loc.setLockAddress(this.getLockAddress());
					if (message!=null && clonedQuest.getOwner()!=null) {
						clonedQuest.getOwner().addNote(loc.getGameObject(),loc.getQuestName(), message);
					}
				}
			}
		}
	}
}