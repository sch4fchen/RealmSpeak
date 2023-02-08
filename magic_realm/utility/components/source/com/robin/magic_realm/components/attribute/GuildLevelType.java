package com.robin.magic_realm.components.attribute;

public class GuildLevelType {
	public enum GuildLevel {
		Apprentice,
		Journeyman,
		Master
	}
	
	public static int getIntFor(GuildLevel level) {
		switch (level) {
		case Apprentice:
			return 1;
		case Journeyman:
			return 2;
		case Master:
			return 3;
		default:
			return 0;
		}
	}
}