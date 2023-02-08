package com.robin.magic_realm.components.utility;

import java.util.ArrayList;

import com.robin.game.objects.*;

public class RealmLoader {

	public static String DATA_PATH = "data/MagicRealmData.xml";

	private GameData master; // needed to determine changes
	private GameData data;

	public RealmLoader() {
		master = new GameData();
		master.loadFromPath(DATA_PATH);

		data = new GameData();
		data.loadFromPath(DATA_PATH);
	}
	
	public RealmLoader(GameData data) {
		this.master = data.copy();
		this.data = data.copy();
	}
	
	public void cleanupData(String keyVals) {
		long maxid = master.getMaxId();
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<GameObject> found = pool.find(keyVals);
		ArrayList<GameObject> toDelete = new ArrayList<>();
		for (GameObject go : data.getGameObjects()) {
			if (go.getId()<=maxid) { // only consider objects in the master
				if (!found.contains(go)) {
					// Make sure it isn't held by...
					GameObject hb = go;
					while(hb.getHeldBy()!=null) {
						hb = hb.getHeldBy();
					}
					if (!found.contains(hb)) {
						toDelete.add(go);
					}
				}
			}
		}
		for (GameObject go:toDelete) {
			data.removeObject(go);
		}
	}

	public GameData getMaster() {
		return master;
	}

	public GameData getData() {
		return data;
	}
	
	public static void main(String[] args) {
		RealmLoader loader = new RealmLoader();
		GamePool pool = new GamePool(loader.getData().getGameObjects());
		ArrayList<String> query = new ArrayList<>();
		query.add("rw_expansion_1");
		query.add("treasure");
		String tab = "\t";
		System.out.println(
				"Name"
				+tab+"Great"
				+tab+"Large"
				+tab+"Discard"
				+tab+"Weight"
				+tab+"Fame Reward"
				+tab+"Fame"
				+tab+"Notoriety"
				+tab+"Gold"
				+tab+"Text"
				);
		for(GameObject go:pool.find(query)) {
			int twt = go.getThisInt("treasure_within_treasure");
			String great = go.hasThisAttribute("great")?"Great":" ";
			String large = twt==0?(go.getThisAttribute("treasure").equals("large")?"Large":" "):("P"+twt);
			String discard = go.getThisAttribute("discard");
			if (discard == null) discard = " ";
			String nat = go.getThisAttribute("native");
			if (nat==null) nat = " ";
			System.out.println(go.getName()
					+tab+great
					+tab+large
					+tab+discard
					+tab+go.getThisAttribute(Constants.WEIGHT)
					+tab+nat
					+tab+go.getThisInt("fame")
					+tab+go.getThisInt("notoriety")
					+tab+go.getThisInt("base_price")
					+tab+go.getThisAttribute("text"));
		}
	}
}