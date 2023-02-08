package com.robin.magic_realm.RealmQuestBuilder;

public class KeyValuePair {
	private String key;
	private String value;
	public KeyValuePair(String key,String value) {
		this.key = key;
		this.value = value;
	}
	public String toString() {
		return key;
	}
	public String getKey() {
		return key;
	}
	public String getValue() {
		return value;
	}
}