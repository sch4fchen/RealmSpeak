package com.robin.game.objects;

import org.junit.*;

public class GameObjectTest {
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
	public void testAllAttributesMatch() {
		GameObject one = GameObject.createEmptyGameObject();
		one.setThisAttribute("foo",1);
		one.setThisAttribute("asdf",2);
		one.setThisAttribute("qwer",4);
		one.setThisAttribute("zxcv","finder");
		one.setAttribute("spam","hand","green");
		one.setAttribute("rrewm","cool");
		one.setAttribute("spam","tire","blue");
		
		GameObject two = GameObject.createEmptyGameObject();
		two.copyAttributesFrom(one);
		
		GameObject three = GameObject.createEmptyGameObject();
		three.copyAttributesFrom(one);
		three.setThisAttribute("different");
		
		Assert.assertTrue(one.allAttributesMatch(two));
		Assert.assertFalse(one.allAttributesMatch(three));
	}
}