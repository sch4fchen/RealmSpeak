package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.SpellCreator;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class QuestRewardSpellEffectOnTile extends QuestReward {
	
	public static final String SPELL = "_spell";
	public static final String REMOVE = "_remove";
	
	public enum EffectOnTile {
		Fog,
		FrozenWater,
		ViolentStorm,
	}
	
	public QuestRewardSpellEffectOnTile(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		String spell;
		switch (getSpell()) {
		case Fog:
			spell = "fog";
			break;
		case FrozenWater:
			spell = "frozen water";
			break;
		case ViolentStorm:
			spell = "violent storm";
			break;
		default:
			return;
		}
		
		GameWrapper gameWrapper = GameWrapper.findGame(getGameObject().getGameData());
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameData());
		SpellWrapper spellWrapper = SpellCreator.CreateSpellWrapper(spell, character);
		
		TileLocation charactersLocation = character.getCurrentLocation();
		if (charactersLocation != null && charactersLocation.tile != null) {
			spellWrapper.addTarget(hostPrefs, charactersLocation.tile.getGameObject());
		}
		
		if (remove()) {
			spellWrapper.unaffectTargets();
			return;
		}
		spellWrapper.affectTargets(frame, gameWrapper, false, null);
	}
	
	private EffectOnTile getSpell() {
		return EffectOnTile.valueOf(getString(SPELL));
	}
	
	private Boolean remove() {
		return getBoolean(REMOVE);
	}
	
	public String getDescription() {
		if (remove()) {
			return("Remove the spell "+getSpell()+" from the characters tile.");
		}
		return "Cast the spell "+getSpell()+" on the characters tile";
	}
	public RewardType getRewardType() {
		return RewardType.SpellEffectOnTile;
	}
}