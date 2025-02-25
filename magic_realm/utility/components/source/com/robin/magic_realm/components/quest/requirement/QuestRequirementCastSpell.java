package com.robin.magic_realm.components.quest.requirement;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.CharacterActionType;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class QuestRequirementCastSpell extends QuestRequirement {
	private static Logger logger = Logger.getLogger(QuestRequirementCastSpell.class.getName());

	public static final String REGEX_FILTER = "_regex";
	public static final String SCROLL_OR_BOOK = "_scroll_or_book";
	public static final String ARTIFACT = "_scroll_or_book";
	public static final String RING = "_ring";

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
		if (mustUseArtifact()) {
			GameObject spell = reqParams.objectList.get(0);
			GameObject spellOwner = spell.getHeldBy();
			if (!spellOwner.hasThisAttribute(Constants.ARTIFACT)) {
				logger.fine("Did not use an Artifact for casting the spell.");
				return false;
			}
		}
		if (mustUseRing()) {
			GameObject spell = reqParams.objectList.get(0);
			SpellWrapper spellWrapper = new SpellWrapper(spell);
			if (spellWrapper.getIncantationObject()==null || !spellWrapper.getIncantationObject().hasThisAttribute(Constants.RING)) {
				logger.fine("Did not use a Ring for casting the spell.");
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
		if (mustUseScrollOrBook()) {
			sb.append(" using a scroll or book");
		}
		if (mustUseArtifact()) {
			sb.append(" using an artifact");
		}
		if (mustUseScrollOrBook() && mustUseRing()) {
			sb.append(" and");
		}
		if (mustUseRing()) {
			sb.append(" using a ring");
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
	public boolean mustUseArtifact() {
		return getBoolean(ARTIFACT);
	}
	public boolean mustUseRing() {
		return getBoolean(RING);
	}
}