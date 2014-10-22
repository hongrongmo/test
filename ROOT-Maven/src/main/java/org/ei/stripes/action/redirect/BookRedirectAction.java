package org.ei.stripes.action.redirect;

import java.net.MalformedURLException;
import java.util.Enumeration;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.stripes.action.EVActionBean;


@UrlBinding("/bookredirect.url")
public class BookRedirectAction extends EVActionBean implements RedirectAction{

	private String isbn = "";
	private String url = "";
	public static final String SCIDIR_BOOK_URL = "http://www.sciencedirect.com/science/book/";
	public static final String BOOK_REDIR_TYPE = "book";


	private final static Logger log4j = Logger.getLogger(BookRedirectAction.class);
	@DefaultHandler
    @DontValidate
    public Resolution display() throws Exception {
        
        setRoom(ROOM.blank);
        return getResolution();
    }


    protected Resolution getResolution() throws MalformedURLException {
        if(!GenericValidator.isBlankOrNull(url)){
        	
        	return new ForwardResolution("/WEB-INF/pages/world/redirect.jsp");
    		

        }
        // go to home
         return  gotoStartPage(context.getUserSession());
    }

	public String getIsbn() {
		return isbn;
	}
	
	
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}


	@Override
	public String getRedirectURL() {

		return url;
	}



	@Override
	public String getRedirectType() {
		
		return BOOK_REDIR_TYPE;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}
	
}
