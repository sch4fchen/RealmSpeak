package com.robin.magic_realm.components;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import com.robin.game.objects.GameObject;

public class EmptyCardComponent extends CardComponent {
	
	private Border border;
	
	public EmptyCardComponent() {
		super(GameObject.createEmptyGameObject());
		border = BorderFactory.createLoweredBevelBorder();
	}

	public String getAdditionalInfo() {
		return null;
	}

	public Color getBackingColor() {
		return null;
	}

	public String getCardTypeName() {
		return null;
	}

	public String getName() {
		return null;
	}
	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		border.paintBorder(this,g,0,0,CARD_WIDTH,CARD_HEIGHT);
	}
}