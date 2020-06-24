package de.jeff_media.ChestSort;

import de.jeff_media.ChestSort.utils.TypeMatchPositionPair;

public class ChestSortCategory implements Comparable<ChestSortCategory>{

	// Represents a sorting category
	// Includes an array of strings called typeMatches
	// A typeMatch is like a regular expression, but it only supports * as
	// placeholders
	// e.g. "DIRT" will match the typeMatch "dirt"
	// "COARSE_DIRT" will not match the typeMatch "dirt"
	// "COARSE_DIRT" will match the typeMatch "*dirt"

	final String name;
	boolean sticky = false;
	final TypeMatchPositionPair[] typeMatches;

	ChestSortCategory(String name, TypeMatchPositionPair[] typeMatchPositionPairs) {
		this.name = name;
		this.typeMatches = typeMatchPositionPairs;
	}
	
	void setSticky() {
		this.sticky=true;
	}
	
	boolean isSticky() {
		return this.sticky;
	}
	

	// Checks whether a the given itemname fits into this category and returns the line number. 0 means not found
	short matches(String itemname) {

		boolean asteriskBefore = false;
		boolean asteriskAfter = false;

		// Very, very simple wildcard checks
		for (TypeMatchPositionPair typeMatchPositionPair : typeMatches) {
			String typeMatch = typeMatchPositionPair.getTypeMatch();
			if (typeMatch.startsWith("*")) {
				asteriskBefore = true;
				typeMatch = typeMatch.substring(1);
			}
			if (typeMatch.endsWith("*")) {
				asteriskAfter = true;
				typeMatch = typeMatch.substring(0, typeMatch.length() - 1);
			}

			if (!asteriskBefore && !asteriskAfter) {
				if (itemname.equalsIgnoreCase(typeMatch)) {
					return typeMatchPositionPair.getPosition();
				}
			} else if (asteriskBefore && asteriskAfter) {
				if (itemname.contains(typeMatch)) {
					return typeMatchPositionPair.getPosition();
				}
			} else if (asteriskBefore && !asteriskAfter) {
				if (itemname.endsWith(typeMatch)) {
					return typeMatchPositionPair.getPosition();
				}
			} else {
				if (itemname.startsWith(typeMatch)) {
					return typeMatchPositionPair.getPosition();
				}
			}
		}

		return 0;
	}
	
	public int compareTo(ChestSortCategory compareCategory) {
		return this.name.compareTo(compareCategory.name);
	}

}
