/*
 * $Author:   johna  $
 * $Revision:   1.1  $
 * $Date:   Mar 29 2007 14:10:42  $
 *
 */

package org.ei.struts.emetrics.businessobjects.reports;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletContext;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreationFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.emetrics.businessobjects.reports.castor.*;
import org.ei.struts.emetrics.businessobjects.reports.DataRow;
import org.ei.struts.emetrics.businessobjects.reports.Report;
import org.ei.struts.emetrics.businessobjects.reports.Reports;
import org.ei.struts.emetrics.businessobjects.reports.Result;
import org.ei.struts.emetrics.businessobjects.reports.ResultsSet;
import org.ei.struts.framework.exceptions.DatastoreException;
import org.xml.sax.Attributes;

/**
 * <p>Concrete implementation of {@link Reports} for an in-Jdo
 * Reports backed by an XML data file.</p>
 *
 */

public final class AllReports implements Reports {

	protected static Log log = LogFactory.getLog(AllReports.class);

	public AllReports() throws DatastoreException {
		init();
	}

	private Collection reports = new ArrayList();
    public void addReport(Report report)
    {
        reports.add(report);
    }

    public Collection getReports()
    {
        return reports;
    }

	/**
	 * Absolute pathname to the persistent file we use for loading and storing
	 * persistent data.
	 */
	private String pathname = null;
	private String dtdfile = null;

	public String getPathname() {
		return (this.pathname);
	}
	public void setPathname(String pathname) {
		this.pathname = pathname;
	}
	public String getDtdfile() {
		return (this.dtdfile);
	}
	public void setDtdfile(String dtdfile) {
		this.dtdfile = dtdfile;
	}

	// --------------------------------------------------------- Public Methods

	// Member of the 'Report' Interface
	public void close() throws Exception {
		//do nothing
	}

	// Member of the 'Report' Interface
	public void open() throws Exception {
		//do nothing
	}

	/**
	 * Opens the database and prepares it for transactions
	 */
	private void init() throws DatastoreException {

		try {

		} catch (Exception ex) {
			throw DatastoreException.datastoreError(ex);
		}
	}


	/**
	 * <p>Initiate access to the underlying persistence layer.</p>
	 *
	 * @exception Exception if a reports access error occurs
	 */
	public void open(ServletContext context) throws Exception {

		BufferedInputStream bis = null;

		try {

			// Acquire an input stream to our database file
			log.info(" -- Loading reports from '" + this.getPathname() + "'");
			log.info(" -- Validating reports from '" + this.getDtdfile() + "'");

			bis = new BufferedInputStream(context.getResourceAsStream(this.getPathname()));

			// Construct a digester to use for parsing
			Digester digester = new Digester();
			digester.push(this);
			digester.setValidating(true);
			digester.register(
				"-//EI//EI//EN",
				context.getResource(this.getDtdfile()).toString());

			digester.addFactoryCreate(
				"reports/report",
				new JdoReportCreationFactory(this));

			digester.addCallMethod(
				"reports/report/filename",
				"setFilename",
				0);

			digester.addFactoryCreate(
				"reports/report/resultsset",
				new JdoResultsSetCreationFactory());

			digester.addFactoryCreate(
				"reports/report/resultsset/result",
				new JdoResultCreationFactory());

			digester.addFactoryCreate(
				"reports/report/table/dataheader",
				new JdoDataHeaderCreationFactory());

			digester.addFactoryCreate(
				"reports/report/table/datarow",
				new JdoDataRowCreationFactory());

			// Parse the input stream to initialize our database
			digester.parse(bis);
			bis.close();
			bis = null;

		} catch (IOException e) {

			log.error("Loading reports from '" + pathname + "':", e);
			throw e;

		} finally {

			if (bis != null) {
				try {
					bis.close();
				} catch (IOException ioe) {
					;
				}
				bis = null;
			}
		}

        log.info(" -- Done Loading reports.");
	} // open()

	public Report findReport(int reportId) {
		// TODO Auto-generated method stub

		Report report = null;
		Iterator itrrep = reports.iterator();
		while(itrrep.hasNext()){
			Report rep = (Report) itrrep.next();
			if(rep.getReportId() == reportId)
			{
				report = rep;
				break;
			}
		}
		return report;
	}


} // JdoReports


/**
 * Digester object creation factory for report instances.
 */
class JdoReportCreationFactory implements ObjectCreationFactory {

    public JdoReportCreationFactory(Reports reports) {
        this.allreports = reports;
    }

    private Reports allreports = null;

    private Digester digester = null;

    public Digester getDigester() {
        return (this.digester);
    }

    public void setDigester(Digester digester) {
        this.digester = digester;
    }

    public Object createObject(Attributes attributes) throws DatastoreException {
        int reportId = Integer.parseInt(attributes.getValue("id"));
        String reportTitle = attributes.getValue("title");
        String reportroles = attributes.getValue("roles");
		String reportdescription = attributes.getValue("description");
		int displayid = 0;
		if(attributes.getValue("displayid") != null) {
			displayid = Integer.parseInt(attributes.getValue("displayid"));
		}

        Report report = new JdoReport(reportId);
        report.setTitle(reportTitle);
        report.setRoles(reportroles);
        report.setDescription(reportdescription);
        report.setDisplayId(displayid);

        allreports.addReport(report);

        return (report);
    }

}

/**
 * Digester object creation factory for resultsset instances.
 */
class JdoResultsSetCreationFactory implements ObjectCreationFactory {

    public JdoResultsSetCreationFactory() {
    }

    private Digester digester = null;

    public Digester getDigester() {
        return (this.digester);
    }

    public void setDigester(Digester digester) {
        this.digester = digester;
    }

    public Object createObject(Attributes attributes) {
        int resultsSetId = Integer.parseInt(attributes.getValue("id"));
        String name = attributes.getValue("name");
        Report report = (Report) digester.peek();
        ResultsSet resultsSet = report.createResultsSet(resultsSetId);

		try
		{
			resultsSet.setName(name);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

        return (resultsSet);
    }

}


/**
 * Digester object creation factory for result instances.
 */
class JdoResultCreationFactory implements ObjectCreationFactory {

    public JdoResultCreationFactory() {
    }

    private Digester digester = null;

    public Digester getDigester() {
        return (this.digester);
    }

    public void setDigester(Digester digester) {
        this.digester = digester;
    }

    public Object createObject(Attributes attributes) {
        int resultId = Integer.parseInt(attributes.getValue("id"));
        String resultType = attributes.getValue("type");
        String column = attributes.getValue("column");
        String name = attributes.getValue("name");
		String sortable = attributes.getValue("sortable");
		String database = attributes.getValue("database");

        ResultsSet resultsSet = (ResultsSet) digester.peek();

		Result result = null;

			result = resultsSet.createResult(resultId);

			try
			{
				result.setType(resultType);
				result.setName(name);
				result.setColumn(column);
				result.setSortable(sortable);
				result.setDatabase(database);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

        return (result);
    }

}

/**
 * Digester object creation factory for datarow instances.
 */
class JdoDataRowCreationFactory implements ObjectCreationFactory {

    public JdoDataRowCreationFactory() {

    }

    private Digester digester = null;

    public Digester getDigester() {
        return (this.digester);
    }

    public void setDigester(Digester digester) {
        this.digester = digester;
    }

    public Object createObject(Attributes attributes) {

    	int resultssetid = Integer.parseInt(attributes.getValue("resultssetid"));
        int resultid = Integer.parseInt(attributes.getValue("resultid"));
        int id = Integer.parseInt(attributes.getValue("id"));

        Report report =  (Report) digester.peek();

        DataRow datarow = report.createDataRow(id);

		try
		{
			datarow.setReport( report );
			datarow.setResultsSetId(resultssetid );
			datarow.setResultId(resultid);
			datarow.setType("datarow");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return datarow;
    }

}

/**
 * Digester object creation factory for dataheader instances.
 */
class JdoDataHeaderCreationFactory implements ObjectCreationFactory {

    public JdoDataHeaderCreationFactory() {

    }

    private Digester digester = null;

    public Digester getDigester() {
        return (this.digester);
    }

    public void setDigester(Digester digester) {
        this.digester = digester;
    }

    public Object createObject(Attributes attributes) {

       	int resultssetid = Integer.parseInt(attributes.getValue("resultssetid"));
        int resultid = Integer.parseInt(attributes.getValue("resultid"));
        int id = Integer.parseInt(attributes.getValue("id"));

        Report report =  (Report) digester.peek();

        DataRow dataheader = report.createDataHeader(id);
		try
		{
			dataheader.setParent( report );
			dataheader.setResultsSetId(resultssetid );
			dataheader.setResultId(resultid);
			dataheader.setType("dataheader");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
        return (dataheader);
    }

}
