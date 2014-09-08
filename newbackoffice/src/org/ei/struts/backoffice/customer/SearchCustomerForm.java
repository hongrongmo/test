/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customer/SearchCustomerForm.java-arc   1.0   Jan 14 2008 17:10:42   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:42  $
 *
 */
package org.ei.struts.backoffice.customer;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.util.LabelValueBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.struts.validator.ValidatorForm;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.product.ProductDatabase;
import org.ei.struts.backoffice.region.RegionDatabase;
import org.ei.struts.backoffice.type.TypeDatabase;

/**
 * Form bean for the user signon page.
 */
public final class SearchCustomerForm extends ValidatorForm {

  private static Log log = LogFactory.getLog("SearchCustomerForm");

  // ----------------------------------------------------------- Properties

  private String  contactName		 = null;
  private String  customerName 		 = null;
  private String  customerID 		 = null;
  private String  contractID 		 = null;
  private String  action 			 = null;
  private String  active			 = null;

  private String  startFromMonth 	 = null;
  private String  startFromDay 		 = null;
  private String  startFromYear 	 = null;
  private String  startToMonth 		 = null;
  private String  startToDay 		 = null;
  private String  startToYear 		 = null;
  private String  endFromMonth  	 = null;
  private String  endFromDay		 = null;
  private String  endFromYear		 = null;
  private String  endToMonth  		 = null;
  private String  endToDay			 = null;
  private String  endToYear			 = null;

  private String[]  products		= new String[]{};
  private String[]  salesRegion = new String[]{};
  private String[]  customerType = new String[]{};
  private String[]  contractType	 = new String[]{};
  private String[]  status = new String[]{};
  private String[]  accessType = new String[]{};

  private boolean m_blnPossibleMatches = false;
  private Collection m_colSearchResults = new ArrayList();

  // ----------------------------------------------------------- Helper Methods
  public Collection getAllContractType() {
	  Collection colContractType = new ArrayList();
	  Iterator allContractType = Constants.getAllContractType().iterator();
	  while(allContractType.hasNext())
	  {
		  LabelValueBean contractTypeBean = (LabelValueBean)allContractType.next();
		  log.info("Value = "+(contractTypeBean).getLabel());
		  if(!(contractTypeBean.getLabel().equals(Constants.EMPTY_STRING)))
		  {
			  colContractType.add(contractTypeBean);
		  }
	  }

    return colContractType;
  }

  public Collection getAllType() {
  	return (new TypeDatabase()).getTypes();
  }

  public Collection getAllRegions() {
  	return (new RegionDatabase()).getRegions();
  }

  public Collection getAllProducts() {
    return (new ProductDatabase()).getProducts();
  }
  public Collection getAllMonths() {
	return Constants.getMonths();
  }

  public Collection getAllDays() {
     return Constants.getDays();
  }

  public Collection getAllYears() {
    return Constants.getYears();
  }

  public Collection getAllStatus() {
  	Collection colStatus = new ArrayList();
		  Iterator allStatus = Constants.getAllStatus().iterator();
		  while(allStatus.hasNext())
		  {
			  LabelValueBean statusBean = (LabelValueBean)allStatus.next();
			  if(!(statusBean.getLabel().equals(Constants.EMPTY_STRING)))
			  {
				  colStatus.add(statusBean);
			  }
		  }

    return colStatus;
  }


  public Collection getAllAccessType() {
  	Collection colAccessType = new ArrayList();
		  Iterator allAccessType = Constants.getAllAccessType().iterator();
		  while(allAccessType.hasNext())
		  {
			  LabelValueBean accessTypeBean = (LabelValueBean)allAccessType.next();
			  if(!(accessTypeBean.getLabel().equals(Constants.EMPTY_STRING)))
			  {
				  colAccessType.add(accessTypeBean);
			  }
		  }

    return colAccessType;
  }
  public Collection getSearchResults() { return m_colSearchResults; }
  public void setSearchResults(Collection searchresults) { this.m_colSearchResults = searchresults; }

  public boolean getPossibleMatches() {return (this.m_blnPossibleMatches);}
  public void setPossibleMatches(boolean possiblematches) {this.m_blnPossibleMatches = possiblematches;}

  // ----------------------------------------------------------- Get/Set

  public String getContactName() {return (this.contactName);}
  public void setContactName(String contactName) {this.contactName = contactName;}

  public void setCustomerName(String customerName) {this.customerName = customerName;}
  public String getCustomerName() {return (this.customerName);}

  public void setCustomerID(String customerID) {this.customerID = customerID;}
  public String getCustomerID() {return (this.customerID);}

  public void setContractID(String contractID) {this.contractID = contractID;}
  public String getContractID() {return (this.contractID);}

  public String getStartToDay() {return (this.startToDay);}
  public void setStartToDay(String startToDay) {this.startToDay = startToDay;}

  public String getStartToMonth() {return (this.startToMonth);}
  public void setStartToMonth(String startToMonth) {this.startToMonth = startToMonth;}

  public String getStartToYear() {return (this.startToYear);}
  public void setStartToYear(String startToYear) {this.startToYear = startToYear;}

  public String getStartFromDay() {return (this.startFromDay);}
  public void setStartFromDay(String startFromDay) {this.startFromDay = startFromDay;}

  public String getStartFromMonth() {return (this.startFromMonth);}
  public void setStartFromMonth(String startFromMonth) {this.startFromMonth = startFromMonth;}

  public String getStartFromYear() {return (this.startFromYear);}
  public void setStartFromYear(String startFromYear) {this.startFromYear = startFromYear;}

  public String getEndFromDay() {return (this.endFromDay);}
  public void setEndFromDay(String endFromDay) {this.endFromDay = endFromDay;}

  public String getEndFromMonth() {return (this.endFromMonth);}
  public void setEndFromMonth(String endFromMonth) {this.endFromMonth = endFromMonth;}

  public String getEndFromYear() {return (this.endFromYear);}
  public void setEndFromYear(String endFromYear) {this.endFromYear = endFromYear;}

  public String getEndToDay() {return (this.endToDay);}
  public void setEndToDay(String endToDay) {this.endToDay = endToDay;}

  public String getEndToMonth() {return (this.endToMonth);}
  public void setEndToMonth(String endToMonth) {this.endToMonth = endToMonth;}

  public String getEndToYear() {return (this.endToYear);}
  public void setEndToYear(String endToYear) {this.endToYear = endToYear;}

  public String getActive() {return (this.active);}
  public void setActive(String active) {this.active = active;}

  public String getAction() {return (this.action);}
  public void setAction(String action) {this.action = action;}

  public void setSalesRegion(String[] salesRegion) {this.salesRegion = salesRegion;}
  public String[] getSalesRegion() { return (this.salesRegion);}

  public String[] getCustomerType() {return (this.customerType);}
  public void setCustomerType(String[] customerType) {this.customerType = customerType;}

  public String[] getContractType() {return (this.contractType);}
  public void setContractType(String[] contractType) {this.contractType = contractType;}

  public String[] getProducts() {return (this.products);}
  public void setProducts(String[] products) {this.products = products;}

  public String[] getAccessType() {return (this.accessType);}
  public void setAccessType(String[] accessType) {this.accessType = accessType;}

  public String[] getStatus() {return (this.status);}
    public void setStatus(String[] status) {this.status = status;}


  /**
   * Reset all properties to their default values.
   *
   * @param mapping The mapping used to select this instance
   * @param request The servlet request we are processing
   */
  public void reset(ActionMapping mapping,
                    HttpServletRequest request) {


    this.customerName = null;
    this.customerID = null;
    this.contractID = null;
    this.active = null;
    this.contactName = null;
    this.startFromMonth = null;
    this.startFromDay = null;
    this.startFromYear = null;
    this.startToMonth = null;
    this.startToDay = null;
    this.startToYear = null;
    this.endFromMonth = null;
    this.endFromDay = null;
    this.endFromYear = null;
    this.endToMonth = null;
    this.endToDay = null;
    this.endToYear = null;

    this.products		= new String[]{};
    this.salesRegion = new String[]{};
    this.customerType = new String[]{};
    this.contractType	 = new String[]{};

    this.m_blnPossibleMatches = false;
    this.m_colSearchResults = new ArrayList();
  }

    /**
   * Validate the properties that have been set from this HTTP request,
   * and return an <code>ActionErrors</code> object that encapsulates any
   * validation errors that have been found.  If no errors are found, return
   * <code>null</code> or an <code>ActionErrors</code> object with no
   * recorded error messages.
   *
   * @param mapping The mapping used to select this instance
   * @param request The servlet request we are processing
   */
  public ActionErrors validate(ActionMapping mapping,
                               HttpServletRequest request) {

    // Perform validator framework validations
    ActionErrors errors = super.validate(mapping, request);

    MessageResources resources = (MessageResources) request.getAttribute( Globals.MESSAGES_KEY );
    if((action!=null)&&(action.equals("report")))
	{
		if(
			(
				((getStartFromDay() == null) || (getStartFromDay().equals(Constants.EMPTY_STRING))) ||
				((getStartFromMonth() == null) || (getStartFromMonth().equals(Constants.EMPTY_STRING))) ||
				((getStartFromYear() == null) || (getStartFromYear().equals(Constants.EMPTY_STRING)))
			)
			&&
			(
				((getStartToDay() == null) || (getStartToDay().equals(Constants.EMPTY_STRING))) ||
				((getStartToMonth() == null) || (getStartToMonth().equals(Constants.EMPTY_STRING))) ||
				((getStartToYear() == null) || (getStartToYear().equals(Constants.EMPTY_STRING)))
			)
			&&
			(
				((getEndFromDay() == null) || (getEndFromDay().equals(Constants.EMPTY_STRING))) ||
				((getEndFromMonth() == null) || (getEndFromMonth().equals(Constants.EMPTY_STRING))) ||
				((getEndFromYear() == null) || (getEndFromYear().equals(Constants.EMPTY_STRING)))
			)
			&&
			(
				((getEndToDay() == null) || (getEndToDay().equals(Constants.EMPTY_STRING))) ||
				((getEndToMonth() == null) || (getEndToMonth().equals(Constants.EMPTY_STRING))) ||
				((getEndToYear() == null) || (getEndToYear().equals(Constants.EMPTY_STRING)))
			)
		)
		{
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.required",resources.getMessage("prompt.startOrEndDate")));
		}
	}

    else
    {

    	if(
        	((getCustomerName() == null)  || getCustomerName().equals(Constants.EMPTY_STRING))
      	&&
        	((getContactName() == null)  || getContactName().equals(Constants.EMPTY_STRING))
      	)
      	{
      		errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.required",resources.getMessage("prompt.customerContactName")));
    	}
	}

    return errors;
  }

}