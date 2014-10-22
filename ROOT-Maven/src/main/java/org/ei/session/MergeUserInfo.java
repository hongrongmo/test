package org.ei.session;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.validator.GenericValidator;



/**
 * This object will hold information about the state of the merge for a user.  This should be stored in session and be 
 * where all the merge info is stored. 
 * 
 *
 */
public class MergeUserInfo implements Serializable{
	
	private static final long serialVersionUID = 4800641322475017984L;
	
	//popup messages
    public static final String MERGE_SUCCESS_POPUP = "mergeSuccess";
    public static final String MERGE_NEEDED = "resendEmail";
    public static final String MERGE_EMAIL_SENT = "sendEmailSuccess";
    public static final String MERGE_EMAILS_NOT_MATCH = "emailsDoNotMatch";
    public static final String MERGE_ALREADY_MERGED = "alreadyMerged";
    public static final String MERGE_NEEDS_REGISTERED = "needsregistered";
    public static final String MERGE_NEEDS_ACCESS = "needsaccess";
	//merged flag yes
	public static final String MERGED_YES = "Y";
	//merged flag no. Needs merged
	public static final String MERGED_NO = "N";
	//merged flag opt out. User was given the chance to merge but decided to contiue with 
	//a new profile
	public static final String MERGED_OPT_OUT = "O";
	//merge flag ignored. User merged but didn't pick this profile
	public static final String MERGED_IGNORE = "I";
	
	//email that the token was sent to.
	private String mergeEmail = "";
	
	//This is whether the current user needs merged or not. 
	private boolean needsMerged = false;

	//if within a session the user asked for a email to be sent to them to 
	//merge their account but still wants to stay in ev without getting the 
	//resend email popup
	private boolean emailSent = false;
	
	private String emailSentTo = "";
	
	private boolean popupSeen = false;
	
	//We have a couple of messages that will need to be displayed to the user during a merge.
	//these should be stored in the properties file and this should be key to them
	private String popupMsg = "";
	
	//the token the user used to start the merge. This needs to be in session if the person logs in during the
	//merge flow so that we can go to the right path
	private String mergeToken;
	
	//List of usernames in the customer system.
	//used to remind the user what their username is.
	private List<String> csUserNames;
	
	private boolean legacyInstitutionCredentials;
	
	public MergeUserInfo() {
		
	}

	
	public boolean getNeedsMerged() {
		return needsMerged;
	}

	public void setNeedsMerged(boolean needsMerged) {
		this.needsMerged = needsMerged;
	}

	public boolean getEmailSent() {
		return emailSent;
	}

	public void setEmailSent(boolean emailSent) {
		this.emailSent = emailSent;
	}
	public void setEmailSentTo(String emailSentTo) {
		this.emailSentTo = emailSentTo;
	}
	public String getEmailSentTo(){
		return this.emailSentTo;
	}
	public String getPopupMsg() {
		return popupMsg;
	}

	public void setPopupMsg(String popupMsg) {
		this.popupMsg = popupMsg;
	}

	public String getMergeToken() {
		return mergeToken;
	}

	public void setMergeToken(String mergeToken) {
		this.mergeToken = mergeToken;
	}
	
	public String getPopupMsgAndRemove(){
		String popmsg = this.popupMsg;
		this.popupMsg = "";
		return popmsg;
	}


	public boolean getPopupSeen() {
		return popupSeen;
	}


	public void setPopupSeen(boolean popupSeen) {
		this.popupSeen = popupSeen;
	}
	
	public boolean getShowPopup(){
		
		if((needsMerged && !emailSent && !popupSeen && GenericValidator.isBlankOrNull(mergeToken)) || !GenericValidator.isBlankOrNull(popupMsg)){
			return true;
		}
		
		return false;
	}


	public List<String> getCsUserNames() {
		return csUserNames;
	}

	public List<String> getCsUserNamesAndClear() {
		List<String> ret = csUserNames;
		
		csUserNames = null;
		
		return ret;
	}

	public void setCsUserNames(List<String> csUserNames) {
		this.csUserNames = csUserNames;
	}


	public String getMergeEmail() {
		return mergeEmail;
	}


	public void setMergeEmail(String mergeEmail) {
		this.mergeEmail = mergeEmail;
	}


	public boolean isLegacyInstitutionCredentials() {
		return legacyInstitutionCredentials;
	}


	public void setLegacyInstitutionCredentials(boolean legacyInstitutionCredentials) {
		this.legacyInstitutionCredentials = legacyInstitutionCredentials;
	}
}
