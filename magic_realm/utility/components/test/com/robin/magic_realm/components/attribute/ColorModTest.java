package com.robin.magic_realm.components.attribute;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.*;
import com.robin.game.objects.GameObject;

@SuppressWarnings("static-method")
public class ColorModTest {
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
	
	private static GameObject createColorModThing(String modString) {
		GameObject go = GameObject.createEmptyGameObject();
		go.setThisAttribute("color_mod",modString);
		return go;
	}

	@Test
	public void testConvertNoColor() {
		ArrayList<GameObject> things = new ArrayList<>();
		things.add(GameObject.createEmptyGameObject());
		
		ArrayList<ColorMagic> colors = new ArrayList<>();
		colors.add(new ColorMagic(ColorMagic.WHITE,true));
		
		ArrayList<ColorMagic> result = ColorMod.getConvertedColorsForThings(things,colors);
		
		assertEquals(1,result.size());
		assertTrue(result.contains(new ColorMagic(ColorMagic.WHITE,true)));
	}
	
	@Test
	public void testConvertOneColor() {
		ArrayList<GameObject> things = new ArrayList<>();
		things.add(createColorModThing("1.2"));
		
		ArrayList<ColorMagic> colors = new ArrayList<>();
		colors.add(new ColorMagic(ColorMagic.WHITE,true));
		
		ArrayList<ColorMagic> result = ColorMod.getConvertedColorsForThings(things,colors);
		
		assertEquals(1,result.size());
		assertTrue(result.contains(new ColorMagic(ColorMagic.GRAY,true)));
		assertFalse(result.contains(new ColorMagic(ColorMagic.WHITE,true)));
	}
	
	@Test
	public void testConvertMultipleColorsInOne() {
		ArrayList<GameObject> things = new ArrayList<>();
		things.add(createColorModThing("1.2;1.3"));
		
		ArrayList<ColorMagic> colors = new ArrayList<>();
		colors.add(new ColorMagic(ColorMagic.WHITE,true));
		
		ArrayList<ColorMagic> result = ColorMod.getConvertedColorsForThings(things,colors);
		
		assertEquals(2,result.size());
		assertTrue(result.contains(new ColorMagic(ColorMagic.GRAY,true)));
		assertTrue(result.contains(new ColorMagic(ColorMagic.GOLD,true)));
		assertFalse(result.contains(new ColorMagic(ColorMagic.WHITE,true)));
	}
	
	@Test
	public void testConvertOneColorInMultiple() {
		ArrayList<GameObject> things = new ArrayList<>();
		things.add(createColorModThing("1.2"));
		things.add(createColorModThing("1.3"));
		
		ArrayList<ColorMagic> colors = new ArrayList<>();
		colors.add(new ColorMagic(ColorMagic.WHITE,true));
		
		ArrayList<ColorMagic> result = ColorMod.getConvertedColorsForThings(things,colors);
		
		assertEquals(2,result.size());
		assertTrue(result.contains(new ColorMagic(ColorMagic.GRAY,true)));
		assertTrue(result.contains(new ColorMagic(ColorMagic.GOLD,true)));
		assertFalse(result.contains(new ColorMagic(ColorMagic.WHITE,true)));
	}
	
	@Test
	public void testConvertColorSwap() {
		ArrayList<GameObject> things = new ArrayList<>();
		things.add(createColorModThing("1.5"));
		things.add(createColorModThing("5.1"));
		
		ArrayList<ColorMagic> colors = new ArrayList<>();
		colors.add(new ColorMagic(ColorMagic.WHITE,true));
		colors.add(new ColorMagic(ColorMagic.GOLD,true));
		
		ArrayList<ColorMagic> result = ColorMod.getConvertedColorsForThings(things,colors);
		
		assertEquals(2,result.size());
		assertTrue(result.contains(new ColorMagic(ColorMagic.BLACK,true)));
		assertTrue(result.contains(new ColorMagic(ColorMagic.GOLD,true)));
		assertFalse(result.contains(new ColorMagic(ColorMagic.WHITE,true)));
	}
	
	@Test
	public void testAllThreeInPlayWithWhite() {
		ArrayList<GameObject> things = new ArrayList<>();
		things.add(createColorModThing("1.5;4.2"));
		things.add(createColorModThing("1.2;1.3;1.4"));
		things.add(createColorModThing("1.5"));
		
		ArrayList<ColorMagic> colors = new ArrayList<>();
		colors.add(new ColorMagic(ColorMagic.WHITE,true));
		
		ArrayList<ColorMagic> result = ColorMod.getConvertedColorsForThings(things,colors);
		
		assertEquals(4,result.size());
		assertFalse(result.contains(new ColorMagic(ColorMagic.WHITE,true)));
		assertTrue(result.contains(new ColorMagic(ColorMagic.GRAY,true)));
		assertTrue(result.contains(new ColorMagic(ColorMagic.GOLD,true)));
		assertTrue(result.contains(new ColorMagic(ColorMagic.PURPLE,true)));
		assertTrue(result.contains(new ColorMagic(ColorMagic.BLACK,true)));
	}
	@Test
	public void testAllThreeInPlayWithWhiteAndPurple() {
		ArrayList<GameObject> things = new ArrayList<>();
		things.add(createColorModThing("1.5;4.2"));
		things.add(createColorModThing("1.2;1.3;1.4"));
		things.add(createColorModThing("1.5"));
		
		ArrayList<ColorMagic> colors = new ArrayList<>();
		colors.add(new ColorMagic(ColorMagic.WHITE,true));
		colors.add(new ColorMagic(ColorMagic.PURPLE,true));
		
		ArrayList<ColorMagic> result = ColorMod.getConvertedColorsForThings(things,colors);
		
		assertEquals(4,result.size());
		assertFalse(result.contains(new ColorMagic(ColorMagic.WHITE,true)));
		assertTrue(result.contains(new ColorMagic(ColorMagic.GRAY,true)));
		assertTrue(result.contains(new ColorMagic(ColorMagic.GOLD,true)));
		assertTrue(result.contains(new ColorMagic(ColorMagic.PURPLE,true)));
		assertTrue(result.contains(new ColorMagic(ColorMagic.BLACK,true)));
	}
	
	@Test
	public void testAllThreeInPlayWithPurple() {
		ArrayList<GameObject> things = new ArrayList<>();
		things.add(createColorModThing("1.5;4.2"));
		things.add(createColorModThing("1.2;1.3;1.4"));
		things.add(createColorModThing("1.5"));
		
		ArrayList<ColorMagic> colors = new ArrayList<>();
		colors.add(new ColorMagic(ColorMagic.PURPLE,true));
		
		ArrayList<ColorMagic> result = ColorMod.getConvertedColorsForThings(things,colors);
		
		assertEquals(1,result.size());
		assertFalse(result.contains(new ColorMagic(ColorMagic.WHITE,true)));
		assertTrue(result.contains(new ColorMagic(ColorMagic.GRAY,true)));
	}
	
	@Test
	public void testReversePowerWithWhiteAndPurple() {
		ArrayList<GameObject> things = new ArrayList<>();
		things.add(createColorModThing("1.5;4.2"));
		
		ArrayList<ColorMagic> colors = new ArrayList<>();
		colors.add(new ColorMagic(ColorMagic.WHITE,true));
		colors.add(new ColorMagic(ColorMagic.PURPLE,true));
		colors.add(new ColorMagic(ColorMagic.GOLD,true));
		
		ArrayList<ColorMagic> result = ColorMod.getConvertedColorsForThings(things,colors);
		
		assertEquals(3,result.size());
		assertFalse(result.contains(new ColorMagic(ColorMagic.WHITE,true)));
		assertFalse(result.contains(new ColorMagic(ColorMagic.PURPLE,true)));
		assertTrue(result.contains(new ColorMagic(ColorMagic.BLACK,true)));
		assertTrue(result.contains(new ColorMagic(ColorMagic.GRAY,true)));
		assertTrue(result.contains(new ColorMagic(ColorMagic.GOLD,true)));
	}
}