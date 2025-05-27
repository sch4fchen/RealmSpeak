package com.robin.magic_realm.components.swing;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import com.robin.magic_realm.components.attribute.RelationshipType;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class RelationshipTable extends JTable {
	private RelationshipTableModel tableModel;
	public RelationshipTable(ArrayList<String[]> relationshipNames,CharacterWrapper character) {
		tableModel = new RelationshipTableModel(character,relationshipNames);
		setModel(tableModel);
		setDefaultRenderer(String.class,new RelationshipNameRenderer());
		setDefaultRenderer(Color.class,new RelationshipRenderer());
		getTableHeader().setResizingAllowed(false);
	}
	public void fireTableDataChanged() {
		tableModel.fireTableDataChanged();
	}
	private static class RelationshipTableModel extends AbstractTableModel {
		private final String[] header = {" ","Enemy","Unfriendly","Neutral","Friendly","Ally"};
		private CharacterWrapper character;
		private ArrayList<String[]> list;
		public RelationshipTableModel(CharacterWrapper character,ArrayList<String[]> list) {
			this.character = character;
			this.list = list;
		}
		public int getRowCount() {
			return list.size();
		}
		public int getColumnCount() {
			return header.length;
		}
		public String getColumnName(int column) {
			return header[column];
		}
		public Class getColumnClass(int column) {
			return column==0?String.class:Color.class;
		}
		public Object getValueAt(int row, int column) {
			if (row<list.size()) {
				String[] ret = list.get(row);
				String relBlock = ret[0];
				String fullName = ret[1];
				String groupName = fullName.substring(1); // First letter is either N or V
				
				if (relBlock.length()>Constants.GAME_RELATIONSHIP.length()) {
					fullName = fullName+" "+relBlock.substring(relBlock.length()-1);
				}
				
				if (!character.isCharacter()) {
					character = character.getHiringCharacter();
				}
				
				boolean rovingNative = ret.length>=3&&ret[2]!=null&&ret[2].matches(Constants.ROVING_NATIVE)?true:false;
				int rel = character.getRelationship(relBlock,groupName,rovingNative);
				
				Color lightColor = Color.white;
				
				switch(column) {
					case 0:
						return fullName; // need full name for renderer to work correctly
					case 1:
						return rel<=RelationshipType.ENEMY?Color.red:lightColor;
					case 2:
						return rel==RelationshipType.UNFRIENDLY?Color.orange:lightColor;
					case 3:
						return rel==RelationshipType.NEUTRAL?Color.yellow:lightColor;
					case 4:
						return rel==RelationshipType.FRIENDLY?Color.cyan:lightColor;
					case 5:
						return rel>=RelationshipType.ALLY?Color.green:lightColor;
				}
			}
			return null;
		}
	}
	private static class RelationshipRenderer extends DefaultTableCellRenderer {
		public RelationshipRenderer() {
			setOpaque(true);
		}
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasFocus, int row, int column) {
			setBackground((Color)value);
			setText("");
			return this;
		}
	}
	private static class RelationshipNameRenderer extends DefaultTableCellRenderer {
		private final Font NORMAL_FONT = new Font("Dialog",Font.PLAIN,12);
		private final Font ITALIC_FONT = new Font("Dialog",Font.ITALIC,12);
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasFocus, int row, int column) {
			String name = ((String)value).substring(1);
			String type = ((String)value).substring(0,1);
			setText(name);
			setFont("N".equals(type)?NORMAL_FONT:ITALIC_FONT);
			return this;
		}
	}
}