package com.robin.magic_realm.components;

import java.awt.Graphics;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.utility.Constants;

public class EventChitComponent extends StateChitComponent {
	protected EventChitComponent(GameObject obj) {
		super(obj);
		darkColor = MagicRealmColor.PINK;
	}
	public String getName() {
	    return EVENT;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		TextType tt;
		
		if (getGameObject().hasThisAttribute(Constants.ALWAYS_VISIBLE) || isFaceUp()) {
			tt = new TextType("Event",getChitSize(),"BOLD");
			tt.draw(g,0,12,Alignment.Center);
			
			String event = getAttribute("this","event");
			tt = new TextType(event,getChitSize(),"BOLD");
			tt.draw(g,0,12+tt.getHeight(g),Alignment.Center);
		}
	}
}