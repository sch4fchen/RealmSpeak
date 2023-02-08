package com.robin.magic_realm.MRCBuilder;

public class FieldPos {

	public FieldPos(int i, int j) {
		row = i;
		col = j;
	}

	public int getRow() {
		return row - 1;
	}

	public int getCol() {
		return col - 1;
	}

	public int row;
	public int col;
}