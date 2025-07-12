package com.robin.magic_realm.components.swing;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import com.robin.game.objects.*;
import com.robin.game.server.GameClient;
import com.robin.general.graphics.GraphicsUtil;
import com.robin.general.graphics.TextType;
import com.robin.general.io.PreferenceManager;
import com.robin.general.swing.ListChooser;
import com.robin.general.swing.MouseUtility;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.*;
import com.robin.magic_realm.map.Tile;

public class CenteredMapView extends JComponent {
	
	public static final int CLICK_CLEARING_ACTION = 1;
	public static final int CLICK_SEASON_ACTION = 2;

	private static BufferedImage tileLayer; // Just the tiles - shared between ALL map views.
	
	private static CenteredMapView singleton;
	public static void initSingleton(GameData data) {
		singleton = new CenteredMapView(data);
		singleton.setScale(0.5); // default for singleton
	}
	public static CenteredMapView getSingleton() {
		if (singleton==null) {
			initSingleton(GameClient.GetMostRecentClient().getGameData());
		}
		return singleton;
	}
	public static void clearTileLayer() {
		if (tileLayer!=null) {
			tileLayer.flush();
			tileLayer = null;
			singleton = null;
		}
	}
	
	private static final float[] DASH = {7.0f,10.0f};
	private static final Font SEASON_FONT = new Font("Dialog",Font.BOLD,12);
	private static final Font INSTRUCTION_FONT = new Font("Dialog",Font.BOLD,18);
	private static final Color MAP_ATTENTION_COLOR = new Color(100,255,100,190);
	private static final Font MAP_ATTENTION_FONT = new Font("Arial",Font.BOLD,24);
	private static final Stroke THIN_STROKE = new BasicStroke(2);
	private static final Stroke THICK_STROKE = new BasicStroke(3);
	private static final Stroke PLOT_PATH_STROKE = new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
	private static final Stroke PLOT_OFFROAD_PATH_STROKE = new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1.0f,DASH,0);
	private static final Color HIGHLIGHT_COLOR = new Color(255,255,255,120);
	
	private static double MIN_SCALE = 0.1;
	private static double MAX_SCALE = 1.0;

	public static final int MAP_BORDER = 10;
	
	protected Hashtable<Point,TileComponent> mapGrid;
	protected Hashtable<Point, Rectangle> mapGridCoor;
	protected Dimension tileSize = new Dimension(TileComponent.TILE_WIDTH,TileComponent.TILE_HEIGHT);
	
	// These are needed for map building
	protected Hashtable<Point, Tile> planningMapGrid;	// Point:Tile
	protected ArrayList<Point> availablePositions;
	
	protected Point mouseHover = null;
	protected Point sticky = null;
	protected Point2D.Double offset = new Point2D.Double(0,0);
	
	private static final int JIGGLE_RADIUS_ALLOWED = 10;
	protected Point startingPoint = null;
	protected int furthestDist;
	protected boolean overJiggle;
	
	protected double scale = 1.0;
	protected Rectangle normalMapRect;
	protected Rectangle borderRect;
	
	protected BufferedImage mapImage = null;
	protected boolean replot = true;
	
	protected GameData gameData; // keep a pointer to the data object for summoning monsters and such
	protected GameWrapper game;
	
	protected int lastMapRepaint = -1;
	
	protected RealmCalendar calendar;
	private TileLocation currentTileLocation = null;
	
	private String mapAttentionMessage = null;
	
	private String markClearingAlertText = null;
	
	private ArrayList<TileLocation> clearingPlot = null; // a collection of TileLocation objects to indicate a path to be drawn on the map
	
	private TileComponent viewTile = null;
	
	private boolean enableShiftFlip;
	private boolean gmFunctions;
	private boolean showEmbellishments = true;
	private boolean hostMap = false; // identifies whether this is the host or not
	
	private MapRightClickMenu mapRightClickMenu;
	
	private Hashtable<Point, Color> positionColors; // a hashtable to mark positions on the map that may not have a tile!
	
	private ArrayList<ChangeListener> changeListeners = null;
	private ArrayList<ActionListener> actionListeners = null;
	
	private boolean mapReady = false;
	private String anchorTileName = "Borderland";
	
	private TileComponent tileBeingPlaced = null;
	private ChangeListener tilePlacementListener = null;
	private ImageIcon tileImageIcon = null;
	
	private boolean clearingHighlight = false;
	private boolean showSeasonIcon = false;
	private boolean drawSeasonInfo = false;
	
	private int chatLineBufferSize = 10;
	private int chatLinesToShow = 10;
	private ArrayList<ChatLine> chatLines;
	private Hashtable<String,ChatStyle> chatStyles;
	
	private int interpolation = 0;
	private String rendering = "interpolation";
	
	private PreferenceManager prefs;
	
	/**
	 * Constructor requires that the map has already been built - this just displays a built map
	 */
	public CenteredMapView(GameData data) {
		this(data,true,false);
		prefs = new PreferenceManager("RealmSpeak","map.cfg");
		prefs.loadPreferences();
		interpolation = prefs.getInt(rendering);
	}
	public CenteredMapView(GameData data,boolean enableShiftFlip,boolean enableGmFunctions) {
		this.gmFunctions = enableGmFunctions;
		mapRightClickMenu = new MapRightClickMenu();
		this.enableShiftFlip = enableShiftFlip;
		setDoubleBuffered(true);
		this.gameData = data;
		game = GameWrapper.findGame(gameData);
		calendar = RealmCalendar.getCalendar(data);
		mapGrid = new Hashtable<>();
		planningMapGrid = new Hashtable<>();
		availablePositions = new ArrayList<>();
		chatLines = new ArrayList<>();
		chatStyles = new Hashtable<>();
		for (ChatStyle style:ChatStyle.styles) {
			chatStyles.put(style.getStyleName(),style);
		}
		anchorTileName = findAnchorTileName();
		
		positionColors = new Hashtable<>();
		
		updateGrid();
		
		updateMapSize();
		MouseInputAdapter mia = new MouseInputAdapter() {
			public void mousePressed(MouseEvent ev) {
				mapAttentionMessage = null;
				startingPoint = ev.getPoint();
				overJiggle = false;
				sticky = ev.getPoint();
				
				if (tileBeingPlaced==null) return;
				
				if (MouseUtility.isRightOrControlClick(ev)) { // right or middle mouse click
					if (gmFunctions && ev.getButton() == 2) {				
						clearTileBeingPlaced();
						currentTileLocation = null;
						rebuildFromScratch();
						updateGrid();
						return;
					}
					
					int r = tileBeingPlaced.getRotation();
					r = (r+1) % 6;
					tileBeingPlaced.setRotation(r);
					updateTileBeingPlaced();
				}
				else {
					// Place tile and fire listener if over a valid placement location
					placeTile(ev.getPoint());
				}
			}
			public void mouseDragged(MouseEvent ev) {
				Point p = ev.getPoint();
				
				if (startingPoint==null) { // not sure how this ever happens, but this will fix the problem
					startingPoint = ev.getPoint();
				}
				int dist = (int)startingPoint.distance(p);
				if (dist>JIGGLE_RADIUS_ALLOWED) {
					overJiggle = true;
				}
				
				mouseHover = p;
				if (sticky==null) { // not sure how this can happen, but it did once!
					sticky = p;
				}
				offset.x += (p.x - sticky.x);
				offset.y += (p.y - sticky.y);
				sticky = p;
				fixOffset();
			}
			public void mouseReleased(MouseEvent ev) {
				if (!overJiggle) {
					mouseClicked(ev);
				}
				sticky = null;
				startingPoint = null;
			}
			public void mouseClicked(MouseEvent ev) {
				mapAttentionMessage = null;
				if (tileBeingPlaced==null) {
					if (MouseUtility.isRightOrControlClick(ev)) { // right mouse click
						TileLocation tl = getTileLocationAtPoint(ev.getPoint());
						mapRightClickMenu.setTileLocation(tl);
						mapRightClickMenu.show(CenteredMapView.this,ev.getPoint().x,ev.getPoint().y);
					}
					else if (startingPoint!=null) {
						if (drawSeasonInfo) {
								fireActionPerformed(CLICK_SEASON_ACTION,"");
						}
						else {
							TileLocation tl = getTileLocationAtPoint(startingPoint);
							if (tl!=null && tl.isInClearing()) {
								fireActionPerformed(CLICK_CLEARING_ACTION,tl.asKey());
							}
						}
					}
				}
			}
			public void mouseMoved(MouseEvent ev) {
				mouseHover = ev.getPoint();
				currentTileLocation = getTileLocationAtPoint(mouseHover);
				updateShiftFlip(ev.isShiftDown());
			}
			public void mouseExited(MouseEvent ev) {
				mouseHover = null;
				currentTileLocation = null;
				updateShiftFlip(false);
			}
		};
		addMouseListener(mia);
		addMouseMotionListener(mia);
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				int d = e.getWheelRotation();
				setScale(scale - (d*0.1*scale));
				fireStateChanged();
			}
		});
		doLayout();
		centerMap();
	}
	private String findAnchorTileName() {
		GamePool pool = new GamePool(gameData.getGameObjects());
		for (GameObject obj : pool.find("tile")) {
			if (obj.hasThisAttribute(Constants.ANCHOR_TILE)) {
				return obj.getName();
			}
		}
		throw new IllegalStateException("Borderland or other staring tile is missing from tiles!!");
	}
	public Rectangle getNormalMapRectangle() {
		return normalMapRect;
	}
	public void setClearingHighlight(boolean val) {
		clearingHighlight = val;
	}
	public void setShowSeasonIcon(boolean val) {
		showSeasonIcon = val;
		repaint();
	}
	public void setShowChatLines(int val) {
		chatLinesToShow = val;
		repaint();
	}
	public void updateShiftFlip(boolean shiftDown) {
		if (enableShiftFlip && shiftDown && currentTileLocation!=null) {
			if (!currentTileLocation.tile.equals(viewTile)) {
				viewTile = currentTileLocation.tile;
				replot = true;
			}
		}
		else {
			if (viewTile!=null) {
				viewTile = null;
				replot = true;
			}
		}
		if (enableShiftFlip && tileBeingPlaced!=null) {
			tileBeingPlaced.setShowFlipside(shiftDown);
			updateTileBeingPlacedIcon();			
		}
		repaint();
	}
	public void addChangeListener(ChangeListener listener) {
		if (changeListeners==null) {
			changeListeners = new ArrayList<>();
		}
		if (!changeListeners.contains(listener)) {
			changeListeners.add(listener);
		}
	}
	public void removeChangeListener(ChangeListener listener) {
		if (changeListeners!=null) {
			changeListeners.remove(listener);
			if (changeListeners.isEmpty()) {
				changeListeners = null;
			}
		}
	}
	private void fireStateChanged() {
		if (changeListeners!=null) {
			ChangeEvent ev = new ChangeEvent(this);
			for (ChangeListener listener : changeListeners) {
				listener.stateChanged(ev);
			}
		}
	}
	public void addActionListener(ActionListener listener) {
		if (actionListeners==null) {
			actionListeners = new ArrayList<>();
		}
		if (!actionListeners.contains(listener)) {
			actionListeners.add(listener);
		}
	}
	public void removeActionListener(ActionListener listener) {
		if (actionListeners!=null) {
			actionListeners.remove(listener);
			if (actionListeners.isEmpty()) {
				actionListeners = null;
			}
		}
	}
	private void fireActionPerformed(int id,String command) {
		if (actionListeners!=null) {
			ActionEvent ev = new ActionEvent(this,id,command);
			for (ActionListener listener : actionListeners) {
				listener.actionPerformed(ev);
			}
		}
	}
	public boolean isMapReady() {
		return mapReady;
	}
	public boolean isTileAtPosition(Point pos) {
		return mapGrid.get(pos)!=null;
	}
	public void updateTilesStyle() {
		ArrayList<GameObject> tileObjects = RealmObjectMaster.getRealmObjectMaster(gameData).getTileObjects();
		for (GameObject obj : tileObjects) {
			TileComponent tc = (TileComponent)RealmComponent.getRealmComponent(obj);
			tc.initFilepaths();
			tc.doRepaint();
		}
	}
	public void updateGrid() {
		mapGrid.clear();
		planningMapGrid.clear();
		availablePositions.clear();
		RealmObjectMaster.getRealmObjectMaster(gameData).resetTileObjects();
		Collection<GameObject> tileObjects = RealmObjectMaster.getRealmObjectMaster(gameData).getTileObjects();
		
		// Add all the tiles
		String anchorTileName = this.anchorTileName;
		int emptyCount = 0;
		ArrayList<Point> points = new ArrayList<>();
		for (GameObject obj : tileObjects) {
			if (obj.hasThisAttribute(Constants.ANCHOR_TILE)) {
				anchorTileName = obj.toString();
			}
			TileComponent tc = (TileComponent)RealmComponent.getRealmComponent(obj); // ClassCastException here when building triple boards?
			tc.resetClearingPositions();
			tc.doRepaint();
			String pos = obj.getAttribute(Tile.MAP_GRID,Tile.MAP_POSITION);
			String rot = obj.getAttribute(Tile.MAP_GRID,Tile.MAP_ROTATION);
			
			if (pos!=null && rot!=null) {
				tc.setRotation(Integer.parseInt(rot));
				Point gp = GraphicsUtil.asPoint(pos);
				points.add(gp);
				addTile(tc,gp);
			}
			else {
				emptyCount++;
			}
		}
		
		mapReady = (emptyCount==0);
		if (emptyCount>0) {
			if (emptyCount<tileObjects.size()) {
				// Add a border of empty tiles
				ArrayList<Point> pos = Tile.findAvailableMapPositions(planningMapGrid,anchorTileName);
				for (Point gp : pos) {
					EmptyTileComponent empty = new EmptyTileComponent();
					availablePositions.add(gp);
					addTile(empty,gp);
				}
			}
			else {
				// Add a single empty tile
				EmptyTileComponent empty = new EmptyTileComponent();
				Point n = new Point(0,0);
				availablePositions.add(n);
				addTile(empty,n);
			}
		}
		else {
			// Set adjacent tiles so paths can be connected
			ClearingUtility.initAdjacentTiles(mapGrid);
		}
	}
	public String getMapAttentionMessage() {
		return mapAttentionMessage;
	}
	public void setMapAttentionMessage(String val) {
		this.mapAttentionMessage = val;
	}
	public void clearMapAttentionMessage() {
		this.mapAttentionMessage = null;
	}
	public String getMarkClearingAlertText() {
		return markClearingAlertText;
	}
	public void setMarkClearingAlertText(String markClearingAlertText) {
		this.markClearingAlertText = markClearingAlertText;
	}
	public void clearMarkClearingAlertText() {
		this.markClearingAlertText = null;
	}
	/**
	 * Centers the whole map (good a place as any I suppose)
	 */
	public void centerMap() {
		double cx = (borderRect.width>>1);
		double cy = (borderRect.height>>1);
		centerOn(new Point2D.Double(cx,cy));
	}
	private Point findLocationPoint(TileLocation tl) {
		if (tl!=null) {
			if (tl.tile.getLastPaintLocation()==null) {
				// If the map hasn't yet been plotted, then do it now!!
				paint(getGraphics());
			}
			Point gridPos = findGridPos(tl.tile);
			if (gridPos!=null) {
	//			Rectangle plotPos = (Rectangle)mapGridCoor.get(gridPos);
				Point clearingPos;
				if (tl.hasClearing()) {
					ClearingDetail clearing = tl.clearing.correctSide();
					clearingPos = clearing.getAbsolutePosition();
					if (tl.isBetweenClearings()) {
						ClearingDetail otherClearing = tl.getOther().clearing.correctSide();
						clearingPos = GraphicsUtil.midPoint(clearingPos,otherClearing.getAbsolutePosition());
					}
				}
				else if (tl.isBetweenTiles()) {
					Point c1 = new Point(tl.tile.getLastPaintLocation().getLocation());
					c1.x += (TileComponent.iconDimensions.width>>1);
					c1.y += (TileComponent.iconDimensions.height>>1);
					Point c2 = new Point(tl.getOther().tile.getLastPaintLocation().getLocation());
					c2.x += (TileComponent.iconDimensions.width>>1);
					c2.y += (TileComponent.iconDimensions.height>>1);
					clearingPos = GraphicsUtil.midPoint(c1,c2);
				}
				else {
					// I hate the offroad point for the dotted lines
	//				Point p = tl.tile.getOffroadPoint();
	//				clearingPos = new Point(p.x+plotPos.x,p.y+plotPos.y);
					Rectangle r = tl.tile.getLastPaintLocation();
					clearingPos = new Point(r.x+(r.width>>1),r.y+(r.height>>1));
				}
				if (clearingPos!=null) {
					return new Point(clearingPos.x,clearingPos.y);
				}
			}
		}
		
		return null;
	}
	private Point findGridPos(TileComponent tile) {
		for (Point gridPos : mapGrid.keySet()) {
			TileComponent t = mapGrid.get(gridPos);
			if (tile==t) { // found it
				return gridPos;
			}
		}
		return null;
	}
	/**
	 * Centers the map so that the specified location lies in the exact center of the field of view
	 */
	public void centerOn(TileLocation tl) {
		if (tl!=null) {
			// Center on location
			Point p = findLocationPoint(tl);
			if (p!=null) {
				centerOn(p);
			}
		}
	}
	/**
	 * Centers the map so that the real (unscaled) point lies in the exact center of the field of view
	 */
	public void centerOn(Point realMapPoint) {
		centerOn(new Point2D.Double(realMapPoint.x,realMapPoint.y));
	}
	/**
	 * Centers the map so that the real (unscaled) point lies in the exact center of the field of view
	 */
	private void centerOn(Point2D.Double realMapPoint) {
		// First, scale the mapPoint to the current scale
		double scaledX = realMapPoint.getX()*scale;
		double scaledY = realMapPoint.getY()*scale;
		
		// Calculate offset based on this center
		Dimension size = getSize();
		Point center = new Point(size.width>>1,size.height>>1);
		offset.x = (center.x-scaledX);
		offset.y = (center.y-scaledY);
		
		repaint();
	}
	private void fixOffset() {
		Dimension size = getSize();
		Point center = new Point(size.width>>1,size.height>>1);
		Point offScreenLimit = new Point(center.x - (int)(borderRect.width*scale),center.y - (int)(borderRect.height*scale));
		if (offset.x>center.x) offset.x = center.x;
		if (offset.x<offScreenLimit.x) offset.x = offScreenLimit.x;
		if (offset.y>center.y) offset.y = center.y;
		if (offset.y<offScreenLimit.y) offset.y = offScreenLimit.y;
		repaint();
	}
	public double getScale() {
		return scale;
	}
	public void setScale(double val) {
		val = Math.max(MIN_SCALE,val);
		val = Math.min(MAX_SCALE,val);
		
		// Remember the unscaled map point before scaling, so it can be centered there afterwards
		Dimension size = getSize();
		Point2D.Double center = new Point2D.Double(size.width/2,size.height/2);
		double normalX = (center.x - offset.x)/scale;
		double normalY = (center.y - offset.y)/scale;
		this.scale = val;
		centerOn(new Point2D.Double(normalX,normalY));
	}
	/**
	 * For testing
	 */
	public void flipTileAtPoint(Point p) {
		TileLocation tl = getTileLocationAtPoint(p);
		if (tl!=null) {
			tl.tile.flip();
			replot = true;
			repaint();
		}
	}
	/**
	 * For testing ONLY
	 */
	public void revealChitsAtPoint(Point p) {
		TileLocation tl = getTileLocationAtPoint(p);
		if (tl!=null) {
			tl.tile.setChitsFaceUp();
			replot = true;
			repaint();
		}
	}
	public void markClearingConnectionsAtPoint(Point p) {
		TileLocation tl = getTileLocationAtPoint(p);
		if (tl!=null) {
			if (tl.hasClearing()) {
				markClearingConnections(tl.clearing,true);
			}
			else {
				System.out.println("Marking tile: "+tl.tile.getTileName());
				tl.tile.setMarked(true);
			}
			replot = true;
			repaint();
		}
	}
	public void markAdjacentTiles(TileComponent tile,boolean setMark) {
		markAdjacentTiles(tile,setMark,0);
	}
	public void markAdjacentTiles(TileComponent tile,boolean setMark,int recurse) {
		for (TileComponent adj:tile.getAllAdjacentTiles()) {
			if (adj.isMarked()!=setMark) {
				adj.setMarked(setMark);
			}
			if (recurse>0) {
				markAdjacentTiles(adj,setMark,recurse-1); // I know, a clumsy solution, but it works.  Wouldn't want to recurse more than about twice!
			}
		}
		replot = true;
		repaint();
	}
	public void markAllTiles(boolean setMark) {
		for (TileComponent tile : mapGrid.values()) {
			if (tile.isMarked()!=setMark) {
				tile.setMarked(setMark);
			}
		}
		replot = true;
		repaint();
	}
	public ArrayList<ClearingDetail> getAllMarkedClearings() {
		ArrayList<ClearingDetail> list = new ArrayList<>();
		for (TileComponent tile :  mapGrid.values()) {
			for (ClearingDetail clearing : tile.getClearings()) {
				if (clearing.isMarked()) {
					list.add(clearing);
				}
			}
		}
		return list;
	}
	public void markAllClearings(boolean setMark) {
		for (TileComponent tile : mapGrid.values()) {
			for (ClearingDetail clearing : tile.getClearings()) {
				if (clearing.isMarked()!=setMark) {
					clearing.setMarked(setMark);
				}
			}
		}
		replot = true;
		repaint();
	}
	public void markAllMapEdges(boolean setMark) {
		String anchorTileName = this.anchorTileName;
		ArrayList<ClearingDetail> allMapEdges = new ArrayList<>();
		for (TileComponent tile : mapGrid.values()) {
			allMapEdges.addAll(tile.getMapEdges());
			if (tile.getGameObject().hasThisAttribute(Constants.ANCHOR_TILE)) {
				anchorTileName = tile.toString();
			}
		}
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameData);
		Collection<String> keyVals = GamePool.makeKeyVals(hostPrefs.getGameKeyVals());
		Hashtable<Point, Tile> mapGrid = Tile.readMap(gameData,keyVals);
		for (ClearingDetail detail:allMapEdges) {
			// Only mark those edges that are actually connected to the Borderland
			Tile mapTile = mapGrid.get(Tile.getPositionFromGameObject(detail.getParent().getGameObject()));
			ArrayList<PathDetail> paths = detail.getConnectedPaths();
			if (paths!=null) {
				for (PathDetail path:paths) {
					ClearingDetail other = path.findConnection(detail);
					if (mapTile.connectsToTilename(mapGrid,"clearing_"+other.getNum(),anchorTileName)) {
						detail.setMarked(setMark);
						break;
					}
				}
			}
		}
	}
	public static void markMapEdges(ClearingDetail clearing,boolean setMark) {
		for (PathDetail path : clearing.getConnectedPaths()) {
			if (path.connectsToMapEdge()) {
				path.getEdgeAsClearing().setMarked(setMark);
			}
		}
	}
	public void markClearings(String clearingType,boolean setMark) {
		for (TileComponent tile : mapGrid.values()) {
			ArrayList<ClearingDetail> c = tile.getClearings(clearingType);
			if (!c.isEmpty()) {
				for (ClearingDetail clearing : c) {
					clearing.setMarked(setMark);
				}
//				tile.doRepaint(); // KEEP FOR NOW
			}
		}
		replot = true;
		repaint();
	}
	public void markRuinsClearings(boolean setMark) {
		for (TileComponent tile : mapGrid.values()) {
			if (!tile.getTileType().matches("R")) continue;
			ArrayList<ClearingDetail> c = tile.getClearings();
			if (!c.isEmpty()) {
				for (ClearingDetail clearing : c) {
					clearing.setMarked(setMark);
				}
//				tile.doRepaint(); // KEEP FOR NOW
			}
		}
		replot = true;
		repaint();
	}
	public void markClearing(ClearingDetail clearing,boolean setMark) {
		clearing.setMarked(setMark);
		replot = true;
		repaint();
	}
	public void markClearings(ArrayList<ClearingDetail> clearings,boolean setMark) {
		markClearings(clearings,setMark,null);
	}
	public void markClearings(ArrayList<ClearingDetail> clearings,boolean setMark,Color color) {
		for (ClearingDetail clearing : clearings) {
			clearing.setMarked(setMark);
			if (color!=null) {
				clearing.setMarkColor(color);
			}
//			clearing.getParent().doRepaint(); // KEEP FOR NOW
		}
		replot = true;
		repaint();
	}
	public void markClearingConnections(ClearingDetail clearing,boolean setMark) {
		ArrayList<PathDetail> c = clearing.getConnectedPaths();
		for (PathDetail path : c) {
			ClearingDetail connectedClearing = path.findConnection(clearing);
			connectedClearing.setMarked(setMark);
//			connectedClearing.getParent().doRepaint(); // KEEP FOR NOW
		}
		replot = true;
		repaint();
	}
	public ArrayList<ClearingDetail> markClearingsInTile(TileComponent tile,Collection<String> types,boolean includeAdjacent) {
//		tile.doRepaint(); // KEEP FOR NOW
		ArrayList<ClearingDetail> list = new ArrayList<>();
		for (ClearingDetail clearing : tile.getClearings()) {
			if (types==null||types.contains(clearing.getType())) {
				clearing.setMarked(true);
				list.add(clearing);
			}
		}
		if (includeAdjacent) {
			for (TileComponent adj : tile.getAllAdjacentTiles()) {
				list.addAll(markClearingsInTile(adj,types,false));
			}
		}
		replot = true;
		repaint();
		return list;
	}
	/**
	 * For testing ONLY
	 */
	public void summonMonstersAtPoint(Point p) {
		TileLocation tl = getTileLocationAtPoint(p);
		if (tl!=null) {
			int monsterDie = RandomNumber.getHighLow(1,6); // Change number here for testing
			if (tl.hasClearing()) {
				SetupCardUtility.summonMonsters(new ArrayList<GameObject>(),tl,gameData,true,true,monsterDie,null,-1);
			}
			else {
				SetupCardUtility.resetDenizens(gameData,monsterDie,false);
			}
			replot = true;
			repaint();
		}
	}
	public TileLocation getTileLocationAtPoint(Point p) {
		double normalX = (p.x - offset.x)/scale;
		double normalY = (p.y - offset.y)/scale;
		Point normal = new Point((int)normalX,(int)normalY);
		for (Point gridPos : mapGridCoor.keySet()) {
			Rectangle rect = mapGridCoor.get(gridPos);
			if (rect.contains(normal)) {
				TileComponent tile = mapGrid.get(gridPos);
				if (tile!=null) { // not sure how this is possible, but it happened once during map builder
					Shape shape = tile.getShape(rect.x,rect.y,tileSize.width);
					if (shape.contains(normal)) {
						Point relative = new Point(normal.x - rect.x,normal.y - rect.y);
						if (viewTile==tile) {
							viewTile.setShowFlipside(true);
						}
						ClearingDetail clearing = tile.findClearing(relative);
						if (viewTile==tile) {
							viewTile.setShowFlipside(false);
						}
						if (clearing!=null) {
							return new TileLocation(clearing);
						}
						return new TileLocation(tile);
					}
				}
			}
		}
		return null;
	}
	public void resetScale() {
		this.scale = 1.0;
		repaint();
	}
	private void addTile(TileComponent tile,Point pos) {
		mapGrid.put(pos,tile);
		if (!(tile instanceof EmptyTileComponent)) {
			Tile rep = new Tile(tile.getGameObject());
			rep.setMapPosition(pos);
			rep.setRotation(tile.getRotation());
			planningMapGrid.put(pos,rep);
		}
//		tileSize = tile.getTileSize();
	}
	public Collection<TileComponent> getTiles() {
		return mapGrid.values();
	}
	
	public Point convertGridToCoordinate(Point gridPoint) {
		int colWidth = (tileSize.width*3)/4;
		int rowHeight = tileSize.height;
		int rowAdjust = tileSize.height>>1;
		int x = (gridPoint.x * colWidth) - borderRect.x;
		int y = ((gridPoint.x * rowAdjust) + (gridPoint.y * rowHeight)) - borderRect.y;
		return new Point(x,y);
	}
	public void updateMapSize() {
		int colWidth = (tileSize.width*3)/4;
		int rowHeight = tileSize.height;
		int rowAdjust = tileSize.height>>1;
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (Enumeration<Point> e=mapGrid.keys();e.hasMoreElements();) {
			Point pos = e.nextElement();
			int x = pos.x * colWidth;
			int y = (pos.x * rowAdjust) + (pos.y * rowHeight);
			if (x<minX) {
				minX = x;
			}
			if (x>maxX) {
				maxX = x;
			}
			if (y<minY) {
				minY = y;
			}
			if (y>maxY) {
				maxY = y;
			}
		}
		if (mapGrid.isEmpty()) {
			minX = 1;
			maxX = 1;
			minY = 1;
			maxY = 1;
		}
		normalMapRect = new Rectangle(minX,minY,maxX+tileSize.width-minX,maxY+tileSize.height-minY);
		borderRect = new Rectangle(
				normalMapRect.x - MAP_BORDER,
				normalMapRect.y - MAP_BORDER,
				normalMapRect.width + (MAP_BORDER<<1),
				normalMapRect.height + (MAP_BORDER<<1));
		
		offset.x = borderRect.x + (borderRect.width>>1);
		offset.y = borderRect.y + (borderRect.height>>1);
		
		mapGridCoor = new Hashtable<>();
		for (Point gridPos : mapGrid.keySet()) {
			Point plotPos = convertGridToCoordinate(gridPos);
			Rectangle rect = new Rectangle(plotPos.x,plotPos.y,tileSize.width,tileSize.height);
			mapGridCoor.put(gridPos,rect);
		}
	}
	public Point2D.Double getOffset() {
		return offset;
	}
	public void setOffset(Point2D.Double p) {
		offset = p;
	}
	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		int mr = game.getMapRepaint();
		if (mr!=lastMapRepaint) {
			replot = true;
			lastMapRepaint = mr;
			for (GameObject go:RealmObjectMaster.getRealmObjectMaster(gameData).getTileObjects()) {
				go.bumpVersion();
			}
		}
		if (viewTile!=null) {
			viewTile.setShowFlipside(true);
		}
		if (mapImage==null) {
			if (tileLayer==null) {
				tileLayer = new BufferedImage(borderRect.width,borderRect.height,BufferedImage.TYPE_3BYTE_BGR);
				Graphics ig = tileLayer.getGraphics();
				ig.setColor(UIManager.getColor("ScrollPane.background"));
				ig.fillRect(0,0,borderRect.width,borderRect.height);
//				g.setColor(Color.black);
//				g.drawRect(0,0,borderRect.width-1,borderRect.height-1);
			}
			mapImage = new BufferedImage(borderRect.width,borderRect.height,BufferedImage.TYPE_3BYTE_BGR);
			replot = false;
			paintMap(mapImage.getGraphics(),true);
		}
		if (replot && mapImage!=null) {
			replot = false;
			paintMap(mapImage.getGraphics(),false); // exclude shadow on any replots (shadow is already there)
		}
		
		if (mapImage==null) { // Not sure how this is possible, but at least we wont continue here!
			return;
		}
		int w = mapImage.getWidth();
		int h = mapImage.getHeight();
		
		int sx = (int)offset.getX();
		int sy = (int)offset.getY();
		int sw = (int)(w * scale);
		int sh = (int)(h * scale);
		
		switch (interpolation) {
		case 1:
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			break;
		case 2:
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			break;
		default:
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		}

		g.drawImage(mapImage,sx,sy,sx+sw-1,sy+sh-1,0,0,w,h,null);
		g.setColor(Color.darkGray);
		g.drawRect(sx,sy,sw,sh);
		
		// Draw clearingPlot, if any
		if (clearingPlot!=null && clearingPlot.size()>1) {
			g.setColor(Color.yellow);
			Point last = null;
			for (TileLocation tl : clearingPlot) {
				g.setStroke(tl.hasClearing()?PLOT_PATH_STROKE:PLOT_OFFROAD_PATH_STROKE);
				Point p = findLocationPoint(tl);
				if (p!=last) { // only draw if the points are different (hence a line)
					double csx = p.x*scale;
					double csy = p.y*scale;
					
					p.x = ((int)csx)+sx;
					p.y = ((int)csy)+sy;
					
					if (last!=null) {
						g.drawLine(last.x,last.y,p.x,p.y);
					}
					last = p;
				}
			}
		}
		
		// Draw position colors, if any
		g.setStroke(THIN_STROKE);
		for (Point pos : positionColors.keySet()) {
			Color c = positionColors.get(pos);
			g.setColor(c);
			Rectangle plotPos = mapGridCoor.get(pos);
			if (plotPos==null) {
				Point p = convertGridToCoordinate(pos);
				plotPos = new Rectangle(p.x,p.y,1,1); // the width and height are just dummy numbers here
			}
			Shape shape = TileComponent.getHexShape(plotPos.x,plotPos.y,sx,sy,3,scale);
			g.draw(shape);
		}
		
		drawSeasonInfo = false;
		if (showSeasonIcon) {
			ImageIcon icon = calendar.getFullSeasonIcon(game.getMonth());
			if (icon!=null) {
				Dimension size = getSize();
				String seasonName = calendar.getSeasonName(game.getMonth());
				int iw = icon.getIconWidth();
				int x = size.width-iw-5;
				int y = 5;
				g.setStroke(THICK_STROKE);
				g.setColor(Color.black);
				g.drawRect(x-1,4,101,101);
				g.drawImage(icon.getImage(),x,y,null);
				boolean usingWeather = calendar.isUsingWeather();
				String weather = calendar.getWeatherName(game.getMonth());
				
				g.setColor(HIGHLIGHT_COLOR);
				g.fillRect(x+10,7,iw-20,17);
				if (usingWeather) g.fillRect(x+10,87,iw-20,17);
				
				g.setColor(Color.black);
				g.setFont(SEASON_FONT);
				GraphicsUtil.drawCenteredString(g,x,3,iw,20,seasonName);
				
				if (mouseHover!=null && mouseHover.x>=x && mouseHover.y<icon.getIconHeight()+y) {
					drawSeasonInfo = true;
				}
				
				if (usingWeather) {
					GraphicsUtil.drawCenteredString(g,x,85,iw,20,weather);
					if (drawSeasonInfo) {
						String desc = calendar.getSeasonDescription(game.getMonth());
						g.setColor(Color.white);
						int ww = Math.min(size.width,200);
						int wh = Math.min(size.height-105,desc.length()>0?150:100);
						int wx = x+101-ww;
						int wy = y+101;
						g.fillRect(wx,wy,ww,wh);
						g.setColor(Color.black);
						g.drawRect(wx,wy,ww,wh);
						
						int days = calendar.getDays(game.getMonth());
						int basic = calendar.getBasicPhases(game.getMonth());
						int sunlight = calendar.getSunlightPhases(game.getMonth());
						int sheltered = calendar.getShelteredPhases(game.getMonth());
						
						int dx = wx+10;
						int dy = wy+25;
						g.drawString("Days:",dx,dy);
						g.drawString(String.valueOf(days),dx+100,dy);
						dy+=20;
						g.drawString("Basic:",dx,dy);
						g.drawString(String.valueOf(basic),dx+100,dy);
						dy+=20;
						g.drawString("Sunlight:",dx,dy);
						g.drawString(String.valueOf(sunlight),dx+100,dy);
						dy+=20;
						g.drawString("Sheltered:",dx,dy);
						g.drawString(String.valueOf(sheltered),dx+100,dy);
						if (desc.length()>0) {
							dy+=10;
							TextType tt = new TextType(desc,ww-60,"BOLD_BLUE");
							tt.draw(g,dx+20,dy);
						}
					}
				}
			}
		}
		
		// Draw mouse-over clearing contents, if any
		if (currentTileLocation!=null && !(currentTileLocation.tile instanceof EmptyTileComponent) && showEmbellishments && !drawSeasonInfo) {
			if (clearingHighlight && currentTileLocation.isInClearing()) {
				Point p = findLocationPoint(currentTileLocation);
				double csx = (p.x-TileComponent.CLEARING_RADIUS)*scale;
				double csy = (p.y-TileComponent.CLEARING_RADIUS)*scale;
				double csr = (TileComponent.CLEARING_RADIUS*scale)*2;
				Stroke old = g.getStroke();
				g.setColor(HIGHLIGHT_COLOR);
				g.fillOval((int)csx+sx,(int)csy+sy,(int)csr,(int)csr);
				g.setColor(Color.blue);
				g.setStroke(THICK_STROKE);
				g.drawOval((int)csx+sx,(int)csy+sy,(int)csr,(int)csr);
				g.setStroke(old);
				g.setFont(INSTRUCTION_FONT);
				String val;
				if (currentTileLocation.clearing.isEdge()) {
					val = "Exit";
				}
				else {
					val = String.valueOf(currentTileLocation.clearing.getNum());
				}
				GraphicsUtil.drawCenteredString(g,(int)csx+sx,(int)csy+sy,(int)csr,(int)csr,val);
			}
			
			Dimension maxSize = getSize();
			int contentsX = 5;
			int contentsY = 5;
			Collection<RealmComponent> c = currentTileLocation.tile.getOffroadRealmComponents();
			if (currentTileLocation.clearing!=null) {
				c.addAll(currentTileLocation.clearing.getClearingComponents());
			}
			if (!c.isEmpty()) {
				ArrayList<RealmComponent> seenContents = new ArrayList<>();
				ArrayList<RealmComponent> plainSightContents = new ArrayList<>(); // These are the dropped (not abandoned) items in the clearing
				
				// First, see if there are any spells affecting this tile
				SpellMasterWrapper spellMaster = SpellMasterWrapper.getSpellMaster(gameData);
				ArrayList<SpellWrapper> spells = spellMaster.getAffectingSpells(currentTileLocation.tile.getGameObject());
				for (SpellWrapper spell:spells) {
					RealmComponent rc = RealmComponent.getRealmComponent(spell.getGameObject());
					seenContents.add(rc);
				}
				
				// Now check all the rest
				for (RealmComponent rc : c) {
					if (rc instanceof StateChitComponent) {
						StateChitComponent state = (StateChitComponent)rc;
						if (!state.hasBeenSeen()) {
							continue;	// I hate using continues, but this is really the best choice here
						}
					}
					if (rc.isPlainSight()) {
						plainSightContents.add(rc);
					}
					else {
						seenContents.add(rc);
					}
				}
				drawMouseHoverContents(g,seenContents,contentsX,contentsY,maxSize);
				if (!plainSightContents.isEmpty()) {
					g.setColor(Color.blue);
					g.setFont(INSTRUCTION_FONT);
					g.drawString("In Plain Sight:",contentsX,maxSize.height-CardComponent.CARD_HEIGHT-10);
					drawMouseHoverContents(g,plainSightContents,contentsX,maxSize.height-CardComponent.CARD_HEIGHT-5,maxSize);
				}
			}
			
			// Add color magic
			ArrayList<ColorMagic> colorMagic = new ArrayList<>();
			if (currentTileLocation.hasClearing()) {
				colorMagic.addAll(currentTileLocation.clearing.getAllSourcesOfColor(true));
			}
			contentsX = 5;
			contentsY = ChitComponent.S_CHIT_SIZE + 5;
			ArrayList<ColorMagic> unique = new ArrayList<>();
			for (ColorMagic cm : colorMagic) {
				if (!unique.contains(cm)) {
					ImageIcon icon = cm.getIcon();
					g.drawImage(icon.getImage(),contentsX,contentsY,null);
					contentsY += icon.getIconHeight();
					unique.add(cm);
				}
			}
		}
		if (!showEmbellishments) {
			Dimension size = getSize();
			Rectangle r = new Rectangle(0,size.height-40,size.width,40);
			g.setColor(MagicRealmColor.FORESTGREEN);
			g.fillRect(r.x,r.y,r.width,r.height);
			g.setFont(INSTRUCTION_FONT);
			g.setColor(Color.white);
			
			String string;
			if (game.getPlaceGoldSpecials()) {
				string = "Placing Visitor/Mission Chits";
			}
			else if (game.getGameMapBuilder()!=null) {
				if (tileBeingPlaced!=null) {
					string = "Click to place "+tileBeingPlaced.getGameObject().getName()+".  Right-click (or CTRL-click) to rotate.";
				}
				else {
					string = game.getGameMapBuilder()+" is adding a tile";
				}
			}
			else {
				string = hostMap?"Start game when ready.":"Waiting for host to start game";
			}
			
			GraphicsUtil.drawCenteredString(g,r,string);
			g.setStroke(THICK_STROKE);
			g.setColor(Color.red);
			g.drawRect(r.x+1,r.y+1,r.width-3,r.height-3);
		}
		else if (markClearingAlertText!=null) {
			Dimension size = getSize();
			Rectangle r = new Rectangle(0,size.height-40,size.width,40);
			g.setColor(MagicRealmColor.FORESTGREEN);
			g.fillRect(r.x,r.y,r.width,r.height);
			g.setFont(INSTRUCTION_FONT);
			g.setColor(Color.white);
			GraphicsUtil.drawCenteredString(g,r,markClearingAlertText);
			g.setStroke(THICK_STROKE);
			g.setColor(Color.red);
			g.drawRect(r.x+1,r.y+1,r.width-3,r.height-3);
		}
		
		if (mapAttentionMessage!=null) {
			Dimension size = getSize();
			Rectangle r = new Rectangle(50,size.height>>1,size.width-100,60);
			g.setColor(MAP_ATTENTION_COLOR);
			g.fillRect(r.x,r.y,r.width,r.height);
			g.setFont(MAP_ATTENTION_FONT);
			g.setColor(Color.black);
			GraphicsUtil.drawCenteredString(g,r.x,r.y-2,r.width,r.height,mapAttentionMessage);
			g.setStroke(THICK_STROKE);
			g.setColor(Color.white);
			g.drawRect(r.x+1,r.y+1,r.width-3,r.height-3);
		}
		
		// Draw tile being placed (if any)
		if (mouseHover!=null && tileImageIcon!=null) {
			int tw = (int)(tileImageIcon.getIconWidth() * scale);
			int th = (int)(tileImageIcon.getIconHeight() * scale);
			int x = mouseHover.x - (tw>>1);
			int y = mouseHover.y - (th>>1);
			g.drawImage(tileImageIcon.getImage(),x,y,tw,th,null);
		}
		
		if (viewTile!=null) {
			viewTile.setShowFlipside(false);
		}
		
		drawChatLines(g);
//g.setColor(Color.red);
//g.fillOval((int)offset.x-10,(int)offset.y-10,20,20);
	}
	private static void drawMouseHoverContents(Graphics2D g,ArrayList<RealmComponent> contents,int contentsX,int contentsY,Dimension maxSize) {
		Rectangle[] plot = new Rectangle[contents.size()];
		int n=0;
		for (RealmComponent rc:contents) {
			Dimension d = rc.getSize();
			plot[n++] = new Rectangle(contentsX,contentsY,d.width,d.height);
			contentsX += d.width;
			contentsX += 5;
		}
		if (plot.length>0) {
			// Resize if the contents run off the edge
			Rectangle test = plot[plot.length-1];
			if ((test.x+test.width)>maxSize.width) {
				double scaling = ((double)(maxSize.width-test.width))/((double)(test.x));
				for (int i=0;i<plot.length;i++) {
					plot[i].x = (int)(plot[i].x*scaling);
				}
			}
		}
		
		// Finally, draw them
		n=0;
		for (RealmComponent rc : contents) {
			Rectangle r = plot[n++];
			rc.paint(g.create(r.x,r.y,r.width,r.height));
		}
	}
	private void paintMap(Graphics g1,boolean tileShadow) {
		Graphics2D g = (Graphics2D)g1;
		
		ArrayList<Point> sortedKeys = new ArrayList<>(mapGrid.keySet());
		
		Collections.sort(sortedKeys,new Comparator<Point>() {
			public int compare(Point o1,Point o2) {
				Point p1 = o1;
				Point p2 = o2;
				int d = p1.x-p2.x;
				if (d==0) {
					d = p1.y-p2.y;
				}
				return d;
			}
		});
		
		if (tileLayer==null) {
			return;
		}
		
		// The key here, is that not every tile gets painted, even though tile.paintTo is called for every tile!
		// The tileLayer serves as a map "memory" so that the tiles don't have to be completely drawn every time.
		Graphics tileGraphics = tileLayer.getGraphics();
		for (Point gridPos : sortedKeys) {
			TileComponent tile = mapGrid.get(gridPos);
			if (tile!=null) { // tile might be null if the grid is reset in the middle of a paint
				tile.useShadow(tileShadow);
				Rectangle plotPos = mapGridCoor.get(gridPos);
				tile.paintTo(tileGraphics,plotPos.x,plotPos.y,tileSize.width+4,tileSize.height+4);
			}
		}
		
		g.drawImage(tileLayer,0,0,null);
		
		// embellishments (all playing pieces) are ALWAYS drawn
		if (showEmbellishments) {
			ChitDisplayOption displayOption = mapRightClickMenu.getDisplayOption();
			for (Point gridPos : sortedKeys) {
				TileComponent tile = mapGrid.get(gridPos);
				if (tile instanceof EmptyTileComponent) continue; 
				tile.drawEmbellishments(g,displayOption);
			}
		}
	}
	/**
	 * @param replot The replot to set.
	 */
	public void setReplot(boolean replot) {
		this.replot = replot;
	}
	
	public void setClearingPlot(ArrayList<TileLocation> clearingPlot) {
		this.clearingPlot = clearingPlot;
		repaint();
	}
	public void clearClearingPlot() {
		this.clearingPlot = null;
		repaint();
	}
	public void setPositionColor(Point p,Color c) {
		positionColors.put(p,c);
		repaint();
	}
	public void clearPositionColors() {
		positionColors.clear();
		repaint();
	}
	public void clearPositionColor(Point p) {
		if (positionColors.remove(p)!=null) {
			repaint();
		}
	}
	
	private KeyListener shiftKeyListener = new KeyListener() {
		public void keyTyped(KeyEvent e) {
		}

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
				updateShiftFlip(true);
			}
		}

		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode()==KeyEvent.VK_SHIFT) {
				updateShiftFlip(false);
			}
		}
	};
	public KeyListener getShiftKeyListener() {
		return shiftKeyListener;
	}
	public boolean isShowEmbellishments() {
		return showEmbellishments;
	}
	public void setShowEmbellishments(boolean showEmbellishments) {
		this.showEmbellishments = showEmbellishments;
	}
	public boolean isHostMap() {
		return hostMap;
	}
	public void setHostMap(boolean hostMap) {
		this.hostMap = hostMap;
	}
	private class MapRightClickMenu extends JPopupMenu implements ActionListener {
		
		private ChitDisplayOption displayOption;
		
		private JMenuItem addTile; // used by GM tool
		private JMenuItem removeTile; // used by GM tool
		private JMenuItem flipTile; // used by GM tool
		private JMenuItem addTileToGame; // used by GM tool
		private JMenuItem removeTileFromGame; // used by GM tool
		
		private JCheckBoxMenuItem showCharacters;
		private JCheckBoxMenuItem showMonsters;
		private JCheckBoxMenuItem showNatives;
		private JCheckBoxMenuItem showDwellings;
		private JCheckBoxMenuItem showTileChits;
		private JCheckBoxMenuItem showDroppedInventory;
		private JCheckBoxMenuItem showSiteCards;
		private JCheckBoxMenuItem showTileBewitchingSpells;
		
		private JMenuItem showAllChits;
		private JMenuItem showNoChits;
		
		private JMenuItem interpolationNearestNeighbor;
		private JMenuItem interpolationBilinear;
		private JMenuItem interpolationBicubic;
		
		private JSeparator separator;
		private JMenuItem showClearingDetail;
		
		private TileLocation tileLocation;
		private ClearingDetail clearing;
		
		public MapRightClickMenu() {
			displayOption = new ChitDisplayOption();
			initMenu();
		}
		public void setTileLocation(TileLocation tl) {
			tileLocation = tl;
			if (tl!=null && tl.isInClearing()) {
				clearing = tl.clearing;
				if (clearing.getClearingComponents().isEmpty()) {
					clearing = null;
				}
			}
			else {
				clearing = null;
			}
			separator.setVisible(clearing!=null);
			showClearingDetail.setVisible(clearing!=null);
		}
		private void initMenu() {
			if (gmFunctions) {
				addTile = new JMenuItem("Add Tile (GM)");
				addTile.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						GameObject tile = chooseTile();
						if (tile == null) return;
						TileComponent tileComponent = (TileComponent) RealmComponent.getRealmComponent(tile);
						ChangeListener changeListener = new ChangeListener() {
							public void stateChanged(ChangeEvent ev) {
								rebuildFromScratch();
								updateGrid();
								HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameData);
								ClearingUtility.markBorderlandConnectedClearings(hostPrefs,gameData);
							}
						};
						setTileBeingPlaced(changeListener,tileComponent);
					}
				});
				add(addTile);
				removeTile = new JMenuItem("Remove Tile (GM)");
				removeTile.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						if (tileLocation==null || tileLocation.tile instanceof EmptyTileComponent) return;
						if (tileLocation.tile.getGameObject().hasThisAttribute(Constants.ANCHOR_TILE)) {
							JOptionPane.showConfirmDialog(null, "Starting tile (e.g. Borderland) cannot be removed!", "Removing Tile", JOptionPane.DEFAULT_OPTION);
							return;
						}
						GameObject tileGo = tileLocation.tile.getGameObject();
						String position = tileGo.getAttribute(Tile.MAP_GRID,Tile.MAP_POSITION);
						int x = Integer.parseInt(position.split(",")[0]);
						int y = Integer.parseInt(position.split(",")[1]);
						Point point = new Point(x,y);
						mapGrid.remove(point);
						tileGo.removeAttribute(Tile.MAP_GRID,Tile.MAP_POSITION);
						tileGo.removeThisAttribute(ClearingDetail.BL_CONNECT);
						currentTileLocation = null;
						rebuildFromScratch();
						updateGrid();
						HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameData);
						ClearingUtility.markBorderlandConnectedClearings(hostPrefs,gameData);
					}
				});
				add(removeTile);
				flipTile = new JMenuItem("Flip Tile (GM)");
				flipTile.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						if (tileLocation==null || tileLocation.tile instanceof EmptyTileComponent) return;
						tileLocation.tile.flip();
						redraw();
					}
				});
				add(flipTile);				
				add(new JSeparator());
				addTileToGame = new JMenuItem("Add Tile to GameData (GM)");
				addTileToGame.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						GameObject tile = chooseTileToCopy();
						if (tile == null) return;
						GameObject go = gameData.createNewObject(tile);
						go.setThisAttribute("tile");
						HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameData);
						go.setThisAttribute(hostPrefs.getGameKeyVals());
					}
				});
				add(addTileToGame);
				removeTileFromGame = new JMenuItem("Remove Tile from GameData (GM)");
				removeTileFromGame.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						GameObject tile = chooseTileToRemove();
						if (tile == null) return;
						if (tile.hasThisAttribute(Constants.ANCHOR_TILE)) {
							JOptionPane.showConfirmDialog(null, "Starting tile (e.g. Borderland) cannot be removed!", "Removing Tile", JOptionPane.DEFAULT_OPTION);
							return;
						}
						tile.removeThisAttribute(ClearingDetail.BL_CONNECT);
						while (tile.getHold().size()>0) {
							tile.remove(tile.getHold().get(0));
						}
						gameData.removeObject(tile);
					}
				});
				add(removeTileFromGame);
				add(new JSeparator());
			}
			
			showCharacters = new JCheckBoxMenuItem("Show Characters",displayOption.characters);
			showCharacters.addActionListener(this);
			add(showCharacters);
			showMonsters = new JCheckBoxMenuItem("Show Monsters",displayOption.monsters);
			showMonsters.addActionListener(this);
			add(showMonsters);
			showNatives = new JCheckBoxMenuItem("Show Natives",displayOption.natives);
			showNatives.addActionListener(this);
			add(showNatives);
			showDwellings = new JCheckBoxMenuItem("Show Dwellings",displayOption.dwellings);
			showDwellings.addActionListener(this);
			add(showDwellings);
			showTileChits = new JCheckBoxMenuItem("Show Tile Chits",displayOption.tileChits);
			showTileChits.addActionListener(this);
			add(showTileChits);
			showDroppedInventory = new JCheckBoxMenuItem("Show Dropped Inventory",displayOption.droppedInventory);
			showDroppedInventory.addActionListener(this);
			add(showDroppedInventory);
			showSiteCards = new JCheckBoxMenuItem("Show Site Cards",displayOption.siteCards);
			showSiteCards.addActionListener(this);
			add(showSiteCards);
			showTileBewitchingSpells = new JCheckBoxMenuItem("Show Tile Bewitching Spells",displayOption.tileBewitchingSpells);
			showTileBewitchingSpells.addActionListener(this);
			add(showTileBewitchingSpells);
			add(new JSeparator());
			showAllChits = new JMenuItem("Show ALL Chits");
			showAllChits.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					showCharacters.setSelected(true);
					showMonsters.setSelected(true);
					showNatives.setSelected(true);
					showDwellings.setSelected(true);
					showTileChits.setSelected(true);
					showDroppedInventory.setSelected(true);
					showSiteCards.setSelected(true);
					showTileBewitchingSpells.setSelected(true);
					redraw();
				}
			});
			add(showAllChits);
			showNoChits = new JMenuItem("Hide ALL Chits");
			showNoChits.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					showCharacters.setSelected(false);
					showMonsters.setSelected(false);
					showNatives.setSelected(false);
					showDwellings.setSelected(false);
					showTileChits.setSelected(false);
					showDroppedInventory.setSelected(false);
					showSiteCards.setSelected(false);
					showTileBewitchingSpells.setSelected(false);
					redraw();
				}
			});
			add(showNoChits);
			add(new JSeparator());
			add(new JSeparator());
			JLabel interpolationLabel = new JLabel("INTERPOLATION");
			add(interpolationLabel);
			interpolationNearestNeighbor = new JMenuItem("Off (NearestNeighbor)");
			interpolationNearestNeighbor.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					interpolation = 0;
					prefs.set(rendering, 0);
					prefs.savePreferences();
					redraw();
				}
			});
			add(interpolationNearestNeighbor);
			interpolationBilinear = new JMenuItem("Bilinear");
			interpolationBilinear.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					interpolation = 1;
					prefs.set(rendering, 1);
					prefs.savePreferences();
					redraw();
				}
			});
			add(interpolationBilinear);
			interpolationBicubic = new JMenuItem("Bicubic");
			interpolationBicubic.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					interpolation = 2;
					prefs.set(rendering, 2);
					prefs.savePreferences();
					redraw();
				}
			});
			add(interpolationBicubic);
			
			separator = new JSeparator();
			add(separator);
			showClearingDetail = new JMenuItem("Show Clearing Detail");
			showClearingDetail.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					// Use the trade dialog to show detail
					RealmTradeDialog viewer = new RealmTradeDialog(new JFrame(),"Contents of "+clearing.fullString(),false,false,false);
					viewer.setRevealAll(false);
					ArrayList<RealmComponent> cc = clearing.getClearingComponents();
					ArrayList<RealmComponent> all = new ArrayList<>();
					for (RealmComponent rc:cc) {
						all.add(rc);
						if (rc.isNative()) {
							NativeSteedChitComponent horse = (NativeSteedChitComponent)rc.getHorse();
							if (horse!=null) {
								all.add(horse);
							}
						}
						else if (rc.isMonster()) {
							MonsterChitComponent monster = (MonsterChitComponent)rc;
							MonsterPartChitComponent weapon = monster.getWeapon();
							if (weapon!=null) {
								all.add(weapon);
							}
						}
					}
					
					viewer.setTradeComponents(all,false);
					viewer.setVisible(true);
				}
			});
			add(showClearingDetail);
		}
		public ChitDisplayOption getDisplayOption() {
			return displayOption;
		}
		public void actionPerformed(ActionEvent ev) {
			redraw();
		}
		private void redraw() {
			displayOption.characters = showCharacters.isSelected();
			displayOption.monsters = showMonsters.isSelected();
			displayOption.natives = showNatives.isSelected();
			displayOption.dwellings = showDwellings.isSelected();
			displayOption.tileChits = showTileChits.isSelected();
			displayOption.droppedInventory = showDroppedInventory.isSelected();
			displayOption.siteCards = showSiteCards.isSelected();
			displayOption.tileBewitchingSpells = showTileBewitchingSpells.isSelected();
			CenteredMapView.this.setReplot(true);
			CenteredMapView.this.repaint();
		}
	}
	public void setTileBeingPlaced(ChangeListener listener,TileComponent tile) {
		this.tilePlacementListener = listener;
		this.tileBeingPlaced = tile;
		this.tileBeingPlaced.setRotation(0);
		updateTileBeingPlaced();
	}
	private void updateTileBeingPlacedIcon() {
		this.tileImageIcon = tileBeingPlaced.isShowFlipSide()?
				tileBeingPlaced.getRepresentativeFullSizeFlipIcon():tileBeingPlaced.getRepresentativeFullSizeIcon();
	}
	private void updateTileBeingPlaced() {
		updateTileBeingPlacedIcon();
		
		// Set valid places
		Tile tile = new Tile(tileBeingPlaced.getGameObject());
		
//		ArrayList availableMapPositions = Tile.findAvailableMapPositions(planningMapGrid);
//		if (availableMapPositions.isEmpty()) {
//			availableMapPositions.add(new Point(0,0));
//		}
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameData);
		for (Point gp : availablePositions) {
			boolean valid = Tile.isMappingPossibility(planningMapGrid,tile,gp,tileBeingPlaced.getRotation(),anchorTileName,hostPrefs.hasPref(Constants.MAP_BUILDING_HILL_TILES));
			EmptyTileComponent empty = (EmptyTileComponent)mapGrid.get(gp);
			empty.setValidPosition(valid);
		}
		setReplot(true);
		repaint();
	}
	public void clearTileBeingPlaced() {
		this.tilePlacementListener = null;
		this.tileBeingPlaced = null;
		this.tileImageIcon = null;
	}
	public ArrayList<Tile> getPlaceables(GameObject tile) {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameData);
		ArrayList<Tile> placeables = new ArrayList<>();
		for (Point gp : availablePositions) {
			for (int r=0;r<6;r++) {
				Tile t = new Tile(tile);
				if (Tile.isMappingPossibility(planningMapGrid,t,gp,r,anchorTileName,hostPrefs.hasPref(Constants.MAP_BUILDING_HILL_TILES))) {
					placeables.add(t);
				}
			}
		}
		return placeables;
	}
	/**
	 * Places a single tile, randomly, from a collection of tiles
	 */
	public void placeRandom(ChangeListener listener,Collection<GameObject> tiles) {
		clearTileBeingPlaced(); // just in case
		this.tilePlacementListener = listener;
		ArrayList<Tile> placeables = new ArrayList<>();
		boolean earlyExit = false;
		Hashtable<String, Integer> hashPlaceableCount = new Hashtable<>();
		for (GameObject go : tiles) {
			earlyExit = false;
			if (go.hasThisAttribute(Constants.ANCHOR_TILE) && !go.hasThisAttribute(Constants.BOARD_NUMBER)) {
				placeables.clear();
				earlyExit = true;
			}
			Collection<Tile> c = getPlaceables(go);
			hashPlaceableCount.put(go.getName(),c.size());
			placeables.addAll(c);
			if (earlyExit) {
				break;
			}
		}
		
		if (!placeables.isEmpty()) {
			int r = RandomNumber.getRandom(placeables.size());
			Tile t = placeables.get(r);
			tileBeingPlaced = (TileComponent)RealmComponent.getRealmComponent(t.getGameObject());
			tileBeingPlaced.setRotation(t.getRotation());
			addPlacementToMap(t.getMapPosition());
		}
		else {
			System.err.println("------------------------------------");
			System.err.println("Unable to place randomly?!?");
			System.err.println("  earlyExit = "+earlyExit);
			System.err.println("  tiles="+tiles);
			System.err.println("  availablePositions="+availablePositions);
			System.err.println("  hashPlaceableCount="+hashPlaceableCount);
		}
	}
	public void rebuildFromScratch() {
		clearTileBeingPlaced();
		mapImage = null;
		clearTileLayer();
		updateGrid();
//		Rectangle before = borderRect;
		updateMapSize();
//		Rectangle after = borderRect;
		
		// Rebuild tile layer
//		BufferedImage oldTileLayer = tileLayer;
//		tileLayer = new BufferedImage(borderRect.width,borderRect.height,BufferedImage.TYPE_3BYTE_BGR);
//				Graphics ig = tileLayer.getGraphics();
//				ig.setColor(UIManager.getColor("ScrollPane.background"));
//				ig.fillRect(0,0,borderRect.width,borderRect.height);
//		int dx = before.x - after.x;
//		int dy = before.y - after.y;
//		tileLayer.getGraphics().drawImage(oldTileLayer,dx,dy,null);
////		paintMap(mapImage.getGraphics(),true);
		
		setReplot(true);
		centerMap();
		repaint();
	}
	private void addPlacementToMap(Point gp) {
		tileBeingPlaced.setShowFlipside(false);
		ChangeEvent ev = new ChangeEvent(tileBeingPlaced);
		ChangeListener listener = tilePlacementListener;
		GameObject obj = tileBeingPlaced.getGameObject();
		obj.setAttribute(Tile.MAP_GRID,Tile.MAP_POSITION,gp.x+","+gp.y);
		obj.setAttribute(Tile.MAP_GRID,Tile.MAP_ROTATION,tileBeingPlaced.getRotation());
//		tileBeingPlaced.doRepaint();
		
		rebuildFromScratch();
		listener.stateChanged(ev);
	}
	private void placeTile(Point p) {
		TileLocation tl = getTileLocationAtPoint(p);
		if (tl!=null && tl.tile instanceof EmptyTileComponent) {
			EmptyTileComponent placement = (EmptyTileComponent)tl.tile;
			if (placement.isValidPosition()) {
				Point gp = findGridPos(placement);
				addPlacementToMap(gp);
			}
		}
	}
	public ArrayList<ClearingDetail> getAllOccupiedClearings() {
		ArrayList<ClearingDetail> list = new ArrayList<>();
		ArrayList<Point> sortedKeys = new ArrayList<>(mapGrid.keySet());
		
		Collections.sort(sortedKeys,new Comparator<Point>() {
			public int compare(Point o1,Point o2) {
				Point p1 = o1;
				Point p2 = o2;
				int d = p1.x-p2.x;
				if (d==0) {
					d = p1.y-p2.y;
				}
				return d;
			}
		});
		
		for (Point gridPos : sortedKeys) {
			TileComponent tile = mapGrid.get(gridPos);
			if (tile!=null) { // tile might be null if the grid is reset in the middle of a paint
				for (ClearingDetail clearing:tile.getClearings()) {
					ArrayList<RealmComponent> components = clearing.getClearingComponents();
					boolean show = false;
					for (RealmComponent rc:components) {
						if (rc.isStateChit()) {
							StateChitComponent state = (StateChitComponent)rc;
							if (state.hasBeenSeen()) {
								show = true;
								break;
							}
						}
						else {
							show = true;
							break;
						}
					}
					if (show) {
						list.add(clearing);
					}
				}
			}
		}
		return list;
	}
	private static Color TRANSPARENT_WHITE = new Color(255,255,255,190);
	private void drawChatLines(Graphics2D g) {
		Dimension size = getSize();
		int chatWidth = 200;
		int x = size.width-chatWidth;
		int y = size.height-20;

		int maxHeaderWidth = 0;
		for (int i=0;i<chatLinesToShow && i<chatLines.size();i++) {
			ChatLine line = chatLines.get(i);
			TextType tt = new TextType(line.getHeader(),2000,"CHAT");
			int w = tt.getWidth(g);
			maxHeaderWidth = Math.max(maxHeaderWidth,w);
		}
		
		maxHeaderWidth += 40;
		
		for (int i=0;i<chatLinesToShow && i<chatLines.size();i++) {
			ChatLine line = chatLines.get(i);
			ChatStyle style = chatStyles.get(line.getTextStyleName());
			TextType tt = new TextType(line.getText(),chatWidth,"CHAT");
			int h = tt.getHeight(g); 
			y -= h;
			g.setColor(TRANSPARENT_WHITE);
			g.fillRect(x-maxHeaderWidth-30,y+3,chatWidth+maxHeaderWidth+40,h);
			tt.draw(g,x,y,TextType.Alignment.Left,style.getColor());
			
			tt = new TextType(line.getHeader(),maxHeaderWidth,"STAT_BLACK");
			tt.draw(g,x-maxHeaderWidth-20,y,TextType.Alignment.Right,style.getColor());
		}
	}
	public void addChatLine(ChatLine line) {
		chatLines.add(0,line);
		while(chatLines.size()>chatLineBufferSize) {
			chatLines.remove(chatLines.size()-1);
		}
		repaint();
	}
	private GameObject chooseTile() {
		GamePool pool = new GamePool(gameData.getGameObjects());
		Hashtable<String, GameObject> hash = new Hashtable<>();
		ArrayList<String> tileList = new ArrayList<>();
		for (GameObject tile : pool.find("tile")) {
			if (!tile.hasAttribute(Tile.MAP_GRID, Tile.MAP_POSITION)) {
				tileList.add(tile.getName());
				hash.put(tile.getName(), tile);
			}
		}
		Collections.sort(tileList);
		return tileChooser("Select a tile to place:", tileList,hash);
	}
	private GameObject chooseTileToCopy() {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameData);
		RealmLoader rl = new RealmLoader();
		GamePool pool = new GamePool(rl.getData().getGameObjects());
		Hashtable<String, GameObject> hash = new Hashtable<>();
		GamePool existingPool = new GamePool(gameData.getGameObjects());
		ArrayList<String> existingTileNames = new ArrayList<>();
		for (GameObject tile : existingPool.find("tile")) {
			existingTileNames.add(tile.getName());
		}
		
		String searchKey = "tile";
		if (hostPrefs.getAlternativeTilesEnabled()) {
			searchKey = "a_tile";
		}
		
		ArrayList<String> tileList = new ArrayList<>();
		for (GameObject tile : pool.find(searchKey)) {
			if (!existingTileNames.contains(tile.getName())) {
				tileList.add(tile.getName());				
				hash.put(tile.getName(), tile);
			}
		}
		return tileChooser("Select a tile to add", tileList,hash);
	}
	private GameObject chooseTileToRemove() {
		GamePool pool = new GamePool(gameData.getGameObjects());
		Hashtable<String, GameObject> hash = new Hashtable<>();
		ArrayList<String> tileList = new ArrayList<>();
		for (GameObject tile : pool.find("tile")) {
			if (!tile.hasAttribute(Tile.MAP_GRID,Tile.MAP_POSITION)) {
				tileList.add(tile.getName());		
				hash.put(tile.getName(), tile);
			}
		}
		return tileChooser("Select a tile to remove", tileList,hash);
	}
	private GameObject tileChooser(String headline, ArrayList<String> tileList, Hashtable<String, GameObject> tileHash) {
		Collections.sort(tileList);
		ListChooser chooser = new ListChooser(new JFrame(), headline, tileList);
		chooser.setDoubleClickEnabled(true);
		chooser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		chooser.setLocationRelativeTo(this);
		chooser.setVisible(true);
		Vector<String> v = chooser.getSelectedItems();
		if (v != null && !v.isEmpty()) {
			return tileHash.get(v.get(0));
		}
		return null;
	}
}