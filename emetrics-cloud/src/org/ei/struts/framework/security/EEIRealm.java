package org.ei.struts.framework.security;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * EEIRealm - EEI Realm implementation.
 *
 */
public class EEIRealm extends EEIRealmBase {

   public static final String PRINCIPAL_SESSION_KEY = EEIRealm.class.getName() + ".PRINCIPAL";

   private String THE_USERNAME = null;
   private String THE_PASSWORD = null;
   private List THE_ROLES;

    /**
     * Construct a new EEIRealm.
     *
     */
   public EEIRealm() {

   }


    /**
     * Construct a new EEIRealm for the specified username and password.
     *
     * @param name The username of the user represented by this Principal
     * @param password Credentials used to authenticate this user
     * @param roles List of roles (must be Strings) possessed by this user
     */
   public EEIRealm(String username, String password, List roles) {
	   this.THE_USERNAME = username;
	   this.THE_PASSWORD = password;
	   this.THE_ROLES = roles;

   }

   /**
    * Return a short name for this Realm implementation.
    */
   protected String getName() {
      return null;
   }

   /**
    * Return the password associated with the given principal's user name.
    */
   protected String getPassword(String username) {
      return (THE_USERNAME.equals(username) ? THE_PASSWORD : null);
   }

   /**
    * Return the Principal associated with the given user name.
    */
   protected Principal getPrincipal(String username) {
      if (THE_USERNAME.equals(username)) {

         return new ApplicationPrincipal(THE_USERNAME, THE_PASSWORD, THE_ROLES);
      } else {
         return null;
      }
   }



   /**
    * Get a Principal object for the current user.
    */
   public Principal getUserPrincipal(HttpServletRequest request) {
      return (Principal) request.getSession().getAttribute(PRINCIPAL_SESSION_KEY);
   }

   /**
    * Set the username of the current user.
    * WARNING: Calling this method will set the user for this session -- authenticate the user before calling
    * this method.
    *
    * @param principal the user Principal object
    */
   public void setUserPrincipal(Principal principal, HttpServletRequest request) {
      request.getSession().setAttribute(PRINCIPAL_SESSION_KEY, principal);
   }

}