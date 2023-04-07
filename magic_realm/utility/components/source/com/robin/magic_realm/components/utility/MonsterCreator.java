package com.robin.magic_realm.components.utility;

import java.util.ArrayList;

import com.robin.game.objects.*;

public class MonsterCreator {
	
	private String monsterKey;
	private ArrayList<GameObject> monstersCreated;
	
	public MonsterCreator(String monsterKey) {
		this.monsterKey = monsterKey;
		monstersCreated = new ArrayList<>();
	}
	public ArrayList<GameObject> getMonstersCreated() {
		return monstersCreated;
	}
	public GameObject createOrReuseMonster(GameData data) {
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<String> query = new ArrayList<>();
		query.add(monsterKey);
		query.add(Constants.DEAD);
		GameObject go = pool.findFirst(query);
		if (go==null) {
			go = data.createNewObject();
		}
		monstersCreated.add(go);
		go.removeThisAttribute(Constants.DEAD); // shouldn't be DEAD anymore
		SetupCardUtility.updateGeneratedMonsterInt(go);
		return go;
	}
	public static void setupSide(GameObject go,String side,String strength,int sharpness,int attackSpeed,int attackLength,int moveSpeed,String color) {
		go.removeAttribute(side,"strength");
		go.removeAttribute(side,"attack_speed");
		go.removeAttribute(side,"sharpness");
		go.removeAttribute(side,"length");
		if (strength!=null) {
			go.setAttribute(side,"strength",strength);
			go.setAttribute(side,"attack_speed",attackSpeed);
			go.setAttribute(side,"length",attackLength);
			if (sharpness>0) {
				go.setAttribute(side,"sharpness",sharpness);
			}
		}
		if (moveSpeed!=-1) {
			go.setAttribute(side,"move_speed",moveSpeed);
		}
		go.setAttribute(side,"chit_color",color);
	}
	public void setupGameObject(GameObject go,String name,String iconType,String vulnerability,boolean armored) {
		setupGameObject(go,name,iconType,vulnerability,armored,false);
	}
	public void setupGameObject(GameObject go,String name,String iconType,String vulnerability,boolean armored,boolean flies) {
		setupGameObject(go,name,iconType,vulnerability,armored,flies,false);
	}
	public void setupGameObject(GameObject go,String name,String iconType,String vulnerability,boolean armored,boolean flies,boolean small) {
		setupGameObject(go,name,iconType,vulnerability,armored,flies,small,"monsters2");
	}
	public void setupGameObject(GameObject go,String name,String iconType,String vulnerability,boolean armored,boolean flies,boolean small,String iconFolder) {
		go.setName(name);
		go.setThisAttribute("monster");
		go.setThisAttribute(monsterKey);
		go.setThisAttribute("vulnerability",vulnerability);
		go.setThisAttribute("icon_type",iconType);
		go.setThisAttribute("icon_folder",iconFolder);
		go.removeThisAttribute(Constants.ARMORED);
		go.removeThisAttribute("flying");
		if (armored) {
			go.setThisAttribute(Constants.ARMORED);
		}
		if (flies) {
			go.setThisAttribute("flying");
		}
		if (small) {
			go.setThisAttribute(Constants.SMALL);
		}
	}
}