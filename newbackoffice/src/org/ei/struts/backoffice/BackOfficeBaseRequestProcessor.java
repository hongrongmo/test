/*
 * Created on Jan 29, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.backoffice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.RequestProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.MDC;

import org.ei.struts.backoffice.user.User;
/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BackOfficeBaseRequestProcessor extends RequestProcessor {

  private static Log log = LogFactory.getLog("SECURITY");

  /* (non-Javadoc)
   * @see org.apache.struts.action.RequestProcessor#processRoles(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.struts.action.ActionMapping)
   */
  protected boolean processRoles(
    HttpServletRequest request,
    HttpServletResponse response,
    ActionMapping mapping)
    throws IOException, ServletException {

    if(mapping.getRoles() != null) {
      boolean allowed = false;
      HttpSession session = request.getSession(true);
      if(session != null) {
        User user = (User) session.getAttribute(Constants.USER_KEY);
        if(user != null) {
          Perl5Util perl5 = new Perl5Util();
          Collection roles = new ArrayList();
          perl5.split(roles,"#,#", mapping.getRoles());
          Collection userroles = Arrays.asList(user.getRoles());
          allowed = userroles.containsAll(roles);
          if(allowed)
          {
            MDC.put("remoteAddress",request.getRemoteAddr());
            MDC.put("userInfo",user.toString());
            log.warn(getIDParameters(request) + " - " + mapping);
          }
        }
      }
      if(!allowed) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      }
      return allowed;
    }
    else {
      return super.processRoles(request, response, mapping);
    }


  }

  private String getIDParameters(HttpServletRequest request)
  {
    StringBuffer result = new StringBuffer();
    Enumeration pnames = request.getParameterNames();
    while (pnames.hasMoreElements()) {
      String pname = (String) pnames.nextElement();
      if(pname.endsWith("ID"))
      {
        String pvalues[] = request.getParameterValues(pname);
        result.append(pname);
        result.append("=");
        for (int i = 0; i < pvalues.length; i++) {
          if (i > 0) {
            result.append(", ");
          }
          result.append(pvalues[i]);
        }
        if(pnames.hasMoreElements())
        {
          result.append(" ");
        }
      }
    }
    return result.toString();
  }
}

