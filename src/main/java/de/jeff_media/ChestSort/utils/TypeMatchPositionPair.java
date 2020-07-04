package de.jeff_media.ChestSort.utils;

public class TypeMatchPositionPair {
	
	final String typeMatch;
	final String formattedPosition;
	final boolean sticky;
	
	public String getTypeMatch() {
		return typeMatch;
	}
	
	public String getTypeMatchWithSticky() {
		if(sticky) return getTypeMatch() + "~" + getFormattedPosition();
		return getTypeMatch();
	}

	public short getPosition() {
		return position;
	}
	
	public String getFormattedPosition() {
		return formattedPosition;
	}

	final short position;
	
	public TypeMatchPositionPair(String typeMatch,short position) {
		this(typeMatch,position,false);
	}

	public TypeMatchPositionPair(String typeMatch, short position, boolean appendLineNumber) {
		this.typeMatch=typeMatch;
		this.position=position;
		this.formattedPosition=Utils.shortToStringWithLeadingZeroes(position);
		this.sticky=appendLineNumber;
	}

}
