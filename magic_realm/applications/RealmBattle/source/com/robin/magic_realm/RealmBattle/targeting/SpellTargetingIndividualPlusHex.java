package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.RealmObjectMaster;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingIndividualPlusHex extends SpellTargetingIndividual {
	public SpellTargetingIndividualPlusHex(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}
	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		super.populate(battleModel,activeParticipant);
		
		secondaryTargetChoiceString = "Select a tile to FLY the target to:";
		TileLocation here = battleModel.getBattleLocation();
		
		ArrayList<GameObject> adjTiles = new ArrayList<>();
		for (TileComponent tile : here.tile.getAllAdjacentTiles()) {
			adjTiles.add(tile.getGameObject());
		}
		
		if (adjTiles.isEmpty()) { // this only happens during battle simulator
			RealmObjectMaster rom = RealmObjectMaster.getRealmObjectMaster(battleModel.getGameData());
			adjTiles.addAll(rom.getTileObjects());
		}
		
		for (GameObject go : gameObjects) {
			if (!RealmComponent.getRealmComponent(go).hasMagicProtection()) {
				identifiers.add(go.getName());
				secondaryTargets.put(go.getName(),adjTiles);
			}
		}
		return true;
	}
}