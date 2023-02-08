package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.attribute.Speed;
import com.robin.magic_realm.components.quest.DieRollType;
import com.robin.magic_realm.components.table.PowerOfThePit;
import com.robin.magic_realm.components.utility.DieRollBuilder;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardPowerOfThePit extends QuestReward {
	
	public static final String DIE_ROLL = "_dr";
	
	public QuestRewardPowerOfThePit(GameObject go) {
		super(go);
	}

	@Override
	public void processReward(JFrame frame, CharacterWrapper character) {
		PowerOfThePit powerOfthePit = new PowerOfThePit(frame, character.getGameObject(),new Speed(0));
		DieRoller roller;
		if (getString(DIE_ROLL).equals(DieRollType.Random.toString())) {
			roller = DieRollBuilder.getDieRollBuilder(frame, character, getDieRoll()).createRoller(powerOfthePit);
		}
		else {
			roller = DieRollBuilder.getDieRollBuilder(frame, character, getDieRoll()).createRoller(powerOfthePit,1);
		}
		powerOfthePit.apply(character,roller);
	}
	
	@Override
	public RewardType getRewardType() {
		return RewardType.PowerOfThePit;
	}
	@Override
	public String getDescription() {
		return "Grants the character a Power of the Pit.";
	}
	private int getDieRoll() {
		String dieRoll = getString(DIE_ROLL);
		return getDieRoll(DieRollType.valueOf(dieRoll));
	}
}