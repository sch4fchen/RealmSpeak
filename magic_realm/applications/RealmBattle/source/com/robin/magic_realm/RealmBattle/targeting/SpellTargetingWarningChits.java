package com.robin.magic_realm.RealmBattle.targeting;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.utility.Constants;
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
		String type = warningChit.getThisAttribute(RealmComponent.TILE_TYPE);
		GamePool pool = new GamePool(activeCharacter.getGameData().getGameObjects());
		ArrayList<String> query = new ArrayList<>();
		query.add(RealmComponent.WARNING);
		query.add("!"+RealmComponent.DWELLING);
		query.add(RealmComponent.TILE_TYPE+"="+type);
		ArrayList<GameObject> options = pool.find(query);
		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(combatFrame,"Select other warning chit",false);
		String key = "chit";
		int keyN = 0;
		for (GameObject option : options) {
			RealmComponent rc = RealmComponent.getRealmComponent(option);
			GameObject tile = option.getHeldBy();
			chooser.addOption(key+keyN,tile.getNameWithNumber());
			chooser.addRealmComponentToOption(key+keyN,rc);
			keyN++;
		}
		chooser.setVisible(true);
		spell.addTarget(hostPrefs,warningChit);
		spell.setExtraIdentifier(chooser.getFirstSelectedComponent().getGameObject().getStringId());
		return true;
	}
	public boolean populate(BattleModel battleModel, RealmComponent activeParticipant) {
		gameObjects.clear();
		TileLocation loc = battleModel.getBattleLocation();
		if (loc == null || loc.tile == null) return false;
		ArrayList<String> invalidTypes = new ArrayList<>();
		if (spell.getGameObject().hasThisAttribute(Constants.TARGET_INVALID_CHIT_TYPES)) {
			StringTokenizer types = new StringTokenizer(spell.getGameObject().getThisAttribute(Constants.TARGET_INVALID_CHIT_TYPES),",");
			while(types.hasMoreTokens()) {
				invalidTypes.add(types.nextToken());
			}
		}
		for (GameObject go : loc.tile.getHold()) {
			if (go.hasThisAttribute(RealmComponent.WARNING) && !go.hasThisAttribute(RealmComponent.DWELLING)) {
				if (!invalidTypes.contains(go.getThisAttribute(RealmComponent.TILE_TYPE))) {
					gameObjects.add(go);
				}
			}
		}
		return true;
	}
}