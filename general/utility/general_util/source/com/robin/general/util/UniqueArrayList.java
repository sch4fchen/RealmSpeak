package com.robin.general.util;

import java.util.ArrayList;

/**
 * This isn't a complete implementation, but works for what I want right now.
 */
public class UniqueArrayList<T> extends ArrayList<T> {
	private boolean allowNull;
	
	public UniqueArrayList() {
		this(true);
	}
	public UniqueArrayList(boolean allowNull) {
		super();
		this.allowNull = allowNull;
	}
	public boolean add(T obj) {
		if ((allowNull || obj!=null) && !contains(obj)){
			return super.add(obj);
		}
		return false;
	}
}