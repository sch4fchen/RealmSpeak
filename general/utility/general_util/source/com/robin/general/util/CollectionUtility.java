package com.robin.general.util;

import java.util.Collection;

public class CollectionUtility {
	public static <T> boolean containsAny(Collection<T> collection, Collection<T> query) {
		if (collection == null || collection.isEmpty() || query == null || query.isEmpty())
			return false;
		for (T val : query) {
			if (collection.contains(val))
				return true;
		}
		return false;
	}
}