package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.StateChitComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.SpellUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingBewitched extends SpellTargetingSingle {
	
	private ArrayList<GameObject> possibleSecondaryTargets = new ArrayList<>();
	private BattleModel battleModel = null;
	
	public SpellTargetingBewitched(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}
	
	public boolean isAddableItem(RealmComponent item) {
		return (item.isWeapon() || item.isArmor() || item.isTreasure() || item.getGameObject().hasThisAttribute(Constants.BROOMSTICK));
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		this.battleModel = battleModel;
		for (RealmComponent participant : combatFrame.findCanBeSeen(battleModel.getAllBattleParticipants(true),true,true)) {
			ArrayList<SpellWrapper> bewitchingSpells = SpellUtility.getBewitchingSpells(participant.getGameObject());
			if (!participant.hasMagicProtection() && !participant.hasMagicColorImmunity(spell)) {
				possibleSecondaryTargets.add(participant.getGameObject());
				if (bewitchingSpells!=null && !bewitchingSpells.isEmpty()) {
					gameObjects.add(participant.getGameObject());
				}
			}
			if (participant.isCharacter()) {
				CharacterWrapper character = new CharacterWrapper(participant.getGameObject());
				for (GameObject go:character.getInventory()) {
					RealmComponent itemRc = RealmComponent.getRealmComponent(go);
					if (isAddableItem(itemRc)) {
						possibleSecondaryTargets.add(go);
						ArrayList<SpellWrapper> spells = SpellUtility.getBewitchingSpells(go);
						if (spells!=null && !spells.isEmpty()) {
							gameObjects.add(go);
						}
					}
					else if (go.hasThisAttribute(Constants.PHASE_CHIT) && !gameObjects.contains(participant.getGameObject())) {
						gameObjects.add(participant.getGameObject());
					}
				}
				for (StateChitComponent chit : character.getFlyChits()) {
					if (chit.getGameObject().hasThisAttribute(Constants.BROOMSTICK)) {
						possibleSecondaryTargets.add(chit.getGameObject());
						ArrayList<SpellWrapper> spells = SpellUtility.getBewitchingSpells(chit.getGameObject());
						if (spells!=null && !spells.isEmpty()) {
							gameObjects.add(chit.getGameObject());
						}
					}
				}
			} else if (participant.isMonster() || participant.isNative()) {				
				for (GameObject held : participant.getHold()) {
					if (held.hasThisAttribute(Constants.MONSTER_WEAPON)
							|| held.hasThisAttribute(Constants.SHIELD)
							|| held.hasThisAttribute(Constants.GIANT_CLUB)
							|| held.hasThisAttribute(Constants.GIANT_AXE)) {
						possibleSecondaryTargets.add(held);
						ArrayList<SpellWrapper> spells = SpellUtility.getBewitchingSpells(held);
						if (spells!=null && !spells.isEmpty()) {
							gameObjects.add(held);
						}
					}
				}
			}
		}
		
		return true;
	}
	
	public void updateSecondaryTargetsAfterSelection(TileLocation battleLocation, RealmComponent theTarget) {
		ArrayList<SpellWrapper> bewitchingSpells = SpellUtility.getBewitchingSpells(theTarget.getGameObject());
		RealmComponentOptionChooser spellChooser = new RealmComponentOptionChooser(combatFrame,"Select a spell for "+spell.getName()+":",false);
		for (SpellWrapper spell : bewitchingSpells) {
			if (!spell.isCurse() && !spell.isMesmerize()) {
				spellChooser.addRealmComponent(RealmComponent.getRealmComponent(spell.getGameObject()));
			}
		}
		CharacterWrapper character = new CharacterWrapper(theTarget.getGameObject());
		for (GameObject go:character.getInventory()) {
			if (go.hasThisAttribute(Constants.PHASE_CHIT)) {
				GameObject spellGo = theTarget.getGameObject().getGameData().getGameObject(Long.valueOf(go.getThisAttribute(Constants.SPELL_ID)));
				spellChooser.addRealmComponent(RealmComponent.getRealmComponent(spellGo));
			}
		}
		RealmComponent selectedSpell = null;
		if (spellChooser.hasOptions()) {
			spellChooser.setVisible(true);
			selectedSpell = spellChooser.getFirstSelectedComponent();
			if (selectedSpell!=null) {
				spell.setExtraIdentifier(selectedSpell.getGameObject().getStringId());
			}
			else {
				return;
			}
		}
		else {
			JOptionPane.showMessageDialog(combatFrame,"No spell target.",spell.getName()+" : No spell target available.",JOptionPane.INFORMATION_MESSAGE);
		}
		
		SpellWrapper selectedSpellWrapper = new SpellWrapper(selectedSpell.getGameObject());
		RealmComponentOptionChooser secondaryTargetChooser = new RealmComponentOptionChooser(combatFrame,"Select secondary target for "+spell.getName()+":",false);
		
		if (selectedSpellWrapper.isAbsorbEssence()) {
			for (GameObject go : possibleSecondaryTargets) {
				if (RealmComponent.getRealmComponent(go).isCharacter()) {
					secondaryTargetChooser.addRealmComponent(RealmComponent.getRealmComponent(go));
				}
			}
		}
		else {
			SpellTargeting spellTargeting = SpellTargeting.getTargeting(combatFrame,selectedSpellWrapper);
			if (spellTargeting == null) {
				JOptionPane.showMessageDialog(combatFrame,"No secondary target.",spell.getName()+" : No secondary target available.",JOptionPane.INFORMATION_MESSAGE);
			}
			spellTargeting.populate(battleModel, selectedSpell);
			for (GameObject go : possibleSecondaryTargets) {
				if (spellTargeting.getPossibleTargets().contains(go)) {
					secondaryTargetChooser.addRealmComponent(RealmComponent.getRealmComponent(go));
				}
			}
		}
		if (secondaryTargetChooser.hasOptions()) {
			secondaryTargetChooser.setVisible(true);
			RealmComponent secondaryTarget = secondaryTargetChooser.getFirstSelectedComponent();
			if (secondaryTarget!=null) {
				spell.setSecondaryTarget(secondaryTarget.getGameObject());
			}
		}
		else {
			JOptionPane.showMessageDialog(combatFrame,"No secondary target.",spell.getName()+" : No secondary target available.",JOptionPane.INFORMATION_MESSAGE);
		}
	}
}