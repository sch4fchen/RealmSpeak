package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class RemovePotentialBlocksEffect implements ISpellEffect {
	
	public RemovePotentialBlocksEffect(){
	}
	
	@Override
	public void apply(SpellEffectContext context) {
		ClearingDetail cl = context.Target.getCurrentLocation().clearing;
		if (cl==null) return;
		for (RealmComponent rc : cl.getClearingComponents()) {
			if (!rc.isAnyLeader()) continue;
			CharacterWrapper leader = new CharacterWrapper(rc.getGameObject());
			if (leader.hasBlockDecision(context.Target.getGameObject())) {
				leader.removeBlockDecision(context.Target.getGameObject());
			}
		}
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
