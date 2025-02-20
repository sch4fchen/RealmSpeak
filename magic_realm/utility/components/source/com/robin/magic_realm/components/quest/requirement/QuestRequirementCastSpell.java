package com.robin.magic_realm.components.quest.requirement;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementCastSpell extends QuestRequirement {
	private static Logger logger = Logger.getLogger(QuestRequirementCastSpell.class.getName());

	public static final String REGEX_FILTER = "_regex";
	public static final String SCROLL_OR_BOOK = "_scroll_or_book";

	public QuestRequirementCastSpell(GameObject go) {
		super(go);
	}

	@Override
	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		if (reqParams.actionType != CharacterActionType.CastSpell) {
			return false;
		}

		String regex = getRegExFilter();
		if (regex != null && regex.trim().length() > 0) {
			Pattern pattern = Pattern.compile(regex);
			if (reqParams.objectList.size()!=1) {
				logger.fine("Nothing to match to regex /"+regex+"/");
				return false;
			}
			GameObject go = reqParams.objectList.get(0);
			if (!pattern.matcher(go.getName()).find()) {
				logger.fine(go.getName()+" does not match regex /"+regex+"/");
				return false;
			}
		}
		
		if (mustUseScrollOrBook()) {
			GameObject spell = reqParams.objectList.get(0);
			GameObject spellOwner = spell.getHeldBy();
			if (!spellOwner.hasThisAttribute(Constants.SCROLL) && !spellOwner.hasThisAttribute(Constants.BOOK)) {
				logger.fine("Did not use scroll or book for casting the spell.");
				return false;
			}
		}

		return true;
	}

	@Override
	public RequirementType getRequirementType() {
		return RequirementType.CastSpell;
	}

	@Override
	protected String buildDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Must cast a spell");
		String regex = getRegExFilter();
		if (regex != null && regex.trim().length() > 0) {
			sb.append(" matching /");
			sb.append(regex);
			sb.append("/");
		}
		sb.append(".");
		return sb.toString();
	}

	public String getRegExFilter() {
		return getString(REGEX_FILTER);
	}
	
	public boolean mustUseScrollOrBook() {
		return getBoolean(SCROLL_OR_BOOK);
	}
}