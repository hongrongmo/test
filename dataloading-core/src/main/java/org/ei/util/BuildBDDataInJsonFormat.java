package org.ei.util;

import java.io.FileWriter;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ei.common.bd.*;
import org.ei.common.Constants;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;


public class BuildBDDataInJsonFormat
{
	public static String URL="jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
	public static String driver="oracle.jdbc.driver.OracleDriver";
	public static String username="ap_correction1";
	public static String password="ei3it";
    public static String tableName="bd_master";
    public static String database="cpx";
    public static int loadnumber=0;
    public static String action="bd_loading";
    Perl5Util perl = new Perl5Util();
    
	public static void main(String args[]) throws Exception
	{
		BuildBDDataInJsonFormat bjson = new BuildBDDataInJsonFormat();
		long startTime = System.currentTimeMillis();
	    long endTime = System.currentTimeMillis();
		if(args.length>5)
		{
			bjson.database = args[0];
			System.out.println("DATABASE:: "+bjson.database);
			bjson.username = args[1];
			System.out.println("USERNAME:: "+bjson.username);
			bjson.password = args[2];
			System.out.println("PASSWORD:: "+bjson.password);			
			bjson.tableName = args[3];
			System.out.println("TABLENAME:: "+bjson.tableName);
			bjson.URL=args[4];
			System.out.println("URL:: "+bjson.URL);
			bjson.loadnumber = Integer.parseInt(args[5]);
			System.out.println("LOADNUMBER:: "+bjson.loadnumber);			
		}
		else
		{
			bjson.loadnumber = Integer.parseInt(args[0]);
		}
		bjson.getDataFromDatabase(bjson.loadnumber,database);
	}
	
	private void getDataFromDatabase(int loadnumber,String dbname) throws Exception
    {      
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        FileWriter out = null;
        String outfileName = dbname+"_"+loadnumber+".json";
        HashMap<String,String> dataMap = null;
        JsonObjectBuilder evo = null;
        JsonArrayBuilder builder = null;
        
        try
        {
        	out = new FileWriter(outfileName); 
        	con = getConnection(this.URL,this.driver,this.username,this.password);
            stmt = con.createStatement();
            System.out.println("loadnumber= "+loadnumber+" dbname= "+dbname);
            System.out.println("Running the query...");
            String sqlQuery = null;
            
            if(dbname.equalsIgnoreCase("cpx"))
            {
	        	if(loadnumber!=1)
	        	{
	        		sqlQuery="select DOI,PII,PUI,ISSN,EISSN,ISBN,CITTYPE,ARTICLENUMBER,PAGE,PAGECOUNT,VOLUME,ISSUE,PUBLICATIONYEAR,CITATIONTITLE,ABSTRACTDATA,AUTHOR,AUTHOR_1,DATABASE FROM BD_MASTER WHERE LOADNUMBER="+loadnumber+" and DATABASE='"+dbname+"'";
	        	}
	        	else
	        	{
	        		sqlQuery="select DOI,PII,PUI,ISSN,EISSN,ISBN,CITTYPE,ARTICLENUMBER,PAGE,PAGECOUNT,VOLUME,ISSUE,PUBLICATIONYEAR,CITATIONTITLE,ABSTRACTDATA,AUTHOR,AUTHOR_1,DATABASE FROM BD_MASTER WHERE DATABASE='"+dbname+"'";
	        		
	        	}
            }
            else if(dbname.equalsIgnoreCase("ins"))
            {
            	if(loadnumber!=1)
	        	{
	        		sqlQuery="select pdoi DOI,anum as PII,'' as PUI,psn ISSN, npsn EISSN,sbn ISBN,nrtype CITTYPE,'' as ARTICLENUMBER,pipn PAGE,'' as PAGECOUNT,pvol VOLUME,piss ISSUE,pyr PUBLICATIONYEAR,ti CITATIONTITLE,ab ABSTRACTDATA,aus AUTHOR,aus2 AUTHOR_1,'INS' DATABASE,sspdate,fdate,cdate,su FROM INS_MASTER WHERE LOAD_NUMBER="+loadnumber;
	        	}
	        	else
	        	{
	        		sqlQuery="select pdoi DOI,anum as PII,'' as PUI,psn ISSN, npsn EISSN,sbn ISBN,nrtype CITTYPE,'' as ARTICLENUMBER,pipn PAGE,'' as PAGECOUNT,pvol VOLUME,piss ISSUE,pyr PUBLICATIONYEAR,ti CITATIONTITLE,ab ABSTRACTDATA,aus AUTHOR,aus2 AUTHOR_1,'INS' DATABASEsspdate,fdate,cdate,su FROM INS_MASTER";
	        		
	        	}
            }
            else if(dbname.equalsIgnoreCase("ibf"))
            {
            	if(loadnumber!=1)
	        	{
	        		sqlQuery="select DOI DOI,anum as PII,'' as PUI,'' as ISSN, '' as EISSN,'' as ISBN,rtype CITTYPE,'' as ARTICLENUMBER,ipn PAGE,'' as PAGECOUNT,vol VOLUME,iss ISSUE,pyr PUBLICATIONYEAR,ti CITATIONTITLE,ab ABSTRACTDATA,aus AUTHOR,'' as AUTHOR_1,'IBF' DATABASE,su,oinfo FROM IBF_MASTER WHERE LOAD_NUMBER="+loadnumber;
	        	}
	        	else
	        	{
	        		sqlQuery="select DOI DOI,anum as PII,'' as PUI,'' as ISSN, '' as EISSN,'' as ISBN,rtype CITTYPE,'' as ARTICLENUMBER,ipn PAGE,'' as PAGECOUNT,vol VOLUME,iss ISSUE,pyr PUBLICATIONYEAR,ti CITATIONTITLE,ab ABSTRACTDATA,aus AUTHOR,'' as AUTHOR_1,'IBF' DATABASE,su,oinfo FROM IBF_MASTER";
	        		
	        	}
            }
            
            //out.write("{\"evrecords\":[\n");
        	
        	builder = Json.createArrayBuilder();
        	
            System.out.println("SQLQUERY= "+sqlQuery);
            System.out.println("database="+database);
            rs = stmt.executeQuery(sqlQuery);
            while (rs.next())
    		{
    			try
    			{
    				dataMap = new HashMap();
    				String doi = rs.getString("DOI");
    				String pii = rs.getString("PII");
    				String pui = rs.getString("PUI");
    				String issn = rs.getString("ISSN"); 				
    				String eissn = rs.getString("EISSN");
    				String isbn = rs.getString("ISBN");
    				String cittype = rs.getString("CITTYPE");
    				String articlenumber = rs.getString("ARTICLENUMBER");  				
    				String page = rs.getString("PAGE");
    				String pagecount = rs.getString("PAGECOUNT");
    				String volume = rs.getString("VOLUME");
    				//System.out.println("volume="+volume);
    				String issue = rs.getString("ISSUE"); 	
    				//System.out.println("issue="+issue);
    				String publicationyear = rs.getString("PUBLICATIONYEAR");
    				String citationtitle = rs.getString("CITATIONTITLE");
    				String abstractdata = getStringFromClob(rs.getClob("ABSTRACTDATA"));  
    				String author = rs.getString("AUTHOR");  		
    				String database = rs.getString("DATABASE");
    				
    				if(doi!=null)
    				{
    					dataMap.put("DOI", doi.trim());
    				}
    				else
    				{
    					dataMap.put("DOI", "");
    				}
    				if(pii!=null)
    				{
    					dataMap.put("PII", pii.trim());
    				}
    				else
    				{
    					dataMap.put("PII", "");
    				}
    				
    				if(pui!=null)
    				{
    					dataMap.put("PUI", pui.trim());
    				}
    				else
    				{
    					dataMap.put("PUI", "");
    				}
    				
    				if(issn!=null)
    				{
    					dataMap.put("ISSN", issn);
    				}
    				else
    				{
    					dataMap.put("ISSN", "");
    				}
    				
    				
    				
    				if(isbn!=null)
    				{   					
    					if(database.equalsIgnoreCase("INS"))
    					{
    						//System.out.println("ISBN1="+isbn+" accessnumber="+pii);
    						dataMap.put("ISBN", isbn);
    					}
    					else
    					{
    						dataMap.put("ISBN", getISBN(isbn));
    					}
    				}
    				else
    				{
    					dataMap.put("ISBN", "");
    				}
    				
    				if(eissn!=null)
    				{
    					if(!database.equalsIgnoreCase("INS"))
    					{
    						dataMap.put("EISSN", eissn);
    					}
    				} 
    				else
    				{
    					dataMap.put("EISSN", "");
    				}
    				
    				if(cittype!=null)
    				{
    					dataMap.put("CITTYPE", cittype.trim());
    				}
    				else
    				{
    					dataMap.put("CITTYPE", "");
    				}
    				
    				if(articlenumber!=null)
    				{
    					dataMap.put("ARTICLENUMBER", articlenumber.trim());
    				}
    				else
    				{
    					dataMap.put("ARTICLENUMBER", "");
    				}
    				
    				if(page!=null)
    				{
    					if(database.equalsIgnoreCase("INS")||database.equalsIgnoreCase("IBF"))
    					{
    						dataMap.put("FIRSTPAGE", getINSFirstPage(page));
    						dataMap.put("PAGECOUNT", getINSLastPage(page));
    					}
    					else
    					{
    						dataMap.put("FIRSTPAGE", getBDFirstPage(page));
    					}
    				}
    				else
    				{
    					dataMap.put("FIRSTPAGE", "");
    				}
    				
    				if(pagecount!=null)
    				{
    					dataMap.put("PAGECOUNT", getPageCount(pagecount));
    				}
    				//else
    				//{
    				//	dataMap.put("PAGECOUNT", "");
    				//}
    				
    				if(volume!=null)
    				{
    					//dataMap.put("VOLUME", getFirstNumber(volume));
    					dataMap.put("VOLUME", volume);
    				}
    				else
    				{
    					dataMap.put("VOLUME", "");
    				}
    				
    				if(issue!=null)
    				{
    					//dataMap.put("issue", getFirstNumber(issue));
    					dataMap.put("ISSUE", issue);
    				}
    				else
    				{
    					dataMap.put("ISSUE", "");
    				}
    				
    				if(database.equalsIgnoreCase("INS"))
					{
						String strYear = "";
						if(publicationyear != null && validYear(getPubYear(publicationyear)))
			            {
			                strYear=getPubYear(publicationyear);
			            }
			            else if (rs.getString("sspdate") != null && validYear(getPubYear(rs.getString("sspdate"))))
			            {
			                strYear=getPubYear(rs.getString("sspdate"));
			            }
			            else if (rs.getString("fdate") != null && validYear(getPubYear(rs.getString("fdate"))))
			            {
			                strYear=getPubYear(rs.getString("fdate"));
			            }
			            else if (rs.getString("cdate") != null && validYear(getPubYear(rs.getString("cdate"))))
			            {
			                strYear=getPubYear(rs.getString("cdate"));
			            }
			            else if (rs.getString("su") != null && validYear(getPubYear(rs.getString("su"))))
			            {

			                strYear=getPubYear(rs.getString("su"));
			            }
						if(validYear(strYear))
						{
							dataMap.put("PUBLICATIONYEAR",strYear);
						}
						else
						{
							System.out.println("Invalid INS year "+strYear);
						}
					}
    				else if(database.equalsIgnoreCase("IBF"))
    				{
    					String strYear = "";
						if(publicationyear != null && validYear(getPubYear(publicationyear)))
			            {
			                strYear=getPubYear(publicationyear);
			            }
						else if (rs.getString("su") != null && validYear(getPubYear(rs.getString("su"))))
			            {

			                strYear=getPubYear(rs.getString("su"));
			            }
						
						if(validYear(strYear))
						{
							dataMap.put("PUBLICATIONYEAR",strYear);
						}
						else
						{
							System.out.println("Invalid IBF year "+strYear);
						}
    				}
    				else if(publicationyear!=null && publicationyear.length()>3)
    				{	    					    				
	    				dataMap.put("PUBLICATIONYEAR", publicationyear.substring(0,4));	    			
    				}
    				else
    				{
    					System.out.println("Invalid year "+publicationyear);
    				}
    				
    				if(citationtitle!=null)
    				{
    					if(database.equalsIgnoreCase("ins") || database.equalsIgnoreCase("ibf"))
    					{
    						dataMap.put("CITATIONTITLE", citationtitle);
    					}
    					else
    					{
    						dataMap.put("CITATIONTITLE", getTitle(citationtitle));
    					}
    				}
    				else
    				{
    					dataMap.put("CITATIONTITLE", "");
    				}
    				
    				if(abstractdata!=null)
    				{
    					dataMap.put("ABSTRACT", abstractdata);
    					
    				}
    				else
    				{
    					dataMap.put("ABSTRACT", "");
    				}
    				
    				if(author!=null)
    				{   					
						if(rs.getString("AUTHOR_1") !=null)
						{
							author=author+rs.getString("AUTHOR_1");
						}
						if(database.equalsIgnoreCase("INS"))
						{
							dataMap.put("AUTHOR",getInsAuthor(author));
							//System.out.println("AUTHOR="+getInsAuthor(author));
							dataMap.put("AUTHORSURENAME", getInsFirstAuthorSureName(author));
							//System.out.println("AUTHORSURENAME="+ getInsFirstAuthorSureName(author));
	    					dataMap.put("AUTHORGIVENNAME", getInsFirstAuthorGivenName(author));
	    					//System.out.println("AUTHORGIVENNAME"+ getInsFirstAuthorGivenName(author));
						}
						else
						{
	    					dataMap.put("AUTHORSURENAME", getFirstAuthorSureName(author));
	    					dataMap.put("AUTHORGIVENNAME", getFirstAuthorGivenName(author));
						}
    				}
    				else
    				{
    					dataMap.put("AUTHOR","");
						//System.out.println("AUTHOR="+getInsAuthor(author));
						dataMap.put("AUTHORSURENAME", "");
						//System.out.println("AUTHORSURENAME="+ getInsFirstAuthorSureName(author));
    					dataMap.put("AUTHORGIVENNAME", "");
    					//System.out.println("AUTHORGIVENNAME"+ getInsFirstAuthorGivenName(author));
    				}
    				
    				if(database!=null)
    				{
    					dataMap.put("DATABASE", database);
    				}
    				
    				builder.add(writeJson(dataMap));
    				
    				
    			}
    			catch (Exception e)
                {
    				e.printStackTrace();
                }
    		}
            JsonArray eva = builder.build();
        	//evo = Json.createObjectBuilder();
        	//evo.add("evrecords", eva);
        	Gson gson = new GsonBuilder().setPrettyPrinting().create();
        	JsonParser jp = new JsonParser();
        	//JsonElement je = jp.parse(evo.build().toString());
        	JsonElement je = jp.parse(eva.toString());
        	String prettyJsonString = gson.toJson(je);
        	out.write(prettyJsonString);
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
            
            if(out != null)
	    	{
	    		out.close();
	    	}
        }
    }
	
	private boolean validYear(String year)
    {

        return year.matches("[1-2][0-9][0-9][0-9]");
    }
	
	private String getPubYear(String y)
    {

        String year = "";
        String regularExpression = "((19\\d|20\\d)\\d)\\w?";
        Pattern p = Pattern.compile(regularExpression);
        Matcher m = p.matcher(y);
        if (m.find())
        {
            year = m.group(1).trim();
        }

        return year;
    }
	
	private String getStringFromClob(Clob clob) throws Exception
    {
        String temp = "";
        if (clob != null)
        {
            temp = clob.getSubString(1, (int) clob.length());
        }

        return temp;
    }
	
	public  String getInsAuthor(String aString)
	        throws Exception
    {

        StringBuffer bf = new StringBuffer();
        StringTokenizer st = new StringTokenizer(aString, Constants.AUDELIMITER);
        String s;

        while (st.hasMoreTokens())
        {
            s = st.nextToken().trim();
            if(s.length() > 0)
            {
                if(s.indexOf(Constants.IDDELIMITER) > -1)
                {
                     int i = s.indexOf(Constants.IDDELIMITER);
                      s = s.substring(0,i);
                }
                s = s.trim();

                bf.append(s+";");
            }

        }

        return bf.toString();

    }
	
	public  String getInsFirstAuthorSureName(String aString)
	        throws Exception
    {
		String lastName = "";
        StringTokenizer st = new StringTokenizer(aString, Constants.AUDELIMITER);
        String s;

        while (st.hasMoreTokens())
        {
            s = st.nextToken().trim();
            if(s.length() > 0)
            {
                if(s.indexOf(Constants.IDDELIMITER) > -1)
                {
                     int i = s.indexOf(Constants.IDDELIMITER);
                      s = s.substring(0,i);
                }
                s = s.trim();

                if(s.indexOf(",")>0)
                {
                	lastName=s.substring(0,s.indexOf(","));
                	
                }
                else if(s.indexOf(" ")>0)
                {
                	lastName=s.substring(s.lastIndexOf(" ")+1);
                }
                else
                {
                	lastName = s;
                }
                break;
            }

        }

        return lastName.trim();
    }
	
	public  String getInsFirstAuthorGivenName(String aString)
	        throws Exception
    {
		String givenName = "";
        StringTokenizer st = new StringTokenizer(aString, Constants.AUDELIMITER);
        String s;

        while (st.hasMoreTokens())
        {
            s = st.nextToken().trim();
            if(s.length() > 0)
            {
                if(s.indexOf(Constants.IDDELIMITER) > -1)
                {
                     int i = s.indexOf(Constants.IDDELIMITER);
                      s = s.substring(0,i);
                }
                s = s.trim();

                if(s.indexOf(",")>0)
                {
                	givenName=s.substring(s.indexOf(",")+1);
                	
                }
                else if(s.indexOf(" ")>0)
                {
                	givenName=s.substring(0,s.indexOf(" "));
                }
                else
                {
                	givenName = s;
                }
                break;
            }

        }

        return givenName.trim();
    }
	
	public String getFirstAuthorSureName(String bdAuthor)
    {
        if(bdAuthor != null && !bdAuthor.trim().equals(""))
        {
            BdAuthors aus = new BdAuthors(bdAuthor);
            List ausArray = aus.getAuthors(); 
            return ((BdAuthor)ausArray.get(0)).getSurname();
        }
        return "";
    }
	
	public String getFirstAuthorGivenName(String bdAuthor)
    {
        if(bdAuthor != null && !bdAuthor.trim().equals(""))
        {
            BdAuthors aus = new BdAuthors(bdAuthor);
            List ausArray = aus.getAuthors();         
            return ((BdAuthor)ausArray.get(0)).getGivenName();
        }
        return "";
    }
	
	private String getFirstNumber(String v)
    {

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
	
	private String getPageCount(String pagecount)
	{
		String output="";
		
		if(pagecount!=null && pagecount.length()>0)
		{
			//System.out.println("PAGECOUNT= "+pagecount);
			String[] pagecounts = pagecount.split(Constants.AUDELIMITER,-1);
			//System.out.println("PAGECOUNTS_SIZE= "+pagecounts.length);
			
			for(int i=0;i<pagecounts.length;i++)
			{
				//System.out.println("INDEX= "+i);
				String pagecountElement = pagecounts[i];
				if(pagecountElement!=null && pagecountElement.length()>0)
				{
					String [] pagecountElementArray = pagecountElement.split(Constants.IDDELIMITER,-1);
					//System.out.println("PAGECOUNTSELEMENT_SIZE= "+pagecountElementArray.length);
					String pagecountType = null;
					String pagecountvalue = null;
					if(pagecountElementArray.length>0)
					{
						pagecountType = pagecountElementArray[0];
						//System.out.println("pagecountType= "+pagecountType);
					}
					if(pagecountElementArray.length>1)
					{
						pagecountvalue = pagecountElementArray[1];
						output = pagecountvalue;
						//System.out.println("pagecountvalue= "+pagecountvalue);
					}										
										
				}//if
				if(output.length()>0)
				{
					break; //just take the first value
				}
			}//for
		}
		//System.out.println("PAGECOUNT1= "+output);
		return output;
	}
	private JsonObject writeJson(HashMap<String,String> dataMap) throws Exception
	{
		//System.out.println("issue "+ dataMap.get("ISSUE"));
		//System.out.println("firstpage "+ dataMap.get("FIRSTPAGE"));
		//System.out.println("pui="+dataMap.get("PUI")+" citationtitle "+ dataMap.get("CITATIONTITLE"));
		String piiLabel = "";
		String lastPage = "";
		if(database.equalsIgnoreCase("ins") || database.equalsIgnoreCase("ibf"))
		{
			piiLabel = "accessionnumber";
			if(database.equalsIgnoreCase("ibf"))
			{
				lastPage = "lastpage";
			}
			else
			{
				lastPage = "pagecount";
			}
		}
		else
		{
			piiLabel = "pii";
			lastPage = "lastpage";
		}
		
		JsonObject evJson = Json.createObjectBuilder()
                .add("pui", dataMap.get("PUI"))
                .add("doi", dataMap.get("DOI")==null?"":dataMap.get("DOI"))           
                .add(piiLabel, dataMap.get("PII")==null?"":dataMap.get("PII"))              
                .add("issn", dataMap.get("ISSN")==null?"":dataMap.get("ISSN"))
                .add("eissn", dataMap.get("EISSN")==null?"":dataMap.get("EISSN"))
                .add("isbn", dataMap.get("ISBN")==null?"":dataMap.get("ISBN"))                
                .add("doctype", dataMap.get("CITTYPE")==null?"":dataMap.get("CITTYPE"))
                .add("articlenumber", dataMap.get("ARTICLENUMBER")==null?"":dataMap.get("ARTICLENUMBER"))
                .add("firstpage", dataMap.get("FIRSTPAGE")==null?"":dataMap.get("FIRSTPAGE"))
                .add(lastPage, dataMap.get("PAGECOUNT")==null?"":dataMap.get("PAGECOUNT"))
                .add("volume", dataMap.get("VOLUME")==null?"":dataMap.get("VOLUME"))              
                .add("issue", dataMap.get("ISSUE")==null?"":dataMap.get("ISSUE"))
                .add("publicationyear", dataMap.get("PUBLICATIONYEAR")==null?"":dataMap.get("PUBLICATIONYEAR"))
                .add("citationtitle", dataMap.get("CITATIONTITLE")==null?"":dataMap.get("CITATIONTITLE"))                
                .add("abstract", dataMap.get("ABSTRACT")==null?"":dataMap.get("ABSTRACT"))
                .add("firstauthorsurename", dataMap.get("AUTHORSURENAME")==null?"":dataMap.get("AUTHORSURENAME"))
                .add("firstauthorgivenname", dataMap.get("AUTHORGIVENNAME")==null?"":dataMap.get("AUTHORGIVENNAME")) 
                .add("author", dataMap.get("AUTHOR")==null?"":dataMap.get("AUTHOR"))
                .add("database", dataMap.get("DATABASE")==null?"":dataMap.get("DATABASE"))                          
                .build();
         
        //System.out.println("Object: " + personObject);
		return evJson;
	}
	
	private String getBDFirstPage(String v)
    {
        BdPage pages = new BdPage(v);
        return pages.getStartPage();
    }
	
	private String getINSFirstPage(String v)
    {

        MatchResult mResult = null;
        if (v == null)
        {
            return "";
        }

        if (perl.match("/[A-Z]?[0-9][0-9]*/", v))
        {
            mResult = perl.getMatch();
        }
        else
        {
            return "";
        }

        return mResult.toString();
    }
	
	private String getINSLastPage(String v)
    {

        String mResult = null;
        if (v == null)
        {
            return "";
        }

        if (v.indexOf("-")>0)
        {
            mResult = v.substring(v.indexOf("-")+1);
            //System.out.println("page="+v+" lastPage="+mResult);
        }
        else
        {
            return "";
        }

        return mResult.toString();
    }

	
	private String getISBN(String isbnString) throws Exception
    {
        List list = new ArrayList();
        if(isbnString != null)
        {
            BdIsbn isbn = new BdIsbn(isbnString);
            List isbnList = isbn.getISBN();
            for(int i=0;i<isbnList.size();i++)
            {
                BdIsbn isbnObject = (BdIsbn)isbnList.get(i);

                if(isbnObject.getValue() !=null)
                {
                    list.add(isbnObject.getValue());
                }
            }
        }

        return String.join(",", list);
    }
	
	  private String getTitle(String citationTitle) throws Exception
	    {
	        List list = new ArrayList();
	        if(citationTitle != null)
	        {
	            BdCitationTitle ct = new BdCitationTitle(citationTitle);
	            List ctList = ct.getCitationTitle();
	            List tctList = ct.getTranslatedCitationTitle();

	            for(int i=0;i<ctList.size();i++)
	            {
	                BdCitationTitle ctObject = (BdCitationTitle)ctList.get(i);

	                if(ctObject.getTitle() !=null)
	                {
	                    list.add(ctObject.getTitle());
	                }	               
	            }
	            
	            for(int i=0;i<tctList.size();i++)
	            {
	                BdCitationTitle tctObject = (BdCitationTitle)tctList.get(i);

	                if(tctObject.getTitle() !=null)
	                {
	                    list.add(tctObject.getTitle());
	                }	                
	            }
	        }

	        return String.join(",", list);	        
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