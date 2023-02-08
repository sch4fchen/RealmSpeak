package com.robin.hexmap;

import java.util.ArrayList;
import java.util.Collection;

public class MoveRule {

	private Object userData;
	private float moveLeft;
	private Collection<String> rules;
	private boolean canMoveBoatless;
	
	public MoveRule(Object userData,float moveLeft) {
		this.userData = userData==null?"":userData;
		this.moveLeft = moveLeft;
		this.rules = new ArrayList<>();
		canMoveBoatless = false;
	}
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	public Object getUserData() {
		return userData;
	}
	public float getMoveLeft() {
		return moveLeft;
	}
	public String getMoveLeftString() {
		return String.valueOf(moveLeft);
	}
	public void addRule(String rule) {
		if (!rules.contains(rule)) {
			rules.add(rule);
		}
	}
	public boolean hasRule(String rule) {
		return rules.contains(rule);
	}
	public String toString() {
		return userData.toString().substring(0,1)+moveLeft;
	}
	public void setCanMoveBoatless(boolean val) {
		canMoveBoatless = val;
	}
	public boolean getCanMoveBoatless() {
		return canMoveBoatless;
	}
}