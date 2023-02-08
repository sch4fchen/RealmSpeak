package com.robin.magic_realm.components;

import java.awt.Rectangle;
import java.awt.Shape;

import com.robin.game.objects.GameObject;

public abstract class SquareChitComponent extends ChitComponent {
	
	protected SquareChitComponent(GameObject obj) {
		super(obj);
	}
	
	public Shape getShape(int x,int y,int size) {
		return new Rectangle(x,y,size-1,size-1);
	}
}