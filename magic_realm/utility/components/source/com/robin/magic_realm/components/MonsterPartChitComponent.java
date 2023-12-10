package com.robin.magic_realm.components;

import java.awt.Graphics;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.magic_realm.components.attribute.Speed;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class MonsterPartChitComponent extends MonsterChitComponent {
	private HostPrefWrapper hostPrefs = null;
	protected MonsterPartChitComponent(GameObject obj) {
	    super(obj);
	}
	public String getName() {
	    return MONSTER_PART;
	}
	private MonsterChitComponent getWielder() {
		return (MonsterChitComponent)RealmComponent.getRealmComponent(getGameObject().getHeldBy());
	}
	protected int sizeModifier() {
		return getWielder().sizeModifier();
	}
	protected int speedModifier() {
		return getWielder().speedModifier();
	}
	public Strength getStrength() {
		Strength strength = super.getStrength();
		if (gameObject.hasThisAttribute(Constants.ALTER_WEIGHT)) strength = new Strength(getGameObject().getThisAttribute(Constants.ALTER_WEIGHT));
		if (strength.getChar()!="T" && getGameObject().getHeldBy().hasThisAttribute(Constants.STRONG_MF)) {
			strength.modify(1);
		}
		return strength;
	}

	public Strength getVulnerability() {
		Strength vul = new Strength("M"); // This is the default "size" for a monster weapon
		if (gameObject.hasThisAttribute(Constants.ALTER_WEIGHT)) vul = new Strength(getGameObject().getThisAttribute(Constants.ALTER_WEIGHT));
		vul.modify(sizeModifier());
		return vul;
	}
	public Speed getMoveSpeed() {
		return null;
	}
	public Speed getFlySpeed() {
		return null;
	}
	public Integer getLength() {
		if (hostPrefs==null) {
			hostPrefs = HostPrefWrapper.findHostPrefs(getGameObject().getGameData());
		}
		if (hostPrefs.hasPref(Constants.ADV_DRAGON_HEADS)) {
			Integer dragon_length = getFaceAttributeInteger("dragon_length");
			if (dragon_length!=null) {
				return dragon_length;
			}
		}
		return super.getLength();
	}
	public boolean isMissile() {
		if (hostPrefs==null) {
			hostPrefs = HostPrefWrapper.findHostPrefs(getGameObject().getGameData());
		}
		if (hostPrefs.hasPref(Constants.ADV_DRAGON_HEADS)) {
			if (getGameObject().hasThisAttribute("dragon_missile")) {
				return true;
			}
		}
		return super.isMissile();
	}
	
	public boolean isDamaged() {
		return getGameObject().hasThisAttribute(Constants.DAMAGED);
	}
	
	public void setDamaged(boolean value) {
		if (value) {
			getGameObject().setThisAttribute(Constants.DAMAGED);
		}
		else {
			getGameObject().removeThisAttribute(Constants.DAMAGED);
		}
	}
	
	public boolean isDestroyed() {
		return getGameObject().hasThisAttribute(Constants.DESTROYED);
	}
	
	public void setDestroyed(boolean value) {
		if (value) {
			getGameObject().setThisAttribute(Constants.DESTROYED);
		}
		else {
			getGameObject().removeThisAttribute(Constants.DESTROYED);
		}
	}
	
	public void paintComponent(Graphics g1) {
		super.paintComponent(g1);
		if (isDamaged() && !isDestroyed()) {
			TextType tt = new TextType("DAMAGED",getChitSize(),"TITLE_GRAY");
			tt.draw(g1,0,getChitSize()>>2);
		}
	}
}