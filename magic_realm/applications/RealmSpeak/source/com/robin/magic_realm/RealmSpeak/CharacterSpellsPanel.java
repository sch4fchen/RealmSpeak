package com.robin.magic_realm.RealmSpeak;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.*;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.ComponentTools;
import com.robin.general.swing.MouseUtility;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.swing.RealmObjectPanel;
import com.robin.magic_realm.components.swing.SpellInfoDialog;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.SpellMasterWrapper;
import com.robin.magic_realm.components.wrapper.SpellWrapper;

public class CharacterSpellsPanel extends CharacterFramePanel {
	
	private RealmObjectPanel recordedSpellsObjectPanel;
	
	private JPanel bottom;
	private ArrayList<SpellWrapper> bewitchingSpells;
	private SpellListModel listModel;
	private JList bewitchingSpellsList;
	private SpellMasterWrapper spellMaster;
	
	public CharacterSpellsPanel(CharacterFrame parent) {
		super(parent);
		spellMaster = SpellMasterWrapper.getSpellMaster(getGameHandler().getClient().getGameData());
		init();
	}
	private void init() {
		setLayout(new BorderLayout());
		
		JLabel ins = new JLabel("Right-click spell for more info",SwingConstants.CENTER);
		ins.setOpaque(true);
		ins.setBackground(MagicRealmColor.PALEYELLOW);
		ins.setFont(new Font("Dialog",Font.BOLD,14));
		add(ins,"North");
		
		// Main spell panel
		recordedSpellsObjectPanel = new RealmObjectPanel(false,false);
		recordedSpellsObjectPanel.setSelectionMode(RealmObjectPanel.SINGLE_SELECTION);
		add(new JScrollPane(recordedSpellsObjectPanel),"Center");
		recordedSpellsObjectPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent ev) {
				if (MouseUtility.isRightOrControlClick(ev)) {
					Component c = recordedSpellsObjectPanel.getComponentAt(ev.getPoint());
					if (c!=null && c instanceof SpellCardComponent) {
						SpellCardComponent sc = (SpellCardComponent)c;
						SpellWrapper spell = new SpellWrapper(sc.getGameObject());
						SpellInfoDialog.showSpellInfo(getGameHandler().getMainFrame(),spell);
					}
				}
			}
		});
		
		// Bottom Panel
		bottom = new JPanel(new BorderLayout());
		
		// Title label
		JLabel label = new JLabel("Bewitching Spells:");
		label.setFont(new Font("Dialog",Font.BOLD,14));
		label.setOpaque(true);
		label.setBackground(MagicRealmColor.PALEYELLOW);
		bottom.add(label,"North");
		
		// Bewitching Spells Table
		listModel = new SpellListModel();
		bewitchingSpellsList = new JList(listModel);
		bewitchingSpellsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		bewitchingSpellsList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent ev) {
				if (ev.getClickCount()==2 || MouseUtility.isRightOrControlClick(ev)) {
					int index = bewitchingSpellsList.getSelectedIndex();
					if (index>=0 && index<listModel.getSize()) {
						SpellWrapper spell = bewitchingSpells.get(index);
						SpellInfoDialog.showSpellInfo(getGameHandler().getMainFrame(),spell);
					}
				}
			}
			public void mousePressed(MouseEvent ev) {
				if ( MouseUtility.isRightOrControlClick(ev)) {
					int index = bewitchingSpellsList.locationToIndex(ev.getPoint());
					if (index>=0 && index<listModel.getSize()) {
						SpellWrapper spell = bewitchingSpells.get(index);
						SpellInfoDialog.showSpellInfo(getGameHandler().getMainFrame(),spell);
					}
				}
			}
		});
		JScrollPane sp = new JScrollPane(bewitchingSpellsList);
		ComponentTools.lockComponentSize(sp,70,70);
		bottom.add(sp,"Center");
		JLabel instruction = new JLabel("Double-click bewitching spells for more info",SwingConstants.CENTER);
		instruction.setForeground(Color.red);
		bottom.add(instruction,"South");
		add(bottom,"South");
	}
	public void updatePanel() {
		recordedSpellsObjectPanel.removeAll();
		ArrayList<GameObject> spells = getCharacter().getAllSpells();
		recordedSpellsObjectPanel.addObjects(spells);
		if (!getGameHandler().getHostPrefs().hasPref(Constants.HOUSE2_NO_SPELL_LIMIT)) {
			int total = recordedSpellsObjectPanel.getComponentCount();
			for (int i=total;i<Constants.MAX_SPELL_COUNT;i++) {
				recordedSpellsObjectPanel.add(new EmptyCardComponent());
			}
		}
		
		// Add the virtual spell cards (enhanced magic) here
		recordedSpellsObjectPanel.addObjects(CharacterWrapper.getAllVirtualSpellsFor(spells));
		
		bewitchingSpells = spellMaster.getAffectingSpells(getCharacter().getGameObject());
		listModel.update();
		bottom.setVisible(!bewitchingSpells.isEmpty());
	}
	private class SpellListModel extends AbstractListModel<String> {
		public String getElementAt(int index) {
			if (index<getSize()) {
				SpellWrapper spell = bewitchingSpells.get(index);
				String caster = spell.getCaster().getGameObject().getName();
				String spellName = spell.getName();
				String duration = spell.getGameObject().getThisAttribute("duration");
				StringBuffer sb = new StringBuffer();
				sb.append(spellName);
				GameObject inc = spell.getIncantationObject();
				if (inc!=null && !inc.hasThisAttribute(RealmComponent.CHARACTER_CHIT)) { // If not a character chit, then show what it was
					String incName = spell.getIncantationObject().getName();
					sb.append(" (");
					sb.append(incName);
					sb.append(")");
				}
				sb.append(", cast by ");
				sb.append(caster);
				sb.append(" (");
				sb.append(duration);
				sb.append(" spell");
				if (spell.isInert()) {
					sb.append(" - currently INERT");
				}
				sb.append(")");
				return sb.toString();
			}
			return null;
		}
		public int getSize() {
			return bewitchingSpells==null?0:bewitchingSpells.size();
		}
		public void update() {
			fireContentsChanged(this, 0, getSize());
		}
	}
}