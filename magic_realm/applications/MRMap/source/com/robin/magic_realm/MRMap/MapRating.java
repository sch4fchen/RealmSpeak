package com.robin.magic_realm.MRMap;

import java.util.ArrayList;

import com.robin.game.objects.*;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.utility.ClearingUtility;
import com.robin.magic_realm.components.utility.RealmObjectMaster;

public class MapRating {
	public static int getMapRating(GameData data) {
		/*
		 * For every valley tile, count the number of accessible tiles from the highest connected clearing,
		 * without crossing a cave clearing, any hidden path or secret passage.  The lowest count of tiles
		 * for any one of those, will be the rating for the entire map.
		 */
		
		ClearingUtility.initAdjacentTiles(data);
		
		ArrayList<GameObject> tiles = RealmObjectMaster.getRealmObjectMaster(data).getTileObjects();
		GamePool pool = new GamePool(tiles);
		ArrayList<GameObject> valleyTiles = pool.find("tile,tile_type=V");
		int rating = Integer.MAX_VALUE;
		for (GameObject go:valleyTiles) {
			rating = Math.min(rating,getTileRating(go));
		}
		
		return rating;
	}
	private static int getTileRating(GameObject go) {
		TileComponent tile = (TileComponent)RealmComponent.getRealmComponent(go);
		int clearingNum = ClearingUtility.recommendedClearing(go);
		ArrayList<ClearingDetail> search = new ArrayList<>();
		search.add(tile.getClearing(clearingNum)); // seed clearing
		
		// First, find ALL connected clearings to the start point
		ArrayList<ClearingDetail> found = new ArrayList<>();
		while(!search.isEmpty()) {
			ArrayList<ClearingDetail> next = new ArrayList<>();
			for (ClearingDetail clearing:search) {
				for (PathDetail path:clearing.getAllConnectedPaths()) {
					if (path.isHidden() || path.isSecret()) continue;
					ClearingDetail otherEnd = path.findConnection(clearing);
					if (otherEnd==null || otherEnd.isCave() || otherEnd.isWater() || otherEnd.isEdge() || found.contains(otherEnd)) continue;
					
					found.add(otherEnd);
					next.add(otherEnd);
				}
			}
			search = next;
		}
		
		// Now, count the number of individual tiles involved
		ArrayList<TileComponent> connectedTiles = new ArrayList<TileComponent>();
		for (ClearingDetail clearing:found) {
			if (!connectedTiles.contains(clearing.getParent())) {
				connectedTiles.add(clearing.getParent());
			}
		}

		return connectedTiles.size()-1;		// don't count the original tile
	}
}