/*
 * $Author:   johna  $
 * $Revision:   1.1  $
 * $Date:   Oct 05 2005 13:59:12  $
 *
*/


package org.ei.struts.emetrics.actions;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.ei.struts.emetrics.Constants;
import org.ei.struts.emetrics.businessobjects.reports.DataRow;
import org.ei.struts.emetrics.businessobjects.reports.Report;
import org.ei.struts.emetrics.businessobjects.reports.Result;
import org.ei.struts.emetrics.businessobjects.reports.ResultsSet;
import org.ei.struts.emetrics.businessobjects.reports.castor.JdoReport;
import org.ei.struts.emetrics.customer.view.UserContainer;
import org.ei.struts.emetrics.customer.view.UserView;
import org.ei.struts.emetrics.service.EmetricsServiceImpl;
import org.ei.struts.framework.FrameworkBaseAction;


public final class AllReportAction extends FrameworkBaseAction {


    // ----------------------------------------------------- Instance Variables


  /**
  * Commons Logging instance.
  */
  protected static Log log = LogFactory.getLog(AllReportAction.class);

	// override login to be true so executeAction is called
	// by request processor / base action class
	public boolean isLoggedIn(HttpServletRequest request) {
		return true;
	}


    // --------------------------------------------------------- Public Methods


    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param actionForm The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception Exception if business logic throws an exception
     */
    public ActionForward executeAction(ActionMapping mapping,
                 ActionForm form,
                 HttpServletRequest request,
                 HttpServletResponse response)
        throws Exception {


        //      Extract attributes we will need
        Locale locale = getLocale(request);
        MessageResources messages = getResources(request);
        HttpSession session = request.getSession();

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String today = formatter.format(calendar.getTime());

        // Get application service
        EmetricsServiceImpl serviceImpl = (EmetricsServiceImpl) getFrameworkService();

		String strReportKey = "REPORTS".concat(Constants.USER_INTERNAL);
        Collection allreports = serviceImpl.getReportsByRole(Constants.USER_INTERNAL);

		Iterator itrReports = allreports.iterator();
		while(itrReports.hasNext())
		{
			Report report = (JdoReport) itrReports.next();

			log.info("      JdoReport  report " + report.getReportId());

			Hashtable h = new Hashtable();
			h.put("custid","0");
			h.put("month","2005-01");
			h.put("offset", "0");
			h.put("range",new Integer(7));

			String custid = "0";
			String range = "7";
			String month = "2005-01";

			String hashid = today + report.getReportId() + month + range + custid;
			log.info(" ======== HASH ID IS " + hashid);

/*
			List data = new ArrayList();
			List resultssets = report.getResultsSets();
			Iterator iter = resultssets.iterator();
			if (iter.hasNext()) {
				ResultsSet resultsset = (ResultsSet) iter.next();
				data = resultsset.populate(h);
			}

			FileOutputStream out = new FileOutputStream(hashid);
			ObjectOutputStream outs = new ObjectOutputStream(out);
			long beginWriting = System.currentTimeMillis();
			outs.writeObject(data);
			long endWriting = System.currentTimeMillis();
			System.out.println("******* Writing time: "+Long.toString(endWriting - beginWriting));
			outs.flush();
			outs.close();
*/
		}

        return (mapping.findForward(Constants.SUCCESS_KEY));

    }
}
