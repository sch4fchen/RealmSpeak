package com.robin.game.GameBuilder;

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import com.robin.general.swing.*;
import com.robin.game.objects.*;

public class GameSetupTableModel extends AbstractTableModel implements ColumnSizable {

	protected String[] columnHeaders = {
		"Setup name",
		"Cmds"
	};
	
	protected ArrayList data;

	public GameSetupTableModel(ArrayList data) {
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
		return col==0?String.class:Integer.class;
	}
	public Object getValueAt(int row,int col) {
		if (row<data.size()) {
			GameSetup setup = (GameSetup)data.get(row);
			switch(col) {
				case 0:
					return setup.getName();
				case 1:
					return Integer.valueOf(setup.getCommandCount());
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