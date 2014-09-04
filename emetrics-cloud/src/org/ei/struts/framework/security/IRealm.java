package org.ei.struts.framework.security;

import java.security.Principal;

/**
 * IRealm - realm interface for Security system. Implement this interface to provide
 * a realm implementation against which Security system can authenticate and authorize users.
 *
 */
public interface IRealm {

   /**
    * Authenticate a user.
    *
    * @param username a username
    * @param password a plain text password, as entered by the user
    *
    * @return a Principal object representing the user if successful, false otherwise
    */
   public Principal authenticate(String username, String password);

   /**
    * Test for role membership.
    *
    * Use Principal.getName() to get the username from the principal object.
    *
    * @param principal Principal object representing a user
    * @param rolename name of a role to test for membership
    *
    * @return true if the user is in the role, false otherwise
    */
   public boolean isUserInRole(Principal principal, String rolename);
}