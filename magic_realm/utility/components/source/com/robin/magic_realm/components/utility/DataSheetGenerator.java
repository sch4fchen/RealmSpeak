package com.robin.magic_realm.components.utility;

import java.io.*;
import java.util.*;

import com.robin.game.objects.*;
import com.robin.general.io.ArgumentParser;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

/**
 * Translates all the counter data captured in the XML file into a datasheet which can be used for proofreading
 */
public class DataSheetGenerator {
	private static void printLine(PrintStream stream,String[] line) {
		for (int i=0;i<line.length;i++) {
			if (i>0) {
				stream.print(",");
			}
			stream.print(line[i]==null?"":line[i]);
		}
		stream.print("\n");
	}
	private static final String[] MONSTER_INFO = {
		"Name",
		"Vul",
		"Armored",
		"Length",
		"Front Attack",
		"Front Move",
		"Back Attack",
		"Back Move",
		"Fame",
		"Notoriety",
	};
	private static void printMonsterInfo(PrintStream stream,GameData data) {
		stream.println("*** MONSTERS ***");
		printLine(stream,MONSTER_INFO);
		GamePool pool = new GamePool(data.getGameObjects());
		Collection<GameObject> monsters = pool.find("monster");
		String[] line = new String[MONSTER_INFO.length];
		for (GameObject monster : monsters) {
			MonsterChitComponent rc = (MonsterChitComponent)RealmComponent.getRealmComponent(monster);
			line[0] = monster.getName();
			line[1] = rc.getVulnerability().toString();
			line[2] = rc.isArmored()?"Yes":"";
			line[3] = rc.getLength().toString();
			rc.setLightSideUp();
			line[4] = rc.getAttackString();
			line[5] = rc.getMoveSpeed()==null?"":String.valueOf(rc.getMoveSpeed().getNum());
			rc.setDarkSideUp();
			line[6] = rc.getAttackString();
			line[7] = rc.getMoveSpeed()==null?"":String.valueOf(rc.getMoveSpeed().getNum());
			line[8] = monster.getThisAttribute("fame");
			line[9] = monster.getThisAttribute("notoriety");
			printLine(stream,line);
		}
		stream.println("---------------------");
	}
	private static final String[] NATIVE_INFO = {
		"Name",
		"Vul",
		"Icon",
		"Armored",
		"Length",
		"Front Attack",
		"Front Move",
		"Back Attack",
		"Back Move",
		"Fame",
		"Notoriety",
		"Gold",
	};
	private static void printNativeInfo(PrintStream stream,GameData data) {
		stream.println("*** NATIVES ***");
		printLine(stream,NATIVE_INFO);
		GamePool pool = new GamePool(data.getGameObjects());
		Collection<GameObject> natives = pool.find("hire_type");
		String[] line = new String[NATIVE_INFO.length];
		for (GameObject aNative : natives) {
			NativeChitComponent rc = (NativeChitComponent)RealmComponent.getRealmComponent(aNative);
			line[0] = aNative.getName();
			line[1] = rc.getVulnerability().toString();
			line[2] = aNative.getThisAttribute("icon_type");
			line[3] = rc.isArmored()?"Yes":"";
			line[4] = rc.getLength().toString();
			rc.setLightSideUp();
			line[5] = rc.getAttackString();
			line[6] = String.valueOf(rc.getFaceAttributeInteger("move_speed"));
			rc.setDarkSideUp();
			line[7] = rc.getAttackString();
			line[8] = String.valueOf(rc.getFaceAttributeInteger("move_speed"));
			line[9] = aNative.getThisAttribute("fame");
			line[10] = aNative.getThisAttribute("notoriety");
			line[11] = aNative.getThisAttribute("base_price");
			printLine(stream,line);
		}
		stream.println("---------------------");
	}
	private static final String[] NATIVE_HORSE_INFO = {
		"Name",
		"Vul",
		"Armored",
		"Front Attack",
		"Front Move",
		"Back Attack",
		"Back Move",
	};
	private static void printNativeHorseInfo(PrintStream stream,GameData data) {
		stream.println("*** NATIVE HORSES ***");
		printLine(stream,NATIVE_HORSE_INFO);
		GamePool pool = new GamePool(data.getGameObjects());
		Collection<GameObject> nativeHorses = pool.find("native,horse");
		String[] line = new String[NATIVE_HORSE_INFO.length];
		for (GameObject nativeHorse : nativeHorses) {
			NativeSteedChitComponent rc = (NativeSteedChitComponent)RealmComponent.getRealmComponent(nativeHorse);
			line[0] = nativeHorse.getName();
			line[1] = rc.getVulnerability().toString();
			line[2] = rc.isArmored()?"Yes":"";
			rc.setLightSideUp();
			line[3] = rc.getAttackString();
			line[4] = String.valueOf(rc.getFaceAttributeInteger("move_speed"));
			rc.setDarkSideUp();
			line[5] = rc.getAttackString();
			line[6] = String.valueOf(rc.getFaceAttributeInteger("move_speed"));
			printLine(stream,line);
		}
		stream.println("---------------------");
	}
	private static final String[] HORSE_INFO = {
		"Name",
		"Vul",
		"Armored",
		"Front Attack",
		"Front Move",
		"Back Attack",
		"Back Move",
		"Gold",
	};
	private static void printHorseInfo(PrintStream stream,GameData data) {
		stream.println("*** HORSES ***");
		printLine(stream,HORSE_INFO);
		GamePool pool = new GamePool(data.getGameObjects());
		Collection<GameObject> horses = pool.find("!native,horse");
		String[] line = new String[HORSE_INFO.length];
		for (GameObject horse : horses) {
			SteedChitComponent rc = (SteedChitComponent)RealmComponent.getRealmComponent(horse);
			line[0] = horse.getName();
			line[1] = rc.getVulnerability().toString();
			line[2] = rc.isArmored()?"Yes":"";
			line[3] = rc.getTrotStrength().toString();
			line[4] = String.valueOf(rc.getTrotSpeed().getNum());
			line[5] = rc.getGallopStrength().toString();
			line[6] = String.valueOf(rc.getGallopSpeed().getNum());
			line[7] = horse.getThisAttribute("base_price");
			printLine(stream,line);
		}
		stream.println("---------------------");
	}
	private static final String[] WEAPON_INFO = {
		"Name",
		"Weight",
		"Length",
		"Unalerted",
		"Alerted",
		"Gold",
	};
	private static void printWeaponInfo(PrintStream stream,GameData data) {
		stream.println("*** WEAPONS ***");
		printLine(stream,WEAPON_INFO);
		GamePool pool = new GamePool(data.getGameObjects());
		Collection<GameObject> weapons = pool.find("weapon,!character");
		String[] line = new String[WEAPON_INFO.length];
		for (GameObject weapon : weapons) {
			WeaponChitComponent rc = (WeaponChitComponent)RealmComponent.getRealmComponent(weapon);
			line[0] = weapon.getName();
			line[1] = rc.getWeight().toString();
			line[2] = String.valueOf(rc.getLength());
			rc.setLightSideUp();
			line[3] = rc.getAttackString();
			rc.setDarkSideUp();
			line[4] = rc.getAttackString();
			line[5] = weapon.getThisAttribute("base_price");
			printLine(stream,line);
		}
		stream.println("---------------------");
	}
	private static final String[] ARMOR_INFO = {
		"Name",
		"Weight/Vul",
		"Fame",
		"Notoriety",
		"Gold Intact",
		"Gold Damaged",
		"Gold Destroyed",
	};
	private static void printArmorInfo(PrintStream stream,GameData data) {
		stream.println("*** ARMOR ***");
		printLine(stream,ARMOR_INFO);
		GamePool pool = new GamePool(data.getGameObjects());
		Collection<GameObject> armors = pool.find("armor,!character,!treasure");
		String[] line = new String[ARMOR_INFO.length];
		for (GameObject armor : armors) {
			ArmorChitComponent rc = (ArmorChitComponent)RealmComponent.getRealmComponent(armor);
			line[0] = armor.getName();
			line[1] = rc.getVulnerability().toString();
			line[2] = armor.getThisAttribute("fame");
			line[3] = armor.getThisAttribute("notoriety");
			line[4] = armor.getAttribute("intact","base_price");
			line[5] = armor.getAttribute("damaged","base_price");
			line[6] = armor.getAttribute("destroyed","base_price");
			printLine(stream,line);
		}
		stream.println("---------------------");
	}
	private static final String[] TREASURE_INFO = {
		"Expansion",
		"Name",
		"TWT",
		"Great",
		"Size",
		"Weight",
		"Fame",
		"Notoriety",
		"Price",
	};
	private static void printTreasureInfo(PrintStream stream,GameData data) {
		stream.println("*** TREASURE ***");
		printLine(stream,TREASURE_INFO);
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<GameObject> treasures = pool.find("treasure");
		Collections.sort(treasures,new Comparator<GameObject>() {
			public int compare(GameObject go1,GameObject go2) {
				int ret = 0;
				ret = go1.getName().compareTo(go2.getName());
				return ret;
			}
		});
		String[] line = new String[TREASURE_INFO.length];
		for (GameObject treasure:treasures) {
			TreasureCardComponent rc = (TreasureCardComponent)RealmComponent.getRealmComponent(treasure);
			String newTreasure = (rc.getGameObject().hasThisAttribute("rw_expansion_1") || rc.getGameObject().hasThisAttribute("super_realm"))
								&&!rc.getGameObject().hasThisAttribute("original_game")?"X1":"";
			line[0] = newTreasure;
			line[1] = treasure.getName();
			line[2] = treasure.hasThisAttribute("treasure_within_treasure")?"twt":"";
			line[3] = treasure.hasThisAttribute("great")?"great":"";
			line[4] = treasure.getThisAttribute("treasure");
			line[5] = treasure.getThisAttribute("weight");
			line[6] = treasure.getThisAttribute("fame");
			line[7] = treasure.getThisAttribute("notoriety");
			line[8] = treasure.getThisAttribute("base_price");
			printLine(stream,line);
		}
		stream.println("---------------------");
	}
	public static void main(String[]args) {
		ArgumentParser ap = new ArgumentParser(args);
		String path = ap.getValueForKey("path");
		PrintStream dump = null;
		try {
			if (path!=null) {
				File file = new File(path);
				dump = new PrintStream(new FileOutputStream(file));
			}
			else {
				dump = new PrintStream(System.out);
			}
			RealmLoader loader = new RealmLoader();
			GameData data = loader.getData();
			HostPrefWrapper.createDefaultHostPrefs(data);
			System.out.println("Writing file...");
			printTreasureInfo(dump,data);
			printMonsterInfo(dump,data);
			printNativeInfo(dump,data);
			printNativeHorseInfo(dump,data);
			printHorseInfo(dump,data);
			printWeaponInfo(dump,data);
			printArmorInfo(dump,data);
			dump.close();
			System.out.println("Done");
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
}