package com.robin.magic_realm.components;

import java.awt.Graphics;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.components.utility.Constants;

public class GateChitComponent extends StateChitComponent {
	protected GateChitComponent(GameObject obj) {
		super(obj);
		darkColor = MagicRealmColor.BLUE;
	}
	public String getName() {
	    return GATE;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		TextType tt;
		
		if (getGameObject().hasThisAttribute(Constants.ALWAYS_VISIBLE) || isFaceUp()) {
			String gate = getThisAttribute("gate");
			tt = new TextType(StringUtilities.capitalize(gate),getChitSize(),"BOLD");
			tt.draw(g,0,12,Alignment.Center);
			
			tt = new TextType("Gate",getChitSize(),"BOLD");
			tt.draw(g,0,12+tt.getHeight(g),Alignment.Center);
		}
	}
}