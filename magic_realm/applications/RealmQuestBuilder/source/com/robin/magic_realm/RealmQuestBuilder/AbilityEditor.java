/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
package com.robin.magic_realm.RealmQuestBuilder;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GameObjectBlockManager;
import com.robin.general.swing.ComponentTools;
import com.robin.magic_realm.RealmCharacterBuilder.EditPanel.*;
import com.robin.magic_realm.components.CharacterActionChitComponent;
import com.robin.magic_realm.components.quest.QuestMinorCharacter;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class AbilityEditor extends GenericEditor {
	public enum AbilityType {
		ColorSource,
		DieModification,
		ExtraAction,
		ExtraChit,
		MiscellaneousAbilities,
		MonsterInteraction,
		MonsterImmunity,
		MonsterControl,
		MonsterFear,
		SpecialAction,
		TacticsChange,
	}

	public static final String TEMPLATE_ABILITY_BLOCK = "design";
	private CharacterWrapper template;
	private AbilityType type;
	private AdvantageEditPanel editPanel;

	public AbilityEditor(JFrame frame,String title,AbilityType type,CharacterWrapper template) {
		super(frame,null);
		this.setTitle(title);
		this.type = type;
		this.template = template;//new CharacterWrapper(GameObject.createEmptyGameObject());
		initComponents();
	}
	public void update(GameObject target,String targetBlock) {
		target.copyAttributeBlockFrom(template.getGameObject(),TEMPLATE_ABILITY_BLOCK);
		target.removeAttributeBlock(targetBlock);
		target.copyAttributeBlock(TEMPLATE_ABILITY_BLOCK,targetBlock);
		target.removeAttributeBlock(TEMPLATE_ABILITY_BLOCK);
		target.setAttribute(targetBlock,QuestMinorCharacter.ABILITY_DESCRIPTION,editPanel.getSuggestedDescription());
		target.setAttribute(targetBlock,QuestMinorCharacter.ABILITY_TYPE,type.toString());
		
		GameObjectBlockManager manTarget = new GameObjectBlockManager(target);
		manTarget.clearBlocks(Constants.BONUS_CHIT+TEMPLATE_ABILITY_BLOCK);
		GameObjectBlockManager manTemplate = new GameObjectBlockManager(template.getGameObject());
		GameObject go = manTemplate.extractGameObjectFromBlocks(Constants.BONUS_CHIT+TEMPLATE_ABILITY_BLOCK,true);
		if (go != null) {
			CharacterActionChitComponent extraChit = new CharacterActionChitComponent(go);
			manTarget.storeGameObjectInBlocks(extraChit.getGameObject(),Constants.BONUS_CHIT+TEMPLATE_ABILITY_BLOCK);
		}
	}
	@Override
	protected boolean isValidForm() {
		return true;
	}
	@Override
	protected void save() {
		editPanel.apply();
	}
	private void initComponents() {
		setSize(700,500);
		setLayout(new BorderLayout());
		editPanel = null;
		JDialog frame = new JDialog();
		switch(type) {
			case ColorSource:
				editPanel = new ColorSourceEditPanel(template,TEMPLATE_ABILITY_BLOCK);
				break;
			case DieModification:
				editPanel = new DieModEditPanel(template,TEMPLATE_ABILITY_BLOCK);
				break;
			case ExtraAction:
				editPanel = new ExtraActionEditPanel(template,TEMPLATE_ABILITY_BLOCK);
				break;
			case ExtraChit:
				editPanel = new ExtraChitEditPanel(frame, template,TEMPLATE_ABILITY_BLOCK);
				break;
			case MiscellaneousAbilities:
				editPanel = new MiscellaneousEditPanel(template,TEMPLATE_ABILITY_BLOCK);
				break;
			case MonsterInteraction:
			case MonsterImmunity:
				editPanel = new MonsterInteractionEditPanel(template,TEMPLATE_ABILITY_BLOCK,Constants.MONSTER_IMMUNITY);
				break;
			case MonsterControl:
				editPanel = new MonsterInteractionEditPanel(template,TEMPLATE_ABILITY_BLOCK,Constants.MONSTER_CONTROL);
				break;
			case MonsterFear:
				editPanel = new MonsterInteractionEditPanel(template,TEMPLATE_ABILITY_BLOCK,Constants.MONSTER_FEAR);
				break;
			case SpecialAction:
				editPanel = new SpecialActionEditPanel(template,TEMPLATE_ABILITY_BLOCK);
				break;
			case TacticsChange:
				editPanel = new TacticsChangeEditPanel(template,TEMPLATE_ABILITY_BLOCK);
				break;
		}
		if (editPanel==null) {
			throw new IllegalStateException("Unsupported AbilityType "+type.toString());
		}
		add(editPanel);
		add(buildOkCancelLine(),BorderLayout.SOUTH);
	}
	public static void main(String[] args) {
		ComponentTools.setSystemLookAndFeel();
		AbilityEditor editor = new AbilityEditor(new JFrame(),"Ability Editor Test",AbilityType.DieModification,new CharacterWrapper(GameObject.createEmptyGameObject()));
		editor.setLocationRelativeTo(null);
		editor.setVisible(true);
		if (!editor.getCanceledEdit()) {
			// Do something
		}
		System.exit(0);
	}
}