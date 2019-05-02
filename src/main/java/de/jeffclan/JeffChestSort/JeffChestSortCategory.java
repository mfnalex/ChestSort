package de.jeffclan.JeffChestSort;

import de.jeffclan.utils.TypeMatchPositionPair;

public class JeffChestSortCategory {

	// Represents a sorting category
	// Includes an array of strings called typeMatches
	// A typeMatch is like a regular expression, but it only supports * as
	// placeholders
	// e.g. "DIRT" will match the typeMatch "dirt"
	// "COARSE_DIRT" will not match the typeMatch "dirt"
	// "COARSE_DIRT" will match the typeMatch "*dirt"

	String name;
	TypeMatchPositionPair[] typeMatches;

	JeffChestSortCategory(String name, TypeMatchPositionPair[] typeMatchPositionPairs) {
		this.name = name;
		this.typeMatches = typeMatchPositionPairs;
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

			if (asteriskBefore == false && asteriskAfter == false) {
				if (itemname.equalsIgnoreCase(typeMatch)) {
					return typeMatchPositionPair.getPosition();
				}
			} else if (asteriskBefore == true && asteriskAfter == true) {
				if (itemname.contains(typeMatch)) {
					return typeMatchPositionPair.getPosition();
				}
			} else if (asteriskBefore == true && asteriskAfter == false) {
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

}
