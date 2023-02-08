package com.robin.game.objects;

public class GameObjectDeletionChange extends GameObjectChange {

	public GameObjectDeletionChange(GameObject go) {
		super(go);
	}
	
	public void applyChange(GameData data) {
		GameObject go = data.getGameObject(getId());
		if (go!=null) {
			data.removeObject(go);
//			System.out.println("applychange = Deleting "+go.getName());
		}
	}
	
	protected void applyChange(GameData data, GameObject go) {
	}

	protected void rebuildChange(GameData data, GameObject go) {
	}
}