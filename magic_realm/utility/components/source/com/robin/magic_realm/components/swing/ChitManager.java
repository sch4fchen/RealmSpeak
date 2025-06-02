package com.robin.magic_realm.components.swing;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import com.robin.general.swing.AggressiveDialog;
import com.robin.general.util.StringBufferedList;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.ChitComponent;
import com.robin.magic_realm.components.StateChitComponent;
import com.robin.magic_realm.components.utility.RealmLogging;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public abstract class ChitManager extends AggressiveDialog {
	
	private static final Font LABEL_FONT = new Font("Dialog",Font.BOLD,16);
	
	protected CharacterWrapper character;
	
	protected JPanel masterPanel;
	
	protected ChitBinPanel activeChits;
	protected ChitBinPanel fatiguedChits;
	protected ChitBinPanel woundedChits;
	
	protected JPanel southDisplay;
	private JLabel statusLabel;
	
	protected int currentCount;
	protected int initialCount;
	
	private JButton resetButton;
	private JButton okayButton;
	private JButton cancelButton;
	
	private boolean finished = false;
	private boolean chitsMoved;
	private boolean countTooLarge = false;
	
	private ArrayList<ChitComponent> modifiedChits;
	
	protected abstract int totalPossibleCount();
	
	protected boolean includeAlertedChits() {
		return true;
	}
	
	protected abstract boolean canClickActive(CharacterActionChitComponent clickedChit);
	protected abstract boolean canClickFatigue(CharacterActionChitComponent clickedChit);
	protected abstract boolean canClickWound(CharacterActionChitComponent clickedChit);
	
	protected abstract void activeClick(CharacterActionChitComponent clickedChit);
	protected abstract void fatigueClick(CharacterActionChitComponent clickedChit);
	protected abstract void woundClick(CharacterActionChitComponent clickedChit);
	
	protected abstract void updateStatusLabel(JLabel label);
	
	protected abstract String getActionName();
	
	public ChitManager(JFrame parent,String title,boolean modal,CharacterWrapper character,int count) {
		this(parent,title,modal,character,count,false);
	}
	public ChitManager(JFrame parent,String title,boolean modal,CharacterWrapper character,int count,boolean includeCancelButton) {
		super(parent,title,modal);
		this.character = character;
		initialCount = count;
		setSize(670,500);
		setLocationRelativeTo(parent);
		initComponents(includeCancelButton);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		modifiedChits = new ArrayList<ChitComponent>();
	}
	protected void initialize() {
		resetChits();
		int totalPossible = totalPossibleCount();
		if (initialCount>totalPossible) {
			initialCount = totalPossible;
			currentCount = initialCount;
			countTooLarge = true;
		}
		if (initialCount==0) {
			resetButton.setVisible(false); // makes no sense to show it if there is nothing to do!
		}
		updateStatusLabel(statusLabel);
		updateControls();
	}
	/**
	 * Override this method if you want to validate the okay button.  The default implementation
	 * simply returns true.
	 */
	protected boolean validateOkayButton() {
		return true;
	}
	protected boolean canClickNonActionChits() {
		return false;
	}
	protected void finishedChitUpdate(ArrayList<ChitComponent> chits) {
		StringBufferedList active = new StringBufferedList();
		StringBufferedList fatigued = new StringBufferedList();
		StringBufferedList wounded = new StringBufferedList();
		for (ChitComponent chit:modifiedChits) {
			CharacterActionChitComponent actionChit = (CharacterActionChitComponent)chit;
			if (actionChit.isActive()) active.append(actionChit.getShortName());
			if (actionChit.isFatigued()) fatigued.append(actionChit.getShortName());
			if (actionChit.isWounded()) wounded.append(actionChit.getShortName());
		}
		if (active.size()>0) {
			RealmLogging.logMessage(character.getGameObject().getName(),
					"After "+getActionName()+", these chits were made active: "+active.toString());
		}
		if (fatigued.size()>0) {
			RealmLogging.logMessage(character.getGameObject().getName(),
					"After "+getActionName()+", these chits were moved to fatigue: "+fatigued.toString());
		}
		if (wounded.size()>0) {
			RealmLogging.logMessage(character.getGameObject().getName(),
					"After "+getActionName()+", these chits were wounded: "+wounded.toString());
		}
	}

	/**
	 * Override this method if you want to allow clicking on non-action chits
	 */
	protected boolean canClickNonActionChit(ChitComponent chit) {
		return false;
	}
	public boolean isCountTooLarge() {
		return countTooLarge;
	}
	private void initComponents(boolean includeCancelButton) {
		JLabel label;
		MouseAdapter mouse = new MouseAdapter() {
			public void mousePressed(MouseEvent ev) {
				ChitBinPanel panel = (ChitBinPanel)ev.getSource();
				panel.handleClick(ev.getPoint());
				updateControls();
				repaint();
			}
		};
		getContentPane().setLayout(new BorderLayout());
		masterPanel = new JPanel(new GridLayout(1,3));
			JPanel activePanel = new JPanel(new BorderLayout());
				activeChits = new ChitBinPanel(new ChitBinLayout(new ArrayList<StateChitComponent>(character.getCompleteChitList()))) {
					public boolean canClickChit(ChitComponent chit) {
						if (chit.isActionChit()) {
							return canClickActive((CharacterActionChitComponent)chit);
						}
						return canClickNonActionChits();
					}
					public void handleClick(Point p) {
						ChitComponent clickedChit = getClickedChit(p);
						if (clickedChit!=null && clickedChit.isActionChit()) {
							if (canClickChit(clickedChit)) {
								activeClick((CharacterActionChitComponent)clickedChit);
								if (!modifiedChits.contains(clickedChit)) modifiedChits.add(clickedChit);
								updateStatusLabel(statusLabel);
							}
						}
					}
				};
				activeChits.addMouseListener(mouse);
				label = new JLabel("Active",SwingConstants.CENTER);
				label.setFont(LABEL_FONT);
			activePanel.add(label,"North");
			activePanel.add(activeChits,"Center");
		masterPanel.add(activePanel);
			JPanel fatiguedPanel = new JPanel(new BorderLayout());
				fatiguedChits = new ChitBinPanel(new ChitBinLayout(new ArrayList<StateChitComponent>(character.getCompleteChitList()))) {
					public boolean canClickChit(ChitComponent chit) {
						if (chit.isActionChit()) {
							return canClickFatigue((CharacterActionChitComponent)chit);
						}
						return canClickNonActionChits();
					}
					public void handleClick(Point p) {
						ChitComponent clickedChit = getClickedChit(p);
						if (clickedChit!=null && clickedChit.isActionChit()) {
							if (canClickChit(clickedChit)) {
								fatigueClick((CharacterActionChitComponent)clickedChit);
								if (!modifiedChits.contains(clickedChit)) modifiedChits.add(clickedChit);
								updateStatusLabel(statusLabel);
							}
						}
					}
				};
				fatiguedChits.addMouseListener(mouse);
				label = new JLabel("Fatigued",SwingConstants.CENTER);
				label.setFont(LABEL_FONT);
			fatiguedPanel.add(label,"North");
			fatiguedPanel.add(fatiguedChits,"Center");
		masterPanel.add(fatiguedPanel);
			JPanel woundedPanel = new JPanel(new BorderLayout());
				woundedChits = new ChitBinPanel(new ChitBinLayout(new ArrayList<StateChitComponent>(character.getCompleteChitList()))) {
					public boolean canClickChit(ChitComponent chit) {
						if (chit.isActionChit()) {
							return canClickWound((CharacterActionChitComponent)chit);
						}
						return canClickNonActionChits();
					}
					public void handleClick(Point p) {
						ChitComponent clickedChit = getClickedChit(p);
						if (clickedChit!=null && clickedChit.isActionChit()) {
							if (canClickChit(clickedChit)) {
								woundClick((CharacterActionChitComponent)clickedChit);
								if (!modifiedChits.contains(clickedChit)) modifiedChits.add(clickedChit);
								updateStatusLabel(statusLabel);
							}
						}
					}
				};
				woundedChits.addMouseListener(mouse);
				label = new JLabel("Wounded",SwingConstants.CENTER);
				label.setFont(LABEL_FONT);
			woundedPanel.add(label,"North");
			woundedPanel.add(woundedChits,"Center");
		masterPanel.add(woundedPanel);
		getContentPane().add(masterPanel,"Center");
			southDisplay = new JPanel(new BorderLayout());
				resetButton = new JButton("Reset");
				resetButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						resetChits();
						updateStatusLabel(statusLabel);
						updateControls();
					}
				});
			southDisplay.add(resetButton,"West");
				statusLabel = new JLabel("",SwingConstants.CENTER);
				statusLabel.setFont(LABEL_FONT);
			southDisplay.add(statusLabel,"Center");
				Box controls = Box.createHorizontalBox();
				if (includeCancelButton) {
						cancelButton = new JButton("Cancel");
						cancelButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ev) {
								// Exit without finishing
								finished = false;
								setVisible(false);
								dispose();
							}
						});
					controls.add(cancelButton);
					controls.add(Box.createHorizontalStrut(20));
				}
					okayButton = new JButton("Done");
					okayButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent ev) {
							if (validateOkayButton()) {
								// Apply changes here...
								activeChits.makeAllChitsActive();
								fatiguedChits.makeAllChitsFatigued();
								woundedChits.makeAllChitsWounded();
								finishedChitUpdate(modifiedChits);
								
								// Exit
								finished = true;
								setVisible(false);
								dispose();
							}
						}
					});
				controls.add(okayButton);
			southDisplay.add(controls,"East");
		getContentPane().add(southDisplay,"South");
	}
	protected void resetChits() {
		currentCount = initialCount;
		activeChits.reset();
		fatiguedChits.reset();
		woundedChits.reset();
		ArrayList<StateChitComponent> list = new ArrayList<>(character.getCompleteChitList());
		for (int i=0;i<list.size();i++) {
			StateChitComponent chit = list.get(i);
			if (chit.isActionChit()) {
				CharacterActionChitComponent aChit = (CharacterActionChitComponent)chit;
				if (aChit.isActive() || aChit.isColor()) {
					activeChits.addChit(aChit,i);
				}
				else if (aChit.isFatigued()) {
					fatiguedChits.addChit(aChit,i);
				}
				else if (aChit.isWounded()) {
					woundedChits.addChit(aChit,i);
				}
				else if (aChit.isAlerted() && includeAlertedChits()) {
					activeChits.addChit(aChit,i);
				}
			}
			else {
				activeChits.addChit(chit,i);
			}
		}
		chitsMoved = false;
		modifiedChits.clear();
	}
	protected void moveChit(CharacterActionChitComponent aChit,ChitBinPanel from,ChitBinPanel to) {
		int n = from.getPosition(aChit);
		from.removeChitAt(n);
		to.addChit(aChit,n);
		chitsMoved = true;
	}
	public boolean isFinished() {
		return finished;
	}
	protected abstract boolean canPressOkay();
	private void updateControls() {
		resetButton.setEnabled(chitsMoved);
		okayButton.setEnabled(canPressOkay());
	}
	/**
	 * @return		True if no chits are active or fatigued
	 */
	public boolean isDead() {
		return activeChits.getAllChits().size()-activeChits.getColorChits().size()==0 && fatiguedChits.getAllChits().size()==0;
	}
	public JPanel getMasterPanel() {
		return masterPanel;
	}
}