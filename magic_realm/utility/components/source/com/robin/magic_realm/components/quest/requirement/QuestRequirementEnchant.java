package com.robin.magic_realm.components.quest.requirement;

import java.util.StringTokenizer;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementEnchant extends QuestRequirement {

	public static final String TYPE = "_type";
	public static final String CHIT = "_chit";
	public static final String SITE = "_site";
	
	public QuestRequirementEnchant(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		if (getSite()!=null || getChit()!=null) {
			if ((reqParams.objectList==null||reqParams.objectList.isEmpty())) {
				return false;
			}
			GameObject go = reqParams.objectList.get(0);
			String chitName = null;
			if (getChit()!=null) {
				chitName = getChit().toLowerCase();
			}
			
			if (getType().matches(RealmComponent.TILE)) {
				RealmComponent tileRc = RealmComponent.getRealmComponent(go);
				if (!tileRc.isTile()) {
					return false;
				}
				TileComponent tile = (TileComponent)tileRc;
				boolean foundTl = false;
				boolean foundChit = false;
				for (RealmComponent rc : tile.getAllClearingComponents()) {
					if (foundTl && foundChit) {
						break;
					}
					if (!foundTl && (rc.isTreasureLocation() || rc.isRedSpecial() || rc.isDwelling()) && !rc.isCacheChit()) {
						if (rc.getGameObject().getName().toLowerCase().matches(getSite().toLowerCase())) {
							foundTl = true;
							break;
						}
					}
					if (!foundChit && (rc.isWarning() || rc.isSound())) {
						String name = rc.getGameObject().getName().toLowerCase().trim();
						String nameWithoutTileType = null;
						StringTokenizer stringTokenizer = new StringTokenizer(name," ");
						if (stringTokenizer.hasMoreTokens()) {
							nameWithoutTileType = stringTokenizer.nextToken().toLowerCase();
						}
						if (chitName == null || name.matches(chitName) || nameWithoutTileType.matches(chitName)) {
							foundChit = true;
						}
					}
				}
				
				if (getSite()!=null && !foundTl) {
					return false;
				}
				if (getChit()!=null && !foundChit) {
					return false;
				}
			}
			else {
				if (getChit()!=null) {
					String name = go.getName().toLowerCase().trim();
					String nameWithoutTileType = null;
					StringTokenizer stringTokenizer = new StringTokenizer(name," ");
					if (stringTokenizer.hasMoreTokens()) {
						nameWithoutTileType = stringTokenizer.nextToken().toLowerCase();
					}
					if (!name.matches(chitName) && !nameWithoutTileType.matches(chitName)) {
						return false;
					}
				}
			}
		}
		return reqParams.actionType == CharacterActionType.Enchant && getType().matches(reqParams.actionName);
	}

	protected String buildDescription() {
		if (getType().matches(RealmComponent.TILE)) {
			StringBuilder sb = new StringBuilder();
			sb.append("Character must enchant a tile");
			if (getSite()!=null) {
				sb.append(" with the "+getSite());
			}
			if (getSite()!=null && getChit()!=null) {
				sb.append(" and");
			}
			if (getChit()!=null) {
				sb.append(" with the "+getChit());
			}
			sb.append(".");
			return sb.toString();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("Character must enchant a ");
		if (getChit()!=null) {
			sb.append(getChit()+" ");
		}
		sb.append("chit.");
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
	
	private String getChit() {
		String chit = getString(CHIT);
		if (chit==null || chit.matches(NONE)) {
			return null;
		}
		return chit;
	}
}