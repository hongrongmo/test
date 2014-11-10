package org.ei.books.collections;

import java.util.LinkedList;
import java.util.List;


public class Ele extends ReferexCollection {
	private String name = "ELE";
	protected Ele() {
	}

	protected Ele(String name) {
		this.name = name;
	}

  public int getColMask() {
    return 2;
  }

  public int getSortOrder() { return 1;}

	public String getAbbrev() {
		// TODO Auto-generated method stub
		return this.name;
	}

	public String getDisplayName() {
		// TODO Auto-generated method stub
		return "Electronics & Electrical";
	}

	public String getShortname() {
		// TODO Auto-generated method stub
		return "Electronics";
	}
	public List populateSubjects(boolean ele, boolean elestar) {
	      List subjs = new LinkedList();


	      return subjs;

	    }

}
