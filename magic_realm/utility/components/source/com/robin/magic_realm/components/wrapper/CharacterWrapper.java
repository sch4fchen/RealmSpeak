package com.robin.magic_realm.components.wrapper;

import java.util.*;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import com.robin.game.objects.*;
import com.robin.game.server.GameClient;
import com.robin.general.swing.*;
import com.robin.general.util.*;
import com.robin.magic_realm.components.*;
import com.robin.magic_realm.components.attribute.*;
import com.robin.magic_realm.components.attribute.DayAction.ActionId;
import com.robin.magic_realm.components.attribute.GuildLevelType.GuildLevel;
import com.robin.magic_realm.components.effect.ISpellEffect;
import com.robin.magic_realm.components.effect.PhaseChitEffectFactory;
import com.robin.magic_realm.components.effect.SpellEffectContext;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.quest.requirement.QuestRequirementParams;
import com.robin.magic_realm.components.swing.CenteredMapView;
import com.robin.magic_realm.components.swing.RealmComponentOptionChooser;
import com.robin.magic_realm.components.swing.TileLocationChooser;
import com.robin.magic_realm.components.utility.*;

/**
 * This class is a wrapper class that will facilitate easy manipulation
 * of character GameObjects - it should also work with hired leaders
 */
public class CharacterWrapper extends GameObjectWrapper {
	
	public static enum ActionState{Pending,Completed,Cancelled,Invalid}
	private static Logger logger = Logger.getLogger(CharacterWrapper.class.getName());

	// PlayerBlock attribute keys
	public static final String MISSING_IN_ACTION = "MIA__";
	public static final String PLAYER_BLOCK = "RS_PB__";
	public static final String NAME_KEY = "plNm__";
	public static final String PASSWORD_KEY = "plPw__";
	public static final String EMAIL_KEY = "plEm__";
	public static final String CHARACTER_LEVEL = "plLv__";
	public static final String CHARACTER_STAGE = "plSt__";
	public static final String STARTING_CHARACTER_STAGE = "SplSt__";
	public static final String CHARACTER_EXTRA_CHIT_MARKER = "plStEcm__";
	public static final String CHARACTER_JOIN_ORDER = "plJnOr__"; // specifies join order (important for choosing mission chits)
	public static final String CHARACTER_CHOOSE_GOLD_SPECIAL = "plChGS__";
	public static final String JUST_UNHIRED = "_jst_unh_";
	public static final String GOLD_KEY = "gol__";
	public static final String NOTORIETY_KEY = "not__";
	public static final String FAME_KEY = "fam__";
	public static final String IS_BLOCKED = "blck__";
	public static final String IS_SLEEP = "sleep__";
	public static final String CURRENT_MONTH = "c_mnth";
	public static final String CURRENT_DAY = "c_day";
	public static final String BASIC_PHASES = "b_phs";
	public static final String SUNLIGHT_PHASES = "s_phs";
	public static final String SHELTERED_PHASES = "sh_phs";
	public static final String MOUNTAIN_MOVE_COST = "mt_mov";
	public static final String MONSTER_ROLL = "m_rlll";
	public static final String DO_RECORD = "d_rec__"; // tells character to record actions
	public static final String PLAY_ORDER = "p_ord__";
	public static final String GAME_OVER = "gm_over__";
	public static final String ACTION_FOLLOWER = "foll_";
	public static final String NO_SUMMON = "_no_sum_"; // Used by FOLLOW logic to determine which followers summon monsters normally (when following as a group, only one summon occurs)
	public static final String MOVE_HISTORY = "_m_hist__"; // A complete list of all clearings visited in order
	public static final String MOVE_HISTORY_JUMP = "_JUMP_"; // An indicator that the player jumped to a new location without "walking" there
	public static final String MOVE_HISTORY_DAY = "_DAY_"; // A marker for the passage of days
	public static final String MOVE_HISTORY_DAY_KEY = "_m_histd__";// daykeys for each move
	public static final String WISH_STRENGTH = "_w_stren_"; // WISH result 6
	public static final String PEER_ANY = "_peer_any_"; // Toadstool Circle Peer any clearing
	public static final String STORMED = "_strmd_"; // Affected by Violent Storm today
	public static final String LOST_PHASES = "_lost_phases_"; // Number of lost phases by Violent Storm today
	public static final String CHEATER = "_CHEATER_"; // A flag that is added when the character uses the cheat commands
	public static final String SPELL_EXTRA_ACTIONS = "_sxa_";
	public static final String SPELL_EXTRA_ACTION_SOURCE = "_sxas_";
	public static final String MINION_ID = "_minion_";
	public static final String TRANSMORPH_ID = "_transmorph_";
	public static final String LAST_PLAYER = "_last_pl";
	public static final String LAST_PREEMPTIVE_PLAYER = "_last_pp";
	public static final String CHARACTER_TYPE = "_pl_ch_ty_"; // Currently only applies to Elf:  great or light
	public static final String CAMPAIGN = "_cmpgn_";
	public static final String CACHE_NUMBER = "_cHcNm_";
	public static final String WEATHER_FATIGUE = "_weathF_";
	public static final String EXTRA_WOUNDS = "_xtWnd_";
	public static final String DO_INSTANT_PEER = "_do_insp_"; // Informs the combat frame to do an instant peer
	public static final String SPELL_CONFLICTS = "_sp_cfct_";
	public static final String STOP_FOLLOWING = "_st_F";
	public static final String NEXT_PENDING_ACTION = "_npa_";
	public static final String DEATH_REASON = "_dxr_";
	public static final String FORTIFIED = "_frtfid_";
	public static final String FORT_DAMAGED = "_frtdmg_";
	public static final String NEEDS_INVENTORY_CHECK = "_invch_";
	public static final String NEEDS_ACTION_PANEL_UPDATE = "_appu_";
	public static final String TREACHERY_PREFERENCE = "_trpr_";
	public static final String CHAT_STYLE = "_chs_";
	public static final String FOLLOW_RESTS = "_fllr_";
	public static final String NEED_QUEST_CHECK = "_qc_";
	public static final String DISCARDED_QUESTS = "_dq_";
	
	public static final String CURRENT_GUILD = "_ccg_";
	public static final String CURRENT_GUILD_LEVEL = "_ccgl_";

	public static final String BLOCKING = "bkkng_"; // indicates the character is blocking everything in the clearing
	public static final String BLOCK_DECISION = "bkkng_dec"; // the blocking character has decided what to do
	
	public static final String COMBAT_STATUS = "cmb_st__";
	public static final String COMBAT_PLAY_ORDER = "cmb_ord__";
	public static final String MELEE_PLAY_ORDER = "mlee_ord__";
	public static final String COMBAT_COUNT = "cmb_cnt__"; // the number of combat clearings left to resolve
	
	public static final String ALL_DAYS = "all_days"; // keeps a record of all the days this character has been playing
	public static final String FOUND_HIDDEN_ENEMIES = "hidEnem__";
	public static final String DISC_TREASURE_LOCATIONS = "dTls__";	// an AttributeList of TLs
	public static final String DISC_HIDDEN_PATHS = "dPath__";		// an AttributeList of Paths
	public static final String DISC_SECRET_PASSAGES = "dPass__";	// an AttributeList of Passages
	public static final String DISC_OTHER = "dOther__";				// an AttributeList of Gates and Guilds
	public static final String STARTING_GOLD_VALUE = "stGold__";	// an indicator of the character's starting value - needed to calculate gold victory requirements
	
	public static final String COMPLETED_MISSIONS = "cMissions__";	// an AttributeList of completed missions
	public static final String COMPLETED_CAMPAIGNS = "cCampaigns__";// an AttributeList of completed campaigns
	public static final String COMPLETED_TASKS = "cTasks__";		// an AttributeList of completed tasks
	public static final String FAILED_MISSIONS = "fMissions__";		// an AttributeList of failed missions
	public static final String FAILED_CAMPAIGNS = "fCampaigns__";	// an AttributeList of failed campaigns
	public static final String FAILED_TASKS = "fTasks__";			// an AttributeList of failed tasks
	
	public static final String STARTING_SPELLS = "sSpells__";		// The spells you start the game with
	public static final String RECORDED_SPELLS = "rSpells__";		// Spells you aquire
	
	public static final String KILL_BLOCK = "kills_b"; // record of all kills
	public static final String SPELL_BLOCK = "spells_b"; // record of all casted spells
	
	public static final String PHASE_CHITS = "phaseChits__";
	
	// Victory Requirements
	public static final String VICTORY_REQ_BLOCK = "VR__";
	public static final String V_QUEST_POINTS = "QP";
	public static final String V_GREAT_TREASURES = "GT";
	public static final String V_USABLE_SPELLS = "US";
	public static final String V_FAME = "F";
	public static final String V_NOTORIETY = "N";
	public static final String V_GOLD = "G";
	public static final String HIGHEST_EARNED_VPS = "_h_evps_";
	
	public static final String V_MONTHLY_VPS = "MVP";
	public static final String V_NEW_VPS = "nVP";
	public static final String V_DEDUCT_VPS = "dVP";
	
	// Quests
	public static final String QUEST_ID = "QSTID";
	public static final String POST_QUEST_PARAMS = "QSTPQP";
	public static final String QUEST_BONUS_VPS = "QSTBONUS";
	
	// Curses
	public static final String CURSES_BLOCK = "CRS__";
	
	// Character Enemies
	public static final String ENEMY_CHARACTER_BLOCK = "FRND_C_"; // a block holding enemy character ids
	
	// Hirelings
	public static final String HIRELING_BLOCK = "HIRE_B_";
	
	// Other
	public static final String WANTS_COMBAT = "WANTS_CMB_";
	public static final String WANTS_DAYEND_TRADING = "WANTS_DET_";
	public static final String IS_DAYEND_TRADING = "IS_DET_";
	public static final String RUN_AWAY_LAST_USED_CHIT = "RUN_AWAY_CHIT";
	
	// Day Actions
	public static final String RELCHANGE_GROUP_LIST = "_relch_gl_";
	
	// an Attribute list of native group names that are battling this evening.
	/*
	 * Each list entry will also contain the roll result string(s)
	 * 
	 * 	rogues[5&4]
	 */
	/*
	 * Block where attributes will be native group names, which hold an attribute list of roller results
	 * (in case there is trouble/insult/challenge)
	 * 
	 * This block should be cleared out at midnight every night
	 */
	public static final String BATTLING_NATIVE_BLOCK = "BATT_N_";
	
	// Contains a list of clearings (ClearingDetail objects) that the character is planning to
	// traverse (includes starting clearing).  This is used to determine move rules and next possible
	// moves when plotting actions.
	private ArrayList<TileLocation> clearingPlot;
	
	public CharacterWrapper(GameObject gameObject) {
		super(gameObject);
	}
	public boolean isSpellCaster() {
		String levelKey = "level_"+getCharacterLevel();
		return getGameObject().hasAttribute(levelKey,"spellcount");
	}
	public boolean isMagicUser() {
		return getGameObject().hasThisAttribute("magicuser");
	}
	public boolean isFighter() {
		return getGameObject().hasThisAttribute("fighter");
	}
	public boolean isFemale() {
		return getGameObject().getThisAttribute("pronoun") == "she";
	}
	public boolean isMale() {
		return getGameObject().getThisAttribute("pronoun") == "he";
	}
	public GenderType getGender() {
		String gender = getGameObject().getThisAttribute("pronoun");
		if (gender == null) {
			return GenderType.Undefined;
		}
		if (gender.matches("she")) {
			return GenderType.Female;
		}
		if (gender.matches("he")) {
			return GenderType.Male;
		}
		return GenderType.Undefined;
	}
	public String toString() {
		return getCharacterName()+" played by "+getPlayerName();
	}
	public void activateCheater() {
		setBoolean(CHEATER,true);
	}
	public void clearCheater() {
		clear(CHEATER);
	}
	public boolean hasCheated() {
		return getBoolean(CHEATER);
	}
	
	public void activateBeginner() {
		getGameObject().setThisAttribute(Constants.DAYTIME_ACTIONS);
	}
	public void deactivateBeginner() {
		getGameObject().removeThisAttribute(Constants.DAYTIME_ACTIONS);
	}
	public boolean isBeginner() {
		return getGameObject().getThisAttribute(Constants.DAYTIME_ACTIONS) != null;
	}
	public void toggleBeginner() {
		if (isBeginner()) {
			deactivateBeginner();
		}
		else {
			activateBeginner();
		}
	}
	
	public String getBlockName() {
		return PLAYER_BLOCK;
	}
	public static boolean hasPlayerBlock(GameObject in) {
		if (in.hasAttributeBlock(PLAYER_BLOCK)) {
			int size = in.getAttributeBlock(PLAYER_BLOCK).size();
			return size>0;
		}
		return false;
	}
	/**
	 * This completely blows away the player attributes.  Take care when calling this method!
	 */
	public void clearPlayerAttributes() {
		clearPlayerAttributes(false);
	}
	public void clearPlayerAttributes(boolean saveDiscoveries) {
		clearAllDays();
		removeAllCurses();
		
		ArrayList<String> tls = makeNewList(getTreasureLocationDiscoveries());
		ArrayList<String> hps = makeNewList(getHiddenPathDiscoveries());
		ArrayList<String> sps = makeNewList(getSecretPassageDiscoveries());
		
		getGameObject().removeAttributeBlock(PLAYER_BLOCK);
		
		if (saveDiscoveries) {
			setList(DISC_TREASURE_LOCATIONS,tls);
			setList(DISC_HIDDEN_PATHS,hps);
			setList(DISC_SECRET_PASSAGES,sps);
		}
		
		getGameObject().removeAttributeBlock(VICTORY_REQ_BLOCK);
	}
	private static ArrayList<String> makeNewList(ArrayList<String> inList) {
		ArrayList<String> list = new ArrayList<>();
		if (inList!=null) {
			list.addAll(inList);
		}
		return list;
	}
	public static Collection<String> getKeyVals() {
		ArrayList<String> keyVals = new ArrayList<>();
		keyVals.add(NAME_KEY);
		return keyVals;
	}
	public boolean isCharacter() {
		return !isHiredLeader() && !isMinion() && !isControlledMonster();
	}
	public boolean isHiredLeader() {
		return getGameObject().hasThisAttribute("native");
	}
	public boolean isControlledMonster() {
		return getGameObject().hasThisAttribute("monster") || getGameObject().hasThisAttribute("animal");
	}
	public boolean isMinion() {
		return isFamiliar() || isPhantasm();
	}
	public boolean isFamiliar() {
		return getGameObject().hasThisAttribute("familiar");
	}
	public boolean isPhantasm() {
		return getGameObject().hasThisAttribute("phantasm");
	}
	public CharacterWrapper getHiringCharacter() {
		if (!isCharacter()) {
			RealmComponent rc = RealmComponent.getRealmComponent(getGameObject());
			if (rc!=null&&rc.getOwner()!=null) {
				return new CharacterWrapper(rc.getOwner().getGameObject());
			}
		}
		return null;
	}
	
	// Getters
	public String getIconPath() {
		String name = getGameObject().getThisAttribute(Constants.ICON_TYPE);
		if (name==null) {
			System.out.println("Why is icon_type null for "+getGameObject().getName()+"?");
		}
		String folder = getGameObject().getThisAttribute(Constants.ICON_FOLDER);
		String path = folder+"/";
		return path+name;
	}
	public ImageIcon getIcon() {
		return ImageCache.getIcon(getIconPath(),20);
	}
	public ImageIcon getMidSizedIcon() {
		return ImageCache.getIcon(getIconPath(),40);
	}
	public ImageIcon getFullSizedIcon() {
		return ImageCache.getIcon(getIconPath());
	}
	public String getCharacterName() {
		return getGameObject().getName();
	}
	public String getPlayerName() {
		return getString(NAME_KEY);
	}
	public String getPlayerPassword() {
		String pw = getString(PASSWORD_KEY);
		if (pw==null) {
			pw = "";
		}
		return pw.trim();
	}
	public boolean hasPlayerPassword() {
		return getPlayerPassword().length()>0;	
	}
	public boolean validPlayerPassword(String val) {
		String pw = getPlayerPassword();
		return getPlayerPassword().length()==0 || pw.equals(val);
	}
	public String getPlayerEmail() {
		return getString(EMAIL_KEY);
	}
	public boolean isBlocked() {
		return getBoolean(IS_BLOCKED);
	}
	public boolean isBlocking() {
		return getBoolean(BLOCKING);
	}
	public boolean hasBlockDecision(GameObject go) {
		ArrayList<String> list = getList(BLOCK_DECISION);
		return list!=null && list.contains(go.getStringId());
	}
	public void removeBlockDecision(GameObject go) {
		ArrayList<String> list = getList(BLOCK_DECISION);
		if (list!=null && list.contains(go.getStringId())) {
			list.remove(go.getStringId());
		}
	}
	public boolean isSleep() {
		return getBoolean(IS_SLEEP);
	}
	public int getCharacterLevel() {
		return getInt(CHARACTER_LEVEL);
	}
	public int getStartingLevel() {
		return getInt("S"+CHARACTER_LEVEL);
	}
	public int getCharacterStage() {
		return getInt(CHARACTER_STAGE);
	}
	public int getCharacterExtraChitMarkers() {
		return getInt(CHARACTER_EXTRA_CHIT_MARKER);
	}
	public int getCharacterJoinOrder() {
		return getInt(CHARACTER_JOIN_ORDER);
	}
	public boolean getNeedsChooseGoldSpecial() {
		return getBoolean(CHARACTER_CHOOSE_GOLD_SPECIAL);
	}
	public boolean isJustUnhired() {
		return getBoolean(JUST_UNHIRED);
	}
	public double getGold() {
		return getDouble(GOLD_KEY);
	}
	public int getRoundedGold() {
		return (int)Math.floor(getGold());
	}
	public double getNotoriety() {
		return getDouble(NOTORIETY_KEY);
	}
	public int getRoundedNotoriety() {
		return (int)Math.floor(getNotoriety());
	}
	public double getFame() {
		return getDouble(FAME_KEY);
	}
	public int getRoundedFame() {
		return (int)Math.floor(getFame());
	}
	public int getCurrentMonth() {
		return getInt(CURRENT_MONTH);
	}
	public int getCurrentDay() {
		return getInt(CURRENT_DAY);
	}
	public GamePhaseType getCurrentGamePhase() {
		return GameWrapper.findGame(getGameObject().getGameData()).getGamePhase();
	}
	public int getBasicPhases() {
		return getInt(BASIC_PHASES);
	}
	public int getSunlightPhases() {
		return getInt(SUNLIGHT_PHASES);
	}
	public int getShelteredPhases() {
		return getInt(SHELTERED_PHASES);
	}
	public int getMountainMoveCost() {
		int val = getInt(MOUNTAIN_MOVE_COST);
		if (affectedByKey(Constants.MOUNTAIN_MOVE_ADJ)) {
			val--;
		}
		return val;
	}
	public int getCombatStatus() {
		return getInt(COMBAT_STATUS);
	}
	public int getCombatPlayOrder() {
		return getInt(COMBAT_PLAY_ORDER);
	}
	public int getMeleePlayOrder() {
		return getInt(MELEE_PLAY_ORDER);
	}
	public int getCombatCount() {
		return getInt(COMBAT_COUNT);
	}
	public boolean isDoRecord() {
		if (getBoolean(DO_RECORD)) {
			return canPlay();
		}
		return false;
	}
	public int getPlayOrder() {
		return getInt(PLAY_ORDER);
	}
	public boolean isLastPlayer() {
		return getBoolean(LAST_PLAYER);
	}
	public boolean isLastPreemptivePlayer() {
		return getBoolean(LAST_PREEMPTIVE_PLAYER);
	}
	public String getCharacterType() {
		return getString(CHARACTER_TYPE);
	}
	public boolean isGameOver() {
		return getBoolean(GAME_OVER);
	}
	public boolean isActive() {
		return !isDead() && !isGone();
	}
	public boolean isDead() {
		return getGameObject().hasThisAttribute(Constants.DEAD);
	}
	public boolean isGone() {
		return getGameObject().hasThisAttribute(Constants.GONE);
	}
	public boolean getDoInstantPeer() {
	    return getBoolean(DO_INSTANT_PEER);
	}
	public boolean getNoSummon() {
		return getBoolean(NO_SUMMON);
	}
	public Strength getWishStrength() {
		String val = getString(WISH_STRENGTH);
		if (val!=null) {
			return new Strength(val);
		}
		return null;
	}
	public boolean getPeerAny() {
		return getBoolean(PEER_ANY);
	}
	public boolean getStormed() {
		return getBoolean(STORMED);
	}
	public int getLostPhases() {
		return getInt(LOST_PHASES);
	}
	public boolean isFortified() {
		return getBoolean(FORTIFIED);
	}
	public boolean isFortDamaged() {
		return getBoolean(FORT_DAMAGED);
	}
	public boolean needsQuestCheck() {
		return getBoolean(NEED_QUEST_CHECK);
	}
	public boolean alreadyDiscardedQuests() {
		return getBoolean(DISCARDED_QUESTS);
	}
	public String getCurrentCampaign() {
		return getString(CAMPAIGN);
	}
	public int getWeatherFatigue() {
		return getInt(WEATHER_FATIGUE);
	}
	public int getExtraWounds() {
		return getInt(EXTRA_WOUNDS);
	}
	public boolean hasSpellConflicts() {
		return getBoolean(SPELL_CONFLICTS);
	}
	public ArrayList<SpellWrapper> getSpellConflicts() {
		ArrayList<SpellWrapper> conflicts = new ArrayList<>();
		ArrayList<String> list = getList(SPELL_CONFLICTS);
		for (String id : list) {
			GameObject go = getGameObject().getGameData().getGameObject(Long.valueOf(id));
			conflicts.add(new SpellWrapper(go));
		}
		return conflicts;
	}
	public boolean getNeedsInventoryCheck() {
		return getBoolean(NEEDS_INVENTORY_CHECK);
	}
	public boolean getNeedsActionPanelUpdate() {
		return getBoolean(NEEDS_ACTION_PANEL_UPDATE);
	}
	
	// Other getters
	public int getNextCacheNumber() {
		int n = getInt(CACHE_NUMBER);
		n += 1;
		setInt(CACHE_NUMBER, n);
		return n;
	}
	public String getGameStatus() {
		return getGameStatus(false);
	}
	public String getGameStatus(boolean waiting) {
		GameWrapper game = GameWrapper.findGame(getGameObject().getGameData());
		String prefix = hasCheated()?"Cheater! ":"";
		String result = "";
		if (isDead()) {
			result = "Dead";
		}
		else if (isGone()) {
			result = "Left the Map";
		}
		else if (needsToSetVps() || needsToDeductVps()) {
			result = "Assigning VPs";
		}
		else if (getNeedsChooseGoldSpecial()) {
			result = "Visitor/Mission";
		}
		else if (waiting) {
			result = "Waiting...";
		}
		else if (isDoRecord()) {
			result = "Recording";
		}
		else if (game.isDayEnd()) {
			if (isDayEndTradingActive()) {
				result = "Day End Trading";
			}
			else {
				result = "Done Trading";
			}
		}
		else if (getCombatCount()>0) { // has combats to handle
			if (getCombatStatus()>0) { // currently handling a combat
				switch(getCombatStatus()) {
					case Constants.COMBAT_PREBATTLE:		result = "Battling Natives"; break;
					case Constants.COMBAT_LURE:			result = "Luring"; break;
					case Constants.COMBAT_RANDOM_ASSIGN:	result = "Random Assignment"; break;
					case Constants.COMBAT_DEPLOY:			result = "Deploying"; break;
					case Constants.COMBAT_ACTIONS:		result = "Combat Actions"; break;
					case Constants.COMBAT_ASSIGN:			result = "Assigning Targets"; break;
					case Constants.COMBAT_POSITIONING:		result = "Positioning"; break;
					case Constants.COMBAT_TACTICS:		result = "Changing Tactics"; break;
					case Constants.COMBAT_RESOLVING:		result = "Viewing Results"; break;
					case Constants.COMBAT_FATIGUE:		result = "Fatigue/Wounds"; break;
					case Constants.COMBAT_DONE:			result = "Finished Combat";  break;
					default:							result = "In Combat"; break;
				}
			}
			else {
				result = "Waiting for Combat";
			}
		}
		else {
			int order = getPlayOrder();
			if (order==0) {
				result = "Finished";
			}
			else if (order==1) {
				result = "Playing Turn";
			}
			else if (order>1) {
				result = "Waiting to Play Turn";
			}
		}
		
 		if (isMissingInAction()) {
			result = result + " (Offline)";
		}
 		else if (isSleep()) {
			result = result + " (Asleep)";
 		}
		
		return prefix+result;
	}
	public Strength getVulnerability() {
		Strength vul = new Strength(getGameObject().getThisAttribute("vulnerability"));
		GameObject transform = getTransmorph();
		if (transform!=null) {
			vul = new Strength(transform.getThisAttribute("vulnerability"));
			int mod = 0;
			if (transform.hasThisAttribute(Constants.WEAKENED_VULNERABILITY)) {
				mod--;
			}
			if (transform.hasThisAttribute(Constants.STRENGTHENED_VULNERABILITY)) {
				mod++;
			}
			if (transform.hasThisAttribute(Constants.ALTER_SIZE_DECREASED_VULNERABILITY)) {
				mod--;
			}
			if (transform.hasThisAttribute(Constants.ALTER_SIZE_INCREASED_VULNERABILITY)) {
				mod++;
			}
			vul.modify(mod);
		}
		else if (getGameObject().hasThisAttribute(Constants.ENHANCED_VULNERABILITY)) {
			// Beserker has the ability to raise his vulnerability temporarily
			vul = new Strength(getGameObject().getThisAttribute(Constants.ENHANCED_VULNERABILITY));
		}
		int mod = 0;
		if (hasActiveInventoryThisKey(Constants.REDUCED_VULNERABILITY)) {
			mod--;
		}
		if (getGameObject().hasThisAttribute(Constants.WEAKENED_VULNERABILITY)) {
			mod--;
		}
		if (getGameObject().hasThisAttribute(Constants.STRENGTHENED_VULNERABILITY)) {
			mod++;
		}
		if (getGameObject().hasThisAttribute(Constants.CURDLE_OF_BONE)) {
			mod++;
		}
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_DECREASED_VULNERABILITY)) {
			mod--;
		}
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_INCREASED_VULNERABILITY)) {
			mod++;
		}
		vul.modify(mod);
		return vul;
	}
	public Strength getWeight() {
		GameObject transmorph = getTransmorph();
		int mod = 0;
		if (transmorph!=null) {
			if (transmorph.hasThisAttribute(Constants.ALTER_SIZE_INCREASED_WEIGHT)) mod++;
			if (transmorph.hasThisAttribute(Constants.ALTER_SIZE_DECREASED_WEIGHT)) mod--;
			return new Strength(transmorph.getThisAttribute("vulnerability")+mod);
		}
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_INCREASED_WEIGHT)) mod++;
		if (getGameObject().hasThisAttribute(Constants.ALTER_SIZE_DECREASED_WEIGHT)) mod--;
	    return new Strength(getGameObject().getThisAttribute("vulnerability")+mod);
	}
	/**
	 * @param speedToBeat			The speed to beat (all options must be FASTER than)
	 * @param includeActionChits		true if the action chits are to be included in the options
	 * @param flipHorses			true if the horses are to be flipped when collected (like when running!)
	 * 
	 * @return 						All the options (RealmComponent objects) the character could use as a maneuver
	 */
	public ArrayList<RealmComponent> getMoveSpeedOptions(Speed speedToBeat,boolean includeActionChits,boolean flipHorses) {
		ArrayList<RealmComponent> list = new ArrayList<>();
		ArrayList<RealmComponent> searchList = new ArrayList<>();
		if (includeActionChits) {
			searchList.addAll(getActiveMoveChits());
			searchList.addAll(getFlyChits());
		}
		for (GameObject item : getActiveInventory()) {
			searchList.add(RealmComponent.getRealmComponent(item));
		}
		GameObject transmorph = getTransmorph();
		if (transmorph!=null) {
			// Character is transmorphed!
			RealmComponent rc = RealmComponent.getRealmComponent(transmorph);
			if (rc.isMonster()) {
				MonsterChitComponent monster = (MonsterChitComponent)rc;
				searchList.add(monster.getMoveChit());
			}
		}
		
		// Search
		for (RealmComponent rc : searchList) {		
			if (!rc.getGameObject().hasThisAttribute(Constants.UNPLAYABLE)) {
				Speed speed;
				if (rc.isHorse()) {
					// see if the gallop side can be used, if not, use trot speed
					Strength heaviestInv = getNeededSupportWeight();
					SteedChitComponent horse = (SteedChitComponent)rc;
					if (horse.getGallopStrength().strongerOrEqualTo(heaviestInv)) {
						if (flipHorses) horse.setGallop();
						speed = horse.getGallopSpeed();
					}
					else {
						if (flipHorses) horse.setWalk();
						speed = horse.getTrotSpeed();
					}
				}
				else {
					speed = BattleUtility.getMoveSpeed(rc);
				}
				if (speed!=null && speed.fasterThan(speedToBeat)) {
					list.add(rc);
				}
			}
		}
		return list;
	}
	/**
	 * @return		true if this component (really just a chit) is restricted from use (light/great elf)
	 */
	public static boolean isRestrictedFromUse(String charType,RealmComponent rc) {
		if (charType!=null) {
			String restrict = rc.getGameObject().getThisAttribute("restrict");
			if (restrict!=null && !restrict.equals(charType)) {
				return true; // skip over chits that don't match the char type
			}
		}
		return false;
	}
	/**
	 * @param speedToBeat			The speed to beat
	 * @param includeActionChits	true if the action chits are to be included in the options
	 * 
	 * @return 						All the options the character could use as an attack chit
	 */
	public Collection<RealmComponent> getFightSpeedOptions(Speed speedToBeat,boolean includeActionChits) {
		ArrayList<RealmComponent> list = new ArrayList<>();
		ArrayList<RealmComponent> searchList = new ArrayList<>();
		if (includeActionChits) {
			searchList.addAll(getActiveFightChits());
		}
		for (GameObject item : getActiveInventory()) {
			searchList.add(RealmComponent.getRealmComponent(item));
		}
		for (RealmComponent rc : searchList) {
			if (!rc.getGameObject().hasThisAttribute(Constants.UNPLAYABLE)) {
				Speed speed = BattleUtility.getFightSpeed(rc);
				if (speed!=null && speed.fasterThan(speedToBeat)) {
					list.add(rc);
				}
			}
		}
		return list;
	}
	public boolean canFollow() {
		return canMove() || mustFly();
	}
	/**
	 * @return		true if character can move at all
	 */
	public boolean canMove() {
		if (getWeight().isMaximum()) return false;
		Strength moveStrength = getMoveStrength(true,false);
		return canMove(moveStrength);
	}
	/**
	 * @param moveStrength			strength to test
	 * 
	 * @return						true if provided strength is strong enough to carry all inventory
	 */
	public boolean canMove(Strength moveStrength) {
		if (getWeight().isMaximum()) return false;
		if (mustFly()) { // If you MUST fly, you can't MOVE
			return false;
		}
		Strength supportWeight = getNeededSupportWeight(false);
		return moveStrength.strongerOrEqualTo(supportWeight); // don't include character weight when determining if move is possible
	}
	/**
	 * @return		The weight of the active weapon.  If no weapon is active, its assumed a dagger is used, which has
	 * 				a negligible weight.
	 */
	public Strength getActiveWeaponWeight() {
		ArrayList<WeaponChitComponent> weapons = getActiveWeapons();
		if (weapons == null || weapons.isEmpty()) {
			return new Strength();
		}
		Strength lowestWeaponsWeight = Strength.valueOf("T");
		for (WeaponChitComponent weapon : weapons) {
			if ((weapon.getWeight()).weakerTo(lowestWeaponsWeight)) {
				lowestWeaponsWeight = weapon.getWeight();
			}
		}
		return lowestWeaponsWeight;
	}
	/**
	 * @param fightStrength			strength to test
	 * 
	 * @return						true if provided strength is strong enough to wield the active weapon
	 */
	public boolean canFight(Strength fightStrength) {
		ArrayList<WeaponChitComponent> weapons = getActiveWeapons();
		if (weapons!=null && !weapons.isEmpty()) {
			for (WeaponChitComponent weapon : weapons) {
				Strength weaponWeight = new Strength(weapon.getWeight());
				if (fightStrength.strongerOrEqualTo(weaponWeight)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}
	/**
	 * @return		The weight that needs support for activation and movement.  This takes into account
	 * 				character vulnerability, inventory, and pack horses.
	 */
	public Strength getNeededSupportWeight() {
		return getNeededSupportWeight(true);
	}
	public Strength getNeededSupportWeight(boolean includeCharacterWeight) {
		Strength active = getActiveWeight(includeCharacterWeight);
		Strength inactive = getInactiveWeight();
		Strength convoy = getConvoyStrength();
		
		if (convoy.strongerOrEqualTo(inactive)) {
			// Only worry about active weight if the convoy can handle the rest
			return active;
		}
		// Otherwise, return the heavier of the two
		return active.strongerThan(inactive)?active:inactive;
	}
	/**
	 * @return		The strength of the strongest packhorse
	 */
	private Strength getConvoyStrength() {
		Strength strongest = new Strength();
		
		// Pack horses
		for (GameObject go : getInactiveInventory()) {
			Strength horseStrength = new Strength(go.getAttribute("trot","strength"));
			if (horseStrength.strongerThan(strongest)) {
				strongest = horseStrength;
			}
		}
		
		// Hirelings
		Strength hirelingMoveStrength = getBestFollowingHirelingStrength();
		if (hirelingMoveStrength.strongerThan(strongest)) {
			strongest = hirelingMoveStrength;
		}
		
		// Nomads
		for (GameObject go : getNomads()) {
			Strength nomadStrength = new Strength(go.getThisAttribute(Constants.CARRIER));
			if (nomadStrength.strongerThan(strongest)) {
				strongest = nomadStrength;
			}
		}
			
		return strongest;
	}
	/**
	 * @return		The maximum weight of the character.  This includes the character weight to start,
	 * 				and then all items that are being carried (ie., activated).
	 */
	private Strength getActiveWeight(boolean includeCharacterWeight) {
		Strength heaviest = includeCharacterWeight?getWeight():new Strength();
		for (GameObject go:getActiveInventory()) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isHorse() || rc.isNativeHorse()) continue;
			Strength itemWeight = rc.getWeight();
			if (itemWeight.strongerThan(heaviest)) {
				heaviest = itemWeight;
			}
		}
		return heaviest;
	}
	public GameObject getWeightlessInactiveItem() { // Due to bag of weightless
		GameObject weightlessItem = null;
		if (hasActiveInventoryThisKey(Constants.REDUCE_WEIGHT)) {
			Strength heaviest = new Strength(); // negligible
			for (GameObject go:getInactiveInventory()) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (rc.isHorse() || rc.isNativeHorse()) continue;
				Strength itemWeight = rc.getWeight();
				if (itemWeight.strongerThan(heaviest)) {
					heaviest = itemWeight;
					weightlessItem = go;
				}
			}
		}
		return weightlessItem;
	}
	/**
	 * @return		The maximum weight of inactive inventory
	 */
	private Strength getInactiveWeight() {
		return getInactiveWeight(getWeightlessInactiveItem());
	}
	private Strength getInactiveWeight(GameObject ignore) {
		Strength heaviest = new Strength(); // negligible
		for (GameObject go:getInactiveInventory()) {
			if (ignore==null || !ignore.equals(go)) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (rc.isHorse() || rc.isNativeHorse()) continue;
				Strength itemWeight = rc.getWeight();
				if (itemWeight.strongerThan(heaviest)) {
					heaviest = itemWeight;
				}
			}
		}
		return heaviest;
	}
	/**
	 * @return		A Strength object representing the best move strength available to the character
	 */
	public Strength getMoveStrength(boolean includeActionChits,boolean includeConvoyStrength) {
		Strength best = new Strength(); // negligible by default
		if (includeActionChits) {
			// Check active chits
			for (CharacterActionChitComponent chit : getActiveChits()) {
				if (chit.isMove() && !chit.isMoveLock()) { // DUCK chit cannot be played to carry items
					if (!chit.getGameObject().hasThisAttribute(Constants.UNPLAYABLE)) {
						Strength chitStrength = chit.getStrength();
						if (chitStrength.strongerThan(best)) {
							best = chitStrength;
						}
					}
				}
			}
		}
		GameObject transmorph = getTransmorph();
		if (transmorph!=null) {
			MonsterChitComponent monster = (MonsterChitComponent)RealmComponent.getRealmComponent(transmorph);
			Strength transmorphStrength = monster.getVulnerability();
			if (transmorphStrength.strongerThan(best)) {
				best = transmorphStrength;
			}
		}
		if (isHiredLeader() || isControlledMonster()) {
			Strength leaderStrength = getHirelingMoveStrength(RealmComponent.getRealmComponent(getGameObject()));
			if (leaderStrength.strongerThan(best)) {
				best = leaderStrength;
			}
		}
		// Check active treasures and horses (pack horses are figured in later when determining needed support weight
		for (GameObject item : getInventory()) {
			RealmComponent rc = RealmComponent.getRealmComponent(item);
			Strength itemStrength = null;
			if (item.hasThisAttribute(Constants.ACTIVATED)) {
				if (rc.isTreasure() && item.hasThisAttribute("move_speed")) {
					itemStrength = new Strength(item.getThisAttribute("strength"));
				}
				else if (rc.isHorse()) {
					itemStrength = new Strength(item.getAttribute("trot","strength"));
				}
				else if (rc.isNomad() && item.hasThisAttribute(Constants.CARRIER)) {
					itemStrength = new Strength(item.getThisAttribute(Constants.CARRIER));
				}
			}
			if (includeConvoyStrength && rc.isHorse()) { // include pack horses when doing things like opening VAULTs
				itemStrength = new Strength(item.getAttribute("trot","strength"));
			}
			if (includeConvoyStrength && rc.isNomad()) {
				itemStrength = new Strength(item.getThisAttribute(Constants.CARRIER));
			}
			if (itemStrength!=null && itemStrength.strongerThan(best)) {
				best = itemStrength;
			}
		}
		// Check move strengths of following hirelings (and their horses, if any)
		Strength hirelingMoveStrength = getBestFollowingHirelingStrength();
		if (hirelingMoveStrength.strongerThan(best)) {
			best = hirelingMoveStrength;
		}
		return best;
	}
	private Strength getBestFollowingHirelingStrength() {
		Strength best = new Strength();
		for (RealmComponent rc : getFollowingHirelings()) {
			Strength hirelingMoveStrength = getHirelingMoveStrength(rc);
			if (hirelingMoveStrength.strongerThan(best)) {
				best = hirelingMoveStrength;
			}
		}
		return best;
	}
	private static Strength getHirelingMoveStrength(RealmComponent rc) {
		Strength hirelingMoveStrength = new Strength(rc.getGameObject().getThisAttribute("vulnerability"));
		RealmComponent nativeHorse = (RealmComponent)rc.getHorse();
		if (nativeHorse!=null) {
			Strength horseMoveStrength = new Strength(nativeHorse.getGameObject().getAttribute("trot","strength"));
			if (horseMoveStrength.strongerThan(hirelingMoveStrength)) {
				hirelingMoveStrength = horseMoveStrength;
			}
		}
		return hirelingMoveStrength;
	}
	/**
	 * @return		A Strength object representing the best fight strength available to the character
	 */
	public Strength getFightStrength() {
		return getFightStrength(true,false);
	}
	public Strength getFightStrength(boolean includeActionChits,boolean includeHirelings) {
		Strength best = new Strength(); // negligible by default
		if (includeActionChits) {
			// Check active chits
			for (CharacterActionChitComponent chit : getActiveChits()) {
				if (chit.isFight()) {
					Strength chitStrength = chit.getStrength();
					if (chitStrength.strongerThan(best)) {
						best = chitStrength;
					}
				}
			}
		}
		// Check active treasures
		for (GameObject treasure:getEnhancingItems()) {
			if (treasure.hasThisAttribute("attack_speed")) {
				Strength treasureStrength = new Strength(treasure.getThisAttribute("strength"));
				if (treasureStrength.strongerThan(best)) {
					best = treasureStrength;
				}
			}
		}
		if (includeHirelings) {
			// Check fight strengths of following hirelings
			for (RealmComponent rc : getFollowingHirelings()) {
				Strength fight1 = new Strength(rc.getGameObject().getAttribute("light","strength"));
				Strength fight2 = new Strength(rc.getGameObject().getAttribute("dark","strength"));
				if (fight1.strongerThan(best)) {
					best = fight1;
				}
				if (fight2.strongerThan(best)) {
					best = fight2;
				}
			}
		}
		if (isHiredLeader() || isControlledMonster()) {
			Strength fight1 = new Strength(getGameObject().getAttribute("light","strength"));
			Strength fight2 = new Strength(getGameObject().getAttribute("dark","strength"));
			if (fight1.strongerThan(best)) {
				best = fight1;
			}
			if (fight2.strongerThan(best)) {
				best = fight2;
			}
		}
		return best;
	}
	/**
	 * @return			true when the client that owns this character is disconnected
	 */
	public boolean isMissingInAction() {
		return getBoolean(MISSING_IN_ACTION);
	}
	public void setMissingInAction(boolean val) {
		setBoolean(MISSING_IN_ACTION,val);
	}
	private static ArrayList<String> getAllRelationshipBlocks(HostPrefWrapper hostPrefs) {
		ArrayList<String> list = new ArrayList<>();
		int boards = hostPrefs.getMultiBoardEnabled()?hostPrefs.getMultiBoardCount():1;
		for (int i=0;i<boards;i++) {
			String block = Constants.GAME_RELATIONSHIP;
			if (i>0) {
				block = block + Constants.MULTI_BOARD_APPENDS.substring(i-1,i);
			}
			list.add(block);
		}
		return list;
	}
	
	public void changeRelationship(GameObject denizen,int val) {
		changeRelationship(RealmUtility.getRelationshipBlockFor(denizen),RealmUtility.getRelationshipGroupName(denizen),val, false);
	}
	
	public void changeRelationshipTo(GameObject denizen, int val){
		changeRelationship(RealmUtility.getRelationshipBlockFor(denizen),RealmUtility.getRelationshipGroupName(denizen),val, true);
	}
	
	public void changeRelationship(String relBlock,String groupName, int val, boolean absoluteValue) {
		if (!isCharacter()) {
			getHiringCharacter().changeRelationship(relBlock,groupName,val, false);
			return;
		}
		groupName = groupName.toLowerCase().trim();
		int rel = getGameObject().getInt(relBlock,groupName);
		
		if(absoluteValue){
			rel = val;
		} else {
			rel += val;
		}

		if (rel==0) {
			getGameObject().removeAttribute(relBlock,groupName);
		}
		else {
			getGameObject().setAttribute(relBlock,groupName,rel);
		}
		
		addListItem(RELCHANGE_GROUP_LIST,groupName);
	}
	public boolean hasChangedRelationshipToday(GameObject denizen) {
		if (!isCharacter()) {
			return getHiringCharacter().hasChangedRelationshipToday(denizen);
		}
		String groupName = RealmUtility.getRelationshipGroupName(denizen).toLowerCase().trim();
		return hasListItem(RELCHANGE_GROUP_LIST,groupName);
	}
	public void addDamagedRelations(GameObject denizen) {
		if (!isCharacter()) {
			getHiringCharacter().addDamagedRelations(denizen);
		}
		String groupName = RealmUtility.getRelationshipGroupName(denizen);
		if (groupName!=null) {
			getGameObject().addThisAttributeListItem(Constants.DAMAGED_RELATIONS,groupName.toLowerCase().trim());
		}
	}
	public void removeDamagedRelations(GameObject denizen) {
		if (!isCharacter()) {
			getHiringCharacter().removeDamagedRelations(denizen);
		}
		String groupName = RealmUtility.getRelationshipGroupName(denizen);
		if (groupName!=null) {
			removeDamagedRelations(groupName.toLowerCase().trim());
		}
	}
	public void removeDamagedRelations(String groupName) {
		getGameObject().removeThisAttributeListItem(Constants.DAMAGED_RELATIONS,groupName.toLowerCase().trim());
	}
	public boolean hasDamagedRelations(GameObject denizen) {
		if (!isCharacter()) {
			getHiringCharacter().hasDamagedRelations(denizen);
		}
		String groupName = RealmUtility.getRelationshipGroupName(denizen);
		if (groupName==null) {
			return false;
		}
		return getGameObject().hasThisAttributeListItem(Constants.DAMAGED_RELATIONS,groupName.toLowerCase().trim());
	}
	public ArrayList<String[]> getAllies(HostPrefWrapper hostPrefs) {
		return getGroupsWithRelationship(hostPrefs,RelationshipType.ALLY);
	}
	/**
	 * @param relationship		One of ALLY,FRIENDLY,NEUTRAL,UNFRIENDLY,ENEMY
	 * 
	 * @return				All the groupNames associated with provided relationship
	 */
	public ArrayList<String[]> getGroupsWithRelationship(HostPrefWrapper hostPrefs,int relationship) {
		if (!isCharacter()) {
			return getHiringCharacter().getGroupsWithRelationship(hostPrefs,relationship);
		}
		ArrayList<String[]> list = new ArrayList<>();
		for (String relBlock:getAllRelationshipBlocks(hostPrefs)) {
			Hashtable<String,Object> hash = getGameObject().getAttributeBlock(relBlock);
			for (String groupName : hash.keySet()) {
				int rel = Integer.valueOf(hash.get(groupName).toString()).intValue();
				if (rel>RelationshipType.ALLY) rel=RelationshipType.ALLY;
				if (rel<RelationshipType.ENEMY) rel=RelationshipType.ENEMY;
				if (rel==relationship) {
					String[] ret = new String[2];
					ret[0] = relBlock;
					ret[1] = groupName;
					list.add(ret);
				}
			}
		}
		return list;
	}
	public int getRelationship(GameObject denizen) {
		boolean ravingNative = denizen.hasThisAttribute(Constants.ROVING_NATIVE);
		int relationship = getRelationship(RealmUtility.getRelationshipBlockFor(denizen),RealmUtility.getRelationshipGroupName(denizen),ravingNative);
		if (isNegativeAuraInClearing()) {
			relationship--;
		}
		if (isProfaneIdolInClearing()) {
			relationship--;
		}
		if (this.affectedByKey(Constants.DAZZLE)) {
			relationship++;
		}
		return relationship;
	}
	public boolean isNegativeAuraInClearing() {
		if (getCurrentLocation()!=null && getCurrentLocation().clearing!=null) {
			for (RealmComponent rc : getCurrentLocation().clearing.getDeepClearingComponents()) {
				if (rc.getGameObject().hasThisAttribute(Constants.NEGATIVE_AURA) && !rc.isSpell() && !rc.getGameObject().hasThisAttribute(Constants.SPELL_DENIZEN)) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean isProfaneIdolInClearing() {
		if (getCurrentLocation()!=null && getCurrentLocation().clearing!=null) {
			for (RealmComponent rc : getCurrentLocation().clearing.getDeepClearingComponents()) {
				if (rc.getGameObject().hasThisAttribute(Constants.PROFANE_IDOL)) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * @param groupName		The name of the group you are testing the relationship for.  Might be a native group, or a visitor.
	 * 
	 * @return				One of ALLY,FRIENDLY,NEUTRAL,UNFRIENDLY,ENEMY
	 */
	public int getRelationship(String relBlock,String groupName) {
		return getRelationship(relBlock, groupName, false);
	}
	public int getRelationship(String relBlock,String groupName, boolean ravingNative) {
		if (!isCharacter()) {
			CharacterWrapper hiringCharacter = getHiringCharacter();
			if (hiringCharacter!=null) {
				return hiringCharacter.getRelationship(relBlock,groupName);
			}
		}
		groupName = groupName.toLowerCase();
		
		int rel = getGameObject().getInt(relBlock,groupName);
		if (ravingNative && !getGameObject().hasAttribute(relBlock,groupName)) {
			rel = RelationshipType.ENEMY;
		}
		int mod = 0;
		ArrayList<GameObject> list = getAllActiveInventoryThisKeyAndValue(Constants.MEETING_MOD,null);
		for (GameObject item:list) {
			mod = Math.max(item.getThisInt(Constants.MEETING_MOD),mod); // only take the best
		}
		
		return rel + mod;
	}
	public ArrayList<String> getRelationshipList(String relBlock,int rel) {
		ArrayList<String> list = new ArrayList<>();
		OrderedHashtable<String,Object> hash = getGameObject().getAttributeBlock(relBlock);
		for (String groupName : hash.keySet()) {
			if (rel==getGameObject().getInt(relBlock,groupName)) {
				list.add(groupName);
			}
		}
		return list;
	}
	public void clearRelationships(HostPrefWrapper hostPrefs) {
		ArrayList<String> relBlocks = getAllRelationshipBlocks(hostPrefs);
		for (String relBlock:relBlocks) {
			if (getGameObject().hasAttributeBlock(relBlock)) {
				getGameObject().removeAttributeBlock(relBlock);
			}
		}
	}
	public void initRelationships(HostPrefWrapper hostPrefs) {
		// Init relationships
		if (getCharacterLevel()>=3) { // only get the base relationships at level 3 or higher - otherwise all neutral!
			OrderedHashtable<String,Object> baseBlock = getGameObject().getAttributeBlock(Constants.BASE_RELATIONSHIP);
			ArrayList<String> relBlocks = getAllRelationshipBlocks(hostPrefs);
			for (String relBlock:relBlocks) {
				for (String key:baseBlock.keySet()) {
					int baseRel = getGameObject().getInt(Constants.BASE_RELATIONSHIP,key);
					int currRel = getGameObject().getInt(relBlock,key);
					getGameObject().setAttribute(relBlock,key,baseRel+currRel);
				}
			}
			if (hostPrefs.hasPref(Constants.TE_KNIGHT_ADJUSTMENT) && getGameObject().hasThisAttribute("knight")) {
				// Apply the 3rd edition Knight adjustment
				ArrayList<String[]> allies = getAllies(hostPrefs);
				for (String[] ret:allies) {
					String relBlock = ret[0];
					String groupName = ret[1];
					changeRelationship(relBlock,groupName,-1, false);
				}
			}
		}
	}
	public String getFollowStringId() {
		Collection<String> c = getCurrentActions();
		if (c!=null && c.size()==1) {
			String action = c.iterator().next();
			if (getIdForAction(action)==ActionId.Follow) {
				int tilde = action.indexOf('~');
				String id = action.substring(tilde+1);
				return id;
			}
		}
		return null;
	}
	public CharacterWrapper getCharacterImFollowing() {
		String followId = getFollowStringId();
		if (followId!=null) {
			GameObject go = getGameObject().getGameData().getGameObject(Long.valueOf(followId));
			return new CharacterWrapper(go);
		}
		return null;
	}
	public boolean isFollowingCharacterPlayingTurn() {
		CharacterWrapper following = getCharacterImFollowing();
		return following!=null && following.isPlayingTurn() && !isStopFollowing();
	}
	public void setStopFollowing(boolean val) {
		setBoolean(STOP_FOLLOWING,val);
	}
	public boolean isStopFollowing() {
		return getBoolean(STOP_FOLLOWING);
	}
	/**
	 * When the character gets a kill, add it here (based on current day)
	 */
	public void addKill(GameObject kill,Spoils spoils) {
		String dayKey = getCurrentDayKey();
		if (!getGameObject().hasAttributeListItem(KILL_BLOCK,dayKey,kill.getStringId())) {
			getGameObject().addAttributeListItem(KILL_BLOCK,dayKey,kill.getStringId());
			getGameObject().addAttributeListItem(KILL_BLOCK,dayKey+"S",spoils.asKey());
		}
	}
	public boolean areKills(String dayKey) {
		return getGameObject().hasAttribute(KILL_BLOCK,dayKey);
	}
	/**
	 * Returns a collection of game objects for the specified day key
	 */
	public ArrayList<GameObject> getKills(String dayKey) {
		ArrayList<GameObject> kills = new ArrayList<>();
		Collection<String> ids = getGameObject().getAttributeList(KILL_BLOCK,dayKey);
		if (ids!=null) {
			GameData data = getGameObject().getGameData();
			for (String id : ids) {
				GameObject kill = data.getGameObject(Long.valueOf(id));
				if (!kills.contains(kill)) { // This check should be unnecessary if addKill is doing its job
					kills.add(kill);
				}
			}
		}
		return kills;
	}
	/**
	 * Returns a collection of game objects for the specified day key
	 */
	public ArrayList<Spoils> getKillSpoils(String dayKey) {
		ArrayList<Spoils> killSpoils = new ArrayList<>();
		ArrayList<String> keys = getGameObject().getAttributeList(KILL_BLOCK,dayKey+"S");
		if (keys!=null) {
			for(Object key:keys) {
				killSpoils.add(new Spoils(key.toString()));
			}
		}
		return killSpoils;
	}
	/**
	 * When the character casts a spell, add it here (based on current day)
	 */
	public void addCastedSpell(GameObject spell) {
		String dayKey = getCurrentDayKey();
		getGameObject().addAttributeListItem(SPELL_BLOCK,dayKey,spell.getStringId());
	}
	/**
	 * Returns a collection of game objects for all day keys
	 */
	public ArrayList<GameObject> getCastedSpells() {
		ArrayList<GameObject> spells = new ArrayList<>();
		OrderedHashtable<String, Object> spellBlock = getGameObject().getAttributeBlock(SPELL_BLOCK);
		for (String dayKey : spellBlock.orderedKeys()) {
			spells.addAll(getCastedSpells(dayKey));
		}
		return spells;
	}
	/**
	 * Returns a collection of game objects for the specified day key
	 */
	public ArrayList<GameObject> getCastedSpells(String dayKey) {
		ArrayList<GameObject> spells = new ArrayList<>();
		Collection<String> ids = getGameObject().getAttributeList(SPELL_BLOCK,dayKey);
		if (ids!=null) {
			GameData data = getGameObject().getGameData();
			for (String id : ids) {
				GameObject spell = data.getGameObject(Long.valueOf(id));
				spells.add(spell);
			}
		}
		return spells;
	}
	
	/**
	 * @param dayKey		The day you wish to fetch
	 * 
	 * @return				A Collection of actions for the given day
	 */
	public ArrayList<String> getActions(String dayKey) {
		return getList(dayKey);
	}
	/**
	 * @return		The total number of actions for the current day
	 */
	public int getCurrentActionCount() {
		ArrayList<String> c = getCurrentActions();
		return c==null?0:c.size();
	}
	/**
	 * @return		A Collection of action strings optionally appended with a ClearingDetail.getShorthand()
	 */
	public ArrayList<String> getCurrentActions() {
		return getCurrentActions(false);
	}
	
	public ArrayList<String> getCurrentActionsCodes() {
		ArrayList<String> actions = new ArrayList<>();
		for (String action : getCurrentActions(false)) {
			actions.add(action.split("-")[0]);
		}
		return actions;
	}
	/**
	 * @param excludeBlockedActions		If true, then no blocked actions will be included
	 * 
	 * @return		A Collection of action strings optionally appended with a ClearingDetail.getShorthand()
	 */
	public ArrayList<String> getCurrentActions(boolean excludeBlockedActions) {
		ArrayList<String> c = getList(getCurrentDayKey());
		if (c==null) {
			c = new ArrayList<>();
		}
		if (excludeBlockedActions) {
			return filterBlocked(c);
		}
		return c;
	}
	public String getNextPendingAction() {
		return getString(NEXT_PENDING_ACTION);
	}
	public void setNextPendingAction(String val) {
		setString(NEXT_PENDING_ACTION,val);
	}
	public boolean hasCurrentAction(String action) {
		ActionId id = getIdForAction(action);
		Collection<String> c = getCurrentActions();
		if (c!=null) {
			for (String test : c) {
				ActionId testId = getIdForAction(test);
				if (id==testId) {
					return true;
				}
			}
		}
		return false;
	}
	private static ArrayList<String> filterBlocked(Collection<String> in) {
		ArrayList<String> list = new ArrayList<>();
		for (String action : in) {
			if (Constants.BLOCKED.equals(action)) {
				break;
			}
			list.add(action);
		}
		return list;
	}
	/**
	 * @return		A Collection of action string representing every recorded action, starting with the earliest.
	 * 				Note, blocked actions that never occurred are left out of this list!!
	 */
	public Collection<String> getAllActions() {
		ArrayList<String> list = new ArrayList<>();
		Collection<String> dayKeys = getAllDayKeys();
		if (dayKeys!=null && !dayKeys.isEmpty()) {
			for (String dayKey : getAllDayKeys()) {
				Collection<String> actions = getActions(dayKey);
				if (actions!=null && !actions.isEmpty()) {
					list.addAll(filterBlocked(actions));
				}
			}
		}
		return list;
	}
	
	/**
	 * This is called by the RealmTurnPanel to rebuild the actions, in case of being blocked.
	 */
	public void clearCurrentActions() {
		removeAttribute(getCurrentDayKey());
		removeAttribute(getCurrentDayKey()+"V");
		removeAttribute(getCurrentDayKey()+"C");
		//clearActionBuffer();
	}
	public void clearActionBuffer() {
		removeAttribute(getCurrentDayKey()+"P");
		removeAttribute(getCurrentDayKey()+"M");
		removeAttribute(getCurrentDayKey()+"R");
	}
	/**
	 * @return		A Collection of Strings that represent the types of clearings "from" where you are doing it,
	 * 				and "to" where you are doing it.  For example, MM means from Mt to Mt (Enh. Peer)
	 */
	public ArrayList<String> getCurrentActionTypeCodes() {
		return getList(getCurrentDayKey()+"C");
	}
	/**
	 * @return		A Collection of "T" or "F", where "T" means the action is valid at the time of record
	 */
	public Collection<String> getCurrentActionValids() {
		return getList(getCurrentDayKey()+"V");
	}
	public void setCurrentActionValids(ArrayList<String> in) {
		setList(getCurrentDayKey()+"V",in);
	}
	/**
	 * @param in	Set the current action list for the current day
	 */
	public void setCurrentActions(ArrayList<String> in) {
		setList(getCurrentDayKey(),in);
	}
	public void addCurrentAction(String action) {
		addListItem(getCurrentDayKey(),action);
	}
	public void addCurrentActionValid(boolean val) {
		addListItem(getCurrentDayKey()+"V",val?"T":"F");
	}
	public void addActionPerformedToday(String action,ActionState state,String message,DieRoller roller) {
		String prefix=" ";
		switch(state) {
			case Completed:
				prefix="C";
				break;
			case Cancelled:
				prefix="X";
				break;
			case Invalid:
				prefix="I";
				break;
			default:
				break;
		}
		addListItem(getCurrentDayKey()+"P",prefix+action);
		addListItem(getCurrentDayKey()+"M",message==null?"":message);
		addListItem(getCurrentDayKey()+"R",roller==null?"":roller.getStringResult());
	}
	public boolean hasDoneActionsToday() {
		ArrayList<String> current = getCurrentActions();
		if (current!=null && current.size()>0){
			String firstAction = current.get(0);
			ActionId id = CharacterWrapper.getIdForAction(firstAction);
			if (id==ActionId.Follow) return true; // If you followed someone, you've done actions.
		}
		ArrayList<String> list = getList(getCurrentDayKey()+"P");
		return list!=null && list.size()>0;
	}
	public ActionState getStateForAction(String action,int index) {
		ActionState state = ActionState.Pending; // default
		ArrayList<String> list = getList(getCurrentDayKey()+"P");
		if (list!=null && index<list.size()) {
			String val = list.get(index);
			if (action.equals(val.substring(1))) { // not sure why it wouldn't...
				String token = val.substring(0,1);
				if ("C".equals(token)) {
					state = ActionState.Completed;
				}
				else if ("X".equals(token)) {
					state = ActionState.Cancelled;
				}
				else if ("I".equals(token)) {
					state = ActionState.Invalid;
				}
			}
		}
		return state;
	}
	public String getMessageForAction(int index) {
		String message = null;
		ArrayList<String> list = getList(getCurrentDayKey()+"M");
		if (list!=null && index<list.size()) {
			message = list.get(index);
		}
		return message;
	}
	public DieRoller getRollerForAction(int index) {
		DieRoller roller = null;
		ArrayList<String> list = getList(getCurrentDayKey()+"R");
		if (list!=null && index<list.size()) {
			String stringResult = list.get(index);
			if (stringResult.length()>0) {
				roller = new DieRoller(stringResult,25,6);
			}
		}
		return roller;
	}
	/**
	 * @see #getCurrentActionTypeCodes()
	 */
	public void setCurrentActionTypeCodes(ArrayList<String> in) {
		setList(getCurrentDayKey()+"C",in);
	}
	public void addCurrentActionTypeCode(String code) {
		addListItem(getCurrentDayKey()+"C",code);
	}
	
	/**
	 * Returns the planned clearing, assuming all actions are valid.  This method DOES take into account
	 * the actions that are currently recorded.
	 */
	public TileLocation getPlannedLocation() {
		TileLocation ret = null;
		if (clearingPlot!=null && !clearingPlot.isEmpty()) {
			ret = clearingPlot.get(clearingPlot.size()-1);
		}
		else {
			ret = getCurrentLocation();
		}
		
		return ret;
	}
	/**
	 * @return		The clearing the character is currently occupying.
	 */
	public TileLocation getCurrentLocation() {
		TileLocation tl = ClearingUtility.getTileLocation(getGameObject());
		return tl;
	}
	
	public ClearingDetail getCurrentClearing(){
		return getCurrentLocation().clearing;
	}
	
	public TileComponent getCurrentTile() {
		TileComponent tile = (TileComponent)RealmComponent.getRealmComponent(getGameObject().getHeldBy());
		return tile;
	}
	public void moveToLocation(JFrame frame,TileLocation location) {
		// Clear out any plain sight items
		TileLocation current = getCurrentLocation();
		if (current!=null && current.isInClearing()) {
			for(RealmComponent rc:current.clearing.getClearingComponentsInPlainSight(this)) {
				if (rc.isAtYourFeet(this)) {
					rc.clearAtYourFeet();
				}
			}
		}
		
		// Move
		ClearingUtility.moveToLocation(getGameObject(),location);
		
		// Make sure history is updated
		addMoveHistory(location);
		
		// Inactivate horses if entering a cave or river
		if (location!=null&&location.isInClearing()) {
			boolean cave = location.clearing.isCave();
			boolean water = location.clearing.isWater();
			for (GameObject inv : getInventory()) {
				RealmComponent rc = RealmComponent.getRealmComponent(inv);
				if ((cave || water) && (rc.isHorse() || rc.isNative()) && rc.isActivated() && !rc.getGameObject().hasThisAttribute(Constants.STEED_IN_CAVES_AND_WATER)) {
					if (frame!=null) {
						String clearing = "cave";
						if (water) {
							clearing = "river";
						}
						JOptionPane.showMessageDialog(frame,"Your "+inv.getName()+" was inactivated on entering the "+clearing+".","",JOptionPane.WARNING_MESSAGE);
					}
					rc.setActivated(false);
					if (inv.hasThisAttribute(Constants.BREAK_CONTROL_WHEN_INACTIVE)) {
						SpellMasterWrapper spellmaster = SpellMasterWrapper.getSpellMaster(getGameData());
						for (SpellWrapper spell : spellmaster.getAffectingSpells(inv)) {
							if (spell.isControlHorseSpell()) spell.expireSpell();
						}
					}
				}
				else if (rc.isGoldSpecial()) {
					GoldSpecialChitComponent gs = (GoldSpecialChitComponent)rc;
					if (gs.isComplete(this,location)) {
						gs.gainReward(this);
						gs.expireEffect(this);
						if (!gs.isTask()) {
							location.clearing.add(inv,null);
							GameClient.broadcastClient("host",gs.getGameObject().getName()+" is dropped in "+location);
						}
					}
				}
			}
		}
		
		if (getGameObject().hasThisAttribute(Constants.LOST_IN_THE_MAZE)) {
			if (location==null || location.clearing==null) {
				getGameObject().removeThisAttribute(Constants.LOST_IN_THE_MAZE);
			}
			else {
				boolean mazeIsHere = false;
				for (RealmComponent tl : location.clearing.getTreasureLocations()) {
					if (tl.getGameObject().hasThisAttribute(Constants.MAZE)) {
						mazeIsHere = true;
						break;
					}
				}
				if (!mazeIsHere) {
					getGameObject().removeThisAttribute(Constants.LOST_IN_THE_MAZE);
				}
			}
		}
	}
	public void checkForLostInTheMaze(TileLocation current) {
		// Lost in the Maze rule for Super Realm
		if (current!=null && current.hasClearing() && hasCharacterTileAttribute(Constants.SP_MOVE_IS_RANDOM) && !affectedByKey(Constants.REALM_MAP)) {
			for (RealmComponent tl : current.clearing.getTreasureLocations()) {
				if (tl.getGameObject().hasThisAttribute(Constants.MAZE)) {
					getGameObject().setThisAttribute(Constants.LOST_IN_THE_MAZE);
					for (CharacterWrapper follower : getActionFollowers()) {
						follower.getGameObject().setThisAttribute(Constants.LOST_IN_THE_MAZE);
					}
					break;
				}
			}
		}
	}
	public void applySunset() {
		logger.fine("applySunset");
		if (isSleep()) {
			// Waking up
			setSleep(false);
			for (CharacterActionChitComponent chit : getFatiguedChits()) {
				chit.makeActive();
			}
		}
		
		clearActionBuffer(); // No need to hang onto this stuff forever
		
		// Expire day spells
		for (SpellWrapper spell : getAliveSpells()) {
			// Day spells are over at sunset
			if (spell.isDaySpell()) {
				spell.expireSpell();
			}
		}
		
		// Unblock so that day end trades are good
		setBlocked(false);
		
		// End any pony locks
		setPonyLock(false);
		
		// End any stop following commands
		setStopFollowing(false);
	}
	public void applyMidnight() {
		if (isGone()) {
			return;
		}
		TileLocation current = getCurrentLocation();
		
		logger.fine("applyMidnight");
//		setHidden(false); // This doesn't happen at midnight:  happens at the start of the player turn (rule 8.3)
		setBlocked(false);
		setBlocking(false);
		setNoSummon(false);
		setPeerAny(false);
		setStormed(false);
		setLostPhases(0);
		setFortified(false);
		setFortDamaged(false);
		setNeedsQuestCheck(true);
		setDiscardedQuests(false);
		clearActionFollowers();
		removeAttribute(RELCHANGE_GROUP_LIST);
		removeRunAwayLastUsedChit();
		getGameObject().removeThisAttribute(Constants.SAILS_LAST_CLEARING);
		
		if (getPonyGameObject()!=null) {
			ArrayList<RealmComponent> fhList = getFollowingHirelings();
			if (!fhList.isEmpty()) {
				for (RealmComponent fh:fhList) {
					BattleHorse fhHorse = fh.getHorse();
					if (fhHorse==null || !fhHorse.doublesMove()) {
						// By default, set pony lock to true if you have underling that can't follow you on a pony
						setPonyLock(true);
						break;
					}
				}
			}
		}
		
		markAllInventoryNotNew();
		ArrayList<GameObject> inv = getInventory();
		for (GameObject item : inv) {
			RealmComponent rc = RealmComponent.getRealmComponent(item);
			if (rc.isWeapon()) {
				// Unalert weapons
				((WeaponChitComponent)rc).setAlerted(false);
			}
			else if (rc.isGoldSpecial()) {
				GoldSpecialChitComponent gs = (GoldSpecialChitComponent)rc;
				if (!gs.isMission() && !gs.isCampaign()) continue;
				boolean drop = false;
				// Evaluate a satisfied condition
				if (gs.isComplete(this,current)) {
					gs.gainReward(this);
					gs.expireEffect(this);
					if (!gs.isTask()) {
						drop = true;
					}
				}
				else {
					// Decrement "daysLeft" on any gold specials
					int daysLeft = rc.getGameObject().getThisInt("daysLeft");
					daysLeft--;
					if (daysLeft<=0) {
						// The chit should expire (undo its effect) and drop into the clearing.
						GameClient.broadcastClient(getGameObject().getName(),"Failed to complete "+gs.getGameObject().getName()+" by the required time limit.");
						gs.makePayment(this); // when it expires (instead of being completed) you have to pay a fine
						addFailedGoldSpecial(gs);
						gs.expireEffect(this);
						drop = true;
						QuestRequirementParams qp = new QuestRequirementParams();
						qp.actionType = CharacterActionType.FailMissionCampaign;
						qp.actionName = getGameObject().getName();
						qp.targetOfSearch = getGameObject();
						addPostQuestParams(qp);
					}
					else {
						rc.getGameObject().setThisAttribute("daysLeft",daysLeft);
					}
				}
								
				if (drop) {
					if (current.isInClearing()) {
						GameClient.broadcastClient("host",gs.getGameObject().getName()+" is dropped in "+current);
						current.clearing.add(gs.getGameObject(),null);
					}
					// what to do otherwise?  random clearing?
				}
			}
		}
		
		// Fatigue alerted chits
		boolean noFatigue = 
				getGameObject().hasAttribute(Constants.OPTIONAL_BLOCK,Constants.NO_MAGIC_FATIGUE)
				|| affectedByKey(Constants.NO_MAGIC_FATIGUE);
		for (CharacterActionChitComponent chit : getAlertedChits()) {
			if (noFatigue) {
				chit.makeActive();
			}
			else {
				chit.makeFatigued();
				RealmUtility.reportChitFatigue(this,(RealmComponent)chit,"Fatigued alerted chit: ");
			}
		}
		
		// Remove found hidden enemies list/flag
		setFoundHiddenEnemies(false);
		
		// Heal curses at Chapel
		if (current.hasClearing() && !current.isBetweenClearings()) {
			for (RealmComponent rc : current.clearing.getClearingComponents()) {
				if (rc.isDwelling()) {
					if (rc.getGameObject().getName().startsWith("Chapel") || rc.getGameObject().hasThisAttribute(Constants.REMOVE_CURSES)) { // In case there is a Chapel B
						RealmLogging.logMessage(getGameObject().getName(),"Spent the night at the Chapel.");
						removeAllCurses();
						deactivateAllEvilObjects();
						break;
					}
				}
			}
		}
		
		// Remove any enhanced vulnerability (ie., the BERSERKER)
		getGameObject().removeThisAttribute(Constants.ENHANCED_VULNERABILITY);
		
		// Expire combat spells
		for (SpellWrapper spell : getAliveSpells()) {
			// Combat spells are over at midnight
			if (spell.isCombatSpell()) {
				spell.expireSpell();
			}
		}
		
		// Expire potions
		for (GameObject item:getActivatedTreasureObjects()) {
			if (item.hasThisAttribute(Constants.POTION)) {
				expirePotion(item);
			}
		}
		/**
		 * All potion effects (original game):
		 * 
		 * 	no_effort_limit
		 *  asterisk_speed
		 *  wounds_to_fatigue
		 *  rest_double
		 *  wish_and_curse (immediate)
		 *  cancel_spell (immediate) or curse
		 * 	
		 */
		
		// Expire Battling Natives
		if (getGameObject().hasAttributeBlock(BATTLING_NATIVE_BLOCK)) {
			OrderedHashtable<String, Object> hash = getGameObject().getAttributeBlock(BATTLING_NATIVE_BLOCK);
			for (String nativeGroup : hash.keySet()) {
				getGameObject().removeAttribute(BATTLING_NATIVE_BLOCK,nativeGroup);
			}
		}
	}
	public void expirePotion(GameObject potion) {
		GameObject discardTarget = TreasureUtility.handleExpiredPotion(potion);
		GameObject trader = discardTarget;
		if (discardTarget!=null && discardTarget.hasThisAttribute("dwelling")) {
			trader = SetupCardUtility.getDwellingLeader(discardTarget);
		}
		if (trader!=null) {
			Note note = getNoteTrade(trader);
			if (note==null) {
				addNote(trader,"Trade",potion.getName());
			}
			else {
				ArrayList<String> list = note.getNoteAsList();
				if (!list.contains(potion.getName())) {
					list.add(potion.getName());
				}
				addNote(trader,"Trade",list);
			}
		}
		updateChitEffects();
	}
	public boolean canWalkWoods(TileComponent tile, ClearingDetail fromClearing, ClearingDetail toClearing) {
		String condition = null;
		
		if (tile.isValley() && this.isValeWalker()) return true;
		if (canWaterRun(fromClearing,toClearing)) return true;
		
		GameObject transmorph = getTransmorph();
		if (transmorph!=null) {
			condition = transmorph.getThisAttribute(Constants.WALK_WOODS);
		}
		else {
			condition = getGameObject().getThisAttribute(Constants.WALK_WOODS);
		}
		
		if (condition==null || condition.trim().length()!=0) {
			ArrayList<GameObject> inventory = this.getActiveInventoryAndTravelers();
			for (GameObject item : inventory) {
				String itemCondition = item.getThisAttribute(Constants.WALK_WOODS);
				if (itemCondition != null && (condition==null || (condition.trim().length()!=0 && itemCondition.trim().length()==0))) {
					condition = itemCondition;
					break;
				}
			}
		}
		
		if (condition!=null) {
			if (condition.trim().length()==0) {
				// if condition is empty, then we are good to go
				return true;
			}
			// not empty?  there are conditions we need to verify
			if ("7th".equals(condition)) {
				return RealmCalendar.isSeventhDay(getCurrentDay());
			}
		}
		return false;
	}
	
	public boolean canWaterRun(ClearingDetail fromClearing, ClearingDetail toClearing) {
		if ((fromClearing!=null && fromClearing.isWater() && (toClearing==null || !toClearing.isMountain())) || (toClearing!=null && toClearing.isWater()) ) {
			if (this.getGameObject().hasThisAttribute(Constants.WATER_RUN)) return true;
			Collection<CharacterActionChitComponent> moveChits = this.getActiveMoveChits();
			for (CharacterActionChitComponent chit : moveChits) {
				if (chit.getGameObject().hasThisAttribute(Constants.WATER_RUN)) return true;
			}
		}
		return false;
	}
	
	/**
	 * Can move on secret passages or hidden paths without discovering.
	 * Cannot be targeted by attacks.
	 * Immune to all spells.
	 */
	public boolean isMistLike() {
		GameObject transmorph = getTransmorph();
		if (transmorph!=null) {
			return transmorph.hasThisAttribute(Constants.MIST_LIKE);
		}
		return getGameObject().hasThisAttribute(Constants.MIST_LIKE);
	}
	
	//can walk woods in valley tiles
	public boolean isValeWalker(){
		return this.getGameObject().hasThisAttribute(Constants.VALE_WALKER);
	}
	
	//can walk through secret paths, but doesn't learn them
	public boolean isSpiritGuided(){
		return this.getGameObject().hasThisAttribute(Constants.SPIRIT_GUIDE);
	}
	
	public boolean hasMeditatePathsAndPassages(){
		return this.getGameObject().hasThisAttribute(Constants.MEDITATE_PATHS_AND_PASSAGES);
	}
	/**
	 * Returns all the clearings that are free and clear (ie., paths known, availablity, etc.)
	 */
	public ArrayList<ClearingDetail> findAvailableClearingMoves() {
		return findAvailableClearingMoves(false);
	}
	public ArrayList<ClearingDetail> findAvailableClearingMoves(boolean ignoreActionPhaseCheck) {
		ArrayList<ClearingDetail> ret = new ArrayList<>();
		TileLocation tl = getPlannedLocation();
		if (tl.hasClearing()) {
			if (tl.isBetweenClearings()) { // only two options in this case!
				boolean one = false;
				if (ignoreActionPhaseCheck || canMoveToClearing(tl.clearing)) {
					ret.add(tl.clearing);
					one = true;
				}
				if (ignoreActionPhaseCheck || canMoveToClearing(tl.getOther().clearing)) {
					ret.add(tl.getOther().clearing);
					one = true;
				}
				if (!one) {
					ret.add(tl.getOther().clearing); // I think this is always the clearing you ran FROM
				}
			}
			else {
				if (canWalkWoods(tl.tile,null,null)) {
					// add ALL the clearings in the tile
					ret.addAll(tl.tile.getClearings());
					ret.addAll(tl.tile.getMapEdges());
				}
				else {
					for (ClearingDetail clearing : tl.tile.getClearings()) {
						if (canWalkWoods(tl.tile,tl.clearing,clearing)) {
							ret.add(clearing);
						}
					}
				}
				ArrayList<PathDetail> paths = new ArrayList<>();
				ArrayList<PathDetail> cPaths = tl.clearing.getConnectedPaths();
				if (cPaths!=null) {
					paths.addAll(cPaths);
				}
				cPaths = tl.clearing.getConnectedMapEdges();
				if (cPaths!=null) {
					paths.addAll(cPaths);
				}
				for (PathDetail path:paths) {
					if (validPath(path)) {
						ClearingDetail connectedClearing = path.findConnection(tl.clearing);
						if (connectedClearing==null) {
							connectedClearing = path.getEdgeAsClearing();
						}
						if (!ret.contains(connectedClearing)) {
							// Test that the connectedClearing CAN be moved to:
							if (ignoreActionPhaseCheck || canMoveToClearing(connectedClearing)) {
								ret.add(connectedClearing);
							}
						}
					}
				}
			}
		}
		else if (tl.isTileOnly() && !tl.isFlying()) {
			// add ALL the clearings in the tile
			ret.addAll(tl.tile.getClearings());
		}
		return ret;
	}
	/**
	 * Returns all the clearings that are available assuming a future discovery is made (hidden path or passage)
	 */
	public ArrayList<ClearingDetail> findPossibleClearingMoves() {
		ArrayList<ClearingDetail> ret = new ArrayList<>();
		TileLocation tl = getPlannedLocation();
		if (tl.hasClearing() && !tl.isBetweenClearings()) {
			Collection<PathDetail> c = tl.clearing.getConnectedPaths();
			if (c!=null) {
				for (PathDetail path : c) {
					if (!validPath(path)) {
						ClearingDetail connectedClearing = path.findConnection(tl.clearing);
						// Test that the connectedClearing CAN be moved to:
						if (canMoveToClearing(connectedClearing)) {
							ret.add(connectedClearing);
						}
					}
				}
			}
		}
		else {
			// FIXME Handle characters that are walking woods
		} // flying characters are in the air, and thus have no possible clearings
		return ret;
	}
	/**
	 * @return		true if character can traverse path
	 */
	public boolean validPath(PathDetail path) {
		boolean mistLike = isMistLike();
		if (!mistLike && path.getTo().connectionHasThorns(path.getFrom())) {
			return false;
		}
		
		if (!path.requiresDiscovery() || isSpiritGuided() || hasMeditatePathsAndPassages()) return true;
		if (path.connectsToMapEdge()) return true;
		if (!path.connectsToMapEdge() && canWalkWoods(path.getFrom().getParent(),path.getFrom(),path.getTo())) return true;
		
		if (path.isHidden() && (hasHiddenPathDiscovery(path.getFullPathKey()) || mistLike || moveRandomly())) return true;
		if (path.isSecret() && (hasSecretPassageDiscovery(path.getFullPathKey()) || mistLike || moveRandomly())) return true;
		if (path.isHidden() && hasActiveInventoryThisKeyAndValue(Constants.ROAD_KNOWLEDGE,Constants.ROAD_KNOWLEDGE_HIDDEN)){
			return true;
		}
		if (path.isSecret() && hasActiveInventoryThisKeyAndValue(Constants.ROAD_KNOWLEDGE,Constants.ROAD_KNOWLEDGE_SECRET)){
			return true;
		}
		return false;
	}
	public boolean addsOneToMoveExceptCaves() {
		if (affectedByKey(Constants.NONCAVE_MOVE_DISADVANTAGE)) {
			return true;
		}
		if (affectedByKey(Constants.NO_SUNLIGHT)) {
			boolean house = HostPrefWrapper.findHostPrefs(getGameObject().getGameData()).hasPref(Constants.HOUSE1_DWARF_ACTION);
			if (house && !getGameObject().hasThisAttribute(Constants.CUSTOM_CHARACTER)) {
				return true;
			}
		}
		return false;
	}
	public boolean canUseSunlightPhases() {
		if (affectedByKey(Constants.NO_SUNLIGHT)) {
			boolean house = HostPrefWrapper.findHostPrefs(getGameObject().getGameData()).hasPref(Constants.HOUSE1_DWARF_ACTION);
			if (house && !getGameObject().hasThisAttribute(Constants.CUSTOM_CHARACTER)) {
				return true;
			}
			return false;
		}
		return true;
	}
	public boolean canMoveToClearing(ClearingDetail clearing) {
		PhaseManager pm = getPhaseManager(true);
		updatePhaseManagerWithCurrentActions(pm);
		String testMove = "M";
		int cost = clearing.moveCost(this,getPlannedLocation());
		if (cost>1) {
			for (int i=1;i<cost;i++) {
				testMove = testMove+",M";
			}
		}
		if (clearing.isCave() && clearing.isLighted()) {
			pm.markInCave(true);
		}
		else if (clearing.isCave()) {
			pm.markInCave();
		}
		else if (!clearing.holdsDwelling()) {
			// If the clearing you are moving to is outside, and you've already used sheltered phases, then this is a no go.
			if (pm.hasUsedSheltered()) {
				return false;
			}
		}
		return pm.canAddAction(testMove,isPonyActive());
	}
	public ArrayList<GameObject> getAllOpenableSites() {
		ArrayList<GameObject> openable = new ArrayList<>();
		TileLocation tl = getCurrentLocation();
		if (tl!=null && tl.hasClearing()) {
			Collection<RealmComponent> c = tl.clearing.getClearingComponents();
			for (RealmComponent rc : c) {
				GameObject thing = rc.getGameObject();
				// Does it need to be opened?
				if (thing.hasThisAttribute(Constants.NEEDS_OPEN)) {
					// make sure this site has been discovered
					if (hasTreasureLocationDiscovery(thing.getName())) {
						openable.add(thing);
					}
				}
			}
		}
		return openable;
	}
	public RealmComponent getActiveBoots() {
		Collection<GameObject> inv = getInventory();
		for (GameObject go : inv) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isActivated() && rc.isTreasure() && go.hasThisAttribute("boots")) {
				return rc;
			}
		}
		return null;
	}
	public BattleHorse getActiveSteed() {
		return getActiveSteed(-1);
	}
	public BattleHorse getActiveSteed(int attackOrderPos) {
		BattleHorse steed = null;
		Collection<GameObject> inv = getInventory();
		for (GameObject go : inv) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if ((rc.isNativeHorse() && !go.hasThisAttribute(RealmComponent.MONSTER_STEED)) || (rc.isActivated() && rc.isHorse())) {
				steed = (BattleHorse)rc;
				CombatWrapper horseCombat = new CombatWrapper(go);
				if ((horseCombat.getKilledBy()!=null && !(horseCombat.getHitByOrderNumber()==attackOrderPos))
						|| rc.getGameObject().hasThisAttribute(Constants.DEAD)) {
					// A dead horse, does not an active steed make!
					steed = null;
				}
				break;
			}
		}
		return steed;
	}
	public WeaponChitComponent getActivePrimaryWeapon() {
		if (getTransmorph()==null) {
			Collection<GameObject> inv = getInventory();
			for (GameObject go : inv) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (rc.isActivated() && rc.isWeapon()) {
					return (WeaponChitComponent)rc;
				}
			}
		}
		return null;
	}
	public ArrayList<WeaponChitComponent> getActiveWeapons() {
		if (getTransmorph()==null) {
			Collection<GameObject> inv = getInventory();
			ArrayList <WeaponChitComponent> list = new ArrayList<>();
			for (GameObject go : inv) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (rc.isActivated() && rc.isWeapon()) {
					list.add((WeaponChitComponent)rc);
				}
			}
			return list;
		}
		return null;
	}
	public Collection<GameObject> getCurrentClearingExtraActionObjects() {
		ArrayList<GameObject> list = new ArrayList<>();
		TileLocation current = getCurrentLocation(); // must be in the same clearing when recording
		if (current!=null && current.hasClearing() && !current.isBetweenClearings()) {
			// Now we can search for this case
			for (RealmComponent rc : current.clearing.getClearingComponents()) {
				String free = rc.getGameObject().getThisAttribute(Constants.EXTRA_ACTIONS_CLEARING);
				if (free!=null) {
					list.add(rc.getGameObject());
				}
			}
		}
		return list;
	}
	public boolean canUseInstantTeleport() {
		TileLocation loc = getCurrentLocation();
		if(loc == null || loc.clearing==null) {
			return false;
		}
		for (RealmComponent tl : loc.clearing.getTreasureLocations()) {
			if (tl.getGameObject().hasThisAttribute(Constants.TELEPORT_TO_LOCATION) && hasTreasureLocationDiscovery(tl.getGameObject().getName())) {
				return true;
			}
		}
		return false;
	}
	public boolean hasSpecialActions() {
		return !getSpecialActions().isEmpty();
	}
	public boolean hasSpecialAction(ActionId id) {
		String name = DayAction.getDayAction(id).getName().toUpperCase();
		ArrayList<String> specialActions = getSpecialActions();
		return specialActions.contains(name);
	}
	private ArrayList<String> getSpecialActions() {
		ArrayList<String> specialActions = new ArrayList<>();
		if (getGameObject().hasThisAttribute(Constants.SPECIAL_ACTION)) {
			specialActions.addAll(getGameObject().getThisAttributeList(Constants.SPECIAL_ACTION));
		}
		specialActions.addAll(getActiveInventoryValuesForThisKey(Constants.SPECIAL_ACTION,null));
		return specialActions;
	}
	public PhaseManager getPhaseManager(boolean useClearingPlot) {
		PhaseManager pm = new PhaseManager(this,getPonyGameObject(),getBasicPhases(),getSunlightPhases(),getShelteredPhases());
		pm.setPonyLock(isPonyLock());
		if (affectedByKey(Constants.EXTRA_DWELLING_PHASE)) {
			pm.addExtraDwellingPhase();
		}
		
		if (getGameObject().hasThisAttribute(Constants.EXTRA_CAVE_PHASE)) {
			pm.addExtraCavePhase(getGameObject());
		}
		
		if(getGameObject().hasThisAttribute(Constants.TORCH_BEARER)){
			pm.addExtraCavePhase(getGameObject());
		}		
		
		ArrayList<String> extra = getGameObject().getThisAttributeList(Constants.EXTRA_ACTIONS);
		if (extra!=null) {
			for (String extraAction : extra) {
				pm.addFreeAction(extraAction,getGameObject(),null,true); // force character actions on the phase manager
			}
		}
		if (getGameObject().hasThisAttribute(Constants.EXTRA_PHASE)) {
			pm.addExtraBasicPhase();
		}
		if (getGameObject().hasThisAttribute(Constants.MEDITATE_EXTRA_PHASE)) {
			pm.addExtraBasicPhase();
		}
		ArrayList<String> spellExtras = getSpellExtras();
		if (spellExtras!=null) {
			ArrayList<GameObject> spellExtraSources = getSpellExtraSources();
			if (spellExtraSources!=null) { // should never be NULL, but if it is, I don't want to break the game
				Iterator<GameObject> ss = spellExtraSources.iterator();
				for (String spellExtra : spellExtras) {
					GameObject source = ss.next();
					pm.addFreeAction(spellExtra,source);
				}
			}
		}
		TileLocation current = getCurrentLocation();
		boolean cave = current!=null && current.isInClearing() && current.clearing.isCave();
		boolean water = current!=null && current.isInClearing() && current.clearing.isWater();
		
		// search active treasures to determine if any items provide a free action
		for (GameObject item:getEnhancingItemsAndNomads()) {
			ArrayList<String> free = item.getThisAttributeList(Constants.EXTRA_ACTIONS);
			if (free!=null) {
				for (String freeAction : free) {
					pm.addFreeAction(freeAction,item,null,true);
				}
			}
			if (item.hasThisAttribute(Constants.EXTRA_CAVE_PHASE)) {
				pm.addExtraCavePhase(item);
			}
		}
		// search clearing for free actions
		// must be in the same clearing when recording
		// ... and when using!
//		TileLocation planned = getPlannedLocation(); 
		
		// Does character have a horse active?
		BattleHorse steed = getActiveSteed();
		if (steed!=null && steed.extraMove() && !cave && !water) {
			pm.addFreeAction(DayAction.getDayAction(ActionId.Move).getCode(),steed.getGameObject());
		}
		
		if (useClearingPlot) {
			ArrayList<TileLocation> cp = getClearingPlot();
			ArrayList<TileLocation> list = new ArrayList<>();
			if (cp!=null && !cp.isEmpty()) {
				list.addAll(cp);
			}
			if (list.isEmpty()) {
				list.add(getCurrentLocation());
			}
			for (TileLocation tl : list) {
				pm.updateClearing(tl);
			}
		}
		
		return pm;
	}
	public void updatePhaseManagerWithCurrentActions(PhaseManager pm) {
		TileLocation current = getCurrentLocation();
		if (getClearingPlot()!=null) {
			boolean pony = isPonyActive();
			int moveNumber = 0;
			TileLocation loc = current;
			HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameData());
			for (String action : getCurrentActions()) {
				boolean inCave = false;
				boolean inWater = false;
				boolean onMountain = false;
				loc = getClearingPlot().get(moveNumber);
				if (action.startsWith("M") || action.startsWith("FLY")) {
					inCave = loc!=null && loc.isInClearing() && loc.clearing.isCave();
					inWater = loc!=null && loc.isInClearing() && loc.clearing.isWater();
					if (hostPrefs.hasPref(Constants.FE_PONY_NO_MOUNTAINS) &&!inCave&&!inWater) {
						TileLocation newLocation = ClearingUtility.deduceLocationFromAction(getGameData(),action);
						onMountain = newLocation!=null && newLocation.isInClearing() && newLocation.clearing.isMountain();
					}
					moveNumber++;
				}
				pm.forcePerformedAction(action,pony&&!inCave&&!inWater&&!onMountain,loc);
			}
		}
		pm.removeLocationSpecificFreeActions(getPlannedLocation());
	}
	public GameObject getPonyGameObject() {
		BattleHorse steed = getActiveSteed();
		if (steed!=null && steed.doublesMove()) {
			return steed.getGameObject();
		}
		return null;
	}
	public boolean isPonyActive() {
		BattleHorse steed = getActiveSteed();
		if (steed!=null && steed.doublesMove()) {
			return true;
		}
		return false;
	}
	public boolean actionIsValid(String action,TileLocation location) {
		ActionId id = getIdForAction(action);
		
		GameObject transmorph = getTransmorph();

		// Test move types
		if (id==ActionId.Move && mustFly()) {
			return false;
		}
		if (id!=ActionId.Move && location!=null && location.isTileOnly() && !location.isFlying()) {
			return false;
		}
		if (id==ActionId.Fly && !canFly(location)) {
			return false;
		}
		if (location!=null && location.isBetweenTiles() && location.isFlying() && id==ActionId.Fly) {
			return true;
		}
		
		// Test hide
		if (id==ActionId.Hide) {
			RealmCalendar cal = RealmCalendar.getCalendar(getGameObject().getGameData());
			if (cal.isHideDisabled(getCurrentMonth())) {
				HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameData());
				boolean ableToIgnoreDisableHide = hostPrefs.hasPref(Constants.HOUSE3_SNOW_HIDE_EXCLUDE_CAVES) && location!=null && location.isInClearing() && location.clearing.isCave();
				if (!ableToIgnoreDisableHide) {
					return false;
				}
			}
		}
		
		if (location!=null) {
			if (location.isBetweenClearings() && id!=ActionId.Move && id!=ActionId.Fly) {
				return false;
			}
			if (location.isBetweenTiles() && id!=ActionId.Fly) {
				return false;
			}
		}
		
		// Test character specific restrictions
		if (isHiredLeader() || isControlledMonster()) {
			// hired leaders and controlled monsters can record REST and ALERT, in the case they have followers
			if (id==ActionId.Spell
					|| id==ActionId.SpellPrep
					|| id==ActionId.RemSpell) {
				return false;
			}
		}
		else if (isFamiliar()) {
			if (id!=ActionId.Move
					&& id!=ActionId.Follow
					&& id!=ActionId.EnhPeer) {
				return false;
			}
		}
		else if (isPhantasm()) {
			if (id!=ActionId.Move
					&& id!=ActionId.Spell
					&& id!=ActionId.SpellPrep
					&& id!=ActionId.EnhPeer) {
				return false;
			}
		}
		else if (isMistLike()) {
			if (id!=ActionId.Move
					&& id!=ActionId.Hide
					&& id!=ActionId.Follow) {
				return false;
			}
		}
		else if (transmorph!=null) {
			if (id==ActionId.Trade
					|| id==ActionId.Hire
					|| id==ActionId.Spell
					|| id==ActionId.SpellPrep
					|| id==ActionId.EnhPeer
					|| id==ActionId.RemSpell) {
				return false;
			}
		}
		if (id==ActionId.Follow && location!=null) {
			// Can't follow unless its the first and only action
			if (getCurrentActions()==null || getCurrentActions().isEmpty()) {
				// Must be in a clearing
				if (location.hasClearing() && !location.isBetweenClearings()) {
					// Must be someone to follow
					for (RealmComponent rc : location.clearing.getClearingComponents()) {
						// Someone, that isn't yourself (character or native leader only!)
						if (rc.isPlayerControlledLeader() && !rc.getGameObject().equals(getGameObject()) && !rc.getGameObject().hasThisAttribute(Constants.CAMOUFLAGE)) {
							// Only one is required!
							return true;
						}
					}
				}
			}
			return false;
		}
		else if (id==ActionId.EnhPeer && !isMinion() && location!=null) {
			HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameObject().getGameData());
			boolean flyingActivity = hostPrefs.hasPref(Constants.ADV_FLYING_ACTIVITIES) && getCurrentActionsCodes().contains(DayAction.FLY_ACTION.getCode());
			if (flyingActivity) {
				return true;
			}
			boolean enhancedPeer = hasActiveInventoryThisKeyAndValue(Constants.SPECIAL_ACTION,"ENHANCED_PEER");
			boolean mtToMtPeer = location.hasClearing() && location.clearing.isMountain() && hasActiveInventoryThisKey(Constants.MOUNTAIN_PEER);
			if (!enhancedPeer && !mtToMtPeer) {
				return false;
			}
			return true;
		}
		if (id==ActionId.RemSpell) {
			if (!hasActiveInventoryThisKeyAndValue(Constants.SPECIAL_ACTION,"REMOTE_SPELL")) {
				return false;
			}
		}
		
		if (id==ActionId.Cache) {
			HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameObject().getGameData());
			return hostPrefs.hasPref(Constants.ADV_CACHING);
		}
		
		if (id==ActionId.Rest) {
			return !hasActiveInventoryThisKey(Constants.MAJOR_WOUND);
		}
		
		if (id==ActionId.Repair) {
			return hasActiveInventoryThisKeyAndValue(Constants.SPECIAL_ACTION,"REPAIR");
		}
		if (id==ActionId.Heal) {
			return hasActiveInventoryThisKeyAndValue(Constants.SPECIAL_ACTION,"HEAL");
		}
		if (id==ActionId.Fortify) {
			return hasActiveInventoryThisKeyAndValue(Constants.SPECIAL_ACTION,"FORTIFY");
		}
		
		return true;
	}
	public void addMinion(GameObject go) {
		addListItem(MINION_ID,go.getStringId());
	}
	public void removeMinion(GameObject go) {
		ArrayList<String> list = getList(MINION_ID);
		if (list!=null && list.contains(go.getStringId())) {
			list = new ArrayList<>(list);
			list.remove(go.getStringId());
			if (list.size()>0) {
				setList(MINION_ID,list);
			}
			else {
				setBoolean(MINION_ID,false);
			}
		}
	}
	/**
	 * A list of minions, which might include a familiar and/or a phantasm
	 */
	public ArrayList<GameObject> getMinions() {
		GameData data = getGameObject().getGameData();
		ArrayList<String> list = getList(MINION_ID);
		if (list!=null && !list.isEmpty()) {
			ArrayList<GameObject> ret = new ArrayList<>();
			for (String id : list) {
				GameObject fam = data.getGameObject(Long.valueOf(id));
				ret.add(fam);
			}
			return ret;
		}
		return null;
	}
	public RealmComponent getFamiliar() {
		ArrayList<GameObject> minions = getMinions();
		if (minions!=null) {
			for (GameObject min:getMinions()) {
				RealmComponent rc = RealmComponent.getRealmComponent(min);
				if (rc.isFamiliar()) {
					return rc;
				}
			}
		}
		return null;
	}
	public int getMinionCount() {
		ArrayList<String> list = getList(MINION_ID);
		return list==null?0:list.size();
	}
	public ArrayList<GameObject> getInventory() {
		ArrayList<GameObject> ret = new ArrayList<>();
		for (GameObject go : getGameObject().getHold()) {
			if (go.hasThisAttribute(Constants.REQUIRES_APPROVAL)) continue;
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc==null) {
				//go._outputDetail();
				System.err.println(go+" has no RC?");
			}
			else if (rc.isItem() || rc.isPhaseChit() || rc.isGoldSpecial() || rc.isBoon() || rc.isMinorCharacter()) {
				ret.add(go);
			}
		}
		return ret;
	}
	public Collection<GameObject> getBoons(GameObject denizen) {
		String nativeName = denizen.getThisAttribute("native");
		if (nativeName==null) {
			nativeName = denizen.getThisAttribute(Constants.VISITOR);
		}
		ArrayList<GameObject> list = new ArrayList<>();
		Collection<GameObject> c = getInventory();
		for (GameObject item : c) {
			if (item.hasThisAttribute("boon")) {
				String toWhom = item.getThisAttribute("boon");
				if (toWhom.equals(nativeName)) {
					list.add(item);
				}
			}
		}
		return list;
	}
	public ArrayList<GameObject> getSellableInventory() {
		ArrayList<GameObject> list = new ArrayList<>();
		Collection<GameObject> c = getInventory();
		for (GameObject item : c) {
			RealmComponent rc = RealmComponent.getRealmComponent(item);
			if (rc.isItem() // must be an item (not a boon, quest, phase chit, other)
					&& !rc.isNativeHorse() // leaders can't sell their own horse!!
					) { // you don't sell boons, you buy them
				if (item.hasThisAttribute(Constants.ACTIVATED) && item.hasThisAttribute("potion")) continue; // No activated potions
				if (item.hasThisAttribute(Constants.ACTIVATED) && item.hasThisAttribute(Constants.CURSED)) continue; // No activated cursed gear
				if (rc.isEnchanted()) continue; // No enchanted artifacts/books
				list.add(item);
			}
		}
		return list;
	}
	/**
	 * @return		A Collection of GameObject objects that represent ALL the inventory that the
	 * 				character can currently carry.
	 */
	public ArrayList<GameObject> getScorableInventory() {
		Strength strength = getMoveStrength(true,false);
		ArrayList<GameObject> carryable = new ArrayList<>();
		
		for (GameObject go:getInventory()) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			Strength itemWeight = rc.getWeight();
			if (strength.strongerOrEqualTo(itemWeight) || rc.isHorse() || rc.isNativeHorse()) {
				carryable.add(go);
			}
		}
		
		return carryable;
	}
	public GameObject getActiveInventoryThisKey(String key) {
		ArrayList<GameObject> inv = getInventory();
		inv.addAll(getFollowingTravelers());
		
		for (GameObject go:inv) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isActivated() && go.hasThisAttribute(key)) {
				return go;
			}
		}
		return null;
	}
	public boolean affectedByKey(String key) {
		return getGameObject().hasThisAttribute(key)
			|| hasActiveInventoryThisKey(key)
			|| SpellUtility.affectedByBewitchingSpellKey(getGameObject(),key);
	}
	
	public ArrayList<GameObject> getActiveInventoryAndTravelers() {
		ArrayList<GameObject> ret = getActiveInventory();
		ret.addAll(getFollowingTravelers());
		ret.add(getGameObject());
		return ret;
	}
	
	public boolean hasActiveInventoryThisKey(String key) {
		return hasActiveInventoryThisKeyAndValue(key,null);
	}
	public boolean hasActiveInventoryThisKeyAndValue(String key,String value) {
		GameQuery query = new GameQuery("this");
		return query.hasGameObjectWithKeyAndValue(getActiveInventoryAndTravelers(),key,value);
	}
	public ArrayList<GameObject> getAllActiveInventoryThisKeyAndValue(String key,String value) {
		GameQuery query = new GameQuery("this");
		return query.allGameObjectsWithKeyAndValue(getActiveInventoryAndTravelers(),key,value);
	}
	public ArrayList<String> getActiveInventoryValuesForThisKey(String key,String delim) {
		ArrayList<String> values = new ArrayList<>();
		for (GameObject item:getAllActiveInventoryThisKeyAndValue(key,null)) {
			Object val = item.getObject("this",key);
			ArrayList list;
			if (val instanceof ArrayList) {
				list = (ArrayList)val;
			}
			else {
				list = new ArrayList();
				list.add(val);
			}
			for (Iterator i=list.iterator();i.hasNext();) {
				String listVal = (String)i.next();
				if (delim==null) {
					values.add(listVal);
				}
				else {
					values.addAll(StringUtilities.stringToCollection(listVal,delim));
				}
			}
		}
		return values;
	}
	public Integer getLowestIntegerForActiveInventoryKey(String key) {
		Integer lowest = null;
		if (getGameObject().hasThisAttribute(key)) {
			lowest = getGameObject().getThisInt(key);
		}
		for (GameObject go:getAllActiveInventoryThisKeyAndValue(key,null)) {
			Integer num = go.getInteger("this",key);
			if (lowest==null || (num!=null&&num.intValue()<lowest.intValue())) {
				lowest = num;
			}
		}
		return lowest;
	}
	public Integer getHighestIntegerForActiveInventoryKey(String key) {
		Integer highest = null;
		if (getGameObject().hasThisAttribute(key)) {
			highest = getGameObject().getThisInt(key);
		}
		for (GameObject go:getAllActiveInventoryThisKeyAndValue(key,null)) {
			Integer num = go.getInteger("this",key);
			if (highest==null || (num!=null&&num.intValue()>highest.intValue())) {
				highest = num;
			}
		}
		return highest;
	}
	public int getReplaceFight() {
		Integer num = getLowestIntegerForActiveInventoryKey(Constants.REPLACE_FIGHT);
		return num==null?0:num.intValue();
	}
	public boolean canReplaceFight(RealmComponent target) {
		ArrayList<RealmComponent> list = new ArrayList<>();
		list.add(target);
		return canReplaceFight(list);
	}
	public boolean canReplaceFight(Collection<RealmComponent> targetComponents) {
		int replaceFight = getReplaceFight();
		Speed speedToBeatFight = null;
		if (replaceFight>0) {
			// if the targets move beats or equals, then no replace fight happens
			speedToBeatFight = new Speed(replaceFight);
			for (RealmComponent chitRc : targetComponents) {
				BattleChit chit = (BattleChit) chitRc;
				if (chit.getMoveSpeed().fasterThanOrEqual(speedToBeatFight)) {
					speedToBeatFight = null;
					break;
				}
			}
		}
		return (speedToBeatFight!=null);
	}
	public int getReplaceMove() {
		Integer num = getLowestIntegerForActiveInventoryKey(Constants.REPLACE_MOVE);
		return num==null?0:num.intValue();
	}
	public boolean canReplaceMove(RealmComponent attacker) {
		ArrayList<RealmComponent> list = new ArrayList<>();
		list.add(attacker);
		return canReplaceFight(list);
	}
	public boolean canReplaceMove(Collection<RealmComponent> attackerComponents) {
		int replaceMove = getReplaceMove();
		Speed speedToBeatMove = null;
		if (replaceMove>0) {
			// if the targets move beats or equals, then no replace move happens
			speedToBeatMove = new Speed(replaceMove);
			for (RealmComponent chitRc : attackerComponents) {
				BattleChit chit = (BattleChit) chitRc;
				if (chit.getAttackSpeed().fasterThanOrEqual(speedToBeatMove)) {
					speedToBeatMove = null;
					break;
				}
			}
		}
		return (speedToBeatMove!=null);
	}
	/**
	 * This method is called at the beginning of every phase of a character's turn, and at midnight, to make sure
	 * that ALL inventory is "not new" for purposes of knowing when they can be activated.  (3ed rule 7.5.5.f)
	 */
	public void markAllInventoryNotNew() {
		Collection<GameObject> inv = getInventory();
		for (GameObject go : inv) {
			if (go.hasThisAttribute(Constants.TREASURE_NEW)) {
				go.removeThisAttribute(Constants.TREASURE_NEW);
			}
		}
	}
	public ArrayList<GameObject> getActiveInventory() {
		ArrayList<GameObject> active = new ArrayList<>();
		Collection<GameObject> inv = getInventory();
		for (GameObject go : inv) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isActivated()) {
				active.add(go);
			}
		}
		return active;
	}
	public ArrayList<GameObject> getInactiveInventory() {
		return getInactiveInventory(false);
	}
	public ArrayList<GameObject> getInactiveInventory(boolean includePhaseChits) {
		ArrayList<GameObject> inactive = new ArrayList<>();
		for (GameObject go:getInventory()) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (!rc.isActivated() && !rc.isBoon() && (includePhaseChits || !rc.isPhaseChit())) {
				inactive.add(go);
			}
		}
		return inactive;
	}
	public ArrayList<GameObject> getDroppableInventory() {
		ArrayList<GameObject> list = new ArrayList<>();
		for (GameObject go:getInventory()) {
			Inventory inv = new Inventory(go);
			if (inv.canDrop()) {
				list.add(go);
			}
		}
		return list;
	}
	/**
	 * Returns all activated treasures and travelers.
	 */
	public ArrayList<GameObject> getEnhancingItems() {
		ArrayList<GameObject> items = getActivatedTreasureObjects();
		items.addAll(getFollowingTravelers());
		return items;
	}
	/**
	 * Returns all activated treasures, nomads and travelers.
	 */
	public ArrayList<GameObject> getEnhancingItemsAndNomads() {
		ArrayList<GameObject> items = getActivatedTreasureObjectsAndNomads();
		items.addAll(getFollowingTravelers());
		return items;
	}
	private ArrayList<GameObject> getActivatedTreasureObjectsAndNomads() {
		ArrayList<GameObject> activatedTreasures = new ArrayList<>();
		Collection<GameObject> inv = getInventory();
		for (GameObject go : inv) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (((rc.isActivated() && (rc.isTreasure() || rc.isMinorCharacter()))) || rc.isNomad()) {
				activatedTreasures.add(go);
			}
		}
		return activatedTreasures;
	}
	public ArrayList<GameObject> getActivatedTreasureObjects() {
		ArrayList<GameObject> activatedTreasures = new ArrayList<>();
		Collection<GameObject> inv = getInventory();
		for (GameObject go : inv) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isActivated() && (rc.isTreasure() || rc.isMinorCharacter())) {
				activatedTreasures.add(go);
			}
		}
		return activatedTreasures;
	}
	public ArrayList<GameObject> getMinorCharacters() {
		ArrayList<GameObject> minorChars = new ArrayList<>();
		for (GameObject go:getActiveInventory()) {
			if (go.hasThisAttribute(Quest.QUEST_MINOR_CHARS)) {
				minorChars.add(go);
			}
		}
		return minorChars;
	}
	public ArrayList<GameObject> getFollowingTravelers() {
		ArrayList<GameObject> travelers = new ArrayList<>();
		for (RealmComponent rc:getFollowingHirelings()) {
			if (rc.isTraveler()) {
				travelers.add(rc.getGameObject());
			}
		}
		return travelers;
	}
	public ArrayList<GameObject> getNomads() {
		ArrayList<GameObject> nomads = new ArrayList<>();
		Collection<GameObject> inv = getInventory();
		for (GameObject go : inv) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isNomad()) {
				nomads.add(go);
			}
		}
		return nomads;
	}
	public void setQuestBonusVps(int val) {
		getGameObject().setAttribute(CharacterWrapper.VICTORY_REQ_BLOCK,CharacterWrapper.QUEST_BONUS_VPS,val);
	}
	public void addQuestBonusVps(int val) {
		getGameObject().setAttribute(CharacterWrapper.VICTORY_REQ_BLOCK,CharacterWrapper.QUEST_BONUS_VPS,getQuestBonusVps()+val);
	}
	public int getQuestBonusVps() {
		return getGameObject().getInt(CharacterWrapper.VICTORY_REQ_BLOCK,CharacterWrapper.QUEST_BONUS_VPS);
	}
	public Score getQuestPointScore() {
		int count = 0;
		ArrayList<GameObject> list = new ArrayList<>();
		for(Quest quest:getAllQuests()) {
			if (quest.getState()==QuestState.Complete) {
				count += quest.getInt(QuestConstants.VP_REWARD);
				list.add(quest.getGameObject());
			}
		}
		count=count+getQuestBonusVps();
		return new Score(0,count,1,getGameObject().getInt(CharacterWrapper.VICTORY_REQ_BLOCK,CharacterWrapper.V_QUEST_POINTS),list);
	}
	public Score getGreatTreasureScore() {
		int count = 0;
		ArrayList<GameObject> list = new ArrayList<>();
		for (GameObject go:getScorableInventory()) {
			if (go.hasThisAttribute("great")) {
				list.add(go);
				count++;
			}
		}
		return new Score(0,count,1,getGameObject().getInt(CharacterWrapper.VICTORY_REQ_BLOCK,CharacterWrapper.V_GREAT_TREASURES),list);
	}
	public Score getUsableSpellScore() {
		int count = 0;
		ArrayList<GameObject> recSpells = getRecordedSpells(getGameObject().getGameData());
		for (GameObject go:recSpells) {
			if (canLearn(go,true)) { // only include spells that are still learnable, in case you learned a spell with an artifact (enhanced magic rules), and that artifact is gone!
				count++;
			}
		}
		return new Score(count,0,2,getGameObject().getInt(CharacterWrapper.VICTORY_REQ_BLOCK,CharacterWrapper.V_USABLE_SPELLS),null);
	}
	public String getFameString() {
		StringBuffer sb = new StringBuffer();
		
		int recordedFame = getRoundedFame();
		if (hasCurse(Constants.DISGUST)) {
			sb.append("-1 (");
			sb.append(recordedFame);
			sb.append(")");
		}
		else {
			sb.append(recordedFame);
		}
		
		return sb.toString();
	}
	public Score getFameScore() {
		int recordedFame = getRoundedFame();
		if (hasCurse(Constants.DISGUST)) {
			recordedFame = -1;
		}
		
		// Add fame from all treasures (not Fame Price!!)
		int treasureFame = 0;
		ArrayList<GameObject> list = new ArrayList<>();
		for (GameObject item : getScorableInventory()) {
			if (!item.hasThisAttribute("native")) { // no Fame Price values allowed!
				if (item.hasThisAttribute("fame")) {
					list.add(item);
					treasureFame += item.getThisInt("fame");
				}
			}
		}
		
		return new Score(recordedFame,treasureFame,10,getGameObject().getInt(CharacterWrapper.VICTORY_REQ_BLOCK,CharacterWrapper.V_FAME),list);
	}
	public String getNotorietyString() {
		StringBuffer sb = new StringBuffer();
		
		int recordedNotoriety = getRoundedNotoriety();
		sb.append(recordedNotoriety);
		
		return sb.toString();
	}
	public Score getNotorietyScore() {
		int notoriety = getRoundedNotoriety();
		
		// Add notoriety from all treasures
		int treasureNot = 0;
		ArrayList<GameObject> list = new ArrayList<>();
		for (GameObject item : getScorableInventory()) {
			if (item.hasThisAttribute("notoriety")) {
				list.add(item);
				treasureNot += item.getThisInt("notoriety");
			}
		}
		return new Score(notoriety,treasureNot,20,getGameObject().getInt(CharacterWrapper.VICTORY_REQ_BLOCK,CharacterWrapper.V_NOTORIETY),list);
	}
	public String getGoldString() {
		StringBuffer sb = new StringBuffer();
		
		int recordedGold = getRoundedGold();
		if (hasCurse(Constants.ASHES)) {
			sb.append("-1 (");
			sb.append(recordedGold);
			sb.append(")");
		}
		else {
			sb.append(recordedGold);
		}
		
		return sb.toString();
	}
	public Score getGoldScore() {
		int recordedGold = getRoundedGold();
		if (hasCurse(Constants.ASHES)) {
			recordedGold = -1;
		}
		int startingWorth = getInt(STARTING_GOLD_VALUE);
		
		return new Score(recordedGold,-startingWorth,30,getGameObject().getInt(CharacterWrapper.VICTORY_REQ_BLOCK,CharacterWrapper.V_GOLD),null);
	}
	public int getTotalAssignedVPs() {
		int vps = 0;
		vps += getQuestPointScore().getAssignedVictoryPoints();
		vps += getGreatTreasureScore().getAssignedVictoryPoints();
		vps += getUsableSpellScore().getAssignedVictoryPoints();
		vps += getFameScore().getAssignedVictoryPoints();
		vps += getNotorietyScore().getAssignedVictoryPoints();
		vps += getGoldScore().getAssignedVictoryPoints();
		return vps;
	}
	public int getTotalScore() {
		if (isDead()) {
			return -100;
		}
		int score = 0;
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameObject().getGameData());
		score += getQuestPointScore().getTotalScore();
		if (!hostPrefs.hasPref(Constants.QST_QUEST_CARDS)) {
			score += getGreatTreasureScore().getTotalScore();
			score += getUsableSpellScore().getTotalScore();
			score += getFameScore().getTotalScore();
			score += getNotorietyScore().getTotalScore();
			score += getGoldScore().getTotalScore();
		}
		return score;
	}
	public int getTotalEarnedVps(boolean restrictToAssigned,boolean excludeStartingWorth) {
		int evps = 0;
		//evps += getQuestPointScore().getEarnedVictoryPoints(restrictToAssigned); // quest points don't count towards earned VPs!
		evps += getGreatTreasureScore().getEarnedVictoryPoints(restrictToAssigned);
		evps += getUsableSpellScore().getEarnedVictoryPoints(restrictToAssigned);
		evps += getFameScore().getEarnedVictoryPoints(restrictToAssigned);
		evps += getNotorietyScore().getEarnedVictoryPoints(restrictToAssigned);
		evps += getGoldScore().getEarnedVictoryPoints(restrictToAssigned,excludeStartingWorth);
		return evps;
	}
	
	public boolean canUseMagicSight() {	
		for (GameObject item:getEnhancingItemsAndNomads()) {
			if (item.hasThisAttribute(Constants.MAGIC_SIGHT)) {
				return true;
			}
		}		
		return false;
	}
	
	public boolean mustUseMagicSight(boolean optionalRule) {
		/*
		 * See rule 43.6 - only ways to get magic sight:  activated Phantom Glass (treasure), spell (world fades), or Witch King
		 * 
		 * Magic sight is self-cancelling, so if character is affected by two, they don't have it
		 */
		
		boolean hasIt = false;
		
		// check activated treasures (there is only 1, but this code will support expansions)
		for (GameObject item:getEnhancingItemsAndNomads()) {
			if (item.hasThisAttribute(Constants.MAGIC_SIGHT)) {
				hasIt = !hasIt; // toggle it
			}
		}
		
		// check character
		if (getGameObject().hasThisAttribute(Constants.MAGIC_SIGHT)) {
			hasIt = !hasIt; // toggle it
			if (optionalRule) {
				return true;
			}
		}
		
		// TODO check active spells for magic_sight
		if (affectedByKey(Constants.COMBAT_HIDE)) {
			hasIt = !hasIt; // toggle it
			if (optionalRule) {
				return true;
			}
		}
		
		return hasIt;
	}
	
	// Setters
	public void setPlayerName(String val) {
		setString(NAME_KEY,val);
	}
	public void removePlayerName() {
		removeAttribute(NAME_KEY);
	}
	public void setPlayerPassword(String val) {
		setString(PASSWORD_KEY,val);
		for(RealmComponent rc:getAllHirelings()) {
			if (rc.isPlayerControlledLeader()) {
				CharacterWrapper character = new CharacterWrapper(rc.getGameObject());
				character.setPlayerPassword(val);
			}
		}
		if (getMinionCount()>0) {
			for(GameObject go:getMinions()) {
				CharacterWrapper minion = new CharacterWrapper(go);
				minion.setPlayerPassword(val);
			}
		}
	}
	public void setPlayerEmail(String val) {
		setString(EMAIL_KEY,val);
	}
	public void setBlocked(boolean val) {
		if (!isMinion()) { // Minions can never be blocked
			setBoolean(IS_BLOCKED,val);
		}
	}
	public void setBlocking(boolean val) {
		setBoolean(BLOCKING,val);
		
		// Regardless, clear all decisions!
		setBoolean(BLOCK_DECISION,false);
	}
	public void addBlockDecision(GameObject go) {
		addListItem(BLOCK_DECISION,go.getStringId());
	}
	public void setSleep(boolean val) {
		setBoolean(IS_SLEEP,val);
	}
	public void setCharacterLevel(int val) {
		setInt(CHARACTER_LEVEL,val);
	}
	public void setStartingLevel(int val) {
		setInt("S"+CHARACTER_LEVEL,val);
	}
	public void setCharacterStage(int val) {
		setInt(CHARACTER_STAGE,val);
	}
	public void setCharacterExtraChitMarkers(int val) {
		setInt(CHARACTER_EXTRA_CHIT_MARKER,val);
	}
	public void setCharacterJoinOrder(int val) {
		setInt(CHARACTER_JOIN_ORDER,val);
	}
	public void setNeedsChooseGoldSpecial(boolean val) {
		setBoolean(CHARACTER_CHOOSE_GOLD_SPECIAL,val);
	}
	public void setJustUnhired(boolean val) {
		setBoolean(JUST_UNHIRED,val);
	}
	public void setGold(double val) {
		setDouble(GOLD_KEY,val);
	}
	public void setNotoriety(double val) {
		setDouble(NOTORIETY_KEY,val);
	}
	public void setFame(double val) {
		setDouble(FAME_KEY,val);
	}
	public void setCurrentMonth(int val) {
		setInt(CURRENT_MONTH,val);
	}
	public void setCurrentDay(int val) {
		setInt(CURRENT_DAY,val);
	}
	public void setBasicPhases(int val) {
		setInt(BASIC_PHASES,val);
	}
	public void setSunlightPhases(int val) {
		if (canUseSunlightPhases()) {
			setInt(SUNLIGHT_PHASES,val);
		}
		else {
			setInt(SUNLIGHT_PHASES,0);
		}
	}
	public void setShelteredPhases(int val) {
		setInt(SHELTERED_PHASES,val);
	}
	public void setMountainMoveCost(int val) {
		setInt(MOUNTAIN_MOVE_COST,val);
	}
	public void setDoRecord(boolean val) {
		setBoolean(DO_RECORD,val);
	}
	public void setPlayOrder(int val) {
		setInt(PLAY_ORDER,val);
	}
	public void setLastPlayer(boolean val) {
		setBoolean(LAST_PLAYER,val);
	}
	public void setLastPreemptivePlayer(boolean val) {
		setBoolean(LAST_PREEMPTIVE_PLAYER,val);
	}
	public void clearCharacterType() {
		setBoolean(CHARACTER_TYPE,false);
	}
	public void setCharacterType(String val) {
		setString(CHARACTER_TYPE,val);
	}
	public void setGameOver(boolean val) {
		setBoolean(GAME_OVER,val);
	}
	public void addPenaltyVps() {
		calculatePenaltyVps(false);
	}
	public void removePenaltyVps() {
		calculatePenaltyVps(true);
	}
	public void calculatePenaltyVps(boolean revert) {
		double fame = getFameScore().getRecordedPoints();
		if (fame<0) {
			int penaltyFame = (int)Math.ceil(Math.abs(fame)/10);
			if (revert) {
				penaltyFame = -penaltyFame;
			}
			addDeductVPs(-penaltyFame);
		}
		int notoriety = getNotorietyScore().getRecordedPoints();
		if (notoriety<0) {
			int penaltyNotoriety = (int)Math.ceil(Math.abs(notoriety)/20);
			if (revert) {
				penaltyNotoriety = -penaltyNotoriety;
			}
			addDeductVPs(-penaltyNotoriety);
		}
	}
	public void setCombatStatus(int val) {
		setInt(COMBAT_STATUS,val);
	}
	public void setCombatPlayOrder(int val) {
		setInt(COMBAT_PLAY_ORDER,val);
	}
	public void setMeleePlayOrder(int val) {
		setInt(MELEE_PLAY_ORDER,val);
	}
	public void setCombatCount(int val) {
		setInt(COMBAT_COUNT,val);
	}
	public void setDoInstantPeer(boolean val) {
		setBoolean(DO_INSTANT_PEER,val);
	}
	public void setNoSummon(boolean val) {
		setBoolean(NO_SUMMON,val);
	}
	public void setWishStrength(Strength in) {
		setString(WISH_STRENGTH,in.toString());
		updateChitEffects();
	}
	public void setPeerAny(boolean val) {
		setBoolean(PEER_ANY,val);
	}
	public void setStormed(boolean val) {
		setBoolean(STORMED,val);
	}
	public void setLostPhases(int val) {
		setInt(LOST_PHASES,val);
	}
	public void addLostPhases(int val) {
		setInt(LOST_PHASES,getLostPhases()+val);
	}
	public void setFortified(boolean val) {
		setBoolean(FORTIFIED,val);
	}
	public void setFortDamaged(boolean val) {
		setBoolean(FORT_DAMAGED,val);
	}
	public void setNeedsQuestCheck(boolean val) {
		setBoolean(NEED_QUEST_CHECK,val);
	}
	public void setDiscardedQuests(boolean val) {
		setBoolean(DISCARDED_QUESTS,val);
	}
	public void setCurrentCampaign(String val) {
		if (val==null) {
			removeAttribute(CAMPAIGN);
		}
		else {
			setString(CAMPAIGN,val);
		}
	}
	public void setWeatherFatigue(int val) {
		setWeatherFatigue(val,true);
	}
	public void setWeatherFatigue(int val,boolean includeFollowers) {
		if (isCharacter() && !hasActiveInventoryThisKey(Constants.NO_FATIGUE) && !getGameObject().hasThisAttribute(Constants.NO_WEATHER_FATIGUE)) {
			if (getTransmorph()==null) {
				setInt(WEATHER_FATIGUE,val);
			}
		}
		if(includeFollowers) {
			for (CharacterWrapper follower:getActionFollowers()) {
				if (follower.isCharacter()) {
					follower.setWeatherFatigue(follower.getWeatherFatigue()+1);
				}
			}
		}
	}
	public void clearWeatherFatigue() {
		removeAttribute(WEATHER_FATIGUE);
	}
	public void setExtraWounds(int val) {
		setWeatherFatigue(val,true);
	}
	public void setExtraWounds(int val,boolean includeFollowers) {
		setInt(EXTRA_WOUNDS,val);
		if(includeFollowers) {
			for (CharacterWrapper follower:getActionFollowers()) {
				if (follower.isCharacter()) {
					follower.setExtraWounds(val);
				}
			}
		}
	}
	public void clearExtraWounds() {
		removeAttribute(EXTRA_WOUNDS);
	}
	public void setSpellConflicts(ArrayList<SpellWrapper> list) {
		clearSpellConflicts();
		for (SpellWrapper spell:list) {
			addListItem(SPELL_CONFLICTS,spell.getGameObject().getStringId());
		}
	}
	public void clearSpellConflicts() {
		setBoolean(SPELL_CONFLICTS,false);
	}
	public void setNeedsInventoryCheck(boolean val) {
		setBoolean(NEEDS_INVENTORY_CHECK,val);
	}
	public void setNeedsActionPanelUpdate(boolean val) {
		setBoolean(NEEDS_ACTION_PANEL_UPDATE,val);
	}
	
	// Adders
	public void addGold(double val) {
		addGold(val,false);
	}
	public void addGold(double val,boolean ignoreCurses) {
		if (!ignoreCurses && hasCurse(Constants.ASHES) && val<0) {
			throw new IllegalStateException("Cannot subtract gold from character with ASHES curse");
		}
		if (val!=0) { // no point adding zero!
			double newGold = getGold() + val;
			if (getGameObject().hasThisAttribute(Constants.MAXIMUM_GOLD)) {
				int max = getGameObject().getThisInt(Constants.MAXIMUM_GOLD);
				if (newGold>max) {
					newGold = max;
				}
			}
			if (newGold<0) { // this can happen during a "demand gold" situation when the character gets a net amount that is more than their maximum
				newGold = 0;
			}
			setGold(newGold);
		}
	}
	public void addNotoriety(double val) {
		if (!isCharacter()) {
			getHiringCharacter().addNotoriety(val);
			return;
		}
		if (val!=0) { // no point adding zero!
			setNotoriety(getNotoriety()+val);
		}
	}
	public void addFame(double val) {
		addFame(val,true);
	}
	public void addFame(double val, boolean checkCurse) {
		if (!isCharacter()) {
			getHiringCharacter().addFame(val);
			return;
		}
		if (checkCurse && hasCurse(Constants.DISGUST) && val<0) {
			throw new IllegalStateException("Cannot subtract fame from character with DISGUST curse");
		}
		if (val!=0) { // no point adding zero!
			setFame(getFame()+val);
		}
	}
	public void clearActionFollowers() {
		removeAttribute(ACTION_FOLLOWER);
	}
	public void addActionFollower(CharacterWrapper follower) {
		addListItem(ACTION_FOLLOWER,follower.getGameObject().getStringId());
	}
	
	/**
	 * Removes an action follower from the character.
	 * 
	 * @param follower			The Follower to remove
	 * @param monsterDie		The current monsterDie.  This is needed to summon monsters
	 * 							appropriately for the abandoned follower.
	 */
	public void removeActionFollower(CharacterWrapper follower,DieRoller monsterDieRoller,DieRoller nativeDieRoller) {
		ArrayList<String> list = getList(ACTION_FOLLOWER);
		if (list!=null && !list.isEmpty()) {
			ArrayList<String> newlist = new ArrayList<>(list);
			int index = newlist.indexOf(follower.getGameObject().getStringId());
			if (index>=0) {
				newlist.remove(index);
				if (index==0) {
					HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameObject().getGameData());
					
					// "first" follower is removed, so we need to update the NO_SUMMON to the next follower, if there is any
					if (newlist.size()>0) {
						String id = newlist.iterator().next();
						GameObject go = getGameObject().getGameData().getGameObject(Long.valueOf(id));
						CharacterWrapper nextFollower = new CharacterWrapper(go);
						nextFollower.setNoSummon(true);
					}
					
					// Summon monsters now.
					if (monsterDieRoller!=null) { // Might be null if sleep was the cause for stopping following
						SetupCardUtility.summonMonsters(hostPrefs,new ArrayList<GameObject>(),follower,monsterDieRoller,nativeDieRoller);
					}
				}
				setList(ACTION_FOLLOWER,newlist);
			}
		}
	}
	/**
	 * These are the characters and leaders that specified they were following you during the day
	 */
	public ArrayList<CharacterWrapper> getActionFollowers() {
		GameData data = getGameObject().getGameData();
		ArrayList<CharacterWrapper> ret = new ArrayList<>();
		ArrayList<String> list = getList(ACTION_FOLLOWER);
		if (list!=null && !list.isEmpty()) {
			for (String i : list) {
				GameObject go = data.getGameObject(Long.valueOf(i));
				CharacterWrapper follower = new CharacterWrapper(go);
				if (!follower.isStopFollowing()) {
					ret.add(follower);
				}
			}
		}
		return ret;
	}
	public ArrayList<CharacterWrapper> getStoppedActionFollowers() {
		GameData data = getGameObject().getGameData();
		ArrayList<CharacterWrapper> ret = new ArrayList<>();
		ArrayList<String> list = getList(ACTION_FOLLOWER);
		if (list!=null && !list.isEmpty()) {
			for (String i : list) {
				GameObject go = data.getGameObject(Long.valueOf(i));
				CharacterWrapper follower = new CharacterWrapper(go);
				if (follower.isStopFollowing()) {
					ret.add(follower);
				}
			}
		}
		return ret;
	}
	public void setEnemyCharacter(GameObject enemyCharacterObject,boolean enemy) {
		String id = "E"+enemyCharacterObject.getStringId(); // Have to have a letter in from of the number!!
		boolean currentEnemy= getGameObject().hasAttribute(ENEMY_CHARACTER_BLOCK,id);
		if (currentEnemy!=enemy) {
			if (enemy) {
				getGameObject().setAttributeBoolean(ENEMY_CHARACTER_BLOCK,id,true);
			}
			else {
				getGameObject().removeAttribute(ENEMY_CHARACTER_BLOCK,id);
			}
		}
	}
	/**
	 * @return		true if characterObject is considered an enemy, and combat is to be ignored.  Note that the friendship
	 * 				might not be mutual!  Just because 'A' considers 'B' a friend, doesn't mean the same is true for 'B'!
	 */
	public boolean isEnemy(GameObject characterObject) {
		if (characterObject.equals(getGameObject())) { // you are always your own friend!
			return false;
		}
		String id = "E"+characterObject.getStringId();
		return getGameObject().hasAttribute(ENEMY_CHARACTER_BLOCK,id);
	}
	public ArrayList<String> getTreasureLocationDiscoveries() {
		if (isMinion()) {
			return getHiringCharacter().getTreasureLocationDiscoveries();
		}
		return getList(DISC_TREASURE_LOCATIONS);
	}
	public ArrayList<String> getOtherChitDiscoveries() {
		if (isMinion()) {
			return getHiringCharacter().getOtherChitDiscoveries();
		}
		return getList(DISC_OTHER);
	}
	/**
	 * @return		The list of treasure location discoveries for the current clearing only
	 */
	public ArrayList<String> getCurrentClearingKnownTreasureLocations(boolean includeSiteCardLocationText) {
		ArrayList<String> list = new ArrayList<>();
		TileLocation current = getCurrentLocation();
		if (current.isInClearing()) {
			Hashtable<String,String> stuff = new Hashtable<>();
			for (RealmComponent rc : current.clearing.getClearingComponents(false)) {
				stuff.put(rc.getGameObject().getName(),rc.getGameObject().getName());
				
				// Search for Site Cards, which remain IN the Treasure Location
				if (rc.isTreasureLocation()) {
					for (GameObject go : rc.getGameObject().getHold()) {
						String thingName = go.getName() + (includeSiteCardLocationText?(" ( + "+rc.getGameObject().getName()+")"):"");
						stuff.put(go.getName(),thingName);
					}
				}
			}
			
			ArrayList<String> tls = getTreasureLocationDiscoveries();
			if (tls!=null && !tls.isEmpty()) {
				for (String disc : tls) {
					if (stuff.containsKey(disc)) {
						list.add(stuff.get(disc));
					}
				}
			}
		}
		
		return list;
	}
	/**
	 * @return		The list of gate discoveries for the current clearing only
	 */
	public ArrayList<String> getCurrentClearingKnownOtherChits() {
		ArrayList<String> list = new ArrayList<>();
		TileLocation current = getCurrentLocation();
		if (current.isInClearing()) {
			Hashtable<String,String> stuff = new Hashtable<>();
			for (RealmComponent rc : current.clearing.getClearingComponents(false)) {
				if (rc.isGate()) {
					stuff.put(rc.getGameObject().getName(),rc.getGameObject().getName());
				}
			}
			
			ArrayList<String> tls = getOtherChitDiscoveries();
			if (tls!=null && !tls.isEmpty()) {
				for (String disc : tls) {
					if (stuff.containsKey(disc)) {
						list.add(stuff.get(disc));
					}
				}
			}
		}
		
		return list;
	}
	/**
	 * @return		The list of hidden path discoveries for the current clearing only
	 */
	public ArrayList<String> getCurrentClearingKnownHiddenPaths() {
		ArrayList<String> list = new ArrayList<>();
		TileLocation current = getCurrentLocation();
		if (current.isInClearing()) {
			ArrayList<String> stuff = new ArrayList<>();
			
			for (PathDetail path : current.clearing.getConnectedPaths()) {
				if (path.isHidden()) {
					stuff.add(path.getFullPathKey());
				}
			}
			
			ArrayList<String> hps = getHiddenPathDiscoveries();
			if (hps!=null && !hps.isEmpty()) {
				for (String disc : hps) {
					if (stuff.contains(disc)) {
						list.add(disc);
					}
				}
			}
		}
		
		return list;
	}
	/**
	 * @return		The list of hidden path discoveries for the current clearing only
	 */
	public ArrayList<String> getCurrentClearingKnownSecretPassages() {
		ArrayList<String> list = new ArrayList<>();
		TileLocation current = getCurrentLocation();
		if (current.isInClearing()) {
			ArrayList<String> stuff = new ArrayList<>();
			
			for (PathDetail path : current.clearing.getConnectedPaths()) {
				if (path.isSecret()) {
					stuff.add(path.getFullPathKey());
				}
			}
			
			ArrayList<String> sps = getSecretPassageDiscoveries();
			if (sps!=null && !sps.isEmpty()) {
				for (String disc : sps) {
					if (stuff.contains(disc)) {
						list.add(disc);
					}
				}
			}
		}
		
		return list;
	}
	public ArrayList<String> getHiddenPathDiscoveries() {
		if (isMinion()) {
			return getHiringCharacter().getHiddenPathDiscoveries();
		}
		return getList(DISC_HIDDEN_PATHS);
	}
	public ArrayList<String> getSecretPassageDiscoveries() {
		if (isMinion()) {
			return getHiringCharacter().getSecretPassageDiscoveries();
		}
		return getList(DISC_SECRET_PASSAGES);
	}
	public void addTreasureLocationDiscovery(String name) {
		if (hasTreasureLocationDiscovery(name)) { // don't add it more than once!
			return;
		}
		GameData gameData = getGameObject().getGameData();
		GameObject discovery = gameData.getGameObjectByNameIgnoreCase(name);
		if (isMinion()) {
			getHiringCharacter().addTreasureLocationDiscovery(name);
			discovery.setThisAttribute(Constants.DISCOVERED);
			return;
		}

		addListItem(DISC_TREASURE_LOCATIONS,name);
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(gameData);
		if ((hostPrefs.hasPref(Constants.EXP_BOUNTY_POINTS_FOR_DISCOVERIES) || this.affectedByKey(Constants.ADVENTURER)) && this.isCharacter() && !discovery.hasThisAttribute(Constants.DISCOVERED)) {
			RealmComponent rc = RealmComponent.getRealmComponent(gameData.getGameObjectByNameIgnoreCase(name));
			if (rc!=null) { 
				if (rc.isTreasureLocation() && discovery.hasThisAttribute(Constants.DISCOVERY)) {
					if (rc.isChit()) {
						this.addFame(5);
					}
					else {
						this.addFame(10);
						this.addNotoriety(5);
					}
				}
				else if (rc.isRedSpecial()) {
					this.addFame(10);
					this.addNotoriety(10);
				}
			}
		}
		if (hostPrefs.hasPref(Constants.SR_DEDUCT_VPS)) {
			addDeductVPs(1);
		}
		discovery.setThisAttribute(Constants.DISCOVERED);
		
		for (CharacterWrapper actionFollower : getActionFollowers()) {
			if (!actionFollower.isMinion()) { // otherwise, there is a possiblilty for an infinite loop!
				actionFollower.addTreasureLocationDiscovery(name);
			}
		}
	}
	public boolean hasTreasureLocationDiscovery(String name) {
		if (isMinion()) {
			return getHiringCharacter().hasTreasureLocationDiscovery(name);
		}
		return hasListItem(DISC_TREASURE_LOCATIONS,name);
	}
	public void addCompletedMission(String name) {
		if (hasMissionCompleted(name)) {
			return;
		}
		if (isMinion()) {
			getHiringCharacter().addCompletedMission(name);
			return;
		}
		addListItem(COMPLETED_MISSIONS,name);
	}
	public boolean hasMissionCompleted(String name) {
		if (isMinion()) {
			return getHiringCharacter().hasMissionCompleted(name);
		}
		return hasListItem(COMPLETED_MISSIONS,name);
	}
	public ArrayList<String> getCompletedMissions() {
		if (isMinion()) {
			return getHiringCharacter().getCompletedMissions();
		}
		return getList(COMPLETED_MISSIONS);
	}
	public boolean hasMissionFailed(String name) {
		if (isMinion()) {
			return getHiringCharacter().hasMissionFailed(name);
		}
		return hasListItem(FAILED_MISSIONS,name);
	}
	public void addCompletedCampaign(String name) {
		if (hasCampaignCompleted(name)) {
			return;
		}
		if (isMinion()) {
			getHiringCharacter().addCompletedCampaign(name);
			return;
		}
		addListItem(COMPLETED_CAMPAIGNS,name);
	}
	public boolean hasCampaignCompleted(String name) {
		if (isMinion()) {
			return getHiringCharacter().hasCampaignCompleted(name);
		}
		return hasListItem(COMPLETED_CAMPAIGNS,name);
	}
	public ArrayList<String> getCompletedCampaigns() {
		if (isMinion()) {
			return getHiringCharacter().getCompletedCampaigns();
		}
		return getList(COMPLETED_CAMPAIGNS);
	}
	public boolean hasCampaignFailed(String name) {
		if (isMinion()) {
			return getHiringCharacter().hasCampaignFailed(name);
		}
		return hasListItem(FAILED_CAMPAIGNS,name);
	}
	public void addCompletedTask(String name) {
		if (hasTaskCompleted(name)) {
			return;
		}
		if (isMinion()) {
			getHiringCharacter().addCompletedTask(name);
			return;
		}
		addListItem(COMPLETED_TASKS,name);
	}
	public boolean hasTaskCompleted(String name) {
		if (isMinion()) {
			return getHiringCharacter().hasTaskCompleted(name);
		}
		return hasListItem(COMPLETED_TASKS,name);
	}
	public GameObject getCompletedActiveTask() {
		for (GameObject item : getInventory()) {
			if (item.hasThisAttribute(Constants.TASK) && item.hasThisAttribute(Constants.TASK_COMPLETED)) {
				return item;
			}
		}
		return null;
	}
	public ArrayList<String> getCompletedTasks() {
		if (isMinion()) {
			return getHiringCharacter().getCompletedTasks();
		}
		return getList(COMPLETED_TASKS);
	}
	public boolean hasTaskFailed(String name) {
		if (isMinion()) {
			return getHiringCharacter().hasTaskFailed(name);
		}
		return hasListItem(FAILED_TASKS,name);
	}
	public void addFailedGoldSpecial(GoldSpecialChitComponent gs) {
		if (isMinion()) {
			getHiringCharacter().addFailedGoldSpecial(gs);
			return;
		}
		if (gs.isMission()) {
			addListItem(FAILED_MISSIONS,gs.getName());
		}
		if (gs.isCampaign()) {
			addListItem(FAILED_CAMPAIGNS,gs.getName());
		}
		if (gs.isTask()) {
			addListItem(FAILED_TASKS,gs.getName());
		}
	}
	public void addHiddenPathDiscovery(String name) {
		if (isMinion()) {
			getHiringCharacter().addHiddenPathDiscovery(name);
		}
		else {
			addListItem(DISC_HIDDEN_PATHS,name);
			for (CharacterWrapper actionFollower : getActionFollowers()) {
				actionFollower.addHiddenPathDiscovery(name);
			}
		}
	}
	public boolean hasHiddenPathDiscovery(String name) {
		if (affectedByKey(Constants.KNOWS_ROADS)) {
			return true;
		}
		if (isMinion()) {
			return getHiringCharacter().hasHiddenPathDiscovery(name);
		}
		return hasListItem(DISC_HIDDEN_PATHS,name);
	}
	public void addSecretPassageDiscovery(String name) {
		if (isMinion()) {
			getHiringCharacter().addSecretPassageDiscovery(name);
		}
		else {
			addListItem(DISC_SECRET_PASSAGES,name);
			for (CharacterWrapper actionFollower : getActionFollowers()) {
				actionFollower.addSecretPassageDiscovery(name);
			}
		}
	}
	public boolean hasSecretPassageDiscovery(String name) {
		if (affectedByKey(Constants.KNOWS_ROADS)) {
			return true;
		}
		if (isMinion()) {
			return getHiringCharacter().hasSecretPassageDiscovery(name);
		}
		return hasListItem(DISC_SECRET_PASSAGES,name);
	}
	public void addOtherChitDiscovery(String name) {
		if (isMinion()) {
			getHiringCharacter().addOtherChitDiscovery(name);
		}
		else {
			addListItem(DISC_OTHER,name);
			for (CharacterWrapper actionFollower : getActionFollowers()) {
				actionFollower.addOtherChitDiscovery(name);
			}
		}
	}
	public boolean hasOtherChitDiscovery(String name) {
		if (isMinion()) {
			return getHiringCharacter().hasOtherChitDiscovery(name);
		}
		return hasListItem(DISC_OTHER,name);
	}
	public ArrayList<String> getAllDiscoveryKeys() {
		ArrayList<String> list = new ArrayList<>();
		ArrayList<String> tl = getList(DISC_TREASURE_LOCATIONS);
		if (tl!=null) list.addAll(tl);
		ArrayList<String> hp = getList(DISC_HIDDEN_PATHS);
		if (hp!=null) list.addAll(hp);
		ArrayList<String> sp = getList(DISC_SECRET_PASSAGES);
		if (sp!=null) list.addAll(sp);
		ArrayList<String> other = getList(DISC_OTHER);
		if (other!=null) list.addAll(other);
		return list;
	}
	public void clearWishStrength() {
		getGameObject().removeAttribute(getBlockName(),WISH_STRENGTH);
		updateChitEffects();
	}
	public void decrementCombatCount() {
		int count = getInt(COMBAT_COUNT);
		if (count>0) {
			count--;
		}
		if (count==0) {
			getGameObject().removeAttribute(getBlockName(),COMBAT_COUNT);
		}
		else {
			setInt(COMBAT_COUNT,count);
		}
	}
	public void clearCombat() {
		getGameObject().removeAttribute(getBlockName(),COMBAT_STATUS);
		getGameObject().removeAttribute(getBlockName(),COMBAT_COUNT);
//		getGameObject().removeAttribute(getBlockName(),COMBAT_PLAY_ORDER); // Don't clear this out!!
		// Potentially add other combat things here
	}
	public void setHidden(boolean val) {
		setHidden(val,true);
	}
	public void setHidden(boolean val,boolean includeFollowingHirelings) {
		ChitComponent ccc = (ChitComponent)RealmComponent.getRealmComponent(getGameObject());
		if (ccc.isHidden()!=val) {
			ccc.setHidden(val);
		}
		
		if (includeFollowingHirelings) {
			// Hirelings hide and unhide with character
			for (RealmComponent rc : getFollowingHirelings()) {
				rc.setHidden(val);
			}
		}
	}
	public boolean isHidden() {
		ChitComponent ccc = (ChitComponent)RealmComponent.getRealmComponent(getGameObject());
		return ccc.isHidden();
	}
	public void unhideAllCharacterFollowers() {
		// Unhide the followers that are currently following
		for (CharacterWrapper actionFollower : getActionFollowers()) {
			actionFollower.setHidden(false);
		}
		
		// ALSO unhide any followers that may be abandoned (like after running)
		for (RealmComponent rc : getAllHirelings()) {
			if (!rc.isPlayerControlledLeader()) { // leaders can handle themselves
				GameObject hireling = rc.getGameObject();
				GameObject heldBy = hireling.getHeldBy();
				if (heldBy!=null) { // should never be null, but I don't want a game killing NPE in any case
					RealmComponent hrc = RealmComponent.getRealmComponent(heldBy);
					if (hrc!=null && !hrc.isPlayerControlledLeader()) {
						rc.setHidden(false);
					}
				}
			}
		}
	}
	/**
	 * Sets ALL hidden enemies found.
	 */
	public void setFoundHiddenEnemies(boolean val) {
		setBoolean(FOUND_HIDDEN_ENEMIES,val);
		
		// Don't forget to update the followers!
		for (CharacterWrapper actionFollower : getActionFollowers()) {
			actionFollower.setFoundHiddenEnemies(val);
		}
	}
	public void addFoundHiddenEnemy(GameObject enemy) {
		if (enemy!=null) {
			StringBuffer sb = new StringBuffer();
			String foundEnemyList = getString(FOUND_HIDDEN_ENEMIES);
			if (foundEnemyList!=null) {
				sb.append(foundEnemyList);
			}
			if (sb.length()>0) {
				sb.append(",");
			}
			sb.append(enemy.getName());
			setString(FOUND_HIDDEN_ENEMIES,sb.toString());
			
			// Don't forget to update the followers!
			for (CharacterWrapper actionFollower : getActionFollowers()) {
				actionFollower.addFoundHiddenEnemy(enemy);
			}
		}
	}
	/**
	 * @return	true if you found ALL hidden enemies
	 */
	public boolean foundAllHiddenEnemies() {
		String foundEnemyList = getString(FOUND_HIDDEN_ENEMIES);
		if ((foundEnemyList!=null && foundEnemyList.length()==0) || this.getGameObject().hasThisAttribute(Constants.TRACKERS_SENSE)) {
			return true;
		}
		for (GameObject nomad : getNomads()) {
			if (nomad.hasThisAttribute(Constants.FAIRY_SENSE)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @return	true if you found ANY hidden enemies
	 */
	public boolean foundHiddenEnemies() {
		String foundEnemyList = getString(FOUND_HIDDEN_ENEMIES);
		if (foundEnemyList!=null || this.getGameObject().hasThisAttribute(Constants.TRACKERS_SENSE)) {
			return true;
		}
		for (GameObject nomad : getNomads()) {
			if (nomad.hasThisAttribute(Constants.FAIRY_SENSE)) {
				return true;
			}
		}
		return false;
	}
	public ArrayList<String> getFoundEnemies() {
		if (foundAllHiddenEnemies()) {
			throw new IllegalStateException("Don't use getFoundEnemies, when foundAllHiddenEnemies is true");
		}
		ArrayList<String> list = null;
		String foundEnemyList = getString(FOUND_HIDDEN_ENEMIES);
		if (foundEnemyList!=null && foundEnemyList.length()>0) {
			list = new ArrayList<>();
			StringTokenizer tokens = new StringTokenizer(foundEnemyList,",");
			while(tokens.hasMoreTokens()) {
				list.add(tokens.nextToken());
			}
		}
		return list;
	}
	/**
	 * Returns true if given enemy was found.
	 */
	public boolean foundHiddenEnemy(GameObject enemy) {
		if (this.getGameObject().hasThisAttribute(Constants.TRACKERS_SENSE)) return true;
		String foundEnemyList = getString(FOUND_HIDDEN_ENEMIES);
		if (foundEnemyList!=null) {
			if (foundEnemyList.length()==0 || foundEnemyList.indexOf(enemy.getName())>=0) {
				// an empty list, or one that contains the name of the enemy is a true result
				return true;
			}
		}
		for (GameObject nomad : getNomads()) {
			if (nomad.hasThisAttribute(Constants.FAIRY_SENSE)) {
				return true;
			}
		}
		// a null list, or one that lacks the name of the enemy is a false result
		return false;
	}
	/**
	 * This sets how many vps must be added before the character can record his/her next turn
	 */
	public void setNewVPRequirement(int vps) {
		getGameObject().setAttribute(VICTORY_REQ_BLOCK,V_NEW_VPS,vps);
	}
	public void addDeductVPs(int amount) {
		int oldAmount = 0;
		if (getGameObject().hasAttribute(VICTORY_REQ_BLOCK,V_DEDUCT_VPS)) {
			getGameObject().getAttributeInt(VICTORY_REQ_BLOCK,V_DEDUCT_VPS);
		}
		amount = oldAmount + amount;
		getGameObject().setAttribute(VICTORY_REQ_BLOCK,V_DEDUCT_VPS,amount);
	}
//	public void updateNewVPRequirement(int currentDay) {
//		if (getGameObject().hasAttribute(VICTORY_REQ_BLOCK,V_MONTHLY_VPS)) {
//			int vps = getGameObject().getAttributeInt(VICTORY_REQ_BLOCK,V_MONTHLY_VPS);
//			int daysInWeek = RealmCalendar.DAYS_IN_A_MONTH/RealmCalendar.WEEKS_IN_A_MONTH;
//			int vpsPerWeek = vps/RealmCalendar.WEEKS_IN_A_MONTH;
//			
//			int weeksUsed = (currentDay-1)/daysInWeek;
//			vps -= (vpsPerWeek * weeksUsed);
//			
//			setNewVPRequirement(vps);
//		}
//	}
	public void clearNewVPRequirement() {
		getGameObject().removeAttribute(VICTORY_REQ_BLOCK,V_NEW_VPS);
	}
	public void clearDeductVPs() {
		getGameObject().removeAttribute(VICTORY_REQ_BLOCK,V_DEDUCT_VPS);
	}
	public int getNewVPRequirement() {
		return getGameObject().getAttributeInt(VICTORY_REQ_BLOCK,V_NEW_VPS);
	}
	public int getVPsToDeduct() {
		return getGameObject().getAttributeInt(VICTORY_REQ_BLOCK,V_DEDUCT_VPS);
	}
	public boolean needsToSetVps() {
		return getGameObject().hasAttribute(VICTORY_REQ_BLOCK,V_NEW_VPS);
	}
	public boolean needsToDeductVps() {
		return getGameObject().hasAttribute(VICTORY_REQ_BLOCK,V_DEDUCT_VPS);
	}
	public void initializeVpsSetup(HostPrefWrapper hostPrefs,int level, RealmCalendar cal) {
		if (!hostPrefs.getRequiredVPsOff() && !hostPrefs.hasPref(Constants.QST_BOOK_OF_QUESTS)) {
			int vps = 0;
			if (hostPrefs.hasPref(Constants.EXP_SUDDEN_DEATH) || hostPrefs.isFixedVps()) {
				vps = hostPrefs.getVpsToAchieve();
			}
			else {
				int totalMonths = hostPrefs.getNumberMonthsToPlay();
				for (int i = 0; i < totalMonths; i++) {
					int vp = cal.getVictoryPoints(i + 1);
					vps += vp;
				}
			}

			// Lower level characters get to choose fewer VPs
			vps -= (Constants.MAX_LEVEL - level);

			if (vps < Constants.MINIMUM_VPS) {
				vps = Constants.MINIMUM_VPS;
			}

			if (hostPrefs.hasPref(Constants.HOUSE2_ANY_VPS)) {
				vps = -1;
			}

			if (hostPrefs.hasPref(Constants.QST_QUEST_CARDS) && vps!=-1 && !hostPrefs.hasPref(Constants.SR_DEDUCT_VPS)) {
				addVictoryRequirements(vps,0,0,0,0,0);
			}
			else {
				setNewVPRequirement(vps);
			}
		}
	}
	/**
	 * This add vps to each of the categories
	 */
	public void addVictoryRequirements(int questPoints,int greatTreasures,int usableSpells,int fame,int notoriety,int gold) {
		addVictoryRequirement(V_QUEST_POINTS,questPoints);
		addVictoryRequirement(V_GREAT_TREASURES,greatTreasures);
		addVictoryRequirement(V_USABLE_SPELLS,usableSpells);
		addVictoryRequirement(V_FAME,fame);
		addVictoryRequirement(V_NOTORIETY,notoriety);
		addVictoryRequirement(V_GOLD,gold);
	}
	public void setVictoryRequirements(int questPoints,int greatTreasures,int usableSpells,int fame,int notoriety,int gold) {
		setVictoryRequirement(V_QUEST_POINTS,questPoints);
		setVictoryRequirement(V_GREAT_TREASURES,greatTreasures);
		setVictoryRequirement(V_USABLE_SPELLS,usableSpells);
		setVictoryRequirement(V_FAME,fame);
		setVictoryRequirement(V_NOTORIETY,notoriety);
		setVictoryRequirement(V_GOLD,gold);
	}
	public int getCurrentVictoryRequirement(String key) {
		int current = 0;
		if (getGameObject().hasAttribute(VICTORY_REQ_BLOCK,key)) {
			current = getGameObject().getAttributeInt(VICTORY_REQ_BLOCK,key);
		}
		return current;
	}
	private void addVictoryRequirement(String key,int val) {
		int current = getCurrentVictoryRequirement(key);
		getGameObject().setAttribute(VICTORY_REQ_BLOCK,key,current+val);
	}
	private void setVictoryRequirement(String key,int val) {
		getGameObject().setAttribute(VICTORY_REQ_BLOCK,key,val);
	}
	public boolean immuneToCurses() {
		return getGameObject().hasAttribute(Constants.OPTIONAL_BLOCK,Constants.CURSE_IMMUNITY)
				|| affectedByKey(Constants.CURSE_IMMUNITY) || getGameObject().hasThisAttribute(Constants.PENTAGRAM) || hasActiveInventoryThisKey(Constants.PENTAGRAM);
	}
	public void applyCurse(String curse) {
		if (isCharacter() && !isMistLike() && !hasMagicProtection()) { // only true characters can be cursed!
			getGameObject().setAttribute(CURSES_BLOCK,curse);
		}
	}
	public boolean immuneToMesmerize() {
		return getGameObject().hasAttribute(Constants.OPTIONAL_BLOCK,Constants.MESMERIZE_IMMUNITY)
				|| affectedByKey(Constants.MESMERIZE_IMMUNITY) || getGameObject().hasThisAttribute(Constants.PENTAGRAM) || hasActiveInventoryThisKey(Constants.PENTAGRAM);
	}
	public void applyMesmerize(String effect) {
		if (isCharacter() && !isMistLike() && !hasMagicProtection() && !immuneToMesmerize() && !hasMesmerizeEffect(effect)) { // only true characters can be cursed!
			getGameObject().setAttribute(CURSES_BLOCK,Constants.MESMERIZE);
			getGameObject().addThisAttributeListItem(Constants.MESMERIZE, effect);
		}
	}
	public void removeMesmerizeEffect(String effect) {
		getGameObject().removeThisAttributeListItem(Constants.MESMERIZE, effect);
		if (getGameObject().getThisAttributeList(Constants.MESMERIZE).isEmpty()) {
			getGameObject().removeThisAttribute(Constants.MESMERIZE);
		}
	}
	public boolean hasCurses() {
		Hashtable hash = getGameObject().getAttributeBlock(CURSES_BLOCK);
		return hash!=null && hash.size()>0;
	}
	public boolean hasCurse(String curse) {
		return !isNullifiedCurses() && getGameObject().hasAttribute(CURSES_BLOCK,curse);
	}
	public boolean hasMesmerizeEffect(String curse) {
		return !isNullifiedCurses() && getGameObject().hasThisAttribute(Constants.MESMERIZE) && getGameObject().getThisAttributeList(Constants.MESMERIZE).contains(curse);
	}
	public void removeCurse(String curse) {
		getGameObject().removeAttribute(CURSES_BLOCK,curse);
		if (curse.matches(Constants.MESMERIZE)) {
			getGameObject().removeThisAttribute(Constants.MESMERIZE);
		}
		for (String effect : Constants.MESMERIZE_EFFECTS) {
			if (curse.matches(effect)) {
				removeMesmerizeEffect(effect);
			}
		}
	}
	public ArrayList<String> getAllCurses() {
		ArrayList<String> list = new ArrayList<>();
		if (getGameObject().hasAttribute(CURSES_BLOCK,Constants.ASHES)) list.add(Constants.ASHES);
		if (getGameObject().hasAttribute(CURSES_BLOCK,Constants.DISGUST)) list.add(Constants.DISGUST);
		if (getGameObject().hasAttribute(CURSES_BLOCK,Constants.EYEMIST)) list.add(Constants.EYEMIST);
		if (getGameObject().hasAttribute(CURSES_BLOCK,Constants.ILL_HEALTH)) list.add(Constants.ILL_HEALTH);
		if (getGameObject().hasAttribute(CURSES_BLOCK,Constants.SQUEAK)) list.add(Constants.SQUEAK);
		if (getGameObject().hasAttribute(CURSES_BLOCK,Constants.WITHER)) list.add(Constants.WITHER);
		if (getGameObject().hasAttribute(CURSES_BLOCK,Constants.MESMERIZE)) {
			list.addAll(getGameObject().getThisAttributeList(Constants.MESMERIZE));
		}
		return list;
	}
	public void removeAllCurses() {
		GameObject go = getGameObject();
		if (go.hasAttribute(CURSES_BLOCK,Constants.ASHES)) {
			getGameObject().removeAttribute(CURSES_BLOCK,Constants.ASHES);
			RealmLogging.logMessage(go.getName(),"ASHES curse is removed.");
		}
		if (go.hasAttribute(CURSES_BLOCK,Constants.DISGUST)) {
			getGameObject().removeAttribute(CURSES_BLOCK,Constants.DISGUST);
			RealmLogging.logMessage(go.getName(),"DISGUST curse is removed.");
		}
		if (go.hasAttribute(CURSES_BLOCK,Constants.EYEMIST)) {
			getGameObject().removeAttribute(CURSES_BLOCK,Constants.EYEMIST);
			RealmLogging.logMessage(go.getName(),"EYEMIST curse is removed.");
		}
		if (go.hasAttribute(CURSES_BLOCK,Constants.ILL_HEALTH)) {
			getGameObject().removeAttribute(CURSES_BLOCK,Constants.ILL_HEALTH);
			RealmLogging.logMessage(go.getName(),"ILL_HEALTH curse is removed.");
		}
		if (go.hasAttribute(CURSES_BLOCK,Constants.SQUEAK)) {
			getGameObject().removeAttribute(CURSES_BLOCK,Constants.SQUEAK);
			RealmLogging.logMessage(go.getName(),"SQUEAK curse is removed.");
		}
		if (go.hasAttribute(CURSES_BLOCK,Constants.WITHER)) {
			getGameObject().removeAttribute(CURSES_BLOCK,Constants.WITHER);
			RealmLogging.logMessage(go.getName(),"WITHER curse is removed.");
		}
		if (go.hasAttribute(CURSES_BLOCK,Constants.MESMERIZE)) {
			getGameObject().removeAttribute(CURSES_BLOCK,Constants.MESMERIZE);
			getGameObject().removeThisAttribute(Constants.MESMERIZE);
			RealmLogging.logMessage(go.getName(),"MESMERIZE is removed.");
		}
	}
	public void nullifyCurses() {
		getGameObject().setThisAttribute(Constants.CURSES_NULLIFIED);
	}
	public boolean isNullifiedCurses() {
		return getGameObject().hasThisAttribute(Constants.CURSES_NULLIFIED) || getGameObject().hasThisAttribute(Constants.PENTAGRAM) || hasActiveInventoryThisKey(Constants.PENTAGRAM);
	}
	public void restoreCurses() {
		getGameObject().removeThisAttribute(Constants.CURSES_NULLIFIED);
	}
	public ArrayList<GameObject> getAllCursedStuff() {
		return getAllActiveInventoryThisKeyAndValue(Constants.CURSED,null);
	}
	public void deactivateAllEvilObjects() {
		for (GameObject thing:getAllCursedStuff()) {
			TreasureUtility.removeCursedItem(this,thing);
			RealmLogging.logMessage(getGameObject().getName(),"The "+thing.getName()+" is destroyed by the magic of the Chapel.");
		}
	}
	public void expireTemporaryPotions() {
		for (GameObject thing : getActiveInventory()) {
			if (thing.hasThisAttribute(Constants.ONESHOT) && thing.hasThisAttribute(Constants.POTION)) {
				expirePotion(thing);
			}
		}
	}
	// Other utility
	
	/**
	 * Returns an array of strings depicting the level number and name:
	 *   4 - Woodsgirl
	 */
    public String[] getCharacterLevels()
    {
        ArrayList<String> levels = new ArrayList<>();
        int level = 1;
        String lastLevelName = null;
        do
        {
            String levelName = getCharacterLevelName(level);
            if(levelName != null)
            {
                levels.add(level+" - "+levelName);
                lastLevelName = levelName;
                level++;
            } else
            {
                levels.add((level)+" - "+lastLevelName+" + 1");
                levels.add((level+1)+" - "+lastLevelName+" + 2");
                levels.add((level+2)+" - "+lastLevelName+" + 3");
                levels.add((level+3)+" - "+lastLevelName+" + 4");
                levels.add((level+4)+" - "+lastLevelName+" + 5");
                levels.add((level+5)+" - "+lastLevelName+" + 6");
                return levels.toArray(new String[levels.size()]);
            }
        } while(true);
    }
	public String getCharacterLevelName() {
		if (!isCharacter()) {
			return getGameObject().getName();
		}
		int level = getCharacterLevel();
		if (level<1) {
			return getGameObject().getName();
		}
		String prepend = "";
		String append = "";
		if (level>Constants.MAX_LEVEL) {
			if (level==Constants.MAX_EXP_LEVEL) {
				prepend = "Immortal ";
			}
			else {
				append = " + "+(level-Constants.MAX_LEVEL);
			}
			level = Constants.MAX_LEVEL;
		}
		String name =  prepend+getCharacterLevelName(level)+append;
		String boardNumber = "";
		if (getGameObject().hasThisAttribute(Constants.BOARD_NUMBER)) {
			boardNumber = " "+getGameObject().getThisAttribute(Constants.BOARD_NUMBER);
		}
		return name+boardNumber;
	}
	public String getCharacterLevelName(int level) {
		String levelKey = "level_"+level;
		String name = getGameObject().getAttribute(levelKey,"name");
		if (name!=null) {
			String charType = getCharacterType();
			if (charType!=null) {
				return StringUtilities.capitalize(charType)+" "+name;
			}
			return name;
		}
		return null;
	}
	/**
	 * Returns an array of strings depicting the starting locations for this character
	 */
	public String[] getStartingLocations(boolean forceInnStart) {
		ArrayList<String> startingLocations = new ArrayList<>();
		String startList = getGameObject().getThisAttribute("start");
		StringTokenizer tokens = new StringTokenizer(startList,",");
		while(tokens.hasMoreTokens()) {
			String loc = tokens.nextToken();
			startingLocations.add(loc.trim());
			if (forceInnStart) {
				// If the game has already started, then you don't get a choice, and must start at the Inn
				break;
			}
		}
		return startingLocations.toArray(new String[startingLocations.size()]);
	}
	/**
	 * Clears out everything contained by the object representing the character (all inventory and chits)
	 */
	public void clearHold() {
		getGameObject().clearHold();
	}
	/**
	 * This method is called once when the character is added, and never again.  This is a good place to general
	 * initialization for the character.
	 * 
	 * Update:  This method is now called every time you level up
	 */
	public void generalInitialization() {
		// By default, everyone get's these two for free (only relevant with the expansion)
		addOtherChitDiscovery("Magic Guild");
		addOtherChitDiscovery("Fighters Guild");
		if (getGameObject().hasThisAttribute(Constants.KNOWS_ROADS)) {
			// add ALL paths/passages
			Collection<GameObject> tiles = RealmObjectMaster.getRealmObjectMaster(getGameObject().getGameData()).getTileObjects();
			ArrayList<PathDetail> discoveries = new ArrayList<>();
			for (GameObject go : tiles) {
				TileComponent tile = (TileComponent)RealmComponent.getRealmComponent(go);
				discoveries.addAll(tile.getHiddenPaths());
				discoveries.addAll(tile.getSecretPassages());
			}
			for (PathDetail path : discoveries) {
				if (path.isHidden() && !hasHiddenPathDiscovery(path.getFullPathKey())) {
					addHiddenPathDiscovery(path.getFullPathKey());
				}
				else if (path.isSecret() && !hasSecretPassageDiscovery(path.getFullPathKey())) {
					addSecretPassageDiscovery(path.getFullPathKey());
				}
			}
		}
		// Add a familiar, if there is one, and if you need one!
		if (!DebugUtility.isDisableFamiliar() && hasFamiliar() && getFamiliar()==null) {
			setActiveFamiliar(true);
			
			GameObject familiar = getGameObject().getGameData().createNewObject();
			familiar.setName(getCharacterLevelName(4)+"'s Familiar");
			familiar.setThisAttribute("familiar");
			familiar.setThisAttribute("icon_type","familiar");
			familiar.setThisAttribute("icon_folder","characters");
			familiar.addThisAttributeListItem(Constants.SPECIAL_ACTION,"ENHANCED_PEER");
			
			CharacterWrapper charFam = new CharacterWrapper(familiar);
			charFam.setPlayerName(getPlayerName());
			charFam.setPlayerPassword(getPlayerPassword());
			charFam.setPlayerEmail(getPlayerEmail());
			charFam.setWantsCombat(false); // same default
			TileLocation tl = getCurrentLocation();
			tl.clearing.add(charFam.getGameObject(),null);
			RealmComponent rc = RealmComponent.getRealmComponent(charFam.getGameObject());
			
			addMinion(familiar);
			
			// Make this character the owner of the familiar
			rc.setOwner(RealmComponent.getRealmComponent(getGameObject()));
		}
		
		fetchCompanions();
		fetchExtraChits();
		fetchExtraInventory();
	}
	private void fetchExtraChits() {
		int currentLevel = getCharacterLevel();
		for (int n=1;n<=currentLevel;n++) {
			String levelKey = "level_"+n;
			if (getGameObject().hasAttribute(levelKey,Constants.BONUS_CHIT)) {
				addBonusChit(levelKey);
			}
			for (int i = 1; i <= 2; i++) {
				levelKey = "level_" + n + "_" + i;
				if (getGameObject().hasAttribute(levelKey,Constants.BONUS_CHIT)) {
					addBonusChit(levelKey);
				}
			}
		}
	}
	private void addBonusChit(String levelKey) {
		// Make sure chit hasn't already been created
		GameObject bonusChit = null;
		for (GameObject go : getGameObject().getHold()) {
			if (go.hasThisAttribute(RealmComponent.CHARACTER_CHIT) && levelKey.equals(go.getName())) {
				bonusChit = go;
				break;
			}
		}
		if (bonusChit == null) {
			// Nope! Let's make it.
			GameObjectBlockManager man = new GameObjectBlockManager(getGameObject());
			bonusChit = man.extractGameObjectFromBlocks(Constants.BONUS_CHIT + levelKey, false);
			bonusChit.setThisAttribute("icon_type", getGameObject().getThisAttribute("icon_type"));
			bonusChit.setThisAttribute("icon_folder", getGameObject().getThisAttribute("icon_folder"));
			bonusChit.setThisAttribute(Constants.CHIT_EARNED);
			getGameObject().add(bonusChit);
		}
	}
	private void fetchExtraInventory() {
		int currentLevel = getCharacterLevel();
		for (int n=1;n<=currentLevel;n++) {
			String levelKey = "level_"+n;
			if (getGameObject().hasAttribute(levelKey,Constants.BONUS_INVENTORY)) {
				addExtraInventory(levelKey);
			}
			for (int i = 1; i <= 2; i++) {
				levelKey = "level_" + n + "_" + i;
				if (getGameObject().hasAttribute(levelKey,Constants.BONUS_INVENTORY)) {
					addExtraInventory(levelKey);
				}
			}
		}
	}
	private void addExtraInventory(String levelKey) {
		// Make sure inventory hasn't already been created
		GameObject bonusInv = null;
		for (GameObject go : getGameObject().getHold()) {
			String tag = go.getThisAttribute(Constants.LEVEL_KEY_TAG);
			if (levelKey.equals(tag)) {
				bonusInv = go;
				break;
			}
		}
		if (bonusInv == null) {
			// Nope! Let's make it.
			GameObjectBlockManager man = new GameObjectBlockManager(getGameObject());
			bonusInv = man.extractGameObjectFromBlocks(Constants.BONUS_INVENTORY + levelKey, false);
			getGameObject().add(bonusInv);
		}
	}
	public void tagUnplayableChits() {
		// Tag unplayable chits (if any)
		String charType = getCharacterType();
		for (CharacterActionChitComponent chit:getChits(-1,true)) {
			if (CharacterWrapper.isRestrictedFromUse(charType,chit)) {
				chit.getGameObject().setThisAttribute(Constants.UNPLAYABLE);
			}
			else {
				chit.getGameObject().removeThisAttribute(Constants.UNPLAYABLE);
			}
		}
	}
	public static final String[] DONT_COPY_ATTRIBUTES = {
		"name",
		"spellcount",
		"spelltypes",
		"weapon",
		"armor",
		"custom_armor",
		"optkey",
		"badge_icon",
	};
	/**
	 * This updates the character based on their current level.  This method can be called multiple times without harm.  It will
	 * be called when the initial character is added, and then again each time they advance a level.
	 */
	public void updateLevelAttributes(HostPrefWrapper hostPrefs) {
		getGameObject().setName(getCharacterLevelName());
		eraseLevelAttributes();
		List<String> dontCopy = Arrays.asList(DONT_COPY_ATTRIBUTES);
		// This should copy attributes from level_x into the "this" block.
		// Attributes in optional_x should go into the "optional" block.
		int currentLevel = getCharacterLevel();
		for (int n = 1; n <= currentLevel; n++) {
			copyAttributes(dontCopy, "level_" + n, "this", null);
			copyAttributes(dontCopy, "optional_" + n, "optional", hostPrefs);
			for (int i = 1; i <= 2; i++) {
				copyAttributes(dontCopy, "level_" + n + "_" + i, "this", null);
				copyAttributes(dontCopy, "optional_" + n + "_" + i, "optional", hostPrefs);
			}
		}
		generalInitialization();
	}
	public ArrayList<String> getLevelAdvantages() {
		ArrayList<String> advantages = new ArrayList<>();
		ArrayList<String> list = getGameObject().getThisAttributeList("advantages");
		if (list!=null) {
			for (String i : list) {
				advantages.add(i);
			}
		}
		return advantages;
	}
	public ArrayList<String> getOptionalLevelAdvantages() {
		ArrayList<String> advantages = new ArrayList<>();
		ArrayList<String> list = getGameObject().getAttributeList("optional","advantages");
		if (list!=null) {
			for (String i : list) {
				advantages.add(i);
			}
		}
		return advantages;
	}
	private void copyAttributes(List<String> dontCopy,String blockName,String targetBlock,HostPrefWrapper hostPrefs) {
		OrderedHashtable<String, Object> levelBlock = getGameObject().getAttributeBlock(blockName);
		if (hostPrefs!=null) {
			String optKey = (String)levelBlock.get("optkey");
			if (optKey!=null && !hostPrefs.hasPref(optKey)) {
				// Don't copy the attributes, if the specified option is not turned on
				return;
			}
		}
		for (String key : levelBlock.keySet()) {
			if (!dontCopy.contains(key)) {
				Object val = levelBlock.get(key);
				if (val instanceof ArrayList) {
					ArrayList list = (ArrayList)val;
					for (Iterator s=list.iterator();s.hasNext();) {
						String string = (String)s.next();
						getGameObject().addAttributeListItem(targetBlock,key,string);
					}
				}
				else {
					getGameObject().setAttribute(targetBlock,key,(String)val);
				}
			}
		}
	}
	public void eraseLevelAttributes() {
		for (int n = 1; n <= Constants.MAX_LEVEL; n++) {
			String blockName = "level_" + n;
			eraseLevelBlock(blockName);
			for (int i = 1; i <= 2; i++) {
				blockName = "level_" + n + "_" + i;
				eraseLevelBlock(blockName);
			}
		}
		getGameObject().removeAttributeBlock("optional");
	}
	private void eraseLevelBlock(String blockName) {
		List<String> dontCopy = Arrays.asList(DONT_COPY_ATTRIBUTES);
		OrderedHashtable<String,Object> levelBlock = getGameObject().getAttributeBlock(blockName);
		for (String key : levelBlock.keySet()) {
			if (!dontCopy.contains(key)) {
				getGameObject().removeThisAttribute(key);
			}
		}
	}
	/**
	 * Fetches the character's chits based on level
	 */
	public void fetchCharacterChits(GameData data) {
		GamePool pool = new GamePool(data.getGameObjects());
		int charLevel = getCharacterLevel();
		for (int i=1;i<=charLevel;i++) {
			String levelKey = "character_level="+i;
			fetchChits(levelKey,pool);
		}
	}
	/**
	 * Fetches the chits for a given level, and adds them to the wrapped getGameObject()
	 */
	public void fetchChits(String levelKey,GamePool pool) {
		ArrayList<String> keyVals = new ArrayList<>();
		keyVals.add("character_chit="+getGameObject().getThisAttribute(Constants.ICON_TYPE));
		keyVals.add(levelKey);
		Collection<GameObject> found = pool.extract(keyVals);
		getGameObject().addAll(found);
	}
	public boolean canLoot(RealmComponent rc) {
		if (this.affectedByKey(Constants.TREASURE_LOCATION_FEAR) && rc.getGameObject().hasThisAttribute(RealmComponent.TREASURE_LOCATION)) {
			ArrayList<String> fears = new ArrayList<>();
			fears.addAll(this.getGameObject().getThisAttributeList(Constants.TREASURE_LOCATION_FEAR));
			fears.addAll(this.getActiveInventoryValuesForThisKey(Constants.TREASURE_LOCATION_FEAR,","));
			
			String name = rc.getGameObject().getThisAttribute(RealmComponent.TREASURE_LOCATION);
			return !fears.contains(name);
		}
		return true;
	}
	/**
	 * @param spell	The spell to test.
	 * 
	 * @return		true if the character can learn the provided spell
	 */
	public boolean canLearn(GameObject spell) {
		return canLearn(spell,false);
	}
	public boolean canLearn(GameObject spell,boolean ignoreDuplicates) {
		// Check to see if character has a chit to support learning the new spell
		int number = RealmUtility.convertMod(spell.getThisAttribute("spell"));
		
		// Examine all chits for a match (active, inactive, or enchanted)
		boolean hasType = false;
		ArrayList<RealmComponent> allChits = new ArrayList<>();
		allChits.addAll(getAllChits());
		allChits.addAll(getDedicatedChits()); // don't forget the chits dedicated to spells!
		allChits.addAll(getTransmorphedChits()); // and let's grab those that are transformed too, since that is also legal (apparently) - see bug 1733
		for (RealmComponent i : allChits) {
			CharacterActionChitComponent chit = (CharacterActionChitComponent)i;
			if (chit.isMagic()) {
				if (chit.getMagicNumber()==number) {
					hasType = true;
					break;
				}
			}
		}
		if (!hasType) {
			HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameObject().getGameData());
			if (hostPrefs.hasPref(Constants.OPT_ENHANCED_ARTIFACTS) || affectedByKey(Constants.ENHANCED_ARTIFACTS)) {
				// Search artifacts
				for (GameObject item:getInventory()) {
					RealmComponent rc = RealmComponent.getRealmComponent(item);
					if (rc.isMagicChit()) { // doesn't matter if it is enchanted or not!
						MagicChit chit = (MagicChit)rc;
						if (chit.getEnchantableNumbers().contains(number) || (rc.isTreasure() && ((TreasureCardComponent)rc).getAllMagicNumbers(8).contains(number))) {
							hasType = true;
							break;
						}
					}
				}
			}
		}
		
		if (hasType) {
			// Now check to see that the character doesn't already have this spell recorded
			return ignoreDuplicates || !hasAlreadyLearned(spell);
		}
		return false;
	}
	public boolean hasAlreadyLearned(GameObject spell) {
		// Now check to see that the character doesn't already have this spell recorded
		for (GameObject recSpell:getRecordedSpells(spell.getGameData())) {
			if (recSpell.getName().equals(spell.getName())) { // look for matches based on name
				return true;
			}
		}
		return false;
	}
	public void startingSpell(GameObject spell) {
		// Clone the spell
		spell = spell.getGameData().createNewObject(spell);
		
		// Mark the spell as instance
		spell.setThisAttribute(Constants.SPELL_INSTANCE); // prevents it from appearing in spell chooser for other characters
		
		// Add it to starting spells
		addListItem(STARTING_SPELLS,String.valueOf(spell.getId()));
		
		// Add it to character
		getGameObject().add(spell);
	}
	public GameObject recordNewSpell(JFrame frame,GameObject spell) {
		return recordNewSpell(frame,spell,false);
	}
	public GameObject recordNewSpell(JFrame frame,GameObject spell,boolean force) {
		// Check spell limits
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameObject().getGameData());
		if (!force && !hostPrefs.hasPref(Constants.HOUSE2_NO_SPELL_LIMIT)) {
			// Not NO_SPELL_LIMIT, which means there IS a spell limit
			int total = getListCount(STARTING_SPELLS) + getListCount(RECORDED_SPELLS);
			if (total==Constants.MAX_SPELL_COUNT) {
				// You are at the max, and must erase a recorded spell
				RealmComponent rc = RealmComponent.getRealmComponent(spell);
				int ret = JOptionPane.showConfirmDialog(
						frame,
						"You already have "+total+" spells recorded.\n\nDo you want to erase one so that this new spell can be added?",
						"All Spell Slots Full",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE,
						rc.getIcon());
				if (ret==JOptionPane.YES_OPTION) {
					RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(
							frame,
							"Which spell to erase?",
							true);
					for (GameObject go:getAllSpells()) {
						SpellWrapper rs = new SpellWrapper(go);
						if (!rs.isAlive()) {
							chooser.addGameObjectToOption(chooser.generateOption(),go);
						}
					}
					chooser.addOption("none","None");
					chooser.setVisible(true);
					
					String key = chooser.getSelectedOptionKey();
					if (key!=null) {
						if ("none".equals(key)) {
							return null;
						}
						RealmComponent sc = chooser.getFirstSelectedComponent();
						if (!eraseSpell(sc.getGameObject())) {
							// This should NEVER happen!!!
							return null;
						}
					}
				}
				else {
					return null;
				}
			}
		}
		
		// Clone the spell
		spell = spell.getGameData().createNewObject(spell);
		
		// Mark the spell as instance
		spell.setThisAttribute(Constants.SPELL_INSTANCE); // prevents it from appearing in spell chooser for other characters
		
		// Add it to recorded spells
		addListItem(RECORDED_SPELLS,String.valueOf(spell.getId()));
		
		// Add it to character
		getGameObject().add(spell);
		
		return spell;
	}
	public boolean eraseSpell(GameObject spell) {
		if (!removeListItem(STARTING_SPELLS,spell.getStringId())) {
			if (!removeListItem(RECORDED_SPELLS,spell.getStringId())) {
				// This should NEVER happen!!!
				return false;
			}
		}
		return true;
	}
	public boolean isStartingSpell(GameObject spell) {
		ArrayList<String> sSpellIds = getList(STARTING_SPELLS);
		return sSpellIds!=null && sSpellIds.contains(spell.getStringId());
	}
	public boolean isRecordedSpell(GameObject spell) {
		ArrayList<String> rSpellIds = getList(RECORDED_SPELLS);
		return rSpellIds!=null && rSpellIds.contains(spell.getStringId());
	}
	public boolean hasSpells() {
		return !getAllSpellIds().isEmpty();
	}
	private ArrayList<String> getAllSpellIds() {
		ArrayList<String> sSpellIds = getList(STARTING_SPELLS);
		ArrayList<String> rSpellIds = getList(RECORDED_SPELLS);
		ArrayList<String> spellIds = new ArrayList<>();
		if (sSpellIds!=null) {
			spellIds.addAll(sSpellIds);
		}
		if (rSpellIds!=null) {
			spellIds.addAll(rSpellIds);
		}
		return spellIds;
	}
	public ArrayList<MagicChit> getColorMagicChits() {
		ArrayList<MagicChit> colorChits = new ArrayList<>();
		colorChits.addAll(getColorChits());
		colorChits.addAll(getEnchantedArtifacts());
		return colorChits;
	}
	public boolean hasOnlyStaffAsActivatedWeapon() {
		ArrayList<GameObject> activeInventory = this.getActiveInventory();
		for (GameObject item : activeInventory) {
			RealmComponent rc = RealmComponent.getRealmComponent(item);
			if (rc.isWeapon() && !item.getName().toLowerCase().matches("staff")) {
				return false;
			}
		}
		return true;
	}
	public boolean hasActiveArmorChits() {
		ArrayList<GameObject> activeInventory = this.getActiveInventory();
		for (GameObject item : activeInventory) {
			RealmComponent rc = RealmComponent.getRealmComponent(item);
			if (rc.isArmor() || rc.isArmorCard()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @return		A Collection of SpellSet objects representing all the spells available to the character, whether it
	 * 				be a recorded spell, or one that was awakened in a book or artifact.
	 */
	public ArrayList<SpellSet> getCastableSpellSets() {
		// Find all color sources
		Collection<ColorMagic> infiniteColors = getInfiniteColorSources();
		ArrayList<MagicChit> colorChits = new ArrayList<>();
		ArrayList<CharacterActionChitComponent> magicChits = new ArrayList<>();
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameObject().getGameData());
		if ((!hostPrefs.hasPref(Constants.FE_STEEL_AGAINST_MAGIC) && !this.affectedByKey(Constants.STAFF_RESTRICTED_SPELLCASTING)) || hasOnlyStaffAsActivatedWeapon()) {
			colorChits = getColorMagicChits();
			// Find all available magic chits
			magicChits = getActiveMagicChits();
		}
		
		// Start a collection of potential spell sets
		ArrayList<SpellSet> potentialSets = new ArrayList<>();
		
		// Collect all spells, recorded and in inventory (awakened)
		TileLocation current = getCurrentLocation();
		String code = null;
		if (current!=null && current.hasClearing() && !current.isBetweenClearings()) {
			code = current.clearing.getTypeCode();
		}
		for (GameObject go:getAllSpells()) {
			SpellWrapper spell = new SpellWrapper(go);
			if (current!=null) { // battle sim
				if (spell.canCast(code,current.tile.getClearingCount())) {
					SpellSet set = new SpellSet(spell.getGameObject());
					potentialSets.add(set);
				}
			}
		}
		
		// Find all treasures with spells on them
		String dayKey = getCurrentDayKey();
		for (GameObject treasure:getActivatedTreasureObjects()) {
			Collection<GameObject> awakenedSpells = SpellUtility.getSpells(treasure,Boolean.TRUE,false,false);
			if (awakenedSpells.size()>0) {
				ArrayList<String> availMagicTypes = TreasureCardComponent.readAvailableMagicTypes(dayKey,treasure);
				if (availMagicTypes.isEmpty()) continue;
				for (GameObject go : awakenedSpells) {
					SpellWrapper spell = new SpellWrapper(go);
					if (spell.canCast(code,current.tile.getClearingCount())) {
						SpellSet set = new SpellSet(spell.getGameObject());
						String spellType = set.getCastMagicType();
						if (availMagicTypes.contains(spellType)) {
							set.addTypeObject(treasure);
							potentialSets.add(set);
						}
					}
				}
			}
		}
		
		// Now filter out the non-castable spells (missing some component)
		boolean optionalArtifacts = hostPrefs.hasPref(Constants.OPT_ENHANCED_ARTIFACTS) || affectedByKey(Constants.ENHANCED_ARTIFACTS);
		ArrayList<SpellSet> castableSpellSets = new ArrayList<>();
		for (SpellSet set:potentialSets) {
			// First, validate chit types (if needed)
			String spellType = set.getCastMagicType();
			if (set.getValidTypeObjects().size()==0
					|| optionalArtifacts) { // Allows MAGIC chits to cast spells on artifacts!
				for (CharacterActionChitComponent chit : magicChits) {
					if (spellType.equals(chit.getMagicType()) && !set.alreadyHasChit(chit)) {
						set.addTypeObject(chit.getGameObject());
					}
				}
			}
			// Check for artifacts and spell books
			for (GameObject treasure:getActivatedTreasureObjects()) {
				if (optionalArtifacts || treasure.hasThisAttribute(Constants.RING) || treasure.hasThisAttribute(Constants.PROVIDES_MAGIC_CHIT)) {
					if (treasure.hasThisAttribute(SpellWrapper.INCANTATION_TIE)) {
						continue; // tied up treasures cannot be used again
					}
					ArrayList<String> availMagicTypes = TreasureCardComponent.readAvailableMagicTypes(dayKey,treasure);
					if (availMagicTypes.contains(spellType)) {
						MagicChit chit = (MagicChit)RealmComponent.getRealmComponent(treasure);
						if (!chit.isColor()) {
							set.addTypeObject(treasure);
						}
					}
				}
			}
			if (set.getValidTypeObjects().size()>0) {
				// Second, validate colors (but only if it has valid type objects!
				ColorMagic spellColor = set.getColorMagic();
				if (spellColor==null) { // ANY
					if (infiniteColors.size()>0) {
						set.setInfiniteColor(infiniteColors.iterator().next());
					}
					if (colorChits.size()>0) {
						// Any of the chits may be used to cast the spell
						set.addColorChits(colorChits);
					}
				}
				else { // Specific color
					if (infiniteColors.contains(spellColor)) {
						set.setInfiniteColor(spellColor);
					}
					else {
						// No infinite source?  Search the color chits
						for (MagicChit colorChit:colorChits) {
							if (colorChit.getColorMagic().sameColorAs(spellColor)) {
								set.addColorChit(colorChit);
							} else if (spellColor.isPrismColor() && colorChit.getColorMagic().isPrismColor()) {
								if (this.affectedByKey(Constants.CHITS_PRISM_COLOR)) {
									set.addColorChit(colorChit);
								}
							}
						}
					}
				}
			}
			
			if (set.getSpell().hasThisAttribute("non_mountain_clearing")) {
				if (current!=null && current.tile!=null && current.tile.getTileType().matches("M")) {
					continue;
				}
			}
			if (set.getSpell().hasThisAttribute("river_or_adjacent_hex")) {
				boolean riverTile = false;
				boolean adjacentIsRiverTile = false;
				if (current==null || current.tile==null) {
					continue;
				}
				
				if (current.tile.getTileType().matches("ST")) {
					riverTile = true;
				} else {
					for (TileComponent adjacentTile : current.tile.getAllAdjacentTiles()) {
						if (adjacentTile!=null && adjacentTile.getTileType().matches("ST")) {
							adjacentIsRiverTile = true;
							break;
						}
					}
				}
				if (!riverTile && !adjacentIsRiverTile) {
					continue;
				}			
			}
			
			// Finally, add the spell set, but only if it can be cast
			if (set.canBeCast() && !castableSpellSets.contains(set)) {
				castableSpellSets.add(set);
			}
		}
		
		return castableSpellSets;
	}
	
	/**
	 * Returns a collection of GameObjects representing all spells (starting and recorded)
	 */
	public ArrayList<GameObject> getAllSpells() {
		GameData data = getGameObject().getGameData();
		ArrayList<GameObject> allSpells = new ArrayList<>();
		ArrayList<String> spellIds = getAllSpellIds();
		if (!spellIds.isEmpty()) {
			for (String id : spellIds) {
				GameObject spell = data.getGameObject(Long.valueOf(id));
				allSpells.add(spell);
			}
		}
		return allSpells;
	}
	public static ArrayList<GameObject> getAllVirtualSpellsFor(GameObject spell) {
		ArrayList<GameObject> virtualSpells = new ArrayList<>();
		findVirtualSpellsFor(spell,virtualSpells);
		return virtualSpells;
	}
	public static ArrayList<GameObject> getAllVirtualSpellsFor(ArrayList<GameObject> spells) {
		// Add the virtual spell cards (enhanced magic) here
		ArrayList<GameObject> virtualSpells = new ArrayList<>();
		for (GameObject spell:spells) {
			findVirtualSpellsFor(spell,virtualSpells);
		}
		return virtualSpells;
	}
	private static void findVirtualSpellsFor(GameObject spell,ArrayList<GameObject> virtualSpells) {
		for (GameObject go : spell.getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isSpell()) {
				SpellWrapper vSpell = new SpellWrapper(go);
				if (vSpell.isAlive() && vSpell.isVirtual()) {
					virtualSpells.add(go);
				}
			}
		}
	}
	public int getRecordedSpellCount() {
		ArrayList<String> spellIds = getList(RECORDED_SPELLS);
		return spellIds==null?0:spellIds.size();
	}
	public ArrayList<GameObject> getRecordedSpells(GameData data) {
		ArrayList<GameObject> recordedSpells = new ArrayList<>();
		ArrayList<String> spellIds = getList(RECORDED_SPELLS);
		if (spellIds!=null && !spellIds.isEmpty()) {
			for (String id : spellIds) {
				recordedSpells.add(data.getGameObject(Long.valueOf(id)));
			}
		}
		return recordedSpells;
	}
	public void calculateStartingWorth() {
		int worth = getStartingGold();
		for (GameObject item : getInventory()) {
			int basePrice = item.getThisInt("base_price");
			if (basePrice==0) {
				// might be armor
				basePrice = item.getInt("intact","base_price");
			}
			worth += basePrice;
		}
		setInt(STARTING_GOLD_VALUE,worth);
	}
	/**
	 * Fetches all the character's starting inventory
	 */
	public void fetchStartingInventory(JFrame parentFrame,GameData data,boolean chooseSource) {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(data);
		String hostKeyVals = hostPrefs.getGameKeyVals();
        int startingInventoryLevel = getCharacterLevel();
        if (startingInventoryLevel > 4)
        {
            startingInventoryLevel = 4;
        }
		String levelKey = "level_"+startingInventoryLevel;
		GamePool pool = new GamePool(data.getGameObjects());
		fetchStartingWeapon(parentFrame,levelKey,pool,hostKeyVals,chooseSource,hostPrefs);
		fetchStartingArmor(parentFrame,levelKey,pool,hostKeyVals,chooseSource,hostPrefs);
		for (int i = 1; i <= 2; i++) {
			levelKey = "level_" + startingInventoryLevel + "_" + i;
			fetchStartingWeapon(parentFrame, levelKey, pool, hostKeyVals, chooseSource,hostPrefs);
			fetchStartingArmor(parentFrame, levelKey, pool, hostKeyVals, chooseSource,hostPrefs);
		}
	}
	private void fetchCompanions() { // custom characters only
		ArrayList<String> companionNames = getGameObject().getThisAttributeList(Constants.COMPANION_NAME);
		if (companionNames!=null && !companionNames.isEmpty()) {
			// Remove any existing companions (in case we are developing here!)
			for (RealmComponent hireling:getAllHirelings()) {
				if (hireling.getGameObject().hasThisAttribute("companion")) {
					String name = hireling.getGameObject().getName();
					if (hireling.getGameObject().hasThisAttribute(Constants.BOARD_NUMBER)) {
						name = name.substring(0,name.length()-2);
					}
					companionNames.remove(name);
				}
			}
			
			// Now fetch em
			ArrayList<GameObject> ccs = CustomCharacterLibrary.getSingleton().getCharacterCompanions(getGameObject());
			if (!ccs.isEmpty()) {
				String board = getGameObject().getThisAttribute(Constants.BOARD_NUMBER);
				for (String name : companionNames) {
					for (GameObject go:ccs) {
						if (go.getName().equals(name)) {
							GameObject companion = getGameObject().getGameData().createNewObject();
							companion.copyAttributesFrom(go);
							if (board!=null) {
								companion.setName(companion.getName()+" "+board);
								companion.setThisAttribute(Constants.BOARD_NUMBER,board);
							}
							for (GameObject held : go.getHold()) {
								GameObject heldThing = getGameObject().getGameData().createNewObject();
								heldThing.copyAttributesFrom(held);
								if (board!=null) {
									heldThing.setName(companion.getName()+" "+board);
									heldThing.setThisAttribute(Constants.BOARD_NUMBER,board);
								}
								companion.add(heldThing);
							}
							companion.setThisAttribute(Constants.HIDDEN);
							getGameObject().add(companion);
							addHireling(companion);
							ccs.remove(go);
							break;
						}
					}
				}
			}
		}
	}
	private void fetchStartingWeapon(JFrame frame,String levelKey,GamePool pool,String hostKeyVals,boolean chooseSource,HostPrefWrapper hostPrefs) {
		String weapon = getGameObject().getAttribute(levelKey,"weapon");
		if (weapon==null) { //e.g. levelKey = level_4_1
			return;
		}
		GameObject item = null;
		if (getGameObject().hasThisAttribute(Constants.CUSTOM_CHARACTER)) {
			item = fetchWeaponFromTemplate(weapon);
		}
		if (item == null) {
			// Fetch from the main object pool
			item = fetchItem(frame,pool,weapon,hostKeyVals,chooseSource);
		}
		if (item==null) return; // Might be null if someone strips a character, suicides, and respawns them
		getGameObject().add(item);
		WeaponChitComponent wcc = (WeaponChitComponent)RealmComponent.getRealmComponent(item);
		wcc.setAlerted(false);
		wcc.setActivated(true);
	}
	private void fetchStartingArmor(JFrame frame,String levelKey,GamePool pool,String hostKeyVals,boolean chooseSource,HostPrefWrapper hostPrefs) {
		boolean custom = getGameObject().hasThisAttribute(Constants.CUSTOM_CHARACTER);
		String armor = getGameObject().getAttribute(levelKey,"armor");
		if (armor!=null) {
			StringTokenizer st = new StringTokenizer(armor,",");
			boolean twoHandedWeaponResctriction = hostPrefs.hasPref(Constants.OPT_TWO_HANDED_WEAPONS) && !this.affectedByKey(Constants.STRONG);
			while(st.hasMoreTokens()) {
				String token = st.nextToken();
				GameObject item = fetchItem(frame,pool,token,hostKeyVals,!custom && chooseSource);
				if (item!=null) { // Might be null if someone strips a character, suicides, and respawns them
					if (custom) {
						// make a copy of the item
						GameObject go = item;
						item = getGameObject().getGameData().createNewObject();
						item.copyAttributesFrom(go);
					}
					getGameObject().add(item);
					ArmorChitComponent acc = (ArmorChitComponent)RealmComponent.getRealmComponent(item);
					acc.setIntact(true);
					if (item.hasThisAttribute(Constants.SHIELD) && twoHandedWeaponResctriction) {
						acc.setActivated(false);
					} else {
						acc.setActivated(true);
					}
				}
			}
		}
		
		if (custom) {
			String custom_armor = getGameObject().getAttribute(levelKey,"custom_armor");
			if (custom_armor == null) return;
			GameObject customArmor = fetchArmorFromTemplate(custom_armor);
			if (customArmor == null) {
				// Fetch from the main object pool
				customArmor = fetchItem(frame,pool,custom_armor,hostKeyVals,chooseSource);
			}
			if (customArmor==null) return; // Might be null if someone strips a character, suicides, and respawns them
			getGameObject().add(customArmor);
		}
	}
	private GameObject fetchWeaponFromTemplate(String weapon) {
		return fetchItemFromTemplate(weapon, Constants.WEAPON_START_LOCATION);
	}
	private GameObject fetchArmorFromTemplate(String armorName) {
		return fetchItemFromTemplate(armorName, Constants.ARMOR_START_LOCATION);
	}
	private GameObject fetchItemFromTemplate(String item_name, String startLocation) {
		// Fetch from a template
		ArrayList<GameObject> unFilteredItems = null;
		if (startLocation.matches(Constants.WEAPON_START_LOCATION)) {
			unFilteredItems = CustomCharacterLibrary.getSingleton().getCharacterWeapons(getGameObject());
		}
		else if (startLocation.matches(Constants.ARMOR_START_LOCATION)) {
			unFilteredItems = CustomCharacterLibrary.getSingleton().getCharacterArmor(getGameObject());
		}
		else return null;
		
		//filter duplicates
		ArrayList<GameObject> items = new ArrayList<>();
		ArrayList<String> itemNames = new ArrayList<>();
		for (GameObject go:unFilteredItems) {
			if (!itemNames.contains(go.getName())) {
				items.add(go);
				itemNames.add(go.getName());
			}
		}
		GameObject item = null;
		if (!items.isEmpty()) {
			String board = getGameObject().getThisAttribute(Constants.BOARD_NUMBER);
			for (GameObject go:items) {
				GameObject newItem = getGameObject().getGameData().createNewObject();
				newItem.copyAttributesFrom(go);
				if (board!=null) {
					newItem.setName(newItem.getName()+" "+board);
					newItem.setThisAttribute(Constants.BOARD_NUMBER,board);
				}
				if (go.getName().equals(item_name)) {
					// Found it - copy the attributes
					item = newItem;
					getGameObject().add(item);
				}
				else {
					String weight = "";
					if (startLocation.matches(Constants.WEAPON_START_LOCATION)) {
						WeaponChitComponent wcc = (WeaponChitComponent)RealmComponent.getRealmComponent(newItem);
						wcc.setAlerted(false);
						wcc.setActivated(false);
						weight = wcc.getWeight().toString();
					}
					if (startLocation.matches(Constants.ARMOR_START_LOCATION)) {
						ArmorChitComponent acc = (ArmorChitComponent)RealmComponent.getRealmComponent(newItem);
						acc.setActivated(false);
						weight = acc.getWeight().toString();
					}
					
					// otherwise, the item should be added to an appropriate native group
					String itemLocation = newItem.getThisAttribute(startLocation);
					if (itemLocation==null) {
						// Use weight to determine
						itemLocation = "Inn";
						if ("L".equals(weight)) { // House - Soldiers
							itemLocation = "House";
						}
						else if ("M".equals(weight)) { // Guard - Guard
							itemLocation = "Guard";
						}
						else if ("H".equals(weight)) { // Chapel - Order
							itemLocation = "Chapel";
						}
						// There should never be a T here:  the weaponLocation would not be null in the case of a custom weapon 
					}
					if (!itemLocation.equals("None")) { // should never BE null, but oh well
						if (board!=null) {
							itemLocation = itemLocation+" "+board;
						}
						
						GameObject dwelling = getGameObject().getGameData().getGameObjectByName(itemLocation);
						dwelling.add(newItem);
					}
				}
			}
		}
		return item;
	}
	private GameObject fetchItem(JFrame frame,GamePool pool,String itemName,String hostKeyVals,boolean chooseSource) {
		GameObject item = null;
		itemName = RealmUtility.updateNameToBoard(getGameObject(),itemName);
		ArrayList<String> keyVals = new ArrayList<>();
		keyVals.add(hostKeyVals);
		keyVals.add("name="+itemName);
		keyVals.add("!magic");
		ArrayList<GameObject> found = pool.extract(keyVals);
		if (found!=null && found.size()>0) {
			ArrayList<GameObject> available = new ArrayList<>();
			for (GameObject obj:found) {
				GameObject heldBy = obj.getHeldBy();
				if (heldBy==null || !heldBy.hasKey(NAME_KEY)) {
					available.add(obj);
				}
			}
			
			if (available.size()>0) {
				if (chooseSource) {
					RealmComponentOptionChooser chooser = new RealmComponentOptionChooser(frame,"Choose a "+itemName+":",false);
					Hashtable<String,GameObject> hashOptions = new Hashtable<>();
					for (GameObject obj:available) {
						String where = obj.getHeldBy()==null?"?":obj.getHeldBy().getName();
						String option = chooser.generateOption(where);
						chooser.addGameObjectToOption(option,obj);
						hashOptions.put(option,obj);
					}
					chooser.setVisible(true);
					
					String selectedOption = chooser.getSelectedOptionKey();
					item = hashOptions.get(selectedOption);
				}
				else {
					item = available.get(RandomNumber.getRandom(available.size()));
				}
			}
		}
		return item;
	}
	public ArrayList<String> getAllDayKeys() {
		return getGameObject().getAttributeList(PLAYER_BLOCK,ALL_DAYS);
	}
	public void clearAllDays() {
		ArrayList<String> list = getAllDayKeys();
		if (list!=null) {
			for (String dayKey : list) {
				getGameObject().removeAttributeBlock(dayKey);
			}
		}
	}
	public int getBasicPhases(String dayKey) {
		return getGameObject().getInt(dayKey,BASIC_PHASES);
	}
	public int getSunlightPhases(String dayKey) {
		return getGameObject().getInt(dayKey,SUNLIGHT_PHASES);
	}
	public int getShelteredPhases(String dayKey) {
		return getGameObject().getInt(dayKey,SHELTERED_PHASES);
	}
	public void startNewDay(RealmCalendar cal,HostPrefWrapper hostPrefs) {
		applyMidnight();
		String dayKey = getCurrentDayKey();
		if (!getGameObject().hasAttributeBlock(dayKey)) {
			getGameObject().addAttributeListItem(PLAYER_BLOCK,ALL_DAYS,dayKey);
			getGameObject().setAttribute(dayKey,BASIC_PHASES,String.valueOf(getBasicPhases()));
			getGameObject().setAttribute(dayKey,SUNLIGHT_PHASES,String.valueOf(getSunlightPhases()));
			getGameObject().setAttribute(dayKey,SHELTERED_PHASES,String.valueOf(getShelteredPhases()));
			
			TileLocation current = getCurrentLocation();
			
			int month = getCurrentMonth();
			if (cal.isFagitue1Outside(month)) {
				if (!current.isInside(hostPrefs.hasPref(Constants.HOUSE2_RED_SPECIAL_SHELTER))) {
					setWeatherFatigue(1);
				}
			}
			else if (cal.isFatigue1Heat(month)) {
				if (!current.isShaded()) {
					setWeatherFatigue(1);
				}
			}
			
			if (isCharacter()) {
				// Volcano logic
				if (current.hasClearing()) {
					updateClearingEffects(current.clearing);
					if (current.isBetweenClearings()) {
						updateClearingEffects(current.getOther().clearing);
					}
				}
			}
			
			// If no move history has been recorded yet, then be sure to put the current location in!
			if (!hasMoveHistory()) {
				addMoveHistory(getCurrentLocation());
			}
			
			// Be sure to bump the day!
			bumpDayMoveHistory();
		}
		else {
			throw new IllegalArgumentException("That month/day has already been recorded for this character!!");
		}
	}
	private void updateClearingEffects(ClearingDetail clearing) {
		if (!getGameObject().hasThisAttribute(Constants.NO_TERRAIN_HARM)) {
			if (clearing.hasExtra("fatigue")) {
				setWeatherFatigue(1); // NOT cumulative with weather
			}
			else if (clearing.hasExtra("wound")) {
				setExtraWounds(1);
			}
			else if (clearing.hasExtra("serious_wound")) {
				setExtraWounds(RandomNumber.getDieRoll());
			}
		}
	}
	public DieRoller getMonsterRoll(String dayKey) {
		String val = getGameObject().getAttribute(dayKey,MONSTER_ROLL);
		if (val!=null) {
			return new DieRoller(val,25,5);
		}
		return null;
	}
	public void setTodaysMonsterRoll(DieRoller roller) {
		String dayKey = getCurrentDayKey();
		getGameObject().setAttribute(dayKey,MONSTER_ROLL,roller.getStringResult());
	}
	public String getCurrentDayKey() {
		return DayKey.getString(getCurrentMonth(),getCurrentDay());
	}
	public ArrayList<TileLocation> getClearingPlot() {
		return clearingPlot;
	}
	public void setClearingPlot(ArrayList<TileLocation> clearingPlot) {
		this.clearingPlot = clearingPlot;
	}
	public void resetClearingPlot() {
		clearingPlot = null;
	}
	public void rebuildClearingPlot() {
		clearingPlot = new ArrayList<>();
		clearingPlot.add(getCurrentLocation());

		for (String action : getCurrentActions()) {
			if (action.indexOf('-')>0) {
				TileLocation tl = ClearingUtility.deduceLocationFromAction(getGameObject().getGameData(),action);
				clearingPlot.add(tl);
			}
		}
	}

	public void chompClearingPlot() {
		if (clearingPlot!=null && !clearingPlot.isEmpty()) {
			clearingPlot.remove(clearingPlot.size()-1);
			if (clearingPlot.isEmpty()) {
				clearingPlot = null;
			}
		}
	}
	public void doHeal() {
		if (hasCurse(Constants.WITHER)) {
			removeCurse(Constants.WITHER);
		}
		ArrayList<CharacterActionChitComponent> chitsToHeal = new ArrayList<>();
		chitsToHeal.addAll(getFatiguedChits());
		chitsToHeal.addAll(getWoundedChits());
		for (CharacterActionChitComponent chit : chitsToHeal) {
			chit.makeActive();
		}
	}
	public void doHealWoundsToFatigue() {
		for (CharacterActionChitComponent chit : getWoundedChits()) {
			if (chit.getEffortAsterisks()==0) {
				chit.makeActive();
				RealmUtility.reportChitFatigue(this,chit,"Healed chit to active: ");
			}
			else {
				chit.makeFatigued();
				RealmUtility.reportChitFatigue(this,chit,"Healed chit to fatigue: ");
			}
		}
	}
	/**
	 * Total number of asterisks the character can play in a single combat round.
	 */
	public int getEffortLimit() {
		int effortLimit = 2;
		if (getGameObject().hasThisAttribute(Constants.EFFORT_LIMIT)) {
			// Character may have a different starting effort limit
			effortLimit = getGameObject().getThisInt(Constants.EFFORT_LIMIT);
		}
		for (GameObject item:getActiveInventory()) {
			if (item.hasThisAttribute(Constants.EFFORT_LIMIT)) {
				effortLimit += item.getThisInt(Constants.EFFORT_LIMIT);
			}
		}
		return effortLimit;
	}
	/**
	 * Total number of asterisks the character can play without fatigue in a single combat round.
	 */
	public int getEffortFreeAsterisks() {
		if (hasActiveInventoryThisKey(Constants.NO_FATIGUE)) {
			return 99; // that should be enough to cover any situation!  :-)
		}
		return 1;
	}
	public ArrayList<CharacterActionChitComponent> getEnchantableChits() {
		if (isPhantasm()) {
			return getHiringCharacter().getEnchantableChits();
		}
		ArrayList<CharacterActionChitComponent> ret = new ArrayList<>();
		for (CharacterActionChitComponent chit:getActiveMagicChits()) {
			if ("MAGIC".equals(chit.getAction()) && chit.getMagicNumber()<6) {
				ret.add(chit);
			}
		}
		return ret;
	}
	public ArrayList<CharacterActionChitComponent> getAllMagicChits() {
		ArrayList<CharacterActionChitComponent> ret = new ArrayList<>();
		for (CharacterActionChitComponent chit : getAllChits()) {
			if ("MAGIC".equals(chit.getAction())) {
				ret.add(chit);
			}
		}
		return ret;
	}
	public ArrayList<StateChitComponent> getAllMagicStateChits() {
		ArrayList<StateChitComponent> ret = new ArrayList<>();
		for (CharacterActionChitComponent chit : getAllChits()) {
			if ("MAGIC".equals(chit.getAction())) {
				ret.add(chit);
			}
		}
		return ret;
	}
	public ArrayList<CharacterActionChitComponent> getActiveMagicChits() {
		ArrayList<CharacterActionChitComponent> ret = new ArrayList<>();
		ArrayList<CharacterActionChitComponent> pool = new ArrayList<>();
		pool.addAll(getActiveChits());
		pool.addAll(getAlertedChits()); // I think ONLY MAGIC chits can be alerted
		for (CharacterActionChitComponent chit : pool) {
			if ("MAGIC".equals(chit.getAction())) {
				ret.add(chit);
			}
		}
		return ret;
	}
	public Collection<CharacterActionChitComponent> getActiveMoveChits() {
		ArrayList<CharacterActionChitComponent> ret = new ArrayList<>();
		for (CharacterActionChitComponent chit : getActiveChits()) {
			if (chit.isMove()) {
				ret.add(chit);
			}
		}
		return ret;
	}
	public Collection<RealmComponent> getActiveMoveChitsAsRealmComponents() {
		ArrayList<RealmComponent> ret = new ArrayList<>();
		for (CharacterActionChitComponent chit : getActiveChits()) {
			if (chit.isMove()) {
				ret.add(chit);
			}
		}
		return ret;
	}
	public Collection<CharacterActionChitComponent> getActiveFightChits() {
		ArrayList<CharacterActionChitComponent> ret = new ArrayList<>();
		for (CharacterActionChitComponent chit : getActiveChits()) {
			if (chit.isFight()) {
				ret.add(chit);
			}
		}
		return ret;
	}
	public Collection<RealmComponent> getActiveFightChitsAsRealmComponents() {
		ArrayList<RealmComponent> ret = new ArrayList<>();
		for (CharacterActionChitComponent chit : getActiveChits()) {
			if (chit.isFight()) {
				ret.add(chit);
			}
		}
		return ret;
	}
	public Collection<CharacterActionChitComponent> getAllEffortChits() {
		ArrayList<CharacterActionChitComponent> allEffortChits = new ArrayList<>();
		Collection<CharacterActionChitComponent> c = getAllChits();
		for (CharacterActionChitComponent chit : c) {
			if (chit.getEffortAsterisks()>0) {
				allEffortChits.add(chit);
			}
		}
		return allEffortChits;
	}
	public Collection<CharacterActionChitComponent> getActiveEffortChits() {
		ArrayList<CharacterActionChitComponent> activeEffortChits = new ArrayList<>();
		Collection<CharacterActionChitComponent> c = getActiveChits();
		for (CharacterActionChitComponent chit : c) {
			if (chit.getEffortAsterisks()>0) {
				activeEffortChits.add(chit);
			}
		}
		return activeEffortChits;
	}
	public Collection<CharacterActionChitComponent> getActiveFightAlertChits(Speed fastestAttacker) { // only one right now:  BERSERK
		ArrayList<CharacterActionChitComponent> activeFightAlertChits = new ArrayList<>();
		Collection<CharacterActionChitComponent> c = getActiveChits();
		for (CharacterActionChitComponent chit : c) {
			if (chit.isFightAlert()) {
				Speed chitSpeed = chit.getSpeed();
				if (hasMesmerizeEffect(Constants.WEAKENED)) {
					chitSpeed = new Speed(chitSpeed.getNum()+1);
				}
				if (chitSpeed.fasterThan(fastestAttacker)) {
					activeFightAlertChits.add(chit);
				}
			}
		}
		return activeFightAlertChits;
		
	}
	public Collection<StateChitComponent> getFlyChits() {
		return getFlyChits(false);
	}
	public Collection<StateChitComponent> getFlyChits(boolean excludeActionChits) {
		ArrayList<StateChitComponent> flyChits = new ArrayList<>();
		for (GameObject go : getGameObject().getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isFlyChit()) {
				flyChits.add((StateChitComponent)rc);
			}
			else if (!excludeActionChits && rc.isActionChit()) {
				CharacterActionChitComponent chit  = (CharacterActionChitComponent)rc;
				if (chit.isFly() && chit.isActive() && chit.getGameObject().hasThisAttribute(Constants.CHIT_EARNED)) {
					flyChits.add(chit);
				}
			}
			else if (go.hasThisAttribute(Quest.QUEST_MINOR_CHARS) && go.hasThisAttribute("activated")) {
				for (GameObject bonusChit : go.getHold()) {
					if (bonusChit.hasThisAttribute(Constants.CHIT_EARNED)) {
						RealmComponent bonusChitRc = RealmComponent.getRealmComponent(bonusChit);
						CharacterActionChitComponent bonusActionChit = (CharacterActionChitComponent) bonusChitRc;
						if (bonusActionChit.isFlyChit() || (bonusActionChit.isFly() && bonusActionChit.isActive())) {
							flyChits.add(bonusActionChit);
						}
					}
				}
			}
		}
		return flyChits;
	}
	
	public ArrayList<CharacterActionChitComponent> getRestableChits() {
		ArrayList<CharacterActionChitComponent> restChoices = new ArrayList<>();
		if (!hasCurse(Constants.WITHER)) {
			restChoices.addAll(getFatiguedChits());
		}
		restChoices.addAll(getWoundedChits());
		return restChoices;
	}

	/**
	 * Returns a Collection of chits that are dedicated to spells
	 */
	public Collection<RealmComponent> getDedicatedChits() {
		ArrayList<RealmComponent> list = new ArrayList<>();
		for (SpellWrapper spell : getAliveSpells()) {
			GameObject go = spell.getIncantationObject();
			if (go!=null) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (rc.isActionChit()) {
					list.add(rc);
				}
			}
		}
		return list;
	}
	public Collection<RealmComponent> getTransmorphedChits() {
		ArrayList<RealmComponent> list = new ArrayList<>();
		if (getTransmorph()==null) return list;
		for (GameObject go : getGameObject().getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (!rc.isActionChit()) continue;
			list.add(rc);
		}
		return list;
	}
	/**
	 * Returns a sorted list of ALL chits (action and FLY)
	 */
	public ArrayList<StateChitComponent> getCompleteChitList() {
		ArrayList<StateChitComponent> list = new ArrayList<>();
		list.addAll(getAllChits());
		Collections.sort(list);
		list.addAll(0,getFlyChits(true));
		return list;
	}
	public ArrayList<CharacterActionChitComponent> getAllChits() {
		return getChits(-1);
	}
	public ArrayList<CharacterActionChitComponent> getNonWoundedChits() {
		ArrayList<CharacterActionChitComponent> list = new ArrayList<>();
		list.addAll(getActiveChits());
		list.addAll(getFatiguedChits());
		return list;
	}
	public ArrayList<CharacterActionChitComponent> getActiveAndAlertChits() {
		ArrayList<CharacterActionChitComponent> list = new ArrayList<>();
		list.addAll(getActiveChits());
		list.addAll(getAlertedChits());
		return list;
	}
	public ArrayList<CharacterActionChitComponent> getActiveChits() { // normal chit state
		return getChits(CharacterActionChitComponent.ACTIVE_ID);
	}
	public ArrayList<CharacterActionChitComponent> getFatiguedChits() {
		return getChits(CharacterActionChitComponent.FATIGUED_ID);
	}
	public ArrayList<CharacterActionChitComponent> getWoundedChits() {
		return getChits(CharacterActionChitComponent.WOUNDED_ID);
	}
	public ArrayList<CharacterActionChitComponent> getAlertedChits() {
		return getChits(CharacterActionChitComponent.ALERT_ID);
	}
	public ArrayList<CharacterActionChitComponent> getColorChits() {
		if (isPhantasm()) {
			return getHiringCharacter().getColorChits();
		}
		ArrayList<CharacterActionChitComponent> ret = new ArrayList<>();
		ret.addAll(getChits(CharacterActionChitComponent.COLOR_WHITE_ID));
		ret.addAll(getChits(CharacterActionChitComponent.COLOR_GRAY_ID));
		ret.addAll(getChits(CharacterActionChitComponent.COLOR_GOLD_ID));
		ret.addAll(getChits(CharacterActionChitComponent.COLOR_PURPLE_ID));
		ret.addAll(getChits(CharacterActionChitComponent.COLOR_BLACK_ID));
		return ret;
	}
	public void initChits() {
		int characterLevel = getCharacterLevel();
		for (GameObject go : getGameObject().getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isActionChit()) {
				int level = go.getThisInt("level");
				if (level<=characterLevel) {
					go.setThisAttribute(Constants.CHIT_EARNED);
				}
				else {
					go.removeThisAttribute(Constants.CHIT_EARNED);
				}
			}
		}
	}
	public boolean needsAdvancement() {
		int stage = getCharacterStage();
		int chitCount = getCharacterExtraChitMarkers();
		return chitCount<stage;
	}
	public boolean needsLevelUp() {
		int level = getCharacterLevel();
		int actualStage = getCharacterExtraChitMarkers();
		int actualLevel = actualStage/3;
		return level < actualLevel;
	}
	public ArrayList<CharacterActionChitComponent> getAllActionChitsSorted(int level) {
		ArrayList<CharacterActionChitComponent> list = new ArrayList<>();
		for (GameObject go : getGameObject().getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isActionChit()) {
				CharacterActionChitComponent c = (CharacterActionChitComponent)rc;
				if (c.getGameObject().getThisInt("level")<=level) {
					list.add(c);
				}
			}
		}
		Collections.sort(list,new Comparator<CharacterActionChitComponent>() {
			public int compare(CharacterActionChitComponent c1,CharacterActionChitComponent c2) {
				int ret = 0;
				int s1 = c1.getGameObject().getThisInt("stage");
				int s2 = c2.getGameObject().getThisInt("stage");
				ret = s1-s2;
				return ret;
			}
		});
		return list;
	}
	public ArrayList<CharacterActionChitComponent> getAdvancementChits() {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameObject().getGameData());
		ArrayList<CharacterActionChitComponent> list = new ArrayList<>();
		int nextLevel = getCharacterLevel()+1;
		RealmComponent berserk = null;
		for (GameObject go : getGameObject().getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isActionChit()) {
				if (validChit(hostPrefs,go)) {
					if ("berserk".equals(go.getThisAttribute("action"))) {
						berserk = rc;
					}
					int level = go.getThisInt("level");
					if (level<=nextLevel && !go.hasThisAttribute(Constants.CHIT_EARNED)) {
						list.add((CharacterActionChitComponent)rc);
					}
				}
			}
		}
		if (berserk!=null && list.size()>1) {
			list.remove(berserk);
		}
		return list;
	}
	private static boolean validChit(HostPrefWrapper hostPrefs,GameObject go) {
		boolean optionalChits = hostPrefs!=null && hostPrefs.hasPref(Constants.OPT_CHAR_ABILITY_WIZARD_MAGIC_CHIT);
		String opt = go.getThisAttribute("optional");
		if (opt!=null) {
			boolean add = "add".equals(opt);
			if (add != optionalChits) {
				// This chit cannot be used in this scenario, so skip it!
				return false;
			}
		}
		return true;
	}
	private ArrayList<CharacterActionChitComponent> getChits(int stateId) {
		return getChits(stateId,false);
	}
	private ArrayList<CharacterActionChitComponent> getChits(int stateId,boolean includeUnearnedChits) {
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameObject().getGameData());
		boolean transmorphed = isTransmorphed();
		ArrayList<CharacterActionChitComponent> list = new ArrayList<>();
		ArrayList<GameObject> hold = new ArrayList<>(getGameObject().getHold());
		for (GameObject go : hold) {
			if (includeUnearnedChits || go.hasThisAttribute(Constants.CHIT_EARNED)) {
				if (validChit(hostPrefs,go)) {
					RealmComponent rc = RealmComponent.getRealmComponent(go);
					if (rc instanceof CharacterActionChitComponent) {
						CharacterActionChitComponent chit = (CharacterActionChitComponent)rc;
						if (!transmorphed || chit.isColor()) { // if transmorphed, you only have color chits
							if (stateId==-1 || chit.getStateId()==stateId) {
								list.add(chit);
							}
						}
					}
				}
			}
			else if (go.hasThisAttribute(Quest.QUEST_MINOR_CHARS) && go.hasThisAttribute("activated")) {
				for (GameObject bonusChit : go.getHold()) {
					if (bonusChit.hasThisAttribute(Constants.CHIT_EARNED)) {
						RealmComponent rc = RealmComponent.getRealmComponent(bonusChit);
						CharacterActionChitComponent bonusActionChit = (CharacterActionChitComponent) rc;
						if (!transmorphed || bonusActionChit.isColor()) { // if transmorphed, you only have color chits
							if (stateId == -1 || bonusActionChit.getStateId() == stateId) {
								list.add(bonusActionChit);
							}
						}
					}
				}
			}
		}
		return list;
	}
	public Collection<ColorMagic> getChitColorSources() {
		ArrayList<ColorMagic> ret = new ArrayList<>();
		Collection<CharacterActionChitComponent> colorChits = getColorChits();
		for (CharacterActionChitComponent chit : colorChits) {
			ret.add(chit.getColorMagic());
		}
		return ret;
	}
	public Collection<ColorMagic> getEnchantedArtifactColorSources() {
		ArrayList<ColorMagic> ret = new ArrayList<>();
		for(MagicChit chit:getEnchantedArtifacts()) {
			ret.add(chit.getColorMagic());
		}
		return ret;
	}
	public ArrayList<MagicChit> getEnchantedArtifacts() {
		ArrayList<MagicChit> ret = new ArrayList<>();
		for(GameObject go:getInventory()) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isMagicChit()) {
				MagicChit chit = (MagicChit)rc;
				if (chit.isColor()) {
					ret.add(chit);
				}
			}
		}
		return ret;
	}
	/**
	 * @return		All infinite color sources affecting the character
	 */
	public ArrayList<ColorMagic> getInfiniteColorSources() {
		// Get all clearing and color sources
		ArrayList<ColorMagic> color = new ArrayList<>();
		TileLocation tl = getCurrentLocation();
		boolean inClearing = tl!=null && tl.hasClearing() && !tl.isBetweenClearings();
		if (inClearing) {
			color.addAll(tl.clearing.getAllSourcesOfColor(false));
		}
		
		// 7th day color magic!
		RealmCalendar cal = RealmCalendar.getCalendar(getGameObject().getGameData());
		color.addAll(cal.getColorMagic(getCurrentMonth(),getCurrentDay()));
		
		if (isPhantasm()) { // can use color from character or clearing! (I think)
			color.addAll(getHiringCharacter().getInfiniteColorSources());
		}
		
		// Modify color appropriately here
		if (inClearing) {
			color = ColorMod.getConvertedColorsForThings(tl.clearing.getAllActivatedStuff(),color);
		}
		else {
			color = ColorMod.getConvertedColorsForThings(getActiveInventory(),color);
		}
		
		return color;
	}
	public void updateChitEffects() {
		// Clear out all alternate chit effects
		ArrayList<CharacterActionChitComponent> allChits = getAllChits();
		for (CharacterActionChitComponent chit:allChits) {
			chit.removeAlternateStrength();
			chit.removeAlternateSpeed();
		}
		
		// Only add back those from activated treasures/spells
		for (GameObject item:getEnhancingItems()) {
			TreasureUtility.applyChitEffects(allChits,item);
		}
		
		SpellMasterWrapper sm = SpellMasterWrapper.getSpellMaster(getGameObject().getGameData());
		for (CharacterActionChitComponent chit:allChits) {
			for (SpellWrapper spell:sm.getAffectingSpells(chit.getGameObject())) {
				if (spell.getGameObject().hasThisAttribute(Constants.FINAL_CHIT_SPEED)) {
					chit.setAlternateSpeed(new Speed(spell.getGameObject().getThisInt(Constants.FINAL_CHIT_SPEED)));
				}
				if (spell.getGameObject().hasThisAttribute(Constants.FINAL_CHIT_STRENGTH)) {
					chit.setAlternateStrength(new Strength(spell.getGameObject().getThisAttribute(Constants.FINAL_CHIT_STRENGTH)));
				}
				if (spell.getGameObject().hasThisAttribute(Constants.FINAL_CHIT_HARM)) {
					Strength newHarm = new Strength(spell.getGameObject().getThisAttribute(Constants.FINAL_CHIT_HARM));
					if (chit.getGameObject().hasThisAttribute(Constants.FINAL_CHIT_HARM)) {
						Strength chitHarm = new Strength(chit.getGameObject().getThisAttribute(Constants.FINAL_CHIT_HARM));
						if (newHarm.strongerThan(chitHarm)) {
							chit.getGameObject().setThisAttribute(Constants.FINAL_CHIT_HARM, newHarm.toString());
							chit.setAlternateStrength(new Strength(spell.getGameObject().getThisAttribute(Constants.FINAL_CHIT_HARM)));
						}
					}
					else {
						chit.getGameObject().setThisAttribute(Constants.FINAL_CHIT_HARM, newHarm.toString());
						chit.setAlternateStrength(new Strength(spell.getGameObject().getThisAttribute(Constants.FINAL_CHIT_HARM)));
					}
				}
			}
		}
	}
	/**
	 * @return			true if the character MUST fly
	 */
	public boolean mustFly() {
		GameObject transmorph = getTransmorph();
		if (transmorph!=null && (transmorph.hasThisAttribute(Constants.FLYING) || getGameObject().hasThisAttribute(Constants.GROW_WINGS))) {
			return true;
		}
		else if ((isControlledMonster() || isHiredLeader()) && (getGameObject().hasThisAttribute(Constants.FLYING) || getGameObject().hasThisAttribute(Constants.GROW_WINGS))) {
			return true;
		}
		TileLocation planned = getPlannedLocation();
		if (planned!=null && planned.isBetweenTiles()) {
			return true;
		}
		return false;
	}
	public StrengthChit getStrongestFlyStrengthChit(boolean includeFlyingSteeds) {
		ArrayList<StrengthChit> list = getFlyStrengthChits(includeFlyingSteeds);
		if (!list.isEmpty()) {
			return list.get(0); // get the strongest
		}
		return null;
	}
	public StrengthChit getFastestFlyStrengthChit(boolean includeFlyingSteeds) {
		ArrayList<StrengthChit> list = getFlyStrengthChits(includeFlyingSteeds);
		if (!list.isEmpty()) {
			Collections.sort(list,new Comparator<StrengthChit>() {
				public int compare(StrengthChit c1,StrengthChit c2) {
					return c1.getSpeed().getNum() - c2.getSpeed().getNum();
				}
			});
			return list.get(0); // get the fastest
		}
		return null;
	}
	/**
	 * @return		A StrengthChit object describing the flying ability of this character
	 */
	public ArrayList<StrengthChit> getFlyStrengthChits(boolean includeFlyingSteeds) {
		/*
		 * What makes you fly?
		 * - Flying Carpet (this should work)
		 * - Broomstick Spell (FLY CHIT)
		 * - Hurricane Winds Spell (FLY CHIT... but only during the encounter stage)
		 * - Riding a flying steed (controlled monster)
		 * - Transmorphed into a flying monster (this works)
		 * - Already flying
		 */
		ArrayList<StrengthChit> list = new ArrayList<>();
		
		GameObject transmorph = getTransmorph();
		if (transmorph!=null && (transmorph.hasThisAttribute(Constants.FLYING) || transmorph.hasThisAttribute(Constants.GROW_WINGS))) {
			list.add(new StrengthChit(
								transmorph,
								new Strength(transmorph.getThisAttribute("vulnerability")),
								BattleUtility.getMoveSpeed(RealmComponent.getRealmComponent(transmorph))));
		}
		if ((isControlledMonster() || isHiredLeader()) && (getGameObject().hasThisAttribute(Constants.FLYING) || getGameObject().hasThisAttribute(Constants.GROW_WINGS))) {
			list.add(new StrengthChit(
							getGameObject(),
							new Strength(getGameObject().getThisAttribute("vulnerability")),
							BattleUtility.getMoveSpeed(RealmComponent.getRealmComponent(getGameObject()))));
		}
		else {
			// Check Treasures (Flying Carpet and Glider)
			for (GameObject item:getEnhancingItems()) {
				String val = item.getThisAttribute("fly_strength");
				if (val!=null) {
					if (item.hasThisAttribute("clearing_req")) {
						String clearingType = item.getThisAttribute("clearing_req");
						ClearingDetail clearing = getCurrentLocation().clearing;
						if (clearing==null || !clearing.getTypeCode().matches(clearingType)) {
							continue;
						}
					}
					list.add(new StrengthChit(item,new Strength(val),new Speed(item.getThisInt("fly_speed"))));
				}
			}
			
			// Check fly chits
			for (RealmComponent rc : getFlyChits()) {
				if (rc.isFlyChit()) {
					FlyChitComponent flyChit = (FlyChitComponent)rc;
					if (flyChit.getOwnerId()==null) { // for now, only allow fly chits that are not OWNED (not malicious)
						list.add(new StrengthChit(
									flyChit.getGameObject(),
									flyChit.getStrength(),
									flyChit.getSpeed()));
					}
				}
				else {
					CharacterActionChitComponent flyChit = (CharacterActionChitComponent)rc;
					list.add(new StrengthChit(
									flyChit.getGameObject(),
									flyChit.getStrength(),
									flyChit.getSpeed()));
				}
			}
		}
		if (includeFlyingSteeds) {
			// Check followers, to see if there is a steed you can ride (find the strongest one)
			ArrayList<CharacterWrapper> followers = getActionFollowers();
			if (followers.isEmpty() && isDoRecord()) {
				// A flying steed might only have recorded that they are following you, and not yet be an actual action follower,
				// as is the case during recording.  Should capture that here somehow, so that the fly icon can be highlighted
				// during the record action phase (good for solo games).
				ArrayList<GameObject> possible = RealmObjectMaster.getRealmObjectMaster(getGameObject().getGameData()).getPlayerCharacterObjects();
				String me = getGameObject().getStringId();
				for (GameObject go:possible) {
					if (!go.equals(getGameObject()) && CharacterWrapper.hasPlayerBlock(go)) {
						CharacterWrapper poss = new CharacterWrapper(go);
						if (me.equals(poss.getFollowStringId())) {
							followers.add(poss);
						}
					}
				}
			}
			
			for (CharacterWrapper follower:followers) {
				if (follower.mustFly()) {
					// I don't think there is danger for an infinite loop here...  followers following followers
					// are sorted out long before we get here.
					list.addAll(follower.getFlyStrengthChits(false)); // actually, the "false" here will prevent a loop
				}
			}
			
			for (RealmComponent rc:getFollowingHirelings()) {
				if (rc.getGameObject().hasThisAttribute(Constants.FLYING) || rc.getGameObject().hasThisAttribute(Constants.GROW_WINGS)) {
					list.add(new StrengthChit(
									rc.getGameObject(),
									new Strength(rc.getGameObject().getThisAttribute("vulnerability")),
									BattleUtility.getMoveSpeed(RealmComponent.getRealmComponent(rc.getGameObject()))));
				}
			}
			
			for (GameObject item:getActiveInventory()) {
				RealmComponent itemRc = RealmComponent.getRealmComponent(item);
				if ((itemRc.isHorse() && ((SteedChitComponent)itemRc).flies()) || (itemRc.isNativeHorse() && ((NativeSteedChitComponent)itemRc).flies())) {
					list.add(new StrengthChit(
							item,
							new Strength(item.getThisAttribute("vulnerability")),
							BattleUtility.getMoveSpeed(itemRc)));
				}
			}
		}
		
		// Sort list by strength (strongest first)
		Collections.sort(list,new Comparator<StrengthChit>() {
			public int compare(StrengthChit c1,StrengthChit c2) {
				return c2.getStrength().getLevels() - c1.getStrength().getLevels();
			}
		});
			
		return list;
	}
	/**
	 * @return			true if the character has the means to fly
	 */
	public boolean canFly(TileLocation location) {
		if (getWeight().isMaximum()) return false;
		if (mustFly()) {
			return true;
		}
		if (location!=null && location.isFlying() && (location.isBetweenTiles() || location.isTileOnly())) {
			if (location.isTileOnly()) {
				TileLocation current = getCurrentLocation();
				if (!current.isBetweenTiles() && !getGameObject().hasThisAttribute(Constants.LAND_FIRST)) {
					return true;
				}
			}
			else {
				// already flying
				return true;
			}
		}
		StrengthChit flyStrengthChit = getStrongestFlyStrengthChit(true);
		return flyStrengthChit!=null && flyStrengthChit.getStrength().strongerOrEqualTo(getVulnerability());
	}
	/**
	 * Forces the character to land.  If they are already on the ground, this has no further effect.
	 * 
	 * @return	true if the character was flying, and then landed
	 */
	public boolean land(JFrame frame) {
		TileLocation current = getCurrentLocation();
		if (current!=null && current.clearing==null && !current.isBetweenTiles() && current.isFlying()) { // NEVER land when between tiles!!!
			if (affectedByKey(Constants.REALM_MAP)) {
				CenteredMapView.getSingleton().setMarkClearingAlertText("Select clearing to land");
				ArrayList<ClearingDetail> clearingsMarked = CenteredMapView.getSingleton().markClearingsInTile(current.tile,null,true);
				for(ClearingDetail clearing:clearingsMarked) {
					if (clearing.isAffectedByViolentWinds()) {
						clearing.setMarked(false);
					}
				}
				TileLocationChooser chooser = new TileLocationChooser(frame,CenteredMapView.getSingleton(),current);
				chooser.setVisible(true);
				CenteredMapView.getSingleton().markAllClearings(false);
				current = chooser.getSelectedLocation();
			}
			else {
				int clearingCount = current.tile.getClearingCount();
				ArrayList<Integer> clearingsTriedToLand = new ArrayList<>();
				while(current.clearing==null) {
					int r = RandomNumber.getHighLow(1,6);
					if (!current.tile.getClearing(r).isAffectedByViolentWinds()) {
						current.clearing = current.tile.getClearing(r);
					}
					else {
						if (!clearingsTriedToLand.contains(r)) {
							clearingsTriedToLand.add(r);
						}
					}
					if (clearingsTriedToLand.size() == clearingCount) return false;
				}
			}
			
			current.setFlying(false); // landing
			jumpMoveHistory(); // Because we didn't walk here
			moveToLocation(frame,current);
			
			// Make sure the LAND_FIRST attribute is removed, if used
			if (getGameObject().hasThisAttribute(Constants.LAND_FIRST)) {
				getGameObject().removeThisAttribute(Constants.LAND_FIRST);
			}
			
			if (current.clearing.isWater()) {
				RealmCalendar cal = RealmCalendar.getCalendar(getGameData());
				CharacterWrapper character = new CharacterWrapper(getGameObject());
				if (cal.isFlood(character.getCurrentMonth())) {
					character.setWeatherFatigue(character.getWeatherFatigue()+1);
				}
			}
			
			// Land all followers
			for (CharacterWrapper follower : getActionFollowers()) {
				follower.moveToLocation(frame, current);
			}
			
			// Look for any leftover followers that might have been just unhired!
			ArrayList<GameObject> hold = new ArrayList<>(getGameObject().getHold());
			for (GameObject go : hold) {
				RealmComponent rc = RealmComponent.getRealmComponent(go);
				if (!rc.isCompanion() && (rc.isMonster() || rc.isNative()) && rc.getTermOfHire()==0) {
					ClearingUtility.moveToLocation(go,current);
				}
			}
			
			return true;
		}
		return false;
	}
	/**
	 * Does all the stuff necessary to make this character gone from the board, like when they move off the map.
	 */
	public void makeGone() {
		// Make sure there is no "new" inventory
		markAllInventoryNotNew();
		
		clearNotes();
		
		// Expire bewitching spells (guarantees inventory is untransformed)
		SpellMasterWrapper spellMaster = SpellMasterWrapper.getSpellMaster(getGameObject().getGameData());
		spellMaster.expireBewitchingSpells(getGameObject());
		
		// Make sure all minions are removed from the map
		Collection<GameObject> minions = getMinions();
		if (minions!=null) {
			for (GameObject min : minions) {
				ClearingUtility.moveToLocation(min,null);
				CharacterWrapper minion = new CharacterWrapper(min);
				minion.clearPlayerAttributes();
			}
		}
		
		// Unhire hirelings left on the map, and make sure they aren't targeting anyone
		for (RealmComponent rc : getAllHirelings()) {
			GameObject hireling = rc.getGameObject();
			if (!getGameObject().getHold().contains(hireling)) {
				removeHireling(hireling);
			}
		}
		
		// Remove from clearing
		ClearingUtility.moveToLocation(getGameObject(),null);
		
		// Cancel all spells
		for (SpellWrapper spell : getAliveSpells()) {
			spell.expireSpell(); // Restored monsters will be put to location null, which is out of the game
		}
		
		// Mark GONE
		getGameObject().setThisAttribute(Constants.GONE);
	}
	/**
	 * Does all the stuff necessary to make this character "dead"
	 */
	public void makeDead(String reason) {
		TileLocation current = getCurrentLocation();
		if (current.isInClearing() && current.clearing.isEdge()) {
			PathDetail path = current.clearing.getConnectedMapEdges().get(0);
			current = new TileLocation(path.getEdgeClearing());
		}
		
		setDeathReason(reason);
		// Mark DEAD
		getGameObject().setThisAttribute(Constants.DEAD);
		
		// Make sure character name is restored
		clearCharacterType();
		setCharacterLevel(4);
		getGameObject().setName(getCharacterLevelName());
		
		// Make sure there is no "new" inventory
		markAllInventoryNotNew();
		
		clearNotes();
		
		// Expire bewitching spells (guarantees inventory is untransformed)
		SpellMasterWrapper spellMaster = SpellMasterWrapper.getSpellMaster(getGameObject().getGameData());
		spellMaster.expireBewitchingSpells(getGameObject());
		
		for (GameObject minorCharacterGo : getMinorCharacters()) {
			getGameObject().remove(minorCharacterGo);
		}
		
		// Deal with spoils
		if (!getGameObject().hasThisAttribute(Constants.SPOILS_INVENTORY_SETUP)
				&& !getGameObject().hasThisAttribute(Constants.SPOILS_INVENTORY_TAKEN)) {
			// Drop all inventory in clearing if not destined for something else
			for (GameObject item : getInventory()) {
				RealmComponent itemRc = RealmComponent.getRealmComponent(item);
				
				// Check for activated potions (which should NOT be dropped)
				if (itemRc.isTreasure() && item.hasThisAttribute("potion") && item.hasThisAttribute("activated")) {
					TreasureUtility.handleExpiredPotion(item);
				}
				else if (itemRc.isBoon()) {
					// Boons are simply lost into the ether!
					getGameObject().remove(item);
				}
				else {
					// All non-potions (activated or not) are abandoned in the clearing
					current.clearing.add(item,null);
					item.removeThisAttribute(Constants.ACTIVATED);
					item.removeThisAttribute(Constants.ENCHANTED_COLOR); // in case you have an enchanted artifact
				}
				
				if (itemRc.isCard()) {
					CardComponent card = (CardComponent)itemRc;
					if (card.getGameObject().hasThisAttribute("color_source")) {
						card.setFaceUp();
					}
					else {
						card.setFaceDown();
					}
				}
				
				if (itemRc.isWeapon()) {
					WeaponChitComponent weapon = (WeaponChitComponent)itemRc;
					weapon.setAlerted(false);
				}
			}
		}
		
		// Make sure all minions are removed from the map
		Collection<GameObject> minions = getMinions();
		if (minions!=null) {
			for (GameObject min : minions) {
				ClearingUtility.moveToLocation(min,null);
				CharacterWrapper minion = new CharacterWrapper(min);
				minion.clearPlayerAttributes();
			}
		}
		
		// Abandon all hirelings in the clearing, and make sure they aren't targeting anyone
		for (RealmComponent rc : getAllHirelings()) {
			GameObject hireling = rc.getGameObject();
			removeHireling(hireling);
		}
		
		// Remove player attributes for hired native leaders
		RealmComponent rc = RealmComponent.getRealmComponent(getGameObject());
		if (rc.isNativeLeader()) {
			HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameObject().getGameData());
			this.clearPlayerAttributes(!rc.isMonster() && hostPrefs.hasPref(Constants.HOUSE2_NATIVES_REMEMBER_DISCOVERIES));
		}
			
		Collection<RealmComponent> beforeCc = current.clearing.getClearingComponents();
		
		// Cancel all spells
		for (SpellWrapper spell : getAliveSpells()) {
			spell.expireSpell(); // Restored monsters will be put into character's clearing, where they will be immediately killed in the next bit of code
		}
		
		// Be sure to kill off any monsters that reappear
		Collection<RealmComponent> afterCc = current.clearing.getClearingComponents();
		afterCc.removeAll(beforeCc);
		if (afterCc.size()>0) {
			for (RealmComponent appear : afterCc) {
				if (appear.isMonster()) {
					RealmUtility.makeDead(appear);
				}
			}
		}
		
		// Remove from clearing
		ClearingUtility.moveToLocation(getGameObject(),null);
		
		// Mark all chits active again
		for (CharacterActionChitComponent chit : getAllChits()) {
			chit.makeActive();
		}
	}
	/**
	 * @return		null if thing can be activated, or a String describing why not.
	 */
	public String getActivateFailReason(GameObject thing) {
		return null;
	}
	/**
	 * @return		null if thing can be deactivated, or a String describing why not.
	 */
	public String getDeactivateFailReason(GameObject thing) {
		return null;
	}
	
	// Static methods
	private static Hashtable<String,ActionId> actionIdHash = null;
	public static DayAction getActionForString(String action) {
		ActionId id = getIdForAction(action);
		if (id!=ActionId.NoAction) {
			return DayAction.getDayAction(id);
		}
		return null;
	}
	public static ActionId getIdForAction(String action) {
		if (actionIdHash==null) {
			// build it
			actionIdHash = new Hashtable<>();
			
			actionIdHash.put(DayAction.getDayAction(ActionId.Hide).getCode(),ActionId.Hide);
			actionIdHash.put(DayAction.getDayAction(ActionId.Search).getCode(),ActionId.Search);
			actionIdHash.put(DayAction.getDayAction(ActionId.Trade).getCode(),ActionId.Trade);
			actionIdHash.put(DayAction.getDayAction(ActionId.Rest).getCode(),ActionId.Rest);
			actionIdHash.put(DayAction.getDayAction(ActionId.Alert).getCode(),ActionId.Alert);
			actionIdHash.put(DayAction.getDayAction(ActionId.Hire).getCode(),ActionId.Hire);
			actionIdHash.put(DayAction.getDayAction(ActionId.Spell).getCode(),ActionId.Spell);
			actionIdHash.put(DayAction.getDayAction(ActionId.SpellPrep).getCode(),ActionId.SpellPrep);
			actionIdHash.put(DayAction.getDayAction(ActionId.Cache).getCode(),ActionId.Cache);

			// Special actions
			actionIdHash.put(DayAction.getDayAction(ActionId.Heal).getCode(),ActionId.Heal);
			actionIdHash.put(DayAction.getDayAction(ActionId.Repair).getCode(),ActionId.Repair);
			actionIdHash.put(DayAction.getDayAction(ActionId.Fortify).getCode(),ActionId.Fortify);
			
			// These are all "StartsWith" things
			actionIdHash.put(DayAction.getDayAction(ActionId.Move).getCode(),ActionId.Move);
			actionIdHash.put(DayAction.getDayAction(ActionId.Move).getCode()+"!",ActionId.Move);
			actionIdHash.put(DayAction.getDayAction(ActionId.EnhPeer).getCode(),ActionId.EnhPeer);
			actionIdHash.put(DayAction.getDayAction(ActionId.Fly).getCode(),ActionId.Fly);
			actionIdHash.put(DayAction.getDayAction(ActionId.Follow).getCode(),ActionId.Follow);
			actionIdHash.put(DayAction.getDayAction(ActionId.RemSpell).getCode(),ActionId.RemSpell);
		}
		if (action!=null) {
			ActionId t1 = actionIdHash.get(action);
			if (t1==null) { // try 3 chars
				t1 = actionIdHash.get(action.substring(0,3));
				if (t1==null) { // try 2 chars
					t1 = actionIdHash.get(action.substring(0,2));
					if (t1==null) { // try 1
						t1 = actionIdHash.get(action.substring(0,1));
					}
				}
			}
			if (t1!=null) {
				return t1;
			}
		}
		return ActionId.NoAction;
	}
	public static ImageIcon getIconForAction(String action) {
		ActionId id = getIdForAction(action);
		DayAction da = DayAction.getDayAction(id);
		if (da!=null) {
			return ImageCache.getIcon("actions/"+da.getIconName());
		}
		return null;
	}
	
	/**
	 * @return			Collection of SpellWrapper objects representing living (cast) spells
	 */
	public ArrayList<SpellWrapper> getAliveSpells() {
		ArrayList<SpellWrapper> list = new ArrayList<>();
		ArrayList<GameObject> spells = getAllSpells();
		for (GameObject go:spells) {
			SpellWrapper spell = new SpellWrapper(go);
			if (spell.isAlive()) {
				list.add(spell);
			}
		}
		
		for (GameObject vs:getAllVirtualSpellsFor(spells)) {
			list.add(new SpellWrapper(vs));
		}
		
		// check inventory (might be a spell being cast from artifact/book)
		for (GameObject treasure:getActivatedTreasureObjects()) {
			Collection<GameObject> awakenedSpells = SpellUtility.getSpells(treasure,Boolean.TRUE,false,true);
			if (awakenedSpells.size()>0) {
				for (GameObject go : awakenedSpells) {
					SpellWrapper spell = new SpellWrapper(go);
					if (spell.isAlive()) {
						list.add(spell);
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * Adds a Meeting roll to the character (expires at midnight).  Determines if native group is battling or not,
	 * though the roll is added regardless.
	 * 
	 * @param nativeGroupName	The lowercase native group name
	 * @param dieRollResult		The String version of the DieRoller that was rolled
	 * @param result			The title for the result of the roll
	 */
	public void addBattlingNativeRoll(String nativeGroupName,String dieRollResult,String result) {
		getGameObject().addAttributeListItem(BATTLING_NATIVE_BLOCK,nativeGroupName,dieRollResult+"**"+result);
	}
	public void addBattlingNative(GameObject nativeMember) {
		RealmComponent rc = RealmComponent.getRealmComponent(nativeMember);
		if (rc.getOwner()!=null) return; // hirelings can't ever be battling natives!!
		String nativeGroupName = nativeMember.getThisAttribute("native").toLowerCase();
		getGameObject().addAttributeListItem(BATTLING_NATIVE_BLOCK,nativeGroupName,"");
		String clanId = nativeMember.getThisAttribute("clan");
		if (clanId!=null) {
			boolean isFoe = false;
			for (GameObject inv : getInventory()) {
				if (!isFoe && inv.hasThisAttribute(RealmComponent.GOLD_SPECIAL)) {
					ArrayList<String> foes = ((GoldSpecialChitComponent)RealmComponent.getRealmComponent(inv)).getFoes();
					for (String foe : foes) {
						if (nativeGroupName.toLowerCase().matches(foe.toLowerCase())) {
							isFoe = true;
							break;
						}
					}
				}
			}
			
			if (!isFoe) {
				GamePool pool = new GamePool(getGameData().getGameObjects());
				ArrayList<GameObject> hqs = pool.extract("native,rank=HQ,clan="+clanId);
				for (GameObject hq : hqs) {
					String nativeClanName = hq.getThisAttribute("native").toLowerCase();
					getGameObject().addAttributeListItem(BATTLING_NATIVE_BLOCK,nativeClanName,"");
				}
			}
		}
	}
	public List<String> getBattlingNativeGroups() {
		ArrayList<String> list = new ArrayList<>();
		Hashtable<String, Object> hash = getGameObject().getAttributeBlock(BATTLING_NATIVE_BLOCK);
		if (hash!=null) {
			list.addAll(hash.keySet());
			Collections.sort(list);
		}
		return list;
	}
	public boolean isBattling(GameObject nativeMember) {
		return isBattling(nativeMember.getThisAttribute("native"));
	}
	public boolean isBattling(String groupName) {
		return getGameObject().hasAttribute(BATTLING_NATIVE_BLOCK,groupName.toLowerCase());
	}
	/**
	 * @return		All hirelings in the game object hold
	 */
	public ArrayList<RealmComponent> getFollowingHirelings() {
		ArrayList<RealmComponent> list = new ArrayList<>();
		for (GameObject go : getGameObject().getHold()) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isNative() || rc.isMonster() || rc.isTraveler()) {
				list.add(rc);
			}
		}
		return list;
	}
	public CharacterWrapper getEmployer() {
		RealmComponent characterRc = RealmComponent.getRealmComponent(getGameObject());
		if (characterRc.getOwnerId()!=null) {
			return new CharacterWrapper(characterRc.getOwner().getGameObject());
		}
		return null;
	}
	/**
	 * Does the code necessary to make the hireling hired.  Note that if the hireling is already hired by this character,
	 * it will simply have an additional 14 days added to its term.
	 */
	public void addHireling(GameObject hireling) {
		addHireling(hireling,Constants.TERM_OF_HIRE);
	}
	public void addHireling(GameObject hireling,int termOfHire) {
		// Better make sure this "character" isn't already a hired leader, in which case we want to hire via employer
		RealmComponent characterRc = RealmComponent.getRealmComponent(getGameObject());
		if (!characterRc.isCharacter()) {
			CharacterWrapper employer = new CharacterWrapper(characterRc.getOwner().getGameObject());
			employer.addHireling(hireling,termOfHire);
		}
		else {
			RealmComponent rc = RealmComponent.getRealmComponent(hireling);
			
			// Set the owner
			rc.setOwner(characterRc);
			
			// Add hire term
			rc.addTermOfHire(termOfHire);
			
			// Check to see if hireling is a leader, and if so, make him active
			if (rc.isNativeLeader()) {
				CharacterWrapper leader = new CharacterWrapper(hireling);
				leader.setJustUnhired(false);
				leader.setPlayerName(getPlayerName());
				leader.setPlayerPassword(getPlayerPassword());
				leader.setPlayerEmail(getPlayerEmail());
				leader.setWantsCombat(getWantsCombat()); // same default
			}
			
			// Finally, make sure the hireling ID is present in the hireling block
			String id = "H"+hireling.getStringId();
			if (!getGameObject().hasAttribute(HIRELING_BLOCK,id)) {
				getGameObject().setAttribute(HIRELING_BLOCK,id);
			}
		}
	}
	/**
	 * Unhires the hireling, by removing its owner, and dropping it in the clearing (assuming it is following the character)
	 */
	public void removeHireling(GameObject hireling) {
		RealmComponent characterRc = RealmComponent.getRealmComponent(getGameObject());
		RealmComponent rc = RealmComponent.getRealmComponent(hireling);
		
		if (!characterRc.equals(rc.getOwner())) return; // not this character's hireling?  return!
		
		hireling.removeThisAttribute(Constants.LOST_IN_THE_MAZE);
		rc.clearOwner(); // this also clears the term of hire
		rc.clearTargets(); // make sure they aren't targeting anyone
		rc.setHidden(false); // make sure they aren't hidden
		
		GameObject heldByGo = hireling.getHeldBy();
		if (heldByGo!=null) { // heldByGo might be null in the BattleBuilder
			RealmComponent heldBy = RealmComponent.getRealmComponent(heldByGo);
			if (heldBy!=null) { // held might be null in the BattleBuilder
				if (heldBy.isPlayerControlledLeader()) {
					// If hireling is currently in a character inventory, then "drop" him in the current clearing.
					CharacterWrapper holdingCharacter = new CharacterWrapper(heldByGo);
					TileLocation tl = holdingCharacter.getCurrentLocation();
	//				if (tl.isFlying()) { // in case hire term ends while character is flying
	//					holdingCharacter.land(null);
	//					tl = holdingCharacter.getCurrentLocation();
	//				}
					if (tl!=null && tl.hasClearing()) { // might not have a location if using the BattleBuilder
						tl.clearing.add(hireling,null);
					}
				}
			}
		}
		
		// Remove ID from hireling block
		String id = "H"+hireling.getStringId();
		getGameObject().removeAttribute(HIRELING_BLOCK,id);
		setNeedsInventoryCheck(true); // without the hireling, you might have to adjust your inventory!
		
		// Hired Leaders and Controlled Monsters have a bit more work to do
		if (hasPlayerBlock(rc.getGameObject())) {
			CharacterWrapper leader = new CharacterWrapper(hireling);
			TileLocation current = rc.getCurrentLocation();
			
			if (current != null && current.isFlying()) {
				leader.land(null);
				current = rc.getCurrentLocation();
			}
			
			// "Drop" all following hirelings
			for (RealmComponent follow:leader.getFollowingHirelings()) {
				ClearingUtility.moveToLocation(follow.getGameObject(),current);
			}
			
			ArrayList<GameObject> booty = new ArrayList<>(leader.getInventory());
			if (rc.isNativeLeader()) {
				// All inventory is added to the native's box
				GameObject holder = SetupCardUtility.getDenizenHolder(hireling);
				StringBufferedList list = new StringBufferedList();
				for (GameObject item:booty) {
					RealmComponent itemRc = RealmComponent.getRealmComponent(item);
					if (!itemRc.isNativeHorse()) { // everything except the native's own horse!! of course!!
						// leaders cannot activate items, so there is no need to deactivate here
						holder.add(item);
						list.append(item.getName());
					}
				}
				appendNote(hireling,"Trade","Stolen: "+list.toString());
			}
			else if (rc.isMonster() || rc.isNative()) {
				// All inventory is dropped in the clearing
				for (GameObject item:booty) {
					RealmComponent itemRc = RealmComponent.getRealmComponent(item);
					if (!itemRc.isNativeHorse() && !itemRc.isMonsterPart()) { // everything except the monster's weapon (if any)
						ClearingUtility.moveToLocation(item,current);
					}
				}
			}
			
			// Tag as "just unhired" so the frame can be removed from play, and player attributes can be stripped
			leader.setJustUnhired(true);
		}
		
		if (hireling.hasThisAttribute(Constants.ABSORBED_CHITS)) {
			Collection<String> chitIds = hireling.getThisAttributeList(Constants.ABSORBED_CHITS);
			for (String chitId : chitIds) {
				GameObject chit = hireling.getGameData().getGameObject(chitId);
				this.getGameObject().add(chit);
			}
			updateChitEffects();
			hireling.removeThisAttribute(Constants.ABSORBED_CHITS);
			ClearingUtility.moveToLocation(hireling,null);
		}
		
		hireling.removeThisAttribute(Constants.HIRELING);
		// Finally, make sure any clones are expunged (stupid clones!!  kill them all!)
		if (hireling.hasThisAttribute(Constants.CLONED)) {
			hireling.clearHold();
			if (hireling.getHeldBy()!=null) {
				hireling.getHeldBy().remove(hireling);
			}
			RealmUtility.makeDead(RealmComponent.getRealmComponent(hireling));
			hireling.removeThisAttribute(RealmComponent.REALMCOMPONENT_BLOCK);
			hireling.setThisAttribute(Constants.OUT_OF_GAME);
			hireling.setName("Hireling (cloned, removed from play)");
			hireling.removeThisAttribute("setup_start");
			hireling.removeThisAttribute("denizen");
			hireling.removeThisAttribute("monster");
			hireling.removeThisAttribute("native");
			hireling.removeThisAttribute("garrison");
			hireling.removeThisAttribute("clearing");
			// I'd like to simply delete the object...  but there are problems with this. Instead, lets erase the clone's memory so it has no effect!
			//hireling.clearAllAttributes();
			//getGameData().removeObject(hireling);
		}
	}
	/**
	 * @return		All hirelings hired by this character (whether or not they are following)
	 */
	public ArrayList<RealmComponent> getAllHirelings() {
		GameData data = getGameObject().getGameData();
		ArrayList<RealmComponent> list = new ArrayList<>();
		OrderedHashtable<String, Object> hash = getGameObject().getAttributeBlock(HIRELING_BLOCK);
		for (String id : hash.keySet()) {
			list.add(RealmComponent.getRealmComponentFromId(data,id.substring(1)));
		}
		return list;
	}
	public ArrayList<RealmComponent> getAllHirelingsFromSame(RealmComponent rc) {
		ArrayList<RealmComponent> list = new ArrayList<>();
		if (rc.isNative()) {
			String groupName = RealmUtility.getGroupName(rc);
			for (RealmComponent test:getAllHirelings()) {
				if (RealmUtility.getGroupName(test).equals(groupName)) {
					list.add(test);
				}
			}
		}
		return list;
	}
	public ArrayList<RealmComponent> getAllControlledMonstersWithSameName(String monsterType) {
		ArrayList<RealmComponent> list = new ArrayList<>();
		for (RealmComponent hireling:getAllHirelings()) {
			if (hireling.isMonster() && hireling.getGameObject().getName().matches(monsterType)) {
				list.add(hireling);
			}
		}
		return list;
	}
	public void setWantsCombat(boolean val) {
		setBoolean(WANTS_COMBAT,val);
	}
	/**
	 * @return		true if the character wants the combat screen to come up
	 */
	public boolean getWantsCombat() {
		return getBoolean(WANTS_COMBAT);
	}
	public void setWantsDayEndTrades(boolean val) {
		setBoolean(WANTS_DAYEND_TRADING,val);
	}
	/**
	 * @return		true if the character wants to do day end trading/rearrangement
	 */
	public boolean getWantsDayEndTrades() {
		return getBoolean(WANTS_DAYEND_TRADING);
	}
	public void setDayEndTradingActive(boolean val) {
		setBoolean(IS_DAYEND_TRADING,val);
	}
	public boolean isDayEndTradingActive() {
		return getBoolean(IS_DAYEND_TRADING);
	}
	/**
	 * @return		true if MOVE actions have been recorded for today
	 */
	public boolean hasMoveActionsToday() {
		Collection<String> actions = getCurrentActions(true);
		for (String action : actions) {
			if (action.startsWith("M-")) {
				return true;
			}
		}
		return false;
	}
	public void addMoveHistory(TileLocation tl) {
		if (tl!=null) { // might be null in combat simulator
			addListItem(MOVE_HISTORY,tl.asKey());
			addListItem(MOVE_HISTORY_DAY_KEY,getCurrentDayKey());
		}
	}
	public void jumpMoveHistory() {
		addListItem(MOVE_HISTORY,MOVE_HISTORY_JUMP);
		addListItem(MOVE_HISTORY_DAY_KEY,getCurrentDayKey());
	}
	public void bumpDayMoveHistory() {
		addListItem(MOVE_HISTORY,MOVE_HISTORY_DAY);
		addListItem(MOVE_HISTORY_DAY_KEY,getCurrentDayKey());
	}
	public void clearMoveHistory() {
		removeAttribute(MOVE_HISTORY);
		removeAttribute(MOVE_HISTORY_DAY_KEY);
	}
	public boolean hasMoveHistory() {
		Collection<String> c = getList(MOVE_HISTORY);
		return c!=null && !c.isEmpty();
	}
	public ArrayList<String> getMoveHistory() {
		return getList(MOVE_HISTORY);
	}
	public ArrayList<String> getMoveHistoryDayKeys() {
		return getList(MOVE_HISTORY_DAY_KEY);
	}
	public void addSpellExtraAction(String action,GameObject spellObject) {
		addListItem(SPELL_EXTRA_ACTIONS,action);
		addListItem(SPELL_EXTRA_ACTION_SOURCE,spellObject.getStringId());
	}
	public void removeSpellExtraAction(String action) {
		ArrayList<String> list = getList(SPELL_EXTRA_ACTIONS);
		if (list!=null) {
			list = new ArrayList<>(list);
			int n = list.indexOf(action);
			if (n>=0) {
				list.remove(n);
				setList(SPELL_EXTRA_ACTIONS,list);
				
				list = getList(SPELL_EXTRA_ACTION_SOURCE);
				list.remove(n);
				setList(SPELL_EXTRA_ACTION_SOURCE,list);
			}
		}
	}
	public ArrayList<String> getSpellExtras() {
		ArrayList<String> list = getList(SPELL_EXTRA_ACTIONS);
		if (list!=null && !list.isEmpty()) {
			return new ArrayList<>(list);
		}
		return null;
	}
	public ArrayList<GameObject> getSpellExtraSources() {
		ArrayList<String> list = getList(SPELL_EXTRA_ACTION_SOURCE);
		if (list!=null && !list.isEmpty()) {
			GameData data = getGameObject().getGameData();
			ArrayList<GameObject> ret = new ArrayList<>();
			for (String id : list) {
				GameObject go = data.getGameObject(Long.valueOf(id));
				ret.add(go);
			}
			return ret;
		}
		return null;
	}
	public void setTransmorph(GameObject in) {
		if (in!=null) {
			setString(TRANSMORPH_ID,in.getStringId());
		}
		else {
			setBoolean(TRANSMORPH_ID,false);
		}
	}
	public GameObject getTransmorph() {
		String id = getString(TRANSMORPH_ID);
		if (id!=null) {
			GameObject ret = getGameObject().getGameData().getGameObject(Long.valueOf(id));
			return ret;
		}
		return null;
	}
	public ArrayList<GameObject> inactivateAllBelongings() {
		ArrayList<GameObject> list = new ArrayList<>();
		for (GameObject item : getActiveInventory()) {
			if (TreasureUtility.doDeactivate(null,this,item)) {
				list.add(item);
			}
		}
		return list;
	}
	public boolean isTransmorphed() {
		return getBoolean(TRANSMORPH_ID);
	}
	public boolean isTransformedBeast() {
		return isTransmorphed() && getTransmorph() != null && getTransmorph().hasThisAttribute(Constants.BEAST);
	}
	public boolean isTransformed() {
		return isTransmorphed() && getTransmorph() != null && getTransmorph().hasThisAttribute("animal");
	}
	public boolean isStatue() {
		return isTransmorphed() && getTransmorph() != null && getTransmorph().hasThisAttribute("statue");
	}
	public boolean canChangeTactics() {
		GameObject transmorph = getTransmorph();
		if (transmorph!=null) {
			MonsterChitComponent monster = (MonsterChitComponent)RealmComponent.getRealmComponent(transmorph);
			
			// Might need to choose a side
			return (!monster.canPinOpponent() || monster.isPinningOpponent());
		}
		return false;
	}
	/**
	 * This special ability to do actions during the day (instead of recording them during Birdsong) can come to a
	 * characters in two ways.  Through the Timeless Jewel treasure, or the spell Prophecy.  Both have the attribute
	 * "daytime_actions".  The spell applies this attribute to the character while it is active.
	 */
	public boolean canDoDaytimeRecord() {
		return affectedByKey(Constants.DAYTIME_ACTIONS);
	}
	public boolean canCancelAction() {
		return affectedByKey(Constants.CANCEL_RECORDED_ACTION);
	}
	public boolean hasMagicProtection() {
		return affectedByKey(Constants.MAGIC_PROTECTION) || affectedByKey(Constants.DISENCHANT_POTION);
	}
	public boolean hasHealing() {
		return hasActiveInventoryThisKey(Constants.WOUNDS_TO_FATIGUE);
	}
	public String getPronoun() {
		return getGameObject().getThisAttribute("pronoun"); // he or she
	}
	public String getPronounOwnership() {
		String pronoun = getPronoun();
		return "he".equals(pronoun)?"his":"her";
	}
	
	/**
	 * This method checks to make sure the ridden horse (if any) or boots are strong enough to carry ALL the inventory.  If not,
	 * the horse/boots is deactivated or the new item is dropped.
	 * 
	 * @param frame			The parent frame
	 * @param newItem			The item that may be dropped.  Can be null.
	 * @param listener			The ChangeListener (needed for proper updates to the server!)
	 */
	public void checkInventoryStatus(JFrame frame,GameObject newItem,ChangeListener listener) {
		if (newItem!=null && newItem.hasThisAttribute("color_source")) {
			newItem.setThisAttribute(Constants.ACTIVATED);
		}
		if (isCharacter()) {
			Strength weight = getNeededSupportWeight();
			SteedChitComponent horse = (SteedChitComponent)getActiveSteed();
			if (horse!=null) {
				if (horse.getTrotStrength().strongerOrEqualTo(weight)) {
					horse = null;
				}
			}
			RealmComponent boots = getActiveBoots();
			if (boots!=null) {
				Strength bootStrength = RealmUtility.getBootsStrength(boots.getGameObject());
				if (bootStrength.strongerOrEqualTo(weight)) {
					boots = null;
				}
			}
			
			if (horse!=null || boots!=null) {
				StringBuffer who = new StringBuffer();
				StringBuffer sb = new StringBuffer("Deactivate the ");
				if (horse!=null) {
					sb.append(horse.getGameObject().getName());
					who.append(horse.getGameObject().getName());
				}
				if (boots!=null) {
					if (horse!=null) {
						sb.append(" and the ");
						who.append(" and ");
					}
					sb.append(boots.getGameObject().getName());
					who.append(boots.getGameObject().getName());
				}
				ButtonOptionDialog dialog = null;
				HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(getGameObject().getGameData());
				if (newItem!=null) {
					RealmComponent rc = RealmComponent.getRealmComponent(newItem);
					dialog = new ButtonOptionDialog(frame,rc.getIcon(),"The "+newItem.getName()+" is too heavy for your "+who.toString()+".","Heavy Inventory",false);
					if (hostPrefs.hasPref(Constants.ADV_DROPPING)) {
						dialog.addSelectionObject("Drop the "+newItem.getName());
					}
					dialog.addSelectionObject("Abandon the "+newItem.getName());
				}
				else {
					RealmComponent rc = RealmComponent.getRealmComponent(getGameObject());
					dialog = new ButtonOptionDialog(frame,rc.getIcon(),"The "+getGameObject().getName()+"'s current inventory is too heavy for "+getPronounOwnership()+" "+who.toString()+".","Heavy Inventory",false);
				}
				dialog.addSelectionObject(sb.toString());
				dialog.pack();
				dialog.setLocationRelativeTo(frame);
				dialog.setVisible(true);
				
				String sel = (String)dialog.getSelectedObject();
				if (sel.startsWith("Drop")) {
					TreasureUtility.doDrop(this,newItem,listener,true);
				}
				else if (sel.startsWith("Abandon")) {
					TreasureUtility.doDrop(this,newItem,listener,false);
				}
				else {
					if (horse!=null) {
						TreasureUtility.doDeactivate(frame,this,horse.getGameObject());
					}
					if (boots!=null) {
						TreasureUtility.doDeactivate(frame,this,boots.getGameObject());
					}
				}
			}
		}
	}
	/**
	 * This method is here primarily for the Fog spell
	 */
	public boolean canPeer() {
		if (hasCharacterTileAttribute(Constants.SP_NO_PEER)) return false;
		TileLocation loc = getCurrentLocation();
		if (loc!=null && loc.clearing!=null) {
			for (RealmComponent rc : loc.clearing.getDeepClearingComponents()) {
				if (rc.getGameObject().hasThisAttribute(Constants.MIST_CRYSTAL)) {
					return false;
				}
			}
		}
		return true;
	}
	public boolean moveRandomly() {
		return (hasCharacterTileAttribute(Constants.SP_MOVE_IS_RANDOM) || getGameObject().hasThisAttribute((Constants.LOST_IN_THE_MAZE))) && !affectedByKey(Constants.REALM_MAP);
	}
	public boolean hasCharacterTileAttribute(String attribute) {
		if (getGameObject().hasThisAttribute(attribute)) {
			return true;
		}
		// Check the tile
		TileLocation tl = getCurrentLocation();
		if (tl!=null && tl.tile.getGameObject().hasThisAttribute(attribute)) {
			return true;
		}
		return false;
	}
	public boolean isPlayingTurn() {
		return getGameStatus().endsWith("Playing Turn");
	}
	public void setPonyLock(boolean val) {
		setBoolean(Constants.PONY_LOCK,val);
	}
	public boolean isPonyLock() {
		return getBoolean(Constants.PONY_LOCK);
	}
	private static final String NOTE_BLOCK = "_ntbk_";
	private static final String SOURCE_TAG = "source";
	private static final String EVENT_TAG = "event";
	private static final String DATE_TAG = "date";
	private static final String NOTE_TAG = "note";
	public void addNote(RealmComponent rc,String event,String note) {
		addNote(rc.getGameObject(),event,note);
	}
	public void appendNote(GameObject go,String event,String note) {
		if (!isCharacter()) {
			// Forward notes on to hiring character
			getHiringCharacter().appendNote(go,event,note);
			return;
		}
		String oldNote = deleteNote(go.getStringId(),event);
		if (oldNote!=null) {
			note = oldNote + ".  "+note;
		}
		addNote(go,event,note);
	}
	public void addNoteTrade(GameObject trader,Collection<GameObject> hold) {
		// Update the character notebook accordingly
		StringBufferedList list = new StringBufferedList();
		for (GameObject go : hold) {
			RealmComponent rc = RealmComponent.getRealmComponent(go);
			if (rc.isTreasure() || rc.isSpell()) {
				list.append(go.getName());
			}
		}
		addNote(trader,"Trade",list.toString());
	}
	public Note getNoteTrade(GameObject trader) {
		return getNote(trader.getStringId(),"Trade");
	}
	public void addNote(GameObject go,String event,ArrayList<String> list) {
		StringBufferedList sb = new StringBufferedList();
		for(String val:list) {
			sb.append(val);
		}
		addNote(go,event,sb.toString());
	}
	public void addNote(GameObject go,String event,String note) {
		addNote(go.getStringId(),event,note,true);
	}
	public void addNote(String playerName,String event,String note) {
		addNote("PLAYER"+playerName,event,note,false);
	}
	public void addNote(String source,String event,String note,boolean deleteOld) {
		if (source==null || event==null || note==null) return;
		if (!isCharacter()) {
			// Forward notes on to hiring character
			getHiringCharacter().addNote(source,event,note,deleteOld);
			return;
		}
		if (deleteOld) {
			deleteNote(source,event);
		}
		GameObject go = getGameObject();
		GameWrapper game = GameWrapper.findGame(go.getGameData());
		String date = game.getMonth()+":"+game.getDay();
		int current = go.getInt(NOTE_BLOCK,NOTE_BLOCK);
		String num = String.valueOf(current);
		go.setAttribute(NOTE_BLOCK,SOURCE_TAG+num,source);
		go.setAttribute(NOTE_BLOCK,EVENT_TAG+num,event);
		go.setAttribute(NOTE_BLOCK,DATE_TAG+num,date);
		go.setAttribute(NOTE_BLOCK,NOTE_TAG+num,note);
		
		// Increment note number
		current++;
		go.setAttribute(NOTE_BLOCK,NOTE_BLOCK,current);
	}
	public String deleteNote(int id) {
		GameObject go = getGameObject();
		String oldNote = go.getAttribute(NOTE_BLOCK,NOTE_TAG+id);
		go.removeAttribute(NOTE_BLOCK,SOURCE_TAG+id);
		go.removeAttribute(NOTE_BLOCK,EVENT_TAG+id);
		go.removeAttribute(NOTE_BLOCK,DATE_TAG+id);
		go.removeAttribute(NOTE_BLOCK,NOTE_TAG+id);
		return oldNote;
	}
	private String deleteNote(String tSource,String tEvent) {
		int index = getNoteIndex(tSource,tEvent);
		if (index>=0) {
			return deleteNote(index);
		}
		return null;
	}
	public Note getNote(String tSource,String tEvent) {
		int index = getNoteIndex(tSource,tEvent);
		if (index>=0) {
			return getNote(index);
		}
		return null;
	}
	private int getNoteIndex(String tSource,String tEvent) {
		GameObject go = getGameObject();
		int current = go.getInt(NOTE_BLOCK,NOTE_BLOCK);
		for (int i=0;i<current;i++) {
			String source = go.getAttribute(NOTE_BLOCK,SOURCE_TAG+i);
			if (source!=null && source.equals(tSource)) {
				String event = go.getAttribute(NOTE_BLOCK,EVENT_TAG+i);
				if (event!=null && event.equals(tEvent)) {
					return i;
				}
			}
		}
		return -1;
	}
	private Note getNote(int index) {
		GameObject go = getGameObject();
		Note note = null;
		String source = go.getAttribute(NOTE_BLOCK,SOURCE_TAG+index);
		if (source!=null) {
			String event = go.getAttribute(NOTE_BLOCK,EVENT_TAG+index);
			String date = go.getAttribute(NOTE_BLOCK,DATE_TAG+index);
			String noteText = go.getAttribute(NOTE_BLOCK,NOTE_TAG+index);
			note = new Note(index,go.getGameData(),source,event,date,noteText);
		}
		return note;
	}
	public ArrayList<Note> getNotes() {
		ArrayList<Note> list = new ArrayList<>();
		GameObject go = getGameObject();
		int current = go.getInt(NOTE_BLOCK,NOTE_BLOCK);
		for (int i=0;i<current;i++) {
			Note note = getNote(i);
			if (note!=null) {
				list.add(note);
			}
		}
		return list;
	}
	public void clearNotes() {
		getGameObject().removeAttributeBlock(NOTE_BLOCK);
	}
	public boolean hasFamiliar() {
		return getGameObject().hasThisAttribute(Constants.USE_FAMILIAR);
	}
	public void setActiveFamiliar(boolean val) {
		setBoolean(Constants.ACTIVE_FAMILIAR,val);
	}
	public boolean hasActiveFamiliar() {
		return getBoolean(Constants.ACTIVE_FAMILIAR);
	}
	public boolean canPlay() {
		if (isFamiliar()) {
			return getHiringCharacter().hasActiveFamiliar();
		}
		return true;
	}
	public void setHighestEarnedVps(int val) {
		setInt(HIGHEST_EARNED_VPS,val);
	}
	public int getHighestEarnedVps() {
		return getInt(HIGHEST_EARNED_VPS);
	}
	public int getStartingGold() {
		int startingGold = 10; // default start with 10g
		if (getGameObject().hasThisAttribute(Constants.STARTING_GOLD)) {
			startingGold = getGameObject().getThisInt(Constants.STARTING_GOLD);
		}
        boolean developmentPastFour = HostPrefWrapper.findHostPrefs(getGameObject().getGameData()).hasPref("DevelopmentPastFour");
        if(developmentPastFour && getCharacterLevel() > 4) {
            startingGold += 15;
        }
		return startingGold;
	}
	public void setDeathReason(String val) {
		setString(DEATH_REASON,val);
	}
	public String getDeathReason() {
		return getString(DEATH_REASON);
	}
	public void updatePathKnowledge(PathDetail path) {
		if(isSpiritGuided() || isMistLike())return;
		addPathKnowledge(path);
	}
	public void addPathKnowledge(PathDetail path) {	
		String pathName = path.getFullPathKey();
		if (path.isSecret() && !hasSecretPassageDiscovery(pathName)) {
			addSecretPassageDiscovery(path.getFullPathKey());
		}
		else if (path.isHidden() && !hasHiddenPathDiscovery(pathName)) {
			addHiddenPathDiscovery(path.getFullPathKey());
		}
	}
    public int getStartingStage() {
        return getInt(STARTING_CHARACTER_STAGE);
    }

    public void setStartingStage(int val) {
        setInt(STARTING_CHARACTER_STAGE, val);
    }
    
    public void setCurrentGuild(String guild) {
    	setString(CURRENT_GUILD,guild);
    }
    public String getCurrentGuild() {
    	return getString(CURRENT_GUILD);
    }
    public void setCurrentGuildLevel(int level) {
    	setInt(CURRENT_GUILD_LEVEL,level);
    }
    public int getCurrentGuildLevel() {
    	return getInt(CURRENT_GUILD_LEVEL);
    }
    public void clearGuild() {
    	clear(CURRENT_GUILD);
    	clear(CURRENT_GUILD_LEVEL);
    }
    public String getCurrentGuildBadgeName() {
    	String guild = getCurrentGuild();
    	if (guild!=null) {
    		return guild+getCurrentGuildLevel();
    	}
    	return null;
    }
    public String getCurrentGuildLevelName() {
    	String guild = getCurrentGuild();
    	if (guild!=null) {
    		String levelName = "";
    		int level = getCurrentGuildLevel();
    		switch(level) {
    			case 1:
    				levelName=GuildLevel.Apprentice.toString();
    				break;
    			case 2:
    				levelName=GuildLevel.Journeyman.toString();
    				break;
    			case 3:
    				levelName=GuildLevel.Master.toString();
    				break;
    		}
    		return StringUtilities.capitalize(guild)+" Guild "+levelName;
    	}
    	return null;
    }
    public boolean isGuildMember(RealmComponent rc) {
    	return rc.isGuild() && rc.getGameObject().getThisAttribute("guild").equals(getCurrentGuild());
    }
    public ArrayList<GameObject> getInventoryToApprove() {
    	ArrayList<GameObject> list = new ArrayList<>();
		for (GameObject go : getGameObject().getHold()) {
			if (go.hasThisAttribute(Constants.REQUIRES_APPROVAL)) {
				list.add(go);
			}
		}
		return list;
    }
    public boolean hasItemsToApprove() {
    	return !getInventoryToApprove().isEmpty();
    }
    public void setTreacheryPreference(boolean val) {
    	setBoolean(TREACHERY_PREFERENCE,val);
    }
    public boolean getTreacheryPreference() {
    	return getBoolean(TREACHERY_PREFERENCE);
    }
    public void setChatStyle(String styleName) {
    	setString(CHAT_STYLE,styleName);
    }
    public String getChatStyle() {
    	String val = getString(CHAT_STYLE);
    	return val==null?"black":val;
    }
    public void setFollowRests(int rests) {
    	setInt(FOLLOW_RESTS,rests);
    }
    public int getFollowRests() {
    	return getInt(FOLLOW_RESTS);
    }
    public void clearFollowRests() {
    	clear(FOLLOW_RESTS);
    }
    public int getRestBonus(int rests) {
		int advantage = 0;
		if (getGameObject().hasThisAttribute(Constants.REST_DOUBLE)) {
			// Character has special advantage
			advantage += 1;
		}
		// Check activated treasures for rest double
		for (GameObject item:getEnhancingItemsAndNomads()) {
			if (item.hasThisAttribute(Constants.REST_DOUBLE)) {
				advantage += 1;
				break;
			}
		}
		return rests * advantage;
    }
    public void addQuest(JFrame frame,Quest quest) {
    	if (getGameData().getDataId()!=quest.getGameData().getDataId()) {
    		// This is just here to protect me from myself, in case I forget to copy the quest game objects across over to the current game data file
    		throw new IllegalStateException("Quest GameObject is not from the same GameData as the character!");
    	}
    	
		// Initialize quest
		quest.initialize(frame,this);
		
		// Might be steps fulfilled right away, so check
		QuestRequirementParams questRequirementParams = new QuestRequirementParams();
		questRequirementParams.timeOfCall = getCurrentGamePhase();
		quest.testRequirements(frame,this,questRequirementParams);
    	
    	addListItem(QUEST_ID,quest.getGameObject().getStringId());
    }
    public void removeQuest(Quest quest) {
    	removeListItem(QUEST_ID,quest.getGameObject().getStringId());
    }
    public ArrayList<GameObject> getAllQuestObjects() {
    	ArrayList<GameObject> quests = new ArrayList<>();
    	ArrayList<String> list = getList(QUEST_ID);
    	if (list!=null) {
    		for(Object o:list) {
    			String id = (String)o;
    			GameObject go = getGameData().getGameObject(Long.valueOf(id));
    			quests.add(go);
    		}
    	}
    	return quests;
    }
    public ArrayList<Quest> getAllQuests() {
    	ArrayList<Quest> quests = new ArrayList<>();
    	for(GameObject go:getAllQuestObjects()) {
    		quests.add(new Quest(go));
    	}
    	return quests;
    }
    public ArrayList<Quest> getAllNonEventQuests() {
		ArrayList<Quest> quests = getAllQuests();
		ArrayList<Quest> personalQuests = new ArrayList<>();
		for (Quest quest : quests) {
			if (!quest.isEvent()) {
				personalQuests.add(quest);
			}
		}
		return personalQuests;
    }
    public int getQuestCount() {
    	ArrayList<String> list = getList(QUEST_ID);
    	return list==null?0:list.size();
    }
	public boolean testQuestRequirements(JFrame parentFrame) {
		QuestRequirementParams qr = new QuestRequirementParams();
		qr.timeOfCall = getCurrentGamePhase();
		return testQuestRequirements(parentFrame,qr,true);
	}
	public boolean testQuestRequirements(JFrame parentFrame,QuestRequirementParams reqParams) {
		return testQuestRequirements(parentFrame,reqParams,true);
	}
	private boolean testQuestRequirements(JFrame parentFrame,QuestRequirementParams reqParams,boolean processPost) {
		boolean reward = false;
		if (processPost && processPostQuestParams(parentFrame)) reward = true; // Process anything that might have been missed before testing new reqParams
		ArrayList<Integer> cardTypesWithReward = new ArrayList<>();
		for(Quest quest:getAllQuests()) {
			int uid = quest.getUniqueId();
			if (cardTypesWithReward.contains(uid)) continue;
			if (quest.testRequirements(parentFrame,this,reqParams)) {
				reward = true;
				cardTypesWithReward.add(quest.getUniqueId());
			}
		}
		return reward;
	}
	public int getCompletedQuestCount() {
		int count = 0;
		for(Quest quest:getAllQuests()) {
			QuestState state = quest.getState();
			if (state==QuestState.Complete) {
				count++;
			}
		}
		return count;
	}
	public int getUnfinishedNotAllPlayQuestCount() {
		int count = 0;
		for(Quest quest:getAllQuests()) {
			if (quest.isAllPlay()) continue;
			QuestState state = quest.getState();
			if (state!=QuestState.Complete && state!=QuestState.Failed) {
				count++;
			}
		}
		return count;
	}
	public ArrayList<QuestCardComponent> getUnfinishedNotAllPlayQuests() {
		ArrayList<QuestCardComponent> quests = new ArrayList<>();
		for(Quest quest:getAllQuests()) {
			if (quest.isAllPlay()) continue;
			QuestState state = quest.getState();
			if (state!=QuestState.Complete && state!=QuestState.Failed) {
				quests.add((QuestCardComponent) RealmComponent.getRealmComponent(quest.getGameObject()));
			}
		}
		return quests;
	}
	public int getUnfinishedNonEventQuestCount() {
		int count = 0;
		for(Quest quest:getAllQuests()) {
			if (quest.isEvent()) continue;
			QuestState state = quest.getState();
			if (state!=QuestState.Complete && state!=QuestState.Failed) {
				count++;
			}
		}
		return count;
	}
	public int getActiveQuestCount() {
		int count = 0;
		for(Quest quest:getAllQuests()) {
			QuestState state = quest.getState();
			if (state==QuestState.Active) {
				count++;
			}
		}
		return count;
	}
	public int getQuestSlotCount(HostPrefWrapper hostPrefs) {
		if (hostPrefs.getQuestCardsHandSize() == 0) {
			if (hostPrefs.hasPref(Constants.QST_SR_QUESTS)) {
				return (getCharacterLevel()+1)/2;
			}
			return getCharacterLevel() + 1;
		}
		return hostPrefs.getQuestCardsHandSize();
	}
	public void addPostQuestParams(QuestRequirementParams qp) {
		qp.dayKey = getCurrentDayKey();
		addListItem(POST_QUEST_PARAMS,qp.asString());
	}
	public boolean processPostQuestParams(JFrame frame) {
		if (!getBoolean(POST_QUEST_PARAMS)) return false;
		boolean reward = false;
		for(Object obj:getList(POST_QUEST_PARAMS)) {
			String val = obj.toString();
			QuestRequirementParams qp = QuestRequirementParams.valueOf(val,getGameData());
			if (testQuestRequirements(frame,qp,false)) {
				reward = true;
			}
		}
		clear(POST_QUEST_PARAMS);
		return reward;
	}
	public void distributeMonsterControlInCurrentClearing() {
		distributeMonsterControlInCurrentClearing(true);
	}
	public void distributeMonsterControlInCurrentClearing(boolean onlyEnhancedMonsterControl) {
		TileLocation current = this.getCurrentLocation();
		if (current != null && current.clearing != null) {
			ArrayList<RealmComponent> clearingComponents = current.clearing.getClearingComponents();
			for (RealmComponent monster : clearingComponents) {
				if (!monster.isMonster()) continue;
				ArrayList<RealmComponent> characterCanControl = new ArrayList<>();
				for (RealmComponent characterRc : clearingComponents) {
					if (!characterRc.isCharacter()) continue;
					Hashtable<String,Integer[]> controllableMonsters = characterRc.getControllableMonsters(onlyEnhancedMonsterControl);
					for (String monsterType : controllableMonsters.keySet()) {
						if (monster.getGameObject().getName().matches(monsterType.toString())) {
							if (!characterCanControl.contains(characterRc) && (controllableMonsters.get(monsterType)[1]==0 || (new CharacterWrapper(characterRc.getGameObject()).getAllControlledMonstersWithSameName(monsterType).size()<controllableMonsters.get(monsterType)[1]))) {
								characterCanControl.add(characterRc);
							}
						}
					}
				}
				if (characterCanControl.toArray().length == 1) { // only if exactly one character can control this monster
					CharacterWrapper characterWrapper = new CharacterWrapper(characterCanControl.get(0).getGameObject());
					int duration = characterCanControl.get(0).getControllableMonsterDuration(onlyEnhancedMonsterControl,monster.getGameObject().getName());
					RealmComponent monsterOwner = monster.getOwner();
					
					if(monsterOwner!=null && monsterOwner.isCharacter() && monsterOwner.getGameObject() == characterWrapper.getGameObject()) {
						if(monster.getTermOfHire()<duration) {
							monster.setTermOfHire(duration);
						}
					}
					else {
						if(monsterOwner!=null && monsterOwner.isCharacter()) {
							CharacterWrapper owner = new CharacterWrapper(monsterOwner.getGameObject());
							owner.removeHireling(monster.getGameObject());
						}
						characterWrapper.addHireling(monster.getGameObject(), duration);
					}
				}
			}
		}
	}
	public void applyPhaseChit(JFrame frame, GameObject phaseChit, SpellWrapper spell) {
		getGameObject().addThisAttributeListItem(PHASE_CHITS,phaseChit.getStringId());
		if (phaseChit.hasAttributeBlock(Constants.EFFECTS)) {
			GameWrapper game = GameWrapper.findGame(getGameObject().getGameData());
			SpellEffectContext context = new SpellEffectContext(frame, game, RealmComponent.getRealmComponent(getGameObject()), spell, getGameObject());
			for (String effect : phaseChit.getAttributeBlock(Constants.EFFECTS).keySet()) {
				ISpellEffect[] effects = PhaseChitEffectFactory.create(effect);
				for (ISpellEffect spellEffect : effects) {
					spellEffect.apply(context);
				}
			}
		}
	}
	public void endActivePhaseChits() {
		GameData gameData = getGameObject().getGameData();
		if (getGameObject().hasThisAttribute(PHASE_CHITS)) {
			for (String phaseChitId : getGameObject().getThisAttributeList(PHASE_CHITS)) {
				GameObject phaseChit = gameData.getGameObject(Long.valueOf(phaseChitId));
				if (phaseChit.hasAttributeBlock(Constants.EFFECTS)) {
					String spellId = phaseChit.getThisAttribute(Constants.SPELL_ID);
					GameObject spell = gameData.getGameObject(Long.valueOf(spellId));
					SpellWrapper spellWrapper = new SpellWrapper(spell);
					GameWrapper game = GameWrapper.findGame(getGameObject().getGameData());
					SpellEffectContext context = new SpellEffectContext(null, game, RealmComponent.getRealmComponent(getGameObject()), spellWrapper, getGameObject());
					for (String effect : phaseChit.getAttributeBlock(Constants.EFFECTS).keySet()) {
						ISpellEffect[] effects = PhaseChitEffectFactory.create(effect);
						for (ISpellEffect spellEffect : effects) {
							spellEffect.unapply(context);
						}
					}
				}
			}
			getGameObject().removeThisAttribute(PHASE_CHITS);
		}
	}

    public void setRunAwayLastUsedChit(String chit) {
    	setString(RUN_AWAY_LAST_USED_CHIT,chit);
    }
    public String getRunAwayLastUsedChit() {
    	return getString(RUN_AWAY_LAST_USED_CHIT);
    }
    public void removeRunAwayLastUsedChit() {
    	setString(RUN_AWAY_LAST_USED_CHIT,null);
    }
}