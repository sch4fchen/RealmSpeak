package com.robin.magic_realm.components.quest.requirement;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementTraveler extends QuestRequirement {
	
	public static final String TRAVELER_REGEX = "_regex";
	public static final String SAME_TILE = "_tile";
	public static final String MARK = "_mark";
	public static final String NO_MARK = "_no_mark";

	public QuestRequirementTraveler(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		TileLocation loc = character.getCurrentLocation();
		if (loc==null) return false;
		if (!sameTile() && !loc.isInClearing()) return false;
		Pattern pattern = Pattern.compile(getRegExFilter());
		String questId = getParentQuest().getGameObject().getStringId();
		if (sameTile()) {
			for (ClearingDetail cl : loc.tile.getClearings()) {
				for (RealmComponent traveler : cl.getClearingComponents()) {
					if (!traveler.isTraveler()) continue;
					if (getRegExFilter().isEmpty() || pattern.matcher(traveler.getGameObject().getName()).find()) {
						if (requiresMark() && !Quest.GameObjectHasQuestMark(traveler.getGameObject(), questId)) continue;
						if (requiresNoMark() && Quest.GameObjectHasQuestMark(traveler.getGameObject(), questId)) continue;
						return true;
					}
				}
			}
		} else {
			for (RealmComponent traveler : loc.clearing.getClearingComponents()) {
				if (!traveler.isTraveler()) continue;
				if (getRegExFilter().isEmpty() || pattern.matcher(traveler.getGameObject().getName()).find()) {
					if (requiresMark() && !Quest.GameObjectHasQuestMark(traveler.getGameObject(), questId)) continue;
					if (requiresNoMark() && Quest.GameObjectHasQuestMark(traveler.getGameObject(), questId)) continue;
					return true;
				}
			}
		}
		return false;
	}
	
	protected String buildDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Character must be in the same");
		if (sameTile()) {
			sb.append(" tile as");
		} else {
			sb.append(" clearing as");
		}
		if (requiresMark()) {
			sb.append(" a marked");
		}
		sb.append(" traveler");
		if (!getRegExFilter().isEmpty()) {
			sb.append("with the name: "+getRegExFilter());
		}
		else {
			sb.append(".");
		}
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Traveler;
	}
	
	private String getRegExFilter() {
		return getString(TRAVELER_REGEX).trim();
	}
	private boolean sameTile() {
		return getBoolean(SAME_TILE);
	}
	public boolean requiresMark() {
		return getBoolean(MARK);
	}
	public boolean requiresNoMark() {
		return getBoolean(NO_MARK);
	}
}