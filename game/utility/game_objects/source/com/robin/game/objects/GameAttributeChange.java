package com.robin.game.objects;

/**
 * This class will encapsulate a single change that needs to happen to the GameData
 */
public class GameAttributeChange extends GameObjectChange {
	
	private String blockName;
	private String attributeName;
	private String newValue;
	
	public GameAttributeChange(GameObject go) {
		super(go);
		blockName = null;
		attributeName = null;
		newValue = null;
	}
//	public boolean equals(Object o1) {
//		if (o1 instanceof GameAttributeChange) {
//			GameAttributeChange other = (GameAttributeChange)o1;
//			return (other.getId()==getId()
//					&& other.blockName.equals(blockName)
//					&& other.attributeName.equals(attributeName)
//					&& nullEquals(other.newValue,newValue));
//		}
//		return false;
//	}
//	public boolean sameTypeOfChange(GameObjectChange o1) {
//		if (o1 instanceof GameAttributeChange) {
//			GameAttributeChange other = (GameAttributeChange)o1;
//			return (other.getId()==getId()
//					&& other.blockName.equals(blockName)
//					&& other.attributeName.equals(attributeName));
//		}
//		return false;
//	}
	public void setAttribute(String blockName,String attributeName,String newValue) {
		this.blockName = blockName;
		this.attributeName = attributeName;
		this.newValue = newValue;
	}
	public void deleteAttribute(String inBlockName,String inAttributeName) {
		this.blockName = inBlockName;
		this.attributeName = inAttributeName;
		this.newValue = null;
	}
	public void deleteAttributeBlock(String inBlockName) {
		this.blockName = inBlockName;
		this.attributeName = null;
		this.newValue = null;
	}
	protected void applyChange(GameData data,GameObject go) {
		if (blockName!=null) {
			if (attributeName!=null) {
				if (newValue!=null) {
					go._setAttribute(blockName,attributeName,newValue);
				}
				else {
					go._removeAttribute(blockName,attributeName);
				}
			}
			else {
				go._removeAttributeBlock(blockName);
			}
		}
	}
	protected void rebuildChange(GameData data,GameObject go) {
		if (blockName!=null) {
			if (attributeName!=null) {
				if (newValue!=null) {
					go.setAttribute(blockName,attributeName,newValue);
				}
				else {
					go.removeAttribute(blockName,attributeName);
				}
			}
			else {
				go.removeAttributeBlock(blockName);
			}
		}
	}
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		if (blockName!=null) {
			if (attributeName!=null) {
				if (newValue!=null) {
					sb.append(":  Sets ["+blockName+":"+attributeName+"] = "+newValue);
				}
				else {
					sb.append(":  Deletes ["+blockName+":"+attributeName+"]");
				}
			}
			else {
				sb.append(":  Deletes ["+blockName+"]");
			}
		}
		else {
			sb.append(":  Empty AttributeChange action");
		}
		return sb.toString();
	}
}