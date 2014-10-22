package org.ei.biz.personalization.cars;

import java.io.Serializable;


public class QuickLinkInfo implements Serializable {

	private static final long serialVersionUID = -6840258453667508774L;
	private String linkLabel;
	private String linkMouseOverText;
	private String linkURL;



	public String getLinkLabel() {
		return linkLabel;
	}

	public void setLinkLabel(String linkLabel) {
		this.linkLabel = linkLabel;
	}


	public String getLinkMouseOverText() {
		return linkMouseOverText;
	}


	public void setLinkMouseOverText(String linkMouseOverText) {
		this.linkMouseOverText = linkMouseOverText;
	}


	public String getLinkURL() {
		return linkURL;
	}


	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}
}

