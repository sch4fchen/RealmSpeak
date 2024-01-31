package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.magic_realm.components.attribute.RelationshipType;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementRelationship extends QuestRequirement {

	public static final String NATIVE_GROUP = "_native_grp_rgx";
	public static final String RELATIONSHIP_LEVEL = "_rel_lvl";
	public static final String EXCEED_LEVEL = "_excd_lvl";
	public static final String SUBCEED_LEVEL = "_sucd_lvl";
	
	public QuestRequirementRelationship(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		Pattern pattern = Pattern.compile(getNativesRegex().toLowerCase());
		for (GameObject nativeGroup : getRepresentativeNatives(character)) {
			if (getNativesRegex().isEmpty() || pattern.matcher(nativeGroup.getName().toLowerCase()).find()) {
					if (exceedAllowed() && character.getRelationship(nativeGroup) >= getRelationshipLevel()) {
						return true;
					}
					if (subceedAllowed() && character.getRelationship(nativeGroup) <= getRelationshipLevel()) {
						return true;
					}
					if(character.getRelationship(nativeGroup) == getRelationshipLevel()) {
						return true;
					}
			}
		}
		return false;
	}

	protected String buildDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Characters relationship with ");
		if (!getNativesRegex().isEmpty()) {
				sb.append(getNativesRegex());
		}
		else {
			sb.append("any natives");
		}
		sb.append(" must be "+getRelationship());
		if (exceedAllowed()) {
			sb.append(" or better");
		}
		if (subceedAllowed()) {
			sb.append(" or worse");
		}
		sb.append(".");
		return sb.toString();
	}
	public RequirementType getRequirementType() {
		return RequirementType.Relationship;
	}
	private String getNativesRegex() {
		return getString(NATIVE_GROUP);
	}
	public static ArrayList<GameObject> getRepresentativeNatives(CharacterWrapper character) {
		GamePool pool = new GamePool(character.getGameData().getGameObjects());
		ArrayList<String> queryNatives = new ArrayList<String>();
		ArrayList<String> queryVisitors = new ArrayList<String>();
		queryNatives.add("native");
		queryNatives.add("rank=HQ");
		queryVisitors.add(Constants.VISITOR);
		ArrayList<GameObject> representativeNatives = pool.find(queryNatives);
		representativeNatives.addAll(pool.find(queryVisitors));
		return representativeNatives;
	}
	private int getRelationshipLevel() {
		return RelationshipType.getIntFor(getRelationship());
	}
	private String getRelationship() {
		return getString(RELATIONSHIP_LEVEL);
	}
	private boolean exceedAllowed() {
		return getBoolean(EXCEED_LEVEL);
	}
	private boolean subceedAllowed() {
		return getBoolean(SUBCEED_LEVEL);
	}
}