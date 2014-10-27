package org.ei.books.collections;

import java.util.LinkedList;
import java.util.List;

public class Sec extends ReferexCollection {
	private String name = "SEC";
	protected Sec() {
	}

	protected Sec(String name) {
		this.name = name;
	}

  public int getColMask() {
    return 64;
  }
  public int getSortOrder() { return 5;}
	public String getAbbrev() {
		// TODO Auto-generated method stub
		return "SEC";
	}

	public String getDisplayName() {
		// TODO Auto-generated method stub
		return "Security & Networking";
	}

	public String getShortname() {
		// TODO Auto-generated method stub
		return "Security";
	}

	  public List populateSubjects(boolean che, boolean chestar) {
	      List subjs = new LinkedList();

          return subjs;
      }
}
