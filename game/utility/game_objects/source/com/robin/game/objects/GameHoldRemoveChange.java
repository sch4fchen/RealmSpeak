package com.robin.game.objects;

public class GameHoldRemoveChange extends GameObjectChange {
	
	private Long holdId;
	
	public GameHoldRemoveChange(GameObject go) {
		super(go);
		holdId = null;
	}
//	public boolean equals(Object o1) {
//		if (o1 instanceof GameHoldRemoveChange) {
//			GameHoldRemoveChange other = (GameHoldRemoveChange)o1;
//			return (other.getId()==getId()
//					&& other.holdId.equals(holdId));
//		}
//		return false;
//	}
//	public boolean sameTypeOfChange(GameObjectChange o1) {
//		return false;
//	}
	public void setHoldId(long val) {
		holdId = Long.valueOf(val);
	}
	protected void applyChange(GameData data,GameObject go) {
		GameObject toRemove = data.getGameObject(holdId);
		go._remove(toRemove);
	}
	protected void rebuildChange(GameData data,GameObject go) {
		GameObject toRemove = data.getGameObject(holdId);
		go.remove(toRemove);
	}
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(":  Removes from hold "+holdId);
		return sb.toString();
	}
}