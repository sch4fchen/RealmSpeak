package com.robin.general.util;

import java.util.*;

/**
 * A utility class for handling and populating lists of objects hashed by key.  Multiple objects can be added
 * for a single key.  Note that the lists are uniqued by default, unless specified otherwise.
 */
public class HashLists<K,T> implements Map {
    
	private boolean forceUnique;
    private Hashtable<K,ArrayList<T>> hash;
    
	public HashLists() {
		this(true);
	}
	public HashLists(boolean forceUnique) {
		this.forceUnique = forceUnique;
	    hash = new Hashtable<>();
	}
	public Object put(Object key,Object val) {
	    ArrayList<T> list = getList(key);
	    if (list==null) {
	        list = new ArrayList<>();
	        hash.put((K)key,list);
	    }
	    if (!forceUnique || !list.contains(val)) {
		    list.add((T)val);
	    }
	    return null;
	}
	public void putList(K key,ArrayList<T> list) {
		hash.put(key,list);
	}
	public Object get(Object key) {
	    return hash.get(key);
	}
	public ArrayList<T> getList(Object key) {
	    return hash.get(key);
	}
	public ArrayList<T> getListAsNew(Object key) {
		ArrayList<T> list = getList(key);
		if (list!=null) {
			return new ArrayList<>(list);
		}
		return null;
	}
	public int size() {
	    return hash.size();
	}
	public void clear() {
	    hash.clear();
	}
	public boolean containsKey(Object key) {
	    return hash.containsKey(key);
	}
	public boolean containsValue(Object val) {
	    for (ArrayList<T> list : hash.values()) {
	        if (list.contains(val)) {
	            return true;
	        }
	    }
	    return false;
	}
	public boolean isEmpty() {
	    return hash.isEmpty();
	}
	public Set<K> keySet() {
	    return hash.keySet();
	}
	public void putAll(Map map) {
	    for (Object key : map.keySet()) {
	        ArrayList list = getList(key);
	        Object val = map.get(key);
	        if (val instanceof Collection) {
	            list.addAll((Collection)val);
	        }
	        else {
	            list.add(val);
	        }
	    }
	}
	public Set entrySet() {
	    return hash.entrySet();
	}
	public Object remove(Object key) {
	    return hash.remove(key);
	}
	public void removeKeyValue(Object key,Object val) {
		if (key!=null) {
			ArrayList list = getList(key);
			if (list!=null && list.contains(val)) {
				list.remove(val);
				if (list.isEmpty()) {
					remove(key);
				}
			}
		}
	}
	public void removeValue(Object val) {
	    for (ArrayList<T> list : hash.values()) {
	        if (list.contains(val)) {
	            list.remove(val);
	        }
	    }
	}
	public Collection<ArrayList<T>> values() {
	    return hash.values();
	}
}