package com.robin.magic_realm.components.quest.requirement;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementTraveler extends QuestRequirement {
	
	public static final String TRAVELER_REGEX = "_regex";
	public static final String MARK = "_mark";

	public QuestRequirementTraveler(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		TileLocation loc = character.getCurrentLocation();
		if (loc==null || !loc.isInClearing()) return false;
		Pattern pattern = Pattern.compile(getRegExFilter());
		String questId = getParentQuest().getGameObject().getStringId();
		for (RealmComponent traveler : loc.clearing.getClearingComponents()) {
			if (getRegExFilter().isEmpty() || pattern.matcher(traveler.getGameObject().getName()).find()) {
				if (requiresMark()) {
					String mark = traveler.getGameObject().getThisAttribute(QuestConstants.QUEST_MARK);
					if (mark==null || !mark.equals(questId)) continue;
				}
				return true;
			}
		}
		return false;
	}
	
	protected String buildDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Character must be in the same clearing as");
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
	public boolean requiresMark() {
		return getBoolean(MARK);
	}
}