package com.robin.magic_realm.RealmBattle.targeting;

import java.util.Collection;

import com.robin.general.util.StringUtilities;
import com.robin.magic_realm.RealmBattle.BattleModel;
import com.robin.magic_realm.RealmBattle.CombatFrame;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class SpellTargetingMagic extends SpellTargetingMultiple {

	public SpellTargetingMagic(CombatFrame combatFrame, SpellWrapper spell) {
		super(combatFrame, spell);
	}

	public boolean populate(BattleModel battleModel,RealmComponent activeParticipant) {
		// Assume that activeParticipant IS character
		CharacterWrapper character = new CharacterWrapper(activeParticipant.getGameObject());
		String targetType = spell.getGameObject().getThisAttribute("target");
		int paren1 = targetType.indexOf("(");
		int paren2 = targetType.indexOf(")");
		boolean nonMagicChangedChitsOnly = spell.getGameObject().hasThisAttribute(Constants.TARGETS_NON_MAGIC_CHANGED_CHITS);
		if (paren1>0 && paren2>paren1) {
			String chitList = targetType.substring(paren1+1,paren2);
			Collection<CharacterActionChitComponent> allChits = character.getActiveMagicChits();
			Collection<String> types = null;
			if (!"all".equals(chitList)) {
				types = StringUtilities.stringToCollection(chitList,",");
			}
			for (CharacterActionChitComponent chit : allChits) {
				if (types==null || types.contains(chit.getMagicType())) {
					if (!nonMagicChangedChitsOnly
							|| (!chit.getGameObject().hasThisAttribute(Constants.MAGIC_CHANGE) && !chit.getGameObject().hasThisAttribute(Constants.MAGIC_CHANGE_BY_FREE_SPELL)))
					gameObjects.add(chit.getGameObject());
				}
			}
		}
		return true;
	}
}