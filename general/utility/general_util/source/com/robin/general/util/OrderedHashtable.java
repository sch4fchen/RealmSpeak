package com.robin.general.util;

import java.util.*;

/**
 * A Hashtable class that guarantees the order of the keys and values added
 * to it.
 */
public class OrderedHashtable<T,U> extends Hashtable<T,U> {
	protected ArrayList<T> orderedKeys = new ArrayList<>();
	
	// overrides
	public void clear() {
		super.clear();
		orderedKeys.clear();
	}
	public U put(T key,U value) {
		U ret = super.put(key,value);
		if (orderedKeys==null) {
			orderedKeys = new ArrayList<>();
		}
		if (!orderedKeys.contains(key)) {
			orderedKeys.add(key);
		}
		return ret;
	}
	public void putAll(Map map) {
		for (int i=0;i<orderedKeys.size();i++) {
			T key = orderedKeys.get(i);
			U val = (U)map.get(key);
			put(key,val);
		}
	}
	public Set<T> keySet() {
		return new LinkedHashSet<>(orderedKeys);
	}
	public Collection<U> values() {
		ArrayList<U> vals = new ArrayList<>();
		if (orderedKeys==null) {
			orderedKeys = new ArrayList<>();
		}
		for (T key:orderedKeys) {
			vals.add(get(key));
		}
		return vals;
	}
	public U remove(Object key) {
		U ret = super.remove(key);
		orderedKeys.remove(key);
		return ret;
	}
	
	// custom	
	public Object remove(int index) {
		String key = (String)orderedKeys.get(index);
		return remove(key);
	}
	public Object getKey(int index) {
		return orderedKeys.get(index);
	}
	public Object getValue(int index) {
		return get(getKey(index));
	}
	public int indexOf(Object key) {
		return orderedKeys.indexOf(key);
	}
	public ArrayList<T> orderedKeys() {
		return orderedKeys;
	}
	public Object insert(int index,T key,U val) {
		ArrayList<T> newOrderedKeys = new ArrayList<>();
		for (int i=0;i<orderedKeys.size();i++) {
			if (i==index) {
				newOrderedKeys.add(key);
			}
			newOrderedKeys.add(orderedKeys.get(i));
		}
		orderedKeys = newOrderedKeys;
		return this.put(key,val);
	}
	public Object replace(int index,T key,U val) {
		ArrayList<T> newOrderedKeys = new ArrayList<>();
		for (int i=0;i<orderedKeys.size();i++) {
			String currentKey = (String)orderedKeys.get(i);
			if (i==index) {
				newOrderedKeys.add(key);
				remove(currentKey);
			}
			else {
				newOrderedKeys.add(orderedKeys.get(i));
			}
		}
		orderedKeys = newOrderedKeys;
		return this.put(key,val);
	}
	public void sortKeys(Comparator<T> comparator) {
		Collections.sort(orderedKeys, comparator);
	}
}