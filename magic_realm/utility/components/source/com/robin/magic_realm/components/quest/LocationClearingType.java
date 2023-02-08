package com.robin.magic_realm.components.quest;

import com.robin.magic_realm.components.ClearingDetail;

public enum LocationClearingType {
	Any,
	Plain,
	Cave,
	Mountain,
	Woods,
	Water,
	;
	
	public boolean matches(ClearingDetail clearing) {
		switch(this)
		{
			case Any:		return true;
			case Plain:		return clearing.isNormal();
			case Cave:		return clearing.isCave();
			case Mountain:	return clearing.isMountain();
			case Woods:		return clearing.isWoods();
			case Water:		return clearing.isWater();
		}
		return false;
	}
}