/*
 * $Author:   johna  $
 * $Revision:   1.0  $
 * $Date:   Oct 06 2005 16:06:34  $
 *
 */



package org.ei.struts.emetrics.businessobjects.reports.castor;

import org.ei.struts.emetrics.businessobjects.reports.DataRow;
import org.ei.struts.emetrics.businessobjects.reports.Report;


/**
 * <p>Concrete implementation of {@link GenericRow} for an in-Odmg
 * report backed by an XML data file.</p>
 * Used for table rows and table headers where parent is report
 *
 */

public class JdoDataRow extends JdoGenericRow implements DataRow {

    /**
     * The parent{@link LegendLabel,DataRow,DataHeader,XAxisLabels} with which we are associated.
     */
    
    // ----------------------------------------------------------- Constructors


    /**
     * <p>Construct a new JdoTableRow associated with the specified
     * {@link JdoReport}.
     *
     * @param resultset The result set with which we are associated
     * @param resultId The result id of this result
     */
    public JdoDataRow(int rowId, Report report) {
        super(rowId, report);
        this.type     = "datarow";
       
    }


    public JdoDataRow() {

    }

    /**
     * Return a String representation of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("<"+type+" id=\"");
        sb.append(rowId);
        sb.append("\"");
        if (report !=null) {
            sb.append(" reportid=\"");
            sb.append(report.getReportId());
            sb.append("\"");
        }
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
