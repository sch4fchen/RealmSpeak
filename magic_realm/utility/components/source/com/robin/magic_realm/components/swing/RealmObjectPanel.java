package com.robin.magic_realm.components.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import com.robin.game.objects.GameObject;
import com.robin.general.graphics.GraphicsUtil;
import com.robin.general.swing.ImageCache;
import com.robin.magic_realm.components.*;

public class RealmObjectPanel extends JPanel implements Scrollable {
	private static final ArrayList<RealmComponent> EMPTY_LIST = new ArrayList<>();
	public static final int SINGLE_SELECTION = 1;
	public static final int MULTIPLE_SELECTION = 2;
	
	private static ImageIcon flipIcon = null;
	
	private static final Color disabledColor = new Color(255,255,255,180);

	protected ArrayList selected = new ArrayList();
	protected boolean selectionEnabled;
	protected boolean manualFlipEnabled;
	protected int selectionMode = MULTIPLE_SELECTION;
	
	protected boolean flipView = false;
	
	private ArrayList<ListSelectionListener> listSelectionListeners;
	
	private FlowLayout layout;
	
	private ArrayList<RealmComponent> flipped = new ArrayList<>();
	
	public RealmObjectPanel() {
		this(false,false); // default panel is non-interactive
	}
	public RealmObjectPanel(boolean enableSelection,boolean enableManualFlip) {
		super();
		this.selectionEnabled = enableSelection;
		this.manualFlipEnabled = enableManualFlip;
		layout = new FlowLayout();
		setLayout(layout);
		if (enableSelection || enableManualFlip) { // no point adding a listener unless one of these is true
			addMouseListener(mouseListener);
		}
		addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) {
				adjustSize();
			}
			public void ancestorMoved(AncestorEvent event) {
			}

			public void ancestorRemoved(AncestorEvent event) {
			}
		});
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent event) {
				adjustSize();
			}
		});
	}
	public void setManualFlipEnabled(boolean val) {
		manualFlipEnabled = val;
	}
	public boolean getManualFlipEnabled() {
		return manualFlipEnabled;
	}
	public void enableSelection() {
		disableSelection();
		addMouseListener(mouseListener);
	}
	public void disableSelection() {
		removeMouseListener(mouseListener);
	}
	public void activateFlipView() {
		if (!flipView) {
			addMouseListener(flipViewListener);
			flipView = true;
			repaint();
		}
	}
	/**
	 * This method was necessary to workaround a bug in Java 1.5 where findComponentAt wasn't working!!
	 */
	private RealmComponent findRealmComponentAt(Point p) {
		Component[] c = getComponents();
		if (c!=null && c.length>0) {
			for (int i=0;i<c.length;i++) {
				if (c[i] instanceof RealmComponent) {
					if (c[i].getBounds().contains(p.x,p.y)) {
						return (RealmComponent)c[i];
					}
				}
			}
		}
		return null;
	}
	public ArrayList<RealmComponent> getAllRealmComponents() {
		Component[] c = getComponents();
		if (c!=null && c.length>0) {
			ArrayList<RealmComponent> list = new ArrayList<>();
			for (int i=0;i<c.length;i++) {
				if (c[i] instanceof RealmComponent) {
					list.add((RealmComponent)c[i]);
				}
			}
			return list;
		}
		return null;
	}
	public void removeAll() {
//		clearSelected();
		if (getComponentCount()>0) {
			selected.clear();
			super.removeAll();
		}
	}
	public void addSelectionListener(ListSelectionListener listener) {
		if (listSelectionListeners == null) {
			listSelectionListeners = new ArrayList<>();
		}
		if (!listSelectionListeners.contains(listener)) {
			listSelectionListeners.add(listener);
		}
	}
	public void removeSelectionListener(ListSelectionListener listener) {
		if (listSelectionListeners!=null && listSelectionListeners.contains(listener)) {
			listSelectionListeners.remove(listener);
			if (listSelectionListeners.isEmpty()) {
				listSelectionListeners = null;
			}
		}
	}
	private void fireSelectionChanged() {
		if (listSelectionListeners!=null) {
			ListSelectionEvent ev = new ListSelectionEvent(this,0,0,false);
			for (ListSelectionListener listener : listSelectionListeners) {
				listener.valueChanged(ev);
			}
		}
	}
	public void selectAll() {
		selected.clear();
		selected.addAll(Arrays.asList(getComponents()));
		repaint();
		fireSelectionChanged();
	}
	public void addSelected(Component comp) {
		if (selected.contains(comp)) {
			selected.remove(comp);
		}
		else {
			selected.add(comp);
		}
		repaint();
		fireSelectionChanged();
	}
	public void setSelected(Component comp) {
		selected.clear();
		addSelected(comp);
	}
	public void clearSelected() {
		selected.clear();
		repaint();
		fireSelectionChanged();
	}
	public GameObject[] getSelectedGameObjects() {
		ArrayList<GameObject> gameObjects = new ArrayList<>();
		for (Iterator i=selected.iterator();i.hasNext();) {
			RealmComponent comp = (RealmComponent)i.next();
			gameObjects.add(comp.getGameObject());
		}
		return gameObjects.toArray(new GameObject[gameObjects.size()]);
	}
	public GameObject getSelectedGameObject() {
		GameObject[] sel = getSelectedGameObjects();
		if (sel.length>0) {
			return sel[0];
		}
		return null;
	}
	public RealmComponent getSelectedComponent() {
		if (selected.size()>0) {
			return (RealmComponent)selected.get(0);
		}
		return null;
	}
	public Collection<RealmComponent> getSelectedComponents() {
		return new ArrayList<RealmComponent>(selected);
	}
	public int getSelectedCount() {
		return selected.size();
	}
	public boolean removeGameObject(GameObject obj) {
		Component[] all = getComponents();
		for (int i=0;i<all.length;i++) {
			if (all[i] instanceof RealmComponent) {
				GameObject test = ((RealmComponent)all[i]).getGameObject();
				if (test==obj) {// test pointers
					remove(all[i]);
					adjustSize();
					return true;
				}
			}
		}
		return false;
	}
	public void addRealmComponent(RealmComponent rc) {
		super.add(rc);
		adjustSize();
	}
	public void addRealmComponents(Collection<RealmComponent> c) {
		for (RealmComponent rc : c) {
			addRealmComponent(rc);
		}
	}
	public void addObject(GameObject obj) {
		RealmComponent comp = RealmComponent.getRealmComponent(obj);
		if (comp!=null && !(comp instanceof TileComponent)) {
			super.add(comp);
		}
		adjustSize();
	}
	public void addObjects(Collection<GameObject> c) {
		for (GameObject go : c) {
			RealmComponent comp = RealmComponent.getRealmComponent(go);
			if (comp!=null && !(comp instanceof TileComponent)) {
				super.add(comp);
			}
		}
		adjustSize();
	}
	public Component add(Component comp,int index) {
		throw new IllegalArgumentException("Use addObject instead.");
	}
	public void add(Component comp,Object constraints) {
		throw new IllegalArgumentException("Use addObject instead.");
	}
	public void add(Component comp,Object constraints,int index) {
		throw new IllegalArgumentException("Use addObject instead.");
	}
	public void adjustSize() {
		validate();
		doLayout(); // This is important to guarantee all the components have positions before adjusting the size
		Component[] comp = getComponents();
		if (comp.length>0) {
			int maxW = 100;
			int maxH = 100;
			for (int i=0;i<comp.length;i++) {
				Rectangle corn = comp[i].getBounds();
				
				if ((corn.x + corn.width)>maxW) {
					maxW = corn.x + corn.width;
				}
				if ((corn.y + corn.height)>maxH) {
					maxH = corn.y + corn.height;
				}
			}
			Dimension d = new Dimension(maxW,maxH);
			setPreferredSize(d);
			revalidate();
		}
	}
	public void paint(Graphics g) {
		Collection<RealmComponent> rcs = getAllRealmComponents();
		if (rcs==null) rcs = EMPTY_LIST;
		if (flipViewOn==true) {
			for (RealmComponent rc : rcs) {
				if (rc.isChit()) {
					ChitComponent chit = (ChitComponent)rc;
					chit.setShowFlipSide(true);
				}
			}
		}
		
		super.paint(g);
		
		Color edgeColor = Color.blue;
		Color mainColor = Color.cyan;
		for (Iterator i=selected.iterator();i.hasNext();) {
			Component comp = (Component)i.next();
			Rectangle rect = comp.getBounds();
			for (int n=0;n<4;n++) {
				Color color = GraphicsUtil.convertColor(edgeColor,mainColor,(n*100)/4);
				g.setColor(color);
				g.drawRect(rect.x+n,rect.y+n,rect.width-(n<<1),rect.height-(n<<1));
			}
		}
		if (!isEnabled()) {
			g.setColor(disabledColor);
			for (RealmComponent rc : rcs) {
				Rectangle r = rc.getBounds();
				g.fillRect(r.x,r.y,r.width,r.height);
			}
		}
		else if (flipView && !rcs.isEmpty()) {
			if (flipIcon==null) {
				flipIcon = ImageCache.getIcon("actions/peer",16,16);
			}
			g.setColor(Color.green);
			g.fillRect(0,0,24,24);
			g.drawImage(flipIcon.getImage(),4,4,null);
		}
		
		if (flipViewOn==true) {
			for (Iterator i=rcs.iterator();i.hasNext();) {
				RealmComponent rc = (RealmComponent)i.next();
				if (rc.isChit()) {
					ChitComponent chit = (ChitComponent)rc;
					chit.setShowFlipSide(false);
				}
			}
		}
	}
	public void setSelectionMode(int val) {
		if (selectionMode==SINGLE_SELECTION || selectionMode==MULTIPLE_SELECTION) {
			selectionMode = val;
		}
		else throw new IllegalArgumentException("Invalid selection mode");
	}
	public int getSelectionMode() {
		return selectionMode;
	}
	
	
	// Scrollable interface
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return visibleRect.height>>1;
	}
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 5;
	}
	
	MouseListener mouseListener = new MouseAdapter() {
		public void mousePressed(MouseEvent ev) {
			Point mousePos = ev.getPoint();
			RealmComponent target = findRealmComponentAt(mousePos);
			if (target!=null) {
				if (manualFlipEnabled && ev.isShiftDown()) {
					// flip the object
					if (!target.isSpell() && !target.isTreasure()) {
						target.flip();
						updateFlipped(target);
						repaint();
					}
				}
				else if (selectionEnabled) {
					if (selectionMode==MULTIPLE_SELECTION) {
						addSelected(target);
					}
					else {
						setSelected(target);
					}
				}
			}
		}
	};
	
	private void updateFlipped(RealmComponent frc) {
		for(int i=0;i<flipped.size();i++) {
			RealmComponent rc = flipped.get(i);
			if (rc==frc) {
				flipped.remove(i);
				return;
			}
		}
		flipped.add(frc);
	}
	
	public void restoreFlipped() {
		for(RealmComponent rc:flipped) {
			rc.flip();
		}
		flipped.clear();
	}
	
	private boolean flipViewOn = false;
	MouseAdapter flipViewListener = new MouseAdapter() {
		public void mousePressed(MouseEvent ev) {
			Point p = ev.getPoint();
			if (p.x<24 && p.y<24) {
				flipViewOn = true;
				repaint();
			}
		}
		public void mouseReleased(MouseEvent ev) {
			flipViewOn = false;
			repaint();
		}
		public void mouseExited(MouseEvent ev) {
			flipViewOn = false;
			repaint();
		}
	};
	
	/**
	 * Testing
	 */
	public static void main(String[]args) {
		JFrame frame = new JFrame("RealmObjectPanel test");
		frame.setSize(640,480);
		frame.getContentPane().setLayout(new BorderLayout());
		RealmObjectPanel rop = new RealmObjectPanel();
		frame.getContentPane().add(new JScrollPane(rop),"Center");
//		RealmLoader loader = new RealmLoader();
//		Collection monsters = loader.getMonsters();
//		rop.addObjects(monsters);
		frame.setVisible(true);
	}
}