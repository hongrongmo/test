package org.ei.books.collections;

import java.util.LinkedList;
import java.util.List;

public class Mat extends ReferexCollection {

	private String name="MAT";
	protected Mat() {
	}

	protected Mat(String name) {
		this.name = name;
	}


  public int getColMask() {
    return 1;
  }
  public int getSortOrder() { return 3;}
	public String getAbbrev() {
		// TODO Auto-generated method stub
		return this.name;
	}

	public String getDisplayName() {
		// TODO Auto-generated method stub
		return "Materials & Mechanical";
	}

	public String getShortname() {
		// TODO Auto-generated method stub
		return "Materials";
	}
	public List populateSubjects(boolean mat, boolean matstar) {
    List subjs = new LinkedList();
    return subjs;
  }

}
