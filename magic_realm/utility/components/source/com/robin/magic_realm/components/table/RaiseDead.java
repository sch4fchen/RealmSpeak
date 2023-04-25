package com.robin.magic_realm.components.table;

import java.util.ArrayList;

import javax.swing.JFrame;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.CombatWrapper;

public class RaiseDead extends MonsterTable {

	public static final String KEY = "RaiseDead";
	
	public RaiseDead(JFrame frame) {
		super(frame);
	}
	public String getTableKey() {
		return KEY;
	}
	public String getTableName(boolean longDescription) {
		return "Raise Dead";
	}
	public String getMonsterKey() {
		return "undead";
	}
	public String applyOne(CharacterWrapper character) {
		return raiseUndead(character,12,true);
	}
	public String applyTwo(CharacterWrapper character) {
		return raiseUndead(character,8,true);
	}
	public String applyThree(CharacterWrapper character) {
		return raiseUndead(character,4,true);
	}
	public String applyFour(CharacterWrapper character) {
		return raiseUndead(character,2,true);
	}
	public String applyFive(CharacterWrapper character) {
		return raiseUndead(character,1,true);
	}
	public String applySix(CharacterWrapper character) {
		return raiseUndead(character,2,false);
	}
	private String raiseUndead(CharacterWrapper character, int count, boolean hire) {
		TileLocation tl = character.getCurrentLocation();
		GameObject go = ClearingUtility.getItemInClearingWithKey(tl,Constants.NO_UNDEAD);
		if (go==null) {
			CombatWrapper characterCombat = new CombatWrapper(character.getGameObject());
			GameData data = character.getGameObject().getGameData();
			for (int i=0;i<count;i++) {
				GameObject undead = createUndead(getMonsterCreator(),data);
				undead.setThisAttribute(Constants.UNDEAD);
				if (hire) {
					character.addHireling(undead);
					CombatWrapper combat = new CombatWrapper(undead);
					combat.setSheetOwner(true);
				}
				if (tl!=null && tl.isInClearing()) {
					tl.clearing.add(undead,null);
					if (characterCombat.getRaiseTheDead() && !characterCombat.getRaisedDead()) {
						CombatWrapper tlCombat = new CombatWrapper(tl.tile.getGameObject());
						tlCombat.addRaisedUndead(undead);
					}
				}
			}
			return "Raised "+count+" "+(hire?"":"Enemy")+" Undead";
		}
		return go.getName()+" prevented Undead!";
	}
	public static GameObject createUndead(MonsterCreator monsterCreator,GameData data) {
		int r = RandomNumber.getRandom(11);
		if (r<4) {
			return createSkeleton(monsterCreator,data);
		}
		else if (r<6) {
			return createSkeletonArcher(monsterCreator,data);
		}
		else if (r<8) {
			return createSkeletonSwordsman(monsterCreator,data);
		}
		return createZombie(monsterCreator,data,r-8);
	}
	public ArrayList<GameObject> getOneOfEach(CharacterWrapper character) {
		GameData data = character.getGameObject().getGameData();
		ArrayList<GameObject> list = new ArrayList<>();
		list.add(createSkeleton(getMonsterCreator(),data));
		list.add(createSkeletonArcher(getMonsterCreator(),data));
		list.add(createSkeletonSwordsman(getMonsterCreator(),data));
		for (int i=0;i<3;i++) {
			list.add(createZombie(getMonsterCreator(),data,i));
		}
		return list;
	}
	public static GameObject createSkeleton(MonsterCreator monsterCreator,GameData data) {
		// L4/4 and L2/6
		GameObject go = monsterCreator.createOrReuseMonster(data);
		monsterCreator.setupGameObject(go,"Skeleton","skull","M",false);
		MonsterCreator.setupSide(go,"light","L",0,4,0,4,"white");
		MonsterCreator.setupSide(go,"dark","L",0,2,0,6,"gray");
		return go;
	}
	public static GameObject createSkeletonArcher(MonsterCreator monsterCreator,GameData data) {
		// -/3 and M*3/5
		GameObject go = monsterCreator.createOrReuseMonster(data);
		monsterCreator.setupGameObject(go,"Skeletal Archer","skullbow","M",false);
		MonsterCreator.setupSide(go,"light",null,0,0,0,3,"white");
		MonsterCreator.setupSide(go,"dark","M",1,3,14,5,"gray");
		return go;
	}
	public static GameObject createSkeletonSwordsman(MonsterCreator monsterCreator,GameData data) {
		// L*4/4 and L*2/6
		GameObject go = monsterCreator.createOrReuseMonster(data);
		monsterCreator.setupGameObject(go,"Skeletal Swordsman","skullsword","M",false);
		MonsterCreator.setupSide(go,"light","L",1,4,3,4,"white");
		MonsterCreator.setupSide(go,"dark","L",1,2,3,6,"gray");
		return go;
	}
	public static GameObject createZombie(MonsterCreator monsterCreator,GameData data,int r) {
		GameObject go = monsterCreator.createOrReuseMonster(data);
		int z = RandomNumber.getRandom(2)+1;
		monsterCreator.setupGameObject(go,"Zombie","zombie"+z,"M",false);
		switch(r) {
			case 0:
				// M5/5 and H6/5
				MonsterCreator.setupSide(go,"light","M",0,5,0,5,"peach");
				MonsterCreator.setupSide(go,"dark","H",0,6,0,5,"purple");
				break;
			case 1:
				// M5/4 and T5/5
				MonsterCreator.setupSide(go,"light","M",0,5,0,4,"peach");
				MonsterCreator.setupSide(go,"dark","T",0,5,0,5,"purple");
				break;
			case 2:
				// L4/5 and M6/4
				MonsterCreator.setupSide(go,"light","L",0,4,0,5,"peach");
				MonsterCreator.setupSide(go,"dark","M",0,6,0,4,"purple");
				break;
		}
		return go;
	}
}