package com.robin.magic_realm.components;

public interface BattleHorse extends BattleChit {
	public boolean isDead();
	public void setWalk();
	public void setGallop();
	public boolean doublesMove();
	public boolean extraMove();
}