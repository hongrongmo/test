package org.ei.dataloading.inspec.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.domain.DataDictionary;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DriverConfig;

import org.ei.common.*;


public class InspecCheckCorrections
{

	public static final int MATCH_CASE_INSENSITIVE = 4;
    public static final int REPLACE_FIRST = 2;
    private String serverLocation;
	private String m_strPoolname = "search";
	private String m_strPoolsFilename = "./etc/pools.xml";
	private final static int INSPECMASK = 2;
    private final static String INSPECSTRING = "ins";

    protected static Log log = LogFactory.getLog(InspecCheckCorrections.class);

	private ConnectionBroker m_broker = null;

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
	public InspecCheckCorrections() {

		try {

			m_broker = ConnectionBroker.getInstance(m_strPoolsFilename);

		} catch(Exception e) {
			log.error(e);
			System.exit(0);
		}
    }


	public static void main(String[] args)throws Exception
	{

 		 InspecCheckCorrections test = new InspecCheckCorrections();
 		 test.begin();

	}

	 public void begin ()
		throws Exception
	{

		try
		{
			getCorrectionData();

		}
		catch(Exception e1)
		{
			e1.printStackTrace();
		}
		finally
		{
			/*if(con != null)
			{
				try
				{
					con.close();
				}
				catch(Exception e2)
				{
					e2.printStackTrace();
				}
			} */
		}
	}

	public void getCorrectionData()
				throws Exception
	{

		Statement p_stmt  = null;
		ResultSet p_rs    = null;
		 Connection con = null;
         con = m_broker.getConnection(m_strPoolname);

		try
		{
			DatabaseConfig dbconf = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
			if(dbconf == null)
			{
				 System.exit(-1);
			}

			Database ins = dbconf.getDatabase(INSPECSTRING);
			if(ins == null)
			{
				 System.exit(-1);
			}
			p_stmt = con.createStatement();

			p_rs = p_stmt.executeQuery("select anum, cls from TMPINS_MASTER");

			while(p_rs.next())
			{

				String anum = null;
				String cls 	= null;

					//  Get All Possible Variables

					//ANUM
					if(p_rs.getString("ANUM") != null)
					{
						anum = p_rs.getString("ANUM").trim();
					}
					//CLS
					if(p_rs.getString("CLS") != null)
					{
						cls = p_rs.getString("CLS").trim();
					}

				//compareCls(anum,cls,con);
				checkClassCodes(anum, cls, ins);

			}
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
		}
		finally
		{
			if(p_rs != null)
			{
				try
				{
					p_rs.close();
				}
				catch(Exception e2)
				{
					e2.printStackTrace();
				}
			}
			if(p_stmt != null)
			{
				try
				{
					p_stmt.close();
				}
				catch(Exception e3)
				{
					e3.printStackTrace();
				}
			}
			if(con != null)
			{
				try
				{
					m_broker.replaceConnection(con,m_strPoolname);
				}
				catch(ConnectionPoolException cpe)
				{
				}
            }
		}
	}

	public void checkClassCodes(String anum, String cls, Database db)
	{
		DataDictionary dataDictionary = db.getDataDictionary();
		PrintWriter outFile = null;
		try
		{

			outFile = new PrintWriter(new FileWriter("MissingCodes.txt", true));
			if (anum != null && cls != null) {
				String[] ccodes = (cls.split(Constants.AUDELIMITER));
				if(ccodes != null)
				{
					for(int i = 0; i < ccodes.length; i++)
					{
						String cctitle = dataDictionary.getClassCodeTitle(ccodes[i]);
						if(cctitle == null)
						{
							outFile.println("missing code: " + ccodes[i]);
						}

					}
				}
			}
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
		}
		finally
		{

			if(outFile != null)
			{
				try
				{
					outFile.close();
				}
				catch(Exception e2)
				{
					e2.printStackTrace();
				}
			}
		}

    }

    public static void compareCls(String anum, String cls, Connection con)
    	throws Exception
    {
		Statement c_stmt  = null;
		ResultSet c_rs    = null;

		PreparedStatement c_pstmt_u = null;
		PreparedStatement c_pstmt_i = null;

		String master_anum  = null;
		String master_cls 	= null;

	    PrintWriter outFile = null;
	    String replacedcls = null;


		try
		{

			outFile = new PrintWriter(new FileWriter("MatchedRecords.txt", true));

			c_stmt = con.createStatement();

			StringBuffer sqlQuery  = new StringBuffer("select anum, cls from INS_MASTER  WHERE ANUM = '"+anum+ "' ");

			if (anum != null && cls != null) {
				c_rs = c_stmt.executeQuery(sqlQuery.toString());
				System.out.println("anum:"+anum);
				while(c_rs.next())
				{
					//ANUM
					if(c_rs.getString("ANUM") != null)
					{
						master_anum = c_rs.getString("ANUM").trim();
					}
					//CLS
					if(c_rs.getString("CLS") != null)
					{
						master_cls = c_rs.getString("CLS").trim();
					}

					if(cls != null  && master_cls != null) {

						replacedcls = cls.replace((char)30,';');

						if((anum.equals(master_anum)) && !(master_cls.equals(replacedcls)))
						{
								//***Write To File here
							outFile.println("Comparing anum: "+anum+""+"  liverecord: "+master_cls+"  | Correction:" +replacedcls);
						}

					}

				}
			}
		}
		catch(Exception e1)
		{
			e1.printStackTrace();
		}
		finally
		{
			if(c_rs != null)
			{
				try
				{
					c_rs.close();
				}
				catch(Exception e2)
				{
					e2.printStackTrace();
				}
			}
			if(c_stmt != null)
			{
				try
				{
					c_stmt.close();
				}
				catch(Exception e3)
				{
					e3.printStackTrace();
				}
			}
			if(c_pstmt_u != null)
			{
				try
				{
					c_pstmt_u.close();
				}
				catch(Exception e4)
				{
					e4.printStackTrace();
				}
			}
			if(c_pstmt_i != null)
			{
				try
				{
					c_pstmt_i.close();
				}
				catch(Exception e5)
				{
					e5.printStackTrace();
				}
			}
			if(outFile != null)
			{
				try
				{
					outFile.close();
				}
				catch(Exception e2)
				{
					e2.printStackTrace();
				}
			}
		}
	}


	private static Connection getConnection(String connectionURL,
											 String driver,
											 String username,
											 String password)
		throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL,
										  username,
										  password);
		return con;
	}


}
