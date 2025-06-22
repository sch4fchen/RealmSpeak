package com.robin.magic_realm.components.attribute;

import java.util.ArrayList;

import com.robin.game.objects.GameObject;

public class Score {
	private int recordedPoints;
	private int ownedPoints;
	private int mult;
	private int vps;
	private boolean noPenalty = false;
	private ArrayList<GameObject> scoringGameObjects;
	public Score(int recordedPoints,int ownedPoints,int mult,int vps,ArrayList<GameObject> scoringGameObjects, boolean noPenalty) {
		this.recordedPoints = recordedPoints;
		this.ownedPoints = ownedPoints;
		this.mult = mult;
		this.vps = vps;
		this.scoringGameObjects = scoringGameObjects;
		this.noPenalty = noPenalty;
	}
	public Score(int recordedPoints,int ownedPoints,int mult,int vps,ArrayList<GameObject> scoringGameObjects) {
		this.recordedPoints = recordedPoints;
		this.ownedPoints = ownedPoints;
		this.mult = mult;
		this.vps = vps;
		this.scoringGameObjects = scoringGameObjects;
	}
	public ArrayList<GameObject> getScoringGameObjects() {
		return scoringGameObjects;
	}
	public int getRecordedPoints() {
		return recordedPoints;
	}
	public int getOwnedPoints() {
		return ownedPoints;
	}
	public int getPoints() {
		return recordedPoints+ownedPoints;
	}
	public int getScore() {
		int score = getPoints() - getRequired();
		if (noPenalty) {
			return score;
		}
		return score<0?score*3:score;
	}
	public boolean hasPenalty() {
		return getScore()<0;
	}
	public int getMultiplier() {
		return mult;
	}
	public int getAssignedVictoryPoints() {
		return vps;
	}
	public int getRequired() {
		return mult*vps;
	}
	public int getBasicScore() {
		double val = (double)getScore()/(double)getMultiplier();
		return (Double.valueOf(Math.floor(val))).intValue();
	}
	public int getBonusScore() {
		if (noPenalty) { 
			return 0;
		}
		return vps>0?(getBasicScore()*vps):0;
	}
	public int getTotalScore() {
		return getBasicScore()+getBonusScore();
	}
	public int getEarnedVictoryPoints(boolean restrictToAssigned) {
		return getEarnedVictoryPoints(restrictToAssigned,false);
	}
	public int getEarnedVictoryPoints(boolean restrictToAssigned,boolean excludeStartingWorth) {
		double p = getPoints();
		if (excludeStartingWorth) {
			p -= ownedPoints;
		}
		double val = p/getMultiplier();
		int earnedVps = (Double.valueOf(Math.floor(val))).intValue();
		if (restrictToAssigned) {
			if (vps>0) {
				earnedVps = Math.min(earnedVps,vps); // only get credit for the number of points you've assigned
			}
			else {
				earnedVps = 0; // only those categories you've assigned count
			}
		}
		return earnedVps;
	}
	public static void printResult(int score,int mult) {
		double val = (double)score/(double)mult;
		System.out.println("("+score+"/"+mult+")="+val);
		System.out.println("Math.floor("+score+"/"+mult+")="+Double.valueOf(Math.floor(val)).intValue());
		System.out.println("Math.ceil("+score+"/"+mult+")="+Double.valueOf(Math.ceil(val)).intValue());
		System.out.println("Math.round("+score+"/"+mult+")="+Double.valueOf(Math.round(val)).intValue());
	}
	public static void main(String[] args) {
		printResult(-1,30);
		printResult(-27,30);
		printResult(-33,30);
		printResult(27,30);
		printResult(33,30);
	}
}