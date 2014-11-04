/*
 * $Author:   johna  $
 * $Revision:   1.2  $
 * $Date:   Oct 05 2005 15:28:14  $
 *
 */


package org.ei.struts.emetrics.businessobjects.reports.castor;


import org.ei.struts.emetrics.businessobjects.reports.Report;


/**
 * <p>Concrete implementation of {@link GenericRow} for an in-JDO
 * report backed by an XML data file.</p>
 * Used for LegendLabel, DataRow and DataHeader similar types different names
 *
 */

public class JdoGenericRow  {

    // ----------------------------------------------------------- Constructors

    public JdoGenericRow(int rowId, Report report) {

        this.rowId    = rowId;
        this.report   = report;
    }

    public JdoGenericRow() {


    }
    // ----------------------------------------------------- Instance Variables

    /**
     * The reportId for this row.
     */
    protected Report report;

    /**
     * The rowId for this row.
     */
    protected int rowId;




    // ------------------------------------------------------------- Properties


    /**
     * The {@link Parent object} with which we are associated.
     */
    public Report getParent() {
        return (this.report);
    }

    public void setParent(Report parent) {
        this.report = parent;
    }

    /**
     * The row Id for this row.
     */
    public int getRowId() {
        return (this.rowId);
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    /**
     * The Report with which we are associated.
     */
    public Report getReport() {
        return (this.report);
    }

    public void setReport(Report report) {
        this.report = report;
    }

    /**
     * The results set id of this row.
     */
    protected int resultsSetId;

    public int getResultsSetId() {
        return (this.resultsSetId);
    }

    public void setResultsSetId(int resultsSetId) {
     	this.resultsSetId = resultsSetId;
    }



    /**
     * The results id of this row.
     */
    protected int resultId;

    public int getResultId() {
        return (this.resultId);
    }

    public void setResultId(int resultId) {
		this.resultId = resultId;

    }


    /**
     * The type of this row.
     */
    protected String type = null;

    public String getType() {
        return (this.type);
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Return a String representation of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("<"+type+" id=\"");
        sb.append(rowId);
        sb.append("\"");
        if (resultsSetId > -1) {
            sb.append(" resultssetid=\"");
            sb.append(resultsSetId);
            sb.append("\"");
        }
        if (resultId > -1) {
            sb.append(" resultid=\"");
            sb.append(resultId);
            sb.append("\"");
        }
        sb.append(">");
        return (sb.toString());

    }
}
