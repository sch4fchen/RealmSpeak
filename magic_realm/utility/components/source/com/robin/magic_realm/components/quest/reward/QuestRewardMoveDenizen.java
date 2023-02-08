package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.PathDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardMoveDenizen extends QuestReward {
	
	public enum MoveOption {
		CharactersTile,
		CharactersClearing,
		Location
	}
	
	public enum MoveFromOption {
		Everywhere,
		CharactersTile,
		CharactersClearing
	}
	
	public enum ClearingSelection {
		Random,
		One,
		Two,
		Three,
		Four,
		Five,
		Six,
		RandomForEachDenizen
	}
	
	public static final String DENIZEN_REGEX = "_drx";
	public static final String MOVE_FROM_OPTION = "_mfo";
	public static final String MOVE_OPTION = "_mo";
	public static final String CLEARING = "_cl";
	public static final String LOCATION = "_loc";
	public static final String AMOUNT = "_amnt";
	public static final String MOVE_HIRELINGS = "_mh";
	public static final String MOVE_COMPANIONS = "_mc";
	public static final String MOVE_SUMMONED = "_ms";
	public static final String MOVE_LIMITED = "_ml";
	
	public QuestRewardMoveDenizen(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		ArrayList<GameObject> denizens = character.getGameData().getGameObjectsByNameRegex(getDenizenNameRegex());
		QuestLocation loc = getQuestLocation();
		TileLocation charactersLoc= character.getCurrentLocation();
		ArrayList<GameObject> denizensToMove = new ArrayList<>();
		for (GameObject denizen : denizens) {
			RealmComponent denizenRc = RealmComponent.getRealmComponent(denizen);
			if (denizenRc == null || !denizenRc.isDenizen()) continue;
			TileLocation denizenLoc = denizenRc.getCurrentLocation();
			if(getMoveFromOption() == MoveFromOption.CharactersClearing
					&& (denizenLoc == null || charactersLoc == null || denizenLoc.tile != charactersLoc.tile || denizenLoc.clearing != charactersLoc.clearing)) {
				continue;
			}
			if(getMoveFromOption() == MoveFromOption.CharactersTile && (denizenLoc == null || charactersLoc == null || denizenLoc.tile != charactersLoc.tile)) {
				continue;
			}
			if (!moveHirelings() && denizen.hasThisAttribute(Constants.HIRELING)) {
				continue;
			}
			if (!moveCompanions() && denizen.hasThisAttribute(Constants.COMPANION)) {
				continue;
			}
			if (!moveSummoned() && denizen.hasThisAttribute(Constants.SUMMONED)) {
				continue;
			}
			if (moveOnlyHirelingsCompanionsSummonedMonsters() && !denizen.hasThisAttribute(Constants.HIRELING) && !denizen.hasThisAttribute(Constants.COMPANION) && !denizen.hasThisAttribute(Constants.SUMMONED)) {
				continue;
			}
			denizensToMove.add(denizen);
		}
		
		if (numberOfDenizens()!=0) {
			Collections.shuffle(denizensToMove);
		}
		
		switch (getMoveOption()) {
			case CharactersClearing:
				if(character.getCurrentLocation() == null || character.getCurrentLocation().clearing == null) return;
				TileLocation charactersClearing = character.getCurrentLocation();
				moveDenizens(charactersClearing, denizensToMove, numberOfDenizens());
				return;
			case CharactersTile:
				if(character.getCurrentLocation() == null) return;
				TileLocation charactersTile = character.getCurrentLocation();
				moveDenizensToTile(charactersTile, denizensToMove, numberOfDenizens());
				return;
			case Location:
				ArrayList<TileLocation> locations = loc.fetchAllLocations(frame, character, character.getGameData());
				moveDenizensToLocation(locations, denizensToMove);
				break;
		}
	}
	
	private static void moveDenizens(TileLocation locationToMove, ArrayList<GameObject> denizensToMove, int numberOfDenizensToMove) {
		int movedDenizens = 0;
		for (GameObject denizen : denizensToMove) {
			if(!locationToMove.clearing.isEdge()) {
				locationToMove.clearing.add(denizen,null);
			} else {
				ArrayList<PathDetail> path = locationToMove.clearing.getConnectedPaths();
				ClearingDetail connectedClearing = path.get(0).findConnection(locationToMove.clearing);
				connectedClearing.add(denizen,null);
			}
			movedDenizens++;
			if (numberOfDenizensToMove!=0 && movedDenizens>=numberOfDenizensToMove) {
				return;
			}
		}
	}
	private static void moveDenizensToClearing(TileLocation locationToMove, int clearingNumber, ArrayList<GameObject> denizensToMove, int numberOfDenizensToMove) {
		locationToMove.clearing = locationToMove.tile.getClearing(clearingNumber);
		if (locationToMove.clearing != null) {
			moveDenizens(locationToMove, denizensToMove, numberOfDenizensToMove);
		}
	}
	private void moveDenizensToTile(TileLocation tileToMove, ArrayList<GameObject> denizensToMove, int numberOfDenizensToMove) {
		switch (getClearing()) {
			case Random:
				int random = RandomNumber.getRandom(tileToMove.tile.getClearingCount());
				tileToMove.clearing = tileToMove.tile.getClearings().get(random);
				moveDenizens(tileToMove, denizensToMove, numberOfDenizensToMove);
				return;
			case One:
				moveDenizensToClearing(tileToMove, 1, denizensToMove, numberOfDenizensToMove);
				break;
			case Two:
				moveDenizensToClearing(tileToMove, 2, denizensToMove, numberOfDenizensToMove);
				break;
			case Three:
				moveDenizensToClearing(tileToMove, 3, denizensToMove, numberOfDenizensToMove);
				break;
			case Four:
				moveDenizensToClearing(tileToMove, 4, denizensToMove, numberOfDenizensToMove);
				break;
			case Five:
				moveDenizensToClearing(tileToMove, 5, denizensToMove, numberOfDenizensToMove);
				break;
			case Six:
				moveDenizensToClearing(tileToMove, 6, denizensToMove, numberOfDenizensToMove);
				break;
			case RandomForEachDenizen:
				int movedDenizens = 0;
				for (GameObject denizen : denizensToMove) {
					int randomForDenizen = RandomNumber.getRandom(tileToMove.tile.getClearingCount());
					tileToMove.clearing = tileToMove.tile.getClearings().get(randomForDenizen);
					tileToMove.clearing.add(denizen, null);
					movedDenizens++;
					if (numberOfDenizens() != 0 && movedDenizens>=numberOfDenizens()) {
						break;
					}
				}
			}
	}
	private void moveDenizensToLocation(ArrayList<TileLocation> locations, ArrayList<GameObject> denizensToMove) {
		int movedDenizens = 0;
		int numberOfDenizensToMove = numberOfDenizens();
		switch (getClearing()) {
			case Random:
				int random = RandomNumber.getRandom(locations.size());
				TileLocation locationToMove = locations.get(random);
				for (GameObject denizen : denizensToMove) {
					locationToMove.clearing.add(denizen, null);
					movedDenizens++;
					if (numberOfDenizensToMove != 0 && movedDenizens>=numberOfDenizensToMove) {
						break;
					}
				}
				break;
			case One:
				moveDenizensToLocationToClearing(locations, 1, denizensToMove, numberOfDenizensToMove);
				break;
			case Two:
				moveDenizensToLocationToClearing(locations, 2, denizensToMove, numberOfDenizensToMove);
				break;
			case Three:
				moveDenizensToLocationToClearing(locations, 3, denizensToMove, numberOfDenizensToMove);
				break;
			case Four:
				moveDenizensToLocationToClearing(locations, 4, denizensToMove, numberOfDenizensToMove);
				break;
			case Five:
				moveDenizensToLocationToClearing(locations, 5, denizensToMove, numberOfDenizensToMove);
				break;
			case Six:
				moveDenizensToLocationToClearing(locations, 6, denizensToMove, numberOfDenizensToMove);
				break;
			case RandomForEachDenizen:
				for (GameObject denizen : denizensToMove) {
					int randomForDenizen = RandomNumber.getRandom(locations.size());
					TileLocation locationToMoveForDenizen = locations.get(randomForDenizen);
					locationToMoveForDenizen.clearing.add(denizen, null);
					movedDenizens++;
					if (numberOfDenizensToMove != 0 && movedDenizens>=numberOfDenizensToMove) {
						break;
					}
				}
				break;
			}
	}
	private static void moveDenizensToLocationToClearing(ArrayList<TileLocation> locations, int clearingNumber, ArrayList<GameObject> denizensToMove, int numberOfDenizensToMove) {
		ArrayList<TileLocation> validLocations = new ArrayList<>();
		for (TileLocation loc : locations) {
			if (loc.clearing.getNum() == clearingNumber) {
				validLocations.add(loc);
			}
		}
		if (!validLocations.isEmpty()) {
			int random = RandomNumber.getRandom(validLocations.size());
			TileLocation selectedLocation = validLocations.get(random);
			moveDenizens(selectedLocation, denizensToMove, numberOfDenizensToMove);
		}
	}
	
	public String getDescription() {
		StringBuffer sb = new StringBuffer();
		if (numberOfDenizens()!=0) {
			sb.append("Up to "+numberOfDenizens()+" ");
		}
		sb.append(getDenizenNameRegex() +" are moved to ");
		if (getMoveOption() == MoveOption.Location && getQuestLocation() != null) {
			sb.append(getQuestLocation().getName());
		}
		else {
			sb.append(getMoveOption());
		}
		sb.append(".");
		return sb.toString();
	}
	private String getDenizenNameRegex() {
		return getString(DENIZEN_REGEX);
	}
	private MoveFromOption getMoveFromOption() {
		String moveFromOption = getString(MOVE_FROM_OPTION);
		if (moveFromOption == null) return MoveFromOption.Everywhere;
		return MoveFromOption.valueOf(moveFromOption);
	}
	private MoveOption getMoveOption() {
		return MoveOption.valueOf(getString(MOVE_OPTION));
	}
	private ClearingSelection getClearing() {
		return ClearingSelection.valueOf(getString(CLEARING));
	}
	private int numberOfDenizens() {
		return getInt(AMOUNT);
	}
	private Boolean moveHirelings() {
		return getBoolean(MOVE_HIRELINGS);
	}
	private Boolean moveCompanions() {
		return getBoolean(MOVE_COMPANIONS);
	}
	private Boolean moveSummoned() {
		return getBoolean(MOVE_SUMMONED);
	}
	private Boolean moveOnlyHirelingsCompanionsSummonedMonsters() {
		return getBoolean(MOVE_LIMITED);
	}
	public RewardType getRewardType() {
		return RewardType.MoveDenizen;
	}
	public boolean usesLocationTag(String tag) {
		QuestLocation loc = getQuestLocation();
		return loc!=null && tag.equals(loc.getName());
	}
	public QuestLocation getQuestLocation() {
		String id = getString(LOCATION);
		if (id!=null) {
			GameObject go = getGameData().getGameObject(Long.valueOf(id));
			if (go!=null) {
				return new QuestLocation(go);
			}
		}
		return null;
	}
	
	public void setQuestLocation(QuestLocation location) {
		setString(LOCATION,location.getGameObject().getStringId());
	}
	public void updateIds(Hashtable<Long, GameObject> lookup) {
		updateIdsForKey(lookup,LOCATION);
	}
	
}