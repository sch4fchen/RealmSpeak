package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.ChitType;
import com.robin.magic_realm.components.quest.LocationClearingType;
import com.robin.magic_realm.components.quest.LocationTileSideType;
import com.robin.magic_realm.components.quest.QuestConstants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementClearing extends QuestRequirement {
	public static final String TILE = "_tile";
	public static final String TYPE = "_type";
	public static final String TILE_SIDE = "_tile_side";
	public static final String CHIT_NAME = "_chit_name";
	public static final String CHIT_TYPE = "_chit_type";
	public static final String CHIT_AMOUNT = "_chit_amount";
	public static final String MARK_REQUIRED = "_req_mark";

	public QuestRequirementClearing(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		TileLocation loc = character.getCurrentLocation();
		if (loc == null || loc.clearing == null) return false;
		if (getTileSide() !=  LocationTileSideType.Any && !getTileSide().matches(loc.tile)) return false;
		
		ArrayList<ClearingDetail> clearingsToCheck = new ArrayList<>();
		if (checkTile()) {
			clearingsToCheck.addAll(loc.tile.getClearings());
		}
		else {
			clearingsToCheck.add(loc.clearing);
		}
		
		if (noChitsToCheck()) {
			for (ClearingDetail clearing : clearingsToCheck) {
				if (getClearingType() != LocationClearingType.Any && !getClearingType().matches(clearing)) continue;
				return true;
			}
			return false;
		}
		
		if (getChitAmount() == 0) return true;
		ArrayList<RealmComponent> componentsToCheck = new ArrayList<>();
		for (ClearingDetail clearing : clearingsToCheck) {
			if (getClearingType() != LocationClearingType.Any && !getClearingType().matches(clearing)) continue;
			componentsToCheck.addAll(clearing.getDeepClearingComponents());
		}
		int foundChits = 0;
		String regex = getChitName().trim();
		Pattern pattern = regex.length()==0?null:Pattern.compile(regex);
		String questId = getParentQuest().getGameObject().getStringId();
		
		for (RealmComponent rc : componentsToCheck) {
			if (pattern != null && !pattern.matcher(rc.getGameObject().getName()).find()) continue;
			if (getChitType() != null && getChitType() != ChitType.Any && !getChitType().matches(rc)) continue;
			if (requiresMark()) {
				String mark = rc.getGameObject().getThisAttribute(QuestConstants.QUEST_MARK);
				if (mark==null || !mark.equals(questId)) continue;
			}
			foundChits++;
			if (foundChits == getChitAmount()) return true;
		}
		
		return false;
	}

	private boolean noChitsToCheck() {
		return ((getChitName()==null || getChitName().isEmpty()) && (getChitType()==null || getChitType() == ChitType.Any) && !requiresMark());
	}
	
	protected String buildDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Character must be in a");
		if (getTileSide() != LocationTileSideType.Any) {
			sb.append(" "+getTileSide());
		}
		if (checkTile()) {
			sb.append(" tile with a clearing");
		} else {
			sb.append(" clearing");
		}
		if (getClearingType() != LocationClearingType.Any) {
			sb.append(" of type "+getClearingType());
		}
		if (!noChitsToCheck()) {
			sb.append(" with");
			if (getChitAmount() == 1) {
				sb.append(" a");
			}
			if (getChitAmount() != 0 && getChitAmount() != 1) {
				sb.append(" "+getChitAmount());
			}
			if (requiresMark()) {
				sb.append(" marked");
			}
			if (getChitAmount() == 1) {
				sb.append(" chit");
			}
			else {
				sb.append(" chits");
			}
			if ((getChitName() != null && !getChitName().isEmpty()) || (getChitType() != null && getChitType() != ChitType.Any)) {
				sb.append(" matching");
			}
			if (getChitName() != null && !getChitName().isEmpty()) {
				sb.append(" the name "+getChitName());
			}
			if (getChitName() != null && !getChitName().isEmpty() && getChitType() != null && getChitType() != ChitType.Any) {
				sb.append(" and");
			}
			if (getChitType() != null && getChitType() != ChitType.Any) {
				sb.append(" the type "+getChitType());
			}
		}
		sb.append(".");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Clearing;
	}
	public boolean checkTile() {
		return getBoolean(TILE);
	}
	public LocationClearingType getClearingType() {
		return LocationClearingType.valueOf(getString(TYPE));
	}
	public LocationTileSideType getTileSide() {
		return LocationTileSideType.valueOf(getString(TILE_SIDE));
	}
	public String getChitName() {
		return getString(CHIT_NAME);
	}
	public ChitType getChitType() {
		return ChitType.valueOf(getString(CHIT_TYPE));
	}
	public int getChitAmount() {
		return getInt(CHIT_AMOUNT);
	}
	private boolean requiresMark() {
		return getBoolean(MARK_REQUIRED);
	}
}