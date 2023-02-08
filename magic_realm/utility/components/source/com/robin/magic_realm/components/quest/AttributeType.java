package com.robin.magic_realm.components.quest;

public enum AttributeType {
	GreatTreasures,
	RecordedSpells,
	Fame,
	Notoriety,
	Gold,
	;
	
	public String getDescription(boolean plural) {
		switch(this) {
			case Fame:				return plural?"Fame points":"Fame point";
			case Notoriety:			return plural?"Notoriety points":"Notoriety point";
			case Gold:				return "Gold";
			case RecordedSpells:	return plural?"new spells":"new spell";
			case GreatTreasures:	return plural?"Great Treasures":"Great Treasure";
		}
		return "NO DESCRIPTION";
	}
}