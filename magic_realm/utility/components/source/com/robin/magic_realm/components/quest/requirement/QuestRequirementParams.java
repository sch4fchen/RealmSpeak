package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.List;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.components.attribute.ColorMagic;
import com.robin.magic_realm.components.quest.*;

public class QuestRequirementParams {
	// General
	public GamePhaseType timeOfCall = GamePhaseType.Unspecified; // default
	public String actionName;
	public CharacterActionType actionType = CharacterActionType.Unknown; // default
	public String dayKey;
	public ArrayList<GameObject> objectList =  new ArrayList<>();
	public ColorMagic burnedColor;

	// Character-to-character trade: objectList holds what was given
	public ArrayList<GameObject> receivedList = new ArrayList<>();
	public int goldGiven;
	public int goldReceived;
	private static final String CHARACTER_TRADE_MARKER = "CTRADE";

	// Search stuff
	public int dieResult;
	public SearchResultType searchType = SearchResultType.Any;
	public GameObject targetOfSearch;
	public boolean searchHadAnEffect;
	
	public QuestRequirementParams copy(GameData gameData) {
		return valueOf(asString(),gameData);
	}
	
	public void clearTables() {
		actionName = null;
		actionType = CharacterActionType.Unknown;
		dieResult = -1;
		searchType = null;
		objectList.clear();
		searchHadAnEffect = false;
		targetOfSearch = null;
		receivedList.clear();
		goldGiven = 0;
		goldReceived = 0;
	}
	public String asString() {
		ArrayList<String> list = new ArrayList<>();
		list.add(timeOfCall.toString());
		list.add(actionName);
		list.add(dayKey);
		list.add(actionType.toString());
		list.add(String.valueOf(dieResult));
		list.add(searchType == null ? SearchResultType.Any.toString() : searchType.toString());
		list.add(searchHadAnEffect?"T":"F");
		list.add(targetOfSearch==null?"null":targetOfSearch.getStringId());
		if (objectList!=null) {
			for(GameObject res:objectList) {
				if (res!=null) list.add(res.getStringId());
			}
		}
		if (actionType==CharacterActionType.TradingWithCharacter) {
			list.add(CHARACTER_TRADE_MARKER);
			list.add(String.valueOf(goldGiven));
			list.add(String.valueOf(goldReceived));
			for(GameObject res:receivedList) {
				if (res!=null) list.add(res.getStringId());
			}
		}
		return StringUtilities.collectionToString(list,"@");
	}
	public static QuestRequirementParams valueOf(String s,GameData gameData) {
		ArrayList<String> list = StringUtilities.stringToCollection(s,"@",true);
		QuestRequirementParams qp = new QuestRequirementParams();
		if (list.size()>=8) {
			qp.timeOfCall = GamePhaseType.valueOf(list.get(0));
			qp.actionName = list.get(1)==null?null:list.get(1);
			qp.dayKey = list.get(2)==null?null:list.get(2);
			qp.actionType = CharacterActionType.valueOf(list.get(3));
			qp.dieResult = Integer.parseInt(list.get(4));
			qp.searchType = SearchResultType.valueOf(list.get(5));
			qp.searchHadAnEffect = "T".equals(list.get(6));
			qp.targetOfSearch = readGameObject(list.get(7),gameData);
		}
		if (list.size()>8) {
			List<String> rest = list.subList(8,list.size());
			int marker = rest.indexOf(CHARACTER_TRADE_MARKER);
			List<String> objects = marker>=0?rest.subList(0,marker):rest;
			for(String val:objects) {
				qp.objectList.add(readGameObject(val,gameData));
			}
			if (marker>=0 && rest.size()>=marker+3) {
				qp.goldGiven = Integer.parseInt(rest.get(marker+1));
				qp.goldReceived = Integer.parseInt(rest.get(marker+2));
				for(String val:rest.subList(marker+3,rest.size())) {
					qp.receivedList.add(readGameObject(val,gameData));
				}
			}
		}
		return qp;
	}
	private static GameObject readGameObject(String val,GameData gameData) {
		if (val==null) return null;
		return gameData.getGameObject(Long.valueOf(val));
	}
}