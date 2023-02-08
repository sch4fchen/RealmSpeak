package com.robin.magic_realm.RealmQuestBuilder;

import java.util.*;

import javax.swing.table.DefaultTableModel;

import com.robin.game.objects.GameData;
import com.robin.general.swing.GameOption;
import com.robin.general.swing.GameOptionPane;
import com.robin.magic_realm.components.quest.Quest;
import com.robin.magic_realm.components.swing.HostGameSetupDialog;

public class RuleLimitationTableModel extends DefaultTableModel {
	private static String[] RuleLimitationHeader = { "On", "Off", "Rule Name" };
	private static Class[] RuleLimitationClass = { Boolean.class, Boolean.class, String.class };

	ArrayList<String> keys;
	Hashtable<String, String> descriptions;

	Quest quest;

	public RuleLimitationTableModel(Quest quest, GameData realmSpeakData) {
		this.quest = quest;

		keys = new ArrayList<>();
		descriptions = new Hashtable<>();

		HostGameSetupDialog dialog = new HostGameSetupDialog(null, null, realmSpeakData);
		GameOptionPane gop = dialog.getGameOptionPane();
		for (String key : gop.getGameOptionKeys()) {
			keys.add(key);
		}
		for (String key : keys) {
			GameOption go = gop.getGameOption(key);
			if (go != null) {
				descriptions.put(key, go.getDescription());
			}
		}
	}

	public void setQuest(Quest quest) {
		this.quest = quest;
		fireTableDataChanged();
	}

	public int getColumnCount() {
		return RuleLimitationHeader.length;
	}

	public String getColumnName(int index) {
		return RuleLimitationHeader[index];
	}

	public Class getColumnClass(int index) {
		return RuleLimitationClass[index];
	}

	public int getRowCount() {
		return keys == null ? 0 : keys.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < getRowCount()) {
			String key = keys.get(rowIndex);
			switch (columnIndex) {
				// case 0:
				// return loc.getName();
				// case 1:
				// return loc.getLocationType().toString();
				case 2:
					return descriptions.get(key);
			}
		}
		return null;
	}

	public boolean isCellEditable(int row, int col) {
		return Boolean.class.equals(getColumnClass(col));
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}
}