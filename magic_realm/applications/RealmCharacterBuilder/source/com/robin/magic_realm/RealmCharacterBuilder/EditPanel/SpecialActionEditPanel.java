package com.robin.magic_realm.RealmCharacterBuilder.EditPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class SpecialActionEditPanel extends AdvantageEditPanel implements ActionListener {

	private String[][] SPECIAL_ACTIONS = {
			{"ENHANCED_PEER","Can ENHANCED PEER into any clearing from anywhere."},
			{"REMOTE_SPELL","Can REMOTE SPELL in any clearing from anywhere."},
			{"HEAL","Can HEAL one asterisk of fatigue or wound on any other character."},
			{"REPAIR","Can REPAIR one armor item in carried inventory"},
			{"FORTIFY","Can FORTIFY during the day to gain armor-like protection in all directions (Suit of Armor) equal to their vulnerability.  " +
					"The FORTIFY action requires a successful roll on the HIDE table (though not affected by HIDE advantages!).  Fortified characters cannot run from battle.  " +
					"This fortification can be damaged and destroyed as if it were armor.  Regardless of its condition at the end of the battle, the fortification is lost at MIDNIGHT."},
	};
	
	private String currentSpecialAction = null;

	public SpecialActionEditPanel(CharacterWrapper character, String levelKey) {
		super(character, levelKey);
		
		setLayout(new BorderLayout());
		Box main = Box.createVerticalBox();
		
		currentSpecialAction = SPECIAL_ACTIONS[0][0];
		
		ArrayList<String> special = getAttributeList(Constants.SPECIAL_ACTION);
		if (special!=null) {
			currentSpecialAction = special.get(0);
		}		
		
		ButtonGroup bg = new ButtonGroup();
		for (int i=0;i<SPECIAL_ACTIONS.length;i++) {
			JRadioButton button = new JRadioButton("<html>"+SPECIAL_ACTIONS[i][0]+" - "+SPECIAL_ACTIONS[i][1]+"</html>");
			String action = getButtonAction(button);
			button.setSelected(action.equals(currentSpecialAction));
			button.addActionListener(this);
			bg.add(button);
			main.add(button);
			main.add(Box.createVerticalStrut(20));
		}
		main.add(Box.createVerticalGlue());
		add(main,"Center");
	}
	private static String getButtonAction(JRadioButton button) {
		String text = button.getText();
		return text.substring(6,text.indexOf(" - "));
	}
	public void actionPerformed(ActionEvent ev) {
		JRadioButton button = (JRadioButton)ev.getSource();
		currentSpecialAction = getButtonAction(button);
	}

	protected void applyAdvantage() {
		ArrayList<String> list = new ArrayList<>();
		list.add(currentSpecialAction);
		setAttributeList(Constants.SPECIAL_ACTION,list);
	}

	public boolean isCurrent() {
		return hasAttribute(Constants.SPECIAL_ACTION);
	}

	public String toString() {
		return "Special Day Action";
	}
	
	public String getSuggestedDescription() {
		for (int i=0;i<SPECIAL_ACTIONS.length;i++) {
			if (currentSpecialAction.equals(SPECIAL_ACTIONS[i][0])) {
				return SPECIAL_ACTIONS[i][1];
			}
		}
		return null;
	}
}