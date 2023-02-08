package com.robin.game.objects;

import java.util.ArrayList;

public class GameCommandExtract extends GameCommand {

	public static String NAME = "Extract";
	
	public GameCommandExtract(GameSetup gameSetup) {
		super(gameSetup);
	}
	public String getTypeName() {
		return NAME;
	}
	protected String process(ArrayList<GameObject> allGameObjects) {
		GamePool fromPool = parent.getPool(from);
		GamePool toPool = parent.getPool(to);
		return extract(fromPool,toPool);
	}
	public String extract(GamePool fromPool,GamePool toPool) {
		ArrayList<GameObject> extracted = fromPool.extract(keyVals,count);
		toPool.addAll(extracted);
		return "Extracted:  "+extracted.size()+":  "+from+"="+fromPool.size()+"   "+to+"="+toPool.size()+"\n";
	}
	public boolean usesFrom() {
		return true;
	}
	public boolean usesTo() {
		return true;
	}
	public boolean usesKeyVals() {
		return true;
	}
	public boolean isExtract() {
		return true;
	}
}