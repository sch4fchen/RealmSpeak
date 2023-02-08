package com.robin.magic_realm.components;

import java.awt.*;

import com.robin.game.objects.*;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.utility.Constants;

public class DwellingChitComponent extends SquareChitComponent {
	public static final String NORMAL_SIDE = LIGHT_SIDE_UP;
	public static final String OTHER_SIDE = DARK_SIDE_UP;

	protected DwellingChitComponent(GameObject obj) {
		super(obj);
		
		try {
			lightColor = MagicRealmColor.getColor("tan");
			darkColor = MagicRealmColor.getColor("darkgray");
		}
		catch(Exception ex) {
			System.out.println("problem with "+obj.getName()+": "+ex);
		}
	}
	public String getName() {
	    return DWELLING;
	}
	protected int getSortOrder() {
		return 1000; // want this on the bottom!
	}
	
	public int getChitSize() {
		return T_CHIT_SIZE;
	}
	public String getLightSideStat() {
		return "this";
	}
	public String getDarkSideStat() {
		return "this";
	}
	
	public void paintComponent(Graphics g1) {
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		
		boolean useColor = useColorIcons();
		
		// Draw image
		String icon_type = gameObject.getThisAttribute(Constants.ICON_TYPE);
		if (icon_type!=null) {
			if (gameObject.hasThisAttribute("super_realm")) {
				drawIcon(g,gameObject.getThisAttribute(Constants.ICON_FOLDER),icon_type,1.1,0,20,null);
			} else {
				drawIcon(g,"dwellings"+(useColor?"_c":""),icon_type,0.9);
			}
		}
		
		if (useColor) {
			TextType tt = new TextType(getGameObject().getName(),T_CHIT_SIZE,"BOLD");
			tt.draw(g1,5,2,Alignment.Left);
		}
	}
}