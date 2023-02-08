package com.robin.magic_realm.components;

import java.util.ArrayList;

import javax.swing.ImageIcon;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.attribute.ColorMagic;

public interface MagicChit {
	public GameObject getGameObject();
	public boolean isEnchantable();
	public boolean isColor();
	public boolean compatibleWith(ColorMagic colorMagic);
	public ColorMagic getColorMagic();
	public void enchant(int magicNumber);
	public void makeFatigued();
	public ArrayList<Integer> getEnchantableNumbers();
	public ImageIcon getIcon();
	public boolean sameChitAttributes(MagicChit chit);
	public String asKey();
}