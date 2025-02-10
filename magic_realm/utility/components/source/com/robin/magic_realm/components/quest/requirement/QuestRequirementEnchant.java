package com.robin.magic_realm.components.quest.requirement;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementEnchant extends QuestRequirement {

	public static final String TYPE = "_type";
	public static final String SITE = "_site";
	public static final String NONE = "none";
	
	public QuestRequirementEnchant(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		if (getSite()!=null) {
			if (reqParams.objectList==null||reqParams.objectList.isEmpty()) {
				return false;
			}
			GameObject go = reqParams.objectList.get(0);
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			TileComponent tile = (TileComponent)rc;
			boolean foundTl = false;
			for (ClearingDetail cl : tile.getClearings()) {
				if (foundTl) {
					break;
				}
				for (RealmComponent site : cl.getTreasureLocationsAndRedSpecialsAndDwellings()) {
					if (site.toString().toLowerCase().matches(getSite().toLowerCase())) {
						foundTl = true;
						break;
					}
				}
			}
			if (!foundTl) {
				return false;
			}
		}
		return reqParams.actionType == CharacterActionType.Enchant && getType().matches(reqParams.actionName);
	}

	protected String buildDescription() {
		if (getType().matches("chit")) {
			return "Character must enchant a chit.";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Character must enchant a tile");
		if (getSite()!=null) {
			sb.append(" with the "+getSite());
		}
		sb.append(".");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Enchant;
	}
	
	private String getType() {
		return getString(TYPE);
	}
	
	private String getSite() {
		String tl = getString(SITE);
		if (tl==null || tl.matches(NONE)) {
			return null;
		}
		return tl;
	}
}