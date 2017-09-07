package org.ei.stripes.action.lindahall;

import java.util.List;

import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.Validate;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.ContactInfo;
import org.ei.domain.LhlUserInfo;
import org.ei.service.CSWebService;
import org.ei.service.CSWebServiceImpl;
import org.ei.session.UserPreferences;

import com.elsevier.webservices.schemas.csas.constants.types.v7.AuthenticationStatusCodeType;
import com.elsevier.webservices.schemas.csas.constants.types.v7.UserProfileStatusCodeType;
import com.elsevier.webservices.schemas.csas.types.v13.AuthenticateUserRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.AuthenticationResponseStatusType;
import com.elsevier.webservices.schemas.csas.types.v13.PathChoiceInfoType;
import com.elsevier.webservices.schemas.csas.types.v13.UserDataType;
import com.elsevier.webservices.schemas.csas.types.v13.UserInfoType;
import com.elsevier.webservices.schemas.csas.types.v13.UserProfileRespPayloadType;

/**
 * This class attempts to authenticate a user against the Linda Hall Library DDS (Document
 * Delivery Service).  This is a special authentication outside of the EV product
 * authentication.  The following needs to happen to make this work:
 * 
 * 1) A user profile needs to be added to the CS account that needs LHL access.
 * 2) There are 2 Text Zones setup for LHL:  Linda Hall DDS password and Linda Hall DDS
 * username.  Accounts that need access should set the username to the same as on the 
 * associated profile.  
 * 3) The authenticate() method below will retrieve the username from the Text Zone and
 * add the incoming password from the form to do the authentication.
 *   
 * @author harovetm
 *
 */
@UrlBinding("/lindahall/auth.url")
public class LindaHallAuthAction extends LindaHallAction {
    private final static Logger log4j = Logger.getLogger(LindaHallAuthAction.class);

    @Validate(required=true)
    private String lhlpassword;

    @DefaultHandler
    @DontValidate
    public Resolution auth() {
        LhlUserInfo userinfo = (LhlUserInfo) context.getRequest().getSession().getAttribute(LHL_SESSION_USERINFO);
        if (userinfo != null) {
            // Show order form
            return buildOrderFormResolution();
        }
        return new ForwardResolution("/WEB-INF/pages/customer/lindahall/auth.jsp");
    }
    
    /**
     * Handles authentication
     * @return
     */
    @HandlesEvent("authsubmit")
    @Validate
    public Resolution authsubmit() {

        // Authenticate the user against the Linda Hall Account in CS
        try {
            boolean auth = authenticate();
            if (auth) {
                // Show order form
                return buildOrderFormResolution();
            } else {
                // Add error message
                context.getValidationErrors().add("lhlpassword", new LocalizableError("org.ei.stripes.action.lindahall.LindaHallAuthAction.lhlpassword.equalTo"));
            }
        } catch (Exception e) {
            // Add error message
            context.getValidationErrors().add("lhlpassword", new SimpleError("Unable to authenticate at this time."));
        }
        
        return new ForwardResolution("/WEB-INF/pages/customer/lindahall/auth.jsp");
    }    

    CSWebService service = new CSWebServiceImpl();

    /**
     * Use CSAS to authenticate user
     * @return
     * @throws Exception 
     */
    private boolean authenticate() throws Exception {
        AuthenticationResponseStatusType status;
        CSWebService service = new CSWebServiceImpl();

        // Get the Linda Hall user name from a Text Zone
        Map<String,String> textzones = context.getUserSession().getUserTextZones();
        String tz_lhlusername = textzones.get(UserPreferences.TZ_LHL_USERNAME);
        if (GenericValidator.isBlankOrNull(tz_lhlusername)) {
            throw new IllegalArgumentException("No Linda Hall username setup for this account!");
        }
        
        log4j.info("Attempting to authenticate Linda Hall user: " + tz_lhlusername);

        // Authenticate via CSAS - DO NOT USE THE CURRENT AUTHTOKEN!!!  If you do, the current
        // EV user will be switched to the Linda Hall user profile.
        AuthenticateUserRespPayloadType payload = service.authenticateLindaHall(tz_lhlusername, lhlpassword);
        status = payload.getStatus();

        if (AuthenticationStatusCodeType.OK.equals(status.getStatusCode())) {
            // If there is Path Choice info something is wrong in account setup!
            List<PathChoiceInfoType> paths = payload.getPathChoiceInfo();
            if (!paths.isEmpty()) {
                log4j.error("Path Choice encountered trying to authenticate Linda Hall user: " + tz_lhlusername);
                return false;
            }
            
            // Now get the profile to get the rest of the information
            UserProfileRespPayloadType profilepayload = service.getProfile(Integer.parseInt(payload.getUserInfo().getWebUserId()));
            if (UserProfileStatusCodeType.OK.equals(profilepayload.getStatus().getStatusCode())) {
                
                UserInfoType authuserinfo = payload.getUserInfo();
                UserDataType profileuserdata = profilepayload.getUserData();

                // Store the user info
                LhlUserInfo lhluserinfo = new LhlUserInfo();
                lhluserinfo.setAccountNumber(authuserinfo.getAccountNumber());
                lhluserinfo.setAccountName(authuserinfo.getAccountName());
                lhluserinfo.setDeptName(authuserinfo.getDepartmentName());
                
                ContactInfo contactinfo = new ContactInfo();
                contactinfo.setAccountNumber(authuserinfo.getAccountNumber());
                if (GenericValidator.isBlankOrNull(profileuserdata.getRegistrationData().getOrganizationName())) {
                    contactinfo.setCompany(authuserinfo.getAccountName());
                } else {
                    contactinfo.setCompany(profileuserdata.getRegistrationData().getOrganizationName());
                }
                contactinfo.setAddress1(profileuserdata.getRegistrationData().getAddress().getAddress1());
                contactinfo.setAddress2(profileuserdata.getRegistrationData().getAddress().getAddress2());
                contactinfo.setCity(profileuserdata.getRegistrationData().getAddress().getCity());
                contactinfo.setState(profileuserdata.getRegistrationData().getAddress().getStateProvince());
                contactinfo.setZip(profileuserdata.getRegistrationData().getAddress().getPostalCode());
                contactinfo.setCountry(profileuserdata.getRegistrationData().getAddress().getCountry());
                
                contactinfo.setFaxNumber(profileuserdata.getRegistrationData().getPerson().getFaxNumber());
                contactinfo.setPhoneNumber(profileuserdata.getRegistrationData().getPerson().getPhoneNumber());
                contactinfo.setUserEmail(profileuserdata.getRegistrationData().getPerson().getEmailAddress());
                contactinfo.setUserFirstName(profileuserdata.getRegistrationData().getPerson().getFirstName());
                contactinfo.setUserLastName(profileuserdata.getRegistrationData().getPerson().getLastName());
                contactinfo.setUserLastName(profileuserdata.getRegistrationData().getPerson().getLastName());
                
                lhluserinfo.setContactInfo(contactinfo);
                
                context.getRequest().getSession().setAttribute(LHL_SESSION_USERINFO, lhluserinfo);
                
                // Terminate the CARS session to help CS clean up
                service.terminate(payload.getAuthToken());
                
            } else {
                log4j.error("Unable to retrieve Linda Hall user profile: " + tz_lhlusername + ", response = " + profilepayload.getStatus().getStatusText());
                return false;
            }
            
            return true;
            
        } else {
            log4j.error("Unable to authenticate Linda Hall user: " + tz_lhlusername + ", response = " + status.getStatusText());
            return false;
        }
    }
    public String getLhlpassword() {
        return this.lhlpassword;
    }

    public void setLhlpassword(String lhlpassword) {
        this.lhlpassword = lhlpassword;
    }

}
