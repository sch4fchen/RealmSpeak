package com.robin.magic_realm.components.events;

import com.robin.game.server.GameHost;

public class PrismEvent implements IEvent {
	private static final String title = "Prism";
	private static final String description = "A source of grey, gold and purple magic is in every clearing of every hex.";
	public void apply(GameHost host) {
	}
	public void expire(GameHost host) {
	}
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public String getDescription() {
		return description;
	}
}