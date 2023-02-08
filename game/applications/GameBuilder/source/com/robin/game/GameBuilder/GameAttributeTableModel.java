package com.robin.game.GameBuilder;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.robin.general.util.OrderedHashtable;
import com.robin.general.util.StringUtilities;

public class GameAttributeTableModel extends AbstractTableModel {

	protected String[] columnHeaders = {
		"Key",
		"Value"
	};
	
	protected OrderedHashtable data;

	public GameAttributeTableModel(OrderedHashtable data) {
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
		return String.class;
	}
	public Object getValueAt(int row,int col) {
		if (row<data.size()) {
			switch(col) {
				case 0:
					return data.getKey(row);
				case 1:
					Object val = data.getValue(row);
					if (val instanceof ArrayList) {
						StringUtilities.collectionToString((ArrayList)val,",");
					}
					return val;
				default:
					throw new IllegalArgumentException("Invalid column index");
			}
		}
		return null;
	}
}