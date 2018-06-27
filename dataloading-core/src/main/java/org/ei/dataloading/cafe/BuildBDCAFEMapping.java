package org.ei.dataloading.cafe;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.net.*;
import java.util.regex.*;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.dataloading.inspec.loadtime.*;
import org.ei.domain.*;
import org.ei.query.base.*;
import org.ei.dataloading.bd.loadtime.XmlCombiner;
import org.ei.dataloading.CombinedXMLWriter;
import org.ei.common.bd.*;

public class BuildBDCAFEMapping
{
	public static long startTime = System.currentTimeMillis();
	public static long endTime = System.currentTimeMillis();
	public static String tableName1 = "bd_master";
	public static String tableName2 = "cafe_master";
	public static String tableName3 = "HMO_BD_CAFE_MAPPING";
    public static String URL="jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
	public static String driver="oracle.jdbc.driver.OracleDriver";
	public static String username="ap_ev_search";
	public static String password="ei3it";
	public static String database="bd";
	public static String loadnumber;
	public static String action;
	
	public static void main(String args[]) throws Exception
	{
		BuildBDCAFEMapping build = new BuildBDCAFEMapping();
		

	    if(args.length>8)
		{
	    	build.URL = args[0];
			System.out.println("URL:: "+build.URL);
			build.username = args[1];
			System.out.println("USERNAME:: "+build.username);
			build.password = args[2];
			System.out.println("PASSWORD:: "+build.password);			
			build.tableName1 = args[3];
			System.out.println("TABLENAME1:: "+build.tableName1);
			build.tableName2=args[4];
			System.out.println("TABLENAME2:: "+build.tableName2);
			build.tableName3 = args[5];
			System.out.println("TABLENAME3:: "+build.tableName3);
			build.loadnumber = args[6];
			System.out.println("LOADNUMBER:: "+build.loadnumber);
			build.database = args[7];
			System.out.println("DATABASE:: "+build.database);
			build.action = args[8];
			System.out.println("DATABASE:: "+build.action);

		}	   
		else if(args.length>2)
		{				
			build.loadnumber = args[0];
			System.out.println("LoadNumber:: "+build.loadnumber);
			build.database = args[1];
			System.out.println("DATABASE:: "+build.database);
			build.action = args[2];
			System.out.println("DATABASE:: "+build.action);
			
		}		
		else
		{
			System.out.println("not enough parameters");
		}	  
	    
	    if(action!=null && action.equals("delete"))
	    {
	    	build.deleteMapping(loadnumber);
	    }
	    else if(action!=null && action.equals("update"))
	    {
	    	build.buildMapping(loadnumber);
	    }
	    else
	    {
	    	System.out.println("please enter action parameter");
	    }
	    endTime = System.currentTimeMillis();
		System.out.println("total Time used "+(endTime-startTime)/1000.0+" seconds");
	}
	
	public void deleteMapping(String loadnumber) throws Exception
	{
		Statement stmt = null;
		Connection con = null;
		String sqlQuery = null;	
		
		try
		{
			if(this.database.equals("bd"))
			{
				sqlQuery = "delete from "+tableName3+" where bd_pui in (select pui from "+tableName1+" where database='cpx' and loadnumber="+loadnumber+")";
			}
			else if(this.database.equals("cafe"))
			{
				sqlQuery = "delete from "+tableName3+" where cafe_pui in (select pui from "+tableName1+" where database='cpx' and loadnumber="+loadnumber+")";
			}
			System.out.println("Running delete Query "+sqlQuery);
			con = getConnection(URL,driver,username,password);
			con.setAutoCommit(true);
			stmt = con.createStatement();
			stmt.executeQuery(sqlQuery);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{

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
		}
	    
	}
		
	
	public void buildMapping(String loadnumber) throws Exception
	{
		Statement stmt = null;
		Statement addStmt = null;
		Statement checkStmt = null;
		Statement deleteStmt = null;
		ResultSet rs = null;
		ResultSet checkRs = null;
		Connection con = null;
		String sqlQuery = null;
		
		String mid = null;
		String accessnumber = null;
		String doi = null;
		String pui = null;
		String issn = null;
		String coden = null;
		String volume = null;
		String issue = null;
		String page = null;
		String articlenumber = null;
		//String loadnumber = null;
		String updatenumber = null;
		String pii = null;
		String citationtitle = null;
		String dedupkey = null;
		
		try
		{
			con = getConnection(URL,driver,username,password);
			con.setAutoCommit(true);
			stmt = con.createStatement();
			addStmt = con.createStatement();
			checkStmt = con.createStatement();
			deleteStmt = con.createStatement();
			Set puiSet= getPUI(con,this.database);
			sqlQuery = "select M_id,accessnumber,doi,pui,issn,coden,volume,issue,page,articlenumber,loadnumber,updatenumber,pii,citationtitle from "+tableName1+" where database='cpx' and loadnumber="+loadnumber;
	        rs = stmt.executeQuery(sqlQuery);
			int i=0;
			while (rs.next())
			{
				
				try
				{
					mid = rs.getString("M_ID");
					accessnumber = rs.getString("ACCESSNUMBER");
					doi = rs.getString("DOI");
					if(doi==null)
					{
						doi="";
					}
					pui = rs.getString("PUI");
					issn = rs.getString("ISSN");
					if(issn==null)
					{
						issn="";
					}
					coden = rs.getString("CODEN");
					if(coden==null)
					{
						coden="";
					}
					volume = rs.getString("VOLUME");
					if(volume==null)
					{
						volume="";
					}
					issue = rs.getString("ISSUE");
					if(issue==null)
					{
						issue="";
					}
					page = rs.getString("PAGE");
					if(page==null)
					{
						page="";
					}
					articlenumber = rs.getString("ARTICLENUMBER");
					if(articlenumber==null)
					{
						articlenumber="";
					}
					loadnumber = rs.getString("LOADNUMBER");
					updatenumber = rs.getString("UPDATENUMBER");
					pii = rs.getString("PII");
					citationtitle = rs.getString("CITATIONTITLE");	
					if(!puiSet.contains(pui+accessnumber))
					{
						if(checkPui(deleteStmt,pui)!=null)
						{
							if(database.equals("bd"))
							{
								deleteStmt.executeQuery("delete from "+tableName3+" where bd_pui='"+pui+"'");
							}
							else if(database.equals("cafe"))
							{
								deleteStmt.executeQuery("delete from "+tableName3+" where cafe_pui='"+pui+"'");
							}
							System.out.println("delete "+database+"_pui "+pui);
						}
						dedupkey = getDedupKey(issn,coden,volume,issue,getPage(page,articlenumber));  
						String[] mapping = checkCafe(checkStmt,accessnumber,doi,pui,pii,citationtitle,dedupkey);
						if(mapping!=null && mapping.length==5)
						{						
							String insertQuery = "insert into "+tableName3+"(BD_ACCESSNUMBER,CAFE_ACCESSNUMBER,BD_PUI,CAFE_PUI,MATCHED_CRITERION,LOADNUMBER)"
									+ "values('"+mapping[0]+"','"+mapping[1]+"','"+mapping[2]+"','"+mapping[3]+"','"+mapping[4]+"',"+Integer.parseInt(loadnumber)+")";
		 
							//System.out.println(insertQuery);
							addStmt.addBatch(insertQuery);
							i++;
							if(i>100)
							{
								addStmt.executeBatch();
								System.out.println("commit point");
								i=0;
							}  
						}
					
					
					}
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
	            
	        }
			addStmt.executeBatch();
	        
		}
		catch (Exception e)
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
			if (addStmt != null) {
				try {
					addStmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (checkStmt != null) {
				try {
					checkStmt.close();
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
		}
	    
	}
	
	private String checkPui(Statement stmt,String pui)
	{
		ResultSet rs = null;		
		String result = null;
		try
		{			
			String sqlQuery = "select bd_pui from "+tableName3+" where bd_pui='"+pui+"'";
			rs = stmt.executeQuery(sqlQuery);
			while (rs.next())
			{
				result=rs.getString("BD_pui");
			}
		}
		catch (Exception e)
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
		}
		return result;
	}
	
	private String[] checkCafe(Statement stmt,String accessnumber,String doi,String pui,String pii,String citationtitle,String dedupkey)
	{
		
		ResultSet rs = null;		
		String[] result = null;
		try
		{			
			String sqlQuery = "select M_id,accessnumber,doi,pui,issn,coden,volume,issue,page,articlenumber,loadnumber,updatenumber,pii,citationtitle from "+tableName2+" where database='cpx' and pui='"+pui+"'";
			String sqlQuery1 = "select M_id,accessnumber,doi,pui,issn,coden,volume,issue,page,articlenumber,loadnumber,updatenumber,pii,citationtitle from "+tableName2+" where database='cpx' and accessnumber='"+accessnumber+"'";
			String sqlQuery2 = "select M_id,accessnumber,doi,pui,issn,coden,volume,issue,page,articlenumber,loadnumber,updatenumber,pii,citationtitle from "+tableName2+" where database='cpx' and doi='"+doi+"'";
			String sqlQuery3 = "select M_id,accessnumber,doi,pui,issn,coden,volume,issue,page,articlenumber,loadnumber,updatenumber,pii,citationtitle from "+tableName2+" where database='cpx' and pii='"+pii+"'";
			if(citationtitle !=null && citationtitle.indexOf("'")>-1)
			{
				citationtitle = citationtitle.replaceAll("'","''''");
			}
							
			String sqlQuery4 = "select M_id,accessnumber,doi,pui,issn,coden,volume,issue,page,articlenumber,loadnumber,updatenumber,pii,citationtitle from "+tableName2+" where database='cpx' and citationtitle='"+citationtitle+"'";
			rs = stmt.executeQuery(sqlQuery);			
			result=checkResult(rs,"pui",accessnumber,doi,pui,pii,citationtitle,dedupkey);
			if(result==null)
			{
				rs = stmt.executeQuery(sqlQuery1);			
				result=checkResult(rs,"accessnumber",accessnumber,doi,pui,pii,citationtitle,dedupkey);
				if(result==null)
				{
					rs = stmt.executeQuery(sqlQuery2);			
					result=checkResult(rs,"doi",accessnumber,doi,pui,pii,citationtitle,dedupkey);
					if(result==null)
					{
						rs = stmt.executeQuery(sqlQuery3);			
						result=checkResult(rs,"pii",accessnumber,doi,pui,pii,citationtitle,dedupkey);
						if(result==null)
						{
							//System.out.println("running Query 4"+sqlQuery4);
							rs = stmt.executeQuery(sqlQuery4);			
							result=checkResult(rs,"citationtitle",accessnumber,doi,pui,pii,citationtitle,dedupkey);
						}
					}
				}
			}
		}
		catch (Exception e)
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
		}
		return result;
	}
			
	private String[] checkResult(ResultSet rs,String checkParameter,String accessnumber,String doi,String pui,String pii,String citationtitle,String dedupkey)
	{
		String cafe_mid = null;
		String cafe_accessnumber = null;
		String cafe_doi = null;
		String cafe_pui = null;
		String cafe_issn = null;
		String cafe_coden = null;
		String cafe_volume = null;
		String cafe_issue = null;
		String cafe_page = null;
		String cafe_articlenumber = null;
		//String loadnumber = null;
		String cafe_updatenumber = null;
		String cafe_pii = null;
		String cafe_citationtitle = null;
		String cafe_dedupkey = null;
		boolean mappingFlag = false;
		String mappingMethod = null;
		String[] result = null;
		
		try
		{
			while (rs.next())
			{			
				cafe_mid = rs.getString("M_ID");
				cafe_accessnumber = rs.getString("ACCESSNUMBER");
				cafe_doi = rs.getString("DOI");
				if(cafe_doi==null)
				{
					cafe_doi="";
				}
				cafe_pui = rs.getString("PUI");
				cafe_issn = rs.getString("ISSN");
				if(cafe_issn==null)
				{
					cafe_issn="";
				}
				cafe_coden = rs.getString("CODEN");
				if(cafe_coden==null)
				{
					cafe_coden="";
				}
				cafe_volume = rs.getString("VOLUME");
				if(cafe_volume==null)
				{
					cafe_volume="";
				}
				cafe_issue = rs.getString("ISSUE");
				if(cafe_issue==null)
				{
					cafe_issue="";
				}
				cafe_page = rs.getString("PAGE");
				if(cafe_page==null)
				{
					cafe_page="";
				}
				cafe_articlenumber = rs.getString("ARTICLENUMBER");
				if(cafe_articlenumber==null)
				{
					cafe_articlenumber="";
				}
				
				cafe_pii = rs.getString("PII");
				cafe_citationtitle = rs.getString("CITATIONTITLE");	
				if(cafe_citationtitle!=null && cafe_citationtitle.indexOf("'")>-1)
				{
					cafe_citationtitle=cafe_citationtitle.replaceAll("'", "''''");
				}
				cafe_dedupkey = getDedupKey(cafe_issn,cafe_coden,cafe_volume,cafe_issue,getPage(cafe_page,cafe_articlenumber));  
				if(!checkParameter.equals("pui") && (cafe_pui.equals(pui)))
				{
					mappingMethod=checkParameter+",pui";
				}
				else if(!checkParameter.equals("accessnumber") && (cafe_accessnumber.equals(accessnumber)))
				{
					mappingMethod=checkParameter+",accessnumber";
				}
				else if	(!checkParameter.equals("doi") && cafe_doi!=null && doi!=null && cafe_doi.equals(doi))
				{
					mappingMethod=checkParameter+",doi";
				}
				else if(!checkParameter.equals("pii") && cafe_pii != null && pii != null && cafe_pii.equals(pii))
				{
					mappingMethod=checkParameter+",pii";
				}
				else if(!checkParameter.equals("citationtitle") && cafe_citationtitle!= null && citationtitle!= null && cafe_citationtitle.equals(citationtitle))
				{
					mappingMethod=checkParameter+",citationtitle";
				}
				else if(!checkParameter.equals("dedupkey") && cafe_dedupkey!=null && dedupkey!=null && cafe_dedupkey.equals(dedupkey))
				{
					mappingMethod=checkParameter+",dedupkey";
				}
				if(mappingMethod!=null)	
				{
					result = new String[5];
					result[0]=accessnumber;
					result[1]=cafe_accessnumber;
					result[2]=pui;
					result[3]=cafe_pui;
					result[4]=mappingMethod;
					break;
				}
	
			}
		}
		catch(Exception e)
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
			
		}
		
		return result;
		
	}
	
	 public String getPage(String xp,
             String ar)
	{
		String strPage = null;
		
		if(ar != null)
		{
			strPage = ar;
		}
		else
		{
			strPage = xp;
		}
		
		return strPage;
	}
	
	public String getDedupKey(String issn,
            String coden,
            String volume,
            String issue,
            String page)
	throws Exception
	{
		Perl5Util perl = new Perl5Util();
		String firstVolume = getFirstNumber(volume);
		String firstIssue = getFirstNumber(issue);
		String firstPage = getFirstPage(page);
		
		if ((issn == null && coden == null) ||
			firstVolume == null ||
			firstIssue == null ||
			firstPage == null)
		{
			return null;
		}
		
		StringBuffer buf = new StringBuffer();
		
		if (issn != null)
		{
			buf.append(perl.substitute("s/-//g", issn));
		}
		else
		{
			buf.append(BdCoden.convert(coden));
		}
		
		buf.append("vol" + firstVolume);
		buf.append("is" + firstIssue);
		buf.append("pa" + firstPage);
		
		return buf.toString().toLowerCase();
	
	}
	
	private String getFirstNumber(String v)
    {
		Perl5Util perl = new Perl5Util();
        MatchResult mResult = null;
        if (v == null)
        {
            return null;
        }

        if (perl.match("/[1-9][0-9]*/", v))
        {
            mResult = perl.getMatch();
        }
        else
        {
            return null;
        }

        return mResult.toString();
    }
	
	private String getFirstPage(String v)
    {
        BdPage pages = new BdPage(v);
        return pages.getStartPage();
    }
	
	public Set getPUI(Connection con,String database) throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		String pui = null;
		String an = null;
		Set puiSet = new HashSet();
		
		try
		{
			
			stmt = con.createStatement();
			if(this.database.equals("bd"))
			{
				sqlQuery = "select BD_PUI PUI,BD_ACCESSNUMBER AN from "+tableName3;
			}
			else if(this.database.equals("cafe"))
			{
				sqlQuery = "select CAFE_PUI PUI,CAFE_ACCESSNUMBER AN from "+tableName3;
			}
	        rs = stmt.executeQuery(sqlQuery);			
			while (rs.next())
			{
				pui = rs.getString("PUI");
				an = rs.getString("AN");
				puiSet.add(pui+an);
			}
					
		}
		catch (Exception e)
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
		}
		return puiSet;
	    
	}

	protected Connection getConnection(String connectionURL,
		                               String driver,
		                               String username,
		                               String password)
		           						   throws Exception
		{
			System.out.println("URL==> "+connectionURL);
			System.out.println("DRIVER==> "+driver);
			System.out.println("USERNAME==> "+username);
			System.out.println("PASSWORD==> "+password);
			Class.forName(driver);
		   	Connection con = DriverManager.getConnection(connectionURL,
		                                              username,
		                                              password);
		    return con;
    }

}