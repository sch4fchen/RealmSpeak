package com.robin.magic_realm.components.utility;

import java.util.ArrayList;
import java.util.Collection;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class RealmDirectInfoHolder {
	
	// Direct Info Commands for TRADE interface
	public static final String TRADE_INIT = "trInit";
	public static final String TRADE_BUSY = "trBusy";
	public static final String TRADE_ADD_OBJECTS = "trAdd";
	public static final String TRADE_REMOVE_OBJECTS = "trRem";
	public static final String TRADE_ADD_DISC = "trAddD";
	public static final String TRADE_REMOVE_DISC = "trRemD";
	public static final String TRADE_ACCEPT = "trAccept";
	public static final String TRADE_UNACCEPT = "trUnAcc";
	public static final String TRADE_CANCEL = "trCanc";
	public static final String TRADE_GOLD = "trGold";
	public static final String TRADE_DONE = "trDone";
	
	// Direct Info Commands for showing popup messages
	public static final String POPUP_MESSAGE = "puMessage";
	
	// Direct Info Commands for querying players
	public static final String QUERY_YN = "quYesNo";
	public static final String QUERY_YN_NUM = "quYesNoNum"; // Placeholder for when I implement "Ask Demon"
	public static final String QUERY_RESPONSE = "quResponse";
	public static final String QUERY_CANCEL = "quCancel";
	
	// Static responses to query
	public static final String QUERY_RESPONSE_YES = "qurYES";
	public static final String QUERY_RESPONSE_NO = "qurNO";
	
	// Direct Info Commands for energizing spells
	public static final String SPELL_AFFECT_TARGETS = "spAffTarg";
	public static final String SPELL_AFFECT_TARGETS_EXPIRE_IMMEDIATE = "spAffTargEi";
	public static final String SPELL_WISH_FORCE_TRANSPORT = "spForTrans";
	
	// Direct Info Command for passing detail log to clients
	public static final String HOST_DETAIL_LOG = "hsDetailLog";
	
	public static final String HOST_NEED_EMAIL = "hsNeedEmail";
	public static final String CLIENT_RESPOND_EMAIL = "clRespEmail";
	
	public static final String RANDOM_NUMBER_GENERATOR = "hsRndNumGen";
	
	private static final int CHAR_ACTIVE = 0;
	private static final int CHAR_INCLUDE = 1;
	private static final int COMMAND = 2;
	private static final int GAME_OBJECT_ID_LIST_START = 3;
	
	private GameData data;
	private ArrayList<String> list;
	public RealmDirectInfoHolder(GameData data) {
		this.data = data;
		list = new ArrayList<>();
		// Three required placeholders
		list.add("");
		list.add("");
		list.add("");
	}
	public String toString() {
		return "RealmDirectInfoHolder: "+list.toString();
	}
	public RealmDirectInfoHolder(GameData data,ArrayList<String> in) {
		this(data);
		if (in.size()<GAME_OBJECT_ID_LIST_START) {
			throw new IllegalArgumentException("Invalid list");
		}
		list = new ArrayList<>(in);
	}
	public RealmDirectInfoHolder(GameData data,String playerName) {
		this(data);
		list.set(CHAR_ACTIVE,playerName);
	}
	public RealmDirectInfoHolder(GameData data,CharacterWrapper active,CharacterWrapper include) {
		this(data);
		list.set(CHAR_ACTIVE,active.getGameObject().getStringId());
		list.set(CHAR_INCLUDE,include.getGameObject().getStringId());
	}
	public String getPlayerName() {
		return list.get(CHAR_ACTIVE);
	}
	public void setCommand(String in) {
		list.set(COMMAND,in);
	}
	public void setGold(int in) {
		clearGameObjectList();
		list.add(String.valueOf(in));
	}
	public int getGold() {
		String val = list.get(GAME_OBJECT_ID_LIST_START);
		return Integer.valueOf(val).intValue();
	}
	public void setString(String in) {
		clearGameObjectList();
		list.add(in);
	}
	public String getString() {
		return list.get(GAME_OBJECT_ID_LIST_START);
	}
	public void setGameObjects(Collection<GameObject> in) {
		clearGameObjectList();
		for (GameObject i : in) {
			addGameObject(i);
		}
	}
	public void setStrings(Collection<String> in) {
		clearGameObjectList();
		list.addAll(in);
	}
	public void clearGameObjectList() {
		list = new ArrayList<>(list.subList(0,3));
	}
	public void addGameObject(GameObject go) {
		list.add(go.getStringId());
	}
	public String getCommand() {
		return list.get(COMMAND);
	}
	public CharacterWrapper getActiveCharacter() {
		String id = list.get(CHAR_ACTIVE);
		GameObject go = data.getGameObject(Long.valueOf(id));
		return new CharacterWrapper(go);
	}
	public CharacterWrapper getIncludeCharacter() {
		String id = list.get(CHAR_INCLUDE);
		GameObject go = data.getGameObject(Long.valueOf(id));
		return new CharacterWrapper(go);
	}
	public ArrayList<GameObject> getGameObjects() {
		ArrayList<GameObject> ret = new ArrayList<>();
		for (int i=GAME_OBJECT_ID_LIST_START;i<list.size();i++) {
			String id = list.get(i);
			ret.add(data.getGameObject(Long.valueOf(id)));
		}
		return ret;
	}
	public ArrayList<String> getStrings() {
		ArrayList<String> ret = new ArrayList<>();
		for (int i=GAME_OBJECT_ID_LIST_START;i<list.size();i++) {
			String string = list.get(i);
			ret.add(string);
		}
		return ret;
	}
	public ArrayList<String> getInfo() {
		return list;
	}
}