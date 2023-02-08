package com.robin.game.objects;

import java.util.ArrayList;

public class GameCommandDistribute extends GameCommand {

	public static String NAME = "Distribute";
	
	public GameCommandDistribute(GameSetup gameSetup) {
		super(gameSetup);
	}
	public String getTypeName() {
		return NAME;
	}
	protected String process(ArrayList<GameObject> allGameObjects) {
		GamePool fromPool = parent.getPool(from);
		GamePool toPool = parent.getPool(to);
		return distribute(fromPool,toPool);
	}
	private String distribute(GamePool fromPool,GamePool toPool) {
		int distributed = fromPool.distribute(toPool,count,transferType);
		return "Distributed:  "+distributed+":  "+from+"="+fromPool.size()+"   "+to+"="+toPool.size()+"\n";
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