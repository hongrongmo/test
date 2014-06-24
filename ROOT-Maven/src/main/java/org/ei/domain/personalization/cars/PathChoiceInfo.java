package org.ei.domain.personalization.cars;

import java.io.Serializable;


public class PathChoiceInfo  implements Serializable {
	
	private static final long serialVersionUID = 4659999933796516015L;
	private String pathChoiceNumber;
	private String pathChoiceDescription;
	
	
	public String getPathChoiceNumber() {
		return pathChoiceNumber;
	}
	
	public void setPathChoiceNumber(String pathChoiceNumber) {
		this.pathChoiceNumber = pathChoiceNumber;
	}
	
	public String getPathChoiceDescription() {
		return pathChoiceDescription;
	}
	
	public void setPathChoiceDescription(String pathChoiceDescription) {
		this.pathChoiceDescription = pathChoiceDescription;
	}
	
}


