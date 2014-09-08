package org.ei.struts.framework;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.tiles.TilesRequestProcessor;
/**
 * A customized RequestProcessor that checks the user's preferred Locale
 * from the request each time. If a Locale is not in the session or
 * the one in the session doesn't match the request, the Locale in the
 * request is set to the session.
 */
public class SecureRequestProcessor extends TilesRequestProcessor {

    /**
     * Commons Logging instance.
     */
    protected static Log log = LogFactory.getLog("SecureRequestProcessor");


   /**
     * If this action is protected by security roles, make sure that the
     * current user possesses at least one of them.  Return <code>true</code>
     * to continue normal processing, or <code>false</code> if an appropriate
     * response has been created and processing should terminate.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param mapping The mapping we are using
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    protected boolean processRoles(HttpServletRequest request,
                                   HttpServletResponse response,
                                   ActionMapping mapping)
        throws IOException, ServletException {

		// TODO:
		return true;
    }

}