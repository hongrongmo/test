/*
 *
 *
 */


package org.ei.struts.emetrics.businessobjects.reports;

/**
 * <p>Concrete implementation of {@link Report} for an in-memory
 * database backed by an XML data file.</p>
 *
 */

public interface  Result {



    // ------------------------------------------------------------- Constructors

    /**
     * The {@link ResultsSet} with which we are associated.
     */
    public abstract ResultsSet getResultsSet();


    public abstract void setResultsSet(ResultsSet resultsSet);


    public int getResultId() ;

    public void setResultId(int resultId);

    /**
     * The {@link Report} with which we are associated.
     */
    public abstract Report getReport();

    public abstract void setReport(Report report);


    /**
     * The name of this result.
     */

    public abstract String getName();

    public abstract void setName(String name);


	/**
	 * The type of this result.
	 */

	 public abstract String getType();

	 public abstract void setType(String type);


    /**
     * The name of the column for this result.
     */

    public abstract String getColumn();

    public abstract void setColumn(String column);


	public abstract String getSortable();

	public abstract void setSortable(String sortable);

	public abstract String getDatabase();

	public abstract void setDatabase(String database);

    // --------------------------------------------------------- Public Methods




}
