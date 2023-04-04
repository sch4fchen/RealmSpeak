package com.robin.magic_realm.components;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.attribute.Harm;
import com.robin.magic_realm.components.attribute.Speed;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public interface BattleChit {
	
	// Battle
	public RealmComponent getTarget();
	public GameObject getGameObject();
	public void changeWeaponState();
	public boolean isImmuneTo(RealmComponent rc);
	
    // Chit handling
	public void flip();
	public void setFacing(String val);
	public String getName();
	public boolean isDenizen();
	public boolean isCharacter();
	
	// Stats
	public Integer getLength();
	public Speed getMoveSpeed();
	public Speed getFlySpeed();
	public Speed getAttackSpeed();
	public Harm getHarm();
	public String getMagicType();
	public String getAttackSpell();
	public int getManeuverCombatBox();
	public int getAttackCombatBox();
	public boolean isMissile();
	public String getMissileType();
	public boolean hitsOnTie();
	public boolean isMonster();
	public boolean isNative();
	public boolean hasAnAttack();
	
	public boolean applyHit(GameWrapper game,HostPrefWrapper hostPrefs,BattleChit attacker,int box,Harm attackerHarm,int attackOrderPos);
}