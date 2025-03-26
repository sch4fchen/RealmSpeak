package com.robin.magic_realm.components.swing;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.*;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.AggressiveDialog;
import com.robin.general.swing.ComponentTools;
import com.robin.general.util.HashLists;
import com.robin.general.util.OrderedHashtable;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.utility.RealmLoader;
import com.robin.magic_realm.components.utility.RealmUtility;

/**
 * A chooser that will display a panel of buttons, each holding text and a number of RealmComponents.  Clicking
 * a button makes the decision, and the window closes.  The chooser will return the text and the components.
 */
public class RealmComponentOptionChooser extends AggressiveDialog {
	private static final int MAX_GROUP_SIZE = 12;
	private static final Font font = new Font("Ariel",Font.PLAIN,20);
	
	public static enum DisplayOption {
		Normal,
		Flipside,
		SmallIcon,
		MediumIcon,
		Darkside,
		FaceUp,
	}
	
	private Font TITLE_FONT = new Font("Dialog", Font.BOLD, 14);

	private OrderedHashtable<String, String> textHash;
	private HashLists<String, ArrayList<RealmComponent>> componentHashLists;
	private HashLists<Object, ImageIcon> iconHashLists;
	
	private JScrollPane viewComponentsPane;
	private RealmObjectPanel viewComponentsPanel;

	private JPanel buttonPanel;
	private JButton cancelButton;

	private String selectedKey = null;
	private String selectedText = null;
	private ArrayList<RealmComponent> selectedComponents = null;
	
	private int hTextPos = SwingConstants.LEADING;
	private int vTextPos = SwingConstants.CENTER;
	
	private int genId = 0; // used when an option is not added directly
	
	private int maxGroupSize = MAX_GROUP_SIZE;
	private int forceColumns = 0;
	private boolean fillByRow = true;
	private boolean sortBiggestFirst = false;

	public RealmComponentOptionChooser(JFrame parent, String title,boolean includeCancel) {
		super(parent, "", true);
		textHash = new OrderedHashtable<>();
		componentHashLists = new HashLists<>();
		iconHashLists= new HashLists<>();
		initComponents(title,includeCancel);
		updateLayout();
	}
	
	public RealmComponentOptionChooser(JFrame parent, String title,String cancelButton) {
		super(parent, "", true);
		textHash = new OrderedHashtable<>();
		componentHashLists = new HashLists<>();
		iconHashLists= new HashLists<>();
		initComponents(title,true,cancelButton);
		updateLayout();
	}

	public int getMaxGroupSize() {
		return maxGroupSize;
	}

	public void setMaxGroupSize(int maxGroupSize) {
		this.maxGroupSize = maxGroupSize;
	}
	
	public void setVisible(boolean val) {
		if (val) {
			updateLayout();
		}
		super.setVisible(val);
	}
	
	private void initComponents(String title,boolean includeCancel) {
		initComponents(title,includeCancel,null);
	}
	
	private void initComponents(String title,boolean includeCancel, String cancelButtonText) {
		buttonPanel = new JPanel(new BorderLayout());
		getContentPane().setLayout(new BorderLayout(5, 5));
		getContentPane().add(buttonPanel, "Center");
		if (includeCancel) {
			Box box = Box.createHorizontalBox();
			box.add(Box.createHorizontalGlue());
			String text = cancelButtonText==null?"Cancel":cancelButtonText;
			cancelButton = new JButton(text);
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					selectedKey = null;
					selectedText = null;
					selectedComponents = null;
					cleanExit();
				}
			});
			box.add(cancelButton);
			box.add(Box.createHorizontalGlue());
			getContentPane().add(box, "South");
		}
		
		JPanel topPanel = new JPanel(new BorderLayout());
		
		viewComponentsPanel = new RealmObjectPanel(false,false);
		viewComponentsPane = new JScrollPane(viewComponentsPanel);
		viewComponentsPane.setVisible(false);
		ComponentTools.lockComponentSize(viewComponentsPane,100,100);
		topPanel.add(viewComponentsPane,"North");
		
		JTextArea titleLabel = new JTextArea(title);
		titleLabel.setFont(TITLE_FONT);
		titleLabel.setEditable(false);
		titleLabel.setOpaque(false);
		topPanel.add(titleLabel,"South");
		
		getContentPane().add(topPanel, "North");
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}
	
	public void setViewComponents(Collection<RealmComponent> c) {
		viewComponentsPanel.addRealmComponents(c);
		viewComponentsPane.setVisible(true);
		pack();
	}
	
	public boolean hasOptions() {
		return !textHash.isEmpty();
	}
	
	public void addOption(String optionKey, String text) {
		textHash.put(optionKey, text);
	}
	
	public String generateOption() {
		return generateOption("");
	}
	public String generateOption(String val) {
		String option = "__GEN__"+(genId++)+">";
		addOption(option,val);
		return option;
	}
	
	/**
	 * Generates a numeric option, so that one doesn't HAVE to be created beforehand
	 */
	public String addRealmComponent(RealmComponent rc) {
		return addRealmComponent(rc,false);
	}
	public void addRealmComponent(RealmComponent rc,String text) {
		String key = generateOption(text);
		addRealmComponentToOption(key,rc);
	}
	/**
	 * Generates a numeric option, so that one doesn't HAVE to be created beforehand
	 */
	public String addRealmComponent(RealmComponent rc,boolean includeName) {
		String option = generateOption(includeName?rc.getGameObject().getName():"");
		addRealmComponentToOption(option,rc);
		return option;
	}

	public void addGameObjectToOption(String optionKey, GameObject go) {
		addRealmComponentToOption(optionKey,RealmComponent.getRealmComponent(go));
	}
	public void addRealmComponentToOption(String optionKey, RealmComponent rc) {
		addRealmComponentToOption(optionKey,rc,DisplayOption.Normal);
	}
	public void addRealmComponentToOption(String optionKey, RealmComponent rc,DisplayOption displayOption) {
		if (textHash.get(optionKey) == null) {
			throw new IllegalArgumentException("Invalid optionKey");
		}
		
		ImageIcon icon = null;
		if (rc.isTile()) {
			// Tiles are always treated the same way
			TileComponent tile = (TileComponent)rc;
			icon = tile.getRepresentativeIcon();
		}
		else {
			if (displayOption==DisplayOption.Flipside) {
				if (rc.isChit()) {
					icon = ((ChitComponent)rc).getFlipSideIcon();
				}
				else if (rc.isCard()) {
					icon = ((CardComponent)rc).getFlipSideIcon();
				}
			}
			else if (rc.isChit() && displayOption==DisplayOption.Darkside) {
				ChitComponent chit = (ChitComponent)rc;
				if (chit.isDarkSideUp()) {
					icon = ((ChitComponent)rc).getIcon(); // already darkside up, so don't flip it
				}
				else {
					icon = ((ChitComponent)rc).getFlipSideIcon();
				}
			}
			else if (displayOption==DisplayOption.MediumIcon) {
				icon = rc.getMediumIcon();
			}
			else if (displayOption==DisplayOption.SmallIcon) {
				icon = rc.getSmallIcon();
			}
			else if (rc.isCard() && displayOption==DisplayOption.FaceUp) {
				icon = rc.getFaceUpIcon();
			}
			else {
				icon = rc.getIcon();
			}
		}
		
		componentHashLists.put(optionKey, rc);
		iconHashLists.put(optionKey,icon);
//		updateLayout();
	}

	private JPanel createButtonPanel(ArrayList<String> keys) {
		int totalButtons = keys.size();
		int columns = (int) Math.sqrt(totalButtons);
		if (columns==0) {
			columns = 1;
		}
		if (forceColumns>0) {
			columns = forceColumns;
		}
		int rows = totalButtons / columns;
		if (rows == 0) {
			rows = 1;
		}
		if ((columns * rows) < totalButtons) { // make sure there is enough rows (rounding up)
			rows++;
		}
		int cellCount = rows * columns;
		
		JPanel panel = new JPanel(new BorderLayout());
		Box box = Box.createHorizontalBox();
		panel.add(box,"Center");
		JPanel[] column = new JPanel[columns];
		for (int i=0;i<columns;i++) {
			column[i] = new JPanel(new GridLayout(rows,1));
			box.add(column[i]);
		}
		
		if (sortBiggestFirst) {
			Collections.sort(keys,new Comparator<String>() {
				public int compare(String k1, String k2) {
					ArrayList<ImageIcon> list = iconHashLists.getList(k1);
					int c1 = list==null?-1:list.size();
					list = iconHashLists.getList(k2);
					int c2 = list==null?-1:list.size();
					return c2-c1;
				}
			});
		}
		
		int n=0;
		for (String key:keys) {
			String text = textHash.get(key);
			ArrayList rcs = componentHashLists.getList(key);
			ArrayList<ImageIcon> icons = iconHashLists.getList(key);
			SelectButton aButton = new SelectButton(key, text, rcs, icons);
			column[getColumn(rows,columns,n++)].add(aButton);
			cellCount--;
		}
		// Fill in the rest of the last row (if needed)
		for (int i = 0; i < cellCount; i++) {
			column[getColumn(rows,columns,n++)].add(Box.createGlue());
		}
		return panel;
	}
	private int getColumn(int rows,int columns,int pos) {
		return fillByRow?(pos%columns):(pos/rows);
	}

	private void updateLayout() {
		ArrayList<String> group = null;
		ArrayList<ArrayList<String>> send = new ArrayList<>();
		for (String key : textHash.keySet()) {
			if (group==null) {
				group = new ArrayList<>();
				send.add(group);
			}
			group.add(key);
			if (group.size()==maxGroupSize) {
				group = null;
			}
		}
		buttonPanel.removeAll();
		JTabbedPane tabs = null;
		if (send.size()>1) {
			tabs = new JTabbedPane(SwingConstants.LEFT);
			tabs.setFont(font);
			buttonPanel.add(tabs,"Center");
		}
		int n=1;
		for (ArrayList<String> list:send) {
			JPanel panel = createButtonPanel(list);
			if (tabs==null) {
				buttonPanel.add(panel,"Center");
			}
			else {
				tabs.addTab(String.valueOf(n++),panel);
			}
		}
		pack();
		setLocationRelativeTo(null);
	}

	private static ImageIcon buildIcon(Collection<ImageIcon> icons) {
		// Determine total size of icon
		int maxHeight = 0;
		int totalWidth = 0;
		int spacer = 2;
		for (ImageIcon icon : icons) {
			Dimension d = new Dimension(icon.getIconWidth(),icon.getIconHeight());
			if (totalWidth > 0) {
				totalWidth += spacer;
			}
			totalWidth += d.width;

			if (d.height > maxHeight) {
				maxHeight = d.height;
			}
		}

		// Now draw image
		BufferedImage image = new BufferedImage(totalWidth, maxHeight, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = image.getGraphics();
		int x = 0;
		for (ImageIcon icon : icons) {
			Dimension d = new Dimension(icon.getIconWidth(),icon.getIconHeight());
			if (x > 0) {
				x += spacer;
			}
			int y = (maxHeight - d.height) >> 1;
			g.drawImage(icon.getImage(),x,y,null);
			x += d.width;
		}

		return new ImageIcon(image);
	}
	public ArrayList<RealmComponent> getSelectedComponents() {
		return selectedComponents;
	}
	/**
	 * Convenience method to get the first component on the button or null if none selected
	 */
	public RealmComponent getFirstSelectedComponent() {
		if (selectedComponents!=null && !selectedComponents.isEmpty()) {
			return selectedComponents.get(0);
		}
		return null;
	}
	/**
	 * Convenience method to get the last component on the button or null if none selected
	 */
	public RealmComponent getLastSelectedComponent() {
		if (selectedComponents!=null && !selectedComponents.isEmpty()) {
			return selectedComponents.get(selectedComponents.size()-1);
		}
		return null;
	}
	public String getSelectedText() {
		return selectedText;
	}
	public String getSelectedOptionKey() {
		return selectedKey;
	}
	private void cleanExit() {
		setVisible(false);
		dispose();
	}
	/**
	 * Convenience method for doing a simple GameObject selection.  If the game object doesn't translate to a
	 * RealmComponent, then the name is used by itself.
	 */
	public void addGameObjects(Collection<GameObject> list,boolean includeName) {
		int keyN = 0;
		for (GameObject go : list) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			String key = "N"+(keyN++);
			addOption(key,(includeName || rc==null)?go.getName():"");
			if (rc!=null) {
				addRealmComponentToOption(key,rc);
			}
		}
	}
	public void addGameObject(GameObject go,String text) {
		addGameObject(go,text,DisplayOption.Normal);
	}
	public void addGameObject(GameObject go,String text,DisplayOption displayOption) {
		String key = generateOption(text);
		addRealmComponentToOption(key,RealmComponent.getRealmComponent(go),displayOption);
	}
	/**
	 * Convenience method for doing a simple RealmComponent selection
	 */
	public void addRealmComponents(Collection list,boolean includeName) {
		addRealmComponents(list,includeName,DisplayOption.Normal);
	}
	public void addRealmComponents(Collection<RealmComponent> list,boolean includeName,DisplayOption displayOption) {
		int keyN = 0;
		for (RealmComponent rc : list) {
			String key = "N"+(keyN++);
			addOption(key,includeName?rc.getGameObject().getName():"");
			addRealmComponentToOption(key,rc,displayOption);
		}
	}
	/**
	 * Convenience method for doing a simple String selection
	 */
	public void addStrings(Collection<String> list) {
		int keyN = 0;
		for (String string : list) {
			String key = "N"+(keyN++);
			addOption(key,string);
		}
	}

	private class SelectButton extends JButton implements ActionListener {
		private String key;
		private String text;
		private ArrayList<RealmComponent> rcs;

		public SelectButton(String key, String text, ArrayList<RealmComponent> rcs, Collection<ImageIcon> icons) {
			super(text);
			this.key = key;
			this.text = text;
			this.rcs = rcs;
			if (icons != null && !icons.isEmpty()) {
				setIcon(buildIcon(icons));
			}
			setHorizontalTextPosition(hTextPos);
			setVerticalTextPosition(vTextPos);
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent ev) {
			selectedKey = key;
			selectedText = text;
			selectedComponents = rcs;
			cleanExit();
		}
	}

	public int getForceColumns() {
		return forceColumns;
	}

	public void setForceColumns(int forceColumns) {
		this.forceColumns = forceColumns;
	}

	public void setButtonTextPosition(int hor,int ver) {
		hTextPos = hor;
		vTextPos = ver;
	}
	
	public boolean isFillByRow() {
		return fillByRow;
	}
	
	public void setFillByRow(boolean val) {
		fillByRow = val;
	}
	
	public boolean isSortBiggestFirst() {
		return sortBiggestFirst;
	}
	
	public void setSortBiggestFirst(boolean val) {
		sortBiggestFirst = val;
	}
	
	public static void main(String[] args) {
		ComponentTools.setSystemLookAndFeel();
		
		RealmUtility.setupTextType();

		System.out.print("loading...");
		RealmLoader loader = new RealmLoader();
		System.out.println("Done");
		GameObject character = loader.getData().getGameObjectByName("Wizard");
		System.out.println(character);

		RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(new JFrame(), "Choose one:",true);
		GameObject crag = loader.getData().getGameObjectByName("Crag");
		GameObject horse = loader.getData().getGameObjectByName("Horse");
		chooser.addOption("horse2","Side 2");
		chooser.addRealmComponentToOption("horse2",RealmComponent.getRealmComponent(horse),DisplayOption.Flipside);
		chooser.addRealmComponentToOption("horse2",RealmComponent.getRealmComponent(crag));
		chooser.addOption("horse1","Side 1");
		chooser.addRealmComponentToOption("horse1",RealmComponent.getRealmComponent(horse));
		chooser.addRealmComponentToOption("horse1",RealmComponent.getRealmComponent(crag));
		chooser.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.out.println("Exiting");
				System.exit(0);
			}
		});
		
		ArrayList<RealmComponent> testList = new ArrayList<>();
		testList.add(RealmComponent.getRealmComponent(loader.getData().getGameObjectByName("Bashkar 1")));
		testList.add(RealmComponent.getRealmComponent(loader.getData().getGameObjectByName("Bashkar 2")));
		testList.add(RealmComponent.getRealmComponent(loader.getData().getGameObjectByName("Bashkar 3")));
		
		chooser.setViewComponents(testList);
		chooser.setVisible(true);
		
		System.out.println("Selected Text = "+chooser.getSelectedText());
		System.out.println("Selected Components = "+chooser.getSelectedComponents());
		
		System.exit(0);
	}
}