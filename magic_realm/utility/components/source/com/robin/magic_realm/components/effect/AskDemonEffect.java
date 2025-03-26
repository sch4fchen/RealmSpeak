package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.Constants;

public class AskDemonEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		// The target (demon) is actually irrelevant here: we use only the extra identifier
		String string = context.Spell.getExtraIdentifier();
		
		if (string.matches(Constants.ASK_DEMON_PEEK_TILE)) {
			GameObject tile = context.Spell.getSecondaryTarget();
			context.Game.addInformation(context.Spell.getCaster().getPlayerName(),tile.getStringId());
			return;
		}
		
		if (string.matches(Constants.ASK_DEMON_PEEK_BOX)) {
			GameObject box = context.Spell.getSecondaryTarget();
			context.Game.addInformation(context.Spell.getCaster().getPlayerName(),box.getStringId());
			return;
		}
		
		if (string.matches(Constants.ASK_DEMON_PEEK_QUEST_CARDS)) {
			GameObject player = context.Spell.getSecondaryTarget();
			context.Game.addInformation(context.Spell.getCaster().getPlayerName(),player.getStringId());
			return;
		}
		
		int index = string.indexOf(Constants.DEMON_Q_DELIM);
		String playerName = string.substring(0,index);
		String question = string.substring(index+Constants.DEMON_Q_DELIM.length());
		context.Game.addQuestion(context.Spell.getCaster().getPlayerName(),playerName,question);
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
