package com.robin.general.swing;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class MultiQueryDialog extends AggressiveDialog {

	protected Hashtable<String, JTextComponent> textComponents = new Hashtable<>();
	protected Hashtable<String, JComboBox<String>> comboBoxes = new Hashtable<>();
	protected ArrayList<JTextComponent> requiredInputComponents = new ArrayList<>();
	
	protected Box layoutBox;
	protected JButton okay;
	protected JButton cancel;
	protected UniformLabelGroup group = new UniformLabelGroup();
	protected boolean okayPressed = false;

	public MultiQueryDialog(JFrame parent,String title) {
		super(parent,title);
		layoutBox = Box.createVerticalBox();
		layoutBox.add(Box.createVerticalGlue());
		Box controls = Box.createHorizontalBox();
		controls.add(Box.createHorizontalGlue());
			cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					okayPressed = false;
					close();
				}
			});
		controls.add(cancel);
		controls.add(Box.createHorizontalGlue());
			okay = new JButton("Okay");
			okay.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					okayPressed = true;
					close();
				}
			});
		controls.add(okay);
		controls.add(Box.createHorizontalGlue());
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(layoutBox,"Center");
		getContentPane().add(controls,"South");
                getRootPane().setDefaultButton(okay);
		setModal(true);
	}
	public void updateSize() {
		int width = group.getMaxPixelWidth()+200;
		int height = (layoutBox.getComponentCount()+3)*25;
		setSize(width,height);
	}
	public void close() {
		setVisible(false);
	}
	public void updateButtons() {
		boolean allClear = true;
		for (JTextComponent tc : requiredInputComponents) {
			if (tc.getText().trim().length()==0) {
				allClear = false;
				break;
			}
		}
		okay.setEnabled(allClear);
	}
	/**
	 * Adds the component
	 */
	private void addComponent(String label,JComponent component) {
		component.setMaximumSize(new Dimension(200,25));
		int count = layoutBox.getComponentCount();
		Box line = group.createLabelLine(label);
		line.add(component);
		layoutBox.add(line,count-1); // adds the line before the glue
		updateSize();
		updateButtons();
	}
	public void addQueryLine(String key,String label,JTextComponent textComponent) {
		this.addQueryLine(key,label,textComponent,false);
	}
	public void addQueryLine(String key,String label,JTextComponent textComponent,boolean requireInput) {
		if (requireInput) {
			textComponent.addCaretListener(new CaretListener() {
				public void caretUpdate(CaretEvent ev) {
					updateButtons();
				}
			});
			requiredInputComponents.add(textComponent);
		}
		addComponent(label,textComponent);
		textComponents.put(key,textComponent);
	}
	public void addQueryLine(String key,String label,JComboBox<String> comboBox) {
		addComponent(label,comboBox);
		comboBoxes.put(key,comboBox);
	}
	public String getText(String key) {
		JTextComponent textComponent = textComponents.get(key);
		if (textComponent!=null) {
			return textComponent.getText().trim();
		}
		return null;
	}
	public Object getComboChoice(String key) {
		JComboBox<String> comboBox = comboBoxes.get(key);
		if (comboBox!=null) {
			return comboBox.getSelectedItem();
		}
		return null;
	}
	public boolean saidOkay() {
		return okayPressed;
	}
	public static void main(String[]args) {
		MultiQueryDialog dialog = new MultiQueryDialog(new JFrame(),"test");
		dialog.addQueryLine("name","Name",new JTextField(),true);
		dialog.addQueryLine("address","Address",new JTextField(),true);
			JComboBox<String> cb = new JComboBox<>();
			cb.addItem("Northern");
			cb.addItem("Southern");
			cb.addItem("Norweestum");
		dialog.addQueryLine("county","County",cb);
		dialog.setVisible(true);
		
		if (dialog.saidOkay()) {
			System.out.println("Name:  "+dialog.getText("name"));
			System.out.println("Address:  "+dialog.getText("address"));
			System.out.println("County:  "+dialog.getComboChoice("county"));
		}
	}
}