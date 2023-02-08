package com.robin.magic_realm.RealmBattle;

import java.util.Comparator;

import com.robin.magic_realm.components.RealmComponent;

public class TargetIndexComparator implements Comparator<RealmComponent> {
	public int compare(RealmComponent rc1,RealmComponent rc2) {
		int ret = 0;
		ret = rc1.getTargetIndex()-rc2.getTargetIndex();
		return ret;
	}
}