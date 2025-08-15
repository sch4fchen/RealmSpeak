package com.robin.magic_realm.components.events;

import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.wrapper.GameWrapper;

public class Prowl1Event implements IEvent {
	private static final String title = "Prowl I";
	private static final String description = "An additional row of monsters is prowling this day.";
	public void applyBirdsong(GameData data) {
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<GameObject> mrGameObjects = pool.extract(GameWrapper.getKeyVals());
		if (mrGameObjects == null || mrGameObjects.isEmpty()) return;
		GameWrapper game = new GameWrapper(mrGameObjects.get(0));
		DieRoller monsterDie = game.getMonsterDie();
		monsterDie.addRedDie();
		game.setMonsterDie(monsterDie);
		RealmLogging.logMessage("Event","Prowl I: An additional row of monsters is prowling this day.");
	}
	public void applySunset(GameData data) {
	}
	public void expire(GameData data) {
	}
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public String getDescription(GameData data) {
		return description;
	}
}