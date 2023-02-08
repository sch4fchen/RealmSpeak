package com.robin.magic_realm.MRMap;

import java.awt.*;

import com.robin.magic_realm.map.Tile;

public class TileMappingPossibility {
	protected Tile tile;
	protected Point position;
	protected int rotation;
	public TileMappingPossibility(Tile tile,Point position,int rotation) {
		super();
		this.tile = tile;
		this.position = position;
		this.rotation = rotation;
	}
	public Tile getTile() {
		return tile;
	}
	public Point getPosition() {
		return position;
	}
	public int getRotation() {
		return rotation;
	}
}