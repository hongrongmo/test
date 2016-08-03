package org.ei.dataloading.cafe;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.util.regex.*;
import java.util.zip.*;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.domain.*;
import org.ei.dataloading.CombinedXMLWriter;
import org.ei.query.base.FastQueryWriter;
import org.ei.util.GUID;

public class AuthorProfileCorrection 
{
	Perl5Util perl = new Perl5Util();	
	private static String tablename;	
	private static String currentDb;	
	private static Connection con = null;
	static String url="jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
	static String driver="oracle.jdbc.driver.OracleDriver";
	static String username="ap_correction1";
	static String password="ei3it";
	static String database;
	static String action;
	static String elasticSearchUrl;
	static int updateNumber=0;
	static boolean test = false;
	static String tempTable="au_profile_correction_temp";
	static String addTable="au_profile_correction_insert";
	static String deleteTable="au_profile_correction_delete";
	static String sqlldrFileName="authorProfileCorrectionFileLoader.sh";
	public static final String AUDELIMITER = new String(new char[] {30});
	public static final String IDDELIMITER = new String(new char[] {31});
	public static final String GROUPDELIMITER = new String(new char[] {02});
	private static long startTime = System.currentTimeMillis();
	private static long endTime = System.currentTimeMillis();
	private static long midTime = System.currentTimeMillis();
	private static String filename = "";
	private static String path="";
	private static String bucketName="";
	private static String key="";

	
		
	public static void main(String args[])
	        throws Exception
	{
	        
		String fileToBeLoaded   = null;
		String input;
		String tableToBeTruncated = "au_profile_correction_temp,au_profile_correction_insert,au_profile_correction_delete";
		int iThisChar; // To read individual chars with System.in.read()

		try
		{
		    do
		    {
		        System.out.println("do you want to run test mode");
		        iThisChar = System.in.read();
		        if(iThisChar!=121 && iThisChar!=110)
		        {
		            System.out.println("please enter y or n");
		        }
		        else if(iThisChar==121)
		        {
		            test = true;
		            Thread.currentThread().sleep(1000);
		        }
		        else if(iThisChar==110)
		        {
		            test = false;
		            Thread.currentThread().sleep(1000);
		        }
		    }
		    while(iThisChar!=121 && iThisChar!=110);
		
		}
		catch (Exception ioe)
		{
		    System.out.println("IO error trying to read your input!");
		    System.exit(1);
		}


		if(args.length>9)
		{
		    if(args[5]!=null)
		    {
		        url = args[5];
		    }
		    if(args[6]!=null)
		    {
		        driver = args[6];
		    }
		    if(args[7]!=null)
		    {
		        username = args[7];
		        System.out.println("username= "+username);
		    }
		    if(args[8]!=null)
		    {
		        password = args[8];
		        System.out.println("password= "+password);
		    }
		    if(args[9]!=null)
		    {
		        sqlldrFileName = args[9];
		        System.out.println("using sqlloaderfile "+sqlldrFileName);
		    }
		    else
		    {
		        System.out.println("Does not have sqlldr file");
		        System.exit(1);		       
		    }
		}
		 
	    if(args.length>10)
	    {
	    	if(args[10] !=null)
	    	{
	    		bucketName = args[10];
	    		System.out.println("get correction files from S3 bucket: " + bucketName);
	    	}
	    }
			     
		
		if(args.length >11)
		{
			if(args[11] !=null)
			{
				key = args[11];
			}
		}

        if(args.length>5)
        {
            if(args[0]!=null)
            {
                fileToBeLoaded = args[0];
                if(fileToBeLoaded.indexOf("/")>-1)
    			{
    				
    				path = fileToBeLoaded.substring(0,fileToBeLoaded.lastIndexOf("/"));
    				filename = fileToBeLoaded.substring(fileToBeLoaded.lastIndexOf("/")+1);
    				
    			}
                else
                {
                	filename=fileToBeLoaded;
                }
              
            }

            if(args[1]!=null)
            {
                tableToBeTruncated = args[1];				
            }	          

            if(args[2]!=null && args[2].length()>0)
            {
                Pattern pattern = Pattern.compile("^\\d*$");
                Matcher matcher = pattern.matcher(args[2]);
                if (matcher.find())
                {
                    updateNumber = Integer.parseInt(args[2]);
                }
                else
                {
                    System.out.println("did not find updateNumber or updateNumber has wrong format");
                    System.exit(1);
                }
            }

            if(args[3]!=null)
            {
                action = args[3];
            }
            else
            {
                System.out.println("Are we doing 'update' or 'delete'");
                System.exit(1);
            }

            if(args[4]!=null)
            {
            	elasticSearchUrl= args[4];
            }
            else
            {
                System.out.println("Does not have elasticSearchUrl URL");
                System.exit(1);
            }
		}
		else
		{
		    System.out.println("not enough parameters");
		    System.exit(1);
		}

        midTime = System.currentTimeMillis();
        endTime = System.currentTimeMillis();
        System.out.println("Time for finish reading input parameter"+(endTime-startTime)/1000.0+" seconds");
	       
		try
		{

	        	AuthorProfileCorrection au = new AuthorProfileCorrection();
	            con = au.getConnection(url,driver,username,password);
	            if(action!=null)
	            {
	                /**********delete all data from temp table *************/

	                if(test)
	                {
	                    System.out.println("about to truncate table "+tableToBeTruncated);
	                    System.out.println("press enter to continue");
	                    Thread.currentThread().sleep(500);
	                    System.in.read();
	                    Thread.currentThread().sleep(1000);
	                }

	                au.cleanUp(tableToBeTruncated);

	                midTime = endTime;
	                endTime = System.currentTimeMillis();
	                System.out.println("time for truncate table "+(endTime-midTime)/1000.0+" seconds");
	                
	                /************** load data into temp table ****************/

	                if(test)
	                {
	                    System.out.println("about to parse data file "+fileToBeLoaded);
	                    System.out.println("press enter to continue");
	                    System.in.read();
	                    Thread.currentThread().sleep(1000);
	                }
	                	         
	                //read from filesystem
	                AuthorProfile c = new AuthorProfile(updateNumber);
	                String outFileName = filename.toLowerCase().replace(".xml","").replace(".zip","");
	                String deleteFile = outFileName+"_Delete_"+updateNumber+".out";
	                   	   outFileName= outFileName+"au"+updateNumber+".out";
	                String affOutFileName = outFileName+"_af_"+updateNumber+".out";
	                FileWriter outFile = new FileWriter(outFileName);
	                FileWriter affOutFile = new FileWriter(affOutFileName);
	                
	                if(bucketName==null || bucketName.length()==0)
	                {	                		   	                	
	                    c.readFile(filename,outFile,affOutFile);               
	                }
	                //read from s3 bucket
	                else if (bucketName !=null && bucketName.length() >0)
	                {	                	
	                    //c.readFileS3(filename,outFile);    
	                }
	               	              
	                if(test)
	                {
	                    System.out.println("sql loader file "+outFileName+" created;");
	                    System.out.println("about to load data file "+outFileName);
	                    System.out.println("press enter to continue");
	                    System.in.read();
	                    Thread.currentThread().sleep(1000);
	                }
	                Runtime r = Runtime.getRuntime();

	                Process p = r.exec("./"+sqlldrFileName+" "+outFileName);
	                int t = p.waitFor();

	                int tempTableCount = au.getTempTableCount();
	 
	                System.out.println(tempTableCount+" records was loaded into the temp table");
	                	                
	                endTime = System.currentTimeMillis();
	                System.out.println("time for loading temp table "+(endTime-midTime)/1000.0+" seconds");
	                midTime = endTime;
	                //System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
	                /*
	                p = r.exec("./AuthorProfileDeleteFileLoader.sh  "+deleteFile);
	                t = p.waitFor();
	                int deleteTableCount = au.getDeleteTableCount();
	                System.out.println(deleteTableCount+" records was loaded into the delete table");
	                endTime = System.currentTimeMillis();
	                System.out.println("time for loading delete table "+(endTime-midTime)/1000.0+" seconds");
	                */
	                midTime = endTime;
	                
	                if(tempTableCount>0)
	                {                 
	                    if(test)
	                    {
	                        System.out.println("begin to update tables");
	                        System.out.println("press enter to continue");
	                        System.in.read();
	                        Thread.currentThread().sleep(1000);
	                    }
	                    au.runCorrection(outFileName,updateNumber,database,action);
	                }
	                else
	                {
	                    System.out.println("no record was loaded into the temp table");
	                    System.exit(1);
	                }

	                endTime = System.currentTimeMillis();
	                System.out.println("time for run correction table "+(endTime-midTime)/1000.0+" seconds");
	                midTime = endTime;
	                	         
	            }
	            else if(action.equalsIgnoreCase("extractupdate")||action.equalsIgnoreCase("extractdelete"))
	            {
	                au.doExtract(updateNumber,database,action);
	                System.out.println(database+" "+updateNumber+"  extract is done.");
	                
	                endTime = System.currentTimeMillis();
	                System.out.println("time for run fast extract along "+(endTime-midTime)/1000.0+" seconds");
	                midTime = endTime;
	            }
	            else
	            {
	                System.out.println(database+" "+updateNumber+" correction is done.");
	               
	            }
	            
	            

	        }
	        finally
	        {
	            if (con != null)
	            {
	                try
	                {
	                    con.close();
	                }
	                catch (Exception e)
	                {
	                    e.printStackTrace();
	                }
	            }
	            System.out.println("total process time "+(System.currentTimeMillis()-startTime)/1000.0+" seconds");
	        }

	        System.exit(1);
	    }
	
	private void deleteAuthorProfile(File deleteFile)
    {
		//add content later
    	String temp = null;
    }
	
	
	private void cleanUp(String tableToBeTruncate)
    {
        Statement stmt = null;
        String[] tableName = null;
        if(tableToBeTruncate.indexOf(",")>-1)
        {
            tableName = tableToBeTruncate.split(",",-1);
        }
        else
        {
            tableName = new String[1];
            tableName[0] = tableToBeTruncate;
        }

        try
        {
            stmt = con.createStatement();

            for(int i=0;i<tableName.length;i++)
            {

                if(i==0)
                {
                    this.tempTable=tableName[i];
                    System.out.println("truncate temp table "+this.tempTable);
                }

                if(i==1)
                {
                    this.addTable=tableName[i];
                    System.out.println("truncate add table "+this.addTable);
                }
                
                if(i==2)
                {
                    this.deleteTable=tableName[i];
                    System.out.println("truncate delete table "+this.deleteTable);
                }
                
                stmt.executeUpdate("truncate table "+tableName[i]);
			
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch (Exception e)
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
	 
	private void runCorrection(String fileName,int updateNumber,String database,String action)
    {
    	long midTime = System.currentTimeMillis();
        CallableStatement pstmt = null;
        boolean blnResult = false;
        try
        {
        
            if(action != null && action.equalsIgnoreCase("update"))
            {
                //System.out.println("updateNumber= "+updateNumber+" fileName= "+fileName+" database= "+database);
                if(test)
                {
                    System.out.println("begin to execute stored procedure update_authorprofile_temp_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call update_au_profile_temp_table(?,?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.setString(2,fileName);
                pstmt.executeUpdate();

                
                endTime = System.currentTimeMillis();
                System.out.println("time for update_au_profile_temp_table "+(endTime-midTime)/1000.0+" seconds");
                midTime = endTime;
                //System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
                
                if(test)
                {
                    System.out.println("begin to execute stored procedure update_authorprofile_master_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call update_au_profile_master_table(?)}");
                pstmt.setInt(1,updateNumber);
              
                pstmt.executeUpdate();
                              
                endTime = System.currentTimeMillis();
                System.out.println("time for update_authorprofile_master_table "+(endTime-midTime)/1000.0+" seconds");
                midTime = endTime;
                //System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");

            }
            else if(action != null && action.equalsIgnoreCase("delete"))
            {
                if(test)
                {
                    System.out.println("begin to delete author profile record");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                deleteData(con);
                                
                endTime = System.currentTimeMillis();
                System.out.println("time for delete author profile record "+(endTime-midTime)/1000.0+" seconds");
                midTime = endTime;

            }
            else
            {
                System.out.println("What do you want me to do? action "+action+" not known");
                System.exit(1);
            }
            System.out.println("updateNumber= "+updateNumber+" fileName= "+fileName+" database= "+database);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch(Exception se)
                {
                }
            }
        }

    }

	
	private void doExtract(int updateNumber,String dbname,String action) throws Exception
	{
		//add content later
		System.out.println("extract data to elasticsearch server");
		 	
	}
	 
	private int getTempTableCount()
    {
        Statement stmt = null;
        String[] tableName = null;
        int count = 0;
        ResultSet rs = null;

        try
        {
            stmt = con.createStatement();

            rs = stmt.executeQuery("select count(*) count from "+tempTable+"");
            if(rs.next())
            {
                count = rs.getInt("count");
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            if (stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        return count;
    }
	
	private int getDeleteTableCount()
    {
        Statement stmt = null;
        int count = 0;
        ResultSet rs = null;

        try
        {
            stmt = con.createStatement();

            rs = stmt.executeQuery("select count(*) count from au_correction_temp_delete");
            if(rs.next())
            {
                count = rs.getInt("count");
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            if (stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        return count;
    }
	
	private void deleteData(Connection con1)
	{
		Statement stmt = null;
		String sqlQuery = "select * from "+tempTable;
		ResultSet rs = null;
		//Connection con1=null;
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			stmt = con1.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			String authorid = "";
			while (rs.next())
			{
				authorid= rs.getString("authorid");
				
				if(checkRecord(authorid,con1))
				{
					deleteRecord(authorid,con1);
				}
			}

		}
		catch(Exception e)
		{
			System.out.print("Exception on GeoRefCorrection.deleteData "+e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}			
		}
	}
	
	private void deleteRecord(String authorid,Connection con1)
	{
		PreparedStatement stmt = null;
		String sqlQuery1 = "delete from author_profile where authorid='"+authorid+"'";
		
		//Connection con1 = null;
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
	
		    stmt = con1.prepareStatement(sqlQuery1);
		    stmt.executeUpdate();
		    con1.commit();

		}
		catch(Exception e)
		{
			System.out.println("Exception on AuthorProfileCorrection.deleteRecord "+e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}		
		}

	}

	private boolean checkRecord(String authorid, Connection con1)
	{
		Statement stmt = null;
		String sqlQuery = "select authorid from author_profile where authorid='"+authorid+"'";
		ResultSet rs = null;
		//Connection con1 = null;
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			stmt = con.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			String auid = null;
			int inFast=0;
			while (rs.next())
			{
				auid=rs.getString("authorid");
			}
			if(auid != null)
			{
				Thread.currentThread().sleep(250);
				int inSearchServer = checkSearchServer(auid);
				if(inSearchServer<1)
				{
					return true;
				}
				else
				{
					System.out.println("****record "+auid+" is still in Search Server****");

				}
			}
			else
			{
				System.out.println("****record "+auid+" is not in author_profile table****");
			}

		}
		catch(Exception e)
		{
			System.out.print("Exception on authorProfileCorrection.checkRecord "+e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}
			
		}
		return false;

	}
	
	private int checkSearchServer(String term1)
	{

		System.out.println("checking in Search server");

		return 0;

	}


}

