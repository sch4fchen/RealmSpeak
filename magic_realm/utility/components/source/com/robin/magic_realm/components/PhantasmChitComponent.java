package com.robin.magic_realm.components;

import java.awt.Graphics;

import com.robin.game.objects.GameObject;

public class PhantasmChitComponent extends SquareChitComponent {
	public PhantasmChitComponent(GameObject go) {
		super(go);
		lightColor = MagicRealmColor.GRAY;
		darkColor = MagicRealmColor.DARKGRAY;
	}

	public String getLightSideStat() {
		return "this"; // only one stat side
	}
	public String getDarkSideStat() {
		return "this"; // only one stat side
	}
	public int getChitSize() {
		return M_CHIT_SIZE;
	}
	public String getName() {
		return getGameObject().getName();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Draw image
		drawIcon(g,"characters","phantasm",0.75);
	}
}