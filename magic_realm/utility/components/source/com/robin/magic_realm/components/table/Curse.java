package com.robin.magic_realm.components.table;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class Curse extends RealmTable {
	
	public static final String KEY = "Curse";
	
	private boolean harm;
	private boolean cursed;
	private GameObject caster;
	
	public Curse(JFrame frame, GameObject caster) {
		super(frame,null);
		this.caster = caster;
	}
	public boolean harmWasApplied() {
		return harm;
	}
	public String getTableName(boolean longDescription) {
		return "Curse!";
	}
	public String getTableKey() {
		return KEY;
	}
	public String getDestClientName(GameObject target) {
		RealmComponent targetRc = RealmComponent.getRealmComponent(target);
		// Determine the destination client
		RealmComponent destOwner = targetRc.getOwner();
		// destOwner should NOT be null at this point!
		if (destOwner == null) {
			RealmComponent chasterRc = RealmComponent.getRealmComponent(this.caster);
			destOwner = chasterRc.getOwner();
		}
		CharacterWrapper destCharacter = new CharacterWrapper(destOwner.getGameObject());
		return destCharacter.getPlayerName();
	}
	public String apply(CharacterWrapper character,DieRoller roller) {
		harm = false; // by default
		if (character.isCharacter() && !character.isMistLike() && !character.hasMagicProtection()) {
			cursed = false;
			String result = super.apply(character,roller);
			if (!cursed) {
				sendMessage(character.getGameObject().getGameData(),
						getDestClientName(character.getGameObject()),
						getCurseTitle(character),
						"The "+character.getCharacterName()+" is hit with "+result+", but it has no effect!");
				result = result + ", but not affected.";
			}
			return result;
		}
		sendMessage(character.getGameObject().getGameData(),
				getDestClientName(character.getGameObject()),
				getCurseTitle(character),
				"The "+character.getCharacterName()+" is hit with a curse, but it has no effect!");
		return "Unaffected";
	}
	private static String getCurseTitle(CharacterWrapper character) {
		return character.getGameObject().getName()+"'s Curse!";
	}
	public String applyOne(CharacterWrapper character) {
		if (!character.hasCurse(Constants.EYEMIST) && !character.immuneToCurses()) { // only inform them if they don't already have the curse
			cursed = true;
			// No search activity, except enhanced PEER
			character.applyCurse(Constants.EYEMIST);
			if (getParentFrame()!=null) {
				sendMessage(character.getGameObject().getGameData(),
						getDestClientName(character.getGameObject()),
						getCurseTitle(character),
						"The "+character.getCharacterName()+" is cursed with "+Constants.EYEMIST+", and cannot do searches.");
			}
		}
		return "Eyemist";
	}

	public String applyTwo(CharacterWrapper character) {
		if (!character.hasCurse(Constants.SQUEAK) && !character.immuneToCurses()) { // only inform them if they don't already have the curse
			cursed = true;
			// Cannot be hidden
			character.applyCurse(Constants.SQUEAK);
			
			// Unhide character if hidden
			if (character.isHidden()) {
				character.setHidden(false);
			}
			if (getParentFrame()!=null) {
				sendMessage(character.getGameObject().getGameData(),
						getDestClientName(character.getGameObject()),
						getCurseTitle(character),
						"The "+character.getCharacterName()+" is cursed with "+Constants.SQUEAK+", and cannot be hidden.");
			}
		}
		return "Squeak";
	}

	public String applyThree(CharacterWrapper character) {
		if (!character.hasCurse(Constants.WITHER) && !character.immuneToCurses()) { // only inform them if they don't already have the curse
			cursed = true;
			// Cannot have ANY active effort asterisks
			character.applyCurse(Constants.WITHER);
			
			ArrayList<CharacterActionChitComponent> toFatigue = new ArrayList<>();
			toFatigue.addAll(character.getActiveEffortChits());
			toFatigue.addAll(character.getAlertedChits());
			toFatigue.addAll(character.getColorChits());
			
			// Fatigue all active effort asterisks here
			for (CharacterActionChitComponent chit : toFatigue) {
				chit.makeFatigued();
				harm = true;
			}
			if (getParentFrame()!=null) {
				sendMessage(character.getGameObject().getGameData(),
						getDestClientName(character.getGameObject()),
						getCurseTitle(character),
						"The "+character.getCharacterName()+" is cursed with "+Constants.WITHER+", and cannot have any active effort chits.");
			}
		}
		return "Wither";
	}

	public String applyFour(CharacterWrapper character) {
		if (!character.hasCurse(Constants.ILL_HEALTH) && !character.immuneToCurses()) { // only inform them if they don't already have the curse
			cursed = true;
			// Cannot REST
			character.applyCurse(Constants.ILL_HEALTH);
			if (getParentFrame()!=null) {
				sendMessage(character.getGameObject().getGameData(),
						getDestClientName(character.getGameObject()),
						getCurseTitle(character),
						"The "+character.getCharacterName()+" is cursed with "+Constants.ILL_HEALTH+", and cannot rest.");
			}
		}
		return "Ill Health";
	}

	public String applyFive(CharacterWrapper character) {
		if (!character.hasCurse(Constants.ASHES) && !character.immuneToCurses()) { // only inform them if they don't already have the curse
			cursed = true;
			// GOLD is worthless - can add to it, but not subtract
			character.applyCurse(Constants.ASHES);
			if (getParentFrame()!=null) {
				sendMessage(character.getGameObject().getGameData(),
						getDestClientName(character.getGameObject()),
						getCurseTitle(character),
						"The "+character.getCharacterName()+" is cursed with "+Constants.ASHES+", making GOLD worthless.");
			}
		}
		return "Ashes";
	}

	public String applySix(CharacterWrapper character) {
		if (!character.hasCurse(Constants.DISGUST) && !character.immuneToCurses()) { // only inform them if they don't already have the curse
			cursed = true;
			// FAME is worthless - can add to it, but not subtract
			character.applyCurse(Constants.DISGUST);
			if (getParentFrame()!=null) {
				sendMessage(character.getGameObject().getGameData(),
						getDestClientName(character.getGameObject()),
						getCurseTitle(character),
						"The "+character.getCharacterName()+" is cursed with "+Constants.DISGUST+", making FAME worthless.");
			}
		}
		return "Disgust";
	}
	public static Curse doNow(JFrame parent,GameObject attacker,GameObject target) {
		Curse curse = new Curse(parent, attacker);
		CharacterWrapper victim = new CharacterWrapper(target);
		// Use the "victim" here instead of the caster, because the victim is the one rolling for the curse (coming from an Imp!!)
		DieRoller roller = DieRollBuilder.getDieRollBuilder(parent,victim).createRoller(curse);
		String result = curse.apply(victim,roller);
		RealmLogging.logMessage(target.getName(),"Cursed with "+result);
		return curse;
	}
}