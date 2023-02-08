package com.robin.magic_realm.components;

import java.awt.Graphics;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.utility.Constants;

public class MinorCharacterChitComponent extends RoundChitComponent {

	protected MinorCharacterChitComponent(GameObject go) {
		super(go);
		lightColor = MagicRealmColor.GOLD;
		darkColor = MagicRealmColor.BROWN;
	}
	public String getLightSideStat() {
		return "this";
	}

	public String getDarkSideStat() {
		return "this";
	}

	public int getChitSize() {
		return M_CHIT_SIZE;
	}

	public String getName() {
		return getGameObject().getName();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		String icon_type = gameObject.getThisAttribute(Constants.ICON_TYPE);
		String iconDir = gameObject.getThisAttribute(Constants.ICON_FOLDER);
		drawIcon(g, iconDir, icon_type, 0.7);

		int pos = (M_CHIT_SIZE>>1)-10;
		TextType tt = new TextType(getGameObject().getName(),M_CHIT_SIZE-10,"WHITE_NOTE");
		tt.draw(g,5,pos,Alignment.Center);
		pos += (tt.getHeight(g)*2);
	}
}