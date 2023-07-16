package com.robin.magic_realm.components;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.utility.Constants;

public class RedSpecialChitComponent extends StateChitComponent {
	protected RedSpecialChitComponent(GameObject obj) {
		super(obj);
		darkColor = MagicRealmColor.RED;
	}
	public String getName() {
	    return RED_SPECIAL;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		TextType tt;
		
		if (getGameObject().hasThisAttribute(Constants.ALWAYS_VISIBLE) || isFaceUp()) {
			int y=5;
			String name = gameObject.getName();
			tt = new TextType(name,getChitSize()-4,"BOLD");
			tt.draw(g,2,y,Alignment.Center);
			
			y += tt.getHeight(g);
			
			String clearing = getThisAttribute("clearing");
			tt = new TextType(clearing,getChitSize()-4,"BOLD");
			tt.draw(g,2,y,Alignment.Center);
		}
	}
	/**
	 * Override method so that red specials can be added to tile
	 */
	protected void explode() {
		addPileToTile();
	}
//	/**
//	 * Override method so that red specials can be added to tile
//	 */
//	public void setFacing(String val) {
//		super.setFacing(val);
//		if (isFaceUp()) {
//			addPileToTile();
//		}
//	}
	/**
	 * Remaps the hold of the red special chit to the tile
	 */
	public void addPileToTile() {
		if (gameObject.getHoldCount()>0) {
			GameObject tile = gameObject.getHeldBy();
			Collection<GameObject> hold = new ArrayList<>(gameObject.getHold()); // this construction is necessary to prevent concurrent modification errors
			for (GameObject chit : hold) {
				StateChitComponent state = (StateChitComponent)RealmComponent.getRealmComponent(chit);
				state.setFaceUp();
				tile.add(chit);
			}
		}
	}
}