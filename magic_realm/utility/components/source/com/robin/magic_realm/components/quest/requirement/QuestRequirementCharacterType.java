package com.robin.magic_realm.components.quest.requirement;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.quest.TransmorphType;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementCharacterType extends QuestRequirement {

	public static final String REQUIRES_TRANSMORPHED = "_req_transmorphed";
	public static final String TYPE = "_type";
	public static final String REGEX_FILTER = "_regex";
	
	public QuestRequirementCharacterType(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		RealmComponent rc = RealmComponent.getRealmComponent(character.getGameObject());
		GameObject transmorph = character.getTransmorph();
		RealmComponent transmorphRc = null;
		if (transmorph != null) {
			transmorphRc = RealmComponent.getRealmComponent(transmorph);
		}
		if (mustBeTransmorphed() && transmorph == null) {
			return false;
		}
		switch(getType()) {
			case Denizen:
				if (!rc.isDenizen() && transmorph != null && !transmorphRc.isDenizen()) return false;
				break;
			case Native:
				if (!rc.isNative() && transmorph != null && !transmorphRc.isNative()) return false;
				break;
			case Monster:
				if (!rc.isMonster() && transmorph != null && !transmorphRc.isMonster()) return false;
				break;
			case Animal:
				if (transmorph != null && !transmorphRc.isTransformAnimal()) return false;
				break;
			case Mist:
				if (!character.isMistLike()) return false;
				break;
			case Statue:
				if (!character.isStatue()) return false;
				break;
			case Any:
			default:
				break;
		}
		if (getRegExFilter() != null && !getRegExFilter().isEmpty()) {
			Pattern pattern = Pattern.compile(getRegExFilter());
			return pattern.matcher(character.getName()).find() || (transmorph != null && pattern.matcher(transmorph.getName()).find());
		}
		return true;
	}

	protected String buildDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Character must be ");
		if (getType() != TransmorphType.Any) {
			sb.append("a "+getType());
		}
		if (getRegExFilter() != null && !getRegExFilter().isEmpty()) {
			if (getType() != TransmorphType.Any) {
				sb.append(" and ");
			}
			sb.append("match " +getRegExFilter());
		}
		sb.append(".");
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.CharacterType;
	}
	
	private boolean mustBeTransmorphed() {
		return getBoolean(REQUIRES_TRANSMORPHED);
	}
	
	private TransmorphType getType() {
		return TransmorphType.valueOf(getString(TYPE));
	}
	
	public String getRegExFilter() {
		return getString(REGEX_FILTER);
	}
}