/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/tokens/FindTokenAction.java-arc   1.0   Jan 14 2008 17:11:12   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:11:12  $
 *
 */
package org.ei.struts.backoffice.tokens;

import java.lang.reflect.InvocationTargetException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
//import org.apache.struts.action.ActionMessage;
//import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.actions.LookupDispatchAction;
import org.apache.struts.util.MessageResources;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ei.struts.backoffice.Constants;

import org.ei.ppd.token.TokenAuth;
import org.ei.ppd.token.TokenWrapper;
import org.ei.ppd.user.User;
import org.ei.ppd.user.UserDatabase;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $
 */
public final class FindTokenAction extends LookupDispatchAction {

  private static Log log = LogFactory.getLog(FindTokenAction.class);

    protected Map getKeyMethodMap() {
        Map map = new HashMap();

        map.put("action.search","search");
        map.put("action.cancel","cancel");
        map.put("action.sendmail","sendemail");

        return map;
    }

    public ActionForward search(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response)
        throws Exception {
		// Extract attributes and parameters we will need
		Locale locale = getLocale(request);
		MessageResources messages = getResources(request);
		HttpSession session = request.getSession();

        Collection auths = new ArrayList();

        DynaActionForm tokenform = (DynaActionForm) form;
        String tokenId = (String) tokenform.get("tokenId");

        if((tokenId != null) && !tokenId.equals(Constants.EMPTY_STRING))
        {
            Pattern emailPattern = Pattern.compile("@");
            if(!((Matcher) emailPattern.matcher(tokenId)).find())
            {
                TokenAuth auth = (new TokenWrapper()).getToken(tokenId);
                if(auth != null)
                {
                    auths.add(auth);
                }
            }
            else
            {
                User user = (new UserDatabase()).findUser(tokenId);
                if(user != null)
                {
                    auths = (new TokenWrapper()).getAuthorizationHistory(user.getUserId());
                }
            }

            if(auths.isEmpty())
            {
        		ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.no.results"));
    			saveErrors(request, errors);

                return (mapping.findForward("success"));
            }

            request.setAttribute("allAuths", auths);
        }

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



    public ActionForward sendemail(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response)
        throws Exception {


        return search(mapping, form, request, response);
    }

    public ActionForward cancel(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response)
        throws Exception {

        return search(mapping, form, request, response);
    }

}