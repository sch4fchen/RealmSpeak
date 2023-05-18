package com.robin.magic_realm.RealmSpeak;

import javax.swing.SwingUtilities;

import com.robin.game.objects.GameObject;
import com.robin.general.io.PreferenceManager;
import com.robin.general.sound.SoundCache;
import com.robin.general.swing.ComponentTools;
import com.robin.magic_realm.components.CardComponent;
import com.robin.magic_realm.components.CharacterChitComponent;
import com.robin.magic_realm.components.ChitComponent;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.attribute.ChatLine;
import com.robin.magic_realm.components.attribute.ChatLine.HeaderMode;
import com.robin.magic_realm.components.utility.CustomUiUtility;
import com.robin.magic_realm.components.utility.RealmUtility;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class RealmSpeakOptions {
	public static final String NETWORKING_OPTION = "netwOpt";
	public static final String FRAME_WIDTH = "frameWidth";
	public static final String FRAME_HEIGHT = "frameHeight";
	public static final String FRAME_X = "frameX";
	public static final String FRAME_Y = "frameY";
	public static final String LAST_SAVE_LOCATION = "lastSaveLocation";
	public static final String LAST_EXPORT_LOCATION = "lastExportLocation";
	
	public static final String METAL_LNF = "metalLnf";
	public static final String ACTION_ICONS = "actionIcons";
	public static final String CHIT_DISPLAY_STYLE = "chitDisplayStyle";
	public static final String CHIT_DISPLAY_ARMOR = "chitDisplayArmor";
	public static final String CHIT_DISPLAY_SUBLINE = "chitDisplaySubline";
	public static final String CHARACTER_CHIT_DISPLAY_STYLE = "characterChitDisplayStyle";
	public static final String TILES_DISPLAY_STYLE = "tilesDisplayStyle";
	public static final String MAP_SLIDER = "mapSlider";
	public static final String HIGHLIGHT_CLEARING_NUMBERS = "hClearN";
	public static final String SHOW_SEASON_ICON = "sSeasI";
	public static final String NUMBER_OF_CHAT_LINES = "nlChat";
	public static final String HEADER_CHAT_LINES = "hChatL";
	public static final String DAILY_COMBAT = "dailyCombat";
	public static final String MONSTER_NUMBERS = "monsterNumbers";
	public static final String CHIT_KILLED_BY = "killedBy";
	public static final String SETUP_CARD_LAYOUT = "setupCardLayout";
	public static final String HEAVY_INV_WARNING = "heavyInvWarning";
	public static final String INCOMPLETE_PHASE_WARNING = "incompletePhaseWarning";
	public static final String INVALID_PHASE_WARNING = "invalidPhaseWarning";
	public static final String MOVE_AFTER_HIRE_WARNING = "moveAfterHireWarning";
	public static final String UNASSIGNED_HIRELINGS_WARNING = "unassignedHirelingsWarning";
	public static final String TURN_END_RESULTS = "turnEndResultsOption";
	public static final String ENABLE_SOUND = "enableSound";
	
	PreferenceManager options;
	
	public RealmSpeakOptions() {
		readFramePreferences();
	}
	public PreferenceManager getOptions() {
		return options;
	}
	public void apply(RealmSpeakFrame frame) {
		RealmGameHandler gameHandler = frame.getGameHandler();
		switch(options.getInt(RealmSpeakOptions.CHIT_DISPLAY_STYLE)) {
			case RealmComponent.DISPLAY_STYLE_CLASSIC:
				RealmComponent.displayStyle = RealmComponent.DISPLAY_STYLE_CLASSIC;
				break;
			case RealmComponent.DISPLAY_STYLE_COLOR:
				RealmComponent.displayStyle = RealmComponent.DISPLAY_STYLE_COLOR;
				break;
			case RealmComponent.DISPLAY_STYLE_FRENZEL:
				RealmComponent.displayStyle = RealmComponent.DISPLAY_STYLE_FRENZEL;
				break;
			case RealmComponent.DISPLAY_STYLE_LEGENDARY:
				RealmComponent.displayStyle = RealmComponent.DISPLAY_STYLE_LEGENDARY;
				break;
			case RealmComponent.DISPLAY_STYLE_ALTERNATIVE:
				RealmComponent.displayStyle = RealmComponent.DISPLAY_STYLE_ALTERNATIVE;
				break;
			default:
				RealmComponent.displayStyle = RealmComponent.DISPLAY_STYLE_CLASSIC;
				break;
		}
		RealmComponent.displayArmor = options.getBoolean(RealmSpeakOptions.CHIT_DISPLAY_ARMOR);
		RealmComponent.displaySubline = options.getBoolean(RealmSpeakOptions.CHIT_DISPLAY_SUBLINE);
		
		switch(options.getInt(RealmSpeakOptions.CHARACTER_CHIT_DISPLAY_STYLE)) {
			case CharacterChitComponent.DISPLAY_STYLE_CLASSIC:
				CharacterChitComponent.displayStyle = CharacterChitComponent.DISPLAY_STYLE_CLASSIC;
				break;
			case CharacterChitComponent.DISPLAY_STYLE_LEGENDARY_CLASSIC:
				CharacterChitComponent.displayStyle = CharacterChitComponent.DISPLAY_STYLE_LEGENDARY_CLASSIC;
				break;
			case CharacterChitComponent.DISPLAY_STYLE_LEGENDARY:
				CharacterChitComponent.displayStyle = CharacterChitComponent.DISPLAY_STYLE_LEGENDARY;
				break;
			case CharacterChitComponent.DISPLAY_STYLE_ALTERNATIVE:
				CharacterChitComponent.displayStyle = CharacterChitComponent.DISPLAY_STYLE_ALTERNATIVE;
				break;
			default:
				CharacterChitComponent.displayStyle = CharacterChitComponent.DISPLAY_STYLE_CLASSIC;
				break;
		}
		switch(options.getInt(RealmSpeakOptions.TILES_DISPLAY_STYLE)) {
			case TileComponent.DISPLAY_TILES_STYLE_LEGENDARY:
				TileComponent.displayTilesStyle = TileComponent.DISPLAY_TILES_STYLE_LEGENDARY;
				break;
			case TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS:
				TileComponent.displayTilesStyle = TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS;
				break;
			default:
				TileComponent.displayTilesStyle = TileComponent.DISPLAY_TILES_STYLE_CLASSIC;
				break;
		}
		GameObject.showNumbers = options.getBoolean(MONSTER_NUMBERS);
		ChitComponent.killedByOption = options.getBoolean(CHIT_KILLED_BY);
		CardComponent.killedByOption = options.getBoolean(CHIT_KILLED_BY);
		if (gameHandler!=null) {
			gameHandler.updateToolbarOptions(getActionIconState());
			gameHandler.getInspector().setZoomSlider(options.isPref(MAP_SLIDER));
			gameHandler.getInspector().setClearingHighlight(options.isPref(HIGHLIGHT_CLEARING_NUMBERS));
			gameHandler.getInspector().setShowSeasonIcon(options.isPref(SHOW_SEASON_ICON));
			gameHandler.getInspector().setShowChatLines(options.getInt(RealmSpeakOptions.NUMBER_OF_CHAT_LINES));
			gameHandler.getInspector().redrawMap();
			gameHandler.updateCharacterFrames();
		}
		String headerMode = options.get(HEADER_CHAT_LINES);
		if (headerMode!=null) {
			ChatLine.setHeaderMode(HeaderMode.valueOf(headerMode));
		}
		SoundCache.setSoundEnabled(options.getBoolean(ENABLE_SOUND,true));
		if (options.getBoolean(RealmSpeakOptions.METAL_LNF)) {
			ComponentTools.setMetalLookAndFeel();
		}
		else {
			ComponentTools.setSystemLookAndFeel();
		}
		CustomUiUtility.initColors();
		SwingUtilities.updateComponentTreeUI(frame);
	}
	private void readFramePreferences() {
		options = RealmUtility.getRealmSpeakPrefs();
		if (options.canLoad()) {
			options.loadPreferences();
		}
		else {
			// setup defaults. Might need more defaults here
			options.set(METAL_LNF,true);
			options.set(CustomUiUtility.BACKGROUND_COLOR, CustomUiUtility.BACKGROUND_COLOR_0);
			options.set(ACTION_ICONS,ActionIcon.ACTION_ICON_NORMAL);
			options.set(MAP_SLIDER,false);
			options.set(DAILY_COMBAT,"ON");
			options.set(HEAVY_INV_WARNING,true);
			options.set(INCOMPLETE_PHASE_WARNING,true);
			options.set(INVALID_PHASE_WARNING,true);
			options.set(MOVE_AFTER_HIRE_WARNING,true);
			options.set(UNASSIGNED_HIRELINGS_WARNING,true);
			options.set(CHIT_DISPLAY_STYLE,RealmComponent.DISPLAY_STYLE_LEGENDARY);
			options.set(CHARACTER_CHIT_DISPLAY_STYLE,CharacterChitComponent.DISPLAY_STYLE_LEGENDARY);
			options.set(TILES_DISPLAY_STYLE,TileComponent.DISPLAY_TILES_STYLE_LEGENDARY_WITH_ICONS);
			options.set(CHIT_KILLED_BY,true);
			options.set(SETUP_CARD_LAYOUT,true);
		}
	}
	public void save() {
		options.savePreferences();
	}
	public int getActionIconState() {
		switch(options.getInt(RealmSpeakOptions.ACTION_ICONS)) {
			case ActionIcon.ACTION_ICON_MEDIUM:
				return ActionIcon.ACTION_ICON_MEDIUM;
			case ActionIcon.ACTION_ICON_SMALL:
				return ActionIcon.ACTION_ICON_SMALL;
			case ActionIcon.ACTION_ICON_FULL_TEXT:
				return ActionIcon.ACTION_ICON_FULL_TEXT;
			case ActionIcon.ACTION_ICON_ABBREV_TEXT:
				return ActionIcon.ACTION_ICON_ABBREV_TEXT;
			case ActionIcon.ACTION_ICON_NORMAL:
			default:
				return ActionIcon.ACTION_ICON_NORMAL;
		}
	}
	public boolean dailyCombatOn(CharacterWrapper character) {
		String val = options.get(DAILY_COMBAT);
		if ("ON".equals(val)) {
			return true;
		}
		else if ("ON_S".equals(val)) {
			return character.isSpellCaster();
		}
		return false;
	}
}