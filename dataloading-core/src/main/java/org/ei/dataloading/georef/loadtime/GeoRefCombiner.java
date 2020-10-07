package org.ei.dataloading.georef.loadtime;

import java.io.*;


import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.*;

//import org.ei.data.*;
//import org.ei.data.georef.runtime.*;
import org.ei.domain.*;
import org.ei.util.StringUtil;
import org.ei.util.GUID;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

import org.ei.dataloading.*;
import org.ei.common.*;
import org.ei.common.georef.*;
import org.ei.util.kafka.*;

public class GeoRefCombiner
extends Combiner
{
public static final String AUDELIMITER = Constants.AUDELIMITER;
public static final String IDDELIMITER = Constants.IDDELIMITER;
public String[] EVCombinedRecKeys = {EVCombinedRec.DATABASE, EVCombinedRec.AUTHOR, EVCombinedRec.EDITOR, EVCombinedRec.AUTHOR_AFFILIATION, EVCombinedRec.COUNTRY, EVCombinedRec.AFFILIATION_LOCATION, EVCombinedRec.LANGUAGE, EVCombinedRec.DOCTYPE, EVCombinedRec.ABSTRACT, EVCombinedRec.ISSN, EVCombinedRec.ISBN, EVCombinedRec.PUBLISHER_NAME, EVCombinedRec.INT_PATENT_CLASSIFICATION, EVCombinedRec.CONTROLLED_TERMS, EVCombinedRec.UNCONTROLLED_TERMS, EVCombinedRec.AVAILABILITY, EVCombinedRec.PUB_YEAR, EVCombinedRec.TITLE, EVCombinedRec.TRANSLATED_TITLE, EVCombinedRec.MONOGRAPH_TITLE, EVCombinedRec.SERIAL_TITLE, EVCombinedRec.CONFERENCE_LOCATION, EVCombinedRec.REPORTNUMBER, EVCombinedRec.CLASSIFICATION_CODE, EVCombinedRec.DEDUPKEY, EVCombinedRec.STARTPAGE, EVCombinedRec.CODEN, EVCombinedRec.CONFERENCE_NAME, EVCombinedRec.MEETING_DATE, EVCombinedRec.LOAD_NUMBER, EVCombinedRec.VOLUME, EVCombinedRec.ISSUE, EVCombinedRec.ACCESSION_NUMBER, EVCombinedRec.DOI, EVCombinedRec.PUB_SORT};
Perl5Util perl = new Perl5Util();
private GRFDataDictionary dictionary = GRFDataDictionary.getInstance();
private Map<String,String> categoryMap = dictionary.getCategories();
private static String tablename;
//private static final Database GRF_DATABASE = new GRFDatabase();
private static String databaseIndexName = "grf";
private static String propertyFileName;
private static int loadNumber = 0;

public static void main(String args[])
                        throws Exception
{
	  if(args.length<9)
	  {
		  System.out.println("not enough parameters, need 9 parameters to run");
		  System.exit(1);
	  }
	  String driver = null;
	  String url  = null;
	  String username = null;
	  String password = null;
	  url = args[0];
	  driver  = args[1];
	  username  = args[2];
	  password  = args[3];
	  int loadNumber = 0;
	  int recsPerbatch = Integer.parseInt(args[5]);
	  String operation = args[6];
	  tablename = args[7];
	  String environment = args[8].toLowerCase();
	  if(args.length>9)
	  {
		  propertyFileName=args[9].toLowerCase();
	  }
	
	  try {
	    loadNumber = Integer.parseInt(args[4]);
	  }
	  catch(NumberFormatException e) {
	    loadNumber = 0;
	  }

	  Combiner.TABLENAME = tablename;
	
	  CombinedWriter writer = new CombinedXMLWriter(recsPerbatch,
	                                                loadNumber,
	                                                databaseIndexName, environment);
	  writer.setOperation(operation);
	
	  GeoRefCombiner c = new GeoRefCombiner(writer);
	  if(loadNumber > 100000)
	  {
	    c.writeCombinedByWeekNumber(url,
	                                driver,
	                                username,
	                                password,
	                                loadNumber);
	  }
	  // extract the whole thing
	  else if(loadNumber == 1)
	  {
		  c.writeCombinedByTable( url,
					                  driver,
					                  username,
					                  password);
	  }
	  else if(loadNumber == 0)
	  {
		  int endYear = Integer.parseInt(c.getYear());
	
	    for(int yearIndex = 1918; yearIndex <= endYear+1; yearIndex++)
	    {
	  	System.out.println("Processing year " + yearIndex + "...");
	      // create  a new writer so we can see the loadNumber/yearNumber in the filename
	      c = new GeoRefCombiner(new CombinedXMLWriter(recsPerbatch, yearIndex,databaseIndexName, environment));
	      c.writeCombinedByYear(url,
	                          driver,
	                          username,
	                          password,
	                          yearIndex);
	    }
	  }
	  else
	  {
	    c.writeCombinedByYear(url,
	                          driver,
	                          username,
	                          password,
	                          loadNumber);
	  }
}

	private String getYear()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy");
		Date date = new Date();
		System.out.println("Current Year= "+dateFormat.format(date));
		return dateFormat.format(date);

	}


public GeoRefCombiner(CombinedWriter writer)
{
  super(writer);

  DataValidator d = new DataValidator();
  d.setErrorHandler(new LocalErrorHandler());
  d.setEntityResolver(new LocalEntityResolver());

  ((CombinedXMLWriter) writer).setDataValidator(d);

}

public GeoRefCombiner(CombinedWriter writer,String propertyFileName)
{
	super(writer);
	this.propertyFileName=propertyFileName;
	DataValidator d = new DataValidator();
	d.setErrorHandler(new LocalErrorHandler());
	d.setEntityResolver(new LocalEntityResolver());

	((CombinedXMLWriter) writer).setDataValidator(d);
    
}

public static int getResultSetSize(ResultSet resultSet)
{
	    int size = -1;
	    try
	    {
	        resultSet.last();
	        size = resultSet.getRow();
	        resultSet.beforeFirst();
	    }
	    catch(SQLException e)
	    {
	        return size;
	    }

	    return size;
}

public void writeCombinedByTableHook(Connection con)
throws Exception
{
	Statement stmt = null;
	ResultSet rs = null;
	try
	{

		stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		System.out.println("Running the query...");
		String sqlQuery = "select * from " + Combiner.TABLENAME;
		System.out.println(sqlQuery);
		rs = stmt.executeQuery(sqlQuery);

		System.out.println("Got records ...from table::"+Combiner.TABLENAME);
		writeRecs(rs);
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


public void writeCombinedByWeekHook(Connection con,
                                    int weekNumber)
                                    throws Exception
{
  Statement stmt = null;
  ResultSet rs = null;

  try
  {
    stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

    String sqlQuery = "select * from " + Combiner.TABLENAME + " where load_number ='" + weekNumber + "' AND load_number != 0";
    //String sqlQuery = "select * from " + Combiner.TABLENAME + " where m_id='grf_1ee3914119594abb20M7fcd2061377551'";
    //String sqlQuery = "select * from " + Combiner.TABLENAME + " where m_id='grf_1ee3914119594abb20M7fcd2061377551' or m_id='grf_1ee3914119594abb20M7fc72061377551'";
    //String sqlQuery = "select * from " + Combiner.TABLENAME + " where m_id='grf_1ee3914119594abb20M7ff02061377551'";
    //String sqlQuery = "select * from " + Combiner.TABLENAME + " where m_id='grf_1ee3914119594abb20M80002061377551'";
    rs = stmt.executeQuery(sqlQuery);
    int rsCount = getResultSetSize(rs);
    System.out.println("processing load number "+weekNumber+", total record count is "+rsCount);
    if(rsCount > 0)
    {
	      writeRecs(rs);
	      this.writer.end();
	      this.writer.flush();
    }
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

public void writeCombinedByYearHook(Connection con,
                                    int year)
                                    throws Exception
{
  Statement stmt = null;
  ResultSet rs = null;

  try
  {
    stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

    // Here we will use the Z44: UPDATE CODE to get break the data into years
    /* Field Z44 is used to enter the update code. The update code consists of six digits. The first four
    are the update year. The last two are the number of the update within the year. Currently, GeoRef
    is updated twice per month, so there are 24 updates per year and the update codes for 1996 are
    199601, 199602, 199603, etc. through 199624. Prior to 1994, only the year (first four digits) is
    given in this field. */

    String sqlquery = "SELECT * FROM " + Combiner.TABLENAME + " WHERE UPDATE_CODE IS NOT NULL AND SUBSTR(UPDATE_CODE,1,4) =  '" + year+"'";
    //String sqlquery = "select * from " + Combiner.TABLENAME + " where m_id='grf_1ee3914119594abb20M7fcd2061377551'";
    //String sqlquery = "select * from " + Combiner.TABLENAME + " where m_id='grf_1ee3914119594abb20M7fcd2061377551' or m_id='grf_1ee3914119594abb20M7fc72061377551'";
    //String sqlquery = "select * from " + Combiner.TABLENAME + " where m_id='grf_1ee3914119594abb20M7ff02061377551'";
    //String sqlquery = "select * from " + Combiner.TABLENAME + " where m_id='grf_1ee3914119594abb20M80002061377551'";
    //String sqlquery = "select * from " + Combiner.TABLENAME + " where m_id='grf_e5855a1195a1d697f21ee2061377551'";
    rs = stmt.executeQuery(sqlquery);
    int rsCount = getResultSetSize(rs);
    System.out.println("processing year "+year+", total record count is "+rsCount);
    if(rsCount > 0)
    {
	      writeRecs(rs);
	      this.writer.end();
	      this.writer.flush();
    }
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

public void writeRecs(ResultSet rs)
                        throws Exception
{
	
	int totalCount =-1;
	Thread thread = null;
	KafkaService kafka=null;
	Map<String,String> batchData = new ConcurrentHashMap<String,String>();   
    Map<String,String> missedData = new ConcurrentHashMap<String,String>();
    int counter=0;
    int batchSize = 0;
    MessageSender sendMessage=null;
	long processTime = System.currentTimeMillis();
	String accessNumber = null;
	
	int i = 1;
	try
	{
	    DocumentView runtimeDocview = new CitationView();
	    runtimeDocview.setResultSet(rs);
	    EVCombinedRec recSecondBox = null;
		EVCombinedRec[] recArray = null;
		totalCount = getResultSetSize(rs); 
		if(this.propertyFileName!=null)
		{
			kafka = new KafkaService(processTime+"_grf_"+loadNumber, this.propertyFileName);
		}
		System.out.println("epoch="+processTime+" database=grf totalCount="+totalCount);
	   
	    while (rs.next())
	    {
	    	try{
		        String firstGUID = "";
		        int numCoords = 1;
		        int coordCount = 0;
		       
			  	Vector recVector = new Vector();
		        for(int currentCoord = 0; currentCoord < numCoords; currentCoord++)
		        {
		        	EVCombinedRec rec = new EVCombinedRec();
		        	String processInfo = processTime+"-"+totalCount+'-'+i+"-grf-"+rs.getString("LOAD_NUMBER")+'-'+rs.getString("UPDATENUMBER");
	            	rec.put(EVCombinedRec.PROCESS_INFO, processInfo);
		        	String[] coords = null;
		        	String[] secondBoxCoords= null;
				    coordCount++;
					
	
					rec.putIfNotNull(EVCombinedRec.DATABASE, databaseIndexName);
	
					// AUS
					String aString = rs.getString("PERSON_ANALYTIC");
					if(aString != null)
					{				  
					  rec.put(EVCombinedRec.AUTHOR, aString.split(AUDELIMITER));
					}
	
					// EDS
					String eString = rs.getString("PERSON_MONOGRAPH");
					if(eString != null)
					{
					  String otherEditors = rs.getString("PERSON_COLLECTION");
					  if(otherEditors != null)
					  {
						eString = eString.concat(AUDELIMITER).concat(otherEditors);
					  }
					  rec.put(EVCombinedRec.EDITOR, eString.split(AUDELIMITER));
					}
	
					// AFF
					String dtStrings = runtimeDocview.new DocumentTypeDecorator(runtimeDocview.createColumnValueField("DOCUMENT_TYPE")).getValue();
					String affilitation = rs.getString("AUTHOR_AFFILIATION");
					if(affilitation != null)
					{
					  List affilations= new ArrayList();
					  String[] affilvalues = null;
					  String[] values = null;
					  affilvalues = affilitation.split(AUDELIMITER);
					  for(int x = 0 ; x < affilvalues.length; x++)
					  {
						  affilations.add(affilvalues[x]);
					  }
	
					  // parse out second Affilitation institutions
					  if(rs.getString("AFFILIATION_SECONDARY") != null)
					  {
						String secondaffiliations = rs.getString("AFFILIATION_SECONDARY");
						affilvalues = secondaffiliations.split(AUDELIMITER);
						for(int x = 0 ; x < affilvalues.length; x++)
						{
						  values = affilvalues[x].split(IDDELIMITER);
						  affilations.add(values[0]);
						}
					  }
					  
					  //add university into author affiliation field if document_type contains( T )
					  //pre frank's request, changed on 11/15/2016 by hmo
					  if(dtStrings!=null && dtStrings.indexOf('T')>-1)
					  {
						  String university = rs.getString("UNIVERSITY");
						  if(university!=null && university.length()>0)
						  {
							  affilations.add(university);
						  }
					  }
					  
					  if(!affilations.isEmpty())
					  {
						rec.putIfNotNull(EVCombinedRec.AUTHOR_AFFILIATION, (String[]) affilations.toArray(new String[]{}));
					  }
					}
					else if(dtStrings!=null && dtStrings.indexOf('T')>-1 && rs.getString("UNIVERSITY")!=null)
					{					  
						rec.putIfNotNull(EVCombinedRec.AUTHOR_AFFILIATION, rs.getString("UNIVERSITY"));					  
					}
	
	
					// CO - Author Affiliation Countries and Author Affiliation Location(s)
					// Uses runtime Docview instance to access decorator class
					String country = rs.getString("AUTHOR_AFFILIATION_COUNTRY");
					if(country != null)
					{
					  List affcountries = new ArrayList();
					  List affilitationlocations = new ArrayList();
	
					  DocumentView.FieldDecorator cd = runtimeDocview.new CountryDecorator(country);
					  affcountries.add(cd.getValue());
	
					  // parse out second Affilitation Countries
					  if(rs.getString("AFFILIATION_SECONDARY") != null)
					  {
						Map countries = GRFDataDictionary.getInstance().getCountries();
						String secondaffiliations = rs.getString("AFFILIATION_SECONDARY");
						String[] affilvalues = secondaffiliations.split(AUDELIMITER);
						for(int z = 0 ; z < affilvalues.length; z++)
						{
						  String[] values = affilvalues[z].split(IDDELIMITER);
						  for(int x = 0; x < values.length; x++)
						  {
							if(countries.containsValue(values[x]))
							{
							  affcountries.add(values[x]);
							}
							else if(x != 0)
							{
							  // pick up address info that is not the first entry, which is the Affiliated Institution
							  // or the name of a country
							  affilitationlocations.add(values[x]);
							}
						  }
						}
					  }
					  if(!affcountries.isEmpty())
					  {
						rec.putIfNotNull(EVCombinedRec.COUNTRY, (String[]) affcountries.toArray(new String[]{}));
						affilitationlocations.addAll(affcountries);
					  }
					  if(rs.getString("AUTHOR_AFFILIATION_ADDRESS") != null)
					  {
						affilitationlocations.add(rs.getString("AUTHOR_AFFILIATION_ADDRESS"));
					  }
					  if(!affilitationlocations.isEmpty())
					  {
						rec.putIfNotNull(EVCombinedRec.AFFILIATION_LOCATION, (String[]) affilitationlocations.toArray(new String[]{}));
					  }
	
					}
	
					// LA
					String laStrings = runtimeDocview.new LanguageDecorator(runtimeDocview.createColumnValueField("LANGUAGE_TEXT")).getValue();
					if(laStrings != null)
					{
					  rec.putIfNotNull(EVCombinedRec.LANGUAGE, laStrings.split(AUDELIMITER));
					}
	
					// DT
	
						
					if(dtStrings != null && dtStrings.equals("In Process"))
					{
						rec.putIfNotNull(EVCombinedRec.DOCTYPE, "GI");
					}
					else if(dtStrings != null)
					{
	
					  // Get EV system DOC_TYPE codes for indexing and append them to (or use in favor of ?) the GeoRef values
					  //modified by hmo at 20190904 to deal with null value of "BIBLIOGRAPHIC_LEVEL_CODE" column
					  String mappingcode = null;
					  if(runtimeDocview.createColumnValueField("DOCUMENT_TYPE").getValue()!=null && runtimeDocview.createColumnValueField("BIBLIOGRAPHIC_LEVEL_CODE").getValue()!=null)
					  {
						  mappingcode = runtimeDocview.createColumnValueField("DOCUMENT_TYPE").getValue().concat(AUDELIMITER).concat(runtimeDocview.createColumnValueField("BIBLIOGRAPHIC_LEVEL_CODE").getValue());
					  }
					  else if(runtimeDocview.createColumnValueField("DOCUMENT_TYPE").getValue()!=null)
					  {
						  mappingcode = runtimeDocview.createColumnValueField("DOCUMENT_TYPE").getValue();
					  }
					  
					  if(mappingcode != null)
					  {
						// DocumentTypeMappingDecorator takes <DOCTYPE>AUDELIMITER<BIBCODE> String as field argument
						mappingcode = runtimeDocview.new DocumentTypeMappingDecorator(mappingcode).getValue();
						// DO NOT CONCAY GEOPREF DOCTYPES OR ELSE THEY WILL SHOW UP IN NAVIGATOR TOO
						dtStrings = mappingcode; // dtStrings.concat(AUDELIMITER).concat(mappingcode);
						
					  }
					  //System.out.println("**** "+rs.getString("ID_NUMBER")+" dtStrings2="+dtStrings.replaceAll(AUDELIMITER, "*"));
					  rec.putIfNotNull(EVCombinedRec.DOCTYPE, dtStrings.split(AUDELIMITER));
					}
	
	
	
					// AB
					String abString =  StringUtil.getStringFromClob(rs.getClob("ABSTRACT"));
					if (abString != null && abString.length() > 0)
					{
					  rec.put(EVCombinedRec.ABSTRACT, abString);
					}
	
					// SN
					if (rs.getString("ISSN") != null || rs.getString("EISSN") != null)
					{
					  String issn = rs.getString("ISSN");
					  String e_issn = rs.getString("EISSN");
					  StringBuffer issnString = new StringBuffer();
					  if(issn != null && issn.length()>0)
					  {
						issnString.append(issn);
						if(e_issn != null && e_issn.length()>0)
						{
						  issnString.append(AUDELIMITER);
						}
					  }
					  if(e_issn != null && e_issn.length()>0)
					  {
						issnString.append(e_issn);
					  }
					  rec.put(EVCombinedRec.ISSN, (issnString.toString()).split(AUDELIMITER));
					}
					// Multiple ISBNs exist in GeoRef (ID:2008-005033)
					if(rs.getString("ISBN") != null)
					{
					  rec.put(EVCombinedRec.ISBN,(rs.getString("ISBN")).split(AUDELIMITER));
					}
					if(rs.getString("PUBLISHER") != null)
					{
					  rec.put(EVCombinedRec.PUBLISHER_NAME,(rs.getString("PUBLISHER")).split(AUDELIMITER));
					}
	
	
					// COORDINATES - Extract attached Index Terms
					if(rs.getString("COORDINATES") != null)
					{
					  String strcoordinates = rs.getString("COORDINATES");
					  String[] termcoordinate = strcoordinates.split(AUDELIMITER);
					  List geoterms = new ArrayList();
					  for(int j = 0; j < termcoordinate.length; j++)
					  {
						String[] termcoordinates = termcoordinate[j].split(IDDELIMITER);
						if(termcoordinates.length == 1)
						{
							String[] termcoordinates_tmp = new String[2];
							termcoordinates_tmp[0] = j + "";
							termcoordinates_tmp[1] = termcoordinates[0];
							termcoordinates = termcoordinates_tmp;
	
						}
						if(termcoordinates.length == 2)
						{
						  if(!termcoordinates[0].matches("\\d+"))
						  {
							  geoterms.add(termcoordinates[0]);
						  }
						  coords = parseCoordinates(termcoordinates[1]);
						  if(coords != null &&  coords.length>4)
						  {
							  if(coords[4].indexOf("-") == -1 && coords[3].indexOf("-") != -1)
							  {
								//secondBoxCoords = parseCoordinates(termcoordinates[1]);
								//System.out.println(secondBoxCoords[1] + "," + secondBoxCoords[2] + "," + secondBoxCoords[3] + "," + secondBoxCoords[4]);
								coords[3] = "180";
								//recSecondBox = new EVCombinedRec();
							  }
	
							  if(j == currentCoord)
							  {
								//System.out.println(coords[1] + "," + coords[2] + "," + coords[3] + "," + coords[4]);
								rec.put(EVCombinedRec.LAT_SE, coords[1]);
								rec.put(EVCombinedRec.LAT_NW, coords[2]);
								rec.put(EVCombinedRec.LNG_SE, coords[3]);
								rec.put(EVCombinedRec.LNG_NW, coords[4]);
								rec.put(EVCombinedRec.LAT_NE, coords[2]);
								rec.put(EVCombinedRec.LNG_NE, coords[3]);
								rec.put(EVCombinedRec.LAT_SW, coords[1]);
								rec.put(EVCombinedRec.LNG_SW, coords[4]);
							  }
					  		}
						}
					  }
					  if(!geoterms.isEmpty())
					  {
						rec.putIfNotNull(EVCombinedRec.INT_PATENT_CLASSIFICATION, (String[])geoterms.toArray(new String[]{}));
					  }
					}
	
					// INDEX_TERMS (CVS)
					if(rs.getString("INDEX_TERMS") != null)
					{
					  String[] idxterms = rs.getString("INDEX_TERMS").split(AUDELIMITER);
					  for(int z = 0; z < idxterms.length; z++)
					  {
						idxterms[z] = idxterms[z].replaceAll("[A-Z]*" + IDDELIMITER,"");
					  }
					  rec.putIfNotNull(EVCombinedRec.CONTROLLED_TERMS, idxterms);
					}
	
					// FLS
					if(rs.getString("UNCONTROLLED_TERMS") != null)
					{
					  rec.put(EVCombinedRec.UNCONTROLLED_TERMS,(rs.getString("UNCONTROLLED_TERMS")).split(AUDELIMITER));
					}
	
					if(rs.getString("AVAILABILITY") != null)
					{
					  rec.putIfNotNull(EVCombinedRec.AVAILABILITY, rs.getString("AVAILABILITY").split(AUDELIMITER));
					}
					
					rec.putIfNotNull(EVCombinedRec.PUB_YEAR, runtimeDocview.getYear());
					rec.putIfNotNull(EVCombinedRec.TITLE, runtimeDocview.getTitle());
					rec.putIfNotNull(EVCombinedRec.TRANSLATED_TITLE, runtimeDocview.getTranslatedTitle());
					rec.putIfNotNull(EVCombinedRec.MONOGRAPH_TITLE, runtimeDocview.getMonographTitle());
					rec.putIfNotNull(EVCombinedRec.SERIAL_TITLE, rs.getString("TITLE_OF_SERIAL"));
	
					// RN - a multi field
					if(rs.getString("LOCATION_OF_MEETING") != null)
					{
					  rec.putIfNotNull(EVCombinedRec.CONFERENCE_LOCATION, rs.getString("LOCATION_OF_MEETING").split(AUDELIMITER));
					}
	
					// RN - a multi field
					if(rs.getString("REPORT_NUMBER") != null)
					{
					  rec.putIfNotNull(EVCombinedRec.REPORTNUMBER,(rs.getString("REPORT_NUMBER")).split(AUDELIMITER));
					}
	
					// CL
					if(rs.getString("CATEGORY_CODE") != null)
					{
					  rec.put(EVCombinedRec.CLASSIFICATION_CODE,getCategoryCodes((rs.getString("CATEGORY_CODE")).split(AUDELIMITER)));
					}
					if(rs.getString("seq_num") != null)
					{
						rec.put(EVCombinedRec.PARENT_ID, rs.getString("seq_num"));
					}
					String pages = runtimeDocview.getPages();
					rec.put(EVCombinedRec.DEDUPKEY,
							getDedupKey(rec.getString(EVCombinedRec.ISSN),
										rec.getString(EVCombinedRec.CODEN),
										rs.getString("VOLUME_ID"),
										rs.getString("ISSUE_ID"),
										pages));
					rec.putIfNotNull(EVCombinedRec.STARTPAGE, getFirstPage(pages));
	
					// CODEN
					String coden = rs.getString("CODEN");
					if(coden != null)
					{
					  List codens = new ArrayList();
					  String[] codenvalues = null;
					  codenvalues = coden.split(AUDELIMITER);
					  for(int x = 0 ; x < codenvalues.length; x++)
					  {
						  codens.add(codenvalues[x]);
					  }
					  if(!codens.isEmpty())
					  {
						rec.putIfNotNull(EVCombinedRec.CODEN, (String[]) codens.toArray(new String[]{}));
					  }
					}
	
					//rec.putIfNotNull(EVCombinedRec.CODEN, rs.getString("CODEN"));
	
					rec.putIfNotNull(EVCombinedRec.CONFERENCE_NAME, rs.getString("NAME_OF_MEETING"));
					rec.putIfNotNull(EVCombinedRec.MEETING_DATE, rs.getString("DATE_OF_MEETING"));
	
					if(currentCoord == 0)
					{
						firstGUID = rs.getString("M_ID");
					}
					if(numCoords == 1)
					{
						rec.putIfNotNull(EVCombinedRec.DOCID, firstGUID);
					}
					else
					{
						rec.putIfNotNull(EVCombinedRec.DOCID, firstGUID + "_" + (coordCount));
	
					}
					rec.putIfNotNull(EVCombinedRec.LOAD_NUMBER, rs.getString("LOAD_NUMBER"));
					
					//added by hmo on 2019/09/04 for adding updatenumber to Fast search
					rec.putIfNotNull(EVCombinedRec.UPDATE_NUMBER, rs.getString("UPDATENUMBER"));
					
					rec.putIfNotNull(EVCombinedRec.VOLUME, getFirstNumber(rs.getString("VOLUME_ID")));
					rec.putIfNotNull(EVCombinedRec.ISSUE, getFirstNumber(rs.getString("ISSUE_ID")));
					accessNumber=rs.getString("ID_NUMBER");
					rec.putIfNotNull(EVCombinedRec.ACCESSION_NUMBER,rs.getString("ID_NUMBER"));
					rec.putIfNotNull(EVCombinedRec.DOI, rs.getString("DOI"));
	
					rec.put(EVCombinedRec.PUB_SORT, Integer.toString(i));
					
					//added on 3/16/2016
					if(rs.getString("TITLE_OF_COLLECTION")!=null)
					{
						//String test = "";
						rec.putIfNotNull(EVCombinedRec.TITLE_OF_COLLECTION, runtimeDocview.getTitleOFCOLLECTION() );
					}
					
					if(rs.getString("UNIVERSITY")!=null)
					{
						rec.putIfNotNull(EVCombinedRec.UNIVERSITY, rs.getString("UNIVERSITY"));
						
					}
					
					if(rs.getString("TYPE_OF_DEGREE")!=null)
					{
						rec.putIfNotNull(EVCombinedRec.TYPE_OF_DEGREE, rs.getString("TYPE_OF_DEGREE"));
					}
					
					if(rs.getString("ANNOTATION")!=null)
					{
						rec.putIfNotNull(EVCombinedRec.ANNOTATION, rs.getString("ANNOTATION").split(AUDELIMITER));
					}
					
					if(rs.getString("MAP_SCALE")!=null)
					{
						rec.putIfNotNull(EVCombinedRec.MAP_SCALE, rs.getString("MAP_SCALE").split(AUDELIMITER));
					}
					
					if(rs.getString("MAP_TYPE")!=null)
					{
						rec.putIfNotNull(EVCombinedRec.MAP_TYPE, rs.getString("MAP_TYPE").split(AUDELIMITER));
					}
					
					if(rs.getString("SOURCE_NOTE")!=null)
					{
						rec.putIfNotNull(EVCombinedRec.SOURCE_NOTE, rs.getString("SOURCE_NOTE"));
					}
	
					//end of added
					
					try
					{
					  recVector.add(rec);
					  if(recSecondBox != null)
					  {
	
						if(secondBoxCoords != null)
						{
							coordCount++;
							for(int b = 0; b < EVCombinedRecKeys.length; b++)
							{
								Object recTemp = rec.get(EVCombinedRecKeys[b]);
								if(recTemp != null)
								{
									recSecondBox.put(EVCombinedRecKeys[b],rec.get(EVCombinedRecKeys[b]));
								}
							}
							secondBoxCoords[4] = "-180";
							recSecondBox.put(EVCombinedRec.LAT_SE, secondBoxCoords[1]);
							recSecondBox.put(EVCombinedRec.LAT_NW, secondBoxCoords[2]);
							recSecondBox.put(EVCombinedRec.LNG_SE, secondBoxCoords[3]);
							recSecondBox.put(EVCombinedRec.LNG_NW, secondBoxCoords[4]);
							recSecondBox.put(EVCombinedRec.LAT_NE, secondBoxCoords[2]);
							recSecondBox.put(EVCombinedRec.LNG_NE, secondBoxCoords[3]);
							recSecondBox.put(EVCombinedRec.LAT_SW, secondBoxCoords[1]);
							recSecondBox.put(EVCombinedRec.LNG_SW, secondBoxCoords[4]);
							recSecondBox.putIfNotNull(EVCombinedRec.DOCID, firstGUID + "_" + (coordCount));
					  		recVector.add(recSecondBox);
							}
						  }
		
						}
						catch(Exception e)
						{
						  System.out.println("MID1 = " + rs.getString("M_ID"));
						  e.printStackTrace();
						}
						
				} // for
		        i++;
				recArray = (EVCombinedRec[])recVector.toArray(new EVCombinedRec[0]);
				
				if(this.propertyFileName==null)
				{
					this.writer.writeRec(recArray); //use this line for FAST extraction
				}
				else
				{
					 /**********************************************************/
			        //following code used to test kafka by hmo@2020/01/30
			        //this.writer.writeRec(recArray,kafka);
			        /***********************************************************/
					
			        //this.writer.writeRec(recArray,kafka);
			        
			        this.writer.writeRec(recArray,kafka, batchData, missedData);
		            if(counter<batchSize)
		            {            	
		            	counter++;
		            }
		            else
		            { /*       
		            	 thread = new Thread(sendMessage);
		            	 sendMessage= new MessageSender(kafka,batchData,missedData);		            	 
		            	 thread.start(); 
		            	 */
		            	 kafka.runBatch(batchData,missedData);
		            	 batchData = new ConcurrentHashMap<String,String>();
		            	 counter=0;		
		            	 
		            }
				}
		        
		         
		        
			}
			catch(Exception e)
			{
			  System.out.println("MID2 = " + rs.getString("M_ID"));
			  e.printStackTrace();
			}
	    } // while
  }
  catch(Exception e)
  {
    e.printStackTrace();
  }
  finally
  {
	  	System.out.println("Total "+i+" records");
	  	if(i!=totalCount)
		{
			System.out.println("**Got "+i+" records instead of "+totalCount);
		}
	  	
	  	if(this.propertyFileName!=null)
	  	{
			try
		  	{
				/*
		  		 thread = new Thread(sendMessage);
		      	 sendMessage= new MessageSender(kafka,batchData,missedData);            	 
		      	 thread.start(); 
		      	 */
				kafka.runBatch(batchData,missedData);
		  	}
		  	catch(Exception ex) 
		  	{
		  		ex.printStackTrace();
		  	}
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
  }
}

private String[] getCategoryCodes(String[] in)
{
	if(in!=null)
	{
		String[] out = new String[in.length];
		//int j=0;
		for(int i=0;i<in.length;i++)
		{
			//out[j]=in[i];
			//j=j+1;
			String categoryText = categoryMap.get(in[i]);
			if(categoryText!=null)
			{
				if( categoryText.indexOf("(")>0)
				{
					categoryText=categoryText.substring(0,categoryText.indexOf("(")-1);
				}
				out[i]=in[i]+"-"+categoryText.trim();
				//j=j+1;
				//out[j]=categoryText.trim()+" (category)";
				//j=j+1;
			}
		}
		return out;
		
	}
	else
	{
		return null;
	}
}

private String[] parseMeridianData(String column)
{
  String[] keys = null;
  if(column != null)
  {
    String[] features = column.split("\\|");
    keys = new String[features.length];
    for(int i = 0; i < features.length; i++)
    {
      keys[i] = features[i].split(";")[0];
    }
  }
  return keys;
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

private String getFirstPage(String v)
{

  MatchResult mResult = null;
  if (v == null)
  {
    return null;
  }

  if (perl.match("/[A-Z]?[0-9][0-9]*/", v))
  {
    mResult = perl.getMatch();
  }
  else
  {
    return null;
  }

  return mResult.toString();
}


private String getDedupKey(String issn,
                            String coden,
                            String volume,
                            String issue,
                            String page)
                            throws Exception
{

  String firstVolume = getFirstNumber(volume);
  String firstIssue = getFirstNumber(issue);
  String firstPage = getFirstPage(page);

  if ((issn == null && coden == null) ||
  firstVolume == null ||
  firstIssue == null ||
  firstPage == null)
  {
    return (new GUID()).toString();
  }

  StringBuffer buf = new StringBuffer();

  if (issn != null)
  {
    buf.append(perl.substitute("s/-//g", issn));
  }
  else if(coden != null)
  {
    buf.append(coden);
  }

  buf.append("vol" + firstVolume);
  buf.append("is" + firstIssue);
  buf.append("pa" + firstPage);

  return buf.toString().toLowerCase();

}

private String[] parseCoordinates(String cs) throws Exception
{
	    cs = cs.replaceAll("[^a-zA-Z0-9]", "");
		String coordString = cs.trim().replaceAll("([NEWS])","-$1");

		String[] coords = coordString.split("-");
		for(int i=1;i< coords.length;i++)
		{
			if(coords[i]!=null && coords[i].length() < 7)
			{
				int padCount = 8 - coords[i].length();
				for(int p=0;p < padCount;p++)
					coords[i] += "0";
			}

			coords[i] = coords[i].replaceAll("[NE]","+").substring(0,coords[i].length()-4).replaceAll("\\+","");
			coords[i] = coords[i].replaceAll("[WS]","-");
			if(coords[i].substring(0,1).indexOf("-") != -1)
				coords[i] = coords[i].replaceAll("^(-)0{1,2}(.*?)","$1$2");
			else
				coords[i] = coords[i].replaceAll("^0{1,2}(.*?)","$1");
		}

		return coords;
}

private class LocalEntityResolver implements EntityResolver {

  public LocalEntityResolver() {
      // TODO Auto-generated constructor stub
  }
  public InputSource resolveEntity(String publicId, String systemId) {
      InputStreamReader is = null;
//      try {
          String dtdfile = new File(systemId).getName();
          //System.out.println("<!--" + dtdfile + " == " + systemId + "-->");
          //InputStream in = this.getClass().getClassLoader().getResourceAsStream("org/ei/data/" + dtdfile);   //original

          //HH 01/18/2015
          InputStream in = this.getClass().getClassLoader().getResourceAsStream("org/ei/data/georef/loadtime/" + dtdfile);

          if(in != null)
          {
            is = new InputStreamReader(in);
          }
          else
          {
            System.out.println("Cannot open resource as stream");
          }
//      } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
//          e.printStackTrace();
//      }
      return new InputSource(is);
  }

}

private class LocalErrorHandler implements ErrorHandler {
  public void warning(SAXParseException e) throws SAXException {
    System.out.println("Warning: ");
    printInfo(e);
  }

  public void error(SAXParseException e) throws SAXException {
    System.out.println("Error: ");
    printInfo(e);
  }

  public void fatalError(SAXParseException e) throws SAXException {
    System.out.println("Fatal error: ");
    printInfo(e);
  }

  private void printInfo(SAXParseException e) {
    //System.out.println("   Public ID: " + e.getPublicId());
    //System.out.println("   System ID: " + e.getSystemId());
    System.out.println("   Line number: " + e.getLineNumber());
    System.out.println("   Column number: " + e.getColumnNumber());
    System.out.println("   Message: " + e.getMessage());
  }
}

}
