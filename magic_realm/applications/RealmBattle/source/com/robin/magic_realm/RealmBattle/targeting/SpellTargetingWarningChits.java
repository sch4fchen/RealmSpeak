package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.swing.RealmObjectChooser;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingWarningChits extends SpellTargeting {

	protected SpellTargetingWarningChits(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}
	public boolean hasTargets() {
		return !gameObjects.isEmpty();
	}
	public boolean assign(HostPrefWrapper hostPrefs, CharacterWrapper activeCharacter) {
		GameObject warningChit = null;
		if (gameObjects.size()==1) {
			warningChit = gameObjects.get(0);
		}
		else {
			warningChit = gameObjects.get(RandomNumber.getRandom(gameObjects.size()));
		}
		spell.addTarget(hostPrefs,warningChit);
		String type = warningChit.getThisAttribute(RealmComponent.TILE_TYPE);
		GamePool pool = new GamePool(activeCharacter.getGameData().getGameObjects());
		ArrayList<String> query = new ArrayList<>();
		query.add(RealmComponent.WARNING);
		query.add("!"+RealmComponent.DWELLING);
		query.add(RealmComponent.TILE_TYPE+"="+type);
		//RealmComponentOptionChooser chooser2 = new RealmComponentOptionChooser();
		RealmObjectChooser chooser = new RealmObjectChooser("Select other warning chit",activeCharacter.getGameData(),true);
		chooser.addObjectsToChoose(pool.find(query));
		chooser.setVisible(true);
		if (!chooser.pressedOkay()) return false;
		return true;
	}
	public boolean populate(BattleModel battleModel, RealmComponent activeParticipant) {
		gameObjects.clear();
		TileLocation loc = battleModel.getBattleLocation();
		if (loc == null || loc.tile == null) return false;
		for (GameObject go : loc.tile.getHold()) {
			if (go.hasThisAttribute(RealmComponent.WARNING) && !go.hasThisAttribute(RealmComponent.DWELLING)) { 
				gameObjects.add(go);
			}
		}
		return hasTargets();
	}
}

/*
Migration: PURPLE, warning chit, Instant: Swap the warning chit in the spellcaster’s hex with another warning
chit of the same tile type. The chits can be face-up or face-down. The chits cannot be of type ‘V’ (Valley) or ‘H’
(Hills). Example: spellcaster takes the Bones F chit in his Forest tile, and moves it to the tile with the Stink F chit;
then he takes the Stink F chit and moves it to his tile. Note: If this causes warning chits to be moved that brought
out a camp or campfire location, those locations move to the new tile during regeneration, as if being placed on the
map for the first time (see rule 8.15.2.g).
*/