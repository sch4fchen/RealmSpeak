package com.robin.magic_realm.components.events;

import com.robin.game.objects.GameData;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.utility.SetupCardUtility;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class RegenerateEvent implements IEvent {
	private static final String title = "Regenerate";
	private static final String description = "All prowling denizens immediately regenerate.";
	public void applyBirdsong(GameData data) {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(data);
		GameWrapper game = GameWrapper.findGame(data);
		SetupCardUtility.resetGeneralDwellings(data);
		DieRoller monsterDieRoller = game.getMonsterDie();
		SetupCardUtility.resetDenizens(data, monsterDieRoller.getValue(0), hostPrefs.hasPref(Constants.SR_HORSES_REGENERATION));
		if (monsterDieRoller.getNumberOfDice()>1) {
			SetupCardUtility.resetDenizens(data, monsterDieRoller.getValue(1), hostPrefs.hasPref(Constants.SR_HORSES_REGENERATION));
		}
		DieRoller nativeDieRoller = game.getNativeDie();
		SetupCardUtility.resetNatives(data, nativeDieRoller.getValue(0));
		if (nativeDieRoller.getNumberOfDice()>1) {
			SetupCardUtility.resetNatives(data, nativeDieRoller.getValue(1));
		}
		RealmLogging.logMessage("Event","Regenerate: All prowling denizens regenerated.");
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