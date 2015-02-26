package org.ei.data.inspec.runtime;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DataDictionary;
import org.ei.domain.DatabaseConfig;
import org.ei.thesaurus.*;

public class InspecDataDictionary
    implements DataDictionary
{
    private static Hashtable<String, String> classCodes;
    private Hashtable<String, String> treatmentCodes;


    //HH 01/16/2015
    private static InspecDataDictionary instance = null;
    public static InspecDataDictionary getInstance()
    {
    	if(instance == null)
    	{
    		synchronized (InspecDataDictionary.class)
    		{
    			instance = new InspecDataDictionary();
    			try
    			{
    				classCodes = instance.loadClassCodes();
    			}

    			catch(Exception e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}
    	return instance;
    }


    public String getClassCodeTitle(String classCode)
    {
        //HH 01/14/2015
        String classCodeTitle = null;
        try
        {
            if (classCode != null)
            {
                classCodeTitle = (String)classCodes.get(classCode.toUpperCase());

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return classCodeTitle;
    }

    public Hashtable<String, String> getClassCodes()
    {
        return this.classCodes;
    }

    public Hashtable<String, String> getTreatments()
    {
        return this.treatmentCodes;
    }

    public InspecDataDictionary()
    {

       treatmentCodes = new Hashtable<String, String>();

       treatmentCodes.put("A","Applications (APP)");
       treatmentCodes.put("B", "Bibliography (BIB)");
       treatmentCodes.put("E","Economic (ECO)");
       treatmentCodes.put("X","Experimental (EXP)");
       treatmentCodes.put("G","General or Review (GEN)");
       treatmentCodes.put("H","Historical (HIS)");
       treatmentCodes.put("L","Literature review (LIT)");
       treatmentCodes.put("M","Management aspects (MAN)");
       treatmentCodes.put("N", "New development (NEW)");
       treatmentCodes.put("R","Product review (PRO)");
       treatmentCodes.put("P","Practical (PRA)");
       treatmentCodes.put("T","Theoretical or Mathematical (THR)");

    }

    public Hashtable<String, String> getAuthorityCodes()
    {
        return null;
    }

    public String getTreatmentTitle(String mTreatmentCode)
    {
        return null;
    }


    /****
     * Author: HT
     * Date: 01/14/2015
     * Desc: retrieve Ins Class codes & title dynamically from DB table instead of statically specified in Prog
     */

    public Hashtable<String, String> loadClassCodes ()
                        throws ThesaurusException
    {
        Hashtable<String,String> classCodesList = new Hashtable<String,String>();

        ConnectionBroker broker = null;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;


        StringBuffer buf = new StringBuffer("select class_code,class_title from INSPEC_CLASSIFICATION");

        try
        {

			try
			{
				broker = ConnectionBroker.getInstance();
            	con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
			}
			catch(Exception	 e)
			{
				//added for dataloading, need to think about it what to do later
				//e.printStackTrace();
				System.out.println("running from dataloading");
				if(con == null)
				{
					con = getConnection();
				}
			}
            stmt = con.createStatement();

            rs = stmt.executeQuery(buf.toString());

            while (rs.next())
            {
                if((rs.getString("CLASS_CODE") !=null) && (rs.getString("CLASS_TITLE") !=null))
                {
                    classCodesList.put(rs.getString("CLASS_CODE"),rs.getString("CLASS_TITLE"));
                }

            }

        }
        catch(Exception ex)
        {

            throw new ThesaurusException(ex);
        }

        finally
        {
            if(rs !=null)
            {
                try
                {
                    rs.close();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }

            }

            if(stmt !=null)
            {
                try
                {
                    stmt.close();
                }
                catch(Exception e2)
                {
                    e2.printStackTrace();
                }
            }

            if (con != null) {
                try {
					if(broker!=null)
                    	broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
					else
						con.close();
                } catch (Exception cpe) {
                    cpe.printStackTrace();
                }
            }
        }
        return classCodesList;
    }

	public Connection getConnection() throws Exception
	{

			String connectionURL="jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";//used for cloud dataloading
			//String connectionURL="jdbc:oracle:thin:@127.0.0.1:5521:eid";//use for local machine
			String driver="oracle.jdbc.driver.OracleDriver";
			String username="ap_ev_search";
			String password="ei3it";
			System.out.println("connectionURL= "+connectionURL);
			System.out.println("driver= "+driver);
			System.out.println("username= "+username);
			System.out.println("password= "+password);

			Class.forName(driver);
			Connection con = DriverManager.getConnection(connectionURL,
										  username,
										  password);
			return con;
	}
}
