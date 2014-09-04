package org.ei.struts.backoffice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.LookupDispatchAction;
import org.ei.struts.backoffice.user.User;


/**
 * An abstract Action class that all Backoffice Action classes can extend.
 */
abstract public class BackOfficeBaseLookupDispatchAction extends LookupDispatchAction {


  public ActionForward dispatchMethod( ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response, String name ) throws Exception{

    if(isLoggedIn(request)) {
      // It just calls a worker method that contains the real execute logic
      return super.dispatchMethod(mapping, form, request, response, name);
    }
    else {
        return (mapping.findForward("login"));
    }
  }


  public boolean isLoggedIn( HttpServletRequest request ){
    User user = null;
    HttpSession session = request.getSession(true);

    if(session != null) {
      user = (User) session.getAttribute(Constants.USER_KEY);
    }
    return ( user != null );
  }


}
