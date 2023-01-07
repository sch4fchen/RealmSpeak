/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
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