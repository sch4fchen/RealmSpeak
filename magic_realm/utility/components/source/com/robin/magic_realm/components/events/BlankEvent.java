package com.robin.magic_realm.components.events;

import com.robin.game.server.GameHost;

public class BlankEvent implements IEvent {
	public void apply(GameHost host) {
		host.broadcast("host","No event today.");
	}
	public void expire(GameHost host) {
	}
}