package com.robin.magic_realm.components.table;

import java.util.*;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.Speed;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.utility.SpellUtility.SummonType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class DevilsSpell extends RealmTable {
	
	public static final String KEY = "DevilsSpell";
	private static final String[] RESULT = {
		"Fiery Chasm Opens",
		"Summon Winged Demon",
		"Wither",
		"Negative Aura",
		"Bad Luck",
		"Weakened",
	};
	
	private GameObject caster;
	private ArrayList<GameObject> kills;
	private boolean harm;
	private Speed speed;
	
	public DevilsSpell(JFrame frame,GameObject caster,Speed attackSpeed) {
		super(frame,null);
		this.caster = caster;
		this.speed = attackSpeed;
		kills = new ArrayList<>();
	}
	public boolean harmWasApplied() {
		return kills.size()>0 || harm;
	}
	public String getTableName(boolean longDescription) {
		return "Devil's Spell";
	}
	public String getTableKey() {
		return KEY;
	}
	public String apply(CharacterWrapper character,DieRoller roller) {
		harm = false;
		if (character.isMistLike()) {
			return "Unaffected - Mist";
		}
		else if (character.hasMagicProtection()) {
			return "Unaffected - Magic Protection";
		}
		return super.apply(character,roller);
	}
	public String applyOne(CharacterWrapper character) {
		String destClientName = DemonsEffects.getDestClientName(caster,character.getGameObject()); // Get this before killing anybody!
		ArrayList<RealmComponent> killed = DemonsEffects.killEverythingInClearing(character,new Strength("RED"),true,false,speed,caster,false,kills);
		
		StringBuffer message = new StringBuffer();
		message.append("Fiery Chasm Opens\n\n");
		message.append("All unhidden characters, natives, and monsters in the clearing are killed.\n");
		message.append("Visitors, and hidden characters, natives, and monsters are unaffected");
		message.append(DemonsEffects.getKilledString(killed));
		
		sendMessage(character.getGameObject().getGameData(),
				destClientName,
				"Devils Spell",
				message.toString());
		
		return RESULT[0];
	}

	public String applyTwo(CharacterWrapper character) {
		sendMessage(character.getGameObject().getGameData(),
				DemonsEffects.getDestClientName(caster,character.getGameObject()),
				"Devil's Spell",
				"Summons a Winged Demon.");
		
		GameObject spellGo = character.getGameObject().getGameData().createNewObject();
		spellGo.setThisAttribute("name", "Devils Spell - Summon Winged Demon");
		spellGo.setThisAttribute("spell", "V");
		spellGo.setThisAttribute(Constants.SPELL_DENIZEN, "V");
		spellGo.setThisAttribute("duration", "instant");
		spellGo.setThisAttribute("target", "clearing");
		spellGo.setThisAttribute("magic_color", "black");
		spellGo.setThisAttribute(Constants.UNEFFECT_AT_MIDNIGHT);
		caster.add(spellGo);
		SpellWrapper spell = new SpellWrapper(spellGo);
		spell.castSpellByDenizen(caster);
		SpellUtility.summonCompanion(getParentFrame(),caster,null,spell,SummonType.demon.toString(),2);
		spell.recognizeCastedSpellByDenizen();
		
		return RESULT[1];
	}

	public String applyThree(CharacterWrapper character) {
		sendMessage(character.getGameObject().getGameData(),
				DemonsEffects.getDestClientName(caster,character.getGameObject()),
				"Devil's Spell",
				"The "+character.getCharacterName()+" is cursed with "+Constants.WITHER+", and cannot have any active effort chits.");
		
		character.applyCurse(Constants.WITHER);
		return RESULT[2];
	}

	public String applyFour(CharacterWrapper character) {
		sendMessage(character.getGameObject().getGameData(),
				DemonsEffects.getDestClientName(caster,character.getGameObject()),
				"Devil's Spell",
				"The "+character.getCharacterName()+" is cursed with Negative Aura.");
		
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(character.getGameObject().getGameData());
		GameObject spellGo = character.getGameObject().getGameData().createNewObject();
		spellGo.setThisAttribute("name", "Devils Spell - Negative Aura");
		spellGo.setThisAttribute("spell", "V");
		spellGo.setThisAttribute(Constants.SPELL_DENIZEN, "V");
		spellGo.setThisAttribute("duration", "day");
		spellGo.setThisAttribute("target", "character");
		spellGo.setThisAttribute("magic_color", "black");
		spellGo.setThisAttribute(Constants.NEGATIVE_AURA);
		spellGo.setThisAttribute(Constants.ALTERNATIVE_SPELL_EFFECT, "negative aura");
		
		caster.add(spellGo);
		SpellWrapper spell = new SpellWrapper(spellGo);
		spell.castSpellByDenizen(caster);
		spell.addTarget(hostPrefs, character.getGameObject());
		spell.affectTargets(getParentFrame(), GameWrapper.findGame(character.getGameObject().getGameData()), false, null);
		spell.recognizeCastedSpellByDenizen();

		return RESULT[3];
	}

	public String applyFive(CharacterWrapper character) {
		sendMessage(character.getGameObject().getGameData(),
				DemonsEffects.getDestClientName(caster,character.getGameObject()),
				"Devil's Spell",
				"The "+character.getCharacterName()+" is cursed with Bad Luck.");
		
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(character.getGameObject().getGameData());
		GameObject spellGo = character.getGameObject().getGameData().createNewObject();
		spellGo.setThisAttribute("name", "Devils Spell - Bad Luck");
		spellGo.setThisAttribute("spell", "VIII");
		spellGo.setThisAttribute(Constants.SPELL_DENIZEN, "VIII");
		spellGo.setThisAttribute("duration", "permanent");
		spellGo.setThisAttribute("target", "character");
		spellGo.setThisAttribute("magic_color", "any");
		spellGo.addThisAttributeListItem("diemod","+1:all:all");
		caster.add(spellGo);
		SpellWrapper spell = new SpellWrapper(spellGo);
		spell.castSpellByDenizen(caster);
		spell.addTarget(hostPrefs, character.getGameObject());
		spell.affectTargets(getParentFrame(), GameWrapper.findGame(character.getGameObject().getGameData()), false, null);
		spell.recognizeCastedSpellByDenizen();
		
		return RESULT[4];
	}

	public String applySix(CharacterWrapper character) {
		sendMessage(character.getGameObject().getGameData(),
				DemonsEffects.getDestClientName(caster,character.getGameObject()),
				"Devil's Spell",
				"The "+character.getCharacterName()+" is cursed with Weakened, target's vulnerability is lowered one level until Sunset of the next day.");
		
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(character.getGameObject().getGameData());
		GameObject spellGo = character.getGameObject().getGameData().createNewObject();
		spellGo.setThisAttribute("name", "Devils Spell - Weakened");
		spellGo.setThisAttribute("spell", "VIII");
		spellGo.setThisAttribute("spell_denizen", "VIII");
		spellGo.setThisAttribute("duration", "permanent");
		spellGo.setThisAttribute("target", "character");
		spellGo.setThisAttribute("magic_color", "any");
		spellGo.setThisAttribute(Constants.WEAKENED_VULNERABILITY);
		spellGo.setThisAttribute("uneffect_at_midnight");
		caster.add(spellGo);
		SpellWrapper spell = new SpellWrapper(spellGo);
		spell.castSpellByDenizen(caster);
		spell.addTarget(hostPrefs, character.getGameObject());
		spell.affectTargets(getParentFrame(), GameWrapper.findGame(character.getGameObject().getGameData()), false, null);
		spell.recognizeCastedSpellByDenizen();
		
		character.getGameObject().setThisAttribute(Constants.WEAKENED_VULNERABILITY);
		
		return RESULT[5];
	}
	public ArrayList<GameObject> getKills() {
		return kills;
	}
	public static DevilsSpell doNow(JFrame parent,GameObject attacker,GameObject target,boolean casterRolls,int redDie,Speed attackSpeed) {
		DevilsSpell ds = new DevilsSpell(parent,attacker,attackSpeed);
		CharacterWrapper caster = new CharacterWrapper(attacker);
		CharacterWrapper victim = new CharacterWrapper(target);
		DieRoller roller = DieRollBuilder.getDieRollBuilder(parent,casterRolls?caster:victim,redDie).createRoller(ds);
		String result = ds.apply(victim,roller);
		RealmLogging.logMessage(caster.getGameObject().getName(),"Devil's Spell roll: "+roller.getDescription());
		RealmLogging.logMessage(caster.getGameObject().getName(),"Devil's Spell result: "+result);
		return ds;
	}
}