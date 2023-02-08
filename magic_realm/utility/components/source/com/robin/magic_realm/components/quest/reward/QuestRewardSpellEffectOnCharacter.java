package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class QuestRewardSpellEffectOnCharacter extends QuestReward {
	
	public static final String SPELL = "_spell";
	public static final String REMOVE = "_remove";
	
	public enum EffectOnCharacter {
		BlazingLightX,
		DivineMight,
		DivineProtection,
		HurricaneWinds,
		Lost,
		PeaceWithNature,
		Premonition,
		Prophecy,
		Shrink,
		Slowed,
		SpiritGuide,
		ValeWalker
	}
	
	public QuestRewardSpellEffectOnCharacter(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		String effect;
		switch (getSpell()) {
		case BlazingLightX:
			effect = Constants.TORCH_BEARER;
			break;
		case DivineMight:
			effect = Constants.STRONG_MF;
			break;
		case DivineProtection:
			effect = Constants.ADDS_ARMOR;
			break;
		case HurricaneWinds:
			GameObject hurricanWinds = character.getGameData().getGameObjectByNameIgnoreCase("hurricane winds");
			if (hurricanWinds == null) return;
			if (remove()) {
				character.getGameObject().removeThisAttribute(Constants.BLOWS_TARGET);
				hurricanWinds.removeAttribute(SpellWrapper.SPELL_BLOCK_NAME,SpellWrapper.SECONDARY_TARGET);
				return;
			}
			
			if(character.getCurrentLocation() == null || character.getCurrentLocation().tile == null) return;
			hurricanWinds.setAttribute(SpellWrapper.SPELL_BLOCK_NAME,SpellWrapper.SECONDARY_TARGET,character.getCurrentLocation().tile.getGameObject().getStringId());
			character.getGameObject().setThisAttribute(Constants.BLOWS_TARGET,hurricanWinds.getStringId());
			return;
		case Lost:
			effect = Constants.SP_MOVE_IS_RANDOM;
			break;
		case PeaceWithNature:
			effect = Constants.PEACE_WITH_NATURE;
			break;
		case Premonition:
			effect = Constants.CHOOSE_TURN;
			break;
		case Prophecy:
			effect = Constants.DAYTIME_ACTIONS;
			break;
		case Shrink:
			effect = Constants.SHRINK;
			break;
		case Slowed:
			effect = Constants.SLOWED;
			break;
		case SpiritGuide:
			effect = Constants.SPIRIT_GUIDE;
			break;
		case ValeWalker:
			effect = Constants.VALE_WALKER;
			break;
		default:
			return;
		}
		
		if (remove()) {
			character.getGameObject().removeThisAttribute(effect);
			return;
		}
		character.getGameObject().setThisAttribute(effect);
	}
	
	private EffectOnCharacter getSpell() {
		return EffectOnCharacter.valueOf(getString(SPELL));
	}
	
	private Boolean remove() {
		return getBoolean(REMOVE);
	}
	
	public String getDescription() {
		if (remove()) {
			return "Removes the spell effect "+getSpell()+" from the character.";
		}
		return "Applies the spell effect "+getSpell()+" on the character (with unlimited duration).";
	}
	public RewardType getRewardType() {
		return RewardType.SpellEffectOnCharacter;
	}

}