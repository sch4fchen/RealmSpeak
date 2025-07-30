package com.robin.magic_realm.RealmSpeak;

import java.util.ArrayList;

import com.robin.game.objects.GameData;
import com.robin.game.objects.GameObject;
import com.robin.game.objects.GamePool;
import com.robin.game.server.GameHost;
import com.robin.general.util.RandomNumber;
import com.robin.magic_realm.components.events.BlankEvent;
import com.robin.magic_realm.components.events.IEvent;
import com.robin.magic_realm.components.events.PrismEvent;

public class RealmEvents {
	
	private static String eventsConfiguration = "eventsConfiguration";
	private static String eventsForTheWeek = "events";
	private static String activeEvents = "events_active";
	private static String Blank = "Blank";
	
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
		HurricaneWinds,
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
		FlutterMigrate,
		PatterMigrate,
		SlitherMigrate,
		HowlMigrate,
		RoarMigrate,
		MountainSurge,
		PeacefulDay,
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
		event.apply(host);
	}
	
	public static void expireEvents(GameHost host) {
		GameObject config = findEventsConfig(host.getGameData());
		ArrayList<String> events = config.getThisAttributeList(activeEvents);
		if (events==null) return;
		
		for (String eventString : events) {
			IEvent event = createEvent(Events.valueOf(eventString));
			event.expire(host);
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
			list.add(Blank);
		}
		Events[] possibleEvents = Events.values();
		for (int i=0;i<3;i++) {
			int j = RandomNumber.getRandom(Events.values().length);
			list.add(possibleEvents[j].toString());
		}
		config.setThisAttributeList(eventsForTheWeek,list);
	}
	
	private static GameObject findEventsConfig(GameData data) {
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
			case Prism: return new PrismEvent();
			case Blank:
			default: return new BlankEvent();
		}
	}
}