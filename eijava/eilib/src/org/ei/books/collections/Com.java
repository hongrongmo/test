package org.ei.books.collections;

import java.util.LinkedList;
import java.util.List;

public class Com extends ReferexCollection {

	protected Com() {
	}

  public int getColMask() {
    return 32;
  }

  public int getSortOrder() { return 4;}
	public String getAbbrev() {
		// TODO Auto-generated method stub
		return "COM";
	}

	public String getDisplayName() {
		// TODO Auto-generated method stub
		return "Computing";
	}

	public String getShortname() {
		// TODO Auto-generated method stub
		return "Computing";
	}

      public List populateSubjects(boolean che, boolean chestar) {
          List subjs = new LinkedList();


        return subjs;
       }

}
