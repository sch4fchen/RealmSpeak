package com.robin.magic_realm.components;

import java.awt.Graphics;
import java.awt.Graphics2D;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.TreasureUtility;
import com.robin.magic_realm.components.utility.TreasureUtility.ArmorType;

public class ArmorChitComponent extends RoundChitComponent {
	public static final String DAMAGED = LIGHT_SIDE_UP;
	public static final String INTACT = DARK_SIDE_UP;

	public ArmorChitComponent(GameObject obj) {
		super(obj);
		
		try {
			lightColor = MagicRealmColor.getColor(getAttribute("damaged","chit_color"));
			darkColor = MagicRealmColor.getColor(getAttribute("intact","chit_color"));
		}
		catch(Exception ex) {
			System.out.println("problem with "+obj.getName()+": "+ex);
		}
	}
	public boolean isShieldType() {
		return TreasureUtility.getArmorType(getGameObject())==ArmorType.Shield;
	}
	public boolean isHelmetType() {
		return TreasureUtility.getArmorType(getGameObject())==ArmorType.Helmet;
	}
	public boolean isBreastplateType() {
		return TreasureUtility.getArmorType(getGameObject())==ArmorType.Breastplate;
	}
	public boolean isSuitOfArmorType() {
		return TreasureUtility.getArmorType(getGameObject())==ArmorType.Armor;
	}
	public String getLightSideStat() {
		return "damaged";
	}
	public String getDarkSideStat() {
		return "intact";
	}
	public void setIntact(boolean val) {
		if (val) {
			setDarkSideUp();
		}
		else {
			setLightSideUp();
		}
	}
	public boolean isDamaged() {
		return isLightSideUp();
	}
	public String getName() {
	    return ARMOR;
	}
	public int getChitSize() {
		return 75;
	}

	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		super.paintComponent(g);
		
		// Draw image
		String icon_type = gameObject.getThisAttribute(Constants.ICON_TYPE);
		String icon_folder = gameObject.getThisAttribute(Constants.ICON_FOLDER);
		if (icon_type!=null) {
			double size = 0.5;
			if(gameObject.hasThisAttribute(Constants.ICON_SIZE)) {
				size = Double.parseDouble(gameObject.getThisAttribute(Constants.ICON_SIZE));
			}
			drawIcon(g,icon_folder,icon_type,size);
		}
		
		// Draw Stats
		String vulnerability = getAttribute("this","vulnerability");
		TextType tt = new TextType(vulnerability,getChitSize(),"BIG_BOLD");
		
		if (getGameObject().hasThisAttribute(Constants.MAGIC_COLOR_BONUS_ACTIVE) && getGameObject().hasThisAttribute(Constants.MAGIC_COLOR_BONUS_ARMOR)) {
			vulnerability = getGameObject().getThisAttribute(Constants.MAGIC_COLOR_BONUS_ARMOR);
			tt.draw(g,0,getChitSize()-(getChitSize()>>3)-tt.getHeight(g),Alignment.Center,MagicRealmColor.PURPLE);
		}
		else {
			tt.draw(g,0,getChitSize()-(getChitSize()>>3)-tt.getHeight(g),Alignment.Center);
		}
		
		if (isDamaged()) {
			tt = new TextType("DAMAGED",getChitSize(),"TITLE_GRAY");
			tt.draw(g,0,getChitSize()>>2);
		}
		
		// If magic, draw name
		if (gameObject.hasKey("magic")) {
			String name = gameObject.getName();
			int space = name.indexOf(" ");
			if (space>=0) { // this is a weak solution
				name = name.substring(0,space);
				tt = new TextType(name,getChitSize(),"TITLE_RED");
				tt.draw(g,getChitSize()-10-tt.getWidth(g),(getChitSize()>>2)+10,Alignment.Left);
			}
		}
		
		drawDamageAssessment(g);
	}
	public Strength getVulnerability() {
		return new Strength(getAttribute("this", "vulnerability"));
	}
}