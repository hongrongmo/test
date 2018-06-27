package org.ei.books.collections;

import java.util.LinkedList;
import java.util.List;

public class Che extends ReferexCollection {
	private String name = "CHE";
	protected Che() {
	}

	protected Che(String name) {
		this.name = name;
	}

  public int getColMask() {
    return 4;
  }

  public int getSortOrder() { return 2;}

	public String getAbbrev() {
		// TODO Auto-generated method stub
		return this.name;
	}

	public String getDisplayName() {
		// TODO Auto-generated method stub
		return "Chemical, Petrochemical & Process";
	}

	public String getShortname() {
		// TODO Auto-generated method stub
		return "Chemical";
	}

	  public List populateSubjects(boolean che, boolean chestar) {
	      List ches = new LinkedList();


	    return ches;
	   }

}
