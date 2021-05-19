package de.jeff_media.chestsort.utils;


public class CategoryLinePair {
	final String categoryName;
	final String formattedPosition;
	final boolean sticky;
	final short position;
	
	public CategoryLinePair(String categoryName,short position) {
		this(categoryName,position,false);
	}
	
	public CategoryLinePair(String categoryName,short position,boolean sticky) {
		this.categoryName=categoryName;
		this.formattedPosition=Utils.shortToStringWithLeadingZeroes(position);
		this.position=position;
		this.sticky=sticky;
	}
	
	public String getCategoryNameSticky() {
		if(sticky) return getCategoryName() + "~" + getFormattedPosition();
		return getCategoryName();
	}

	public String getCategoryName() {
		return categoryName;
	}

	public String getFormattedPosition() {
		return formattedPosition;
	}
	
	public int getPosition() {
		return position;
	}
}
