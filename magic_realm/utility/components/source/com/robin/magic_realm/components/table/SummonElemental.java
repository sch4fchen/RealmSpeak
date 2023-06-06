package com.robin.magic_realm.components.table;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.general.swing.ButtonOptionDialog;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.MonsterCreator;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class SummonElemental extends MonsterTable {

	public static final String KEY = "SummonElemental";
	
	public enum ElementalType {
		Earth,
		Fire,
		Water,
		Air
	}
	
	public SummonElemental(JFrame frame) {
		super(frame);
	}
	public String getTableKey() {
		return KEY;
	}
	public String getTableName(boolean longDescription) {
		return "Summon Elemental";
	}
	public String getMonsterKey() {
		return "elemental";
	}
//public String apply(CharacterWrapper character, DieRoller inRoller) {
//	System.err.println("REMOVE THIS LINE - SummonElemental");
//	return applyOne(character);
//}
	public String applyOne(CharacterWrapper character) {
		String earth = "Earth Elemental";
		String fire = "Fire Elemental";
		String water = "Water Elemental";
		String air = "Air Elemental";
		
		ButtonOptionDialog chooseSearch = new ButtonOptionDialog(getParentFrame(), null, "Choice:", "Summon Elemental", false);
		chooseSearch.addSelectionObject(earth);
		chooseSearch.addSelectionObject(fire);
		chooseSearch.addSelectionObject(water);
		chooseSearch.addSelectionObject(air);
		chooseSearch.setVisible(true);
		String choice = (String)chooseSearch.getSelectedObject();
		if (choice.equals(earth)) {
			return "Choice - " + applyTwo(character);
		}
		else if (choice.equals(fire)) {
			return "Choice - " + applyThree(character);
		}
		else if (choice.equals(water)) {
			return "Choice - " + applyFour(character);
		}
		else if (choice.equals(air)) {
			return "Choice - " + applyFive(character);
		}
		return null;
	}
	public String applyTwo(CharacterWrapper character) {
		summonElemental(character,ElementalType.Earth);
		return "Earth Elemental Summoned";
	}
	public String applyThree(CharacterWrapper character) {
		summonElemental(character,ElementalType.Fire);
		return "Fire Elemental Summoned";
	}
	public String applyFour(CharacterWrapper character) {
		summonElemental(character,ElementalType.Water);
		return "Water Elemental Summoned";
	}
	public String applyFive(CharacterWrapper character) {
		summonElemental(character,ElementalType.Air);
		return "Air Elemental Summoned";
	}
	public String applySix(CharacterWrapper character) {
		return "No Effect";
	}
	public GameObject createElemental(GameData data, ElementalType type) {
		GameObject elemental = getMonsterCreator().createOrReuseMonster(data);
		switch(type) {
			case Earth:
				getMonsterCreator().setupGameObject(elemental,"Earth Elemental","earth","T",true);
				MonsterCreator.setupSide(elemental,"light","T",0,4,0,6,"tan");
				MonsterCreator.setupSide(elemental,"dark","RED",0,4,0,6,"red");
				elemental.setAttribute("dark","pins");
				elemental.setThisAttribute(Constants.ICON_TYPE+Constants.ALTERNATIVE,"WarOfTheGods-monsters-elemental-earth");
				elemental.setThisAttribute(Constants.ICON_FOLDER+Constants.ALTERNATIVE,"wesnoth/units/addons");
				elemental.setThisAttribute(Constants.ICON_SIZE+Constants.ALTERNATIVE,"0.9");
				elemental.setThisAttribute(Constants.ICON_Y_OFFSET+Constants.ALTERNATIVE,"9");
				break;
			case Fire:
				getMonsterCreator().setupGameObject(elemental,"Fire Elemental","fire","H",false);
				MonsterCreator.setupSide(elemental,"light","H",2,6,1,4,"lightorange");
				MonsterCreator.setupSide(elemental,"dark","H",1,2,1,6,"orange");
				elemental.setThisAttribute(Constants.ICON_TYPE+Constants.ALTERNATIVE,"WarOfTheGods-monsters-elemental-fire1");
				elemental.setThisAttribute(Constants.ICON_FOLDER+Constants.ALTERNATIVE,"wesnoth/units/addons");
				elemental.setThisAttribute(Constants.ICON_SIZE+Constants.ALTERNATIVE,"0.9");
				elemental.setThisAttribute(Constants.ICON_Y_OFFSET+Constants.ALTERNATIVE,"9");
				break;
			case Water:
				getMonsterCreator().setupGameObject(elemental,"Water Elemental","water","H",false);
				MonsterCreator.setupSide(elemental,"light","H",0,4,0,2,"lightblue");
				MonsterCreator.setupSide(elemental,"dark","H",0,2,0,4,"blue");
				elemental.setThisAttribute(Constants.ICON_TYPE+Constants.ALTERNATIVE,"Reign_of_the_Lords-elementals-nymph");
				elemental.setThisAttribute(Constants.ICON_FOLDER+Constants.ALTERNATIVE,"wesnoth/units/addons");
				elemental.setThisAttribute(Constants.ICON_SIZE+Constants.ALTERNATIVE,"0.9");
				elemental.setThisAttribute(Constants.ICON_Y_OFFSET+Constants.ALTERNATIVE,"9");
				break;
			case Air:
				getMonsterCreator().setupGameObject(elemental,"Air Elemental","air","M",false,true);
				MonsterCreator.setupSide(elemental,"light","M",0,3,0,4,"white");
				MonsterCreator.setupSide(elemental,"dark","H",0,4,0,4,"gray");
				elemental.setThisAttribute(Constants.ICON_TYPE+Constants.ALTERNATIVE,"dust-devil");
				elemental.setThisAttribute(Constants.ICON_FOLDER+Constants.ALTERNATIVE,"wesnoth/units/monsters");
				elemental.setThisAttribute(Constants.ICON_SIZE+Constants.ALTERNATIVE,"0.9");
				elemental.setThisAttribute(Constants.ICON_Y_OFFSET+Constants.ALTERNATIVE,"9");
				break;
		}
		elemental.setThisAttribute("elemental");
		return elemental;
	}
	private void summonElemental(CharacterWrapper character, ElementalType type) {
		GameData data = character.getGameObject().getGameData();
		GameObject elemental = createElemental(data, type);
		TileLocation tl = character.getCurrentLocation();
		character.addHireling(elemental);
		CombatWrapper combat = new CombatWrapper(elemental);
		combat.setSheetOwner(true);
		if (tl!=null && tl.isInClearing()) {
			tl.clearing.add(elemental,null);
		}
	}
	public ArrayList<GameObject> getOneOfEach(CharacterWrapper character) {
		applyTwo(character);
		applyThree(character);
		applyFour(character);
		applyFive(character);
		return getMonsterCreator().getMonstersCreated();
	}
}