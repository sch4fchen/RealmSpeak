package com.robin.magic_realm.components.summary;

public abstract class SummaryEvent {
	
	protected abstract String getKey();
	protected abstract String getDataString();
	
	public SummaryEvent() {
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getKey());
		sb.append(getDataString());
		return sb.toString();
	}
	public static SummaryEvent getSummaryEvent(String val) {
		String key = val.substring(0,3);
		String data = val.substring(3);
		SummaryEvent ev = null;
		if (CharacterMoveEvent.KEY.equals(key)) {
			ev = new CharacterMoveEvent(data);
		}
		return ev;
	}
}