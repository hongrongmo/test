package org.ei.stripes.action.results;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.config.EVProperties;
import org.ei.config.JSPPathProperties;

@UrlBinding("/blog/document.url")
public class BlogThisDocumentAction extends SearchResultsAbstractAction {

	private static final Logger log4j = Logger.getLogger(BlogThisDocumentAction.class);

	private String mid;

	/**
	 * This should be open to world!
	 */
	/*
	 * TMH - per Steve Petric do NOT open this to the world!
	@Override
	public IAccessControl getAccessControl() {
		return new NoAuthAccessControl();
	}
	*/

	@DefaultHandler
	public Resolution document() {
		return new ForwardResolution("/WEB-INF/pages/customer/record/blogdocument.jsp");
	}

	public String getXMLPath() {
		return EVProperties.getJSPPath(JSPPathProperties.BLOG_THIS_PATH);
	}

	//
	//
	// GETTERS / SETTERS
	//
	//

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}
}
