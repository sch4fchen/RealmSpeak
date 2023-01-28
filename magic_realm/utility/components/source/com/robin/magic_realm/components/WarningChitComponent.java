/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
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
			
			String warning = getAttribute("this","warning");
			tt = new TextType(warning,getChitSize(),"BOLD");
			tt.draw(g,0,y,Alignment.Center);
			
			String type = getAttribute("this","tile_type");
			tt = new TextType(type,getChitSize(),"BOLD");
			tt.draw(g,0,y+tt.getHeight(g),Alignment.Center);
		}
	}
}