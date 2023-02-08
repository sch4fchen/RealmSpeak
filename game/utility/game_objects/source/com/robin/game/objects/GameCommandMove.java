package com.robin.game.objects;

import java.util.ArrayList;

public class GameCommandMove extends GameCommand {

	public static String NAME = "Move";
	
	public GameCommandMove(GameSetup gameSetup) {
		super(gameSetup);
	}
	public String getTypeName() {
		return NAME;
	}
	protected String process(ArrayList<GameObject> allGameObjects) {
		GamePool fromPool = parent.getPool(from);
		GamePool toPool = parent.getPool(to);
		return move(fromPool,toPool);
	}
	public String move(GamePool fromPool,GamePool toPool) {
		int moved = fromPool.move(toPool,count,transferType);
		return "Moved:  "+moved+":  "+from+"="+fromPool.size()+"   "+to+"="+toPool.size()+"\n";
	}
	public boolean usesFrom() {
		return true;
	}
	public boolean usesTo() {
		return true;
	}
	public boolean usesCount() {
		return true;
	}
	public boolean usesTransferType() {
		return true;
	}
}