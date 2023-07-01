package com.robin.magic_realm.components.effect;

import com.robin.game.objects.GameObject;

public class MoveSoundEffect implements ISpellEffect {

	@Override
	public void apply(SpellEffectContext context) {
		// Target is a sound chit, secondary target is the tile to move to
		GameObject soundChit = context.Target.getGameObject();
		GameObject targetTile = context.Spell.getSecondaryTarget();
		if (targetTile==null) targetTile = context.Spell.getCaster().getCurrentLocation().tile.getGameObject();
		targetTile.add(soundChit);
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
