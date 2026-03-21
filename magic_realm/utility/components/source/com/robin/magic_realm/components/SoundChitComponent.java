package com.robin.magic_realm.components;

import java.awt.Graphics;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.utility.Constants;

public class SoundChitComponent extends StateChitComponent {
	protected SoundChitComponent(GameObject obj) {
		super(obj);
		darkColor = MagicRealmColor.RED;
	}
	public String getName() {
	    return SOUND;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		TextType tt;
		
		if (getGameObject().hasThisAttribute(Constants.ALWAYS_VISIBLE) || isFaceUp()) {
			int y = 12;
			String iconName = gameObject.getThisAttribute(Constants.ICON_TYPE);
			String iconFolder = gameObject.getThisAttribute(Constants.ICON_FOLDER);
			Double size = 0.4;
			if (gameObject.hasThisAttribute(Constants.ICON_SIZE)) {
				size = Double.parseDouble(gameObject.getThisAttribute(Constants.ICON_SIZE));
			}
			if (iconName!=null && iconFolder!=null) {
				drawIcon(g,iconFolder,iconName,size,0,-20,null);
				y = 20;
			}
			
			String sound = getThisAttribute("sound");
			tt = new TextType(sound,getChitSize(),"BOLD");
			tt.draw(g,0,y,Alignment.Center);
			
			String clearing = getThisAttribute("clearing");
			tt = new TextType(clearing,getChitSize(),"BOLD");
			tt.draw(g,0,y+tt.getHeight(g),Alignment.Center);
		}
	}
}