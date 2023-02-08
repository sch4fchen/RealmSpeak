package com.robin.general.util;

public class LogicalDirection {
	// Square directions (S,W,E,N)
	public static final int SQUARE_INDEX_SOUTH = 0;
	public static final int SQUARE_INDEX_WEST = 1;
	public static final int SQUARE_INDEX_EAST = 2;
	public static final int SQUARE_INDEX_NORTH = 3;
	public static final int[] SQUARE_DX = {0,-1,1,0};
	public static final int[] SQUARE_DY = {1,0,0,-1};
	
	// Full direction
	public static final int[] DIAG_DX = {-1,0,1,-1,0,1,-1,0,1};
	public static final int[] DIAG_DY = {1,1,1,0,0,0,-1,-1,-1};
	public static final int[] DIAG_CENTER_FIRST_DX = {0,-1,0,1,-1,1,-1,0,1};
	public static final int[] DIAG_CENTER_FIRST_DY = {0,1,1,1,0,0,-1,-1,-1};
	public static final int[] DIAG_NO_CENTER_DX = {-1,0,1,-1,1,-1,0,1};
	public static final int[] DIAG_NO_CENTER_DY = {1,1,1,0,0,-1,-1,-1};
	
	public static final int BINARY_SOUTH = 1;
	public static final int BINARY_WEST = 2;
	public static final int BINARY_EAST = 4;
	public static final int BINARY_NORTH = 8;
	
}