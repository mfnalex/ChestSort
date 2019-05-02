package de.jeffclan.utils;

public class TypeMatchPositionPair {
	
	String typeMatch;
	public String getTypeMatch() {
		return typeMatch;
	}

	public short getPosition() {
		return position;
	}

	short position;
	
	public TypeMatchPositionPair(String typeMatch,short position) {
		this.typeMatch=typeMatch;
		this.position=position;
	}

}
