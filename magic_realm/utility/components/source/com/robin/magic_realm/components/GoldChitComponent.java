package com.robin.magic_realm.components;

import java.awt.Graphics;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.utility.Constants;

public class GoldChitComponent extends StateChitComponent {
	protected GoldChitComponent(GameObject obj) {
		super(obj);
		darkColor = MagicRealmColor.YELLOW;
	}
	public String getName() {
	    return GOLD;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		TextType tt;
		
		if (getGameObject().hasThisAttribute(Constants.ALWAYS_VISIBLE) || isFaceUp()) {
			tt = new TextType("GOLD",getChitSize(),"BOLD");
			tt.draw(g,0,12,Alignment.Center);
			
			String gold = getThisAttribute("gold");
			tt = new TextType(gold,getChitSize(),"BOLD");
			tt.draw(g,0,12+tt.getHeight(g),Alignment.Center);
		}
	}
}