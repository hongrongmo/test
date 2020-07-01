/*
 * Created on Feb 24, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.dataloading.upt.loadtime;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.config.ApplicationProperties;
import org.ei.dataloading.CombinedWriter;
import org.ei.dataloading.CombinedXMLWriter;
import org.ei.dataloading.CombinerTimestamp;
import org.ei.dataloading.Combiner;
import org.ei.dataloading.EVCombinedRec;
import org.ei.dataloading.MessageSender;
import org.ei.util.GUID;
import org.ei.xml.Entity;
import org.ei.data.upt.runtime.*;
import org.ei.util.DiskMap;
import org.ei.common.upt.*;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ei.common.Constants;
import org.ei.common.Country;
import org.ei.domain.ClassNodeManager;
import org.ei.domain.Database;
import org.ei.util.kafka.*;

/**
 * @author KFokuo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class UPTCombiner extends CombinerTimestamp {

    Perl5Util perl = new Perl5Util();
    char DELIM = (char) 30;
    char NESTED_DELIM = (char) 31;
    public static final String AUDELIMITER    = new String(new char[] {30});
    public static  String driver = "oracle.jdbc.driver.OracleDriver";
    public static  String url = "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI";
    public static  String username = "ap_pro1";
    public static  String password = "ei3it";
    public static ClassNodeManager nodeManager = null;
    public static String US_CY = "US";
    public static String EP_CY = "EP";
    Hashtable hashtable = new Hashtable();
    private static final Database UPTDatabase = new UPTDatabase();
	private String ipcdir;
	//DiskMap ipc;
	Hashtable ipc;

    //HH 01/28/2015 to use same ClassNodemanager without change
    static ApplicationProperties applicationProperties;


    public UPTCombiner(String database,CombinedWriter writer) {
        super(writer);
    }

    public UPTCombiner(CombinedWriter writer) {
        super(writer);
        InitApplicatiopnProperties();			//HH 01/28/2015 to use same ClassNodemanager without change
        init();

    }
    //HH 01/28/2015 to use same ClassNodemanager without change
    public static void InitApplicatiopnProperties()
    {
    	applicationProperties = ApplicationProperties.getInstance();
    	applicationProperties.setProperty(ApplicationProperties.USPTO_LUCENE_INDEX_DIR, "uspto");
    	applicationProperties.setProperty(ApplicationProperties.IPC_LUCENE_INDEX_DIR,"ipc");
    	applicationProperties.setProperty(ApplicationProperties.ECLA_LUCENE_INDEX_DIR,"ecla");
    	ClassNodeManager.init(applicationProperties);
    }

    public void init() {

        try {
            nodeManager = ClassNodeManager.getInstance();			
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(0);
        }

    }
    public void writeCombinedByWeekHook(Connection con, int week) throws Exception {

        Statement stmt = null;
        ResultSet rs = null;

        try {
            //stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,  ResultSet.CONCUR_READ_ONLY); //use for ES extraction only
            stmt = con.createStatement();
            //Prod
            String query="SELECT isc,dun,dan,pd,inv_ctry,xpb_dt,inv_addr,asg_addr,fre_ti,ger_ti,ltn_ti,asg_ctry,la,cit_cnt,ref_cnt,ucl,usc,ucc,fd,kd,dt,ds,inv,asg,ti,ab,oab,pn,py,ac,kc,pi,ain,aid,aic,aik,ds,ecl,fec,ipc,ipc8,ipc8_2,fic,aty,pe,ae,icc,ecc,isc,esc,m_id,load_number,seq_num,CLASSIFICATION_CPC,UPDATE_NUMBER FROM "+Combiner.TABLENAME+" WHERE  LOAD_NUMBER = " + week;
            
            // HH Temp for testing
            //String query="SELECT isc,dun,dan,pd,inv_ctry,xpb_dt,inv_addr,asg_addr,fre_ti,ger_ti,ltn_ti,asg_ctry,la,cit_cnt,ref_cnt,ucl,usc,ucc,fd,kd,dt,ds,inv,asg,ti,ab,oab,pn,py,ac,kc,pi,ain,aid,aic,aik,ds,ecl,fec,ipc,ipc8,ipc8_2,fic,aty,pe,ae,icc,ecc,isc,esc,m_id,load_number,seq_num,CLASSIFICATION_CPC FROM "+Combiner.TABLENAME+" WHERE PN='9925573' and LOAD_NUMBER = " + week;
            
            System.out.println("Running the query..."+query);
            rs = stmt.executeQuery(query);

            System.out.println("Got records ...");
            writeRecs(rs, con, week);
            System.out.println("Wrote records.");

            this.writer.flush();
            this.writer.end();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {

            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /*
			if(this.ipc!=null)
			{
				try{
					this.ipc.close();
				}
				 catch (Exception e) {
					e.printStackTrace();
				}
			}
			*/
            if (nodeManager != null)
                nodeManager.close();
        }
    }
    
    public void writeCombinedByTableHook(Connection con)
    		throws Exception
    		{
    			Statement stmt = null;
    			ResultSet rs = null;
    			try
    			{
    			
    				//stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    				stmt = con.createStatement();
    				
    				System.out.println("Running the query...");
    				String sqlQuery = "select * from " + Combiner.TABLENAME;
    				System.out.println(sqlQuery);
    				rs = stmt.executeQuery(sqlQuery);
    				
    				System.out.println("Got records ...from table::"+Combiner.TABLENAME);
    				writeRecs(rs,con,1);
    				System.out.println("Wrote records.");
    				this.writer.end();
    				this.writer.flush();
    			
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

    public void writeCombinedByTimestampHook(Connection con, long timestamp) throws Exception {

        Statement stmt = null;
        ResultSet rs = null;

        try {

        	//stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        	stmt = con.createStatement();
        	 
            String query="SELECT isc,dun,dan,pd,inv_ctry,xpb_dt,inv_addr,asg_addr,fre_ti,ger_ti,ltn_ti,asg_ctry,la,cit_cnt,ref_cnt,ucl,usc,ucc,fd,kd,dt,ds,inv,asg,ti,ab,oab,pn,py,ac,kc,pi,ain,aid,aic,aik,ds,ecl,fec,ipc,ipc8,ipc8_2,fic,aty,pe,ae,icc,ecc,isc,esc,m_id,load_number,seq_num,CLASSIFICATION_CPC,UPDATE_NUMBER FROM "+Combiner.TABLENAME+" WHERE  update_number="+timestamp+" and load_number!="+timestamp;
            System.out.println("Running the query..."+query);

            rs = stmt.executeQuery(query);
            System.out.println("Got records ...");
            writeRecs(rs, con, (int)timestamp);
            System.out.println("Wrote records.");
            this.writer.end();
            this.writer.flush();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {

            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
           
            if (nodeManager != null)
                nodeManager.close();
        }
    }
    public void writeCombinedByYearHook(Connection con, int year) throws Exception {

        Statement stmt = null;
        ResultSet rs = null;

        try {

        	//stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        	stmt = con.createStatement();
        	 
            String query="SELECT isc,dun,dan,pd,inv_ctry,xpb_dt,inv_addr,asg_addr,fre_ti,ger_ti,ltn_ti,asg_ctry,la,cit_cnt,ref_cnt,ucl,usc,ucc,fd,kd,dt,ds,inv,asg,ti,ab,oab,pn,py,ac,kc,pi,ain,aid,aic,aik,ds,ecl,fec,ipc,ipc8,ipc8_2,fic,aty,pe,ae,icc,ecc,isc,esc,m_id,load_number,seq_num,CLASSIFICATION_CPC,UPDATE_NUMBER FROM " + Combiner.TABLENAME + " WHERE  PY = '" + year + "'";
            System.out.println("Running the query..."+query);
            rs = stmt.executeQuery(query);
            //rs = stmt.executeQuery("SELECT isc,dun,dan,pd,inv_ctry,xpb_dt,inv_addr,asg_addr,fre_ti,ger_ti,ltn_ti,asg_ctry,la,cit_cnt,ref_cnt,ucl,usc,ucc,fd,kd,dt,ds,inv,asg,ti,ab,oab,pn,py,ac,kc,pi,ain,aid,aic,aik,ds,ecl,fec,ipc,ipc8,ipc8_2,fic,aty,pe,ae,icc,ecc,isc,esc,m_id,load_number FROM " +Combiner.TABLENAME + " WHERE M_ID in ('upt_9f671b1194ed5ace833362061377553', 'upt_1bd0dd411759e6051dM78e82061377553', 'upt_1bd0dd411759e6051dM7fe42061377553', 'upt_1bd0dd411759e6051dM7c812061377553', 'upt_1bd0dd411759e6051dM7b7c2061377553', 'upt_1bd0dd411759e6051dM7e6c2061377553', 'upt_1bd0dd411759e6051dM79432061377553', 'upt_d70d7a11759deb7a8M77012061377553', 'upt_1bd0dd411759e6051dM7c1e2061377553', 'upt_b5f53a11759e3aa7cM7bd92061377553', 'upt_1bd0dd411759e6051dM790b2061377553', 'upt_1bd0dd411759e6051dM7f372061377553', 'upt_d70d7a11759deb7a8M74082061377553', 'upt_1bd0dd411759e6051dM77dc2061377553', 'upt_1bd0dd411759e6051dM7e082061377553', 'upt_1bd0dd411759e6051dM7e312061377553')");
            System.out.println("Got records ...");
            writeRecs(rs, con, year);
            System.out.println("Wrote records.");
            this.writer.flush();
            this.writer.end();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {

            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            /*
			if(this.ipc!=null)
			{
				try{
					this.ipc.close();
				}
				 catch (Exception e) {
					e.printStackTrace();
				}
			}
			*/
            if (nodeManager != null)
                nodeManager.close();
        }
    }
    
    public static int getResultSetSize(Connection con,int week)
	{
		    int size = -1;
		    Statement stmt = null;
	        ResultSet rs = null;
	        String query=null;

	        try 
	        {
	        	 stmt = con.createStatement();   
	        	 if(week==1)
	        	 {
	        		query="SELECT count(*) count FROM " + Combiner.TABLENAME;
	        	 }
	        	 else if(week>100)
	        	 {
	        		 query="SELECT count(*) count FROM " + Combiner.TABLENAME + " WHERE  load_number=" + week; 
	        	 }
	             System.out.println("Running the query..."+query);
	             rs = stmt.executeQuery(query);
	             while (rs.next()) 
	             {      	 
	            	 size = rs.getInt("count");
	             }
		    }
		    catch(SQLException e)
		    {
		    	e.printStackTrace();		      
		    }
	        finally 
	        {

	            if (rs != null) {
	                try {
	                    rs.close();
	                }
	                catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	            if (stmt != null) {
	                try {
	                    stmt.close();
	                }
	                catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
		    return size;
	        }
	}

    public void writeRecs(ResultSet rs, Connection con, int week) throws Exception 
    {

        int i = 0;
        String mid = null;
        //Thread thread=null;
        KafkaService kafka=null;
        kafka = new KafkaService(); //use it for ES extraction only
        int MAX_THREAD = 100; 
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREAD);  
        long processTime = System.currentTimeMillis();
        int totalCount =0;
        
        try 
        {
        	
        	totalCount = getResultSetSize(con, week);  //used for ES extraction only
    	    System.out.println("epoch="+processTime+" database=UPT totalCount="+totalCount+" for week of "+week); //used for ES extrasction only
    	    
            while (rs.next()) 
            {
            	try 
            	{
	                ++i;
	
	                mid = rs.getString("m_id");
	
	                EVCombinedRec rec = new EVCombinedRec();
	                String processInfo = processTime+"-"+totalCount+'-'+i+"-upt-"+rs.getString("LOAD_NUMBER")+'-'+rs.getString("UPDATE_NUMBER");
	            	rec.put(EVCombinedRec.PROCESS_INFO, processInfo);
	
	                /*
	                if (Combiner.EXITNUMBER != 0 && i > Combiner.EXITNUMBER) {
	                    break;
	                }
	                */
	                if (validYear(rs.getString("py"))) {
	
	                    if (rs.getClob("ab") != null) {
	
	                        String abs = getStringFromClob(rs.getClob("ab"));
	
	                        if (!abs.equalsIgnoreCase("QQ")) {
	                            rec.put(EVCombinedRec.ABSTRACT, Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(abs))));
	                        }
	                    }
	
	                    if (rs.getClob("oab") != null) {
	
	                        String oab = getStringFromClob(rs.getClob("oab"));
	
	                        if (!oab.equalsIgnoreCase("QQ")) {
	                            rec.put(EVCombinedRec.OTHER_ABSTRACT, Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(oab))));
	                        }
	                    }
	
	                    if (rs.getString("m_id") != null)
	                        rec.put(EVCombinedRec.DOCID, rs.getString("m_id"));
	
	                    if (rs.getString("seq_num") != null)
	                        rec.put(EVCombinedRec.PARENT_ID, rs.getString("seq_num"));
	
	                    String patentNumber = Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("pn"))));
	
	                    String kindCode = Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("kc"))));
	
	                    String authCode = Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("ac"))));
	
	                    if (authCode != null)
	                        rec.put(EVCombinedRec.AUTHORITY_CODE, authCode);
	
	                    if (patentNumber != null) {
	                        rec.put(EVCombinedRec.PATENT_NUMBER, formatPN(patentNumber, kindCode, authCode));
	                    }
	
	                    if (kindCode != null)
	                        rec.put(EVCombinedRec.PATENT_KIND, Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(kindCode))));
	
	                    if (rs.getString("kd") != null)
	                        rec.put(EVCombinedRec.KIND_DESCR, Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("kd")))));
	
	                    if (rs.getString("ti") != null)
	                        rec.put(EVCombinedRec.TITLE, Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("ti")))));
	
	                    if (rs.getString("inv") != null) {
	
	                        if (rs.getString("asg") != null) {
	
	                            List lstAsg = convertString2List(rs.getString("asg"));
	                            List lstInv = convertString2List(rs.getString("inv"));
	
	                            if (authCode.equals(EP_CY) && lstInv.size() > 0 && lstAsg.size() > 0) {
	                                lstInv = AssigneeFilter.filterInventors(lstAsg, lstInv, false);
	                            }
	
	                            String[] arrVals = (String[]) lstInv.toArray(new String[1]);
	
	                            arrVals[0] = replaceNull(arrVals[0]);
	
	                            for (int j = 0; j < arrVals.length; j++) {
	                                arrVals[j] = formatAuthor(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(arrVals[j]))));
	                            }
	
	                            if (arrVals != null)
	                                rec.put(EVCombinedRec.AUTHOR, arrVals);
	
	                        }
	                        else {
	
	                            rec.put(EVCombinedRec.AUTHOR, convert2Array(formatAuthor(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("inv")))))));
	                        }
	                    }
	
	                    if (rs.getString("asg") != null) {
	
	                        if (rs.getString("inv") != null) {
	
	                            List lstAsg = convertString2List(rs.getString("asg"));
	                            List lstInv = convertString2List(rs.getString("inv"));
	
	                            if (authCode.equals(US_CY) && lstInv.size() > 0 && lstAsg.size() > 0) {
	                                lstAsg = AssigneeFilter.filterInventors(lstInv, lstAsg, true);
	                            }
	
	                            String[] arrVals = (String[]) lstAsg.toArray(new String[1]);
	
	                            arrVals[0] = replaceNull(arrVals[0]);
	
	                            for (int j = 0; j < arrVals.length; j++) {
	                                arrVals[j] = Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(arrVals[j])));
	
	                            }
	
	                            if (arrVals != null)
	                                rec.put(EVCombinedRec.AUTHOR_AFFILIATION, arrVals);
	                        }
	                        else {
	                            rec.put(EVCombinedRec.AUTHOR_AFFILIATION, convert2Array(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("asg"))))));
	                        }
	                    }
	
	                    if (rs.getString("fd") != null)
	                        rec.put(EVCombinedRec.PATENT_FILING_DATE, Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(formatDate(rs.getString("fd"))))));
	
	                    if (rs.getString("py") != null)
	                        rec.put(EVCombinedRec.PUB_YEAR, Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("py")))));
	
	                    if (rs.getString("pd") != null) {
	
	                        rec.put(EVCombinedRec.PATENTISSUEDATE, Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(formatDate(rs.getString("pd"))))));
	
	                        boolean isApp = isApplication(kindCode);
	
	                        if (isApp) {
	                            rec.put(EVCombinedRec.PATENTAPPDATE, formatDate(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("pd"))))));
	                        }
	                    }
	
	                    //String appCtry = Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("aic"))));
	
	
	                    /*
	                    if (appCtry != null)
	                        rec.put(EVCombinedRec.APPLICATION_COUNTRY, Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(appCtry))));
	                    */
	                    String app_country = null;
	
	                    if(rs.getString("aic") != null)
	                    {
	                        app_country = rs.getString("aic");
	
	                        List app_countryList = new ArrayList();
	
	                        String[] values = null;
	                        values = app_country.split(AUDELIMITER);
	                        for(int x = 0 ; x < values.length; x++)
	                        {
	                            app_countryList.add(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(values[x]))));
	                        }
	
	                        if(!app_countryList.isEmpty())
	                        {
	                            rec.putIfNotNull(EVCombinedRec.APPLICATION_COUNTRY, (String[]) app_countryList.toArray(new String[]{}));
	                        }
	                    }
	
	
	
	                    if (rs.getString("ain") != null) {
	                        List pubNums = formatPN(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("ain")))), app_country);
	
	                        List lstAppNums = new ArrayList();
	
	                        if (rs.getString("dan") != null) {
	                            lstAppNums.add(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("dan")))));
	                        }
	                        if (rs.getString("dun") != null) {
	                            lstAppNums.add(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("dun")))));
	                        }
	
	                        lstAppNums.addAll(pubNums);
	
	                        String arrAppNums[] = null;
	
	                        arrAppNums = (String[]) lstAppNums.toArray(new String[1]);
	                        arrAppNums[0] = replaceNull(arrAppNums[0]);
	
	                        rec.put(EVCombinedRec.APPLICATION_NUMBER, arrAppNums);
	                    }
	                    else {
	
	                        List lstAppNums = new ArrayList();
	
	                        if (rs.getString("dan") != null) {
	                            lstAppNums.add(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("dan")))));
	                        }
	                        if (rs.getString("dun") != null) {
	                            lstAppNums.add(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("dun")))));
	                        }
	
	                        String arrAppNums[] = null;
	
	                        arrAppNums = (String[]) lstAppNums.toArray(new String[1]);
	                        arrAppNums[0] = replaceNull(arrAppNums[0]);
	
	                        rec.put(EVCombinedRec.APPLICATION_NUMBER, arrAppNums);
	                    }
	
	                    if (rs.getString("aid") != null)
	                        rec.put(EVCombinedRec.PATENTAPPDATE, Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(formatDate(rs.getString("aid"))))));
	                    else {
	
	                        boolean isApp = isApplication(kindCode);
	
	                        if (rs.getString("xpb_dt") != null) {
	                            if (!isApp) {
	                                rec.put(EVCombinedRec.PATENTAPPDATE, Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(formatDate(rs.getString("xpb_dt"))))));
	                            }
	                        }
	                    }
	
	                    if (rs.getString("pi") != null) {
	                        rec.put(EVCombinedRec.PRIORITY_NUMBER, getPriorityNumber(replaceNull(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("pi")))))));
	                        rec.put(EVCombinedRec.PRIORITY_DATE, getPriorityDate(replaceNull(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("pi")))))));
	                        rec.put(EVCombinedRec.PRIORITY_KIND, getPriorityKind(replaceNull(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("pi")))))));
	                        rec.put(EVCombinedRec.PRIORITY_COUNTRY, getPriorityCountry(replaceNull(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("pi")))))));
	                    }
	
	                    rec.put(EVCombinedRec.DEDUPKEY, getDedupKey());
	
	                    if (rs.getString("aty") != null) {
	                        String atys[] = convert2Array(rs.getString("aty"));
	
	                        atys[0] = replaceNull(atys[0]);
	
	                        for (int j = 0; j < atys.length; j++) {
	                            atys[j] = formatAuthor(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(atys[j]))));
	                        }
	
	                        rec.put(EVCombinedRec.ATTORNEY_NAME, atys);
	                    }
	                    if (rs.getString("pe") != null) {
	
	                        String pes[] = convert2Array(rs.getString("pe"));
	
	                        pes[0] = replaceNull(pes[0]);
	
	                        for (int j = 0; j < pes.length; j++) {
	                            pes[j] = formatAuthor(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(pes[j]))));
	                        }
	
	                        rec.put(EVCombinedRec.PRIMARY_EXAMINER, pes);
	                    }
	                    if (rs.getString("ae") != null) {
	
	                        String aes[] = convert2Array(rs.getString("ae"));
	
	                        aes[0] = replaceNull(aes[0]);
	
	                        for (int j = 0; j < aes.length; j++) {
	                            aes[j] = formatAuthor(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(aes[j]))));
	                        }
	
	                        rec.put(EVCombinedRec.ASSISTANT_EXAMINER, aes);
	                    }
	                   
	                    if (authCode.equalsIgnoreCase("us"))
	                        rec.put(EVCombinedRec.DATABASE, "upa");
	                    else if(authCode.equalsIgnoreCase("wo"))
	                    	rec.put(EVCombinedRec.DATABASE, "wop");
	                    else if(authCode.equalsIgnoreCase("ep"))
	                    	rec.put(EVCombinedRec.DATABASE, "eup");
	                    else
	                        rec.put(EVCombinedRec.DATABASE, authCode+"p");
	
	                    if (rs.getString("ds") != null)
	                        rec.put(EVCombinedRec.DESIGNATED_STATES, removeDupDS(convert2Array(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("ds")))))));
	
	                    List lstEclaCodes = new ArrayList();
	                    List lstFecCodes = new ArrayList();
	
	                    if (rs.getString("ecl") != null)
	                        lstEclaCodes = convertString2List(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("ecl")))));
	
	                    if (rs.getString("fec") != null)
	                        lstFecCodes = convertString2List(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("fec")))));
	
	                    lstEclaCodes.addAll(lstFecCodes);
	
	                    String eclaVals[] = (String[]) lstEclaCodes.toArray(new String[1]);
	
	                    eclaVals = eclaNormalize(eclaVals);
	                    
	                    String[] cpcValues = null;
	                    if (rs.getString("CLASSIFICATION_CPC") != null) {
	
	                        String sbrCPC = rs.getString("CLASSIFICATION_CPC");
	                        cpcValues = sbrCPC.split(Constants.IDDELIMITER);	                     
	                    }
	                    eclaVals = removeDupCode(eclaVals,cpcValues);
	                    
	                    rec.put(EVCombinedRec.ECLA_CODES, removeSpaces(eclaVals));
	
	                    if (rs.getString("ecc") != null)
	                        rec.put(EVCombinedRec.ECLA_CLASSES, removeSpaces(convert2Array(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("ecc")))))));
	
	                    if (rs.getString("esc") != null)
	                        rec.put(EVCombinedRec.ECLA_SUB_CLASSES, removeSpaces(convert2Array(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("esc")))))));
	
	                    List arrIpcCodes = new ArrayList();
	                    List arrFicCodes = new ArrayList();
	                    List arrIpc8Codes = new ArrayList();
	
	                    if (rs.getString("ipc8") != null) {
	
	                        StringBuffer sbrIPC8 = new StringBuffer(rs.getString("ipc8"));
	
	                        if (rs.getString("ipc8_2") != null) {
	                           sbrIPC8.append(rs.getString("ipc8_2"));
	                        }
	
	                        arrIpc8Codes = IPC8Classification.build(sbrIPC8.toString());
	                        arrIpc8Codes = normalizeIpc8Codes(arrIpc8Codes);
	                    }
	                    else
	                    {
	
	                        if (rs.getString("ipc") != null) {
	                            arrIpcCodes = convertString2List(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("ipc")))));
	                            String subs[] = convert2Array(rs.getString("isc"));
	                            arrIpcCodes = normalizeIpcCodes(arrIpcCodes, subs);
	                        }
	
	                        if (rs.getString("fic") != null) {
	                            arrFicCodes = convertString2List(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("fic")))));
	                            String subs[] = convert2Array(rs.getString("isc"));
	                            arrFicCodes = normalizeIpcCodes(arrFicCodes, subs);
	                        }
	                    }
	                    arrIpcCodes.addAll(arrIpc8Codes);
	                    arrIpcCodes.addAll(arrFicCodes);
	
	                    String[] ipcValues = (String[]) arrIpcCodes.toArray(new String[1]);
	
	                    ipcValues[0] = replaceNull(ipcValues[0]);
	
	                    rec.put(EVCombinedRec.INT_PATENT_CLASSIFICATION, removeSpaces(ipcValues));
	
	                    if (rs.getString("icc") != null)
	                        rec.put(EVCombinedRec.INT_PATENT_CLASSES, removeSpaces(convert2Array(replaceNull(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("icc"))))))));
	
	                    if (rs.getString("isc") != null)
	                        rec.put(EVCombinedRec.INT_PATENT_SUB_CLASSES, removeSpaces(convert2Array(replaceNull(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("isc"))))))));
	
	                    List lstUspToCodes = new ArrayList();
	                    List lstUspToClassCodes = new ArrayList();
	                    List lstUspToSubCodes = new ArrayList();
	
	                    if (rs.getString("ucl") != null)
	                        lstUspToCodes = normalizeUSCodes(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("ucl")))));
	
	                    if (rs.getString("ucc") != null)
	                        lstUspToClassCodes = normalizeUSClass(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("ucc")))));
	
	                    if (rs.getString("usc") != null)
	                        lstUspToSubCodes = convertString2List(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("usc")))));
	
	                    String usptoCodes[] = (String[]) lstUspToCodes.toArray(new String[1]);
	                    String usptoClassCodes[] = (String[]) lstUspToClassCodes.toArray(new String[1]);
	                    String usptoSubCodes[] = (String[]) lstUspToSubCodes.toArray(new String[1]);
	
	                    usptoCodes[0] = replaceNull(usptoCodes[0]);
	                    usptoClassCodes[0] = replaceNull(usptoClassCodes[0]);
	                    usptoSubCodes[0] = replaceNull(usptoSubCodes[0]);
	
	                    rec.put(EVCombinedRec.USPTOCODE, removeSpaces(usptoCodes));
	                    rec.put(EVCombinedRec.USPTOCLASS, removeSpaces(usptoClassCodes));
	                    rec.put(EVCombinedRec.USPTOSUBCLASS, removeSpaces(usptoSubCodes));
	
	                    if (rs.getString("load_number") != null)
	                        rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("load_number"));
	                    
	                    if (rs.getString("update_number") != null)
	                        rec.put(EVCombinedRec.UPDATE_NUMBER, rs.getString("update_number"));
	
	                    if (rs.getString("dt") != null && rs.getString("kc") !=null)
	                        rec.put(EVCombinedRec.DOCTYPE, formatDocType(rs.getString("dt"),rs.getString("kc")));
	
	                    if (rs.getString("la") != null)
	                        rec.put(EVCombinedRec.LANGUAGE, Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("la")))));
	
	                    if (rs.getString("cit_cnt") != null)
	                        rec.put(EVCombinedRec.PCITED, rs.getString("cit_cnt"));
	
	                    if (rs.getString("inv_ctry") != null) {
	                        rec.put(EVCombinedRec.COUNTRY, getCountry(convert2Array(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("inv_ctry")))))));
	                    }
	                    else {
	                        if (rs.getString("asg_ctry") != null) {
	                            rec.put(EVCombinedRec.COUNTRY, getCountry(convert2Array(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("asg_ctry")))))));
	
	                        }
	                    }
	
	                    List titles = new ArrayList();
	
	                    if (rs.getString("fre_ti") != null)
	                        titles.add(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("fre_ti")))));
	                    if (rs.getString("ger_ti") != null)
	                        titles.add(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("ger_ti")))));
	                    if (rs.getString("ltn_ti") != null)
	                        titles.add(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("ltn_ti")))));
	
	                    String[] tiVals = (String[]) titles.toArray(new String[1]);
	
	                    tiVals[0] = replaceNull(tiVals[0]);
	
	                    rec.put(EVCombinedRec.TRANSLATED_TITLE, tiVals);
	
	                    String[] patentIds = getReferences(con, mid);
	
	                    rec.put(EVCombinedRec.PCITEDINDEX, patentIds);
	
	
	                    List usclNames = new ArrayList();
	                    List eclaNames = new ArrayList();
	                    List ipcNames = new ArrayList();
	
	                    hashtable.clear();
	
	                    if (rs.getString("ucl") != null)
	                        usclNames = getUSCLClassName(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("ucl")))));
	
	                    if (rs.getString("ecl") != null)
	                        eclaNames = getECLAClassName(removeSpaces(eclaVals));
	
	                    if (rs.getString("ipc") != null || rs.getString("ipc8") != null && ipcValues!=null)
	                        ipcNames = getIPCClassName(removeSpaces(ipcValues));
	
	                    List allNames = new ArrayList();
	
	                    if(!hashtable.isEmpty()){
	                        Enumeration e = hashtable.elements();
	                         while( e.hasMoreElements() ){
	                             allNames.add(e.nextElement());
	                         }
	                    }                    
	                    
	                    if(cpcValues!=null)
	                    {
	                    	rec.put(EVCombinedRec.CPCCLASS, removeSpaces(cpcValues));
	                    }
	                    
	                    String arrNames[] = null;
	
	                    arrNames = (String[]) allNames.toArray(new String[1]);
	                    arrNames[0] = replaceNull(arrNames[0]);
	
	                    rec.put(EVCombinedRec.NOTES, arrNames);	                 
	
	                    //writer.writeRec(rec);//use this line for FAST extraction
	                    
	                    //use this block of code to send data to kafka server
	                    //**********************************************************
	        	        //following code used to test kafka by hmo@2020/01/30
	        	        //this.writer.writeRec(recArray,kafka);
	        	        //**********************************************************
	                    
	        	        //writer.writeRec(rec,kafka);
	        	       
	                    //use thread to run kafka message
	                    
	                    MessageSender sendMessage= new MessageSender(rec,kafka,this.writer);
	                    pool.execute(sendMessage);
	       	         	//thread = new Thread(sendMessage);
	       	         	//thread.start();
	       	         	
	       	         	
	       	         	
	
	                }
				}
				catch (Exception e) {
				      System.out.println("MID=" + mid);
				      e.printStackTrace();
				
				}
            }
        }
        catch (Exception ex) {            
            ex.printStackTrace();

        }
        finally 
        {
			if (rs != null) 
			{
			    try {
			        rs.close();
			    }
			    catch (Exception e) {
			        e.printStackTrace();
			    }
			}
			try 
        	{
        		pool.shutdown();
        	}
        	catch(Exception ex) 
        	{
        		ex.printStackTrace();
        	}
        	if(kafka!=null)
 	        {
        		try 
            	{
        			kafka.close();
            	}
            	catch(Exception ex) 
            	{
            		ex.printStackTrace();
            	}
	        	
	        
	        }
        	System.out.println("total "+i +" records");
 		    if(i!=totalCount)
 		    {
 		    	System.out.println("**Got "+i+" records instead of "+totalCount+" for week of "+week );
 		    }
            
        }
    } //writeRecs

    public String[] eclaNormalize(String[] eclas) {
        for (int i = 0; i < eclas.length; i++) {
            String s = eclas[i];
            if (s == null) {
                s = "";
            }
            else {
                s = ECLAClassNormalizer.normalize(s);
            }

            eclas[i] = s;
        }

        return eclas;
    }

    public List normalizeIpcCodes(List codes, String[] subs) {

        List newCodes = new ArrayList();

        for (int i = 0; i < codes.size(); i++) {
            String code = (String) codes.get(i);
            code = IPCClassNormalizer.trimLeadingZeroFromSubClass(code, subs);
            newCodes.add(code);
        }

        return newCodes;
    }
    
    private String[] removeDupCode(String[] ecla,String[] cpc)
    {
    	List outputCodes = new ArrayList();
    	if(ecla == null || ecla.length==0)
    	{
    		return cpc;
    	}
    	else if(cpc == null || cpc.length==0)
    	{
    		return ecla;
    	}
    	else
    	{    		
    		for(int i=0;i<ecla.length;i++)
    		{
    			String eclaCode = ecla[i];
    			if(!outputCodes.contains(eclaCode))
    			{
    				outputCodes.add(eclaCode);
    			}  			
    		}
    		
    		for(int j=0;j<cpc.length;j++)
			{
				String cpcCode = cpc[j];
				if(!outputCodes.contains(cpcCode))
    			{
    				outputCodes.add(cpcCode);
    			}
			}
    	}
    	String[] arr = new String[outputCodes.size()];
    	return (String[])outputCodes.toArray(arr);
    }

    public List normalizeIpc8Codes(List codes) {

        List newCodes = new ArrayList();

        for (int i = 0; i < codes.size(); i++) {
            IPC8Classification ipc = (IPC8Classification)codes.get(i);
            String code = ipc.getCode();
            newCodes.add(code);
        }

        return newCodes;
    }
    public String replaceAmpersand(String sVal) {

        if (sVal == null)
            return "";

        if (perl.match("/&amp;/i", sVal)) {
            sVal = perl.substitute("s/&amp;/&/ig", sVal);
        }

        return sVal;
    }
    public List getUSCLClassName(String codes) throws Exception {

        List lstCodes = convertString2List(codes);
        List names = new ArrayList();
        String[] arrNames = null;

        try {
            for (int i = 0; i < lstCodes.size(); i++) {
                String code = (String) lstCodes.get(i);

                if (perl.match("/SLASH/i", code))
                    code = perl.substitute("s/SLASH/\\//ig", code);

                if (perl.match("/PERIOD/i", code))
                    code = perl.substitute("s/PERIOD/\\./ig", code);

                code = USPTOClassNormalizer.normalize(code);

                String name = nodeManager.seekUS(code);

                if(name != null){
                    name = perl.substitute("s/\\(\\:\\)/QQ/ig", name);
                    String key = perl.substitute("s/[^a-zA-Z]//g", name);
                    if(name.indexOf("QQ") > 1){
                        String[] temp = name.split("QQ");
                        String[] tempKey = key.split("QQ");
                        hashtable.put(tempKey[tempKey.length-1].toLowerCase(), temp[temp.length-1].toLowerCase());
                    }
                }
            }
            if(!hashtable.isEmpty()){
                Enumeration e = hashtable.elements();
                 while( e.hasMoreElements() ){
                     names.add(e.nextElement());
                 }
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
        return names;
    }
    public List getECLAClassName(String[] lstCodes) throws Exception {

        List names = new ArrayList();
        String[] arrNames = null;

        try {
            for (int i = 0; i < lstCodes.length; i++) {
                String code = lstCodes[i];

                if (perl.match("/SLASH/i", code))
                    code = perl.substitute("s/SLASH/\\//ig", code);

                if (perl.match("/PERIOD/i", code))
                    code = perl.substitute("s/PERIOD/\\./ig", code);

                String name = nodeManager.seekECLA(code);

                if (name != null){
                    name = perl.substitute("s/\\(\\:\\)/QQ/ig", name);
                    String key = perl.substitute("s/[^a-zA-Z]//g", name);
                    if(name.indexOf("QQ") > 1){
                        String[] temp = name.split("QQ");
                        String[] tempKey = key.split("QQ");
                        hashtable.put(tempKey[tempKey.length-1].toLowerCase(), temp[temp.length-1].toLowerCase());
                    }
                }
            }
            if(!hashtable.isEmpty()){
                Enumeration e = hashtable.elements();
                 while( e.hasMoreElements() ){
                     names.add(e.nextElement());
                 }
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }


        return names;

    }



	public synchronized String seekIPC(String code) throws Exception
	{
		//String s = ipc.get(code);
		String s = nodeManager.seekIPC(code);
		s = Entity.replaceLatinChars(s);
		// System.out.println("Code="+code+" name= "+s);
		/*
		if (s == null && code!=null) {
			s = getDescriptionFromLookupIndex(code);
		}
		*/
		if (s == null && code!=null) {
			s = (String)ipc.get(code);
		}
		return s;
    }

	public String getDescriptionFromLookupIndex(String code) throws Exception
	{
	        Connection conn = null;
	        Statement stmt = null;
	        ResultSet rset = null;
	        String description = null;
	        if(code.indexOf("'")>0)
	        {
	        	code = code.replaceAll("'", "");
	        }
	        String sql = "select description from CMB_IPC_LOOKUP WHERE replace(ipccode,'SLASH','')='" + code + "'";
	        int rows = 0;

	        try {

	            conn = getConnection(url, driver, username, password);
	            stmt = conn.createStatement();
	            rset = stmt.executeQuery(sql);
	            while (rset.next()) {
	                description = rset.getString("description");
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new Exception(e);
	        } finally {
	            if (rset != null) {
	                try {
	                    rset.close();
	                } catch (Exception se) {
	                    se.printStackTrace();
	                }
	            }

	            if (stmt != null) {
	                try {
	                    stmt.close();
	                } catch (Exception se) {
	                    se.printStackTrace();
	                }
	            }

	            if (conn != null) {
	                try {
	                    conn.close();
	                } catch (Exception e1) {
	                     e1.printStackTrace();
	                }
	            }
	        }

	        return description;
    }
	
	public Hashtable getAllDescriptionFromLookupIndex() throws Exception
	{
	        Connection conn = null;
	        Statement stmt = null;
	        ResultSet rset = null;
	        String description = null;
	        String ipcCode = null;
	        Hashtable ipcDescription = new Hashtable();
	        String sql = "select replace(ipccode,'SLASH','') ipccode,description from CMB_IPC_LOOKUP";
	        int rows = 0;

	        try {

	            conn = getConnection(url, driver, username, password);
	            stmt = conn.createStatement();
	            rset = stmt.executeQuery(sql);
	            while (rset.next()) {
	            	ipcCode = rset.getString("ipccode");
	                description = rset.getString("description");
	                ipcDescription.put(ipcCode, description);
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new Exception(e);
	        } finally {
	            if (rset != null) {
	                try {
	                    rset.close();
	                } catch (Exception se) {
	                    se.printStackTrace();
	                }
	            }

	            if (stmt != null) {
	                try {
	                    stmt.close();
	                } catch (Exception se) {
	                    se.printStackTrace();
	                }
	            }

	            if (conn != null) {
	                try {
	                    conn.close();
	                } catch (Exception e1) {
	                     e1.printStackTrace();
	                }
	            }
	        }

	        return ipcDescription;
    }

    public List getIPCClassName(String[] lstCodes) throws Exception {

        List names = new ArrayList();
        String[] arrNames = null;

        try {
            for (int i = 0; i < lstCodes.length; i++) {
                String code = lstCodes[i];

                if (perl.match("/SLASH/i", code))
                    code = perl.substitute("s/SLASH/\\//ig", code);

                if (perl.match("/PERIOD/i", code))
                    code = perl.substitute("s/PERIOD/\\./ig", code);

                code = IPCClassNormalizer.normalize(code);

                String name = seekIPC(code);

                if (name != null){
                    name = perl.substitute("s/\\(\\:\\)/QQ/ig", name);
                    String key = perl.substitute("s/[^a-zA-Z]//g", name);
                    if(name.indexOf("QQ") > 1){
                        String[] temp = name.split("QQ");
                        String[] tempKey = key.split("QQ");
                        hashtable.put(tempKey[tempKey.length-1].toLowerCase(), temp[temp.length-1].toLowerCase());
                    }
                }
            }
            if(!hashtable.isEmpty()){
                Enumeration e = hashtable.elements();
                 while( e.hasMoreElements() ){
                     names.add(e.nextElement());
                 }
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }

        return names;

    }
    public String[] getReferences(Connection conn, String mid) {

        PreparedStatement stmt = null;
        ResultSet rset = null;

        List ids = new ArrayList();

        String[] sIds = null;

        try {

            stmt = conn.prepareStatement("select cit_pn,cit_cy,cit_mid,cit_pk from patent_refs where prt_mid = ?");
            stmt.setString(1, mid);

            rset = stmt.executeQuery();

            while (rset.next()) {

                StringBuffer fullPn = new StringBuffer();

                String pn = rset.getString("cit_pn");
                String cit_cy = rset.getString("cit_cy");

                if (pn != null && cit_cy != null && !pn.equalsIgnoreCase("QQ")) {
                    fullPn.append(cit_cy).append(pn);
                    if(cit_cy.equalsIgnoreCase("ep")&&rset.getString("cit_pk")!=null)
                        fullPn.append(rset.getString("cit_pk"));
                    String pNum = fullPn.toString();
                    ids.add(pNum);
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            close(rset);
            close(stmt);
        }

        sIds = (String[]) ids.toArray(new String[1]);
        sIds[0] = replaceNull(sIds[0]);

        return sIds;

    }
    public void close(ResultSet rset) {

        if (rset != null) {
            try {
                rset.close();
                rset = null;
            }
            catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void close(Statement stmt) {

        if (stmt != null) {
            try {
                stmt.close();
                stmt = null;
            }
            catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void close(Connection con) {

        if (con != null) {
            try {
                con.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    public boolean isApplication(String kindCode) {

        boolean isApplication = false;

        if (kindCode == null)
            return isApplication;

        if (kindCode.equalsIgnoreCase("A1") || kindCode.equalsIgnoreCase("A2") || kindCode.equalsIgnoreCase("A3") || kindCode.equalsIgnoreCase("A4") || kindCode.equalsIgnoreCase("A8") || kindCode.equalsIgnoreCase("A9") || kindCode.equalsIgnoreCase("P1")) {
            isApplication = true;
        }

        return isApplication;

    }
    public String[] removeDupDS(String vals[]) {

        List lstVals = new ArrayList();

        for (int i = 0; i < vals.length; i++) {
            String sVal = vals[i];

            if (!lstVals.contains(sVal))
                lstVals.add(sVal);
        }

        String[] sVals = (String[]) lstVals.toArray(new String[1]);

        return sVals;

    }
    public String[] formatPN(String pn, String kindCode, String authCode) {

        List lstVals = new ArrayList();

        StringBuffer sbPnum = new StringBuffer();

        if (kindCode == null)
            kindCode = "";

        if (authCode == null)
            authCode = "";

        lstVals.add(pn);

        String newPnum = trimZeros(pn);

        lstVals.add(newPnum);

        sbPnum.append(authCode).append(pn);

        lstVals.add(sbPnum.substring(0, sbPnum.length()));

        sbPnum.setLength(0);

        sbPnum.append(authCode).append(newPnum);

        lstVals.add(sbPnum.substring(0, sbPnum.length()));

        sbPnum.setLength(0);

        sbPnum.append(authCode).append(pn).append(kindCode);

        lstVals.add(sbPnum.substring(0, sbPnum.length()));

        sbPnum.setLength(0);

        sbPnum.append(authCode).append(newPnum).append(kindCode);

        lstVals.add(sbPnum.substring(0, sbPnum.length()));

        String[] sVals = (String[]) lstVals.toArray(new String[1]);

        return sVals;
    }

    public String[] formatPN(String pn) {

        List lstVals = new ArrayList();

        StringBuffer sbPnum = new StringBuffer();

        lstVals.add(pn);

        String newPnum = trimZeros(pn);

        lstVals.add(newPnum);

        String[] sVals = (String[]) lstVals.toArray(new String[1]);

        return sVals;
    }

    public List formatPN(String pn, String country) {

        if (country == null)
            country = "";

        if (pn == null)
            pn = "";

        List lstVals = new ArrayList();
        if((country.indexOf(AUDELIMITER) > 1) || (pn.indexOf(AUDELIMITER) > 1))
        {
            String[] valuesCountry = country.split(AUDELIMITER);
            String[] valuesPn = pn.split(AUDELIMITER);
            for(int x = 0 ; x < valuesPn.length; x++)
            {
                lstVals.add(valuesPn[x]);
                String newPnum = trimZeros(valuesPn[x]);
                lstVals.add(newPnum);
                String resultCountry = null;
                if(valuesCountry.length ==  valuesPn.length){
                    resultCountry = valuesCountry[x];
                }
                else{
                    resultCountry = valuesCountry[0];
                }
                if(resultCountry== null)
                    resultCountry="";

                StringBuffer sbPnum = new StringBuffer();
                sbPnum.append(resultCountry).append(valuesPn[x]);
                lstVals.add(sbPnum.substring(0, sbPnum.length()));
                sbPnum.setLength(0);
                sbPnum.append(resultCountry).append(newPnum);
                lstVals.add(sbPnum.substring(0, sbPnum.length()));

            }
        }
        else
        {
            StringBuffer sbPnum = new StringBuffer();
            lstVals.add(pn);
            String newPnum = trimZeros(pn);
            lstVals.add(newPnum);
            sbPnum.append(country).append(pn);
            lstVals.add(sbPnum.substring(0, sbPnum.length()));
            sbPnum.setLength(0);
            sbPnum.append(country).append(newPnum);
            lstVals.add(sbPnum.substring(0, sbPnum.length()));
        }

        return lstVals;
    }

    public String trimZeros(String sVal) {

        if (sVal == null) {
            return sVal;
        }
        else {
            return sVal.replaceFirst("^(D|PP|RE|T|H|X|RX|AI)?[0]+","$1");
        }

       /*
        char[] schars = sVal.toCharArray();
        int index = 0;
        for (; index < sVal.length(); index++) {

            if (schars[index] != '0') {
                break;
            }
        }

        return (index == 0) ? sVal : sVal.substring(index);
        */
    }
    public String[] removeSpaces(String[] arrCodes) {

        for (int i = 0; i < arrCodes.length; i++) {
            String code = arrCodes[i];
            code = perl.substitute("s/\\s+//", code);

            if (perl.match("/\\/\\//", code))
                code = perl.substitute("s/\\/\\//\\//", code);

            if (perl.match("/\\./", code))
                code = perl.substitute("s/\\./PERIOD/ig", code);

            if (perl.match("/\\//", code))
                code = perl.substitute("s/\\//SLASH/ig", code);

            arrCodes[i] = code;
        }

        return arrCodes;
    }
    public String formatDate(String s) {

        if (s == null)
            return "";
        else if (s.trim().equals("") || s.length() != 8)
            return "";

        StringBuffer sDate = new StringBuffer();

        String year = s.substring(0, 4);
        String month = s.substring(4, 6);
        String day = s.substring(6, 8);

        sDate.append(month).append("/").append(day).append("/").append(year);

        return sDate.toString();

    }
    public String[] getCountry(String[] countries) {

        List lstCtry = new ArrayList();
        Hashtable hashCtry = new Hashtable();

        for (int i = 0; i < countries.length; i++) {
            hashCtry.put(countries[i],"");
        }

        if(!hashCtry.isEmpty()){
            Enumeration e = hashCtry.keys();
             while( e.hasMoreElements() ){
                 String country = Country.formatCountry(e.nextElement().toString());
                 if (country != null)
                        lstCtry.add(country);
             }
        }

        String[] arrVals = (String[]) lstCtry.toArray(new String[1]);

        return arrVals;
    }
    public String[] formatDocType(String dt,String kc) throws Exception {

        List values = new ArrayList();

        values.add(dt);
        values.add("PA");
        if (dt.equalsIgnoreCase("ug")||dt.equalsIgnoreCase("ua"))
        {
            switch (kc.toUpperCase().charAt(0))
            {
              case  'S': values.add("DP"); break;
              case  'P': values.add("PP"); break;
              case  'E': values.add("RE"); break;
              case  'H': values.add("SR"); break;
              //default: values.add("UT"); break;
            }
        }


        String[] arrVals = (String[]) values.toArray(new String[1]);

        return arrVals;
    }
    public String formatAuthor(String s) {

        if (s == null)
            return "";

        s = perl.substitute("s/;/,/g", s);

        return s;
    }
    public List normalizeUSCodes(String sList) {

        if (sList == null)
            return new ArrayList();

        List lstVals = new ArrayList();
        List newVals = new ArrayList();

        perl.split(lstVals, "/" + DELIM + "/", sList);

        for (int i = 0; i < lstVals.size(); i++) {
            String code = (String) lstVals.get(i);
            code = USPTOClassNormalizer.normalize(code);
            newVals.add(code);

        }

        return newVals;
    }
    public List normalizeUSClass(String sList) {

        if (sList == null)
            return new ArrayList();

        List lstVals = new ArrayList();
        List newVals = new ArrayList();

        perl.split(lstVals, "/" + DELIM + "/", sList);

        for (int i = 0; i < lstVals.size(); i++) {
            String code = (String) lstVals.get(i);
            code = USPTOClassNormalizer.normalizeClass(code);
            newVals.add(code);

        }

        return newVals;
    }
    public List normalizeUSSubClass(String sList) {

        if (sList == null)
            return new ArrayList();

        List lstVals = new ArrayList();
        List newVals = new ArrayList();

        perl.split(lstVals, "/" + DELIM + "/", sList);

        for (int i = 0; i < lstVals.size(); i++) {
            String code = (String) lstVals.get(i);
            code = USPTOClassNormalizer.normalizeSubClass(code);
            newVals.add(code);

        }

        return newVals;
    }
    public List convertString2List(String sList) {

        if (sList == null)
            return new ArrayList();

        List lstVals = new ArrayList();

        perl.split(lstVals, "/" + DELIM + "/", sList);

        return lstVals;
    }
    public String[] convert2Array(String sVal) {

    	String[] arrVals = null;
    	if(sVal!=null)
    	{
	        List values = new ArrayList();
	
	        perl.split(values, "/" + DELIM + "/", sVal);
	
	        arrVals = (String[]) values.toArray(new String[1]);
	
	        arrVals[0] = replaceNull(arrVals[0]);
    	}
    	else
    	{
    		arrVals = new String[1];
    		arrVals[0] = "";
    	}

        return arrVals;
    }
    public String[] formatAddrLoc(String invAddr, String invCtry) {

        if (invAddr == null)
            invAddr = "";

        List values = new ArrayList();

        perl.split(values, "/" + DELIM + "/", invAddr);

        StringBuffer addrBuff = new StringBuffer();

        for (Iterator iter = values.iterator(); iter.hasNext();) {
            String addr = (String) iter.next();
            addr = Entity.prepareString(addr);
            addrBuff.append(addr).append(" ");
        }

        values.clear();

        perl.split(values, "/" + DELIM + "/", invCtry);

        String[] ctyVals = (String[]) values.toArray(new String[1]);

        ctyVals[0] = replaceNull(ctyVals[0]);

        String[] countries = getCountry(ctyVals);

        values.clear();

        for (int i = 0; i < countries.length; i++) {

            values.add(countries[i]);
        }

        values.add(addrBuff.toString());

        String[] allAddrs = (String[]) values.toArray(new String[1]);

        return allAddrs;

    }

    public String[] getPriorityNumber(String priInfo) {

        ArrayList pFields = new ArrayList();

        if (priInfo != null && !priInfo.trim().equals("")) {

            ArrayList arrPriInfo = new ArrayList();
            perl.split(arrPriInfo, "/" + DELIM + "/", priInfo);

            for (Iterator iter = arrPriInfo.iterator(); iter.hasNext();) {
                ArrayList arrPriRecs = new ArrayList();
                String priRec = (String) iter.next();
                perl.split(arrPriRecs, "/" + NESTED_DELIM + "/", priRec);

                if (arrPriRecs.size() >= 1)
                    pFields.add((String) arrPriRecs.get(0));

                if (arrPriRecs.size() >= 2)
                    pFields.add((String) arrPriRecs.get(1));

                if (arrPriRecs.size() >= 3)
                    pFields.add(formatDate((String) arrPriRecs.get(2)));

                if (arrPriRecs.size() == 4)
                    pFields.add((String) arrPriRecs.get(3));
            }
        }

        String[] arrVals = (String[]) pFields.toArray(new String[1]);

        arrVals[0] = replaceNull(arrVals[0]);

        return arrVals;

    }
    public String[] getPriorityCountry(String priInfo) {

        ArrayList arrPriCtry = new ArrayList();

        if (priInfo != null && !priInfo.equals("")) {

            ArrayList arrPriInfo = new ArrayList();
            perl.split(arrPriInfo, "/" + DELIM + "/", priInfo);

            for (Iterator iter = arrPriInfo.iterator(); iter.hasNext();) {
                ArrayList arrPriRecs = new ArrayList();
                String priRec = (String) iter.next();
                perl.split(arrPriRecs, "/" + NESTED_DELIM + "/", priRec);

                if (arrPriRecs.size() >= 1)
                    arrPriCtry.add(arrPriRecs.get(0));
            }
        }
        String[] arrVals = (String[]) arrPriCtry.toArray(new String[1]);

        arrVals[0] = replaceNull(arrVals[0]);

        return arrVals;
    }
    public String[] getPriorityDate(String priInfo) {

        ArrayList arrPriDt = new ArrayList();

        if (priInfo != null && !priInfo.equals("")) {

            ArrayList arrPriInfo = new ArrayList();
            perl.split(arrPriInfo, "/" + DELIM + "/", priInfo);

            for (Iterator iter = arrPriInfo.iterator(); iter.hasNext();) {
                ArrayList arrPriRecs = new ArrayList();
                String priRec = (String) iter.next();
                perl.split(arrPriRecs, "/" + NESTED_DELIM + "/", priRec);

                if (arrPriRecs.size() >= 3)
                    arrPriDt.add(formatDate((String) arrPriRecs.get(2)));
            }
        }

        String[] arrVals = (String[]) arrPriDt.toArray(new String[1]);

        arrVals[0] = replaceNull(arrVals[0]);

        return arrVals;
    }
    public String[] getPriorityKind(String priInfo) {

        ArrayList arrPriKinds = new ArrayList();

        if (priInfo != null && !priInfo.equals("")) {

            ArrayList arrPriInfo = new ArrayList();
            perl.split(arrPriInfo, "/" + DELIM + "/", priInfo);

            for (Iterator iter = arrPriInfo.iterator(); iter.hasNext();) {
                ArrayList arrPriRecs = new ArrayList();
                String priRec = (String) iter.next();
                perl.split(arrPriRecs, "/" + NESTED_DELIM + "/", priRec);

                if (arrPriRecs.size() == 4)
                    arrPriKinds.add(arrPriRecs.get(3));
            }
        }

        String[] arrVals = (String[]) arrPriKinds.toArray(new String[1]);

        arrVals[0] = replaceNull(arrVals[0]);

        return arrVals;
    }

    private String getDedupKey() throws Exception {

        return (new GUID()).toString();

    }
    private String getStringFromClob(Clob clob) throws Exception {
        String temp = "";
        if (clob != null) {
            temp = clob.getSubString(1, (int) clob.length());
        }

        if (temp.equalsIgnoreCase("QQ"))
            temp = "";
        return temp;
    }
    public String replaceNull(String sVal) {

        if (sVal == null)
            sVal = "";

        return sVal;
    }
    private boolean validYear(String year) {
        if (year == null) {
            return false;
        }

        if (year.length() != 4) {
            return false;
        }

        return perl.match("/[1-9][0-9][0-9][0-9]/", year);
    }

    public static void main(String args[]) throws Exception {
        String url = args[0];
        String driver = args[1];
        String username = args[2];
        String password = args[3];

        int loadNumber = Integer.parseInt(args[4]);
        long timestamp=0;
        int recsPerbatch = Integer.parseInt(args[5]);
        String operation = args[6];
        Combiner.TABLENAME = args[7];
        String environment = args[8].toLowerCase();
        if(args.length==10)
         timestamp = Long.parseLong(args[9]);

        System.out.println("Table Name=" + args[7]);
        System.out.println("LoadNumber=" + loadNumber);
        System.out.println("RecsPerFile=" + recsPerbatch);
        System.out.println("Exit At=" + Combiner.EXITNUMBER);
        String dbname = "upt";
        if (timestamp > 0)
            dbname=dbname+"cit";

        CombinedWriter writer = new CombinedXMLWriter(recsPerbatch, loadNumber, dbname);
        writer.setOperation(operation);
        UPTCombiner c = new UPTCombiner(writer);
        c.url=url;
        c.driver=driver;
        c.username=username;
        c.password=password;
        try {
        	c.ipc = c.getAllDescriptionFromLookupIndex();
            if (timestamp==0 && (loadNumber > 3000 || loadNumber < 1000) && loadNumber>1)
            {
                System.out.println("Processing loadnumber " + loadNumber + "...");
                c.writeCombinedByWeekNumber(url, driver, username, password, loadNumber);
            }
            else if(timestamp > 0)
            {
                System.out.println("Processing timestamp " + timestamp + "...");
                c.writeCombinedByTimestamp(url, driver, username, password, timestamp);
            }
            else if(loadNumber==1)
            {
                System.out.println("Processing timestamp " + timestamp + "...");
                c.writeCombinedByTable(url, driver, username, password);
            }
            else if(loadNumber == 0 && timestamp < 0)
            {
                //extract all by year
                for(int yearIndex = 1998; yearIndex <= 2016; yearIndex++)
                {

                    System.out.println("Processing year " + yearIndex + "...");
                      // create  a new writer so we can see the loadNumber/yearNumber in the filename
                      c = new UPTCombiner(new CombinedXMLWriter(recsPerbatch, yearIndex,UPTDatabase.getIndexName(), environment));
                      c.writeCombinedByYear(url,
                                          driver,
                                          username,
                                          password,
                                          yearIndex);
                }
            }
            else
            {
                System.out.println("Processing combined year " + loadNumber + "...");
                c.writeCombinedByYear(url, driver, username, password, loadNumber);
            }




        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally
        {
            System.exit(1);
        }
        System.exit(1);
    }
}
