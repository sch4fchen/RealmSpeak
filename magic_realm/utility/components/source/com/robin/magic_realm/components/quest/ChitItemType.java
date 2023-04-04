package com.robin.magic_realm.components.quest;

import java.util.ArrayList;

public enum ChitItemType {
	None,
	Treasure,
	Weapon,
	Armor,
	Great,
	Horse,
	;
	
	static String[] ItemKeyVals = {"item"};
	static String[] TreasureKeyVals = {"item","treasure"};
	static String[] WeaponKeyVals = {"item","weapon","!character","!treasure","!magic"};
	static String[] ArmorKeyVals = {"item","armor","!character","!treasure","!magic"};
	static String[] GreatKeyVals = {"item","great"};
	static String[] HorseKeyVals = {"item","horse"};
	public String[] getKeyVals() {
		switch(this) {
			case None:		return ItemKeyVals;
			case Treasure:	return TreasureKeyVals;
			case Weapon:	return WeaponKeyVals;
			case Armor:		return ArmorKeyVals;
			case Great:		return GreatKeyVals;
			case Horse:		return HorseKeyVals;
		}
		throw new IllegalStateException("Unknown ChitItemType?"); // can this even happen?
	}
	public static ArrayList<String> listToStrings(ArrayList<ChitItemType> types) {
		if (types==null) return null;
		ArrayList<String> list = new ArrayList<>();
		for(ChitItemType cit:types) {
			list.add(cit.toString());
		}
		return list;
	}
	public static ArrayList<ChitItemType> listToTypes(ArrayList<String> strings) {
		if (strings==null) return null;
		ArrayList<ChitItemType> list = new ArrayList<>();
		for(String string:strings) {
			list.add(ChitItemType.valueOf(string));
		}
		return list;
	}
}