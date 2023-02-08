package com.robin.game.objects;

public class GameHoldAddChange extends GameObjectChange {
	
	public Long holdId;
	
	public GameHoldAddChange(GameObject go) {
		super(go);
		holdId = null;
	}
//	public boolean equals(Object o1) {
//		if (o1 instanceof GameHoldAddChange) {
//			GameHoldAddChange other = (GameHoldAddChange)o1;
//			return (other.getId()==getId()
//					&& other.holdId.equals(holdId));
//		}
//		return false;
//	}
	public void setHoldId(long val) {
		holdId = Long.valueOf(val);
	}
	protected void applyChange(GameData data,GameObject go) {
		GameObject toAdd = data.getGameObject(holdId);
		go._add(toAdd);
	}
	protected void rebuildChange(GameData data,GameObject go) {
		GameObject toAdd = data.getGameObject(holdId);
		go.add(toAdd);
	}
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(":  Adds hold "+holdId);
		return sb.toString();
	}
//	public boolean sameTypeOfChange(GameObjectChange o1) {
//		return false;
//	}
}