package org.ei.domain.bundles;

import java.util.ListResourceBundle;

public class EvResources extends ListResourceBundle  {

	// ResourceBundle myResources = ResourceBundle.getBundle("EvResources", currentLocale);

	static final Object[][] contents = {
		// LOCALIZE THIS
			{"s1", "Welcome---"},         
	  // END OF MATERIAL TO LOCALIZE

	};

	protected Object[][] getContents() {
		// TODO Auto-generated method stub
		return contents;
	}
}
