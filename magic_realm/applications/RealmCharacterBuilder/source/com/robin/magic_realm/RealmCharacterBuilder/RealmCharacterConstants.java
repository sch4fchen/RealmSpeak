package com.robin.magic_realm.RealmCharacterBuilder;

import com.robin.magic_realm.components.utility.Constants;

public class RealmCharacterConstants {
	
	public static String CUSTOM_ICON_BASE_PATH = "custom/";
	
	public static String[] CHIT_TYPES = {
		"MOVE",
		"FIGHT",
		"MAGIC",
		"FLY",
		"SPECIAL",
	};
	
	public static String[] MOVE_POSITIONS = {
		"CHARGE",
		"DODGE",
		"DUCK",
	};
	
	public static String[] FIGHT_POSITIONS = {
		"THRUST",
		"SWING",
		"SMASH",
	};
	
	public static String[] ONOFF = {
		"OFF",
		"ON",
	};
	
	public static String[] YESNO = {
		"NO",
		"YES",
	};
	
	public static String[][] DEFAULT_RELATIONSHIPS = {
		{Constants.GAME_RELATIONSHIP,"NBashkars"},
		{Constants.GAME_RELATIONSHIP,"NCompany"},
		{Constants.GAME_RELATIONSHIP,"NGuard"},
		{Constants.GAME_RELATIONSHIP,"NLancers"},
		{Constants.GAME_RELATIONSHIP,"NOrder"},
		{Constants.GAME_RELATIONSHIP,"NPatrol"},
		{Constants.GAME_RELATIONSHIP,"NRogues"},
		{Constants.GAME_RELATIONSHIP,"NSoldiers"},
		{Constants.GAME_RELATIONSHIP,"NWoodfolk"},
		{Constants.GAME_RELATIONSHIP,"VCrone"},
		{Constants.GAME_RELATIONSHIP,"VScholar"},
		{Constants.GAME_RELATIONSHIP,"VShaman"},
		{Constants.GAME_RELATIONSHIP,"VWarlock"},
		{Constants.GAME_RELATIONSHIP,"NDragonmen"},
		{Constants.GAME_RELATIONSHIP,"NMurker"},
		{Constants.GAME_RELATIONSHIP,"NAborigines"},
		{Constants.GAME_RELATIONSHIP,"NBandits"},
		{Constants.GAME_RELATIONSHIP,"NConjurors"},
		{Constants.GAME_RELATIONSHIP,"NCoven"},
		{Constants.GAME_RELATIONSHIP,"NCultists"},
		{Constants.GAME_RELATIONSHIP,"NDwarves"},
		{Constants.GAME_RELATIONSHIP,"NElves"},
		{Constants.GAME_RELATIONSHIP,"NEnchanters"},
		{Constants.GAME_RELATIONSHIP,"NHunters"},
		{Constants.GAME_RELATIONSHIP,"NMages"},
		{Constants.GAME_RELATIONSHIP,"NMercenaries"},
		{Constants.GAME_RELATIONSHIP,"NPagans"},
		{Constants.GAME_RELATIONSHIP,"NPeasants"},
		{Constants.GAME_RELATIONSHIP,"NSummoners"},
		{Constants.GAME_RELATIONSHIP,"NZealots"},
	};
	
	public static String[] SPELL_COUNT = {
		"0",
		"1",
		"2",
		"3",
		"4",
		"5",
		"6",
	};
	
	public static String[] SPEEDS = {
		"1",
		"2",
		"3",
		"4",
		"5",
		"6",
		"7",
		"8",
	};
	public static String[] SPEEDS_W_NEG = {
		" ",
		"1",
		"2",
		"3",
		"4",
		"5",
		"6",
		"7",
		"8",
	};
	
	public static String[] STRENGTHS = {
		"L",
		"M",
		"H",
		"T",
	};
	
	public static String[] STRENGTHS_PLUS_NEG = {
		"",
		"L",
		"M",
		"H",
		"T",
	};
	
	public static String[] MAGICS = {
		"I",
		"II",
		"III",
		"IV",
		"V",
		"VI",
		"VII",
		"VIII",
	};
	
	public static String[] EFFORTS = {
		"",
		"*",
		"**",
		"***",
	};
	public static String[] SHARPNESS = {
		" ",
		"1",
		"2",
		"3",
	};
	public static final String[] STARTING_LOCATION_OPTION = {		
			"None",
			"Crone",
			"Lancer Dwelling",
			"Woodfolk Dwelling",
			
			"Inn",
			"Guard",
			"Bashkar Dwelling",
			"Dragonmen Dwelling",
			
			"House",
			"Company Dwelling",
			"Murker Dwelling",
			"Warlock",
			
			"Chapel",
			"Patrol Dwelling",
			"Scholar",
			"Shaman",
	};
}