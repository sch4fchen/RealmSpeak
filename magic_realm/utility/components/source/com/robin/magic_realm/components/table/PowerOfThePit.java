package com.robin.magic_realm.components.table;

import java.util.*;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.Speed;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class PowerOfThePit extends RealmTable {
	
	public static final String KEY = "PowerOfThePit";
	private static final String[] RESULT = {
		"Fiery Chasm Opens",
		"Carried Away",
		"Terror",
		"Blight",
		"Forget",
		"Rust",
	};
	
	private GameObject caster;
	private boolean makeDeadWhenKilled = false; // If PoP is called outside of combat (ie., looting), this should be set to true
	
	private ArrayList<GameObject> kills;
	private boolean harm;
	private Speed speed;
	
	public PowerOfThePit(JFrame frame,GameObject caster,Speed attackSpeed) {
		super(frame,null);
		this.caster = caster;
		this.speed = attackSpeed;
		kills = new ArrayList<>();
	}
	public boolean harmWasApplied() {
		return kills.size()>0 || harm;
	}
	public String getTableName(boolean longDescription) {
		return "Power of the Pit";
	}
	public String getTableKey() {
		return KEY;
	}
	public void setMakeDeadWhenKilled(boolean makeDeadWhenKilled) {
		this.makeDeadWhenKilled = makeDeadWhenKilled;
	}
	public String apply(CharacterWrapper character,DieRoller roller) {
		harm = false;
		if (character.isMistLike()) {
			return "Unaffected - Mist";
		}
		else if (character.hasMagicProtection()) {
			return "Unaffected - Magic Protection";
		}
		else if (character.hasActiveInventoryThisKey(Constants.ABSORB_POP)) {
			GameObject go = character.getActiveInventoryThisKey(Constants.ABSORB_POP);
			go.removeThisAttribute(Constants.ACTIVATED);
			character.getGameObject().remove(go);
			RealmLogging.logMessage(character.getGameObject().getName(),go.getName()+" absorbs Power of the Pit attack!");
			RealmLogging.logMessage(character.getGameObject().getName(),go.getName()+" vanishes.");
			return "Unaffected - "+go.getName();
		}
		return super.apply(character,roller);
	}
	public String applyOne(CharacterWrapper character) {
		// All unhidden characters, natives, and monsters in the clearing are killed.  Visitors, and hidden
		// characters, natives, and monsters are unaffected.
		String destClientName = DemonsEffects.getDestClientName(caster,character.getGameObject()); // Get this before killing anybody!
		ArrayList<RealmComponent> killed = DemonsEffects.killEverythingInClearing(character,new Strength("RED"),true,false,speed,caster,makeDeadWhenKilled,kills);
		
		StringBuffer message = new StringBuffer();
		message.append("Fiery Chasm Opens\n\n");
		message.append("All unhidden characters, natives, and monsters in the clearing are killed.\n");
		message.append("Visitors, and hidden characters, natives, and monsters are unaffected");
		message.append(DemonsEffects.getKilledString(killed));
		
		sendMessage(character.getGameObject().getGameData(),
				destClientName,
				"Power of the Pit",
				message.toString());
		
		return RESULT[0];
	}

	public String applyTwo(CharacterWrapper character) {
		sendMessage(character.getGameObject().getGameData(),
				DemonsEffects.getDestClientName(caster,character.getGameObject()),
				"Power of the Pit",
				"Carried Away\n\n"
				+"The target is instantly killed.\n\n     "+character.getGameObject().getName()+" was killed.");
		// The target is instantly killed.
		DemonsEffects.kill(character.getGameObject(),speed,caster,makeDeadWhenKilled,kills);
		return RESULT[1];
	}

	public String applyThree(CharacterWrapper character) {
		String destClientName = DemonsEffects.getDestClientName(caster,character.getGameObject()); // Get this before killing anybody!
		
		// All Light and Medium Monsters, Natives, and Horses in the clearing are killed.
		ArrayList<RealmComponent> killed = DemonsEffects.killEverythingInClearing(character,new Strength("H"),false,true,speed,caster,makeDeadWhenKilled,kills);
		
		// Each character in the clearing must wound all Light and Medium MOVE/FIGHT chits.
		TileLocation tl = character.getCurrentLocation();
		if (tl.hasClearing() && !tl.isBetweenClearings()) {
			for (RealmComponent rc:tl.clearing.getClearingComponents()) {
				if (rc.isCharacter() && !rc.isMistLike()) {
					CharacterWrapper aChar = new CharacterWrapper(rc.getGameObject());
					boolean hasAtLeastOneGoodChit = aChar.isTransmorphed();
					for (CharacterActionChitComponent chit:aChar.getAllChits()) {
						if (chit.isMove() || chit.isFight()) {
							String str = chit.getGameObject().getThisAttribute("strength");
							if ("L".equals(str) || "M".equals(str)) {
								if (!chit.isWounded()) {
									chit.makeWounded();
									harm = true;
								}
							}
						}
						if (!chit.isWounded()) {
							hasAtLeastOneGoodChit = true;
						}
					}
					if (!hasAtLeastOneGoodChit) {
						DemonsEffects.kill(rc.getGameObject(),speed,caster,makeDeadWhenKilled,kills);
						killed.add(rc);
					}
				}
			}
		}
		
		StringBuffer message = new StringBuffer();
		message.append("Terror\n\n");
		message.append("Each character in the clearing must wound all Light and Medium MOVE/FIGHT chits.\n");
		message.append("All Light and Medium Monsters, Natives, and Horses in the clearing are killed.");
		message.append(DemonsEffects.getKilledString(killed));
		
		sendMessage(character.getGameObject().getGameData(),
				destClientName,
				"Power of the Pit",
				message.toString());
		
		return RESULT[2];
	}

	public String applyFour(CharacterWrapper character) {
		String destClientName = DemonsEffects.getDestClientName(caster,character.getGameObject()); // Get this before killing anybody!
		boolean hasChits = character.isCharacter() && !character.isTransmorphed();
		boolean hasAtLeastOneGoodChit = false;
		for (CharacterActionChitComponent chit:character.getAllChits()) {
			if (chit.getEffortAsterisks()>0 && !chit.isWounded()) {
				chit.makeWounded();
				harm = true;
			}
			if (!chit.isWounded()) {
				hasAtLeastOneGoodChit = true;
			}
		}
		StringBuffer message = new StringBuffer();
		message.append("Blight\n\n");
		message.append("All of the target's active chits that show effort asterisks become wounded.\n");
		message.append("Chits that are already fatigued or that show no asterisks are not affected.");
		if (hasChits && !hasAtLeastOneGoodChit) {
			DemonsEffects.kill(character.getGameObject(),speed,caster,makeDeadWhenKilled,kills);
			ArrayList<RealmComponent> killed = new ArrayList<RealmComponent>();
			killed.add(RealmComponent.getRealmComponent(character.getGameObject()));
			message.append(DemonsEffects.getKilledString(killed));
		}
		sendMessage(character.getGameObject().getGameData(),
				destClientName,
				"Power of the Pit",
				message.toString());
		return RESULT[3];
	}

	public String applyFive(CharacterWrapper character) {
		sendMessage(character.getGameObject().getGameData(),
				DemonsEffects.getDestClientName(caster,character.getGameObject()),
				"Power of the Pit",
				"Forget\n\n"
				+"All of the target's active MAGIC chits become fatigued.");
		
		// All of the target's active MAGIC chits become fatigued
		for (CharacterActionChitComponent chit : character.getActiveMagicChits()) {
			if (!chit.isFatigued()) {
				chit.makeFatigued();
				harm = true;
			}
		}
		return RESULT[4];
	}

	public String applySix(CharacterWrapper character) {
		sendMessage(character.getGameObject().getGameData(),
				DemonsEffects.getDestClientName(caster,character.getGameObject()),
				"Power of the Pit",
				"Rust\n\n"
				+"All of the target's active armor counters suffer damage. Intact armor counters\n"
				+"become damaged, damaged armor counters are destroyed. Armor cards and inactive\n"
				+"counters are not affected.");
		// The target's active armor counters are damaged.  Armor cards and inactive counters are NOT affected.
		ArrayList<GameObject> destroyed = new ArrayList<>();
		for (GameObject inv:character.getActiveInventory()) {
			RealmComponent rc = RealmComponent.getRealmComponent(inv);
			if (rc.isArmor() && !rc.getGameObject().hasThisAttribute(Constants.OINTMENT_OF_STONE)) {
				ArmorChitComponent armor = (ArmorChitComponent)rc;
				if (armor.isDamaged()) {
					if (makeDeadWhenKilled) {
						destroyed.add(inv);
					}
					else {
						CombatWrapper combat = new CombatWrapper(inv);
						combat.setKilledBy(caster);
						combat.setKilledLength(17);
						combat.setKilledSpeed(speed);
					}
				}
				else {
					armor.setIntact(false);
				}
				harm = true;
			}
		}
		for (GameObject thing:destroyed) {
			TreasureUtility.handleDestroyedItem(character,thing);
		}
		return RESULT[5];
	}
	public ArrayList<GameObject> getKills() {
		return kills;
	}
	public static PowerOfThePit doNow(JFrame parent,GameObject attacker,GameObject target,boolean casterRolls,int redDie,Speed attackSpeed) {
		PowerOfThePit pop = new PowerOfThePit(parent,attacker,attackSpeed);
		pop.setMakeDeadWhenKilled(false);
		CharacterWrapper caster = new CharacterWrapper(attacker);
		CharacterWrapper victim = new CharacterWrapper(target);
		DieRoller roller = DieRollBuilder.getDieRollBuilder(parent,casterRolls?caster:victim,redDie).createRoller(pop);
		String result = pop.apply(victim,roller);
		RealmLogging.logMessage(caster.getGameObject().getName(),"Power of the Pit roll: "+roller.getDescription());
		RealmLogging.logMessage(caster.getGameObject().getName(),"Power of the Pit result: "+result);
		return pop;
	}
}