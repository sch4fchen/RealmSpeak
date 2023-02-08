package com.robin.magic_realm.components.quest.requirement;

import org.junit.*;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.quest.*;

@SuppressWarnings("static-method")
public class QuestRequirementParamsTest {
	@BeforeClass
	public static void oneTimeSetUp() {
		// one-time initialization code
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
	}

	/**
	 * Tears down the test fixture. (Called after every test case method.)
	 */
	@After
	public void tearDown() {
	}
	
	@Test
	public void testRoundTrip() {
		GameData gameData = new GameData();
		GameObject go1 = gameData.createNewObject();
		go1.setName("targetOfSearch");
		
		QuestRequirementParams qp = new QuestRequirementParams();
		qp.actionName = "action";
		qp.actionType = CharacterActionType.CompleteMissionCampaign;
		qp.dayKey = "foobar";
		qp.dieResult = 2;
		qp.searchHadAnEffect = true;
		qp.searchType = SearchResultType.Awaken;
		qp.targetOfSearch = go1;
		qp.timeOfCall = GamePhaseType.Birdsong;
		for(int i=0;i<5;i++) {
			GameObject go = gameData.createNewObject();
			go.setName("Object "+i);
			qp.objectList.add(go);
		}
		
		String string = qp.asString();
		System.out.println(string);
		QuestRequirementParams back = QuestRequirementParams.valueOf(string,gameData);
		
		Assert.assertEquals(qp.actionName,back.actionName);
		Assert.assertEquals(qp.actionType,back.actionType);
		Assert.assertEquals(qp.dayKey,back.dayKey);
		Assert.assertEquals(qp.dieResult,back.dieResult);
		Assert.assertEquals(qp.searchHadAnEffect,back.searchHadAnEffect);
		Assert.assertEquals(qp.objectList.size(),back.objectList.size());
		Assert.assertEquals(qp.searchType,back.searchType);
		Assert.assertEquals(qp.targetOfSearch.getName(),back.targetOfSearch.getName());
		Assert.assertEquals(qp.timeOfCall,back.timeOfCall);
		
	}
	
	@Test
	public void testMinimalRoundTrip() {
		GameData gameData = new GameData();
		GameObject go1 = gameData.createNewObject();
		go1.setName("targetOfSearch");
		
		QuestRequirementParams qp = new QuestRequirementParams();
		String string = qp.asString();
		System.out.println(string);
		QuestRequirementParams.valueOf(string,gameData);
		// No exceptions
	}
}