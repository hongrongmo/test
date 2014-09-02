package org.ei.struts.backoffice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.ei.struts.backoffice.user.User;


/**
 * An abstract Action class that all Backoffice Action classes can extend.
 */
abstract public class BackOfficeBaseAction extends Action {


  public ActionForward execute( ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response ) throws Exception{

    if(isLoggedIn(request)) {
      // It just calls a worker method that contains the real execute logic
      return executeAction( mapping,form,request,response);
    }
    else {
        return (mapping.findForward("login"));
    }
  }

  abstract public ActionForward executeAction( ActionMapping mapping,
                                               ActionForm form,
                                               HttpServletRequest request,
                                               HttpServletResponse response)
    throws Exception;


  public boolean isLoggedIn( HttpServletRequest request ){
    User user = null;
    HttpSession session = request.getSession(true);

    if(session != null) {
      user = (User) session.getAttribute(Constants.USER_KEY);
    }
    return ( user != null );
  }


}
