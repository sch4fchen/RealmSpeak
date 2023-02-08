package com.robin.magic_realm.components.utility;

import junit.framework.Assert;

import org.junit.Test;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.ArmorChitComponent;
import com.robin.magic_realm.components.TestBaseWithLoader;
import com.robin.magic_realm.components.swing.RealmLogWindow;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class TreasureUtilityTest extends TestBaseWithLoader {
	@Test
	public void testDestroyedArmor() {
		GameObject steve = findGameObject("Amazon");
		CharacterWrapper character = new CharacterWrapper(steve);
		GameObject helmet = findGameObject("Helmet");
		ArmorChitComponent armor = new ArmorChitComponent(helmet);
		armor.setIntact(false);
		TreasureUtility.handleDestroyedItem(character,helmet);
		Assert.assertFalse(armor.isDamaged());
		System.out.println("testDestroyedArmor() logging output:");
		System.out.println(RealmLogWindow.getSingleton().toString());
	}
	
	@Test
	public void testDestroyedArmor_BadReturnDwelling() {
		GameObject steve = findGameObject("Amazon");
		CharacterWrapper character = new CharacterWrapper(steve);
		GameObject helmet = findGameObject("Helmet");
		helmet.setThisAttribute(Constants.ARMOR_RETURN_DWELLING,"Foobar");
		ArmorChitComponent armor = new ArmorChitComponent(helmet);
		armor.setIntact(false);
		TreasureUtility.handleDestroyedItem(character,helmet);
		Assert.assertFalse(armor.isDamaged());
		System.out.println("testDestroyedArmor_BadReturnDwelling() logging output:");
		System.out.println(RealmLogWindow.getSingleton().toString());
	}
}