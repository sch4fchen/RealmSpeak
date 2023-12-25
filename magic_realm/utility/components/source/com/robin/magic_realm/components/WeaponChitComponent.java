package com.robin.magic_realm.components;

import java.awt.Graphics;
import java.awt.Graphics2D;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.StarShape;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.utility.ClearingUtility;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class WeaponChitComponent extends RoundChitComponent {
	public static final String UNALERTED = LIGHT_SIDE_UP;
	public static final String ALERTED = DARK_SIDE_UP;

	public WeaponChitComponent(GameObject obj) {
		super(obj);
		try {
			lightColor = MagicRealmColor.getColor(getAttribute("unalerted","chit_color"));
			darkColor = MagicRealmColor.getColor(getAttribute("alerted","chit_color"));
		}
		catch(Exception ex) {
			System.out.println("problem with "+obj.getName()+": "+ex);
		}
	}
	public void setAlerted(boolean val) {
		if (val) {
			setDarkSideUp();
		}
		else {
			setLightSideUp();
		}
	}
	public boolean isAlerted() {
		return !isLightSideUp();
	}
	public String getName() {
	    return WEAPON;
	}
	
	public int getChitSize() {
		return 75;
	}
	public Strength getWeight() {
		Strength strength = super.getWeight();
		TileLocation tl = ClearingUtility.getTileLocation(getGameObject());
		if (tl!=null && tl.isInClearing() && tl.clearing.hasSpellEffect(Constants.HEAVIED)) {
			strength.modify(1);
		}
		return strength;
	}
	
	public int getLength() {
		CombatWrapper combatWeapon = new CombatWrapper(gameObject);
		if (combatWeapon.wasThrown()) return 12;
		if (gameObject.hasThisAttribute(Constants.MAGIC_COLOR_BONUS_ACTIVE) && gameObject.hasThisAttribute(Constants.MAGIC_COLOR_BONUS_LENGTH)) return gameObject.getThisInt(Constants.MAGIC_COLOR_BONUS_LENGTH);
		if (gameObject.hasThisAttribute(Constants.ALTER_WEIGHT)) {
			int difference = (new Strength(gameObject.getThisAttribute(Constants.ALTER_WEIGHT))).getLevels()-(new Strength((gameObject.getThisAttribute(Constants.WEIGHT)))).getLevels();
			int length = gameObject.getThisInt("length")+difference;
			if (length<=0) return 0;
			if (length>=18) return 18;
			return length;
		}
		return gameObject.getThisInt("length");
	}
	public String getLightSideStat() {
		return "unalerted";
	}
	public String getDarkSideStat() {
		return "alerted";
	}
	public Speed getSpeed() {
		String val = getFaceAttributeString("attack_speed");
		int mod = 0;
		if (new CombatWrapper(getWielder().getGameObject()).isFreezed()) {
			mod = 1;
		}
		if (gameObject.hasThisAttribute(Constants.MAGIC_COLOR_BONUS_ACTIVE)) {
			String magicSpeed = getFaceAttributeString(Constants.MAGIC_COLOR_BONUS_SPEED);
			if (magicSpeed!=null && magicSpeed.trim().length()>0) return new Speed(magicSpeed,mod);
		}
		if (val!=null && val.trim().length()>0) {
			if (gameObject.hasThisAttribute(Constants.ALTER_WEIGHT)) {
				int difference = (new Strength(gameObject.getThisAttribute(Constants.ALTER_WEIGHT))).getLevels()-(new Strength((gameObject.getThisAttribute(Constants.WEIGHT)))).getLevels();
				int baseSpeed = Integer.valueOf(val).intValue();
				if (!isMissile()) {
					if (baseSpeed+difference<2) {
						difference = 2-baseSpeed;
					}
				}
				else {
					if (baseSpeed+difference<1) {
						difference = 1-baseSpeed;
					}
				}
				mod = mod+difference;
			}
			return new Speed(val,mod);
		}
		return null;
	}
	public Strength getStrength() {
		Strength strength = new Strength();
		String val = getFaceAttributeString("strength");
		if (val!=null && val.trim().length()>0) {
			strength = new Strength(val);
		}
		TileLocation tl = ClearingUtility.getTileLocation(getGameObject());
		if (tl!=null && tl.isInClearing() && tl.clearing.hasSpellEffect(Constants.HEAVIED)) {
			strength.modify(1);
		}
		if (gameObject.hasThisAttribute(Constants.ALTER_WEIGHT)) {
			int difference = (new Strength(gameObject.getThisAttribute(Constants.ALTER_WEIGHT))).getLevels()-(new Strength((gameObject.getThisAttribute(Constants.WEIGHT)))).getLevels();
			strength.modify(difference);
		}
		return strength;
	}
	public CharacterWrapper getWielder() {
		GameObject go = getGameObject().getHeldBy();
		if (go!=null) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc!=null && rc.isCharacter()) {
				return new CharacterWrapper(rc.getGameObject());
			}
		}
		return null;
	}
	public int getSharpness() {
		int sharpness = getFaceAttributeInt("sharpness");
		sharpness += getGameObject().getThisInt(Constants.ADD_SHARPNESS);
		
		if (gameObject.hasThisAttribute(Constants.MAGIC_COLOR_BONUS_ACTIVE)) {
			sharpness += getFaceAttributeInt(Constants.MAGIC_COLOR_BONUS_SHARPNESS);
		}
		
		CharacterWrapper wielder = getWielder();
		if (wielder!=null && wielder.hasActiveInventoryThisKey(Constants.INCREASE_SHARP)) {
			sharpness++;
		}
		
		if (sharpness>0 && getGameObject().hasThisAttribute(Constants.BLUNT) && !getGameObject().hasThisAttribute(Constants.ENCHANTED_WEAPON)) {
			sharpness--;
		}
		
		if (sharpness>0) {
			TileLocation tl = ClearingUtility.getTileLocation(getGameObject());
			if (tl!=null && tl.isInClearing() && tl.clearing.hasSpellEffect(Constants.BLUNTED)) {
				sharpness--;
			}
		}
		return sharpness;
	}
	public boolean hasAlertAdvantage() {
		// Report true if alerted side is different than non-alerted
		return !(equivalentSidesFor("strength")
				&& equivalentSidesFor("attack_speed")
				&& equivalentSidesFor("sharpness"));
	}
	private boolean equivalentSidesFor(String attribute) {
		String light = getGameObject().getAttribute(getLightSideStat(),attribute);
		String dark = getGameObject().getAttribute(getDarkSideStat(),attribute);
		return light==null?dark==null:light.equals(dark);
	}
	public boolean isMissile() {
		return gameObject.hasThisAttribute("missile") && !gameObject.hasThisAttribute(Constants.ENCHANTED_WEAPON);
	}
	public boolean isThrowable() {
		return gameObject.hasThisAttribute(Constants.THROWABLE);
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
		String statSide = isAlerted()?"alerted":"unalerted";
		String asterisk = isAlerted()?"*":"";
		
		String speed = getAttribute(statSide,"attack_speed");
		String strength = getStrength().getChitString();
		int sharpness = getSharpness();
		
		// Draw attack
		TextType tt = new TextType(strength,getChitSize(),"BIG_BOLD");
		int x = (getChitSize()>>1)-(getChitSize()>>3)-(5*sharpness);
		int y = getChitSize()-15-tt.getHeight(g)+5;
		tt.draw(g,x,y,Alignment.Left);
		x += tt.getWidth(g)+5;
		y += tt.getHeight(g)-8;
		for (int i=0;i<sharpness;i++) {
			StarShape star = new StarShape(x,y,5,8);
			g.fill(star);
			x += 12;
		}
		
		// Draw alert asterisk
		tt = new TextType(asterisk,getChitSize(),"BIG_BOLD");
		tt.draw(g,getChitSize()-10-tt.getWidth(g),(getChitSize()>>2)-5,Alignment.Left);
		
		// Draw speed
		x = 15;
		y = 5;
		tt = new TextType(speed,getChitSize(),"BIG_BOLD");
		tt.draw(g,x,y,Alignment.Left);
		
		// If magic, draw name
		if (gameObject.hasKey("magic")) {
			String name = gameObject.getThisAttribute("alt_name");
			if (name==null) {
				name = gameObject.getName();
				int space = name.indexOf(' ');
				name = name.substring(0,space);
			}
			tt = new TextType(name,getChitSize(),"TITLE_RED");
			tt.draw(g,getChitSize()-10-tt.getWidth(g),(getChitSize()>>2)+10,Alignment.Left);
		}
	}
	public String getAttackString() {
		Strength str = getStrength();
		StringBuffer sb = new StringBuffer(str.isNegligible()?"":str.toString());
		Speed speed = getSpeed();
		if (speed!=null) {
			sb.append(getSpeed().getNum());
		}
		for (int i=0;i<getSharpness();i++) {
			sb.append("*");
		}
		return sb.toString();
	}
}