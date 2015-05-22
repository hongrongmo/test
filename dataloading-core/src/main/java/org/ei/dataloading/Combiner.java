package org.ei.dataloading;


import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;


public abstract class Combiner
{

    public static String TABLENAME;
    public static String CURRENTDB;
    public static int EXITNUMBER;

    protected CombinedWriter writer;



    public Combiner(CombinedWriter writer)
    {
        this.writer = writer;
    }

    public void writeCombinedByYear(String connectionURL,
                                    String driver,
                                    String username,
                                    String password,
                                    int year)
            throws Exception
        {


            Connection con = null;
            PrintStream out = null;

            try
            {
                con = getConnection(connectionURL,
                                    driver,
                                    username,
                                    password);



                writeCombinedByYearHook(con,
                                        year);
            }
            finally
            {

                if(con != null)
                {
                    try
                    {
                        con.close();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                if(out != null)
                {
                    try
                    {

                        out.close();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void writeCombinedByTable(	String connectionURL,
                							String driver,
                							String username,
                							String password) throws Exception
        {
        	 Connection con = null;

             try
             {
                 con = getConnection(connectionURL,
                                     driver,
                                     username,
                                     password);



                 writeCombinedByTableHook(con);
             }
             finally
             {

                 if(con != null)
                 {
                     try
                     {
                         con.close();
                     }
                     catch(Exception e)
                     {
                         e.printStackTrace();
                     }
                 }
             }
        
        }
    	public abstract void writeCombinedByTableHook(Connection con) throws Exception;

        public abstract void writeCombinedByYearHook(Connection con,
                                                     int year)
                        throws Exception;



        public abstract void writeCombinedByWeekHook(Connection con,
                                                     int week)
            throws Exception;








        public void writeCombinedByWeekNumber(String connectionURL,
                                              String driver,
                                              String username,
                                              String password,
                                              int weekNumber)
            throws Exception
        {

            Connection con = null;
                        PrintStream out = null;

            try
            {


                con = getConnection(connectionURL,
                                    driver,
                                    username,
                                    password);


                writeCombinedByWeekHook(con,
                                        weekNumber);

            }
            finally
            {
                if(con != null)
                {
                    try
                    {
                        con.close();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }


                if(out != null)
                {
                    try
                    {
                        out.close();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }

            }


        }




        protected Connection getConnection(String connectionURL,
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
