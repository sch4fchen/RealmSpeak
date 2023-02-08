package com.robin.game.objects;

import java.util.ArrayList;

public class GameCommandAlter extends GameCommand {

	public static String NAME = "Alter";
	
	public GameCommandAlter(GameSetup gameSetup) {
		super(gameSetup);
	}
	public String getTypeName() {
		return NAME;
	}
	protected String process(ArrayList<GameObject> allGameObjects) {
		return alter(allGameObjects);
	}
	public String alter(ArrayList<GameObject> allGameObjects) {
		// First find the targetObject to alter
		GameObject targetObjectToAlter = null;
		for (GameObject go : allGameObjects) {
			if (go.equalsId(targetObject.getId())) {
				targetObjectToAlter = go;
				break;
			}
		}
		if (targetObjectToAlter==null) return "Alter: Object not found!";
		if (getValue().matches("REMOVE")) {
			targetObjectToAlter.removeThisAttribute(getAttribute());
			return "Altered:  "+targetObjectToAlter+":  "+getAttribute()+" was removed";
		}
		targetObjectToAlter.setThisAttribute(getAttribute(),getValue());
		return "Altered:  "+targetObjectToAlter+":  "+getAttribute()+": "+getValue();
	}
	public boolean usesTargetObject() {
		return true;
	}
	public boolean usesAttribute() {
		return true;
	}
	public boolean usesValue() {
		return true;
	}
}