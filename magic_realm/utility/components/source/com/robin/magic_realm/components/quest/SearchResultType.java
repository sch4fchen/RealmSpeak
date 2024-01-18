package com.robin.magic_realm.components.quest;

import java.util.ArrayList;

public enum SearchResultType {
	Any,
	Awaken,
	Clues,
	Counters,
	Curse,
	Mesmerize,
	DiscoverChits,
	HiddenEnemies,
	LearnAndAwaken,
	LearnSpell,
	Passages,
	Paths,
	PerceiveSpell,
	TakeTopTreasure,
	Take2ndTreasure,
	Take3rdTreasure,
	Take4thTreasure,
	Take5thTreasure,
	Take6thTreasure,
	TreasureCards,
	
	CaveTeleport,
	MountainTeleport,
	WoodsTeleport,
	RuinsTeleport,
	PeerEnchantAnyClearing,
	PowerOfThePit,
	
	Gold,
	Notoriety,
	
	Wish,
	Heal,
	SummonDemon,
	
	Rest,
	RemoveCurse,
	Wound,
	Unhide,
	;
	public boolean canGetTreasure() {
		switch(this) {
			case TakeTopTreasure:
			case Take2ndTreasure:
			case Take3rdTreasure:
			case Take4thTreasure:
			case Take5thTreasure:
			case Take6thTreasure:
			case TreasureCards:
			case Counters:
				return true;
			default: return false;
		}
	}
	public boolean canGetSpell() {
		switch(this) {
			case LearnAndAwaken:
			case Awaken:
			case PerceiveSpell:
			case LearnSpell:
				return true;
			default: return false;
		}
	}
	public static String[] optionalValues() {
		ArrayList<String> list = new ArrayList<>();
		list.add("");
		for(SearchResultType rt:values()) {
			list.add(rt.toString());
		}
		return list.toArray(new String[0]);
	}
	public static SearchResultType getLootSearchResultType(int treasureNumber) {
		switch(treasureNumber) {
			case 1:		return SearchResultType.TakeTopTreasure;
			case 2:		return SearchResultType.Take2ndTreasure;
			case 3:		return SearchResultType.Take3rdTreasure;
			case 4:		return SearchResultType.Take4thTreasure;
			case 5:		return SearchResultType.Take5thTreasure;
			case 6:		return SearchResultType.Take6thTreasure;
		}
		return null;
	}
	public static SearchResultType[] getSearchResultTypes(SearchTableType table) {
		ArrayList<SearchResultType> list = new ArrayList<>();
		list.add(SearchResultType.Any);
		switch(table) {
			case Any:
				return SearchResultType.values();
			case Locate:
				list.add(SearchResultType.Passages);
				list.add(SearchResultType.Clues);
				list.add(SearchResultType.DiscoverChits);
				break;
			case Loot:
				list.add(SearchResultType.TakeTopTreasure);
				list.add(SearchResultType.Take2ndTreasure);
				list.add(SearchResultType.Take3rdTreasure);
				list.add(SearchResultType.Take4thTreasure);
				list.add(SearchResultType.Take5thTreasure);
				list.add(SearchResultType.Take6thTreasure);
				break;
			case MagicSight:
				list.add(SearchResultType.Counters);
				list.add(SearchResultType.TreasureCards);
				list.add(SearchResultType.PerceiveSpell);
				list.add(SearchResultType.DiscoverChits);
				break;
			case Peer:
				list.add(SearchResultType.Clues);
				list.add(SearchResultType.Paths);
				list.add(SearchResultType.HiddenEnemies);
				break;
			case ReadRunes:
				list.add(SearchResultType.LearnAndAwaken);
				list.add(SearchResultType.Awaken);
				list.add(SearchResultType.Curse);
				break;
			case ToadstoolCircle:
				list.add(SearchResultType.Counters);
				list.add(SearchResultType.TreasureCards);
				list.add(SearchResultType.CaveTeleport);
				list.add(SearchResultType.PeerEnchantAnyClearing);
				list.add(SearchResultType.PowerOfThePit);
				break;
			case CryptOfTheKnight:
				list.add(SearchResultType.Counters);
				list.add(SearchResultType.TreasureCards);
				list.add(SearchResultType.Gold);
				list.add(SearchResultType.Curse);
				break;
			case EnchantedMeadow:
				list.add(SearchResultType.Counters);
				list.add(SearchResultType.Wish);
				list.add(SearchResultType.Heal);
				list.add(SearchResultType.Curse);
				break;
			case FountainOfHealth:
				list.add(SearchResultType.Heal);
				list.add(SearchResultType.Rest);
				list.add(SearchResultType.RemoveCurse);
				list.add(SearchResultType.Wound);
				break;
			case ArcheologicalDig:
				list.add(SearchResultType.DiscoverChits);
				list.add(SearchResultType.Clues);
				list.add(SearchResultType.Gold);
				list.add(SearchResultType.Wound);
				break;
			case CircleOfStones:
				list.add(SearchResultType.Counters);
				list.add(SearchResultType.TreasureCards);
				list.add(SearchResultType.MountainTeleport);
				list.add(SearchResultType.RuinsTeleport);
				list.add(SearchResultType.Curse);
				break;
			case EtherealAbbey:
				list.add(SearchResultType.Counters);
				list.add(SearchResultType.TreasureCards);
				list.add(SearchResultType.LearnSpell);
				list.add(SearchResultType.Mesmerize);
				break;
			case FairyGrove:
				list.add(SearchResultType.Counters);
				list.add(SearchResultType.TreasureCards);
				list.add(SearchResultType.LearnSpell);
				list.add(SearchResultType.WoodsTeleport);
				list.add(SearchResultType.Mesmerize);
				list.add(SearchResultType.Gold);
				break;
			case HauntedGrave:
				list.add(SearchResultType.Counters);
				list.add(SearchResultType.TreasureCards);
				list.add(SearchResultType.Notoriety);
				list.add(SearchResultType.LearnSpell);
				list.add(SearchResultType.SummonDemon);
				list.add(SearchResultType.Unhide);
				break;
		}
		
		return list.toArray(new SearchResultType[list.size()]);
	}
}