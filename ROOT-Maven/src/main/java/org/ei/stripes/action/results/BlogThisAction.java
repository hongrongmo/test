package org.ei.stripes.action.results;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.domain.Abstract;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DocID;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.EIDoc;
import org.ei.domain.Keys;
import org.ei.domain.MultiDatabaseDocBuilder;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.action.SystemMessage;
import org.ei.stripes.util.HttpRequestUtil;

@UrlBinding("/blog/{$event}.url")
public class BlogThisAction extends EVActionBean {
	private static final Logger log4j = Logger.getLogger(BlogThisAction.class);

	private String mid;

	// Legacy request parameters
	private String MID;
	private String DATABASE;

	/**
	 * This should be open to world!
	 */
	@Override
	public IAccessControl getAccessControl() {
		return new NoAuthAccessControl();
	}

	@HandlesEvent("open")
	public Resolution display() {
		HttpServletRequest request = context.getRequest();
		request.setAttribute("server", HttpRequestUtil.getServerBaseAddress(request,true,false));

		// Build an entire EIDoc object just to get the title. Ugh...
		try {
			List<DocID> entryList = new ArrayList<DocID>();
			DocID id = new DocID(0, this.mid, DatabaseConfig.getInstance().getDatabase(database));
			entryList.add(id);
			DocumentBuilder builder = new MultiDatabaseDocBuilder();
			List<EIDoc> docList = (List<EIDoc>) builder.buildPage(entryList, Abstract.ABSTRACT_FORMAT);
			EIDoc entry = (EIDoc) docList.get(0);
			// Try to get TITLE first
			if (entry.getElementDataMap().containsKey(Keys.TITLE)) {
				request.setAttribute("title", entry.getElementDataMap().get(Keys.TITLE).getElementData()[0]);
			} else if (entry.getElementDataMap().containsKey(Keys.BOOK_TITLE)) {
				request.setAttribute("title", entry.getElementDataMap().get(Keys.BOOK_TITLE).getElementData()[0]);
			} else {
				request.setAttribute("title", "No title found");
			}
		} catch (Exception e) {
			log4j.error("Unable render open blog for mid: '" + this.mid +"'", e);
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}

		return new ForwardResolution("/WEB-INF/pages/customer/record/openblog.jsp");
	}

	@HandlesEvent("legacyopenblog")
	public Resolution legacyopenblog() {
		// Convert "MID" and "DATABASE" values
		HttpServletRequest request = context.getRequest();
		if (GenericValidator.isBlankOrNull(this.MID) || GenericValidator.isBlankOrNull(this.DATABASE)) {
			log4j.error("Unable to redirect legacy request: " + request.getRequestURL().toString());
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		} else {
			return new RedirectResolution("/blog/open.url?mid=" + this.MID + "&database=" + this.DATABASE);
		}
	}

	@HandlesEvent("legacyblogdocument")
	public Resolution legacyblogdocument() {
		// Convert "MID" and "DATABASE" values
		HttpServletRequest request = context.getRequest();
		if (GenericValidator.isBlankOrNull(this.MID) || GenericValidator.isBlankOrNull(this.DATABASE)) {
			log4j.error("Unable to redirect legacy request: " + request.getRequestURL().toString());
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		} else {
			return new RedirectResolution("/blog/document.url?mid=" + this.MID + "&database=" + this.DATABASE);
		}
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

	public String getMID() {
		return MID;
	}

	public void setMID(String mID) {
		MID = mID;
	}

	public String getDATABASE() {
		return DATABASE;
	}

	public void setDATABASE(String dATABASE) {
		DATABASE = dATABASE;
	}

}
