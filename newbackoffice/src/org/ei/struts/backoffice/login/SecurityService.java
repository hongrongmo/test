package org.ei.struts.backoffice.login;

import org.ei.struts.backoffice.user.User;
import org.ei.struts.backoffice.user.UserDatabase;
import org.ei.struts.backoffice.Constants;

public class SecurityService implements IAuthentication {

  public User login(String username, String password)
    throws Exception {

    User user = (new UserDatabase()).findUserByUsername(username);
    if((user != null)  && user.getPassword().equals(password) && (user.getIsEnabled() == Constants.ENABLED)) {
      // do nothing
    }
    else {
      // If the login method is invalid, throw an InvalidLoginException.
      // Create a msg that can be inserted into a log file.
      String msg = "Invalid Login Attempt by " + username;
      throw new Exception( msg );
    }

    return user;
  }
}
