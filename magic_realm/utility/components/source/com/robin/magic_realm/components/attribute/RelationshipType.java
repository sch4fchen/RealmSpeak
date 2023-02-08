package com.robin.magic_realm.components.attribute;

public class RelationshipType {
	public static final int ENEMY = -2;
	public static final int UNFRIENDLY = -1;
	public static final int NEUTRAL = 0;
	public static final int FRIENDLY = 1;
	public static final int ALLY = 2;
	
	public static final String[] RelationshipNames = {
		"ENEMY",
		"UNFRIENDLY",
		"NEUTRAL",
		"FRIENDLY",
		"ALLY",
	};
	
	public static String getNameFor(int rel) {
		if (rel<ENEMY) rel = ENEMY;
		if (rel>ALLY) rel = ALLY;
		return RelationshipNames[rel+2];
	}
	public static int getIntFor(String name) {
		for (int i=0;i<RelationshipNames.length;i++) {
			if (RelationshipNames[i].equalsIgnoreCase(name)) {
				return i-2;
			}
		}
		throw new IllegalArgumentException();
	}
}