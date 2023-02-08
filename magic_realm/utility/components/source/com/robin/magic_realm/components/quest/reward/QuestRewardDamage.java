package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.DamageType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardDamage extends QuestReward {
	public static final String DAMAGE_TYPE = "_dt";
	public static final String AMOUNT = "_am";
	public static final String INCLUDE_FOLLOWERS = "_if";	
	
	public QuestRewardDamage(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		DamageType type = getDamageType();
		switch (type) {
		case WeatherFatigue:
			character.setWeatherFatigue(getAmount(),includeFollowers());
			break;
		case Wounds:
			character.setExtraWounds(getAmount(),includeFollowers());
			break;
		}	
		return;
	}
	
	public String getDescription() {	
		return "Character receives " +getAmount() + " " +getDamageType().toString().toLowerCase() +".";
	}

	public RewardType getRewardType() {
		return RewardType.Damage;
	}
	
	private DamageType getDamageType() {
		return DamageType.valueOf(getString(DAMAGE_TYPE));
	}
	
	private int getAmount() {
		return getInt(AMOUNT);
	}
	
	private boolean includeFollowers() {
		return getBoolean(INCLUDE_FOLLOWERS);
	}
}