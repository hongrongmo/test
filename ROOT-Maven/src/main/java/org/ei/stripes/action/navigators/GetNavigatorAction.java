package org.ei.stripes.action.navigators;

import java.io.StringReader;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wink.json4j.JSONArray;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.ISecuredAction;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.domain.navigators.NavigatorStandaloneSearch;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SearchException;
import org.ei.session.UserSession;
import org.ei.stripes.action.EVActionBean;


@UrlBinding("/getnav.json")
public class GetNavigatorAction extends EVActionBean implements ISecuredAction {

	private String searchId = "";
	private String field = "";
	private final static Logger log4j = Logger.getLogger(GetNavigatorAction.class);


	@DefaultHandler
	public Resolution getNavigators() throws InfrastructureException, SearchException {

		JSONArray result = new JSONArray();
		if(StringUtils.isNotBlank(searchId) && StringUtils.isNotBlank(field)){
			NavigatorStandaloneSearch nss = new NavigatorStandaloneSearch(searchId, field);
			UserSession usersession = getContext().getUserSession();
			result = nss.runSearch(usersession.getSessionid(), usersession.getCartridge(), usersession.getRecordsPerPage());
		}

		return new StreamingResolution("text/json", new StringReader(result.toString()));
	}
    @Override
    public IAccessControl getAccessControl() {
        //
        // Authenticate and terminate requests do NOT require previous
        // authentication!
        //

        return new NoAuthAccessControl();

    }

	public String getSearchId() {
		return searchId;
	}


	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}


	public String getField() {
		return field;
	}


	public void setField(String field) {
		this.field = field;
	}

}
