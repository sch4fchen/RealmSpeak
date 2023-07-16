package com.robin.magic_realm.components;

import java.awt.Graphics;
import java.awt.Graphics2D;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class CacheChitComponent extends TreasureLocationChitComponent {
	public static String DEPLETED_CACHE = "depleted_cache";
	protected CacheChitComponent(GameObject obj) {
		super(obj);
		darkColor = MagicRealmColor.GOLD;
	}
	public String getName() {
	    return CACHE_CHIT;
	}
	/**
	 * Test to see if cache is empty.  If it is, have it remove itself from the clearing.
	 */
	public void testEmpty() {
		if (isEmpty()) {
			TileComponent tile = (TileComponent)RealmComponent.getRealmComponent(getGameObject().getHeldBy());
			tile.getGameObject().remove(getGameObject());
			getGameObject().removeThisAttribute("clearing");
			getGameObject().setThisAttribute(DEPLETED_CACHE);
			clearOwner();
		}
	}
	public boolean isEmpty() {
		return getGameObject().getHoldCount()==0 && getGold()==0.0;
	}
	public double getGold() {
		CharacterWrapper cache = new CharacterWrapper(getGameObject());
		return cache.getGold();
	}

	public void paintComponent(Graphics g1) {
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D)g1;
		
		TextType tt;
		
		// Draw Image
		String drawText = null;
		String icon_type;
		RealmComponent owner = getOwner();
		if (owner.isCharacter()) {
			icon_type = owner.getGameObject().getThisAttribute("icon_type");
			if (icon_type != null) {
				String iconFolder = owner.getGameObject().getThisAttribute("icon_folder");
				drawIcon(g, iconFolder, icon_type, .4);
			}
		}
		else if (owner.isNative()) {
			drawText = owner.getGameObject().getName().substring(0,1)+owner.getGameObject().getThisAttribute("rank");
		}
		else if (owner.isMonster()) {
			drawText = owner.getGameObject().getName().substring(0,6);
		}
		if (drawText!=null) {
			tt = new TextType(drawText,getChitSize(),"BOLD");
			tt.draw(g,0,15,Alignment.Center);
		}
		
		tt = new TextType("CACHE",getChitSize(),"BOLD_BLUE");
		tt.draw(g,0,2,Alignment.Center);
		
		if (getGameObject().hasThisAttribute(Constants.ALWAYS_VISIBLE) || isFaceUp()) {
			int half = getChitSize()>>1;
			
			g.setColor(BACKING);
			g.fillOval(half+5,half+5,15,15);
			
			String clearing = getThisAttribute("cache_number");
			tt = new TextType(clearing,half,"BOLD_BLUE");
			tt.draw(g,half+1,18+tt.getHeight(g),Alignment.Center);
		}
	}
}