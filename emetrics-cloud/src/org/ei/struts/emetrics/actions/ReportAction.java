/*
 * $Author:   johna  $
 * $Revision:   1.34  $
 * $Date:   Oct 05 2007 16:37:20  $
 *
 */

package org.ei.struts.emetrics.actions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.ei.struts.emetrics.Constants;
import org.ei.struts.emetrics.businessobjects.ranges.*;
import org.ei.struts.emetrics.businessobjects.reports.DataRow;
import org.ei.struts.emetrics.businessobjects.reports.Report;
import org.ei.struts.emetrics.businessobjects.reports.Result;
import org.ei.struts.emetrics.businessobjects.reports.ResultsSet;
import org.ei.struts.emetrics.customer.view.UserContainer;
import org.ei.struts.emetrics.customer.view.UserView;
import org.ei.struts.emetrics.service.EmetricsServiceImpl;
import org.ei.struts.framework.FrameworkBaseAction;

public final class ReportAction extends FrameworkBaseAction {

	// ----------------------------------------------------- Instance Variables

	/**
	 * Commons Logging instance.
	 */
	protected static Log log = LogFactory.getLog(ReportAction.class);

	// --------------------------------------------------------- Public Methods

	/**
	 * Process the specified HTTP request, and create the corresponding HTTP
	 * response (or forward to another web component that will create it).
	 * Return an <code>ActionForward</code> instance describing where and how
	 * control should be forwarded, or <code>null</code> if the response has
	 * already been completed.
	 * 
	 * @param mapping
	 *            The ActionMapping used to select this instance
	 * @param actionForm
	 *            The optional ActionForm bean for this request (if any)
	 * @param request
	 *            The HTTP request we are processing
	 * @param response
	 *            The HTTP response we are creating
	 * 
	 * @exception Exception
	 *                if business logic throws an exception
	 */
	public ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// Extract attributes we (may) need
		Locale locale = getLocale(request);
		MessageResources messages = getResources(request);
		HttpSession session = request.getSession();

		// Populate the form
		if (form == null) {
			log.info(" Creating new reportForm bean under key "
					+ mapping.getName());
			form = new ReportForm();
			request.setAttribute(mapping.getName(), form);
		}

		// Cast form into a reportForm
		ReportForm rptform = (ReportForm) form;

		Report report = null;

		// Get application service
		EmetricsServiceImpl serviceImpl = (EmetricsServiceImpl) getFrameworkService();

		// Validate the request parameters specified by the user
		ActionErrors errors = new ActionErrors();

		UserContainer userContainer = null;
		UserView user = null;
		try {
			userContainer = getUserContainer(request);
			user = (UserView) userContainer.getUserView();
		} catch (Exception e) {
			log.error(" Failed to get user", e);
			errors.add("loginError", new ActionError("error.password.mismatch"));
		}
		Collection members = user.getConsortium();

		log.info(user.toString());
		rptform.setCustomerId(user.getCustomerId());
		rptform.setIsConsortium(!members.isEmpty() && !user.getParentonly());

		Collection allreports = serviceImpl.getReportsByRole(user.getRole());
		rptform.setAllReports(allreports);

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			return (mapping.findForward("error.noreportid"));
		}

		String reportid = rptform.getRepid();

		if (rptform.getAction().equals(Constants.ACTION_DESCRIBE)) {
			return (mapping.findForward(Constants.DESCRIPTIONS_KEY));
		}

		if (reportid == null || reportid.equals(Constants.EMPTY_STRING)) {
			// if no reportid is specified - just list the reports!
			if (request.getParameter("exportType") == null) {
				rptform.setAction(Constants.ACTION_LIST);
				return (mapping.findForward(Constants.SUCCESS_KEY));
			}
		}
		rptform.setAction(Constants.ACTION_DISPLAY_REPORT);

		// We do not have a reference to the selected Report.
		// So first we have to lookup the object
		report = serviceImpl.getReport(reportid);
		if (report.getReportId() == Constants.DEPARTMENT_REPORT_ID) {
			report = report.copyOf();
			log.info(report.toString());
		}
		rptform.setReport(report);

		// Setup the hashtable used to bind pstmt parameters
		// in the report object
		// These names match the 'pstmst' values in the ressultset definition
		// of the report
		// Extra values will not have adverse affects
		Map h = new HashMap();

		String custid = String.valueOf(user.getCustomerId());

		h.put("custid", custid);
		h.put("path",
				servlet.getInitParameter("reportdata")
						.concat(System.getProperty("file.separator"))
						.concat(custid.equals("0") ? "totals" : custid)
						.concat(System.getProperty("file.separator")));
		h.put("filename", report.getFilename());

		// if this is a consotrium and we do not want only the parent's data
		if (!members.isEmpty() && !user.getParentonly()) {
			h.put("filename", "Consortium".concat(report.getFilename()));
		}

		IRange range = null;

		// TODO: Fix this - make type of date range a property or arttibute of
		// report
		if (report.getReportId() == Constants.COUNTER2_DATABASE_REPORT1_ID) {
			// month range - provided by two values
			Map params = new HashMap();
			params.put("oneyear", rptform.getOneyear());
			range = new YearRange(params);
		} else if (report.getReportId() == Constants.CUSTOMER_DATABASE_COMBO
				&& rptform.getIsConsortium()) {
			range = new MemberRange(members);
			rptform.setMembers(((MemberRange) range).getMembers());
			if (!rptform.getMember().equalsIgnoreCase(Constants.ALL)) {
				h.put("path",
						servlet.getInitParameter("reportdata")
								.concat(System.getProperty("file.separator"))
								.concat(rptform.getMember())
								.concat(System.getProperty("file.separator")));
				h.put("filename", report.getFilename());
			}
		} else if ((report.getReportId() == Constants.CUSTOMER_DATABASE_COMBO)
				|| (report.getReportId() == Constants.MARKETER_DATABASE_COMBO)) {
			range = new AllDataRange();
		} else if ((report.getTitle().toLowerCase().indexOf("usage by") > -1)
				|| (report.getReportId() == 810)) {
			range = new AllDataRange();
			String filename = (String) h.get("filename");
			h.put("filename",
					filename.replaceAll("\\{0\\}", rptform.getQuarter()));
		} else if (report.getTitle().toLowerCase().indexOf("custom") > -1) {
			if (rptform.getSearchtype().equals(Constants.RANGE)) {
				// month range - provided by two values
				Map params = new HashMap();
				params.put("startdate", rptform.getEndmonth());
				params.put("enddate", rptform.getMonth());
				range = new MonthRange(params);
			} else {
				// single day
				Map params = new HashMap();
				params.put("startdate", rptform.getSingleday());
				range = new SingleDayRange(params);
			}
		} else {
			if (report.getTitle().toLowerCase().indexOf("month") > -1) {
				// month range from two values
				Map params = new HashMap();
				params.put("startdate", rptform.getEndmonth());
				params.put("enddate", rptform.getMonth());
				range = new MonthRange(params);
			} else if (report.getTitle().toLowerCase().indexOf("combination") > -1) {
				// range that will always return true - include all data in
				// report
				// mostly for reports with non-date value as key
				range = new AllDataRange();
			} else {
				// month range from single value
				Map params = new HashMap();
				params.put("startdate", rptform.getMonth());
				range = new SingleMonthRange(params);
			}
		}

		h.put("range", range);

		ResultsSet resultsset = report.getResultsSet();
		List data = resultsset.populate(h);
		List columns = resultsset.getColumnList();

		if (report.getReportId() != Constants.COUNTER2_DATABASE_REPORT1_ID) {

			Collection tablerows = report.getTableRows();
			columns = new ArrayList();

			// add column header for the default column
			// <dataheader... in the report definition <table> element
			int resultid = report.getDataHeader().getResultId();
			Result result = report.getResultsSet().findResult(resultid);
			columns.add(result);

			// add column header for each element in a row of the results
			// <datarow... in the report definition <table> element
			Iterator iter = tablerows.iterator();
			while (iter.hasNext()) {
				DataRow datarow = (DataRow) iter.next();
				resultid = datarow.getResultId();

				result = report.getResultsSet().findResult(resultid);
				columns.add(result);
			}
		}

		if (!(rptform.getSortableColumns()).isEmpty()) {
			if (!(Constants.EMPTY_STRING).equals(rptform.getOrderby())) {

				BeanComparator reversedNaturalOrderBeanComparator = new BeanComparator(
						rptform.getOrderby(), new ReverseComparator(
								new ComparableComparator()));

				try {
					Collections.sort((List) data,
							reversedNaturalOrderBeanComparator);
				} catch (Exception e) {
					log.error("Cannot sort data", e);
				}

				if ((Constants.ASCENDING).equals(rptform.getDirection())) {
					Collections.reverse((List) data);
				}
			}
		}

		request.setAttribute(Constants.DISPLAYTABLE_KEY, columns);
		request.setAttribute(Constants.REPORT_DATA_KEY, data);

		// Forward control to the specified success URI
		if (request.getParameter("exportType") != null) {

			if (reportid.equals(String
					.valueOf(Constants.COUNTER2_DATABASE_REPORT1_ID))) {
				response.setHeader("Pragma", "No-cache");
				response.setHeader("Cache-Control", "no-store");
				response.setDateHeader("Expires", 0);
				response.setHeader("Cache-Control", "no-cache");

				String exportFile = "exportfile";
				String exportType = request.getParameter("exportType");
				try {
					switch (Integer.parseInt(exportType)) {
					default:
					case 1:
						exportFile = exportFile.concat(".csv");
						break;
					case 2:
						exportFile = exportFile.concat(".xls");
						break;
					case 3:
						exportFile = exportFile.concat(".xml");
						break;
					}

					response.setHeader("Content-Disposition",
							"inline; filename=" + exportFile);

					resultsset.setUser(user);
					switch (Integer.parseInt(exportType)) {
					default:
					case 1:
						response.setContentType("text/csv");
						resultsset.toCSV(response.getWriter());
						break;
					case 2:
						response.setContentType("application/vnd.ms-excel");
						resultsset.toXLS(response.getOutputStream());
						break;
					case 3:
						response.setContentType("text/xml");
						resultsset.toXML(response.getWriter());
						break;
					}
				} catch (NumberFormatException nfe) {
				}
				return null;
			} else {
				request.setAttribute(Constants.RAW_RESULTSSET, resultsset);
				return (mapping.findForward(Constants.EXPORT_KEY));
			}
		} else {
			return (mapping.findForward(Constants.SUCCESS_KEY));
		}

	}

}