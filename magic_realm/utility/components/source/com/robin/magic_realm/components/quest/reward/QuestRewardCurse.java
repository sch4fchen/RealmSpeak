package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.quest.DieRollType;
import com.robin.magic_realm.components.table.Curse;
import com.robin.magic_realm.components.utility.DieRollBuilder;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardCurse extends QuestReward {
	
	public static final String DIE_ROLL = "_dr";
	public static final String REMOVE_CURSES = "_rmv_curses";
	
	public QuestRewardCurse(GameObject go) {
		super(go);
	}

	@Override
	public void processReward(JFrame frame, CharacterWrapper character) {
		if (removeCurses()) {
			character.removeAllCurses();
			return;
		}
		
		Curse curse = new Curse(frame, character.getGameObject());
		DieRoller roller;
		if (getString(DIE_ROLL).equals(DieRollType.Random.toString())) {
			roller = DieRollBuilder.getDieRollBuilder(frame, character, getDieRoll()).createRoller(curse);
		}
		else {
			roller = DieRollBuilder.getDieRollBuilder(frame, character, getDieRoll()).createRoller(curse,1);
		}
		
		curse.apply(character,roller);
	}
	
	@Override
	public RewardType getRewardType() {
		return RewardType.Curse;
	}
	@Override
	public String getDescription() {
		if (removeCurses()) {
			return "Remove all curses from the character.";
		}
		return "Curse the character.";
	}
	private int getDieRoll() {
		String dieRoll = getString(DIE_ROLL);
		return getDieRoll(DieRollType.valueOf(dieRoll));
	}
	private Boolean removeCurses() {
		return getBoolean(REMOVE_CURSES);
	}
}