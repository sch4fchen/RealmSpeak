package com.robin.game.GameBuilder;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import com.robin.game.objects.GameObject;
import com.robin.general.swing.ColumnSizable;

public class GameObjectTableModel extends AbstractTableModel implements ColumnSizable {

	protected String[] columnHeaders = {
		"ID",
		"Object name",
		"Held by",
		"Holds"
	};
	
	protected ArrayList<GameObject> data;

	public GameObjectTableModel(ArrayList<GameObject> data) {
		this.data = data;
	}
	public int getRowCount() {
		return data.size();
	}
	public int getColumnCount() {
		return columnHeaders.length;
	}
	public String getColumnName(int col) {
		return columnHeaders[col];
	}
	public Class getColumnClass(int col) {
		switch(col) {
			case 0:
				return Long.class;
			case 3:
				return Integer.class;
			default:
				return String.class;
		}
	}
	public Object getValueAt(int row,int col) {
		if (row<data.size()) {
			GameObject obj = data.get(row);
			switch(col) {
				case 0:
					return Long.valueOf(obj.getId());
				case 1:
					return obj.getName();
				case 2:
					GameObject hb = obj.getHeldBy();
					return hb==null?"":hb.toString();
				case 3:
					return Integer.valueOf(obj.getHoldCount());
				default:
					throw new IllegalArgumentException("Invalid column index");
			}
		}
		return null;
	}
	
	// ColumnSizable interface
	public void setTableHeaderSize(JTable table) {
		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(0).setMaxWidth(40);
		colModel.getColumn(3).setMaxWidth(40);
	}
}