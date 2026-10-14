package com.robin.magic_realm.components.utility;

import java.util.ArrayList;
import java.util.HashMap;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

/**
 * A class to encapsulate the logic of fetching groups of objects, based on current host settings.  It should
 * also be able to "remember" queries, and not requery when the keyVals are the same.  (Have to be careful with
 * that, in case I'm querying for something that may change)
 */
public class RealmObjectMaster {
	
	private static HashMap<Long, RealmObjectMaster> map = null;
	
	private GameData data = null;
	private HostPrefWrapper hostPrefs = null;
	private int gameObjectCountForPlayers = -1;
	private ArrayList<GameObject> playerCharacterObjects = null;
	private ArrayList<GameObject> denizenObjects = null;
	private ArrayList<GameObject> tileObjects = null;
	private ArrayList<GameObject> dwellingObjects = null;
	
	private RealmObjectMaster(GameData data) {
		this.data = data;
		this.hostPrefs = HostPrefWrapper.findHostPrefs(data);
		if (this.hostPrefs == null) {
			this.hostPrefs = HostPrefWrapper.createDefaultHostPrefs(data);
		}
	}
	
	public static synchronized void resetAll() {
		if (map!=null) {
			map.clear();
			map = null;
		}
	}
	
	public ArrayList<GameObject> findObjects(String baseQuery,ArrayList<String> keyVals) {
		String query = StringUtilities.collectionToString(keyVals,",");
		if (baseQuery!=null && baseQuery.length()>0) {
			if (query.length()>0) {
				query += ",";
			}
			query += baseQuery;
		}
		return findObjects(query);
	}
	
	public ArrayList<GameObject> findObjects(String keyVals) {
		keyVals = hostPrefs.getGameKeyVals()+","+keyVals;
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<GameObject> objects = pool.find(keyVals);
		return objects;
	}
	
	/**
	 * @return		A Collection of all characters (dead or alive), all Natives (hired or not, dead or alive),
	 * 				and all the monsters (they may get controlled!)
	 */
	// Shared across host/network threads: publish each lazy list only once fully built, never half-built.
	public synchronized ArrayList<GameObject> getPlayerCharacterObjects() {
		int dataSize = data.getGameObjects().size();
		if (gameObjectCountForPlayers!=dataSize) {
			gameObjectCountForPlayers = dataSize;
			playerCharacterObjects = null;
		}
		if (playerCharacterObjects==null) {
			ArrayList<GameObject> list = new ArrayList<>();
			list.addAll(findObjects("character"));
			list.addAll(findObjects("native,rank")); // not just leaders anymore, due to Hypnotize spell!
			list.addAll(findObjects("monster,!part"));
//			list.addAll(getCachedObjects("familiar",false));
			playerCharacterObjects = list;
		}
		return playerCharacterObjects;
	}
	
	/**
	 * @return		A Collection of all natives & monsters, dead or alive, controlled/hired or not.
	 */
	public synchronized ArrayList<GameObject> getDenizenObjects() {
		if (denizenObjects==null) {
			ArrayList<GameObject> list = new ArrayList<>();
			list.addAll(findObjects("native,rank"));
			list.addAll(findObjects("monster,!part"));
			denizenObjects = list;
		}
		return denizenObjects;
	}
	
	public synchronized void resetDenizenObjects() {
		denizenObjects=null;
	}
	
	/**
	 * @return		A Collection of all the tile objects
	 */
	public synchronized ArrayList<GameObject> getTileObjects() {
		if (tileObjects==null) {
			tileObjects = new ArrayList<>(findObjects("tile"));
		}
		return tileObjects;
	}
	public synchronized void resetTileObjects() {
		tileObjects = null;
	}
	
	/**
	 * @return		A Collection of all the dwelling objects
	 */
	public synchronized ArrayList<GameObject> getDwellingObjects() {
		if (dwellingObjects==null) {
			ArrayList<GameObject> list = new ArrayList<>();
			list.addAll(findObjects("dwelling"));
			list.addAll(findObjects("guild"));
			dwellingObjects = list;
		}
		return dwellingObjects;
	}
	
	/**
	 * @return		The relevant RealmObjectMaster associated with the provided GameData
	 */
	public static synchronized RealmObjectMaster getRealmObjectMaster(GameData data) {
		if (map==null) {
			map = new HashMap<>();
		}
		Long id = Long.valueOf(data.getDataId());
		RealmObjectMaster rom = map.get(id);
		if (rom==null) {
			rom = new RealmObjectMaster(data);
			map.put(id,rom);
		}
		return rom;
	}
}