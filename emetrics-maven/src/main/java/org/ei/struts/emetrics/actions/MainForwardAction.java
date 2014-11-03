/*
 * $Author:   JohnM  $
 * $Revision:   1.3  $
 * $Date:   Dec 03 2004 14:54:08  $
 *
*/

package org.ei.struts.emetrics.actions;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.ei.struts.framework.FrameworkBaseAction;

public class MainForwardAction extends FrameworkBaseAction {

  /**
  * Commons Logging instance.
  */
  protected static Log log = LogFactory.getLog(MainForwardAction.class);

    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param servlet The ActionServlet making this request
     * @param mapping The ActionMapping used to select this instance
     * @param actionForm The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public ActionForward executeAction(
				 ActionMapping mapping,
				 ActionForm form,
				 HttpServletRequest request,
				 HttpServletResponse response)
	throws IOException, ServletException
    {
		String path = mapping.getParameter();

	log.info(" path is " + path);
	
		RequestDispatcher rd = servlet.getServletContext().getRequestDispatcher(path);
		HttpSession session = request.getSession(false);
	
		//	Forward control to the specified resource
		rd.forward(request, response);

		// Tell the controller servlet that the response has been created
		return (null);
    }
 }
