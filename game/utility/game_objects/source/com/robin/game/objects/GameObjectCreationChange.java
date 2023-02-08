package com.robin.game.objects;

public class GameObjectCreationChange extends GameObjectChange {
	
	public GameObjectCreationChange(GameObject go) {
		super(go);
	}
	
	protected void applyChange(GameData data, GameObject go) {
		// don't actually have to do anything here! :-)
	}
	
	protected void rebuildChange(GameData data, GameObject go) {
		// don't actually have to do anything here! :-)
	}

//	public boolean equals(Object o) {
//		if (o instanceof GameObjectCreationChange) {
//			GameObjectCreationChange other = (GameObjectCreationChange)o;
//			return (other.getId()==getId());
//		}
//		return false;
//	}
//
//	public boolean sameTypeOfChange(GameObjectChange other) {
//		return false;
//	}
}