package com.robin.game.objects;

import java.util.ArrayList;

public class GameCommandAddTo extends GameCommand {

	public static String NAME = "Add";
	
	public GameCommandAddTo(GameSetup gameSetup) {
		super(gameSetup);
	}
	public String getTypeName() {
		return NAME;
	}
	protected String process(ArrayList<GameObject> allGameObjects) {
		GamePool fromPool = parent.getPool(from);
		return addTo(fromPool,allGameObjects);
	}
	public String addTo(GamePool fromPool,ArrayList<GameObject> allGameObjects) {
		// First find the targetObject copy
		GameObject targetObjectCopy = null;
		for (GameObject copyObject : allGameObjects) {
			if (copyObject.equalsId(targetObject.getId())) {
				targetObjectCopy = copyObject;
				break;
			}
		}
		
		// Now, populate the contains of the copy
		ArrayList<GameObject> picked = fromPool.pick(count,transferType);
		for (GameObject obj : picked) {
			targetObjectCopy.add(obj);
		}
		return "Picked:  "+picked.size()+":  "+from+"="+fromPool.size()+"\n";
	}
	public boolean usesFrom() {
		return true;
	}
	public boolean usesTargetObject() {
		return true;
	}
	public boolean usesCount() {
		return true;
	}
	public boolean usesTransferType() {
		return true;
	}
}