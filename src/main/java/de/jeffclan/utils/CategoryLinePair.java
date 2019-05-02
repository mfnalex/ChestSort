package de.jeffclan.utils;


public class CategoryLinePair {
	String categoryName;
	short position;
	
	public CategoryLinePair(String categoryName,short position) {
		this.categoryName=categoryName;
		this.position=position;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public short getPosition() {
		return position;
	}
}
