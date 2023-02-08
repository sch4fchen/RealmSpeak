package com.robin.magic_realm.RealmSpeak;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.general.swing.ComponentTools;
import com.robin.magic_realm.components.swing.RelationshipTable;

public class CharacterRelationshipPanel extends CharacterFramePanel {

	protected RelationshipTable relationshipTable;
	protected Hashtable<String, JCheckBox> charIdBoxHash; // id:JCheckBox hash for characters
	protected Hashtable<String, GameObject> charNameObjectHash; // name:GameObject hash for characters
	
	public CharacterRelationshipPanel(CharacterFrame parent) {
		super(parent);
		init();
	}
	private void init() {
		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1.0;
		setLayout(gridBag);
		
		relationshipTable = new RelationshipTable(getGameHandler().getRelationshipNames(),getCharacter());
		JScrollPane sp = new JScrollPane(relationshipTable);
		
		gbc.weightx = 1.0;
		gridBag.setConstraints(sp,gbc);
		add(sp);
		
		// one checkbox for every character
		charIdBoxHash = new Hashtable<>();
		charNameObjectHash = new Hashtable<>();
		GamePool pool = getGameHandler().getGamePool();
		ArrayList<GameObject> allChars = pool.find("character");
		Collections.sort(allChars,new Comparator<GameObject>() {
			public int compare(GameObject go1,GameObject go2) {
				return go1.getName().compareTo(go2.getName());
			}
		});
		
		JPanel enemyPanel = new JPanel(new GridLayout(allChars.size()+1,1));
		JLabel panelHeader = new JLabel("ENEMIES",SwingConstants.CENTER);
		panelHeader.setBackground(Color.red);
		panelHeader.setForeground(Color.white);
		panelHeader.setOpaque(true);
		enemyPanel.add(panelHeader);
		ComponentTools.lockComponentSize(enemyPanel,100,allChars.size()*18);
		for (GameObject aChar : allChars) {
			if (!aChar.equals(getCharacter().getGameObject())) { // no checkbox option for self
				JCheckBox cb = new JCheckBox(aChar.getName(),false);
				charIdBoxHash.put(aChar.getStringId(),cb);
				charNameObjectHash.put(aChar.getName(),aChar);
				cb.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						JCheckBox thisCb = (JCheckBox)ev.getSource();
						boolean enemy = thisCb.isSelected();
						GameObject theChar = charNameObjectHash.get(thisCb.getText());
						getCharacter().setEnemyCharacter(theChar,enemy);
					}
				});
				enemyPanel.add(cb);
			}
		}
		sp = new JScrollPane(enemyPanel);
		
		gbc.weightx = 0.2;
		gridBag.setConstraints(sp,gbc);
		add(sp);
	}
	public void updatePanel() {
		for (GameObject aChar : charNameObjectHash.values()) {
			JCheckBox cb = charIdBoxHash.get(aChar.getStringId());
			cb.setSelected(getCharacter().isEnemy(aChar));
		}
		relationshipTable.fireTableDataChanged();
	}
}