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
		for (GameObject go : allGameObjects) {
			if (go.equalsId(targetObject.getId())) {
				if (getValue().matches("REMOVE")) {
					go.removeThisAttribute(getAttribute());
					return "Altered:  "+go+":  "+getAttribute()+" was removed";
				}
				go.setThisAttribute(getAttribute(),getValue());
				return "Altered:  "+go+":  "+getAttribute()+": "+getValue();
			}
		}
		return "Alter: Object not found!";
		
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