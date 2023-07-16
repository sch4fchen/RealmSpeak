package com.robin.magic_realm.components;

import java.awt.Graphics;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.utility.Constants;

public class HamletChitComponent extends StateChitComponent {
	protected HamletChitComponent(GameObject obj) {
		super(obj);
		darkColor = MagicRealmColor.LIGHTGREEN;
	}
	public String getName() {
	    return HAMLET;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		TextType tt;
		
		if (getGameObject().hasThisAttribute(Constants.ALWAYS_VISIBLE) || isFaceUp()) {
			tt = new TextType("Hamlet",getChitSize(),"BOLD");
			tt.draw(g,0,12,Alignment.Center);
			
			String traveller = getThisAttribute("hamlet");
			tt = new TextType(traveller,getChitSize(),"BOLD");
			tt.draw(g,0,12+tt.getHeight(g),Alignment.Center);
		}
	}
}