package org.ei.dataloading.knovel.loadtime;


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

public class KnovelCorrection {

	Perl5Util perl = new Perl5Util();
	
	private static String tablename;	
	private static String currentDb;	
	private static Connection con = null;
	static String url="jdbc:oracle:thin:@jupiter:1521:eidb1";
	static String driver="oracle.jdbc.driver.OracleDriver";
	static String username="ap_ev_search";
	static String password="ei3it";
	static String database;
	static String action;
	static int updateNumber=0;
	static boolean test = false;
	static String tempTable="knovel_correction_temp";
	static String addTable="knovel_correction_add";
	static String deleteTable="knovel_correction_delete";
	static String sqlldrFileName="KnovelCorrectionFileLoader.sh";
	public static final String AUDELIMITER = new String(new char[] {30});
	public static final String IDDELIMITER = new String(new char[] {31});
	public static final String GROUPDELIMITER = new String(new char[] {02});
	private static long startTime = System.currentTimeMillis();
	private static long endTime = System.currentTimeMillis();
	private static long midTime = System.currentTimeMillis();
	private static String filename = "";
	private static String path="";
	
	public static void main(String args[])
	        throws Exception
	    {
	        
	        String fileToBeLoaded   = null;
	        String input;
	        String tableToBeTruncated = "knovel_correction_temp,knovel_correction_insert,knovel_correction_delete";
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


	        if(args.length>10)
	        {
	            if(args[6]!=null)
	            {
	                url = args[6];
	            }
	            if(args[7]!=null)
	            {
	                driver = args[7];
	            }
	            if(args[8]!=null)
	            {
	                username = args[8];
	                System.out.println("username= "+username);
	            }
	            if(args[9]!=null)
	            {
	                password = args[9];
	                System.out.println("password= "+password);
	            }
	            if(args[10]!=null)
	            {
	                sqlldrFileName = args[10];
	                System.out.println("using sqlloaderfile "+sqlldrFileName);
	            }
	            else
	            {
	                System.out.println("Does not have sqlldr file");
	                System.exit(1);
	            }
	        }

	        if(args.length>6)
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
	                //System.out.println("PATH="+path);
    				//System.out.println("filename="+filename);
	            }

	            if(args[1]!=null)
	            {
	                tableToBeTruncated = args[1];				
	            }

	            if(args[2]!=null)
	            {
	                database = args[2];
	            }

	            if(args[3]!=null && args[3].length()>0)
	            {
	                Pattern pattern = Pattern.compile("^\\d*$");
	                Matcher matcher = pattern.matcher(args[3]);
	                if (matcher.find())
	                {
	                    updateNumber = Integer.parseInt(args[3]);
	                }
	                else
	                {
	                    System.out.println("did not find updateNumber or updateNumber has wrong format");
	                    System.exit(1);
	                }
	            }

	            if(args[4]!=null)
	            {
	                action = args[4];
	            }
	            else
	            {
	                System.out.println("Are we doing 'update' or 'delete'");
	                System.exit(1);
	            }

	            if(args[5]!=null)
	            {
	                FastSearchControl.BASE_URL = args[5];
	            }
	            else
	            {
	                System.out.println("Does not have FastSearch URL");
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
	        //System.out.println("total Time used"+(endTime-startTime)/1000.0+" seconds");
	        try
	        {

	            KnovelCorrection kc = new KnovelCorrection();
	            con = kc.getConnection(url,driver,username,password);
	            if(action!=null && !(action.equals("extractupdate")||action.equals("extractdelete") ||action.equals("lookupIndex")))
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


	                kc.cleanUp(tableToBeTruncated);

	                midTime = endTime;
	                endTime = System.currentTimeMillis();
	                System.out.println("time for truncate table "+(endTime-midTime)/1000.0+" seconds");
	                //System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
	                
	                /************** load data into temp table ****************/

	                if(test)
	                {
	                    System.out.println("about to parse data file "+fileToBeLoaded);
	                    System.out.println("press enter to continue");
	                    System.in.read();
	                    Thread.currentThread().sleep(1000);
	                }

	                KnovelReader c = new KnovelReader(String.valueOf(updateNumber),database,filename);
	                //c.init(database+"_"+filename);
	                c.readGroupFile(path,filename);
	                //String dataFile="out/"+filename+"."+updateNumber+".out";
	                String outFile = filename.replace(".xml","");
	                String deleteFile = "out/"+database+"_"+outFile+"_Delete_"+updateNumber+".out";
	                   	   outFile="out/"+database+"_"+outFile+"_master_"+updateNumber+".out";
	                File f = new File(outFile);
	                if(!f.exists())
	                {
	                    System.out.println("datafile: "+outFile+" does not exists");
	                    System.exit(1);
	                }

	                if(test)
	                {
	                    System.out.println("sql loader file "+outFile+" created;");
	                    System.out.println("about to load data file "+outFile);
	                    System.out.println("press enter to continue");
	                    System.in.read();
	                    Thread.currentThread().sleep(1000);
	                }
	                Runtime r = Runtime.getRuntime();

	                Process p = r.exec("./"+sqlldrFileName+" "+outFile);
	                int t = p.waitFor();

	                int tempTableCount = kc.getTempTableCount();
	 
	                System.out.println(tempTableCount+" records was loaded into the temp table");
	                	                
	                endTime = System.currentTimeMillis();
	                System.out.println("time for loading temp table "+(endTime-midTime)/1000.0+" seconds");
	                midTime = endTime;
	                //System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
	                
	                p = r.exec("./KnovelDeleteFileLoader.sh  "+deleteFile);
	                t = p.waitFor();
	                int deleteTableCount = kc.getDeleteTableCount();
	                System.out.println(deleteTableCount+" records was loaded into the delete table");
	                
	                endTime = System.currentTimeMillis();
	                System.out.println("time for loading delete table "+(endTime-midTime)/1000.0+" seconds");
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
	                    kc.runCorrection(outFile,updateNumber,database,action);
	                }
	                else
	                {
	                    System.out.println("no record was loaded into the temp table");
	                    System.exit(1);
	                }

	                endTime = System.currentTimeMillis();
	                System.out.println("time for run correction table "+(endTime-midTime)/1000.0+" seconds");
	                midTime = endTime;
	                
	                //processing delete file
	                File d = new File(deleteFile);
	                if(!d.exists())
	                {
	                    System.out.println("deleteDatafile: "+deleteFile+" does not exists");
	                    System.exit(1);
	                }
	                kc.createDeleteFile(d);
	                kc.zipDeleteFile(deleteFile.replace(".out",".zip"));
	                	         
	            }
	            else if(action.equalsIgnoreCase("extractupdate")||action.equalsIgnoreCase("extractdelete"))
	            {

	                kc.doFastExtract(updateNumber,database,action);
	                System.out.println(database+" "+updateNumber+" fast extract is done.");
	                
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
	
	private void createDeleteFile(File deleteFile)
    {
    	long midTime = System.currentTimeMillis();
    	Statement stmt = null;
        ResultSet rs = null;
        BufferedReader reader=null;
        String line =null;
        FileWriter outputFile = null;
        try
        {
        	StringBuffer accessnumberBuffer = new StringBuffer();
        	reader = new BufferedReader(new FileReader(deleteFile));
        	outputFile = new FileWriter("delete.txt");
        	while ((line = reader.readLine()) != null) 
        	{
                 String[] accessnumber = line.split("|");
                 if (accessnumber[0] != null) 
                 {
                	 accessnumberBuffer.append("'"+accessnumber[0]+"',");
                 }
            }
        	
        	if(accessnumberBuffer.length()>0)
        	{
	        	String accessnumberString = accessnumberBuffer.substring(0,accessnumberBuffer.length()-1);
	            stmt = con.createStatement();
	            String sqlString = "select m_id from knovel_master where accessnumber in ("+accessnumberString+")";
	            System.out.println("SQL="+sqlString);
	            rs = stmt.executeQuery(sqlString);
	       
	            while(rs.next())
	            {
	            	outputFile.write(rs.getString("m_id")+"\n");
	            }
        	}
            outputFile.close();
            
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        finally
        {
            if(rs != null)
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

            if(stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }
            
            if(outputFile!=null)
            {
            	 try
                 {
            		 outputFile.close();
                 }
                 catch(Exception e1)
                 {
                     e1.printStackTrace();
                 }
            }
        }
    }
	
	private void zipDeleteFile(String filename)
	{
		byte[] buffer = new byte[1024];
		FileOutputStream fos=null;
		ZipOutputStream zos=null;
		ZipEntry ze=null;
		FileInputStream in=null;
		File d = new File("delete.txt");
    	
    	try{
    		
    		if(d.exists())
    		{
    			fos = new FileOutputStream(filename);
    			zos = new ZipOutputStream(fos);
    			ze= new ZipEntry("delete.txt");
    			zos.putNextEntry(ze);
    			in = new FileInputStream("delete.txt");
   	   
	    		int len;
	    		while ((len = in.read(buffer)) > 0) {
	    			zos.write(buffer, 0, len);
	    		}
    		}   		
    		zos.closeEntry();
           
          
    		System.out.println("Done");

    	}catch(IOException ex){
    	   ex.printStackTrace();
    	}
    	 finally
         {
             if(in != null)
             {
                 try
                 {
                     in.close();
                 }
                 catch(Exception e1)
                 {
                     e1.printStackTrace();
                 }

             }

             if(zos != null)
             {
                 try
                 {
                     zos.close();
                 }
                 catch(Exception e1)
                 {
                     e1.printStackTrace();
                 }
             }
             
             if(fos!=null)
             {
            	 try
                 {
                     fos.close();
                 }
                 catch(Exception e1)
                 {
                     e1.printStackTrace();
                 }
             }
             
             if(d.exists())
             {
            	 d.delete();
             }
         }

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
                    System.out.println("begin to execute stored procedure update_knovel_temp_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call update_knovel_temp_table(?,?,?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.setString(2,fileName);
                pstmt.setString(3,database);
                pstmt.executeUpdate();

                
                endTime = System.currentTimeMillis();
                System.out.println("time for update_knovel_temp_table "+(endTime-midTime)/1000.0+" seconds");
                midTime = endTime;
                //System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
                
                if(test)
                {
                    System.out.println("begin to execute stored procedure update_knovel_master_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call update_knovel_master_table(?,?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.setString(2,database);
                pstmt.executeUpdate();
                              
                endTime = System.currentTimeMillis();
                System.out.println("time for update_knovel_master_table "+(endTime-midTime)/1000.0+" seconds");
                midTime = endTime;
                //System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");

            }
            else if(action != null && action.equalsIgnoreCase("delete"))
            {
                if(test)
                {
                    System.out.println("begin to delete knovel record");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                deleteData();
                                
                endTime = System.currentTimeMillis();
                System.out.println("time for delete knovel record "+(endTime-midTime)/1000.0+" seconds");
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

	
	private void processLookupIndex(HashMap update,HashMap backup) throws Exception
	{
		System.out.println("running processLookupIndex");
	}
	
	private void doFastExtract(int updateNumber,String dbname,String action) throws Exception
	{
		 	CombinedXMLWriter writer = new CombinedXMLWriter(50000,
										                  	updateNumber,
										                  	dbname,
										                  	"dev");

			Statement stmt = null;
			ResultSet rs = null;
			try
			{
				stmt = con.createStatement();
				if(action.equalsIgnoreCase("update") || action.equalsIgnoreCase("extractupdate"))
				{
					System.out.println("Running the query...");
					writer.setOperation("add");
					KnovelCombiner c = new KnovelCombiner(writer);	
					if(updateNumber==1)
					{
						rs = stmt.executeQuery("select ACCESSNUMBER,DOC_TYPE,DOI,PII,OAI,ISBN,EISBN,LANGUAGE,TITLE,AUTHOR,AFFILIATION,M_ID,PUBLISHER,DOC_FORMAT,substr(PUBLISH_DATE,1,4) PUBLISH_DATE,PARENTID,JOURNAL_NAME,JOURNAL_SUBNAME,VOLUME,ISSUE,ABSTRACT,SUBJECT,TABLE_OF_CONTENT,LOADNUMBER,COPYRIGHT,DATABASE from knovel_master");
					}
					else
					{
						rs = stmt.executeQuery("select ACCESSNUMBER,DOC_TYPE,DOI,PII,OAI,ISBN,EISBN,LANGUAGE,TITLE,AUTHOR,AFFILIATION,M_ID,PUBLISHER,DOC_FORMAT,substr(PUBLISH_DATE,1,4) PUBLISH_DATE,PARENTID,JOURNAL_NAME,JOURNAL_SUBNAME,VOLUME,ISSUE,ABSTRACT,SUBJECT,TABLE_OF_CONTENT,LOADNUMBER,COPYRIGHT,DATABASE from knovel_master  where database='kno' and updateNumber='"+updateNumber+"'");
					}
					c.writeRecs(rs);
				}
				else
				{
					System.out.println("unknown action");
				}
				writer.end();
				writer.flush();
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
	}
	
	 private void outputLookupIndex(HashMap lookupData, int updateNumber)
	 {
		 System.out.println("running outputLookupIndex");
	 }
	 
	 public HashMap getLookupData(String action) throws Exception
	 {
		 HashMap map = new HashMap();
		 System.out.println("running getLookupData");
		 return map;
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

            rs = stmt.executeQuery("select count(*) count from knovel_correction_delete");
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
	
	private void deleteData()
	{
		Statement stmt = null;
		String sqlQuery = "select * from georef_master_delete";
		ResultSet rs = null;
		Connection con1=null;
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			stmt = con1.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			String m_id = "";
			while (rs.next())
			{
				m_id= rs.getString("m_id");

				if(checkRecord(m_id))
				{
					deleteRecord(m_id);
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

			if (con1 != null)
			{
				try
				{
					con1.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}
		}
	}
	
	private void deleteRecord(String m_id)
	{
		PreparedStatement stmt = null;
		String sqlQuery1 = "delete from knovel_master where m_id='"+m_id+"'";
		String sqlQuery2 = "delete from saved_records where guid='"+m_id+"'";
		
		Connection con1 = null;
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			con1.setAutoCommit(false);
			stmt = con1.prepareStatement(sqlQuery2);
		    stmt.executeUpdate();
		    stmt = con1.prepareStatement(sqlQuery1);
		    stmt.executeUpdate();
		    con1.commit();

		}
		catch(Exception e)
		{
			System.out.println("Exception on KnovelCorrection.deleteRecord "+e.getMessage());
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

			if (con1 != null)
			{
				try
				{
					con1.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}
		}

	}

	private boolean checkRecord(String m_id)
	{
		Statement stmt = null;
		String sqlQuery = "select id_number from knovel_master where m_id='"+m_id+"'";
		ResultSet rs = null;
		Connection con1 = null;
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			stmt = con.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			String accessnumber = null;
			int inFast=0;
			while (rs.next())
			{
				accessnumber=rs.getString("id_number");
			}
			if(accessnumber != null)
			{
				Thread.currentThread().sleep(250);
				inFast = checkFast(accessnumber,"an","kno");
				if(inFast<1)
				{
					return true;
				}
				else
				{
					System.out.println("****record "+m_id+" is still in Fast****");

				}
			}
			else
			{
				System.out.println("****record "+m_id+" is not in knovel_master****");
			}

		}
		catch(Exception e)
		{
			System.out.print("Exception on KnovelCorrection.checkRecord "+e.getMessage());
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

			if (con1 != null)
			{
				try
				{
					con1.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}
		}
		return false;

	}
	
	private int checkFast(String term1, String searchField, String database)
	{

		List outputList = new ArrayList();

		String[] credentials = new String[]{"KNO"};
		String[] dbName = {database};
		int c = 0;



		try
		{
			DatabaseConfig databaseConfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
			int intDbMask = databaseConfig.getMask(dbName);
			if(term1 != null)
			{
				Thread.currentThread().sleep(250);
				SearchControl sc = new FastSearchControl();

				//int oc = Integer.parseInt((String)inputMap.get(term1));
				Query queryObject = new Query(databaseConfig, credentials);
				queryObject.setDataBase(intDbMask);

				String searchID = (new GUID()).toString();
				queryObject.setID(searchID);
				queryObject.setSearchType(Query.TYPE_QUICK);

				queryObject.setSearchPhrase("{"+term1+"}",searchField,"","","","","","");
				queryObject.setSearchQueryWriter(new FastQueryWriter());
				queryObject.compile();
				String sessionId = null;
				int pagesize = 25;
				SearchResult result = sc.openSearch(queryObject,sessionId,pagesize,false);
				c = result.getHitCount();
			}

		}
		catch(Exception e)
		{
			System.out.print("Exception on KnovelCorrection.checkFast "+e.getMessage());
			e.printStackTrace();
		}

		return c;

	}


}