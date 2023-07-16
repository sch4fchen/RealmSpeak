package com.robin.magic_realm.components;

import java.awt.Graphics;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.TextType;
import com.robin.general.graphics.TextType.Alignment;

/**
 * A Phase chit is created by a Phase Spell, and is placed into the target's inventory.
 * It can be activated (never traded or dropped), and expires at the end of the phase.
 */
public class PhaseChitComponent extends SquareChitComponent {
	protected PhaseChitComponent(GameObject go) {
		super(go);
		lightColor = MagicRealmColor.YELLOW;
		darkColor = MagicRealmColor.YELLOW;
	}
	public String getLightSideStat() {
		return "this";
	}

	public String getDarkSideStat() {
		return "this";
	}

	public int getChitSize() {
		return M_CHIT_SIZE;
	}

	public String getName() {
		return getGameObject().getName();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int pos = 2;
		
		// Draw the title
		TextType tt = new TextType("Phase Chit",M_CHIT_SIZE-10,"TITLE");
		tt.draw(g,5,pos,Alignment.Center);
		pos += (tt.getHeight(g)*2);
		
		String desc = gameObject.getThisAttribute("text");
		if (desc!=null) {
			tt = new TextType(desc,M_CHIT_SIZE-10,"NORMAL");
			tt.draw(g,5,pos,Alignment.Center);
		}
	}
}