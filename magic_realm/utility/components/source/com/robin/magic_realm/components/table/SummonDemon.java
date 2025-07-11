package com.robin.magic_realm.components.table;

import javax.swing.JFrame;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.MonsterCreator;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class SummonDemon extends MonsterTable {

	public static final String KEY = "SummonDemon";
	
	public enum DemonType {
		Devil,
		WingedDemon,
		Demon,
		Ghoul,
		Zombie,
		Ghost,
	}
	
	public SummonDemon(JFrame frame) {
		super(frame);
	}
	public String getTableKey() {
		return KEY;
	}
	public String getTableName(boolean longDescription) {
		return "Summon Demon";
	}
	public String getMonsterKey() {
		return "summoned_demon";
	}
	public String applyOne(CharacterWrapper character) {
		summon(character,DemonType.Devil);
		return "Devil Summoned";
	}
	public String applyTwo(CharacterWrapper character) {
		summon(character,DemonType.WingedDemon);
		return "Winged Demon Summoned";
	}
	public String applyThree(CharacterWrapper character) {
		summon(character,DemonType.Demon);
		return "Demon Summoned";
	}
	public String applyFour(CharacterWrapper character) {
		summon(character,DemonType.Ghoul);
		return "Ghoul Summoned";
	}
	public String applyFive(CharacterWrapper character) {
		summon(character,DemonType.Zombie);
		return "Zombie Summoned";
	}
	public String applySix(CharacterWrapper character) {
		summon(character,DemonType.Ghost);
		return "Ghost Summoned";
	}
	public GameObject createDemon(GameData data, DemonType type) {
		GameObject demon = getMonsterCreator().createMonster(data);
		switch(type) {
			case Devil:
				getMonsterCreator().setupGameObject(demon,"Devil","demon","X",true,false,false,"wesnoth/units");
				MonsterCreator.setupSide(demon,"light","",0,0,18,-1,"gray");
				MonsterCreator.setupSide(demon,"dark","RED",0,0,18,-1,"darkgray");
				demon.setAttribute("light", "magic_type", "V");
				demon.setAttribute("light", "attack_spell", Constants.DEVILS_SPELL);
				demon.setThisAttribute(Constants.NO_CHANGE_TACTICS);
				demon.setThisAttribute(Constants.DEVIL);
				demon.setThisAttribute(Constants.ICON_SIZE,"0.9");
				break;
			case WingedDemon:
				getMonsterCreator().setupGameObject(demon,"Winged Demon","galerunner","T",false,true,false,"wesnoth/units");
				MonsterCreator.setupSide(demon,"light","",0,3,17,3,"gray");
				MonsterCreator.setupSide(demon,"dark","M",0,3,17,3,"darkgray");
				demon.setAttribute("light", "magic_type", "V");
				demon.setAttribute("light", "attack_spell", Constants.POWER_OF_THE_PIT);
				demon.setThisAttribute(Constants.DEMON);
				demon.setThisAttribute(Constants.ICON_SIZE,"0.9");
				break;
			case Demon:
				getMonsterCreator().setupGameObject(demon,"Demon","yuureNightmare","T",false,false,false,"wesnoth/units");
				MonsterCreator.setupSide(demon,"light","",0,2,17,4,"gray");
				MonsterCreator.setupSide(demon,"dark","H",0,2,17,4,"darkgray");
				demon.setAttribute("light", "magic_type", "V");
				demon.setAttribute("light", "attack_spell", Constants.POWER_OF_THE_PIT);
				demon.setThisAttribute(Constants.DEMON);
				demon.setThisAttribute(Constants.ICON_SIZE,"0.9");
				break;
			case Ghoul:
				getMonsterCreator().setupGameObject(demon,"Ghoul","ghoul","H",false,false,false,"wesnoth/units/undead");
				MonsterCreator.setupSide(demon,"light","M",0,4,0,4,"gray");
				MonsterCreator.setupSide(demon,"dark","M",0,4,0,4,"darkgray");
				demon.setThisAttribute(Constants.GHOUL);
				demon.setThisAttribute(Constants.UNDEAD);
				undead.setThisAttribute(Constants.UNDEAD_SUMMONED);
				demon.setThisAttribute(Constants.ICON_SIZE,"0.9");
				break;
			case Zombie:
				getMonsterCreator().setupGameObject(demon,"Zombie","zombie","H",false,false,false,"wesnoth/units/undead");
				MonsterCreator.setupSide(demon,"light","L",0,5,0,5,"gray");
				MonsterCreator.setupSide(demon,"dark","L",0,5,0,5,"darkgray");
				demon.setThisAttribute(Constants.ZOMBIE);
				demon.setThisAttribute(Constants.UNDEAD);
				undead.setThisAttribute(Constants.UNDEAD_SUMMONED);
				demon.setThisAttribute(Constants.ICON_SIZE,"0.9");
				break;
			case Ghost:
				getMonsterCreator().setupGameObject(demon,"Ghost","ghost","M",true,false,false,"wesnoth/units");
				MonsterCreator.setupSide(demon,"light","M",0,2,0,2,"gray");
				MonsterCreator.setupSide(demon,"dark","M",0,2,0,2,"darkgray");
				demon.setThisAttribute(Constants.GHOST);
				demon.setThisAttribute(Constants.UNDEAD);
				undead.setThisAttribute(Constants.UNDEAD_SUMMONED);
				demon.setThisAttribute(Constants.ICON_SIZE,"0.9");
				break;
		}
		demon.setThisAttribute(Constants.SPOILS_NONE);
		demon.setThisAttribute(Constants.SUPER_REALM);
		return demon;
	}
	private void summon(CharacterWrapper character, DemonType type) {
		GameData data = character.getGameObject().getGameData();
		GameObject demon = createDemon(data, type);
		CombatWrapper combat = new CombatWrapper(demon);
		combat.setSheetOwner(false);
		TileLocation tl = character.getCurrentLocation();
		if (tl!=null && tl.isInClearing()) {
			tl.clearing.add(demon,null);
		}
	}
}