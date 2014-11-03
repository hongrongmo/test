/*
 *
 *
 */


package org.ei.struts.emetrics.businessobjects.reports;

/**
 * <p>Concrete implementation of {@link GenericRow} for an in-memory
 * report backed by an XML data file.</p>
 * Used for LegendLabel, DataRow and DataHeader similar types different names
 *
 */

public interface DataRow {


    /**
     * The row Id for this row.
     */
    public abstract int getRowId();

    public abstract void setRowId(int rowId);

    /**
     * The Report with which we are associated.
     */
    public abstract Report getReport();

    public abstract void setReport(Report report);


    public abstract Report getParent();

    public abstract void setParent(Report report);

    /**
     * The results set id of this row.
     */

    public abstract int getResultsSetId();

    public abstract void setResultsSetId(int resultsSetId);

    /**
     * The results set id of this row.
     */

    public abstract int getResultId();

    public abstract void setResultId(int resultId);


    /**
     * The type of this row.
     */
    public abstract String getType();

    public abstract void setType(String type);

}
