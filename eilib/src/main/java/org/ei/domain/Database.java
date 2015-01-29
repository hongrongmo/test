package org.ei.domain;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.sort.SortField;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;
import org.ei.fulldoc.LinkingStrategy;

public abstract class Database implements Comparable<Object> {
	public static String DEFAULT_ELSEVIER_TEXT_COPYRIGHT = "Compilation and indexing terms, Copyright 2014 Elsevier Inc.";
	public static String DEFAULT_ELSEVIER_HTML_COPYRIGHT = "Compilation and indexing terms, &copy; 2014 Elsevier Inc.";

	public List<SortField> getSortableFields() {
		return Arrays.asList(new SortField[] { SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR });
	}

	public int compareTo(Object obj) {
		Database d = (Database) obj;
		if (d != null) {
			if (getSortValue() < d.getSortValue()) {
				return -1;
			} else if (getSortValue() > d.getSortValue()) {
				return 1;
			} else if (getSortValue() == d.getSortValue()) {
				return 0;
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	public DataDictionary getDataDictionary() {
		return null;
	}

	public boolean equals(Object o) {
		boolean isEqual = false;
		Database da = null;

		try {
			da = (Database) o;
			if (compareTo(da) == 0) {
				isEqual = true;
			}
		} catch (ClassCastException e) {
			// Do nothing isEqual is already false;
		}

		return isEqual;

	}

	public String getUpdates(int updatesNum) throws InfrastructureException {
		ConnectionBroker broker = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer range = new StringBuffer();
		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
			String sql = "select load_number from load_number where database=? order by load_number desc";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, getID());
			rs = pstmt.executeQuery();
			int c = 1;
			String weekNumber = null;
			String end = null;
			while (rs.next()) {
				weekNumber = rs.getString("load_number");
				if (c == 1) {
					end = weekNumber;
				}

				if (c == updatesNum) {
					range.append(weekNumber);
					range.append("-");
					range.append(end);
					return range.toString();
				}

				c++;
				if (c == 20) {
					break;
				}
			}
			range.append(weekNumber);
			range.append("-");
			range.append(end);
		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.UNKNOWN_INFRASTRUCTURE_ERROR, "Unable to retreive updates", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return range.toString();
	}

	public Database getBackfile() {
		return null;
	}

	public boolean isBackfile() {
		return false;
	}

	public Database[] getChildren() {
		return null;
	}

	public boolean hasChildren() {
		return false;
	}

	public LimitGroups limitSearch(String[] credentials, String[] fields) {
		return null;
	}

	protected String getBaseTableHook() {
		return null;
	}

	public boolean linkLocalHoldings(String linkLabel) {
		return true;
	}

	public boolean hasField(SearchField searchField, int mask) {
		return false;
	}

	public int getStartYear(boolean backFile) {
		return 1970;
	}

	public int getEndYear() {
		return SearchForm.ENDYEAR;
	}

	public abstract String getID();

	public abstract String getLegendID();

	public abstract String getName();

	public abstract String getShortName();

	public abstract String getIndexName();

	public abstract String getSingleCharName();

	public abstract int getMask();

	public abstract int getSortValue();

	public void toXML(Writer out) throws IOException {
		out.write("<DB>");
		out.write("<ID>");
		out.write(getID());
		out.write("</ID>");
		out.write("<DBMASK>");
		out.write(Integer.toString(getMask()));
		out.write("</DBMASK>");
		out.write("<DBNAME>");
		out.write(getName());
		out.write("</DBNAME>");
		out.write("<DBSNAME>");
		out.write(getShortName());
		out.write("</DBSNAME>");
		out.write("<DBINAME>");
		out.write(getIndexName());
		out.write("</DBINAME>");
		out.write("</DB>");
	}

	public abstract DocumentBuilder newBuilderInstance();

	public abstract SearchControl newSearchControlInstance();

	public Map<?, ?> getTreatments() {
		return new Hashtable<Object, Object>();
	};

	public abstract LinkingStrategy getLinkingStrategy();

}
