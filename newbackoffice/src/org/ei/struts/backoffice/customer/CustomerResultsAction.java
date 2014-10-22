package org.ei.struts.backoffice.customer;

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
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.ei.struts.backoffice.BackOfficeBaseAction;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;

/**
 * Implements the logic to authenticate a user for the storefront application.
 */
public class CustomerResultsAction extends BackOfficeBaseAction {

  /**
  * Commons Logging instance.
  */
  protected static Log log = LogFactory.getLog("CustomerResultsAction");

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
    SearchCustomerForm srchform = (SearchCustomerForm) form;


    // Validate the transactional control token
    ActionErrors errors = new ActionErrors();

//    if (!isTokenValid(request)) {
//      log.trace(" Checking transactional control token");
//      errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.transaction.token"));
//    }
//    resetToken(request);

    String customerName = srchform.getCustomerName();
  	String customerID = srchform.getCustomerID();
  	String contractID = srchform.getContractID();
  	String startFromDay = srchform.getStartFromDay();
  	String startFromYear= srchform.getStartFromYear();
  	String startToDay = srchform.getStartToDay();
  	String startToYear = srchform.getStartToYear();
  	String endFromDay = srchform.getEndFromDay();
  	String endFromYear = srchform.getEndFromYear();
  	String endToDay = srchform.getEndToDay();
  	String endToYear = srchform.getEndToYear();
  	String active = srchform.getActive();
  	String action = srchform.getAction();
  	String contactName = srchform.getContactName();
    String startFromMonth =null;
    String startToMonth = null;
    String endFromMonth = null;
    String endToMonth = null;
    if((srchform.getStartFromMonth()!=null)&&(!srchform.getStartFromMonth().equals("")))
  		startFromMonth = Integer.toString(Integer.parseInt(srchform.getStartFromMonth()) + 1);
  	if((srchform.getStartToMonth()!=null)&&(!srchform.getStartToMonth().equals("")))
  		startToMonth = Integer.toString(Integer.parseInt(srchform.getStartToMonth()) + 1);
  	if((srchform.getEndFromMonth()!=null)&&(!srchform.getEndFromMonth().equals("")))
  		endFromMonth = Integer.toString(Integer.parseInt(srchform.getEndFromMonth()) + 1);
  	if((srchform.getEndToMonth()!=null)&&(!srchform.getEndToMonth().equals("")))
  		endToMonth = Integer.toString(Integer.parseInt(srchform.getEndToMonth()) + 1);

  	String[] salesRegion = srchform.getSalesRegion();
  	String[] customerType = srchform.getCustomerType();
  	String[] contractType = srchform.getContractType();
  	String[] products = srchform.getProducts();
  	String[] accessType = srchform.getAccessType();
  	String[] status = srchform.getStatus();

    SearchBroker searchBroker = new SearchBroker();

    Collection customerView = new ArrayList();
    Perl5Util perl = new Perl5Util();


    if(contactName.length() < 1)
    {
		customerView = searchBroker.getCustomerView(accessType,status,customerName,salesRegion,customerType,contractType,products,action,active,startFromDay,startFromMonth,startFromYear,startToDay,startToMonth,startToYear,endFromDay,endFromMonth,endFromYear,endToDay,endToMonth,endToYear);
  	}
  	else
  	{
		if(perl.match("/^\\d+\\./",contactName))
		{
			contactName=Constants.cleanIpAddress(contactName);
			customerView = searchBroker.getCustomerByIP(contactName);
			log.info(" IP ADDRESS SEARCH!! " + contactName);
		}
		else
		{
		  customerView = searchBroker.getCustomerByContact(contactName);
	  	}
	}

    if(customerView.isEmpty()) {
      if(contactName.length() < 1) {
	      if(customerName.length() > 0) {
	        customerView = searchBroker.getPossibleCustomerView(customerName,salesRegion,customerType,contractType,products,action,active,startFromDay,startFromMonth,startFromYear,startToDay,startToMonth,startToYear,endFromDay,endFromMonth,endFromYear,endToDay,endToMonth,endToYear);
	        srchform.setPossibleMatches(true);
		    }
	    }
	    else
	    {
			if(!(perl.match("/^\\d+\\./",contactName)))
			{
				contactName=Constants.cleanIpAddress(contactName);
        		customerView = searchBroker.getPossibleCustomerByContact(contactName);
        		srchform.setPossibleMatches(true);
			}
      }
    }

    srchform.setSearchResults(customerView);

    // Set a transactional control token to prevent double posting
//    if (log.isTraceEnabled()) {
//      log.trace(" Setting transactional control token");
//    }
//    saveToken(request);

    if(customerView.size() == 0)  {

      errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.no.results"));
      saveErrors(request, errors);

      return (mapping.getInputForward());
    }
    else if((customerView.size() == 1) && (srchform.getPossibleMatches() == false)){

      StringBuffer path = new StringBuffer();

      Iterator itr = customerView.iterator();
      Customer cust = (Customer) itr.next();

      path.append(mapping.findForward("editCustomer").getPath());
      path.append("&").append(Tokens.CUSTOMERID).append("=");
      path.append(cust.getCustomerID());

      return new ActionForward(path.toString());
    }
    else {
      return (mapping.findForward(Tokens.SUCCESS));
    }
  }

}