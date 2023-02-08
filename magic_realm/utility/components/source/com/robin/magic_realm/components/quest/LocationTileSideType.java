package com.robin.magic_realm.components.quest;

import com.robin.magic_realm.components.TileComponent;

public enum LocationTileSideType {
	Any,
	Unenchanted,
	Enchanted,
	;
	
	public boolean matches(TileComponent tile) {
		switch(this) {
			case Any:			return true;
			case Unenchanted:	return !tile.isEnchanted();
			case Enchanted:		return tile.isEnchanted();
		}
		return false;
	}
}