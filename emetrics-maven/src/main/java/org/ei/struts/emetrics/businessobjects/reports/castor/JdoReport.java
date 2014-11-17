/*
 * $Author:   johna  $
 * $Revision:   1.9  $
 * $Date:   Apr 02 2007 09:38:14  $
 *
 */


package org.ei.struts.emetrics.businessobjects.reports.castor;


import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Date;
import javax.swing.text.TableView.TableRow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.emetrics.businessobjects.reports.DataRow;
import org.ei.struts.emetrics.businessobjects.reports.Report;
import org.ei.struts.emetrics.businessobjects.reports.Result;
import org.ei.struts.emetrics.businessobjects.reports.ResultsSet;
import org.ei.struts.emetrics.businessobjects.reports.csv.CSVResultsSet;
import org.ei.struts.emetrics.utils.StringUtils;
import org.jCharts.Chart;


/**
 * <p>Concrete implementation of {@link Report} for an in-Jdo
 * reports backed by an XML data file.</p>
 *
 */

public class JdoReport implements Report {


    // ----------------------------------------------------------- Constructors
    public Report copyOf() {

      // copy all of the report basic properties
      Report areport = new JdoReport(this.reportId);
      areport.setTitle(this.title);
      areport.setRoles(this.roles);
      areport.setDescription(this.description);
      areport.setFilename(this.filename);
      areport.setDisplayId(this.displayId);

      ResultsSet resultsSet = areport.createResultsSet(this.getResultsSet().getResultsSetId());

      DataRow dh = areport.createDataHeader(this.getDataHeader().getRowId());
			dh.setResultsSetId(this.getDataHeader().getResultsSetId());
			dh.setResultId(this.getDataHeader().getResultId());


      Iterator itrresults = this.getResultsSet().getResults().iterator();
      while(itrresults.hasNext()) {
      	Result res = (Result) itrresults.next();
  		  Result result = areport.getResultsSet().createResult(res.getResultId());

				result.setType(res.getType());
				result.setName(res.getName());
				result.setColumn(res.getColumn());
				result.setSortable(res.getSortable());
				result.setDatabase(res.getDatabase());
      }


      Iterator itrdatarows = datarows.iterator();
      while(itrdatarows.hasNext()) {
      	DataRow dr = (DataRow) itrdatarows.next();
        DataRow newdr = areport.createDataRow(dr.getRowId());

  			newdr.setResultsSetId(dr.getResultsSetId());
  			newdr.setResultId(dr.getResultId());
      }

      return areport;
    }


    /**
     * <p>Construct a new Report associated with the specified
     * {@link reports}.
     *
     * @param reports The reports  with which we are associated
     * @param reportId The reportId of this report
     */
    public JdoReport(int reportId) {
        super();
        this.reportId = reportId;
        log.info("New report id " + reportId + " created.");
    }


    public JdoReport() {
    }

    // ----------------------------------------------------- Instance Variables
    /**
     * Logging output for this reports instance.
     */
    private Log log = LogFactory.getLog(this.getClass());


    /**
     * The {@link ResultSet}s for this Report, keyed by id.
     */
    private ResultsSet resultsset = null;


    /**
     * The {@link Chart}s for this Report, keyed by id.
     */
    private Collection charts = new ArrayList();


    /**
     * The {@link TableRow}s for the table of this report, keyed by id.
     */
    private Collection datarows = new ArrayList();

    /**
     * The table header of this report (resultssetid/resultid).
     */
    private DataRow dataHeader = null;


    /**
     * The id for this report.
     */
    private int reportId;
    private int parentId;
	private int displayId;


    // ------------------------------------------------------------- Properties


    /**
     * The {@link Reports} with which we are associated.
     */


    /**
     * The filename data source of this report.
     */
    private String filename = null;

    public String getFilename() {
        return (this.filename);
    }

    public void setFilename(String string) {
		this.filename = StringUtils.normaliseWhitespace(string);
    }

    /**
     * The title of this report.
     */
    private String title = null;

    public String getTitle() {
        return (this.title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * The roles that can access this report.
     */
    private String roles = null;

    public String getRoles() {
        return (this.roles);
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

	private String description = null;

    /**
     * Find and return all {@link Chart}s associated with this report.
     * If there are none, a zero-length array is returned.
     */
    public Collection getCharts() {
		return (charts);

    }

    public void setCharts(Collection charts) {
		this.charts = charts;
    }

    /**
     * Return all {@link TableRows}s associated with this report.
     * If there are none, an empty list is returned.
     */
    public Collection getTableRows() {
		return datarows;

    }

    /**
     * Set all {@link TableRows}s associated with this report.
     * If there are none, a zero-length array is returned.
     */
    public void setTableRows(Collection datarows) {
		this.datarows = datarows;
    }

    /**
     * The Report Id (must be unique).
     */
    public int getReportId() {
        return ( this.reportId );
    }

    /**
     * Set id for this report.
     * Also set the parentId for the tablerows.
     */
    public void setReportId(int reportId) {
        this.reportId = reportId;
        this.parentId = reportId;
    }

    /**
     * Return Id for this report (must be unique).
     */
    public int getParentId() {
        return ( this.parentId );
    }

    /**
     * Return Id for this report (must be unique).
     */
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }



    /**
     * Return a new {@link DataHeader} associated with this
     * Report, for the specified id name.
     *
     * @param null
     *
     */
    public DataRow getDataHeader() {
        return (this.dataHeader);
    }

    public void setDataHeader(DataRow dataHeader) {
        this.dataHeader = dataHeader;
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Create and return a new {@link XAxisLabels} associated with this
     * Chart, for the specified id name.
     *
     * @param xAxisLabelsId XAxisLabelsId for which to create a xAxisLabels
     *
     */
    public DataRow createDataHeader(int dataHeaderId) {

		dataHeader = new JdoDataHeader(dataHeaderId, this);
		dataHeader.setType("dataHeader");
		dataHeader.setParent(this);
		return (dataHeader);

    }


    /**
     * Create and return a new {@link TableRow} associated with this
     * Report table, for the specified id name.
     *
     * @param rowid Rowid for which to create a TableRow
     *
     * @exception IllegalArgumentException if the rowid is not unique
     *  for this report
     */
    public DataRow createDataRow(int rowid) {

    	DataRow datarow = new JdoDataRow(rowid, this);
		datarow.setType("datarow");
		datarow.setParent(this);

		datarows.add(datarow);

		return datarow;

    }


    /**
     * Find and return the {@link TableRow} associated with the specified
     * rowId.  If none is found, return <code>null</code>.
     *
     * @param rowId RowId to look up
     */
    public DataRow findDataRows(int rowId) {

    	DataRow tablerow = null;
    	DataRow item = null;
		try
		{

			synchronized (datarows) {
				// 5. now iterate over the resultsset to print find the one
				java.util.Iterator iter = datarows.iterator();
				lookup: while (iter.hasNext())
				{
					item = (DataRow) iter.next();
					if (item.getRowId() == rowId) {
						tablerow = item; // found it
						break lookup;
					}
				}

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return tablerow;
    }


    /**
     * Create and return a new {@link ResultsSet} associated with this
     * Report, for the specified id name.
     *
     * @param id Id for which to create a resultsSet
     *
     * @exception IllegalArgumentException if the id is not unique
     *  for this report
     */
    public ResultsSet createResultsSet(int resultsSetId) {

		resultsset = new CSVResultsSet(this, resultsSetId);

		if (log.isInfoEnabled()) {
			log.info("Creating ResultsSet: " + resultsset.toString() );
		}
		return (resultsset);

    }



    /**
     * Return a String representation of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("<report id=\"");
        sb.append(reportId);
        sb.append("\"");
        if (title != null) {
            sb.append(" title=\"");
            sb.append(title);
            sb.append("\"");
        }
		if (roles != null) {
			sb.append(" roles=\"");
			sb.append(roles);
			sb.append("\"");
        }
		if (description != null) {
			sb.append(" description=\"");
			sb.append(description);
			sb.append("\"");
		}
        if (filename != null) {
            sb.append(" filename=\"");
            sb.append(filename);
            sb.append("\"");
        }

        sb.append(">");

        if(resultsset != null) {
        	sb.append(resultsset.toString() );
        }
        sb.append("<table>");
        if(dataHeader != null) {
    		sb.append(dataHeader.toString() );
    	}

        Iterator itrdatarows = datarows.iterator();
        while(itrdatarows.hasNext()) {

        	DataRow dr = (DataRow) itrdatarows.next();
        	sb.append(dr.toString() );

        }
        sb.append("</table>");
        sb.append("</report>");


        return (sb.toString());

    }


	/**
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param string
	 */
	public void setDescription(String string) {
		description = string;
	}

	/**
	 * @return
	 */
	public int getDisplayId() {
		return displayId;
	}

	/**
	 * @param i
	 */
	public void setDisplayId(int i) {
		displayId = i;
	}



	public void setResultsSets(Collection resultssets) {
		// TODO Auto-generated method stub

	}



	public ResultsSet getResultsSet() {
		// TODO Auto-generated method stub
		return resultsset;
	}



	public void setResultsSet(ResultsSet resultsset) {
		this.resultsset = resultsset;

	}


    private long m_timestmp = 0l;

    public long getTimestamp() { return m_timestmp; }
    public void setTimestamp(long timestmp) {
        m_timestmp = timestmp;
    }

    public Date getLastTouched() { return new Date(m_timestmp); }

}
