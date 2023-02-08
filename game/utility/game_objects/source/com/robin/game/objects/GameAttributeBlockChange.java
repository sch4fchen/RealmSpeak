package com.robin.game.objects;

public class GameAttributeBlockChange extends GameObjectChange {
	
	private Long sourceId;
	private String from;
	private String to;
	
	public GameAttributeBlockChange(GameObject go) {
		super(go);
		sourceId = null;
		from = null;
		to = null;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		if (sourceId==null) {
			sb.append(":  rename attribute block "+from+" to "+to);
		}
		else {
			sb.append(":  copy attribute block "+from+" from gameobject "+sourceId);
		}
		return sb.toString();
	}
	public void rename(String inFrom,String inTo) {
		this.sourceId = null;
		this.from = inFrom;
		this.to = inTo;
	}
	public void copyFrom(GameObject go,String blockName) {
		sourceId = Long.valueOf(go.getId());
		from = blockName;
		to = null;
	}
	protected void applyChange(GameData data, GameObject go) {
		if (sourceId==null) {
			go._renameAttributeBlock(from,to);
		}
		else {
			GameObject source = data.getGameObject(sourceId);
			go._copyAttributeBlockFrom(source,from);
		}
	}
	protected void rebuildChange(GameData data, GameObject go) {
		if (sourceId==null) {
			go.renameAttributeBlock(from,to);
		}
		else {
			GameObject source = data.getGameObject(sourceId);
			go.copyAttributeBlockFrom(source,from);
		}
	}
//	public boolean equals(Object o) {
//		if (o instanceof GameAttributeBlockChange) {
//			GameAttributeBlockChange gab = (GameAttributeBlockChange)o;
//			return (gab.getId()==getId() && gab.from.equals(from) && gab.to.equals(to));
//		}
//		return false;
//	}
//	public boolean sameTypeOfChange(GameObjectChange other) {
//		return false;
//	}
}