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
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.*;

import com.robin.game.objects.*;
import com.robin.general.swing.*;
import com.robin.magic_realm.components.quest.*;

public class QuestCounterEditor extends GenericEditor {
	
	private Quest quest;
	private QuestCounter counter;
	
	private JTextField name;
	private IntegerField startCount;
	
	public QuestCounterEditor(JFrame parent,GameData realmSpeakData,Quest quest,QuestCounter counter) {
		super(parent,realmSpeakData);
		this.quest = quest;
		this.counter = counter;
		initComponents();
		name.setText(counter.getName());
		startCount.setText(String.valueOf(counter.getStartCount()));
		setLocationRelativeTo(parent);
	}
		protected boolean isValidForm() {
		return true;
	}
	protected void save() {
		saveCounter();
	}
	private void saveCounter() {
		counter.setName(name.getText());
		counter.setStartCount(startCount.getInt());
	}
	private void initComponents() {
		setTitle("Quest Counter");
		setSize(420,140);
		setLayout(new BorderLayout());
		add(buildForm(),BorderLayout.CENTER);
		add(buildOkCancelLine(),BorderLayout.SOUTH);
		updateControls();
	}
	private void updateControls() {
		String cntName = name.getText();
		boolean conflict = false;
		for (QuestCounter cnt:quest.getCounters()) {
			if (cnt!=counter && cnt.getName().equals(cntName)) {
				conflict = true;
				break;
			}
		}
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
		form.add(Box.createVerticalGlue());
		
		line = group.createLabelLine("Initial count");
		startCount = new IntegerField(0);
		startCount.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				updateControls();
			}
		});
		ComponentTools.lockComponentSize(startCount,50,25);
		line.add(startCount);
		line.add(Box.createHorizontalStrut(10));
		line.add(Box.createHorizontalGlue());
		form.add(line);
		form.add(Box.createVerticalGlue());
		
		return form;
	}
}