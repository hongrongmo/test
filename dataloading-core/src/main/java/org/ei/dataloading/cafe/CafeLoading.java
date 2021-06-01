package org.ei.dataloading.cafe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ei.common.Constants;
import org.ei.common.bd.BdAffiliation;
import org.ei.common.bd.BdAffiliations;
import org.ei.common.bd.BdAuthor;
import org.ei.common.bd.BdAuthors;
import org.ei.dataloading.CombinedXMLWriter;
import org.ei.dataloading.DataLoadDictionary;
import org.ei.dataloading.EVCombinedRec;
import org.ei.dataloading.awss3.AmazonS3Service;
import org.ei.dataloading.bd.loadtime.ApiAtm;
import org.ei.dataloading.bd.loadtime.BaseTableDriver;
import org.ei.dataloading.bd.loadtime.BaseTableWriter;
import org.ei.dataloading.bd.loadtime.XmlCombiner;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.Namespace;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.jdom2.Attribute;
import org.jdom2.Document;                  //// replace svn jdom with recent jdom2
import org.ei.util.GUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.xml.sax.InputSource;
import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;
import org.ei.dataloading.bd.loadtime.BdParser;

public class CafeLoading
{
	private static BaseTableWriter baseWriter;
    private String loadNumber = null;
    private int updatenumber = 0;
    private String databaseName="cpx";
    private int batchsize = 0;
    private static String startRootElement ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><bibdataset xmlns:xocs=\"http://www.elsevier.com/xml/xocs/dtd\" xmlns:ce=\"http://www.elsevier.com/xml/common/dtd\" xmlns:xoe=\"http://www.elsevier.com/xml/xoe/dtd\" xmlns:aii=\"http://www.elsevier.com/xml/ani/internal\" xmlns:mml=\"http://www.w3.org/1998/Math/MathML\" xmlns:ait=\"http://www.elsevier.com/xml/ait/dtd\">";
    private static String endRootElement   ="</bibdataset>";
    private static Connection con;
    private static String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
    private static String driver = "oracle.jdbc.driver.OracleDriver";
    private static String username = "ap_ev_search";
    private static String password = "ei3it";
    private Map processingRecords = new HashMap();
    private static List<String> blockedCpxIssnList;
    private static List<String> blockedPchIssnList;
    private static List<String> blockedGeoIssnList;
    private static List<String> cittypeList;
    private Namespace xocsNamespace=Namespace.getNamespace("xocs","http://www.elsevier.com/xml/xocs/dtd");
    private Namespace noNamespace=Namespace.getNamespace("","");
    private Namespace ceNamespace=Namespace.getNamespace("ce","http://www.elsevier.com/xml/ani/common");
    private Namespace xoeNamespace=Namespace.getNamespace("xoe","http://www.elsevier.com/xml/xoe/dtd");
    private Namespace aitNamespace=Namespace.getNamespace("ait","http://www.elsevier.com/xml/ani/ait");
    private Namespace xmlNamespace=Namespace.getNamespace("xml","http://www.w3.org/XML/1998/namespace");
    
    private Perl5Util perl = new Perl5Util();
    BufferedReader bfReader = null;
    boolean cafe = false;
    public String s3FileLoc = "";
    private int affid = 0;
    private boolean inabstract = false;
	private HashSet entity = null;
	private String propertyFileName = "lib/config.properties";
	BdParser b = new BdParser();
	CombinedXMLWriter writer = new CombinedXMLWriter(50000,updatenumber,databaseName,"dev");

	XmlCombiner x = new XmlCombiner(writer,propertyFileName,databaseName);
	
	private static int totalCount = 0;
	
    public static void main(String args[])
        throws Exception

    {
        
        long startTime = System.currentTimeMillis();
        String archivedDate="0";
        if(args.length<3)
        {
            System.out.println("please enter three parameters as \" weeknumber filename databaseName action url driver username password\"");
            System.exit(1);
        }
        
        archivedDate = args[0];     
        String databaseName = args[1];
        String size = args[2];

        if(args.length>3)
        {
            url = args[3];
            driver = args[4];
            username = args[5];
            password = args[6];         
        }
        else
        {
            System.out.println("USING DEFAULT DATABASE SETTING");
            System.out.println("DATABASE URL= "+url);
            System.out.println("DATABASE USERNAME= "+username);
            System.out.println("DATABASE PASSWORD= "+password);
        }
        CafeLoading c;
        Statement updateStatement = null;
        Statement selectStatement = null;
        
        try
        {    
            c = new CafeLoading(archivedDate,databaseName);    
            c.updatenumber= Integer.parseInt(c.loadNumber);
            c.batchsize = Integer.parseInt(size);
            con = c.getConnection(url,driver,username,password);
            updateStatement = con.createStatement();
            //PreparedStatement selectStatement =con.prepareStatement("select loadnumber from cpx_metadata where eid in ?");
            selectStatement = con.createStatement();
            //c.setBlockedIssnList(con);
            List cafekeyList = c.getCafeKeys(archivedDate,con);
            totalCount = cafekeyList.size();
            if(totalCount>0)
            {
	            for(int i=0;i<cafekeyList.size();i++)
	            {            	
	            	String cafeKey = (String)cafekeyList.get(i);
	            	String cafeDoc = c.getCafeDocFromS3(selectStatement,updateStatement,cafeKey, i);
	            	
	            }
	            
	            c.saveContentToDatabase(selectStatement,updateStatement,c.updatenumber,c.processingRecords);
            }
            else
            {
            	System.out.println("Sorry, No sns message found for archivedDate="+archivedDate);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
        	if(updateStatement!=null)
        	{        		
        		try
                {
        			updateStatement.executeBatch();
        			updateStatement.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
        	}
        	if(selectStatement!=null)
        	{        		
        		try
                {
        			selectStatement.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
        	}
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
    
    private void processData(Statement selectStatement,Statement updateStatement,String cafeDoc, int index)
    {
    	  String carID = null;
    	  String accessnumber = null;
    	  String pui = null;
    	  String eid = null;
    	  String author = null;
    	  String author_1 = null;
    	  String affiliation = null;
    	  String affiliation_1 = null;
    	  //String loadnumber = null;
    	  int updatenumber= Integer.parseInt(this.loadNumber);
    	  Statement stmt = null;
          ResultSet rs = null;
          BdParser parser = new BdParser();
          try
          {
        	SAXBuilder builder = new SAXBuilder();
			builder.setExpandEntities(false);
			builder.setFeature( "http://xml.org/sax/features/namespaces", true );
			//System.out.println(cafeDoc);
			Document doc = builder.build(new InputSource(new StringReader(cafeDoc)));
      		Element cpxRoot = doc.getRootElement();
      		Element docRoot = cpxRoot.getChild("doc",xocsNamespace);
      		
      		Element meta = docRoot.getChild("meta",xocsNamespace);
      		
      		eid = meta.getChildText("eid",xocsNamespace);
      		//System.out.println("eid="+eid);
      		Element itemE = docRoot.getChild("item",xocsNamespace);
      		Element item = itemE.getChild("item",noNamespace);
    		Element bibrecord = item.getChild("bibrecord",noNamespace);
    		//System.out.println("bibrecord="+bibrecord.getName());
    		Element head = bibrecord.getChild("head",noNamespace);
    		//System.out.println("head="+head.getName());
    		Element itemInfo = bibrecord.getChild("item-info",noNamespace);
    		//System.out.println("itemInfo="+itemInfo.getName());
    		Element itemIdList = itemInfo.getChild("itemidlist",noNamespace);
    		//System.out.println("itemIdList="+itemIdList.getName());
    		List itemids = itemIdList.getChildren("itemid",noNamespace);
    		for(int i=0;i<itemids.size();i++)
    		{
    			Element itemid = (Element)itemids.get(i);
    			if(itemid.getAttributeValue("idtype",noNamespace).equalsIgnoreCase("PUI"))
    			{
    				pui = itemid.getTextTrim();    	
    			}
    			else if(itemid.getAttributeValue("idtype",noNamespace).equalsIgnoreCase("CAR-ID"))
    			{
    				carID = itemid.getTextTrim();
    				if(carID==null)
    				{
    					carID="";
    				}
    				//System.out.println("carID="+carID);
    			}
    			else if(itemid.getAttributeValue("idtype",noNamespace).equalsIgnoreCase("CPX"))  				
    			{
    				accessnumber = itemid.getTextTrim();
    				//System.out.println("accessnumber="+accessnumber);
    			}
    		}
    		   		
    		List authorgroup = head.getChildren("author-group",noNamespace);
    		String auid ="";
    		String afid ="";
    		if(authorgroup!=null)
    		{
	    		//Map auAfIds = getAuthorIDAndAffiliationID(authorgroup);
    			Map auAfIds = getAuthorIDAndAffiliationID(authorgroup,"fullauthor");
	    		auid = (String)auAfIds.get("author");
	    		afid = (String)auAfIds.get("affiliation");
    		}
    		//System.out.println("afid="+afid+" auid="+auid);
    		HashMap record = new HashMap();
    		record.put("eid", eid);
    		record.put("pui", pui);
    		record.put("accessnumber", accessnumber);
    		record.put("carID", carID);
    		record.put("auid", auid);
    		record.put("afid", afid);
    		processingRecords.put(eid,record);
   		    if(processingRecords.size()>batchsize)
   		    {
   		    	saveContentToDatabase(selectStatement,updateStatement,updatenumber,processingRecords);
   		    	processingRecords = new HashMap();
   		    }
    		//saveContentToDatabase(selectStatement,updateStatement,eid,pui,accessnumber,carID,updatenumber,auid,afid,index);    		
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
              
          }
    }
    
    private void saveContentToDatabase(Statement selectStatement,Statement updateStatement,int updatenumber,Map processingRecords)
    {
    	try
    	{
	    	Iterator itr = processingRecords.keySet().iterator();
	    	StringBuffer selectSquery = new StringBuffer("select eid from cpx_metadata where eid in (");
	    	String pui = null;
	    	String accessnumber = null;
	    	String carid = null;
	    	String auid = null;
	    	String afid = null;
	    	String eid = null;
	    	
	    	int index =0;
	    	while (itr.hasNext())
	    	{
	    	    eid = (String)itr.next();  
	    	    if(index!=0)
	    	    {
	    	    	selectSquery.append(",");
	    	    }
	    	    selectSquery.append("'"+eid+"'");
	    	    index++;
	    	}
	    	selectSquery.append(")");
	    	//System.out.println("select query= "+selectSquery.toString());
	    	ResultSet rs = selectStatement.executeQuery(selectSquery.toString());
	    	while(rs.next())
	    	{
	    		eid = rs.getString("eid");
	    		Map recordMap = (HashMap)processingRecords.get(eid);
	    		pui = (String)recordMap.get("pui");
	    		accessnumber = (String)recordMap.get("accessnumber");
	    		carid = (String)recordMap.get("carID");
	    		auid = (String)recordMap.get("auid");
	    		afid = (String)recordMap.get("afid");
	    		
	    		String updateQuery ="update cpx_metadata set pui='"+pui+"', an='"+accessnumber+"', carid='"
    	    			+ carid+"', updatenumber="+updatenumber+", author='"+auid+"', affiliation='"+afid+"'"
    	    			+ " where eid='"+eid+"'"; 
    			updateStatement.addBatch(updateQuery);
    			//System.out.println("updateQuery="+updateQuery);
    			processingRecords.remove(eid);
	    	}
	    	
	    	if(processingRecords.size()>0)
	    	{	    	
	    		itr = processingRecords.values().iterator();
	    		while (itr.hasNext())
		    	{
	    			Map recordMap = (HashMap)itr.next(); 
	    			pui = (String)recordMap.get("pui");
		    		accessnumber = (String)recordMap.get("accessnumber");
		    		carid = (String)recordMap.get("carID");
		    		auid = (String)recordMap.get("auid");
		    		afid = (String)recordMap.get("afid");
		    		eid = (String)recordMap.get("eid");
		    		int loadnumber = Integer.parseInt(this.loadNumber.substring(0, 6));
		    		
		    		String insertQuery = "INSERT INTO cpx_metadata(EID, PUI, AN, CARID, LOADNUMBER, UPDATENUMBER,AUTHOR,AFFILIATION) "
		    		    	+ "VALUES ('"+eid+"','"+pui+"','"+accessnumber+"','"+carid+"',"+loadnumber+","+updatenumber+",'"+auid+"','"+afid+"')";
					updateStatement.addBatch(insertQuery);
					//System.out.println("insertQuery="+insertQuery);
		    	}
	    	}
	    	updateStatement.executeBatch();
	    	
    	}
	    catch(Exception	e)
    	{
	    	e.printStackTrace();
    	}
	    
    }
    
    /*
    private void saveContentToDatabase(Statement selectStatement, Statement updatestatement ,String eid,String pui,String accessnumber,
    		String carid,int updatenumber, String auid, String afid, int index)
    {
    	
    	try
    	{
	    	selectStatement.setString(1,eid);
	    	ResultSet rs = selectStatement.executeQuery();
	    	int loadnumber = Integer.parseInt(this.loadNumber.substring(0, 6));
	    	if(rs.next() )
	    	{
	    		int oldLoadNumber = rs.getInt("loadnumber");
	    		//System.out.println("LOADNUMBER="+oldLoadNumber);
	    		if(oldLoadNumber!=0)
	    		{
	    			String updateQuery ="update cpx_metadata set pui='"+pui+"', an='"+accessnumber+"', carid='"
	    	    			+ carid+"', updatenumber="+updatenumber+", author='"+auid+"', affiliation='"+afid+"'"
	    	    			+ "where eid='"+eid+"'"; 
	    			updatestatement.addBatch(updateQuery);
	    			//System.out.println("UPDATE="+updateQuery);
	    		}
	    		
	    	}
	    	else
			{
	    		
	    		String insertQuery = "INSERT INTO cpx_metadata(EID, PUI, AN, CARID, LOADNUMBER, UPDATENUMBER,AUTHOR,AFFILIATION) "
	    		    	+ "VALUES ('"+eid+"','"+pui+"','"+accessnumber+"','"+carid+"',"+loadnumber+","+updatenumber+",'"+auid+"','"+afid+"')";
				updatestatement.addBatch(insertQuery);
				//System.out.println("INSERT="+insertQuery);
			}
	    	
	    	if(index%200==0)
	    	{
	    		updatestatement.executeBatch();
	    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    }
    */
    
    private Map getAuthorIDAndAffiliationID(List authorGroups)
    {
    	Map authorgroupMap = new HashMap();
    	StringBuffer authorIdBuffer = new StringBuffer();
    	StringBuffer affIdBuffer = new StringBuffer();
    	
    	for(int i=0;i<authorGroups.size();i++)
    	{
    		Element authorgroup = (Element)authorGroups.get(i);
    		List authors = authorgroup.getChildren("author");
    		if(authors!=null)
    		{
	    		for(int j=0;j<authors.size();j++)
	    		{
	    			Element author = (Element)authors.get(j);
	    			String authorID = author.getAttributeValue("auid");
	    			String surname = author.getChildText("surname",ceNamespace);
	    			System.out.println("SURENAME="+surname);
	    			if(authorIdBuffer.length()>0)
	    			{
	    				authorIdBuffer.append(";"+authorID);
	    			}
	    			else
	    			{
	    				authorIdBuffer.append(authorID);
	    			}
	    		}
    		}
    		
    		Element affiliation = authorgroup.getChild("affiliation");
    		
    		if(affiliation!=null && affiliation.getAttributeValue("afid")!=null)
    		{
    			String afid = affiliation.getAttributeValue("afid");
    			if(affIdBuffer.length()>0)
    			{
    				affIdBuffer.append(";"+afid);
    			}
    			else
    			{
    				affIdBuffer.append(afid);
    			}
    		}
    	}
    	authorgroupMap.put("auid", authorIdBuffer.toString());
    	authorgroupMap.put("afid", affIdBuffer.toString());
    	return authorgroupMap;
    	
    }
    
    public void setAuthorAndAffs(BdAuthors ausmap,
			  BdAffiliations affs,
			  Element aelement)
	{
		List authorlist = aelement.getChildren("author",noNamespace);
		
		if(authorlist==null || authorlist.size()==0)
		{
			authorlist = aelement.getChildren("contributor",noNamespace);
		}
		
		//affiliations
		
		Element affiliation =(Element) aelement.getChild("affiliation",noNamespace);
		
		if(affiliation != null)
		{
		BdAffiliation aff = new BdAffiliation();
		aff.setAffCountry(affiliation.getAttributeValue("country"));
		Element addresspart =(Element) affiliation.getChild("address-part",noNamespace);
		if(addresspart != null)
		{
			aff.setAffAddressPart(DataLoadDictionary.mapEntity(getMixData(addresspart.getContent())));
		}
		
		Element citygroup = (Element) affiliation.getChild("city-group",noNamespace);
		if(citygroup != null)
		{
		aff.setAffCityGroup(DataLoadDictionary.mapEntity(getMixData(citygroup.getContent())));
		}
		
		Element city = (Element) affiliation.getChild("city",noNamespace);
		if(city != null)
		{
		aff.setAffCity(DataLoadDictionary.mapEntity(getMixData(city.getContent())));
		}
		
		Element state = (Element) affiliation.getChild("state",noNamespace);
		if(state != null)
		{
		aff.setAffState(DataLoadDictionary.mapEntity(state.getText()));
		}
		
		List postalcode = affiliation.getChildren("postal-code",noNamespace);
		StringBuffer zipbuf = new StringBuffer();
		for (int i = 0; i<postalcode.size(); i++)
		{
		Element elmpostalcode = (Element)postalcode.get(i);
		if(postalcode != null)
		{
		Attribute zip = elmpostalcode.getAttribute("zip");
		if(zip != null)
		{
		  zipbuf.append((String) zip.getValue());
		
		  if(i < postalcode.size()-1)
		  {
		      zipbuf.append(", ");
		  }
		  //System.out.println("ZIP1="+zipbuf.toString());
		}
		
		String zipvalue=elmpostalcode.getTextTrim();
		if(zipvalue!=null)
		{			        	
			zipbuf.append(DataLoadDictionary.mapEntity(zipvalue));
			 if(i < postalcode.size()-1)
		   {
		          zipbuf.append(", ");
		   }
			 //System.out.println("ZIP2="+zipbuf.toString());
		}
		}
		}
		if(zipbuf.length() > 0)
		{
		
		aff.setAffPostalCode(DataLoadDictionary.mapEntity(DataLoadDictionary.mapEntity(zipbuf.toString())));
		}
		
		Element text = (Element) affiliation.getChild("text",ceNamespace);
		if(text != null)
		{
		aff.setAffText(DataLoadDictionary.mapEntity(getMixData(text.getContent())));
		}
		
		// organization  - mulity element
		List organization = affiliation.getChildren("organization",noNamespace);
		for (int i = 0; i < organization.size(); i++)
		{
		Element oe = (Element) organization.get(i);
		aff.addAffOrganization(DataLoadDictionary.mapEntity(getMixData(oe.getContent())));
		}
		
		this.affid = this.affid+1;
		aff.setAffid(this.affid);
		
		if(affiliation.getAttributeValue("afid")!=null)
		{
		aff.setAffiliationId(affiliation.getAttributeValue("afid"));
		}
		
		if(affiliation.getAttributeValue("dptid")!=null)
		{
		aff.setAffDepartmentId(affiliation.getAttributeValue("dptid"));
		}
		
		affs.addAff(aff);
		}
		
		//end of affiliation
		
		//begin authors
		for (int e = 0; e< authorlist.size(); e++)
		{
		Element agroupelement =(Element) authorlist.get(e);
		
		BdAuthor aus = new BdAuthor();
		Element author = (Element) authorlist.get(e);
		List atr = author.getAttributes();
		Attribute sec = author.getAttribute("seq");
		aus.setAffid(this.affid);
		
		if(sec != null)
		{
		String secstr = (String) sec.getValue();
		aus.setSec(secstr);
		}
		
		Attribute auid = author.getAttribute("orcid");
		Attribute authorid = author.getAttribute("auid");
		StringBuffer authoridBuffer = new StringBuffer();
		if(auid != null || authorid!=null)
		{
			if(auid != null)
			{
				String auidstr = (String) auid.getValue();
				authoridBuffer.append(auidstr);
			}
			
			//authoridBuffer.append(",");
			
			if(authorid!=null)
			{
				String authoridstr = (String)authorid.getValue();
				authoridBuffer.append(","+authoridstr);
			}
		
		
		}
		
		if(authoridBuffer.length()>0)
		{
			aus.setAuid(authoridBuffer.toString());
			//System.out.println("AUTHORID="+authoridBuffer.toString());
		}
		
		Element indexedName = author.getChild("indexed-name",ceNamespace );
		if(indexedName != null)
		{
			aus.setIndexedName(DataLoadDictionary.mapEntity(getMixData(indexedName.getContent())));
		}
		
		Element initials = author.getChild("initials",ceNamespace);
		if(initials != null)
		{
			//System.out.println("initials="+initials);
			aus.setInitials(DataLoadDictionary.mapEntity(initials.getText()));
		}
		
		Element degrees = author.getChild("degrees", ceNamespace);
		if(degrees != null)
		{
			aus.setDegrees(DataLoadDictionary.mapEntity(degrees.getText()));		
		}
		
		Element surname = author.getChild("surname", ceNamespace);
		
		if(surname != null)
		{
			String sureNameString = getMixData(surname.getContent());			
			aus.setSurname(DataLoadDictionary.mapEntity(getMixData(surname.getContent())));
			//aus.setSurname(getMixData(surname.getContent()));
		}
		
		Element givenName = author.getChild("given-name", ceNamespace);
		if(givenName != null)
		{
		String givenNameString = getMixData(givenName.getContent());
		//outputIntoCharNumber(givenNameString);		
		aus.setGivenName(DataLoadDictionary.mapEntity(getMixData(givenName.getContent())));
		//aus.setGivenName(getMixData(givenName.getContent()));
		}
		
		Element suffix = author.getChild("suffix",ceNamespace);
		if(suffix != null)
		{
		aus.setSuffix(DataLoadDictionary.mapEntity(suffix.getText()));
		}
		
		Element nametext= author.getChild("nametext", ceNamespace);
		if(nametext != null)
		{
		aus.setNametext(DataLoadDictionary.mapEntity(getMixData(nametext.getContent())));
		}
		
		Element prefferedName= author.getChild("preffered-name", ceNamespace);
		
		// prefferedName block
		
		if(prefferedName != null)
		{
		Element prefferedNameInitials = prefferedName.getChild("initials",ceNamespace);
		if(prefferedNameInitials != null)
		{
		aus.setPrefnameInitials(DataLoadDictionary.mapEntity(getMixData(prefferedNameInitials.getContent())));
		}
		
		Element prefferedNameIndexedname = prefferedName.getChild("indexed_name",ceNamespace);
		if(prefferedNameIndexedname != null)
		{
		
		aus.setPrefnameIndexedname(DataLoadDictionary.mapEntity(getMixData(prefferedNameIndexedname.getContent())));
		}
		
		Element prefferedNameDegrees = prefferedName.getChild("degree", ceNamespace);
		if(prefferedNameDegrees != null)
		{
		aus.setPrefnameDegrees(DataLoadDictionary.mapEntity(prefferedNameDegrees.getText()));
		}
		
		Element prefferedNameSurname = prefferedName.getChild("surname", ceNamespace);
		if(prefferedNameSurname != null)
		{
		aus.setPrefnameSurname(DataLoadDictionary.mapEntity(getMixData(prefferedNameSurname.getContent())));
		}
		Element prefferedNameGivenname = prefferedName.getChild("given-name", ceNamespace);
		
		if(prefferedNameGivenname != null)
		{
		aus.setPrefnameGivenname(DataLoadDictionary.mapEntity(getMixData(prefferedNameGivenname.getContent())));
		}
		}
		// end of prefferedName block
		
		Element eaddress = author.getChild("e-address", ceNamespace);
		if(eaddress != null)
		{
		aus.setEaddress(eaddress.getTextTrim());
		}
		
		// Author alias
		Element alias = author.getChild("alias", ceNamespace);
		if(alias != null)
		{
		aus.setAlias(alias.getTextTrim());
		//System.out.println("ALIAS1= "+alias.getTextTrim());
		}
		
		// Author alt-name
		Element altName = author.getChild("alt-name", ceNamespace);
		if(altName != null)
		{
		aus.setAltName(DataLoadDictionary.mapEntity(altName.getTextTrim()));
		//System.out.println(this.accessNumber+" altName1= "+altName.getTextTrim());
		}
		
		
		
		ausmap.addCpxaus(aus);
		}
	
	}
	
    private String getAbstractMixData(List l)
	{
		StringBuffer result = new StringBuffer();
		for(int i=0;i<l.size();i++)
		{
			Element abstractPara = (Element)l.get(i);
			result.append(getMixData(abstractPara.getContent())+"<br/>");
		}
		return result.toString();
	}

	private String getMixData(List l)
	{
		StringBuffer b = new StringBuffer();
		StringBuffer result = getMixData(l,b);
		return result.toString();
    }

    private StringBuffer getMixData(List l, StringBuffer b)
    {
    	
        Iterator it = l.iterator();

        while(it.hasNext())
        {
            Object o = it.next();

            if(o instanceof Text )
            {

				String text=((Text)o).getText();

				text= perl.substitute("s/</&lt;/g",text);   	//restore by hmo@3/21/2021
				text= perl.substitute("s/>/&gt;/g",text);		//restore by hmo@3/21/2021
				text= perl.substitute("s/\n//g",text);
				text= perl.substitute("s/\r//g",text);
				text= perl.substitute("s/\t//g",text);	
				text= perl.substitute("s/�/\"/g",text);	
				text= perl.substitute("s/�/\"/g",text);
				b.append(text);
				//System.out.println("text2::"+text);

            }
            else if(o instanceof EntityRef)
            {
  				if(inabstract)
  						entity.add(((EntityRef)o).getName());

                  b.append("&").append(((EntityRef)o).getName()).append(";");
            }
            else if(o instanceof Element)
            {
                Element e = (Element)o;
                b.append("<").append(e.getName());
                List ats = e.getAttributes();
                if(!ats.isEmpty())
                {	Iterator at = ats.iterator();
					while(at.hasNext())
        			{
						Attribute a = (Attribute)at.next();
					   	b.append(" ").append(a.getName()).append("=\"").append(a.getValue()).append("\"");
					}
				}
                b.append(">");
                getMixData(e.getContent(), b);
                b.append("</").append(e.getName()).append(">");
            }
        }
		
        return b;
    }

    private  StringBuffer getMixCData(List l, StringBuffer b)
	{
		inabstract=true;
		b=getMixData(l,b);
		inabstract=false;
		return b;
	}
    
    private StringBuffer getMixData(Iterator it, StringBuffer b)
    {
        
        while(it.hasNext())
        {
            Object o = it.next();

            if(o instanceof Text )
            {

				//String text=((Text)o).getTextTrim();
				String text=((Text)o).getText();

				//System.out.println("text3::"+text);

				text= perl.substitute("s/&/&amp;/g",text);
				text= perl.substitute("s/</&lt;/g",text);
				text= perl.substitute("s/>/&gt;/g",text);
				text= perl.substitute("s/\n//g",text);
				text= perl.substitute("s/\r//g",text);
				text= perl.substitute("s/\t//g",text);

				//System.out.println("text4::"+text);
				b.append(text);

            }
            else if(o instanceof EntityRef)
            {
  				if(inabstract)
  						entity.add(((EntityRef)o).getName());

                  b.append("&").append(((EntityRef)o).getName()).append(";");
            }
            else if(o instanceof Element)
            {
                Element e = (Element)o;
                b.append("<").append(e.getName());
                List ats = e.getAttributes();
                if(!ats.isEmpty())
                {	Iterator at = ats.iterator();
					while(at.hasNext())
        			{
						Attribute a = (Attribute)at.next();
					   	b.append(" ").append(a.getName()).append("=\"").append(a.getValue()).append("\"");
					}
				}
                b.append(">");
                getMixData(e.getDescendants(), b);
                b.append("</").append(e.getName()).append(">");
            }
        }
		
        return b;
    }
    
    private String getMixData(Iterator l)
	{
		StringBuffer b = new StringBuffer();
		StringBuffer result = getMixData(l,b);
		return result.toString();
    }
	
	public String auffToStringBuffer(BdAffiliations aufflist,
					  StringBuffer secondAffGroup)
	{
		StringBuffer bufaffiliations = new StringBuffer();
		StringBuffer returnbuf = new StringBuffer();
		if (aufflist != null && aufflist.getAffmap() != null)
		{
			Iterator itr = aufflist.getAffmap().keySet().iterator();
			while (itr.hasNext())
			{
				BdAffiliation aAffiliation =(BdAffiliation) itr.next();
				
				if(aAffiliation.getAffid() != 0)
				{
					bufaffiliations.append(aAffiliation.getAffid());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				if(aAffiliation.getAffOrganization() != null)
				{
				//System.out.println("Organization="+aAffiliation.getAffOrganization());
					bufaffiliations.append(aAffiliation.getAffOrganization());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				if(aAffiliation.getAffCityGroup() != null)
				{
					bufaffiliations.append(aAffiliation.getAffCityGroup());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				if (aAffiliation.getAffCountry() != null)
				{
					bufaffiliations.append(aAffiliation.getAffCountry());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				
				if (aAffiliation.getAffAddressPart() != null)
				{
					bufaffiliations.append(aAffiliation.getAffAddressPart());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				if(aAffiliation.getAffCity() != null)
				{
					bufaffiliations.append(aAffiliation.getAffCity());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				if(aAffiliation.getAffState() != null)
				{
					bufaffiliations.append(aAffiliation.getAffState());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				if(aAffiliation.getAffPostalCode() != null)
				{
					bufaffiliations.append(aAffiliation.getAffPostalCode());
				
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				if(aAffiliation.getAffText() != null)
				{
					bufaffiliations.append(aAffiliation.getAffText());
				
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				
				///*use for CAFE data
				
				if(aAffiliation.getAffiliationId() != null)
				{
					bufaffiliations.append(aAffiliation.getAffiliationId());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				if(aAffiliation.getAffDepartmentId() != null)
				{
					bufaffiliations.append(aAffiliation.getAffDepartmentId());
				}
				bufaffiliations.append(Constants.IDDELIMITER);
				//*/
				bufaffiliations.append((Constants.AUDELIMITER));
			
			}
		}
			
		if(bufaffiliations.length() < 3500 )
		{
			return bufaffiliations.toString();
		}
		else if(bufaffiliations.length() >= 3500)
		{
		
			int endFirstAffGroupMarker = bufaffiliations.lastIndexOf((Constants.AUDELIMITER), 3500)+1;
			String firstAffGroup = bufaffiliations.substring(0, endFirstAffGroupMarker);
			if(bufaffiliations.length() > 7500)
			{
				String secAffGroup = bufaffiliations.substring(endFirstAffGroupMarker,(endFirstAffGroupMarker+3500));
				int endSecondAffGroup = secAffGroup.lastIndexOf((Constants.AUDELIMITER))+1;
				secondAffGroup.append(secAffGroup.substring(0,endSecondAffGroup));
			}
			else
			{
				secondAffGroup.append(bufaffiliations.substring(endFirstAffGroupMarker));
			}
			return firstAffGroup;
		}
		return null;
	}
	
	public String auToStringBuffer(BdAuthors auslist)
	{
		StringBuffer bufauthor = new StringBuffer();
		StringBuffer returnbuf = new StringBuffer();
		if (auslist != null && auslist.getCpxaus() != null)
		{
			Iterator itr = auslist.getCpxaus().keySet().iterator();
			int i=0;
			while(itr.hasNext())
			{
				BdAuthor aAuthor =(BdAuthor) itr.next();
				if ( aAuthor.getSec() != null)
				{
					bufauthor.append(aAuthor.getSec());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getAuid() != null)
				{
					bufauthor.append(aAuthor.getAuid());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getAffidStr() != null)
				{
					bufauthor.append(aAuthor.getAffidStr());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getIndexedName() != null)
				{
					bufauthor.append(aAuthor.getIndexedName());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getInitials() != null)
				{
					bufauthor.append(aAuthor.getInitials());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getSurname() != null)
				{
					bufauthor.append(aAuthor.getSurname());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getDegrees() != null)
				{
					bufauthor.append(aAuthor.getDegrees());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getGivenName() != null)
				{
					bufauthor.append(aAuthor.getGivenName());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getSuffix() != null)
				{
					bufauthor.append(aAuthor.getSuffix());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getNametext() != null)
				{
					bufauthor.append(aAuthor.getNametext());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getPrefnameInitials() != null)
				{
					bufauthor.append(aAuthor.getPrefnameInitials());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getPrefnameIndexedname() != null)
				{
					bufauthor.append(aAuthor.getPrefnameIndexedname());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getPrefnameDegrees() != null)
				{
					bufauthor.append(aAuthor.getPrefnameDegrees());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getPrefnameSurname() != null)
				{
					bufauthor.append(aAuthor.getPrefnameSurname());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getPrefnameGivenname() != null)
				{
					bufauthor.append(aAuthor.getPrefnameGivenname());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getEaddress() != null)
				{
					bufauthor.append(aAuthor.getEaddress());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getAuthorId() != null)
				{
					bufauthor.append(aAuthor.getAuthorId());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getAlias() != null)
				{
					bufauthor.append(aAuthor.getAlias());
					//System.out.println("ALIAS3= "+aAuthor.getAlias());
				}
				bufauthor.append(Constants.IDDELIMITER);
				if(aAuthor.getAltName() != null)
				{
					bufauthor.append(aAuthor.getAltName());
					//System.out.println("AltName3= "+aAuthor.getAltName());
				}
				bufauthor.append(Constants.IDDELIMITER);
				//System.out.println("AUTHOR BUFFER"+i+"="+bufauthor.toString());
				bufauthor.append((Constants.AUDELIMITER));
				i++;
			}
		}
		
		if(bufauthor.length() < 3995 )
		{
			return bufauthor.toString();
		}
		else
		{
			int endFirstAuGroupMarker = bufauthor.lastIndexOf(Constants.AUDELIMITER, 3995)+1;
			String firstAuGroup = bufauthor.substring(0, endFirstAuGroupMarker);
			return firstAuGroup;
		}
	
	
	}
    
    private Map getAuthorIDAndAffiliationID(List authorGroups,String fullAuthorFlag)
    {
    	Map authorgroupMap = new HashMap();
    	StringBuffer authorIdBuffer = new StringBuffer();
    	StringBuffer affIdBuffer = new StringBuffer();
    	
    	BdAuthors ausmap = new BdAuthors();
		BdAffiliations affmap = new BdAffiliations();
		
		StringBuffer secondAuGroup = new StringBuffer();
		StringBuffer secondAffGroup = new StringBuffer();
    	for(int i=0;i<authorGroups.size();i++)
    	{
    		Element authorgroup = (Element)authorGroups.get(i);   		
			
			b.setAuthorAndAffs( ausmap,
								affmap,
								authorgroup);
			  		
    	}
    		
    	String authors = b.auToStringBuffer(ausmap,secondAuGroup);
    	{
    		if(secondAuGroup!=null && secondAuGroup.length()>0)
    		{
    			authors=authors+secondAuGroup.toString();
    		}
    	}

		String affiliation = b.auffToStringBuffer(affmap, secondAffGroup);

		if(secondAffGroup.length() > 0)
		{
			affiliation = affiliation+secondAffGroup.toString();
		}
    	
		if(authors.indexOf("'")>-1)
		{
			authors = authors.replaceAll("'", "''");
		}
		//System.out.println("AUTHOR="+authors);
		
		if(affiliation.indexOf("'")>-1)
		{
			affiliation = affiliation.replaceAll("'", "''");
		}
    	authorgroupMap.put("author", authors);
    	authorgroupMap.put("affiliation", affiliation);
    	return authorgroupMap;
    	
    }
    
    private String getCafeDocFromS3(Statement selectStatement,Statement updateStatement, String key, int index)
    {
    	AmazonS3 s3Client;
		S3Object object;
		String docString=null;
		InputStream objectData=null;
		try 
		{			
			s3Client = AmazonS3Service.getInstance().getAmazonS3Service();			
			object = s3Client.getObject( new GetObjectRequest("sccontent-ani-xml-prod", key));
			objectData = object.getObjectContent();
			docString = saveContentToFile(objectData,key);
			//docString = docString.replaceAll("><", ">\n<");
			processData(selectStatement,updateStatement, docString,index);
	        extractToES(docString);
			// Process the objectData stream.
			
		 } 
		catch (AmazonServiceException ase) 
		{
	            System.out.println("KEY "+key+" Caught an AmazonServiceException, which" +
	            		" means your request made it " +
	                    "to Amazon S3, but was rejected with an error response" +
	                    " for some reason.");
	            System.out.println("Error Message:    " + ase.getMessage());
	            System.out.println("HTTP Status Code: " + ase.getStatusCode());
	            System.out.println("AWS Error Code:   " + ase.getErrorCode());
	            System.out.println("Error Type:       " + ase.getErrorType());
	            System.out.println("Request ID:       " + ase.getRequestId());
	            //throw ase;
		} 
		catch (AmazonClientException ace) 
		{
	            System.out.println("KEY "+key+" Caught an AmazonClientException, which means"+
	            		" the client encountered " +
	                    "an internal error while trying to " +
	                    "communicate with S3, " +
	                    "such as not being able to access the network.");
	            System.out.println("Error Message: " + ace.getMessage());
	            //throw ace;
		}
		catch(Exception ioe)
		{
			System.out.println("Other Error Message: " + ioe.getMessage());
		}
		finally
		{
			try
			{
				objectData.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
    
    	return docString;
    }
    
    private void extractToES(String cafeDoc)
    {
    	try
    	{
	    	SAXBuilder builder = new SAXBuilder();
			builder.setExpandEntities(false);
			builder.setFeature( "http://xml.org/sax/features/namespaces", true );
			//System.out.println(cafeDoc);
			Document doc = builder.build(new InputSource(new StringReader(cafeDoc)));
	  		Element cpxRoot = doc.getRootElement();
	  		Element docRoot = cpxRoot.getChild("doc",xocsNamespace);	  		
	  		Element meta = docRoot.getChild("meta",xocsNamespace);	  		
	  		String eid = meta.getChildText("eid",xocsNamespace);
	  		//System.out.println("eid="+eid);
	  		Element itemE = docRoot.getChild("item",xocsNamespace);
	  		Element item = itemE.getChild("item",noNamespace);
			Element bibrecord = item.getChild("bibrecord",noNamespace);
			//System.out.println("bibrecord="+bibrecord.getName());
			Element head = bibrecord.getChild("head",noNamespace);
			Hashtable parsedRecord = parse(itemE);
    	}
    	catch(Exception e)
        {
        	e.printStackTrace();
        }
    	
    }
    
    public Hashtable parse(Element cpxRoot) throws Exception
	{
		Hashtable record = new Hashtable();
		Element source = null;
		String midstr= null;
		try
		{
			//reset accessnumber and PUI for every record
			//setAccessNumber("");
			//setPui("");
			if(cpxRoot != null)
			{
				//System.out.println("NAME= "+cpxRoot.getName());
				/*
				String namespaceURI = cpxRoot.getNamespace().getURI();
				if(namespaceURI!=null && namespaceURI.length()>0)
				{
					noNamespace = Namespace.getNamespace("","http://www.elsevier.com/xml/ani/ani");
				}
				*/				
				Element item = cpxRoot.getChild("item",noNamespace);

				if(item!= null)
				{					
					String eid = item.getChildText("document-id",xoeNamespace);	
					if(eid!=null)
					{
						record.put(EVCombinedRec.EID,eid);
					}
					//System.out.println("EID1="+eid);
					
					Element fundingList = cpxRoot.getChild("funding-list",xocsNamespace);					
					Element openAccess = cpxRoot.getChild("open-access",xocsNamespace);
					
					if(openAccess!=null)
					{
						/*
						if(openAccess.getChild("oa-access-effective-date",xocsNamespace)!=null)
						{
							String oaAcessEffectiveDate = openAccess.getChildText("oa-access-effective-date",xocsNamespace);
							record.put("OAACCESSDATE",oaAcessEffectiveDate);
							//System.out.println("oaAcessEffectiveDate="+oaAcessEffectiveDate);
						}
						
						if(openAccess.getChild("oa-user-license",xocsNamespace)!=null)
						{
							String oaUserLicense = openAccess.getChildText("oa-user-license",xocsNamespace);
							record.put("OAUSERLICENSE",oaUserLicense);
							//System.out.println("oaUserLicense="+oaUserLicense);
						}
						*/
						
						if(openAccess.getChild("oa-article-status",xocsNamespace)!=null)
						{
							Element oaArticleStatus = openAccess.getChild("oa-article-status",xocsNamespace);
							
							//String isOpenAccess = oaArticleStatus.getAttributeValue("free-to-read-status"); //this only use for testing "free-to-read-status" for now
							String isOpenAccess = oaArticleStatus.getAttributeValue("is-open-access");//this is used for production for now at 1/20/2021
							//String freeToReadStatus = oaArticleStatus.getAttributeValue("free-to-read-status");
							if(isOpenAccess!=null)
							{
								record.put(EVCombinedRec.ISOPENACESS,isOpenAccess);
							}
							//record.put("FREETOREADSTATUS",freeToReadStatus);
							//System.out.println("freeToReadStatus="+freeToReadStatus);
						}
						
					}
					
					/*
					Element indexeddate = item.getChild("indexeddate",xocsNamespace);
					if(indexeddate !=null)
					{
						String epoch = indexeddate.getAttributeValue("epoch");
						record.put("EPOCH",epoch);
					}
					//System.out.println("indexeddate"+indexeddate);
					 */
					 
					//String database = getDatabaseName().toLowerCase();
					String database = "cpx";
					String mid = database+"_"+(new GUID()).toString();
					record.put("M_ID",mid);

				    //PUBDATE
					Element processinfo = item.getChild("process-info",aitNamespace);
					if(processinfo!=null)
					{
					    Element datesort = processinfo.getChild("date-sort",aitNamespace);
						StringBuffer yearvalue = new StringBuffer();
						if(datesort!=null)
						{
							yearvalue.append((String) datesort.getAttributeValue("year"));
							if(datesort.getAttributeValue("month") != null )
							{
							    //yearvalue.append(Constants.IDDELIMITER);
							    yearvalue.append((String) datesort.getAttributeValue("month"));
							}
							else
							{
								yearvalue.append("01");
							}
							
							if(datesort.getAttributeValue("day") != null)
							{
							    //yearvalue.append(Constants.IDDELIMITER);
							    yearvalue.append((String) datesort.getAttributeValue("day"));
							}
							else
							{
								yearvalue.append("01");
							}
							
							if(yearvalue.length() > 0 )
							{
								record.put(EVCombinedRec.DATESORT, yearvalue.toString());
							}
						}
						
						
						Element status = processinfo.getChild("status",aitNamespace);
						String stage = status.getAttributeValue("stage");
						if(stage!=null && stage.length() > 0 )
						{
							record.put("STAGE", stage);
						}
						/*
						Element dateDelivery = processinfo.getChild("date-delivered",aitNamespace);
						if(dateDelivery!=null) {
							String dateDeliveryYear = dateDelivery.getAttributeValue("year");
							String dateDeliveryMonth = dateDelivery.getAttributeValue("month");
							String dateDeliveryDay = dateDelivery.getAttributeValue("day");
							record.put("DELIVERYDATE", dateDeliveryDay+" "+dateDeliveryMonth+" "+dateDeliveryYear);
						}
						*/
						
					}


					//bibrecord

					Element bibrecord 	= item.getChild("bibrecord",noNamespace);
					if(bibrecord==null)
					{
						System.out.println("bibrecord is null");
					}
					if(bibrecord != null)
					{
						//System.out.println("bibrecord");
						Element item_info 	= bibrecord.getChild("item-info",noNamespace);
						Element copyright   = item_info.getChild("copyright",noNamespace);

						if(copyright != null)
						{
							String right = copyright.getTextTrim();
							//System.out.println("COPYRIGHT "+right);
							if(right==null)
							{
								right = copyright.getAttributeValue("type");
								//System.out.println("COPYRIGHT_TYPE is not null "+right);
							}
							record.put(EVCombinedRec.COPYRIGHT,DataLoadDictionary.mapEntity(right));
							//System.out.println("COPYRIGHT"+copyright.getTextTrim());
						}

						Element itemidlist 	= item_info.getChild("itemidlist",noNamespace);

						Element doi = itemidlist.getChild("doi", ceNamespace);
						if(doi != null)
						{
							record.put(EVCombinedRec.DOI,doi.getTextTrim());
						}

						Element pii = itemidlist.getChild("pii", ceNamespace);
						if(pii != null)
						{
							record.put(EVCombinedRec.PII,pii.getTextTrim());
						}
						List itemidList = itemidlist.getChildren("itemid",noNamespace);
						String  puisecondary="|";
						for(int i=0;i<itemidList.size();i++)
						{
							Element itemidElement = (Element)itemidList.get(i);
							String  itemid_idtype = itemidElement.getAttributeValue("idtype");
							String  pui=null;
							
							if(itemid_idtype != null &&	
									(itemid_idtype.equals("CPX")||
									itemid_idtype.equals("GEO")||
									itemid_idtype.equals("API")||
									itemid_idtype.equals("SNBOOK")||
									itemid_idtype.equals("APILIT")||
									itemid_idtype.equals("CBNB")||
									itemid_idtype.equals("CHEM")))
							{
								String  itemid = itemidElement.getTextTrim();

								//System.out.println("ACCESSNUMBER= "+itemid);
								
								//pre frank request, add book record as cpx database by hmo at 11/10/2016
								if((!database.equals("cpx") && !itemid_idtype.equals("CPX")) || itemid_idtype.equals("CBNB")|| ((database.equals("cpx") || database.equals("pch")) && (itemid_idtype.equals("CPX") || itemid_idtype.equals("SNBOOK"))))
								{
									record.put(EVCombinedRec.ACCESSION_NUMBER,itemid);
									//setAccessNumber(itemid);
									//System.out.println("DATABSE="+database+" ACCESSNUMBER= "+itemid);
								}
								
								
							}
							else if (itemid_idtype != null && itemid_idtype.equals("PUI"))
							{
								pui = itemidElement.getTextTrim();
								//setPui(pui);
								record.put(EVCombinedRec.PUI,pui);
							}
							else if (itemid_idtype != null && itemid_idtype.equals("SEC"))
							{
								pui = itemidElement.getTextTrim();
								record.put("SEC",pui);
							}
							else if (itemid_idtype != null && itemid_idtype.equalsIgnoreCase("PUISECONDARY"))
							{								
								puisecondary = puisecondary+itemidElement.getTextTrim()+"|";							
								record.put("PUISECONDARY",puisecondary);
								//System.out.println("PUISECONDARY="+puisecondary);
							}
							else if (itemid_idtype != null && itemid_idtype.equals("NORMSTANDARDID"))
							{
								String  normstandardid = itemidElement.getTextTrim();
								record.put(EVCombinedRec.NORMSTANDARDID,normstandardid);
							}
							else if (itemid_idtype != null && itemid_idtype.equals("STANDARDDESIGNATION"))
							{
								String  standarddesignation = itemidElement.getTextTrim();
								record.put(EVCombinedRec.STANDARDDESIGNATION,standarddesignation);
							}
							else if (itemid_idtype != null && itemid_idtype.equals("STANDARDID"))
							{
								String  standardid = itemidElement.getTextTrim();
								record.put(EVCombinedRec.STANDARDID,standardid);
							}						
							
						}

						//head
						Element head = bibrecord.getChild("head",noNamespace);
						if(head != null)
						{
							String docType=null;
							Element citinfo = head.getChild("citation-info",noNamespace);
							if(citinfo != null)
							{
								Element citlang = citinfo.getChild("citation-language",noNamespace);
								if(citlang != null)
								{
									Attribute lang =(Attribute) citlang.getAttribute("lang",xmlNamespace);
									if(lang != null)
									{
										record.put(EVCombinedRec.LANGUAGE,lang.getValue());
									}
								}
								/*
								Element figinfo = citinfo.getChild("figure-information",noNamespace);
								if(figinfo != null)
								{
									String fig = figinfo.getTextTrim();
									record.put("FIG", fig);
								}
								*/
								Element cittype = citinfo.getChild("citation-type",noNamespace);
								if(cittype != null)
								{
									Attribute type =(Attribute) cittype.getAttribute("code");
									if(type != null)
									{
										String cititype=type.getValue();
										
										/******Use this for new AIP LOGIC*/
										///* block for BD comfirmation
										if(record.get("STAGE")!=null && ((String)record.get("STAGE")).equals("S200"))
										{
											docType="ip";
										}
										else										
										{																			 
											docType = cititype;
										}
										
										//record.put(EVCombinedRec.DOCTYPE,getDocType(docType));
										//System.out.println("STAGE="+record.get("STAGE")+"TYPE= "+cititype+"DOCTYPE= "+docType);
									}
								}

								/*
								Element authorKeywords = citinfo.getChild("author-keywords",noNamespace);
								if(authorKeywords != null)
								{
									StringBuffer authorKeywordBuffer = new StringBuffer();
									List authorKeywordList = authorKeywords.getChildren("author-keyword",noNamespace);
									if(authorKeywordList != null)
									{
										for(int i=0;i<authorKeywordList.size();i++)
										{
											Element authorKeyword = (Element)authorKeywordList.get(i);
											String authorKeywordString = getMixData(authorKeyword.getContent());
											if(authorKeywordString != null && authorKeywordString.length()>0)
											{
												if(authorKeywordBuffer.length()>0)
												{
													authorKeywordBuffer.append(Constants.IDDELIMITER);
												}
												authorKeywordBuffer.append(DataLoadDictionary.mapEntity(authorKeywordString));
											}
										}
									}

									record.put("AUTHORKEYWORD",authorKeywordBuffer.toString().replaceAll(">\\s+<", "><"));
								}
								*/
							}//citinfo
							
							/*
							//related-item
							Element relatedItem = head.getChild("related-item",noNamespace);
							if(relatedItem!=null)
							{
								
								if(relatedItem.getChild("doi",ceNamespace)!=null)
								{
									Element rdoi = (Element)relatedItem.getChild("doi",ceNamespace);
									record.put("RELATEDDOI",rdoi.getTextTrim());																
								}
								
								if(relatedItem.getChild("pii",ceNamespace)!=null)
								{
									Element rpii = (Element)relatedItem.getChild("pii",ceNamespace);
									record.put("RELATEDPII",rpii.getTextTrim());																
								}
							
								List relatePUIs = relatedItem.getChildren("itemid",noNamespace);
								if(relatePUIs != null)
								{
									String relatedPUIContent="";
									for(int i=0;i<relatePUIs.size();i++)
									{
										Element relatedPUI = (Element)relatePUIs.get(i);
										if(relatedPUIContent.length()>0)
										{
											relatedPUIContent = relatedPUIContent+","+relatedPUI.getTextTrim();
										}
										else
										{
											relatedPUIContent = relatedPUI.getTextTrim();
										}
										
									}
									record.put("RELATEDPUI",relatedPUIContent);
								}
							}
							*/
							
							//citation title
							Element cittitle = head.getChild("citation-title",noNamespace);
							StringBuffer cbnbForeignTitle = new StringBuffer();
							if(cittitle != null)
							{
								List cittextlst = cittitle.getChildren("titletext",noNamespace);

								if(cittextlst != null)
								{
									StringBuffer cittext = new StringBuffer();

									for (int i = 0; i < cittextlst.size(); i++)
									{
										Element cittextelm = (Element)cittextlst.get(i);
										cittext.append(i);
										cittext.append(Constants.IDDELIMITER);
										if(cittextelm.getContent()!=null)
										{											
											cittext.append(DataLoadDictionary.mapEntity(getMixData(cittextelm.getContent())));
											//cittext.append(getMixData(cittextelm.getContent()));
										}
										
										cittext.append(Constants.IDDELIMITER);
										if(cittextelm.getAttribute("original")!=null)
										{
											cittext.append(cittextelm.getAttributeValue("original"));
										}
										cittext.append(Constants.IDDELIMITER);
										
										if(cittextelm.getAttribute("lang",xmlNamespace)!=null)
										{
											//System.out.println("LANG="+cittextelm.getAttributeValue("lang",xmlNamespace));
											if(cittextelm.getAttributeValue("lang",xmlNamespace).equals("SPA"))
											{
												if(cbnbForeignTitle.length()>0)
												{
													cbnbForeignTitle.append(";");
												}
												cbnbForeignTitle.append(DataLoadDictionary.mapEntity(getMixData(cittextelm.getContent())));
												
											}
											cittext.append(cittextelm.getAttributeValue("lang",xmlNamespace));
										}
										
										if(i<cittextlst.size()-1)
										{
											cittext.append(Constants.AUDELIMITER);
										}
									}
									String citation = cittext.toString();
									if(this.databaseName.equalsIgnoreCase("elt"))
									{
										citation = citation.replaceAll("<inf>", "<sub>");
										citation = citation.replaceAll("</inf>", "</sub>");

									}
									
									if(cbnbForeignTitle.length()>0)
									{
										//System.out.println("****************CBNBFOREIGNTITLE="+cbnbForeignTitle.toString());
										record.put("CBNBFOREIGNTITLE",cbnbForeignTitle.toString());
									}
									//record.put(EVCombinedRec.TITLE,x.prepareCitationTitle(citation.replaceAll(">\\s+<", "><")));
								}
							}

							Element abstracts = head.getChild("abstracts",noNamespace);
							String 	abstractString =null;
							String 	abstractString1 =null;
							if(abstracts!= null && abstracts.getChild("abstract",noNamespace)!=null)
							{
								
								List abstractDatas = abstracts.getChildren("abstract",noNamespace);
								for(int f=0; f<abstractDatas.size();f++)
								{
									String abstractCopyRight=null;
									String abstractPerspective=null;
									String abstractlanguage=null;
									
									Element abstractData = (Element)abstractDatas.get(f);
									if(	abstractData.getChild("publishercopyright",noNamespace)!=null)
									{							
									 	abstractCopyRight= DataLoadDictionary.mapEntity(abstractData.getChildTextTrim("publishercopyright",noNamespace));
										//System.out.println("COPYRIGHT="+ abstractCopyRight);
									}
									else
									{
										abstractCopyRight=null;
									}
	
									// abstract data									
									
									if(abstractData.getAttributeValue("lang",xmlNamespace) != null)
									{
										//System.out.println("ABSTRACT_Language "+abstractData.getAttributeValue("lang",xmlNamespace));
										abstractlanguage=abstractData.getAttributeValue("lang",xmlNamespace);
									}
									
									/*
									if(abstractData.getAttributeValue("original") != null)
									{
										//System.out.println("ABSTRACT_original "+abstractData.getAttributeValue("original"));
										record.put("ABSTRACTORIGINAL", abstractData.getAttributeValue("original"));
									}
	
									if(abstractData.getAttributeValue("perspective") != null)
									{
										//System.out.println("ABSTRACTPERSPECTIVE "+abstractData.getAttributeValue("perspective"));
										abstractPerspective = abstractData.getAttributeValue("perspective");
										record.put("ABSTRACTPERSPECTIVE", abstractPerspective);
									}
									*/
									//if(abstractData.getChildTextTrim("para",ceNamespace) != null)
									if(abstractData.getChildren("para",ceNamespace) != null)
									{
										
										//String abstractString = dictionary.mapEntity(getMixData(abstractData.getChild("para",ceNamespace).getContent()));															
									
										String abstractC = DataLoadDictionary.mapEntity(getAbstractMixData(abstractData.getChildren("para",ceNamespace)));
										if(abstractlanguage==null || abstractlanguage.equalsIgnoreCase("eng"))
										{
											abstractString = abstractC;
											if(abstractCopyRight!=null)
											{
												abstractString = abstractString+" "+abstractCopyRight;
											}
											if(this.databaseName.equalsIgnoreCase("elt"))
											{
												abstractString = abstractString.replaceAll("<inf>", "<sub>");
												abstractString = abstractString.replaceAll("</inf>", "</sub>");	
												
											}
											break;
										}
										else
										{
											abstractString1 = abstractC;
											if(abstractCopyRight!=null)
											{
												abstractString1 = abstractString1+" "+abstractCopyRight;
											}
											if(this.databaseName.equalsIgnoreCase("elt"))
											{
												abstractString1 = abstractString1.replaceAll("<inf>", "<sub>");
												abstractString1 = abstractString1.replaceAll("</inf>", "</sub>");	
												
											}
										}
									}
									
									if(abstractString==null && abstractString1!=null)
									{
										abstractString = abstractString1;
									}
									
									if(abstractString!=null)
									{
										record.put(EVCombinedRec.ABSTRACT, abstractString.replaceAll(">\\s+<", "><"));
									}
									
								}

							}

							//author-group conatins (redundunt authors - removing redundunt authors;

							List authorgroup = head.getChildren("author-group",noNamespace);
							BdAuthors ausmap = new BdAuthors();
							BdAffiliations affmap = new BdAffiliations();
							BdAuthors collaborationMap = new BdAuthors();
							BdAffiliations collaborationAffMap = new BdAffiliations();
							this.affid = 0;
							for (int e=0 ; e < authorgroup.size() ; e++)
							{
								Element agroup= (Element)authorgroup.get(e);
								if(agroup.getChild("collaboration",noNamespace)!=null)
								{
									b.setCollaborationAndAffs( collaborationMap,
															 collaborationAffMap,
															 (Element) authorgroup.get(e));
								}
								else
								{
									b.setAuthorAndAffs( ausmap,
													affmap,
													(Element) authorgroup.get(e));
								}
							}
							StringBuffer secondAuGroup = new StringBuffer();
							StringBuffer secondAffGroup = new StringBuffer();
							
							record.put("COLLABORATION", b.auToStringBuffer(collaborationMap));	
							record.put("COLLABORATION_AFF", auffToStringBuffer(collaborationAffMap, secondAffGroup));
							record.put("AUTHOR", b.auToStringBuffer(ausmap, secondAuGroup));
							if(secondAuGroup.length() > 0)
							{
								record.put("AUTHOR_1",secondAuGroup.toString());
							}
							

							record.put("AFFILIATION", auffToStringBuffer(affmap, secondAffGroup).toString());

							if(secondAffGroup.length() > 0)
							{
								record.put("AFFILIATION_1",secondAffGroup.toString());
							}

							//enhancement

							Element enhancement = head.getChild("enhancement",noNamespace);

							if(enhancement != null)
							{
									Element descriptorgroup = enhancement.getChild("descriptorgroup",noNamespace);
									//parseDescriptorgroup(descriptorgroup,record);

									Element classificationgroup = enhancement.getChild("classificationgroup",noNamespace);
									//parseClassificationgroup(classificationgroup,record);

									Element manufacturergroup = enhancement.getChild("manufacturergroup",noNamespace);
									//parseManufacturergroup(manufacturergroup,record);

									Element tradenamegroup = enhancement.getChild("tradenamegroup",noNamespace);
									//parseTradenamegroup(tradenamegroup,record);

									Element sequencebanks = enhancement.getChild("sequencebanks",noNamespace);
									//parseSequencebanks(sequencebanks,record);

									Element chemicalgroup = enhancement.getChild("chemicalgroup",noNamespace);
									//parseChemicalgroup(chemicalgroup,record);

									Element apidescriptorgroup = enhancement.getChild("API-descriptorgroup",noNamespace);
									if(apidescriptorgroup!=null)
									{

										Element autoposting = apidescriptorgroup.getChild("autoposting",noNamespace);
								        //APICC
										if(autoposting != null)
										{

											Element classificationdescription = autoposting.getChild("API-CC", noNamespace);

											if(classificationdescription != null)
											{

												List classdescgroup = classificationdescription.getChildren("classification", noNamespace);
												StringBuffer apiccterms = new StringBuffer();
												if(classdescgroup != null)
												{
													//System.out.println("ENTER classdescgroup");
													for (int j = 0; j < classdescgroup.size(); j++)
													{
														Element ell = (Element)classdescgroup.get(j);
														List apicc = ell.getChildren("classification-description",noNamespace);

														if(apicc != null)
														{
															for (int i = 0; i < apicc.size(); i++)
															{
																Element el = (Element)apicc.get(i);
																if(el!=null)
																{
																	apiccterms.append(DataLoadDictionary.mapEntity((String)el.getTextTrim()));
																}
																if(i<(apicc.size()-1) )
																{
																	apiccterms.append(Constants.AUDELIMITER);
																}
																
															}
														}
														if(j<(classdescgroup.size()-1) )
														{
															apiccterms.append(Constants.AUDELIMITER);
														}
													}
												}

												if(apiccterms != null && apiccterms.length()> 0)
												{
													String apiccstr = apiccterms.toString();
													record.put("CLASSIFICATIONDESC",apiccstr);
												}
											}


									//APICT
									// exclude CRN from API-CT

										Element apicttop = autoposting.getChild("API-CT",noNamespace);
										if(apicttop != null)
										{
											List ctTerms = apicttop.getChildren("autoposting-term",noNamespace);

											StringBuffer apict = new StringBuffer();
											StringBuffer apictextended = new StringBuffer();

											for (int j = 0; j < ctTerms.size(); j++)
											{
												Element el = (Element)ctTerms.get(j);
												boolean isCRN = false;


												if(el.getAttribute("CAS-nr")!= null  &&
														(el.getAttributeValue("CAS-nr").equals("y")||
														 el.getAttributeValue("CAS-nr").equals("b")))
												{
													isCRN = true;
												}
												if(!isCRN)
												{
													StringBuffer termbuf = new StringBuffer();
													String pref = (String)el.getAttributeValue("prefix");
													String postf = (String)el.getAttributeValue("postfix");
													String term = (String)el.getTextTrim();
													if ( pref != null && pref.length() > 0)
													{
														termbuf.append(DataLoadDictionary.mapEntity(pref)).append("-");
													}if(term!=null)
													{
														termbuf.append(DataLoadDictionary.mapEntity(term));
													}
													if ( postf != null && postf.length() > 0)
													{
														termbuf.append("-").append(DataLoadDictionary.mapEntity(postf));
													}
													if(apict.length() < 3000)
													{
														apict.append(termbuf.toString()).append(Constants.IDDELIMITER);
													}
													else if(apict.length() >= 3000)
													{
														apictextended.append(termbuf.toString()).append(Constants.IDDELIMITER);
													}
												}
											}
											if(apict != null &&
													apict.toString().length() > 0)
											{

												record.put("APICT",apict.toString());
											}

											if(apictextended != null &&
													apictextended.toString().length()> 0)
											{
												record.put("APICT1",apictextended.toString());
											}

										}

										Element apilt = autoposting.getChild("API-LT",noNamespace);
										StringBuffer apiterms = new StringBuffer();
										StringBuffer apigroups = new StringBuffer();
										StringBuffer apiterms1 = new StringBuffer();
										StringBuffer apigroups1 = new StringBuffer();
										if(apilt != null)
										{
											List apiltgroup = apilt.getChildren("API-LT-group",noNamespace);
											for (int i = 0; i < apiltgroup.size(); i++)
											{
												Element ltgroup =(Element) apiltgroup.get(i);
												List apilttop = ltgroup.getChildren("autoposting-term",noNamespace);
												apiterms = new StringBuffer();
												if(apilttop != null)
												{
														for (int j = 0; j < apilttop.size(); j++)
														{
															Element el = (Element)apilttop.get(j);
															boolean isCRN = false;
															if(el.getAttribute("CAS-nr")!= null  &&
																	(el.getAttributeValue("CAS-nr").equals("y")||
																	 el.getAttributeValue("CAS-nr").equals("b")))
															{
																isCRN = true;
															}
															if(!isCRN)
															{

																StringBuffer termbuf = new StringBuffer();
																String pref = (String)el.getAttributeValue("prefix");
																String postf = (String)el.getAttributeValue("postfix");
																String term = (String)el.getTextTrim();
																if ( pref != null && pref.length() > 0)
																{
																	termbuf.append(DataLoadDictionary.mapEntity(pref)).append("-");
																}
																if(term!=null)
																{
																	termbuf.append(DataLoadDictionary.mapEntity(term));
																}
																if ( postf != null && postf.length() > 0)
																{
																	termbuf.append("-").append(DataLoadDictionary.mapEntity(postf));
																}
																if(apiterms.length() < 3000)
																{

																	apiterms.append(termbuf.toString()).append(Constants.IDDELIMITER);
																}
																else if(apiterms.length() >= 3000)
																{
																	apiterms1.append(termbuf.toString()).append(Constants.IDDELIMITER);
																}
															}
														}

												}
												if(apiterms != null && apiterms.length()>0)
												{
													apigroups.append(apiterms);
													apigroups.append(Constants.GROUPDELIMITER);
												}
												if(apiterms1 != null && apiterms1.length()>0)
												{
													apigroups1.append(apiterms1);
													apigroups1.append(Constants.GROUPDELIMITER);
												}
											}
											// end of groups
											if(apiterms != null && apiterms.length()>0)
											{
												record.put("APILT",apigroups.toString());
											}
											if(apiterms1 != null && apiterms1.length()>0)
											{
												record.put("APILT1",apiterms1.toString());
											}

										}
										Element apiams = autoposting.getChild("API-AMS",noNamespace);

										if(apiams != null)
										{
											StringBuffer apiamsbuf = new StringBuffer();
											if(apiams.getChild("API-term",noNamespace)!= null)
											{
												Element ams = apiams.getChild("API-term",noNamespace);
												apiamsbuf.append(DataLoadDictionary.mapEntity(ams.getTextTrim()));
											}
											record.put("APIAMS",apiamsbuf.toString());
										}

										//API-APC field

										Element apiapc = autoposting.getChild("API-APC", noNamespace);

										if(apiapc != null)
										{
											StringBuffer apiapcbuf = new StringBuffer();
											if(apiapc.getChildren("API-term", noNamespace)!= null)
											{
												List l = apiapc.getChildren("API-term", noNamespace);
												for (int i = 0; i < l.size(); i++)
												{
													Element el = (Element)l.get(i);
													if(el != null)
													{
														apiapcbuf.append(DataLoadDictionary.mapEntity(el.getTextTrim()));
													}
													apiapcbuf.append(Constants.IDDELIMITER);
												}

											}
											if(apiapcbuf != null && apiapcbuf.length()> 0)
											{
												record.put("APIAPC", apiapcbuf.toString());
											}
										}


										//API-CRN field

										Element apicrn = autoposting.getChild("API-CRN", noNamespace);

										if(apicrn != null)
										{
											StringBuffer apicrnbuf = new StringBuffer();
											if(apicrn.getChildren("autoposting-term", noNamespace)!= null)
											{
												List l = apicrn.getChildren("autoposting-term", noNamespace);

												for (int i = 0; i < l.size(); i++)
												{
													Element el = (Element)l.get(i);
													if(el.getAttributeValue("CAS-nr") != null)
													{
														String carnr =(String) el.getAttributeValue("CAS-nr");
														apicrnbuf.append(carnr);

													}
													apicrnbuf.append(Constants.AUDELIMITER);
													if(el.getAttributeValue("postfix") != null)
													{
														String carnr =(String) el.getAttributeValue("postfix");
														apicrnbuf.append(carnr);

													}
													apicrnbuf.append(Constants.AUDELIMITER);
													apicrnbuf.append(DataLoadDictionary.mapEntity(el.getTextTrim()));
													apicrnbuf.append(Constants.IDDELIMITER);
												}
											}
											if(apicrnbuf != null && apicrnbuf.length()> 0)
											{
												record.put("CASREGISTRYNUMBER", apicrnbuf.toString());
											}
										}

										Element apialc = autoposting.getChild("API-ALC",noNamespace);
										if(apialc != null)
										{
											StringBuffer apialcbuf = new StringBuffer();
											if(apialc.getChild("LTM-COUNT",noNamespace)!= null)
											{
												Element ltmcount = apialc.getChild("LTM-COUNT",noNamespace);
												apialcbuf.append(ltmcount.getTextTrim());

											}
											apialcbuf.append(Constants.IDDELIMITER);
											if(apialc.getChild("LT-COUNT", noNamespace) != null)
											{
												Element ltcount = apialc.getChild("LT-COUNT", noNamespace);
												apialcbuf.append(ltcount.getTextTrim());
											}
											if(apialc.getChild("LTM-COUNT",noNamespace)!= null  ||
													apialc.getChild("LT-COUNT", noNamespace) != null)
											{
												record.put("APIALC",apialcbuf.toString());
											}
										}
										//APIATM

										if(autoposting.getChild("API-ATM-group",noNamespace) != null)
										{
											ApiAtm apiatm = new ApiAtm(autoposting.getChild("API-ATM-group",noNamespace));
											record.put("APIATM",apiatm.toAPIString());
										}

										//APIAT

										Element apiat = autoposting.getChild("API-AT",noNamespace);
										if(apiat != null)
										{
											StringBuffer termsbuf = new StringBuffer();
											List apiltmtop = apiat.getChildren("autoposting-term",noNamespace);
											for (int i = 0; i < apiltmtop.size(); i++)
											{
												Element el =(Element) apiltmtop.get(i);
												StringBuffer buf= new StringBuffer();
												String pref = (String)el.getAttributeValue("prefix");
												String postf = (String)el.getAttributeValue("postfix");
												String term = (String)el.getTextTrim();
												if ( pref != null && pref.length() > 0)
												{
													buf.append(pref).append("-");
												}
												buf.append(term);
												if ( postf != null && postf.length() > 0)
												{
													buf.append("-").append(postf);
												}

												termsbuf.append(buf.toString()).append(Constants.IDDELIMITER);

											}

											//end of groups
											record.put("APIAT",termsbuf.toString());
										}


										Element apiltm = autoposting.getChild("API-LTM",noNamespace);

										if(apiltm != null)
										{
											StringBuffer apimterms = new StringBuffer();
											StringBuffer apimgroups = new StringBuffer();
											List apiltmgroup = apiltm.getChildren("API-LTM-group",noNamespace);
											for (int i = 0; i < apiltmgroup.size(); i++)
											{
												Element egroup =(Element) apiltmgroup.get(i);
												List apiltmtop = egroup.getChildren("autoposting-term",noNamespace);
												apimterms = new StringBuffer();

												if(apiltmtop != null)
												{
														for (int j = 0; j < apiltmtop.size(); j++)
														{
															Element el = (Element)apiltmtop.get(j);

															StringBuffer termbuf = new StringBuffer();
															String pref = (String)el.getAttributeValue("prefix");
															String postf = (String)el.getAttributeValue("postfix");
															String term = (String)el.getTextTrim();
															if ( pref != null && pref.length() > 0)
															{
																termbuf.append(pref).append("-");
															}
															termbuf.append(term);
															if ( postf != null && postf.length() > 0)
															{
																termbuf.append("-").append(postf);
															}

															apimterms.append(termbuf.toString()).append(Constants.IDDELIMITER);
														}

												}
												apimgroups.append(apimterms);
												apimgroups.append(Constants.GROUPDELIMITER);
											}
											//end of groups
											record.put("APILTM",apimgroups.toString());
										}
									}
								}

							}

							//SOURCE SOURCETYPE SOURCECOUNTRY SOURCEID

							source =(Element) head.getChild("source",noNamespace);
							//parseSourceElement(source,record,bibrecord);

							//CORRESPONDENCE
							//Element correspondence = (Element) head.getChild("correspondence",noNamespace);
							//change to get multiple correspondence
							List correspondences = head.getChildren("correspondence",noNamespace);
							//parseCorrespondenceElement(correspondences,record);
							
							//GRANTLIST
							Element grantlist = (Element) head.getChild("grantlist",noNamespace);
							StringBuffer grantBuffer = new StringBuffer();
							//String fundingText = null;
							if(fundingList !=null)
							{
								List fundinggroup = fundingList.getChildren("funding",xocsNamespace);
								for (int i = 0; i < fundinggroup.size(); i++)
								{
									Element funding =(Element) fundinggroup.get(i);
									if(funding.getChildText("funding-id",xocsNamespace)!=null)
									{
										List fundingIDgroup = funding.getChildren("funding-id",xocsNamespace);
										StringBuffer fundingIDbuffer = new StringBuffer();
										for(int j=0;j<fundingIDgroup.size();j++)
										{
											Element fundingid =(Element) fundingIDgroup.get(j);
											fundingIDbuffer.append(fundingid.getTextTrim());
											if(j<fundingIDgroup.size()-1)
											{
												fundingIDbuffer.append(",");
											}
										}
										grantBuffer.append(fundingIDbuffer.toString());
										//System.out.println("FUNDINGID="+fundingIDbuffer.toString());
									}
										
									
									grantBuffer.append(Constants.IDDELIMITER);
									
									if(funding.getChildText("funding-agency-acronym",xocsNamespace)!=null)
									{
										grantBuffer.append(DataLoadDictionary.mapEntity(funding.getChildText("funding-agency-acronym",xocsNamespace)));
									}
									
									grantBuffer.append(Constants.IDDELIMITER);
									
									if(funding.getChildText("funding-agency",xocsNamespace)!=null)
									{
										grantBuffer.append(DataLoadDictionary.mapEntity(funding.getChildText("funding-agency",xocsNamespace)));
									}
									
									grantBuffer.append(Constants.IDDELIMITER);
									
									if(funding.getChildText("funding-agency-id",xocsNamespace)!=null)
									{
										grantBuffer.append(DataLoadDictionary.mapEntity(funding.getChildText("funding-agency-id",xocsNamespace)));
									}
									else if(funding.getChildText("funding-agency-ids",xocsNamespace)!=null)
									{
										grantBuffer.append(DataLoadDictionary.mapEntity(funding.getChildText("funding-agency-ids",xocsNamespace)));
									}
									
									grantBuffer.append(Constants.IDDELIMITER);
									
									if(funding.getChildText("funding-agency-country",xocsNamespace)!=null)
									{
										grantBuffer.append(DataLoadDictionary.mapEntity(funding.getChildText("funding-agency-country",xocsNamespace)));
									}
									
									grantBuffer.append(Constants.IDDELIMITER);
									
									if(funding.getChildText("funding-agency-matched-string",xocsNamespace)!=null)
									{
										grantBuffer.append(DataLoadDictionary.mapEntity(funding.getChildText("funding-agency-country",xocsNamespace)));
									}
																		 
									if(i<fundinggroup.size()-1)
									{
										grantBuffer.append(Constants.AUDELIMITER);
									}	
								}
								
								if(grantBuffer.length()>0)
								{
									record.put("GRANTLIST",grantBuffer.toString());
									//System.out.println(eid+" get funding from funding-list");
								}
																
								if(fundingList.getChild("funding-text",xocsNamespace)!=null)
								{
									List fundingTextList =  fundingList.getChildren("funding-text",xocsNamespace);
									StringBuffer fundingTextBuffer = new StringBuffer();
									for (int j = 0; j < fundingTextList.size(); j++)
									{
										Element fundingText =(Element) fundingTextList.get(j);
										fundingTextBuffer.append(DataLoadDictionary.mapEntity(fundingText.getTextTrim()));
										if(j<fundingTextList.size()-1)
										{
											fundingTextBuffer.append(Constants.AUDELIMITER);
										}
									}
									//System.out.println("GRANTtext from funding="+fundingTextBuffer.toString());
									record.put("GRANTTEXT",fundingTextBuffer.toString());
									//System.out.println(eid+" get fundingText from funding-list");
								}
								
							}
							
							if(grantlist!=null && grantBuffer.length()==0)
							{
								List grantgroup = grantlist.getChildren("grant",noNamespace);
								for (int i = 0; i < grantgroup.size(); i++)
								{
									Element grant =(Element) grantgroup.get(i);
									if(grant.getChildText("grant-id",noNamespace)!=null)
									{
										grantBuffer.append(grant.getChildText("grant-id",noNamespace));
									}
									grantBuffer.append(Constants.IDDELIMITER);
									
									if(grant.getChildText("grant-acronym",noNamespace)!=null)
									{
										grantBuffer.append(DataLoadDictionary.mapEntity(grant.getChildText("grant-acronym",noNamespace)));
									}
									grantBuffer.append(Constants.IDDELIMITER);
									
									if(grant.getChildText("grant-agency",noNamespace)!=null)
									{
										grantBuffer.append(DataLoadDictionary.mapEntity(grant.getChildText("grant-agency",noNamespace)));
									}
									if(i<grantgroup.size()-1)
									{
										grantBuffer.append(Constants.AUDELIMITER);
									}									
								}
								if(grantBuffer.length()>0)
								{
									record.put("GRANTLIST",grantBuffer.toString());
									//System.out.println(eid+" get funding from grantlist");
								}
								
								//added by hmo on 7/17/2017
								//modify by hmo on 02/08/2019
								if(record.get("GRANTTEXT")==null && grantlist.getChild("grant-text",noNamespace)!=null)
								{
									String grantText =  grantlist.getChildText("grant-text",noNamespace);
									record.put("GRANTTEXT",DataLoadDictionary.mapEntity(grantText));
									//System.out.println(eid+" get fundingText from grantlist");
								}
								
							}


						}

						Element tail = bibrecord.getChild("tail",noNamespace);
						if(tail != null)
						{
							Element bibliography = tail.getChild("bibliography",noNamespace);
							if(bibliography != null && bibliography.getAttributeValue("refcount")!=null)
							{
								record.put("REFCOUNT",(String)bibliography.getAttributeValue("refcount"));

								List referencegroup = bibliography.getChildren("reference", noNamespace);
								if(referencegroup!=null)
								{
									//parseReferencegroup(referencegroup,record);
								}
							}

						}						

						// only for elt database conversion
						record.put("DATABASE",databaseName.trim());
						
						//HH 04/05/2016 for Cafe
						record.put("UPDATERESOURCE", s3FileLoc);
					}
					
					//UpdateNumber
					record.put("UPDATENUMBER",this.updatenumber);					

					if(item.getChild("loadnumber", noNamespace) != null)
					{
						record.put("LOADNUMBER",item.getChild("loadnumber", noNamespace).getText());
					}
					else
					{
						//record.put("LOADNUMBER",weekNumber);
						//System.out.println("LOADNUMBER="+weekNumber);
					}

                   //record.put("LOADNUMBER", item.getChildText("load-number", noNamespace));

					Element additionalsrcinfo = null;
					if( source != null)
					{
						additionalsrcinfo = source.getChild("additional-srcinfo",noNamespace);
					}
					Element additionalsrcinfosecjournal = null;
					if(additionalsrcinfo != null)
					{
						additionalsrcinfosecjournal = additionalsrcinfo.getChild("additional-srcinfo-secjournal",noNamespace);
					}

					Element secondaryjournal = null;
					if(additionalsrcinfosecjournal != null)
					{
						secondaryjournal = additionalsrcinfosecjournal.getChild("secondaryjournal",noNamespace);
					}

                    if (secondaryjournal != null)
                    {
                		if(secondaryjournal.getChild("sourcetitle", noNamespace) != null)
    					{
                			record.put("SECSTI", secondaryjournal.getChild("sourcetitle", noNamespace).getText());
    					}
                		if(secondaryjournal.getChild("issn", noNamespace) != null)
    					{
                			record.put("SECISS", secondaryjournal.getChild("issn", noNamespace).getText());
    					}

                        Element voliss = secondaryjournal.getChild("voliss",noNamespace);
                        if(voliss != null && voliss.getAttributeValue("volume")!=null)
				        {
					        record.put("SECVOLUME",(String)voliss.getAttributeValue("volume"));
					    }
                        if(voliss != null && voliss.getAttributeValue("issue")!=null)
						{
					        record.put("SECISSUE",(String)voliss.getAttributeValue("issue"));
						}

                        Element secpublicationdate = secondaryjournal.getChild("publicationdate",noNamespace);

                        if(secpublicationdate != null)
                        {
                            StringBuffer secpubdate = new StringBuffer();

                            if(secpublicationdate.getChild("year", noNamespace) != null)
                            {
                                  secpubdate.append(secpublicationdate.getChild("year", noNamespace).getText());
                            }
                            secpubdate.append(Constants.IDDELIMITER);
                            if(secpublicationdate.getChild("month", noNamespace) != null)
                            {
                                secpubdate.append(secpublicationdate.getChild("month", noNamespace).getText());
                            }
                            secpubdate.append(Constants.IDDELIMITER);
                            if(secpublicationdate.getChild("day", noNamespace) != null)
                            {
                                secpubdate.append(secpublicationdate.getChild("day", noNamespace).getText());
                            }
                            secpubdate.append(Constants.IDDELIMITER);
                            if(secpublicationdate.getChild("date-text", noNamespace) != null)
                            {
                                secpubdate.append(secpublicationdate.getChild("date-text", noNamespace).getText());
                            }
                            secpubdate.append(Constants.IDDELIMITER);
                            record.put("SECPUBDATE", secpubdate.toString());
                        }
                     }
				}

			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return record;
	}

    
    public String saveContentToFile (InputStream objectData, String key) throws IOException
	{
		BufferedReader breader = null;
		PrintWriter out = null;
		StringBuffer output =new StringBuffer();
		try
		{
			breader = new BufferedReader(new InputStreamReader(objectData));
			//breader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(breader.readLine().replaceAll("><", ">\n<").getBytes())));
			if(breader!=null)
			{
				File file = new File("cafeData");
				if (!file.exists()) 
				{
					file.mkdir();
				}
				
				file = new File("cafeData/"+this.loadNumber);
				if (!file.exists()) 
				{
					file.mkdir();
				} 
				
				file = new File("cafeData/"+this.loadNumber+"/"+key+".xml");
				
				/*
				if (!file.exists()) 
				{
					System.out.println("Downloaded: "+file.getName());
				}
				else
				{
					System.out.println("file:" +  file.getName() + "already exist");				
				}
				*/
				
				String line = null;
				out = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath())));
				while ((line = breader.readLine()) !=null)
				{
					out.println(line.replaceAll("><", ">\n<")+"\n");
					output.append(line.replaceAll("><", ">\n<")+"\n");
				}
			}

		}
		catch (IOException e) {

			e.printStackTrace();			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		finally
		{
			try
			{
				if(breader !=null)
				{
					breader.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}


			try
			{
				if(out !=null)
				{
					out.flush();
					out.close();
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			return output.toString();
		}
		

	}
    
    private List getCafeKeys(String archivedDate,Connection con)
    {
    	List cafeDataKeyList = new ArrayList();
        Statement stmt = null;
        ResultSet rs = null;
        try
        {
            stmt = con.createStatement();                              
            String sqlQuery = "select * from cafe_inventory_temp where doc_type='ani' and to_char(archive_date,'yyyymmddhh')='"+archivedDate+"'";           
            System.out.println("SQLQUERY= "+sqlQuery);
            rs = stmt.executeQuery(sqlQuery);   
            int i=0;
            while(rs.next()) 
            {
            	cafeDataKeyList.add(rs.getString("key"));          	
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
    	return cafeDataKeyList;
    }
    
    public CafeLoading(String loadN,String databaseName)
    {
        this.loadNumber = loadN;
        this.databaseName = databaseName;
    }
    
    private Connection getConnection(String connectionURL,
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
    
    public void setBlockedIssnList(Connection con)
    {
    	Statement stmt = null;
        ResultSet rs = null;
        List<String> cpxIssnList = new ArrayList<>();
        List<String> pchIssnList = new ArrayList<>();
        List<String> geoIssnList = new ArrayList<>();
        String issn = null;
        String database = null;
        try
        {
            stmt = con.createStatement();

            rs = stmt.executeQuery("select sn,database from blocked_issn");
            while (rs.next())
            {
                issn = rs.getString("sn");
                database = rs.getString("database");
                if(issn != null)
                {
                	if(database.equals("cpx"))
                	{
                		cpxIssnList.add(issn);
                	}
                	else if(database.equals("pch"))
                	{
                		pchIssnList.add(issn);
                	}
                	else if(database.equals("geo"))
                	{
                		geoIssnList.add(issn);
                	}
                }
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
        this.blockedCpxIssnList=cpxIssnList;
        this.blockedPchIssnList=pchIssnList;
        this.blockedGeoIssnList=geoIssnList;
       
    }
}