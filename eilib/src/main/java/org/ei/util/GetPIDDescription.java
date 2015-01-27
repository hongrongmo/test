package org.ei.util;

import org.apache.log4j.Logger;
import org.ei.domain.ClassNodeManager;
import org.ei.domain.ClassTitleDisplay;
import org.ei.domain.DatabaseConfig;
import org.ei.data.upt.loadtime.IPCClassNormalizer;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;
import org.ei.connectionpool.*;
import org.ei.data.upt.loadtime.UPTCombiner;

import java.io.IOException;
import java.sql.*;

public class GetPIDDescription {
    private final static Logger log4j = Logger.getLogger(GetPIDDescription.class);

    public static String getDescription(String code) throws InfrastructureException {
        String description = "";
        String ipcCode = "";
        String ipcName = "";
        try {
            ClassNodeManager cManager = ClassNodeManager.getInstance();

            if (code != null && !code.equals("")) {
                if (code.indexOf("[]") > -1) {
                    int delimitPosition = code.indexOf("[]");
                    if (delimitPosition > 0) {
                        ipcCode = code.substring(0, delimitPosition);
                        ipcName = code.substring(delimitPosition + 2);
                    }
                    description = ClassTitleDisplay.getDisplayTitle(cManager.seekIPC(IPCClassNormalizer.normalize(ipcCode), 1));
                } else {
                    description = ClassTitleDisplay.getDisplayTitle(cManager.seekIPC(IPCClassNormalizer.normalize(code), 1));
                }
            }

            if (description.indexOf("No data available") > 0 && ipcName.length() > 0) {
                description = "<UL><li><b>" + ipcName + "</b></li></UL>";
            }

        } catch (IOException e) {
            log4j.error(e);
            throw new InfrastructureException(SystemErrorCodes.PID_DESCRIPTION_ERROR, e);
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return description;
    }

    public static String getDescription(String code, String description) throws InfrastructureException {
        String dp = "";
        try {

            ClassNodeManager cManager = ClassNodeManager.getInstance();

            if (code != null && !code.equals("")) {

                dp = ClassTitleDisplay.getDisplayTitle(cManager.seekIPC(IPCClassNormalizer.normalize(code), 1));

            }

            if (dp.indexOf("No data available") > -1) {
                dp = "<UL><li><b>" + description + "</b></li></UL>";
            }

            if (dp != null && !dp.equals("")) {
                dp = dp.replaceAll("\"", "&#34;");
            }

        } catch (IOException e) {
            log4j.error(e);
            throw new InfrastructureException(SystemErrorCodes.PID_DESCRIPTION_ERROR, e);
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return dp;
    }

    public String getDescriptionFromLookupIndex(String code)
	{
		Connection con=null;
		Statement stmt=null;
		ResultSet rset=null;
		String description=null;
		//ConnectionBroker broker=null;
		String sql = "select description from CMB_IPC_LOOKUP WHERE replace(ipccode,'SLASH','')='"+code+"'";
		int rows = 0;
		
		//HH 01/21/2015 update to be as  eijava, otherwise give pat extract issue

		try
		{
			//broker=ConnectionBroker.getInstance();
			//con=broker.getConnection(DatabaseConfig.SEARCH_POOL);
			con = getConnection(UPTCombiner.url,UPTCombiner.driver,UPTCombiner.username,UPTCombiner.password);
			stmt = con.createStatement();
			rset=stmt.executeQuery(sql);
        	while(rset.next())
			{
				description = rset.getString("description");
			}

		}
		catch(Exception e)
		{
			System.out.println("there is a problem on this code "+code);
			e.printStackTrace();
		}
		finally
		{
			if(rset != null)
			{
				try
				{
					rset.close();
				}
				catch(Exception se)
				{
				}
			}

			if(stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch(Exception se)
				{
				}
			}

			if(con != null)
			{
				try
				{
					//broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
					con.close();
				}
				catch(Exception e1)
                {}
			}
        }

        return description;
	}

	 public Connection getConnection(String connectionURL,
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
