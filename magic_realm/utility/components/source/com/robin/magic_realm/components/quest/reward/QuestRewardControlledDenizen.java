package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.PathDetail;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.quest.GainType;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.utility.ClearingUtility;
import com.robin.magic_realm.components.utility.TemplateLibrary;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class QuestRewardControlledDenizen extends QuestReward {
	public static final String DENIZEN_NAME   = "_dn";
	public static final String GAIN_TYPE      = "_goc";
	public static final String DENIZEN_RENAME = "_dname";
	public static final String RETAIN_ON_MAP  = "_rom";

	public QuestRewardControlledDenizen(GameObject go) {
		super(go);
	}

	public void processReward(JFrame frame, CharacterWrapper character) {
		if (getGainType() == GainType.Gain) {
			GameObject template = TemplateLibrary.getSingleton()
				.getCompanionTemplate(getDenizenKeyName(), getDenizenQuery());
			GameObject companion = TemplateLibrary.getSingleton()
				.createCompanionFromTemplate(getGameData(), template);
			// Stamp the companion with the active game's variant attribute(s) so
			// getPlayerCharacterObjects() (which prefixes the host's game key vals)
			// can find it regardless of which expansion the template came from.
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
			if (!character.getCurrentLocation().clearing.isEdge()) {
				character.getCurrentLocation().clearing.add(companion, null);
			} else {
				ArrayList<PathDetail> paths = character.getCurrentLocation().clearing.getConnectedPaths();
				ClearingDetail adjacent = paths.get(0).findConnection(character.getCurrentLocation().clearing);
				adjacent.add(companion, null);
			}
			// Mirror ControlEffect.apply() so the denizen gets its own CharacterFrame
			CharacterWrapper controlled = new CharacterWrapper(companion);
			controlled.setPlayerName(character.getPlayerName());
			controlled.setWantsCombat(character.getWantsCombat());
			RealmComponent.getRealmComponent(companion)
				.setOwner(RealmComponent.getRealmComponent(character.getGameObject()));
		} else {
			// Lose path: match by owner ID + stored query attribute (name-based search fails
			// when the companion was renamed after summoning).
			String targetQuery = getDenizenQuery();
			String targetKeyName = getDenizenKeyName();
			String charId = String.valueOf(character.getGameObject().getId());
			for (GameObject go : character.getGameData().getGameObjects()) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (rc == null || rc.getOwnerId() == null) continue;
				if (!rc.getOwnerId().equals(charId)) continue;
				String storedQuery = go.getThisAttribute("query");
				if (!targetQuery.equals(storedQuery) && !targetKeyName.equals(go.getName())) continue;
				CharacterWrapper cw = new CharacterWrapper(go);
				cw.removePlayerName();
				rc.clearOwner();
				if (!retainOnMap()) {
					ClearingUtility.moveToLocation(go, null);
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
}
