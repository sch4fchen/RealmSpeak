package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JFrame;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardMarkedDenizensAbility extends QuestReward {
	public static final String CONTROL = "_control";	
	public static final String IMMUNITY = "_immunity";
	public static final String FEAR = "_fear";
	public static final String FRIENDLINESS = "_friendliness";
	public static final String REMOVE_UNCONTROLLED_HIRELINGS = "_remove_uncontrolled_hirelings";
	
	public QuestRewardMarkedDenizensAbility(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		String questId = getParentQuest().getGameObject().getStringId();
		if (controlSelected()) {
			character.getGameObject().addThisAttributeListItem(Constants.MONSTER_CONTROL_MARK, questId);
		} else {
			character.getGameObject().removeThisAttributeListItem(Constants.MONSTER_CONTROL_MARK, questId);
			if (removeUncontrolledHirelings()) {
				for (RealmComponent hireling : character.getAllHirelings()) {
					validateControlForHireling(hireling,character,questId);
				}
			}
		}
		if (immunitySelected()) {
			character.getGameObject().addThisAttributeListItem(Constants.MONSTER_IMMUNITY_MARK, questId);
		} else {
			character.getGameObject().removeThisAttributeListItem(Constants.MONSTER_IMMUNITY_MARK, questId);
		}
		if (fearSelected()) {
			character.getGameObject().addThisAttributeListItem(Constants.MONSTER_FEAR_MARK, questId);
		} else {
			character.getGameObject().removeThisAttributeListItem(Constants.MONSTER_FEAR_MARK, questId);
		}
		if (friendlinessSelected()) {
			character.getGameObject().addThisAttributeListItem(Constants.MONSTER_FRIENDLINESS_MARK, questId);
		} else {
			character.getGameObject().removeThisAttributeListItem(Constants.MONSTER_FRIENDLINESS_MARK, questId);
		}
		return;
	}
	
	public String getDescription() {	
		StringBuilder sb = new StringBuilder();
		boolean add = controlSelected() || immunitySelected() || fearSelected() || friendlinessSelected();
		boolean remove = !controlSelected() || !immunitySelected() || !fearSelected() || !friendlinessSelected();
		if (add) {
			sb.append("Adds ");
			if (controlSelected()) {
				sb.append("control/command ");
			}
			if (immunitySelected()) {
				sb.append("immunity ");
			}
			if (fearSelected()) {
				sb.append("fear ");
			}
			if (friendlinessSelected()) {
				sb.append("friendliness ");
			}
		}
		if (remove) {
			if (add) {
				sb.append("and removes ");
			} else {
				sb.append("Removes ");
			}
			if (!controlSelected()) {
				sb.append("control/command ");
			}
			if (!immunitySelected()) {
				sb.append("immunity ");
			}
			if (!fearSelected()) {
				sb.append("fear ");
			}
			if (!friendlinessSelected()) {
				sb.append("friendliness ");
			}
		}
		sb.append("for marked denizens.");
		return sb.toString();
	}
	
	public boolean controlSelected() {
		return getBoolean(CONTROL);
	}
	public boolean immunitySelected() {
		return getBoolean(IMMUNITY);
	}
	public boolean fearSelected() {
		return getBoolean(FEAR);
	}
	public boolean friendlinessSelected() {
		return getBoolean(FRIENDLINESS);
	}
	public boolean removeUncontrolledHirelings() {
		return getBoolean(REMOVE_UNCONTROLLED_HIRELINGS);
	}

	public RewardType getRewardType() {
		return RewardType.MarkedDenizensAbility;
	}
}