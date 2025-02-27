package com.robin.magic_realm.components.quest.requirement;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementHire extends QuestRequirement {
	
	public static final String REGEX_FILTER = "_regex";
	public static final String NUMBER_OF_HIRELINGS = "_number_of_hirelings";
	
	public QuestRequirementHire(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {		
		if (reqParams.actionType != CharacterActionType.Hire) {
			return false;
		}
		
		int hiredUnderlings = 0;
		String regex = getRegExFilter();
		Pattern pattern = regex==null || regex.trim().length()==0?null:Pattern.compile(regex);
		for (GameObject hireling : reqParams.objectList) {
			if (pattern==null || pattern.matcher(hireling.getName()).find()) {
				hiredUnderlings++;
			}
		}
		
		return hiredUnderlings>0 && hiredUnderlings>=getNumberOfHirelings();
	}
	
	protected String buildDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Character must hire");
		if (getNumberOfHirelings()!=0) {
			sb.append(" "+getNumberOfHirelings());
		}
		if (getRegExFilter()!=null && !getRegExFilter().isEmpty()) {
			sb.append(" "+getRegExFilter()+"s");
		}
		else {
			sb.append(" underlings");
		}
		sb.append(".");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.Hire;
	}
	
	public String getRegExFilter() {
		return getString(REGEX_FILTER);
	}
	
	public int getNumberOfHirelings() {
		return getInt(NUMBER_OF_HIRELINGS);
	}
}