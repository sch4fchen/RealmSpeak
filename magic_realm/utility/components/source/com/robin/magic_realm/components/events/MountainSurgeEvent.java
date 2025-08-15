package com.robin.magic_realm.components.events;

import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmCalendar;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.wrapper.GameWrapper;

public class MountainSurgeEvent implements IEvent {
	private static final String title = "Mountain Surge";
	private static final String description = "At sunset in a random hex Mountain Surge is cast and lasts until the seventh day of the week.";
	public void applyBirdsong(GameData data) {
		TileComponent tile = RealmEvents.chooseRandomTile(data);
		ArrayList<ClearingDetail> clearings = new ArrayList<>();
		if (tile!=null) {
			for (ClearingDetail cl : tile.getClearings()) {
				for (RealmComponent rc : cl.getClearingComponents()) {
					if (rc.isCharacter() || rc.isHiredOrControlled()) {
						clearings.add(cl);
					}
				}
			}
			if (!clearings.isEmpty()) {
				ClearingDetail chosenClearing = clearings.get(RandomNumber.getRandom(clearings.size()));
				GameObject config = RealmEvents.findEventsConfig(data);
				tile.getGameObject().addThisAttributeListItem(Constants.EVENT_MOUNTAIN_SURGE,chosenClearing.getNumString());
				RealmEvents.addEffectForTile(config,Constants.EVENT_MOUNTAIN_SURGE,tile.getGameObject().getStringId());
				RealmLogging.logMessage("Event","Mountain Surge: Cast Mountain Surge in "+chosenClearing.getNumString()+" of "+tile.getGameObject().getNameWithNumber()+".");
			}
		}
	}
	public void applySunset(GameData data) {
	}
	public void expire(GameData data) {
		GameWrapper game = GameWrapper.findGame(data);
		if (RealmCalendar.isSeventhDay(game.getDay())) {
			GameObject config = RealmEvents.findEventsConfig(data);
			ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_MOUNTAIN_SURGE);
			if (ids!=null && !ids.isEmpty()) {
				for (String id : ids) {
					GameObject tile = data.getGameObject(Long.valueOf(id));
					tile.removeThisAttribute(Constants.EVENT_MOUNTAIN_SURGE);
					RealmEvents.removeEffectForTile(config,Constants.EVENT_MOUNTAIN_SURGE,id);
				}
			}
		}
	}
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public String getDescription(GameData data) {
		GameObject config = RealmEvents.findEventsConfig(data);
		String text = "";
		ArrayList<String> ids = RealmEvents.getTileIdsForEffect(config,Constants.EVENT_MOUNTAIN_SURGE);
		if (ids!=null && !ids.isEmpty()) {
			for (String id : ids) {
				GameObject tile = data.getGameObject(Long.valueOf(id));
				for (String cl : tile.getThisAttributeList(Constants.EVENT_MOUNTAIN_SURGE)) {;
					text = text + tile.getNameWithNumber() +" ("+cl+")"+ ", ";
				}
			}
		}
		else {
			return description;
		}
		if (text.isEmpty()) return description;
		text = "In "+text.substring(0,text.length()-2) + " Mountain Surge is cast and lasts until the seventh day of the week.";
		return text;
	}
}