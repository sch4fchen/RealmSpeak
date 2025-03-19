package com.robin.magic_realm.components;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.attribute.Harm;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public abstract class MonsterActionChitComponent extends StateChitComponent implements BattleChit {
	protected MonsterChitComponent monster;
	public MonsterActionChitComponent(GameObject obj) {
		super(obj);
		monster = (MonsterChitComponent)RealmComponent.getRealmComponent(obj);
		lightColor = monster.lightColor;
		darkColor = monster.darkColor;
	}
	public void changeWeaponState(HostPrefWrapper hostPrefs) {
	}
	public boolean hitsOnTie() {
		return false;
	}
	public boolean isMissile() {
		return false;
	}
	public String getMissileType() {
		return null;
	}
	public boolean applyHit(GameWrapper game,HostPrefWrapper hostPrefs, BattleChit attacker, int box, Harm attackerHarm,int attackOrderPos) {
		return monster.applyHit(game,hostPrefs,attacker,box,attackerHarm,attackOrderPos);
	}
	public boolean isActionChit() {
		return true;
	}
}