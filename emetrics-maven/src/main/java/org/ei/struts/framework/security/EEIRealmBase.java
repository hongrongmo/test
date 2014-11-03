package org.ei.struts.framework.security;

import java.security.Principal;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

/**
 * EEIRealm - EEI Realm implementation.
 *
 */
public class EEIRealmBase implements IRealm {


   public static final String PRINCIPAL_SESSION_KEY = EEIRealmBase.class.getName() + ".PRINCIPAL";

   private String THE_USERNAME = "username";
   private String THE_PASSWORD = "password";
   private String THE_ROLE = "inthisrole";




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
     *
     * @param username Username of the Principal to look up
     */
   protected Principal getPrincipal(String username) {
      if (THE_USERNAME.equals(username)) {
         ArrayList roleList = new ArrayList();
         roleList.add(THE_ROLE);
         return new ApplicationPrincipal(THE_USERNAME, THE_PASSWORD, roleList);
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


    /**
     * Return the Principal associated with the specified username and
     * credentials, if there is one; otherwise return <code>null</code>.
     *
     * @param username Username of the Principal to look up
     * @param credentials Password or other credentials to use in
     *  authenticating this username
     */
    public Principal authenticate(String username, String credentials) {

        String serverCredentials = getPassword(username);

        if ( (serverCredentials == null)
             || (!serverCredentials.equals(credentials)) )
            return null;

        return getPrincipal(username);

    }


    /**
     * Return <code>true</code> if the specified Principal has the specified
     * security role, within the context of this Realm; otherwise return
     * <code>false</code>.  This method can be overridden by Realm
     * implementations, but the default is adequate when an instance of
     * <code>ApplicationPrincipal</code> is used to represent authenticated
     * Principals from this Realm.
     *
     * @param principal Principal for whom the role is to be checked
     * @param role Security role to be checked
     */
    public boolean isUserInRole(Principal principal, String role) {

        if ((principal == null) || (role == null) ||
            !(principal instanceof ApplicationPrincipal))
            return (false);
        ApplicationPrincipal ap = (ApplicationPrincipal) principal;
        boolean result = ap.hasRole(role);

        return (result);

    }


}