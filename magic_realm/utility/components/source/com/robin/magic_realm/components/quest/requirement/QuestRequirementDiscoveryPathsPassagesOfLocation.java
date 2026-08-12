package com.robin.magic_realm.components.quest.requirement;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JFrame;

import com.robin.game.objects.GameObject;
import com.robin.magic_realm.components.PathDetail;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.quest.QuestLocation;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class QuestRequirementDiscoveryPathsPassagesOfLocation extends QuestRequirement {
	public static final String LOCATION = "_loc";
	public static final String PATHS = "_paths";
	public static final String PASSAGES = "_passasges";
	public static final String TILE_SIDES = "_tile_sides";
	public static enum TileSides {
		All,
		Unenchanted,
		Enchanted,
		;
	}
	
	public QuestRequirementDiscoveryPathsPassagesOfLocation(GameObject go) {
		super(go);
	}

	protected boolean testFulfillsRequirement(JFrame frame, CharacterWrapper character, QuestRequirementParams reqParams) {
		QuestLocation ql = getQuestLocation();
		if (ql==null) return false;
		for (TileLocation tileLoc : ql.fetchAllLocations(getGameData())) {
			if (paths()) {
				ArrayList<PathDetail> paths = new ArrayList<>();				
				if (getTileSides()==TileSides.Unenchanted) {
					if (tileLoc.tile.isEnchanted()) {
						paths.addAll(tileLoc.tile.getHiddenPaths(false,true));
					} else {
						paths.addAll(tileLoc.tile.getHiddenPaths(true,false));
					}
				} else if (getTileSides()==TileSides.Enchanted) {
					if (tileLoc.tile.isEnchanted()) {
						paths.addAll(tileLoc.tile.getHiddenPaths(true,false));
					} else {
						paths.addAll(tileLoc.tile.getHiddenPaths(false,true));
					}
				} else {
					paths.addAll(tileLoc.tile.getHiddenPaths());
				}
					
				for (PathDetail path : tileLoc.tile.getHiddenPaths()) {
					if (!character.hasHiddenPathDiscovery(path.getFullPathKey())) return false;
				}
			}
			if (passages()) {
				ArrayList<PathDetail> passages = new ArrayList<>();				
				if (getTileSides()==TileSides.Unenchanted) {
					if (tileLoc.tile.isEnchanted()) {
						passages.addAll(tileLoc.tile.getSecretPassages(false,true));
					} else {
						passages.addAll(tileLoc.tile.getSecretPassages(true,false));
					}
				} else if (getTileSides()==TileSides.Enchanted) {
					if (tileLoc.tile.isEnchanted()) {
						passages.addAll(tileLoc.tile.getSecretPassages(true,false));
					} else {
						passages.addAll(tileLoc.tile.getSecretPassages(false,true));
					}
				} else {
					passages.addAll(tileLoc.tile.getSecretPassages());
				}
				
				for (PathDetail passage : passages) {
					if (!character.hasSecretPassageDiscovery(passage.getFullPathKey())) return false;
				}
			}
		}
		return true;		
	}
	
	protected String buildDescription() {
		StringBuilder sb = new StringBuilder();
		return sb.toString();
	}

	public RequirementType getRequirementType() {
		return RequirementType.DiscoveryPathsPassagesOfLocation;
	}
	
	public QuestLocation getQuestLocation() {
		String id = getString(LOCATION);
		if (id != null) {
			GameObject go = getGameData().getGameObject(Long.valueOf(id));
			if (go != null) {
				return new QuestLocation(go);
			}
		}
		return null;
	}
	
	public void updateIds(Hashtable<Long, GameObject> lookup) {
		updateIdsForKey(lookup, LOCATION);
	}
	
	public boolean paths() {
		return getBoolean(PATHS);
	}
	
	public boolean passages() {
		return getBoolean(PASSAGES);
	}
	
	public TileSides getTileSides() {
		return TileSides.valueOf(getString(TILE_SIDES));
	}
}