package com.robin.hexmap;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.*;

import javax.swing.*;

import com.robin.general.graphics.GraphicsUtil;
import com.robin.general.swing.ImageCache;
import com.robin.general.util.HashLists;
import com.robin.general.util.RandomNumber;

public class HexMap extends JComponent implements Scrollable {
	
	private static final Stroke THREE_STROKE = new BasicStroke(3);

	public static final int RANDOM = 1;
	public static final int FORCE_ADJACENT = 2;
	public static final int FORCE_ISLAND = 3;
	
//	public static final int mapBorder = 30;
	
	// Used for identifying selection borders
	public static int[] BORDER_BIT = {0x01,0x02,0x04,0x08,0x10,0x20};
	
	public static Color selectionColor = new Color(255,255,255,80);
	public static Color shadingColor = new Color(100,100,100,80);
	
	public static final Font tagFont = new Font("Dialog",Font.BOLD,24);
	public static final Font labelFont = new Font("Dialog",Font.BOLD,12);
	public static final Font smallFont = new Font("Dialog",Font.BOLD,10);
	
	public static Image[] selIcon = {
		ImageCache.getIcon("sel0").getImage(),
		ImageCache.getIcon("sel1").getImage(),
		ImageCache.getIcon("sel2").getImage(),
		ImageCache.getIcon("sel3").getImage(),
		ImageCache.getIcon("sel4").getImage(),
		ImageCache.getIcon("sel5").getImage()
	};
				
	protected Dimension mapSize;
	
	protected Image defaultImage;	// icon to use for empty hexes
	protected Hashtable<HexMapPoint, HexSet> sets;
	protected Hashtable<HexMapPoint, Hex> hexes;
	protected Hashtable<HexMapPoint, Integer> distanceHash;   // hash of all empty hexes - tells how many spaces away from occupied hexes
	protected Collection<Collection<HexMapPoint>> waterBodies;
	protected Hashtable<HexMapPoint,Rectangle> hexDrawCoordinates;
	protected ArrayList<HexMapPoint> allHexPositions;
	
	// Selections
	protected Hashtable<HexMapPoint, MoveRule> selectionRules;
	protected Hashtable<HexMapPoint, Comparable> selectionBorder;
	
	// Saved selections
	protected Hashtable<HexMapPoint, Comparable> savedSelectionBorder;
	protected Hashtable<HexMapPoint, MoveRule> savedSelectionRules;
	
	// Optimization for water distance searches
	protected HexMapPoint lastWaterMarkStart;
	protected Hashtable<HexMapPoint, Integer> lastMarkHash; // keep this to optimize searches on the same pos
	
	protected int width;			// width (in hexes)
	protected int height;			// height (in hexes)
	protected int iconHeight;
	protected int iconWidth;
	protected int xAdjust;
	protected int yAdjust;
	protected int mapBorder;
	protected Dimension size;
	protected Color mapBackground = new Color(200,200,100);
	protected ArrayList<Token> tokens;
	
	protected boolean showNumbering = true;
	protected boolean showLabels = true;
	protected boolean showMoveCalculation = false;
	protected boolean showMoveRuleText = false;
	protected boolean showSelectShading = true;
	protected boolean showRotatedHexes = true; // true if you want the images to rotate with the set
	protected boolean showCoordinates = false;
	
	protected Hashtable<HexMapPoint, HexTag> hexTags; // hash of HexMapPoint:HexTag pairs - Affected HexMapPoints are tagged when drawn
	protected ArrayList<HexGuide> hexGuides; // list of HexGuide objects
	
	protected double currentScale = 1.0;
	
	public HexMap(ImageIcon defaultImageIcon,int width,int height) {
		this.width = width;
		this.height = height;
		setDefaultImage(defaultImageIcon);
		reset();
	}
	public Color getMapBackground() {
		return mapBackground;
	}
	public void setMapBackground(Color color) {
		mapBackground = color;
	}
	public ArrayList<HexMapPoint> getAllHexPositions() {
		return allHexPositions;
	}
	public Collection<HexMapPoint> getAllTokenPositions() {
		ArrayList<HexMapPoint> positions = new ArrayList<>();
		for (Token token:tokens) {
			HexMapPoint pos = token.getPosition();
			if (!positions.contains(pos)) {
				positions.add(pos);
			}
		}
		return positions;
	}
	public void reset() {
		setDimensions();
		initHexPositions();
		sets = new Hashtable<>();
		hexes = new Hashtable<>();
		selectionRules = new Hashtable<HexMapPoint, MoveRule>();
		selectionBorder = new Hashtable<HexMapPoint, Comparable>();
		tokens = new ArrayList<>();
		clearTags();
		clearGuides();
		setMapBackground(getMapBackground());
	}
	public void setShowNumbering(boolean val) {
		showNumbering = val;
		repaint();
	}
	public void setShowLabels(boolean val) {
		showLabels = val;
		repaint();
	}
	public void setShowMoveCalculation(boolean val) {
		showMoveCalculation = val;
		repaint();
	}
	public void setShowMoveRuleText(boolean val) {
		showMoveRuleText = val;
	}
	public void setDefaultImage(ImageIcon defaultImageIcon) {
		defaultImage = defaultImageIcon.getImage();
		iconHeight = defaultImageIcon.getIconHeight()-1;
		iconWidth = defaultImageIcon.getIconWidth()-1;
		xAdjust = (iconWidth>>1)+(iconWidth>>2);
		yAdjust = iconHeight>>1;
		
		// The map border should allow one extra hex plus 5 pixels.
		mapBorder = (((xAdjust>yAdjust)?xAdjust:yAdjust)*2)+20;
	}
	public void setDimensions() {
		int dimX = (width*xAdjust) + (iconWidth>>2) + 2 + mapBorder;
		int dimY = (height*iconHeight) + 1 + mapBorder;
		mapSize = new Dimension(dimX,dimY);
		setMaximumSize(mapSize);
		setMinimumSize(mapSize);
		setPreferredSize(mapSize);
	}
	public Dimension getMapSize() {
		return mapSize;
	}
	public void initHexPositions() {
		hexDrawCoordinates = new Hashtable<>();
		allHexPositions = new ArrayList<>();
		for (int gx=0;gx<width;gx++) {
			int offset = gx/2;
			int shortCol = gx%2!=0?1:0;
			for (int gy=-offset;gy<height-offset-shortCol;gy++) {
				HexMapPoint pos = new HexMapPoint(gx,gy);
				addHexPosition(pos);
			}
		}
	}
	public boolean isValidHexPosition(HexMapPoint pos) {
		return allHexPositions.contains(pos);
	}
	public void removeHexPosition(HexMapPoint pos) {
		hexDrawCoordinates.remove(pos);
		allHexPositions.remove(pos);
	}
	/**
	 * Adds a water hex to the overall map possibilities
	 */
	public void addHexPosition(HexMapPoint pos) {
		if (!allHexPositions.contains(pos)) {
			int gx = pos.getX();
			int gy = pos.getY();
			int x = gx*xAdjust + (mapBorder>>1);
			int y = (gy*iconHeight)+(gx*yAdjust) + (mapBorder>>1);
			Rectangle r = new Rectangle(x,y,iconWidth,iconHeight);
			hexDrawCoordinates.put(pos,r);
			allHexPositions.add(pos);
		}
	}
	
	public boolean addSet(Vector<HexMapPoint> positionChoices,HexSet set,int placementStyle) {
		Vector<HexMapPoint> overlapping = new Vector<>();
		Vector<HexMapPoint> allGoodCenters = new Vector<>();
		for (int i=0;i<positionChoices.size();i++) {
			boolean isGood = false;
			HexMapPoint pos = positionChoices.elementAt(i);
			if (!set.overlaps(this,pos)) {
				switch(placementStyle) {
					case RANDOM:
						isGood = true;
						break;
					case FORCE_ADJACENT:
						if (sets.size()>0) {
							if (set.adjacentToAnother(this,pos)) {
								isGood=true;
							}
						}
						else {
							// if there are no other hexes, then don't enforce
							isGood = true;
						}
						break;
					case FORCE_ISLAND:
						if (sets.size()>0) {
							if (!set.adjacentToAnother(this,pos)) {
								isGood=true;
							}
						}
						else {
							// if there are no other hexes, then don't enforce
							isGood = true;
						}
						break;
				}
				if (isGood) {
					allGoodCenters.add(pos);
				}
			}
			else {
				overlapping.add(pos);
			}
		}
		// remove all overlapping centers, as these will never be good again.
		for (int i=0;i<overlapping.size();i++) {
			positionChoices.remove(overlapping.elementAt(i));
		}
		if (allGoodCenters.size()>0) {
			int r = RandomNumber.getRandom(allGoodCenters.size());
			HexMapPoint pos = allGoodCenters.elementAt(r);
			addSet(set,pos);
			return true;
		}
		return false;
	}
	public Collection<HexMapPoint> getActiveLandPoints() {
		ArrayList<HexMapPoint> landPoints = new ArrayList<>();
		for (HexMapPoint pos : hexes.keySet()) {
			Hex hex = hexes.get(pos);
			if (hex.isActive()) {
				landPoints.add(pos);
			}
		}
		return landPoints;
	}
	public HexSet getSetAt(HexMapPoint pos) {
		if (hexes.get(pos)!=null) { // easy check
			HexSet[] setArray = getAllSets();
			for (int i=0;i<setArray.length;i++) {
				if (setArray[i].contains(pos)) {
					return setArray[i];
				}
			}
		}
		return null;
	}
	public HexSet[] getAllSets() {
		return sets.values().toArray(new HexSet[0]);
	}
	/**
	 * Selects a region that would be covered by the HexSet, without actually adding the set
	 */
	public void selectSetCoverage(HexSet set,HexMapPoint center) {
		HexMapPoint oldCenter = set.getCenter();
		set.setCenter(center);
		set.selectMap(this);
		set.setCenter(oldCenter);
	}
	public boolean addSet(HexSet set,HexMapPoint center) {
		if (!set.overlaps(this,center)) {
			set.setCenter(center);
			set.loadMap(this);
			sets.put(center,set);
			return true;
		}
		return false; // fails to add tile
	}
	public boolean removeSet(HexSet set) {
		HexMapPoint center = set.getCenter();
		if (center!=null) {
			if (sets.remove(center)!=null) {
				set.unloadMap(this);
				return true;
			}
		}
		return false;
	}
	public boolean isGameHex(Hex hex) {
		return hex!=null;
	}
	public boolean validHex(HexMapPoint pos) {
		return (hexDrawCoordinates.get(pos)!=null);
	}
	public HexMapPoint getHexPosition(Hex findHex) {
		for (HexMapPoint pos : hexes.keySet()) {
			Hex hex = hexes.get(pos);
			if (findHex.equals(hex)) {
				return pos;
			}
		}
		return null;
	}
	public Hex getHex(HexMapPoint pos) {
		return hexes.get(pos);
	}
	public void setHex(HexMapPoint pos,Hex hex) {
		hexes.put(pos,hex);
		hex.updateImageRotation(showRotatedHexes);
	}
	public void clearHex(HexMapPoint pos) {
		hexes.remove(pos);
	}
	public Hashtable<HexMapPoint, Hex> getHexes() {
		return hexes;
	}
	public Hashtable<HexMapPoint, Integer> getDistances() {
		return distanceHash;
	}
	public ArrayList<HexMapPoint> getAllEdgePositions() {
		ArrayList<HexMapPoint> edge = new ArrayList<>();
		for (HexMapPoint pos : hexDrawCoordinates.keySet()) {
			HexMapPoint[] adj = pos.getAdjacentPoints();
			for (int n=0;n<adj.length;n++) {
				if (!isValidHexPosition(adj[n])) {
					edge.add(pos);
					break;
				}
			}
		}
		return edge;
	}
	/**
	 * Populates the distanceHash - call once map is built - can be called multiple times
	 */
	protected void calculateDistances() {
		// First, cycle through every coordinate, and identify land
		Collection<HexMapPoint> toMark = new ArrayList<>();
		for (HexMapPoint pos : hexDrawCoordinates.keySet()) {
			if (hexes.get(pos)!=null) {
				toMark.add(pos);
			}
		}
		distanceHash = markDistances(toMark,hexDrawCoordinates.keySet());
	}
	public Hashtable<HexMapPoint, Integer> markDistances(Collection<HexMapPoint> toMark,Collection<HexMapPoint> searchSet) {
		Hashtable<HexMapPoint, Integer> markHash = new Hashtable<>();
		
		// Now iteratively search for non-occupied hexes adjacent to a distance
		Integer distance = Integer.valueOf(0);
		while(toMark.size()>0) {
			// Mark the toMark
			for (HexMapPoint pos : toMark) {
				markHash.put(pos,distance);
			}
			
			// Find the next layer
			toMark = new ArrayList<>();
			for (HexMapPoint pos : searchSet) {
				if (!markHash.containsKey(pos)) { // only check hexes not already marked
					HexMapPoint[] adj = pos.getAdjacentPoints();
					for (int n=0;n<adj.length;n++) {
						if (markHash.containsKey(adj[n])) {
							toMark.add(pos);
							break;
						}
					}
				}
			}
			
			// Increment the distance
			distance = Integer.valueOf(distance.intValue()+1);
		}
		return markHash;
	}
	/**
	 * Finds all contiguous bodies of empty hexes (water in this case)
	 */
	protected void findWaterBodies() {
		ArrayList<HexMapPoint> allEmpty = new ArrayList<>();
		for (HexMapPoint pos:allHexPositions) {
			if (hexes.get(pos)==null) {
				allEmpty.add(pos);
			}
		}
		
		waterBodies = new ArrayList<>();
		while(allEmpty.size()>0) {
			// Start a new water body
			ArrayList<HexMapPoint> waterBody = new ArrayList<>();
			
			// Pick the first empty hex from allEmpty, and add to a search
			HexMapPoint start = allEmpty.iterator().next();
			ArrayList<HexMapPoint> search = new ArrayList<>();
			search.add(start);
			
			// Do search
			while(search.size()>0) {
				// remove search from allEmpty before starting
				allEmpty.removeAll(search);
				
				ArrayList<HexMapPoint> nextSearch = new ArrayList<>();
				for (HexMapPoint pos : search) {				
					// remove the search hex from allEmpty, so it is not searched again
					allEmpty.remove(pos);
					
					// add the search hex to the water body
					waterBody.add(pos);
					
					// check all adjacent for more valid empty hexes (contained in allEmpty)
					HexMapPoint[] adj = pos.getAdjacentPoints();
					for (int n=0;n<adj.length;n++) {
						if (!nextSearch.contains(adj[n]) && allEmpty.contains(adj[n])) {
							// adjacent empty is in master list, so add it to the next search
							nextSearch.add(adj[n]);
						}
					}
				}
				search = nextSearch;
			}
			
			waterBodies.add(waterBody);
		}
	}
	/**
	 * Returns the shortest distance by water, or null if no connection
	 */
	public Integer getWaterRange(HexMapPoint from,HexMapPoint to) {
		// locate the body of water that has both
		for (Collection<HexMapPoint> waterBody : waterBodies) {
			if (waterBody.contains(from) && waterBody.contains(to)) { // this check is an optimization
				// mutual water body (guaranteed connection!)
				// now, find the shortest path
				if (!from.equals(lastWaterMarkStart)) {
					// only recalculate if a new starting point
					ArrayList<HexMapPoint> toMark = new ArrayList<>();
					toMark.add(from);
					lastWaterMarkStart = from;
					lastMarkHash = markDistances(toMark,waterBody);
				}
				return lastMarkHash.get(to);
			}
		}
		return null; // no mutual water body was found!
	}
	
	public Collection<Collection<HexMapPoint>> getWaterBodies() {
		return waterBodies;
	}
	
	/**
	 * Returns a Collection of Collections of HexMapPoints, representing lakes, or null if none.
	 */
	public Collection<Collection<HexMapPoint>> getLakes() {
		if (waterBodies.size()>1) {
			// First find max water body size
			int maxWaterBodySize = 0;
			for (Collection<HexMapPoint> waterBody : waterBodies) {
				if (waterBody.size()>maxWaterBodySize) {
					maxWaterBodySize = waterBody.size();
				}
			}
			
			// Exclude max water body from the return collection
			ArrayList<Collection<HexMapPoint>> ret = new ArrayList<>();
			for (Collection<HexMapPoint> waterBody : waterBodies) {
				if (waterBody.size()<maxWaterBodySize) {
					ret.add(waterBody);
				}
			}
			return ret;
		}
		return null; // there are no lakes!
	}
	
	/**
	 * Returns a distance from land value (a value of 1, means adjacent to land)
	 */
	public int getDistanceFromLand(HexMapPoint pos) {
		Integer val = distanceHash.get(pos);
		return val.intValue();
	}
	/**
	 * Returns true if the provided pos is on the map at all
	 */
	public boolean validHexMapPoint(HexMapPoint pos) {
		return hexDrawCoordinates.get(pos)!=null;
	}
	public void clearTags() {
		hexTags = new Hashtable<>();
		repaint();
	}
	public void addTag(HexMapPoint pos,HexTag newTag) {
		HexTag tag = hexTags.get(pos);
		if (tag!=null) {
			tag.merge(newTag);
		}
		else {
			hexTags.put(pos,newTag);
		}
		repaint();
	}
	public void clearGuides() {
		hexGuides = new ArrayList<>();
		repaint();
	}
	public void addGuide(HexGuide guide) {
		hexGuides.add(guide);
	}
	public HexMapPoint getHexMapPointForPoint(Point p) {
		return getHexMapPointForPoint(p,1.0);
	}
	public HexMapPoint getHexMapPointForPoint(Point p,double scale) {
		// first I need to determine what possible hexes might contain the point
		// using rectangular logic
		
//		ArrayList possiblePolygons = new ArrayList();
		Point actualPoint = new Point((int)(p.x/scale),(int)(p.y/scale));
		for (Enumeration e=hexDrawCoordinates.keys();e.hasMoreElements();) {
			HexMapPoint pos = (HexMapPoint)e.nextElement();
			Rectangle r = hexDrawCoordinates.get(pos);
			if (r.contains(actualPoint)) {
				// next I need to create a Polygon to represent this hex
				// and see if the point is still contained
				if (makeHexPolygon(r).contains(actualPoint)) {
					// it is?  good!
					return pos;
				}
			}
		}
		return null;
	}
	public Rectangle getHexMapPointRectangle(HexMapPoint pos) {
		return hexDrawCoordinates.get(pos);
	}
	public Point getHexMapPointCenter(HexMapPoint pos) {
		Rectangle r = hexDrawCoordinates.get(pos);
		return new Point(r.x+(r.width>>1),r.y+(r.height>>1));
	}
	/**
	 * Returns hexagon perfectly contained within the rectangle.
	 */
	public Polygon makeHexPolygon(Rectangle r) {
		Polygon hexPoly = new Polygon();
		
		hexPoly.addPoint(r.x,r.y+(r.height>>1));
		hexPoly.addPoint(r.x+(r.width>>2),r.y);
		hexPoly.addPoint(r.x+r.width-(r.width>>2),r.y);
		hexPoly.addPoint(r.x+r.width,r.y+(r.height>>1));
		hexPoly.addPoint(r.x+r.width-(r.width>>2),r.y+r.height);
		hexPoly.addPoint(r.x+(r.width>>2),r.y+r.height);
		
		return hexPoly;
	}
	public JViewport getViewport() {
		Container container = getParent();
		if (container instanceof JViewport) {
			return (JViewport)container;
		}
		return null;
	}
	public void centerOnPosition(HexMapPoint pos) {
		JViewport viewport = getViewport();
		if (viewport!=null) {
			Dimension size = getSize();
			// centering on the position is only meaningful if you are in a JViewport
			Rectangle r = hexDrawCoordinates.get(pos);
			if (r!=null) { // only center on coordinates on the map!
				Rectangle view = viewport.getViewRect();
				int x = r.x - ((view.width - r.width)>>1);
				if (x<0) x=0;
				if (x+view.width>size.width) x=size.width-view.width-1;
				int y = r.y - ((view.height - r.height)>>1);
				if (y<0) y=0;
				if (y+view.height>size.height) y=size.height-view.height-1;
				viewport.setViewPosition(new Point(x,y));
				viewport.repaint(); // this should solve the "nonmoving player" bug
			}
		}
	}
	public void paintComponent(Graphics g) {
		paintComponent(g,currentScale,true);
	}
	public void paintComponent(Graphics g1,double scale,boolean viewOnly) {
		Graphics2D g = (Graphics2D)g1;
		
		if (scale!=1.0) {
			AffineTransform transform = AffineTransform.getScaleInstance(scale,scale);
			g.transform(transform);
		}
		Rectangle view = null;
		JViewport viewport = getViewport();
		if (viewOnly && viewport!=null) {
			// HexMap is in a JViewport, so only show the view
			view = viewport.getViewRect();
		}
		else {
			Dimension size = mapSize;
			view = new Rectangle(0,0,size.width,size.height);
		}
		
		// Draw the map
		g.setColor(getMapBackground());
		g.fillRect(view.x,view.y,view.width,view.height);
		Hashtable<Rectangle, String> labelsToDraw = new Hashtable<>();
		ArrayList<HexMapPoint> moveRulePos = new ArrayList<>();
		ArrayList<MoveRule> moveRules = new ArrayList<>();
		ArrayList<Rectangle> moveRuleRects = new ArrayList<>();
		for (Enumeration e=hexDrawCoordinates.keys();e.hasMoreElements();) {
			HexMapPoint pos = (HexMapPoint)e.nextElement();
			Rectangle r = hexDrawCoordinates.get(pos);
			if (view.intersects(r)) {
				HexTag tag = hexTags.get(pos);
				if (tag==null) {
					Hex hex = hexes.get(pos);
					if (hex==null) {
						g.drawImage(defaultImage,r.x,r.y,null);
						
// This is for debugging water distance
//						g.setColor(Color.blue);
//						GraphicsUtil.drawCenteredString(g,r.x,r.y,r.width,r.height,distanceHash.get(pos).toString());
					}
					else if (!hex.isActive()) {
						Polygon poly = makeHexPolygon(r);
						g.setColor(Color.black);
						g.fill(poly);
					}
					else {
						hex.draw(g,r.x,r.y,showNumbering);
						if (showLabels && hex.getLabel()!=null) {
							labelsToDraw.put(r,hex.getLabel());
						}
					}
					if (showCoordinates) {
						g.setColor(Color.blue);
						int h = r.height>>1;
						GraphicsUtil.drawCenteredString(g,r.x,r.y+(h>>1),r.width,h,pos.getKey());
					}
				}
				else {
					Polygon poly = makeHexPolygon(new Rectangle(r.x+4,r.y+4,r.width-8,r.height-8));
					g.setColor(tag.getColor());
					g.fill(poly);
					g.setColor(Color.black);
					g.setFont(tagFont);
					GraphicsUtil.drawCenteredString(g,r,tag.getTagString());
					Stroke stroke = g.getStroke();
					g.setStroke(THREE_STROKE);
					g.setColor(Color.white);
					g.draw(poly);
					g.setStroke(stroke);
				}
				if (selectionBorder.size()>0) {
					Polygon poly = makeHexPolygon(r);
					Integer flag = (Integer)selectionBorder.get(pos);
					if (flag!=null) {
						
						// Mark Selected by ...
						
						// ...filling in the hex with transparent white color
						g.setColor(selectionColor);
						g.fill(poly);
						
						// ...and drawing a border around the entire selected area
						int val = flag.intValue();
						for (int n=0;n<6;n++) {
							if ((val&BORDER_BIT[n])>0) {
								g.drawImage(selIcon[n],r.x,r.y,null);
							}
						}
						
						// Mark movesleft
						MoveRule rule = selectionRules.get(pos);
						if (rule!=null) {
							moveRulePos.add(pos);
							moveRules.add(rule);
							moveRuleRects.add(r);
						}
					}
					else if (showSelectShading) {
						g.setColor(shadingColor);
						g.fill(poly);
					}
				}
			}
		}
		
		// Draw all labels
		if (showLabels && labelsToDraw.size()>0) {
			for (Rectangle r : labelsToDraw.keySet()) {				
				String label = labelsToDraw.get(r);
				g.setColor(Color.white);
				g.setFont(labelFont);
//				g.drawString(label,r.x+(iconWidth>>1),r.y+(iconHeight>>1));
				GraphicsUtil.drawCenteredString(g,r.x,r.y+iconHeight-(iconHeight>>1),iconWidth,(iconHeight>>1),label);
			}
		}
		
		// Draw guides
		Stroke normalStroke = g.getStroke();
		Stroke thickStroke = new BasicStroke(5);
		for (HexGuide guide : hexGuides) {
			HexMapPoint from = guide.getFrom();
			Rectangle fromR = hexDrawCoordinates.get(from);
			HexMapPoint to = guide.getTo();
			Rectangle toR = hexDrawCoordinates.get(to);
			if (toR!=null) {
				g.setColor(guide.getColor());
				
				Point fromP = new Point(fromR.x+(fromR.width>>1),fromR.y+(fromR.height>>1));
				Point toP = new Point(toR.x+(toR.width>>1),toR.y+(toR.height>>1));
				
				g.setStroke(thickStroke);
				g.drawLine(toP.x,toP.y,fromP.x,fromP.y);
				g.setStroke(normalStroke);
				
				int hexDistance = from.getDistanceFrom(to);
				if (hexDistance>2) {
					if (guide.getShowName()) {
						// Add a name identifier
						Point nameLocation = GraphicsUtil.getPointOnLine(fromP,toP,70);
						Rectangle hexNameRect = new Rectangle(nameLocation.x-10,nameLocation.y-10,20,20);
						
						g.setColor(guide.getColor());
						g.fill(hexNameRect);
						
						g.setColor(Color.black);
						g.draw(hexNameRect);
						GraphicsUtil.drawCenteredString(g,hexNameRect,guide.getName());
					}
					if (guide.getShowDistance()) {
						// Add a distance identifier
						Point distanceIDLocation = GraphicsUtil.getPointOnLine(fromP,toP,100);
						Rectangle hexDistRect = new Rectangle(distanceIDLocation.x-10,distanceIDLocation.y-10,20,20);
						
						g.setColor(guide.getColor());
						g.fill(hexDistRect);
						
						g.setColor(Color.black);
						g.draw(hexDistRect);
						GraphicsUtil.drawCenteredString(g,hexDistRect,""+hexDistance);
					}
				}
			}
			else {
				// how to handle wizards - they don't have a coordinate on the map...
			}
		}
		g.setStroke(normalStroke);
		
		// Setup token count, so that the number of tokens in each occupied hex is known
		// right now this isn't used, but will be
		Hashtable<HexMapPoint, HexTokenDistribution> tokenCountHash = new Hashtable<>();
		ArrayList<Token> badTokens = new ArrayList<>(); // this is only necessary to guarantee that my bug is fixed - keep gettin a demon token off map
		for (Token token:new ArrayList<>(tokens)) {
			HexMapPoint pos = token.getPosition();
			if (pos==null){
				badTokens.add(token);
				continue; // if the position ends up being null, then ignore it
			}
			HexTokenDistribution dist = tokenCountHash.get(pos);
			if (dist==null) {
				try {
					Rectangle rect = hexDrawCoordinates.get(pos);
					if (rect==null) {
						JOptionPane.showMessageDialog(this,"For some reason, "+pos+" has no draw rectangle in the hexDrawCoordinates hashtable!","Failed to draw Token:  "+token.getClass().getName(),JOptionPane.ERROR_MESSAGE);
						badTokens.add(token);
						continue; // skips to the next part of the loop
					}
					dist = new HexTokenDistribution(rect);
					tokenCountHash.put(pos,dist);
				}
				catch(IllegalArgumentException ex) {
					System.out.println("token = "+token.getClass().getName());
					System.out.println("pos = "+pos);
					System.out.println("in all? "+allHexPositions.contains(pos));
					System.out.println("hexDraw = "+hexDrawCoordinates.get(pos));
					ex.printStackTrace();
				}
			}
			dist.incrementTotal();
		}
		tokens.removeAll(badTokens);
		
		// Draw tokens
		for (Token token:new ArrayList<>(tokens)) {
			HexMapPoint pos = token.getPosition();
			HexTokenDistribution dist = tokenCountHash.get(pos);
			if (dist!=null) {
				Rectangle r = dist.getNextDrawRect();
				if (view!=null && r!=null && view.intersects(r)) {
					token.drawToken(g,r.x,r.y,r.width,r.height);
				}
				dist.incrementCurrent();
			}
		}
		
		// Draw move rules
		drawMoveRules(g,moveRulePos,moveRules,moveRuleRects);
	}
	protected void drawMoveRules(Graphics2D g,ArrayList<HexMapPoint> moveRulePos,ArrayList<MoveRule> moveRules,ArrayList<Rectangle> moveRuleRects) {
		if (showMoveCalculation || showMoveRuleText) {
			for (int i=0;i<moveRules.size();i++) {
				MoveRule rule = moveRules.get(i);
				Rectangle r = moveRuleRects.get(i);
				int half = r.height>>1;
				g.setFont(labelFont);
				g.setColor(Color.blue);
				
				if (showMoveRuleText) {
					Rectangle topR = new Rectangle(r.x,r.y+half-10,r.width,10);
					GraphicsUtil.drawCenteredString(g,topR,rule.getUserData().toString());
				}
				if (showMoveCalculation) {
					Rectangle botR = new Rectangle(r.x,r.y+half,r.width,10);
					GraphicsUtil.drawCenteredString(g,botR,rule.getMoveLeftString());
				}
			}
		}
	}
	public void drawTokenOnMap(Graphics g,Token token) {
		HexMapPoint pos = token.getPosition();
		Rectangle r = hexDrawCoordinates.get(pos);
		token.drawToken(g,r.x,r.y,r.width,r.height);
	}
	public boolean isBoatless(HexMapPoint pos) {
		MoveRule rule = selectionRules.get(pos);
		return (rule!=null && rule.getCanMoveBoatless());
	}
	public Collection<HexMapPoint> getSelectedPositions() {
		return selectionRules.keySet();
	}
	public void setSelectedPositions(Collection<HexMapPoint> list) {
		setSelectedPositions(list.toArray(new HexMapPoint[list.size()]));
	}
	public void setSelectedPositions(Hashtable<HexMapPoint,MoveRule> moveRules) {
		setSelectedPositions(moveRules.keySet());
		for (HexMapPoint pos : moveRules.keySet()) {
			MoveRule rule = moveRules.get(pos);
			selectionRules.put(pos,rule);
		}
		repaint();
	}
	public void setSelectedPositions(HexMapPoint[] pos) {
		selectionRules = new Hashtable<>();
		selectionBorder = new Hashtable<>();
		if (pos!=null) {
			for (int i=0;i<pos.length;i++) {
				selectionBorder.put(pos[i],""); // placeholder
			}
		}
		updateSelectionBorder();
		repaint();
	}
	public void saveSelectionState() {
		savedSelectionRules = (Hashtable<HexMapPoint, MoveRule>)selectionRules.clone();
		savedSelectionBorder = (Hashtable<HexMapPoint, Comparable>)selectionBorder.clone();
	}
	public void restoreSelectionState() {
		selectionRules = (Hashtable<HexMapPoint, MoveRule>)savedSelectionRules.clone();
		selectionBorder = (Hashtable<HexMapPoint, Comparable>)savedSelectionBorder.clone();
		updateSelectionBorder();
		repaint();
	}
	/**
	 * Return the selection rule for this selection position.  Returns null if none was applied.
	 */
	public MoveRule getSelectionRule(HexMapPoint pos) {
		return selectionRules.get(pos);
	}
	/**
	 * This will generate a number for every selected hex that will identify which sides require a border
	 */
	private void updateSelectionBorder() {
		for (HexMapPoint pos : selectionBorder.keySet()) {
			int flag = 0x00;
			HexMapPoint[] adj = pos.getAdjacentPoints();
			for (int n=0;n<adj.length;n++) {
				if (selectionBorder.get(adj[n])==null) {
					flag |= BORDER_BIT[n];
				}
			}
			selectionBorder.put(pos,Integer.valueOf(flag));
		}
	}
	public boolean isSelected(HexMapPoint pos) {
		return selectionBorder.get(pos)!=null;
	}
	public void clearSelection() {
		selectionRules = new Hashtable<>();
		selectionBorder = new Hashtable<>();
		repaint();
	}
	public Dimension getPreferredScrollableViewportSize() {
		return getSize();
	}
	public int getScrollableBlockIncrement(Rectangle visibleRect,int orientation,int direction) {
		return iconHeight*5;
	}
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}
	public int getScrollableUnitIncrement(Rectangle visibleRect,int orientation,int direction) {
		return iconHeight>>1;
	}
	public boolean isAdjacentToEmpty(HexMapPoint pos) {
		HexMapPoint[] check = pos.getAdjacentPoints();
		for (int n=0;n<check.length;n++) {
			if (allHexPositions.contains(check[n]) && getHex(check[n])==null) {
				if (accessibleConnection(pos,check[n])) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Returns true if player can move between these two points (no wall)
	 */
	public boolean accessibleConnection(HexMapPoint from,HexMapPoint to) {
		int direction = from.directionOf(to);
		if (direction>=0) {
			Hex fromHex = getHex(from);
			Hex toHex = getHex(to);
			
			return ((fromHex==null || !fromHex.hasWall(direction)) && 
					(toHex==null || !toHex.hasWall((direction+3)%6)));
		}
		return false;
	}
	public void addToken(Token token) {
		if (token.getPosition()==null) {
			throw new IllegalArgumentException("Can't add a Token without a position!");
		}
		tokens.add(token);
		Collections.sort(tokens,new Comparator<Token>() {
			public int compare(Token t1,Token t2) {
				return t1.sortOrder() - t2.sortOrder();
			}
		});
		repaint();
	}
	public ArrayList<Token> getAllTokens() {
		return tokens;
	}
	public HashLists<HexMapPoint,Token> getMappedTokens() {
		HashLists<HexMapPoint,Token> hl = new HashLists<>();
		for (Token token:tokens) {
			HexMapPoint pos = token.getPosition();
			hl.put(pos,token);
		}
		return hl;
	}
	public boolean isOnMap(Token token) {
		return (tokens.contains(token) && isValidHexPosition(token.getPosition()));
	}
	public boolean hasTokens(HexMapPoint pos) {
		for (Token token:tokens) {
			if (token.getPosition().equals(pos)) {
				return true;
			}
		}
		return false;
	}
	public ArrayList<Token> getTokens(HexMapPoint pos) {
		ArrayList<Token> ret = new ArrayList<>();
		for (Token token:tokens) {
			if (token.getPosition().equals(pos)) {
				ret.add(token);
			}
		}
		return ret;
	}
	public void removeToken(Token token) {
		tokens.remove(token);
		repaint();
	}
	public boolean isBlockedByToken(Token thisToken,HexMapPoint pos) {
		ArrayList<Token> posTokens = getTokens(pos);
		for (Token token:posTokens) {
			if (token!=thisToken && token.blocksMove()) {
				return true;
			}
		}
		return false;
	}
	public boolean isFocusTraversable() {
		return true;
	}
	/**
	 * @return Returns the showSelectShading.
	 */
	public boolean isShowSelectShading() {
		return showSelectShading;
	}
	/**
	 * @param showSelectShading The showSelectShading to set.
	 */
	public void setShowSelectShading(boolean showSelectShading) {
		this.showSelectShading = showSelectShading;
	}
	/**
	 * @return Returns the showRotatedHexes.
	 */
	public boolean isShowRotatedHexes() {
		return showRotatedHexes;
	}
	/**
	 * @param showRotatedHexes The showRotatedHexes to set.
	 */
	public void setShowRotatedHexes(boolean showRotatedHexes) {
		this.showRotatedHexes = showRotatedHexes;
		for (Hex hex : getHexes().values()) {
			hex.updateImageRotation(showRotatedHexes);
		}
		revalidate();
		repaint();
	}
	public double getCurrentScale() {
		return currentScale;
	}
	public void setCurrentScale(double currentScale) {
		this.currentScale = currentScale;
	}
}