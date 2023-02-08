package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.SpellCreator;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class QuestRewardPhantasm extends QuestReward {
	
	public static final String REMOVE = "_remove";
	
	public QuestRewardPhantasm(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		GameWrapper gameWrapper = GameWrapper.findGame(getGameObject().getGameData());
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameData());
		SpellWrapper spell = SpellCreator.CreateSpellWrapper("phantasm", character);
		spell.addTarget(hostPrefs, character.getGameObject());
		
		if (remove()) {
			spell.unaffectTargets();
			return;
		}
		
		spell.affectTargets(frame, gameWrapper, false, null);
		return;
	}
	
	private boolean remove() {
		return getBoolean(REMOVE);
	}
	
	public String getDescription() {
		if (remove()) {
			return "Removes the phantasm of the character.";
		}
		return "Gives the character a phantasm.";
	}
	public RewardType getRewardType() {
		return RewardType.Phantasm;
	}

}