package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.attribute.Strength;
import com.robin.magic_realm.components.quest.VulnerabilityType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementChit extends QuestRequirement {	
	public static final String TYPE = "_type";
	public static final String AMOUNT = "_amount";
	public static final String STRENGTH = "_strength";
	public static final String SPEED = "_speed";
	public static final String MAGIC_COLOR = "_magic_color";
	public static final String MAGIC_TYPE = "_magic_type";
	public static final String ONLY_ACTIVE = "_only_active";
	public static final String NOT_FATIGUED = "_not_fatigued";
	public static final String NOT_WOUNDED = "_not_wounded";
	
	public enum ChitType {
		Any,
		Move,
		Fight,
		Magic,
		Fly
	}
		
	public QuestRequirementChit(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		ArrayList<CharacterActionChitComponent> chitsToCheck = character.getAllChits();
		ArrayList<CharacterActionChitComponent> chits = new ArrayList<>();
		switch(getType()) {
		case Move:
			for (CharacterActionChitComponent chit : chitsToCheck) {
				if (chit.isMove()) chits.add(chit);
			}
			break;
		case Fight:
			for (CharacterActionChitComponent chit : chitsToCheck) {
				if (chit.isFight()) chits.add(chit);
			}
			break;
		case Magic:
			for (CharacterActionChitComponent chit : chitsToCheck) {
				if (chit.isMagic() && chit.getMagicNumber()==getMagicType() && (getMagicColor().matches("Any") || chit.getColorMagic().getColorName().matches(getMagicColor()))) {
					chits.add(chit);
				}
			}
			break;
		case Fly:
			for (CharacterActionChitComponent chit : chitsToCheck) {
				if (chit.isFly()) chits.add(chit);
			}
			break;
		case Any:
		default:
			chits.addAll(chitsToCheck);
			break;
		}
		chitsToCheck.clear();
		chitsToCheck.addAll(chits);
		chits.clear();
		for (CharacterActionChitComponent chit : chitsToCheck) {
				if (getStrength() != VulnerabilityType.Any && chit.getStrength().weakerTo(new Strength(getStrength().toString()))) continue;
				if (getSpeed() != 0 && chit.getSpeed().getNum()>getSpeed()) continue;
				if (onlyActive() && !chit.isActive()) continue;
				if (notFatigued() && chit.isFatigued()) continue;
				if (notWounded() && chit.isWounded()) continue;
				chits.add(chit);
		}
		
		if (chits.size()>=getAmount()) {
			return true;
		}
		return false;
	}

	protected String buildDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Must have "+getAmount()+" ");
		if (getType() != ChitType.Any) {
			sb.append(getType()+" ");
		}
		sb.append("chit(s).");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Chit;
	}
	public ChitType getType() {
		return ChitType.valueOf(getString(TYPE));
	}
	private int getAmount() {
		return getInt(AMOUNT);
	}
	private VulnerabilityType getStrength() {
		return VulnerabilityType.valueOf(getString(STRENGTH));
	}
	private int getSpeed() {
		return getInt(SPEED);
	}
	private String getMagicColor() {
		return getString(MAGIC_COLOR);
	}
	private int getMagicType() {
		return getInt(MAGIC_TYPE);
	}
	private boolean onlyActive() {
		return getBoolean(ONLY_ACTIVE);
	}
	private boolean notFatigued() {
		return getBoolean(NOT_FATIGUED);
	}
	private boolean notWounded() {
		return getBoolean(NOT_WOUNDED);
	}
}