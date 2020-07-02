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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import com.amazonaws.util.IOUtils;

public class InstituteProfileCorrection 
{
	Perl5Util perl = new Perl5Util();	
	private static String tablename;	
	private static String currentDb;	
	private static Connection con = null;
	static String url="jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
	static String driver="oracle.jdbc.driver.OracleDriver";
	static String username="ap_correction1";
	static String password="";
	static String database;
	static String action;
	static String elasticSearchUrl;
	static int updateNumber=0;
	static boolean test = false;
	static String tempTable="institute_correction_temp";
	static String addTable="institute_correction_insert";
	static String deleteTable="institute_correction_delete";
	static String sqlldrFileName="instituteProfileCorrectionFileLoader.sh";
	public static final String AUDELIMITER = new String(new char[] {30});
	public static final String IDDELIMITER = new String(new char[] {31});
	public static final String GROUPDELIMITER = new String(new char[] {29});
	private static long startTime = System.currentTimeMillis();
	private static long endTime = System.currentTimeMillis();
	private static long midTime = System.currentTimeMillis();
	private static String filename = "";
	private static String path=".";
	private static String bucketName="";
	private static String key="";

	
		
	public static void main(String args[])
	        throws Exception
	{
	        
		String fileToBeLoaded   = null;
		String input;
		String tableToBeTruncated = "af_profile_correction_temp,af_profile_correction_insert,af_profile_correction_delete";
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

				InstituteProfileCorrection af = new InstituteProfileCorrection();
	            con = af.getConnection(url,driver,username,password);
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

	                af.cleanUp(tableToBeTruncated);

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
	                InstitutionProfile c = new InstitutionProfile(updateNumber);
	                String outFileName = filename.toLowerCase().replace(".xml","").replace(".zip","");
	                String deleteFile = outFileName+"_Delete_"+updateNumber+".out";
	                   	   outFileName= outFileName+"af"+updateNumber+".out";
	                String affOutFileName = outFileName+"_af_"+updateNumber+".out";
	                BufferedWriter outFile = new BufferedWriter(new FileWriter(outFileName));
	                
	                
	                if(bucketName==null || bucketName.length()==0)
	                {	                		   	                	
	                    c.readFile(path+"/"+filename,outFile);               
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

	                int tempTableCount = af.getTempTableCount();
	 
	                System.out.println(tempTableCount+" records was loaded into the temp table");
	                	                
	                endTime = System.currentTimeMillis();
	                System.out.println("time for loading temp table "+(endTime-midTime)/1000.0+" seconds");	              
	                
	                p = r.exec("mv "+outFileName+" "+path);
	                p = r.exec("mv "+affOutFileName+" "+path);
	                
	                t = p.waitFor();
	                
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
	                    af.runCorrection(outFileName,updateNumber,database,action);
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
	                af.doExtract(updateNumber,database,action);
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
                if(test)
                {
                    System.out.println("begin to execute stored procedure update_authorprofile_temp_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call update_af_profile_temp_table(?,?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.setString(2,fileName);
                pstmt.executeUpdate();

                
                endTime = System.currentTimeMillis();
                System.out.println("time for update_af_profile_temp_table "+(endTime-midTime)/1000.0+" seconds");
                midTime = endTime;
                //System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
                
                if(test)
                {
                    System.out.println("begin to execute stored procedure update_authorprofile_master_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call update_af_profile_master_table(?)}");
                pstmt.setInt(1,updateNumber);
              
                pstmt.executeUpdate();
                              
                endTime = System.currentTimeMillis();
                System.out.println("time for update_af_profile_master_table "+(endTime-midTime)/1000.0+" seconds");
                midTime = endTime;
                //System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");

            }
            else if(action != null && action.equalsIgnoreCase("delete"))
            {
                if(test)
                {
                    System.out.println("begin to delete institute profile record");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                deleteData(con);
                                
                endTime = System.currentTimeMillis();
                System.out.println("time for delete institute profile record "+(endTime-midTime)/1000.0+" seconds");
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

            rs = stmt.executeQuery("select count(*) count from "+tempTable);
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

            rs = stmt.executeQuery("select count(*) count from " +tempTable);
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
		System.out.println("SQLQUERY= "+sqlQuery);
		ResultSet rs = null;
		
		try
		{
			
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			
			stmt = con1.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			String affid = "";
			while (rs.next())
			{
				affid= rs.getString("affid");

				if(checkRecord(affid,con1))
				{
					deleteRecord(affid,con1);
				}
			}

		}
		catch(Exception e)
		{
			System.out.print("Exception on InstituteProfileCorrection.deleteData "+e.getMessage());
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
	
	private void deleteRecord(String affid,Connection con1)
	{
		Statement stmt = null;
		String sqlQuery1 = "insert into institute_profile_deletion select * from institute_profile where affid='"+affid+"'";
		String sqlQuery2 = "delete from institute_profile where affid='"+affid+"'";
		String sqlQuery3 = "update cafe_af_lookup set status='deleted' where INSTITUTE_ID='"+affid+"'";
		
		
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
	
			con1.setAutoCommit(false);
			stmt = con1.createStatement();		   
		    stmt.executeUpdate(sqlQuery1);
		    stmt.executeUpdate(sqlQuery2);
		    stmt.executeUpdate(sqlQuery3);
		    con1.commit();
		    con1.setAutoCommit(true);

		}
		catch(Exception e)
		{
			System.out.println("Exception on InstitutionProfileCorrection.deleteRecord "+e.getMessage());
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

	private boolean checkRecord(String affid,Connection con1)
	{
		Statement stmt = null;
		String sqlQuery = "select affid from Institute_profile where affid='"+affid+"'";
		ResultSet rs = null;
		
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			stmt = con.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			//String affid = null;
			int inFast=0;
			while (rs.next())
			{
				affid=rs.getString("affid");
			}
			if(affid != null)
			{
				Thread.currentThread().sleep(250);
				int inSearchServer = checkSearchServer(affid);
				if(inSearchServer<1)
				{
					return true;
				}
				else
				{
					System.out.println("****record "+affid+" is still in Search Server****");

				}
			}
			else
			{
				System.out.println("****record "+affid+" is not in institute_profile table****");
			}

		}
		catch(Exception e)
		{
			System.out.print("Exception on InstituteProfileCorrection.checkRecord "+e.getMessage());
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
	
	private int checkSearchServer(String afid)
	{
		String SERVICE_NAME = "es";
		String REGION = "us-east-1";
		String HOST ="search-evcafe5-ucqg6c7jnb4qbvppj2nee4muwi.us-east-1.es.amazonaws.com";
		String ENDPOINT_ROOT = "http://" + HOST;
		int totalHit =0;
		String PATH = "/cafe/_search";	
		
		HttpClient httpClient = HttpClientBuilder.create().build(); 
		JSONObject json = new JSONObject();
		JSONParser parser = new JSONParser();		
		String s = "{\"stored_fields\": [\"afdoc.afid\"],\"query\": {\"bool\": {\"must\": [{\"match_phrase\": {\"afdoc.afid\": "+afid+"}}]}}}";
		try {
			
			midTime = System.currentTimeMillis();
			
			Object obj = parser.parse(s);		
		    HttpPost request = new HttpPost(ENDPOINT_ROOT + PATH);
		    
		    StringEntity params =new StringEntity(obj.toString());
		    request.addHeader("content-type", "application/json");
		    request.setEntity(params);
		    HttpResponse response = httpClient.execute(request);
		    InputStream responseStream = response.getEntity().getContent();		    

			String responseString = IOUtils.toString(responseStream);
			
			//Standard response {"took":16,"timed_out":false,"_shards":{"total":16,"successful":16,"failed":0},"hits":{"total":1,"max_score":6.5866313,"hits":[{"_index":"evcafe","_type":"author","_id":"aut_M7351e2de156b8ab50beM664e10178163171","_score":6.5866313,"fields":{"audoc.auid":["57104828900"]}}]}}
			System.out.println("RESPONSE= "+responseString);
			JSONArray array = new JSONArray();
			Object responseobj = parser.parse(responseString);
		    		  
		    String hits =  ((JSONObject)responseobj).get("hits").toString();		  
		    Object hitobj = parser.parse(hits);
		    totalHit =  Integer.parseInt(((JSONObject)hitobj).get("total").toString());
		    System.out.println("aFFId "+afid+" hit= "+totalHit);		  		   
		    
		 }catch(ParseException pe){
				
	         System.out.println("position: " + pe.getPosition());
	         System.out.println(pe);	     
		}catch (Exception ex) {

		   ex.printStackTrace();

		}
		if(totalHit>0)
			return 1;
		else
			return 0;

	}


}

