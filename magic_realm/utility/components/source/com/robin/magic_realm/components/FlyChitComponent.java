package com.robin.magic_realm.components;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.attribute.Speed;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class FlyChitComponent extends StateChitComponent {
	protected FlyChitComponent(GameObject go) {
		super(go);
		lightColor = MagicRealmColor.WHITE;
		darkColor = MagicRealmColor.WHITE;
	}
	public String getLightSideStat() {
		return "this";
	}

	public String getDarkSideStat() {
		return "this";
	}

	public int getChitSize() {
		return S_CHIT_SIZE;
	}

	public String getName() {
		return getGameObject().getName();
	}
	
	public void paintComponent(Graphics g1) {
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		
		/*
		 * Owners get to decide things:
		 * 
		 * Hurricane Winds:  The spellcaster chooses which adjacent tile the target flies to.
		 * Broomstick: 	...force him to use the FLY chit to charge or fly away, as the spellcaster chooses.
		 * 				...force him to FLY to the tile he is moving to instead of moving to a particular clearing.
		 * 
		 * If there is no owner, then the chit can be used at the character's discretion.
		 */
		boolean hasOwner = getOwnerId()!=null;
		// Draw Image
		Composite old = g.getComposite();
		AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
		g.setComposite(composite);
		
		String icon_type = null;
		if (hasOwner) {
			icon_type = getOwner().getGameObject().getThisAttribute("icon_type");
		}
		else {
			GameObject heldBy = getGameObject().getHeldBy();
			if (heldBy!=null) {
				icon_type = getGameObject().getHeldBy().getThisAttribute("icon_type");
			}
		}
		if (icon_type != null && getGameObject().getHeldBy()!=null) {
			String iconFolder = getGameObject().getHeldBy().getThisAttribute("icon_folder");
			drawIcon(g, iconFolder, icon_type, 0.4);
		}
		g.setComposite(old);
		
		TextType tt;
		
		int x = 0;
		int y = 10;
		
		tt = new TextType("FLY",getChitSize(),"BOLD");
		tt.draw(g,x,y,Alignment.Center);
		
		String strength = getGameObject().getThisAttribute("strength");
		String speed = getGameObject().getThisAttribute("speed");
		tt = new TextType(strength+speed,getChitSize(),"BOLD");
		y+=tt.getHeight(g);
		tt.draw(g,x,y,Alignment.Center);
		y+=tt.getHeight(g);
		
		String sourceSpell = getGameObject().getThisAttribute("sourceSpell");
		tt = new TextType(sourceSpell,getChitSize(),"MINI_RED");
		tt.draw(g,0,y,Alignment.Center);
	}
	public void expireSourceSpell() {
		String stringId = getGameObject().getThisAttribute(Constants.SPELL_ID);
		GameObject sourceSpell = getGameObject().getGameData().getGameObject(Long.valueOf(stringId));
		SpellWrapper spell = new SpellWrapper(sourceSpell);
		spell.expireSpell();
	}
	public Strength getStrength() {
		if (affectedByKey(Constants.ALTERED_WEIGHT)) return new Strength(getGameObject().getThisAttribute(Constants.ALTERED_WEIGHT));
		return new Strength(getGameObject().getThisAttribute("strength"));
	}
	public Speed getSpeed() {
		return new Speed(getGameObject().getThisAttribute("speed"));
	}
}