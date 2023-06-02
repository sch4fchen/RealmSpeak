package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;

public class MigrationEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		GameObject chit1 = context.Target.getGameObject();
		GameObject chit2 = context.getGameData().getGameObject(context.Spell.getExtraIdentifier());
		GameObject tile1 = chit1.getHeldBy();
		GameObject tile2 = chit2.getHeldBy();
		tile1.add(chit2);
		tile2.add(chit1);
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
