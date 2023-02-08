package com.robin.magic_realm.components.quest.reward;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.AttributeType;
import com.robin.magic_realm.components.quest.GainType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRewardAttribute extends QuestReward {
	public static final String ATTRIBUTE_TYPE = "_at";
	public static final String GAIN_TYPE = "_gt";
	public static final String ATTRIBUTE_CHANGE = "_ac";
	
	public QuestRewardAttribute(GameObject go) {
		super(go);
	}
	
	public void processReward(JFrame frame,CharacterWrapper character) {
		int val = getAttributeChange();
		val = getGainType()==GainType.Gain?val:-val;
		switch(getAttributeType()) {
			case Fame:
				character.addFame(val,false);
				break;
			case Notoriety:
				character.addNotoriety(val);
				break;
			case Gold:
				character.addGold(val);
				break;
			default:
				break;
		}
	}

	public String getDescription() {
		int val = getAttributeChange();
		AttributeType type = getAttributeType();
		return (getGainType()==GainType.Gain?"Gain":"Lose")+" "+val+" "+type.toString();
	}
	
	public RewardType getRewardType() {
		return RewardType.Attribute;
	}
	
	public void setAttributeType(AttributeType attributeType) {
		setString(ATTRIBUTE_TYPE,attributeType.toString());
	}
	
	public AttributeType getAttributeType() {
		return AttributeType.valueOf(getString(ATTRIBUTE_TYPE));
	}
	
	public void setGainType(GainType gainType) {
		setString(GAIN_TYPE,gainType.toString());
	}
	
	public GainType getGainType() {
		return GainType.valueOf(getString(GAIN_TYPE));
	}
	
	public void setAttributeChange(int val) {
		setInt(ATTRIBUTE_CHANGE,val);
	}
	
	public int getAttributeChange() {
		return getInt(ATTRIBUTE_CHANGE);
	}
}