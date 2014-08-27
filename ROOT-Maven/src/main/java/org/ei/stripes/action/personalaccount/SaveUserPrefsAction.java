package org.ei.stripes.action.personalaccount;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.ISecuredAction;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.domain.personalization.IEVWebUser;
import org.ei.domain.personalization.UserPrefs;
import org.ei.exception.InfrastructureException;
import org.ei.session.UserSession;
import org.ei.stripes.action.EVActionBean;

@UrlBinding("/customer/userprefs.url")
public class SaveUserPrefsAction extends EVActionBean implements ISecuredAction {
    private final static Logger log4j = Logger.getLogger(SaveUserPrefsAction.class);

    private UserPrefs userprefs;
    private UserPrefs currentuserprefs;
    private int resultsPerPage;
    private String sortOrder;
    private String dlOutput;
    private String dlFormat;
    private String dlLocation;
    private String highlight;
    private String highlightBackground = "false";
	private boolean showPreview;
	private List<String> hightlght_colors = Arrays.asList("#ff8200","#2babe2","#158c75", "#000000");
    @Override
    public IAccessControl getAccessControl() {
        return new NoAuthAccessControl();

    }

    /**
     * Default handler for generic reactivate request
     *
     * @return Resolution
     * @throws Exception
     */
    @HandlesEvent("save")
    @DontValidate
    public Resolution saveUserPrefs() {
        IEVWebUser user = getContext().getUserSession().getUser();
        try{
	        if (user.isIndividuallyAuthenticated()) {
		        UserPrefs userPrefs = user.getUserPrefs();

		        userPrefs.setUserid(user.getWebUserId());
		        userPrefs.setResultsPerPage(this.resultsPerPage);
		        userPrefs.setDlOutput(this.dlOutput);
		        userPrefs.setDlFormat(this.dlFormat);
		        userPrefs.setDlLocation(this.dlLocation);
		        userPrefs.setSort(this.sortOrder);
		        userPrefs.setShowPreview(this.showPreview);
		        userPrefs.setHighlightBackground(Boolean.valueOf(this.highlightBackground));

		        if(GenericValidator.isBlankOrNull(highlight) || !hightlght_colors.contains(highlight)){
		        	this.highlight = hightlght_colors.get(0);
		        }
		        userPrefs.setHighlight(this.highlight);

		        userPrefs.save();
		        user.setUserPrefs(userPrefs);

		        UserSession userSession = context.getUserSession();
		        userSession.setRecordsPerPage(Integer.toString(userPrefs.getResultsPerPage()));
		        context.updateUserSession(userSession);
	        }else{
	        	log4j.info("User is Not individually authenticated!");
	        	getContext().getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
	        	return new StreamingResolution("text", "");
	        }
        }catch(Exception e){
        	log4j.error("User Preferences could not be saved!");
        	e.printStackTrace();
        	getContext().getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
        	return new StreamingResolution("text", "");
        }
        setRoom(ROOM.blank);


        return new StreamingResolution("text", "");
    }
    @HandlesEvent("savedlprefs")
    @DontValidate
    public Resolution saveDownloadPrefs(){
    	 IEVWebUser user = getContext().getUserSession().getUser();
         try{
 	        if (user.isIndividuallyAuthenticated()) {
 		        UserPrefs userPrefs = user.getUserPrefs();

 		        userPrefs.setUserid(user.getWebUserId());
 		        userPrefs.setDlOutput(this.dlOutput);
 		        userPrefs.setDlFormat(this.dlFormat);
 		        userPrefs.setDlLocation(this.dlLocation);
 		        userPrefs.save();
 		        user.setUserPrefs(userPrefs);

 		        UserSession userSession = context.getUserSession();
 		        userSession.setRecordsPerPage(Integer.toString(userPrefs.getResultsPerPage()));
 		        context.updateUserSession(userSession);
 	        }else{
 	        	log4j.info("User is Not individually authenticated!");
 	        	getContext().getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
 	        	return new StreamingResolution("text", "");
 	        }
         }catch(Exception e){
         	log4j.error("User Preferences could not be saved!");
         	e.printStackTrace();
         	getContext().getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
         	return new StreamingResolution("text", "");
         }
         setRoom(ROOM.blank);


         return new StreamingResolution("text", "");
    }

    /**
     * default handler will just go to the editprefs.jsp
     *
     * @return
     * @throws InfrastructureException
     */
    @DefaultHandler
    public Resolution display() throws InfrastructureException {
        IEVWebUser user = getContext().getUserSession().getUser();

        if (user.isIndividuallyAuthenticated()) {
            this.userprefs = user.getUserPrefs();
            if (this.userprefs == null) {
                this.userprefs = new UserPrefs(user.getWebUserId());
                user.setUserPrefs(this.userprefs);
            }
        }

        this.currentuserprefs = this.userprefs;

        return new ForwardResolution("/WEB-INF/pages/customer/prefs/editprefs.jsp");
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public String getDlOutput() {
        return dlOutput;
    }

    public void setDlOutput(String dlOutput) {
        this.dlOutput = dlOutput;
    }

    public String getDlFormat() {
        return dlFormat;
    }

    public void setDlFormat(String dlFormat) {
        this.dlFormat = dlFormat;
    }

    public boolean getShowPreview() {
        return showPreview;
    }

    public void setShowPreview(boolean showPreview) {
        this.showPreview = showPreview;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public UserPrefs getUserprefs() {
        return userprefs;
    }

    public UserPrefs getCurrentuserprefs() {
        return currentuserprefs;
    }

	public String getHighlight() {
		return highlight;
	}

	public void setHighlight(String highlight) {
		this.highlight = highlight;
	}

	public String getDlLocation() {
		return dlLocation;
	}

	public void setDlLocation(String dlLocation) {
		this.dlLocation = dlLocation;
	}
    public String getHighlightBackground() {
		return highlightBackground;
	}

	public void setHighlightBackground(String highlightBackground) {
		this.highlightBackground = highlightBackground;
	}

}
