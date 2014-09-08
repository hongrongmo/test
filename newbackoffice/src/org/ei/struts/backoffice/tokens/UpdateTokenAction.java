/*
* $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/tokens/UpdateTokenAction.java-arc   1.0   Jan 14 2008 17:11:12   johna  $
* $Revision:   1.0  $
* $Date:   Jan 14 2008 17:11:12  $
*
* ====================================================================
*/


package org.ei.struts.backoffice.tokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.actions.LookupDispatchAction;
import org.apache.struts.util.MessageResources;

import org.ei.struts.backoffice.Constants;

import org.ei.ppd.token.TokenWrapper;

/**
* Implementation of <strong>Action</strong> that populates an instance of
* <code>ContactForm</code> from the profile of the currently logged on
* User (if any).
*
* @author $Author:   johna  $
* @version $Revision:   1.0  $
*/

public final class UpdateTokenAction extends LookupDispatchAction {


  private static Log log = LogFactory.getLog(UpdateTokenAction.class);

    protected Map getKeyMethodMap() {
        Map map = new HashMap();

        map.put("action.resettoken","reset");
        map.put("action.killtoken","kill");
        map.put("action.extendtokentime","extendtokentime");
        map.put("action.extendtokenlife","extendtokenlife");
        map.put("action.cancel","cancel");

        return map;
    }

   // --------------------------------------------------------- Public Methods


    private ActionForward finalize(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response)
        throws Exception {

        DynaActionForm tokenform = (DynaActionForm) form;
        String tokenId = (String) tokenform.get("tokenId");
        if(tokenId != null) {
            Collection allAuths = new ArrayList();
            allAuths.add((new TokenWrapper()).getToken(tokenId));
            request.setAttribute("allAuths", allAuths);
        }

		HttpSession session = request.getSession();

		// Remove the obsolete form bean
		if (mapping.getAttribute() != null) {
			if ("request".equalsIgnoreCase(mapping.getScope())) {
				request.removeAttribute(mapping.getName());
			} else {
				session.removeAttribute(mapping.getName());
			}
		}

        return (mapping.findForward("success"));
    }

    public ActionForward extendtokentime(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response)
        throws Exception {

        DynaActionForm tokenform = (DynaActionForm) form;

        long result = 0;

        Integer hours = (Integer) tokenform.get("hours");
        String tokenId = (String) tokenform.get("tokenId");
    	if((tokenId != null) && (hours != null)) {
            result = (new TokenWrapper()).extendTokenTime(tokenId,hours.intValue());
    	}

        log.info(" extend Time  " + tokenId + " hours " + hours);


        return finalize(mapping, form, request, response);
    }

    public ActionForward extendtokenlife(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response)
        throws Exception {

        DynaActionForm tokenform = (DynaActionForm) form;

        long result = 0;

        Integer days = (Integer) tokenform.get("days");
        String tokenId = (String) tokenform.get("tokenId");
    	if((tokenId != null) && (days != null)) {
            result = (new TokenWrapper()).extendTokenLife(tokenId,days.intValue());
    	}


        log.info(" extend Life " + tokenId + " days " + days);
        return finalize(mapping, form, request, response);
    }

    public ActionForward kill(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response)
        throws Exception {

        DynaActionForm tokenform = (DynaActionForm) form;

        int result = 0;

        String tokenId = (String) tokenform.get("tokenId");
    	if(tokenId != null) {
            result = (new TokenWrapper()).killToken(tokenId);
    	}

        log.info(" kill " + tokenId + " result " + result);
        return finalize(mapping, form, request, response);
    }

    public ActionForward reset(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response)
        throws Exception {

        DynaActionForm tokenform = (DynaActionForm) form;

        long result = 0;

        String tokenId = (String) tokenform.get("tokenId");
    	if(tokenId != null) {
            result = (new TokenWrapper()).resetToken(tokenId);
    	}

        log.info(" reset " + tokenId + " result " + result);

        return finalize(mapping, form, request, response);
    }

    public ActionForward cancel(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response)
        throws Exception {

        return finalize(mapping, form, request, response);
    }


}
