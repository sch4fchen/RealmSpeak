package com.robin.magic_realm.components;

import com.robin.magic_realm.components.attribute.Speed;

public interface Horsebackable {
	public Speed getMoveSpeed(boolean includeHorse);
	public int getManeuverCombatBox(boolean includeHorse);
}