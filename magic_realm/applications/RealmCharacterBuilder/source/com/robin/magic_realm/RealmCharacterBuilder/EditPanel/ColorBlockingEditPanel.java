package com.robin.magic_realm.RealmCharacterBuilder.EditPanel;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import com.robin.general.swing.ImageCache;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class ColorBlockingEditPanel extends AdvantageEditPanel implements ActionListener {

	private static final String[] COLOR_MAGIC = {
		"White",
		"Grey", // this matches the rules, but does NOT match the color magic logic, which is "Gray"
		"Gold",
		"Purple",
		"Black",
	};
	
	private static final String[] COLOR_ICON = {
		"white",
		"gray",
		"gold",
		"purple",
		"black",
	};
	
	private JCheckBox[] colorOption;
	private JLabel[] colorIcon;
	private static Font font = new Font("Dialog",Font.PLAIN,18);
		
	public ColorBlockingEditPanel(CharacterWrapper pChar, String levelKey) {
		super(pChar, levelKey);
		
		setLayout(new BorderLayout());
		Box box = Box.createVerticalBox();
		colorOption = new JCheckBox[COLOR_MAGIC.length];
		colorIcon = new JLabel[COLOR_MAGIC.length];
		for (int i=0;i<COLOR_MAGIC.length;i++) {
			ImageIcon icon = ImageCache.getIcon("colormagic/"+COLOR_ICON[i]);
			Box line = Box.createHorizontalBox();
				colorOption[i] = new JCheckBox(COLOR_MAGIC[i],i==0);
				colorOption[i].setFont(font);
				colorOption[i].addActionListener(this);
				line.add(colorOption[i]);
				colorIcon[i] = new JLabel(icon);
				line.add(colorIcon[i]);
				line.add(Box.createHorizontalGlue());
			box.add(line);
		}
		box.add(Box.createVerticalGlue());
		add(box,"Center");
		
		ArrayList<String> cs = getAttributeList(Constants.BLOCKED_BY_MAGIC_COLOR);
		if (cs!=null) {
			for (int i=0;i<COLOR_MAGIC.length;i++) {
				if (cs.contains(COLOR_MAGIC[i].toLowerCase())) {
					colorOption[i].setSelected(true);
				}
			}
		}
		updateColorIcon();
	}
	
	protected void updateColorIcon() {
		for (int i=0;i<COLOR_MAGIC.length;i++) {
			colorIcon[i].setVisible(colorOption[i].isSelected());
		}
	}

	protected void applyAdvantage() {
		for (int i=0;i<COLOR_MAGIC.length;i++) {
			if (colorOption[i].isSelected()) {
				addAttributeListItem(Constants.BLOCKED_BY_MAGIC_COLOR,COLOR_MAGIC[i].toLowerCase());
			}
		}
	}
	public String getSuggestedDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Is blocked by ");
		for (int i=0;i<COLOR_MAGIC.length;i++) {
			if (colorOption[i].isSelected()) {
				sb.append(COLOR_MAGIC[i].toUpperCase()+" ");
			}
		}
		sb.append("magic color.");
		return sb.toString();
	}

	public boolean isCurrent() {
		return hasAttribute(Constants.BLOCKED_BY_MAGIC_COLOR);
	}

	public String toString() {
		return "Blocked by Magic";
	}

	public void actionPerformed(ActionEvent arg0) {
		updateColorIcon();
	}
}