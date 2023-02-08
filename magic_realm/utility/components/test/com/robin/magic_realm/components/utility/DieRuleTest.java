package com.robin.magic_realm.components.utility;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.*;

@SuppressWarnings("static-method")
public class DieRuleTest extends TestBaseWithLoader {
	
	@Test
	public void testOneDie() {
		DieRule dr = new DieRule(null,"1d:hide:all");
		Assert.assertTrue(dr.isOneDie());
	}

	@Test
	public void testMinusOne() {
		DieRule dr = new DieRule(null,"-1:hide:all");
		Assert.assertTrue(dr.isMinusOne());
	}

	@Test
	public void testPlusOne() {
		DieRule dr = new DieRule(null,"+1:hide:all");
		Assert.assertTrue(dr.isPlusOne());
	}
	
	@Test
	public void testMinusTwo() {
		DieRule dr = new DieRule(null,"-2:peer:all");
		Assert.assertTrue(dr.isMinusTwo());
	}
	
	@Test
	public void testRuinsTileName() {
		GameObject go = findGameObject("Ruins");
		TileComponent tile = (TileComponent)RealmComponent.getRealmComponent(go);
		ArrayList<String> list = tile.getChitDescriptionList();
		DieRule dr = new DieRule(null,"-1:locate:%ruins%");
		Assert.assertTrue(dr.conditionsMet("locate",list));
	}
	@Test
	public void testRuinsChit() {
		ArrayList<String> list = new ArrayList<>();
		list.add("cliff");
		list.add("ruins m");
		list.add("flutter");
		DieRule dr = new DieRule(null,"-1:locate:%ruins%");
		Assert.assertTrue(dr.conditionsMet("locate",list));
		
	}
	@Test
	public void testLostCityChit() {
		ArrayList<String> list = new ArrayList<>();
		list.add("cliff");
		list.add("lost city b");
		list.add("flutter");
		DieRule dr = new DieRule(null,"-1:locate:lost city%");
		Assert.assertTrue(dr.conditionsMet("locate",list));
	}
	@Test
	public void testWoodsTile() {
		GameObject go = findGameObject("Deep Woods");
		TileComponent tile = (TileComponent)RealmComponent.getRealmComponent(go);
		ArrayList<String> list = tile.getChitDescriptionList();
		DieRule dr = new DieRule(null,"-1:locate:% woods");
		Assert.assertTrue(dr.conditionsMet("locate",list));
	}
	@Test
	public void testNotWoods() {
		ArrayList<String> list = new ArrayList<>();
		list.add("cliff");
		list.add("lost city b");
		list.add("flutter");
		list.add("woodsgirl's cache");
		DieRule dr = new DieRule(null,"-1:locate:% woods");
		Assert.assertTrue(!dr.conditionsMet("locate",list));
	}
	@Test
	public void testAllDieModsLists() {
		ArrayList<String> query = new ArrayList<>();
		query.add(Constants.DIEMOD);
		ArrayList<GameObject> dieModObjs = findGameObjects(query);
		for (GameObject go:dieModObjs) {
			for (String blockName : go.getAttributeBlockNames()) {
				if (!go.hasAttribute(blockName,Constants.DIEMOD)) continue;
				go.getAttributeList(blockName,Constants.DIEMOD);
			}
		}
	}
}