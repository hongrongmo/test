package org.ei.books.collections;

import java.util.LinkedList;
import java.util.List;

public class Sec extends ReferexCollection {

	protected Sec() {
	}

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
