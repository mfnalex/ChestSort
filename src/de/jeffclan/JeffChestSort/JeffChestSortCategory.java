package de.jeffclan.JeffChestSort;

public class JeffChestSortCategory {
	
	public String name;
	public String[] typeMatches;
	
	public JeffChestSortCategory(String name, String[] typeMatches) {
		this.name=name;
		this.typeMatches = typeMatches;
	}
	
	public boolean matches(String itemname) {
		
		boolean asteriskBefore = false;
		boolean asteriskAfter = false;
		
		//System.out.println("Checking if "+itemname + " is in cat "+name);
		
		for(String typeMatch : typeMatches) {
			
			//System.out.println("  Checking if "+itemname + " matches "+typeMatch);
			
			if(typeMatch.startsWith("*")) {
				asteriskBefore = true;
				typeMatch=typeMatch.substring(1);
			}
			if(typeMatch.endsWith("*")) {
				asteriskAfter = true;
				typeMatch=typeMatch.substring(0, typeMatch.length()-1);
			}
			
			if(asteriskBefore == false && asteriskAfter == false) {
			if(itemname.equalsIgnoreCase(typeMatch)) {
				
				return true;
			}
			} else if(asteriskBefore == true && asteriskAfter == true) {
				if(itemname.contains(typeMatch)) {
					return true;
				}
			} else if(asteriskBefore == true && asteriskAfter == false) {
				if(itemname.endsWith(typeMatch)) {
					return true;
				}
			} else {
				if(itemname.startsWith(typeMatch)) {
					return true;
				}
			}
		}
		
		return false;
	}

}
