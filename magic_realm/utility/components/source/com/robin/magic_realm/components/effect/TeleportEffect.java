package com.robin.magic_realm.components.effect;

import com.robin.magic_realm.components.utility.SpellUtility;

public class TeleportEffect implements ISpellEffect {
	SpellUtility.TeleportType _teleportType;
	
	public TeleportEffect(SpellUtility.TeleportType type){
		_teleportType = type;
	}
	
	@Override
	public void apply(SpellEffectContext context) {
		SpellUtility.doTeleport(context.Parent,
				context.Spell.getGameObject().getName(),
				context.getCharacterTarget(),
				_teleportType);
	}

	@Override
	public void unapply(SpellEffectContext context) {
	}

}
