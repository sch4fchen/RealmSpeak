package com.robin.hexmap;

public class Rotation {
	public static final int POSITIONS = 6;
	
	public static final int CLOCKWISE = 1;
	public static final int COUNTERCLOCKWISE = -1;
	
	private int position;
	
	public Rotation() {
		this(0);
	}
	public Rotation(int position) {
		this.position = position;
	}
	public void rotate(int direction) {
		if (direction==CLOCKWISE || direction==COUNTERCLOCKWISE) {
			position += direction;
			if (position<0) {
				position = POSITIONS - 1;
			}
			else {
				position %= POSITIONS;
			}
		}
		else {
			throw new IllegalArgumentException("direction "+direction+" is not a valid argument for Rotation.rotate(int direction)");
		}
	}
	public int getCWTurns() {
		return position;
	}
}