package com.robin.magic_realm.components.effect;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.utility.Constants;

public class FrozenWaterEffect implements ISpellEffect {	
	
	@Override
	public void apply(SpellEffectContext context) {
		TileComponent targetTile = context.getTileTarget();
		if(!targetTile.getGameObject().hasThisAttribute(Constants.FROZEN_WATER)){
			targetTile.getGameObject().setThisAttribute(Constants.FROZEN_WATER);
		}
		for (TileComponent tile : targetTile.getAllAdjacentTiles()) {
			if(!tile.getGameObject().hasThisAttribute(Constants.FROZEN_WATER)){
				tile.getGameObject().setThisAttribute(Constants.FROZEN_WATER);
			}
		}
	}

	@Override
	public void unapply(SpellEffectContext context) {
		TileComponent targetTile = context.getTileTarget();
		if(targetTile.getGameObject().hasThisAttribute(Constants.FROZEN_WATER)){
			targetTile.getGameObject().removeThisAttribute(Constants.FROZEN_WATER);
		}
		for (TileComponent tile : targetTile.getAllAdjacentTiles()) {
			if(tile.getGameObject().hasThisAttribute(Constants.FROZEN_WATER)){
				tile.getGameObject().removeThisAttribute(Constants.FROZEN_WATER);
			}
		}
	}

}
