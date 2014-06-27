package org.ei.system;

import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.List;

import org.ei.email.SESEmail;
import org.ei.email.SESMessage;

public class DatabaseMonitor
{
    private static String username;
    private static String password;
    private static String url;
    private static String driver;
    private static String primaryMailServer;
    private static String secondaryMailServer;
    private static String emailAddress1;
    private static String emailAddress2;
    private static String databaseid;
    private static GregorianCalendar cal = new GregorianCalendar();

    private static boolean deleteLock = true;

    public static void main(String args[])
        throws Exception
    {
        username =  args[0];
        password = args[1];
        url = args[2];
        driver = args[3];
        emailAddress1 = args[4];
        emailAddress2 = args[5];
        primaryMailServer = args[6];
        secondaryMailServer = args[7];
        databaseid = args[8];

        Connection con = null;
        File lockfile = null;
        try
        {
            String filename = databaseid+".lock";
            lockfile = new File(filename);
            if(lockfile.exists())
            {
                deleteLock = false;
                throw new Exception("Database lockfile remains");
            }

            FileWriter lockWriter = null;
            try
            {
                lockWriter = new FileWriter(filename);
                lockWriter.write("a");
            }
            finally
            {
                if(lockWriter != null)
                {
                    lockWriter.close();
                }
            }

            con = getConnection();
            long ttime = System.currentTimeMillis();
            Statement stmt1 = null;
            try
            {
            	stmt1 = con.createStatement();
            	stmt1.executeUpdate("update test_table set test_value = "+Long.toString(ttime));
			}
			finally
			{
				stmt1.close();
			}

			Statement stmt2 = null;
			ResultSet rs = null;

            try
            {
            	stmt2 = con.createStatement();
            	rs = stmt2.executeQuery("select * from test_table");
            	if(rs.next())
            	{
            	    long test_value = rs.getLong("test_value");
            	    if(test_value != ttime)
            	    {
            	        throw new Exception("Bad data returned from database");
            	    }
            	}
            	else
            	{
            	    throw new Exception("Bad data returned from database");
            	}
			}
			finally
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

				try
				{
					stmt2.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

            System.out.print(getTimeDisplay());
            System.out.print(" ");
            System.out.print(url);
            System.out.print(" ");
            System.out.println("UP");
        }
        catch(Exception e)
        {
            System.out.print(getTimeDisplay());
            System.out.print(" ");
            System.out.println(url);
            sendAlert(e);

            throw e;
        }
        finally
        {

            try
            {
                if(con != null)
                {
                    con.close();
                }
            }
            catch(Exception e1)
            {

            }

            try
            {
                if(deleteLock)
                {
                    lockfile.delete();
                }
            }
            catch(Exception e1)
            {
                e1.printStackTrace();
            }
        }
    }

    private static String getTimeDisplay()
    {
        StringBuffer buf = new StringBuffer();
        int m = cal.get(Calendar.MONTH);
        buf.append(++m);
        buf.append("-");
        buf.append(cal.get(Calendar.DAY_OF_MONTH));
        buf.append("-");
        buf.append(cal.get(Calendar.YEAR));
        buf.append(":");
        buf.append(cal.get(Calendar.HOUR));
        buf.append(":");
        buf.append(cal.get(Calendar.MINUTE));
        buf.append(":");
        buf.append(cal.get(Calendar.SECOND));
        return buf.toString();

    }



    private static Connection getConnection()
        throws Exception
    {
        Class.forName(driver);
        return DriverManager.getConnection(url,
                                           username,
                                           password);
    }

    private static void sendAlert(Exception e)
    {
        StringBuffer sb = new StringBuffer(url);
        sb.append("--");
        sb.append(e.getMessage());

        try
        {
        	
        	SESMessage sesMessage = new SESMessage();
        	sesMessage.setMessage("Database alert", sb.toString(), false);
        	sesMessage.setFrom("eicustomersupport@elsevier.com");
        	List<String> toList = new ArrayList<String>();
        	toList.add(emailAddress1);
        	toList.add(emailAddress2);
        	sesMessage.setDestination(toList);
        	SESEmail.getInstance().send(sesMessage);
       }
       catch(Exception e1)
       {
            e1.printStackTrace();
            try
            {
                
            	SESMessage sesMessage = new SESMessage();
            	sesMessage.setMessage("Database alert", sb.toString(), false);
            	sesMessage.setFrom("eicustomersupport@elsevier.com");
            	List<String> toList = new ArrayList<String>();
            	toList.add(emailAddress1);
            	toList.add(emailAddress2);
            	sesMessage.setDestination(toList);
            	SESEmail.getInstance().send(sesMessage);
            }
            catch(Exception e2)
            {
                e2.printStackTrace();
            }
       }
    }
}

