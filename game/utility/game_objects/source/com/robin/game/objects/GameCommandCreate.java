package com.robin.game.objects;

import java.util.ArrayList;

public class GameCommandCreate extends GameCommand {
	
	public static String NAME = "Create";
	
	public GameCommandCreate(GameSetup gameSetup) {
		super(gameSetup);
	}
	public String getTypeName() {
		return NAME;
	}
	protected String process(ArrayList<GameObject> allGameObjects) {
		return create(newPool);
	}
	public String create(String inPool) {
		parent.createPool(inPool);
		return "Created "+inPool+"\n";
	}
	public boolean usesNewPool() {
		return true;
	}
	public boolean isCreate() {
		return true;
	}
}