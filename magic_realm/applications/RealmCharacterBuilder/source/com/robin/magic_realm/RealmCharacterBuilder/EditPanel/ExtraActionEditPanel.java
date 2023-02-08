package com.robin.magic_realm.RealmCharacterBuilder.EditPanel;

import java.awt.GridLayout;
import java.util.*;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class ExtraActionEditPanel extends AdvantageEditPanel {
	
	private ButtonGroup group;
	private Hashtable<String,JRadioButton> actionHash;

	public ExtraActionEditPanel(CharacterWrapper pChar,String levelKey) {
		super(pChar,levelKey);
		setLayout(new GridLayout(12,1));
		
		group = new ButtonGroup();
		actionHash = new Hashtable<>();
		addOption("HIDE","H",true); // default
		addOption("MOVE","M");
		addOption("SEARCH","S");
		addOption("TRADE","T");
		addOption("REST","R");
		addOption("ALERT","A");
		addOption("HIRE","HR");
		addOption("SPELL","SP");
		addOption("PEER","P");
		addOption("FLY","FLY");
		addOption("REMOTE SP","RS");
		addOption("CACHE","C");
		
		// Initialize, if you can
		ArrayList<String> extra = getAttributeList(Constants.EXTRA_ACTIONS);
		if (extra!=null) {
			for (String extraAction : extra) {
				JRadioButton button = actionHash.get(extraAction);
				button.setSelected(true);
				break; // assume only ONE per list
			}
		}
	}
	private void addOption(String name,String action) {
		addOption(name,action,false);
	}
	private void addOption(String name,String action,boolean selected) {
		JRadioButton button = new JRadioButton(name,selected);
		group.add(button);
		add(button);
		actionHash.put(action,button);
	}
	public String toString() {
		return "Extra Action";
	}
	public boolean isCurrent() {
		return hasAttribute(Constants.EXTRA_ACTIONS);
	}
	protected void applyAdvantage() {
		for (String action:actionHash.keySet()) {
			JRadioButton button = actionHash.get(action);
			if (button.isSelected()) {
				ArrayList<String> list = new ArrayList<>();
				list.add(action);
				setAttributeList(Constants.EXTRA_ACTIONS,list);
				break;
			}
		}
	}
	public String getSuggestedDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Gets an extra ");
		for (String action:actionHash.keySet()) {
			JRadioButton button = actionHash.get(action);
			if (button.isSelected()) {
				sb.append(button.getText().toUpperCase());
			}
		}
		sb.append(" phase.");
		
		return sb.toString();
	}
}