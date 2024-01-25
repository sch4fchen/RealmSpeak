package com.robin.magic_realm.components.effect;

import java.util.ArrayList;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.Constants;

public class ApplyDieModEffect implements ISpellEffect {
	String _dieModString;
	
	public ApplyDieModEffect(){
	}
	
	@Override
	public void apply(SpellEffectContext context) {
		ArrayList<String> dieMods = getDieMods(context);
		for (String dieMod : dieMods) {
			context.Target.getGameObject().addThisAttributeListItem(Constants.DIEMOD,dieMod);
		}
	}

	@Override
	public void unapply(SpellEffectContext context) {
		ArrayList<String> dieMods = getDieMods(context);
		for (String dieMod : dieMods) {
			if(context.Target.getGameObject().hasThisAttributeListItem(Constants.DIEMOD,dieMod)) {
				context.Target.getGameObject().removeThisAttributeListItem(Constants.DIEMOD,dieMod);
			}
		}
	}

	private static ArrayList<String> getDieMods(SpellEffectContext context) {
		GameObject spell = context.Spell.getGameObject();
		if (spell.hasAttributeBlock(Constants.DIEMOD)) {
			return spell.getAttributeList(Constants.DIEMOD,Constants.DIEMOD);
		}
		return new ArrayList<String>();
	}
}
