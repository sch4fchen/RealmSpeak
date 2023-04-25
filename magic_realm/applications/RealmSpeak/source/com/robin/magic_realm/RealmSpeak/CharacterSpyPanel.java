package com.robin.magic_realm.RealmSpeak;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.ImageCache;
import com.robin.general.swing.JSplitPaneImproved;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.quest.QuestState;
import com.robin.magic_realm.components.swing.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class CharacterSpyPanel extends JPanel {
	
	private static final Font font = new Font("Dialog",Font.BOLD,18);
	private static final Font font2 = new Font("Dialog",Font.BOLD,12);
	
	private RealmGameHandler gameHandler;
	private JTabbedPane tabPane;
	private CharacterWrapper character;
	
	public CharacterSpyPanel(RealmGameHandler gameHandler,CharacterWrapper character) {
		this.gameHandler = gameHandler;
		this.character = character;
		initComponents();
	}
	private void initComponents() {
		setLayout(new BorderLayout());
		
		tabPane = new JTabbedPane(SwingConstants.BOTTOM);
			
		// Character Card
		ImageIcon icon = CharacterChooser.getCharacterImage(character.getGameObject());
		tabPane.addTab("",ImageCache.getIcon("actions/peer"),new JLabel(icon),"Character Card");
		
		// Stats
		Box box = Box.createVerticalBox();
		box.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		
		// Attributes
		ArrayList<String> specialAdvantagesText = character.getGameObject().getThisAttributeList("advantages");
		box.add(getTitle("Special Advantages:"));
		if (specialAdvantagesText!=null) {
			for (String sa : specialAdvantagesText) {
				box.add(getTextLine("    "+sa));
			}
		}
		else {
			box.add(getTextLine("    None"));
		}
		
		// Attributes
		ArrayList<String> optionalAdvantagesText = character.getGameObject().getAttributeList("optional","advantages");
		box.add(getTitle("Optional Advantages:"));
		if (optionalAdvantagesText!=null) {
			for (String sa : optionalAdvantagesText) {
				box.add(getTextLine("    "+sa));
			}
		}
		else {
			box.add(getTextLine("    None"));
		}
		
		Box line = Box.createHorizontalBox();
		JLabel statTitle = new JLabel("Current Fame = ");
		statTitle.setFont(font);
		line.add(statTitle);
		JLabel statLabel = new JLabel(character.getFameString());
		statLabel.setFont(font);
		line.add(statLabel);
		line.add(Box.createHorizontalGlue());
		box.add(line);
		
		line = Box.createHorizontalBox();
		statTitle = new JLabel("Current Notoriety = ");
		statTitle.setFont(font);
		line.add(statTitle);
		statLabel = new JLabel(character.getNotorietyString());
		statLabel.setFont(font);
		line.add(statLabel);
		line.add(Box.createHorizontalGlue());
		box.add(line);
		
		if (character.foundHiddenEnemies()) {
			line = Box.createHorizontalBox();
			JLabel label = new JLabel(ImageCache.getIcon("interface/hiddenenemies"));
			label.setFont(font);
			line.add(label);
			if (character.foundAllHiddenEnemies()) {
				label.setText("Found Hidden Enemies");
			}
			else {
				StringBuffer sb = new StringBuffer();
				for (String he:character.getFoundEnemies()) {
					if (sb.length()==0) {
						sb.append("Found Hidden Enemies: ");
					}
					else {
						sb.append(",");
					}
					sb.append(he);
				}
				label.setText(sb.toString());
			}
			line.add(Box.createHorizontalGlue());
			box.add(line);
		}
		
		// Curses
		if (character.hasCurses()) {
			line = Box.createHorizontalBox();
			JLabel label = new JLabel("Curses:");
			label.setFont(font);
			line.add(label);
			CharacterFrame.updateActiveCurses(character,line);
			line.add(Box.createHorizontalGlue());
			box.add(line);
		}
		
		box.add(Box.createVerticalGlue());
		JPanel statPanel = new JPanel(new BorderLayout());
		statPanel.add(box,"Center");
		tabPane.addTab("",ImageCache.getIcon("tab/victoryreq"),statPanel,"Character Stats");
		
		// Chits
		ChitStateViewer viewer = new ChitStateViewer(gameHandler.getMainFrame(),character,false);
		tabPane.addTab("",ImageCache.getIcon("tab/chits"),viewer.getMasterPanel(),"Character Chits");
		
		// Active Inventory
		JPanel activeInventoryPanel = new JPanel(new FlowLayout());
		for (GameObject go:character.getActiveInventory()) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			activeInventoryPanel.add(new JLabel(rc.getIcon())); // add the icon so that objects aren't removed from the interface elsewhere
		}
		JScrollPane spi = new JScrollPane(activeInventoryPanel);
		spi.setBorder(BorderFactory.createTitledBorder("Activated Inventory"));
		tabPane.addTab("",ImageCache.getIcon("tab/inventory"),spi,"Active Inventory");
		
		// Relationships
		RelationshipTable relationshipTable = new RelationshipTable(gameHandler.getRelationshipNames(),character);
		tabPane.addTab("",ImageCache.getIcon("tab/relationships"),new JScrollPane(relationshipTable),"Current Relationships");
		
		// Following Hirelings
		RealmObjectPanel hirelings = new RealmObjectPanel(false,false);
		hirelings.addRealmComponents(character.getFollowingHirelings());
		JScrollPane sph = new JScrollPane(hirelings);
		sph.setBorder(BorderFactory.createTitledBorder("Following Hirelings"));
		tabPane.addTab("",ImageCache.getIcon("tab/followers"),sph,"Following Hirelings");
		
		// Active and Completed Quests
		if (character.getQuestCount()>0) {
			JPanel questsPanel = new JPanel(new FlowLayout());
			JPanel finishedQuestsPanel = new JPanel(new FlowLayout());
			HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(character.getGameData());
			int slots = character.getQuestSlotCount(hostPrefs);
			for (Quest quest:character.getAllQuests()) {
				QuestCardComponent card = (QuestCardComponent)RealmComponent.getRealmComponent(quest.getGameObject());
				
				QuestState state = quest.getState();
				if (state==QuestState.Assigned && !quest.isAllPlay()) {
					questsPanel.add(new JLabel(new ImageIcon(card.getFaceDownImage())));
					slots--;
				}
				else if (state==QuestState.Active && !quest.isAllPlay()) {
					questsPanel.add(new JLabel(new ImageIcon(card.getFaceUpImage())));
					slots--;
				}
				else if (state==QuestState.Complete || state==QuestState.Failed) {
					finishedQuestsPanel.add(new JLabel(new ImageIcon(card.getFaceUpImage())));
				}
			}
			for(int i=0;i<slots;i++) {
				questsPanel.add(new EmptyCardComponent());
			}
			questsPanel.setBorder(BorderFactory.createTitledBorder("Quest Hand"));
			finishedQuestsPanel.setBorder(BorderFactory.createTitledBorder("Completed Quests"));
			JSplitPaneImproved sp = new JSplitPaneImproved(JSplitPane.VERTICAL_SPLIT,questsPanel,finishedQuestsPanel);
			sp.setDividerLocation(0.50);
			tabPane.addTab("",ImageCache.getIcon("tab/quest"),sp,"Quests");
		}
		
		add(tabPane,"Center");
	}
	private static Box getTitle(String text) {
		Box line = Box.createHorizontalBox();
		JLabel label = new JLabel(text);
		label.setFont(font);
		line.add(label);
		line.add(Box.createHorizontalGlue());
		return line;
	}
	private static Box getTextLine(String text) {
		Box line = Box.createHorizontalBox();
		JLabel label = new JLabel(text);
		label.setFont(font2);
		line.add(label);
		line.add(Box.createHorizontalGlue());
		return line;
	}
}