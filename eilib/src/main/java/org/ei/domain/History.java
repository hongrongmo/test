package org.ei.domain;

/** project specific imports*/
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;

/**
 *
 * Handles Session History related tasks such as
 * <p>
 * a) Storing Query in SESSION_HISTORY based on Session Id.
 * </p>
 * <p>
 * b) Retriving Query from SESSION_HISTORY based on Session Id.
 * </p>
 * <p>
 * c) Clearing Queries in SESSION_HISTORY for a Session Id.
 * </p>
 *
 */
public class History

{

	/**
	 * unique identifer of the session
	 */
	protected String sSessionId;


	/**
	*
	*	takes Session Id as an argument for the object to instantiate
	*
	**/

	public History(String sSesId) throws InfrastructureException {
		if (sSesId == null)
			throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, "Session Id Is NULL");

		this.sSessionId=sSesId;


	}



	/**
	*	gets query in SESSION_HISTORY with Session Id and searchID
	*
	 * @param String
	 *            searchID
	* 	@return Query
	* 	@exception History
	*/

	public Query getQuery(String searchID) throws InfrastructureException {
		Query q = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ConnectionBroker broker = null;

		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			pstmt = con.prepareStatement("select QRY from SESSION_HISTORY where SESSION_ID = ? and SEARCH_ID = ?");
			pstmt.setString(1, this.sSessionId);
			pstmt.setString(2, searchID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				String xmlString = rs.getString("QRY");
				q = new Query(xmlString);
			}

		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}

			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

		return q;

	}

   /**
	*	gets query in SESSION_HISTORY with Session Id and sno
	*
	* 	@param int sno
	* 	@return Query
	* 	@exception History
	*/

	public Query getQuery(int serialNo) throws InfrastructureException {
			Query q = null;
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			ConnectionBroker broker = null;

		try {
				broker = ConnectionBroker.getInstance();
				con = broker.getConnection(DatabaseConfig.SESSION_POOL);
				pstmt = con.prepareStatement("select QRY from SESSION_HISTORY where SESSION_ID = ? and sno = ?");
				pstmt.setString(1, this.sSessionId);
				pstmt.setInt(2, serialNo);
				rs = pstmt.executeQuery();
			if (rs.next()) {
					String xmlString = rs.getString("QRY");
					q = new Query(xmlString);
				}

		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, e);
		} finally {
			if (rs != null) {
				try {
						rs.close();
				} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

			if (pstmt != null) {
				try {
						pstmt.close();
				} catch (Exception e1) {
						e1.printStackTrace();
					}

				}

			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);

				} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}

			return q;

	}


	/**
	*	stores query in CHEM_SESSION_INFO with Session Id as key
	*
	 * @param Query
	 *            InQuery
	* 	@return void
	* 	@exception History
	*/

	public void storeXMLQuery(Query inQuery, boolean insertOnlyInZeroPosition) throws InfrastructureException {

		Connection con = null;
		ConnectionBroker broker = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String query=null;
		try {

			inQuery.setDisplay(false);
			query=inQuery.toXMLString();

			String searchID = inQuery.getID();
			//sub sequence for a particular session

			int nSequenceNumber=0;

			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			stmt=con.createStatement();

			// gets the maximum sequence number that is there in the database
			// for this session id
			//Here we check if the search comes from search history
			// If comes from search history we will not add session_history
			// tables maximum position
			if (insertOnlyInZeroPosition) {
				try {
					rset=stmt.executeQuery("select max(sno) from SESSION_HISTORY where SESSION_ID='"+sSessionId+"'");
					while (rset.next()) {

						nSequenceNumber=rset.getInt(1);
					}

					nSequenceNumber+=1;
				} finally {
					if (rset != null) {
						try {
							rset.close();
							rset = null;
						} catch (Exception e) {
						}
						}
					}

				// inserting the query record into the Session Info table

				try {
					pstmt = con.prepareStatement("insert into SESSION_HISTORY (session_id, sno, search_id, qry) values(?,?,?,?)");
					pstmt.setString(1, sSessionId);
					pstmt.setInt(2, nSequenceNumber);
					pstmt.setString(3, searchID);
					pstmt.setString(4, query);
					pstmt.executeUpdate();
				} finally {
					if (pstmt != null) {
						try {
							pstmt.close();
							pstmt = null;
						} catch (Exception e) {
						}
						}
					}
				}

			int count=0;
			try {
				rset = stmt.executeQuery("select * from SESSION_HISTORY where SESSION_ID='"+sSessionId+"' and  sno=0 ");
				if (rset.next()) {
					count++;
				}
			} finally {
				if (rset != null) {
					try {
						rset.close();
						rset = null;
					} catch (Exception e) {
					}
					}
				}

			try {
				if (count > 0) {
					pstmt = con.prepareStatement("update SESSION_HISTORY set search_id=?,qry=? where SESSION_ID=? and  sno=0");
					pstmt.setString(1, searchID);
					pstmt.setString(2, query);
					pstmt.setString(3, sSessionId);
					pstmt.executeUpdate();
				} else {
					// If row is not present in the zeroth position then we
					// insert query into that row
					pstmt = con.prepareStatement("insert into SESSION_HISTORY (session_id, sno, search_id, qry) values(?,?,?,?)");
					pstmt.setString(1, sSessionId);
					pstmt.setInt(2, 0);
					pstmt.setString(3, searchID);
					pstmt.setString(4, query);
					pstmt.executeUpdate();
				}
			} finally {
				if (pstmt != null) {
					try {
						pstmt.close();
						pstmt = null;
					} catch (Exception e) {
					}
					}
				}
		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
				}
			}

			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (ConnectionPoolException cpe) {
				}
		  	}
		}
	}

	/**
	*	updates the query in SESSION_INFO with Session Id as key
	*
	 * @param Query
	 *            InQuery
	* 	@return void
	* 	@exception History
	*/

	public void updateXMLQuery(Query inQuery) throws InfrastructureException {

		Connection con = null;
		ConnectionBroker broker = null;
		Statement updateStmt=null;
		String query=null;
		try {
			inQuery.setDisplay(false);
            query= inQuery.toXMLString();

			String searchID = inQuery.getID();
			//sub sequence for a particular session

			int nSequenceNumber=0;

			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);

			updateStmt = con.createStatement();
			updateStmt.executeUpdate("update SESSION_HISTORY set qry='"+query+"' where SESSION_ID='"+sSessionId+"' and search_id='"+searchID+"'");

		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, e);
		} finally {
		    if(updateStmt != null) {
				try {
					updateStmt.close();
				} catch (SQLException sqle) {
				}
			}

			if(con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (ConnectionPoolException cpe) {
				}
		  	}
		}
	}

	/**
	*
	*	gets all the queries as XML data for a particular session
 	*
	* 	@return String XMLData
	* 	@exception HistoryException
	*
	*/

	public String getXMLQuery() throws InfrastructureException {

		StringBuffer query=new StringBuffer().append("<SESSION-HISTORY>");
		Connection con = null;
		ConnectionBroker broker = null;
		Statement stmt = null;
		ResultSet rset = null;

		try {

			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);

			stmt = con.createStatement();
			rset = stmt.executeQuery("select QRY from SESSION_HISTORY where session_id='"+sSessionId+"' and sno>0 order by sno ");

			while (rset.next()) {
				query.append(rset.getString(1));
			}

		} catch (Exception sqle) {
			throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, sqle);
		} finally {

			if (rset != null) {
				try {
					rset.close();
				} catch (SQLException e1) {

				}
		    	}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqle) {

				}

		    	}

			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (ConnectionPoolException cpe) {

				}
		  	}
	  	}

		query.append("</SESSION-HISTORY>");
		return query.toString();
	}


	/**
	*
	*	gets most recently executed queries of XML data for a particular session.
	 * The number of queries depending on history page size in run time
	 * properties.
	 * 
 	*   @param int historysize
	* 	@return String XMLData
	* 	@exception HistoryException
	*
	*/

	public String getXMLQuery(int historysize) throws InfrastructureException {

		StringBuffer query=new StringBuffer().append("<SESSION-HISTORY>");
		Connection con = null;
		ConnectionBroker broker = null;
		Statement stmt = null;
		ResultSet rset = null;
		String xmlString=null;

		try {

			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);

			stmt = con.createStatement();
			rset = stmt.executeQuery("select qry,sno from(select sno,qry from session_history where SESSION_ID='" + sSessionId
					+ "' and sno <> 0 order by sno desc) where rownum<=" + historysize + " order by sno");

			while (rset.next()) {
				xmlString=rset.getString(1);
				query.append(xmlString.substring(0,xmlString.trim().length()-15));
				query.append("<SERIAL-NUMBER>"+rset.getInt(2)+"</SERIAL-NUMBER>");
				query.append("</SESSION-DATA>");
			}

		} catch (Exception sqle) {
			throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, sqle);
		} finally {

			if (rset != null) {
				try {
					rset.close();
				} catch (SQLException e1) {

				}
		    	}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqle) {

				}

		    	}

			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (ConnectionPoolException cpe) {

				}
		  	}
	  	}

		query.append("</SESSION-HISTORY>");
		return query.toString();
	}



	/**
	 * clears the session history for a particular session except the recent
	 * query of that session
	*
	*  	@return void
	*  	@exception HistoryException
	*/

	public void clearSessionHistory(boolean flag) throws InfrastructureException {
		Connection con = null;
		ConnectionBroker broker = null;
		Statement stmt = null;

		try {
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);

			stmt=con.createStatement();
			if (flag) {
				stmt.executeUpdate("delete from SESSION_HISTORY where session_id='"+sSessionId+"'");
			} else {
				stmt.executeUpdate("delete from SESSION_HISTORY where session_id='"+sSessionId+"' and sno>0");
			}

		} catch (Exception e) {
			throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqle) {

				}

			}

			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (ConnectionPoolException cpe) {

				}
		  	}
		}
	}



	/**
	*	gets the recent query in the table with the given sesison id
	*
	*  	@return String Query as XML String
	 * @exception SQLException
	 *                ,ConnectionPoolException,NoConnectionAvailableException
	*/

	public String getRecentXMLQuery() throws InfrastructureException {

		String query=null;

		Connection con = null;
		ConnectionBroker broker = null;
		Statement stmt = null;
		ResultSet rset = null;

		try {

			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);

			stmt=con.createStatement();
			rset=stmt.executeQuery("select QRY from SESSION_HISTORY where session_id='"+sSessionId+"' and sno=0");

			while (rset.next()) {
				query=rset.getString(1);
			}
		} catch (Exception e) {

			throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, e);
		} finally {

			if (rset != null) {
				try {
					rset.close();
				} catch (SQLException e1) {

				}
		    }

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqle) {

				}
			}

			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (ConnectionPoolException cpe) {

				}
				}
		  	}

		return query;
	}

	/**
	*	gets the recent query in the table with the given sesison id
	*
	*  	@return String Query as XML String
	 * @exception SQLException
	 *                ,ConnectionPoolException,NoConnectionAvailableException
	*/

	public String getXMLQuery(String searchID) throws InfrastructureException {

		String query=null;

		Connection con = null;
		ConnectionBroker broker = null;
		Statement stmt = null;
		ResultSet rset = null;

		try {

			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);

			stmt=con.createStatement();
			rset=stmt.executeQuery("select QRY from SESSION_HISTORY where session_id='"+sSessionId+"' and search_id='"+searchID+"'");

			while (rset.next()) {
				query=rset.getString(1);
			}
		} catch (Exception e) {

			throw new InfrastructureException(SystemErrorCodes.HISTORY_ERROR, e);
		} finally {

			if (rset != null) {
				try {
					rset.close();
				} catch (SQLException e1) {

				}
		    	}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqle) {

				}
		    	}

			if (con != null) {
				try {
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				} catch (ConnectionPoolException cpe) {

				}
		  	}
	  	}

		return query;
	}


}
