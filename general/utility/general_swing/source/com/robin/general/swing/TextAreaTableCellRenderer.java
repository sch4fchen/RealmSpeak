package com.robin.general.swing;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class TextAreaTableCellRenderer extends JTextArea implements TableCellRenderer {
	private final DefaultTableCellRenderer modelRenderer = new DefaultTableCellRenderer();

	public TextAreaTableCellRenderer() {
		setLineWrap(true);
		setWrapStyleWord(true);
	}

	public Component getTableCellRendererComponent(
			JTable table, Object obj, boolean isSelected, boolean hasFocus,
			int row, int column) {
		modelRenderer.getTableCellRendererComponent(table, obj, isSelected, hasFocus,row, column);
		setForeground(modelRenderer.getForeground());
		setBackground(modelRenderer.getBackground());
		setBorder(modelRenderer.getBorder());
		setFont(modelRenderer.getFont());
		setText(modelRenderer.getText());
		return this;
	}
}