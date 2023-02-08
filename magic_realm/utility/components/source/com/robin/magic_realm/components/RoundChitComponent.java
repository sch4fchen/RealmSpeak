package com.robin.magic_realm.components;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import com.robin.game.objects.*;

public abstract class RoundChitComponent extends ChitComponent {

	protected RoundChitComponent(GameObject obj) {
		super(obj);
	}
	
	public Shape getShape(int x,int y,int size) {
		return new Ellipse2D.Float(x,y,size-1,size-1);
	}
	protected int getSortOrder() {
		return super.getSortOrder()+500;
	}
}