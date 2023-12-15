package com.robin.magic_realm.components;

import java.awt.*;

import com.robin.game.objects.*;
import com.robin.general.graphics.*;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.components.utility.Constants;

public class TreasureLocationChitComponent extends StateChitComponent {
	protected TreasureLocationChitComponent(GameObject obj) {
		super(obj);
		darkColor = MagicRealmColor.GOLD;
	}
	public String getName() {
	    return TREASURE_LOCATION;
	}

	public void paintComponent(Graphics g1) {
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		
		TextType tt;
		
		if (getGameObject().hasThisAttribute(Constants.ALWAYS_VISIBLE) || isFaceUp()) {
			if (!isCacheChit()) {
				int y = 12;
				String iconName = gameObject.getThisAttribute(Constants.ICON_TYPE+"_sr");
				String iconFolder = gameObject.getThisAttribute(Constants.ICON_FOLDER+"_sr");
				if (iconName!=null && iconFolder!=null) {
					drawIcon(g,iconFolder,iconName,0.4,0,-20,null);
					y = 20;
				}
				
				String tl = getThisAttribute("treasure_location");
				if (tl == null || tl.length()==0) {
					tl = getThisAttribute("minor_tl");
				}
				tt = new TextType(StringUtilities.capitalize(tl),getChitSize(),"BOLD");
				tt.draw(g,0,y,Alignment.Center);
			
				String clearing = getThisAttribute("clearing");
				tt = new TextType(clearing,getChitSize(),"BOLD");
				tt.draw(g,0,y+tt.getHeight(g),Alignment.Center);
			}
			
			if (gameObject.hasThisAttribute(Constants.NEEDS_OPEN)) {
				tt = new TextType("CLOSED",getChitSize(),"TITLE_RED");
				tt.draw(g,0,2,Alignment.Center);
			}
			
			if (gameObject.hasThisAttribute(Constants.DESTROYED)) {
				// draw red X
				int size = getChitSize();
				g.setColor(TRANSPARENT_RED);
				
				g.setStroke(thickLine);
				g.drawLine(0,0,size,size);
				g.drawLine(0,size,size,0);
			}
		}
	}
}