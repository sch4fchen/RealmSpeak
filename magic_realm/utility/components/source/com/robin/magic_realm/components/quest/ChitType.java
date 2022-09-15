/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
package com.robin.magic_realm.components.quest;

import com.robin.magic_realm.components.RealmComponent;

public enum ChitType {
	Any,
	TreasureLocation,
	Gate,
	Guild,
	Dwelling,
	Denizen,
	Native,
	ControlledNative,
	NativeLeader,
	HiredNativeLeader,
	Monster,
	ControlledMonster,
	Horse,
	NativeHorse,
	Hireling,
	Companion,
	Familiar,
	MinorCharacter,
	HiredOrControlled,
	Character,
	Phantasm,
	Mist,
	TransformedAnimal,
	Traveler,
	Visitor,
	VisitorMissionCampaign,
	Collectable,
	Item,
	Weapon,
	Armor,
	ArmorCard,
	Treasure,
	GoldSpecialChit,
	Spell,
	ActionChit,
	MagicChit,
	RedSpecialChit,
	Cloned,
	Summoned,
	;
	
	public boolean matches(RealmComponent rc) {
		switch(this) {
			case Any:				return true;
			case TreasureLocation:	return rc.isTreasureLocation();
			case Gate:				return rc.isGate();
			case Guild:				return rc.isGuild();
			case Dwelling:			return rc.isDwelling();
			case Denizen:			return rc.isDenizen();
			case Native:			return rc.isNative();
			case ControlledNative:	return rc.isControlledNative();
			case NativeLeader:		return rc.isNativeLeader();
			case HiredNativeLeader: return rc.isHiredLeader();
			case Monster:			return rc.isMonster();
			case ControlledMonster:	return rc.isControlledMonster();
			case Horse:				return rc.isHorse();
			case NativeHorse:		return rc.isNativeHorse();
			case Hireling:			return rc.isHireling();
			case Companion:			return rc.isCompanion();
			case Familiar:			return rc.isFamiliar();
			case MinorCharacter:	return rc.isMinorCharacter();
			case HiredOrControlled:	return rc.isHiredOrControlled();
			case Character:			return rc.isCharacter();
			case Phantasm:			return rc.isPhantasm();
			case Mist:				return rc.isMistLike();
			case TransformedAnimal:	return rc.isTransformAnimal();
			case Traveler:			return rc.isTraveler();
			case Visitor:			return rc.isVisitor();
			case VisitorMissionCampaign:	return rc.isRedSpecial();
			case Collectable:		return rc.isCollectibleThing();
			case Item:				return rc.isItem();
			case Weapon:			return rc.isWeapon();
			case Armor:				return rc.isArmor();
			case ArmorCard:			return rc.isArmorCard();
			case Treasure:			return rc.isTreasure();
			case GoldSpecialChit:	return rc.isGoldSpecial();
			case Spell:				return rc.isSpell();
			case ActionChit:		return rc.isActionChit();
			case MagicChit:			return rc.isMagicChit();
			case RedSpecialChit:	return rc.isRedSpecial();
			case Cloned:			return rc.isCloned();
			case Summoned:			return rc.isSummoned();
		}
		return false;
	}
}