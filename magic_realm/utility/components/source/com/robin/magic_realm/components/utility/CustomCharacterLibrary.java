package com.robin.magic_realm.components.utility;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class CustomCharacterLibrary {
	private static CustomCharacterLibrary singleton = null;
	public static CustomCharacterLibrary getSingleton() {
		if (singleton==null) {
			singleton = new CustomCharacterLibrary();
		}
		return singleton;
	}
	
	private Hashtable<String,GameObject> customCharacterHash;
	private Hashtable<String,ImageIcon> customCharacterImageHash;
	private CustomCharacterLibrary() {
		customCharacterHash = new Hashtable<>();
		customCharacterImageHash = new Hashtable<>();
	}
	public void addCustomCharacterTemplate(GameObject character,ImageIcon detailImage) {
		customCharacterHash.put(character.getName(),character);
		customCharacterImageHash.put(character.getName(),detailImage);
	}
	public ArrayList<String> getCharacterTemplateNameList() {
		return new ArrayList<>(customCharacterHash.keySet());
	}
	public GameObject getCharacterTemplate(String name) {
		return customCharacterHash.get(name);
	}
	private static String getNameFor(GameObject go) {
		return go.getAttribute("level_4","name");
	}
	public String getCharacterUniqueKey(GameObject go) {
		return getCharacterUniqueKey(getNameFor(go));
	}
	public String getCharacterUniqueKey(String name) {
		GameObject go = getCharacterTemplate(name);
		if (go!=null) {
			CharacterWrapper character = new CharacterWrapper(go);
			return character.getCharacterLevelName(4)+":"+go.getGameData().getCheckSum();
		}
		return null;
	}
	public ArrayList<GameObject> getCharacterTemplateList() {
		return new ArrayList<>(customCharacterHash.values());
	}
	public ImageIcon getCharacterImage(String name) {
		return customCharacterImageHash.get(name);
	}
	public ArrayList<GameObject> getCharacterWeapons(GameObject go) {
		GameObject character = getCharacterTemplate(getNameFor(go));
		GamePool pool = new GamePool(character.getGameData().getGameObjects());
		return pool.find("weapon,!character,!magic");
	}
	public ArrayList<GameObject> getCharacterArmor(GameObject go) {
		GameObject character = getCharacterTemplate(getNameFor(go));
		GamePool pool = new GamePool(character.getGameData().getGameObjects());
		return pool.find("armor,!character,!magic");
	}
	public ArrayList<GameObject> getCharacterCompanions(GameObject go) {
		GameObject character = getCharacterTemplate(getNameFor(go));
		GamePool pool = new GamePool(character.getGameData().getGameObjects());
		return pool.find("companion");
	}
	private ArrayList<String> getAllUniqueKeys() {
		ArrayList<String> list =  new ArrayList<>();
		for (String name:getCharacterTemplateNameList()) {
			list.add(getCharacterUniqueKey(name));
		}
		return list;
	}
	public ArrayList<String> getMissingCharacterNames(ArrayList<String> expectedList) {
		ArrayList<String> allUniqueKeys = getAllUniqueKeys();
		ArrayList<String> list =  new ArrayList<>();
		for (String val:expectedList) {
			if (!allUniqueKeys.contains(val)) {
				int col = val.lastIndexOf(':');
				list.add(val.substring(0,col));
			}
		}
		return list;
	}
}