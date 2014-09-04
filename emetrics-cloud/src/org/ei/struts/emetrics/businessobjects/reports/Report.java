/*
 * $Author:   johna  $
 * $Revision:   1.9  $
 * $Date:   Apr 02 2007 09:31:10  $
 *
 */


package org.ei.struts.emetrics.businessobjects.reports;

import java.util.Collection;
import java.util.Date;

import org.jCharts.Chart;


/**
 * <p>A <strong>Report</strong> which is stored, along with his or her
 * associated {@link ResultsSet}s, in a {@link Report}.</p>
 *
 */

public interface Report {


    public Report copyOf();

    // ------------------------------------------------------------- Properties
    public Date getLastTouched();

    public long getTimestamp();
    public void setTimestamp(long Time);


    public String getFilename();
    public void setFilename(String string);
    /**
     * The title of this report.
     */

    public String getTitle();
    public void setTitle(String title);

    /**
     * The roles that can access this report.
     */

    public String getRoles();
    public void setRoles(String roles);

  	public String getDescription();
  	public void setDescription(String desc);

  	public int getDisplayId();
  	public void setDisplayId(int id);

    /**
     * Find and return all {@link ResultsSet}s associated with this report.
     * If there are none, a zero-length array is returned.
     */
    public ResultsSet getResultsSet();
    public void setResultsSet(ResultsSet resultsset);

    /**
     * Find and return all {@link Chart}s associated with this report.
     * If there are none, a zero-length array is returned.
     */
    public Collection getCharts();
    public void setCharts(Collection charts);
    /**
     * Find and return all {@link dataRow}s associated with this report.
     * If there are none, a zero-length array is returned.
     */
    public Collection getTableRows();
    public void setTableRows(Collection tableRows);

    /**
     * The Report Id (must be unique).
     */
    public int getReportId();
    public void setReportId(int reportId);

    /**
     * The table header of this report (resultssetid/resultid).
     */

    public DataRow getDataHeader();
    public void setDataHeader(DataRow dataHeader);

    // --------------------------------------------------------- Public Methods

    /**
     * Create and return a new {@link XAxisLabels} associated with this
     * Chart, for the specified id name.
     *
     * @param xAxisLabelsId XAxisLabelsId for which to create a xAxisLabels
     *
     */
    public DataRow createDataHeader(int dataHeaderId);


    /**
     * Create and return a new {@link DataRow} associated with this
     * Report table, for the specified id name.
     *
     * @param rowid Rowid for which to create a DataRow
     *
     * @exception IllegalArgumentException if the rowid is not unique
     *  for this report
     */
    public DataRow createDataRow(int rowid);


    /**
     * Find and return the {@link DataRow} associated with the specified
     * rowId.  If none is found, return <code>null</code>.
     *
     * @param rowId RowId to look up
     */
    public DataRow findDataRows(int rowId);

    /**
     * Create and return a new {@link ResultsSet} associated with this
     * Report, for the specified id name.
     *
     * @param id Id for which to create a resultsSet
     *
     * @exception IllegalArgumentException if the id is not unique
     *  for this report
     */
    public ResultsSet createResultsSet(int resultsSetId);


}
