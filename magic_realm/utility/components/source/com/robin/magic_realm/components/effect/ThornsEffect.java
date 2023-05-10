package com.robin.magic_realm.components.effect;

import java.util.ArrayList;

import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TileComponent;

public class ThornsEffect implements ISpellEffect {
	public ThornsEffect(){
	}
	
	@Override
	public void apply(SpellEffectContext context) {
		String clearings = context.Spell.getExtraIdentifier();
		String[] clearingStrings = clearings.split("_");
		String clearing1 = clearingStrings[0];
		String clearing2 = clearingStrings[1];
		ArrayList<RealmComponent> targets = context.Spell.getTargets();
		TileComponent tile1 = (TileComponent)targets.get(0);
		TileComponent tile2 = (TileComponent)targets.get(1);
		
		TileComponent.addThorns(tile1,clearing1,tile2,clearing2);
	}

	@Override
	public void unapply(SpellEffectContext context) {
		String clearings = context.Spell.getExtraIdentifier();
		String[] clearingStrings = clearings.split("_");
		String clearing1 = clearingStrings[0];
		String clearing2 = clearingStrings[1];
		ArrayList<RealmComponent> targets = context.Spell.getTargets();
		TileComponent tile1 = (TileComponent)targets.get(0);
		TileComponent tile2 = (TileComponent)targets.get(1);
		
		TileComponent.removeThorns(tile1,clearing1,tile2,clearing2);
	}

}
