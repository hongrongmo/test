package org.ei.struts.emetrics.businessobjects.reports.csv;

import org.ei.struts.emetrics.businessobjects.reports.Report;
import org.ei.struts.emetrics.businessobjects.reports.Result;
import org.ei.struts.emetrics.businessobjects.reports.ResultsSet;

public class CSVResult implements Result {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

    public CSVResult(ResultsSet resultsSet, int resultId) {

        super();
        this.m_resultsSet   = resultsSet;
        this.resultId     = resultId;
        this.m_report     = resultsSet.getReport();


    }

    public CSVResult() {

    }

	private ResultsSet m_resultsSet;
	private Report m_report;
	private int resultId;
	private String m_name;
	private String m_type;
	private String m_column;
	private String m_sortable;
	private String m_database;

    public int getResultId() {
        return (this.resultId);
    }


    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

	public ResultsSet getResultsSet() {
		return m_resultsSet;
	}

	public void setResultsSet(ResultsSet resultsSet) {
		m_resultsSet = resultsSet;
	}

	public Report getReport() {
		return m_report;
	}

	public void setReport(Report report) {
		m_report = report;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}

	public String getType() {
		return m_type;
	}

	public void setType(String type) {
		m_type = type;
	}

	public String getColumn() {
		return (m_column != null) ? m_column.toLowerCase() : m_column;
	}

	public void setColumn(String column) {
		m_column = (column != null) ? column.toLowerCase() : column;
	}

	public String getSortable() {
		return m_sortable;
	}

	public void setSortable(String sortable) {
		m_sortable = sortable;
	}


	public String getDatabase() {
		return m_database;
	}

	public void setDatabase(String database) {
		m_database = database;
	}

    /**
     * Return a String representation of this object.
     */
    public String toString() {

        StringBuffer sb = new StringBuffer("<result id=\"");
        sb.append(getResultId());
        sb.append("\"");

		sb.append(" reportId=\"");
		sb.append(getResultsSet().getReport().getReportId());
		sb.append("\"");


		sb.append(" resultsSetId=\"");
		sb.append(getResultsSet().getResultsSetId());
		sb.append("\"");

        if (getName() != null) {
            sb.append(" name=\"");
            sb.append(getName());
            sb.append("\"");
        }
        if (getType() != null) {
            sb.append(" type=\"");
            sb.append(getType());
            sb.append("\"");
        }
        if (getColumn() != null) {
            sb.append(" column=\"");
            sb.append(getColumn());
            sb.append("\"");
        }
        if (getDatabase() != null) {
            sb.append(" database=\"");
            sb.append(getDatabase());
            sb.append("\"");
        }
        sb.append(">");
        return (sb.toString());

    }
}
