package com.robin.game.objects;

public class GameBumpVersionChange extends GameObjectChange {
	
	public GameBumpVersionChange(GameObject go) {
		super(go);
	}
	protected void applyChange(GameData data,GameObject go) {
		go._bumpVersion();
	}
	protected void rebuildChange(GameData data,GameObject go) {
		go.bumpVersion();
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(": Bump data version to force redraw");
		return sb.toString();
	}
//	public boolean equals(Object o1) {
//		if (o1 instanceof GameBumpVersionChange) {
//			GameBumpVersionChange other = (GameBumpVersionChange)o1;
//			return (other.getId()==getId());
//		}
//		return false;
//	}
//	public boolean sameTypeOfChange(GameObjectChange o1) {
//		return false;
//	}
}