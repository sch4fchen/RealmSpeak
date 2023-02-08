package com.robin.magic_realm.components.quest.reward;

import java.util.Hashtable;
import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.quest.QuestCounter;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardCounter extends QuestReward {
	public static final String COUNTER = "_c";
	public static final String SET_COUNT = "_sv";
	public static final String RANDOM = "_rnd";
	public static final String INCREASE_COUNT = "_iv";
	public static final String DECREASE_COUNT = "_dv";
	
	public QuestRewardCounter(GameObject go) {
		super(go);
	}
	
	public void processReward(JFrame frame,CharacterWrapper character) {
		QuestCounter counter = getQuestCounter();
		if (needToSetQuestCount()) {
			counter.setCount(getValueToSet());
		}
		if (needToSetQuestCountToRandom()) {
			counter.setCount(RandomNumber.getRandom(100)+1);
		}
		if (needToIncreaseQuestCount()) {
			counter.increaseCountByValue(getValueToIncrease());
		}
		if (needToDecreaseQuestCount()) {
			counter.decreaseCountByValue(getValueToDecrease());
		}
	}
	
	public String getDescription() {
		QuestCounter questCounter = getQuestCounter();
		if (questCounter==null) return "ERROR - No counter found!";
		
		StringBuffer sb = new StringBuffer();
		sb.append(getQuestCounter().getName());
		if (totalChange() != 0) {
			sb.append(" is changed by " +totalChange());
		}
		if (totalChange() != 0 && needToSetQuestCount()) {
			sb.append(" and");
		}
		if (needToSetQuestCount()) {
			sb.append(" is set to " +getValueToSet());
		}
		sb.append(".");
		return sb.toString();
	}
	public RewardType getRewardType() {
		return RewardType.Counter;
	}
	public QuestCounter getQuestCounter() {
		String id = getString(COUNTER);
		if (id!=null) {
			GameObject go = getGameData().getGameObject(Long.valueOf(id));
			if (go!=null) {
				return new QuestCounter(go, go.getThisInt("count"));
			}
		}
		return null;
	}
	public int getValueToSet() {
		return getInt(SET_COUNT);
	}
	public int getValueToIncrease() {
		return getInt(INCREASE_COUNT);
	}
	public int getValueToDecrease() {
		return getInt(DECREASE_COUNT);
	}		
	private int totalChange() {
		return getValueToIncrease()-getValueToDecrease();
	}
	private boolean needToSetQuestCount() {
		return getValueToSet() != QuestConstants.ALL_VALUE;
	}
	private boolean needToSetQuestCountToRandom() {
		return getBoolean(RANDOM);
	}
	private boolean needToIncreaseQuestCount() {
		return getValueToIncrease() != 0;
	}
	private boolean needToDecreaseQuestCount() {
		return getValueToDecrease() != 0;
	}
	public void updateIds(Hashtable<Long, GameObject> lookup) {
		updateIdsForKey(lookup,COUNTER);
	}
}