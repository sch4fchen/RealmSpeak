package com.robin.magic_realm.components.events;

import com.robin.game.server.GameHost;

public interface IEvent {
	void apply(GameHost host);
	void expire(GameHost host);
}