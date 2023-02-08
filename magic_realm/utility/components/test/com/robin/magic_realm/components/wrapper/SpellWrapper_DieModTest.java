package com.robin.magic_realm.components.wrapper;

import static org.junit.Assert.*;

import org.junit.Test;

import com.robin.general.swing.DieRoller;
import com.robin.magic_realm.components.TestBaseWithLoader;
import com.robin.magic_realm.components.table.RealmTable;
import com.robin.magic_realm.components.utility.DieRollBuilder;

public class SpellWrapper_DieModTest extends TestBaseWithLoader {
	
	private CharacterWrapper createBewitchedCharacter(String characterName,String spellName) {
		CharacterWrapper caster = new CharacterWrapper(findGameObject("Magician"));
		CharacterWrapper character = new CharacterWrapper(findGameObject(characterName));
		putGameObjectInClearing(character.getGameObject(),"Crag",6);
		SpellWrapper spell = new SpellWrapper(findGameObject(spellName));
		caster.getGameObject().add(spell.getGameObject());
		spell.castSpell(findGameObject("Magician Magic IV3*")); // doesn't matter
		spell.addTarget(hostPrefs,character.getGameObject());
		spell.affectTargets(parentFrame,game,false,null);
		return character;
	}
	
	@Test
	public void testBadLuck() {
		// SETUP
		CharacterWrapper amazon = createBewitchedCharacter("Amazon","Bad Luck");
		DieRollBuilder builder = new DieRollBuilder(parentFrame,amazon,0);
		
		// EXECUTE
		DieRoller hide = builder.createHideRoller();
		
		// VERIFY
		assertEquals(1,hide.getModifier());
	}
	@Test
	public void testIllusion() {
		// SETUP
		CharacterWrapper amazon = createBewitchedCharacter("Amazon","Illusion");
		DieRollBuilder builder = new DieRollBuilder(parentFrame,amazon,0);
		RealmTable locate = RealmTable.locate(parentFrame,amazon.getCurrentLocation().clearing);
		
		// EXECUTE
		DieRoller search = builder.createRoller(locate);
		DieRoller hide = builder.createHideRoller();
		
		// VERIFY
		assertEquals(1,search.getModifier());
		assertEquals(0,hide.getModifier());
	}
	@Test
	public void testElvenSight() {
		// SETUP
		CharacterWrapper amazon = createBewitchedCharacter("Amazon","Elven Sight");
		DieRollBuilder builder = new DieRollBuilder(parentFrame,amazon,0);
		RealmTable locate = RealmTable.locate(parentFrame,amazon.getCurrentLocation().clearing);
		
		// EXECUTE
		DieRoller search = builder.createRoller(locate);
		DieRoller hide = builder.createHideRoller();
		
		// VERIFY
		assertEquals(0,search.getModifier());
		assertEquals(1,search.getNumberOfDice());
		assertEquals(0,hide.getModifier());
		assertEquals(2,hide.getNumberOfDice());
	}
}