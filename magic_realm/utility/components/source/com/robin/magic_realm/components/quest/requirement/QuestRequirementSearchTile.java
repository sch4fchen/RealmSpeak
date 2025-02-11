package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementSearchTile extends QuestRequirement {
	private static Logger logger = Logger.getLogger(QuestRequirementSearchTile.class.getName());
	
	public static final String TILE_TYPE = "_tile";
	public static final String CHIT = "_chit";
	public static final String HIGHEST_NUMBERED_CLREAING = "_highest_numbered_clearing";
	public static final String TABLENAME = "_table";
	public static final String RESULT1 = "_result1";
	public static final String RESULT2 = "_result2";
	public static final String RESULT3 = "_result3";
	private static final String[] ALL_RESULTS = new String[]{RESULT1,RESULT2,RESULT3};
	public static final String ANY = "any";
	public static final String NONE = "none";
	
	public QuestRequirementSearchTile(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		if (reqParams==null || reqParams.searchType==null) {
			logger.fine("No search was done.");
		}
		TileLocation loc = character.getCurrentLocation();
		if (getTileType()!=null && !getTileType().matches(ANY)) {
			if (!loc.tile.getTileType().toLowerCase().matches(getTileType().toLowerCase())) {
				logger.fine("Wrong tile type.");
				return false;
			}
		}
		SearchTableType reqTable = getRequiredSearchTable();
		if (reqTable!=SearchTableType.Any && !reqTable.toString().equals(reqParams.actionName)) {
			logger.fine("Search table name "+reqParams.actionName+" does not match "+reqTable);
			return false;
		}
		ArrayList<SearchResultType> acceptibleSearchResults = getAcceptableSearchResults();
		if (!acceptibleSearchResults.contains(reqParams.searchType)) {
			logger.fine("Search type "+reqParams.searchType+" wasn't among the acceptable search results: "+StringUtilities.collectionToString(acceptibleSearchResults,","));
			return false;
		}
		if (getChit()!=null && !getChit().matches(NONE)) {
			boolean chitFound = false;
			for (GameObject go : loc.tile.getGameObject().getHold()) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (rc.isSound() || rc.isWarning()) {
					if (go.getName().toLowerCase().matches(getChit().toLowerCase())) {
						chitFound = true;
						break;
					}
					if (rc.isSound() && go.getThisAttribute(RealmComponent.SOUND).toLowerCase().matches(getChit().toLowerCase())) {
						chitFound = true;
						break;
					}
					if (rc.isWarning() && go.getThisAttribute(RealmComponent.WARNING).toLowerCase().matches(getChit().toLowerCase())) {
						chitFound = true;
						break;
					}
				}
			}
			if (!chitFound) { 
				logger.fine("Required chit not present at the tile");
				return false;
			}
		}
		if (highestClearing()) {
			ClearingDetail currentClearing = loc.clearing;
			if (currentClearing==null) {
				logger.fine("Not in a clearing");
				return false;
			}
			int currentNum = currentClearing.getNum();
			
			int highestClearingNumberWithSite = 0;
			int highestClearingNumberWithSound = 0;
			for (ClearingDetail cl : loc.tile.getClearings()) {
				if (cl.getTreasureLocations()!=null && !cl.getTreasureLocations().isEmpty()) {
					if (cl.getNum()>highestClearingNumberWithSite) {
						highestClearingNumberWithSite = cl.getNum();
					}
				}
				if (cl.getSounds()!=null && !cl.getSounds().isEmpty()) {
					if (cl.getNum()>highestClearingNumberWithSound) {
						highestClearingNumberWithSound = cl.getNum();
					}
				}
			}
			if (highestClearingNumberWithSite!=0 && currentNum!=highestClearingNumberWithSite) {
				logger.fine("Not in the highest numbered clearing with a site");
				return false;
			}
			if (highestClearingNumberWithSite==0 && highestClearingNumberWithSound!=0 && currentNum!=highestClearingNumberWithSound) {
				logger.fine("Not in the highest numbered clearing with a sound chit");
				return false;
			}
			
		}
		return true;
	}
	protected String buildDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Must ");
		if ((getTileType()!=null && !getTileType().matches(ANY)) || (getChit()!=null && !getChit().matches(NONE))) {
			sb.append("be in a ");
			if (getTileType()!=null && !getTileType().matches(ANY)) {
				sb.append(getTileType()+" ");
			}
			sb.append("tile ");
			if (getChit()!=null && !getChit().matches(NONE)) {
				sb.append("with a "+getChit()+" chit ");
			}
			sb.append("and ");
		}
		sb.append("get a search result of ");
		sb.append(getSearchResult1());
		SearchResultType r2 = getSearchResult2();
		if (r2!=null) sb.append(" or "+r2);
		SearchResultType r3 = getSearchResult3();
		if (r3!=null) sb.append(" or "+r3);
		SearchTableType table = getRequiredSearchTable();
		if (table!=SearchTableType.Any) {
			sb.append(" from ");
			sb.append(table);
		}
		if (highestClearing()) {
			sb.append(" in the highest numbered clearing with a site (or sound chit)");
		}
		sb.append(".");
		return sb.toString();
	}
	public RequirementType getRequirementType() {
		return RequirementType.SearchTile;
	}
	public SearchTableType getRequiredSearchTable() {
		return SearchTableType.valueOf(getString(TABLENAME));
	}
	public ArrayList<SearchResultType> getAcceptableSearchResults() {
		ArrayList<SearchResultType> list = new ArrayList<SearchResultType>();
		
		for(String key:ALL_RESULTS) {
			SearchResultType type = null;
			try {
				type = SearchResultType.valueOf(getString(key));
			}
			catch(IllegalArgumentException ex) {
				type = null;
			}
			if (type!=null) {
				if (type==SearchResultType.Any) {
					for(SearchResultType s:SearchResultType.values()) {
						if (s!=SearchResultType.Any && !list.contains(s)) {
							list.add(s);
						}
					}
				}
				else if (!list.contains(type)) {
					list.add(type);
				}
			}
		}

		return list;
	}
	public SearchResultType getSearchResult1() {
		return SearchResultType.valueOf(getString(RESULT1));
	}
	public SearchResultType getSearchResult2() {
		try {
			return SearchResultType.valueOf(getString(RESULT2));
		}
		catch(IllegalArgumentException ex) {
			return null;
		}
	}
	public SearchResultType getSearchResult3() {
		try {
			return SearchResultType.valueOf(getString(RESULT3));
		}
		catch(IllegalArgumentException ex) {
			return null;
		}
	}
	public String getTileType() {
		return getString(TILE_TYPE);
	}
	public String getChit() {
		return getString(CHIT);
	}
	public boolean highestClearing() {
		return getBoolean(HIGHEST_NUMBERED_CLREAING);
	}
	
}