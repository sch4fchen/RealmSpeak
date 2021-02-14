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
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import com.robin.game.objects.GameObject;

public class FamiliarChitComponent extends SquareChitComponent {
	public FamiliarChitComponent(GameObject go) {
		super(go);
		lightColor = MagicRealmColor.ORANGE;
		darkColor = MagicRealmColor.BROWN;
	}

	public String getLightSideStat() {
		return "this"; // only one stat side
	}
	public String getDarkSideStat() {
		return "this"; // only one stat side
	}
	public int getChitSize() {
		return S_CHIT_SIZE;
	}
	public String getName() {
		return getGameObject().getName();
	}
	
	public Shape getShape(int x,int y,int size) {
		if (CharacterChitComponent.displayStyle == CharacterChitComponent.DISPLAY_STYLE_LEGENDARY || 
				CharacterChitComponent.displayStyle == CharacterChitComponent.DISPLAY_STYLE_LEGENDARY_CLASSIC) {
		return new Ellipse2D.Float(x,y,size-1,size-1);
		}
		return super.getShape(x, y, size);
	}
	
	public void paintComponent(Graphics g) {
		if (CharacterChitComponent.displayStyle == CharacterChitComponent.DISPLAY_STYLE_LEGENDARY) {
			super.paintComponent(g,false);
			drawIcon(g,"characters_legendary","familiar",0.165);
			return;
		}
		if (CharacterChitComponent.displayStyle == CharacterChitComponent.DISPLAY_STYLE_LEGENDARY_CLASSIC) {
			super.paintComponent(g,false);
			drawIcon(g,"characters_legendary_classic","familiar",0.165);
			return;
		}
		
		super.paintComponent(g);
		drawIcon(g,"characters","familiar",0.65);
	}
}