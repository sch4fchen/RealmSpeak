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
package com.robin.magic_realm.components;

import com.robin.magic_realm.components.attribute.Harm;
import com.robin.magic_realm.components.attribute.Speed;

public class BattleChitAttack {
	
	private BattleChit Chit;
	private Integer Length;
	private Speed Speed;
	private Harm Harm;
	private int WeaponNumber;
	
	
	public BattleChitAttack(BattleChit chit, Integer length, Speed speed, Harm harm, int weaponNumber) {
		this.Chit = chit;
		this.Length = length;
		this.Speed = speed;
		this.Harm = harm;
		this.WeaponNumber = weaponNumber;
	}
	public BattleChitAttack(BattleChit chit, Integer length, Speed speed, Harm harm) {
		this(chit, length, speed, harm, 0);
	}
	
	public BattleChit battleChit() {
		return this.Chit;
	}
	public Integer length() {
		return this.Length;
	}
	public Speed speed() {
		return this.Speed;
	}
	public Harm harm() {
		return this.Harm;
	}
	public int weaponNumber() {
		return this.WeaponNumber;
	}
}