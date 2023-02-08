package com.robin.magic_realm.components;

//import static org.junit.Assert.*;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.junit.*;

import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.GameWrapper;
import com.robin.magic_realm.components.wrapper.HostPrefWrapper;

public abstract class TestBaseWithLoader {

	@BeforeClass
	public static void oneTimeSetUp() {
	}

	@AfterClass
	public static void oneTimeTearDown() {
	}

	private RealmLoader loader;
	protected HostPrefWrapper hostPrefs;
	protected JFrame parentFrame;
	protected GameWrapper game;
	
	/**
	 * Sets up the test fixture. (Called before every test case method.)
	 */
	@Before
	public void setUp() {
		loader = new RealmLoader();
		ArrayList<String> keyVals = new ArrayList<>();
		keyVals.add("original_game");
		loader.getData().doSetup("standard_game",keyVals);
		hostPrefs = HostPrefWrapper.createDefaultHostPrefs(loader.getData());
		hostPrefs.setStartingSeason("No Seasons");
		game = GameWrapper.findGame(loader.getData());
		parentFrame = new JFrame();
	}

	/**
	 * Tears down the test fixture. (Called after every test case method.)
	 */
	@After
	public void tearDown() {
		RealmUtility.resetGame();
		loader = null;
	}
	
	protected ArrayList<GameObject> findGameObjects(ArrayList<String> query) {
		GamePool pool = new GamePool(loader.getData().getGameObjects());
		return pool.find(query);
	}

	protected GameObject findGameObject(String name) {
		return findGameObject(name,false);
	}
	protected GameObject findGameObject(String name,boolean activated) {
		GameObject go = loader.getData().getGameObjectByName(name);
		if (activated) {
			go.setThisAttribute(Constants.ACTIVATED);
		}
		return go;
	}
	protected void putGameObjectInClearing(GameObject go,String tileName,int clearingNum) {
		GameObject tile = findGameObject(tileName);
		tile.add(go);
		go.setThisAttribute("clearing",clearingNum);
	}
}