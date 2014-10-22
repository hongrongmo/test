package org.ei.stripes.action.widget.search;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.http.HttpHeaders;
import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.WorldAccessControl;
import org.ei.stripes.action.EVActionBean;

/**
 * This class has a dual purpose.  It serves to render the Quick, Expert and eBook search forms
 * and it also handles search submissions from these forms.  This is necessary since the
 * Stripes framework use of form objects needs an action bean tied
 * @author harovetm
 *
 */
@UrlBinding("/widget/search.url")
public class SearchWidgetAction extends EVActionBean { //implements IBizBean {

    private final static Logger log4j = Logger.getLogger(SearchWidgetAction.class);
    private final static String SIZE_LG = "lg";
    private final static String SIZE_sm = "sm";
    //mask representation of the databases selected
    private String database;
    private String size = "lg";
    @Override
	public IAccessControl getAccessControl() {
		return new WorldAccessControl();
	}

    @DefaultHandler
    @DontValidate
    public Resolution quicksearch()  {
    	context.getRequest();
        setRoom(ROOM.search);

        if(!GenericValidator.isBlankOrNull(database)){
        	log4j.info("Search Widget display with Database : " + database);
        }else{
        	log4j.info("Search Widget display with NO database ");
        }

        // Forward on
        return new ForwardResolution("/WEB-INF/pages/widget/searchWidget.jsp");
    }

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getReferrer(){
		return getContext().getRequest().getHeader(HttpHeaders.REFERER);
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}





}
