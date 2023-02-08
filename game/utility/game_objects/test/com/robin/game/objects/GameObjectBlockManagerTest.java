package com.robin.game.objects;

import org.junit.*;

public class GameObjectBlockManagerTest {
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
	
	private GameObject createTestGameObject() {
		GameObject go = GameObject.createEmptyGameObject();
		go.setThisAttribute("foo",1);
		go.setThisAttribute("asdf",2);
		go.setThisAttribute("qwer",4);
		go.setThisAttribute("zxcv","finder");
		go.setAttribute("spam","hand","green");
		go.setAttribute("rrewm","cool");
		go.setAttribute("spam","tire","blue");
		return go;
	}

	@Test
	public void testBlockStore() {
		GameObject main = GameObject.createEmptyGameObject();
		GameObjectBlockManager man = new GameObjectBlockManager(main);
		GameObject two = createTestGameObject();
		man.storeGameObjectInBlocks(two,"test");
		GameObject stored = man.extractGameObjectFromBlocks("test",true,true);
		two._outputDetail();
		stored._outputDetail();
		Assert.assertTrue(stored.allAttributesMatch(two));
		main._outputDetail();
		man.clearBlocks("test");
		main._outputDetail();
	}
}