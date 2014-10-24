package org.ei.biz.personalization.cars;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class ScirusSourceInfo implements Serializable {

	private static final long serialVersionUID = -4564105281141440578L;
	private String tabText;
	private List<String> abbreviation= new ArrayList<String>();


	public List<String> getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(List<String> abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getTabText() {
		return tabText;
	}

	public void setTabText(String tabText) {
		this.tabText = tabText;
	}
}

