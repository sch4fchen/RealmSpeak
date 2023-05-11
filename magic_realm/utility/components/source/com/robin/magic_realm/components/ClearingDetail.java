package com.robin.magic_realm.components;

import java.awt.Color;
import java.awt.Point;
import java.util.*;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.map.Tile;

public class ClearingDetail {
	
	private static final int EDGE_NUM = -1;

	public static final int MAGIC_WHITE		= 0;
	public static final int MAGIC_GRAY		= 1;
	public static final int MAGIC_GOLD		= 2;
	public static final int MAGIC_PURPLE	= 3;
	public static final int MAGIC_BLACK		= 4;
	public static final int MAGIC_VARIED	= 5;
	
	private static Color DEFAULT_MARK_COLOR = Color.green;
	
	public static final char[] MAGIC_CHAR = {'W','R','G','P','B','V'};

	protected TileComponent parent;
	protected int num;
	protected String type;
	protected Point position; // Position within the tile
	protected Point absolutePosition; // Position on the map
	protected boolean[] magic;
	protected int side;
	
	private boolean marked = false;
	protected Color markColor = DEFAULT_MARK_COLOR;
	
	private ArrayList<String> extras;
	
	/**
	 * Use this constructor for edges
	 */
	public ClearingDetail(TileComponent parent,String edge,Point position,int side) {
		this(parent,EDGE_NUM,edge,position,side);
	}
	public ClearingDetail(TileComponent parent,int num,String type,Point position,int side) {
		this.parent = parent;
		this.num = num;
		this.type = type;
		this.position = position;
		this.magic = new boolean[6];
		this.side = side;
		Arrays.fill(magic,false);
		extras = new ArrayList<>();
	}
	public TileLocation getTileLocation() {
		return new TileLocation(this);
	}
	public void addExtra(String val) {
		extras.add(val.trim().toLowerCase());
	}
	public boolean hasExtra(String val) {
		return extras.contains(val.trim().toLowerCase());
	}
	public boolean equals(Object o1) {
		if (o1 instanceof ClearingDetail) {
			ClearingDetail other = (ClearingDetail)o1;
			// this doesn't discriminate clearings on opposite sides of the same tile, but that's not a really important check
			return (parent.getGameObject().equals(other.parent.getGameObject()) && num==other.num && type.equals(other.type));
		}
		return false;
	}
	/**
	 * Convenience method for adding something to this clearing
	 */
	public void add(GameObject thing,CharacterWrapper character) {
		parent.getGameObject().add(thing);
		thing.setThisAttribute("clearing",getNumString());
		if (character!=null) {
			thing.setThisAttribute(Constants.PLAIN_SIGHT);
			thing.setThisAttribute(Constants.DROPPED_BY,character.getGameObject().getStringId());
		}
	}
	public void remove(GameObject thing) {
		parent.getGameObject().remove(thing);
		thing.removeThisAttribute("clearing");
	}
	public boolean isEdge() {
		return num==EDGE_NUM;
	}
	public void setPosition(Point p) {
		position = p;
	}
	public String getName() {
		return "clearing_"+num;
	}
	public int getNum() {
		return num;
	}
	public String getNumString() {
		return isEdge()?("."+type):String.valueOf(num);
	}
	public Point getPosition() {
		return position;
	}
	public String getType() {
		return type;
	}
	public String getTypeCode() {
		return isEdge()?"E":type.toUpperCase().substring(0,1);
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setMagic(int colorId,boolean val) {
		magic[colorId]=val;
	}
	public boolean getMagic(int colorId) {
		return magic[colorId];
	}
	public Color getColor() {
		if (magic[MAGIC_WHITE]) {
			return Color.white;
		}
		else if (magic[MAGIC_GRAY]) {
			return Color.darkGray;
		}
		else if (magic[MAGIC_GOLD]) {
			return MagicRealmColor.GOLD;
		}
		else if (magic[MAGIC_PURPLE]) {
			return MagicRealmColor.PURPLE;
		}
		else if (magic[MAGIC_BLACK]) {
			return Color.black;
		}
		else if (magic[MAGIC_VARIED]) {
			return MagicRealmColor.LIGHTGREEN;
		}
		return null;
	}
	public boolean isNormal() {
		return type.equals("normal");
	}
	public boolean isCave() {
		return type.equals("caves");
	}
	public boolean isWater() {
		return type.equals("water");
	}
	public boolean isFrozenWater() {
		return type.equals("frozen_water");
	}
	public boolean isLighted() {
		if (!parent.getGameObject().hasThisAttribute(Constants.LIGHTED)) return false;
		for (String clearing : parent.getGameObject().getThisAttributeList(Constants.LIGHTED)) {
			if (clearing.matches(String.valueOf(num))) return true;
		}
		return false;
	}
	public void setLighted(boolean light) {
		if (light && !isLighted()) {
			parent.getGameObject().addThisAttributeListItem(Constants.LIGHTED, String.valueOf(num));
		}
		if (!light && isLighted()) {
			parent.getGameObject().removeThisAttributeListItem(Constants.LIGHTED, String.valueOf(num));
		}
	}
	public boolean isMountain() {
		return type.equals("mountain");
	}
	public boolean isWoods() {
		return type.equals("woods") || type.equals("frozen_water"); //treat frozen water clearings as woods clearings
	}
	public int moveCost(CharacterWrapper character,TileLocation currentLocation) {
		int val = 1;
		if (isMountain()) {
			val = character.getMountainMoveCost();
		}
		if (isWater()) {
			if (!character.isTransmorphed()) val++;
			if (character.affectedByKey(Constants.WATER_MOVE_ADJ)) val--;
			if (!character.isTransmorphed() && currentLocation.clearing!=null && currentLocation.clearing.isWater()) {
				GamePool pool = new GamePool(this.parent.getGameObject().getGameData().getGameObjects());
				ArrayList<GameObject> waterSources = pool.find("tile,water_source_clearing");
				if (!waterSources.isEmpty()) {
					if (this.distanceToWaterSource(waterSources)>currentLocation.clearing.distanceToWaterSource(waterSources)) {
						val--;
					}
				}
			}
		}
		if (!isCave() && character.addsOneToMoveExceptCaves()) {
			val++;
		}
		return val;
	}
	public String toString() {
		if (num==-1) {
			return type;
		}
		return "clearing_"+num;
	}
	public String parentToString() { // allows me to see the pointer info
		return super.toString();
	}
	public String shortString() {
		StringBuffer sb = new StringBuffer();
		sb.append(parent.getTileName());
		sb.append(" ");
		sb.append(num);
		return sb.toString();
	}
	public String fullString() {
		StringBuffer sb = new StringBuffer();
		sb.append(parent.getTileName());
		sb.append(" ");
		sb.append(num);
		if (!type.equals("normal")) {
			sb.append(" (");
			sb.append(type);
			sb.append(")");
		}
		Collection<ColorMagic> c = getClearingColorMagic();
		if (c.size()>0) {
			for (ColorMagic cm : c) {
				sb.append(" ");
				sb.append(cm.getColorName());
			}
		}
		return sb.toString();
	}
	/**
	 * @return Returns the marked flag.  A clearing that is marked will be highlighted.
	 */
	public boolean isMarked() {
		return marked;
	}
	/**
	 * @param marked The marked to set.
	 */
	public void setMarked(boolean marked) {
		this.marked = marked;
		setMarkColor(DEFAULT_MARK_COLOR);
	}
	public void setMarkColor(Color c) {
		markColor = c;
	}
	public Color getMarkColor() {
		return markColor;
	}
	/**
	 * @return Returns the parent.
	 */
	public TileComponent getParent() {
		return parent;
	}
	public String getDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append(parent.getTileName());
		sb.append(" ");
		if (isEdge()) {
			sb.append(Tile.convertEdge(type, parent.getRotation()));
			sb.append(" edge");
		}
		else {
			sb.append(num);
			if (!"normal".equals(type)) {
				sb.append(" ("+type+")");
			}
		}
		return sb.toString();
	}
	private int distanceToWaterSource(Collection<GameObject> waterSources) {
		int distance = 0;
		ArrayList<ClearingDetail> touchedWaterClearings = new ArrayList<>();
		
		touchedWaterClearings.add(this);
		if (this.parent.getGameObject().hasThisAttribute("water_source_clearing") && this.parent.getGameObject().getThisAttribute("water_source_clearing").matches(this.getNumString())) {
			return distance;
		}
		
		boolean foundNewClearings = true;
		ArrayList<ClearingDetail> newWaterClearings = new ArrayList<>();
		newWaterClearings.add(this);
		ArrayList<ClearingDetail> waterClearingsToCheck = new ArrayList<>();
		while (foundNewClearings) {
			foundNewClearings = false;
			distance++;
			waterClearingsToCheck.clear();
			waterClearingsToCheck.addAll(newWaterClearings);
			newWaterClearings.clear();
			for (ClearingDetail clearing : waterClearingsToCheck) {
				Collection<PathDetail> c = clearing.getConnectedPaths();
				if (c!=null) {
					for (PathDetail path : c) {
						if (!path.connectsToAnEdge()) {
							if (path.getTo().isWater() && path.getType().matches("river") && !touchedWaterClearings.contains(path.getTo())) {
								if (path.getTo().parent.getGameObject().hasThisAttribute("water_source_clearing") && path.getTo().parent.getGameObject().getThisAttribute("water_source_clearing").matches(path.getTo().getNumString())) {
									return distance;
								}
								foundNewClearings = true;
								touchedWaterClearings.add(path.getTo());
								newWaterClearings.add(path.getTo());
							}
						} else {
							ClearingDetail connectedClearing = path.findConnection(path.getFrom());
							if (connectedClearing.isWater() && path.getType().matches("river") && !touchedWaterClearings.contains(connectedClearing)) {
								if (connectedClearing.parent.getGameObject().hasThisAttribute("water_source_clearing") && connectedClearing.parent.getGameObject().getThisAttribute("water_source_clearing").matches(connectedClearing.getNumString())) {
									return distance;
								}
								foundNewClearings = true;
								touchedWaterClearings.add(connectedClearing);
								newWaterClearings.add(connectedClearing);
							}
						}
					}
				}
			}
		}
		
		return -1;
	}
	/**
	 * Returns a PathDetail that connects two clearings, or null if none.
	 */
	public PathDetail getConnectingPath(ClearingDetail other) {
		if (other.isEdge()) {
			return parent.getEdgePath(Tile.convertEdge(other.getType(),parent.getRotation()));
		}
		ArrayList<PathDetail> paths = getConnectedPaths();
		if (paths!=null) { // might be null if this clearing is not connected to any other
			for (PathDetail path : paths) {
				if (path.findConnection(this)==other) {
					return path;
				}
			}
		}
		return null;
	}
	public ArrayList<PathDetail> getConnectedPaths() {
		return parent.findConnections(this);
	}
	public ArrayList<PathDetail> getConnectedMapEdges() {
		return parent.findConnectedMapEdges(this);
	}
	public ArrayList<PathDetail> getAllConnectedPaths() {
		ArrayList<PathDetail> p;
		ArrayList<PathDetail> allPaths = new ArrayList<>();
		p = getConnectedPaths();
		if (p!=null) allPaths.addAll(p);
		p = getConnectedMapEdges();
		if (p!=null) allPaths.addAll(p);
		return allPaths;
	}
	public int getSide() {
		return side;
	}
	/**
	 * Returns this object if on the correctSide, or the other object if not
	 */
	public ClearingDetail correctSide() {
		if (getParent().getFacingIndex()!=side) {
			return getParent().getClearing(getNumString());
		}
		return this;
	}
	public ArrayList<RealmComponent> getClearingComponentsInPlainSight(CharacterWrapper character) {
		boolean hidden = character.isHidden();
		ArrayList<RealmComponent> plainSight = new ArrayList<>();
		ArrayList<RealmComponent> list = getClearingComponents(false);
		for(RealmComponent item:list) {
			if (item.isPlainSight()) {
				if (!hidden || item.isAtYourFeet(character)) {
					plainSight.add(item);
				}
			}
		}
		return plainSight;
	}
	/**
	 * Returns a collection of all RealmComponents in this clearing.  It does not directly return objects contained
	 * by other objects.  This includes face-up site cards which are in the clearing.
	 */
	public ArrayList<RealmComponent> getClearingComponents() {
		return getClearingComponents(true);
	}
	/**
	 * Returns a collection of all RealmComponents in this clearing.  It does not directly return objects contained
	 * by other objects.
	 * 
	 * @param includeSites		If true, then all treasure locations in the clearing are searched for face-up site cards, which are included.
	 */
	public ArrayList<RealmComponent> getClearingComponents(boolean includeSites) {
		ArrayList<RealmComponent> c = getParent().getRealmComponentsAt(getNum());
		if (includeSites) {
			ArrayList<RealmComponent> more = new ArrayList<>();
			for (RealmComponent rc : c) {
				if (rc.isTreasureLocation() && !rc.isCacheChit()) {
					// Check TLs for face up SITE CARDS, cuz those should be painted too
					for (GameObject thing : rc.getGameObject().getHold()) {
						RealmComponent trc = RealmComponent.getRealmComponent(thing);
						if (trc.isTreasure()) {
							TreasureCardComponent treasure = (TreasureCardComponent)trc;
							if (treasure.isFaceUp()) {
								more.add(treasure);
							}
						}
					}
				}
				else if (rc.isPlayerControlledLeader()) {
					CharacterWrapper leader = new CharacterWrapper(rc.getGameObject());
					more.addAll(leader.getFollowingHirelings());
				}
			}
			c.addAll(more);
		}
		return c;
	}
	/**
	 * Returns a complete collection of all RealmComponents in the clearing, including those that are held by
	 * other objects.  In fact, this will get all objects, regardless of depth.
	 */
	public ArrayList<RealmComponent> getDeepClearingComponents() {
		ArrayList<RealmComponent> found = new ArrayList<>();
		for (RealmComponent rc : getParent().getRealmComponentsAt(getNum())) {
			found.add(rc);
			Collection<GameObject> gos = RealmUtility.getAllGameObjectsIn(rc.getGameObject(),true);
			for (GameObject go : gos) {
				RealmComponent inrc = RealmComponent.getRealmComponent(go);
				if (!found.contains(inrc)) {
					found.add(inrc);
				}
			}
		}
		return found;
	}
	
	/**
	 * @return		The ColorMagic for this clearing NOT including items/chits/characters/treasures/etc.  (JUST the
	 * 				clearing's own color magic)
	 */
	public ArrayList<ColorMagic> getClearingColorMagic() {
		ArrayList<ColorMagic> list = new ArrayList<>();
		if (magic[MAGIC_WHITE]) {
			list.add(new ColorMagic(ColorMagic.WHITE,true));
		}
		if (magic[MAGIC_GRAY]) {
			list.add(new ColorMagic(ColorMagic.GRAY,true));
		}
		if (magic[MAGIC_GOLD]) {
			list.add(new ColorMagic(ColorMagic.GOLD,true));
		}
		if (magic[MAGIC_PURPLE]) {
			list.add(new ColorMagic(ColorMagic.PURPLE,true));
		}
		if (magic[MAGIC_BLACK]) {
			list.add(new ColorMagic(ColorMagic.BLACK,true));
		}
		if (magic[MAGIC_VARIED]) {
			GameWrapper gameWrapper = GameWrapper.findGame(this.parent.getGameObject().getGameData());
			DieRoller monsterDie = gameWrapper.getMonsterDie();
			if (monsterDie != null) {
				int number = monsterDie.getValue(0);
				switch (number) {
				case 1:
				case 4:
					list.add(new ColorMagic(ColorMagic.GRAY,true));
					break;
				case 2:
				case 5:
					list.add(new ColorMagic(ColorMagic.GOLD,true));
					break;
				case 3:
				case 6:
					list.add(new ColorMagic(ColorMagic.PURPLE,true));
					break;
				default:
					break;
				}
			}
			DieRoller nativeDie = gameWrapper.getNativeDie();
			if (nativeDie != null) {
				int number = nativeDie.getValue(0);
				switch (number) {
				case 1:
				case 4:
					list.add(new ColorMagic(ColorMagic.GRAY,true));
					break;
				case 2:
				case 5:
					list.add(new ColorMagic(ColorMagic.GOLD,true));
					break;
				case 3:
				case 6:
					list.add(new ColorMagic(ColorMagic.PURPLE,true));
					break;
				default:
					break;
				}
			}
		}
		if (parent.getGameObject().hasThisAttribute(Constants.MOD_COLOR_SOURCE)) {
			ColorMod colorMod = ColorMod.createColorMod(parent.getGameObject().getThisAttribute(Constants.MOD_COLOR_SOURCE));
			list = colorMod.getModifiedColors(list);
		}
		return list;
	}
	/**
	 * Returns all sources of magic in this clearing, available to everyone.
	 */
	public ArrayList<ColorMagic> getAllSourcesOfColor(boolean checkForColorMods) {
		ArrayList<ColorMagic> list = new ArrayList<>();
		for (RealmComponent rc:getClearingComponents()) {
			list.addAll(SpellUtility.getSourcesOfColor(rc));
		}
		list.addAll(getClearingColorMagic());
		
		ArrayList<ColorMagic> uniqueList = new ArrayList<>();
		for (ColorMagic cm:list) {
			if (!uniqueList.contains(cm)) {
				uniqueList.add(cm);
			}
		}
		
		if (checkForColorMods) {
			uniqueList = ColorMod.getConvertedColorsForThings(getAllActivatedStuff(),uniqueList);
		}
		
		Collections.sort(uniqueList);
		
		return uniqueList;
	}
	public ArrayList<GameObject> getAllActivatedStuff() {
		ArrayList<GameObject> stuff = new ArrayList<>();
		for (RealmComponent rc:getClearingComponents()) {
			for (RealmComponent seen:ClearingUtility.dissolveIntoSeenStuff(rc)) {
				GameObject thing = seen.getGameObject();
				if (thing.hasThisAttribute(Constants.ACTIVATED)) {
					stuff.add(thing);
				}
			}
		}
		return stuff;
	}
	public String getShorthand() {
		return getParent().getTileCode()+getNumString();
	}
	public Point getAbsolutePosition() {
		return absolutePosition;
	}
	public void setAbsolutePosition(Point absolutePosition) {
		this.absolutePosition = absolutePosition;
	}
	public RealmComponent getDwellingWitShelter() {
		for (RealmComponent rc : getClearingComponents()) {
			if (rc.isDwelling() && !rc.getGameObject().hasThisAttribute(Constants.NO_SHELTER)) {
				return rc;
			}
		}
		return null;
	}
	public RealmComponent getDwelling() {
		for (RealmComponent rc : getClearingComponents()) {
			if (rc.isDwelling()) {
				return rc;
			}
		}
		return null;
	}
	public RealmComponent getGuild() {
		for (RealmComponent rc : getClearingComponents()) {
			if (rc.isGuild()) {
				return rc;
			}
		}
		return null;
	}
	public ArrayList<RealmComponent> getRedSpecials() {
		ArrayList<RealmComponent> reds = new ArrayList<>();
		for (RealmComponent rc : getClearingComponents()) {
			if (rc.isRedSpecial()) {
				reds.add(rc);
			}
		}
		return reds;
	}
	public boolean holdsDwelling() {
		return getDwelling()!=null;
	}
	public boolean holdsDwellingWithShelter() {
		return getDwellingWitShelter()!=null;
	}
	public boolean holdsGuild() {
		return getGuild()!=null;
	}
	public boolean holdsRedSpecial() {
		for (RealmComponent rc : getClearingComponents()) {
			if (rc.isRedSpecial()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Returns true if this clearing holds a gold special chit for pickup.  Returns false if the chit is a 
	 * Visitor, or if the chit is a 2nd campaign chit (characters can only carry ONE campaign chit at a time)
	 */
	public boolean holdsGoldSpecial(String currentCampaign) {
		for (RealmComponent rc : getClearingComponents()) {
			if (rc.isGoldSpecial() && !rc.getGameObject().hasThisAttribute("visitor")) {
				if (currentCampaign==null || !rc.getGameObject().hasThisAttribute("campaign")) {
					return true;
				}
			}
		}
		return false;
	}
	private String freeActionKey() {
		return "fa_cl"+num;
	}
	private String freeActionObjectKey() {
		return "fao_cl"+num;
	}
	public void addFreeAction(String action,GameObject go) {
		getParent().getGameObject().addThisAttributeListItem(freeActionKey(),action);
		getParent().getGameObject().addThisAttributeListItem(freeActionObjectKey(),go.getStringId());
	}
	public boolean removeFreeAction(String action) {
		ArrayList<String> list = getParent().getGameObject().getThisAttributeList(freeActionKey());
		if (list!=null) {
			int index = list.indexOf(action);
			if (index>=0) {
				list.remove(index);
				ArrayList<String> objectList = getParent().getGameObject().getThisAttributeList(freeActionObjectKey());
				objectList.remove(index);
				if (list.isEmpty()) {
					getParent().getGameObject().removeThisAttribute(freeActionKey());
					getParent().getGameObject().removeThisAttribute(freeActionObjectKey());
				}
				else {
					getParent().getGameObject().setThisAttributeList(freeActionKey(),list);
					getParent().getGameObject().setThisAttributeList(freeActionObjectKey(),objectList);
				}
				return true;
			}
		}
		return false;
	}
	public ArrayList<String> getFreeActions() {
		return getParent().getGameObject().getThisAttributeList(freeActionKey());
	}
	public GameObject getFreeActionObject(String action) {
		ArrayList<String> list = getParent().getGameObject().getThisAttributeList(freeActionKey());
		if (list!=null) {
			int index = list.indexOf(action);
			if (index>=0) {
				ArrayList<String> objectList = getParent().getGameObject().getThisAttributeList(freeActionObjectKey());
				String id = objectList.get(index);
				return parent.getGameObject().getGameData().getGameObject(Long.valueOf(id));
			}
		}
		return null;
	}
	private String spellEffectKey() {
		return "se_cl"+num;
	}
	public void addSpellEffect(String effect) {
		getParent().getGameObject().addThisAttributeListItem(spellEffectKey(),effect);
	}
	public boolean removeSpellEffect(String effect) {
		ArrayList<String> list = getParent().getGameObject().getThisAttributeList(spellEffectKey());
		if (list!=null) {
			int index = list.indexOf(effect);
			if (index>=0) {
				list.remove(index);
				if (list.isEmpty()) {
					getParent().getGameObject().removeThisAttribute(spellEffectKey());
				}
				else {
					getParent().getGameObject().setThisAttributeList(spellEffectKey(),list);
				}
				return true;
			}
		}
		return false;
	}
	public boolean hasSpellEffect(String effect) {
		return getParent().getGameObject().hasThisAttributeListItem(spellEffectKey(),effect);
	}
	public boolean hasKnownGate(CharacterWrapper character) {
		boolean usableGate = false;
		for (RealmComponent rc:getDeepClearingComponents()) {
			if (rc.isGate()) {
				if (character.hasOtherChitDiscovery(rc.getGameObject().getName()) || character.hasActiveInventoryThisKey(Constants.ALL_GATE)) {
					usableGate = true;
				}
			}
			else if (rc.getGameObject().hasThisAttribute(Constants.NO_GATE)) {
				usableGate = false;
				break;
			}
		}
		return usableGate;
	}
	public static String BL_CONNECT = "bl_con";
	public void setConnectsToBorderland(boolean val) {
		if (val) {
			if (parent.getGameObject().hasThisAttributeListItem(BL_CONNECT,getNumString())) return;
			parent.getGameObject().addThisAttributeListItem(BL_CONNECT,getNumString());
		}
		else {
			parent.getGameObject().removeThisAttributeListItem(BL_CONNECT,getNumString());
		}
	}
	public boolean isConnectsToBorderland() {
		return parent.getGameObject().hasThisAttributeListItem(BL_CONNECT,getNumString());
	}
	
	public void energizeItems() {
		ArrayList<ColorMagic> colors = getClearingColorMagic();
		for (RealmComponent rc : getClearingComponents()) {
			if (rc.isItem()) {
				energizeItem(rc.getGameObject(),colors);
			}
			if (rc.isCharacter()) {
				for (GameObject go : (new CharacterWrapper(rc.getGameObject()).getInventory())) {
					energizeItem(go,colors);
				}
			}
		}
	}
	private static void energizeItem(GameObject item, Collection<ColorMagic> colors) {
		if (!item.hasThisAttribute(Constants.MAGIC_COLOR_BONUS)) return;
		ColorMagic requiredColor = ColorMagic.makeColorMagic(item.getThisAttribute(Constants.MAGIC_COLOR_BONUS),true);
		for (ColorMagic c : colors) {
			if (c.sameColorAs(requiredColor)) item.setThisAttribute(Constants.MAGIC_COLOR_BONUS_ACTIVE);
			break;
		}
	}
	
	public boolean connectionHasThorns(TileLocation other) {
		return this.connectionHasThorns(other.clearing);
	}
	public boolean connectionHasThorns(ClearingDetail other) {
		if (this.getTileLocation().tile==null || other==null || other.getTileLocation().tile==null) return false;
		TileComponent tile = this.getTileLocation().tile;
		TileComponent otherTile = other.getTileLocation().tile;
		if (testThorns(tile,otherTile,other)) return true;
		if (testThorns(otherTile,tile,other)) return true;
		return false;
	}
	private boolean testThorns(TileComponent tile, TileComponent otherTile, ClearingDetail other) {
		if (tile==null||otherTile==null||other==null) return false;
		if (tile.getGameObject().hasThisAttribute(Constants.THORNS)) {
			ArrayList<String> allThorns = tile.getGameObject().getThisAttributeList(Constants.THORNS);
			for (String thorns : allThorns) {
				if (thorns.matches(tile.getGameObject().getStringId()+"_"+this.getNum()+"_"+otherTile.getGameObject().getStringId()+"_"+other.getNum())) return true;
				if (thorns.matches(otherTile.getGameObject().getStringId()+"_"+other.getNum()+"_"+tile.getGameObject().getStringId()+"_"+this.getNum())) return true;
			}
		}
		return false;
	}
}