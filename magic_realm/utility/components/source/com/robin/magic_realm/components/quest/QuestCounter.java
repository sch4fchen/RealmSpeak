package com.robin.magic_realm.components.quest;

import com.robin.game.objects.*;

public class QuestCounter extends GameObjectWrapper {
	private static String TAG_FRONT = "<";
	private static String TAG_END = ">";
	private final String COUNT = "count";
	
	public QuestCounter(GameObject go) {
		super(go);
		go.setThisAttribute(COUNT, "0");
	}
	public QuestCounter(GameObject go, int count) {
		super(go);
		go.setThisAttribute(COUNT, count);
	}
	public Quest getParentQuest() {
		GameObject quest = getGameObject().getHeldBy();
		return new Quest(quest);
	}
	public String toString() {
		return getName();
	}
	public void init() {
		getGameObject().setThisAttribute(Quest.QUEST_COUNTER);
		getGameObject().setThisAttribute(COUNT, 0);
	}
	public String getBlockName() {
		return Quest.QUEST_BLOCK;
	}
	public String getTagName() {
		return TAG_FRONT + getName() + TAG_END;
	}
	public void increaseCount() {
		increaseCountByValue(1);
	}
	public void increaseCountByValue(int value) {
		setCount(getCount() + value);
	}
	public void decreaseCount() {
		decreaseCountByValue(1);
	}
	public void decreaseCountByValue(int value) {
		setCount(getCount() - value);
	}
	public int getCount() {
		return getGameObject().getThisInt(COUNT);
	}
	public void setCount(int count) {
		getGameObject().setThisAttribute(COUNT, count);
	}
}