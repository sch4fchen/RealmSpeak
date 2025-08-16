package com.robin.magic_realm.components.events;

import java.awt.Point;
import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.game.server.GameHost;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.ClearingDetail;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.attribute.ColorMagic;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.ClearingUtility;
import com.robin.magic_realm.components.utility.RealmObjectMaster;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;
import com.robin.magic_realm.map.Tile;

public class RealmEvents {
	
	private static final String eventsConfiguration = "RealmEvents";
	private static final String eventsForTheWeek = "events";
	private static final String activeEvents = "events_active";
	private static final String infiniteColorMagicSource = "infinite_colorMagic_Source";
	public static final int firstEventDay = 8;
	public static final int firstEventMonth = 2;
	public static final int blankEventsPerWeek = 7;
	public static final int normalEventsPerWeek = 3;
	
	private static enum Events {
		Blank,
		ViolentStorm,
		Fog,
		Illusion,
		Lost,
		NightOfTheDemon,
		Migrate,
		HorseWhisper,
		FrozenRiver,
		CaveIn,
		//HurricaneWinds,
		ProwlI,
		ProwlII,
		ProwlIII,
		Regenerate,
		Enchant,
		Break,
		White,
		Grey,
		Gold,
		Purple,
		Black,
		ViolentWinds,
		Thorns,
		Flood,
		NegativeAura,
		//FlutterMigrate,
		//PatterMigrate,
		//SlitherMigrate,
		//HowlMigrate,
		//RoarMigrate,
		MountainSurge,
		PeacefulDay,
		Prism
	}

	public RealmEvents() {
	}
	
	public static void drawEvent(GameHost host) {
		GameObject config = findEventsConfig(host.getGameData());
		ArrayList<String> events = config.getThisAttributeList(eventsForTheWeek);
		if (events == null || events.isEmpty()) return;
		int i = RandomNumber.getRandom(events.size());
		String eventString = events.remove(i);
		config.addThisAttributeListItem(activeEvents,eventString);
		IEvent event = createEvent(Events.valueOf(eventString));
		host.broadcast("host","Event: "+event.getTitle());
	}
	
	public static void applyBirdsong(GameHost host) {
		GameObject config = findEventsConfig(host.getGameData());
		ArrayList<String> events = config.getThisAttributeList(activeEvents);
		if (events == null || events.isEmpty()) return;
		
		for (String eventString : events) {
			IEvent event = createEvent(Events.valueOf(eventString));
			event.applyBirdsong(host.getGameData());
		}
	}
	
	public static void applySunset(GameHost host) {
		GameObject config = findEventsConfig(host.getGameData());
		ArrayList<String> events = config.getThisAttributeList(activeEvents);
		if (events == null || events.isEmpty()) return;
		
		for (String eventString : events) {
			IEvent event = createEvent(Events.valueOf(eventString));
			event.applySunset(host.getGameData());
		}
	}
	
	public static void expireEvents(GameHost host) {
		GameObject config = findEventsConfig(host.getGameData());
		ArrayList<String> events = config.getThisAttributeList(activeEvents);
		if (events==null) return;
		
		for (String eventString : events) {
			IEvent event = createEvent(Events.valueOf(eventString));
			event.expire(host.getGameData());
		}
		config.removeThisAttribute(activeEvents);
	}
	
	public static ArrayList<IEvent> getCurrentEvents(GameData gameData) {
		GameObject config = findEventsConfig(gameData);
		ArrayList<String> events = config.getThisAttributeList(activeEvents);
		if (events==null) return null;
		
		ArrayList<IEvent> list = new ArrayList<>();
		for (String eventString : events) {
			IEvent event = createEvent(Events.valueOf(eventString));
			list.add(event);
		}
		
		return list;
	}
	
	public static void shuffleEvents(GameHost host) {
		GameObject config = findEventsConfig(host.getGameData());
		ArrayList<String> list = new ArrayList<>();
		for (int i=0;i<blankEventsPerWeek;i++) {
			list.add(Events.Blank.toString());
		}
		Events[] possibleEvents = Events.values();
		for (int i=0;i<normalEventsPerWeek;i++) {
			int j = RandomNumber.getRandom(Events.values().length);
			list.add(possibleEvents[j].toString());
		}
		config.setThisAttributeList(eventsForTheWeek,list);
		host.broadcast("host","Events shuffled");
	}
	
	public static GameObject findEventsConfig(GameData data) {
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<GameObject> config = pool.find("name="+eventsConfiguration);
		if (config==null || config.isEmpty()) {
			return createNewConfigGameObject(data);
		}
		return config.get(0);
	}
	
	private static GameObject createNewConfigGameObject(GameData data) {
		GameObject config = data.createNewObject();
		config.setName(eventsConfiguration);
		return config;
	}
	
	public static IEvent createEvent(Events eventName){
		switch(eventName){
			case ViolentStorm: return new ViolentStormEvent();
			case Fog: return new FogEvent();
			case Illusion: return new IllusionEvent();
			case Lost: return new LostEvent();
			case NightOfTheDemon: return new NightOfTheDemonEvent();
			case Migrate: return new MigrateEvent();
			case HorseWhisper: return new HorseWhisperEvent();
			case FrozenRiver: return new FrozenRiverEvent();
			case CaveIn: return new CaveInEvent();
			//case HurricaneWinds: return new HurricaneWindsEvent();
			case ProwlI: return new Prowl1Event();
			case ProwlII: return new Prowl2Event();
			case ProwlIII: return new Prowl3Event();
			case Regenerate: return new RegenerateEvent();
			case Enchant: return new EnchantEvent();
			case Break: return new BreakEvent();
			case White: return new WhiteEvent();
			case Grey: return new GreyEvent();
			case Gold: return new GoldEvent();
			case Purple: return new PurpleEvent();
			case Black: return new BlackEvent();
			case ViolentWinds: return new ViolentWindsEvent();
			case Thorns: return new ThornsEvent();
			case Flood: return new FloodEvent();
			case NegativeAura: return new NegativeAuraEvent();
			//case FlutterMigrate: return new FlutterMigrateEvent();
			//case PatterMigrate: return new PatterMigrateEvent();
			//case SlitherMigrate: return new SlitherMigrateEvent();
			//case HowlMigrate: return new HowlMigrateEvent();
			//case RoarMigrate: return new RoarMigrateEvent();
			case MountainSurge: return new MountainSurgeEvent();
			case PeacefulDay: return new PeacefulDayEvent();
			case Prism: return new PrismEvent();
			case Blank:
			default: return new BlankEvent();
		}
	}
	
	public static void addInfiniteColorMagicSource(GameData data,String color) {
		GameObject config = findEventsConfig(data);
		config.addThisAttributeListItem(infiniteColorMagicSource, color);
	}
	
	public static void removeInfiniteColorMagicSource(GameData data,String color) {
		GameObject config = findEventsConfig(data);
		config.removeThisAttributeListItem(infiniteColorMagicSource, color);
	}
	
	public static ArrayList<ColorMagic> getInfiniteColorMagicSources(GameData data) {
		GameObject config = findEventsConfig(data);
		ArrayList<ColorMagic> list = new ArrayList<>();
		if (config.hasThisAttribute(infiniteColorMagicSource)) {
			for (String color : config.getThisAttributeList(infiniteColorMagicSource)) {
				list.add(ColorMagic.makeColorMagic(color,true));
			}
		}
		return list;
	}
	
	public static void addEffectForTile(GameObject config,String effect,String id) {
		config.addThisAttributeListItem(effect, id);
	}
	
	public static void removeEffectForTile(GameObject config,String effect,String id) {
		config.removeThisAttributeListItem(effect, id);
	}
	
	public static ArrayList<String> getTileIdsForEffect(GameObject config,String effect) {
		return config.getThisAttributeList(effect);
	}
	
	public static ArrayList<CharacterWrapper> getLivingCharacters(GameData gameData) {
		GamePool pool = new GamePool(RealmObjectMaster.getRealmObjectMaster(gameData).getPlayerCharacterObjects());
		ArrayList<GameObject> list = pool.find(CharacterWrapper.NAME_KEY);
		ArrayList<CharacterWrapper> active = new ArrayList<>();
		for (GameObject characterGo : list) {
			CharacterWrapper character = new CharacterWrapper(characterGo);
			if (!character.isDead()) {
				active.add(character);
			}
		}
		return active;
	}
	
	public static TileComponent chooseRandomTile(GameData data) {
		return chooseRandomTile(data,null);
	}
	public static TileComponent chooseRandomTile(GameData data,ArrayList<String> tileTypes) {
		ArrayList<TileComponent> list = new ArrayList<>();
		for (CharacterWrapper character: getLivingCharacters(data)) {
			TileLocation loc = character.getCurrentLocation();
			if (loc!=null && loc.tile!=null && !list.contains(loc.tile)) {
				if (tileTypes!=null) {
					for (String type : tileTypes) {
						if (loc.tile.getTileType().matches(type)) {
							list.add(loc.tile);
							break;
						}
					}
				}
				else {
					list.add(loc.tile);
				}
			}
			for (RealmComponent hireling : character.getAllHirelings()) {
				TileLocation locHireling = hireling.getCurrentLocation();
				if (locHireling!=null && locHireling.tile!=null && !list.contains(locHireling.tile)) {
					if (tileTypes!=null) {
						for (String type : tileTypes) {
							if (locHireling.tile.getTileType().matches(type)) {
								list.add(locHireling.tile);
								break;
							}
						}
					}
					else {
						list.add(locHireling.tile);
					}
				}
			}
		}
		if (list.isEmpty()) return null;
		return list.get(RandomNumber.getRandom(list.size()));
	}
	public static GameObject chooseRandomTileWithUnhiredNatives(GameData data) {
		ArrayList<GameObject> list = new ArrayList<>();
		GamePool pool = new GamePool(data.getGameObjects());
		ArrayList<GameObject> denizens = pool.find(RealmComponent.NATIVE+",!treasure");
		for (GameObject denizen: denizens) {
			RealmComponent rc = RealmComponent.getRealmComponent(denizen);
			if (!rc.isHiredOrControlled()) {
				GameObject heldBy = denizen.getHeldBy();
				if (heldBy!=null && heldBy.hasThisAttribute(RealmComponent.TILE)) {
					list.add(heldBy);
				}
			}
		}
		if (list.isEmpty()) return null;
		return list.get(RandomNumber.getRandom(list.size()));
	}
	public static ArrayList<TileComponent> chooseRandomAndAdjacentTiles(GameData data) {
		ClearingUtility.initAdjacentTiles(data);
		
		ArrayList<TileComponent> allTiles = new ArrayList<>();
		TileComponent tile = RealmEvents.chooseRandomTile(data);
		allTiles.add(tile);
		allTiles.addAll(tile.getAllAdjacentTiles());
		return allTiles;
	}
	public static ClearingDetail chooseRandomClearing(GameData data,ArrayList<String> clearingTypes) {
		ArrayList<ClearingDetail> list = new ArrayList<>();
		for (CharacterWrapper character: getLivingCharacters(data)) {
			TileLocation loc = character.getCurrentLocation();
			if (loc!=null && loc.clearing!=null && !list.contains(loc.clearing)) {
				if (clearingTypes!=null) {
					for (String type : clearingTypes) {
						if (loc.clearing.getType().matches(type)) {
							list.add(loc.clearing);
							break;
						}
					}
				}
				else {
					list.add(loc.clearing);
				}
			}
			for (RealmComponent hireling : character.getAllHirelings()) {
				TileLocation locHireling = hireling.getCurrentLocation();
				if (locHireling!=null && locHireling.clearing!=null && !list.contains(locHireling.clearing)) {
					if (clearingTypes!=null) {
						for (String type : clearingTypes) {
							if (locHireling.clearing.getType().matches(type)) {
								list.add(locHireling.clearing);
								break;
							}
						}
					}
					else {
						list.add(locHireling.clearing);
					}
				}
			}
		}
		if (list.isEmpty()) return null;
		return list.get(RandomNumber.getRandom(list.size()));
	}
	public static ArrayList<GameObject> chooseAllTiles(GameData data) {
		GamePool pool = new GamePool(data.getGameObjects());
		return pool.find("tile");
	}
	public static ArrayList<TileComponent> chooseRandomWaterAndAdjacentTiles(GameData data) {
		ArrayList<GameObject> allTiles = RealmEvents.chooseAllTiles(data);
		ArrayList<TileComponent> waterTiles = new ArrayList<>();
		ArrayList<TileComponent> chosenTiles = new ArrayList<>();
		for (GameObject tile : allTiles) {
			TileComponent tileComponent = new TileComponent(tile);
			for (ClearingDetail cl : tileComponent.getClearings()) {
				if (cl.isWater() || cl.isFrozenWater()) {
					waterTiles.add(tileComponent);
					break;
				}
			}
		}
		
		if (!waterTiles.isEmpty()) {
			TileComponent chosenTile = waterTiles.remove(RandomNumber.getRandom(waterTiles.size()));
			chosenTiles.add(chosenTile);
			Point basePosition = Tile.getPositionFromGameObject(chosenTile.getGameObject());
			for (TileComponent tile : waterTiles) {
				Point position = Tile.getPositionFromGameObject(tile.getGameObject());
				if ((position.x==basePosition.x || position.x==basePosition.x-1 || position.x==basePosition.x+1)
						&& (position.y==basePosition.y || position.y==basePosition.y-1 || position.y==basePosition.y+1)) {
					chosenTiles.add(tile);
				}
			}
		}
		return chosenTiles;
	}
}