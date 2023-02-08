package com.robin.magic_realm.components.quest.requirement;

import org.junit.*;

public class QuestRequirementPathTest {
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
	public void testTestPath() { // CV5 PW5 EV4 CV4 CV1 CL4 CL6 P:CL1&CL6 CL1 R1 R2 HP2 HP4 HP1 P:HP4&HP1 HP4 HP1 HP5
		Assert.assertEquals(true,QuestRequirementPath.testPath("CV1 CV2 CV3 CV4 CV5","CV1 CV2 CV3 CV4 CV5"));
		Assert.assertEquals(false,QuestRequirementPath.testPath("CV1 CV2 CV3 CV4","CV1 CV2 CV3 CV4 CV5"));
		Assert.assertEquals(true,QuestRequirementPath.testPath("CV1 CV2 CV3 CV2 CV3 CV4 CV5","CV1 CV2 CV3 CV4 CV5"));
		Assert.assertEquals(false,QuestRequirementPath.testPath("CV1 CV2 CV3 CV2 CV4 CV5","CV1 CV2 CV3 CV4 CV5"));
		Assert.assertEquals(true,QuestRequirementPath.testPath("CV1 CV2 CV3 CV4 CV3 CV2 CV3 CV4 CV5","CV1 CV2 CV3 CV4 CV5"));
		Assert.assertEquals(true,QuestRequirementPath.testPath("CV1 CV2 CV3 P:CV2&CV3 CV2 CV3 CV4 CV5",""));
	}
}