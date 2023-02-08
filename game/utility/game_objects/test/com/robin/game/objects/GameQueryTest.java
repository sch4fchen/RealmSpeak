package com.robin.game.objects;

import java.util.ArrayList;

import org.junit.*;

public class GameQueryTest {
	GameData gameData;
	
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
		gameData = new GameData();
	}

	/**
	 * Tears down the test fixture. (Called after every test case method.)
	 */
	@After
	public void tearDown() {
	}
	
	private GameObject createGameObject(String blockName,String key,String value) {
		GameObject go = gameData.createNewObject();
		go.setAttribute(blockName,key,value);
		return go;
	}

	@Test
	public void testQueryFirstObjectByKey() {
		// SETUP
		ArrayList<GameObject> list = new ArrayList<GameObject>();
		list.add(createGameObject("this","foo","1"));
		list.add(createGameObject("this","bar","1"));
		
		// EXECUTE
		GameQuery query = new GameQuery("this");
		GameObject found = query.firstGameObjectWithKey(list,"bar");
		
		// VERIFY
		Assert.assertEquals(list.get(1),found);
	}

	@Test
	public void testQueryFirstObjectByKeyAndValue() {
		// SETUP
		ArrayList<GameObject> list = new ArrayList<GameObject>();
		list.add(createGameObject("this","bar","1"));
		list.add(createGameObject("this","bar","2"));
		list.add(createGameObject("this","bar","3"));
		
		// EXECUTE
		GameQuery query = new GameQuery("this");
		GameObject found = query.firstGameObjectWithKeyAndValue(list,"bar","2");
		
		// VERIFY
		Assert.assertEquals(list.get(1),found);
	}

	@Test
	public void testQueryAllObjects() {
		// SETUP
		ArrayList<GameObject> list = new ArrayList<GameObject>();
		list.add(createGameObject("this","bar","1"));
		list.add(createGameObject("this","bar","2"));
		list.add(createGameObject("this","bar","2"));
		
		// EXECUTE
		GameQuery query = new GameQuery("this");
		
		// VERIFY
		Assert.assertEquals(0,query.allGameObjectsWithKey(list,"foo").size());
		Assert.assertEquals(3,query.allGameObjectsWithKey(list,"bar").size());
		Assert.assertEquals(2,query.allGameObjectsWithKeyAndValue(list,"bar","2").size());
	}
}