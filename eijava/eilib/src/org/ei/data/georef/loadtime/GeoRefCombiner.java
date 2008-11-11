package org.ei.data.georef.loadtime;

import java.io.*;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.regex.*;

import org.ei.data.*;
import org.ei.data.georef.runtime.*;
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

public class GeoRefCombiner
  extends Combiner
{
  public static final String AUDELIMITER = GRFDocBuilder.AUDELIMITER;
  public static final String IDDELIMITER = GRFDocBuilder.IDDELIMITER;
  public String[] EVCombinedRecKeys = {EVCombinedRec.DATABASE, EVCombinedRec.AUTHOR, EVCombinedRec.EDITOR, EVCombinedRec.AUTHOR_AFFILIATION, EVCombinedRec.COUNTRY, EVCombinedRec.AFFILIATION_LOCATION, EVCombinedRec.LANGUAGE, EVCombinedRec.DOCTYPE, EVCombinedRec.ABSTRACT, EVCombinedRec.ISSN, EVCombinedRec.ISBN, EVCombinedRec.PUBLISHER_NAME, EVCombinedRec.INT_PATENT_CLASSIFICATION, EVCombinedRec.CONTROLLED_TERMS, EVCombinedRec.UNCONTROLLED_TERMS, EVCombinedRec.AVAILABILITY, EVCombinedRec.PUB_YEAR, EVCombinedRec.TITLE, EVCombinedRec.TRANSLATED_TITLE, EVCombinedRec.MONOGRAPH_TITLE, EVCombinedRec.SERIAL_TITLE, EVCombinedRec.CONFERENCE_LOCATION, EVCombinedRec.REPORTNUMBER, EVCombinedRec.CLASSIFICATION_CODE, EVCombinedRec.DEDUPKEY, EVCombinedRec.STARTPAGE, EVCombinedRec.CODEN, EVCombinedRec.CONFERENCE_NAME, EVCombinedRec.MEETING_DATE, EVCombinedRec.LOAD_NUMBER, EVCombinedRec.VOLUME, EVCombinedRec.ISSUE, EVCombinedRec.ACCESSION_NUMBER, EVCombinedRec.DOI, EVCombinedRec.PUB_SORT};
  Perl5Util perl = new Perl5Util();
 
  private static String tablename;
  private static final Database GRF_DATABASE = new GRFDatabase();
  public static void main(String args[])
                          throws Exception
  {
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

    try {
      loadNumber = Integer.parseInt(args[4]);
    }
    catch(NumberFormatException e) {
      loadNumber = 0;
    }

    Combiner.TABLENAME = tablename;
    
    CombinedWriter writer = new CombinedXMLWriter(recsPerbatch,
                                                  loadNumber,
                                                  GRF_DATABASE.getIndexName(), environment);
    writer.setOperation(operation);
        
    GeoRefCombiner c = new GeoRefCombiner(writer);
    if(loadNumber > 200801)
    {
      c.writeCombinedByWeekNumber(url,
                                  driver,
                                  username,
                                  password,
                                  loadNumber);
    }
    // extract the whole thing
    else if(loadNumber == 0)
    {
      for(int yearIndex = 1960; yearIndex <= 2008; yearIndex++)
      {
    	System.out.println("Processing year " + yearIndex + "...");
        // create  a new writer so we can see the loadNumber/yearNumber in the filename
        c = new GeoRefCombiner(new CombinedXMLWriter(recsPerbatch, yearIndex,GRF_DATABASE.getIndexName(), environment));        
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

  public GeoRefCombiner(CombinedWriter writer)
  {
    super(writer);

    DataValidator d = new DataValidator();
    d.setErrorHandler(new LocalErrorHandler());
    d.setEntityResolver(new LocalEntityResolver());
    
    ((CombinedXMLWriter) writer).setDataValidator(d);

  }

  public void writeCombinedByWeekHook(Connection con,
                                      int weekNumber)
                                      throws Exception
  {
    Statement stmt = null;
    ResultSet rs = null;

    try
    {
      stmt = con.createStatement();
      String sqlQuery = "select * from " + Combiner.TABLENAME + " where load_number ='" + weekNumber + "' AND load_number != 0 and load_number < 1000000";      
      //String sqlQuery = "select * from " + Combiner.TABLENAME + " where m_id='grf_1ee3914119594abb20M7fcd2061377551'";
      //String sqlQuery = "select * from " + Combiner.TABLENAME + " where m_id='grf_1ee3914119594abb20M7fcd2061377551' or m_id='grf_1ee3914119594abb20M7fc72061377551'";
      //String sqlQuery = "select * from " + Combiner.TABLENAME + " where m_id='grf_1ee3914119594abb20M7ff02061377551'";
      //String sqlQuery = "select * from " + Combiner.TABLENAME + " where m_id='grf_1ee3914119594abb20M80002061377551'";
      rs = stmt.executeQuery(sqlQuery);
      writeRecs(rs);
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

  public void writeCombinedByYearHook(Connection con,
                                      int year)
                                      throws Exception
  {
    Statement stmt = null;
    ResultSet rs = null;

    try
    {
      stmt = con.createStatement();

      // Here we will use the Z44: UPDATE CODE to get break the data into years
      /* Field Z44 is used to enter the update code. The update code consists of six digits. The first four
      are the update year. The last two are the number of the update within the year. Currently, GeoRef
      is updated twice per month, so there are 24 updates per year and the update codes for 1996 are
      199601, 199602, 199603, etc. through 199624. Prior to 1994, only the year (first four digits) is
      given in this field. */

      String sqlquery = "SELECT * FROM " + Combiner.TABLENAME + " WHERE UPDATE_CODE IS NOT NULL AND SUBSTR(UPDATE_CODE,1,4) =  " + year;
      //String sqlquery = "select * from " + Combiner.TABLENAME + " where m_id='grf_1ee3914119594abb20M7fcd2061377551'";
      //String sqlquery = "select * from " + Combiner.TABLENAME + " where m_id='grf_1ee3914119594abb20M7fcd2061377551' or m_id='grf_1ee3914119594abb20M7fc72061377551'";
      //String sqlquery = "select * from " + Combiner.TABLENAME + " where m_id='grf_1ee3914119594abb20M7ff02061377551'";
      //String sqlquery = "select * from " + Combiner.TABLENAME + " where m_id='grf_1ee3914119594abb20M80002061377551'";
      rs = stmt.executeQuery(sqlquery);

      writeRecs(rs);
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

  private void writeRecs(ResultSet rs)
                          throws Exception
  {
    try
    {
      DocumentView runtimeDocview = new CitationView();
      runtimeDocview.setResultSet(rs);
      EVCombinedRec recSecondBox = null;
	  EVCombinedRec[] recArray = null;
      int i = 1;
      while (rs.next())
      {
          String firstGUID = "";
          int numCoords = 0;
          int coordCount = 0;
          String sts = rs.getString("COORDINATES");
          if(sts == null)
          {
          	numCoords = 1;
		  }
		  else
		  {
          	String[] tc = sts.split(GRFDocBuilder.AUDELIMITER);
          	numCoords = tc.length;
	  	  }
	  	  //System.out.println("NUMCOORDS: " + numCoords);
	  	  Vector recVector = new Vector();
          for(int currentCoord = 0; currentCoord < numCoords; currentCoord++)
          {
                String[] coords = null;
                String[] secondBoxCoords= null;
			    coordCount++;
				EVCombinedRec rec = new EVCombinedRec();

				rec.putIfNotNull(EVCombinedRec.DATABASE, GRF_DATABASE.getIndexName());

				// AUS
				String aString = rs.getString("PERSON_ANALYTIC");
				if(aString != null)
				{
				  // DO NOT USE - alternate spellings
				  /* String altAuthor = rs.getString("ALTERNATE_AUTHOR");
				  if(altAuthor != null)
				  {
					aString = aString.concat(AUDELIMITER).concat(altAuthor);
				  } */
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
				  if(!affilations.isEmpty())
				  {
					rec.putIfNotNull(EVCombinedRec.AUTHOR_AFFILIATION, (String[]) affilations.toArray(new String[]{}));
				  }
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
				String dtStrings = runtimeDocview.new DocumentTypeDecorator(runtimeDocview.createColumnValueField("DOCUMENT_TYPE")).getValue();
				if(dtStrings != null)
				{
				  // Get EV system DOC_TYPE codes for indexing and append them to (or use in favor of ?) the GeoRef values
				  String mappingcode = runtimeDocview.createColumnValueField("DOCUMENT_TYPE").getValue().concat(AUDELIMITER).concat(runtimeDocview.createColumnValueField("BIBLIOGRAPHIC_LEVEL_CODE").getValue());
				  if(mappingcode != null)
				  {
					// DocumentTypeMappingDecorator takes <DOCTYPE>AUDELIMITER<BIBCODE> String as field argument
					mappingcode = runtimeDocview.new DocumentTypeMappingDecorator(mappingcode).getValue();
					// DO NOT CONCAY GEOPREF DOCTYPES OR ELSE THEY WILL SHOW UP IN NAVIGATOR TOO
					dtStrings = mappingcode; // dtStrings.concat(AUDELIMITER).concat(mappingcode);
				  }
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
				  String[] termcoordinate = strcoordinates.split(GRFDocBuilder.AUDELIMITER);
				  List geoterms = new ArrayList();
				  for(int j = 0; j < termcoordinate.length; j++)
				  {
					String[] termcoordinates = termcoordinate[j].split(GRFDocBuilder.IDDELIMITER);
					if(termcoordinates.length == 1)
					{
						String[] termcoordinates_tmp = new String[2];
						termcoordinates_tmp[0] = j + "";
						termcoordinates_tmp[1] = termcoordinates[0];
						termcoordinates = termcoordinates_tmp;
						
					}
					if(termcoordinates.length == 2)
					{
					  geoterms.add(termcoordinates[0]);
					  coords = parseCoordinates(termcoordinates[1]);

					  if(coords != null &&  coords[4].indexOf("-") == -1 && coords[3].indexOf("-") != -1)
				      {
						secondBoxCoords = parseCoordinates(termcoordinates[1]);
						//System.out.println(secondBoxCoords[1] + "," + secondBoxCoords[2] + "," + secondBoxCoords[3] + "," + secondBoxCoords[4]);
			            coords[3] = "180";
			            recSecondBox = new EVCombinedRec();
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

				// Meridian data in Patent Navigators
				/*rec.putIfNotNull(EVCombinedRec.INT_PATENT_CLASSIFICATION, parseMeridianData(rs.getString("LAND")));
				rec.putIfNotNull(EVCombinedRec.ECLA_CODES, parseMeridianData(rs.getString("WATER")));
				rec.putIfNotNull(EVCombinedRec.USPTOCODE, parseMeridianData(rs.getString("OIL")));
				rec.putIfNotNull(EVCombinedRec.PATENT_KIND, parseMeridianData(rs.getString("CITIES"))); */


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
				  rec.put(EVCombinedRec.CLASSIFICATION_CODE,(rs.getString("CATEGORY_CODE")).split(AUDELIMITER));
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
				rec.putIfNotNull(EVCombinedRec.VOLUME, getFirstNumber(rs.getString("VOLUME_ID")));
				rec.putIfNotNull(EVCombinedRec.ISSUE, getFirstNumber(rs.getString("ISSUE_ID")));
				rec.putIfNotNull(EVCombinedRec.ACCESSION_NUMBER,rs.getString("ID_NUMBER"));
				rec.putIfNotNull(EVCombinedRec.DOI, rs.getString("DOI"));

				rec.put(EVCombinedRec.PUB_SORT, Integer.toString(i));

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
				  System.out.println("MID = " + rs.getString("M_ID"));
				  e.printStackTrace();
				}
				i++;
		} // for

		recArray = (EVCombinedRec[])recVector.toArray(new EVCombinedRec[0]);
		this.writer.writeRec(recArray);
      } // while
    }
    catch(Exception e)
    {
      e.printStackTrace();
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
		String coordString = cs.trim().replaceAll("([NEWS])","-$1");

		String[] coords = coordString.split("-");
		for(int i=1;i< coords.length;i++)
		{
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
//        try {
            String dtdfile = new File(systemId).getName();
            //System.out.println("<!--" + dtdfile + " == " + systemId + "-->");
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("org/ei/data/" + dtdfile);
            if(in != null)
            {
              is = new InputStreamReader(in);
            }
            else
            {
              System.out.println("Cannot open resource as stream");
            }
//        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
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
      System.out.println("   Public ID: " + e.getPublicId());
      System.out.println("   System ID: " + e.getSystemId());
      System.out.println("   Line number: " + e.getLineNumber());
      System.out.println("   Column number: " + e.getColumnNumber());
      System.out.println("   Message: " + e.getMessage());
    }
  }

}
