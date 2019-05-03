package de.jeffclan.utils;

public class TypeMatchPositionPair {
	
	String typeMatch;
	String formattedPosition;
	public String getTypeMatch() {
		return typeMatch;
	}

	public short getPosition() {
		return position;
	}
	
	public String getFormattedPosition() {
		return formattedPosition;
	}

	short position;
	
	public TypeMatchPositionPair(String typeMatch,short position) {
		this.typeMatch=typeMatch;
		this.position=position;
		this.formattedPosition=Utils.shortToStringWithLeadingZeroes(position);
	}

}
