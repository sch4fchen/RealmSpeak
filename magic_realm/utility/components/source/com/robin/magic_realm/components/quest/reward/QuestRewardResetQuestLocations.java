package com.robin.magic_realm.components.quest.reward;

import java.util.Hashtable;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardResetQuestLocations extends QuestReward {
	public static final String RESET_ALL_LOCATIONS = "_all_loc";
	public static final String LOCATION = "_loc";

	public QuestRewardResetQuestLocations(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame, CharacterWrapper character) {
		if (resetAllLocations() || getQuestLocation() == null) {
			Quest quest = getParentQuest();
			for (QuestLocation location : quest.getLocations()) {
				location.clearLockAddress();
				location.resolveQuestStart(frame, character);
			}
			return;
		}
		getQuestLocation().clearLockAddress();
		getQuestLocation().resolveQuestStart(frame, character);
	}

	private boolean resetAllLocations() {
		return getBoolean(RESET_ALL_LOCATIONS);
	}

	public String getDescription() {
		if (resetAllLocations()) {
			return "All quest locations are reset.";
		}
		return "Reset " + getQuestLocation().getName() + ".";
	}

	public RewardType getRewardType() {
		return RewardType.ResetQuestLocations;
	}

	public void setQuestLocation(QuestLocation location) {
		setString(LOCATION, location.getGameObject().getStringId());
	}

	public QuestLocation getQuestLocation() {
		String id = getString(LOCATION);
		if (id != null) {
			GameObject go = getGameData().getGameObject(Long.valueOf(id));
			if (go != null) {
				return new QuestLocation(go);
			}
		}
		return null;
	}

	public void updateIds(Hashtable<Long, GameObject> lookup) {
		updateIdsForKey(lookup, LOCATION);
	}
}