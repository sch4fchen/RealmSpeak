/* 
 * RealmSpeak is the Java application for playing the board game Magic Realm.
 * Copyright (c) 2005-2015 Robin Warren
 * E-mail: robin@dewkid.com
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 *
 * http://www.gnu.org/licenses/
 */
package com.robin.magic_realm.RealmGm;

import java.util.*;

import com.robin.game.objects.*;
import com.robin.game.server.GameHost;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.MRMap.*;
import com.robin.magic_realm.RealmCharacterBuilder.RealmCharacterBuilderModel;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TravelerChitComponent;
import com.robin.magic_realm.components.quest.*;
import com.robin.magic_realm.components.quest.requirement.QuestRequirement;
import com.robin.magic_realm.components.quest.requirement.QuestRequirement.RequirementType;
import com.robin.magic_realm.components.utility.*;
import com.robin.magic_realm.components.wrapper.*;

public class RealmSpeakInit {
	
	private JFrameWithStatus frame;
	
	private RealmLoader loader;
	private GameData data; // convenience
	
	private ArrayList<String> appendNames;
	private HostPrefWrapper hostPrefs;
	
	private int lastRating;
	private int mapAttempt;
	
	public RealmSpeakInit(JFrameWithStatus frame) {
		this.frame = frame;
	}
	public RealmSpeakInit() {
		this.frame = null;
	}
	public void loadData() {
		loader = new RealmLoader();
		data = loader.getData();
		data.setDataName(GameHost.DATA_NAME);
	}
	public GameData getGameData() {
		return loader.getData();
	}
	public GameData getMaster() {
		return loader.getMaster();
	}
	public void buildGame() {
		// Create all the necessary starting objects now, so we don't have issues with them later!
		GameWrapper.findGame(data);
		SpellMasterWrapper.getSpellMaster(data);
		SummaryEventWrapper sew = SummaryEventWrapper.getSummaryEventWrapper(data);
		sew.addSummaryEvent("RealmSpeak Version "+Constants.REALM_SPEAK_VERSION);
		
		hostPrefs = HostPrefWrapper.findHostPrefs(data);

		// Add any custom characters now
		RealmCharacterBuilderModel.addCustomCharacters(hostPrefs,data);
		
		// Construct quest "deck" if any
		if (hostPrefs.hasPref(Constants.QST_QUEST_CARDS) || hostPrefs.hasPref(Constants.QST_SR_QUESTS)) {
			prepQuestDeck();
		}
		else if (hostPrefs.hasPref(Constants.QST_BOOK_OF_QUESTS)) {
			prepBookOfQuests();
		}
		else if (hostPrefs.hasPref(Constants.QST_GUILD_QUESTS)) {
			prepGuildQuests();
		}
		
		// Handle all pre-setup initialization
		appendNames = new ArrayList<>();
		if (hostPrefs.getAlternativeTilesEnabled()) {
			enableAlternativeTilesInLoader(loader);
		}
		else if (hostPrefs.getMixExpansionTilesEnabled()) {
			enableEtilesInLoader(loader);
		}
		if (hostPrefs.getMultiBoardEnabled()) {
			prepMultiboard();
		}
		if (hostPrefs.getMixExpansionTilesEnabled()) {
			prepExpansionMix();
		}
		if (hostPrefs.getIncludeExpansionSpells()) {
			prepExpansionSpells("rw_expansion_1");
		}
		if(hostPrefs.getIncludeNewSpells()){
			prepExpansionSpells("new_spells_1");
		}
		if(hostPrefs.getIncludeNewSpells2()){
			prepExpansionSpells("new_spells_2");
		}
		if(hostPrefs.getIncludeSrSpells()){
			prepExpansionSpells("super_realm");
		}
		if(hostPrefs.getSwitchDaySpells()){
			prepExpansionSpells("upg_day_spells");
			removeSpells("upg_swap_out");
		}
		if (hostPrefs.hasPref(Constants.OPT_POWER_OF_THE_PIT_ATTACK)) {
			GamePool pool = new GamePool(data.getGameObjects());
			ArrayList<GameObject> popSpells = pool.find("powerofthepit");
			for (GameObject go:popSpells) {
				go.setThisAttribute("duration","attack");
				go.setThisAttribute("strength","");
			}
		}
		
		// Cleanup happens AFTER multiplications and mixes
		loader.cleanupData(hostPrefs.getGameKeyVals());
		
		// Set numbers for the monsters
		RealmUtility.prepMonsterNumbers(data);
		
		// Do the setup
		StringBuffer sb = new StringBuffer();
		data.doSetup(sb,hostPrefs.getGameSetupName(),GamePool.makeKeyVals(hostPrefs.getGameKeyVals()));
		//System.out.println(sb.toString()); // UNCOMMENT THIS LINE TO SEE SETUP DETAILS
		
		// Mark item starting locations
		markItemStartingLocations();
		
		// Remove any tiles that don't have chits
		cleanupTiles();
		
		// Get rid of unused generators (if playing expansion)
		removeUnusedGenerators();
		
		// Assign all travelers
		assignTravelerTemplates();
		
		// Build the map
		if (hostPrefs.getBoardAutoSetup()) {
			doBoardAutoSetup();
		}
		if (frame != null) {
			frame.resetStatus();
		}
		
		// Match up the gold specials
		RealmUtility.doMatchGoldSpecials(data);
		
		// Some items require a spell be cast (Flying Carpet)
		doItemSpellCasting();
		
		if (hostPrefs.hasPref(Constants.FE_DEADLY_REALM)) {
			SetupCardUtility.turnMonstersAndNativesDarkSideUp(data);
		}
	}
	private void cleanupTiles() {
		ArrayList<GameObject> tiles = RealmObjectMaster.getRealmObjectMaster(data).getTileObjects();
		ArrayList<GameObject> unused = new ArrayList<>();
		for(GameObject tile:tiles) {
			if (tile.getHoldCount()==0) {
				unused.add(tile);
			}
		}
		GameObject.stripListKeyVals("this",hostPrefs.getGameKeyVals(),unused);
		RealmObjectMaster.getRealmObjectMaster(data).resetTileObjects();
	}
	private void removeUnusedGenerators() {
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<GameObject> generators = pool.find(hostPrefs.getGameKeyVals()+",generator");
		for (GameObject generator:generators) {
			if (generator.getHoldCount()==0) {
				generator.stripKeyVals("this",hostPrefs.getGameKeyVals());
			}
		}
	}
	private void assignTravelerTemplates() {
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<GameObject> travelers = pool.find(hostPrefs.getGameKeyVals()+",traveler");
		for (GameObject go:travelers) {
			TravelerChitComponent traveler = (TravelerChitComponent)RealmComponent.getRealmComponent(go);
			traveler.assignTravelerTemplate();
		}
	}
	private void enableAlternativeTilesInLoader(RealmLoader rl) {
		GamePool tilePool = new GamePool(rl.getData().getGameObjects());
		ArrayList<GameObject> tiles = tilePool.find("tile");
		GameObject.stripListKeyVals("this",hostPrefs.getGameKeyVals(),tiles);
		ArrayList<GameObject> atiles = tilePool.find("a_tile");
		GameObject.setListKeyVals("this",hostPrefs.getGameKeyVals(),atiles);
		for (GameObject go:atiles)
		{
			go.addThisAttributeListItem("tile", "");
		}
	}
	private void enableEtilesInLoader(RealmLoader rl) {
		GamePool etilePool = new GamePool(rl.getData().getGameObjects());
		ArrayList<GameObject> etiles = etilePool.find("etile");
		GameObject.setListKeyVals("this",hostPrefs.getGameKeyVals(),etiles);
	}
	private void prepMultiboard() {
		RealmLoader doubleLoader = new RealmLoader();
		if (hostPrefs.getMixExpansionTilesEnabled()) {
			// Make sure the etile objects are available for multiboard duplication
			enableEtilesInLoader(doubleLoader);
		}
		
		RealmCharacterBuilderModel.addCustomCharacters(hostPrefs,doubleLoader.getData());
		
		doubleLoader.cleanupData(hostPrefs.getGameKeyVals());
		int count = hostPrefs.getMultiBoardCount();
		for (int n=0;n<count-1;n++) {
			String appendName = " "+Constants.MULTI_BOARD_APPENDS.substring(n,n+1);
			appendNames.add(appendName);
		}
		for (String appendName:appendNames) {
			long start = data.getMaxId()+1;
			doubleLoader.getData().renumberObjectsStartingWith(start);
			for (GameObject go : doubleLoader.getData().getGameObjects()) {
				if (!go.hasThisAttribute("season")) { // The one exception
					GameObject dub = data.createNewObject(go.getId());
					dub.copyFrom(go);
					dub.setThisAttribute(Constants.BOARD_NUMBER,appendName.trim());
					dub.setName(dub.getName()+appendName);
				}
			}
		}
		
		// Resolve objects (holds can't be calculated until all are loaded!)
		for (GameObject obj : data.getGameObjects()) {
			obj.resolveHold(data.getGameObjectIDHash());
		}
		
		// Expand the setup to accommodate the new tiles
		ArrayList<String> tiedPools = new ArrayList<>();
		tiedPools.add("SPELL_I");
		tiedPools.add("SPELL_II");
		tiedPools.add("SPELL_III");
		tiedPools.add("SPELL_IV");
		tiedPools.add("SPELL_V");
		tiedPools.add("SPELL_VI");
		tiedPools.add("SPELL_VII");
		tiedPools.add("SPELL_VIII");
		data.findSetup(hostPrefs.getGameSetupName()).expandSetup(appendNames,tiedPools,Constants.BOARD_NUMBER);
	}
	private void prepExpansionMix() {
		// Collect all regular tiles in groups and count them
		GamePool tilePool = new GamePool(RealmObjectMaster.getRealmObjectMaster(data).getTileObjects());

		// Remove the Borderland tile from the mixing: it is REQUIRED
		tilePool.extract("name=Borderland");
		
		appendNames.add(0,"");
		for (String appendName:appendNames) {
			String extraQuery = appendName.length()==0?(",!"+Constants.BOARD_NUMBER):(","+Constants.BOARD_NUMBER+"="+appendName.trim());
			ArrayList<GameObject> mountains = tilePool.find("tile_type=M"+extraQuery);
			int mCount = mountains.size();
			ArrayList<GameObject> caves = tilePool.find("tile_type=C"+extraQuery);
			int cCount = caves.size();
			ArrayList<GameObject> valleys = tilePool.find("tile_type=V"+extraQuery);
			int vCount = valleys.size();
			
			// Mix in expansion tiles per group (XC=C and XM=M and S=V)
			mountains.addAll(tilePool.find("tile_type=XM"+extraQuery));
			caves.addAll(tilePool.find("tile_type=XC"+extraQuery));
			valleys.addAll(tilePool.find("tile_type=S"+extraQuery));
			
			// Strip all game key vals (so initially, NONE of these tiles will make it to the map builder)
			GameObject.stripListKeyVals("this",hostPrefs.getGameKeyVals(),mountains);
			GameObject.stripListKeyVals("this",hostPrefs.getGameKeyVals(),caves);
			GameObject.stripListKeyVals("this",hostPrefs.getGameKeyVals(),valleys);
			
			// Random pick an appropriate # of tiles from each group, and add back the game key vals
			for (int i=0;i<mCount;i++) {
				int r = RandomNumber.getRandom(mountains.size());
				GameObject go = mountains.remove(r);
				go.setThisKeyVals(hostPrefs.getGameKeyVals());
				go.setThisAttribute("tile_type","M");
			}
			for (int i=0;i<cCount;i++) {
				int r = RandomNumber.getRandom(caves.size());
				GameObject go = caves.remove(r);
				go.setThisKeyVals(hostPrefs.getGameKeyVals());
				go.setThisAttribute("tile_type","C");
			}
			for (int i=0;i<vCount;i++) {
				int r = RandomNumber.getRandom(valleys.size());
				GameObject go = valleys.remove(r);
				go.setThisKeyVals(hostPrefs.getGameKeyVals());
				go.setThisAttribute("tile_type","V");
			}
		}
		RealmObjectMaster.getRealmObjectMaster(data).resetTileObjects();
	}
	private void prepExpansionSpells(String spellKey) {
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<GameObject> expansionSpells = pool.find("spell," + spellKey);
		for (GameObject go:expansionSpells) {
			go.setThisKeyVals(hostPrefs.getGameKeyVals());
		}
	}
	
	private void removeSpells(String spellKey){
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<GameObject> toRemove = pool.find("spell," + spellKey);
		for (GameObject go:toRemove) {
			go.stripThisKeyVals(hostPrefs.getGameKeyVals());
		}	
	}
	
	private void markItemStartingLocations() {
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<String> query = new ArrayList<>();
		query.add("item");
		query.add(hostPrefs.getGameKeyVals());
		for(GameObject item:pool.find(query)) {
			GameObject heldBy = item.getHeldBy();
			if (heldBy==null) continue; // shouldn't happen
			item.setThisAttribute(Constants.SETUP,heldBy.getStringId());
		}
	}
	private void doBoardAutoSetup() {
		Collection<String> keyVals = GamePool.makeKeyVals(hostPrefs.getGameKeyVals());
		lastRating = -1;
		mapAttempt = 0;
		MapProgressReportable reporter = new MapProgressReportable() {
			public void setProgress(int current,int total) {
				String lr = "";
				if (lastRating>=0) {
					lr = " (Last Map Rating = "+lastRating+")";
				}
				if (frame != null) {
					frame.showStatus("Attempt #"+mapAttempt+":  Building map ... "+current+" out of "+total+lr);
				}
			}
		};
		int minRating = hostPrefs.getMinimumMapRating();
		int rating = -1;
		while(rating<minRating) {
			mapAttempt++;
			while(!MapBuilder.autoBuildMap(data,keyVals,reporter)) {
				mapAttempt++;
			}
			
			rating = MapRating.getMapRating(data);
			lastRating = rating;
		}
		
		// Reset treasure location monsters
		RealmUtility.finishBoardSetupAfterBuild(hostPrefs,data);
			
		GameWrapper.findGame(data).setCurrentMapRating(rating);
	}
	private void prepQuestDeck() {
		prepQuestDeck(data);
	}
	public static void prepQuestDeck(GameData data) {
		prepQuestDeck(data, false);
	}
	public static void prepQuestDeck(GameData data, boolean checkForDuplicateQuests) {
		QuestDeck deck = QuestDeck.findDeck(data);
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(data);
		for(Quest template:QuestLoader.loadAllQuestsFromQuestFolder()) {
			if ((hostPrefs.hasPref(Constants.HOUSE3_QTR_AND_SR_QUEST_CARDS) && (template.getBoolean(QuestConstants.WORKS_WITH_QTR) || template.getBoolean(QuestConstants.WORKS_WITH_SR)))
					|| (hostPrefs.hasPref(Constants.QST_QUEST_CARDS) && !hostPrefs.hasPref(Constants.HOUSE3_EXCHANGE_QTR_AND_SR_QUEST_CARDS) && template.getBoolean(QuestConstants.WORKS_WITH_QTR))
					|| (hostPrefs.hasPref(Constants.QST_SR_QUESTS) && !hostPrefs.hasPref(Constants.HOUSE3_EXCHANGE_QTR_AND_SR_QUEST_CARDS) && template.getBoolean(QuestConstants.WORKS_WITH_SR))
					|| (hostPrefs.hasPref(Constants.QST_SR_QUESTS) && hostPrefs.hasPref(Constants.HOUSE3_EXCHANGE_QTR_AND_SR_QUEST_CARDS) && template.getBoolean(QuestConstants.WORKS_WITH_QTR))
					|| (hostPrefs.hasPref(Constants.QST_QUEST_CARDS) && hostPrefs.hasPref(Constants.HOUSE3_EXCHANGE_QTR_AND_SR_QUEST_CARDS) && template.getBoolean(QuestConstants.WORKS_WITH_SR))) {
				if (hostPrefs.hasPref(Constants.HOUSE3_NO_EVENTS_AND_ALL_PLAY_QUESTS) && template.isAllPlay()) continue;
				if (hostPrefs.hasPref(Constants.HOUSE3_NO_CHARACTER_QUEST_CARDS) && !template.isAllPlay()) continue;
				if (hostPrefs.hasPref(Constants.HOUSE3_NO_SECRET_QUESTS) && template.isSecretQuest()) continue;
				boolean doesRequireActivation = false;
				if ((hostPrefs.hasPref(Constants.HOUSE3_NO_EVENTS_AND_ALL_PLAY_QUESTS_WITHOUT_ACTIVATION) || hostPrefs.hasPref(Constants.HOUSE3_NO_EVENTS_AND_ALL_PLAY_QUESTS_WITH_ACTIVATION)) && template.isAllPlay()) {
					QuestStep step = template.getSteps().get(0);
					if (step == null) continue;
					for (QuestRequirement req : step.getRequirements()) {
						if (req.getRequirementType() == RequirementType.Active) {
							doesRequireActivation = true;
						}
					}
				}
				if (!doesRequireActivation && hostPrefs.hasPref(Constants.HOUSE3_NO_EVENTS_AND_ALL_PLAY_QUESTS_WITHOUT_ACTIVATION)) continue;
				if (doesRequireActivation && hostPrefs.hasPref(Constants.HOUSE3_NO_EVENTS_AND_ALL_PLAY_QUESTS_WITH_ACTIVATION)) continue;
				if (checkForDuplicateQuests && deck.getAllQuestNames().contains(template.getName())) continue;
				int count = template.getInt(QuestConstants.CARD_COUNT);
				if (count>0) {
					// Add the template to the data object and init deck
					Quest quest = template.copyQuestToGameData(data);
					if (quest.isAllPlay()) {
						deck.addAllPlayCard(quest); // count is ignored for all play cards
					}
					else {
						deck.addCards(quest,count);
					}
				}
			}
		}
		deck.shuffle();
	}
	private void prepBookOfQuests() {
		prepBookOfQuests(data);
	}
	public static void prepBookOfQuests(GameData data) {
		prepBookOfQuests(data,false);
	}
	public static void prepBookOfQuests(GameData data, boolean checkForDuplicateQuests) {
		QuestBookEvents book = QuestBookEvents.findBook(data);
		HostPrefWrapper hostPrefs = HostPrefWrapper.findHostPrefs(data);
		for(Quest template:QuestLoader.loadAllQuestsFromQuestFolder()) {
			if (hostPrefs.hasPref(Constants.HOUSE3_NO_EVENTS_AND_ALL_PLAY_QUESTS) && template.isEvent()) continue;
			boolean doesRequireActivation = false;
			if ((hostPrefs.hasPref(Constants.HOUSE3_NO_EVENTS_AND_ALL_PLAY_QUESTS_WITHOUT_ACTIVATION) || hostPrefs.hasPref(Constants.HOUSE3_NO_EVENTS_AND_ALL_PLAY_QUESTS_WITH_ACTIVATION)) && template.isEvent()) {
				QuestStep step = template.getSteps().get(0);
				if (step == null) continue;
				for (QuestRequirement req : step.getRequirements()) {
					if (req.getRequirementType() == RequirementType.Active) {
						doesRequireActivation = true;
					}
				}
			}
			if (!doesRequireActivation && hostPrefs.hasPref(Constants.HOUSE3_NO_EVENTS_AND_ALL_PLAY_QUESTS_WITHOUT_ACTIVATION)) continue;
			if (doesRequireActivation && hostPrefs.hasPref(Constants.HOUSE3_NO_EVENTS_AND_ALL_PLAY_QUESTS_WITH_ACTIVATION)) continue;
			if (template.getBoolean(QuestConstants.WORKS_WITH_BOQ)) {
				if (checkForDuplicateQuests && book.getAllEventNames().contains(template.getName())) continue;
				Quest quest = template.copyQuestToGameData(data);
				if (quest.isEvent()) {
					book.addEvent(quest);
				}
			}
		}
	}
	private void prepGuildQuests() {
		prepGuildQuests(data);
	}
	public static void prepGuildQuests(GameData data) {
		prepGuildQuests(data, false);
	}
	public static void prepGuildQuests(GameData data, boolean checkForDuplicateQuests) {
		QuestDeck deck = QuestDeck.findDeck(data);
		for(Quest template:QuestLoader.loadAllQuestsFromQuestFolder()) {
			if (checkForDuplicateQuests && deck.getAllQuestNames().contains(template.getName())) continue;
			if (template.getGuild()!=null) {
				Quest quest = template.copyQuestToGameData(data);
				deck.addCards(quest,1);
			}
		}
		deck.shuffle();
	}
	private void doItemSpellCasting() {
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<String> query = new ArrayList<>();
		query.addAll(GamePool.makeKeyVals(hostPrefs.getGameKeyVals()));
		query.add(Constants.CAST_SPELL_ON_INIT);
		Collection<GameObject> needsSpellInit = pool.find(query);
		for (GameObject go : needsSpellInit) {
			for (GameObject sgo : go.getHold()) {
				if (sgo.hasThisAttribute("spell")) {
					SpellWrapper spell = new SpellWrapper(sgo);
					spell.castSpellNoEnhancedMagic(go);
					spell.addTarget(hostPrefs,go);
					spell.makeInert(); // starts off as inert
				}
			}
		}
	}
}