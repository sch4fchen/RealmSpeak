package com.robin.magic_realm.components;

import java.awt.Graphics;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.components.utility.Constants;

public class GuildChitComponent extends StateChitComponent {
	
	protected GuildChitComponent(GameObject obj) {
		super(obj);
		darkColor = MagicRealmColor.getColor(obj.getThisAttribute("chit_color"));
	}
	public String getName() {
	    return GUILD;
	}
	public void flip() {
		setFaceUp();
	}
	public void setLightSideUp() {
		if (isDarkSideUp()) {
			super.setLightSideUp();
			updateSize();
		}
	}
	public void setDarkSideUp() {
		if (isLightSideUp()) {
			super.setDarkSideUp();
			updateSize();
		}
	}
	public int getChitSize() {
		return isFaceUp()?H_CHIT_SIZE:super.getChitSize();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		TextType tt;
		
		if (getGameObject().hasThisAttribute(Constants.ALWAYS_VISIBLE) || isFaceUp()) {
			updateSize();
			
			String icon_type = "house";// gameObject.getThisAttribute(Constants.ICON_TYPE);
			String iconDir = "dwellings_c";//gameObject.getThisAttribute(Constants.ICON_FOLDER);
			drawIcon(g, iconDir, icon_type, 0.7);
			
			String guild = getGameObject().getName();
			tt = new TextType(StringUtilities.capitalize(guild),getChitSize(),"BOLD");
			tt.draw(g,0,H_CHIT_SIZE-20,Alignment.Center);
		}
	}
}