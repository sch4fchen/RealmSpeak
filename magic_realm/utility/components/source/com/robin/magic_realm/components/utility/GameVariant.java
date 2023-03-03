package com.robin.magic_realm.components.utility;

public class GameVariant {
	public static GameVariant ORIGINAL_GAME_VARIANT = new GameVariant("Original Game","original_game","standard_game",true,true,true,true,true);
	public static GameVariant PRUITTS_GAME_VARIANT = new GameVariant("Pruitt's Monsters","alt_monsters1_game","standard_game",true,true,true,true,true);
	public static GameVariant EXP1_GAME_VARIANT = new GameVariant("Expansion One","rw_expansion_1","rw_expansion_1_setup",false,false,true,true,true);
	public static GameVariant SUPER_REALM = new GameVariant("Super Realm","super_realm","super_realm_setup",false,true,false,true,false);
	
	private String title;
	private String keyVals;
	private String setup;
	private boolean allowExp1Tiles;
	private boolean allowExp1Content;
	private boolean allowSrContent;
	private boolean allowAdditionalContent;
	private boolean allowMultiBoardAndAlternativeTiles;

	public GameVariant(String title, String keyVals, String setup, boolean allowExpTiles, boolean allowExpContent,  boolean allowSrContent, boolean allowAdditionalContent, boolean allowMultiBoardAndAlternativeTiles) {
		this.title = title;
		this.keyVals = keyVals;
		this.setup = setup;
		this.allowExp1Tiles = allowExpTiles;
		this.allowExp1Content = allowExpContent;
		this.allowSrContent = allowSrContent;
		this.allowAdditionalContent = allowAdditionalContent;
		this.allowMultiBoardAndAlternativeTiles = allowMultiBoardAndAlternativeTiles;
	}

	public String toString() {
		return title;
	}
	
	public String getTitle() {
		return title;
	}

	public String getKeyVals() {
		return keyVals;
	}

	public String getSetup() {
		return setup;
	}

	public boolean getAllowExp1Tiles() {
		return allowExp1Tiles;
	}
	
	public boolean getAllowExp1Content() {
		return allowExp1Content;
	}
	
	public boolean getAllowSrContent() {
		return allowSrContent;
	}
	
	public boolean getAllowAdditionalContent() {
		return allowAdditionalContent;
	}
	
	public boolean getAllowMultiBoardAndAlternativeTiles() {
		return allowMultiBoardAndAlternativeTiles;
	}
}