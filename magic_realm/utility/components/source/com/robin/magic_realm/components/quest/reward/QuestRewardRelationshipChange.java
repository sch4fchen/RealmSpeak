package com.robin.magic_realm.components.quest.reward;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.GainType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardRelationshipChange extends QuestRewardRelationshipSet {
	public static final String GAIN_TYPE = "_gt";
	public static final String RELATIONSHIP_CHANGE = "_rc";
	
	public QuestRewardRelationshipChange(GameObject go) {
		super(go);
	}
	public void processReward(JFrame frame,CharacterWrapper character) {
		ArrayList<GameObject> representativeNativesToChange = getRepresentativeNatives(character);
		if (representativeNativesToChange==null) return;
		
		// Do the change
		int sign = getGainType()==GainType.Gain?1:-1;
		for(GameObject denizen:representativeNativesToChange) {
			character.changeRelationship(denizen,sign*getRelationshipChange());
		}
	}
	
	public String getDescription() {
		int val = getRelationshipChange();
		String group = getNativeGroup();
		if (group.equals("Clearing")) group = "all natives in the clearing.";
		else group = "the "+group;
		return (getGainType()==GainType.Gain?"Gain":"Lose")+" "+val+" level"+(val==1?"":"s")+" of friendliness with "+group;
	}
	
	public RewardType getRewardType() {
		return RewardType.RelationshipChange;
	}
	
	public GainType getGainType() {
		return GainType.valueOf(getString(GAIN_TYPE));
	}
	
	public int getRelationshipChange() {
		return getInt(RELATIONSHIP_CHANGE);
	}
}