package de.jeffclan.JeffChestSort;

public class JeffChestSortCategory {

	// Represents a sorting category
	// Includes an array of strings called typeMatches
	// A typeMatch is like a regular expression, but it only supports * as
	// placeholders
	// e.g. "DIRT" will match the typeMatch "dirt"
	// "COARSE_DIRT" will not match the typeMatch "dirt"
	// "COARSE_DIRT" will match the typeMatch "*dirt"

	String name;
	String[] typeMatches;

	JeffChestSortCategory(String name, String[] typeMatches) {
		this.name = name;
		this.typeMatches = typeMatches;
	}

	boolean matches(String itemname) {

		boolean asteriskBefore = false;
		boolean asteriskAfter = false;

		for (String typeMatch : typeMatches) {

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

					return true;
				}
			} else if (asteriskBefore == true && asteriskAfter == true) {
				if (itemname.contains(typeMatch)) {
					return true;
				}
			} else if (asteriskBefore == true && asteriskAfter == false) {
				if (itemname.endsWith(typeMatch)) {
					return true;
				}
			} else {
				if (itemname.startsWith(typeMatch)) {
					return true;
				}
			}
		}

		return false;
	}

}
