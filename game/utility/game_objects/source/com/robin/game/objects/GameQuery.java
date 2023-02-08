package com.robin.game.objects;

import java.util.ArrayList;

public class GameQuery {
	
	private String blockName;
	
	public GameQuery() {
	}
	public GameQuery(String blockName) {
		this.blockName = blockName;
	}
	public boolean hasGameObjectWithKey(ArrayList<GameObject> list,String key) {
		return firstGameObjectWithKey(list,key)!=null;
	}
	public boolean hasGameObjectWithKeyAndValue(ArrayList<GameObject> list,String key,String value) {
		return firstGameObjectWithKeyAndValue(list,key,value)!=null;
	}
	public GameObject firstGameObjectWithKey(ArrayList<GameObject> list,String key) {
		ArrayList<GameObject> ret = query(list,key,null,true);
		return ret.isEmpty()?null:ret.get(0);
	}
	public GameObject firstGameObjectWithKeyAndValue(ArrayList<GameObject> list,String key,String value) {
		ArrayList<GameObject> ret = query(list,key,value,true);
		return ret.isEmpty()?null:ret.get(0);
	}
	public ArrayList<GameObject> allGameObjectsWithKey(ArrayList<GameObject> list,String key) {
		return query(list,key,null,false);
	}
	public ArrayList<GameObject> allGameObjectsWithKeyAndValue(ArrayList<GameObject> list,String key,String value) {
		return query(list,key,value,false);
	}
	private ArrayList<GameObject> query(ArrayList<GameObject> list,String key,String value,boolean stopAtFirst) {
		ArrayList<GameObject> ret = new ArrayList<>();
		for (GameObject go:list) {
			ArrayList<String> blockNames = new ArrayList<>();
			if (blockName!=null) {
				blockNames.add(blockName);
			}
			else {
				blockNames.addAll(go.getAttributeBlockNames());
			}
			for (String bn:blockNames) {
				if (value==null && go.hasAttribute(bn,key)) {
					ret.add(go);
				}
				else if (value!=null) {
					Object val = go.getObject(bn,key);
					boolean found = false;
					if (val instanceof ArrayList) {
						found = ((ArrayList)val).contains(value);
					}
					else {
						found = value.equals(val); 
					}
					if (found) {
						ret.add(go);
					}
				}
				if (stopAtFirst && !ret.isEmpty()) {
					return ret;
				}
			}
		}
		return ret;
	}
}