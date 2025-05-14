package com.robin.magic_realm.components.utility;

import java.awt.Point;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.robin.game.objects.*;
import com.robin.game.server.GameClient;
import com.robin.general.graphics.GraphicsUtil;
import com.robin.general.util.RandomNumber;
import com.robin.general.util.StringBufferedList;
import com.robin.hexmap.HexMapPoint;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.swing.RealmObjectPanel;
import com.robin.magic_realm.components.wrapper.*;
import com.robin.magic_realm.map.Tile;

public class ClearingUtility {

	/**
	 * Returns the name of the dwelling (if any) that resides in the specified tile and clearing (6 possibilities)
	 */
	public static GameObject findDwellingInClearing(TileLocation tl) {
		if (tl==null || !tl.isInClearing()) return null;
		return findDwellingInClearing(tl.tile.getGameObject(),tl.clearing.getNum());
	}
	public static GameObject findDwellingInClearing(GameObject tile,int clearing) {
		GamePool pool = new GamePool(tile.getHold());
		ArrayList<String> keyVals = new ArrayList<>();
		keyVals.add("dwelling");
		keyVals.add("tile_type");
		keyVals.add("clearing="+clearing);
		Collection<GameObject> dwellings = pool.find(keyVals);
		if (dwellings.size()==1) {
			GameObject dwelling = dwellings.iterator().next();
			return dwelling;
		}
		
		return null;
	}
	
	public static void markBorderlandConnectedClearings(HostPrefWrapper hostPrefs,GameData gameData) {
		String anchorTileName = "Borderland";
		ArrayList<GameObject> tiles = RealmObjectMaster.getRealmObjectMaster(gameData).getTileObjects();
		Collection<String> keyVals = GamePool.makeKeyVals(hostPrefs.getGameKeyVals());
		Hashtable<Point, Tile> mapGrid = Tile.readMap(gameData,keyVals);
		for(GameObject tile:tiles) {
			if (tile.hasThisAttribute(Constants.ANCHOR_TILE)) {
				anchorTileName = tile.getName();
				break;
			}
		}
		for(GameObject tile:tiles) {
			if (tile.getHoldCount()==0) continue;
			TileComponent tileRc = (TileComponent)RealmComponent.getRealmComponent(tile);
			Tile mapTile = null;
			
			boolean tilePlaced = tile.hasAttribute(Tile.MAP_GRID, Tile.MAP_POSITION);
			if (tilePlaced) {
				mapTile = mapGrid.get(Tile.getPositionFromGameObject(tile));
			}
			for (int i=1;i<=6;i++) {
				ClearingDetail clearing = tileRc.getClearing(i);
				if (clearing==null) continue;
				if (!tilePlaced || mapTile.connectsToTilename(mapGrid,"clearing_"+i,anchorTileName)) {
					clearing.setConnectsToBorderland(true);
				}
				else {
					clearing.setConnectsToBorderland(false);
				}
			}
		}
	}
	
	/**
	 * Returns the highest numbered clearing that connects back to the Borderland tile
	 */
	public static int recommendedClearing(GameObject tile) {
		int lowestUnconnectedClearing = 0;
		TileComponent tileRc = (TileComponent)RealmComponent.getRealmComponent(tile);
		for (int i=6;i>0;i--) {
			ClearingDetail clearing = tileRc.getClearing(i);
			if (clearing==null) continue;
			if (clearing.isConnectsToBorderland()) {
				return i;
			}
			lowestUnconnectedClearing = i;
		}
		return lowestUnconnectedClearing;
	}

	/**
	 * @return		The objects that were dumped
	 */
	public static ArrayList<GameObject> dumpHoldToTile(GameObject tile,GameObject gameObject,int clearing) {
		return ClearingUtility.dumpHoldToTile(tile,gameObject,clearing,null,false,false);
	}

	/**
	 * @return		The objects that were dumped
	 */
	public static ArrayList<GameObject> dumpHoldToTile(GameObject tile,GameObject gameObject,int clearing,String testKey) {
		return ClearingUtility.dumpHoldToTile(tile,gameObject,clearing,testKey,false,false);
	}
		
	/**
	 * @return		The objects that were dumped
	 */
	public static ArrayList<GameObject> dumpGoldSpecialsToTile(GameObject tile,GameObject gameObject,int clearing) {
		return ClearingUtility.dumpHoldToTile(tile,gameObject,clearing,null,true,false);
	}
	
	/**
	 * @return		The objects that were dumped
	 */
	public static ArrayList<GameObject> dumpTravelersToTile(GameObject tile,GameObject gameObject,int clearing) {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameObject.getGameData());
		if (hostPrefs.hasPref(Constants.SR_REVEAL_TRAVELERS)) {
			return ClearingUtility.dumpHoldToTile(tile,gameObject,clearing,null,true,true);
		}
		return new ArrayList<>();
	}
	
	/**
	 * @return		The objects that were dumped
	 */
	public static ArrayList<GameObject> dumpHoldToTile(GameObject tile,GameObject gameObject,int clearing,String testKey,boolean onlyGoldSpecial, boolean onlyTravelers) {
		if (clearing==-1) {
			clearing = recommendedClearing(tile);
		}
		ArrayList<GameObject> added = new ArrayList<>();
		Collection<GameObject> hold = new ArrayList<>(gameObject.getHold()); // this construction is necessary to prevent concurrent modification errors
		boolean itemsAndSpells = !gameObject.hasThisAttribute(Constants.ROVING_NATIVE);
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameObject.getGameData());
		for (GameObject go : hold) {
			if (go.hasThisAttribute(Constants.QUEST)) {
				continue;
			}
			if (testKey==null || go.hasThisAttribute(testKey)) {
				if (!onlyGoldSpecial && hostPrefs.hasPref(Constants.SR_REVEAL_TRAVELERS) && go.hasThisAttribute(RealmComponent.GOLD_SPECIAL)) continue;
				if (onlyGoldSpecial && !go.hasThisAttribute(RealmComponent.GOLD_SPECIAL)) continue;
				if (itemsAndSpells || (!go.hasThisAttribute("item") && !go.hasThisAttribute("spell"))) {
					go.setThisAttribute("clearing",String.valueOf(clearing));
					GameClient.broadcastClient("host",go.getName()+" is added to "+tile.getName()+", clearing "+clearing);
					tile.add(go);
					added.add(go);
					if (go.hasThisAttribute(RealmComponent.GOLD_SPECIAL)) {
						go.removeThisAttribute(Constants.DRAW_BACKSIDE);
					}
				}
			}
			if (go.hasThisAttribute("dwelling")) {
				SetupCardUtility.setupDwellingNatives(go);
			}
		}
		return added;
	}

	public static Collection<GameObject> getAbandonedItems(TileLocation tileLocation) {
		ArrayList<GameObject> list = new ArrayList<>();
		Collection<RealmComponent> stuff = tileLocation.clearing.getClearingComponents();
		for (RealmComponent rc : stuff) {
			GameObject obj = rc.getGameObject();
			if (!rc.isPlainSight() && !obj.hasThisAttribute(Constants.CANNOT_MOVE) && rc.isItem()) {
				list.add(obj);
			}
		}
		return list;
	}

	public static int getAbandonedItemCount(TileLocation tileLocation) {
		return getAbandonedItems(tileLocation).size();
	}

	/**
	 * Restores the map chits in tiles that don't have a character in them
	 */
	public static void restoreChitState(GameData data) {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(data);
		Collection<GameObject> tileObjects = RealmObjectMaster.getRealmObjectMaster(data).getTileObjects();
		
		// Check all the tiles
		boolean chitsStayFaceUp = hostPrefs.hasPref(Constants.HOUSE1_CHIT_REMAIN_FACE_UP);
		for (GameObject obj : tileObjects) {
			TileComponent tc = (TileComponent)RealmComponent.getRealmComponent(obj);
			if (!chitsStayFaceUp && !tc.holdsCharacter()) {
				tc.setChitsFaceDown(); // ALWAYS turn them face down
			}
			tc.resetChitsSummoned(); // reset chits so they can summon again
		}
	}

	/**
	 * If the provided component can hold inventory, then the inventory is deduced and broken down into individual "seen" items.
	 */
	public static ArrayList<RealmComponent> dissolveIntoSeenStuff(RealmComponent rc) {
		ArrayList<RealmComponent> list = new ArrayList<>();
		if (rc.isAnyLeader() || rc.isVisitor() || rc.isCacheChit()) {
			// check all character and native leader treasures
			for (GameObject go : rc.getGameObject().getHold()) {
				RealmComponent arc = RealmComponent.getRealmComponent(go);
				list.addAll(dissolveIntoSeenStuff(arc));
			}
			list.add(rc); // don't forget the character himself!
			
			// Treasures in a native hold not yet seen by a player should NOT give off color
			if (rc.isNativeLeader()) {
				GameObject holder = SetupCardUtility.getDenizenHolder(rc.getGameObject());
				if (holder != null) {
					for (GameObject go : holder.getHold()) {
						if (go.hasThisAttribute(Constants.TREASURE_SEEN)) {
							RealmComponent arc = RealmComponent.getRealmComponent(go);
							list.addAll(dissolveIntoSeenStuff(arc));
						}
					}
				}
			}
			
			if (rc.isCharacter()) {
				// Look for transmorphed inventory in bewitching spells
				CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
				for (SpellWrapper spell:SpellUtility.getBewitchingSpells(character.getGameObject())) {
					for (GameObject item : spell.getGameObject().getHold()) {
						if (item.hasThisAttribute(Constants.TREASURE_SEEN)) {
							list.add(RealmComponent.getRealmComponent(item));
						}
					}
				}
			}
		}
		else {
			if (!rc.isTreasure() || rc.getGameObject().hasThisAttribute(Constants.TREASURE_SEEN)) {
				list.add(rc);
			}
		}
		return list;
	}

	/**
	 * @param toMove			The object to move
	 * @param location			The location to move to
	 */
	public static void moveToLocation(GameObject toMove,TileLocation location) {
		ClearingUtility.moveToLocation(toMove,location,false);
	}

	/**
	 * @param toMove			The object to move
	 * @param location			The location to move to
	 * @param partway			If true, then move the object halfway between clearings
	 */
	public static void moveToLocation(GameObject toMove,TileLocation location,boolean partway) {
		if (location==null) { // happens in the combat simulator and when companions are removed by a quest (other places?)
			toMove.removeThisAttribute("clearing");
			toMove.removeThisAttribute("otherTile");
			toMove.removeThisAttribute("otherClearing");
			GameObject held = toMove.getHeldBy();
			if (held!=null) {
				held.remove(toMove);
			}
			return;
		}
		if (partway) {
			// The clearing you are leaving is ALWAYS the "otherClearing" in the partway move
			TileComponent otherTile = (TileComponent)RealmComponent.getRealmComponent(toMove.getHeldBy());
			if (otherTile==null) {
				throw new IllegalStateException("Cannot move partway with a null starting location");
			}
			if (location.isTileOnly()) { // moving to a non-clearing place
				location.clearing = null;
				if (!location.tile.equals(otherTile)) {
					location.setOther(new TileLocation(otherTile));
				}
			}
			else if (!location.isBetweenClearings()) {
				String numString = toMove.getThisAttribute("clearing");
//				int num = toMove.getThisInt("clearing");
//				if (num==0) {
				if (numString==null) {
					throw new IllegalStateException("Cannot move partway with a starting clearing of zero!");
				}
				location.setOther(new TileLocation(otherTile.getClearing(numString)));
			}
			// else if location is already partway, then nothing further needs be done here
		}
		TileComponent tile = location.tile;
		if (toMove.getHeldBy()==null || !toMove.getHeldBy().equals(tile.getGameObject())) {
			// Add Character to new tile, but make sure both come from same GameData parent (defer to toMove object)
			// FIXME Not sure this is necessary anymore with the change I made to RealmComponent - should remove this code, and see if moving from tile to tile still works.
			GameObject tileObject = toMove.getGameData().getGameObject(tile.getGameObject().getId());
			
			// This line, however, IS required!
			tileObject.add(toMove);
		}
		if (location.hasClearing()) {
			toMove.setThisAttribute("clearing",location.clearing.getNumString());
			if (location.isBetweenClearings()) {
				toMove.setThisAttribute("otherTile",location.getOther().tile.getTileCode());
				toMove.setThisAttribute("otherClearing",location.getOther().clearing.getNumString());
			}
			else {
				toMove.removeThisAttribute("otherTile");
				toMove.removeThisAttribute("otherClearing");
			}
		}
		else {
			toMove.removeThisAttribute("clearing");
			toMove.removeThisAttribute("otherClearing");
			if (location.isBetweenTiles()) {
				toMove.setThisAttribute("otherTile",location.getOther().tile.getTileCode());
			}
			else {
				toMove.removeThisAttribute("otherTile");
			}
		}
		if (location.isFlying()) {
			toMove.setThisAttribute("isflying");
		}
		else {
			toMove.removeThisAttribute("isflying");
		}
	}

	/**
	 * @param pool			The GamePool for the current gameHandler (RealmGameHandler.getGamePool())
	 * @param action		Like M-B6 or P-CV1 or M-B.NW
	 * 
	 * @return				The TileLocation object that corresponds to the clearing referenced in the action
	 */
	public static TileLocation deduceLocationFromAction(GameData data,String action) {
		int dash = action.lastIndexOf("-");
		int afterDash = dash<0?0:dash+1;
		
		return TileLocation.parseTileLocationNoPartway(data,action.substring(afterDash));
	}

	public static int getClearingDieMod(TileLocation tl) {
		int mod = 0;
		if (tl!=null && tl.hasClearing()) {
			ArrayList<RealmComponent> seen = new ArrayList<>();
			// Check for presence of cloven hoof (the only plus_one item at present) in the clearing
			for (RealmComponent rc : tl.clearing.getClearingComponents()) {
				seen.addAll(dissolveIntoSeenStuff(rc));
			}
			for (RealmComponent rc : seen) {
				GameObject go = rc.getGameObject();
				if (go.hasThisAttribute(Constants.PLUS_ONE) && go.hasThisAttribute(Constants.TREASURE_SEEN)) {
					mod++;
				}
			}
		}
		return mod;
	}
	
	public static GameObject getItemInClearingWithKey(TileLocation tl,String key) {
		if (tl!=null && tl.hasClearing()) {
			ArrayList<RealmComponent> seen = new ArrayList<>();
			// Check for presence of cloven hoof (the only plus_one item at present) in the clearing
			for (RealmComponent rc : tl.clearing.getClearingComponents()) {
				seen.addAll(dissolveIntoSeenStuff(rc));
			}
			for (RealmComponent rc : seen) {
				GameObject go = rc.getGameObject();
				if (go.hasThisAttribute(key) && go.hasThisAttribute(Constants.TREASURE_SEEN)) {
					return go;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the TileLocation, if the RealmComponent is on the board, or null if it is not.
	 */
	public static TileLocation getTileLocation(RealmComponent rc) {
		if (rc.isTile()) {
			return new TileLocation((TileComponent)rc);
		}
		return ClearingUtility.getTileLocation(rc.getGameObject());
	}

	public static TileLocation getTileLocation(GameObject go) {
		boolean flying = go.hasThisAttribute("isflying");
		TileComponent tile = null;
		RealmComponent parent = null;
		
		ArrayList<GameObject> searched = new ArrayList<>();
		
		// Find the top level parent, that isn't a tile
		while(parent==null && go.getHeldBy()!=null) {
			searched.add(go);
			GameObject temp = go.getHeldBy();
			if (temp==go) {
				throw new IllegalStateException("GameObject is held by itself! "+go+" --> "+temp);
			}
			RealmComponent tempRc = RealmComponent.getRealmComponent(temp);
			if (tempRc==null) {
				// Bumped into a gameobject that isn't a component (like a native dwelling) - return null
				return null;
			}
			if (tempRc.isTile()) {
				tile = (TileComponent)tempRc;
				parent = RealmComponent.getRealmComponent(go);
			}
			if (tempRc.isSpell()) {
				// The only time, an object ends up INSIDE a spell, is when you are transmorphed!  Therefore, to
				// identify the LOCATION of the spell, look to the target of the spell.
				// UPDATE 9/13/12 - not true: absorb essence does this too
				SpellWrapper spell = new SpellWrapper(temp);
				
				GameObject test;
				if (spell.affectsCaster()) { // Absorb Essence
					test = spell.getCaster().getGameObject();
				}
				else { // All other spells (Just Transmorph?)
					RealmComponent rc = spell.getFirstTarget();
					test = rc==null?null:rc.getGameObject();
				}
				
				if (test!=null) {
					if (!searched.contains(test)) {
						// Haven't searched this before, so this is good (prevents infinite loops)
						temp = test;
					}
				}
			}
			go = temp;
		}
		TileLocation tl = null;
		if (tile!=null && parent!=null) {
			String numString = parent.getGameObject().getThisAttribute("clearing");
			if (numString==null || "0".equals(numString) || tile.getClearing(numString)==null) {
				tl = new TileLocation(tile);
			}
			else {
				tl = new TileLocation(tile.getClearing(numString));
			}
			// Added this next section on 2/15/2006 to accomodate partway readings
			if (parent.getGameObject().hasThisAttribute("otherTile")) {
				TileComponent otherTile = RealmUtility.findTileForCode(parent.getGameObject().getGameData(),parent.getGameObject().getThisAttribute("otherTile"));
				String otherNumString = parent.getGameObject().getThisAttribute("otherClearing");
				if (otherNumString==null) {
					tl.setOther(new TileLocation(otherTile));
				}
				else {
					tl.setOther(new TileLocation(otherTile.getClearing(otherNumString)));
				}
			}
		}
		if (tl!=null && flying) {
			tl.setFlying(true);
		}
		return tl;
	}

	public static int calculateClearingCount(TileLocation tl1,TileLocation tl2) {
		int val = 0;
		if (tl1!=null && tl2!=null && !tl1.equals(tl2)) {
			ArrayList<ClearingDetail> all = new ArrayList<>();
			ArrayList<ClearingDetail> list = new ArrayList<>();
			list.add(tl1.clearing);
			int count = 0;
			while(!list.isEmpty()) {
				count++;
				ArrayList<ClearingDetail> found = new ArrayList<>();
				for (ClearingDetail clearing : list) {
					if (!all.contains(clearing)) {
						all.add(clearing);
						Collection<PathDetail> c = clearing.getConnectedPaths();
						for (PathDetail path : c) {
							ClearingDetail connectedClearing = path.findConnection(clearing);
							if (connectedClearing!=null) {
								if (connectedClearing.equals(tl2.clearing)) {
									// found one!
									if (val==0 || count<val) {
										val = count;
										if (val==1) { // it doesn't get any better than this, so stop searching
											break;
										}
									}
								}
								else {
									found.add(connectedClearing);
								}
							}
						}
					}
				}
				list = found;
			}
		}
		return val;
	}

	/**
	 * Returns all native leaders, visitors, and travelers w/stores that are in the clearing.
	 */
	public static ArrayList<RealmComponent> getAllTraders(CharacterWrapper character,ClearingDetail clearing) {
		ArrayList<RealmComponent> traders = new ArrayList<>();
		for (RealmComponent rc:clearing.getClearingComponents()) {
			if (character.hasDamagedRelations(rc.getGameObject())) {
				continue;
			}
			if (rc.isNative() && rc.getOwnerId()==null) {
				String rank = rc.getGameObject().getThisAttribute("rank");
				if (rank!=null && "HQ".equals(rank)) {
					traders.add(rc);
				}
			}
			else if (rc.isVisitor()) {
				traders.add(rc);
			}
			else if (rc.isTraveler() && rc.getGameObject().hasThisAttribute(Constants.STORE) && rc.getGameObject().hasThisAttribute(Constants.SPAWNED)) {
				traders.add(rc);
			}
			else if (rc.isGuild()) {
				traders.add(rc);
			}
		}
		return traders;
	}

	/**
	 * @return		A collection of unhired natives in the clearing
	 */
	public static ArrayList<RealmComponent> getAllHireables(CharacterWrapper character,ClearingDetail clearing) {
		ArrayList<RealmComponent> hireables = new ArrayList<>();
		Collection<RealmComponent> c = clearing.getClearingComponents();
		for (RealmComponent rc : c) {
			if (character.hasDamagedRelations(rc.getGameObject())) {
				continue;
			}
			if (rc.isNative() && rc.getOwnerId()==null) {
				hireables.add(rc);
			}
			else if (rc.isMonster() && rc.isPacifiedBy(character)) {
				hireables.add(rc);
			}
			else if (rc.isTraveler() && rc.getOwnerId()==null && (rc.getGameObject().hasThisAttribute("base_price") || rc.getGameObject().hasThisAttribute(Constants.HIRE_WITH_CHIT))) {
				hireables.add(rc);
			}
		}
		// Include all of the character's current hirelings (for "rehire")
		for (RealmComponent rc : character.getAllHirelings()) {
			if (rc.isCompanion() || rc.getGameObject().hasThisAttribute(Constants.HIRE_WITH_CHIT)) continue; // companions cannot be "rehired"
			TileLocation tl = getTileLocation(rc);
			if (tl != null && tl.clearing != null && tl.clearing.equals(clearing)) {
				hireables.add(rc);
			}
		}
		return hireables;
	}

	public static ArrayList<RealmComponent> getGuidesInClearing(TileLocation location) {
		ArrayList<RealmComponent> list = new ArrayList<>();
		if (location.isInClearing()) {
			Collection<RealmComponent> c = location.clearing.getClearingComponents();
			for (RealmComponent rc : c) {
				if (rc.isPlayerControlledLeader() && !rc.getGameObject().hasThisAttribute(Constants.CAMOUFLAGE)) {
					// Valid Guide
					list.add(rc);
				}
			}
		}
		return list;
	}

	public static Collection<RealmComponent> getCombatantsInClearing(TileLocation location, GameData data) {
		ArrayList<RealmComponent> list = new ArrayList<>();
		if (location.isInClearing()) {
			for (RealmComponent rc:location.clearing.getClearingComponents()) {
				if (rc.isCharacter() || rc.isMonster() || rc.isNative() || rc.isCombativeTraveler() || rc.isCompanion()) {
					if (rc.isNative() && !rc.isHiredOrControlled() && HostPrefWrapper.findHostPrefs(data).hasPref(Constants.HOUSE2_NO_NATIVES_BATTLING)) continue;
					if (rc.getGameObject().hasThisAttribute(Constants.NO_COMBAT)) continue;
					list.add(rc);
				}
			}
		}
		return list;
	}

	public static ArrayList<CharacterWrapper> getCharactersInClearing(TileLocation location) {
		ArrayList<CharacterWrapper> list = new ArrayList<>();
		if (location.hasClearing()) {
			for (RealmComponent rc:location.clearing.getClearingComponents()) {
				if (rc.isCharacter()) {
					CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
					list.add(character);
				}
			}
		}
		return list;
	}

	public static String showTileChits(JFrame parentFrame,ClearingDetail currentClearing,String title) {
		// Show the tile chits - do I resolve lost city and castle too? - yes, I should
		Collection<StateChitComponent> c = currentClearing.getParent().getClues();
		if (!c.isEmpty()) {
			RealmObjectPanel cluePanel = new RealmObjectPanel();
			StringBufferedList note = new StringBufferedList();
			for (StateChitComponent state : c) {
				if (state.isTreasureLocation()) {
					note.append(state.getGameObject().getName()+" "+state.getGameObject().getThisAttribute("clearing"));
				}
				else {
					note.append(state.getGameObject().getName());
				}
				cluePanel.add(state);
				state.setFaceUpWithoutExplode(); // don't allow explode on RedSpecials for clues
			}
			JOptionPane.showMessageDialog(parentFrame,cluePanel,title,JOptionPane.INFORMATION_MESSAGE);
			
			// restore facing
			for (StateChitComponent state : c) {
				state.setFaceDown();
			}
			return note.toString();
		}
		return null;
	}

	/**
	 * Finds all characters and hired leaders that are not your character, and that are not blocked and are awake.
	 */
	public static ArrayList<RealmComponent> findAllAwakeUnblockedCharactersInClearing(CharacterWrapper character) {
		ArrayList<RealmComponent> list = new ArrayList<>();
		TileLocation current = character.getCurrentLocation();
		for (RealmComponent rc : current.clearing.getClearingComponents()) {
			// Someone, that isn't yourself
			if (!rc.getGameObject().equals(character.getGameObject())) {
				// and is a character or hired leader
				if (rc.isPlayerControlledLeader()) {
					CharacterWrapper other = new CharacterWrapper(rc.getGameObject());
					if (!other.isBlocked() && !other.isSleep()) {
						list.add(rc);
					}
				}
			}
		}
		return list;
	}
	
	public static boolean canUseGates(CharacterWrapper character,ClearingDetail targetClearing) {
		// First, make sure there is a gate (or gates) in BOTH clearings
		if (targetClearing.hasKnownGate(character)) {
			TileLocation current = character.getCurrentLocation();
			if (current.isInClearing() && current.clearing.hasKnownGate(character)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static int getDistanceBetweenTiles(TileComponent t1,TileComponent t2) {
		return getHexMapPoint(t1).getDistanceFrom(getHexMapPoint(t2));
	}
	public static HexMapPoint getHexMapPoint(TileComponent t) {
		return HexMapPoint.readKey(t.getGameObject().getAttribute("mapGrid","mapPosition"));
	}
	
	public static void assignChitClearings(GameData data) {
		ArrayList<GameObject> tiles = RealmObjectMaster.getRealmObjectMaster(data).getTileObjects();
		for(GameObject tile:tiles) {
			if (tile.getHoldCount()==0) continue;
			
			// Make sure all chits have a valid clearing
			TileComponent tileC = (TileComponent)RealmComponent.getRealmComponent(tile);
			ArrayList<GameObject> chits = new ArrayList<>();
			ArrayList<ClearingDetail> connectedClearings = new ArrayList<>();
			for (ClearingDetail clearing:tileC.getClearings()){
				if (clearing.isConnectsToBorderland()) {
					connectedClearings.add(clearing);
				}
			}
			for (GameObject chit : tile.getHold()) {
				chits.add(chit);
				if (chit.hasThisAttribute("red_special")) {
					chits.addAll(chit.getHold());
				}
			}
			for (GameObject chit:chits) {
				if (!chit.hasThisAttribute(RealmComponent.WARNING) && !chit.hasThisAttribute("clearing")) {
					int r = RandomNumber.getRandom(connectedClearings.size());
					ClearingDetail clearing = connectedClearings.get(r);
					chit.setThisAttribute("clearing",clearing.getNum());
				}
			}
		}
	}
	public static void initAdjacentTiles(GameData data) {
		Hashtable<Point,TileComponent> mapGrid = new Hashtable<>();
		for(GameObject go:RealmObjectMaster.getRealmObjectMaster(data).getTileObjects()) {
			TileComponent tc = (TileComponent)RealmComponent.getRealmComponent(go);
			String pos = go.getAttribute("mapGrid","mapPosition");
			String rot = go.getAttribute("mapGrid","mapRotation");
			
			if (pos!=null && rot!=null) {
				tc.setRotation(Integer.valueOf(rot).intValue());
				Point gp = GraphicsUtil.asPoint(pos);
				mapGrid.put(gp,tc);
			}
		}
		initAdjacentTiles(mapGrid);
	}
	public static void initAdjacentTiles(Hashtable<Point,TileComponent> mapGrid) {
		for (Point mapPos:mapGrid.keySet()) {
			TileComponent tile = mapGrid.get(mapPos);
			tile.clearAdjacentTiles();
			for (int d=0;d<6;d++) { // Get ALL adjacent tiles, not just connected ones!
				String edge = Tile.getEdgeName(d);
				Point adjPos = Tile.getAdjacentPosition(mapPos,d);
				TileComponent adjTile = mapGrid.get(adjPos);
				if (adjTile!=null) {
					// found adjacent tile
					tile.putAdjacentTile(edge,adjTile);
				}
			}
		}
	}
	/**
	 * This is the method to call to find the Maze
	 */
	public static RealmComponent findDiscoverToLeaveComponent(TileLocation current,CharacterWrapper character) {
		if (current.isInClearing()) {
			for (RealmComponent rc:current.clearing.getClearingComponents()) {
				if (rc.getGameObject().hasThisAttribute(Constants.DISCOVER_TO_LEAVE)) {
					if (!character.hasTreasureLocationDiscovery(rc.getGameObject().getName())
							&& !character.hasActiveInventoryThisKeyAndValue(Constants.MAP,rc.getGameObject().getName().toLowerCase())) {
						return rc;
					}
				}
			}
		}
		return null;
	}
	
	public static String getEdgeNameBetweenClearings(ClearingDetail clearing1, ClearingDetail clearing2) {
		Collection<PathDetail> c = clearing1.getConnectedMapEdges();
		if (c!=null) {
			int rot1 = Tile.getRotationFromGameObject(clearing1.getTileLocation().tile.getGameObject());
			int rot2 = Tile.getRotationFromGameObject(clearing2.getTileLocation().tile.getGameObject());
			int rot = (9+rot1-rot2)%6;
			for (PathDetail path : c) {
				ClearingDetail edgeClearing = path.getEdgeAsClearing();
				int rotation = (rot+Tile.getEdgeIntByName(edgeClearing.toString()))%6;
				String edgeName = Tile.getEdgeName(rotation);
				ArrayList<PathDetail> connectedMapEdges = clearing2.getConnectedMapEdges();
				if (connectedMapEdges == null)  {
					continue;
				}
				for (PathDetail ed : connectedMapEdges) {
					if (ed.getEdgeAsClearing().getType().matches(edgeName)) {
						return edgeName;
					}
				}
			}
		}
		return "";
	}
}