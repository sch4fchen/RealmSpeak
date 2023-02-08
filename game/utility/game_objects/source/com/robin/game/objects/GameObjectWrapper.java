package com.robin.game.objects;

import java.util.ArrayList;


public abstract class GameObjectWrapper {

	private GameObject gameObject;
	
	public GameObjectWrapper(GameObject obj) {
		gameObject = obj;
		if (gameObject==null) {
			throw new IllegalArgumentException("Can't make a wrapper with a null GameObject!!");
		}
	}
	public boolean equals(Object o1) {
		if (o1 instanceof GameObjectWrapper) {
			if (o1.getClass().getName().equals(getClass().getName())) {
				return gameObject.equals(((GameObjectWrapper)o1).gameObject);
			}
		}
		return false;
	}
	/**
	 * @return	Returns the underlying object
	 */
	public GameObject getGameObject() {
		return gameObject;
	}
	public GameData getGameData() {
		return gameObject.getGameData();
	}
	public abstract String getBlockName();
	
	public void clear(String key) {
		gameObject.removeAttribute(getBlockName(),key);
	}
	public void setName(String name) {
		getGameObject().setName(name);
	}
	
	public String getName() {
		return getGameObject().getName();
	}
	
	
	/**
	 * Utility method for extracting the int value
	 */
	public int getInt(String key) {
		return gameObject.getInt(getBlockName(),key);
	}
	/**
	 * Utility method for extracting the int value
	 */
	public double getDouble(String key) {
		String val = gameObject.getAttribute(getBlockName(),key);
		if (val!=null) {
			try {
				return Double.valueOf(val).doubleValue();
			}
			catch(NumberFormatException ex) {
			}
		}
		return 0.0;
	}
	/**
	 * Utility method for extracting the String value
	 */
	public String getString(String key) {
		return gameObject.getAttribute(getBlockName(),key);
	}
	public boolean getBoolean(String key) {
		return gameObject.hasAttribute(getBlockName(),key);
	}
	public ArrayList<String> getList(String key) {
		return gameObject.getAttributeList(getBlockName(),key);
	}
	public int getListCount(String key) {
		ArrayList<String> list = getList(key);
		return list==null?0:list.size();
	}
	public void setInt(String key,int val) {
		gameObject.setAttribute(getBlockName(),key,String.valueOf(val));
	}
	public void setDouble(String key,double val) {
		gameObject.setAttribute(getBlockName(),key,String.valueOf(val));
	}
	public void setString(String key,String val) {
		if (val==null) {
			gameObject.removeAttribute(getBlockName(),key);
		}
		else {
			gameObject.setAttribute(getBlockName(),key,val);
		}
	}
	public void setBoolean(String key,boolean val) {
		if (val) {
			gameObject.setAttribute(getBlockName(),key);
		}
		else {
			gameObject.removeAttribute(getBlockName(),key);
		}
	}
	public void setList(String key,ArrayList<String> in) {
		gameObject.setAttributeList(getBlockName(),key,in);
	}
	public void addListItem(String key,String val) {
		gameObject.addAttributeListItem(getBlockName(),key,val);
	}
	public boolean removeListItem(String key,String val) {
		boolean ret = false;
		ArrayList<String> list = getList(key);
		if (list!=null && list.contains(val)) {
			list = new ArrayList<>(list);
			ret = list.remove(val);
			setList(key,list);
		}
		return ret;
	}
	public boolean hasListItem(String key,String val) {
		return gameObject.hasAttributeListItem(getBlockName(),key,val);
	}
	public void removeAttribute(String key) {
		if (gameObject.hasAttribute(getBlockName(),key)) {
			gameObject.removeAttribute(getBlockName(),key);
		}
	}
	
	/////////////////////////////////
	
//	private static HashMap cache = null;
//	public static Wrapper getCachedWrapper(String name,GameData data) {
//		return null;
//	}
//	public static void cacheWrapper(String name,GameData data,Wrapper wrapper) {
//		if (cache==null) {
//			cache = new HashMap();
//		}
//		String key = name+data.dataid;
//		cache.put(key,wrapper);
//	}
//	public static void clearCaches() {
//		if (cache!=null) {
//			cache.clear();
//			cache = null;
//		}
//	}
}