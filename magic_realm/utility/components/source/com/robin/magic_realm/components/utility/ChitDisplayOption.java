package com.robin.magic_realm.components.utility;

import com.robin.magic_realm.components.RealmComponent;

public class ChitDisplayOption {
	public boolean characters;
	public boolean monsters;
	public boolean natives;
	public boolean dwellings;
	public boolean tileChits;
	public boolean droppedInventory;
	public boolean siteCards;
	public boolean tileBewitchingSpells;
	
	public ChitDisplayOption() {
		characters = true;
		monsters = true;
		natives = true;
		dwellings = true;
		tileChits = true;
		droppedInventory = true;
		siteCards = true;
		tileBewitchingSpells = true;
	}
	public boolean okayToDraw(RealmComponent rc) {
		return (characters || (!rc.isCharacter() && !rc.isFamiliar() && !rc.isPhantasm()))
					&& (monsters || !rc.isMonster())
					&& (natives || !rc.isNative())
					&& (dwellings || !rc.isDwelling())
					&& (tileChits || !rc.isStateChit())
					&& (siteCards || !(rc.isTreasure() && rc.isTreasureLocation()))
					&& (droppedInventory || !rc.isCollectibleThing());
	}
}