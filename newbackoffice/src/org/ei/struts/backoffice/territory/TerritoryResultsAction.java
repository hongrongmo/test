package org.ei.struts.backoffice.territory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.DynaActionForm;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.ei.struts.backoffice.BackOfficeBaseAction;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.customer.SearchBroker;
/**
 * Implements the logic to authenticate a user for the storefront application.
 */
public class TerritoryResultsAction extends BackOfficeBaseAction {

  /**
  * Commons Logging instance.
  */
  protected static Log log = LogFactory.getLog("TerritoryResultsAction");

  /**
   * Called by the controller when the a user attempts to login to the
   * storefront application.
   */

  public ActionForward executeAction( ActionMapping mapping,
                                      ActionForm form,
                                      HttpServletRequest request,
                                      HttpServletResponse response )
      throws Exception {

    // Extract attributes and parameters we will need
    Locale locale = getLocale(request);
    MessageResources messages = getResources(request);
    HttpSession session = request.getSession();

    // cast the form into the form which this action is associated
    DynaActionForm territoryform = (DynaActionForm)form;

    // because DynaActionForm does not have getters/setters method,
    // we need to retrieve object values by using get() with the name of form property 
    String territoryid = (String) territoryform.get("territoryid");

    // Validate the transactional control token
    ActionErrors errors = new ActionErrors();

//    if (!isTokenValid(request)) {
//      log.trace(" Checking transactional control token");
//      errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.transaction.token"));
//    }
//    resetToken(request);

    Collection customerView = new ArrayList();

    if(Territories.getTerritory(territoryid) != null)
    {
        SearchBroker searchBroker = new SearchBroker();
    
        log.info(" done.");
    
    	customerView = searchBroker.getTerritoryView(territoryid);
    
        // srchform.setSearchResults(customerView);
        territoryform.set("searchResults", customerView); 
        territoryform.set("territory", Territories.getTerritory(territoryid)); 
    }    
    log.info(" done.");
    
    if(customerView.size() == 0)  
    {

        errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.no.results"));
        saveErrors(request, errors);

        return (mapping.getInputForward());
    }

    return (mapping.findForward(Tokens.SUCCESS));
  }
}