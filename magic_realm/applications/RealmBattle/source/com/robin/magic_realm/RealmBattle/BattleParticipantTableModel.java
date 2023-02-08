package com.robin.magic_realm.RealmBattle;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import com.robin.general.swing.*;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class BattleParticipantTableModel extends AbstractTableModel {
	private static final ImageIcon LOCK_ICON = IconFactory.findIcon("icons/lock.gif");
	private CombatFrame parent;
	public BattleParticipantTableModel(CombatFrame parent) {
		this.parent = parent;
	}
	public int getRowCount() {
		return parent.getAllParticipantCount()+1;
	}
	public int getColumnCount() {
		return 5;
	}
	public String getColumnName(int column) {
		return " ";
	}
	public Class getColumnClass(int column) {
		switch(column){
			case 0:	return ImageIcon.class;
			case 1:	return ImageIcon.class;
			case 2:	return ImageIcon.class;
			case 3:	return String.class;
			case 4:	return ImageIcon.class;
		}
		return column==3?String.class:ImageIcon.class;
	}
	public Object getValueAt(int row, int column) {
		if (row<=parent.getAllParticipantCount()) {
			if (row==0) {
				if (column==3) {
					return "Summary";
				}
			}
			else {
				row -= 1;
				RealmComponent participant = parent.getAllParticipants().get(row);
				CombatWrapper combat = new CombatWrapper(participant.getGameObject());
				RealmComponent owner = participant.getOwner();
				switch(column) {
					case 0:
						if (participant.isCharacter() && combat.isLockNext()) {
							return LOCK_ICON;
						}
						else if (parent.getActionState()<Constants.COMBAT_RESOLVING && owner!=null && owner.equals(parent.getActiveParticipant())) {
							return ImageCache.getIcon("combat/activeArrow");
						}
						return null;
					case 1:
						if (owner==null) {
							return participant.getSmallIcon();
						}
						return participant.getOwner().getSmallIcon();
					case 2:
						return participant.getMediumIcon();
					case 3:
						if (parent.getParticipantHasHotspots(row)) {
							return parent.getActionName();
						}
						return "";
					case 4:
						return getAttackersIcon(combat);
				}
			}
		}
		return null;
	}
	private ImageIcon getAttackersIcon(CombatWrapper combat) {
		int attackerCount = combat.getAttackerCount();
		if (attackerCount==1) {
			return combat.getAttackersAsComponents().get(0).getMediumIcon();
		}
		else if (attackerCount>0 && attackerCount<=6) {
			IconGroup group = new IconGroup(IconGroup.HORIZONTAL,1,100,0);
			for (RealmComponent rc:combat.getAttackersAsComponents()) {
				group.addIcon(rc.getMediumIcon());
			}
			return group;
		}
		else if (attackerCount>6) {
			IconGroup group = new IconGroup(IconGroup.VERTICAL,1,0,CombatFrame.PARTICIPANT_ROW_HEIGHT-4);
			int rowCount = 0;
			IconGroup iconRow = null;
			for (RealmComponent rc:combat.getAttackersAsComponents()) {
				if (iconRow==null) {
					iconRow = new IconGroup(IconGroup.HORIZONTAL,1,100-4,0);
					rowCount=0;
				}
				iconRow.addIcon(rc.getMediumIcon());
				rowCount++;
				if (rowCount==6) {
					group.addIcon(iconRow);
					iconRow = null;
				}
			}
			if (iconRow!=null) {
				group.addIcon(iconRow);
			}
			return group;
		}
		return null;
	}
}