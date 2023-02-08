package com.robin.magic_realm.RealmBattle.targeting;

import javax.swing.JOptionPane;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public abstract class SpellTargetingAll extends SpellTargeting {
	public SpellTargetingAll(CombatFrame combatFrame,SpellWrapper spell) {
		super(combatFrame,spell);
	}
	public boolean hasTargets() {
		return !gameObjects.isEmpty();
	}
	public boolean assign(HostPrefWrapper hostPrefs,CharacterWrapper activeCharacter) {
		boolean ignorebattle = spell.getGameObject().hasThisAttribute("nobattle");
		for (GameObject theTarget : gameObjects) {
			spell.addTarget(hostPrefs,theTarget,ignorebattle);
			if (!ignorebattle) {
				combatFrame.makeWatchfulNatives(RealmComponent.getRealmComponent(theTarget),true);
			}
			CombatFrame.broadcastMessage(activeCharacter.getGameObject().getName(),"Targets the "+theTarget.getNameWithNumber()+" with "+spell.getGameObject().getName());
		}
		JOptionPane.showMessageDialog(combatFrame,"All valid targets are selected.",spell.getName()+" Selects ALL",JOptionPane.INFORMATION_MESSAGE);
		return true;
	}
}