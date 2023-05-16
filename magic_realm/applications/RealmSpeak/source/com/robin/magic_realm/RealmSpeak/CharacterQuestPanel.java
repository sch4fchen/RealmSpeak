package com.robin.magic_realm.RealmSpeak;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.*;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.quest.requirement.QuestRequirementParams;
import com.robin.magic_realm.components.swing.*;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class CharacterQuestPanel extends CharacterFramePanel {

	private JButton activateQuestButton;
	private JButton discardQuestButton;
	private JButton drawQuestsButton;
	private JButton viewAllPlayCardsButton;
	
	private ArrayList<Quest> characterQuests;
	
	/// New design
	private QuestView questView;
	private RealmObjectPanel questHandPanel;
	private RealmObjectPanel completedQuestsPanel;
	
	ActionListener activateQuestListener = new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Quest quest = (Quest)ev.getSource();
				doActivateQuest(quest);
				FrameManager.getFrameManager().disposeFrame(FrameManager.DEFAULT_FRAME_KEY);
			}
		};

	protected CharacterQuestPanel(CharacterFrame parent) {
		super(parent);
		initComponents();
	}
	
	private void initComponents() {
		setLayout(new BorderLayout(10, 10));
		JLabel ins = new JLabel("Right-click quest for more info",SwingConstants.CENTER);
		ins.setOpaque(true);
		ins.setBackground(MagicRealmColor.PALEYELLOW);
		ins.setFont(new Font("Dialog",Font.BOLD,14));
		add(ins,"North");
		if (getHostPrefs().isUsingQuestCards() || getHostPrefs().isUsingGuildQuests()) {
			add(createQuestCardPanel());
		}
		else {
			add(createQuestViewPanel());
		}
		updateControls();
	}
	private JPanel createQuestViewPanel() {
		questView = new QuestView();
		return questView;
	}
	private void showQuestInfo(Component c) {
		if (c==null || !(c instanceof QuestCardComponent)) return;
		
		QuestCardComponent qc = (QuestCardComponent)c;
		Quest quest = new Quest(qc.getGameObject());
		QuestState state = quest.getState();
		
		QuestView view = new QuestView(state==QuestState.Assigned && (!quest.isAllPlay() || quest.isActivateable())?activateQuestListener:null);
		view.updatePanel(quest,getCharacter());
		ComponentTools.lockComponentSize(view,640,480);
		FrameManager.showDefaultManagedFrame(getMainFrame(), view, qc.getGameObject().getName(), qc.getFaceUpIcon(), true, ImageCache.getIcon("badges/lore"));
	}
	private JPanel createQuestCardPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		questHandPanel = new RealmObjectPanel(true,false);
		questHandPanel.setBorder(BorderFactory.createTitledBorder("Quest Hand"));
		questHandPanel.setSelectionMode(RealmObjectPanel.SINGLE_SELECTION);
		questHandPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent ev) {
				if (MouseUtility.isRightOrControlClick(ev)) {
					showQuestInfo(questHandPanel.getComponentAt(ev.getPoint()));
				}
			}
		});
		questHandPanel.addSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				updateControls();
			}
		});
		
		completedQuestsPanel = new RealmObjectPanel(true,false);
		completedQuestsPanel.setBorder(BorderFactory.createTitledBorder("Completed Quests"));
		completedQuestsPanel.setSelectionMode(RealmObjectPanel.SINGLE_SELECTION);
		completedQuestsPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent ev) {
				if (MouseUtility.isRightOrControlClick(ev)) {
					showQuestInfo(completedQuestsPanel.getComponentAt(ev.getPoint()));
				}
			}
		});
		completedQuestsPanel.addSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				updateControls();
			}
		});
		
		JSplitPaneImproved splitPane = new JSplitPaneImproved(JSplitPane.VERTICAL_SPLIT,new JScrollPane(questHandPanel),new JScrollPane(completedQuestsPanel));
		splitPane.setDividerLocation(0.5);
		
		panel.add(splitPane,BorderLayout.CENTER);
		
		Box controls = Box.createHorizontalBox();
		controls.add(Box.createHorizontalStrut(10));
		activateQuestButton = new JButton("Activate");
		activateQuestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				Quest selQuest = getSelectedQuest();
				if (selQuest != null) {
					doActivateQuest(selQuest);
				}
			}
		});
		controls.add(activateQuestButton);
		controls.add(Box.createHorizontalStrut(10));
		discardQuestButton = new JButton("Discard");
		discardQuestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				doDiscardQuestCards();
			}
		});
		controls.add(discardQuestButton);
		controls.add(Box.createHorizontalStrut(10));

		drawQuestsButton = new JButton("Draw");
		drawQuestsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				doDrawQuests();
			}
		});
		controls.add(drawQuestsButton);

		if (getHostPrefs().isUsingQuestCards()) {
			viewAllPlayCardsButton = new JButton("All-Play Cards");
			viewAllPlayCardsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {	
					boolean added = false;
					RealmObjectPanel panel = new RealmObjectPanel();
					for(Quest quest:characterQuests) {
						if (quest.isAllPlay() && !quest.getState().isFinished()) {
							panel.addObject(quest.getGameObject());
							added = true;
						}
					}
					panel.addMouseListener(new MouseAdapter() {
						public void mousePressed(MouseEvent ev) {
							RealmObjectPanel panel = (RealmObjectPanel)ev.getSource();
							showQuestInfo(panel.getComponentAt(ev.getPoint()));
						}
					});
	
					ComponentTools.lockComponentSize(panel,640,480);
					
					if (added) {
						FrameManager.showDefaultManagedFrame(getMainFrame(), new JScrollPane(panel), "Available All-Play Cards", ImageCache.getIcon("quests/token"), true, ImageCache.getIcon("badges/lore"));
					}
					else {
						JOptionPane.showMessageDialog(getMainFrame(), "No All-Play cards left.", "None Left", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			});
			controls.add(Box.createHorizontalGlue());
			controls.add(viewAllPlayCardsButton);
		}
		
		panel.add(controls,BorderLayout.SOUTH);
		
		return panel;
	}
	
	private void doActivateQuest(Quest quest) {
		String dayKey = getCharacter().getCurrentDayKey();
		RealmLogging.logMessage(getCharacter().getName(),"Activated Quest Card: "+quest.getName());
		quest.setState(QuestState.Active, getCharacter().getCurrentDayKey(), getCharacter());
		QuestRequirementParams questRequirementParams = new QuestRequirementParams();
		questRequirementParams.timeOfCall = getGameHandler().getGame().getGamePhase();
		if (quest.testRequirements(getMainFrame(), getCharacter(), questRequirementParams)) {
			getCharacter().testQuestRequirements(getMainFrame()); // Make sure that all quests get updated (auto-journal)
			getCharacterFrame().updateCharacter();
			getGameHandler().getInspector().redrawMap();
		}
		if (quest.getState()!=QuestState.Assigned && quest.isAllPlay()) {
			quest.revertAllPlay(dayKey,getCharacter());
			quest.clearAllPlay();
			getCharacterFrame().updateCharacter();
		}
		questHandPanel.repaint();
	}

	private void doDiscardQuestCards() {
		RealmObjectChooser chooser = new RealmObjectChooser("Discard Which Cards?", getGame().getGameData(), false);
		ArrayList<GameObject> list = new ArrayList<>();
		for (Quest quest : getCharacter().getAllQuests()) {
			if (quest.getState() == QuestState.Assigned && !quest.isAllPlay()) {
				list.add(quest.getGameObject());
			}
		}
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(getMainFrame(), "There are no quests to discard.", "Discard", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		chooser.addObjectsToChoose(list);
		chooser.setVisible(true);
		if (chooser.pressedOkay()) {
			questHandPanel.clearSelected();
			ArrayList<GameObject> chosen = chooser.getChosenObjects();
			if (chosen.isEmpty()) {
				JOptionPane.showMessageDialog(getMainFrame(), "No quests were chosen for discard.", "Discard", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			QuestDeck deck = QuestDeck.findDeck(getGame().getGameData());
			for (GameObject go : chosen) {
				Quest quest = new Quest(go);
				getCharacter().removeQuest(quest);
				deck.discardCard(quest);
				if (getHostPrefs().hasPref(Constants.HOUSE3_SHOW_DISCARED_QUEST)) {
					RealmLogging.logMessage(getCharacter().getName(),"Discarded Quest Card: "+quest.getName());
				}
				else {
					RealmLogging.logMessage(getCharacter().getName(),"Discarded a quest card.");
				}
			}
			getCharacter().setDiscardedQuests(true);
			updateControls();
		}
		updatePanel();
		getGameHandler().submitChanges();
	}

	private void doDrawQuests() {
		QuestDeck deck = QuestDeck.findDeck(getGame().getGameData());
		int cardsDrawn = deck.drawCards(getMainFrame(),getCharacter());
		StringBuilder sb = new StringBuilder();
		sb.append("Drew ");
		sb.append(cardsDrawn);
		sb.append(" new quest card");
		sb.append(cardsDrawn==1?"":"s");
		sb.append(".");
		getCharacter().setDiscardedQuests(true); // to make sure character doesn't discard after drawing cards!
		RealmLogging.logMessage(getCharacter().getName(),sb.toString());
		updatePanel();
		updateControls();
		getGameHandler().getInspector().redrawMap();
		getGameHandler().submitChanges();
	}

	private Quest getSelectedQuest() {
		RealmComponent rc = questHandPanel.getSelectedComponent();
		if (rc!=null && rc instanceof QuestCardComponent) {
			return new Quest(rc.getGameObject());
		}
		return null;
	}

	private void updateControls() {
		HostPrefWrapper hostPrefs = getHostPrefs();
		if (hostPrefs.isUsingQuestCards() || hostPrefs.isUsingGuildQuests()) {
			Quest selQuest = getSelectedQuest();
			boolean gameStarted = getGame().getGameStarted();
			activateQuestButton.setEnabled(gameStarted && selQuest != null && selQuest.getState() == QuestState.Assigned && !selQuest.isAllPlay());

			boolean canDiscardQuests = !getCharacter().alreadyDiscardedQuests() && gameStarted;
			boolean characterIsAtLocation = getCharacter().getCurrentLocation() != null;
			boolean characterIsAtDwelling = characterIsAtLocation && getCharacter().getCurrentLocation().isAtDwelling(true);
			boolean characterIsAtGuild = characterIsAtLocation && getCharacter().getCurrentLocation().isAtGuild();
			boolean isBirdsong = getGameHandler().getGame().isRecording();
			discardQuestButton.setEnabled(canDiscardQuests && isBirdsong && selQuest!=null && selQuest.getState() == QuestState.Assigned &&
					((hostPrefs.isUsingQuestCards() && characterIsAtDwelling && !selQuest.isAllPlay()) || (hostPrefs.isUsingGuildQuests() && characterIsAtGuild)));

			boolean hasAvailableSlots = (getCharacter().getQuestSlotCount(hostPrefs) - getCharacter().getUnfinishedNotAllPlayQuestCount()) > 0;
			drawQuestsButton.setEnabled(isBirdsong && hasAvailableSlots && getCharacter().isCharacter() &&
					((hostPrefs.isUsingQuestCards() && characterIsAtDwelling) || (hostPrefs.isUsingGuildQuests() && characterIsAtGuild)));
		}
	}

	public void updatePanel() {
		characterQuests = getCharacter().getAllQuests();
		HostPrefWrapper hostPrefs = getHostPrefs();
		if (hostPrefs.isUsingQuestCards() || hostPrefs.isUsingGuildQuests()) {
			int slots = getCharacter().getQuestSlotCount(hostPrefs);
			questHandPanel.removeAll();
			completedQuestsPanel.removeAll();
			for(Quest quest:characterQuests) {
				if (quest.getState().isFinished()) {
					if  (quest.getInt(QuestConstants.VP_REWARD)>0 || hostPrefs.isUsingGuildQuests()) {
						completedQuestsPanel.addObject(quest.getGameObject());
					}
				}
				else if (!quest.isAllPlay() || hostPrefs.isUsingGuildQuests()) {
					questHandPanel.addObject(quest.getGameObject());
					slots--;
				}
			}
			for(int i=0;i<slots;i++) {
				questHandPanel.add(new EmptyCardComponent());
			}
		}
		else { //BoQ
			if (getCharacter().getAllNonEventQuests().size() == 1) {
				questView.updatePanel(getCharacter().getAllNonEventQuests().get(0),getCharacter());
			}
		}
	}

	public static void main(String[] args) {
		JPanel panel = new JPanel(new BorderLayout(10, 10));

		JScrollPane sp = new JScrollPane(new JTable());
		sp.setPreferredSize(new Dimension(80, 500));
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(sp, BorderLayout.WEST);

		JPanel green = new JPanel();
		green.setBackground(Color.green);
		panel.add(green, BorderLayout.CENTER);

		JOptionPane.showMessageDialog(new JFrame(), panel);
		System.exit(0);
	}
}