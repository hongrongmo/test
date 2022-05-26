package org.ei.dataloading.inspec.loadtime;

/*
 * Update by Hanan, Feb 2013
 * this class for Inspec Correction and/or FAST extract using updatenumber
 */

import java.sql.*;
import java.util.*;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.dataloading.sharedsearch.SharedSearchSearchEntry;
import org.ei.dataloading.*;
import org.ei.dataloading.bd.loadtime.*;
import org.ei.domain.*;
import org.ei.query.base.*;
import org.ei.connectionpool.*;
import java.text.*;
import java.io.*;
import java.lang.Process;
import java.util.regex.*;
import org.ei.util.GUID;
import org.ei.common.*;

public class INSCorrection
{
    Perl5Util perl = new Perl5Util();

    private static String tablename;

    private static String currentDb;

    private static HashMap issnARFix = new HashMap();

    private int intDbMask = 1;
    private static Connection con = null;
    static String url="jdbc:oracle:thin:@neptune.elsevier.com:1521:ei";
    static String driver="oracle.jdbc.driver.OracleDriver";
    static String username="ap_correction";
    static String password="";
    static String database;
    static String action;
    static boolean test = false;
    static String tempTable="ins_correction_temp";
    static String lookupTable="deleted_lookupIndex";
    static String backupTable="ins_temp_backup";
    static String numericalTable="ins_master_numerical_temp";
    static String citationTable="ins_master_citation_temp";
    static String sqlldrFileName="InspecSqlLoaderFile.sh";
    private static String propertyFileName;
    private static int updateNumber= 0;
    String [] ipccode = null;

    /*HT added 09/21/2020 for ES lookup, add BdCorrection constructor to initiat XmlCombiner once instead of being initialized in many individual methods in this class*/
    private static String tableToBeTruncated = "ins_correction_temp,deleted_lookupIndex,ins_temp_backup,numericalTable";
    private static String fileToBeLoaded   = null;
    public static void main(String args[])
        throws Exception
    {   
        String input;
        
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
        catch (IOException ioe)
        {
            System.out.println("IO error trying to read your input!");
            System.exit(1);
        }
        
        if(args.length>11)
        {
        	propertyFileName=args[11];
        }

        if(args.length>10)
        {
            System.out.println("args[6] URL="+args[6]);

            if(args[6]!=null)
            {
                url = args[6];
            }

            System.out.println("args[7] driver="+args[7]);
            if(args[7]!=null)
            {
                driver = args[7];
            }

            System.out.println("args[8] username="+args[8]);
            if(args[8]!=null)
            {
                username = args[8];
            }

            System.out.println("args[9] password="+args[9]);
            if(args[9]!=null)
            {
                password = args[9];
            }

            System.out.println("args[10] sqlloaderFileToBeUse="+args[10]);
            if(args[10]!=null)
            {
                sqlldrFileName = args[10];
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
                System.out.println("args[0] fileToBeLoaded= "+fileToBeLoaded);
            }

            if(args[1]!=null)
            {
                tableToBeTruncated = args[1];
                System.out.println("args[1] tableToBeTruncated= "+tableToBeTruncated);
            }

            if(args[2]!=null)
            {
                database = args[2];
                System.out.println("args[2] database= "+database);
            }

            if(args[3]!=null && args[3].length()>0)
            {
                Pattern pattern = Pattern.compile("^\\d*$");
                Matcher matcher = pattern.matcher(args[3]);
                if (matcher.find())
                {
                    updateNumber = Integer.parseInt(args[3]);
                    //System.out.println("args[3] updateNumber= "+updateNumber);
                }
                else
                {
                    System.out.println("did not find updateNumber or updateNumber has wrong format");
                    System.out.println("you enter "+args[3]+" as updateNumber");
                    System.exit(1);
                }
            }

            if(args[4]!=null)
            {

                action = args[4];
                System.out.println("action= "+action);
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

        /*HT Added 09/21/2020 Move all work below to startCorrection instead*/
        INSCorrection bdc = new INSCorrection();
        bdc.startCorrection();

       
    }

    /*HT 09/21/2020 moved from main to here for readability*/
    public void startCorrection()
    {
    	 long startTime = System.currentTimeMillis();
    	 CombinedXMLWriter writer = new CombinedXMLWriter(50000,
                 updateNumber,
                 database,
                 "dev");

     	INSPECCombiner inscomb = new INSPECCombiner(writer,propertyFileName,database);
     	
    	 try
         { 
    		 /*HT added 09/21/2020 initialize lookup entry*/
    		 inscomb.writeLookupByWeekHook(updateNumber);
     		
             con = getConnection(url,driver,username,password);
             if(action!=null && !(action.equals("extractupdate")||action.equals("extractdelete")||action.equals("lookupindex")||action.equalsIgnoreCase("extractnumerical")))
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

                 cleanUp(tableToBeTruncated);

                 /************** load data into temp table ****************/

                 if(test)
                 {
                     System.out.println("about to parse data file "+fileToBeLoaded);
                     System.out.println("press enter to continue");
                     System.in.read();
                     Thread.currentThread().sleep(1000);
                 }

                 InspecBaseTableDriver c = new InspecBaseTableDriver(updateNumber);
                 //c.writeBaseTableFile(fileToBeLoaded,"XML");
                 
                 c.writeBaseTableFile(fileToBeLoaded);
                 String dataFile=fileToBeLoaded+".out";
                 List dataFileList = new ArrayList();
                 int i=0;
                 while(true)
                 {
                     i++;
                     String datafile=dataFile+"."+i;
                     File f = new File(datafile);
                     if(!f.exists())
                     {
                         if(i<1)
                         {
                             System.exit(1);
                         }
                         else
                         {
                             break;
                         }
                     }
                     //System.out.println("DATAFILE FOUND: "+datafile);
                     dataFileList.add(datafile);

                 }

                 if(test)
                 {
                     System.out.println("sql loader file "+dataFile+" created;");
                     System.out.println("about to load data file "+dataFile);
                     System.out.println("press enter to continue");
                     System.in.read();
                     Thread.currentThread().sleep(1000);
                 }
                 Runtime r = Runtime.getRuntime();

                 for(int j=0;j<dataFileList.size();j++)
                 {
                     String dFile = (String)dataFileList.get(j);
                     String name = dFile;
                     if(dFile.indexOf("/")>-1)
                     {	                    
 	        			int pathSeperator = name.lastIndexOf("/");        			
 	        			name=name.substring(pathSeperator+1);
                     }         			                    
         			
                     Process p = r.exec("./"+sqlldrFileName+" "+name);
                     System.out.println("Loading File "+name);
                     int t = p.waitFor();
                     //break;
                 }

                 int tempTableCount = getTempTableCount();
                 if(tempTableCount>0)
                 {
                     System.out.println(tempTableCount+" records was loaded into the temp table");
                     if(test)
                     {
                         System.out.println("begin to update tables");
                         System.out.println("press enter to continue");
                         System.in.read();
                         Thread.currentThread().sleep(1000);
                     }
                     runCorrection(dataFile,updateNumber,database,action);
                 }
                 else
                 {
                     System.out.println("no record was loaded into the temp table");
                     System.exit(1);
                 }

                 if(test)
                 {
                     System.out.println("finished updating tables");
                     System.out.println("begin to process lookup index");
                     System.out.println("press enter to continue");
                     System.in.read();
                     Thread.currentThread().sleep(1000);
                 }
                 if(action.equalsIgnoreCase("update"))
                 {
                	 if(propertyFileName == null)
                		 processLookupIndex(getLookupData("update",updateNumber),getLookupData("backup",updateNumber));			//fast
                	 /*HT added 09/21/2020 for es*/
                  	else
                  	{
                  		
                 		processESLookupIndex(inscomb.getESLookupData(updateNumber,"update", tempTable, con, database),inscomb.getESLookupData(updateNumber, "backup", backupTable, con, database));			
                  		
                  	}
                 }
                 else if(action.equalsIgnoreCase("delete"))
                 {

                	 if(propertyFileName == null)
                		 processLookupIndex(new HashMap(),getLookupData("backup",updateNumber));			//fast
                	 /*HT added 09/21/2020 for es*/
                	 else
                  		processESLookupIndex(new HashMap(),inscomb.getESLookupData(updateNumber, "backup", backupTable, con, database));
                 }
                 else if(action.equalsIgnoreCase("ins"))
                 {
                	 if(propertyFileName == null)
                		 processLookupIndex(getLookupData("ins",updateNumber),getLookupData("insBackup",updateNumber));			//fast
                	 /*HT added 09/21/2020 for es*/
                	 else
                  		processESLookupIndex(inscomb.getESLookupData(updateNumber, "ins", "ins_master_orig", con, database), inscomb.getESLookupData(updateNumber, "insBackup", "ins_master_orig", con, database));
                 }
             }
             /*Hanan: this is only to generate Fast Extract Files of the
              records that marked as deleted in case the action is "delete"

              since it is testing approach so may i do not need to generate extract files to fast
             Note: when send extracted files to fast, only fast that do deletion,
             but we still keep these marked records for delete in oracle table, not deleted at this point
             */

             if(action.equalsIgnoreCase("lookupIndex"))
             {
            	 if(propertyFileName == null)											//fast
            	 {
            		 outputLookupIndex(getLookupData("lookupIndex",updateNumber),updateNumber);
                     System.out.println(database+" "+updateNumber+" lookup index is done.");
            	 }
                	 
                 else
                	 /*HT added 09/21/2020 for ES lookup*/
                	 writeESIndexOnly(updateNumber,database,inscomb);					
             }
            else if(action.equalsIgnoreCase("extractupdate")||action.equalsIgnoreCase("extractdelete")||action.equalsIgnoreCase("extractnumerical"))
            {

                doFastExtract(updateNumber,database,action, inscomb);
                System.out.println(database+" "+updateNumber+" extraction is done.");
            }        
            else
            {
                System.out.println(database+" "+updateNumber+" correction is done.");
                System.out.println("Please run this program again with parameter \"extractupdate\" or \"extractdelete\" to get fast extract file");
            }

            //bdc.doFastExtract(updateNumber,database,action);
            getError(updateNumber,action);

         } 
    	 catch (Exception e) 
    	 {
        	 System.out.println("Exception starting INS correction?!");
        	 System.out.println("Reason:- " + e.getMessage());
        	 e.printStackTrace();
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
    	
    }
    /**
     * 
     * @author TELEBH
     * @Date: 09/21/2020 
     * @Description: Generate Lookups for ES
     * @throws Exception
     */
    
	public void writeESIndexOnly(int updatenumber, String database, INSPECCombiner c) throws Exception 
	{
		/* TH added 09/21/2020 for ES lookup generation */
		c.setAction("lookup");
		c.writeLookupByWeekHook(updatenumber);
		
		if ((updatenumber > 3000 || updatenumber < 1000) && (updatenumber > 1)) {
			Combiner.TABLENAME = "INS_MASTER_ORIG";
			c.writeCombinedByWeekNumber(url, driver, username, password, updatenumber);
		} 
	
	}
	
    private void outputLookupIndex(HashMap lookupData, int updateNumber)
    {
        String filename = null;
        String path = null;
        if(lookupData.get("AUTHOR")!=null)
        {
            filename = "author-"+updateNumber+".ins";
            path = "./ei/index_au";
            writeToFile((ArrayList)lookupData.get("AUTHOR"),"AUTHOR",updateNumber,filename,path);
        }

        if(lookupData.get("AFFILIATION")!=null)
        {
            filename = "affiliation-"+updateNumber+".ins";
            path = "./ei/index_af";
            writeToFile((ArrayList)lookupData.get("AFFILIATION"),"AFFILIATION",updateNumber,filename,path);
        }

        if(lookupData.get("CONTROLLEDTERM")!=null)
        {
            filename = "controlterms-"+updateNumber+".ins";
            path = "./ei/index_cv";
            writeToFile((ArrayList)lookupData.get("CONTROLLEDTERM"),"CONTROLLEDTERM",updateNumber,filename,path);
        }

        if(lookupData.get("PUBLISHERNAME")!=null)
        {
            filename = "publishername-"+updateNumber+".ins";
            path = "./ei/index_pn";
            writeToFile((ArrayList)lookupData.get("PUBLISHERNAME"),"PUBLISHERNAME",updateNumber,filename,path);
        }

        if(lookupData.get("SERIALTITLE")!=null)
        {
            filename = "serialtitle-"+updateNumber+".ins";
            path = "./ei/index_st";
            writeToFile((ArrayList)lookupData.get("SERIALTITLE"),"SERIALTITLE",updateNumber,filename,path);
        }

        if(lookupData.get(EVCombinedRec.INT_PATENT_CLASSIFICATION)!=null)
        {
            filename = "ipc-"+updateNumber+".ins";
            path = "./ei/index_ipc";
            writeToFile((ArrayList)lookupData.get(EVCombinedRec.INT_PATENT_CLASSIFICATION),"IPC",updateNumber,filename,path);
        }



    }



    private void writeToFile(List data, String field, int updateNumber, String filename, String path)
    {

        FileWriter out;

        File file=new File(path);


        try
        {
            if(!file.exists())
            {
                file.mkdir();
            }



            out = new FileWriter(path+"/"+filename);
            System.out.println("field==> "+field);
            if(data != null)
            {
                for(int i=0;i<data.size();i++)
                {
                    out.write(data.get(i)+"\tins"+"\n");
                }
            }
            out.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }


    private void getError(int updateNumber,String action)
    {
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            stmt = con.createStatement();
            if(action.equalsIgnoreCase("update") || action.equalsIgnoreCase("ins") )
            {
                // Hanan:

                //rs = stmt.executeQuery("select count(*) count from INS_CORRECTION_ERROR where update_number="+updateNumber);
                rs = stmt.executeQuery("select count(*) count,source,update_number  from INS_CORRECTION_ERROR where update_number= "+updateNumber + "group by update_number,source");

                boolean errorFlag = false;
                int i=0;

                while (rs.next())
                {
                    int count=rs.getInt("count");
                    String source=rs.getString("source");

                    if((Integer.toString(count)!=null) && (count>0))
                    {
                        if(source.indexOf("new record")>0)
                        {
                            System.out.println("*********** New Records found while updating data *************");
                            System.out.println("total number of new records in INS_CORRECTION_ERROR table for updateNumber" +rs.getString("update_number").toString() + " is: " + count);

                        }
                        else
                        {
                            errorFlag = true;
                            System.out.println("*********** Error found while updating data *************");
                            System.out.println("total number of errors in INS_CORRECTION_ERROR table for updateNumber" +rs.getString("update_number").toString() + " is: " + count);
                            System.out.println("storedProcedure: " + rs.getString("source").toString());
                        }
                    }
                }

                if(!errorFlag)
                {
                    System.out.println("*********** No Error found  *************");
                }
            }

        }
        catch (SQLException e)
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

    }

    private void doFastExtract(int updateNumber,String dbname,String action, INSPECCombiner c) throws Exception
    {
        CombinedXMLWriter writer = new CombinedXMLWriter(50000,
                                                      updateNumber,
                                                      dbname,
                                                      "dev");

        Statement stmt = null;
        ResultSet rs = null;
        try
        {
        	stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String sqlQuery = null;
			//HH 05/26/2022: Caused bug of generating empty lookups since Sep 17 2021, INSPECCombiner obj already created at startCorrection , comment this out
			//INSPECCombiner c = new INSPECCombiner(writer,this.propertyFileName,this.updateNumber);
            if(action.equalsIgnoreCase("update") || action.equalsIgnoreCase("extractupdate") || action.equalsIgnoreCase("ins") )
            {
                System.out.println("Running the query...");
                writer.setOperation("add");               
				sqlQuery = "select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc, updatenumber from ins_master_orig where updatenumber='"+updateNumber+"'";
                System.out.println("updateQuery= "+	sqlQuery);
                rs = stmt.executeQuery(sqlQuery);
                c.writeRecs(rs,con);              

            }
            else if(action.equalsIgnoreCase("delete") || action.equalsIgnoreCase("extractdelete"))
            {
                writer.setOperation("delete");
                //rs = stmt.executeQuery("select m_id from ins_master_orig where updateNumber='"+updateNumber+"' and anum in (select 'D'||anum from "+tempTable+")");
                //Hanan: since "anum" is NUMBER not varchar so cannot append 'D' to it, use updateflag instead
				sqlQuery = "select m_id from ins_master_orig where updateNumber='"+updateNumber+"' and anum in (select anum from "+tempTable+")";
                rs = stmt.executeQuery(sqlQuery);
                creatDeleteFile(rs,dbname,updateNumber);
                writer.zipBatch();
            }
            else if(action.equalsIgnoreCase("extractnumerical"))
            {
                System.out.println("Running the query...");
                writer.setOperation("add");              
                if(updateNumber==0)
                {
                	rs = stmt.executeQuery("select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc, updatenumber  from ins_master_orig where m_id in (select distinct mid from ins_master_numerical where mid is not null)");
                	
                }
                else
                {
                	rs = stmt.executeQuery("select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc, updatenumber  from ins_master_orig where m_id in (select distinct mid from ins_master_numerical where mid is not null and loadnumber='"+updateNumber+"')");
                }
                c.writeRecs(rs,con);
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

    private void creatDeleteFile(ResultSet rs,String database,int updateNumber)
    {

        String batchidFormat = "0000";
        String batchID = "0001";
        String numberID = "0000";
        File file=new File("fast");
        FileWriter out= null;
        CombinedWriter writer = new CombinedXMLWriter(10000,10000,database);

        try
        {
            if(!file.exists())
            {
                file.mkdir();
            }

            long starttime = System.currentTimeMillis();
            String batchPath = "fast/batch_" + updateNumber+"_"+batchID;

            file=new File(batchPath);
            if(!file.exists())
            {
                file.mkdir();
            }
            String root = batchPath +"/EIDATA/tmp";
            file=new File(root);

            if(!file.exists())
            {
                file.mkdir();
            }

            file = new File(root+"/delete.txt");

            if(!file.exists())
            {
                file.createNewFile();
            }
            out = new FileWriter(file);
            while (rs.next())
            {
                if(rs.getString("M_ID") != null)
                {
                    out.write(rs.getString("M_ID")+"\n");
                }
            }
            out.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(rs !=null)
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

            if(out !=null)
            {
                try
                {
                    out.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }

    }

    private void runCorrection(String fileName,int updateNumber,String database,String action)
    {
        CallableStatement pstmt = null;
        boolean blnResult = false;
        try
        {
            if(test)
            {
                System.out.println("begin to execute stored procedure update_ins_backup_table");
                System.out.println("press enter to continue");
                System.in.read();
                Thread.currentThread().sleep(1000);
            }


            pstmt = con.prepareCall("{ call update_ins_backup_table(?)}");
            pstmt.setInt(1,updateNumber);
            pstmt.executeUpdate();


            if(action != null && action.equalsIgnoreCase("update"))
            {
                if(test)
                {
                    System.out.println("begin to execute stored procedure update_ins_temp_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call update_ins_temp_table(?,?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.setString(2,fileName);
                pstmt.executeUpdate();

                if(test)
                {
                    System.out.println("begin to execute stored procedure update_ins_master_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call update_ins_master_table(?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.executeUpdate();
                
                if(test)
                {
                    System.out.println("begin to execute stored procedure update_ins_master_numerical_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call update_ins_numerical_table(?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.executeUpdate();
                
                if(test)
                {
                    System.out.println("begin to execute stored procedure UPDATE_INS_CITATION_TABLE");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call UPDATE_INS_CITATION_TABLE(?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.executeUpdate();
                
            }
            else if(action != null && action.equalsIgnoreCase("ins"))
            {
                if(test)
                {
                    System.out.println("begin to execute stored procedure update_ins_temp_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call update_ins_temp_table(?,?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.setString(2,fileName);
                pstmt.executeUpdate();

                if(test)
                {
                    System.out.println("begin to execute stored procedure update_ins_master_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call update_ins_master_table(?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.executeUpdate();
            }
            else if(action != null && action.equalsIgnoreCase("delete"))
            {
                if(test)
                {
                    System.out.println("begin to execute stored procedure delete_ins_master_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                //H: print out parameters to trace error
                //System.out.println("delete_ins_master_table procedure parameter 1" + updateNumber);
                //System.out.println("delete_ins_master_table procedure parameter 2" + fileName);
                //System.out.println("delete_ins_master_table procedure parameter 3" + database);

                pstmt = con.prepareCall("{ call delete_ins_master_table(?,?,?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.setString(2,fileName);
                pstmt.setString(3,database);   // Hanan 05/08/13 wzout dbase raise exception
                pstmt.executeUpdate();
            }
            else
            {
                System.out.println("What do you want me to do? action "+action+" not known");
                System.exit(1);
            }

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

    private int getTempTableCount()
    {
        Statement stmt = null;
        String[] tableName = null;
        int count = 0;
        ResultSet rs = null;

        try
        {
            stmt = con.createStatement();
            String sqlQuery = "select count(*) count from INS_CORRECTION_TEMP";
            //System.out.println("**Query**"+sqlQuery);
            rs = stmt.executeQuery(sqlQuery);
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
                    this.lookupTable=tableName[i];
                    System.out.println("truncate lookup table "+this.lookupTable);
                }

                if(i==2)
                {
                    this.backupTable=tableName[i];
                    System.out.println("truncate backup table "+this.backupTable);
                }
                
                if(i==3)
                {
                    this.numericalTable=tableName[i];
                    System.out.println("truncate numerical temp table "+this.numericalTable);
                }
                
                if(i==4)
                {
                    this.citationTable=tableName[i];
                    System.out.println("truncate citation temp table "+this.citationTable);
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

    /*
     * insert AU/AFF marked for deletion into deleted_lookupindex
     */
    private void saveDeletedData(String field,List data,String database)
    {
        PreparedStatement stmt = null;
        Statement count_stmt = null;
        //H: to check lookupindex count
        ResultSet rs=null;
        int count=0;


        try
        {
            if(data!=null)
            {
                for(int i=0;i<data.size();i++)
                {
                    String term = (String)data.get(i);
                    if(term != null && field != null && database != null)
                    {
                        stmt = con.prepareStatement("insert into "+lookupTable+" (field,term,database) values(?,?,?)");
                        stmt.setString(1,field);
                        stmt.setString(2,term);
                        stmt.setString(3,database);
                        stmt.executeUpdate();

                        con.commit();

                        // H:
                        // check count in the lookup table

                        String sqlQuery = "select count(*) count from deleted_lookupIndex";
                        //System.out.println("**Query**"+sqlQuery);
                        count_stmt=con.createStatement();
                        rs = count_stmt.executeQuery(sqlQuery);
                        if(rs.next())
                        {
                            count = rs.getInt("count");

                            //System.out.println("***** LookupIndex count marked for deletion is :"+count);
                        }
                        //H: End


                        if(stmt != null)
                        {
                            stmt.close();
                        }
                        if(count_stmt != null)
                        {
                            count_stmt.close();
                        }
                    }
                }
            }
            con.commit();
            if(stmt != null)
            {
                stmt.close();
            }
            if(count_stmt != null)
            {
                count_stmt.close();
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
            if (count_stmt != null)
            {
                try
                {
                    count_stmt.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /*HT added 09/21/2020 for ES Lookup*/
  	private void processESLookupIndex(Map<String, List<String>> update,Map<String, List<String>> backup)
  			throws Exception {

  		Map<String,String> deletedAuthorLookupIndex = getDeleteData(update, backup, "AUTHOR");
  		Map<String,String> deletedAffiliationLookupIndex = getDeleteData(update, backup, "AFFILIATION");
  		Map<String,String> deletedControlltermLookupIndex = getDeleteData(update, backup, "CONTROLLEDTERM");
  		Map<String,String> deletedPublisherNameLookupIndex = getDeleteData(update, backup, "PUBLISHERNAME");
  		Map<String,String> deletedSerialtitleLookupIndex = getDeleteData(update, backup, "SERIALTITLE");
  		 //H:11/19/2014 check IPC too
        Map<String,String> deletedIpcLookupIndex       		= getDeleteData(update,backup,"IPC");
  		
  		/*ONLY FOR DEBUGGING, UNCOMMENT IN PROD*/
  		System.out.println("AUTHORTOBEDELETEDLIST: " + deletedAuthorLookupIndex.size());
  		System.out.println("AFFTOBEDELETEDLIST: " + deletedAffiliationLookupIndex.size());
  		System.out.println("CVTOBEDELETEDLIST: " + deletedControlltermLookupIndex.size());
  		System.out.println("PNTOBEDELETEDLIST: " + deletedPublisherNameLookupIndex.size());
  		System.out.println("STTOBEDELETEDLIST: " + deletedSerialtitleLookupIndex.size());
  		

  		saveDeletedData("AU", checkES(deletedAuthorLookupIndex, "AU", database), database);
  		saveDeletedData("AF", checkES(deletedAffiliationLookupIndex, "AF", database), database);
  		saveDeletedData("CV", checkES(deletedControlltermLookupIndex, "CV", database), database);
  		saveDeletedData("PN", checkES(deletedPublisherNameLookupIndex, "PN", database), database);
  		saveDeletedData("ST", checkES(deletedSerialtitleLookupIndex, "ST", database), database);
  	//H:11/19/2014 check IPC too
        saveDeletedData("IPC",checkES(deletedIpcLookupIndex,"PID",database),database);

  	}

    private void processLookupIndex(HashMap update,HashMap backup) throws Exception
    {

        database = this.database;


        HashMap outputMap = new HashMap();
        HashMap deletedAuthorLookupIndex            = getDeleteData(update,backup,"AUTHOR");
        HashMap deletedAffiliationLookupIndex       = getDeleteData(update,backup,"AFFILIATION");

        HashMap deletedControlltermLookupIndex      = getDeleteData(update,backup,"CONTROLLEDTERM");
        HashMap deletedPublisherNameLookupIndex     = getDeleteData(update,backup,"PUBLISHERNAME");
        HashMap deletedSerialtitleLookupIndex       = getDeleteData(update,backup,"SERIALTITLE");
        //H:11/19/2014 check IPC too
        HashMap deletedIpcLookupIndex       		= getDeleteData(update,backup,"IPC");

        saveDeletedData("AU",checkFast(deletedAuthorLookupIndex,"AU",database),database);
        saveDeletedData("AF",checkFast(deletedAffiliationLookupIndex,"AF",database),database);
        saveDeletedData("CV",checkFast(deletedControlltermLookupIndex,"CV",database),database);
        saveDeletedData("PN",checkFast(deletedPublisherNameLookupIndex,"PN",database),database);
        saveDeletedData("ST",checkFast(deletedSerialtitleLookupIndex,"ST",database),database);
        //H:11/19/2014 check IPC too
        saveDeletedData("IPC",checkFast(deletedIpcLookupIndex,"PID",database),database);
    }

    
    /*ES added 09/21/2020 for ES Lookup
     *
     * Check ES Count for AU/AFF marked for deletion against update list count
     * when List Count >= FAST COUNT [delete]
     * when List Count< FAST COUNT [ do not delete]
     */
    
    private List<String> checkES(Map<String,String> inputMap, String searchField, String database) throws Exception
    {
        List<String> outputList = new ArrayList<>();

        SharedSearchSearchEntry entry = new SharedSearchSearchEntry("https://shared-search-service-api.prod.scopussearch.net/sharedsearch/document/result");
        outputList = entry.runESLookupCheck(inputMap,searchField,database);
        return outputList;

    }
    
    
    /*
     * Check FAST Count for AU/AFF marked for deletion against update list count
     * when List Count >= FAST COUNT [delete]
     * when List Count< FAST COUNT [ do not delete]
     */
    private List checkFast(HashMap inputMap, String searchField, String database) throws Exception
    {
    	//HH 02/23/2015 set DataBaseConf.DbCorrFlag for local db connection othertan connectionBroker
    	DatabaseConfig.DbCorrFlag = 1;

        List outputList = new ArrayList();
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
        String[] credentials = new String[]{"CPX","PCH","CHM","GEO","GRF","ELT","INS"};
        String[] dbName = {database};

        int intDbMask = databaseConfig.getMask(dbName);

        Iterator searchTerms = inputMap.keySet().iterator();

        while (searchTerms.hasNext())
        {
            String term1=null;
            //H : 11/19/2014 to hold index of IPCCode only
            int i =0;

            try
            {
                SearchControl sc = new FastSearchControl();
                term1 = (String) searchTerms.next();
                //System.out.println("FastSearch: search control term: "+term1);

                int oc = Integer.parseInt((String)inputMap.get(term1));
                Query queryObject = new Query(databaseConfig, credentials);
                queryObject.setDataBase(intDbMask);

                String searchID = (new GUID()).toString();
                queryObject.setID(searchID);
                queryObject.setSearchType(Query.TYPE_QUICK);

              //H:11/19/2014 check IPC too
                if(searchField.equalsIgnoreCase("PID"))
                {
                	if(term1.indexOf("\t") > -1){
                	i = term1.indexOf("\t");
                	term1 = term1.substring(0, i);
                	}
                }

                queryObject.setSearchPhrase("{"+term1+"}",searchField,"","","","","","");
                queryObject.setSearchQueryWriter(new FastQueryWriter());
                queryObject.compile();
                String sessionId = null;
                int pagesize = 25;
                SearchResult result = sc.openSearch(queryObject,sessionId,pagesize,false);

                //H: Fastsearch count
                int c = result.getHitCount();

                //System.out.println("SearchResult Fast getHitcount is: "+c);

                //processLookupIndex lookupindex count
                String indexCount = (String)inputMap.get(term1);

                //H:
                //System.out.println("FAST SEARCH COUNT AGAINST UPDATELIST COUNT FOR KEY Search Term: "+term1+" is: " + Integer.toString(c) + " , " + indexCount);

                if(indexCount!=null && indexCount!="" && Integer.parseInt(indexCount) >= c)
                {
                    outputList.add(term1);

                }

            }
            catch(Exception e)
            {
                //System.out.println("term1= "+term1);
                e.printStackTrace();
            }

        }

      //HH 02/23/2015 Reset DataBaseConf.DbCorrFlag back to default
      DatabaseConfig.DbCorrFlag = 0;

        return outputList;

    }

    /*
     * Find out lookups to be deleted and its count,
     * Lookups that are in old and not in update list will be marked for deletion
     */
    private HashMap<String,String> getDeleteData(Map update,Map backup,String field)
    {
        List backupList = null;
        List updateList = null;
        HashMap deleteLookupIndex = new HashMap();
        if(update !=null && backup != null)
        {
            //System.out.println("parameter Field Value is :"+field);

            backupList = (ArrayList)backup.get(field);   // original Data (old)
            updateList = (ArrayList)update.get(field);   // New Data



            if(backupList!=null)
            {
                String dData = null;

                for(int i=0;i<backupList.size();i++)
                {
                    dData = (String)backupList.get(i);

                    if(dData != null)
                    {
                        if(updateList==null ||(updateList!=null && !updateList.contains(dData)))
                        {
                            if(deleteLookupIndex.containsKey(dData.toUpperCase()))
                            {
                                deleteLookupIndex.put(dData.toUpperCase(),Integer.toString(Integer.parseInt((String)deleteLookupIndex.get(dData.toUpperCase()))+1));

                            }
                            else
                            {
                                deleteLookupIndex.put(dData.toUpperCase(),"1");

                            }

                        }

                    }
                }
            }
        }

        return deleteLookupIndex;
    }

    private boolean checkUpdate(List update,String term)
    {
        if(update != null)
        {
            for(int i=0;i<update.size();i++)
            {
                String updateData = (String)update.get(i);

                if(term.equalsIgnoreCase(updateData))
                {
                    return true;
                }
            }
        }
        return false;

    }


    public HashMap getLookupData(String action,int updateNumber) throws Exception
    {
        Statement stmt = null;
        ResultSet rs = null;
        HashMap results = null;
        try
        {
            stmt = con.createStatement();
            System.out.println("Running the query...");
            if(action.equals("update")||action.equals("ins")||action.equals("lookupindex"))
            {
                //rs = stmt.executeQuery("select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc from ins_master_orig where seq_num is not null and updateNumber='"+updateNumber+"'");

                rs = stmt.executeQuery("select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc from ins_master_orig where updatenumber='"+updateNumber+"'");
                //rs = stmt.executeQuery("select anum,AUS,AUS2,AFFILIATION,AFFILIATION_1,CONTROLLEDTERM,CHEMICALTERM,SOURCETITLE,PUBLISHERNAME,DATABASE,IPC FROM "+tempTable);

            }
            else
            {
                //rs = stmt.executeQuery("select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc from " + backupTable);

                rs = stmt.executeQuery("select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc from " + backupTable);
                //rs = stmt.executeQuery("select ACCESSNUMBER,AUTHOR,AUTHOR_1,AFFILIATION,AFFILIATION_1,CONTROLLEDTERM,CHEMICALTERM,SOURCETITLE,PUBLISHERNAME,DATABASE,IPC FROM "+backupTable);

            }

            System.out.println("Got records ...");
            results = setRecs(rs);

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

        return results;

    }

    private HashMap setRecs(ResultSet rs)
            throws Exception
    {
        int i = 0;
        CombinedWriter writer = new CombinedXMLWriter(10000,10000,"ins","dev");
        //XmlCombiner xml = new XmlCombiner(writer);
        HashMap recs = new HashMap();
        List authorList = new ArrayList();
        List affiliationList = new ArrayList();
        List serialTitleList = new ArrayList();
        List controltermList = new ArrayList();
        List publishernameList = new ArrayList();

        List editorlist=new ArrayList();

        //H: add IPC list
        List ipc = new ArrayList();



        String database = null;
        String accessNumber = null;

        //System.out.println("start setRecs method with input resultset of size: "+rs.getFetchSize());


        try
        {
            INSPECCombiner c = new INSPECCombiner(writer);

            while (rs.next())
            {
                ++i;
                EVCombinedRec rec = new EVCombinedRec();

                accessNumber = rs.getString("anum");


               // if(accessNumber !=null && accessNumber.length()>5)
                //H: 11/19/2014 no limit for anum length, during INS reload ANUM started from 1
                if(accessNumber !=null)
                {
                    rec.put(EVCombinedRec.ACCESSION_NUMBER, accessNumber);

                    // H:
                    //System.out.println("Fast- AccessNumber: "+ rec.get(EVCombinedRec.ACCESSION_NUMBER));

                    if((rs.getString("aus") != null) || (rs.getString("aus2") != null))
                    {
                        StringBuffer aus = new StringBuffer();
                        if(rs.getString("aus") != null)
                        {
                            aus.append(rs.getString("aus"));

                            //H:
                            //System.out.println("Author Name is: "+aus);
                        }
                        if(rs.getString("aus2") != null)
                        {
                            aus.append(rs.getString("aus2"));

                            //H:
                            //System.out.println("Author2 Name is: "+aus);
                        }

                        //recs.put(EVCombinedRec.AUTHOR,c.prepareAuthor(aus.toString()));

                        //H:
                        //authorList.addAll(Arrays.asList(c.prepareAuthor(aus.toString())));
                        authorList.addAll(Arrays.asList(c.prepareAuthor(aus.toString().toUpperCase())));
                        rec.put(EVCombinedRec.AUTHOR, aus.toString().toUpperCase());

                    }
                    else if(rs.getString("eds") != null)
                    {
                        //recs.put(EVCombinedRec.EDITOR, c.prepareAuthor(rs.getString("eds")));

                        //H:
                        //editorlist.addAll(Arrays.asList(c.prepareAuthor(rs.getString("eds"))));
                        editorlist.addAll(Arrays.asList(c.prepareAuthor(rs.getString("eds").toUpperCase())));
                        rec.put(EVCombinedRec.EDITOR, rs.getString("eds").toUpperCase());

                    }

                    if(rs.getString("aaff") != null)
                    {
                        StringBuffer aaff = new StringBuffer(rs.getString("aaff"));

                        if(rs.getString("aaffmulti1") != null)
                        {
                            aaff = new StringBuffer(rs.getString("aaffmulti1"));


                            if (rs.getString("aaffmulti2") != null)
                            {
                                aaff.append(rs.getString("aaffmulti2"));

                            }
                        }

                        //recs.put(EVCombinedRec.AUTHOR_AFFILIATION, c.prepareAuthor(aaff.toString()));

                        //H:
                        //affiliationList.addAll(Arrays.asList(c.prepareAuthor(aaff.toString())));
                        affiliationList.addAll(Arrays.asList(c.prepareAuthor(aaff.toString().toUpperCase())));
                        rec.put(EVCombinedRec.AUTHOR_AFFILIATION,aaff.toString().toUpperCase());
                    }

                    if(rs.getString("cvs") != null)
                    {
                         //recs.put(EVCombinedRec.CONTROLLED_TERMS, c.prepareMulti(rs.getString("cvs")));

                        //H:
                         //controltermList.addAll(Arrays.asList(c.prepareMulti(rs.getString("cvs"))));
                         controltermList.addAll(Arrays.asList(c.prepareMulti(rs.getString("cvs").toUpperCase())));
                         rec.put(EVCombinedRec.CONTROLLED_TERMS, rs.getString("cvs").toString().toUpperCase());

                    }

                    if(rs.getString("ppub") != null)
                    {
                        // recs.put(EVCombinedRec.PUBLISHER_NAME, rs.getString("ppub"));

                        //H:
                         //publishernameList.add(rs.getString("ppub"));

                        publishernameList.add(rs.getString("ppub").toUpperCase());
                        rec.put(EVCombinedRec.PUBLISHER_NAME, rs.getString("ppub").toUpperCase());
                    }

                    if(rs.getString("chi") != null)
                    {
                          //recs.put(EVCombinedRec.CHEMICAL_INDEXING,c.prepareIndexterms(rs.getString("chi")));

                        //H: 11/19/2014 CHI is diff than CVS
                        //controltermList.addAll(Arrays.asList(c.prepareIndexterms(rs.getString("chi").toUpperCase())));
                        rec.put(EVCombinedRec.CHEMICAL_INDEXING, rs.getString("chi").toUpperCase());

                    }

                    if(rs.getString("pubti") != null)
                    {
                        //recs.put(EVCombinedRec.SERIAL_TITLE, rs.getString("pubti"));

                        //H:
                        //serialTitleList.add(rs.getString("pubti"));
                        serialTitleList.add(rs.getString("pubti").toUpperCase());
                        rec.put(EVCombinedRec.SERIAL_TITLE, rs.getString("pubti").toUpperCase());
                    }

                    if(rs.getString("pfjt") != null)
                    {
                        //recs.put(EVCombinedRec.SERIAL_TITLE, rs.getString("pfjt"));

                        //H:
                        serialTitleList.add(rs.getString("pfjt").toUpperCase());
                        rec.put(EVCombinedRec.SERIAL_TITLE, rs.getString("pfjt").toUpperCase());

                    }


                    //if(rs.getString("DATABASE") != null)
                    //{
                    //  database = rs.getString("DATABASE");
                    //}


                    if(rs.getString("ipc")!=null)
                    {
                        String ipcString = rs.getString("ipc");
                        ipcString = perl.substitute("s/\\//SLASH/g", ipcString);
                        //recs.put(EVCombinedRec.INT_PATENT_CLASSIFICATION, ipcString);


                        ipc.addAll(prepareIpc(ipcString));
                    }

                }

                else
                {
                    if(accessNumber==null)
                    {
                        System.out.println("Access Number is Empty!!!!");
                    }
                    if( accessNumber.length()<5)
                    {
                        System.out.println("Access Number is less than five digits!!!!");
                    }

                }
            }

            //H:
            recs.put("AUTHOR",authorList);

            recs.put("EDITOR", editorlist);

            recs.put("AFFILIATION",affiliationList);
            recs.put("CONTROLLEDTERM",controltermList);
            recs.put("PUBLISHERNAME",publishernameList);
            recs.put("SERIALTITLE",serialTitleList);
            recs.put("DATABASE",database);
            //H 11/19/2014 to check fast for IPC
            recs.put("IPC", ipc);
            //recs.put(EVCombinedRec.INT_PATENT_CLASSIFICATION, ipc);

            //recs.put("AUTHOR",authorList);
            //recs.put("AFFILIATION",affiliationList);
            //recs.put("CONTROLLEDTERM",controltermList);
            //recs.put("PUBLISHERNAME",publishernameList);
            //recs.put("SERIALTITLE",serialTitleList);
            //recs.put("DATABASE",database);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return recs;
    }

     List prepareIpc(String aString)
            throws Exception
    {

        ArrayList list = new ArrayList();
        StringTokenizer st = new StringTokenizer(aString, Constants.AUDELIMITER);
        String s;
        String name;
        String code;

        while (st.hasMoreTokens())
        {
            s = st.nextToken().trim();
            if(s.length() > 0)
            {
                if(s.indexOf(Constants.IDDELIMITER) > -1)
                {
                     s = s.replace(Constants.IDDELIMITER,"\t");
                     s = s.toUpperCase().trim();
                }

                list.add(s);
            }

        }

        return list;

    }


     protected Connection getConnection(String connectionURL,
                                             String driver,
                                             String username,
                                             String password)
                throws Exception
     {
                System.out.println("URL= "+connectionURL+" driver= "+driver+" username= "+username+" password= "+password);
                Class.forName(driver);
                Connection con = DriverManager.getConnection(connectionURL,
                                                  username,
                                                  password);
                return con;
     }

}
