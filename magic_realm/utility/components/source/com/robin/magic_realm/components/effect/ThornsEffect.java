package com.robin.magic_realm.components.effect;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

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
		
		moveCharactersBetweenClearings(tile1,Integer.valueOf(clearing1),tile2,Integer.valueOf(clearing2));
		moveCharactersBetweenClearings(tile2,Integer.valueOf(clearing2),tile1,Integer.valueOf(clearing1));
		
		TileComponent.addThorns(tile1,clearing1,tile2,clearing2);
		
		RealmLogging.logMessage(context.Spell.getCaster().getGameObject().getName(),"Casts Thorns on roadway between clearings "+tile1+" "+clearing1+" and "+tile2+" "+clearing2+".");
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

	private static void moveCharactersBetweenClearings(TileComponent tile1, int clearing1, TileComponent tile2, int clearing2) {
		for (RealmComponent rc : tile1.getRealmComponentsBetweenClearing(clearing1)) {
			if (!rc.isCharacter()) continue;
			CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
			TileLocation loc = character.getCurrentLocation();
			TileLocation other = loc.getOther();
			if (loc.isBetweenClearings()
					&& loc.tile!=null && loc.tile.equals(tile1) && loc.clearing!=null && loc.clearing.equals(tile1.getClearing(clearing1))
					&& other!=null && other.tile.equals(tile2) && other.clearing!=null && other.clearing.equals(tile2.getClearing(clearing2))) {
				character.moveToLocation(new JFrame(), new TileLocation(tile2,tile2.getClearing(clearing2),false));
			}
			if (loc.isBetweenClearings()
					&& loc.tile!=null && loc.tile.equals(tile2) && loc.clearing!=null && loc.clearing.equals(tile2.getClearing(clearing2))
					&& other!=null && other.tile.equals(tile1) && other.clearing!=null && other.clearing.equals(tile1.getClearing(clearing1))) {
				character.moveToLocation(null, new TileLocation(tile1,tile1.getClearing(clearing1),false));
			}
		}
	}
}
