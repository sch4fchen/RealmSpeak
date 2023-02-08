package com.robin.game.GameBuilder;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import com.robin.general.swing.ColumnSizable;
import com.robin.general.util.OrderedHashtable;

public class GameBlockTableModel extends AbstractTableModel implements ColumnSizable {

	protected String[] columnHeaders = {
		"BlockName",
		"Size"
	};
	
	protected OrderedHashtable data;

	public GameBlockTableModel(OrderedHashtable data) {
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
		return col==1?Integer.class:String.class;
	}
	public Object getValueAt(int row,int col) {
		if (row<data.size()) {
			switch(col) {
				case 0:
					return data.getKey(row);
				case 1:
					return Integer.valueOf(((OrderedHashtable)data.getValue(row)).size());
				default:
					throw new IllegalArgumentException("Invalid column index");
			}
		}
		return null;
	}
	
	// ColumnSizable interface
	public void setTableHeaderSize(JTable table) {
		TableColumnModel colModel = table.getColumnModel();
		colModel.getColumn(1).setMaxWidth(40);
	}
}