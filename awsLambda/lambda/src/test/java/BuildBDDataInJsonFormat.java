import java.io.FileWriter;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            bjson.getDataFromBDMASTER(bjson.loadnumber,database);
      }
      
      private void getDataFromBDMASTER(int loadnumber,String dbname) throws Exception
    {      
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        FileWriter out = null;
        String outfileName = dbname+"_"+loadnumber+".json";
        HashMap<String,String> dataMap = new HashMap();
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
           
            if(loadnumber!=1)
            {
                  sqlQuery="select DOI,PII,PUI,ISSN,EISSN,ISBN,CITTYPE,ARTICLENUMBER,PAGE,PAGECOUNT,VOLUME,ISSUE,PUBLICATIONYEAR,CITATIONTITLE,ABSTRACTDATA,AUTHOR,AUTHOR_1,DATABASE FROM BD_MASTER WHERE LOADNUMBER="+loadnumber+" and DATABASE='"+dbname+"'";
            }
            else
            {
                  sqlQuery="select DOI,PII,PUI,ISSN,EISSN,ISBN,CITTYPE,ARTICLENUMBER,PAGE,PAGECOUNT,VOLUME,ISSUE,PUBLICATIONYEAR,CITATIONTITLE,ABSTRACTDATA,AUTHOR,AUTHOR_1,DATABASE FROM BD_MASTER WHERE DATABASE='"+dbname+"'";
                  
            }
            //out.write("{\"evrecords\":[\n");
            
            builder = Json.createArrayBuilder();
            
            System.out.println("SQLQUERY= "+sqlQuery);
            rs = stmt.executeQuery(sqlQuery);
            while (rs.next())
            {
                  try
                  {
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
                        if(pii!=null)
                        {
                              dataMap.put("PII", pii.trim());
                        }
                        if(pui!=null)
                        {
                              dataMap.put("PUI", pui.trim());
                        }
                        if(issn!=null)
                        {
                              dataMap.put("ISSN", issn);
                        }
                        if(isbn!=null)
                        {
                              dataMap.put("ISBN", getISBN(isbn));
                        }
                        if(eissn!=null)
                        {
                              dataMap.put("EISSN", eissn);
                        }                       
                        if(cittype!=null)
                        {
                              dataMap.put("CITTYPE", cittype.trim());
                        }
                        if(articlenumber!=null)
                        {
                              dataMap.put("ARTICLENUMBER", articlenumber.trim());
                        }
                        if(page!=null)
                        {
                              dataMap.put("FIRSTPAGE", getFirstPage(page));
                        }
                        if(pagecount!=null)
                        {
                              dataMap.put("PAGECOUNT", getPageCount(pagecount));
                        }
                        if(volume!=null)
                        {
                              //dataMap.put("VOLUME", getFirstNumber(volume));
                              dataMap.put("VOLUME", volume);
                        }
                        if(issue!=null)
                        {
                              //dataMap.put("issue", getFirstNumber(issue));
                              dataMap.put("issue", issue);
                        }
                        
                        if(publicationyear!=null)
                        {
                              dataMap.put("PUBLICATIONYEAR", publicationyear.substring(0,4));
                        }
                        if(citationtitle!=null)
                        {
                              dataMap.put("CITATIONTITLE", getTitle(citationtitle));
                        }
                        if(abstractdata!=null)
                        {
                              dataMap.put("ABSTRACT", abstractdata);
                              
                        }
                        
                        if(author!=null)
                        {                             
                         if(rs.getString("AUTHOR_1") !=null)
                         {
                             author=author+rs.getString("AUTHOR_1");
                         }
                              dataMap.put("AUTHORSURENAME", getFirstAuthorSureName(author));
                              dataMap.put("AUTHORGIVENNAME", getFirstAuthorGivenName(author));
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
      
      private String getStringFromClob(Clob clob) throws Exception
    {
        String temp = null;
        if (clob != null)
        {
            temp = clob.getSubString(1, (int) clob.length());
        }

        return temp;
    }
      
      public String getFirstAuthorSureName(String bdAuthor)
    {
        if(bdAuthor != null && !bdAuthor.trim().equals(""))
        {
            BdAuthors aus = new BdAuthors(bdAuthor);
            List ausArray = aus.getAuthors(); 
            return ((BdAuthor)ausArray.get(0)).getSurname();
        }
        return null;
    }
      
      public String getFirstAuthorGivenName(String bdAuthor)
    {
        if(bdAuthor != null && !bdAuthor.trim().equals(""))
        {
            BdAuthors aus = new BdAuthors(bdAuthor);
            List ausArray = aus.getAuthors();         
            return ((BdAuthor)ausArray.get(0)).getGivenName();
        }
        return null;
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
                  String[] pagecounts = pagecount.split(Constants.AUDELIMITER);
                  
                  for(int i=0;i<pagecounts.length;i++)
                  {
                        String pagecountElement = pagecounts[i];
                        if(pagecountElement!=null && pagecountElement.length()>0)
                        {
                              String [] pagecountElementArray = pagecountElement.split(Constants.IDDELIMITER);
                              String pagecountType = null;
                              String pagecountvalue = null;
                              if(pagecountElementArray.length>0)
                              {
                                    pagecountType = pagecountElementArray[0];
                              }
                              if(pagecountElementArray.length>1)
                              {
                                    pagecountvalue = pagecountElementArray[1];
                                    output = pagecountvalue;
                              }                                                           
                                                            
                        }//if
                        break; //just take the first value
                  }//for
            }
            return output;
      }
      private JsonObject writeJson(HashMap<String,String> dataMap) throws Exception
      {
            JsonObject evJson = Json.createObjectBuilder()
                .add("pui", dataMap.get("PUI"))
                .add("doi", dataMap.get("DOI")==null?"":dataMap.get("DOI"))
                .add("pii", dataMap.get("PII")==null?"":dataMap.get("PII"))              
                .add("issn", dataMap.get("ISSN")==null?"":dataMap.get("ISSN"))
                .add("eissn", dataMap.get("EISSN")==null?"":dataMap.get("EISSN"))
                .add("isbn", dataMap.get("ISBN")==null?"":dataMap.get("ISBN"))                
                .add("doctype", dataMap.get("CITTYPE")==null?"":dataMap.get("CITTYPE"))
                .add("articlenumber", dataMap.get("ARTICLENUMBER")==null?"":dataMap.get("ARTICLENUMBER"))
                .add("firstpage", dataMap.get("PAGE")==null?"":dataMap.get("PAGE"))
                .add("volume", dataMap.get("VOLUME")==null?"":dataMap.get("VOLUME"))              
                .add("issue", dataMap.get("ISSUE")==null?"":dataMap.get("ISSUE"))
                .add("publicationyear", dataMap.get("PUBLICATIONYEAR")==null?"":dataMap.get("PUBLICATIONYEAR"))
                .add("citationtitle", dataMap.get("CITATIONTITLE")==null?"":dataMap.get("CITATIONTITLE"))                
                .add("abstract", dataMap.get("ABSTRACT")==null?"":dataMap.get("ABSTRACT"))
                .add("firstauthorsurename", dataMap.get("AUTHORSURENAME")==null?"":dataMap.get("AUTHORSURENAME"))
                .add("firstauthorgivenname", dataMap.get("AUTHORGIVENNAME")==null?"":dataMap.get("AUTHORGIVENNAME"))               
                .add("database", dataMap.get("DATABASE")==null?"":dataMap.get("DATABASE"))                          
                .build();
         
        //System.out.println("Object: " + personObject);
            return evJson;
      }
      
      private String getFirstPage(String v)
    {
        BdPage pages = new BdPage(v);
        return pages.getStartPage();
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

                  for(int i=0;i<ctList.size();i++)
                  {
                      BdCitationTitle ctObject = (BdCitationTitle)ctList.get(i);

                      if(ctObject.getTitle() !=null)
                      {
                          list.add(ctObject.getTitle());
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

