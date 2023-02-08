package com.robin.magic_realm.components.table;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.MonsterCreator;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public abstract class MonsterTable extends RealmTable {

	public abstract String getMonsterKey();

	private MonsterCreator monsterCreator;
	
	protected MonsterTable(JFrame frame) {
		super(frame, null);
		monsterCreator = new MonsterCreator(getMonsterKey());
	}
	public MonsterCreator getMonsterCreator() {
		return monsterCreator;
	}
	public ArrayList<GameObject> getOneOfEach(CharacterWrapper character) {
		applyOne(character);
		applyTwo(character);
		applyThree(character);
		applyFour(character);
		applyFive(character);
		applySix(character);
		return monsterCreator.getMonstersCreated();
	}
}