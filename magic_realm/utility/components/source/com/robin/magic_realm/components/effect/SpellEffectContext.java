package com.robin.magic_realm.components.effect;

import javax.swing.JFrame;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellEffectContext {
	public JFrame Parent;
	public GameWrapper Game;
	public RealmComponent Target;
	public SpellWrapper Spell;
	public GameObject Caster;
	
	public SpellEffectContext(JFrame parent, GameWrapper game, RealmComponent target, SpellWrapper spell, GameObject caster){
		Parent = parent;
		Game = game;
		Target = target;
		Spell = spell;
		Caster = caster;
	}
	
	public CharacterWrapper getCharacterTarget(){
		return new CharacterWrapper(Target.getGameObject());
	}
	
	public CombatWrapper getCombatTarget(){
		return new CombatWrapper(Target.getGameObject());
	}
	
	public ClearingDetail getClearingTarget(){
		return Spell.getTargetAsClearing(Target);
	}
	
	public TileComponent getTileTarget(){
		return new TileComponent(Target.getGameObject());
	}
	
	public CharacterWrapper getCharacterCaster(){
		return new CharacterWrapper(Caster);
	}
	
	public GameData getGameData(){
		return Game.getGameData();
	}
}
