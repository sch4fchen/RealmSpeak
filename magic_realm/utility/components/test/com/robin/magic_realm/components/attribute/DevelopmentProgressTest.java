package com.robin.magic_realm.components.attribute;

import org.junit.*;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.utility.Constants;
import com.robin.magic_realm.components.utility.RealmLoader;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public class DevelopmentProgressTest {
	
	static GameData gameData;
	
	HostPrefWrapper hostPrefs;
	CharacterWrapper character;
	
	@BeforeClass
	public static void oneTimeSetUp() {
		// one-time initialization code
		RealmLoader loader = new RealmLoader();
		gameData = loader.getData();
	}

	@AfterClass
	public static void oneTimeTearDown() {
		// one-time cleanup code
	}
	/**
	 * Sets up the test fixture. (Called before every test case method.)
	 */
	@Before
	public void setUp() {
		hostPrefs = HostPrefWrapper.findHostPrefs(gameData);
		if (hostPrefs==null) {
			hostPrefs = HostPrefWrapper.createDefaultHostPrefs(gameData);
		}
		hostPrefs.setRequiredVPsOff(true);
		hostPrefs.setPref(Constants.EXP_DEVELOPMENT_PLUS,true);
		hostPrefs.setPref(Constants.HOUSE3_NO_VP_DEVELOPMENT_RAMP,false);
		hostPrefs.setPref(Constants.HOUSE3_NO_RESTRICT_VPS_FOR_DEV,false);
		GameObject amazon = gameData.getGameObjectByName("Amazon");
		character = new CharacterWrapper(amazon);
		character.clearPlayerAttributes();
		character.setStartingStage(3);
		character.setCharacterStage(3);
		character.setStartingLevel(1);
		character.setCharacterLevel(1);
		character.setGold(0);
		character.setHighestEarnedVps(0);
	}

	/**
	 * Tears down the test fixture. (Called after every test case method.)
	 */
	@After
	public void tearDown() {
	}
	
	@Test
	public void testDevelopment_NoGold() {
		DevelopmentProgress dp = DevelopmentProgress.createDevelopmentProgress(hostPrefs,character);
		Assert.assertEquals(0,dp.getBaseVps());
		Assert.assertEquals(0,dp.getCurrentVps());
		Assert.assertEquals(0,dp.getBaseStage());
		Assert.assertEquals(3,dp.getCurrentStage());
		Assert.assertEquals(1,dp.getVpsToNextStage());
		Assert.assertEquals(3,dp.getVpsToNextLevel());
	}
	
	@Test
	public void testDevelopment_GoldToAdvanceOneStage() {
		character.setGold(30);
		DevelopmentProgress dp = DevelopmentProgress.createDevelopmentProgress(hostPrefs,character);
		Assert.assertEquals(0,dp.getBaseVps());
		Assert.assertEquals(1,dp.getCurrentVps());
		Assert.assertEquals(0,dp.getBaseStage());
		Assert.assertEquals(4,dp.getCurrentStage());
		Assert.assertEquals(1,dp.getVpsToNextStage());
		Assert.assertEquals(2,dp.getVpsToNextLevel());
	}
	
	@Test
	public void testDevelopment_GoldToAdvanceOneLevel() {
		character.setGold(90);
		DevelopmentProgress dp = DevelopmentProgress.createDevelopmentProgress(hostPrefs,character);
		Assert.assertEquals(3,dp.getBaseVps());
		Assert.assertEquals(3,dp.getCurrentVps());
		Assert.assertEquals(6,dp.getBaseStage());
		Assert.assertEquals(6,dp.getCurrentStage());
		Assert.assertEquals(2,dp.getVpsToNextStage());
		Assert.assertEquals(6,dp.getVpsToNextLevel());
	}
	
	@Test
	public void testDevelopment_GoldToAdvanceOneLevelAndTwoStages() {
		character.setGold(210);
		DevelopmentProgress dp = DevelopmentProgress.createDevelopmentProgress(hostPrefs,character);
		Assert.assertEquals(3,dp.getBaseVps());
		Assert.assertEquals(7,dp.getCurrentVps());
		Assert.assertEquals(6,dp.getBaseStage());
		Assert.assertEquals(8,dp.getCurrentStage());
		Assert.assertEquals(2,dp.getVpsToNextStage());
		Assert.assertEquals(2,dp.getVpsToNextLevel());
	}
	
	@Test
	public void testDevelopment_GoldToAdvanceTwoLevels() {
		character.setGold(270);
		DevelopmentProgress dp = DevelopmentProgress.createDevelopmentProgress(hostPrefs,character);
		Assert.assertEquals(9,dp.getBaseVps());
		Assert.assertEquals(9,dp.getCurrentVps());
		Assert.assertEquals(9,dp.getBaseStage());
		Assert.assertEquals(9,dp.getCurrentStage());
		Assert.assertEquals(3,dp.getVpsToNextStage());
		Assert.assertEquals(9,dp.getVpsToNextLevel());
	}
	
	@Test
	public void testDevelopment_GoldToAdvanceOneLevelAndTwoStages_NoVPLevelRamp() {
		hostPrefs.setPref(Constants.HOUSE3_NO_VP_DEVELOPMENT_RAMP,true);
		character.setGold(150);
		DevelopmentProgress dp = DevelopmentProgress.createDevelopmentProgress(hostPrefs,character);
		Assert.assertEquals(3,dp.getBaseVps());
		Assert.assertEquals(5,dp.getCurrentVps());
		Assert.assertEquals(6,dp.getBaseStage());
		Assert.assertEquals(8,dp.getCurrentStage());
		Assert.assertEquals(1,dp.getVpsToNextStage());
		Assert.assertEquals(1,dp.getVpsToNextLevel());
	}
	
	@Test
	public void testDevelopment_GoldToAdvanceTwoLevels_NoVPLevelRamp() {
		hostPrefs.setPref(Constants.HOUSE3_NO_VP_DEVELOPMENT_RAMP,true);
		character.setGold(180);
		DevelopmentProgress dp = DevelopmentProgress.createDevelopmentProgress(hostPrefs,character);
		Assert.assertEquals(6,dp.getBaseVps());
		Assert.assertEquals(6,dp.getCurrentVps());
		Assert.assertEquals(9,dp.getBaseStage());
		Assert.assertEquals(9,dp.getCurrentStage());
		Assert.assertEquals(1,dp.getVpsToNextStage());
		Assert.assertEquals(3,dp.getVpsToNextLevel());
	}
	
	@Test
	public void testDevelopment_GoldToAdvanceThreeLevels_NoVPLevelRamp() {
		hostPrefs.setPref(Constants.HOUSE3_NO_VP_DEVELOPMENT_RAMP,true);
		character.setGold(270);
		DevelopmentProgress dp = DevelopmentProgress.createDevelopmentProgress(hostPrefs,character);
		Assert.assertEquals(9,dp.getBaseVps());
		Assert.assertEquals(9,dp.getCurrentVps());
		Assert.assertEquals(12,dp.getBaseStage());
		Assert.assertEquals(12,dp.getCurrentStage());
		Assert.assertEquals(1,dp.getVpsToNextStage());
		Assert.assertEquals(3,dp.getVpsToNextLevel());
	}
	
	@Test
	public void testDevelopment_GoldToAdvanceThreeLevels_NoEffectBecauseRestricted() {
		hostPrefs.setRequiredVPsOff(false);
		character.setGold(270);
		DevelopmentProgress dp = DevelopmentProgress.createDevelopmentProgress(hostPrefs,character);
		Assert.assertEquals(0,dp.getBaseVps());
		Assert.assertEquals(0,dp.getCurrentVps());
		Assert.assertEquals(0,dp.getBaseStage());
		Assert.assertEquals(3,dp.getCurrentStage());
		Assert.assertEquals(1,dp.getVpsToNextStage());
		Assert.assertEquals(3,dp.getVpsToNextLevel());
	}
	@Test
	public void testDevelopment_GoldToAdvanceThreeLevels_WorksBecauseRestrictedButHouseRule() {
		hostPrefs.setRequiredVPsOff(false);
		hostPrefs.setPref(Constants.HOUSE3_NO_RESTRICT_VPS_FOR_DEV,true);
		character.setGold(270);
		DevelopmentProgress dp = DevelopmentProgress.createDevelopmentProgress(hostPrefs,character);
		Assert.assertEquals(9,dp.getBaseVps());
		Assert.assertEquals(9,dp.getCurrentVps());
		Assert.assertEquals(9,dp.getBaseStage());
		Assert.assertEquals(9,dp.getCurrentStage());
		Assert.assertEquals(3,dp.getVpsToNextStage());
		Assert.assertEquals(9,dp.getVpsToNextLevel());
	}
}