package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.quest.DieRollType;
import com.robin.magic_realm.components.table.Mesmerize;
import com.robin.magic_realm.components.utility.DieRollBuilder;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardMesmerize extends QuestReward {
	
	public static final String DIE_ROLL = "_dr";
	public static final String REMOVE_CURSES = "_rmv_curses";
	
	public QuestRewardMesmerize(GameObject go) {
		super(go);
	}

	@Override
	public void processReward(JFrame frame, CharacterWrapper character) {
		if (removeCurses()) {
			character.removeAllCurses();
			return;
		}
		
		Mesmerize mesmerize = new Mesmerize(frame, character.getGameObject());
		DieRoller roller;
		if (getString(DIE_ROLL).equals(DieRollType.Random.toString())) {
			roller = DieRollBuilder.getDieRollBuilder(frame, character, getDieRoll()).createRoller(mesmerize);
		}
		else {
			roller = DieRollBuilder.getDieRollBuilder(frame, character, getDieRoll()).createRoller(mesmerize,1);
		}
		
		mesmerize.apply(character,roller);
	}
	
	@Override
	public RewardType getRewardType() {
		return RewardType.Mesmerize;
	}
	@Override
	public String getDescription() {
		if (removeCurses()) {
			return "Remove all curses from the character.";
		}
		return "Mesmerize the character.";
	}
	private int getDieRoll() {
		String dieRoll = getString(DIE_ROLL);
		return getDieRoll(DieRollType.valueOf(dieRoll));
	}
	private Boolean removeCurses() {
		return getBoolean(REMOVE_CURSES);
	}
}