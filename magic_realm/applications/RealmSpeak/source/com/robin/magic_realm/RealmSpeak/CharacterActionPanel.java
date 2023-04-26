package com.robin.magic_realm.RealmSpeak;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.*;
import com.robin.magic_realm.components.attribute.PhaseManager;
import com.robin.magic_realm.components.quest.GamePhaseType;
import com.robin.magic_realm.components.quest.requirement.QuestRequirementParams;
import com.robin.magic_realm.components.swing.RealmObjectPanel;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.*;

public class CharacterActionPanel extends CharacterFramePanel {
	
	private static Hashtable<Integer, ImageIcon> dieIconHash = null;
	
	private HostPrefWrapper hostPrefs;
	
	protected JToolBar toolbar;
	protected JTable actionHistoryTable;
	
	private FlashingButton finishFlasher;
	private CharacterActionControlManager acm;
	
	public CharacterActionPanel(CharacterFrame parent) {
		super(parent);
		hostPrefs = HostPrefWrapper.findHostPrefs(getCharacter().getGameObject().getGameData());
		init();
	}
	private void init() {
		setLayout(new BorderLayout(5,5));
			toolbar = new JToolBar(getCharacter().getCharacterName()+" Actions");
			initActions();
		add(toolbar,"North");
		actionHistoryTable = new JTable(new ActionHistoryTableModel());
		for (int i=0;i<8;i++) {
			if (i!=5) {
				int n = 40; // 0,1,2
				if (i>2) n=50; // 3,4,6,7
				if (i==6) {
					n=20; // monsterdie
					if (hostPrefs.hasPref(Constants.EXP_MONSTER_DIE_PER_SET) && hostPrefs.getMultiBoardEnabled()) {
						n = n*hostPrefs.getMultiBoardCount();
					}
					if (hostPrefs.hasPref(Constants.EXP_DOUBLE_MONSTER_DIE)) {
						n=n*2;
					}
				}
				TableColumn col = actionHistoryTable.getColumnModel().getColumn(i);
				col.setMinWidth(n);
				col.setMaxWidth(n);
				col.setPreferredWidth(n);
			}
		}
		actionHistoryTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent ev) {
				if (ev.getClickCount()==2) {
					int row = actionHistoryTable.getSelectedRow();
					String val = (String)actionHistoryTable.getValueAt(row,KILLS);
					if (!"-".equals(val)) {
						showKills(row);
					}
				}
			}
		});
		add(new JScrollPane(actionHistoryTable),"Center");
	}
	protected void scrollActionTableToVisible() {
		// Ensure last row is visible
		Rectangle rect = actionHistoryTable.getCellRect(actionHistoryTable.getRowCount()-1,0,true);
		actionHistoryTable.scrollRectToVisible(rect);
		actionHistoryTable.getSelectionModel().setSelectionInterval(actionHistoryTable.getRowCount()-1,actionHistoryTable.getRowCount()-1);
	}
	public void initActions() {
		acm = new CharacterActionControlManager(getGameHandler(),getCharacter());
		acm.setCharacterActionPanel(this);
		
		refreshToolbar();
	}
	public CharacterActionControlManager getActionControlManager() {
		return acm;
	}
	public void modifyToolbarIconStyle(int iconStyle) {
		acm.setIconStyle(iconStyle);
		refreshToolbar();
	}
	public void refreshToolbar() {
		toolbar.removeAll();
		finishFlasher = new FlashingButton(acm.finishAction) {
			public void doWhenFlash() {
				SoundUtility.playClick();
			}
		};
		finishFlasher.setText("");
		finishFlasher.setToolTipText(acm.finishIcon.getText());
		toolbar.add(finishFlasher);
		JButton backButton = toolbar.add(acm.backAction);
		backButton.setToolTipText(acm.backIcon.getText());
		ComponentTools.lockComponentSize(finishFlasher,backButton.getPreferredSize());
		
		toolbar.addSeparator();
		acm.addActionButtons(toolbar);
		toolbar.revalidate();
		toolbar.repaint();
	}
	public void updatePanel() {
		actionHistoryTable.revalidate();
		actionHistoryTable.repaint();
		scrollActionTableToVisible();
		
		if (getCharacter().getNeedsActionPanelUpdate()) {
			getCharacter().setNeedsActionPanelUpdate(false);
			refreshToolbar();
		}
	}
	private void showKills(int row) {
		Collection<String> allDays = getCharacter().getGameObject().getAttributeList(getCharacter().getBlockName(),CharacterWrapper.ALL_DAYS);
		if (allDays!=null && row<allDays.size()) {
			String dayKey = (new ArrayList<String>(allDays)).get(row);
			ArrayList<GameObject> kills = getCharacter().getKills(dayKey);
			RealmObjectPanel panel = new RealmObjectPanel();
			panel.addObjects(kills);
			JOptionPane.showMessageDialog(getGameHandler().getMainFrame(),panel,getCharacter().getCharacterName()+" Kills",JOptionPane.INFORMATION_MESSAGE,null);
		}
	}
	public void updateControls(boolean recordingActions) {
		PhaseManager pm = getCharacter().getPhaseManager(true);
		getCharacter().updatePhaseManagerWithCurrentActions(pm);
		
		if (recordingActions) {
			getCharacterFrame().setCurrentPhaseManager(pm);
		}
		boolean isBirdsong = getGameHandler().getGame().isRecording();
		acm.updateControls(pm,recordingActions,isBirdsong);
		
		if (isBirdsong && getCharacter().needsQuestCheck()) { 
			QuestRequirementParams params = new QuestRequirementParams();
			params.timeOfCall = GamePhaseType.Birdsong;
			if (getCharacter().testQuestRequirements(getMainFrame(),params)) {
				getCharacterFrame().updateCharacter(); // might be recursive, but the requirements shouldn't yield rewards more than once...
				getGameHandler().getInspector().redrawMap();
			}
			getCharacter().setNeedsQuestCheck(false);
		}
		
		toolbar.repaint();
		finishFlasher.setFlashing(!acm.canStillRecord());
	}
	public static final int TURN = 0;
	public static final int MONTH = 1;
	public static final int DAY = 2;
	public static final int COLOR = 3;
	public static final int PHASES = 4;
	public static final int ACTIONS = 5;
	public static final int MONSTER_DIE = 6;
	public static final int KILLS = 7;
	private class ActionHistoryTableModel extends AbstractTableModel {
		protected String[] columnName = {
			"Turn","Mon","Day","Color","Phases","Actions"," ","Kills"
		};
		protected Class[] columnClass = {
			String.class,
			String.class,
			String.class,
			String.class,
			String.class,
			String.class,
			Icon.class,
			String.class,
		};
		public ActionHistoryTableModel() {
		}
		public int getRowCount() {
			Collection<String> allDays = getCharacter().getAllDayKeys();
			return allDays==null?0:allDays.size();
		}
		public int getColumnCount() {
			return columnName.length;
		}
		public String getColumnName(int column) {
			return columnName[column];
		}
		public Class getColumnClass(int column) {
			return columnClass[column];
		}
		public Object getValueAt(int row, int column) {
			ArrayList<String> allDays = getCharacter().getGameObject().getAttributeList(getCharacter().getBlockName(),CharacterWrapper.ALL_DAYS);
			if (allDays!=null && row<allDays.size()) {
				String dayKey = allDays.get(row);
				switch(column) {
					case TURN:
						return Integer.valueOf(row+1); // FIXME this isn't exactly right - doesn't reflect the game turn, only the turn of this character
					case MONTH:
						return Integer.valueOf(DayKey.getMonth(dayKey));
					case DAY:
						return Integer.valueOf(DayKey.getDay(dayKey));
					case COLOR:
						RealmCalendar cal = RealmCalendar.getCalendar(getGameHandler().getClient().getGameData());
						return cal.getColorMagicName(DayKey.getMonth(dayKey),DayKey.getDay(dayKey));
					case PHASES:
						String phases = getCharacter().getBasicPhases(dayKey)+"/"+getCharacter().getSunlightPhases(dayKey);
						if (hostPrefs.hasPref(Constants.OPT_WEATHER)) {
							phases = phases+"/"+getCharacter().getShelteredPhases(dayKey);
						}
						return phases;
					case ACTIONS:
						return getActionString(dayKey);
					case MONSTER_DIE:
						DieRoller roller = getCharacter().getMonsterRoll(dayKey);
						if (roller!=null) {
							if (dieIconHash==null) {
								dieIconHash = new Hashtable<>();
								for (int i=1;i<=6;i++) {
									DieRoller dr = new DieRoller(String.valueOf(i),16,4);
									dr.setAllRed();
									dieIconHash.put(Integer.valueOf(i),dr.getIcon());
								}
							}
							if (roller.getNumberOfDice()==1) {
								return dieIconHash.get(roller.getValue(0));
							}
							roller = new DieRoller(roller.getStringResult(),16,4);
							roller.setAllRed();
							return roller.getIcon();
						}
						return null;
					case KILLS:
						ArrayList<GameObject> kills = getCharacter().getKills(dayKey);
						return kills.isEmpty()?"-":String.valueOf(kills.size());
				}
			}
			return null;
		}
		public String getActionString(String dayKey) {
			boolean today = dayKey.equals(getCharacter().getCurrentDayKey());
			Collection<String> c = getCharacter().getActions(dayKey);
			if (c!=null) {
				StringBuffer sb = new StringBuffer();
				Iterator<String> vai = null;
				if (today) { // only today is drawn using html
					sb.append("<html>");
					Collection<String> v = getCharacter().getCurrentActionValids();
					if (v==null) {
						v = new ArrayList<>();
					}
					vai = v.iterator();
				}
				int zero = sb.length();
				for (String action : c) {			
					// Truncate anything starting with a tilde (~)
					int tilde = action.indexOf('~');
					if (tilde>=0) {
						action = action.substring(0,tilde);
					}
					
					if (sb.length()>zero) {
						sb.append(",");
					}
					if (today) {
						// FIXME Need to recognize the situation where the character enters
						// a move to an invalid clearing!  Low priority though.
						String test = vai.next().toString();
						boolean validAction = test.equals("T");
						if (validAction) {
							sb.append(action);
						}
						else {
							sb.append("<font color=\"#FF0000\">");
							sb.append(action);
							sb.append("</font>");
						}
					}
					else {
						sb.append(action);
					}
					
				}
				if (today) {
					sb.append("</html>");
				}
				return sb.toString();
			}
			if (dayKey.equals(getCharacter().getCurrentDayKey())) {
				return " - Awaiting Actions -";
			}
			return "No Actions Recorded";
		}
	}
}