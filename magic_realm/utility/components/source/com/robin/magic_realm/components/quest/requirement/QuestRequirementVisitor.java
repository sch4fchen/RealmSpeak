package com.robin.magic_realm.components.quest.requirement;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementVisitor extends QuestRequirement {
	
	public static final String VISITOR_REGEX = "_regex";
	public static final String SAME_TILE = "_tile";
	public static final String MARK = "_mark";

	public QuestRequirementVisitor(GameObject go) {
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
				for (RealmComponent visitor : cl.getClearingComponents()) {
					if (!visitor.isVisitor()) continue;
					if (getRegExFilter().isEmpty() || pattern.matcher(visitor.getGameObject().getName()).find()) {
						if (requiresMark() && !Quest.GameObjectHasQuestMark(visitor.getGameObject(), questId)) {
							continue;
						}
						return true;
					}
				}
			}
		} else {
			for (RealmComponent visitor : loc.clearing.getClearingComponents()) {
				if (!visitor.isVisitor()) continue;
				if (getRegExFilter().isEmpty() || pattern.matcher(visitor.getGameObject().getName()).find()) {
					if (requiresMark() && !Quest.GameObjectHasQuestMark(visitor.getGameObject(), questId)) {
						continue;
					}
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
		sb.append(" visitor");
		if (!getRegExFilter().isEmpty()) {
			sb.append("with the name: "+getRegExFilter());
		}
		else {
			sb.append(".");
		}
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Visitor;
	}
	
	private String getRegExFilter() {
		return getString(VISITOR_REGEX).trim();
	}
	private boolean sameTile() {
		return getBoolean(SAME_TILE);
	}
	public boolean requiresMark() {
		return getBoolean(MARK);
	}
}