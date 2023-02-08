package com.robin.hexmap;

public abstract class HexSet {
	protected HexMapPoint center;
	protected Rotation rotation;
	
	public boolean overlaps(HexMap map) {
		return overlaps(map,center);
	}
	public boolean overlaps(HexMap map,HexMapPoint test) {
		Placement[] array = getPlacementArray();
		for (int i=0;i<array.length;i++) {
			HexMapPoint pos = test.getPositionFromPlacement(array[i]);
			if (!map.validHex(pos) || map.getHex(pos)!=null) {
				return true;
			}
		}
		return false;
	}
	public boolean adjacentToAnother(HexMap map,HexMapPoint test) {
		// must check overlaps() first, for this to return accurate results!
		Placement[] array = getPlacementArray();
		for (int i=0;i<array.length;i++) {
			if (array[i].isBorderHex()) {
				HexMapPoint pos = test.getPositionFromPlacement(array[i]);
				HexMapPoint[] check = pos.getAdjacentPoints();
				for (int n=0;n<check.length;n++) {
					Hex hex = map.getHex(check[n]);
					if (map.isGameHex(hex)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public void setCenter(HexMapPoint p) {
		center = p;
	}
	public HexMapPoint getCenter() {
		return center;
	}
	public void setRotation(Rotation r) {
		rotation = r;
	}
	public Rotation getRotation() {
		return rotation;
	}
	public abstract boolean contains(HexMapPoint p);
	public abstract void selectMap(HexMap map);
	public abstract void loadMap(HexMap map);
	public abstract void unloadMap(HexMap map);
	public abstract Placement[] getPlacementArray();
}