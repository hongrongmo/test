/*
 * $Author:   johna  $
 * $Revision:   1.6  $
 * $Date:   Mar 27 2006 16:48:16  $
 *
*/


package org.ei.struts.emetrics.businessobjects.reports;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Map;
import java.util.List;

import org.ei.struts.emetrics.customer.view.UserView;

import org.ei.struts.framework.exceptions.DatastoreException;
/**
 * <p>Concrete implementation of {@link ResultSet} for an in-memory
 * database backed by an XML data file.</p>
 */

public interface ResultsSet {

    /**
     * The id for this results set.
     */
    public abstract int getResultsSetId();
	public void setResultsSetId(int resultsSetId);

    /**
     * The Report owning this ResultsSet.
     */
    public Report getReport();
    public void setReport(Report report);

    /**
     * The username for this ResultsSet.
     */
    public abstract String getName();
    public abstract void setName(String name);

    /**
     * The querystring for this ResultsSet.
     */
    public abstract String getQueryString();
    public abstract void setQueryString(String queryString) ;

    /**
     * The prepared statement fields for querystring of this ResultsSet.
     */
    public abstract String getPstmst();
    public abstract void setPstmst(String pstmst);

    /**
     * Find and return all {@link Results}s associated with this resultSet.
     * If there are none, a zero-length array is returned.
     */
    public abstract Collection getResults();


    // --------------------------------------------------------- Public Methods


    /**
     * Create and return a new {@link ResultsSet} associated with this
     * Report, for the specified id name.
     *
     * @param id Id for which to create a resultsSet
     *
     * @exception IllegalArgumentException if the id is not unique
     *  for this report
     */
    public abstract Result createResult(int resultId);


    /**
     * Find and return the {@link ResultsSet} associated with the specified
     * resultsSetId.  If none is found, return <code>null</code>.
     *
     * @param resultsSetId ResultsSetId to look up
     */
    public abstract Result findResult(int resultId);

    /**
     * Populate {@link populate} the results associated with the specified
     * resultsSet.  Start of SQL. If none is found, return <code>null</code>.
     *
     */
    public abstract List populate (Map pStmntValues)
    	throws DatastoreException;

    public abstract void setColumnList(List columns);
    public abstract List getColumnList();

    public abstract void setUser(UserView auser);
    public abstract UserView getUser();

	public abstract void toXML(java.io.Writer out) throws java.io.IOException;

	public abstract void toCSV(java.io.Writer out) throws java.io.IOException;

	public abstract void toXLS(java.io.OutputStream out) throws java.io.IOException;

}
