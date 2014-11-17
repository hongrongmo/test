package org.ei.thesaurus;

import java.util.Properties;

public class ThesaurusAction{

	public static Properties ACTIONS;
	public static final String SEARCH = "thesTermSearch";
	public static final String BROWSE = "thesBrowse";
	public static final String FULL = "thesFullRec";

	static
	{

		ACTIONS = new Properties();
		ACTIONS.setProperty(SEARCH,"Search");
		ACTIONS.setProperty(BROWSE,"Browse");
		ACTIONS.setProperty(FULL,"Exact Term");
	}




}