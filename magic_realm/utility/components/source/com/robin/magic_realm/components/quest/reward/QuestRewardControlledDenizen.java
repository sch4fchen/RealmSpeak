package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.PathDetail;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.GainType;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.quest.QuestStep;
import com.robin.magic_realm.components.utility.ClearingUtility;
import com.robin.magic_realm.components.utility.TemplateLibrary;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class QuestRewardControlledDenizen extends QuestReward {
	private static Logger logger = Logger.getLogger(QuestStep.class.getName());
	public static final String DENIZEN_NAME   = "_name";
	public static final String GAIN_TYPE      = "_gain_type";
	public static final String DENIZEN_RENAME = "_rename";
	public static final String RETAIN_ON_MAP  = "_retain_on_map";
	public static final String LOCATION_ONLY = "_loc_only";
	public static final String LOCATION = "_loc";
	public static final String MARK = "_mark";
	public static final String REQ_MARK = "_req_mark";

	public QuestRewardControlledDenizen(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame, CharacterWrapper character) {
		if (getGainType() == GainType.Gain) {
			GameObject template = TemplateLibrary.getSingleton().getCompanionTemplate(getDenizenKeyName(), getDenizenQuery());
			GameObject companion = TemplateLibrary.getSingleton().createCompanionFromTemplate(getGameData(), template);
			String gameKeyVals = HostPrefWrapper.findHostPrefs(getGameData()).getGameKeyVals();
			if (gameKeyVals != null && !gameKeyVals.isEmpty()) {
				for (String kv : gameKeyVals.split(",")) {
					String k = kv.trim();
					if (!k.isEmpty()) companion.setThisAttribute(k);
				}
			}
			if (renameDenizenTo() != null && !renameDenizenTo().isEmpty()) {
				companion.setName(renameDenizenTo());
			}
			if (mark()) {
				Quest.GameObjectAddQuestMark(companion, getParentQuest().getGameObject().getStringId());
			}
			if (!character.getCurrentLocation().clearing.isEdge()) {
				character.getCurrentLocation().clearing.add(companion, null);
			} else {
				ArrayList<PathDetail> paths = character.getCurrentLocation().clearing.getConnectedPaths();
				ClearingDetail adjacent = paths.get(0).findConnection(character.getCurrentLocation().clearing);
				adjacent.add(companion, null);
			}
			if (locationOnly()) {
				QuestLocation loc = getQuestLocation();
				if (loc == null) return;
				ArrayList<TileLocation> validLocations = new ArrayList<>();
				validLocations = loc.fetchAllLocations(frame, character, getGameData());
				if(validLocations.isEmpty()) {
					logger.fine("QuestLocation "+loc.getName()+" doesn't have any valid locations!");
					return;
				}
				int random = RandomNumber.getRandom(validLocations.size());
				TileLocation tileLocation = validLocations.get(random);
				tileLocation.clearing.add(companion,null);
			}
			CharacterWrapper controlled = new CharacterWrapper(companion);
			controlled.setPlayerName(character.getPlayerName());
			controlled.setWantsCombat(character.getWantsCombat());
			RealmComponent.getRealmComponent(companion).setOwner(RealmComponent.getRealmComponent(character.getGameObject()));
		} else {
			String targetQuery = getDenizenQuery();
			String targetKeyName = getDenizenKeyName();
			String charId = String.valueOf(character.getGameObject().getId());
			String questId = getParentQuest().getGameObject().getStringId();
			for (GameObject companion : character.getGameData().getGameObjects()) {
				if (requiresMark() && !Quest.GameObjectHasQuestMark(companion, questId)) continue;
				RealmComponent rc = RealmComponent.getRealmComponent(companion);
				if (rc == null || rc.getOwnerId() == null) continue;
				if (!rc.getOwnerId().equals(charId)) continue;
				String storedQuery = companion.getThisAttribute("query");
				if (!targetQuery.equals(storedQuery) && !targetKeyName.equals(companion.getName())) continue;
				CharacterWrapper cw = new CharacterWrapper(companion);
				cw.removePlayerName();
				rc.clearOwner();
				if (!retainOnMap()) {
					ClearingUtility.moveToLocation(companion, null);
				}
				if (mark()) {
					Quest.GameObjectAddQuestMark(companion, getParentQuest().getGameObject().getStringId());
				}
				return;
			}
		}
	}

	public ImageIcon getIcon() {
		GameObject template = TemplateLibrary.getSingleton()
			.getCompanionTemplate(getDenizenKeyName(), getDenizenQuery());
		if (template == null) return null;
		RealmComponent rc = RealmComponent.getRealmComponent(template);
		return rc == null ? null : rc.getIcon();
	}

	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append(getDenizenKeyName());
		if (getGainType() == GainType.Gain) {
			sb.append(" is summoned to the character's clearing and controlled (gets own character window).");
		} else {
			sb.append(" is released from control");
			sb.append(retainOnMap() ? " (stays on map)." : " (removed from map).");
		}
		return sb.toString();
	}

	public RewardType getRewardType() {
		return RewardType.ControlledDenizen;
	}

	private GainType getGainType() {
		return GainType.valueOf(getString(GAIN_TYPE));
	}

	private String getDenizenKeyName() {
		return getString(QuestConstants.KEY_PREFIX + DENIZEN_NAME);
	}

	private String getDenizenQuery() {
		return getString(QuestConstants.VALUE_PREFIX + DENIZEN_NAME);
	}

	private String renameDenizenTo() {
		return getString(DENIZEN_RENAME);
	}

	private boolean retainOnMap() {
		return getBoolean(RETAIN_ON_MAP);
	}
	
	private boolean locationOnly() {
		return getBoolean(LOCATION_ONLY);
	}
	
	private boolean mark() {
		return getBoolean(MARK);
	}
	
	private boolean requiresMark() {
		return getBoolean(REQ_MARK);
	}
	
	public boolean usesLocationTag(String tag) {
		QuestLocation loc = getQuestLocation();
		return loc!=null && tag.equals(loc.getName());
	}
	public QuestLocation getQuestLocation() {
		String id = getString(LOCATION);
		if (id!=null) {
			GameObject go = getGameData().getGameObject(Long.valueOf(id));
			if (go!=null) {
				return new QuestLocation(go);
			}
		}
		return null;
	}
	
	public void setQuestLocation(QuestLocation location) {
		setString(LOCATION,location.getGameObject().getStringId());
	}
	public void updateIds(Hashtable<Long, GameObject> lookup) {
		updateIdsForKey(lookup,LOCATION);
	}
}
