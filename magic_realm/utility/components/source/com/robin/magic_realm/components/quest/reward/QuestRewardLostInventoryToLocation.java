package com.robin.magic_realm.components.quest.reward;

import java.util.Hashtable;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardLostInventoryToLocation extends QuestReward {
	public static final String LOCATION = "_l";

	public QuestRewardLostInventoryToLocation(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		Quest quest = getParentQuest();
		quest.setLostInventoryRule(RewardType.LostInventoryToLocation);
		QuestLocation location = getQuestLocation();
		if (location.needsResolution()) {
			location.resolveStepStart(frame,character);
		}
		quest.setLostInventoryLocation(location);
	}
	
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("All lost inventory will be sent to ");
		sb.append(getQuestLocation());
		sb.append(".");
		return sb.toString();
	}

	public RewardType getRewardType() {
		return RewardType.LostInventoryToLocation;
	}
	
	public boolean usesLocationTag(String tag) {
		QuestLocation loc = getQuestLocation();
		return loc!=null && tag.equals(loc.getName());
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