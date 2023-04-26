package com.robin.magic_realm.RealmSpeak;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.robin.game.objects.*;
import com.robin.general.swing.ComponentTools;
import com.robin.general.swing.IconFactory;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.swing.CenteredMapView;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;
import com.robin.magic_realm.map.Tile;

public class RealmTilePickFrame extends RealmSpeakInternalFrame implements ChangeListener {
	
	private static final Font BIG_FONT = new Font("Dialog",Font.PLAIN,18);
	
	private GameWrapper game;
	private RealmGameHandler gameHandler;
	private CenteredMapView map;
	
	private JButton randomPlaceButton; // random select too?
	
	private JTable tilePickTable;
	private TilePickTableModel tilePickModel;
	private ArrayList<GameObject> tilesToAdd;
	
	private JLabel instruction;
	private boolean canPlace = false;
	private boolean validSelection;
	
	private String nextPlayer;
	
	// For solitaire
	private boolean solePlayer;
	private int solePlayerIndex = -1;
	
	public RealmTilePickFrame(RealmGameHandler handler,GameWrapper game,CenteredMapView map) {
		super("Tile Picker",true,false,true,true);
		this.gameHandler = handler;
		this.game = game;
		this.map = map;
		
		tilesToAdd = new ArrayList<>();
		tilePickModel = new TilePickTableModel();
		
		refreshTiles();
		initComponents();
	}
	public void refreshTiles() {
		map.rebuildFromScratch();
		tilesToAdd.clear();
		
		solePlayerIndex = -1;
		
		// Find tiles (if any) that are to be distributed
		GamePool pool = new GamePool(RealmObjectMaster.getRealmObjectMaster(gameHandler.getClient().getGameData()).getTileObjects());
		ArrayList<GameObject> tiles = pool.find(Constants.PLAYER_TO_PLACE+"="+gameHandler.getClient().getClientName());
		tilesToAdd.addAll(tiles);
		
		// Determine solePlayer:
		GameObject firstTile = tiles.iterator().next();
		String p1 = firstTile.getThisAttribute(Constants.PLAYER_TO_PLACE);
		String p2 = firstTile.getThisAttribute(Constants.PLAYER_TO_PLACE_NEXT);
		if (p1.equals(p2)) {
			solePlayer = true;
		}
		updateSolePlayerIndex();
	}
	/**
	 * This is fired when a tile is placed
	 */
	public void stateChanged(ChangeEvent e) {
		TileComponent tile = (TileComponent)e.getSource();
		int selRow = tilesToAdd.indexOf(tile.getGameObject());
//		tilePickModel.removeCache(selRow);
		GameObject go = tilesToAdd.remove(selRow);
		doNextPlayer(go);
		go.removeThisAttribute(Constants.PLAYER_TO_PLACE);
		go.removeThisAttribute(Constants.PLAYER_TO_PLACE_NEXT);
		solePlayerIndex = -1;
		updateSolePlayerIndex();
		tilePickModel.fireTableDataChanged();
		updateFrame();
		repaint();
		gameHandler.submitChanges();
		gameHandler.updatePickFrame();
		gameHandler.updateControls();
	}
	private void doNextPlayer(GameObject go) {
		if (map.isMapReady()) {
			game.setGameMapBuilder(null);
			GameData data = game.getGameObject().getGameData();
			HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(data);
			// Reset treasure location monsters
			RealmUtility.finishBoardSetupAfterBuild(hostPrefs,data);
		}
		else {
			if (go!=null) {
				nextPlayer = go.getThisAttribute(Constants.PLAYER_TO_PLACE_NEXT);
			}
			game.setGameMapBuilder(nextPlayer);
		}
	}
	private void initComponents() {
		setFrameIcon(IconFactory.findIcon("images/interface/build.gif"));
		getContentPane().setLayout(new BorderLayout());
		
		tilePickTable = new JTable(tilePickModel);
		tilePickTable.setFont(BIG_FONT);
		ComponentTools.lockColumnWidth(tilePickTable,0,160);
		ComponentTools.lockColumnWidth(tilePickTable,1,160);
		tilePickTable.setDefaultRenderer(String.class,new TilePickCellRenderer());
		tilePickModel.updateRowHeights(tilePickTable);
		tilePickTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tilePickTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				updateFrame();
			}
		});
		getContentPane().add(new JScrollPane(tilePickTable),"Center");
		
		instruction = new JLabel("",SwingConstants.CENTER);
		instruction.setFont(BIG_FONT);
		instruction.setOpaque(true);
		instruction.setBackground(MagicRealmColor.PALEYELLOW);
		getContentPane().add(instruction,"South");
		
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		randomPlaceButton = new JButton("Place Random");
		randomPlaceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				doRandomPlace();
			}
		});
		box.add(randomPlaceButton);
		box.add(Box.createHorizontalGlue());
		getContentPane().add(box,"North");
		
		updateFrame();
	}

	public void organize(JDesktopPane desktop) {
		Dimension size = desktop.getSize();
		int w = size.width >> 1;
		int h = ((size.height * 3) / 4) - BOTTOM_HEIGHT;
		int y = size.height - h;
		setSize(w, h);
		setLocation(0, y);
		try {
			setIcon(false);
		}
		catch (PropertyVetoException ex) {
			ex.printStackTrace();
		}
	}
	public boolean onlyOneInstancePerGame() {
		return true;
	}
	public String getFrameTypeName() {
		return "Tiles";
	}
	private TileComponent getSelectedTile() {
		int selRow = tilePickTable.getSelectedRow();
		ArrayList<GameObject> list = tilePickModel.getData();
		if (selRow>=0 && selRow<list.size()) {
			GameObject go = list.get(selRow);
			return (TileComponent)RealmComponent.getRealmComponent(go);
		}
		return null;
	}
	
	private int getBorderLandIndex() {
		for (int i=0;i<tilesToAdd.size();i++) {
			GameObject go = tilesToAdd.get(i);
			if (go.hasThisAttribute(Constants.ANCHOR_TILE) && !go.hasThisAttribute(Constants.BOARD_NUMBER)) {
				return i;
			}
		}
		return -1;
	}
	
	private void updateSolePlayerIndex() {
		if (solePlayerIndex==-1 && !tilesToAdd.isEmpty()) {
			solePlayerIndex = getBorderLandIndex();
			if (solePlayerIndex == -1) {
				boolean placePrioOneFirst = false;
				boolean placePrioTwoFirst = false;
				// No borderland?  Choose a random tile that CAN be placed.
				ArrayList<Integer> placeableIndices = new ArrayList<>();
				for (int i=0;i<tilesToAdd.size();i++) {
					GameObject go = tilesToAdd.get(i);
					Collection<Tile> c = map.getPlaceables(go);
					if (!c.isEmpty()) {
						placeableIndices.add(Integer.valueOf(i));
						go.setThisAttribute(Constants.PLACEABLE);
						if (go.hasThisAttribute(Constants.MAP_BUILDING_PRIO)) {
							if (go.getThisAttribute(Constants.MAP_BUILDING_PRIO).matches("1")) {
								placePrioOneFirst = true;
							} else {
								placePrioTwoFirst = true;
							}
						}
					}
					else {
						go.removeThisAttribute(Constants.PLACEABLE);
					}
				}
				if (canPlace) {
//if (placeableIndices.size()<8) placeableIndices.clear(); // FIXME force a problem
					if (placeableIndices.isEmpty()) {
						// We got a problem here!!  Gotta start over somehow...
						JOptionPane.showMessageDialog(gameHandler.getMainFrame(),"There are no more legal tile placements!  Starting the process over.","No Tile Placements",JOptionPane.ERROR_MESSAGE);
						gameHandler.submitChanges(); // make sure changes are submitted first
						gameHandler.broadcast(Constants.BROADCAST_SPECIAL_ACTION,Constants.MESSAGE_RESTART_MAP_BUILDER);
						return;
					}
					int r = -1;
					Integer n = -1;
					while (true) {
						r = RandomNumber.getRandom(placeableIndices.size());
						n = placeableIndices.get(r);
						if (placePrioOneFirst || placePrioTwoFirst) {
							GameObject go = tilesToAdd.get(n);
							if (!go.hasThisAttribute(Constants.MAP_BUILDING_PRIO)) continue;
							if (placePrioOneFirst && go.getThisAttribute(Constants.MAP_BUILDING_PRIO).matches("1")) break;
							if (!placePrioOneFirst && placePrioTwoFirst) break;
						} else {
							break;
						}
					}
					solePlayerIndex = n.intValue();
				}
			}
			else {
				GameObject go = tilesToAdd.get(solePlayerIndex);
				go.setThisAttribute(Constants.PLACEABLE);
			}
			tilePickModel.fireTableDataChanged();
		}
	}
	
	public void updateFrame() {
		if (solePlayer && solePlayerIndex>=0) {
			tilePickTable.getSelectionModel().setSelectionInterval(0,0);
		}
		
		map.rebuildFromScratch();
		
		String active = game.getGameMapBuilder();
		canPlace = (active!=null && active.equals(gameHandler.getClient().getClientName()));
		
		int validRow = getBorderLandIndex();
		
		int selRow = tilePickTable.getSelectedRow();
		validSelection = (validRow==-1 || selRow==validRow || (solePlayer && selRow>=0));
		
		if (canPlace) {
			if (tilePickTable.getSelectedRowCount()==0) {
				// This is sloppy, but...
				for (int i=0;i<tilesToAdd.size();i++) {
					GameObject go = tilesToAdd.get(i);
					Collection<Tile> c = map.getPlaceables(go);
					if (!c.isEmpty()) {
						go.setThisAttribute(Constants.PLACEABLE);
					}
					else {
						go.removeThisAttribute(Constants.PLACEABLE);
					}
				}
				repaint();
						
				instruction.setText("Select a tile to place...");
			}
			else {
				if (validSelection) {
					instruction.setText("Place your tile on the map");
					map.setTileBeingPlaced(this,getSelectedTile());
				}
				else {
					instruction.setText("You must select the starting tile (Borderland or Falls)");
				}
			}
		}
		else {
			instruction.setText("Wait for your turn...");
		}
		
		randomPlaceButton.setEnabled(!tilesToAdd.isEmpty() && canPlace);
		
		if (canPlace && tilesToAdd.isEmpty()) {
			doNextPlayer(null);
			repaint();
			gameHandler.submitChanges();
			gameHandler.updatePickFrame();
			gameHandler.updateControls();
		}
	}
	
	private void doRandomPlace() {
		tilePickTable.clearSelection();
		if (solePlayerIndex>=0) {
			ArrayList<GameObject> list = new ArrayList<>();
			list.add(tilesToAdd.get(solePlayerIndex));
			map.placeRandom(this,list);
		}
		else {
			map.placeRandom(this,tilesToAdd);
		}
	}
	
	private static final String NOT_PLACEABLE = " (Not Placeable)";
	private class TilePickCellRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
			
			String s = (String)value;
			if (s!=null && s.endsWith(NOT_PLACEABLE)) {
				setForeground(Color.red);
			}
			else {
				setForeground(Color.black);
			}
			
			return this;
		}
	}
	private class TilePickTableModel extends AbstractTableModel {
		private final String[] COL_HEADER = {
				"Tile",
				"Flip",
				"Name",
		};
		private final Class[] COL_CLASS = {
				ImageIcon.class,
				ImageIcon.class,
				String.class,
		};
		
		private Hashtable<GameObject,ImageIcon[]> cache;
		
		public TilePickTableModel() {
			cache = new Hashtable<>();
		}
		public ArrayList<GameObject> getData() {
			if (solePlayer) {
				ArrayList<GameObject> list = new ArrayList<>();
				if (solePlayerIndex>=0) {
					list.add(tilesToAdd.get(solePlayerIndex));
				}
				return list;
			}
			return tilesToAdd;
		}
		public int getColumnCount() {
			return COL_HEADER.length;
		}
		public String getColumnName(int col) {
			return COL_HEADER[col];
		}
		public Class getColumnClass(int col) {
			return COL_CLASS[col];
		}
		public int getRowCount() {
			return getData().size();
		}
		public Object getValueAt(int row, int col) {
			if (row<tilesToAdd.size()) {
				GameObject go = getData().get(row);
				switch(col) {
					case 0:
						return cache.get(go)[0];
					case 1:
						return cache.get(go)[1];
					case 2:
						if (!go.hasThisAttribute(Constants.PLACEABLE)) {
							return "   "+go.getName()+NOT_PLACEABLE;
						}
						return "   "+go.getName();
				}
			}
			return null;
		}
		public void fireTableDataChanged() {
			super.fireTableDataChanged();
			if (tilePickTable!=null) {
				updateRowHeights(tilePickTable);
			}
		}
		public void updateRowHeights(JTable table) {
			int maxIconColWidth = 0;
			for (int i=0;i<getData().size();i++) {
				GameObject go = getData().get(i);
				TileComponent rc = (TileComponent)RealmComponent.getRealmComponent(go);
				ImageIcon[] icon = cache.get(go);
				if (icon == null) {
					icon = new ImageIcon[2];
					icon[0] = rc.getTilePickIcon();
					icon[1] = rc.getTilePickFlipIcon();
					cache.put(go,icon);
				}
				table.setRowHeight(i,icon[0].getIconHeight()+8);
				if (icon[0].getIconWidth()>maxIconColWidth) {
					maxIconColWidth = icon[0].getIconWidth();
				}
			}
		}
	}
}