package com.robin.magic_realm.RealmCharacterBuilder.EditPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GameObjectBlockManager;
import com.robin.general.swing.ComponentTools;
import com.robin.magic_realm.RealmCharacterBuilder.ChitEditDialog;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class ExtraChitEditPanel extends AdvantageEditPanel {
	
	private CharacterActionChitComponent extraChit;
	private JLabel iconLabel;
	private JDialog parentFrame;

	public ExtraChitEditPanel(JDialog frame,CharacterWrapper pChar, String levelKey) {
		super(pChar, levelKey);
		
		parentFrame = frame;
		
		GameObject go = GameObject.createEmptyGameObject();
		go.setName("Extra Chit");
		go.setThisAttribute("action","FIGHT");
		go.setThisAttribute("strength","L");
		go.setThisAttribute("speed",4);
		go.setThisAttribute("character_chit","");
		go.setThisAttribute("stage",0);
		go.setThisAttribute("level",0);
		go.setThisAttribute("effort",0);
		go.setThisAttribute(Constants.BONUS_CHIT,levelKey);
		
		extraChit = new CharacterActionChitComponent(go);
		extraChit.setFaceUp();
		
		setLayout(new BorderLayout());
		
		Box main = Box.createVerticalBox();
		main.add(Box.createVerticalGlue());
		Box line;
			line = Box.createHorizontalBox();
			line.add(Box.createHorizontalGlue());
			iconLabel = new JLabel(extraChit.getIcon());
			line.add(iconLabel);
			line.add(Box.createHorizontalGlue());
		main.add(line);
		main.add(Box.createVerticalStrut(25));
			line = Box.createHorizontalBox();
			line.add(Box.createHorizontalGlue());
			JButton editButton = new JButton("Edit");
			editButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					ChitEditDialog editor = new ChitEditDialog(parentFrame,extraChit);
					editor.setLocationRelativeTo(ExtraChitEditPanel.this);
					editor.setVisible(true);
					updateIcon();
				}
			});
			ComponentTools.lockComponentSize(editButton,100,40);
			line.add(editButton);
			line.add(Box.createHorizontalGlue());
		main.add(line);
		main.add(Box.createVerticalGlue());
		add(main,"Center");
		
		if (hasAttribute(Constants.BONUS_CHIT)) {
			GameObjectBlockManager man = new GameObjectBlockManager(character);
			go = man.extractGameObjectFromBlocks(getBaseBlockName(),true);
			extraChit = new CharacterActionChitComponent(go);
			man.clearBlocks(getBaseBlockName());
			updateIcon();
		}
	}
	private void updateIcon() {
		iconLabel.setIcon(extraChit.getIcon());
	}

	private String getBaseBlockName() {
		return Constants.BONUS_CHIT+getLevelKey();
	}
	
	protected void applyAdvantage() {
		setAttribute(Constants.BONUS_CHIT);
		GameObjectBlockManager man = new GameObjectBlockManager(character);
		man.storeGameObjectInBlocks(extraChit.getGameObject(),getBaseBlockName());
	}

	public boolean isCurrent() {
		return hasAttribute(Constants.BONUS_CHIT);
	}

	public String toString() {
		return "Extra Chit";
	}
	
	public String getSuggestedDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Starts with an extra ");
		sb.append(extraChit.getGameObject().getThisAttribute("action").toUpperCase());
		sb.append(" chit.");
		return sb.toString();
	}
}