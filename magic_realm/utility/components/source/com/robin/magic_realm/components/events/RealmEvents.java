package com.robin.magic_realm.components.events;

import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.game.server.GameHost;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.RealmComponent;
import com.robin.magic_realm.components.TileComponent;
import com.robin.magic_realm.components.attribute.ColorMagic;
import com.robin.magic_realm.components.attribute.TileLocation;
import com.robin.magic_realm.components.utility.RealmObjectMaster;
import com.robin.magic_realm.components.wrapper.CharacterWrapper;

public class RealmEvents {
	
	private static String eventsConfiguration = "RealmEvents";
	private static String eventsForTheWeek = "events";
	private static String activeEvents = "events_active";
	private static String infiniteColorMagicSource = "infinite_colorMagic_Source";
	
	private static enum Events {
		Blank,
		//ViolentStorm,
		//Fog,
		//Illusion,
		//Lost,
		//NightOfTheDemon,
		//Migrate,
		//HorseWhisper,
		//FrozenRiver,
		//CaveIn,
		//HurricaneWinds,
		//ProwlI,
		//ProwlII,
		//ProwlIII,
		Regenerate,
		Enchant,
		Break,
		White,
		Grey,
		Gold,
		Purple,
		Black,
		//ViolentWinds,
		//Thorns,
		//Flood,
		//NegativeAura,
		//FlutterMigrate,
		//PatterMigrate,
		//SlitherMigrate,
		//HowlMigrate,
		//RoarMigrate,
		//MountainSurge,
		//PeacefulDay,
		Prism
	}

	public RealmEvents() {
	}
	
	public static void drawEvent(GameHost host) {
		GameObject config = findEventsConfig(host.getGameData());
		ArrayList<String> events = config.getThisAttributeList(eventsForTheWeek);
		int i = RandomNumber.getRandom(events.size());
		String eventString = events.remove(i);
		config.addThisAttributeListItem(activeEvents,eventString);
		IEvent event = createEvent(Events.valueOf(eventString));
		host.broadcast("host","Event: "+event.getTitle());
		event.apply(host.getGameData());
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
		for (int i=0;i<7;i++) {
			list.add(Events.Blank.toString());
		}
		Events[] possibleEvents = Events.values();
		for (int i=0;i<3;i++) {
			int j = RandomNumber.getRandom(Events.values().length);
			list.add(possibleEvents[j].toString());
		}
		config.setThisAttributeList(eventsForTheWeek,list);
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
			case Regenerate: return new RegenerateEvent();
			case Enchant: return new EnchantEvent();
			case Break: return new BreakEvent();
			case White: return new WhiteEvent();
			case Grey: return new GreyEvent();
			case Gold: return new GoldEvent();
			case Purple: return new PurpleEvent();
			case Black: return new BlackEvent();
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
}