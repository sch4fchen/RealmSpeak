package com.robin.magic_realm.components;

import java.awt.Graphics;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;

// This isn't a "REAL" RealmComponent, because it doesn't wrap a GameObject.  I'm just borrowing the SquareChitComponent paint logic.
public class WeatherChit extends StateChitComponent {
	private int val;
	public WeatherChit(int val) {
		super(GameObject.createEmptyGameObject());
		this.val = val;
		darkColor = MagicRealmColor.RED;
	}
	public String getName() {
	    return "Weather Chit";
	}
	public boolean isLightSideUp() {
		return false;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		TextType tt = new TextType(String.valueOf(val),getChitSize()-4,"STAT_BLACK");
		tt.draw(g,2,(getChitSize()>>2)+2,Alignment.Center);
	}
}