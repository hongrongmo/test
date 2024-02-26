package org.ei.util;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.ei.dataloading.CombinedXMLWriter;
import org.ei.dataloading.EVCombinedRec;
import org.ei.dataloading.awss3.AmazonS3Service;
import org.ei.dataloading.bd.loadtime.XmlCombiner;
import org.ei.dataloading.DataLoadDictionary;
import org.ei.util.db.DbConnection;
import org.ei.common.Constants;
import org.ei.common.bd.BdAuthors;
import org.ei.common.bd.BdCitationTitle;
import org.ei.common.bd.BdConfLocations;
import org.ei.common.bd.BdIsbn;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.text.StringEscapeUtils;

public class CheckBDCompleteness
{
	private static String database="cpx";
	private static String url="jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
    private static String driver="oracle.jdbc.driver.OracleDriver";
	private static String username="ap_ev_search";
	private static String password="ei3it";
	private static String loadnumber=null;
	private static String bucketName = "ev-datasets-cert";
	private DataLoadDictionary dictionary = new DataLoadDictionary();
	private CombinedXMLWriter c=new CombinedXMLWriter(1,1,"cpx");
	
	public static void main(String args[]) throws Exception
	{
		Connection con = null;
		CheckBDCompleteness checkBD = new CheckBDCompleteness();
		long startTime = System.currentTimeMillis();
	    long endTime = System.currentTimeMillis();
	    String[] weeks = null;
	    if(args.length>6)
	    {
	    	loadnumber=args[6];
	    	System.out.println("LOADNUMBER:"+loadnumber);	
	    }
	    
	    if(args.length>5)
	    {
	    	url=args[0];
	    	System.out.println("URL:"+url);
	    	driver=args[1];
	    	System.out.println("DRIVER:"+driver);
	    	username=args[2];
	    	System.out.println("USERNAME:"+username);
	    	password=args[3];
	    	System.out.println("PASSWORD:"+password);
	    	bucketName=args[4];
	    	System.out.println("BUCKETNAME:"+bucketName);
	    	database=args[5];
	    	System.out.println("DATABASE:"+database);
	    	    	
	    }
	    else
	    {
	    	System.out.println("not enough parameters");
	    	System.exit(1);
	    }
	    con = DbConnection.getConnection(url, driver, username, password);
	    if(loadnumber !=null)
	    {
	    	weeks=new String[1];
	    	weeks[0]=loadnumber;
	    }
	    else
	    {
	    	weeks=checkBD.getWeeknumber(database,con);
	    }
	    
	    for(int i=0;i<weeks.length;i++)
	    {
	    	loadnumber=weeks[i];
	    	System.out.println(loadnumber);
	    	checkBD.generateFile(loadnumber,database,con);
	    }
	}
	
	private String[] getWeeknumber(String database, Connection con)
	{		
        Statement stmt = null;
        ResultSet rs = null;
        List<String> weeks = new ArrayList<>();
        try
        {       	
            stmt = con.createStatement();
            CombinedXMLWriter c=new CombinedXMLWriter(1,1,"cpx");
            XmlCombiner xml=new XmlCombiner(c);             
            String sqlQuery = "select loadnumber from bd_master where database='"+database+"' group by loadnumber order by loadnumber";           
            System.out.println("Running the query "+sqlQuery);
            rs = stmt.executeQuery(sqlQuery);
            
            while(rs.next()) 
            {
            	weeks.add(rs.getString("loadnumber"));
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
            return (String[]) weeks.toArray(new String[1]);
        }
	}
	
	 private void generateFile(String loadnumber,String dbname, Connection con) throws Exception
	 {	     
        Statement stmt = null;
        ResultSet rs = null;
        String fileName = dbname+"_"+loadnumber+".txt";
        FileWriter out =null;
        //FileWriter out = new FileWriter(dbname+"_"+loadnumber+".txt");
        try
        {
        	String dir = "./monthly_data";
			File file =new File (dir);
			if(!file.exists())
				file.mkdir();
        	file = new File(dir + "/" + fileName);
			out = new FileWriter(file.getAbsolutePath(),false);
			
            stmt = con.createStatement();
            //CombinedXMLWriter c=new CombinedXMLWriter(1,1,"cpx");
            XmlCombiner xml=new XmlCombiner(c);
            System.out.println("loadnumber= "+loadnumber+" dbname= "+dbname);
            System.out.println("Running the query...");
       
            String sqlQuery = null;
      
            sqlQuery = "select PUI,ACCESSNUMBER,DOI,ISSN,EISSN,ISSUE,ISBN,VOLUME,PAGE,ISSUE,CITTYPE,ARTICLENUMBER,CITATIONTITLE,CONFCODE,CONFNAME,CONFDATE,CONFLOCATION,AUTHOR,AUTHOR_1,LOADNUMBER,UPDATENUMBER,PUBLICATIONYEAR from bd_master where database='"+dbname+"' and loadnumber="+loadnumber;
            //sqlQuery = "select PUI,ACCESSNUMBER,DOI,ISSN,EISSN,ISSUE,ISBN,VOLUME,PAGE,ISSUE,CITTYPE,ARTICLENUMBER,CITATIONTITLE,CONFCODE,CONFNAME,CONFDATE,CONFLOCATION,AUTHOR,AUTHOR_1,LOADNUMBER,UPDATENUMBER,PUBLICATIONYEAR from bd_master where database='"+dbname+"' and (loadnumber="+loadnumber+" or updatenumber like '"+loadnumber+"%')";
            
            System.out.println("SQLQUERY= "+sqlQuery);
            rs = stmt.executeQuery(sqlQuery);   
            out.write("PUI"+"\t"+"ACCESSNUMBER"+"\t"+"DOI"+"\t"+"ISSN"+"\t"+"EISSN"+"\t"+"ISSUE"+"\t"+"ISBN"+"\t"+"VOLUME"+"\t");
            out.write("PAGE(page/firstpage/lastpage)"+"\t"+"CITTYPE"+"\t"+"ARTICLENUMBER"+"\t"+"CITATIONTITLE"+"\t"+"CONFCODE"+"\t");
            out.write("CONFNAME"+"\t"+"CONFDATE"+"\t"+"CONFLOCATION"+"\t"+"AUTHOR"+"\t"+"PUBLICATIONYEAR"+"\t"+"LOADNUMBER"+"\t");
            out.write("UPDATENUMBER"+"\t"+"STARTDATE"+"\t"+"ENDDATE"+"\n");
            while(rs.next()) 
            {
            	   out.write(rs.getString("PUI")+"\t");  
            	   out.write(rs.getString("ACCESSNUMBER")+"\t");  
            	   if(rs.getString("DOI")!=null)
            	   {
            		   out.write(rs.getString("DOI").strip().replaceAll("&amp;", "&")); 
            	   }	            	   
            	   out.write("\t"); 
            	   if(rs.getString("ISSN")!=null)
            	   {
            		   out.write(rs.getString("ISSN").strip()); 
            	   }	            	   
            	   out.write("\t"); 
            	   if(rs.getString("EISSN")!=null)
            	   {
            		   out.write(rs.getString("EISSN").strip()); 
            	   }	            	   
            	   out.write("\t"); 
            	   if(rs.getString("ISSUE")!=null)
            	   {
            		   out.write(rs.getString("ISSUE").strip()); 
            	   }	            	   
            	   out.write("\t"); 
            	   if(rs.getString("ISBN")!=null)
            	   {
            		   out.write(prepareISBN(rs.getString("ISBN")).strip()); 
            	   }	            	   
            	   out.write("\t"); 
            	   if(rs.getString("VOLUME")!=null)
            	   {
            		   out.write(rs.getString("VOLUME").strip()); 
            	   }	            	   
            	   out.write("\t"); 
            	   if(rs.getString("PAGE")!=null)
            	   {
		              StringBuffer pageBuffer = new StringBuffer();
		              String[] pageArr=rs.getString("PAGE").split(Constants.AUDELIMITER);
		              for(int i=0;i<pageArr.length;i++)
		              {	            	 
		            	  pageBuffer.append(pageArr[i]);
		            	  if(i<pageArr.length-1)
		            	  { 
		            		  pageBuffer.append("/");
		            	  }
		              }
		              out.write(pageBuffer.toString().strip()); 
            	   }	            	   
            	   out.write("\t"); 
            	   if(rs.getString("CITTYPE")!=null)
            	   {
            		   out.write(rs.getString("CITTYPE").strip()); 
            	   }	            	   
            	   out.write("\t"); 
            	   if(rs.getString("ARTICLENUMBER")!=null)
            	   {
            		   out.write(rs.getString("ARTICLENUMBER").strip()); 
            	   }
            	   out.write("\t"); 
            	   if(rs.getString("CITATIONTITLE")!=null)
            	   {
		              String origString=prepareCitationTitle(rs.getString("CITATIONTITLE")).strip();
		              String updateString=StringEscapeUtils.unescapeHtml4(origString);		         
		              String titleString=c.removeExtraSpace(prepareCitationTitle(rs.getString("CITATIONTITLE"))).strip();
		              titleString=reverseHtmlEntity(titleString);
		              out.write(titleString);		             
            	   }	
            	   out.write("\t"); 
            	   if(rs.getString("CONFCODE")!=null)
            	   {
            		   out.write(rs.getString("CONFCODE").strip()); 
            	   }
            	   out.write("\t"); 
            	   if(rs.getString("CONFNAME")!=null)
            	   {		               
		              String confString=c.removeExtraSpace(rs.getString("CONFNAME").strip());
		              confString=reverseHtmlEntity(confString);
		              out.write(confString);		             
	        	   }	
	        	   out.write("\t"); 
	        	   if(rs.getString("CONFDATE")!=null)
	        	   {
	        		   out.write(rs.getString("CONFDATE").strip()); 
	        	   }
	        	   out.write("\t"); 
	        	   if(rs.getString("CONFLOCATION")!=null)
	        	   {
	        		   out.write(reverseHtmlEntity(StringEscapeUtils.unescapeHtml4(prepareConfLocation(rs.getString("CONFLOCATION")).strip()))); 
	        	   }	
	        	   out.write("\t"); 
	        	   if (rs.getString("AUTHOR") != null) 
	        	   {
						String authorString = rs.getString("AUTHOR");
						if (rs.getString("AUTHOR_1") != null) {
							authorString = authorString + rs.getString("AUTHOR_1");
						}						
						String auString=prepareBdAuthor(authorString).strip();
						auString=StringEscapeUtils.unescapeHtml4(auString);
						out.write(reverseHtmlEntity(auString)); 
	        	   }	
	        	   out.write("\t"); 
            	   
            	   if (rs.getString("PUBLICATIONYEAR") != null && rs.getString("PUBLICATIONYEAR").length() > 3) {
            		   out.write(rs.getString("PUBLICATIONYEAR").substring(0, 4));						
            	   }
            	   
            	   out.write("\t");
            	   
            	   if(rs.getString("LOADNUMBER")!=null)
            	   {
            		   out.write(rs.getString("LOADNUMBER")); 
            	   }	
            	   out.write("\t"); 
            	   if(rs.getString("UPDATENUMBER")!=null)
            	   {
            		   out.write(rs.getString("UPDATENUMBER")); 
            	   }
            	   
            	   out.write("\t"); 
            	   String startDate="";
            	   String endDate="";
            	  
	        	   if(rs.getString("CONFDATE")!=null)
	        	   {
	        		   String dateString=rs.getString("CONFDATE");
	        		   if(dateString.indexOf("-")>0)
	        		   {
	        			  String[] dateArray = dateString.split("-");
	        			  startDate=dateArray[0];
	        			  endDate=dateArray[1];
	        		   }
	        		   else
	        		   {
	        			   startDate=dateString;
	        		   }
	        	   }
	        	   out.write(startDate.strip());
	        	   out.write("\t"); 
	        	   out.write(endDate.strip());
            	   out.write("\n"); 	  
            	   out.flush();
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
            if (out != null)
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
	 
	 private String reverseHtmlEntity(String input)
	 {
		String text=input;
		if(input!=null)
		{
			text = text.replaceAll("&amp;","&");			
			text = text.replaceAll("&rlarr;","⇄");
			text = text.replaceAll("&cire;","≗");	
			text = text.replaceAll("&thetasym;","ϑ");	
			text = text.replaceAll("&quot;","\"");
			text = text.replaceAll("&lt;","<");
			text = text.replaceAll("&gt;",">");
			text = text.replaceAll("&Ccedil;","Ç");
			text = text.replaceAll("&uuml;","ü");
			text = text.replaceAll("&eacute;","é");
			text = text.replaceAll("&acirc;","â");
			text = text.replaceAll("&auml;","ä");
			text = text.replaceAll("&agrave;","à");
			text = text.replaceAll("&aring;","å");
			text = text.replaceAll("&ccedil;","ç");
			text = text.replaceAll("&ecirc;","ê");
			text = text.replaceAll("&euml;","ë");
			text = text.replaceAll("&egrave;","è");
			text = text.replaceAll("&iuml;","ï");
			text = text.replaceAll("&icirc;","î");
			text = text.replaceAll("&igrave;","ì");
			text = text.replaceAll("&Auml;","Ä");
			text = text.replaceAll("&Aring;","Å");
			text = text.replaceAll("&Eacute;","É");
			text = text.replaceAll("&aelig;","æ");
			text = text.replaceAll("&AElig;","Æ");
			text = text.replaceAll("&ocirc;","ô");
			text = text.replaceAll("&ouml;","ö");
			text = text.replaceAll("&ograve;","ò");
			text = text.replaceAll("&ucirc;","û");
			text = text.replaceAll("&ugrave;","ù");
			text = text.replaceAll("&yuml;","ÿ");
			text = text.replaceAll("&Ouml;","Ö");
			text = text.replaceAll("&Uuml;","Ü");
			text = text.replaceAll("&pound;","£");
			text = text.replaceAll("&yen;","¥");
			text = text.replaceAll("&nbsp;"," ");
			text = text.replaceAll("&iexcl;","¡");
			text = text.replaceAll("&cent;","¢");
			text = text.replaceAll("&pound;","£");
			text = text.replaceAll("&curren;","¤");
			text = text.replaceAll("&yen;","¥");
			text = text.replaceAll("&brvbar;","¦");
			text = text.replaceAll("&sect;","§");
			text = text.replaceAll("&uml;","¨");
			text = text.replaceAll("&copy;","©");
			text = text.replaceAll("&ordf;","ª");
			text = text.replaceAll("&laquo;","«");
			text = text.replaceAll("&not;","¬");
			text = text.replaceAll("&shy;","­");
			text = text.replaceAll("&reg;","®");
			text = text.replaceAll("&macr;","¯");
			text = text.replaceAll("&deg;","°");
			text = text.replaceAll("&plusmn;","±");
			text = text.replaceAll("&sup2;","²");
			text = text.replaceAll("&sup3;","³");
			text = text.replaceAll("&acute;","´");
			text = text.replaceAll("&micro;","µ");
			text = text.replaceAll("&para;","¶");
			text = text.replaceAll("&middot;","·");
			text = text.replaceAll("&cedil;","¸");
			text = text.replaceAll("&sup1;","¹");
			text = text.replaceAll("&ordm;","º");
			text = text.replaceAll("&raquo;","»");
			text = text.replaceAll("&frac14;","¼");
			text = text.replaceAll("&frac12;","½");
			text = text.replaceAll("&frac34;","¾");
			text = text.replaceAll("&iquest;","¿");
			text = text.replaceAll("&Agrave;","À");
			text = text.replaceAll("&Aacute;","Á");
			text = text.replaceAll("&Acirc;","Â");
			text = text.replaceAll("&Atilde;","Ã");
			text = text.replaceAll("&Auml;","Ä");
			text = text.replaceAll("&Aring;","Å");
			text = text.replaceAll("&AElig;","Æ");
			text = text.replaceAll("&Ccedil;","Ç");
			text = text.replaceAll("&Egrave;","È");
			text = text.replaceAll("&Eacute;","É");
			text = text.replaceAll("&Ecirc;","Ê");
			text = text.replaceAll("&Euml;","Ë");
			text = text.replaceAll("&Igrave;","Ì");
			text = text.replaceAll("&Iacute;","Í");
			text = text.replaceAll("&Icirc;","Î");
			text = text.replaceAll("&Iuml;","Ï");
			text = text.replaceAll("&ETH;","Ð");
			text = text.replaceAll("&Ntilde;","Ñ");
			text = text.replaceAll("&Ograve;","Ò");
			text = text.replaceAll("&Oacute;","Ó");
			text = text.replaceAll("&Ocirc;","Ô");
			text = text.replaceAll("&Otilde;","Õ");
			text = text.replaceAll("&Ouml;","Ö");
			text = text.replaceAll("&times;","×");
			text = text.replaceAll("&Oslash;","Ø");
			text = text.replaceAll("&Ugrave;","Ù");
			text = text.replaceAll("&Uacute;","Ú");
			text = text.replaceAll("&Ucirc;","Û");
			text = text.replaceAll("&Uuml;","Ü");
			text = text.replaceAll("&Yacute;","Ý");
			text = text.replaceAll("&THORN;","Þ");
			text = text.replaceAll("&szlig;","ß");
			text = text.replaceAll("&agrave;","à");
			text = text.replaceAll("&aacute;","á");
			text = text.replaceAll("&acirc;","â");
			text = text.replaceAll("&atilde;","ã");
			text = text.replaceAll("&auml;","ä");
			text = text.replaceAll("&aring;","å");
			text = text.replaceAll("&aelig;","æ");
			text = text.replaceAll("&ccedil;","ç");
			text = text.replaceAll("&egrave;","è");
			text = text.replaceAll("&eacute;","é");
			text = text.replaceAll("&ecirc;","ê");
			text = text.replaceAll("&euml;","ë");
			text = text.replaceAll("&igrave;","ì");
			text = text.replaceAll("&iacute;","í");
			text = text.replaceAll("&icirc;","î");
			text = text.replaceAll("&iuml;","ï");
			text = text.replaceAll("&eth;","ð");
			text = text.replaceAll("&ntilde;","ñ");
			text = text.replaceAll("&ograve;","ò");
			text = text.replaceAll("&oacute;","ó");
			text = text.replaceAll("&ocirc;","ô");
			text = text.replaceAll("&otilde;","õ");
			text = text.replaceAll("&ouml;","ö");
			text = text.replaceAll("&divide;","÷");
			text = text.replaceAll("&oslash;","ø");
			text = text.replaceAll("&ugrave;","ù");
			text = text.replaceAll("&uacute;","ú");
			text = text.replaceAll("&ucirc;","û");
			text = text.replaceAll("&uuml;","ü");
			text = text.replaceAll("&yacute;","ý");
			text = text.replaceAll("&thorn;","þ");
			text = text.replaceAll("&yuml;","ÿ");
			text = text.replaceAll("&Lstrok;","Ł");
			text = text.replaceAll("&lstrok;","ł");
			text = text.replaceAll("&OElig;","Œ");
			text = text.replaceAll("&oelig;","œ");
			text = text.replaceAll("&Scaron;","Š");
			text = text.replaceAll("&scaron;","š");
			text = text.replaceAll("&Yuml;","Ÿ");
			text = text.replaceAll("&fnof;","ƒ");
			text = text.replaceAll("&circ;","ˆ");
			text = text.replaceAll("&tilde;","˜");
			text = text.replaceAll("&grave;","`");
			text = text.replaceAll("&acute;","´");
			text = text.replaceAll("&circ;","ˆ");
			text = text.replaceAll("&macr;","¯");
			text = text.replaceAll("&dot;","˙");
			text = text.replaceAll("&die;","¨");
			text = text.replaceAll("&ring;","˚");
			text = text.replaceAll("&caron;","ˇ");
			text = text.replaceAll("&cedil;","¸");
			text = text.replaceAll("&Alpha;","Α");
			text = text.replaceAll("&Beta;","Β");
			text = text.replaceAll("&Gamma;","Γ");
			text = text.replaceAll("&Delta;","Δ");
			text = text.replaceAll("&Epsilon;","Ε");
			text = text.replaceAll("&Zeta;","Ζ");
			text = text.replaceAll("&Eta;","Η");
			text = text.replaceAll("&Theta;","Θ");
			text = text.replaceAll("&Iota;","Ι");
			text = text.replaceAll("&Kappa;","Κ");
			text = text.replaceAll("&Lambda;","Λ");
			text = text.replaceAll("&Mu;","Μ");
			text = text.replaceAll("&Nu;","Ν");
			text = text.replaceAll("&Xi;","Ξ");
			text = text.replaceAll("&Omicron;","Ο");
			text = text.replaceAll("&Pi;","Π");
			text = text.replaceAll("&Rho;","Ρ");
			text = text.replaceAll("&Sigma;","Σ");
			text = text.replaceAll("&Tau;","Τ");
			text = text.replaceAll("&Upsi;","ϒ");
			text = text.replaceAll("&Phi;","Φ");
			text = text.replaceAll("&Chi;","Χ");
			text = text.replaceAll("&Psi;","Ψ");
			text = text.replaceAll("&Omega;","Ω");
			text = text.replaceAll("&alpha;","α");
			text = text.replaceAll("&beta;","β");
			text = text.replaceAll("&gamma;","γ");
			text = text.replaceAll("&delta;","δ");
			text = text.replaceAll("&epsi;","ε");
			text = text.replaceAll("&zeta;","ζ");
			text = text.replaceAll("&eta;","η");
			text = text.replaceAll("&theta;","θ");
			text = text.replaceAll("&iota;","ι");
			text = text.replaceAll("&kappa;","κ");
			text = text.replaceAll("&lambda;","λ");
			text = text.replaceAll("&mu;","μ");
			text = text.replaceAll("&nu;","ν");
			text = text.replaceAll("&xi;","ξ");
			text = text.replaceAll("&omicron;","ο");
			text = text.replaceAll("&pi;","π");
			text = text.replaceAll("&rho;","ρ");
			text = text.replaceAll("&sigmaf;","ς");
			text = text.replaceAll("&sigma;","σ");
			text = text.replaceAll("&tau;","τ");
			text = text.replaceAll("&upsi;","υ");
			text = text.replaceAll("&phi;","φ");
			text = text.replaceAll("&chi;","χ");
			text = text.replaceAll("&psi;","ψ");
			text = text.replaceAll("&omega;","ω");
			text = text.replaceAll("&theta;","θ");
			text = text.replaceAll("&upsih;","ϒ");
			text = text.replaceAll("&straightphi;","ϕ");
			text = text.replaceAll("&piv;","ϖ");
			text = text.replaceAll("&Gammad;","Ϝ");
			text = text.replaceAll("&gammad;","ϝ");
			text = text.replaceAll("&IOcy;","Ё");
			text = text.replaceAll("&DJcy;","Ђ");
			text = text.replaceAll("&GJcy;","Ѓ");
			text = text.replaceAll("&Jukcy;","Є");
			text = text.replaceAll("&DScy;","Ѕ");
			text = text.replaceAll("&Iukcy;","І");
			text = text.replaceAll("&YIcy;","Ї");
			text = text.replaceAll("&Jsercy;","Ј");
			text = text.replaceAll("&LJcy;","Љ");
			text = text.replaceAll("&NJcy;","Њ");
			text = text.replaceAll("&TSHcy;","Ћ");
			text = text.replaceAll("&KJcy;","Ќ");
			text = text.replaceAll("&Ubrcy;","Ў");
			text = text.replaceAll("&DZcy;","Џ");
			text = text.replaceAll("&Acy;","А");
			text = text.replaceAll("&Bcy;","Б");
			text = text.replaceAll("&Vcy;","В");
			text = text.replaceAll("&Gcy;","Г");
			text = text.replaceAll("&Dcy;","Д");
			text = text.replaceAll("&IEcy;","Е");
			text = text.replaceAll("&ZHcy;","Ж");
			text = text.replaceAll("&Zcy;","З");
			text = text.replaceAll("&Icy;","И");
			text = text.replaceAll("&Jcy;","Й");
			text = text.replaceAll("&Kcy;","К");
			text = text.replaceAll("&Lcy;","Л");
			text = text.replaceAll("&Mcy;","М");
			text = text.replaceAll("&Ncy;","Н");
			text = text.replaceAll("&Ocy;","О");
			text = text.replaceAll("&Pcy;","П");
			text = text.replaceAll("&Rcy;","Р");
			text = text.replaceAll("&Scy;","С");
			text = text.replaceAll("&Tcy;","Т");
			text = text.replaceAll("&Ucy;","У");
			text = text.replaceAll("&Fcy;","Ф");
			text = text.replaceAll("&KHcy;","Х");
			text = text.replaceAll("&TScy;","Ц");
			text = text.replaceAll("&CHcy;","Ч");
			text = text.replaceAll("&SHcy;","Ш");
			text = text.replaceAll("&SHCHcy;","Щ");
			text = text.replaceAll("&HARDcy;","Ъ");
			text = text.replaceAll("&Ycy;","Ы");
			text = text.replaceAll("&SOFTcy;","Ь");
			text = text.replaceAll("&Ecy;","Э");
			text = text.replaceAll("&YUcy;","Ю");
			text = text.replaceAll("&YAcy;","Я");
			text = text.replaceAll("&acy;","а");
			text = text.replaceAll("&bcy;","б");
			text = text.replaceAll("&vcy;","в");
			text = text.replaceAll("&gcy;","г");
			text = text.replaceAll("&dcy;","д");
			text = text.replaceAll("&iecy;","е");
			text = text.replaceAll("&zhcy;","ж");
			text = text.replaceAll("&zcy;","з");
			text = text.replaceAll("&icy;","и");
			text = text.replaceAll("&jcy;","й");
			text = text.replaceAll("&kcy;","к");
			text = text.replaceAll("&lcy;","л");
			text = text.replaceAll("&mcy;","м");
			text = text.replaceAll("&ncy;","н");
			text = text.replaceAll("&ocy;","о");
			text = text.replaceAll("&pcy;","п");
			text = text.replaceAll("&rcy;","р");
			text = text.replaceAll("&scy;","с");
			text = text.replaceAll("&tcy;","т");
			text = text.replaceAll("&ucy;","у");
			text = text.replaceAll("&fcy;","ф");
			text = text.replaceAll("&khcy;","х");
			text = text.replaceAll("&tscy;","ц");
			text = text.replaceAll("&chcy;","ч");
			text = text.replaceAll("&shcy;","ш");
			text = text.replaceAll("&shchcy;","щ");
			text = text.replaceAll("&hardcy;","ъ");
			text = text.replaceAll("&ycy;","ы");
			text = text.replaceAll("&softcy;","ь");
			text = text.replaceAll("&ecy;","э");
			text = text.replaceAll("&yucy;","ю");
			text = text.replaceAll("&yacy;","я");
			text = text.replaceAll("&iocy;","ё");
			text = text.replaceAll("&djcy;","ђ");
			text = text.replaceAll("&gjcy;","ѓ");
			text = text.replaceAll("&jukcy;","є");
			text = text.replaceAll("&dscy;","ѕ");
			text = text.replaceAll("&iukcy;","і");
			text = text.replaceAll("&yicy;","ї");
			text = text.replaceAll("&jsercy;","ј");
			text = text.replaceAll("&ljcy;","љ");
			text = text.replaceAll("&njcy;","њ");
			text = text.replaceAll("&tshcy;","ћ");
			text = text.replaceAll("&kjcy;","ќ");
			text = text.replaceAll("&ubrcy;","ў");
			text = text.replaceAll("&dzcy;","џ");
			text = text.replaceAll("&aleph;","ℵ");
			text = text.replaceAll("&ensp;"," ");
			text = text.replaceAll("&emsp;"," ");
			text = text.replaceAll("&thinsp;"," ");
			text = text.replaceAll("&zwnj;","‌");
			text = text.replaceAll("&zwj;","‍");
			text = text.replaceAll("&lrm;","‎");
			text = text.replaceAll("&rlm;","‏");
			text = text.replaceAll("&ndash;","–");
			text = text.replaceAll("&mdash;","—");
			text = text.replaceAll("&lsquo;","‘");
			text = text.replaceAll("&rsquo;","’");
			text = text.replaceAll("&sbquo;","‚");
			text = text.replaceAll("&ldquo;","“");
			text = text.replaceAll("&rdquo;","”");
			text = text.replaceAll("&bdquo;","„");
			text = text.replaceAll("&dagger;","†");
			text = text.replaceAll("&Dagger;","‡");
			text = text.replaceAll("&permil;","‰");
			text = text.replaceAll("&lsaquo;","‹");
			text = text.replaceAll("&rsaquo;","›");
			text = text.replaceAll("&bull;","•");
			text = text.replaceAll("&hellip;","…");
			text = text.replaceAll("&permil;","‰");
			text = text.replaceAll("&prime;","′");
			text = text.replaceAll("&Prime;","″");
			text = text.replaceAll("&tprime;","‴");
			text = text.replaceAll("&rsaquo;","›");
			text = text.replaceAll("&oline;","‾");
			text = text.replaceAll("&Hscr;","ℋ");
			text = text.replaceAll("&frasl;","⁄");
			text = text.replaceAll("&plankv;","ℏ");
			text = text.replaceAll("&euro;","€");
			text = text.replaceAll("&image;","ℑ");
			text = text.replaceAll("&Lscr;","ℒ");
			text = text.replaceAll("&ell;","ℓ");
			text = text.replaceAll("&weierp;","℘");
			text = text.replaceAll("&Rscr;","ℛ");
			text = text.replaceAll("&real;","ℜ");
			text = text.replaceAll("&trade;","™");
			text = text.replaceAll("&mho;","℧");
			text = text.replaceAll("&Bscr;","ℬ");
			text = text.replaceAll("&Escr;","ℰ");
			text = text.replaceAll("&Fscr;","ℱ");
			text = text.replaceAll("&Mscr;","ℳ");
			text = text.replaceAll("&alefsym;","ℵ");
			text = text.replaceAll("&larr;","←");
			text = text.replaceAll("&uarr;","↑");
			text = text.replaceAll("&rarr;","→");
			text = text.replaceAll("&darr;","↓");
			text = text.replaceAll("&harr;","↔");
			text = text.replaceAll("&varr;","↕");
			text = text.replaceAll("&nwarr;","↖");
			text = text.replaceAll("&nearr;","↗");
			text = text.replaceAll("&searr;","↘");
			text = text.replaceAll("&swarr;","↙");
			text = text.replaceAll("&Larr;","↞");
			text = text.replaceAll("&Rarr;","↠");
			text = text.replaceAll("&crarr;","↵");
			text = text.replaceAll("&lharu;","↼");
			text = text.replaceAll("&lhard;","↽");
			text = text.replaceAll("&rharu;","⇀");
			text = text.replaceAll("&rhard;","⇁");
			text = text.replaceAll("&lArr;","⇐");
			text = text.replaceAll("&uArr;","⇑");
			text = text.replaceAll("&rArr;","⇒");
			text = text.replaceAll("&dArr;","⇓");
			text = text.replaceAll("&hArr;","⇔");
			text = text.replaceAll("&part;","∂");
			text = text.replaceAll("&exist;","∃");
			text = text.replaceAll("&nexist;","∄");
			text = text.replaceAll("&empty;","∅");
			text = text.replaceAll("&nabla;","∇");
			text = text.replaceAll("&isin;","∈");
			text = text.replaceAll("&notin;","∉");
			text = text.replaceAll("&ni;","∋");
			text = text.replaceAll("&notni;","∌");
			text = text.replaceAll("&prod;","∏");
			text = text.replaceAll("&coprod;","∐");
			text = text.replaceAll("&sum;","∑");
			text = text.replaceAll("&minus;","−");
			text = text.replaceAll("&mnplus;","∓");
			text = text.replaceAll("&lowast;","∗");
			text = text.replaceAll("&radic;","√");
			text = text.replaceAll("&prop;","∝");
			text = text.replaceAll("&infin;","∞");
			text = text.replaceAll("&ang;","∠");
			text = text.replaceAll("&par;","∥");
			text = text.replaceAll("&npar;","∦");
			text = text.replaceAll("&and;","∧");
			text = text.replaceAll("&or;","∨");
			text = text.replaceAll("&cap;","∩");
			text = text.replaceAll("&cup;","∪");
			text = text.replaceAll("&int;","∫");
			text = text.replaceAll("&conint;","∮");
			text = text.replaceAll("&there4;","∴");
			text = text.replaceAll("&sim;","∼");
			text = text.replaceAll("&nsim;","≁");
			text = text.replaceAll("&sime;","≃");
			text = text.replaceAll("&cong;","≅");
			text = text.replaceAll("&asymp;","≈");
			text = text.replaceAll("&nap;","≉");
			text = text.replaceAll("&ape;","≊");
			text = text.replaceAll("&efDot;","≒");
			text = text.replaceAll("&erDot;","≓");
			text = text.replaceAll("&wedgeq;","≙");
			text = text.replaceAll("&ne;","≠");
			text = text.replaceAll("&equiv;","≡");
			text = text.replaceAll("&nequiv;","≢");
			text = text.replaceAll("&le;","≤");
			text = text.replaceAll("&ge;","≥");
			text = text.replaceAll("&lE;","≦");
			text = text.replaceAll("&gE;","≧");
			text = text.replaceAll("&Lt;","≪");
			text = text.replaceAll("&Gt;","≫");
			text = text.replaceAll("&nlt;","≮");
			text = text.replaceAll("&ngt;","≯");
			text = text.replaceAll("&nle;","≰");
			text = text.replaceAll("&nge;","≱");
			text = text.replaceAll("&lsim;","≲");
			text = text.replaceAll("&gsim;","≳");
			text = text.replaceAll("&pr;","≺");
			text = text.replaceAll("&sc;","≻");
			text = text.replaceAll("&sub;","⊂");
			text = text.replaceAll("&sup;","⊃");
			text = text.replaceAll("&nsub;","⊄");
			text = text.replaceAll("&nsup;","⊅");
			text = text.replaceAll("&sube;","⊆");
			text = text.replaceAll("&supe;","⊇");
			text = text.replaceAll("&nsube;","⊈");
			text = text.replaceAll("&nsupe;","⊉");
			text = text.replaceAll("&oplus;","⊕");
			text = text.replaceAll("&ominus;","⊖");
			text = text.replaceAll("&otimes;","⊗");
			text = text.replaceAll("&odot;","⊙");
			text = text.replaceAll("&vdash;","⊢");
			text = text.replaceAll("&dashv;","⊣");
			text = text.replaceAll("&top;","⊤");
			text = text.replaceAll("&perp;","⊥");
			text = text.replaceAll("&or;","∨");
			text = text.replaceAll("&ltrie;","⊴");
			text = text.replaceAll("&rtrie;","⊵");
			text = text.replaceAll("&sdot;","⋅");
			text = text.replaceAll("&ltimes;","⋉");
			text = text.replaceAll("&rtimes;","⋊");
			text = text.replaceAll("&Ll;","⋘");
			text = text.replaceAll("&Gg;","⋙");
			text = text.replaceAll("&vellip;","⋮");
			text = text.replaceAll("&lceil;","⌈");
			text = text.replaceAll("&rceil;","⌉");
			text = text.replaceAll("&lfloor;","⌊");
			text = text.replaceAll("&rfloor;","⌋");
			text = text.replaceAll("&lang;","⟨");
			text = text.replaceAll("&rang;","⟩");
			text = text.replaceAll("&squf;","▪");
			text = text.replaceAll("&squ;","□");
			text = text.replaceAll("&utrif;","▴");
			text = text.replaceAll("&utri;","▵");
			text = text.replaceAll("&rtrif;","▸");
			text = text.replaceAll("&rtri;","▹");
			text = text.replaceAll("&dtrif;","▾");
			text = text.replaceAll("&dtri;","▿");
			text = text.replaceAll("&ltrif;","◂");
			text = text.replaceAll("&ltri;","◃");
			text = text.replaceAll("&diams;","♦");
			text = text.replaceAll("&diam;","⋄");
			text = text.replaceAll("&loz;","◊");
			text = text.replaceAll("&cir;","○");
			text = text.replaceAll("&female;","♀");
			text = text.replaceAll("&male;","♂");
			text = text.replaceAll("&spades;","♠");
			text = text.replaceAll("&clubs;","♣");
			text = text.replaceAll("&hearts;","♥");
			text = text.replaceAll("&diams;","♦");
			text = text.replaceAll("&LessLess;","⪡");
			text = text.replaceAll("&GreaterGreater;","⪢");
			text = text.replaceAll("&lang;","(");
			text = text.replaceAll("&rang;",")");
			text = text.replaceAll("&mellip;","⋯");
			text = text.replaceAll("&dollar;", "\\$");
			text = text.replaceAll("&rarr;","≤");
			text = text.replaceAll("&asyum;","≈");
			text = text.replaceAll("&lrarr2;","⇆");
			text = text.replaceAll("&rlhar2","⇌");
			text = text.replaceAll("&uyuml;","ÿ");
			text = text.replaceAll("&Lstrok;","Ł");
			text = text.replaceAll("&lstrok;","ł");
			text = text.replaceAll("&Ocire;","Ô");
			text = text.replaceAll("&ocire;","ô");
			text = text.replaceAll("&acire;","â");
			text = text.replaceAll("&Acire;","Â");
			text = text.replaceAll("&Ecire;","Ê");
			text = text.replaceAll("&ecire;","ê");
			text = text.replaceAll("&Icire;","Î");
			text = text.replaceAll("&icire;","î");
			text = text.replaceAll("&lrhar2;","⇋");	
			text = text.replaceAll("&rlarr2;","⇄");		
			
		//****************************************************//
		//try to fixing malformed html entity
			
			text = text.replaceAll("&amp","&");			
			text = text.replaceAll("&rlarr","⇄");
			text = text.replaceAll("&cire","≗");	
			text = text.replaceAll("&thetasym","ϑ");	
			text = text.replaceAll("&quot","\"");
			text = text.replaceAll("&lt","<");
			text = text.replaceAll("&gt",">");
			text = text.replaceAll("&Ccedil","Ç");
			text = text.replaceAll("&uuml","ü");
			text = text.replaceAll("&eacute","é");
			text = text.replaceAll("&acirc","â");
			text = text.replaceAll("&auml","ä");
			text = text.replaceAll("&agrave","à");
			text = text.replaceAll("&aring","å");
			text = text.replaceAll("&ccedil","ç");
			text = text.replaceAll("&ecirc","ê");
			text = text.replaceAll("&euml","ë");
			text = text.replaceAll("&egrave","è");
			text = text.replaceAll("&iuml","ï");
			text = text.replaceAll("&icirc","î");
			text = text.replaceAll("&igrave","ì");
			text = text.replaceAll("&Auml","Ä");
			text = text.replaceAll("&Aring","Å");
			text = text.replaceAll("&Eacute","É");
			text = text.replaceAll("&aelig","æ");
			text = text.replaceAll("&AElig","Æ");
			text = text.replaceAll("&ocirc","ô");
			text = text.replaceAll("&ouml","ö");
			text = text.replaceAll("&ograve","ò");
			text = text.replaceAll("&ucirc","û");
			text = text.replaceAll("&ugrave","ù");
			text = text.replaceAll("&yuml","ÿ");
			text = text.replaceAll("&Ouml","Ö");
			text = text.replaceAll("&Uuml","Ü");
			text = text.replaceAll("&pound","£");
			text = text.replaceAll("&yen","¥");
			text = text.replaceAll("&nbsp"," ");
			text = text.replaceAll("&iexcl","¡");
			text = text.replaceAll("&cent","¢");
			text = text.replaceAll("&pound","£");
			text = text.replaceAll("&curren","¤");
			text = text.replaceAll("&yen","¥");
			text = text.replaceAll("&brvbar","¦");
			text = text.replaceAll("&sect","§");
			text = text.replaceAll("&uml","¨");
			text = text.replaceAll("&copy","©");
			text = text.replaceAll("&ordf","ª");
			text = text.replaceAll("&laquo","«");
			text = text.replaceAll("&not","¬");
			text = text.replaceAll("&shy","­");
			text = text.replaceAll("&reg","®");
			text = text.replaceAll("&macr","¯");
			text = text.replaceAll("&deg","°");
			text = text.replaceAll("&plusmn","±");
			text = text.replaceAll("&sup2","²");
			text = text.replaceAll("&sup3","³");
			text = text.replaceAll("&acute","´");
			text = text.replaceAll("&micro","µ");
			text = text.replaceAll("&para","¶");
			text = text.replaceAll("&middot","·");
			text = text.replaceAll("&cedil","¸");
			text = text.replaceAll("&sup1","¹");
			text = text.replaceAll("&ordm","º");
			text = text.replaceAll("&raquo","»");
			text = text.replaceAll("&frac14","¼");
			text = text.replaceAll("&frac12","½");
			text = text.replaceAll("&frac34","¾");
			text = text.replaceAll("&iquest","¿");
			text = text.replaceAll("&Agrave","À");
			text = text.replaceAll("&Aacute","Á");
			text = text.replaceAll("&Acirc","Â");
			text = text.replaceAll("&Atilde","Ã");
			text = text.replaceAll("&Auml","Ä");
			text = text.replaceAll("&Aring","Å");
			text = text.replaceAll("&AElig","Æ");
			text = text.replaceAll("&Ccedil","Ç");
			text = text.replaceAll("&Egrave","È");
			text = text.replaceAll("&Eacute","É");
			text = text.replaceAll("&Ecirc","Ê");
			text = text.replaceAll("&Euml","Ë");
			text = text.replaceAll("&Igrave","Ì");
			text = text.replaceAll("&Iacute","Í");
			text = text.replaceAll("&Icirc","Î");
			text = text.replaceAll("&Iuml","Ï");
			text = text.replaceAll("&ETH","Ð");
			text = text.replaceAll("&Ntilde","Ñ");
			text = text.replaceAll("&Ograve","Ò");
			text = text.replaceAll("&Oacute","Ó");
			text = text.replaceAll("&Ocirc","Ô");
			text = text.replaceAll("&Otilde","Õ");
			text = text.replaceAll("&Ouml","Ö");
			text = text.replaceAll("&times","×");
			text = text.replaceAll("&Oslash","Ø");
			text = text.replaceAll("&Ugrave","Ù");
			text = text.replaceAll("&Uacute","Ú");
			text = text.replaceAll("&Ucirc","Û");
			text = text.replaceAll("&Uuml","Ü");
			text = text.replaceAll("&Yacute","Ý");
			text = text.replaceAll("&THORN","Þ");
			text = text.replaceAll("&szlig","ß");
			text = text.replaceAll("&agrave","à");
			text = text.replaceAll("&aacute","á");
			text = text.replaceAll("&acirc","â");
			text = text.replaceAll("&atilde","ã");
			text = text.replaceAll("&auml","ä");
			text = text.replaceAll("&aring","å");
			text = text.replaceAll("&aelig","æ");
			text = text.replaceAll("&ccedil","ç");
			text = text.replaceAll("&egrave","è");
			text = text.replaceAll("&eacute","é");
			text = text.replaceAll("&ecirc","ê");
			text = text.replaceAll("&euml","ë");
			text = text.replaceAll("&igrave","ì");
			text = text.replaceAll("&iacute","í");
			text = text.replaceAll("&icirc","î");
			text = text.replaceAll("&iuml","ï");
			text = text.replaceAll("&eth","ð");
			text = text.replaceAll("&ntilde","ñ");
			text = text.replaceAll("&ograve","ò");
			text = text.replaceAll("&oacute","ó");
			text = text.replaceAll("&ocirc","ô");
			text = text.replaceAll("&otilde","õ");
			text = text.replaceAll("&ouml","ö");
			text = text.replaceAll("&divide","÷");
			text = text.replaceAll("&oslash","ø");
			text = text.replaceAll("&ugrave","ù");
			text = text.replaceAll("&uacute","ú");
			text = text.replaceAll("&ucirc","û");
			text = text.replaceAll("&uuml","ü");
			text = text.replaceAll("&yacute","ý");
			text = text.replaceAll("&thorn","þ");
			text = text.replaceAll("&yuml","ÿ");
			text = text.replaceAll("&Lstrok","Ł");
			text = text.replaceAll("&lstrok","ł");
			text = text.replaceAll("&OElig","Œ");
			text = text.replaceAll("&oelig","œ");
			text = text.replaceAll("&Scaron","Š");
			text = text.replaceAll("&scaron","š");
			text = text.replaceAll("&Yuml","Ÿ");
			text = text.replaceAll("&fnof","ƒ");
			text = text.replaceAll("&circ","ˆ");
			text = text.replaceAll("&tilde","˜");
			text = text.replaceAll("&grave","`");
			text = text.replaceAll("&acute","´");
			text = text.replaceAll("&circ","ˆ");
			text = text.replaceAll("&macr","¯");
			text = text.replaceAll("&dot","˙");
			text = text.replaceAll("&die","¨");
			text = text.replaceAll("&ring","˚");
			text = text.replaceAll("&caron","ˇ");
			text = text.replaceAll("&cedil","¸");
			text = text.replaceAll("&Alpha","Α");
			text = text.replaceAll("&Beta","Β");
			text = text.replaceAll("&Gamma","Γ");
			text = text.replaceAll("&Delta","Δ");
			text = text.replaceAll("&Epsilon","Ε");
			text = text.replaceAll("&Zeta","Ζ");
			text = text.replaceAll("&Eta","Η");
			text = text.replaceAll("&Theta","Θ");
			text = text.replaceAll("&Iota","Ι");
			text = text.replaceAll("&Kappa","Κ");
			text = text.replaceAll("&Lambda","Λ");
			text = text.replaceAll("&Mu","Μ");
			text = text.replaceAll("&Nu","Ν");
			text = text.replaceAll("&Xi","Ξ");
			text = text.replaceAll("&Omicron","Ο");
			text = text.replaceAll("&Pi","Π");
			text = text.replaceAll("&Rho","Ρ");
			text = text.replaceAll("&Sigma","Σ");
			text = text.replaceAll("&Tau","Τ");
			text = text.replaceAll("&Upsi","ϒ");
			text = text.replaceAll("&Phi","Φ");
			text = text.replaceAll("&Chi","Χ");
			text = text.replaceAll("&Psi","Ψ");
			text = text.replaceAll("&Omega","Ω");
			text = text.replaceAll("&alpha","α");
			text = text.replaceAll("&beta","β");
			text = text.replaceAll("&gamma","γ");
			text = text.replaceAll("&delta","δ");
			text = text.replaceAll("&epsi","ε");
			text = text.replaceAll("&zeta","ζ");
			text = text.replaceAll("&eta","η");
			text = text.replaceAll("&theta","θ");
			text = text.replaceAll("&iota","ι");
			text = text.replaceAll("&kappa","κ");
			text = text.replaceAll("&lambda","λ");
			text = text.replaceAll("&mu","μ");
			text = text.replaceAll("&nu","ν");
			text = text.replaceAll("&xi","ξ");
			text = text.replaceAll("&omicron","ο");
			text = text.replaceAll("&pi","π");
			text = text.replaceAll("&rho","ρ");
			text = text.replaceAll("&sigmaf","ς");
			text = text.replaceAll("&sigma","σ");
			text = text.replaceAll("&tau","τ");
			text = text.replaceAll("&upsi","υ");
			text = text.replaceAll("&phi","φ");
			text = text.replaceAll("&chi","χ");
			text = text.replaceAll("&psi","ψ");
			text = text.replaceAll("&omega","ω");
			text = text.replaceAll("&theta","θ");
			text = text.replaceAll("&upsih","ϒ");
			text = text.replaceAll("&straightphi","ϕ");
			text = text.replaceAll("&piv","ϖ");
			text = text.replaceAll("&Gammad","Ϝ");
			text = text.replaceAll("&gammad","ϝ");
			text = text.replaceAll("&IOcy","Ё");
			text = text.replaceAll("&DJcy","Ђ");
			text = text.replaceAll("&GJcy","Ѓ");
			text = text.replaceAll("&Jukcy","Є");
			text = text.replaceAll("&DScy","Ѕ");
			text = text.replaceAll("&Iukcy","І");
			text = text.replaceAll("&YIcy","Ї");
			text = text.replaceAll("&Jsercy","Ј");
			text = text.replaceAll("&LJcy","Љ");
			text = text.replaceAll("&NJcy","Њ");
			text = text.replaceAll("&TSHcy","Ћ");
			text = text.replaceAll("&KJcy","Ќ");
			text = text.replaceAll("&Ubrcy","Ў");
			text = text.replaceAll("&DZcy","Џ");
			text = text.replaceAll("&Acy","А");
			text = text.replaceAll("&Bcy","Б");
			text = text.replaceAll("&Vcy","В");
			text = text.replaceAll("&Gcy","Г");
			text = text.replaceAll("&Dcy","Д");
			text = text.replaceAll("&IEcy","Е");
			text = text.replaceAll("&ZHcy","Ж");
			text = text.replaceAll("&Zcy","З");
			text = text.replaceAll("&Icy","И");
			text = text.replaceAll("&Jcy","Й");
			text = text.replaceAll("&Kcy","К");
			text = text.replaceAll("&Lcy","Л");
			text = text.replaceAll("&Mcy","М");
			text = text.replaceAll("&Ncy","Н");
			text = text.replaceAll("&Ocy","О");
			text = text.replaceAll("&Pcy","П");
			text = text.replaceAll("&Rcy","Р");
			text = text.replaceAll("&Scy","С");
			text = text.replaceAll("&Tcy","Т");
			text = text.replaceAll("&Ucy","У");
			text = text.replaceAll("&Fcy","Ф");
			text = text.replaceAll("&KHcy","Х");
			text = text.replaceAll("&TScy","Ц");
			text = text.replaceAll("&CHcy","Ч");
			text = text.replaceAll("&SHcy","Ш");
			text = text.replaceAll("&SHCHcy","Щ");
			text = text.replaceAll("&HARDcy","Ъ");
			text = text.replaceAll("&Ycy","Ы");
			text = text.replaceAll("&SOFTcy","Ь");
			text = text.replaceAll("&Ecy","Э");
			text = text.replaceAll("&YUcy","Ю");
			text = text.replaceAll("&YAcy","Я");
			text = text.replaceAll("&acy","а");
			text = text.replaceAll("&bcy","б");
			text = text.replaceAll("&vcy","в");
			text = text.replaceAll("&gcy","г");
			text = text.replaceAll("&dcy","д");
			text = text.replaceAll("&iecy","е");
			text = text.replaceAll("&zhcy","ж");
			text = text.replaceAll("&zcy","з");
			text = text.replaceAll("&icy","и");
			text = text.replaceAll("&jcy","й");
			text = text.replaceAll("&kcy","к");
			text = text.replaceAll("&lcy","л");
			text = text.replaceAll("&mcy","м");
			text = text.replaceAll("&ncy","н");
			text = text.replaceAll("&ocy","о");
			text = text.replaceAll("&pcy","п");
			text = text.replaceAll("&rcy","р");
			text = text.replaceAll("&scy","с");
			text = text.replaceAll("&tcy","т");
			text = text.replaceAll("&ucy","у");
			text = text.replaceAll("&fcy","ф");
			text = text.replaceAll("&khcy","х");
			text = text.replaceAll("&tscy","ц");
			text = text.replaceAll("&chcy","ч");
			text = text.replaceAll("&shcy","ш");
			text = text.replaceAll("&shchcy","щ");
			text = text.replaceAll("&hardcy","ъ");
			text = text.replaceAll("&ycy","ы");
			text = text.replaceAll("&softcy","ь");
			text = text.replaceAll("&ecy","э");
			text = text.replaceAll("&yucy","ю");
			text = text.replaceAll("&yacy","я");
			text = text.replaceAll("&iocy","ё");
			text = text.replaceAll("&djcy","ђ");
			text = text.replaceAll("&gjcy","ѓ");
			text = text.replaceAll("&jukcy","є");
			text = text.replaceAll("&dscy","ѕ");
			text = text.replaceAll("&iukcy","і");
			text = text.replaceAll("&yicy","ї");
			text = text.replaceAll("&jsercy","ј");
			text = text.replaceAll("&ljcy","љ");
			text = text.replaceAll("&njcy","њ");
			text = text.replaceAll("&tshcy","ћ");
			text = text.replaceAll("&kjcy","ќ");
			text = text.replaceAll("&ubrcy","ў");
			text = text.replaceAll("&dzcy","џ");
			text = text.replaceAll("&aleph","ℵ");
			text = text.replaceAll("&ensp"," ");
			text = text.replaceAll("&emsp"," ");
			text = text.replaceAll("&thinsp"," ");
			text = text.replaceAll("&zwnj","‌");
			text = text.replaceAll("&zwj","‍");
			text = text.replaceAll("&lrm","‎");
			text = text.replaceAll("&rlm","‏");
			text = text.replaceAll("&ndash","–");
			text = text.replaceAll("&mdash","—");
			text = text.replaceAll("&lsquo","‘");
			text = text.replaceAll("&rsquo","’");
			text = text.replaceAll("&sbquo","‚");
			text = text.replaceAll("&ldquo","“");
			text = text.replaceAll("&rdquo","”");
			text = text.replaceAll("&bdquo","„");
			text = text.replaceAll("&dagger","†");
			text = text.replaceAll("&Dagger","‡");
			text = text.replaceAll("&permil","‰");
			text = text.replaceAll("&lsaquo","‹");
			text = text.replaceAll("&rsaquo","›");
			text = text.replaceAll("&bull","•");
			text = text.replaceAll("&hellip","…");
			text = text.replaceAll("&permil","‰");
			text = text.replaceAll("&prime","′");
			text = text.replaceAll("&Prime","″");
			text = text.replaceAll("&tprime","‴");
			text = text.replaceAll("&rsaquo","›");
			text = text.replaceAll("&oline","‾");
			text = text.replaceAll("&Hscr","ℋ");
			text = text.replaceAll("&frasl","⁄");
			text = text.replaceAll("&plankv","ℏ");
			text = text.replaceAll("&euro","€");
			text = text.replaceAll("&image","ℑ");
			text = text.replaceAll("&Lscr","ℒ");
			text = text.replaceAll("&ell","ℓ");
			text = text.replaceAll("&weierp","℘");
			text = text.replaceAll("&Rscr","ℛ");
			text = text.replaceAll("&real","ℜ");
			text = text.replaceAll("&trade","™");
			text = text.replaceAll("&mho","℧");
			text = text.replaceAll("&Bscr","ℬ");
			text = text.replaceAll("&Escr","ℰ");
			text = text.replaceAll("&Fscr","ℱ");
			text = text.replaceAll("&Mscr","ℳ");
			text = text.replaceAll("&alefsym","ℵ");
			text = text.replaceAll("&larr","←");
			text = text.replaceAll("&uarr","↑");
			text = text.replaceAll("&rarr","→");
			text = text.replaceAll("&darr","↓");
			text = text.replaceAll("&harr","↔");
			text = text.replaceAll("&varr","↕");
			text = text.replaceAll("&nwarr","↖");
			text = text.replaceAll("&nearr","↗");
			text = text.replaceAll("&searr","↘");
			text = text.replaceAll("&swarr","↙");
			text = text.replaceAll("&Larr","↞");
			text = text.replaceAll("&Rarr","↠");
			text = text.replaceAll("&crarr","↵");
			text = text.replaceAll("&lharu","↼");
			text = text.replaceAll("&lhard","↽");
			text = text.replaceAll("&rharu","⇀");
			text = text.replaceAll("&rhard","⇁");
			text = text.replaceAll("&lArr","⇐");
			text = text.replaceAll("&uArr","⇑");
			text = text.replaceAll("&rArr","⇒");
			text = text.replaceAll("&dArr","⇓");
			text = text.replaceAll("&hArr","⇔");
			text = text.replaceAll("&part","∂");
			text = text.replaceAll("&exist","∃");
			text = text.replaceAll("&nexist","∄");
			text = text.replaceAll("&empty","∅");
			text = text.replaceAll("&nabla","∇");
			text = text.replaceAll("&isin","∈");
			text = text.replaceAll("&notin","∉");
			text = text.replaceAll("&ni","∋");
			text = text.replaceAll("&notni","∌");
			text = text.replaceAll("&prod","∏");
			text = text.replaceAll("&coprod","∐");
			text = text.replaceAll("&sum","∑");
			text = text.replaceAll("&minus","−");
			text = text.replaceAll("&mnplus","∓");
			text = text.replaceAll("&lowast","∗");
			text = text.replaceAll("&radic","√");
			text = text.replaceAll("&prop","∝");
			text = text.replaceAll("&infin","∞");
			text = text.replaceAll("&ang","∠");
			text = text.replaceAll("&par","∥");
			text = text.replaceAll("&npar","∦");
			text = text.replaceAll("&and","∧");
			text = text.replaceAll("&or","∨");
			text = text.replaceAll("&cap","∩");
			text = text.replaceAll("&cup","∪");
			text = text.replaceAll("&int","∫");
			text = text.replaceAll("&conint","∮");
			text = text.replaceAll("&there4","∴");
			text = text.replaceAll("&sim","∼");
			text = text.replaceAll("&nsim","≁");
			text = text.replaceAll("&sime","≃");
			text = text.replaceAll("&cong","≅");
			text = text.replaceAll("&asymp","≈");
			text = text.replaceAll("&nap","≉");
			text = text.replaceAll("&ape","≊");
			text = text.replaceAll("&efDot","≒");
			text = text.replaceAll("&erDot","≓");
			text = text.replaceAll("&wedgeq","≙");
			text = text.replaceAll("&ne","≠");
			text = text.replaceAll("&equiv","≡");
			text = text.replaceAll("&nequiv","≢");
			text = text.replaceAll("&le","≤");
			text = text.replaceAll("&ge","≥");
			text = text.replaceAll("&lE","≦");
			text = text.replaceAll("&gE","≧");
			text = text.replaceAll("&Lt","≪");
			text = text.replaceAll("&Gt","≫");
			text = text.replaceAll("&nlt","≮");
			text = text.replaceAll("&ngt","≯");
			text = text.replaceAll("&nle","≰");
			text = text.replaceAll("&nge","≱");
			text = text.replaceAll("&lsim","≲");
			text = text.replaceAll("&gsim","≳");
			text = text.replaceAll("&pr","≺");
			text = text.replaceAll("&sc","≻");
			text = text.replaceAll("&sub","⊂");
			text = text.replaceAll("&sup","⊃");
			text = text.replaceAll("&nsub","⊄");
			text = text.replaceAll("&nsup","⊅");
			text = text.replaceAll("&sube","⊆");
			text = text.replaceAll("&supe","⊇");
			text = text.replaceAll("&nsube","⊈");
			text = text.replaceAll("&nsupe","⊉");
			text = text.replaceAll("&oplus","⊕");
			text = text.replaceAll("&ominus","⊖");
			text = text.replaceAll("&otimes","⊗");
			text = text.replaceAll("&odot","⊙");
			text = text.replaceAll("&vdash","⊢");
			text = text.replaceAll("&dashv","⊣");
			text = text.replaceAll("&top","⊤");
			text = text.replaceAll("&perp","⊥");
			text = text.replaceAll("&or","∨");
			text = text.replaceAll("&ltrie","⊴");
			text = text.replaceAll("&rtrie","⊵");
			text = text.replaceAll("&sdot","⋅");
			text = text.replaceAll("&ltimes","⋉");
			text = text.replaceAll("&rtimes","⋊");
			text = text.replaceAll("&Ll","⋘");
			text = text.replaceAll("&Gg","⋙");
			text = text.replaceAll("&vellip","⋮");
			text = text.replaceAll("&lceil","⌈");
			text = text.replaceAll("&rceil","⌉");
			text = text.replaceAll("&lfloor","⌊");
			text = text.replaceAll("&rfloor","⌋");
			text = text.replaceAll("&lang","⟨");
			text = text.replaceAll("&rang","⟩");
			text = text.replaceAll("&squf","▪");
			text = text.replaceAll("&squ","□");
			text = text.replaceAll("&utrif","▴");
			text = text.replaceAll("&utri","▵");
			text = text.replaceAll("&rtrif","▸");
			text = text.replaceAll("&rtri","▹");
			text = text.replaceAll("&dtrif","▾");
			text = text.replaceAll("&dtri","▿");
			text = text.replaceAll("&ltrif","◂");
			text = text.replaceAll("&ltri","◃");
			text = text.replaceAll("&diams","♦");
			text = text.replaceAll("&diam","⋄");
			text = text.replaceAll("&loz","◊");
			text = text.replaceAll("&cir","○");
			text = text.replaceAll("&female","♀");
			text = text.replaceAll("&male","♂");
			text = text.replaceAll("&spades","♠");
			text = text.replaceAll("&clubs","♣");
			text = text.replaceAll("&hearts","♥");
			text = text.replaceAll("&diams","♦");
			text = text.replaceAll("&LessLess","⪡");
			text = text.replaceAll("&GreaterGreater","⪢");
			text = text.replaceAll("&lang","(");
			text = text.replaceAll("&rang",")");
			text = text.replaceAll("&mellip","⋯");
			text = text.replaceAll("&dollar", "\\$");
			text = text.replaceAll("&reg","®");	
			text = text.replaceAll("&le","≤");
			text = text.replaceAll("&rarr","≤");
			text = text.replaceAll("&asyum","≈");
			text = text.replaceAll("&lrarr2","⇆");
			text = text.replaceAll("&rlhar2","⇌");
			text = text.replaceAll("&uyuml","ÿ");
			text = text.replaceAll("&Lstrok","Ł");
			text = text.replaceAll("&lstrok","ł");
			text = text.replaceAll("&Ocire","Ô");
			text = text.replaceAll("&ocire","ô");
			text = text.replaceAll("&acire","â");
			text = text.replaceAll("&Acire","Â");
			text = text.replaceAll("&Ecire","Ê");
			text = text.replaceAll("&ecire","ê");
			text = text.replaceAll("&Icire","Î");
			text = text.replaceAll("&icire","î");
			text = text.replaceAll("&lrhar2","⇋");	
			text = text.replaceAll("&rlarr2","⇄");
		}
		return text;
	 }
	 
	 public String prepareBdAuthor(String bdAuthor) 
	 {
		if (bdAuthor != null && !bdAuthor.trim().equals("")) {
			BdAuthors aus = new BdAuthors(bdAuthor);
			String[] ausArray = c.removeSpace(c.reverseSigns(aus.getSearchValue()));
			return String.join(";", ausArray);
		}
		return null;
	}
	 
	 private String prepareConfLocation(String coAff) 
	 {
		if (coAff != null && !coAff.trim().equals("")) 
		{
			BdConfLocations aff = new BdConfLocations(coAff);
			return aff.getSearchValue();
		}
		return null;

	 }
	 
	 public String prepareCitationTitle(String citationTitle) throws Exception 
	 {
			StringBuffer sBuffer = new StringBuffer();
			if (citationTitle != null) 
			{
				BdCitationTitle ct = new BdCitationTitle(citationTitle);
				List ctList = ct.getCitationTitle();

				for (int i = 0; i < ctList.size(); i++) 
				{
					BdCitationTitle ctObject = (BdCitationTitle) ctList.get(i);

					if (ctObject.getTitle() != null) 
					{
						sBuffer.append(ctObject.getTitle());
					}
					if(i<ctList.size()-1)
					{
						sBuffer.append(";");
					}

				}
			}

			return sBuffer.toString();
		}

	 
	 private String prepareISBN(String isbnString) throws Exception 
	 {
		StringBuffer isbnBuffer = new StringBuffer();
		if (isbnString != null) 
		{
			BdIsbn isbn = new BdIsbn(isbnString);
			List isbnList = isbn.getISBN();
			for (int i = 0; i < isbnList.size(); i++) 
			{
				BdIsbn isbnObject = (BdIsbn) isbnList.get(i);

				if (isbnObject.getValue() != null) 
				{
					isbnBuffer.append(isbnObject.getValue());
				}
				if(i<isbnList.size()-1)
				{
					isbnBuffer.append(";");
				}
			}
		}
		return isbnBuffer.toString();
	}
	
	private void sendToAWSS3(String key_name)
	{
		AmazonS3 s3Client;
		S3Object object;
		try {
			s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
			String file_path = key_name;
			File file = new File(file_path);
			ObjectMetadata md;		
			md = new ObjectMetadata();
			md.setContentType("text/xml");	
			byte[] bytes;
			bytes = file_path.getBytes();
			md.setContentLength(bytes.length);
			bucketName=bucketName+"/weekly/"+this.database;
			System.out.println(bucketName+key_name);
			PutObjectResult response = s3Client.putObject(new PutObjectRequest(bucketName, key_name, new ByteArrayInputStream(file_path.getBytes()), md));	
			System.out.println("Key: " + key_name + " successfully uploaded to S3, Etag: " + response.getETag());
	        }catch(Exception e){
	        	System.out.println("Other Error Message: " + e.getMessage());
	        }
	}
}