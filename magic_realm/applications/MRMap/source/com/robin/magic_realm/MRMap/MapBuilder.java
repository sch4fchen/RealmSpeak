package com.robin.magic_realm.MRMap;

import java.awt.Point;
import java.util.*;

import com.robin.game.objects.*;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLoader;
import com.robin.magic_realm.components.utility.RealmObjectMaster;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.map.Tile;

public class MapBuilder {
	
	public static ArrayList<Tile> startTileList(GameData data,Collection keyVals) {
		ArrayList<Tile> tiles = new ArrayList<>();
		Collection<GameObject> c = RealmObjectMaster.getRealmObjectMaster(data).getTileObjects();
		for (GameObject obj : c) {
			tiles.add(new Tile(obj));
		}
		return tiles;
	}
	public static Tile findAnchorTile(Collection<Tile> tiles) {
		// Find the Borderland tile, and start it at position 0,0 with a random rotation
		for (Tile tile : tiles) {
			if (tile.getGameObject().hasThisAttribute(Constants.ANCHOR_TILE)) {
				return tile;
			}
		}
		throw new IllegalStateException("Borderland or other staring tile is missing from tiles!!");
	}

	public static boolean autoBuildMap(GameData data,Collection keyVals) {
		return autoBuildMap(data,keyVals,null);
	}
	public static boolean autoBuildMap(GameData data,Collection keyVals,MapProgressReportable reporter) {
		boolean autoBuildRiver = true;
		ArrayList<Tile> tiles = startTileList(data,keyVals);
		
		// Find the Borderland tile, and start it at position 0,0 with a random rotation
		Hashtable<Point, Tile> mapGrid = new Hashtable<>();
		Tile anchor = findAnchorTile(tiles);
		mapGrid.put(new Point(0,0),anchor);
		anchor.setMapPosition(new Point(0,0));
		anchor.setRotation(RandomNumber.getRandom(6));
		
		if (reporter!=null) {
			reporter.setProgress(1,tiles.size());
		}
		
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(data);
		
		// Cycle until the mapGrid has all the tiles
		while(mapGrid.size()<tiles.size()) {
			if (reporter!=null) {
				reporter.setProgress(mapGrid.size(),tiles.size());
			}
			// First, identify all connectable map placement locations
			//		- Have paths leading to them
			//		- Adjacent to at least two tiles (unless only one tile on map)
			ArrayList<Point> availableMapPositions = Tile.findAvailableMapPositions(mapGrid,anchor.getGameObject().getName(),autoBuildRiver,hostPrefs.hasPref(Constants.MAP_BUILDING_HILL_TILES));
			
			// Cycle through every available (unplaced) tile
			ArrayList<ArrayList<TileMappingPossibility>> allTileResults = new ArrayList<>();
			ArrayList<ArrayList<TileMappingPossibility>> tileResultsPrio1 = new ArrayList<>();
			ArrayList<ArrayList<TileMappingPossibility>> tileResultsPrio2 = new ArrayList<>();
			for (Tile tile : tiles) {
				// Only use unmapped tiles
				if (!mapGrid.contains(tile)) {
					ArrayList<TileMappingPossibility> tileResults = new ArrayList<>();					
					// Try the tile in every available position
					for (Point pos : availableMapPositions) {						
						// Try every rotation
						for (int rot=0;rot<6;rot++) {
							// Test the tile at pos, with rotation rot
							if (Tile.isMappingPossibility(mapGrid,tile,pos,rot,anchor.getGameObject().getName(),hostPrefs.hasPref(Constants.MAP_BUILDING_HILL_TILES))) {
								tileResults.add(new TileMappingPossibility(tile,pos,rot));
								if (hostPrefs.hasPref(Constants.MAP_BUILDING_INCREASED_PRIO_TILE_PLACEMENT) && Tile.isMappingNextToPrioritizedTile(mapGrid,tile,pos,rot)) {
									tileResults.add(new TileMappingPossibility(tile,pos,rot));
									tileResults.add(new TileMappingPossibility(tile,pos,rot));
								}
							}
						}
					}
					if (tileResults.size()>0) {
						// Adding the tile results in by tile prevents unfair weighting per tile
						if (tile.getGameObject().hasThisAttribute(Constants.MAP_BUILDING_PRIO)) {
							if (tile.getGameObject().getThisAttribute(Constants.MAP_BUILDING_PRIO).matches("1")) {
								tileResultsPrio1.add(tileResults);
							} else {
								tileResultsPrio2.add(tileResults);
							}
						} else {
							allTileResults.add(tileResults);
						}
					}
				}
			}
			if (allTileResults.size()>0 || tileResultsPrio1.size()>0 || tileResultsPrio2.size()>0) {
				// First, pick a random tile result set
				ArrayList<TileMappingPossibility> tileResults = null;
				if (tileResultsPrio1.size()>0) {
					tileResults = tileResultsPrio1.get(RandomNumber.getRandom(tileResultsPrio1.size()));
				} else if (tileResultsPrio2.size()>0) {
					tileResults = tileResultsPrio2.get(RandomNumber.getRandom(tileResultsPrio2.size()));
				} else {
					tileResults = allTileResults.get(RandomNumber.getRandom(allTileResults.size()));
				}
				
				// Then pick a random MappingResult from the set for this tile
				TileMappingPossibility tmp = tileResults.get(RandomNumber.getRandom(tileResults.size()));
				
				// Add it to the grid
				Tile tile = tmp.getTile();
				if (tile.getClearingCount()==6 &&  !tile.hasRiverPaths(0)) {
					/*
					 * This is a TOTAL hack, but should improve the speed of map building...
					 * 
					 * Basically by renaming all successfully placed Tile objects with 6 clearings as the anchor (Borderland),
					 * any logic that searches for connections to the anchor will stop when one of these tiles is located,
					 * shortening EVERY search.
					 * 
					 * Note that by renaming the Tile object, the GameObject is unaffected, so there is no harm.  Might be
					 * confusing if someone were to try to debug this code (How many friggen Borderlands are there!!) but
					 * I'm guessing that will never happen.  Famous last words....?
					 * 
					 * This hack didn't work for at least one Super Realm River tile
					 */
					tile.changeName(anchor.getGameObject().getName());
				}
				Point pos = tmp.getPosition();
				int rot = tmp.getRotation();
				
				tile.setRotation(rot);
				tile.setMapPosition(pos);
				
				mapGrid.put(pos,tile);
			}
			else {
				// It is bad if the allTileResults collection is empty at this point - means that none
				// of the remaining tiles can be placed at all!  We have a dead map, so return false.
				System.out.println(" - no more tile placement options!");
				return false;
			}
		}
		
		if (!validateLakeWoodsTile(hostPrefs, mapGrid, anchor)) return false;
		
		if (!validateRiver(hostPrefs, mapGrid)) return false;
		
		for (Tile tile : mapGrid.values()) {
			tile.writeToGameObject();
		}
		System.out.println();
		return true;
	}
	public static boolean validateLakeWoodsTile(HostPrefWrapper hostPrefs, Hashtable<Point, Tile> mapGrid, Tile anchor) {
		if (hostPrefs.hasPref(Constants.MAP_BUILDING_LAKE_WOODS_MUST_CONNECT)) {
			for (Tile tile : mapGrid.values()) {
				if (tile.getGameObject().getName().matches("Lake Woods")) {
					for (String clearing : tile.getClearings()) {
						if (!tile.connectsToTilename(mapGrid,clearing,anchor.getGameObject().getName())) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	public static boolean validateRiver(HostPrefWrapper hostPrefs, Hashtable<Point, Tile> mapGrid) {
		if (hostPrefs.hasPref(Constants.MAP_BUILDING_NON_RIVER_TILES_ADJACENT_TO_RIVER) || hostPrefs.hasPref(Constants.MAP_BUILDING_2_NON_RIVER_TILES_ADJACENT_TO_RIVER)) {
			int neededCount = 1;
			if (hostPrefs.hasPref(Constants.MAP_BUILDING_2_NON_RIVER_TILES_ADJACENT_TO_RIVER)) {
				neededCount = 2;
			}
			for (Tile tile : mapGrid.values()) {
				if (tile.hasRiverPaths(0)) {
					Point pos = tile.getMapPosition();
					int adjCount = 0;
					for (int edge=0;edge<6;edge++) {
						Point adjPos = Tile.getAdjacentPosition(pos,edge);
						for (int adj=0;adj<6;adj++) {
							Tile adjTile = mapGrid.get(Tile.getAdjacentPosition(adjPos,adj));
							if (adjTile!=null && !adjTile.hasRiverPaths(0)) {
								adjCount++;
							}
						}
						if (adjCount>=neededCount) continue;
						if (edge==6) return false;
					}
				}
			}
		}
		return true;
	}
	public static Hashtable<Point, Tile> getMapGrid(GameData data, HostPrefWrapper hostPrefs) {
		Hashtable<Point, Tile> mapGrid = new Hashtable<>();
		Collection<String> keyVals = GamePool.makeKeyVals(hostPrefs.getGameKeyVals());
		ArrayList<Tile> tiles = startTileList(data,keyVals);
		for (Tile tile : tiles) {
			tile.readFromGameObject();
			mapGrid.put(Tile.getPositionFromGameObject(tile.getGameObject()),tile);
		}
		return mapGrid;
	}
	public static void main(String[]args) {
	    RealmLoader loader = new RealmLoader();
		GameData data = loader.getData();
		System.out.println("loaded "+data.getGameObjects().size());
		ArrayList<String> keyVals = new ArrayList<>();
		keyVals.add("original_game");
		while(!MapBuilder.autoBuildMap(data,keyVals))
		for (GameObject obj : data.getGameObjects()) {
			if (obj.hasKey("tile")) {
				System.out.println(obj+":   "+obj.getAttributeBlock("mapGrid"));
			}
		}
	}
}