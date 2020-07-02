package org.ei.dataloading.bd.loadtime;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.*;

import org.ei.common.bd.*;
import org.ei.common.Constants;



//import org.ei.data.LoadNumber;
//import org.ei.dataloading.bd.*;
import java.sql.*;

import org.ei.common.Constants;
import org.ei.common.bd.BdAuthors;
import org.ei.dataloading.DataLoadDictionary;
import org.ei.dataloading.cafe.GetANIFileFromCafeS3Bucket;
import org.ei.data.compendex.runtime.*;

public class BdReverseRecordBuilder
{
    private static BaseTableWriter baseWriter;
    private int loadNumber;
    private String databaseName;
    private String tableName = "bd_master";
    private static String startRootElement ="<?xml version=\"1.0\" encoding=\"UTF-8\" ?><bibdataset xsi:schemaLocation=\"http://www.elsevier.com/xml/ani/ani http://www.elsevier.com/xml/ani/ani512.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ait=\"http://www.elsevier.com/xml/ani/ait\" xmlns:ce=\"http://www.elsevier.com/xml/ani/common\" xmlns=\"http://www.elsevier.com/xml/ani/ani\">";        																								
    private static String endRootElement   ="</bibdataset>";
    private static Connection con;
    private static String infile;
    private static String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
    private static String driver = "oracle.jdbc.driver.OracleDriver";
    private static String username = "ba_loading";
    private static String password = "";
    private DataLoadDictionary dictionary = new DataLoadDictionary();

   
    public static void main(String args[])
        throws Exception

    {
        int loadN=0;
        long startTime = System.currentTimeMillis();
        if(args.length<7)
        {
            System.out.println("please enter three parameters as \" weeknumber filename databaseName action url driver username password\"");
            System.exit(1);
        }

        loadN = Integer.parseInt(args[0]);

        //infile = args[1];
        String databaseName = args[1];
        String tableName = args[2];
        if(args.length>3)
        {
            url = args[3];
            driver = args[4];
            username = args[5];
            password = args[6];
            //tableName = args[3];
        }
        else
        {
            System.out.println("USING DEFAULT DATABASE SETTING");
            System.out.println("DATABASE URL= "+url);
            System.out.println("DATABASE USERNAME= "+username);
            System.out.println("DATABASE PASSWORD= "+password);
        }
        BdReverseRecordBuilder c;

        try
        {

          
            c = new BdReverseRecordBuilder(loadN,databaseName);            
            con = c.getConnection(url,driver,username,password);
            c.writeFile(con,loadN,databaseName,tableName);
            
        }
        catch(Exception e)
        {
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
    
    public BdReverseRecordBuilder(int loadN,String databaseName)
    {
        this.loadNumber = loadN;
        this.databaseName = databaseName;
   
    }
    
    public void writeFile(Connection con,int loadN,String databaseName, String tableName)
            throws Exception
    {
    	StringBuffer initialString = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    	initialString.append("<bibdataset xsi:schemaLocation=\"http://www.elsevier.com/xml/ani/ani http://www.elsevier.com/xml/ani/ani512.xsd\" ");
    	initialString.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
    	initialString.append("xmlns:ait=\"http://www.elsevier.com/xml/ani/ait\" ");
    	initialString.append("xmlns:ce=\"http://www.elsevier.com/xml/ani/common\" ");
    	initialString.append("xmlns=\"http://www.elsevier.com/xml/ani/ani\">\n");
    	 Statement stmt = null;
         ResultSet rs = null;
         int count = 0;
         boolean checkResult = false;
         FileWriter file = null;
         try
         {
             stmt = con.createStatement();
             file = new FileWriter(databaseName+"_"+loadN+".xml");
             file.write(initialString.toString());
             rs = stmt.executeQuery("select *  from bd_master where loadnumber="+loadN+" and database='"+databaseName+"'");
             while (rs.next())
             {
                 writeRecord(rs,file);
             }
             file.write("</bibdataset>");
         }
         catch (IOException e)
         {
             System.err.println(e);
             System.exit(1);
         }
         finally
         {
             if(rs != null)
             {
                 rs.close();
             }
             
             if(file != null)
             {
            	 file.close();
             }
         }
    	
    }
  
    public void writeRecord(ResultSet rs, FileWriter file) throws Exception
    {
    	
    	Calendar calendar = Calendar.getInstance();
    	int year= calendar.get(Calendar.YEAR);
    	int month = calendar.get(Calendar.MONTH);
    	int day = calendar.get(Calendar.DATE);
    	String dateSort = rs.getString("DATESORT");
    	String accessnumber = rs.getString("ACCESSNUMBER");
    	String copyright = dictionary.AlphaEntitysToNumericEntitys(rs.getString("COPYRIGHT"));
    	String doi = rs.getString("DOI");
    	String pui = rs.getString("PUI");
    	String database = rs.getString("DATABASE");
    	String documentType = rs.getString("CITTYPE");
    	String citationLanguage = rs.getString("CITATIONLANGUAGE");
    	String citationTitle = dictionary.AlphaEntitysToNumericEntitys(rs.getString("CITATIONTITLE"));
    	//System.out.println("AUTHORKEYWORD1 "+rs.getString("AUTHORKEYWORDS"));
    	String authorKeyword = dictionary.AlphaEntitysToNumericEntitys(rs.getString("AUTHORKEYWORDS"));
    	//System.out.println("AUTHORKEYWORD1 "+authorKeyword);
    	String authorString = dictionary.AlphaEntitysToNumericEntitys(rs.getString("AUTHOR"));
    	String author1String = dictionary.AlphaEntitysToNumericEntitys(rs.getString("AUTHOR_1"));
    	String affiliationString = dictionary.AlphaEntitysToNumericEntitys(rs.getString("AFFILIATION"));
    	String affiliation1String = dictionary.AlphaEntitysToNumericEntitys(rs.getString("AFFILIATION_1"));
    	String correspondencename = dictionary.AlphaEntitysToNumericEntitys(rs.getString("CORRESPONDENCENAME"));
    	String correspondenceaffiliation = dictionary.AlphaEntitysToNumericEntitys(rs.getString("correspondenceaffiliation"));
    	String correspondenceeaddress = dictionary.AlphaEntitysToNumericEntitys(rs.getString("CORRESPONDENCEEADDRESS"));
    	String grantlist = dictionary.AlphaEntitysToNumericEntitys(rs.getString("GRANTLIST"));
    	String abstractdata = dictionary.AlphaEntitysToNumericEntitys(rs.getString("ABSTRACTDATA"));
    	String abstract_original = dictionary.AlphaEntitysToNumericEntitys(rs.getString("ABSTRACTORIGINAL"));
    	String abstract_perspective = dictionary.AlphaEntitysToNumericEntitys(rs.getString("ABSTRACTPERSPECTIVE"));
    	String sourcetype = rs.getString("SOURCETYPE");
    	String sourcecountry = dictionary.AlphaEntitysToNumericEntitys(rs.getString("SOURCECOUNTRY"));
    	String sourceid = rs.getString("SOURCEID");
    	String sourcetitle = dictionary.AlphaEntitysToNumericEntitys(rs.getString("SOURCETITLE"));
    	String sourcetitleabbrev = dictionary.AlphaEntitysToNumericEntitys(rs.getString("SOURCETITLEABBREV"));
    	String translatedsourcetitle = dictionary.AlphaEntitysToNumericEntitys(rs.getString("TRANSLATEDSOURCETITLE"));
    	String volumetitle = dictionary.AlphaEntitysToNumericEntitys(rs.getString("VOLUMETITLE"));
    	String issuetitle = dictionary.AlphaEntitysToNumericEntitys(rs.getString("ISSUETITLE"));
    	String issn = rs.getString("ISSN");
    	String eissn = rs.getString("EISSN");
    	String isbnString = rs.getString("ISBN");
    	String coden = rs.getString("CODEN");
    	String volume = rs.getString("VOLUME");
    	String issue = rs.getString("ISSUE");
    	String page = rs.getString("PAGE");
    	String pagecount = rs.getString("PAGECOUNT");
    	String articlenumber = rs.getString("ARTICLENUMBER");
    	String publicationyear = rs.getString("PUBLICATIONYEAR");
    	String publicationdate = rs.getString("PUBLICATIONDATE");
    	String sourcewebsite = dictionary.AlphaEntitysToNumericEntitys(rs.getString("SOURCEWEBSITE"));
    	String contributor = dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONTRIBUTOR"));
    	String contributoraffiliation = dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONTRIBUTORAFFILIATION"));
    	String editors = dictionary.AlphaEntitysToNumericEntitys(rs.getString("EDITORS"));
    	String publishername = dictionary.AlphaEntitysToNumericEntitys(rs.getString("PUBLISHERNAME"));
    	String publisheraddress = dictionary.AlphaEntitysToNumericEntitys(rs.getString("PUBLISHERADDRESS"));
    	String publisherelectronicaddress = dictionary.AlphaEntitysToNumericEntitys(rs.getString("PUBLISHERELECTRONICADDRESS"));
    	String reportnumber = dictionary.AlphaEntitysToNumericEntitys(rs.getString("REPORTNUMBER"));
    	String confname =  dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFNAME"));
    	String confcatnumber =  rs.getString("CONFCATNUMBER");
    	String confcode =  rs.getString("CONFCODE");
    	String conflocation =  dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFLOCATION"));
    	String confdate =  rs.getString("CONFDATE");
    	String confsponsors =  dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFSPONSORS"));
    	String confencepartnumber = rs.getString("CONFERENCEPARTNUMBER");
    	String confercepagerange = rs.getString("CONFERENCEPAGERANGE");
    	String confencepagecount = rs.getString("CONFERENCEPAGECOUNT");
    	String confenceeditor = dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFERENCEEDITOR"));
    	String conferenceorganization = dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFERENCEORGANIZATION"));
    	String conferenceeditoraddress = dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFERENCEEDITORADDRESS")); 	
    	String controlledterm = dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONTROLLEDTERM"));                  
    	String uncontrolledterm = dictionary.AlphaEntitysToNumericEntitys(rs.getString("UNCONTROLLEDTERM"));               
    	String mainheading = dictionary.AlphaEntitysToNumericEntitys(rs.getString("MAINHEADING"));                     
    	String speciesterm = dictionary.AlphaEntitysToNumericEntitys(rs.getString("SPECIESTERM"));                     
    	String regionalterm = dictionary.AlphaEntitysToNumericEntitys(rs.getString("REGIONALTERM"));                    
    	String treatmentcode = rs.getString("TREATMENTCODE");                   
    	String classificationcode = rs.getString("CLASSIFICATIONCODE");              
    	String refcount = rs.getString("REFCOUNT");                        
    	String manufacturer = dictionary.AlphaEntitysToNumericEntitys(rs.getString("MANUFACTURER"));
    	String tradename = dictionary.AlphaEntitysToNumericEntitys(rs.getString("TRADENAME"));
    	String sequencebank = dictionary.AlphaEntitysToNumericEntitys(rs.getString("SEQUENCEBANKS"));
    	String casregistrynumber = rs.getString("CASREGISTRYNUMBER");
    	String chemicalterm = dictionary.AlphaEntitysToNumericEntitys(rs.getString("CHEMICALTERM"));
    	String referenceflag = rs.getString("REFERENCE_FLAG");
 	
    	file.write("<item>\n");
    	file.write("<ait:process-info>\n");
    	file.write("<ait:date-delivered year=\""+year+"\" month=\""+month+"\" day=\""+day+"\"/>\n");
    	if(dateSort!=null)
    	{
    		String[] date = dateSort.split(Constants.IDDELIMITER);
    		if(date.length==3)
    		{
    			file.write("<ait:date-sort year=\""+date[0] +"\" month=\""+date[1]+ "\" day=\""+date[2]+"\"/>\n");
    		}
    		else
    		{
    			System.out.println("record "+accessnumber+" date-sort format is wrong");
    		}
    	}
    	file.write("<ait:status type=\"core\" state=\"new\" stage=\"S300\"/>\n");
    	file.write("</ait:process-info>\n");
    	file.write("<bibrecord>\n");
    	file.write("<item-info>\n");
    	
    	//COPYRIGHT
    	if(copyright!=null && copyright.length()>0)
    	{
    		file.write("<copyright type=\"Elsevier\">"+copyright.trim()+"</copyright>\n");
    	}
    	else
    	{
    		file.write("<copyright type=\"Elsevier\">Copyright 2016 Elsevier B.V., All rights reserved.</copyright>\n");
    	}
    	
    	file.write("<itemidlist>\n");
    	
    	//DOI
    	if(doi!=null && doi.length()>0)
    	{
    		doi = doi.replaceAll("<","&#60;").replaceAll(">", "&#62;").trim();
    		file.write("<ce:doi>"+doi+"</ce:doi>\n");
    		//file.write("<ce:doi><![CDATA["+doi+"]]></ce:doi>\n");
    	}

    	//PUI
    	if(pui!=null && pui.length()>0)
    	{
    		file.write("<itemid idtype=\"PUI\">"+pui.trim()+"</itemid>\n");
    	}
    	
    	//ACCESSNUMBER
    	if(accessnumber!=null && accessnumber.length()>0)
    	{
    		file.write("<itemid idtype=\"CHEM\">"+accessnumber+"</itemid>\n");
    	}
    	
    	file.write("</itemidlist>\n");
    	
    	//DATABASE
    	if(database.equals("chm"))
    	{
    		file.write("<dbcollection>CHEM</dbcollection>\n");
    	}
    	else if(database.equals("cpx") || database.equals("pch"))
    	{
    		file.write("<dbcollection>CPX</dbcollection>\n");
    	}
    	else if(database.equals("elt"))
    	{
    		file.write("<dbcollection>APILIT</dbcollection>\n");
    	}
    	else if(database.equals("geo"))
    	{
    		file.write("<dbcollection>GEO</dbcollection>\n");
    	}
    	
    	
    	file.write("</item-info>\n");
    	file.write("<head>\n");
    	file.write("<citation-info>\n");
    	
    	//CITTYPE
    	if(documentType!=null && documentType.length()>0)
    	{
    		file.write("<citation-type code=\""+documentType+"\"/>\n");
    	}

    	//CITATION-LANGUAGE
    	if(citationLanguage!=null && citationLanguage.length()>0)
    	{
    		file.write("<citation-language xml:lang=\""+citationLanguage+"\"/>\n");
    		file.write("<abstract-language xml:lang=\""+citationLanguage+"\"/>\n");//not capture by us
    	}
    	
    	//AUTHOR-KEYWORD
    	if(authorKeyword!=null && authorKeyword.length()>0)
    	{
    		String[] authorKeywords = authorKeyword.split(Constants.IDDELIMITER);
    		file.write("<author-keywords>\n");
    		for(int i=0; i<authorKeywords.length;i++)
    		{
    			String keyword = authorKeywords[i];
	    		//System.out.println("keyword "+keyword);	
    			if(keyword!=null && keyword.length()>0)
    			{
    				file.write("<author-keyword>"+keyword+"</author-keyword>\n");
    			}
    		}
    		file.write("</author-keywords>\n");
    		
    	}
    	
    	file.write("</citation-info>\n");
    	    	
    	//CITATIONTITLE
    	if(citationTitle!=null )
    	{
    		outputCitationTitle(file,citationTitle);    		
    	}
    	       	
		//AUTHOR and AFFILIATION
		if(author1String!=null)
		{
			authorString = authorString+author1String;
		}
		
		if(affiliation1String!=null)
		{
			affiliationString = affiliationString+affiliation1String;
		}
		outputAuthorGroup(file,authorString,affiliationString);
    	
    
    		
		//CORRESPONDENCEAFFILIATION
		if(correspondencename!=null || correspondenceaffiliation!=null || correspondenceeaddress!=null)
		{
			outputCorrespondence(file,correspondencename,correspondenceaffiliation,correspondenceeaddress);			
		}//correspondence
			
		    		
		//GRANTLIST
		if(grantlist!=null && grantlist.length()>0)
		{
			outputGrantlist(file,grantlist);	
		}//grantlist
		    		
		//ABSTRACT
		if(abstractdata!=null && abstractdata.length()>0)
		{
			outputAbstract(file,abstractdata,abstract_original, abstract_perspective);	
		}//abstract
	    		
		//SOURCE
		file.write("<source");
		if(sourcetype!=null && sourcetype.length()>0)
		{
			file.write(" type=\""+sourcetype+"\""); 
		}
		
		if(sourcecountry!=null && sourcecountry.length()>0)
		{
			file.write(" country=\""+sourcecountry+"\"");
		}
		
		if(sourceid!=null && sourceid.length()>0)
		{
			file.write(" srcid=\""+sourceid+"\"");
		}
		file.write(">\n");
		
		if(sourcetitle!=null && sourcetitle.length()>0)
		{
			file.write("<sourcetitle>"+sourcetitle+"</sourcetitle>\n");
		}
		
		if(sourcetitleabbrev!=null && sourcetitleabbrev.length()>0)
		{
			file.write("<sourcetitle-abbrev>"+sourcetitleabbrev+"</sourcetitle-abbrev>\n");
		}
		
		if(issn!=null && issn.length()>0)
		{
			file.write("<issn type=\"print\">"+issn+"</issn>\n");
		}
		
		if(eissn!=null && eissn.length()>0)
		{
			file.write("<issn type=\"electronic\">"+eissn+"</issn>\n");
		}
				
		if(isbnString!=null && isbnString.length()>0)
		{
			outputISBN(file,isbnString);					
		}
		
		if(coden!=null && coden.length()>0)
		{
			file.write("<codencode>"+coden+"</codencode>\n");
		}
		
		if((volume!=null && volume.length()>0) || (issue!=null && issue.length()>0) || (page!=null && page.length()>0) || (pagecount!=null && pagecount.length()>0))
		{	
			outputVolisspag(file,volume,issue,page,pagecount);	
		}

		//PUBLICATIONYEAR
		if(publicationyear!=null && publicationyear.length()>0)
		{
			outputYear(file,publicationyear);	
		}
		
		//PUBLICATIONDATE
		if(publicationdate!=null && publicationdate.length()>0)
		{
			file.write("<publicationdate>\n<date-text>"+publicationdate+"</date-text>\n</publicationdate>\n");
		}
		
		//SOURCEWEBSITE
		if(sourcewebsite!=null && sourcewebsite.length()>0)
		{
			outputWebsite(file,sourcewebsite);			
		}
		
		//Contributor
		outputContributor(file,contributoraffiliation,contributor);
				
		if(editors!=null && editors.length()>0)
		{
			outputEditors(file,editors);		
		}
		
		if(publishername!=null || publisheraddress!=null || publisherelectronicaddress!=null)
		{
			outputPublisher(file,publishername,publisheraddress,publisherelectronicaddress);
		}
			
		if((reportnumber!=null && reportnumber.length()>0) || (confname!=null && confname.length()>0) || 
			(confcatnumber!=null && confcatnumber.length()>0) || (confcode !=null && confcode.length()>0) || 
			(conflocation!=null && conflocation.length()>0) || (confdate!=null && confdate.length()>0) ||
			(confsponsors!=null && confsponsors.length()>0))
		{
			file.write("<additional-srcinfo>\n");
			if((confname!=null && confname.length()>0) || (confcatnumber!=null && confcatnumber.length()>0) || 
    			(confcode !=null && confcode.length()>0) || (conflocation!=null && conflocation.length()>0) || 
    			(confdate!=null && confdate.length()>0) || (confsponsors!=null && confsponsors.length()>0))
			{
				file.write("<conferenceinfo>\n");
				file.write("<confevent>\n");
				if(confname!=null && confname.length()>0)
				{
					file.write("<confname>"+confname+"</confname>\n");
					//System.out.println(accessnumber+" confname="+confname);
				}
				if(confcatnumber!=null && confcatnumber.length()>0)
				{
					file.write("<confcatnumber>"+confcatnumber+"</confcatnumber>\n");
					//System.out.println(accessnumber+" confcatnumber="+confcatnumber);
				}
				if(confcode!=null && confcode.length()>0)
				{
					file.write("<confcode>"+confcode+"</confcode>\n");
					//System.out.println(accessnumber+" confcode="+confcode);
				}
				if(conflocation!=null && conflocation.length()>0)
				{
					outputConflocation(file,conflocation);
				}
				
				if(confdate!=null && confdate.length()>0)
				{
					file.write("<confdate>\n<date-text>"+confdate+"</date-text>\n</confdate>\n");
					//System.out.println("c-confdate="+confdate);
				}
				
				if(confsponsors!=null && confsponsors.length()>0)
				{
					file.write("<confsponsors>\n");
					String[] confsponsorArray = confsponsors.split(Constants.AUDELIMITER);
					for(int i=0;i<confsponsorArray.length;i++)
					{
						String confsponsor = confsponsorArray[i];
						file.write("<confsponsor>"+confsponsor+"</confsponsor>\n");					
					}
					file.write("</confsponsors>\n");
				}
				file.write("</confevent>\n");
				if((confencepartnumber!=null && confencepartnumber.length()>0) || (confercepagerange!=null && confercepagerange.length()>0) || 
	    			(confencepagecount !=null && confencepagecount.length()>0) || (confenceeditor!=null && confenceeditor.length()>0) || 
	    			(conferenceorganization!=null && conferenceorganization.length()>0) || (conferenceeditoraddress!=null && conferenceeditoraddress.length()>0))
    			{
    				file.write("<confpublication>\n");
    				if(confencepartnumber!=null && confencepartnumber.length()>0)
    				{
    					file.write("<procpartno>"+confencepartnumber+"</procpartno>\n");
    				}
    				
    				if(confercepagerange!=null && confercepagerange.length()>0)
    				{
    					file.write("<procpagerange>"+confercepagerange+"</procpagerange>\n");
    				}
    				
    				if(confencepagecount!=null && confencepagecount.length()>0)
    				{
    					file.write("<procpagecount>"+confencepagecount+"</procpagecount>\n");
    				}
    				
    				if((confenceeditor!=null && confenceeditor.length()>0) || (conferenceorganization!=null && conferenceorganization.length()>0) ||
	    				(conferenceeditoraddress!=null && conferenceeditoraddress.length()>0))
    				{
    					outputConfeditors(file,conferenceeditoraddress,conferenceorganization,confenceeditor);	
    				}
    					
    				file.write("</confpublication>\n");
    			}
				file.write("</conferenceinfo>\n");
				    				
			}
			file.write("</additional-srcinfo>\n");
			
			if(reportnumber!=null && reportnumber.length()>0)
			{
				file.write("<reportinfo>\n");
				file.write("<reportnumber>"+reportnumber+"</reportnumber>\n");
				file.write("</reportinfo>\n");
			}
		}
		file.write("</source>\n");
	    		
	    //ENHANCEMENT		
		if((controlledterm!=null && controlledterm.length()>0) || (uncontrolledterm!=null && uncontrolledterm.length()>0) || 
			(mainheading!= null && mainheading.length()>0) || (speciesterm != null && speciesterm.length()>0) || 
			(regionalterm != null && regionalterm.length()>0) || (treatmentcode != null && treatmentcode.length()>0) || 
			(classificationcode != null && classificationcode.length()>0) || (manufacturer != null && manufacturer.length()>0) || 
			(tradename != null && tradename.length()>0) || (sequencebank != null && sequencebank.length()>0 ) || 
			(chemicalterm != null && chemicalterm.length()>0) || (casregistrynumber != null && casregistrynumber.length()>0))
		{
			file.write("<enhancement>\n");
			if((controlledterm!=null && controlledterm.length()>0) || (uncontrolledterm!=null && uncontrolledterm.length()>0) || 
				(mainheading!= null && mainheading.length()>0) || (speciesterm != null && speciesterm.length()>0) || 
				(regionalterm != null && regionalterm.length()>0) || (treatmentcode != null && treatmentcode.length()>0) || 
				(chemicalterm != null && chemicalterm.length()>0))		    				    				
				{
					file.write("<descriptorgroup>\n");
					if(controlledterm!=null && controlledterm.length()>0)
					{
						outputControlledterm(file,controlledterm);							
					}//if controlledterm
					
					if(uncontrolledterm!=null && uncontrolledterm.length()>0)
					{
						outputUncontrolledterm(file,uncontrolledterm);
					}//if uncontrolledterm
					
					if(mainheading!=null && mainheading.length()>0)
					{
						outputMainHeading(file,mainheading);
					}//if mainheading
					
					if(speciesterm!=null && speciesterm.length()>0)
					{
						outputSpeciesterm(file,speciesterm);
					}//if speciesterm
					
					if(regionalterm!=null && regionalterm.length()>0)
					{
						outputRegionalterm(file,regionalterm);						
					}//if regionalterm
						    						
					if(treatmentcode!=null && treatmentcode.length()>0)
					{
						outputTreaTmentCode(file,treatmentcode);
					}//if treatmentcode
					
					if(chemicalterm!=null && chemicalterm.length()>0)
					{
						outputChemicalterm(file,chemicalterm);
					}//ifchemicalterm				
					file.write("</descriptorgroup>\n");
				} //if descriptorgroup
			
				if(classificationcode!=null && classificationcode.length()>0)
				{
					outputClassificationgroup(file,classificationcode);
				}//if
				
				if(manufacturer!=null && manufacturer.length()>0)
				{
					outputManufacturergroups(file,manufacturer);
				}//if
				
				
				if(tradename!=null && tradename.length()>0)
				{
					outputTradenameGroup(file,tradename);
				}//if
				
				if(sequencebank!=null && sequencebank.length()>0)
				{
					outputSequencebanks(file,sequencebank);
				}//if
				
				if(casregistrynumber!=null && casregistrynumber.length()>0)
				{
					outputChemicalgroup(file, casregistrynumber);
				}//if
				file.write("</enhancement>\n");
			}    							
			file.write("</head>\n");
	    	
			//REFERENCE COUNT
			if(refcount !=null && refcount.length()>0)
			{
				file.write("<tail>\n");
				if(referenceflag!=null && referenceflag.equals("YES"))
				{
					file.write("<bibliography refcount=\""+refcount+"\">\n");	
					getReference(file,accessnumber);
					file.write("</bibliography>\n");
							
				}
				else
				{
					file.write("<bibliography refcount=\""+refcount+"\"/>\n");
				}
				file.write("</tail>\n");
			}	    			
		
		file.write("</bibrecord>\n");
		file.write("</item>\n");    	
    }

	private void outputCitationTitle(FileWriter file,String citationTitle) throws Exception
	{
		file.write("<citation-title>\n");
		String[] ctitles = citationTitle.split(Constants.AUDELIMITER);
		for(int i=0;i<ctitles.length;i++)
		{
			String ctitle = ctitles[i];
			String[] ct = ctitle.split(Constants.IDDELIMITER);
			if(ct.length==4)
			{
				String title = ct[1];
				String original = ct[2];
				String lang = ct[3];
				file.write("<titletext xml:lang=\""+lang+"\" original=\""+original+"\">"+title+"</titletext>\n");
			}
			else
			{
				//System.out.println("record "+accessnumber+" citation-title format is wrong");
			}
			
		}
		file.write("</citation-title>\n");
	}
	
	private void outputWebsite(FileWriter file,String sourcewebsite) throws Exception
	{
		file.write("<website>\n");
		String[] websiteArray = sourcewebsite.split(Constants.IDDELIMITER);
		if(websiteArray.length>0)
		{
			String eaddress = null;
			String websitename = websiteArray[0];
			if(websiteArray.length>1)
			{
				eaddress = websiteArray[1];
			}
			if(websitename!=null && websitename.length()>0)
			{
				file.write("<websitename>"+websitename+"</websitename>\n");
			}
			
			if(eaddress!=null && eaddress.length()>0)
			{
				file.write("<ce:e-address>"+eaddress+"</ce:e-address>\n");
			}
		}
		file.write("</website>\n");
	}
    
	private void outputContributor(FileWriter file,String contributoraffiliation,String contributor) throws Exception
	{
		HashMap contributoraffiliationMap = new HashMap();
		if(contributoraffiliation!=null && contributoraffiliation.length()>0)
		{
			String[] caffiliations = contributoraffiliation.split(Constants.AUDELIMITER);
			for(int i=0;i<caffiliations.length;i++)
			{
				String caffiliation = caffiliations[i];
				if(caffiliation.indexOf(Constants.IDDELIMITER)>-1)
				{
    				String caffid = caffiliation.substring(0,caffiliation.indexOf(Constants.IDDELIMITER));
    				contributoraffiliationMap.put(caffid, caffiliation);
				}
				else
				{
					//System.out.println("no contributor affid for record "+accessnumber+", caffiliation="+caffiliation);
				}
			}
		}
		
		if(contributor!=null && contributor.length()>0)
		{
			String[] contributorGroups = contributor.split(Constants.AUDELIMITER);
			for(int i=0;i<contributorGroups.length;i++)
			{
				String contributorGroup = contributorGroups[i];
				file.write("<contributor-group>\n");
				if(contributorGroup!=null && contributorGroup.length()>0)
				{
					String[] contributors = contributorGroup.split(Constants.IDDELIMITER);
					//System.out.println("CONTRIBUTOR SIZE="+contributors.length);
					
					
					if(contributors.length>0)
					{
						String initials = null;
						String indexed_name = null;
						String degrees = null;
						String surname = null;
						String given_name = null;
						String seq = null;
						String auid = null;
						String type = null;
						String role = null;
						String id = null;
						String contributorid = contributors[0];
						if(contributors.length>1)
						{
							seq = contributors[1];
						}
						if(contributors.length>2)
						{
							auid = contributors[2];
						}
						if(contributors.length>3)
						{
							type = contributors[3];
						}
						if(contributors.length>4)
						{
							role = contributors[4];
						}
						if(contributors.length>5)
						{
							id = contributors[5];
						}
						if(contributors.length>6)
						{
							initials = contributors[6];
						}
						if(contributors.length>7)
						{
							indexed_name = contributors[7];
						}
						if(contributors.length>8)
						{
							degrees = contributors[8];
						}
						if(contributors.length>9)
						{
							surname = contributors[9];
						}
						if(contributors.length>10)
						{
							given_name = contributors[10];
						}
						//String suffix = contributors[11];
						//String nametext = contributors[12];
						//String text = contributors[13];
						//String eaddress = contributors[14];
						file.write("<contributor");
						if(seq!=null && seq.length()>0)
						{
							file.write(" seq=\""+seq+"\"");	    							
						}
						
						if(auid!=null && auid.length()>0)
						{
							file.write(" auid=\""+auid+"\"");	    							
						}
						
						if(type!=null && type.length()>0)
						{
							file.write(" type=\""+type+"\"");	    							
						}
						
						if(role!=null && role.length()>0)
						{
							file.write(" role=\""+role+"\"");	    							
						}
						file.write(">\n");
						
						if(initials!=null && initials.length()>0)
						{
							file.write("<ce:initials>"+initials+"</ce:initials>\n");
						}
						
						if(indexed_name!=null && indexed_name.length()>0)
						{
							file.write("<ce:indexed-name>"+indexed_name+"</ce:indexed-name>\n");
						}
						
						if(degrees!=null && degrees.length()>0)
						{
							file.write("<ce:degrees>"+degrees+"</ce:degrees>\n");
						}
						
						if(surname!=null && surname.length()>0)
						{
							file.write("<ce:surname>"+surname+"</ce:surname>\n");
						}
						
						if(given_name!=null && given_name.length()>0)
						{
							file.write("<ce:given-name>"+given_name+"</ce:given-name>\n");
						}
						file.write("</contributor>\n");
						
						if(contributoraffiliationMap.get(contributorid)!=null)
						{
							String caffiliation = (String)contributoraffiliationMap.get(contributorid);
							String[] caffiliationElements = caffiliation.split(Constants.IDDELIMITER);
							file.write("<affiliation>\n");
							if(caffiliationElements.length>0)
							{
								String affVenue = null;
								String affText = null;
								String affID = caffiliationElements[0];
								if(caffiliationElements.length>1)
								{
									affVenue = caffiliationElements[1];
								}
								
								if(caffiliationElements.length>2)
								{	
									affText = caffiliationElements[2];
								}
								
								if(affText!=null && affText.length()>0)
								{
									file.write("<ce:text>"+affText+"</ce:text>\n");
								}
								
								if(caffiliationElements.length>3)
								{
									String affOrg = caffiliationElements[3];
									String[] affOrgs = affOrg.split(Constants.GROUPDELIMITER);
									
									if(affOrgs.length>0)
									{
										String address_part = null;
										String city_group = null;
										String country = null;
										String affOrganization = affOrgs[0];
										if(affOrgs.length>1)
										{
											address_part = affOrgs[1];
										}
										
										if(affOrgs.length>2)
										{
											city_group = affOrgs[2];
										}
										
										if(affOrgs.length>3)
										{
											country = affOrgs[3];
										}
										
										if(affOrganization!=null && affOrganization.length()>0)
										{
											file.write("<organization>"+affOrganization+"</organization>\n");
										}
										if(address_part!=null && address_part.length()>0)
										{
											file.write("<address-part>"+address_part+"</address-part>\n");
										}
										if(city_group!=null && city_group.length()>0)
										{
											file.write("<city-group>"+city_group+"</city-group>\n");
										}
										if(country!=null && country.length()>0)
										{
											file.write("<country>"+country+"</country>\n");
										}	    								
									}
									else
									{
										System.out.println("affOrgs has wrong format size="+affOrgs.length);
									}
								}
								
							}
							else
							{
								System.out.println("affiliationElements has wrong format size="+caffiliationElements.length);
							}
							file.write("</affiliation>\n");
						}
						
					}
				}
				file.write("</contributor-group>\n");
			}
		}
	}

	private void outputAuthorGroup(FileWriter file, String authorString,String affiliationString) throws Exception
	{
		Map affsMap = new LinkedHashMap();
    	Map authorGroupMap = new LinkedHashMap();
    	
    	
    	if(affiliationString!=null)
    	{
	    	String[] BdAffiliationsArray = affiliationString.split(Constants.AUDELIMITER, -1);
	    	for(int i=0;i<BdAffiliationsArray.length;i++)
	    	{
	    		String BdAffiliation = BdAffiliationsArray[i];	    		
	    		String[] BdAffiliationArray = BdAffiliation.split(Constants.IDDELIMITER, -1);	    		
	    		
	    		if(BdAffiliationArray.length>0)
	    		{
	    			String affid=BdAffiliationArray[0];
	    			
	    			affsMap.put(affid, BdAffiliation);
	    		}
	    		else
	    		{
	    			//System.out.println("record "+accessnumber+" aff wrong format "+BdAffiliationArray.length);
	    		}
	    		
	    	}
    	}
    	
    	BdAuthors aus = new BdAuthors(authorString);
    	List authors = aus.getAuthors();
    	
    	
    	for(int i=0;i<authors.size();i++)
    	{
    		BdAuthor author = (BdAuthor)authors.get(i);
    		String affidStr = author.getAffidString();
    		if(affidStr!=null)
    		{
    			String[] affids = affidStr.split(Constants.GROUPDELIMITER,-1);
    			for(int k=0;k<affids.length;k++)
    			{
    				add(authorGroupMap,affids[k],author);
    			}
    		}
    	}
    	   	
    	Iterator authorGroupkey = authorGroupMap.keySet().iterator();
    	
    	while(authorGroupkey.hasNext())
    	{
    		String authorGroupId = (String)authorGroupkey.next();
    		List authorGroup = (List)authorGroupMap.get(authorGroupId);   
    		String affidStr = null;
    		file.write("<author-group>\n");
    		for(int i=0;i<authorGroup.size();i++)
    		{
    			BdAuthor author = (BdAuthor)authorGroup.get(i);
	    		String authorID = author.getAuid();
	    		String seq = author.getSec();
	    		String surname = author.getSurname();
	    		String givenname = author.getGivenName();
	    		String indexname = author.getIndexedName();
	    		String initials = author.getInitials();
	    		String suffix = author.getSuffix();
	    		String eaddress = author.getEaddress();
	    		String degree = author.getDegrees();
	    		 	   affidStr = author.getAffidString();	    			    			    		
	    		
	    		file.write("<author");
	    		
	    		if(seq!=null)
	    		{
	    			file.write(" seq=\""+seq+"\"");
	    		}
	    		
	    		if(authorID!=null)
	    		{
	    			if(authorID.indexOf(",")>-1)
	    			{
	    				String[] auids = authorID.split(",");
	    				file.write(" orcid=\""+auids[0]+"\" auid=\""+auids[1]+"\"");   				
	    			}
	    			else
	    			{
	    				file.write(" orcid=\""+authorID+"\"");
	    			}
	    		}
	    		
	    		file.write(" type=\"auth\">\n");
	    		
	    		
	    		if(initials!=null)
	    		{
	    			file.write("<ce:initials>"+initials+"</ce:initials>\n");
	    		}
	    		
	    		if(indexname!=null)
	    		{
	    			file.write("<ce:indexed-name>"+indexname+"</ce:indexed-name>\n");
	    		}
	    		
	    		if(surname!=null)
	    		{
	    			file.write("<ce:surname>"+surname+"</ce:surname>\n");
	    		}
	    		
	    		if(givenname!=null)
	    		{
	    			file.write("<ce:given-name>"+givenname+"</ce:given-name>\n");
	    		}
	    		
	    		if(eaddress!=null)
	    		{
	    			file.write("<ce:e-address>"+eaddress+"</ce:e-address>\n");
	    		}
	    		
	    		if(degree!=null)
	    		{
	    			file.write("<ce:degrees>"+degree+"</ce:degrees>\n");
	    		}
	    		
	    		if(suffix!=null)
	    		{
	    			file.write("<ce:suffix>"+suffix+"</ce:suffix>\n");
	    		}
	    		file.write("</author>\n");
    		}// for author
    		    		
    		String affiliation = (String)affsMap.get(authorGroupId);
    		if(affiliation!=null)
    		{
    			
    			String[] BdAffiliationArray = affiliation.split(Constants.IDDELIMITER, -1);
	    		
	    		if(BdAffiliationArray.length>0)
	    		{
	    			String affDeptId=null;
	    			String affiliationId=null;
	    			String affText=null;
	    			String affPostalCode=null;
	    			String affState= null;
	    			String affCity= null;
	    			String affAddressPart=null;
	    			String affCountry=null;
	    			String affOrganization=null;
	    			String affCityGroup=null;
	    			String affid=BdAffiliationArray[0];
	    			if(BdAffiliationArray.length>1)
		    		{
	    				affOrganization=BdAffiliationArray[1];
		    		}
	    			if(BdAffiliationArray.length>2)
		    		{
	    				affCityGroup=BdAffiliationArray[2];
		    		}
	    			if(BdAffiliationArray.length>3)
		    		{
	    				affCountry=BdAffiliationArray[3];
		    		}
	    			if(BdAffiliationArray.length>4)
		    		{
	    				affAddressPart=BdAffiliationArray[4];
		    		}
	    			if(BdAffiliationArray.length>5)
		    		{
	    				affCity=BdAffiliationArray[5];
		    		}
	    			if(BdAffiliationArray.length>6)
		    		{
	    				affState=BdAffiliationArray[6];
		    		}
	    			if(BdAffiliationArray.length>7)
		    		{
	    				affPostalCode=BdAffiliationArray[7];
		    		}
	    			if(BdAffiliationArray.length>8)
		    		{
	    				affText=BdAffiliationArray[8];
		    		}
	    			if(BdAffiliationArray.length>9)
		    		{
	    				affiliationId=BdAffiliationArray[9];
		    		}
	    			if(BdAffiliationArray.length>10)
		    		{
	    				affDeptId=BdAffiliationArray[10];
		    		}
    		
	    		file.write("<affiliation");
	    		if(affCountry!=null && affCountry.length()>0)
	    		{
	    			file.write(" country=\""+affCountry+"\"");
	    		}
	    		
	    		if(affiliationId!=null && affiliationId.length()>0)
	    		{
	    			file.write(" afid=\""+affiliationId+"\"");
	    		}
	    		
	    		if(affDeptId!=null && affDeptId.length()>0)
	    		{
	    			file.write(" dptid=\""+affDeptId+"\"");
	    		}
	    		
	    		file.write(">\n");
	    		
	    			    		
	    		if(affText!=null && affText.length()>0)
	    		{
	    			file.write("<ce:text>"+affText+"</ce:text>\n");
	    		}
	    		else
	    		{
	    			if(affOrganization!=null && affOrganization.length()>0)
	    			{
	    				file.write("<organization>"+affOrganization+"</organization>\n");
	    			}
	    			
	    			if(affAddressPart!=null && affAddressPart.length()>0)
	    			{
	    				file.write("<address-part>"+affAddressPart+"</address-part>\n");
	    			}
	    			
	    			if(affCityGroup!=null && affCityGroup.length()>0)
	    			{
	    				file.write("<city-group>"+affCityGroup+"</city-group>\n"); 
	    			}
	    			else
	    			{
	    				if(affCity!=null && affCity.length()>0)
	    				{
	    					file.write("<city>"+affCity+"</city>\n");
	    				}
	    				
	    				if(affState!=null && affState.length()>0)
	    				{
	    					file.write("<state>"+affState+"</state>\n");
	    				}
	    				
	    				if(affPostalCode!=null && affPostalCode.length()>0)
	    				{
	    					file.write("<postal-code>"+affPostalCode+"</postal-code>\n");
	    				}
	    				
	    			}//if affCityGroup
	    			
	    		}//if affText
	    		file.write("</affiliation>\n");
    		}//if affiliation
    	}    		
    	file.write("</author-group>\n");
    }//while
}

	private void outputCorrespondence(FileWriter file, String correspondencename,String correspondenceaffiliation,String correspondenceeaddress) throws Exception
	{
		file.write("<correspondence>\n");
		if(correspondencename!=null)
		{
			String[] cnames = correspondencename.split(Constants.IDDELIMITER,-1);
			if(cnames.length>0)
			{
				String given_name = null;
				String suffix = null;
				String nametext = null;
				String text = null;
				String initials = null;
				String indexed_name = null;
				String degrees = null;
				String surname = null;
				String id = cnames[0];
				if(cnames.length>1)
				{
					initials = cnames[1];
				}
				if(cnames.length>2)
				{
					indexed_name = cnames[2];
				}
				if(cnames.length>3)
				{
					degrees = cnames[3];
				}
				if(cnames.length>4)
				{
					surname = cnames[4];
				}
				if(cnames.length>5)
				{
					given_name = cnames[5];
				}
				if(cnames.length>6)
				{
					suffix = cnames[6];
				}
				if(cnames.length>7)
				{
					nametext = cnames[7];
				}
				if(cnames.length>8)
				{
					text = cnames[8];
				}
				file.write("<person>\n");
				if(initials!=null && initials.length()>0)
				{
					file.write("<ce:initials>"+initials+"</ce:initials>\n");
				}
				
				if(indexed_name!=null && indexed_name.length()>0)
				{
					file.write("<ce:indexed-name>"+indexed_name+"</ce:indexed-name>\n");
				}
				
				if(degrees!=null && degrees.length()>0)
				{
					file.write("<ce:degrees>"+degrees+"</ce:degrees>\n");
				}
				
				if(surname!=null && surname.length()>0)
				{
					file.write("<ce:surname>"+surname+"</ce:surname>\n");
				}
				
				if(given_name!=null && given_name.length()>0)
				{
					file.write("<ce:given-name>"+given_name+"</ce:given-name>\n");
				}
				
				if(suffix!=null && suffix.length()>0)
				{
					file.write("<ce:suffix>"+suffix+"</ce:suffix>\n");
				}
				
				if(nametext!=null && nametext.length()>0)
				{
					file.write("<ce:nametext>"+nametext+"</ce:nametext>\n");
				}
				
				if(text!=null && text.length()>0)
				{
					file.write("<ce:text>"+text+"</ce:text>\n");
				}
				
				file.write("</person>\n");
				
			}
			else
			{
				System.out.println("correspondencename has wrong format, size is "+cnames.length);
			}//cnames
			    			
		}//correspondencename
		
		if(correspondenceaffiliation!=null)
		{
			file.write("<affiliation");
			String[] affs = correspondenceaffiliation.split(Constants.IDDELIMITER,-1);
			
			//System.out.println("correspondenceaffiliation size = "+affs.length);
			if(affs.length>0)
			{
				String afid = affs[0];
				String venue = null;
				String content =null;
				String dptid = null;
				if(affs.length>1)
				{
					venue = affs[1];
				}
				if(affs.length>2)
				{
					content = affs[2];
				}
				
				if(affs.length>3)
				{
					dptid = affs[3];
				}
				
				if(afid!=null && afid.length()>0 && dptid!=null && dptid.length()>0)
				{
					file.write(" afid=\""+afid+"\" dptid=\""+dptid+"\">\n");
				}
				else if(afid!=null && afid.length()>0)
				{
					file.write(" afid=\""+afid+"\">\n");
				}
				else if(dptid!=null && dptid.length()>0)
				{
					file.write(" dptid=\""+dptid+"\">\n");
				}
				else
				{
					file.write(">\n");
				}
				if(content!=null)
				{
					if(content.indexOf(Constants.GROUPDELIMITER)>-1)
					{
						String[] organizations = content.split(Constants.GROUPDELIMITER);
						
						String organization = null;
						String address_part = null;
						String city_group = null;
						String country = null;
						if(organizations.length>0)
						{
							organization = organizations[0];
						}
						if(organizations.length>1)
						{
							address_part = organizations[1];
						}
						if(organizations.length>2)
						{
							city_group = organizations[2];
						}
						if(organizations.length>3)
						{
							country = organizations[3];
						}
						if(organization!=null && organization.length()>0)
						{
							file.write("<organization>"+organization+"</organization>\n");
						}
						
						if(address_part!=null && address_part.length()>0)
						{
							file.write("<address-part>"+address_part+"</address-part>\n");
						}
						
						if(city_group!=null && city_group.length()>0)
						{
							file.write("<city-group>"+city_group+"</city-group>\n");
						}
						
						if(country!=null && country.length()>0)
						{
							file.write("<country>"+country+"</country>\n");
						}
   				
					}
					else
					{
						file.write("<text>"+content+"</text>\n");
					}//content
				}
				else
				{
					System.out.println("1 correspondence affiliation format is wrong ");
					
				}//content
				file.write("</affiliation>\n");			
			}
			else
			{
				System.out.println("2 correspondence affiliation format is wrong ");
				System.out.println("else affs[0]="+affs[0]);
				System.out.println("else affs[1]="+affs[1]);
				System.out.println("else affs[2]="+affs[2]);
			}//content
		}
			
			
		if(correspondenceeaddress!=null && correspondenceeaddress.length()>0)
		{
			String[] cEaddress = correspondenceeaddress.split(Constants.IDDELIMITER);
			if(cEaddress.length==2)
			{
				file.write("<ce:e-address type=\""+cEaddress[0]+"\">"+cEaddress[1]+"</ce:e-address>\n");
			}
			else
			{
				//System.out.println("record "+accessnumber+" correspondenceeaddress has wrong format");
			}
		}
		file.write("</correspondence>\n");
	}
	
	private void outputGrantlist(FileWriter file, String grantlist) throws Exception
	{
		file.write("<grantlist>\n"); //our database table didn't capture the attribute "complete", so we ignore it here.
		String[] grants = grantlist.split(Constants.AUDELIMITER);
		for(int i=0;i<grants.length;i++)
		{
			String grant = grants[i];
			String[] grantElements = grant.split(Constants.IDDELIMITER);
			if(grantElements.length==3){
				file.write("<grant>\n");
				String grantId = grantElements[0];
				String grantAcronym = grantElements[1];
				String grantAgency = grantElements[2];
				if(grantId!=null && grantId.length()>0){
					file.write("<grant-id>"+grantId+"</grant-id>\n");
				}
				
				if(grantAcronym!=null && grantAcronym.length()>0){
					file.write("<grant-acronym>"+grantAcronym+"</grant-acronym>\n");
				}
				
				if(grantAgency!=null && grantAgency.length()>0){
					file.write("<grant-agency>"+grantAgency+"</grant-agency>\n");
				}
				file.write("</grant>");
				
			}
			else
			{
				//System.out.println("record "+accessnumber+"'s grantlist has a wrong format");
			}
		}
		file.write("</grantlist>\n");
	}

	private void outputAbstract(FileWriter file, String abstractdata, String abstract_original, String abstract_perspective) throws Exception
	{
		file.write("<abstracts>\n");
		file.write("<abstract ");
		if(abstract_original!=null && abstract_original.length()>0 && abstract_perspective!=null && abstract_perspective.length()>0)
		{
			file.write(" original=\""+abstract_original+"\" perspective=\""+abstract_perspective+"\">\n");
		}
		else if(abstract_original!=null && abstract_original.length()>0)
		{
			file.write(" original=\""+abstract_original+"\">\n");
		}
		else if(abstract_perspective!=null && abstract_perspective.length()>0)
		{
			file.write(" perspective=\""+abstract_perspective+"\">\n");
		}
		else
		{
			file.write(">\n");
		}
		if(abstractdata.indexOf("&copy;")>-1)
		{
			String abstractcopyright = abstractdata.substring(abstractdata.indexOf("&copy;"));
			file.write("<publishercopyright>"+abstractcopyright+"</publishercopyright>\n");
			abstractdata = abstractdata.substring(0,abstractdata.indexOf("&copy;")-1);
			//System.out.println("copyright="+abstractcopyright);
			//System.out.println("abstract="+abstractdata);
		}
		file.write("<ce:para>"+abstractdata+"</ce:para>\n");
		file.write("</abstract>\n");
		file.write("</abstracts>\n");
	}

    private void outputISBN(FileWriter file, String isbnString) throws Exception
    {
    	String[] isbns = isbnString.split(Constants.AUDELIMITER);
		for(int i=0;i<isbns.length;i++)
		{
			String isbn = isbns[i];
			if(isbn!=null && isbn.length()>0)
			{
				String[] isbnElements = isbn.split(Constants.IDDELIMITER);
				if(isbnElements.length>0)
				{
					String isbnType = isbnElements[0];
					String isbnLength = isbnElements[1];
					String isbnLevel = isbnElements[2];
					String isbnValue = isbnElements[3];
					file.write("<isbn");
					if(isbnType!=null && isbnType.length()>0)
					{
						file.write(" type=\""+isbnType+"\"");
					}
					
					if(isbnLevel!=null && isbnLevel.length()>0)
					{
						file.write(" level=\""+isbnLevel+"\"");
					}
					
					if(isbnLength!=null && isbnLength.length()>0)
					{
						file.write(" length=\""+isbnLength+"\"");
					}
					file.write(">"+isbnValue+"</isbn>\n");
							
					
				}
			}
		}
    }
    
    private void outputYear(FileWriter file, String publicationyear) throws Exception
    {
    	file.write("<publicationyear");
		String first = null;
		String last = null;
		if(publicationyear.indexOf("-")>0)
		{
			String[] yearArray = publicationyear.split("-");
			first = yearArray[0];
			last  = yearArray[1];
		}
		else
		{
			first = publicationyear;
		}
		file.write(" first=\""+first+"\"");
		if(last != null && last.length()>0)
		{
			file.write(" last=\""+last+"\"");
		}
		file.write(" />\n");
    }
    
    private void outputEditors(FileWriter file, String editors) throws Exception
    {
    	String[] editorArray = editors.split(Constants.AUDELIMITER,-1);
		file.write("<editors>\n");
		for(int i=0;i<editorArray.length;i++)
		{
			file.write("<editor>\n");
			String editor = editorArray[i];
			String[] editorElements = editor.split(Constants.IDDELIMITER,-1);
			if(editorElements.length>0)
			{
				String given_name = null;
				String suffix = null;
				String nametext = null;
				String text = null;
				String initials = null;
				String indexed_name = null;
				String degrees = null;
				String surname = null;
				String id = editorElements[0];
				if(editorElements.length>1)
				{
					initials = editorElements[1];
				}
				if(editorElements.length>2)
				{
					indexed_name = editorElements[2];
				}
				if(editorElements.length>3)
				{
					degrees = editorElements[3];
				}
				if(editorElements.length>4)
				{
					surname = editorElements[4];
				}
				
				if(editorElements.length>5)
				{
					given_name = editorElements[5];
				}
				if(editorElements.length>6)
				{
					suffix = editorElements[6];
				}
				if(editorElements.length>7)
				{
					nametext = editorElements[7];
				}
				if(editorElements.length>8)
				{
					text = editorElements[8];
				}
				file.write("<person>\n");
				if(initials!=null && initials.length()>0)
				{
					file.write("<ce:initials>"+initials+"</ce:initials>\n");
				}
				
				if(indexed_name!=null && indexed_name.length()>0)
				{
					file.write("<ce:indexed-name>"+indexed_name+"</ce:indexed-name>\n");
				}
				
				if(degrees!=null && degrees.length()>0)
				{
					file.write("<ce:degrees>"+degrees+"</ce:degrees>\n");
				}
				
				if(surname!=null && surname.length()>0)
				{
					file.write("<ce:surname>"+surname+"</ce:surname>\n");
				}
				
				if(given_name!=null && given_name.length()>0)
				{
					file.write("<ce:given-name>"+given_name+"</ce:given-name>\n");
				}
				
				if(suffix!=null && suffix.length()>0)
				{
					file.write("<ce:suffix>"+suffix+"</ce:suffix>\n");
				}
				
				if(nametext!=null && nametext.length()>0)
				{
					file.write("<ce:nametext>"+nametext+"</ce:nametext>\n");
				}
				
				if(text!=null && text.length()>0)
				{
					file.write("<ce:text>"+text+"</ce:text>\n");
				}
				
				file.write("</person>\n");
				
			}
			else
			{
				System.out.println("editors has wrong format, size is "+editorElements.length);
			}//editorElements
			file.write("</editor>\n");
		}
		file.write("</editors>\n");
    }
    
    private void outputPublisher(FileWriter file, String publishername,String publisheraddress,String publisherelectronicaddress) throws Exception
    {
    	String[] publishernameArray = null;
		String[] publisheraddressArray = null;
		String[] publisherelectronicaddressArray = null;
		if(publishername!=null && publishername.length()>0)
		{
			publishernameArray = publishername.split(Constants.AUDELIMITER);
		}
		
		if(publisheraddress!=null && publisheraddress.length()>0)
		{
			publisheraddressArray = publisheraddress.split(Constants.AUDELIMITER);
		}
		
		if(publisherelectronicaddress!=null && publisherelectronicaddress.length()>0)
		{
			publisherelectronicaddressArray = publisherelectronicaddress.split(Constants.AUDELIMITER);
		}
		
		if(publishernameArray!=null && publishernameArray.length>0)
		{
			for(int i=0;i<publishernameArray.length;i++)
			{
				file.write("<publisher>\n");
				file.write("<publishername>"+publishernameArray[i]+"</publishername>\n");
				if(publisheraddressArray!=null && publisheraddressArray.length>i)
				{
					//file.write("<publisheraddress>"+publisheraddressArray[i]+"</publisheraddress>\n");
					String paddress = publisheraddressArray[i];
					if(paddress!=null && paddress.length()>0)
					{	
						if(paddress.indexOf(Constants.IDDELIMITER)<0)
						{
							file.write("<publisheraddress>"+paddress+"</publisheraddress>\n");
						}
						else
						{
							String[] publisheraddressElements = paddress.split(Constants.IDDELIMITER);
							file.write("<affiliation");
							if(publisheraddressElements.length>2)
							{
								String affaddress_part = null;
								String affcity_group = null;
								String affcountry = null;
								String afforganization = null;
								String afftext = null;
								String venue = null;
								String afid = publisheraddressElements[0];
								if(publisheraddressElements.length>3)
								{
									venue = publisheraddressElements[1];
								}
								if(publisheraddressElements.length>3)
								{
									afftext = publisheraddressElements[2];
								}
								
								if(publisheraddressElements.length>3)
								{
									afforganization = publisheraddressElements[3];
								}
								if(publisheraddressElements.length>4)
								{
									affaddress_part = publisheraddressElements[4];
								}
								if(publisheraddressElements.length>5)
								{
									affcity_group = publisheraddressElements[5];
								}
								if(publisheraddressElements.length>6)
								{
									affcountry = publisheraddressElements[6];
								}
								if(affcountry!=null && affcountry.length()>0)
								{
									file.write(" country=\""+affcountry+"\">\n");
								}
								else
								{
									file.write(">");
								}
								
								if(venue!=null && venue.length()>0)
								{
									file.write("<venue>"+venue+"</venue>\n");
								}
								if(affaddress_part!=null && affaddress_part.length()>0)
								{
									file.write("<address-part>"+affaddress_part+"</address-part>\n");
								}
								if(affcity_group!=null && affcity_group.length()>0)
								{
									file.write("<affcity-group>"+affcity_group+"</affcity-group>\n");
								}
								
								file.write("</affiliation>\n");	
							}															
							else
							{
								System.out.println("format is wrong publisheraddressElements size"+publisheraddressElements.length);
							}
						}
					}
					
				}
				if(publisherelectronicaddressArray!=null && publisherelectronicaddressArray.length>i)
				{
					String[] pEaddress = publisherelectronicaddressArray[i].split(Constants.IDDELIMITER);
					if(pEaddress.length==2)
					{
						file.write("<ce:e-address");
						if(pEaddress[0]!=null && pEaddress[0].length()>0)
						{
							file.write(" type=\""+pEaddress[0]+"\"");
						}
						file.write(">"+pEaddress[1]+"</ce:e-address>\n");
					}
					
				}
				file.write("</publisher>\n");
			}
		}
		else if(publisheraddressArray!=null && publisheraddressArray.length>0)
		{
			for(int i=0;i<publisheraddressArray.length;i++)
			{
				file.write("<publisher>\n");
				    				
				file.write("<publisheraddress>"+publisheraddressArray[i]+"</publisheraddress>\n");
				
				if(publisherelectronicaddressArray!=null && publisherelectronicaddressArray.length>i)
				{
					file.write("<publisherelectronicaddress>"+publisherelectronicaddressArray[i]+"</publisherelectronicaddress>\n");
				}
				file.write("</publisher>\n");
			}
		}
		else if(publisherelectronicaddressArray!=null && publisherelectronicaddressArray.length>0)
		{
			for(int i=0;i<publisherelectronicaddressArray.length;i++)
			{
				file.write("<publisher>\n");
			
				file.write("<publisherelectronicaddress>"+publisherelectronicaddressArray[i]+"</publisherelectronicaddress>\n");
				
				file.write("</publisher>\n");
			}
		}
    }
    private void outputConflocation(FileWriter file, String conflocation) throws Exception
    {
    	String[] conflocationArray = conflocation.split(Constants.AUDELIMITER);
		for(int i=0;i<conflocationArray.length;i++)
		{
			String cflocation = conflocationArray[i];
			if(cflocation!=null && cflocation.length()>0)
			{
				String[] cflocationElements = cflocation.split(Constants.IDDELIMITER);
				
				if(cflocationElements.length>0)
				{
					file.write("<conflocation");
					String affcountry = null;
					String affcity_group = null;
					String affaddress_part = null;
					String venue = null;
					String afftext = null;
					String afforganization = null;
					String afid = cflocationElements[0];
					if(cflocationElements.length>1)
					{
						venue = cflocationElements[1];
					}
					if(cflocationElements.length>2)
					{
						afftext = cflocationElements[2];
					}
					if(cflocationElements.length>3)
					{
						afforganization = cflocationElements[3];
					}
					if(cflocationElements.length>4)
					{
						affaddress_part = cflocationElements[4];
					}
					if(cflocationElements.length>5)
					{
						affcity_group = cflocationElements[5];
					}
					
					if(cflocationElements.length>6)
					{
						affcountry = cflocationElements[6];
					}
					
					if(affcountry!=null && affcountry.length()>0)
					{
						file.write(" country=\""+affcountry+"\">\n");
					}
					else
					{
						file.write(">");
					}
					
					if(venue!=null && venue.length()>0)
					{
						file.write("<venue>"+venue+"</venue>\n");
					}
					if(affaddress_part!=null && affaddress_part.length()>0)
					{
						file.write("<address-part>"+affaddress_part+"</address-part>\n");
					}
					if(affcity_group!=null && affcity_group.length()>0)
					{
						file.write("<affcity-group>"+affcity_group+"</affcity-group>\n");
					}					
					file.write("</conflocation>\n");	    												
				}
				else
				{									
					System.out.println("afid ="+ cflocationElements[0]);
					System.out.println("venue ="+ cflocationElements[1]);
					//System.out.println("afftext ="+ cflocationElements[2]);
					//System.out.println("afforganization ="+ cflocationElements[3]);									
					System.out.println("format is wrong cflocationElements size"+cflocationElements.length);
				}
			}
		}//for
    }
   
    
    
    private void outputVolisspag(FileWriter file, String volume,String issue,String page, String pagecount) throws Exception
    {
    	file.write("<volisspag>\n");
		if((volume!=null && volume.length()>0) || (issue!=null && issue.length()>0))
		{	    			
			file.write("<voliss");
    		if(volume!=null && volume.length()>0 )
    		{
    			file.write(" volume=\""+dictionary.AlphaEntitysToNumericEntitys(volume)+"\"");
    		}
    		
    		if(issue!=null && issue.length()>0)
    		{
    			file.write(" issue=\""+dictionary.AlphaEntitysToNumericEntitys(issue)+"\"");
    		}
    		file.write("/>\n");    		
		}
		
		if(page!=null && page.length()>0)
		{
			String[] pageArray = page.split(Constants.AUDELIMITER);
		    if(pageArray.length==3)
		    {
		    	String pages = pageArray[0];
		    	String firstPage = pageArray[1];
		    	String lastPage = pageArray[2];
		    	if(pages!=null && pages.length()>0)
		    	{
		    		file.write("<page>"+pages+"</page>\n");
		    	}
		    	else if((firstPage!=null && firstPage.length()>0) || (lastPage!=null && lastPage.length()>0))
		    	{
		    		file.write("<pagerange");	    		    			    		    	
		    		if(firstPage!=null && firstPage.length()>0)
		    		{
		    			file.write(" first=\""+firstPage+"\"");
		    		}
		    		
		    		if(lastPage!=null && lastPage.length()>0)
	    		    {
	    		    	file.write(" last=\""+lastPage+"\""); 
	    		    }
		    		file.write(" />\n");	
		    	}
		    		
		    }//if
			
		}//if
		
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
					}
					
					file.write("<pagecount");
					if(pagecountType!=null && pagecountType.length()>0)
					{
						file.write(" type=\""+pagecountType+"\"");
					}
					file.write(">"+pagecountvalue+"</pagecount>\n");
					
				}//if
			}//for
		}
		file.write("</volisspag>\n");
    }
    
    private void outputConfeditors(FileWriter file, String conferenceeditoraddress,String conferenceorganization,String confenceeditor) throws Exception
    {
    	file.write("<confeditors>\n");
		if(conferenceeditoraddress!=null && conferenceeditoraddress.length()>0)
		{
			file.write("<editoraddress>"+conferenceeditoraddress+"</editoraddress>\n");
		}
		if(conferenceorganization!=null && conferenceorganization.length()>0)
		{
			file.write("<editororganization>"+conferenceorganization+"</editororganization>\n");
		}
		
		if(confenceeditor!=null && confenceeditor.length()>0)
		{
			String[] confeditorArray = confenceeditor.split(Constants.AUDELIMITER);
			for(int i=0;i<confeditorArray.length;i++)
			{
				String confeditor = confeditorArray[i];
				if(confeditor!=null && confeditor.length()>0)
				{
					file.write("<confeditor>\n");
					String[] confeditorElement = confeditor.split(Constants.IDDELIMITER);
					//System.out.println("confeditorElement SIZE="+confeditorElement.length);
					String editorID = null;
					String initials = null;
					String indexed_name = null;
					String degrees = null;
					String surname = null;
					String given_name = null;
					String suffix = null;
					String nametext = null;
					String text = null;
					if(confeditorElement.length>0)
					{
						editorID = confeditorElement[0];
					}
					if(confeditorElement.length>1)
					{
						initials = confeditorElement[1];
					}
					if(confeditorElement.length>2)
					{
						indexed_name = confeditorElement[2];
					}
					if(confeditorElement.length>3)
					{
						degrees = confeditorElement[3];
					}
					if(confeditorElement.length>4)
					{
						surname = confeditorElement[4];
					}
					if(confeditorElement.length>5)
					{
						given_name = confeditorElement[5];
					}
					if(confeditorElement.length>6)
					{
						suffix = confeditorElement[6];
					}
					if(confeditorElement.length>7)
					{
						nametext = confeditorElement[7];
					}
					if(confeditorElement.length>8)
					{
						text = confeditorElement[8];
					}
					if(initials!=null && initials.length()>0)
					{
						file.write("<ce:initials>"+initials+"</ce:initials>\n");
					}
					if(indexed_name!=null && indexed_name.length()>0)
					{
						file.write("<ce:indexed-name>"+indexed_name+"</ce:indexed-name>\n");
					}
					if(degrees!=null && degrees.length()>0)
					{
						file.write("<ce:degrees>"+degrees+"</ce:degrees>\n");
					}
					if(given_name!=null && given_name.length()>0)
					{
						file.write("<ce:given-name>"+given_name+"</ce:given-name>\n");
					}
					if(suffix!=null && suffix.length()>0)
					{
						file.write("<ce:suffix>"+suffix+"</ce:suffix>\n");
					}
					if(nametext!=null && nametext.length()>0)
					{
						file.write("<ce:nametext>"+nametext+"</ce:nametext>\n");
					}
					if(text!=null && text.length()>0)
					{
						file.write("<ce:text>"+text+"</ce:text>\n");
					}	    												
					file.write("</confeditor>\n");
				}
			}
		}
		
		file.write("</confeditors>\n");
	}
	
    
    private void outputControlledterm(FileWriter file, String controlledterm) throws Exception
    {
    	file.write("<descriptors");
		String descriptorsType = null;
		if(databaseName.equals("cpx")  || databaseName.equals("chm"))
		{
			descriptorsType="CCV";
		}
		else if(databaseName.equals("pch")) 
		{
			descriptorsType="PCV";
		}
		else if(databaseName.equals("geo")) 
		{
			descriptorsType="GDE";
		}
		
		file.write(" controlled=\"y\" type=\""+descriptorsType+"\">\n");
		
		String[] controlledtermElements = controlledterm.split(Constants.AUDELIMITER);
		for(int i=0;i<controlledtermElements.length;i++)
		{
			String controlledtermElement = controlledtermElements[i];
			if(controlledtermElement!=null && controlledtermElement.length()>0)
			{
				file.write("<descriptor>\n");
				file.write("<mainterm>"+controlledtermElement+"</mainterm>\n");
				file.write("</descriptor>\n");
			}
		}
		file.write("</descriptors>\n");
    }
    
    private void outputUncontrolledterm(FileWriter file, String uncontrolledterm) throws Exception
    {
    	file.write("<descriptors controlled=\"n\" type=\"CFL\">\n");	    								    							
		
		String[] uncontrolledtermElements = uncontrolledterm.split(Constants.AUDELIMITER);
		for(int i=0;i<uncontrolledtermElements.length;i++)
		{
			String uncontrolledtermElement = uncontrolledtermElements[i];
			if(uncontrolledtermElement!=null && uncontrolledtermElement.length()>0)
			{
				file.write("<descriptor>\n");
				file.write("<mainterm>"+uncontrolledtermElement+"</mainterm>\n");
				file.write("</descriptor>\n");
			}
		}
		file.write("</descriptors>\n");
    }
    
    private void outputMainHeading(FileWriter file, String mainheading) throws Exception
    {
    	file.write("<descriptors controlled=\"y\" type=\"CMH\">\n");	    								    							
		
		String[] mainheadingElements = mainheading.split(Constants.AUDELIMITER);
		for(int i=0;i<mainheadingElements.length;i++)
		{
			String mainheadingElement = mainheadingElements[i];
			if(mainheadingElement!=null && mainheadingElement.length()>0)
			{
				file.write("<descriptor>\n");
				file.write("<mainterm>"+mainheadingElement+"</mainterm>\n");
				file.write("</descriptor>\n");
			}
		}
		file.write("</descriptors>\n");
    }
    
    private void outputSpeciesterm(FileWriter file, String speciesterm) throws Exception
    {
    	file.write("<descriptors controlled=\"y\" type=\"SPC\">\n");	    								    							
		
		String[] speciestermElements = speciesterm.split(Constants.AUDELIMITER);
		for(int i=0;i<speciestermElements.length;i++)
		{
			String speciestermElement = speciestermElements[i];
			if(speciestermElement!=null && speciestermElement.length()>0)
			{
				file.write("<descriptor>\n");
				file.write("<mainterm>"+speciestermElement+"</mainterm>\n");
				file.write("</descriptor>\n");
			}
		}
		file.write("</descriptors>\n");
    }
    
    private void outputRegionalterm(FileWriter file, String regionalterm) throws Exception
    {
    	file.write("<descriptors controlled=\"y\" type=\"RGI\">\n");	    								    							
		
		String[] regionaltermElements = regionalterm.split(Constants.AUDELIMITER);
		for(int i=0;i<regionaltermElements.length;i++)
		{
			String regionaltermElement = regionaltermElements[i];
			if(regionaltermElement!=null && regionaltermElement.length()>0)
			{
				file.write("<descriptor>\n");
				file.write("<mainterm>"+regionaltermElement+"</mainterm>\n");
				file.write("</descriptor>");
			}
		}
		file.write("</descriptors>\n");
    }
    
    private void outputTreaTmentCode(FileWriter file,String treatmentcode) throws Exception
    {
    	file.write("<descriptors controlled=\"y\" type=\"CTC\">\n");	    								    							
		
		String[] treatmentcodeElements = treatmentcode.split(Constants.AUDELIMITER);
		for(int i=0;i<treatmentcodeElements.length;i++)
		{
			String treatmentcodeElement = treatmentcodeElements[i];
			if(treatmentcodeElement!=null && treatmentcodeElement.length()>0)
			{
				file.write("<descriptor>\n");
				file.write("<mainterm>"+treatmentcodeElement+"</mainterm>\n");
				file.write("</descriptor>\n");
			}
		}
		file.write("</descriptors>\n");
    }
    
    private void outputChemicalterm(FileWriter file,String chemicalterm) throws Exception
    {
    	file.write("<descriptors controlled=\"y\" type=\"MED\">\n");	    								    							
		
		String[] chemicaltermElements = chemicalterm.split(Constants.AUDELIMITER);
		for(int i=0;i<chemicaltermElements.length;i++)
		{
			String chemicaltermElement = chemicaltermElements[i];
			if(chemicaltermElement!=null && chemicaltermElement.length()>0)
			{
				file.write("<descriptor>\n");
				file.write("<mainterm>"+chemicaltermElement+"</mainterm>\n");
				file.write("</descriptor>\n");
			}
		}
		file.write("</descriptors>\n");
    }
    
    private void outputClassificationgroup(FileWriter file,String classificationcode) throws Exception
    {
    	CPXDataDictionary classificationDesc = new CPXDataDictionary();
    	file.write("<classificationgroup>\n");
		file.write("<classifications");
		if(databaseName.equals("geo"))
		{
			file.write(" type=\"GEOCLASS\">\n");	
		}
		else
		{
			file.write(" type=\"CPXCLASS\">\n");	
		}
		
		String[] classificationcodeElements = classificationcode.split(Constants.AUDELIMITER);
		for(int i=0;i<classificationcodeElements.length;i++)
		{
			String  classificationcodeElement = classificationcodeElements[i];	    					
			file.write("<classification>\n");
			file.write("<classification-code>"+classificationcodeElement+"</classification-code>\n");
			if(classificationDesc.getClassCodeTitle(classificationcodeElement)!=null)
			{
				file.write("<classification-description>"+classificationDesc.getClassCodeTitle(classificationcodeElement)+"</classification-description>\n");
				//System.out.println("CLASSCODE="+classificationcodeElement+" Desc="+classificationDesc.getClassCodeTitle(classificationcodeElement));
			}
			file.write("</classification>\n");
		}
		file.write("</classifications>\n");
		file.write("</classificationgroup>\n");
    }
    
    private void outputManufacturergroups(FileWriter file,String manufacturer) throws Exception
    {
    	file.write("<manufacturergroup>\n");
		String[] manufacturers = manufacturer.split(Constants.AUDELIMITER);
		for(int i=0;i<manufacturers.length;i++)
		{
			String manufacturersElement = manufacturers[i];
			if(manufacturersElement!=null && manufacturersElement.length()>0)
			{
				file.write("<manufacturers>\n");
				String[] manufacturerArray = manufacturersElement.split(Constants.IDDELIMITER);
				for(int j=0;j<manufacturerArray.length;j++)
				{
					String manufacturerElement = manufacturerArray[j];
					if(manufacturerElement!=null && manufacturerElement.length()>0)
					{
						String[] manufacturerDetail = manufacturerElement.split(Constants.GROUPDELIMITER);
						if(manufacturerDetail.length==2)
						{
							file.write("<manufacturer");
							String manufacturerCountry = manufacturerDetail[0];
							String manufacturerContent = manufacturerDetail[1];
							if(manufacturerCountry!=null && manufacturerCountry.length()>0)
							{
								file.write(" country=\""+manufacturerDetail[0]+"\">\n");
							}
							else
							{
								file.write(">\n");
							}
							
							if(manufacturerContent!=null && manufacturerContent.length()>0) 
							{
								file.write(manufacturerContent);
							}
							
							file.write("</manufacturer>\n");
						}//if
					}//if
				}//for
				file.write("</manufacturers>\n");
			}//if
		}//for
		file.write("</manufacturergroup>\n");
    }
    
    private void outputSequencebanks(FileWriter file,String sequencebank) throws Exception
    {
    	//System.out.println("sequencebank="+sequencebank);
		file.write("<sequencebanks>\n");
		String[] sequencebanks = sequencebank.split(Constants.AUDELIMITER);
		for(int i=0;i<sequencebanks.length;i++)
		{
			String sequencebankElements = sequencebanks[i];
			//System.out.println("sequencebankElements="+sequencebankElements);
			if(sequencebankElements!=null && sequencebankElements.length()>0)
			{
				file.write("<sequencebank");
				String[] sequencebankArray = sequencebankElements.split(Constants.IDDELIMITER);
				String sequencebankName = sequencebankArray[0];
				if(sequencebankName!=null && sequencebankName.length()>0)
				{
					file.write(" name=\""+sequencebankName+"\">\n");
				}
				else
				{
					file.write(">\n");
				}
				
				for(int j=1;j<sequencebankArray.length;j++)
				{
					file.write("<sequence-number>"+sequencebankArray[j]+"</sequence-number>\n");
					//System.out.println("sequence-number="+sequencebankArray[j]);
				}
				file.write("</sequencebank>\n");
				
			}//if
		}//for
		file.write("</sequencebanks>\n");
    }
    
    private void outputTradenameGroup(FileWriter file,String tradename) throws Exception 
    {
    	file.write("<tradenamegroup>\n");
		String[] tradenamesArray = tradename.split(Constants.AUDELIMITER);
		for(int i=0;i<tradenamesArray.length;i++)
		{
			file.write("<tradenames");
			String tradenames = tradenamesArray[i];
			if(tradenames!=null && tradenames.length()>0)
			{
				String[] tradenameElements = tradenames.split(Constants.IDDELIMITER);
				//System.out.println("tradenames SIZE="+tradenameElements.length);
				String tradenameType = tradenameElements[0];
				if(tradenameType!=null && tradenameType.length()>0)
				{
					file.write(" type=\""+tradenameType+"\">\n");
				}
				else
				{
					file.write(">\n");
				}
				for(int j=1;j<tradenameElements.length;j++)
				{
					String tradenameContent = tradenameElements[j];
					file.write("<trademanuitem>\n<tradename>"+tradenameContent+"</tradename>\n</trademanuitem>\n");
				}  								 								
			}//if
			else
			{
				file.write(">\n");
			}
			file.write("</tradenames>\n");
		}//for 						
		file.write("</tradenamegroup>\n");
    }
    
    private void outputChemicalgroup(FileWriter file, String casregistrynumber) throws Exception 
    {
    	file.write("<chemicalgroup>\n");
		String[] chemicalgroup = casregistrynumber.split(Constants.AUDELIMITER);
		for(int i=0;i<chemicalgroup.length;i++)
		{
			file.write("<chemicals>\n");
			String chemicals=chemicalgroup[i];
			if(chemicals!=null && chemicals.length()>0)
			{
				String[] chemicalsElement = chemicals.split(Constants.IDDELIMITER);
				for(int j=0;j<chemicalsElement.length;j++)
				{
					
					String chemical = chemicalsElement[j];
					if(chemical!=null && chemical.length()>0)
					{
						file.write("<chemical>\n");
						String[] chemicalElement = chemical.split(Constants.GROUPDELIMITER);
						String chemical_name = chemicalElement[0];
						
						if(chemical_name!=null && chemical_name.length()>0)
						{
							file.write("<chemical-name>"+chemical_name+"</chemical-name>\n");
						}
						
						for(int k=1; k<chemicalElement.length;k++)
						{    										
							String cas_registry_number = chemicalElement[k];
							if(cas_registry_number != null && cas_registry_number.length()>0)
							{
								file.write("<cas-registry-number>"+cas_registry_number+"</cas-registry-number>\n");
							}
						}
						file.write("</chemical>\n");
					}//if
				}//for						
			}//if
			file.write("</chemicals>\n");
		}//for
		file.write("</chemicalgroup>\n");
    }
    
    public void getReference(FileWriter file, String accessnumber)
    {
    	 Statement stmt = null;
         ResultSet rs = null;        
         
    	 try
         {
             stmt = con.createStatement();           
             rs = stmt.executeQuery("select *  from bd_master_reference where accessnumber='"+accessnumber+"'");
             while (rs.next())
             {
            	                    
            	 String referenceid = rs.getString("REFERENCEID");                     
            	 String referencetitle = rs.getString("REFERENCETITLE");                  
            	 String referenceauthor = rs.getString("REFERENCEAUTHOR");                 
            	 String referencesourcetitle = rs.getString("REFERENCESOURCETITLE");            
            	 String referencepublicationyear = rs.getString("REFERENCEPUBLICATIONYEAR");        
            	 String referencevolume = rs.getString("REFERENCEVOLUME");                
            	 String referenceissue = rs.getString("REFERENCEISSUE");                  
            	 String referencepages = rs.getString("REFERENCEPAGES");                  
            	 String referencefulltext = rs.getString("REFERENCEFULLTEXT");              
            	 String referencetext = rs.getString("REFERENCETEXT");                   
            	 String referencewebsite = rs.getString("REFERENCEWEBSITE");                
            	 String referenceitemid = rs.getString("REFERENCEITEMID");                 
            	 String referenceitemcitationpii = rs.getString("REFERENCEITEMCITATIONPII");        
            	 String referenceitemcitationdoi = rs.getString("REFERENCEITEMCITATIONDOI");        
            	 String referenceitemcitationauthor = rs.getString("REFERENCEITEMCITATIONAUTHOR");     
            	 String refitemcitationsourcetitle = rs.getString("REFITEMCITATIONSOURCETITLE");      
            	 String referenceitemcitationissn = rs.getString("REFERENCEITEMCITATIONISSN");       
            	 String referenceitemcitationisbn = rs.getString("REFERENCEITEMCITATIONISBN");       
            	 String referenceitemcitationpart = rs.getString("REFERENCEITEMCITATIONPART");       
            	 String refitemcitationpublicationyear = rs.getString("REFITEMCITATIONPUBLICATIONYEAR");  
            	 String referenceitemcitationvolume = rs.getString("REFERENCEITEMCITATIONVOLUME");     
            	 String referenceitemcitationissue = rs.getString("REFERENCEITEMCITATIONISSUE");      
            	 String referencecitationpage = rs.getString("REFERENCEITEMCITATIONPAGE");       
            	 String refitemcitationarticlenumber = rs.getString("REFITEMCITATIONARTICLENUMBER");    
            	 String referenceitemcitationwebsite = rs.getString("REFERENCEITEMCITATIONWEBSITE");    
            	 String referenceitemcitationeaddress = rs.getString("REFERENCEITEMCITATIONEADDRESS");   
            	 String referenceitemcitationreftext = rs.getString("REFERENCEITEMCITATIONREFTEXT");    
            	 String referenceitemcitationtitle = rs.getString("REFERENCEITEMCITATIONTITLE");      
            	 String referenceitemcitationcoden = rs.getString("REFERENCEITEMCITATIONCODEN");      
            	 String refcitationsourcetitleabbrev = rs.getString("REFCITATIONSOURCETITLEABBREV");  
            	 String pui = rs.getString("PUI");
      
            	 
            	 file.write("<reference");
            	 //REFERENCEID
            	 if(referenceid!=null && referenceid.length()>0)
            	 {
            		 file.write(" id=\""+referenceid+"\">\n");
            	 }
            	 else
            	 {
            		 file.write(">\n");
            	 }
            	 file.write("<ref-info>\n");
            	 //REFERENCETITLE
            	 if(referencetitle!=null && referencetitle.length()>0)
            	 {
            		 
            		 file.write("<ref-title>\n");
            		 String[] titles = referencetitle.split(",");
            		 for(int i=0;i<titles.length;i++)
            		 {
            			 file.write("<ref-titletext>"+referencetitle+"</ref-titletext>\n");
            		 } 
            		 file.write("</ref-title>\n");
            	 }
            	 
            	 
            	if(referenceauthor!=null && referenceauthor.length()>0)
         		{
         			String[] referenceauthors = referenceauthor.split(Constants.AUDELIMITER);
         			file.write("<ref-authors>\n");
         			for(int i=0;i<referenceauthors.length;i++)
         			{
         				String rauthors = referenceauthors[i];       				
         				if(rauthors!=null && rauthors.length()>0)
         				{
         					String[] referenceauthorElements = rauthors.split(Constants.IDDELIMITER);
         					System.out.println("REFERENCE AUTHOR SIZE="+referenceauthorElements.length);
         					String rid = null;
         					String seq = null;
         					String auid = null;
         					String indexed_name = null;
         					String initials = null;
         					String surname = null;
         					String suffix = null;
         					String nametext = null;
         					
         					if(referenceauthorElements.length<2)
         					{
         						nametext = referenceauthorElements[0];
         						System.out.println("nametext="+nametext);
         					}
         					
         					if(referenceauthorElements.length>=4)
         					{
         						 seq = referenceauthorElements[0];
         						 rid = referenceauthorElements[1];
         						 auid = referenceauthorElements[2];
         						 indexed_name = referenceauthorElements[3];
         					}
         					
     						if(referenceauthorElements.length>=6)
     						{
     							 initials = referenceauthorElements[4];
     							surname = referenceauthorElements[5];
     						    System.out.println("initials="+initials);
     						    System.out.println("surname="+surname);
         					}
     						
     						if(referenceauthorElements.length>=9)
     						{
     							String ELEMENT6 = referenceauthorElements[6];
     							String ELEMENT7 = referenceauthorElements[7];
     							 suffix = referenceauthorElements[8];
     							if(ELEMENT6!=null && ELEMENT6.length()>0)
     								System.out.println("ELEMENT6="+ELEMENT6);
     							if(ELEMENT7!=null && ELEMENT7.length()>0)
     								System.out.println("ELEMENT7="+ELEMENT7);
     						    	System.out.println("suffix="+suffix);
         					}
         						
     						System.out.println("ref ID="+rid);
     						System.out.println("seq="+seq);
     						System.out.println("auid="+auid);
     						System.out.println("indexed_name="+indexed_name);
     						
     						file.write("<author");
     						if(seq!=null && seq.length()>0)
     						{
     							file.write(" seq=\""+seq+"\"");	    							
     						}
     						
     						/*
     						if(auid!=null && auid.length()>0)
     						{
     							file.write(" auid=\""+auid+"\"");	    							
     						}
     						*/
     						
     						file.write(">\n");
     						if(initials!=null && initials.length()>0)
     						{
     							file.write("<ce:initials>"+initials+"</ce:initials>\n");
     						}
     						if(indexed_name!=null && indexed_name.length()>0)
     						{
     							file.write("<ce:indexed_name>"+indexed_name+"</ce:indexed_name>\n");	    							
     						}
     						if(surname!=null && surname.length()>0)
     						{
     							file.write("<ce:surname>"+surname+"</ce:surname>\n");
     						}
     						
     						if(suffix!=null && suffix.length()>0)
     						{
     							file.write("<ce:suffix>"+suffix+"</ce:suffix>\n");    							
     						}
     						file.write("</author>\n");
         						
         					}
         				}
         				
         			}
            	 	file.write("</ref-authors>\n");
            	 	
            	 	//REFERENCESOURCETITLE
            	 	if(referencesourcetitle!=null && referencesourcetitle.length()>0)
            	 	{
            	 		file.write("<ref-sourcetitle>"+referencesourcetitle+"</ref-sourcetitle>\n");
            	 	}
            	 	
            	 	//REFERENCEPUBLICATIONYEAR
            	 	if(referencepublicationyear!=null && referencepublicationyear.length()>0)
            	 	{
            	 		file.write("<ref-publicationyear");
            	 		if(referencepublicationyear.indexOf("-")<0)
            	 		{
            	 			file.write(" first=\""+referencepublicationyear+"\"");
            	 		}
            	 		else
            	 		{
            	 			String[] publicationYears = referencepublicationyear.split("-");
            	 			if(publicationYears[0]!=null && publicationYears[0].length()>0)
            	 			{
            	 				file.write(" first=\""+publicationYears[0]+"\"");
            	 			}
            	 			
            	 			if(publicationYears[1]!=null && publicationYears[1].length()>0)
            	 			{
            	 				file.write(" last=\""+publicationYears[1]+"\"");
            	 			}
            	 			
            	 		}
            	 		file.write("/>\n");
            	 	}
            	 	
            	 	
            	 	//REFERENCEVOLUME
            	 	if((referencevolume!=null && referencevolume.length()>0) || (referenceissue!=null && referenceissue.length()>0) || (referencepages!=null && referencepages.length()>0))            	 	
            	 	{
            	 		file.write("<ref-volisspag>\n");
            	 		file.write("<voliss");
            	 		if(referencevolume!=null && referencevolume.length()>0)
            	 		{
            	 			file.write(" volume=\""+referencevolume+"\"");
            	 		}
            	 		
            	 		if(referenceissue!=null && referenceissue.length()>0)
            	 		{
            	 			file.write(" issue=\""+referenceissue+"\"");
            	 		}
            	 		
            	 		file.write("/>\n");
            	 		
            	 		if(referencepages!=null && referencepages.length()>0)
            	 		{
            	 			if(referencepages.indexOf("PAGES:")>-1)
            	 			{
            	 				file.write("<pages>"+referencepages.substring(6)+"\"</pages>\n");
            	 			}
            	 			else if(referencepages.indexOf("PAGERANGE:")>-1)
            	 			{
            	 				file.write("<pagerange");
            	 				referencepages = referencepages.substring(9);
            	 				if(referencepages.indexOf("-")>-1)
            	 				{
            	 					String[] referenceP = referencepages.split("-");
            	 					if(referenceP[0]!=null && referenceP[0].length()>0)
            	 					{
            	 						file.write(" first=\""+referenceP[0]+"\"");           	 						
            	 					}
            	 					if(referenceP[1]!=null && referenceP[1].length()>0)
            	 					{
            	 						file.write(" last=\""+referenceP[1]+"\"");           	 						
            	 					}
            	 					
            	 				}
            	 				else
            	 				{
            	 					file.write(" first=\""+referencepages+"\"");
            	 				}
            	 				file.write("/>\n");
            	 			}
            	 			else if(referencepages.indexOf("PAGECOUNT:")>-1)
            	 			{
            	 				file.write("<pagecount");
            	 				
            	 				String[] referenceP = referencepages.split(Constants.IDDELIMITER);
            	 				if(referenceP.length==1)
            	 				{
            	 					file.write(">"+referenceP[0]+"</pagecount>\n");
            	 				}
            	 				else
            	 				{
            	 					file.write(" type=\""+referenceP[0]+"\">"+referenceP[1]+"</pagecount>\n");
            	 				}
            	 			
            	 			}
            	 		
            	 		}           	 		            	 		
            	 		file.write("</ref-volisspag>\n");
            	 		file.write("</ref-info>\n");
            	 		//REFERENCEFULLTEXT
            	 		if(referencefulltext!=null && referencefulltext.length()>0)
            	 		{
            	 			file.write("<ref-fulltext>"+referencefulltext+"</ref-fulltext>");
            	 		}
            	 		
            	 		//REFERENCETEXT
            	 		if(referencetext!=null && referencetext.length()>0)
            	 		{
            	 			file.write("<ref-text>"+referencetext+"</ref-text>");
            	 		}
            	 		
            	 		//REFERENCEWEBSITE
            	 		if(referencewebsite!=null && referencewebsite.length()>0)
            	 		{
            	 			file.write("<ref-website>");
            	 			String[] referencewebsites = referencewebsite.split(Constants.IDDELIMITER);
            	 			if(referencewebsites[0]!=null && referencewebsites[0].length()>0)
            	 			{
            	 				file.write("<websitename>"+referencewebsites[0]+"</websitename>");
            	 			}
            	 			
            	 			if(referencewebsites[1]!=null && referencewebsites[1].length()>0)
            	 			{
            	 				file.write("<ce:e-address type=\"email\">"+referencewebsites[1]+"</ce:e-address>");
            	 			}
            	 			        	 			
            	 			file.write("</ref-website>");
            	 		}
            	 		
            	 		if((pui!=null && pui.length()>0) || (accessnumber!=null && accessnumber.length()>0))
            	 		{
            	 			file.write("<refd-itemidlist>");
            	 			file.write("<itemid>");
            	 			file.write("<itemid  idtype=\"CPX\">"+accessnumber+"</itemid>"); 
            	 			if(pui!=null && pui.length()>0)
            	 			{
            	 				file.write("<itemid  idtype=\"PUI\">"+pui+"</itemid>");
            	 			}
            	 			file.write("</itemid>");
            	 			file.write("</refd-itemidlist>");
            	 		}
            	 		
            	 		if((referenceitemcitationpii!=null && referenceitemcitationpii.length()>0) || 
            	 				(referenceitemcitationdoi!=null && referenceitemcitationdoi.length()>0) ||
            	 				(referenceitemcitationauthor!=null && referenceitemcitationauthor.length()>0) || 
            	 				(refitemcitationsourcetitle!=null && refitemcitationsourcetitle.length()>0) ||
            	 				(refcitationsourcetitleabbrev!=null && refcitationsourcetitleabbrev.length()>0) ||
            	 				(referenceitemcitationissn!=null && referenceitemcitationissn.length()>0) ||
            	 				(referenceitemcitationisbn!=null && referenceitemcitationisbn.length()>0) || 
            	 				(referenceitemcitationpart!=null && referenceitemcitationpart.length()>0) ||           	 				
            	 				(refitemcitationpublicationyear!=null && refitemcitationpublicationyear.length()>0) ||
            	 				(referenceitemcitationvolume!=null && referenceitemcitationvolume.length()>0) || 
            	 				(referenceitemcitationissue!=null && referenceitemcitationissue.length()>0) ||
            	 				(referencecitationpage!=null && referencecitationpage.length()>0) ||
            	 				(referenceitemcitationisbn!=null && referenceitemcitationisbn.length()>0) || 
            	 				(refitemcitationarticlenumber!=null && refitemcitationarticlenumber.length()>0) ||          	 				
            	 				(referenceitemcitationwebsite!=null && referenceitemcitationwebsite.length()>0) ||
            	 				(referenceitemcitationeaddress!=null && referenceitemcitationeaddress.length()>0) || 
            	 				(referenceitemcitationreftext!=null && referenceitemcitationreftext.length()>0) ||
            	 				(referenceitemcitationtitle!=null && referenceitemcitationtitle.length()>0) ||
            	 				(referenceitemcitationcoden!=null && referenceitemcitationcoden.length()>0))
            	 		{
            	 			file.write("<refd-itemcitation  type=\"core\">\n");  
            	 			if(referenceitemcitationpii!=null && referenceitemcitationpii.length()>0)
            	 			{
            	 				file.write("<ce:pii>"+referenceitemcitationpii+"</ce:pii>\n");
            	 			}
            	 			
            	 			if(referenceitemcitationdoi!=null && referenceitemcitationdoi.length()>0)
            	 			{
            	 				file.write("<ce:doi>"+referenceitemcitationdoi+"</ce:doi>\n");
            	 			}
            	 			
            	 			if(refitemcitationsourcetitle!=null && refitemcitationsourcetitle.length()>0)
            	 			{
            	 				file.write("<sourcetitle>"+refitemcitationsourcetitle+"</sourcetitlei>\n");
            	 			}
            	 			
            	 			if(refcitationsourcetitleabbrev!=null && refcitationsourcetitleabbrev.length()>0)
            	 			{
            	 				file.write("<sourcetitle-abbrev>"+refcitationsourcetitleabbrev+"</sourcetitle-abbrev>\n");
            	 			}
 	 				         	 			
            	 			if(referenceitemcitationissn!=null && referenceitemcitationissn.length()>0)
            	 			{
            	 				if(referenceitemcitationissn.indexOf(Constants.IDDELIMITER)>-1)
            	 				{
	            	 				String[] issns = referenceitemcitationissn.split(Constants.IDDELIMITER);
	            	 				for(int i=0;i<issns.length;i++)
	            	 				{
	            	 					String issn = issns[i];
	            	 					if(issn!=null && issn.length()>0)
	            	 					{
	            	 						if(issn.indexOf(":")>-1)
	            	 						{
	            	 							String[] issnElements = issn.split(":");
	            	 							file.write("<issn type=\""+issnElements[0]+"\">"+issnElements[1]+"</issn>\n");
	            	 						}
	            	 						else
	            	 						{
	            	 							file.write("<issn type=\"print\">"+issn+"</issn>\n");
	            	 						}
	            	 						
	            	 					}
	            	 				}
            	 				}
            	 				else
            	 				{
            	 					if(referenceitemcitationissn.indexOf(":")>-1)
            	 					{
            	 						String[] issnElements = referenceitemcitationissn.split(":");
        	 							file.write("<issn type=\""+issnElements[0]+"\">"+issnElements[1]+"</issn>\n");           	 						
            	 					}
            	 					else
            	 					{
            	 						file.write("<issn type=\"print\">"+referenceitemcitationissn+"</issn>\n");
            	 					}           	 					
            	 				}
            	 			}
            	 			
            	 			if(referenceitemcitationisbn!=null && referenceitemcitationisbn.length()>0)
            	 			{
            	 				String[] isbns = referenceitemcitationisbn.split(Constants.AUDELIMITER);
            	 				for(int i=0;i<isbns.length;i++)
            	 				{
            	 					String isbnArray = isbns[i];
            	 					if(isbnArray!=null && isbnArray.length()>0)
            	 					{
            	 						String[] isbnElements = isbnArray.split(Constants.IDDELIMITER);
            	 						if(isbnElements.length==4)
            	 						{
            	 							String type = isbnElements[0];
            	 							String length = isbnElements[1];
            	 							String level = isbnElements[2];
            	 							String value = isbnElements[3];
            	 							file.write("<isbn");
            	 							if(type!=null && type.length()>0)
            	 							{
            	 								file.write(" type=\""+type+"\"");
            	 							}
            	 							
            	 							if(length!=null && length.length()>0)
            	 							{
            	 								file.write(" length=\""+length+"\"");
            	 							}
            	 							
            	 							if(level!=null && level.length()>0)
            	 							{
            	 								file.write(" level=\""+level+"\"");
            	 							}
            	 							
            	 							file.write(">"+value+"</isbn>\n");
            	 						}
            	 						else
            	 						{
            	 							System.out.println("records "+accessnumber+" has wrong format in referenceitemcitationisbn");
            	 						}//if
            	 					}//if
            	 				}//for
            	 			}//if
            	 			
            	 			//REFERENCEITEMCITATIONPART
            	 			if(referenceitemcitationpart!=null && referenceitemcitationpart.length()>0)
            	 			{
            	 				file.write("<part>"+referenceitemcitationpart+"</part>");
            	 			}
            	 			
            	 			//REFITEMCITATIONPUBLICATIONYEAR
            	 			if(refitemcitationpublicationyear!=null && refitemcitationpublicationyear.length()>0)
            	 			{
            	 				file.write("<publicationyear");
            	 				if(refitemcitationpublicationyear.indexOf("-")<0)
                    	 		{
                    	 			file.write(" first=\""+refitemcitationpublicationyear+"\"");
                    	 		}
                    	 		else
                    	 		{
                    	 			String[] publicationYears = refitemcitationpublicationyear.split("-");
                    	 			if(publicationYears[0]!=null && publicationYears[0].length()>0)
                    	 			{
                    	 				file.write(" first=\""+publicationYears[0]+"\"");
                    	 			}
                    	 			
                    	 			if(publicationYears[1]!=null && publicationYears[1].length()>0)
                    	 			{
                    	 				file.write(" last=\""+publicationYears[1]+"\"");
                    	 			}                  	 			
                    	 		}
                    	 		file.write("/>\n");            	 				
            	 			}
            	 			
            	 			//REFERENCEITEMCITATIONVOLUME
            	 			if((referenceitemcitationvolume!=null && referenceitemcitationvolume.length()>0) ||
            	 					(referenceitemcitationissue!=null && referenceitemcitationissue.length()>0) ||
            	 					(referencecitationpage!=null && referencecitationpage.length()>0))
            	 			{
            	 				file.write("<volisspag>\n");
                    	 		file.write("<voliss");
                    	 		if(referenceitemcitationvolume!=null && referenceitemcitationvolume.length()>0)
                    	 		{
                    	 			file.write(" volume=\""+referenceitemcitationvolume+"\"");
                    	 		}
                    	 		
                    	 		if(referenceitemcitationissue!=null && referenceitemcitationissue.length()>0)
                    	 		{
                    	 			file.write(" issue=\""+referenceitemcitationissue+"\"");
                    	 		}
                    	 		
                    	 		file.write("/>\n");
                    	 		
                    	 		if(referencecitationpage!=null && referencecitationpage.length()>0)
                    	 		{
                    	 			if(referencecitationpage.indexOf("PAGES:")>-1)
                    	 			{
                    	 				file.write("<pages>"+referencecitationpage.substring(6)+"\"</pages>\n");
                    	 			}
                    	 			else if(referencecitationpage.indexOf("PAGERANGE:")>-1)
                    	 			{
                    	 				file.write("<pagerange");
                    	 				referencecitationpage = referencecitationpage.substring(9);
                    	 				if(referencecitationpage.indexOf("-")>-1)
                    	 				{
                    	 					String[] referenceP = referencecitationpage.split("-");
                    	 					if(referenceP[0]!=null && referenceP[0].length()>0)
                    	 					{
                    	 						file.write(" first=\""+referenceP[0]+"\"");           	 						
                    	 					}
                    	 					if(referenceP[1]!=null && referenceP[1].length()>0)
                    	 					{
                    	 						file.write(" last=\""+referenceP[1]+"\"");           	 						
                    	 					}
                    	 					
                    	 				}
                    	 				else
                    	 				{
                    	 					file.write(" first=\""+referencecitationpage+"\"");
                    	 				}
                    	 				file.write("/>\n");
                    	 			}
                    	 			else if(referencepages.indexOf("PAGECOUNT:")>-1)
                    	 			{
                    	 				file.write("<pagecount");
                    	 				
                    	 				String[] referenceP = referencecitationpage.split(Constants.IDDELIMITER);
                    	 				if(referenceP.length==1)
                    	 				{
                    	 					file.write(">"+referenceP[0]+"</pagecount>\n");
                    	 				}
                    	 				else
                    	 				{
                    	 					file.write(" type=\""+referenceP[0]+"\">"+referenceP[1]+"</pagecount>\n");
                    	 				}
                    	 			
                    	 			}
                    	 		
                    	 		}           	 		            	 		
                    	 		file.write("</volisspag>\n");
            	 				
            	 			}
            	 			
            	 			//
            	 			if(refitemcitationarticlenumber!=null && refitemcitationarticlenumber.length()>0)
            	 			{
            	 				file.write("<article-number>"+refitemcitationarticlenumber+"</article-number>\n");
            	 			}
            	 			
            	 			
            	 			if((referenceitemcitationwebsite!=null && referenceitemcitationwebsite.length()>0) ||
            	 					(referenceitemcitationeaddress!=null && referenceitemcitationeaddress.length()>0) )	
            	 			{
            	 				file.write("<website>\n");
            	 				 
            	 				//REFERENCEITEMCITATIONWEBSITE
                	 			if(referenceitemcitationwebsite!=null && referenceitemcitationwebsite.length()>0)
                	 			{
                	 				file.write("<websitename>"+referenceitemcitationwebsite+"</websitename>\n");
                	 			}
                	 			
                	 			//REFERENCEITEMCITATIONEADDRESS
                	 			if(referenceitemcitationeaddress!=null && referenceitemcitationeaddress.length()>0)
                	 			{
                	 				file.write("<ce:e-address");
                	 				String[] referenceeaddress = referenceitemcitationeaddress.split(Constants.IDDELIMITER);
                	 				if(referenceeaddress[0]!=null && referenceeaddress[0].length()>0)
                	 				{
                	 					file.write(" type=\""+referenceeaddress[0]+"\"");
                	 				}
                	 				file.write(">");
                	 				if(referenceeaddress[1]!=null && referenceeaddress[1].length()>0)
                	 				{
                	 					file.write(referenceeaddress[1]);
                	 				}
                	 				file.write("</ce:e-address>\n");
                	 			}
            	 				
            	 				file.write("</website>\n");
            	 			}
            	 			
            	 			if(referenceitemcitationreftext!=null && referenceitemcitationreftext.length()>0)
            	 			{
            	 				file.write("<ref-text>"+referenceitemcitationreftext+"</ref-text>\n");
            	 			}
            	 			 
            	 			if(referenceitemcitationtitle!=null && referenceitemcitationtitle.length()>0)
            	 			{
            	 				file.write("<ce:citation-title>\n");
            	 				String[] refTitles = referenceitemcitationtitle.split(Constants.AUDELIMITER);
            	 				for(int i=0;i<refTitles.length;i++)
            	 				{
            	 					String refTitle = refTitles[i];
            	 					if(refTitle!=null && refTitle.length()>0)
            	 					{
            	 						file.write("<titletext");
            	 						String[] refTitleElements = refTitle.split(Constants.IDDELIMITER);
            	 						if(refTitleElements.length==4)
            	 						{
            	 							String id = refTitleElements[0];
            	 							String original = refTitleElements[1];
            	 							String lang = refTitleElements[2];
            	 							String titlecontent = refTitleElements[3];
            	 							if(original!=null && original.length()>0)
            	 							{
            	 								file.write(" original=\""+original+"\"");
            	 							}
            	 							
            	 							if(lang!=null && lang.length()>0)
            	 							{
            	 								file.write(" lang=\""+lang+"\"");
            	 							}
            	 							
            	 							file.write(">"+titlecontent+"</titletext\n>");
            	 							
            	 						}
            	 						else
            	 						{
            	 							System.out.println("record "+accessnumber+" has wrong referenceitemcitationtitle format");
            	 						}
            	 					}
            	 					 
            	 				}
            	 				
            	 				file.write("</ce:citation-title>\n");
            	 			}
            	 			
            	 			//CODEN
            	 			if(referenceitemcitationcoden!=null && referenceitemcitationcoden.length()>0)
            	 			{
            	 				file.write("<codencode>"+referenceitemcitationcoden+"</codencode>");
            	 			}
            	 			
	            	 		if(referenceitemcitationauthor!=null && referenceitemcitationauthor.length()>0)
	            	 		{
	            	 			
	            	 		}
            	 		
	            	 		file.write("</refd-itemcitation>\n");
            	 		}               	
            	 	}            	           	 	
         		}
         }
         catch (Exception e)
         {
             System.err.println(e);
             System.exit(1);
         }
         finally
         {
             if(rs != null)
             {
            	 try{
            		 rs.close();
            	 }
            	 catch(Exception er)
            	 {
            		 System.err.println(er); 
            	 }
             }
             
             if(stmt != null)
             {
            	 try{
            		 stmt.close();
            	 }
            	 catch(Exception es)
            	 {
            		 System.err.println(es); 
            	 }
             }
         }
    }
    public void add(Map authorGroup, String key, BdAuthor newValue) {
        List<BdAuthor> currentValue = (List)authorGroup.get(key);
        if (currentValue == null) {
            currentValue = new ArrayList<BdAuthor>();          
        }
        currentValue.add(newValue);
        authorGroup.put(key, currentValue);
    }

     public Connection getConnection(String connectionURL,
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
