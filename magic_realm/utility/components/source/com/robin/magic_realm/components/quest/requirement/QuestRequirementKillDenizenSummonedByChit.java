package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.SetupCardUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementKillDenizenSummonedByChit extends QuestRequirement {

	public static final String CHIT = "_chit";
	public static final String TILE_TYPE = "_tile_type";
	public static final String SAME_CLEARING = "_same_clearing";
	
	public QuestRequirementKillDenizenSummonedByChit(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame,CharacterWrapper character,QuestRequirementParams reqParams) {
		ArrayList<GameObject> kills = character.getKills(character.getCurrentDayKey());
		String chitName = getChit().toLowerCase();
		TileLocation loc = character.getCurrentLocation();
		if (getTileType()!=null && !getTileType().matches(ANY)) {
			if (!loc.tile.getTileType().toLowerCase().matches(getTileType().toLowerCase())) {
				return false;
			}
		}
		ArrayList<String> chitNames = new ArrayList<>();
		if (sameClearing()) {			
			if (getChit().matches(QuestRequirement.NONE)) {
				if (loc==null || loc.clearing==null) {
					return true;
				}
				for (RealmComponent rc : loc.clearing.getClearingComponents()) {
					if (rc.isWarning() || rc.isSound()) {
						return false;
					}
				}
			}
			else {
				if (loc==null || loc.clearing==null) {
					return false;
				}
				boolean chitInClearing = false;
				for (RealmComponent rc : loc.clearing.getClearingComponents()) {
					if (!rc.isWarning() && !rc.isSound()) {
						continue;
					}
					String name = rc.getGameObject().getName().toLowerCase().trim();
					String nameWithoutTileType = null;
					StringTokenizer stringTokenizer = new StringTokenizer(name," ");
					if (stringTokenizer.hasMoreTokens()) {
						nameWithoutTileType = stringTokenizer.nextToken().toLowerCase();
					}
					if (name.matches(chitName) || nameWithoutTileType.matches(chitName)) {
						chitInClearing = true;
						chitNames.add(name);
					}
				}
				if (!chitInClearing) {
					return false;
				}
			}
		}
		else {
			chitNames.add(chitName);
		}
		for(GameObject kill:kills) {
			GameObject holder = SetupCardUtility.getDenizenHolder(kill);
			if (holder!=null) {
				String summonList = holder.getThisAttribute("summon");
				if (getChit().matches(QuestRequirement.NONE) && (summonList==null || summonList.isEmpty())) {
					return true;
				}
				if (summonList!=null) {
					StringTokenizer tokens = new StringTokenizer(summonList,",");
					while(tokens.hasMoreTokens()) {
						String summon = tokens.nextToken().toLowerCase().trim();
						String summonWithoutTileType = null;
						StringTokenizer stringTokenizer = new StringTokenizer(summon," ");
						if (stringTokenizer.hasMoreTokens()) {
							summonWithoutTileType = stringTokenizer.nextToken().toLowerCase().trim();
						}
						for (String chit : chitNames) {
							if (summon.matches(chit) || summonWithoutTileType.matches(chit)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	protected String buildDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Must kill a monster summoned by a ");
		sb.append(getChit());
		sb.append(" chit");
		if (sameClearing()) {
			sb.append(" and be in the same clearing");
		}
		if (getTileType()!=null && !getTileType().matches(ANY)) {
			sb.append(" of type ");
			sb.append(getTileType());
		}
		sb.append(".");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.KillDenizenSummonedByChit;
	}
	
	public String getChit() {
		return getString(CHIT);
	}
	
	public boolean sameClearing() {
		return getBoolean(SAME_CLEARING);
	}
	
	public String getTileType() {
		return getString(TILE_TYPE);
	}

}