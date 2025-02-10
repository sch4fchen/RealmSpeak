package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.general.util.StringUtilities;
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
		SearchTableType reqTable = getRequiredSearchTable();
		if (reqParams==null) {
			logger.fine("No search was done.");
		}
		//TILE_TYPE
		if (reqParams!=null && reqTable!=SearchTableType.Any && !reqTable.toString().equals(reqParams.actionName)) {
			logger.fine("Search table name "+reqParams.actionName+" does not match "+reqTable);
			return false;
		}
		ArrayList<SearchResultType> acceptibleSearchResults = getAcceptableSearchResults();
		if (reqParams!=null && reqParams.searchType!=null && !acceptibleSearchResults.contains(reqParams.searchType)) {
			logger.fine("Search type "+reqParams.searchType+" wasn't among the acceptable search results: "+StringUtilities.collectionToString(acceptibleSearchResults,","));
		}
		//CHIT
		//HIGHEST_NUMBERED_CLREAING
		return false;
	}
	protected String buildDescription() {
		//TILE_TYPE
		//CHIT
		//HIGHEST_NUMBERED_CLREAING
		StringBuilder sb = new StringBuilder();
		sb.append("Must get a search result of ");
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
}