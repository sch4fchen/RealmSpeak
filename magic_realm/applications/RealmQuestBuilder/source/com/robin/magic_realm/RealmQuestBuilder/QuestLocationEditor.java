package com.robin.magic_realm.RealmQuestBuilder;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.*;

import com.robin.game.objects.*;
import com.robin.general.swing.*;
import com.robin.magic_realm.RealmCharacterBuilder.EditPanel.CompanionEditPanel;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.quest.*;

public class QuestLocationEditor extends GenericEditor {
	
	private static final String INVALID = " <== INVALID";
	
	private static ArrayList<String> suggestionWords;
	
	private JFrame parent;
	private Quest quest;
	private QuestLocation location;
	
	private JTextField name;
	private JComboBox<LocationType> type;
	private JCheckBox hideNotifcation;
	private JCheckBox locationForClonedQuests;
	private JComboBox<LocationClearingType> clearingType;
	private JComboBox<LocationTileSideType> tileSideType;
	private JRadioButton sameClearing;
	private JRadioButton sameTile;
	private JLabel descriptionLabel;
	private SuggestionTextArea locationList;
	
	private static String [] companions = getAllCompanionNames();
	
	private static String[] getAllCompanionNames() {
		ArrayList<String> companions = new ArrayList<>();
		String[] people = CompanionEditPanel.COMPANIONS[3];
			boolean first = true;
			for (String name : people) {
				// skip first every time
				if (first) {
					first = false;
					continue;
				}
				String[] ret = name.split(":");
				companions.add(ret[0]);
			}
		return companions.toArray(new String[0]);
	}
	
	public QuestLocationEditor(JFrame parent,GameData realmSpeakData,Quest quest,QuestLocation location) {
		super(parent,realmSpeakData);
		this.parent = parent;
		this.quest = quest;
		this.location = location;
		if (suggestionWords==null) {
			initSuggestionWords(realmSpeakData);
		}
		initComponents();
		setLocationRelativeTo(parent);
		readLocation();
	}
	
	private static void initSuggestionWords(GameData realmSpeakData) {
		suggestionWords = new ArrayList<>();
		Collections.addAll(suggestionWords, QuestConstants.wolfs);
		Collections.addAll(suggestionWords, QuestConstants.transforms);
		Collections.addAll(suggestionWords, companions);
		GamePool pool = new GamePool(realmSpeakData.getGameObjects());
		String query = "!part,!summon,!spell,!tile,!character_chit,!virtual_dwelling,!season,!test,!character";
		for(GameObject go:pool.find(query)) {
			if (suggestionWords.contains(go.getName())) continue;
			suggestionWords.add(go.getName());
		}
		for(GameObject go:pool.find("tile")) {
			TileComponent tile = (TileComponent)RealmComponent.getRealmComponent(go);
			if (!suggestionWords.contains(tile.getTileCode())) {
				suggestionWords.add(tile.getTileCode());
			}
			if (!suggestionWords.contains(go.getName())) {
				suggestionWords.add(go.getName());
			}
			for(ClearingDetail clearing:tile.getClearings()) {
				String name = go.getName()+" "+clearing.getNum();
				if (!suggestionWords.contains(name)) {
					suggestionWords.add(name);
				}
			}
		}
		Collections.sort(suggestionWords);
	}
	
	private void readLocation() {
		name.setText(location.getName());
		type.setSelectedItem(location.getLocationType());
		hideNotifcation.setSelected(location.hideNotification());
		locationForClonedQuests.setSelected(location.locationForClonedQuests());
		clearingType.setSelectedItem(location.getLocationClearingType());
		tileSideType.setSelectedItem(location.getLocationTileSideType());
		sameClearing.setSelected(!location.isSameTile());
		sameTile.setSelected(location.isSameTile());
		if (location.getChoiceAddresses()!=null) {
			StringBuilder sb = new StringBuilder();
			for (String i : location.getChoiceAddresses()) {
				if (sb.length()>0) {
					sb.append("\n");
				}
				sb.append(i);
			}
			locationList.setText(sb.toString());
			verifyLocations();
		}
	}
	protected boolean isValidForm() {
		return true;
	}
	protected void save() {
		saveLocation();
	}
	private void saveLocation() {
		location.setName(name.getText());
		location.setLocationType((LocationType)type.getSelectedItem());
		location.setHideNotification(hideNotifcation.isSelected());
		location.setLocationForClonedQuests(locationForClonedQuests.isSelected());
		location.setLocationClearingType((LocationClearingType)clearingType.getSelectedItem());
		location.setLocationTileSideType((LocationTileSideType)tileSideType.getSelectedItem());
		location.setSameTile(sameTile.isSelected());
		location.clearChoiceAddresses();
		for (String token:getLocationList()) {
			location.addChoiceAddresses(token);
		}
	}
	private void findLocations() {
		ListChooser chooser = new ListChooser(parent,"Locations:",suggestionWords);
		chooser.setDoubleClickEnabled(true);
		chooser.setLocationRelativeTo(this);
		chooser.setVisible(true);
		Vector list = chooser.getSelectedObjects();
		if (list==null) return;
		StringBuilder sb = new StringBuilder();
		for (String token:getLocationList()) {
			sb.append(token);
			if (!QuestLocation.validLocation(realmSpeakData,token)) {
				sb.append(INVALID);
			}
			sb.append("\n");
		}
		for(Object val:list) {
			sb.append(val.toString());
			sb.append("\n");
		}
		locationList.setText(sb.toString());
	}
	private void verifyLocations() {
		StringBuilder sb = new StringBuilder();
		for (String token:getLocationList()) {
			sb.append(token);
			if (!Arrays.asList(QuestConstants.wolfs).contains(token) && !Arrays.asList(QuestConstants.transforms).contains(token) && !Arrays.asList(companions).contains(token) && !QuestLocation.validLocation(realmSpeakData,token)) {
				sb.append(INVALID);
			}
			sb.append("\n");
		}
		locationList.setText(sb.toString());
	}
	private ArrayList<String> getLocationList() {
		ArrayList<String> list = new ArrayList<>();
		String text = locationList.getText();
		text = text.replaceAll(INVALID,"");
		StringTokenizer tokens = new StringTokenizer(text,",;:\t\n\r\f");
		while(tokens.hasMoreTokens()) {
			list.add(tokens.nextToken().trim());
		}
		return list;
	}
	private void initComponents() {
		setTitle("Quest Location");
		setSize(800,480);
		setLayout(new BorderLayout());
		add(buildForm(),BorderLayout.CENTER);
		add(buildOkCancelLine(),BorderLayout.SOUTH);
		
		updateControls();
	}
	private void updateControls() {
		String locName = name.getText();
		boolean conflict = false;
		for (QuestLocation loc:quest.getLocations()) {
			if (loc!=location && loc.getName().equals(locName)) {
				conflict = true;
				break;
			}
		}
		
		LocationType lt = (LocationType)type.getSelectedItem();
		descriptionLabel.setText(lt.getDescription());
		
		okButton.setEnabled(!conflict);
	}
	private Box buildForm() {
		Box form = Box.createVerticalBox();
		Box line;
		UniformLabelGroup group = new UniformLabelGroup();
		form.add(Box.createVerticalStrut(10));
		
		line = group.createLabelLine("Name");
		name = new JTextField();
		name.setDocument(new PlainDocument() {
			public void insertString (int offset, String  str, AttributeSet attr) throws BadLocationException {
				if (str == null) return;
				StringBuffer temp=new StringBuffer();
				for (int i=0;i<str.length();i++) {
					if (!Character.isWhitespace(str.charAt(i))) {
						temp.append(str.charAt(i));
					}
				}
				if (temp.length()>0)
					super.insertString(offset,temp.toString(),attr);
			}
		});
		name.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				updateControls();
			}
		});
		ComponentTools.lockComponentSize(name,200,25);
		line.add(name);
		line.add(Box.createHorizontalStrut(10));
		line.add(new JLabel("(No spaces allowed)"));
		line.add(Box.createHorizontalGlue());
		form.add(line);
		form.add(Box.createVerticalStrut(10));
		
		line = group.createLabelLine("Clearing Type");
		clearingType = new JComboBox<>(LocationClearingType.values());
		ComponentTools.lockComponentSize(clearingType,100,25);
		clearingType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				updateControls();
			}
		});
		line.add(clearingType);
		line.add(Box.createHorizontalGlue());
		form.add(line);
		form.add(Box.createVerticalStrut(10));
		
		line = group.createLabelLine("Tile Side");
		tileSideType = new JComboBox<>(LocationTileSideType.values());
		ComponentTools.lockComponentSize(tileSideType,100,25);
		tileSideType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				updateControls();
			}
		});
		line.add(tileSideType);
		line.add(Box.createHorizontalGlue());
		form.add(line);
		form.add(Box.createVerticalStrut(10));
		
		line = group.createLabelLine("Clearing or Tile (only for requirements)");
		ButtonGroup bg = new ButtonGroup();
		sameClearing = new JRadioButton("Same Clearing",true);
		bg.add(sameClearing);
		line.add(sameClearing);
		sameTile = new JRadioButton("Same Tile",false);
		bg.add(sameTile);
		line.add(sameTile);
		line.add(Box.createHorizontalGlue());
		form.add(line);
		form.add(Box.createVerticalStrut(10));
		
		line = group.createLabelLine("Type");
		type = new JComboBox<>(LocationType.values());
		ComponentTools.lockComponentSize(type,100,25);
		type.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				updateControls();
			}
		});
		line.add(type);
		line.add(Box.createHorizontalStrut(10));
		descriptionLabel = new JLabel("");
		line.add(descriptionLabel);
		line.add(Box.createHorizontalGlue());
		form.add(line);
		form.add(Box.createVerticalStrut(10));
		
		line = group.createLabelLine("Hide notifcation when selecting location");
		hideNotifcation = new JCheckBox();
		ComponentTools.lockComponentSize(hideNotifcation,100,25);
		hideNotifcation.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				updateControls();
			}
		});
		line.add(hideNotifcation);
		line.add(Box.createHorizontalGlue());
		form.add(line);
		
		line = group.createLabelLine("Set location for all cloned quests");
		locationForClonedQuests = new JCheckBox();
		ComponentTools.lockComponentSize(locationForClonedQuests,100,25);
		locationForClonedQuests.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				updateControls();
			}
		});
		line.add(locationForClonedQuests);
		line.add(Box.createHorizontalGlue());
		form.add(line);
		form.add(Box.createVerticalStrut(10));
		
		line = group.createLabelLine("Location(s)");
		locationList = new SuggestionTextArea(10,40);
		locationList.setLineModeOn(true);
		locationList.setAutoSpace(false);
		locationList.setWords(suggestionWords);
		line.add(new JScrollPane(locationList));
		Box buttons = Box.createVerticalBox();
		JButton findButton = new JButton(IconFactory.findIcon("icons/search.gif"));
		findButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				findLocations();
			}
		});
		ComponentTools.lockComponentSize(findButton,80,50);
		buttons.add(findButton);
		buttons.add(Box.createVerticalGlue());
		JButton verifyButton = new JButton("Verify");
		verifyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				verifyLocations();
			}
		});
		ComponentTools.lockComponentSize(verifyButton,80,50);
		buttons.add(verifyButton);
		line.add(buttons);
		line.add(Box.createHorizontalGlue());
		form.add(line);
		
		form.add(Box.createVerticalGlue());
		
		return form;
	}
}