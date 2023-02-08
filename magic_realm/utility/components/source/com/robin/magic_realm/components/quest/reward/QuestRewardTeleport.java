package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.quest.QuestStep;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardTeleport extends QuestReward {
	private static Logger logger = Logger.getLogger(QuestStep.class.getName());
	public static final String LOCATION = "_l";
	
	public QuestRewardTeleport(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		QuestLocation loc = getQuestLocation();		
		ArrayList<TileLocation> validLocations = new ArrayList<TileLocation>();
		validLocations = loc.fetchAllLocations(frame, character, getGameData());
		if(validLocations.isEmpty()) {
			logger.fine("QuestLocation "+loc.getName()+" doesn't have any valid locations!");
			return;
		}
		int random = RandomNumber.getRandom(validLocations.size());
		TileLocation tileLocation = validLocations.get(random);
		character.moveToLocation(frame, tileLocation);
	}
	
	public boolean usesLocationTag(String tag) {
		QuestLocation loc = getQuestLocation();
		return loc!=null && tag.equals(loc.getName());
	}
	
	public String getDescription() {
		return "Teleport to "+getQuestLocation().getName()+".";
	}

	public RewardType getRewardType() {
		return RewardType.Teleport;
	}

	public void setQuestLocation(QuestLocation location) {
		setString(LOCATION,location.getGameObject().getStringId());
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

	public void updateIds(Hashtable<Long, GameObject> lookup) {
		updateIdsForKey(lookup,LOCATION);
	}
}