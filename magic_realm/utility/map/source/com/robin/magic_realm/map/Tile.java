package com.robin.magic_realm.map;

import java.awt.Point;
import java.util.*;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;

public class Tile {
	public static boolean debug = false;
	
	//										0  1  2  3  4  5  
	//										0  3  6  9 12 15  
	private static final String EDGE_MAP = "_S__SW_NW_N__NE_SE_";
	public static final String MAP_GRID = "mapGrid";
	public static final String MAP_POSITION = "mapPosition";
	public static final String MAP_ROTATION = "mapRotation";
	public static final int ROTATION_N		= 3;
	public static final int ROTATION_NE		= 4;
	public static final int ROTATION_NW		= 2;
	
	public static final int ROTATION_S		= 0;
	public static final int ROTATION_SE		= 5;
	public static final int ROTATION_SW		= 1;
	public static final int SIDE_ENCHANTED	= 1;
	
	public static final int SIDE_NORMAL		= 0;
	
	public static Point getAdjacentPosition(Point pos,int rot) {
		switch(rot) {
			case 0:		return new Point(pos.x,pos.y+1);
			case 1:		return new Point(pos.x-1,pos.y+1);
			case 2:		return new Point(pos.x-1,pos.y);
			case 3:		return new Point(pos.x,pos.y-1);
			case 4:		return new Point(pos.x+1,pos.y-1);
			case 5:		return new Point(pos.x+1,pos.y);
		}
		return null;
	}
	
	public static String getEdgeName(int edge) {
		String edgeName=null;
		switch(edge) {
			case 0:		edgeName = "S";   break;
			case 1:		edgeName = "SW";  break;
			case 2:		edgeName = "NW";  break;
			case 3:		edgeName = "N";   break;
			case 4:		edgeName = "NE";  break;
			case 5:		edgeName = "SE";  break;
		}
		return edgeName;
	}
	public static int getEdgeIntByName(String edge) {
		int edgeInt = -1;
		switch(edge) {
			case "S":	edgeInt = 0;   break;
			case "SW":	edgeInt = 1;  break;
			case "NW":	edgeInt = 2;  break;
			case "N":	edgeInt = 3;   break;
			case "NE":	edgeInt = 4;  break;
			case "SE":	edgeInt = 5;  break;
		}
		return edgeInt;
	}
	public static Point getPositionFromGameObject(GameObject obj) {
		String pos = obj.getAttribute(MAP_GRID,MAP_POSITION);
		StringTokenizer st = new StringTokenizer(pos,",");
		int px = Integer.valueOf(st.nextToken()).intValue();
		int py = Integer.valueOf(st.nextToken()).intValue();
		return new Point(px,py);
	}
	public static int getRelativeEdgeNumber(String val) {
		int index = EDGE_MAP.indexOf("_"+val+"_");
		if (index>=0) {
			return index/3;
		}
		throw new IllegalArgumentException("invalid edgename");
	}
	public static int getRotatedEdgeNumber(String val,int rot) {
		return (getRelativeEdgeNumber(val)+rot)%6;
	}
	public static int getRotationFromGameObject(GameObject obj) {
		String rot = obj.getAttribute(MAP_GRID,MAP_ROTATION);
		return Integer.valueOf(rot).intValue();
	}
	public static String convertEdge(String val,int rot) {
		return getEdgeName(getRotatedEdgeNumber(val,rot));
	}
	public static String matchingEdge(String val) {
		int n = getRelativeEdgeNumber(val);
		return getEdgeName((n+3)%6);
	}
	/**
	 * Reconstructs the mapGrid hash from the prebuilt gameobjects.  Note that this method can only be called
	 * AFTER setting up the map with buildMap
	 */
	public static Hashtable<Point, Tile> readMap(GameData data,Collection<String> keyVals) {
		Hashtable<Point, Tile> mapGrid = new Hashtable<>();
		// loop through all gameObjects to get tiles
		GamePool pool = new GamePool(data.getGameObjects());
		for (GameObject obj : pool.extract(keyVals)) {
			if (obj.hasKey("tile")) {
				Tile tile = new Tile(obj);
				if (tile.getGameObject().hasAttribute(Tile.MAP_GRID, Tile.MAP_POSITION)) {
					tile.readFromGameObject();
					mapGrid.put(tile.getMapPosition(),tile);
				}
			}
		}
		return mapGrid;
	}
	
	protected ArrayList<String> clearings;

	protected GameObject gameObject;
	
	protected String name;
	
	protected Hashtable[] paths;
	protected Point position;
	protected int rotation = ROTATION_S;
	protected int side = SIDE_NORMAL;
	protected boolean[] unrotatedPathState;
	
	public Tile(GameObject obj) {
		gameObject = obj;
		name = gameObject.getName();
		position = null;
		unrotatedPathState = new boolean[6];
		Arrays.fill(unrotatedPathState,false);
		build();
		if ("light".equals(obj.getThisAttribute("facing"))) {
			side = SIDE_NORMAL;
		}
		else {
			side = SIDE_ENCHANTED;
		}
	}
	public void build() {
		clearings = new ArrayList<>();
		paths = new Hashtable[2];
		paths[SIDE_NORMAL] = new Hashtable();
		buildPaths(paths[SIDE_NORMAL],gameObject.getAttributeBlock("normal"));
		paths[SIDE_ENCHANTED] = new Hashtable();
		buildPaths(paths[SIDE_ENCHANTED],gameObject.getAttributeBlock("enchanted"));
	}
	public void buildPaths(Hashtable pathHash,Hashtable objHash) {
		int n=1;
		while(true) {
			String baseKey = "path_"+n;
			if (objHash.get(baseKey+"_type")!=null) {
				String from = (String)objHash.get(baseKey+"_from");
				String to = (String)objHash.get(baseKey+"_to");
				
				updatePathHash(pathHash,from,to);
				updatePathHash(pathHash,to,from);
				updateClearingList(from);
				updateClearingList(to);
			}
			else {
				break;
			}
			n++;
		}
	}
	
	public void changeName(String name) {
		this.name = name;
	}
	
	public boolean connectsToTilename(Hashtable<Point,Tile> mapGrid,String clearingKey,String tilename) {
		return connectsToTilename(mapGrid,clearingKey,tilename,new ArrayList<String>());
	} 
	private boolean connectsToTilename(Hashtable<Point,Tile> mapGrid,String clearingKey,String tilename,ArrayList<String> touchedClearings) {
		touchedClearings.add(name+":"+clearingKey);

		// Check the obvious
		if (name.equals(tilename)) {
			// This clearing is on the tile named tilename, so return true
			return true;
		}
	
		// Get all clearings connected to this clearing
		Collection<String> c = getConnected(clearingKey);
		
		// Remove any clearings already "touched"
		if (c!=null && c.size()>0) {
			// Cycle through connected clearings
			for (String connectedClearing : c) {
				if (!touchedClearings.contains(name+":"+connectedClearing)) {
					if (isEdge(connectedClearing)) {
						touchedClearings.add(name+":"+connectedClearing);
						// if path connects to edge, use mapGrid to determine new Tile and
						// call that tile's connectsToTilename method
						
						// First find the tile that connects on that side
						int realEdge = getRealEdgeNumber(connectedClearing);
						Point adjPos = getAdjacentPosition(position,realEdge);
						Tile adjTile = mapGrid.get(adjPos);
						
						if (adjTile!=null) {
							// Find the edge of the adjacent tile that touches this tile
							int adjTileRealEdge = (realEdge+3)%6;
							int adjTileRelativeEdge = adjTileRealEdge-adjTile.getRotation();
							while(adjTileRelativeEdge<0) adjTileRelativeEdge+=6;
							Collection<String> newTileClearings = adjTile.getConnected(getEdgeName(adjTileRelativeEdge));
							if (newTileClearings!=null) {
								for (String newTileClearing : newTileClearings) {
									if (adjTile.connectsToTilename(mapGrid,newTileClearing,tilename,touchedClearings)) {
										// The connected clearing on the adjacent tile connects to tilename, so this connects.
										return true;
									}
								}
							}
						}
					}
					else {
						if (connectsToTilename(mapGrid,connectedClearing,tilename,touchedClearings)) {
							// The connected clearing connects to tilename, so this connects.
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	// These methods are just here so I can code - their purpose will be coded later
	public int getClearingCount() {
		return clearings.size();
	}
	
	public Collection<String> getConnected(String clearing) {
		return (Collection<String>)paths[side].get(clearing);
	}
	public GameObject getGameObject() {
		return gameObject;
	}
	public Point getMapPosition() {
		return position;
	}
	/**
	 * Returns the edge path state (after rotation)
	 */
	public boolean[] getPathState() {
		boolean[] pathState = new boolean[6];
		for (int i=0;i<6;i++) {
			pathState[(i+rotation)%6] = unrotatedPathState[i];
		}
		return pathState;
	}
	public boolean getPathState(int edge) {
		boolean[] pathState = getPathState();
		return pathState[edge];
	}
	public int getRealEdgeNumber(String val) {
		return getRotatedEdgeNumber(val,rotation);
	}
	public int getRotation() {
		return rotation;
	}
	public boolean isEdge(String val) {
		return EDGE_MAP.indexOf("_"+val+"_")>=0;
	}
	public void readFromGameObject() {
		setMapPosition(getPositionFromGameObject(gameObject));
		setRotation(getRotationFromGameObject(gameObject));
	}
	public void setMapPosition(Point p) {
		position = p;
	}
	public void setRotation(int rot) {
		rotation = rot;
	}
	public String toString() {
		return name+":  "+position+"=="+rotation;
	}
	private void updateClearingList(String clearing) {
		if (EDGE_MAP.indexOf("_"+clearing+"_")==-1) {
			// Not an edge
			if (!clearings.contains(clearing)) {
				clearings.add(clearing);
			}
		}
	}
	
	private void updatePathHash(Hashtable<String, ArrayList<String>> pathHash,String from,String to) {
		ArrayList<String> list = pathHash.get(from);
		if (list==null) {
			list = new ArrayList<>();
			pathHash.put(from,list);
		}
		if (!list.contains(to)) {
			list.add(to);
		}
		int index = EDGE_MAP.indexOf("_"+from+"_");
		if (index>=0) {
			unrotatedPathState[index/3]=true;
		}
	}
	public void writeToGameObject() {
		gameObject.setAttribute(MAP_GRID,MAP_POSITION,position.x+","+position.y);
		gameObject.setAttribute(MAP_GRID,MAP_ROTATION,String.valueOf(rotation));
	}
	
	/**
	 * @param mapGrid	The map Hash of Point keys to Tile objects
	 * @param tile		The Tile object to be tested
	 * @param pos		The Point to test the Tile object
	 * @param rot		The rotation to use
	 * 
	 * @return		true if the Tile object will fit at the specified location and rotation.
	 */
	public static boolean isMappingPossibility(Hashtable mapGrid,Tile tile,Point pos,int rot,String anchorTilename) {
		// Setup the position
		tile.setMapPosition(pos);
		tile.setRotation(rot);
		// First test the join
		boolean joinError = false;
		boolean riverConnected = false;
		for (int edge=0;edge<6;edge++) {
			Tile adjTile = (Tile)mapGrid.get(Tile.getAdjacentPosition(pos,edge));
			// Only need to test joins where there is a tile
			if (adjTile!=null) {
				if (tile.getPathState(edge)!=adjTile.getPathState((edge+3)%6)) {
					return false;
				}
				ArrayList<String> pathsTypes = tile.getPathTypes(tile.side,(edge-rot+6)%6);
				ArrayList<String> adjTilePathsTypes = adjTile.getPathTypes(adjTile.side,(edge+9-adjTile.getRotation())%6);
				if ((pathsTypes.contains("river") && !adjTilePathsTypes.contains("river")) || (adjTilePathsTypes.contains("river") && !pathsTypes.contains("river"))) {
					return false;
				}
				if (pathsTypes.contains("river") && adjTilePathsTypes.contains("river")) {
					riverConnected = true;
				}
			}
		}
		if (!riverConnected && tile.hasRiverPaths(tile.side) && !tile.name.matches(anchorTilename)) {
			return false;
		}
		boolean allConnect = true;
		boolean anyConnect = false;
		for (int i=0;i<6;i++) {
			if (tile.connectsToTilename(mapGrid,"clearing_"+(i+1),anchorTilename)) {
				anyConnect = true;
			}
			else {
				allConnect = false;
				if (anyConnect==true) break;
			}
		}
		
		String tileType = tile.getGameObject().getThisAttribute("tile_type");
		// Now, if the tile has 6-clearings, check to be sure the paths lead back to the borderland tile.
		if (tile.getClearingCount()==6 && tileType!="V" && tileType!="W" && tileType!="H" && !tile.hasRiverPaths(tile.side)) {
			// I think I only need to check clearings 2 and 6 (or something like that)
			if (!allConnect) {
				if (debug) System.out.println(tile.name+" doesn't have all 6 clearings connecting");
				joinError = true;
			}
		}
		else {
			if (!anyConnect) {
				if (debug) System.out.println(tile.name+" doesn't have any clearings connecting");
				joinError = true;
			}
		}
		
		// If the tile has no join errors, save the result
		// (no need to check if adjacent to two tiles here)
		return !joinError;
	}
	
	public static boolean isMappingNextToPrioritizedTile(Hashtable mapGrid,Tile tile,Point pos,int rot) {
		tile.setMapPosition(pos);
		tile.setRotation(rot);
		for (int edge=0;edge<6;edge++) {
			Tile adjTile = (Tile)mapGrid.get(Tile.getAdjacentPosition(pos,edge));
			if (adjTile!=null && (tile.getGameObject().hasThisAttribute("map_building_increase_prio_tile_placement") || tile.getGameObject().hasThisAttribute("anchor_tile"))) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<String> getPathTypes(int side,int edge) {
		String sideName; 
		if (side == 0) {
			sideName = "normal";
		}
		else {
			sideName = "enchanted";
		}
		String edgeName = getEdgeName(edge);
		ArrayList<String> pathsTypes = new ArrayList<>();
		int i=1;
		Hashtable attributes = gameObject.getAttributeBlock(sideName);
		while (true) {
			if (attributes.get("path_"+i+"_type")!=null) {
				String from = (String)attributes.get("path_"+i+"_from");
				String to = (String)attributes.get("path_"+i+"_to");
				if (from.matches(edgeName) || to.matches(edgeName)) {
					pathsTypes.add((String)attributes.get("path_"+i+"_type"));
				}
				i++;
			}
			else {
				break;
			}
		}
		return pathsTypes;
	}
	
	public ArrayList<String> getClearingTypes(int side) {
		String sideName; 
		if (side == 0) {
			sideName = "normal";
		}
		else {
			sideName = "enchanted";
		}
		ArrayList<String> clearingTypes = new ArrayList<>();
		Hashtable attributes = gameObject.getAttributeBlock(sideName);
		for (int i=1;i<=9;i++) {
			if (attributes.get("clearing_"+i+"_type")!=null) {
				clearingTypes.add((String)attributes.get("clearing_"+i+"_type"));
			}
		}
		return clearingTypes;
	}
	
	public boolean hasRiverPaths(int side) {
		String sideName; 
		if (side == 0) {
			sideName = "normal";
		}
		else {
			sideName = "enchanted";
		}
		int i=1;
		Hashtable attributes = gameObject.getAttributeBlock(sideName);
		while (true) {
			if (attributes.get("path_"+i+"_type")!=null) {
				String path = (String)attributes.get("path_"+i+"_type");
				i++;
				if (path.matches("river")) {
					return true;
				}
			}
			else {
				break;
			}
		}
		return false;
	}
	
	/**
	 * @return		A Collection of Point objects that reference possible map placements
	 */
	public static ArrayList<Point> findAvailableMapPositions(Hashtable<Point, Tile> mapGrid) {
		return findAvailableMapPositions(mapGrid,false);
	}
	public static ArrayList<Point> findAvailableMapPositions(Hashtable<Point, Tile> mapGrid, boolean autoBuildRiver) {
		ArrayList<Point> availableMapPositions = new ArrayList<>();
		for (Tile tile : mapGrid.values()) {
			Point pos = tile.getMapPosition();
			
			// Cycle through all adjacent positions to the mapped tile
			for (int edge=0;edge<6;edge++) {
				Point adjPos = Tile.getAdjacentPosition(pos,edge);
				// only empty places
				if (mapGrid.get(adjPos)==null) {
					// only undiscovered places
					if (!availableMapPositions.contains(adjPos)) {
						// only joinable places
						if (tile.getPathState(edge)) {
							// Count adjacent tiles (joined or not)
							int adjCount = 0;
							for (int adj=0;adj<6;adj++) {
								Tile adjTile = mapGrid.get(Tile.getAdjacentPosition(adjPos,adj));
								if (adjTile!=null) {
									adjCount++;
								}
							}
							// only places adjacent to two tiles (unless only one tile on map)
							if (mapGrid.size()==1 || adjCount>1 || tile.getGameObject().hasThisAttribute("map_building_prio")) {
								availableMapPositions.add(adjPos);
							}
						}
					}
				}
			}
		}
		return availableMapPositions;
	}
}