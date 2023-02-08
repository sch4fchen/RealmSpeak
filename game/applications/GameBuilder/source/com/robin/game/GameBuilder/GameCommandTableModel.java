package com.robin.game.GameBuilder;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import com.robin.general.swing.*;
import com.robin.game.objects.*;

public class GameCommandTableModel extends AbstractTableModel implements ColumnSizable {

	protected String[] columnHeaders = {
		"Order",
		"Command"
	};
	
	protected ArrayList data;

	public GameCommandTableModel(ArrayList data) {
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
		if (col==0) {
			return Integer.class;
		}
		return String.class;
	}
	public Object getValueAt(int row,int col) {
		if (row<data.size()) {
			GameCommand command = (GameCommand)data.get(row);
			switch(col) {
				case 0:
					return Integer.valueOf(row);
				case 1:
					return command.toString();
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
	}
}