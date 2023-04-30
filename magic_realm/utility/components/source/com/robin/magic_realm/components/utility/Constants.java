package com.robin.magic_realm.components.utility;

import java.awt.*;

public class Constants {
	public static final String ORIGINAL_GAME = "original_game";
	public static final String ALT_MONSTERS1_GAME = "alt_monsters1_game";
	public static final String SUPER_REALM = "super_realm";
	public static final String MULTI_BOARD_APPENDS = "BCDEFGHIJKLMNOP"; // WAY more than enough
	public static final String SETUP = "setup"; // identifies the original object that contained this item at the beginning of the game.
	public static final String ANCHOR_TILE = "anchor_tile";
	public static final String DATA_NAME_COMBAT_FRAME = "CombatFrame";
	
	public static final Font HOTSPOT_FONT = new Font("Dialog",Font.BOLD|Font.ITALIC,12);
	public static final Font RESULT_FONT = new Font("Dialog",Font.BOLD,12);
	public static final Font VUL_FONT = new Font("Dialog",Font.BOLD,16);
	public static final Font ATTRIBUTE_FONT = new Font("Dialog",Font.BOLD,11);
	public static final Font FORTRESS_FONT = new Font("Dialog",Font.BOLD,24);
	public static final int COMBAT_SIDEBAR_WIDTH = 275;
	
	public static final int TERM_OF_HIRE = 14;
	public static final int TEN_YEARS = 3360;
	public static final int MAX_SPELL_COUNT = 14;
	public static final int MAX_LEVEL = 4;
	public static final int MAX_EXP_LEVEL = 11;
	public static final int STAGES_PER_LEVEL = 3;
	public static final int MINIMUM_VPS = 2;
	
	public static final String DIE_ROLL_LOG = "_die_roll_log_";
	
	public static final String APPLICATION_NAME = "Realm Speak";
	public static final String REALM_SPEAK_VERSION = "1.1.7.8";
	public static final String REALM_SPEAK_IMAGES_VERSION = "1.1.7.5";
	
	public static final String PLAYER_TO_PLACE = "p_2_p";
	public static final String PLAYER_TO_PLACE_NEXT = "p_2_p_n";
	public static final String PLACEABLE = "_p_lace";
	
	// Special Broadcasts
	public static final String BROADCAST_SPECIAL_ACTION = "_SPECIAL_ACT_";
	public static final String MESSAGE_RESTART_MAP_BUILDER = "_restart_mapb_";
	public static final String MESSAGE_REFRESH_TILE_PICKER = "_refresh_tilp_";
	public static final String MESSAGE_REPLOT_MAP = "_replotmap_";
	
	// Summary Broadcasts
	public static final String BROADCAST_SUMMARY_ACTION = "_SUMM_ACT_";
	
	public static final String BROADCAST_PRIVATE_MESSAGE = "_PRIVATE_ACT_";
	
	public static final String BROADCAST_CHAT = "_CHATTY_"; 
	public static final String BROADCAST_ATTENTION = "_ATTEN_HUT_"; 
	
	// Combat Status
	public static final int COMBAT_WAIT = 1000;
	
	/*
	 * Prebattle is used to give a chance to roll for battling natives
	 */
	public static final int COMBAT_PREBATTLE = 1;
	
	/*
	 * Each participant can lure (denizens=1, chars=any) by selecting denizens and clicking lure hotspot
	 * GOOD for 3rd
	 */
	public static final int COMBAT_LURE = 2;
	
	/*
	 * This stage is a placeholder for the condition where hirelings are moving sans their employer.  During random
	 * assignment, the character still rolls, but if he/she "wins" the roll, he/she must select a hireling to
	 * take the denizen.
	 */
	public static final int COMBAT_RANDOM_ASSIGN = 3;
	/*
	 * Each hireling can be deployed.  Characters can charge
	 */
	public static final int COMBAT_DEPLOY = 4;
	/*
	 * All encounter actions are presented to each character
	 * GOOD for 3rd
	 */
	public static final int COMBAT_ACTIONS = 5;
	/*
	 * Assign targets by clicking monster pile on any sheet
	 */
	public static final int COMBAT_ASSIGN = 6;
	/*
	 * Play Attack/Maneuver
	 * Position monsters by placing in red boxes
	 */
	public static final int COMBAT_POSITIONING = 7;
	/*
	 * Characters only - replay fight/move depending on treasure
	 * GOOD for 3rd
	 */
	public static final int COMBAT_TACTICS = 8;
	/*
	 * No interaction - just a display
	 * GOOD for 3rd
	 */
	public static final int COMBAT_RESOLVING = 9; // this happens all at once
	/*
	 * Characters only - fatigue/wound chits
	 * GOOD for 3rd
	 */
	public static final int COMBAT_FATIGUE = 10;
	/*
	 * No interaction or display - happens on host end
	 * GOOD for 3rd
	 */
	public static final int COMBAT_DISENGAGE = 11;// this happens all at once
	
	public static final int COMBAT_DONE = 100;
	
	// Used to designate a block point in the character actions
	public static final String BLOCKED = "BLOCKED";
	
	public static final String BOARD_NUMBER = "_boardnm";
	public static final String BOARD_NUMBER_REPLACE_PATTERN = "#bn#";
	
	// Used by RealmComponent ThisAttributes
	public static final String ICON_TYPE = "icon_type";
	public static final String ICON_SIZE = "icon_size";
	public static final String ICON_Y_OFFSET = "icon_y_offset";
	public static final String ICON_TYPE_RIDER = "icon_type_rider";
	public static final String ICON_FOLDER = "icon_folder";
	public static final String SUMMONED_TODAY = "stdy_";// a flag to indicate a chit has summoned for today
	public static final String ALWAYS_VISIBLE = "alwaysVisible";
	public static final String USED_SPELL = "usedSpell";
	public static final String USED_MAGIC_TYPE_LIST = "usedMtList";
	public static final String CHIT_EARNED = "chitEarned";
	public static final String FACING_KEY = "facing";
	public static final String GOLD_SPECIAL_PLACED = "placed";
	public static final String REQUIRES_APPROVAL = "req_app";
	public static final String DISCOVERY = "discovery";
	public static final String DISCOVERED = "discovered";
	public static final String GLIMPSED_COUNTERS = "glimpsedCounters";
	public static final String VULNERABILITY = "vulnerability";
	
	// Used by dwellings
	public static final String NO_SHELTER = "no_shelter";
	
	// Used by items
	public static final String TWO_HANDED = "two_handed";
	public static final String SHIELD = "shield";
	public static final String THROWABLE = "throwable";
	
	// ThisAttributes for DieRoller - The accompaning value lists tables that are affected
	public static final String PLUS_ONE = "plus_one";
	public static final String DIEMOD = "diemod";
	
	public static final String CUSTOM_CHARACTER = "custom_character";
	public static final String WEAPON_START_LOCATION = "weapon_start";
	public static final String ARMOR_START_LOCATION = "armor_start";
	public static final String CHARACTER_POTRAIT_FILE = "potrait_file";
	public static final String CHARACTER_POTRAIT_FOLDER = "potrait_folder";
	
	// Other character advantages (ThisAttributes)
	public static final String PEACE_WITH_NATURE = "peace_with_nature";
	public static final String MAGIC_SIGHT = "magic_sight";
	public static final String KNOWS_ROADS = "knows_roads";
	public static final String USE_FAMILIAR = "use_familiar";
	public static final String ACTIVE_FAMILIAR = "active_familiar";
	public static final String CHOOSE_TURN = "choose_turn";
	public static final String EXTRA_DWELLING_PHASE = "extra_dwelling_phase";
	public static final String NO_SUNLIGHT = "no_sunlight";
	public static final String EXTRA_ACTIONS = "extra_actions";
	public static final String EXTRA_ACTIONS_CLEARING = "extra_actions_clearing"; // gives extra actions just by being in the same clearing
	public static final String REST_DOUBLE = "rest_double";
	public static final String ENHANCED_VULNERABILITY = "enhanced_vul";
	public static final String EXTRA_PHASE = "extra_phase";
	
	// Optional character advantages (ThisAttributes)
	public static final String OPTIONAL_BLOCK = "optional";
	public static final String DRUID_LULL = "druid_lull";
	public static final String NO_MAGIC_FATIGUE = "no_magic_fatigue";
	public static final String LIGHT_GREAT = "light_great";
	public static final String UNPLAYABLE = "unplayable";
	public static final String NO_SPX = "no_spx";
	public static final String CURSE_IMMUNITY = "curse_immunity";
	
	// New character advantages
	public static final String LEVEL_KEY_TAG = "_lkt_";
	public static final String MOUNTAIN_MOVE_ADJ = "mountain_move_adj";
	public static final String WATER_MOVE_ADJ = "water_move_adj";
	public static final String NONCAVE_MOVE_DISADVANTAGE = "noncave_move_disadavantage";
	public static final String FIGHT_NO_WEAPON = "fight_no_weapon"; // allows your FIGHT chits to affect harm at shown strength without a weapon (no sharpness)
	public static final String BLOCK_NO_WEAPON = "block_no_weapon"; // allows your FIGHT chits to block
	public static final String PICKS_LOCKS = "picks_locks";
	public static final String STARTING_GOLD = "start_gold"; // if absent, assume the default of 10
	public static final String MAXIMUM_GOLD = "max_gold"; // if absent, assume no limit to the amount of recorded gold
	public static final String ITEM_RESTRICTIONS = "item_restrictions";
	public static final String COMPANION_NAME = "companion_name";
	public static final String WALK_WOODS = "walk_woods";
	public static final String MONSTER_IMMUNITY = "monster_immunity";
	public static final String MONSTER_CONTROL = "monster_control";
	public static final String MONSTER_CONTROL_ENHANCED = "monster_control_enhanced";
	public static final String MONSTER_CONTROL_DURATION = "monster_control_duration";
	public static final String MONSTER_CONTROL_VALIDATE_CONTROL = "monster_control_validate_control";
	public static final String MONSTER_FEAR = "monster_fear";
	public static final String TREASURE_LOCATION_FEAR = "treasure_location_fear";
	public static final String BONUS_CHIT = "bonus_chit";
	public static final String SPECIAL_ACTION = "special_action";
	public static final String BONUS_INVENTORY = "bonus_inv";
	public static final String ARMORED = "armored";
	public static final String ADDS_ARMOR = "adds_armor";
	public static final String TOUGHNESS = "toughness";	
	public static final String VALE_WALKER="vale_walker";
	public static final String SNEAKY = "sneaky";
	public static final String NO_AMBUSH = "no_ambush";
	public static final String STRONG = "strong";
	public static final String DUAL_WIELDING = "dual_wielding";
	public static final String DUAL_WIELDING_HEAVY = "dual_wielding_heavy";
	public static final String DUAL_WIELDING_TWO_HANDED = "dual_wielding_two_handed";
	public static final String DUAL_WIELDING_ALERT = "dual_wielding_alert";
	public static final String THROWING_WEAPONS = "throwing_weapons";
	public static final String PARRY = "parry";
	public static final String PARRY_LIKE_SHIELD = "parry_like_shield";
	public static final String PARRY_MISSILE = "parry_missile";
	public static final String PARRY_WITH_MISSILE = "parry_with_missile";
	public static final String SHARPSHOOTER = "sharpshooter";
	public static final String BATTLE_MAGE = "battle_mage";
	public static final String MAGIC_MOVE = "magic_move";
	public static final String ENHANCED_MAGIC = "enhanced_magic";
	public static final String ENHANCED_ARTIFACTS = "enhanced_artifacts";
	public static final String STAFF_RESTRICTED_SPELLCASTING = "staff_restricted_spellcasting";
	public static final String ADVENTURER = "adventurer";
	public static final String ADVANCED_SHELTERS = "advanced_shelters";
	public static final String NATIVE_FRIENDLY = "native_friendly";
	
	// Relationships
	public static final String BASE_RELATIONSHIP = "relationship";
	public static final String GAME_RELATIONSHIP = "gamerel";
	
	// Spell induced advantages/disadvantages
	public static final String SP_NO_PEER = "sp_no_peer";
	public static final String SP_MOVE_IS_RANDOM = "sp_mv_random";
	public static final String SP_STORMY = "sp_stormy";
	public static final String SP_PEACE = "sp_peace";
	
	// Character ThisAttribute flags
	public static final String DEAD = "_dead_";
	public static final String GONE = "_gone_"; // left the board
	public static final String RANDOM_ASSIGNMENT_WINNER = "_ra_win_";
	
	// Monster/companion/hireling/visitor flags
	public static final String VISITOR = "visitor";
	public static final String CLONED = "cloned";
	public static final String HIRELING = "hireling";
	public static final String COMPANION = "companion";
	public static final String SUMMONED = "summoned";
	
	// Spoils of War needed action
	public static final String SPOILS_ = "spoils_";
	public static final String SPOILS_DONE = "spoils_done";						// A tag to indicate spoils processed already
	public static final String SPOILS_INVENTORY_DROP = "spoils_inv_drop";		// inventory is abandoned in clearing
	public static final String SPOILS_INVENTORY_SETUP = "spoils_inv_setup";		// inventory is put in killers setup box
	public static final String SPOILS_INVENTORY_TAKEN = "spoils_inv_taken";		// inventory is taken by the killer
	public static final String SPOILS_GROUP_INV_DROP = "spoils_group_inv_drop";	// native group's inventory is abandoned in clearing
	public static final String SPOILS_NONE = "spoils_none";
	
	// Treasure ThisAttributes
	public static final String WEIGHT = "weight";
	public static final String TREASURE_NEW = "tr_is_new"; // a flag so that the activate code knows when an item is brand spankin' new
	public static final String TREASURE_SEEN = "treasure_seen";
	public static final String RED_DIE = "red_die";
	public static final String EXTRA_CAVE_PHASE = "extra_cave_phase";
	public static final String NEEDS_OPEN = "needs_open";
	public static final String SEARCH = "search";
	public static final String NO_LOOT = "no_loot";
	public static final String ACTIVATED = "activated";
	public static final String FATIGUE_ASTERISK = "fatigue_asterisk";
	public static final String FATIGUE_TREMENDOUS = "fatigue_t";
	public static final String KEY = "key";
	public static final String CHIT_SPEED = "chit_speed"; // The value indicates three speeds for 0,1,2 asterisks (ie., 5,4,3)
	public static final String CHIT_STRENGTH = "chit_strength"; // The value indicates three strengths for 0,1,2 asterisks (ie., M,H,T)
	public static final String MOUNTAIN_PEER = "mountain_peer";
	public static final String SLEEP = "sleep"; // Flowers of Rest
	public static final String DAYTIME_ACTIONS = "daytime_actions"; // Timeless Jewel, and the spell "Prophecy"
	public static final String CANNOT_MOVE = "cannot_move";
	public static final String ARMOR_RETURN_DWELLING = "ret_dw";
	public static final String NO_SECRET = "nosecret";
	public static final String PLAIN_SIGHT = "plainsight";
	public static final String DROPPED_BY = "droppedby";
	public static final String ENCHANTED_COLOR = "ench_clr";
	public static final String POTION = "potion";
	public static final String VALUABLE = "valuable";
	public static final String MAGIC_PATH = "magic_path";
	public static final String MAGIC_PATH_EFFECT = "magic_path_effect";
	public static final String MAGIC_PATH_AFFECTED_CHARACTER = "magic_path_character";
	public static final String SUMMON_COMPANION = "summon_companion";
	public static final String MAGIC_FOOD = "magic_food";
	
	// Native ThisAttributes
	public static final String HIDDEN = "hidden";
	
	// Denizen
	public static final String NUMBER = "number";
	public static final String SETUP_START_TILE_REQ = "setup_start_tilereq";
	public static final String WEAPON_USE = "weapon_use";
	public static final String WEAPON_USE_CHIT = "weapon_use_chit";
	public static final String NATIVE_NAME = "native_name";
	
	// Other effects
	public static final String BENEVOLENT = "benevolent"; // for spells
	public static final String AUTHOR = "author";
	public static final String PACIFY = "pacify";
	public static final String EFFORT_LIMIT = "effort_limit";
	public static final String STICKS_TO_WEAPON = "sticks_weapon"; // effect "sticks" to the active weapon
	public static final String ALERTED_WEAPON = "alerted_weapon"; // weapon stays alerted throughout combat
	public static final String AFFECTED_WEAPON_ID = "affected_weapon_id";
	public static final String HIT_TIE = "hit_tie"; // weapon hits on undercut OR tie speed
	public static final String ADD_SHARPNESS = "add_sharpness"; // added sharpess to weapon
	public static final String STOP_WOUNDS = "stop_wounds"; // stops wounds when hitting armor
	public static final String IGNORE_ARMOR = "ignore_armor"; // weapon ignores armor when hitting
	public static final String REPLACE_FIGHT = "replace_fight"; // number indicates the speed you must beat (not equal!)
	public static final String REPLACE_MOVE = "replace_move"; // number indicates the speed you must beat (not equal!)
	public static final String CAST_SPELL_ON_INIT = "cast_spell_on_init";
	public static final String WOUNDS_TO_FATIGUE = "wounds_to_fatigue"; // Vial of healing
	public static final String WISH_AND_CURSE = "wish_and_curse";
	public static final String CANCEL_SPELL = "cancel_spell";
	public static final String MAGIC_PROTECTION = "magic_protection";
	public static final String INSTANT_PEER = "instant_peer"; // ie., Talk to Wise Bird
	public static final String COMBAT_HIDE = "combat_hide"; // ie., World Fades
	public static final String BLOWS_TARGET = "blows_target"; // ie., Hurricane Winds
	public static final String LAND_FIRST = "land_first"; // ie., Hurricane Winds
	public static final String ASKDEMON = "askdemon"; // ie., Ask Demon... well duh!!
	public static final String DEMON_Q_DELIM = ":-:-:"; // the thing that separates the player name from the question
	public static final String PONY_LOCK = "pony_lock";
	public static final String MUST_DEACTIVATE = "must_deactivate";
	public static final String REPAIR_ONE = "repair_one";
	public static final String ADD_CHIT = "add_chit";
	public static final String ANY_EFFORT = "any_effort";
	public static final String LOW_DRINK_COST = "low_drink_cost";
	public static final String REMOVE_CURSE = "remove_curse";
	public static final String ENHANCE_SPELL_SHARPNESS = "enh_spell_shrp";
	public static final String RANDOM_TL = "random_tl";
	public static final String COMBINE = "combine";
	public static final String COMBINE_COUNT = "combine_count";
	public static final String IMMUNE_BREATH = "immune_breath";
	public static final String ABSORB_POP = "absorb_pop";
	public static final String CHIT_SPEED_INC = "chit_speed_inc";
	public static final String CHIT_SPEED_MAGIC_BOOST = "chit_speed_magic_boost";
	public static final String MAJOR_WOUND = "major_wound"; // take 4 wounds on activation, no rest allowed
	public static final String HALF_PRICE = "half_price";
	public static final String CONVERT_CHITS = "convert_chits";
	public static final String CURSED ="cursed"; // cannot be deactivated, except at chapel where the item is destroyed.
	public static final String NO_ACTIVATE = "no_activate";
	public static final String COLOR_CAPTURE = "color_capture";
	public static final String BEAST_AWAY = "beast_away";
	public static final String NO_FATIGUE = "no_fatigue";
	public static final String NOPIN = "nopin";
	public static final String DISCOVER_TO_LEAVE = "discover_to_leave";
	public static final String MAP = "map";
	public static final String DAMAGEABLE = "damageable";
	public static final String DAMAGED = "damaged";
	public static final String DESTROYED = "destroyed"; // this happens to the Ghost Armor
	public static final String AUTO_FLEE = "auto_flee";
	public static final String COMBAT_ONLY = "combat_only";
	public static final String NO_HIDE = "no_hide";
	public static final String NO_UNHIDE = "no_unhide";
	public static final String NO_PROWLING = "no_prowling";
	public static final String NO_GATE = "no_gate";
	public static final String COMPANION_FROM_HOLD = "companion_from_hold";
	public static final String REDUCE_WEIGHT = "reduce_weight";
	public static final String MAGIC_CHANGE = "magic_change";
	public static final String FORCED_ENCHANTMENT = "forced_enchant";
	public static final String TELEPORT = "teleport";
	public static final String TREASURE_CHIT = "treasure_chit";
	public static final String NO_UNDEAD = "no_undead";
	public static final String ADVANCEMENT = "advancement";
	public static final String REDUCED_VULNERABILITY = "red_vul";
	public static final String INCREASE_SHARP = "inc_sharp";
	public static final String LOCKPICK = "lockpick";
	public static final String SMALL = "small";
	public static final String MIST_LIKE = "mist_like";
	public static final String REMOVE_CURSES = "remove_curses";
	public static final String STEED_IN_CAVES_AND_WATER = "steed_in_caves_and_water";
	public static final String TRANSMORPH_IMMUNITY = "transmorph_immunity";
	public static final String TRANSMORPH_IMMUNITY_SELF = "transmorph_immunity_self";
	public static final String MAGIC_IMMUNITY = "magic_immunity";
	public static final String MAGIC_COLOR_BONUS = "magic_color_bonus";
	public static final String MAGIC_COLOR_BONUS_ACTIVE = "magic_color_bonus_active";
	public static final String MAGIC_COLOR_BONUS_ARMOR = "magic_color_bonus_armor";
	public static final String MAGIC_COLOR_BONUS_LENGTH = "magic_color_bonus_length";
	public static final String MAGIC_COLOR_BONUS_SHARPNESS = "magic_color_bonus_sharpness";
	public static final String MAGIC_COLOR_BONUS_SPEED = "magic_color_bonus_speed";
	public static final String NO_CHANGE_TACTICS = "no_change_tactics";
	public static final String CHANGE_TACTICS_AFTER_CASTING = "change_tactics_after_casting";
	public static final String ATTACK_AFTER_CASTING = "attack_after_casting";
	public static final String SPELL_TARGETS_SELF = "spell_targets_self";
	public static final String FAST_CASTER = "fast_caster";
	public static final String SPELL_DENIZEN = "spell_denizen";
	public static final String NO_COMBAT = "no_combat";
	public static final String NO_SUMMONING = "no_summoning";
	public static final String SPELL_PRE_BATTLE = "spell_pre_battle";
	
	// New Spells
	public static final String SUMMONING = "summoning";
	public static final String UNDEAD = "undead";
	public static final String NO_WEIGHT = "no_weight";
	public static final String HEAL_CHIT = "heal_chit";
	public static final String SLOWED = "slowed";
	public static final String SHRINK = "shrink";
	public static final String STRONG_MF = "strong_mf";
	public static final String DRAIN = "drain";
	public static final String UNASSIGN = "unassign";
	public static final String NO_WEATHER_FATIGUE = "no_w_fat";
	public static final String NO_TERRAIN_HARM = "no_ter_harm";
	public static final String DISCOVER_ROAD = "discover_road";
	public static final String ANIMATE = "animate";
	public static final String UNDEAD_PREFIX = "Undead ";
	public static final String CHANGE_TO_COMPANION = "ch2companion";
	public static final String COLOR_MOD = "color_mod";
	public static final String MOD_COLOR_SOURCE = "mod_color_source";
	public static final String FINAL_CHIT_SPEED = "final_chit_speed";
	public static final String FINAL_CHIT_STRENGTH = "final_chit_strength";
	public static final String FINAL_CHIT_HARM = "final_chit_harm";
	public static final String STOP_UNDERCUT = "stop_undercut";
	public static final String NO_UNDERCUT = "no_undercut";
	
	public static final String CLEARING_SPELL_EFFECT = "c_spell_effect";
	public static final String BLUNTED = "blunted";
	public static final String BEWILDERED = "bewildered";
	public static final String HEAVIED = "heavied";
	
	public static final String PHASE_CHIT = "phase_chit";
	public static final String PHASE_CHIT_ID = "phaseChitID";
	public static final String PHASE_CHIT_EFFECTS = "phase_chit_effects";
	public static final String EFFECTS = "effects";
	public static final String MAGIC_SHIELD = "magic_shield";
	public static final String MAGIC_SHIELD_ID = "magicShieldId";
	public static final String MAGIC_WEAPON = "magic_weapon";
	public static final String MAGIC_WEAPON_ID = "magicWeaponId";
	public static final String LIGHTED = "lighted";
	public static final String UNEFFECT_AT_MIDNIGHT = "uneffect_at_midnight";
	public static final String ALTERNATIVE_SPELL_EFFECT = "spell_effect";
	public static final String FREEZING = "freezing";
	public static final String FREEZED = "freezed";
	public static final String HOLY_SHIELD = "holy_shield";
	
	// Curses
	public static final String CURSES_NULLIFIED = "curses_nullified";
	public static final String EYEMIST = "Eyemist";
	public static final String SQUEAK = "Squeak";
	public static final String WITHER = "Wither";
	public static final String ILL_HEALTH = "Ill_Health";
	public static final String ASHES = "Ashes";
	public static final String DISGUST = "Disgust";
	public static final String MESMERIZE = "Mesmerize";
	public static final String WEAKENED = "weakened";
	public static final String INTOXICATED = "intoxicated";
	public static final String CALMED = "calmed";
	public static final String DISTRACTED = "distracted";
	public static final String SAPPED = "sapped";
	public static final String TIRED = "tired";
	public static final String WEAKENED_VULNERABILITY = "weakened_vulnerability";
	public static final String NEGATIVE_AURA = "negative_aura";
	
	// Spells
	public static final String SPELL_ID = "spellID";
	public static final String SPELL_AWAKENED = "Awakened"; // Identifies an original spell that is now awakened
	public static final String SPELL_INSTANCE = "Instance"; // Identifies a spell that is recorded by a character
	public static final String ARTIFACT_ENHANCED_MAGIC = "en_magic";
	public static final String ACTIVATED_ITEMS = "ac_items"; // Identifies the items that were active before the transform (Absorb Essence only)
	public static final String AFFECTS_CASTER = "affects_caster";
	public static final String ATTRIBUTE_ADD = "attribute_add"; // Identifies that the spell will add attributes to the "this" block from the "attribute_add" block.
	
	public static final String POWER_OF_THE_PIT = "powerofthepit";
	public static final String CURSE = "curse";
	public static final String FEAR = "fear";
	public static final String WALL_OF_FORCE = "wall_of_force";
	public static final String TRANSFORM = "transform";
	public static final String MELT_INTO_MIST = "meltintomist";
	public static final String SUMMON_DEMON = "summondemon";
	public static final String DEVILS_SPELL = "devilsspell";
	
	// Magic
	public static final String[] MAGIC_COLORS = new String[]{"White","Grey","Gold","Purple","Black"};
	
	// Expansion Generators
	public static final String GENERATOR = "generator";
	public static final String MIN_LARGE_T = "min_large_t";
	public static final String SUSCEPTIBLETO = "susceptibleto";
	public static final String GENERATOR_FLAMED = "generator_flamed";
	
	public static final String GENERATED = "generated"; // for generated monsters
	public static final String GENERATOR_ID = "generatorid";
	public static final String GM_GROW = "gm_grow";
	public static final String GM_SCARE = "gm_scare";
	
	// Quests
	public static final String QUEST = "quest";
	
	// Travelers
	public static final String USED = "used";
	public static final String TRAVELER_TEMPLATE = "traveler_template";
	public static final String TEMPLATE_ASSIGNED = "temp_ass";
	public static final String SPAWNED = "spawned";
	public static final String CAPTURE = "capture";
	public static final String STORE = "store";
	public static final String STORE_MERCHANT = "merchant";
	public static final String STORE_BLACKSMITH = "blacksmith";
	public static final String STORE_GAMBLER = "gambler";
	public static final String STORE_CLERIC = "cleric";
	public static final String STORE_SELLHOLD = "sellhold";
	public static final String STORE_SPELLCAST = "spellcast";
	public static final String STORE_BARD = "bard";
	public static final String PRICES = "prices";
	public static final String ROAD_KNOWLEDGE = "road_knowledge";
	public static final String ROAD_KNOWLEDGE_HIDDEN = "hidden";
	public static final String ROAD_KNOWLEDGE_SECRET = "secret";
	public static final String ALL_GATE = "allgate";
	public static final String HORSE_MOD = "horse_mod";
	public static final String MEETING_MOD = "meeting_mod";
	public static final String SPELL_MOD = "spell_mod";
	public static final String DWELLING_GOLD = "dwelling_gold";
	public static final String ATTACK_SPEED_TARGET = "attack_speed_target";
	public static final String ATTACK_SPEED_NEW = "attack_speed_new";
	public static final String DOPPLEGANGER = "doppleganger";
	public static final String HIRE_WITH_CHIT = "hire_with_chit";
	public static final String ABSORBED_CHITS = "absorbed_chits";
	public static final String RAISE_DEAD = "raise_dead";
	public static final String CANCEL_RECORDED_ACTION = "change_recorded_action";
	
	// Host Prefs
	
	// Advanced Rules
	public static final String ADV_CACHING = "Caching";
	public static final String ADV_DROPPING = "Dropping";
	public static final String ADV_AMBUSHES = "Ambushes";
	public static final String ADV_SERIOUS_WOUNDS = "SeriousWounds";
	public static final String ADV_DRAGON_HEADS = "DragonHeads";
	public static final String ADV_FLYING_ACTIVITIES = "FlyingActivities";
	
	// Optional Rules
	public static final String OPT_STUMBLE = "Stumble";
	public static final String OPT_FUMBLE = "Fumble";
	public static final String OPT_WEATHER = "Weather";
	public static final String OPT_MISSILE = "OptionalMissileTable";
	public static final String OPT_QUIET_MONSTERS = "QuietMonsters";
	public static final String OPT_NO_BATTLE_DIST = "NoBattleDist";
	public static final String OPT_ALERTED_MONSTERS = "AlertedMonsters";
	public static final String OPT_ALERTED_MONSTERS_VARIANT = "AlertedMonstersVariant";
	public static final String OPT_COMMERCE = "Commerce";
	public static final String OPT_GRUDGES = "GrudgesGrats"; // Grudges and Gratitudes
	public static final String OPT_CHAR_ABILITY_WIZARD_MAGIC_CHIT = "OptCharWizardMagicChit";
	public static final String OPT_CHAR_ABILITY_WIZARD_IGNORES_SPX = "OptCharWizardIgnoresSpx";
	public static final String OPT_CHAR_ABILITY_CAPTAIN = "OptCharCaptain";
	public static final String OPT_CHAR_ABILITY_WOODSGIRL = "OptCharWoodsgirl";
	public static final String OPT_CHAR_ABILITY_MAGICIAN = "OptCharMagician";
	public static final String OPT_CHAR_ABILITY_DRUID_SUMMON = "OptCharDruidSummon";
	public static final String OPT_CHAR_ABILITY_DRUID_CURSES = "OptCharDruidCurses";
	public static final String OPT_CHAR_ABILITY_ELF = "OptCharElf";
	public static final String OPT_PENETRATING_ARMOR = "OptPenArmor";
	public static final String OPT_TWO_HANDED_WEAPONS = "OptTwoHandedWeapons";
	public static final String OPT_DUAL_WIELDING = "OptDualWielding";
	public static final String OPT_DUAL_WIELDING_STRONG = "OptDualWieldingStrong";
	public static final String OPT_DUAL_WIELDING_HEAVY = "OptDualWieldingHeavy";
	public static final String OPT_DUAL_WIELDING_TWO_HANDED = "OptDualWieldingTwoHanded";
	public static final String OPT_THROWING_WEAPONS = "OptThrowingWeapons";
	public static final String OPT_PARRY_LIKE_SHIELD = "OptParryLikeShield";
	public static final String OPT_PARRY = "OptParry";
	public static final String OPT_PARRY_MISSILE = "OptParryMissile";
	public static final String OPT_PARRY_WITH_MISSILE = "OptParryWithMissileWeapons";
	public static final String OPT_SR_STEEL_AGAINST_MAGIC = "OptSrSteelAgainsMagic";
	public static final String OPT_RIDING_HORSES = "OptRidingHorses";
	public static final String OPT_AUTOMATIC_ENCHANTING = "OptAutomaticEnchanting";
	public static final String OPT_ENHANCED_MAGIC = "OptEnhancedMagic";
	public static final String OPT_ENHANCED_ARTIFACTS = "OptEnhancedArtifacts";
	public static final String OPT_POWER_OF_THE_PIT_ATTACK = "OptPowerOfThePitAttack";
	public static final String OPT_POWER_OF_THE_PIT_DEMON = "OptPowerOfThePitDemon";
	public static final String OPT_NATIVES_FRIENDLY = "NativeFriendly";
	
	// Revised Rules
	public static final String REV_MISSILE = "RevisedMissileTable";
	public static final String REV_DAMPEN_FAST_SPELLS = "DampenFastSpells";
	
	// Expanding the Realm Rules
	public static final String EXP_DOUBLE_MONSTER_DIE = "DoubleMonsterDie";
	public static final String EXP_MONSTER_DIE_PER_SET = "MonsterDiePerSet";
	public static final String EXP_NO_DWELLING_START = "NoDwellingStart";
	public static final String EXP_DEVELOPMENT = "AllowDevelopment";
	public static final String EXP_DEVELOPMENT_PLUS = "DevelopmentPastFour";
	public static final String EXP_DEV_EXCLUDE_SW = "DevExcludeStartingWorth";
	public static final String EXP_DEV_3RD_REL = "DevEarn3rdRelationships";
	public static final String EXP_CUSTOM_CHARS = "CustomCharacters";
	public static final String EXP_SUDDEN_DEATH = "SuddenDeath";
	public static final String EXP_BOUNTY_POINTS_FOR_DISCOVERIES = "BountyPointsForDiscoeries";
	
	// 3rd edition rules
	public static final String TE_KNIGHT_ADJUSTMENT = "KnightAdjustment";
	public static final String TE_WATCHFUL_NATIVES = "WatchfulNatives";
	public static final String TE_EXTENDED_TREACHERY = "ExtendedTreachery";
	public static final String TE_EXTENDED_GRUDGES = "ExtendedGrudges";
	public static final String TE_BENEVOLENT_SPELLS = "BenevolentSpells";
	
	// 1st edition rules
	public static final String FE_SEARCH_TABLES = "FirstEditionSearchTables";
	public static final String FE_KILLER_CAVES = "KillerCaves";
	public static final String FE_DEADLY_REALM = "DeadlyRealm";
	public static final String FE_STEEL_AGAINST_MAGIC = "SteelAgainstMagic";
	public static final String FE_AMBUSH_END_OF_COMBATROUND = "AmbushRollEndOfCombatround";
	
	// My House Rules
	public static final String HOUSE1_DWARF_ACTION = "DwarfActions";
	public static final String HOUSE1_NO_NEGATIVE_POINTS = "NoNegPoints";
	public static final String HOUSE1_CHIT_REMAIN_FACE_UP = "ChitsFaceUpAlways";
	public static final String HOUSE1_DONT_RECYCLE_CHARACTERS = "DontRecycleCharacters";
	public static final String HOUSE1_ALLOW_BIRDSONG_REARRANGE = "AllowBirdsongRearrange";
	public static final String HOUSE1_FORCE_INN_AFTER_GAMESTART = "ForceInnAfterGameStart";
	public static final String HOUSE1_NO_SECRETS = "NoSecrets";
	public static final String HOUSE1_ALLOW_LEVEL_GAINS_PAST_FOUR = "AllowLevelGainsPastFour";
	
	// Other House Rules
	public static final String HOUSE2_NO_SPELL_LIMIT = "NoSpellLimit";
	public static final String HOUSE2_MONSTER_WEAPON_NOFLIP = "MonsterWeaponNoFlip";
	public static final String HOUSE2_DECLINE_OPPORTUNITY = "DeclineOpportunity";
	public static final String HOUSE2_RED_SPECIAL_SHELTER = "RedSpecialShelter";
	public static final String HOUSE2_REVISED_ENHANCED_MAGIC = "RevisedEnhancedMagic";
	public static final String HOUSE2_NATIVES_REMEMBER_DISCOVERIES = "NativesRememberDiscoveries";
	public static final String HOUSE2_ANY_VPS = "AnyVps";
	public static final String HOUSE2_NO_MISSION_VISITOR_FLIPSIDE = "NoMissionVisitorFlip";
	public static final String HOUSE2_IGNORE_CAMPAIGNS = "NoCampaigns";
	public static final String HOUSE2_CAMPAIGN_DEBT = "CampaignDebt";
	public static final String HOUSE2_DAY_END_TRADING_ON = "DayEndTradingOn";
	public static final String HOUSE2_NO_NATIVES_BATTLING = "NoNativesBattling";
	public static final String HOUSE2_PEACE_WITH_NATURE_SITES = "PeaceWithNatureSites";
	public static final String HOUSE2_MULTIPLE_SUMMONING = "MultipleSummoning";
	
	// MORE House Rules
	public static final String HOUSE3_DWELLING_ARMOR_REPAIR = "DwellingArmorRepair";
	public static final String HOUSE3_SNOW_HIDE_EXCLUDE_CAVES = "SnowHideExCaves";
	public static final String HOUSE3_NO_VP_DEVELOPMENT_RAMP = "NoVpDevRamp";
	public static final String HOUSE3_NO_RESTRICT_VPS_FOR_DEV = "NoRestrictVpsForDev";
	public static final String HOUSE3_SHOW_DISCARED_QUEST = "ShowDiscardedQuestCardName";
	public static final String HOUSE3_NO_EVENTS_AND_ALL_PLAY_QUESTS = "NoEventsAndAllPlayQuests";
	public static final String HOUSE3_NO_SECRET_QUESTS = "NoSecretQuests";
	public static final String HOUSE3_NO_CHARACTER_QUEST_CARDS = "NoCharacterQuestCards";
	public static final String HOUSE3_NO_EVENTS_AND_ALL_PLAY_QUESTS_WITHOUT_ACTIVATION = "NoEventsAndAllPlayQuestsWithoutActivation";
	public static final String HOUSE3_NO_EVENTS_AND_ALL_PLAY_QUESTS_WITH_ACTIVATION = "NoEventsAndAllPlayQuestsWithActivation";
	public static final String HOUSE3_QTR_AND_SR_QUEST_CARDS = "QuestingTheRealmAndSuperRealmQuestCards";
	public static final String HOUSE3_EXCHANGE_QTR_AND_SR_QUEST_CARDS = "ExchangeQuestingTheRealmAndSuperRealmQuestCards";
	public static final String HOUSE3_SMALL_MONSTERS = "SmallMonsters";
	
	// Super Realm
	public static final String SR_DEDUCT_VPS = "DeductVps";
	public static final String SR_END_OF_MONTH_REGENERATION = "EndOfMonthRegeneration";
	public static final String SR_NO_7TH_DAY_REGENERATION = "No7thDayRegeneration";
	public static final String SR_HORSES_REGENERATION = "HorsesRegeneration";
	
	// Random Number Generators
	public static final String RANDOM_R250_521 = "RndR250_521";
	public static final String RANDOM_MERSENNE_TWISTER = "RndMt";
	public static final String RANDOM_ON_THE_FLY = "RndOnTheFly";
	public static final String RANDOM_GEN_FOR_SETUP = "RndGenForSetup";
	
	// Quests
	public static final String QST_BOOK_OF_QUESTS = "BookOfQuests";
	public static final String QST_QUEST_CARDS = "QuestingTheRealm";
	public static final String QST_GUILD_QUESTS = "GuildQuests";
	public static final String QST_SR_QUESTS = "SuperRealmQuests";
	public enum QuestDeckMode {
		QtR,
		BoQ,
		GQ,
		SR
	}
	
	public static final Stroke THICK_STROKE = new BasicStroke(3);
	public static final String TORCH_BEARER = "torch_bearer";
	public static final String BLENDING = "blending";
	public static final String SPIRIT_GUIDE = "spirit_guide";
	
	public static final String CHARACTERS_NAME_PLACEHOLDER = "CHARACTERS_NAME";
	public static final String OUT_OF_GAME = "OUT_OF_GAME";
	public static final String MAP_BUILDING_PRIO = "map_building_prio";
	
	public static final String CACHE_NAME = "'s Cache #";
}