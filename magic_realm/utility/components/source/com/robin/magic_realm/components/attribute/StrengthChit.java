package com.robin.magic_realm.components.attribute;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;

public class StrengthChit {
	private GameObject go;
	private Strength strength;
	private Speed speed;
	public StrengthChit(GameObject go,Strength st,Speed sp) {
		this.go = go;
		this.strength = st;
		this.speed = sp;
	}
	public String toString() {
		return "StrengthChit from "+go.toString()+": "+strength.toString()+speed.getSpeedString();
	}
	public RealmComponent getRealmComponent() {
		if (go!=null) {
			return RealmComponent.getRealmComponent(go);
		}
		return null;
	}
	public GameObject getGameObject() {
		return go;
	}
	public Speed getSpeed() {
		return speed;
	}
	public Strength getStrength() {
		return strength;
	}
}