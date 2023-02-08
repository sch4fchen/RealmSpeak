package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.swing.RealmObjectChooser;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.*;

public abstract class SpellTargetingMultiple extends SpellTargeting {

	public SpellTargetingMultiple(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean hasTargets() {
		return !gameObjects.isEmpty();
	}
	
	public boolean assign(HostPrefWrapper hostPrefs,CharacterWrapper activeCharacter) {
		int requiredTargets = spell.getGameObject().getThisInt("min_targets"); // most spells, this will be zero
		int maxTargets = spell.getGameObject().getThisInt("max_targets");
		GameData gameData = spell.getGameObject().getGameData();
		RealmObjectChooser chooser = new RealmObjectChooser("Select targets for "+spell.getName()+":",gameData,false);
		chooser.addObjectsToChoose(gameObjects);
		if (maxTargets>0) {
			chooser.setValidRange(1,maxTargets);
		}
		chooser.setVisible(true);
		if (chooser.pressedOkay()) {
			ArrayList<GameObject> chosen = new ArrayList<>(chooser.getChosenObjects());
			
			// This is lazy, but there is really only ONE spell that has such a rule, so I'm not going generalize
			if (requiredTargets==4 && chosen.size()<requiredTargets) {
				if (chosen.size()==1) {
					// All 4 go to same target
					GameObject target = chosen.get(0);
					for (int i=0;i<3;i++) {
						chosen.add(target);
					}
				}
				else if (chosen.size()==2) {
					// 2 go to each
					GameObject t1 = chosen.get(0);
					GameObject t2 = chosen.get(1);
					chosen.add(t1);
					chosen.add(t2);
				}
				else if (chosen.size()==3) {
					// Pick one of the three
					RealmComponentOptionChooser doubleChooser = new RealmComponentOptionChooser(
									combatFrame,
									spell.getGameObject().getName()+" has a minimum of 4 attacks. Which target will be hit twice?",
									false);
					doubleChooser.addGameObjects(chosen,false);
					doubleChooser.setVisible(true);
					if (doubleChooser.getSelectedText()!=null) {
						RealmComponent rc = doubleChooser.getFirstSelectedComponent();
						chosen.add(rc.getGameObject());
					}
				}
			}
			
			for (GameObject theTarget : chosen) {
				spell.addTarget(hostPrefs,theTarget);
				combatFrame.makeWatchfulNatives(RealmComponent.getRealmComponent(theTarget),true);
				String append = "";
				if (hostPrefs.hasPref(Constants.OPT_RIDING_HORSES)) {
					RealmComponent targetRc = RealmComponent.getRealmComponent(theTarget);
					if (targetRc.isNative() && targetRc.getHorse()!=null) {
						CombatWrapper combat = new CombatWrapper(theTarget);
						combat.setTargetingRider(spell.getGameObject());
						append = " (aiming for rider)";
					}
				}
				CombatFrame.broadcastMessage(activeCharacter.getGameObject().getName(),"Targets the "+theTarget.getNameWithNumber()+" with "+spell.getGameObject().getName()+append);
			}
			return true;
		}
		return false;
	}
}