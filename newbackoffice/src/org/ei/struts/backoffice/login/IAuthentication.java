package org.ei.struts.backoffice.login;

import org.ei.struts.backoffice.user.User;
/**
 * Provides methods that the banking security service should implement.
 */
public interface IAuthentication {
  /**
   * The login method is called when a user wants to log in to
   * the online banking application.
   * @param accessNumber- The account access number.
   * @param pin- The account private id number.
   * @returns a DTO object representing the user's personal data.
   * @throws InvalidLoginException if the credentials are invalid.
   */
  public User login( String username, String password)
    throws Exception;
}
