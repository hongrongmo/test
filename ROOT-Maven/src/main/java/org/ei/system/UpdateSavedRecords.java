package org.ei.system;

/** project specific imports*/
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.ei.config.ApplicationProperties;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.Query;
import org.ei.util.StringUtil;

/**
 *
 * Handles EmailAlert related tasks such as
 * <p> a)This object gets distinct userIds and his email address where emailalerts is not null.</p>
 * <p> b)For each userId gets query object where emailAlert is not null. </p>
 * <p> c)For query object it call corresponding SearchControl depending on database of query.
 *      From that compenent gets first 25 documents DocIDs list.</p>
 * <p> d)This DocIds list is send to the EmailAlertResultsManager and then call Email.java to send mail.</p>
 *
 */

public class UpdateSavedRecords {

	private ApplicationProperties eiProps;

	private ConnectionBroker m_broker = null;
	private String m_strPoolname = StringUtil.EMPTY_STRING;
	private String m_strPoolsFilename = StringUtil.EMPTY_STRING;
	private String m_strPropertiesFile = "./etc/emailalert.properties";


	public void cleanup()
	{
		if(m_broker != null)
		{
			try {
				m_broker.closeConnections();
			} catch(ConnectionPoolException cpe) {
			}
		}
	}
	/**
	* This constructor reads runtimeProperties and gets size of documents to saend as mails.
	* and sends mails.
	*/
	private UpdateSavedRecords() {

		try {

			m_strPoolname = DatabaseConfig.SESSION_POOL;

			eiProps = ApplicationProperties.getInstance(m_strPropertiesFile);
			m_strPoolsFilename = eiProps.getProperty("POOLSFILENAME");
			m_broker = ConnectionBroker.getInstance(m_strPoolsFilename);


		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void updateSavedRecords() throws Exception
	{
		Connection con = null;
		PreparedStatement outerStmt = null;
		PreparedStatement innerStmt = null;
		ResultSet rset = null;
		int i = 0;
		Query queryObject=null;

		try
		{
			System.out.println("Starting...");

			con = m_broker.getConnection(m_strPoolname);

			outerStmt = con.prepareStatement("SELECT SAVED_RECORDS.RECORD_ID, E_JAVA.CPX_MASTER.M_ID FROM E_JAVA.CPX_MASTER, E_JAVA.CPX_MASTER_OLD, SAVED_RECORDS WHERE E_JAVA.CPX_MASTER.EX =  E_JAVA.CPX_MASTER_OLD.EX AND E_JAVA.CPX_MASTER_OLD.M_ID = SAVED_RECORDS.GUID");
			rset = outerStmt.executeQuery();


			while (rset.next())
			{

				String strRECORD_ID= rset.getString("RECORD_ID");
				String strM_ID = rset.getString("M_ID");
				System.out.println(strRECORD_ID + "\t" + strM_ID);
				innerStmt= (CallableStatement)con.prepareCall("{ call UpdateSavedRecords_SavedRecord(?,?)}");
				//innerStmt = con.prepareStatement("UPDATE SAVED_RECORDS SET GUID=? WHERE RECORD_ID=?");
				innerStmt.setString(1,strM_ID);
				innerStmt.setString(2,strRECORD_ID);
				try
				{
					innerStmt.execute();
					System.out.println("Updated");
				}
				catch(SQLException sqle)
				{
					System.out.println("Failed");
				}


				if(innerStmt != null)
				{
					try
					{
						innerStmt.close();
					}
					catch(SQLException sqle)
					{
						System.out.println("Exception");
					}
				}
				i++;

			}
			System.out.println("count = \t" + i);


    		} catch(SQLException se) {
    			try {
    				if(con != null)
    					con.rollback();
    			} catch(SQLException subSe) {
    			}
			throw new Exception(se.getMessage());

		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(rset != null) {
				try {
					rset.close();
				} catch(SQLException e1) {
				}
			}

			if(outerStmt != null) {
				try {
					outerStmt.close();
				} catch(SQLException sqle) {
				}
			}
			if(innerStmt != null) {
				try {
					innerStmt.close();
				} catch(SQLException sqle) {
				}
			}
			if(con != null) {
				try {
					m_broker.replaceConnection(con,m_strPoolname);
				} catch(ConnectionPoolException cpe) {
				}
			}
		 }

	}

	public static void main(String[] args) {

	//java -cp ;./lib/eilib.jar;./lib/xerces.jar;./lib/oracle-jdbc.jar; org.ei.system.UpdateSavedRecords

		UpdateSavedRecords updInstance = new UpdateSavedRecords();
		try{

			updInstance.updateSavedRecords();

		} catch(Exception e) {

			System.out.println("Exception e" + e.getMessage());

		} finally {
			if(updInstance != null)
			{
				System.out.println("Cleaning up Connections");
				updInstance.cleanup();
			}
		}


		System.exit(0);


	}

}





