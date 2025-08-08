package com.robin.magic_realm.components;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.attribute.Harm;
import com.robin.magic_realm.components.attribute.Speed;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.wrapper.CombatWrapper;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class EventSpellCardComponent extends SpellCardComponent implements BattleChit {

	public EventSpellCardComponent(GameObject obj) {
		super(obj);
	}

	@Override
	public void changeWeaponState(HostPrefWrapper hostPrefs) {		
	}

	@Override
	public Integer getLength() {
		if (getGameObject().hasThisAttribute("length")) {
			int len = getGameObject().getThisInt("length");
			return Integer.valueOf(len);
		}
		return null;
	}

	@Override
	public Speed getMoveSpeed() {
		return null;
	}

	@Override
	public Speed getFlySpeed() {
		return null;
	}

	@Override
	public Speed getAttackSpeed() {
		Speed speed = new Speed();
		if (getGameObject().hasThisAttribute("attack_speed")) {
			speed = new Speed(getGameObject().getThisInt("attack_speed"));
		}
		return speed;
	}

	@Override
	public Harm getHarm() {
		Strength strength = new Strength(getGameObject().getThisAttribute("strength"));
		int sharpness = getGameObject().getThisInt("sharpness");
		return new Harm(strength,sharpness);
	}

	@Override
	public String getMagicType() {
		return getGameObject().getThisAttribute("magic_type");
	}

	@Override
	public String getAttackSpell() {
		return null;
	}

	@Override
	public int getManeuverCombatBox() {
		CombatWrapper combat = new CombatWrapper(getGameObject());
		return combat.getCombatBoxDefense();
	}

	@Override
	public int getAttackCombatBox() {
		CombatWrapper combat = new CombatWrapper(getGameObject());
		return combat.getCombatBoxAttack();
	}

	@Override
	public boolean isMissile() {
		return getGameObject().hasThisAttribute("missile");
	}

	@Override
	public String getMissileType() {
		return getGameObject().getThisAttribute("missile");
	}

	@Override
	public boolean hitsOnTie() {
		return false;
	}

	@Override
	public boolean hasAnAttack() {
		return getAttackCombatBox()>0;
	}

	@Override
	public boolean applyHit(GameWrapper game, HostPrefWrapper hostPrefs, BattleChit attacker, int box,Harm attackerHarm, int attackOrderPos) {
		return false;
	}	
}