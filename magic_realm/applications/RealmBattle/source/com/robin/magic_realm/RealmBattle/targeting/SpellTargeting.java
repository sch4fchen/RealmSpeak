package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.ButtonOptionDialog;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.*;

public abstract class SpellTargeting {
	
	protected CombatFrame combatFrame;
	protected SpellWrapper spell;
	
	protected ArrayList<GameObject> gameObjects;
	
	public abstract boolean populate(BattleModel battleModel,RealmComponent activeParticipant);
	public abstract boolean assign(HostPrefWrapper hostPrefs,CharacterWrapper activeCharacter);
	public abstract boolean hasTargets();
	
	protected SpellTargeting(CombatFrame combatFrame,SpellWrapper spell) {
		this.combatFrame = combatFrame;
		this.spell = spell;
		gameObjects = new ArrayList<>();
	}
	protected boolean allowTargetingHirelings() {
		return combatFrame.allowsTreachery() || (spell.isBenevolent() && combatFrame.getHostPrefs().hasPref(Constants.TE_BENEVOLENT_SPELLS)) || spell.isBenevolentForHirelings();
	}
	/**
	 * This is the primary access to Spell Targeting
	 */
	public static SpellTargeting getTargeting(CombatFrame combatFrame,SpellWrapper spell) {
		SpellTargeting targeting = null;
		String targetType = spell.getGameObject().getThisAttribute("target");
		if ("multiple".equals(targetType)) {
			targeting = new SpellTargetingOtherOpponents(combatFrame,spell);
		}
		else if ("individual".equals(targetType)) {
			targeting = new SpellTargetingIndividual(combatFrame,spell);
		}
		else if ("individual + hex".equals(targetType)) {
			targeting = new SpellTargetingIndividualPlusHex(combatFrame,spell);
		}
		else if ("attacker".equals(targetType)) {
			targeting = new SpellTargetingAttacker(combatFrame,spell);
		}
		else if ("leader".equals(targetType)) {
			targeting = new SpellTargetingLeader(combatFrame,spell);
		}
		else if ("character".equals(targetType)) {
			targeting = new SpellTargetingCharacter(combatFrame,spell,spell.getGameObject().hasThisAttribute("targetLightOnly"));
		}
		else if ("characters".equals(targetType)) {
			targeting = new SpellTargetingCharacters(combatFrame,spell,spell.getGameObject().hasThisAttribute("targetLightOnly"));
		}
		else if ("caster".equals(targetType)) {
			targeting = new SpellTargetingCaster(combatFrame,spell);
		}
		else if ("clearing".equals(targetType)) {
			targeting = new SpellTargetingClearing(combatFrame,spell);
		}
		else if ("monster".equals(targetType)) {
			targeting = new SpellTargetingMonster(combatFrame,spell);
		}
		else if ("living monster".equals(targetType) || "monster alive".equals(targetType) || "monster_alive".equals(targetType)
				|| "living monsters".equals(targetType) || "monsters alive".equals(targetType) || "monsters_alive".equals(targetType)) {
			targeting = new SpellTargetingAliveMonster(combatFrame,spell);
		}
		else if ("sound".equals(targetType)) {
			targeting = new SpellTargetingSound(combatFrame,spell);
		}
		else if ("artifact".equals(targetType)) {
			targeting = new SpellTargetingArtifact(combatFrame,spell);
		}
		else if (targetType.indexOf("spell")>=0 || targetType.indexOf("curse")>=0) {
			targeting = new SpellTargetingSpellOrCurse(combatFrame,spell);
		}
		else if (targetType.startsWith("magic")) {
			targeting = new SpellTargetingMagic(combatFrame,spell);
		}
		else if ("uncontrolled monsters".equals(targetType) || "monsters uncontrolled".equals(targetType) || "monsters_uncontrolled".equals(targetType)
				||"uncontrolled monster".equals(targetType) || "monster uncontrolled".equals(targetType) || "monster_uncontrolled".equals(targetType)) {
			targeting = new SpellTargetingUncontrolledMonsters(combatFrame,spell);
		}
		else if ("creature, horse, hound".equals(targetType)) {
			targeting = new SpellTargetingCreatureHorseHound(combatFrame,spell);
		}
		else if ("spider, octopus".equals(targetType)) {
			targeting = new SpellTargetingSpiderOctopus(combatFrame,spell);
		}
		else if ("spider, octopus, scorpion".equals(targetType)) {
			targeting = new SpellTargetingSpiderOctopusScorpion(combatFrame,spell);
		}
		else if ("bats".equals(targetType)) {
			targeting = new SpellTargetingBats(combatFrame,spell);
		}
		else if ("wolves".equals(targetType)) {
			targeting = new SpellTargetingWolves(combatFrame,spell);
		}
		else if ("goblins".equals(targetType)) {
			targeting = new SpellTargetingGoblins(combatFrame,spell);
		}
		else if ("orcs, goblins".equals(targetType)) {
			targeting = new SpellTargetingOrcsGoblins(combatFrame,spell);
		}
		else if ("human group".equals(targetType)) {
			targeting = new SpellTargetingHumanGroup(combatFrame,spell);
		}
		else if ("dragon".equals(targetType)) {
			targeting = new SpellTargetingDragon(combatFrame,spell);
		}
		else if ("demon".equals(targetType)) {
			targeting = new SpellTargetingDemon(combatFrame,spell);
		}
		else if ("ask demon".equals(targetType) || "ask_demon".equals(targetType)) {
			targeting = new SpellTargetingAskDemon(combatFrame,spell);
		}
		else if ("weather".equals(targetType)) {
			targeting = new SpellTargetingWeather(combatFrame,spell);
		}
		else if ("weapon".equals(targetType)) {
			targeting = new SpellTargetingWeapon(combatFrame,spell);
		}
		else if ("weapon, denizen".equals(targetType)) {
			targeting = new SpellTargetingWeaponOrDenizen(combatFrame,spell);
		}
		else if ("tile".equals(targetType)) {
			targeting = new SpellTargetingTile(combatFrame,spell);
		}
		else if ("character,tile".equals(targetType) || "character, tile".equals(targetType)) {
			// Show a dialog to make a choice here
			ButtonOptionDialog choice = new ButtonOptionDialog(combatFrame,null,"Target which?",spell.getGameObject().getName());
			choice.addSelectionObject("Character");
			choice.addSelectionObject("This Tile");
			choice.setVisible(true);
			String result = (String)choice.getSelectedObject();
			if (result!=null) {
				if ("Character".equals(result)) {
					targeting = new SpellTargetingCharacter(combatFrame,spell,false);
				}
				else {
					targeting = new SpellTargetingTile(combatFrame,spell);
				}
			}
		}
		else if ("caster item".equals(targetType)) {
			targeting = new SpellTargetingMyItem(combatFrame,spell,true,true);
		}
		else if ("caster armor".equals(targetType)) {
			targeting = new SpellTargetingMyArmor(combatFrame,spell);
		}
		else if ("inactive weapon".equals(targetType)) {
			targeting = new SpellTargetingMyWeapon(combatFrame,spell);
		}
		else if ("hurt chits".equals(targetType)) {
			targeting = new SpellTargetingHurtChit(combatFrame,spell);
		}
		else if ("denizen".equals(targetType)) {
			targeting = new SpellTargetingDenizen(combatFrame,spell);
		}
		else if ("active sword".equals(targetType)) {
			targeting = new SpellTargetingActiveWeaponType(combatFrame,spell,"sword",false);
		}
		else if ("monsters".equals(targetType)) {
			targeting = new SpellTargetingMonsters(combatFrame,spell);
		}
		else if ("undead".equals(targetType)) {
			targeting = new SpellTargetingUndead(combatFrame,spell);
		}
		else if ("dead monster".equals(targetType) || "dead_monster".equals(targetType)) {
			targeting = new SpellTargetingDeadMonster(combatFrame,spell);
		}
		else if ("staff".equals(targetType)) {
			targeting = new SpellTargetingActiveWeaponType(combatFrame,spell,"staff",true);
		}
		else if ("color".equals(targetType)) {
			targeting = new SpellTargetingColor(combatFrame,spell);
		}
		else if ("MOVE chit".equals(targetType)) {
			targeting = new SpellTargetingChit(combatFrame,spell,"MOVE");
		}
		else if ("FIGHT chit".equals(targetType)) {
			targeting = new SpellTargetingChit(combatFrame,spell,"FIGHT");
		}
		else if ("native".equals(targetType)) {
			targeting = new SpellTargetingNative(combatFrame,spell);
		}
		else if ("combat box".equals(targetType)) {
			targeting = new SpellTargetingCombatBox(combatFrame,spell);
		}
		else if ("none".equals(targetType)) {
			targeting = new SpellTargetingNone(combatFrame,spell);
		}
		else if ("beast".equals(targetType)) {
			targeting = new SpellTargetingBeast(combatFrame,spell);
		}
		else if ("animal".equals(targetType)) {
			targeting = new SpellTargetingAnimal(combatFrame,spell);
		}
		else if ("roadway".equals(targetType)) {
			targeting = new SpellTargetingRoadway(combatFrame,spell);
		}
		else if ("active horse".equals(targetType) || "active_horse".equals(targetType)) {
			targeting = new SpellTargetingActiveHorse(combatFrame,spell);
		}
		else if ("warning chits".equals(targetType)) {
			targeting = new SpellTargetingWarningChits(combatFrame,spell);
		}
		else if ("killed denizen".equals(targetType) || "killed_denizen".equals(targetType)) {
			targeting = new SpellTargetingKilledDenizen(combatFrame,spell);
		}
		else if ("character, native leader, controlled monster".equals(targetType)) {
			targeting = new SpellTargetingCharacterNativeLeaderControlledMonster(combatFrame,spell);
		}
		else if ("weapon, native, monster".equals(targetType)) {
			targeting = new SpellTargetingWeaponNativeMonster(combatFrame,spell);
		}
		else if ("active item".equals(targetType)) {
			targeting = new SpellTargetingItem(combatFrame,spell,true,false);
		}
		
		return targeting;
	}
}