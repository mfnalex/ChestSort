package de.jeffclan.utils;


public class CategoryLinePair {
	String categoryName;
	String formattedPosition;
	short position;
	
	public CategoryLinePair(String categoryName,short position) {
		this.categoryName=categoryName;
		this.formattedPosition=Utils.shortToStringWithLeadingZeroes(position);
		this.position=position;
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
