package de.jeffclan.JeffChestSort;

public class JeffChestSortCategory {
	
	public String name;
	public String[] typeMatches;
	
	public JeffChestSortCategory(String name, String[] typeMatches) {
		this.name=name;
		this.typeMatches = typeMatches;
	}
	
	public boolean matches(String itemname) {
		
		for(String typeMatch : typeMatches) {
			if(itemname.equalsIgnoreCase(typeMatch)) {
				return true;
			}
		}
		
		return false;
	}

}
