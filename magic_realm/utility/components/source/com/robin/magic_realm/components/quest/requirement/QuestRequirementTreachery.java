package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class QuestRequirementTreachery extends QuestRequirement {
	public static final String REGEX_FILTER = "_regex";
	public static final String KILL_IN_COMBAT = "kill_in_combat";
	public static final String NATIVE_ONLY = "native_only";

	public QuestRequirementTreachery(GameObject go) {
		super(go);
	}

	@Override
	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		CombatWrapper combatCharacter = new CombatWrapper(character.getGameObject());
		ArrayList<String> ids = combatCharacter.getBetrayedIds();
		if (ids==null || ids.isEmpty()) {
			return false;
		}
		Pattern pattern = Pattern.compile(getRegExFilter());
		for (String id : combatCharacter.getBetrayedIds()) {
			GameObject victim = character.getGameData().getGameObject(id);
			if (getRegExFilter().isEmpty() || pattern.matcher(victim.getName()).find()) {
				if (killInCombat()) {
					boolean killedVictimInCombat = false;
					ArrayList<GameObject> kills = character.getKills(character.getCurrentDayKey());
					for (GameObject kill : kills) {
						if (id.matches(kill.getStringId())) {
							killedVictimInCombat = true;
							break;
						}
					}
					if (!killedVictimInCombat) {
						continue;
					}
				}
				
				CombatWrapper combatVictim = new CombatWrapper(victim);
				ArrayList<String> traitors = combatVictim.getBetrayedByIds();
				if (traitors == null || traitors.isEmpty()) {
					continue;
				}
				for (String traitorId : traitors) {
					if (traitorId.matches(character.getGameObject().getStringId())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public RequirementType getRequirementType() {
		return RequirementType.Treachery;
	}

	@Override
	protected String buildDescription() {
		String regex = getRegExFilter();
		StringBuilder sb = new StringBuilder();
		sb.append("Must commit treachery");
		if (nativeOnly() && (regex == null || regex.trim().length()==0)) {
			sb.append(" to a native");
		}
		if (regex != null && regex.trim().length() > 0) {
			sb.append(" to ");
			sb.append(regex);
		}
		if (killInCombat()) {
			sb.append(" and kill it");
		}
		sb.append(".");
		return sb.toString();
	}

	public boolean killInCombat() {
		return getBoolean(KILL_IN_COMBAT);
	}
	public boolean nativeOnly() {
		return getBoolean(NATIVE_ONLY);
	}
	public String getRegExFilter() {
		return getString(REGEX_FILTER);
	}
}