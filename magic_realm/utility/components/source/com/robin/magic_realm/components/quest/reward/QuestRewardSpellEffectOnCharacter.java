package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class QuestRewardSpellEffectOnCharacter extends QuestReward {
	
	public static final String SPELL = "_spell";
	public static final String REMOVE = "_remove";
	
	public enum EffectOnCharacter {
		Barkskin,
		BlazingLightX,
		DivineMight,
		DivineProtection,
		DivineShield,
		HurricaneWinds,
		Lost,
		NegativeAura,
		PeaceWithNature,
		Premonition,
		Prophecy,
		ProtectionFromMagic,
		Shrink,
		Slowed,
		SpiritGuide,
		TrackersSense,
		ValeWalker,
		WaterRun
	}
	
	public QuestRewardSpellEffectOnCharacter(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame,CharacterWrapper character) {
		String effect;
		switch (getSpell()) {
		case Barkskin:
			effect = Constants.BARKSKIN;
			break;
		case BlazingLightX:
			effect = Constants.TORCH_BEARER;
			break;
		case DivineMight:
			effect = Constants.STRONG_MF;
			break;
		case DivineProtection:
			effect = Constants.STRENGTHENED_VULNERABILITY;
			break;
		case DivineShield:
			effect = Constants.ADDS_ARMOR;
			break;
		case HurricaneWinds:
			GameObject hurricanWindsOriginal = character.getGameData().getGameObjectByNameIgnoreCase("hurricane winds");
			if (hurricanWindsOriginal == null) return;
			GameObject hurricanWinds = hurricanWindsOriginal.copy();
			if (remove()) {
				character.getGameObject().removeThisAttribute(Constants.BLOWS_TARGET);
				hurricanWinds.removeAttribute(SpellWrapper.SPELL_BLOCK_NAME,SpellWrapper.SECONDARY_TARGET);
				return;
			}
			
			if(character.getCurrentLocation() == null || character.getCurrentLocation().tile == null) return;
			ArrayList<TileComponent> adjacentTiles = new ArrayList<>(character.getCurrentLocation().tile.getAllAdjacentTiles());
			TileComponent targetTile = adjacentTiles.get(RandomNumber.getRandom(adjacentTiles.size()));
			SpellWrapper spell = new SpellWrapper(hurricanWinds);
			spell.setSecondaryTarget(targetTile.getGameObject());
			character.getGameObject().setThisAttribute(Constants.BLOWS_TARGET,hurricanWinds.getStringId());
			return;
		case Lost:
			effect = Constants.SP_MOVE_IS_RANDOM;
			break;
		case NegativeAura:
			effect = Constants.NEGATIVE_AURA;
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
		case ProtectionFromMagic:
			effect = Constants.MAGIC_PROTECTION;
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
		case TrackersSense:
			effect = Constants.TRACKERS_SENSE;
			break;
		case ValeWalker:
			effect = Constants.VALE_WALKER;
			break;
		case WaterRun:
			effect = Constants.WATER_RUN;
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