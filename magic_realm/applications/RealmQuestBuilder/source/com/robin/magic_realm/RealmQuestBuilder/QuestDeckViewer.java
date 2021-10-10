/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
package com.robin.magic_realm.RealmQuestBuilder;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import com.robin.general.swing.*;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.utility.Constants;

public class QuestDeckViewer extends AggressiveDialog {	
	JTable table;
	private Constants.QuestDeckMode mode;
	private ArrayList<Quest> quests;
	private int totalVPs=0;
	private int deckCards=0;
	private Quest selectedQuest;
	public QuestDeckViewer(JFrame frame, ArrayList<Quest> input, Constants.QuestDeckMode mode) {
		super(frame, "Quest Deck", true);
		this.quests = input;
		this.mode = mode;
		setLayout(new BorderLayout());
		setSize(800,600);
		
		int allPlayCount = 0;
		int eventCount = 0;
		totalVPs=0;
		deckCards=0;
		switch (mode) {
			case QtR:
			case GQ:
				for(Quest quest:quests) {
					int count = quest.getInt(QuestConstants.CARD_COUNT);
					if (quest.isAllPlay()) {
						allPlayCount += count;
					}
					else {
						deckCards += count;
					}
					totalVPs += (quest.getInt(QuestConstants.VP_REWARD)*count);
				}
				break;
			case BoQ:
				for(Quest quest:quests) {
					if (quest.isEvent()) {
						eventCount ++;
					}
					else {
						deckCards ++;
					}
				}
				break;
		}

		table = new JTable(new DeckTableModel());
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent ev) {
				if (ev.getClickCount()!=2) return;
				int row = table.getSelectedRow();
				int lastColumn = table.getColumnCount()-1;
				String filePath= (String) table.getValueAt(row, lastColumn);
				
				for (Quest quest : quests) {
					if (quest.filepath == filePath) {
						selectedQuest = quest;
						break;
					}
				}
				
				setVisible(false);
				dispose();
			}
		});
		TableSorter.makeSortable(table);
		ComponentTools.lockColumnWidth(table,0,40);
		ComponentTools.lockColumnWidth(table,1,40);
		ComponentTools.lockColumnWidth(table,2,50);
		ComponentTools.lockColumnWidth(table,3,40);
		ComponentTools.lockColumnWidth(table,5,200);
		if (mode == Constants.QuestDeckMode.QtR) {
			ComponentTools.lockColumnWidth(table,6,50);
			ComponentTools.lockColumnWidth(table,7,40);
			ComponentTools.lockColumnWidth(table,8,60);
		}
		add(new JScrollPane(table),BorderLayout.CENTER);
		
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		box.add(new JLabel("Deck Cards: "+deckCards));
		box.add(Box.createHorizontalGlue());
		switch (mode) {
			case QtR:
				box.add(new JLabel("All Play Cards: "+allPlayCount));
				box.add(Box.createHorizontalGlue());
				box.add(new JLabel("Total VPs: "+totalVPs));
				box.add(Box.createHorizontalGlue());
				break;
			case BoQ:
				box.add(new JLabel("Events: "+eventCount));
				box.add(Box.createHorizontalGlue());
				break;
			case GQ:
				break;
		}
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				setVisible(false);
				dispose();
			}
		});
		box.add(close);
		add(box,BorderLayout.SOUTH);
		getRootPane().setDefaultButton(close);
	}
	public Quest getSelectedQuest() {
		return selectedQuest;
	}
	private static String[] HEADER_QtR = {
		"TEST",
		"BAD",
		"ALL",
		"ACT",
		"Name",
		"Minor Characters",
		"Count",
		"VPs",
		"% Draw",
		"FilePath"
	};
	private static String[] HEADER_BoQ = {
			"TEST",
			"BAD",
			"EVENT",
			"ACT",
			"Name",
			"Minor Characters",
			"FilePath"
		};
	private static Class[] CLASS = {
		ImageIcon.class,
		ImageIcon.class,
		ImageIcon.class,
		ImageIcon.class,
		String.class,
		String.class,
		Integer.class,
		Integer.class,
		Integer.class,
		String.class
	};
	static ImageIcon test = IconFactory.findIcon("icons/search.gif");
	static ImageIcon cross = IconFactory.findIcon("icons/cross.gif");
	static ImageIcon check = IconFactory.findIcon("icons/check.gif");
	static ImageIcon plus = IconFactory.findIcon("icons/plus.gif");
	private class DeckTableModel extends AbstractTableModel {
		public DeckTableModel() {
		}

		public int getColumnCount() {
			switch (mode) {
				default:
				case QtR:
					return HEADER_QtR.length;
				case GQ:
				case BoQ:
					return HEADER_BoQ.length;
			}
		}
		
		public Class getColumnClass(int col) {
			return CLASS[col];
		}
		
		public String getColumnName(int col) {
			switch (mode) {
				default:
				case QtR:
					return HEADER_QtR[col];
				case GQ:
				case BoQ:
					return HEADER_BoQ[col];
				}
		}

		public int getRowCount() {
			return quests.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex<getRowCount()) {
				Quest quest = quests.get(rowIndex);
				switch(columnIndex) {
					case 0:			return quest.isTesting()?test:null;
					case 1:			return quest.isBroken()?cross:null;
					case 2:			
						switch (mode) {
							case QtR:
								return quest.isAllPlay()?check:null;
							case GQ:
							case BoQ:
								return quest.isEvent()?check:null;
						}
					case 3:			return quest.isActivateable()?plus:null;
					case 4:			return quest.getName();
					case 5:			return getMinorCharacters(quest);
					case 6:			return quest.getInt(QuestConstants.CARD_COUNT);
					case 7:			return quest.getInt(QuestConstants.VP_REWARD);
					case 8:			return (int)Math.round(quest.getInt(QuestConstants.CARD_COUNT)*100.0/deckCards);
					case 9:			return quest.filepath;
				}
			}
			return null;
		}
		private String getMinorCharacters(Quest quest) {
			StringBuilder sb = new StringBuilder();
			for(QuestMinorCharacter mc:quest.getMinorCharacters()) {
				if (sb.length()>0) sb.append(',');
				sb.append(mc.getName());
			}
			return sb.toString();
		}
	}
}