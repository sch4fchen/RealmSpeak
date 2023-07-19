package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.SpellCreator;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class QuestRewardSpellEffectOnClearing extends QuestReward {
	
	public static final String SPELL = "_spell";
	public static final String REMOVE = "_remove";
	
	public enum EffectOnClearing {
		Bewilder,
		Blunting,
		Gravity,
		MountainSurge,
		RocksGlow,
		Sleep,
		ViolentWinds
	}
	
	public QuestRewardSpellEffectOnClearing(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		String spell;
		switch (getSpell()) {
		case Bewilder:
			spell = "bewilder";
			break;
		case Blunting:
			spell = "blunting";
			break;
		case Gravity:
			spell = "gravity";
			break;
		case MountainSurge:
			spell = "mountain surge";
			break;
		case RocksGlow:
			spell = "rocks glow";
			break;
		case Sleep:
			spell = "sleep";
			break;
		case ViolentWinds:
			spell = "violent winds";
			break;
		default:
			return;
		}
		
		GameWrapper gameWrapper = GameWrapper.findGame(getGameObject().getGameData());
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameData());
		SpellWrapper spellWrapper = SpellCreator.CreateSpellWrapper(spell, character);
		
		TileLocation charactersLocation = character.getCurrentLocation();
		if (charactersLocation != null && charactersLocation.tile != null && charactersLocation.clearing != null) {
			spellWrapper.addTarget(hostPrefs, charactersLocation.tile.getGameObject());
			spellWrapper.setExtraIdentifier(charactersLocation.clearing.getNumString());
		}
		
		if (remove()) {
			spellWrapper.unaffectTargets();
			return;
		}
		spellWrapper.affectTargets(frame, gameWrapper, false, null);
	}
	
	private EffectOnClearing getSpell() {
		return EffectOnClearing.valueOf(getString(SPELL));
	}
	
	private Boolean remove() {
		return getBoolean(REMOVE);
	}
	
	public String getDescription() {
		if (remove()) {
			return("Remove the spell "+getSpell()+" from the characters clearing.");
		}
		return "Cast the spell "+getSpell()+" on the characters clearing";
	}
	public RewardType getRewardType() {
		return RewardType.SpellEffectOnClearing;
	}
}