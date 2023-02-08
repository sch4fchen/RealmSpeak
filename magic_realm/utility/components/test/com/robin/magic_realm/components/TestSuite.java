package com.robin.magic_realm.components;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.robin.game.objects.GameObjectBlockManagerTest;
import com.robin.game.objects.GameObjectTest;
import com.robin.game.objects.GameQueryTest;
import com.robin.magic_realm.components.attribute.ColorModTest;
import com.robin.magic_realm.components.attribute.DevelopmentProgressTest;
import com.robin.magic_realm.components.quest.requirement.QuestRequirementParamsTest;
import com.robin.magic_realm.components.quest.requirement.QuestRequirementPathTest;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.CharacterWrapper_WeightTest;
import com.robin.magic_realm.components.wrapper.SpellWrapper_DieModTest;

@RunWith(Suite.class)
@SuiteClasses(
	{
		GameQueryTest.class,
		GameObjectTest.class,
		GameObjectBlockManagerTest.class,
		ColorModTest.class,
		DevelopmentProgressTest.class,
		
		DieRuleTest.class,
		//RealmUtilityTest.class,
		TreasureUtilityTest.class,
		
		CharacterWrapper_WeightTest.class,
		SpellWrapper_DieModTest.class,
		
		QuestRequirementParamsTest.class,
		QuestRequirementPathTest.class,
	}
)
public class TestSuite {
	// I guess there's nothing really needed here...
}