package com.robin.magic_realm.components.attribute;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;

public class Inventory {
	
	private boolean active;
	private GameObject go;
	private RealmComponent rc;
	
	public Inventory(GameObject go) {
		this.go = go;
		this.rc = RealmComponent.getRealmComponent(go);
		active = go.hasThisAttribute(Constants.ACTIVATED);
	}
	public GameObject getGameObject() {
		return go;
	}
	public RealmComponent getRealmComponent() {
		return rc;
	}
	public boolean isNew() {
		return go.hasThisAttribute(Constants.TREASURE_NEW);
	}
	public boolean canActivate() {
		return !active && !rc.isMinorCharacter() && !rc.isVisitor() && !rc.isGoldSpecial();
	}
	public boolean canDeactivate() {
		return active
				&& !go.hasThisAttribute("color_source")
				&& !go.hasThisAttribute("potion")
				&& !rc.isNativeHorse()
				&& !rc.isGoldSpecial()
				&& !rc.isMinorCharacter()
				&& !rc.isVisitor()
				&& !rc.isBoon();
	}
	public boolean canDrop() {
		return !rc.isBoon()
				&& !rc.isPhaseChit()
				&& !rc.isNativeHorse()
				&& !rc.isMinorCharacter()
				&& !rc.isVisitor()
				&& (!rc.isEnchanted()) // can't drop an enchanted artifact or book
				&& !(active && go.hasThisAttribute("potion")) // can't drop an active potion
				&& !go.hasThisAttribute(Constants.CANNOT_BE_ABANDONED);
	}
}