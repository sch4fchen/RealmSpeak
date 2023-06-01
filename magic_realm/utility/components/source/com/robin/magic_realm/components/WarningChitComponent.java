package com.robin.magic_realm.components;

import java.awt.Graphics;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.utility.ClearingUtility;
import com.robin.magic_realm.components.utility.Constants;

public class WarningChitComponent extends StateChitComponent {
	protected WarningChitComponent(GameObject obj) {
		super(obj);
		darkColor = MagicRealmColor.YELLOW;
	}
	public String getName() {
	    return WARNING;
	}
	/**
	 * Override method so that campfires can be added to tile
	 */
	protected void explode() {
		GameObject tile = gameObject.getHeldBy();
		if (tile!=null) {
			ClearingUtility.dumpHoldToTile(tile,gameObject,-1); // -1, so that best clearing is chosen
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		TextType tt;
		
		if (getGameObject().hasThisAttribute(Constants.ALWAYS_VISIBLE) || isFaceUp()) {
			int y = 12;
			String iconName = gameObject.getThisAttribute(Constants.ICON_TYPE+"_sr");
			String iconFolder = gameObject.getThisAttribute(Constants.ICON_FOLDER+"_sr");
			if (iconName!=null && iconFolder!=null) {
				drawIcon(g,iconFolder,iconName,0.4,0,-20,null);
				y = 20;
			}
			
			String warning = getThisAttribute(RealmComponent.WARNING);
			tt = new TextType(warning,getChitSize(),"BOLD");
			tt.draw(g,0,y,Alignment.Center);
			
			String type = getThisAttribute(RealmComponent.TILE_TYPE);
			tt = new TextType(type,getChitSize(),"BOLD");
			tt.draw(g,0,y+tt.getHeight(g),Alignment.Center);
		}
	}
}