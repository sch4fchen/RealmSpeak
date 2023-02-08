package com.robin.magic_realm.components.table;

import javax.swing.JFrame;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.MonsterCreator;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class SummonAnimal extends MonsterTable {

	public static final String KEY = "SummonAnimal";
	
	public enum AnimalType {
		Basilisk,
		Eagle,
		Bear,
		Wolf,
		Hawk,
		Squirrel,
	}
	
	public SummonAnimal(JFrame frame) {
		super(frame);
	}
	public String getTableKey() {
		return KEY;
	}
	public String getTableName(boolean longDescription) {
		return "Summon Animal";
	}
	public String getMonsterKey() {
		return "summoned_animal";
	}
	public String applyOne(CharacterWrapper character) {
		summonAnimal(character,AnimalType.Basilisk);
		return "Basilisk Summoned";
	}
	public String applyTwo(CharacterWrapper character) {
		summonAnimal(character,AnimalType.Eagle);
		return "Giant Eagle Summoned";
	}
	public String applyThree(CharacterWrapper character) {
		summonAnimal(character,AnimalType.Bear);
		return "Bear Summoned";
	}
	public String applyFour(CharacterWrapper character) {
		summonAnimal(character,AnimalType.Wolf);
		return "Wolf Summoned";
	}
	public String applyFive(CharacterWrapper character) {
		summonAnimal(character,AnimalType.Hawk);
		return "Hawk Summoned";
	}
	public String applySix(CharacterWrapper character) {
		summonAnimal(character,AnimalType.Squirrel);
		return "Squirrel Summoned";
	}
	public GameObject createAnimal(GameData data, AnimalType type) {
		GameObject animal = getMonsterCreator().createOrReuseMonster(data);
		switch(type) {
			case Basilisk:
				getMonsterCreator().setupGameObject(animal,"Basilisk","basilisk","T",true);
				MonsterCreator.setupSide(animal,"light","T",0,5,0,6,"lightgreen");
				MonsterCreator.setupSide(animal,"dark","RED",0,5,0,6,"red");
				animal.setAttribute("dark","pins");
				break;
			case Eagle:
				getMonsterCreator().setupGameObject(animal,"Giant Eagle","eagle","H",false,true);
				MonsterCreator.setupSide(animal,"light","M",0,3,0,4,"lightgreen");
				MonsterCreator.setupSide(animal,"dark","M",0,3,0,4,"forestgreen");
				break;
			case Bear:
				getMonsterCreator().setupGameObject(animal,"Bear","bear","H",false);
				MonsterCreator.setupSide(animal,"light","H",0,3,0,4,"lightgreen");
				MonsterCreator.setupSide(animal,"dark","H",0,3,0,4,"forestgreen");
				break;
			case Wolf:
				getMonsterCreator().setupGameObject(animal,"Wolf","wolf","M",false);
				MonsterCreator.setupSide(animal,"light","M",0,4,0,4,"lightgreen");
				MonsterCreator.setupSide(animal,"dark","M",0,4,0,4,"forestgreen");
				break;
			case Hawk:
				getMonsterCreator().setupGameObject(animal,"Hawk","hawk","L",false,true);
				MonsterCreator.setupSide(animal,"light","L",0,2,0,2,"lightgreen");
				MonsterCreator.setupSide(animal,"dark","L",0,2,0,2,"forestgreen");
				break;
			case Squirrel:
				getMonsterCreator().setupGameObject(animal,"Squirrel","squirrel","L",false,false,true);
				MonsterCreator.setupSide(animal,"light",null,0,0,0,2,"lightgreen");
				MonsterCreator.setupSide(animal,"dark",null,0,0,0,2,"forestgreen");
				break;
		}
		return animal;
	}
	private void summonAnimal(CharacterWrapper character, AnimalType type) {
		GameData data = character.getGameObject().getGameData();
		GameObject animal = createAnimal(data, type);
		TileLocation tl = character.getCurrentLocation();
		character.addHireling(animal);
		CombatWrapper combat = new CombatWrapper(animal);
		combat.setSheetOwner(true);
		if (tl!=null && tl.isInClearing()) {
			tl.clearing.add(animal,null);
		}
		character.getGameObject().add(animal); // so that you don't have to assign as a follower right away
	}
}