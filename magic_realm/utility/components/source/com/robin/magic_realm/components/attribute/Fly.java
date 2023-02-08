package com.robin.magic_realm.components.attribute;

import com.robin.magic_realm.components.*;

public class Fly {
	private RealmComponent rc;
	private Strength strength;
	private Speed speed;
	public Fly() {
		// Used by Smoke Bomb in the case where a character mustFly
	}
	public Fly(RealmComponent rc) {
		this.rc = rc;
		this.strength = _getStrength(rc);
		this.speed = _getSpeed(rc);
	}
	public Fly(StrengthChit sc) {
		this.rc = null;
		this.strength = sc.getStrength();
		this.speed = sc.getSpeed();
	}
	public Strength getStrength() {
		return strength;
	}
	public Speed getSpeed() {
		return speed;
	}
	private static Strength _getStrength(RealmComponent rc) {
		if (rc instanceof CharacterActionChitComponent) {
			CharacterActionChitComponent chit = (CharacterActionChitComponent)rc;
			return chit.getStrength();
		}
		else if (rc instanceof MonsterMoveChitComponent) {
			MonsterMoveChitComponent chit = (MonsterMoveChitComponent)rc;
			return chit.getMoveStrength();
		}
		else if (rc.isMonster()) {
			MonsterChitComponent monster = (MonsterChitComponent)rc;
			return monster.getVulnerability();
		}
		else if (rc.isNative()) {
			NativeChitComponent nativeGuy = (NativeChitComponent)rc;
			return nativeGuy.getVulnerability();
		}
		else if (rc.isTreasure()) {
			return new Strength(rc.getGameObject().getThisAttribute("fly_strength"));
		}
		return new Strength(rc.getGameObject().getThisAttribute("strength"));
	}
	private static Speed _getSpeed(RealmComponent rc) {
		if (rc instanceof MonsterMoveChitComponent) {
			MonsterMoveChitComponent chit = (MonsterMoveChitComponent)rc;
			return chit.getFlySpeed();
		}
		else if (rc.isTreasure()) {
			return new Speed(rc.getGameObject().getThisAttribute("fly_speed"));
		}
		else if (rc.isMonster()) {
			MonsterChitComponent monster = (MonsterChitComponent)rc;
			return monster.getFlySpeed();
		}
		else if (rc.isNative()) {
			NativeChitComponent nativeGuy = (NativeChitComponent)rc;
			return nativeGuy.getFlySpeed();
		}
		return new Speed(rc.getGameObject().getThisAttribute("speed"));
	}
	public void useFly() {
		if (rc!=null) {
			if (rc.isFlyChit()) {
				FlyChitComponent flyChit = (FlyChitComponent)rc;
				flyChit.expireSourceSpell();
			}
			else if (rc instanceof CharacterActionChitComponent) {
				CharacterActionChitComponent chit = (CharacterActionChitComponent)rc;
				chit.makeFatigued();
			}
		}
	}
	public static boolean valid(RealmComponent rc) {
		if (rc.isFlyChit()) {
			return true;
		}
		else if (rc.isActionChit()) {
			if ((rc instanceof MonsterActionChitComponent) && rc.getGameObject().hasThisAttribute("flying")) {
				return true;
			}
			else if ((rc instanceof CharacterActionChitComponent) && ((CharacterActionChitComponent)rc).isFly()) {
				return true;
			}
		}
		else if (rc.isTreasure()) { // Flying Carpet
			return rc.getGameObject().hasThisAttribute("fly_strength");
		}
		return false;
	}
}