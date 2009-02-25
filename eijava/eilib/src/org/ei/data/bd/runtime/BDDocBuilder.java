package org.ei.data.bd.runtime;

import java.util.*;
import java.sql.*;
import java.io.*;

import org.ei.connectionpool.*;
import org.ei.domain.*;
import org.ei.util.StringUtil;
import org.ei.data.*;
import org.ei.data.bd.*;
import org.ei.data.bd.loadtime.*;
import org.apache.oro.text.perl.*;

public class BDDocBuilder
	implements DocumentBuilder
{
	public static String CPX_TEXT_COPYRIGHT = Database.DEFAULT_ELSEVIER_TEXT_COPYRIGHT;
	public static String CPX_HTML_COPYRIGHT = Database.DEFAULT_ELSEVIER_HTML_COPYRIGHT;
	public static String PROVIDER_TEXT = "Ei";
	private static Map issnARFix = new HashMap();
	private static final Key CPX_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "Ei controlled terms");
	private static final Key CPX_CLASS_CODES = new Key(Keys.CLASS_CODES, "Ei classification codes");
	private static final Key CPX_MAIN_HEADING = new Key(Keys.MAIN_HEADING, "Ei main heading");
	private static final Key[] CITATION_KEYS = {Keys.DOCID,Keys.TITLE,Keys.TITLE_TRANSLATION,Keys.EDITORS,Keys.AUTHORS,Keys.AUTHOR_AFFS,Keys.SOURCE,Keys.MONOGRAPH_TITLE, Keys.PAGE_RANGE, Keys.VOLISSUE,Keys.PUBLICATION_YEAR, Keys.PUBLISHER, Keys.ISSUE_DATE, Keys.ISSN, Keys.LANGUAGE ,Keys.NO_SO, Keys.COPYRIGHT,Keys.COPYRIGHT_TEXT, Keys.DOI, Keys.PATAPPNUM, Keys.PATNUM, Keys.PATASSIGN};
	private static final Key[] ABSTRACT_KEYS = {Keys.DOCID,Keys.TITLE,Keys.TITLE_TRANSLATION,Keys.EDITORS,Keys.AUTHORS,Keys.EDITOR_AFFS, Keys.AUTHOR_AFFS,Keys.VOLISSUE, Keys.SOURCE, Keys.PUBLICATION_YEAR, Keys.ISSUE_DATE, Keys.MONOGRAPH_TITLE, Keys.PAGE_RANGE,Keys.CONFERENCE_NAME, Keys.ISSN,Keys.ISBN, Keys.CODEN, Keys.PUBLISHER,Keys.I_PUBLISHER,Keys.CONF_DATE,Keys.SPONSOR, Keys.PROVIDER ,Keys.LANGUAGE, Keys.MAIN_HEADING, CPX_CONTROLLED_TERMS, Keys.UNCONTROLLED_TERMS, Keys.GLOBAL_TAGS, Keys.PRIVATE_TAGS, Keys.ABSTRACT, Keys.NUMBER_OF_REFERENCES,Keys.NO_SO, Keys.COPYRIGHT,Keys.COPYRIGHT_TEXT, Keys.CLASS_CODES , Keys.DOI, Keys.PATAPPNUM, Keys.PATNUM, Keys.PATASSIGN, Keys.REPORT_NUMBER_PAPER};
	private static final Key[] DETAILED_KEYS = {Keys.ACCESSION_NUMBER, Keys.TITLE, Keys.TITLE_TRANSLATION, Keys.AUTHORS,  Keys.AUTHOR_AFFS,Keys.CORRESPONDING_EMAIL,Keys.CORRESPONDING_AUTHORS,Keys.CORRESPONDING_AUTHORS_AFF, Keys.EDITORS,Keys.EDITOR_AFFS, Keys.SERIAL_TITLE, Keys.ABBRV_SERIAL_TITLE, Keys.VOLUME, Keys.ISSUE, Keys.MONOGRAPH_TITLE,Keys.VOLUME_TITLE, Keys.ISSUE_DATE, Keys.PUBLICATION_YEAR, Keys.PAPER_NUMBER, Keys.PAGE_RANGE,Keys.PAGE_ARTICLE_NUMBER, Keys.LANGUAGE, Keys.ISSN, Keys.CODEN, Keys.ISBN, Keys.DOC_TYPE, Keys.CONFERENCE_NAME, Keys.CONF_DATE, Keys.MEETING_LOCATION, Keys.CONF_CODE, Keys.SPONSOR, Keys.PUBLISHER, Keys.ABSTRACT, Keys.ABSTRACT_TYPE, Keys.NUMBER_OF_REFERENCES, Keys.MAIN_HEADING, Keys.CONTROLLED_TERMS, Keys.UNCONTROLLED_TERMS, Keys.CLASS_CODES, Keys.TREATMENTS,Keys.GLOBAL_TAGS, Keys.PRIVATE_TAGS, Keys.DOI, Keys.DOCID,Keys.PROVIDER, Keys.MEDIA, Keys.CSESS, Keys.PATNUM, Keys.PATAPPNUM, Keys.PRIORITY_INFORMATION, Keys.PLING, Keys.PCODE, Keys.PATASSIGN, Keys.NUMBER_OF_CLAIMS, Keys.NUMBER_OF_FIGURES, Keys.NUMBER_OF_TABLES,Keys.SUB_INDEX, Keys.SPECIFIC_NAMES, Keys.SUPPL, Keys.PDFIX, Keys.REPORT_NUMBER_PAPER, Keys.PI,Keys.PAGE_COUNT,Keys.SPECIES_TERMS,Keys.REGION_CONTROLLED_TERMS,Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT};
	private static final Key[] RIS_KEYS = { Keys.RIS_TY, Keys.RIS_LA , Keys.RIS_N1 , Keys.RIS_TI , Keys.RIS_T1 , Keys.RIS_BT , Keys.RIS_JO ,Keys.RIS_T3 , Keys.RIS_AUS , Keys.RIS_AD , Keys.RIS_EDS , Keys.RIS_VL , Keys.RIS_IS , Keys.RIS_PY , Keys.RIS_AN , Keys.RIS_SP , Keys.RIS_EP, Keys.RIS_SN ,  Keys.RIS_BN ,  Keys.RIS_MD ,Keys.RIS_CY , Keys.RIS_PB,  Keys.RIS_N2 , Keys.RIS_KW , Keys.RIS_CVS , Keys.RIS_FLS , Keys.RIS_DO};
	private static final Key[] XML_KEYS = { Keys.ISSN , Keys.MAIN_HEADING , Keys.NO_SO , Keys.MONOGRAPH_TITLE , Keys.PUBLICATION_YEAR , Keys.VOLUME_TITLE , Keys.CONTROLLED_TERM , Keys.ISBN , Keys.AUTHORS , Keys.DOCID , Keys.SOURCE , Keys.NUMVOL , Keys.EDITOR_AFFS , Keys.EDITORS , Keys.PUBLISHER , Keys.VOLUME , Keys.AUTHOR_AFFS , Keys.PROVIDER , Keys.ISSUE_DATE , Keys.COPYRIGHT_TEXT , Keys.DOI , Keys.PAGE_COUNT , Keys.PUBLICATION_DATE , Keys.TITLE , Keys.LANGUAGE , Keys.PAGE_RANGE , Keys.PAPER_NUMBER , Keys.COPYRIGHT , Keys.ISSUE , Keys.ACCESSION_NUMBER , Keys.CONTROLLED_TERMS};
	public static final String DELIMITER = ",";
	static
	{  //ISSNs with AR field problem
		issnARFix.put("00913286", "");
		issnARFix.put("10833668", "");
		issnARFix.put("10179909", "");
		issnARFix.put("15393755", "");
		issnARFix.put("00319007", "");
		issnARFix.put("10502947", "");
		issnARFix.put("00036951", "");
		issnARFix.put("00218979", "");
		issnARFix.put("00219606", "");
		issnARFix.put("00346748", "");
		issnARFix.put("1070664X", "");
		issnARFix.put("10706631", "");
		issnARFix.put("00948276", "");
		issnARFix.put("00431397", "");
	}

    private Database database;

 	private static String queryBD="select * from bd_master where M_ID IN  ";

    public DocumentBuilder newInstance(Database database)
    {
        return new BDDocBuilder(database);
    }

    public BDDocBuilder()
    {
    }

    public BDDocBuilder(Database database)
    {
        this.database = database;
    }

   /** This method takes a list of DocID objects and dataFormat
    *  and returns a List of EIDoc Objects based on a particular
    *  dataformat
    *  @ param listOfDocIDs
    *  @ param dataFormat
    *  @ return List --list of EIDoc's
    *  @ exception DocumentBuilderException
    */
    public List buildPage(List listOfDocIDs, String dataFormat) throws DocumentBuilderException
    {
        List l = null;
        try
        {
			l = loadDocument(listOfDocIDs,dataFormat);

        }
        catch(Exception e)
        {
            throw new DocumentBuilderException(e);
        }

        return l;
    }

    private List loadDocument(List listOfDocIDs, String dataFormat) throws Exception
	{
		Perl5Util perl = new Perl5Util();
		Hashtable oidTable = getDocIDTable(listOfDocIDs);

		List list=new ArrayList();
		int count=0;
		Connection con=null;
		Statement stmt=null;
		ResultSet rset=null;
		ConnectionBroker broker=null;
        String INString=buildINString(listOfDocIDs);

		try
        {
        	broker=ConnectionBroker.getInstance();
			con=broker.getConnection(DatabaseConfig.SEARCH_POOL);
			stmt = con.createStatement();
        	rset=stmt.executeQuery(queryBD+INString);
        	while(rset.next())
			{
				ElementDataMap ht = new ElementDataMap();
				DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
				EIDoc eiDoc = newEIDoc(did, dataFormat,ht);
				database = did.getDatabase();
				buildField(Keys.DOCID,(DocID)oidTable.get(rset.getString("M_ID")),ht);
				formatRIS(buildField(Keys.ACCESSION_NUMBER,rset.getString("ACCESSNUMBER"),ht), dataFormat,Keys.ACCESSION_NUMBER,Keys.RIS_AN);
				formatRIS(buildField(Keys.DOI,rset.getString("DOI"),ht), dataFormat,Keys.DOI,Keys.RIS_DO);

				formatRIS(buildField(Keys.PUBLICATION_YEAR,getYear(rset.getString("PUBLICATIONYEAR"),perl),ht), dataFormat,Keys.PUBLICATION_YEAR,Keys.RIS_PY);
				buildField(Keys.COPYRIGHT,CPX_HTML_COPYRIGHT,ht);
				formatRIS(buildField(Keys.COPYRIGHT_TEXT,CPX_TEXT_COPYRIGHT,ht), dataFormat, Keys.COPYRIGHT_TEXT, Keys.RIS_N1);
				buildField(Keys.ISSUE_DATE,rset.getString("PUBLICATIONDATE"),ht);
				formatRIS(buildField(Keys.MONOGRAPH_TITLE,rset.getString("ISSUETITLE"),ht), dataFormat, Keys.MONOGRAPH_TITLE, Keys.RIS_BT);
				formatRIS(buildField(Keys.VOLUME,getVolume(rset.getString("VOLUME"),perl),ht), dataFormat, Keys.VOLUME, Keys.RIS_VL);
				formatRIS(buildField(Keys.ISSUE,getIssue(rset.getString("ISSUE"),perl),ht), dataFormat, Keys.ISSUE, Keys.RIS_IS);
				formatRISSerialTitle(buildField(Keys.SERIAL_TITLE,rset.getString("SOURCETITLE"),ht), dataFormat, Keys.SERIAL_TITLE, getDocumentType(rset.getString("CITTYPE"),rset.getString("CONFCODE")));
				if(rset.getString("CODEN") != null)
				{
					buildField(Keys.CODEN, BdCoden.convert(rset.getString("CODEN")),ht);
				}
				formatRIS(buildField(Keys.ISSN,getIssn(rset.getString("ISSN"),rset.getString("EISSN")),ht), dataFormat, Keys.ISSN, Keys.RIS_SN);
				buildField(Keys.ABBRV_SERIAL_TITLE,rset.getString("SOURCETITLEABBREV"),ht);
				buildField(Keys.VOLISSUE,getVolumeIssue(rset.getString("VOLUME"),rset.getString("ISSUE")),ht);
				buildField(Keys.SOURCE,getSource(rset.getString("SOURCETITLE"),
										  rset.getString("SOURCETITLEABBREV"),
										  rset.getString("ISSUETITLE"),
										  rset.getString("PUBLISHERNAME")),ht);
				buildField(Keys.NO_SO,getNoSource(rset.getString("SOURCETITLE"),
										  rset.getString("SOURCETITLEABBREV"),
										  rset.getString("ISSUETITLE"),
										  rset.getString("PUBLISHERNAME")),ht);
				formatRIS(buildField(Keys.TITLE,getCitationTitle(rset.getString("CITATIONTITLE")),ht), dataFormat, Keys.TITLE, Keys.RIS_TI);
				formatRIS(buildField(Keys.TITLE_TRANSLATION,getTranslatedCitationTitle(rset.getString("CITATIONTITLE")),ht), dataFormat, Keys.TITLE_TRANSLATION, Keys.RIS_T1);

				formatRIS(buildField(Keys.ISBN,getIsbn(rset.getString("ISBN"),10),ht), dataFormat, Keys.ISBN, Keys.RIS_BN);
				formatRIS(buildField(Keys.ISBN13,getIsbn(rset.getString("ISBN"),13),ht), dataFormat, Keys.ISBN, Keys.RIS_BN);
				buildField(Keys.PAGE_COUNT,getPageCount(rset.getString("PAGECOUNT")),ht);
				buildField(Keys.PAGE_RANGE,getPageRange(rset.getString("PAGE"),
														 rset.getString("PAGECOUNT"),
														 rset.getString("ARTICLENUMBER"),
														 rset.getString("ISSN"),
														 rset.getString("EISSN"),
														 perl),ht);
				buildField(Keys.PAGE_ARTICLE_NUMBER,rset.getString("ARTICLENUMBER"),ht);

				formatRIS(buildField(Keys.PUBLISHER,getPublisher(rset.getString("PUBLISHERNAME"),rset.getString("PUBLISHERADDRESS")),ht),dataFormat, Keys.PUBLISHER, Keys.RIS_PB);
				formatRIS(buildField(Keys.LANGUAGE,getLanguage(rset.getString("CITATIONLANGUAGE")),ht),dataFormat, Keys.LANGUAGE, Keys.RIS_LA);
				formatRIS(buildField(Keys.AUTHORS,getAuthors(Keys.AUTHORS,rset.getString("AUTHOR"),rset.getString("AUTHOR_1"), dataFormat),ht), dataFormat, Keys.AUTHORS, Keys.RIS_AUS);
                if(!rset.getString("DATABASE").equalsIgnoreCase("pch"))
				{
				    formatRIS(buildField(Keys.AUTHOR_AFFS,getAuthorsAffiliation(Keys.AUTHOR_AFFS,rset.getString("AFFILIATION"),rset.getString("AFFILIATION_1"), dataFormat),ht),dataFormat, Keys.AUTHOR_AFFS, Keys.RIS_AD);
                }
				buildField(Keys.PROVIDER,PROVIDER_TEXT,ht);

				formatRIS(buildField(Keys.EDITORS,getEditors(Keys.EDITORS,rset.getString("AUTHOR"),rset.getString("EDITORS"), rset.getString("CONFERENCEEDITOR")),ht), dataFormat, Keys.EDITORS, Keys.RIS_EDS);
				buildField(Keys.VOLUME_TITLE,rset.getString("VOLUMETITLE"),ht);
				buildField(Keys.PAPER_NUMBER,rset.getString("REPORTNUMBER"),ht);
				formatRIS(buildField(Keys.CONTROLLED_TERMS,setElementData(rset.getString("CONTROLLEDTERM")),ht), dataFormat,Keys.CONTROLLED_TERMS,Keys.RIS_CVS);
				buildField(Keys.PATNUM,rset.getString("PATNO"),ht);
				buildField(Keys.PATAPPNUM,rset.getString("APPLN"),ht);
				buildField(Keys.PATASSIGN,rset.getString("ASSIG"),ht);

				if(!dataFormat.equals(Citation.CITATION_FORMAT) && !dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT))
				{
					buildField(Keys.CONF_CODE,rset.getString("CONFCODE"),ht);
					buildField(Keys.NUMBER_OF_REFERENCES,rset.getString("REFCOUNT"),ht);
					formatRIS(buildField(Keys.MAIN_HEADING,rset.getString("MAINHEADING"),ht), dataFormat,Keys.MAIN_HEADING, Keys.RIS_KW);
					formatRIS(buildField(Keys.UNCONTROLLED_TERMS,setElementData(rset.getString("UNCONTROLLEDTERM")),ht), dataFormat,Keys.UNCONTROLLED_TERMS,Keys.RIS_FLS);
					buildField(Keys.TREATMENTS,getTreatments(rset.getString("TREATMENTCODE"),database),ht);
					formatRIS(buildField(Keys.ABSTRACT,getAbstract(rset),ht),dataFormat,Keys.ABSTRACT,Keys.RIS_N2);
					buildField(Keys.SPONSOR,setSponsorData(rset.getString("CONFSPONSORS")),ht);
					formatRIS(buildField(Keys.START_PAGE,getStartPage(rset.getString("PAGE")),ht),dataFormat,Keys.START_PAGE,Keys.RIS_SP);
					formatRIS(buildField(Keys.END_PAGE,getEndPage(rset.getString("PAGE")),ht),dataFormat,Keys.END_PAGE,Keys.RIS_EP);
					formatRISDocType(buildField(Keys.DOC_TYPE,getDocumentType(rset.getString("CITTYPE"),rset.getString("CONFCODE")),ht),dataFormat,Keys.DOC_TYPE,Keys.RIS_TY);
					formatRIS(buildField(Keys.CONFERENCE_NAME,rset.getString("CONFNAME"),ht),dataFormat,Keys.CONFERENCE_NAME , Keys.RIS_BT);
					formatRIS(buildField(Keys.CONF_DATE,rset.getString("CONFDATE"),ht),dataFormat,Keys.CONF_DATE , Keys.RIS_MD);
					formatRIS(buildField(Keys.MEETING_LOCATION,getConferenceLocation(rset.getString("CONFLOCATION")),ht),dataFormat,Keys.MEETING_LOCATION , Keys.RIS_CY);
					buildField(Keys.REGION_CONTROLLED_TERMS,setElementData(rset.getString("REGIONALTERM")),ht);
					buildField(Keys.SPECIES_TERMS,setElementData(rset.getString("SPECIESTERM")),ht);
					buildField(Keys.CLASS_CODES,getClassification(Keys.CLASS_CODES,rset.getString("CLASSIFICATIONCODE"),database),ht);
					buildField(Keys.ABSTRACT_TYPE,getAbstractType(rset.getString("ABSTRACTORIGINAL")),ht);
					buildField(Keys.MEDIA,rset.getString("MEDIA"),ht);
					buildField(Keys.CSESS,rset.getString("CSESS"),ht);
					buildField(Keys.PATNUM,rset.getString("PATNO"),ht);
					buildField(Keys.PATAPPNUM,rset.getString("APPLN"),ht);
					buildField(Keys.PLING,rset.getString("PLING"),ht);
					buildField(Keys.PRIORITY_INFORMATION,rset.getString("PRIOR_NUM"),ht);
					buildField(Keys.PCODE,rset.getString("PCODE"),ht);
					buildField(Keys.NUMBER_OF_CLAIMS,rset.getString("CLAIM"),ht);
					buildField(Keys.SOURCE,rset.getString("SOURC"),ht);
					buildField(Keys.NUMBER_OF_FIGURES,rset.getString("NOFIG"),ht);
					buildField(Keys.NUMBER_OF_TABLES,rset.getString("NOTAB"),ht);
					buildField(Keys.SUB_INDEX,rset.getString("SUB_INDEX"),ht);
					buildField(Keys.SPECIFIC_NAMES,rset.getString("SPECN"),ht);
					buildField(Keys.SUPPL,rset.getString("SUPPL"),ht);
					buildField(Keys.PDFIX,rset.getString("PDFIX"),ht);
					buildField(Keys.REPORT_NUMBER_PAPER,rset.getString("REPORTNUMBER"),ht);
					//buildField(Keys.PI,rset.getString("PII"),ht);
				}


				if(rset.getString("DATABASE").equalsIgnoreCase("pch"))
				{

				    formatRIS(buildField(Keys.CORRESPONDING_AUTHORS,
				    			getEditors(Keys.CORRESPONDING_AUTHORS,
				    				null,
									null,
									rset.getString("CORRESPONDENCENAME")),ht),
				    				dataFormat,
				    				Keys.CORRESPONDING_AUTHORS, Keys.RIS_EDS);


					buildField(Keys.CORRESPONDING_AUTHORS_AFF,getCorrespondingAuAff(rset.getString("CORRESPONDENCEAFFILIATION")),ht);


				}


				//following field used for testing only, should be commented out after testing
				buildField(Keys.CORRESPONDING_AUTHORS,getCorAuthors(Keys.CORRESPONDING_AUTHORS,rset.getString("CORRESPONDENCENAME"),rset.getString("CORRESPONDENCEEADDRESS")),ht);
				//buildField(Keys.CORRESPONDING_EMAIL,getEmail(rset.getString("CORRESPONDENCEEADDRESS")),ht);

				eiDoc.setLoadNumber(rset.getInt("LOADNUMBER"));
				list.add(eiDoc);
                count++;

			}
		}
		finally
		{

			if(rset != null)
			{
				try
				{
					rset.close();
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
			}

			if(stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch(Exception sqle)
				{
					sqle.printStackTrace();
				}
			}

			if(con != null)
			{
				try
				{
					broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
				}
				catch(Exception cpe)
				{
						cpe.printStackTrace();
				}
			}
		}

        return list;

	}

    //BdCoden.convert

	private String getEmail(String email)
	{
		if(email!=null && email.indexOf(BdParser.IDDELIMITER)>-1)
		{
			email = email.substring(email.indexOf(BdParser.IDDELIMITER)+1);
		}
		return email;
	}

	private String getAbstractType(String abstractType)
	{
		if(abstractType != null && abstractType.equalsIgnoreCase("n"))
		{
			return "(Edited Abstract)";
		}
		return null;
	}

	private String getLanguage(String languageString) throws Exception
	{
		if(languageString != null)
		{
			return Language.getIso639Language(languageString);
		}

		return null;

    }

	private String getVolumeIssue(String volume,String issue) throws Exception
	{
		String strVolIss = StringUtil.EMPTY_STRING;;
		if(volume != null)
		{
			strVolIss = strVolIss.concat(replaceVolumeNullWithEmptyString(volume));
		}

		if(issue != null)
		{
			if(strVolIss != null &&
					!strVolIss.equals(StringUtil.EMPTY_STRING))
			{
				strVolIss = strVolIss.concat(", ").concat(replaceIssueNullWithEmptyString(issue));
			}
			else
			{
				strVolIss = replaceIssueNullWithEmptyString(issue);
			}
		}

		if(strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING))
		{
			return strVolIss;
		}
		return null;
	}


	private String getSource(String sourceTitle,String sourceTitleAbbrev,String issueTitle,String publisherName)
	{
		String source = null;
		if(sourceTitle != null || sourceTitleAbbrev != null ||
		   issueTitle  != null || publisherName     != null )
		{
			if(sourceTitle != null)
			{
				source = sourceTitle;
			}
			else if(sourceTitleAbbrev != null)
			{
				source = sourceTitleAbbrev;
			}
			else if(issueTitle != null)
			{
				source = issueTitle;
			}
			else if(publisherName != null )
			{
				source = publisherName;
			}
		}
		return source;
	}

	private String getNoSource(String sourceTitle,String sourceTitleAbbrev,String issueTitle,String publisherName)
	{

		if(sourceTitle == null && sourceTitleAbbrev == null &&
		   issueTitle  == null && publisherName     == null )
		{
				return "NO_SO";
		}
		return null;
	}

	private ElementData getYear(String year,Perl5Util perl)
	{
		if(year != null)
		{
			Year yearObject = new Year(year.trim(),perl);
			return yearObject;
		}
		return null;
	}

	private ElementData getVolume(String volume,Perl5Util perl)
	{
		if(volume != null)
		{
			Volume volumeObject = new Volume(volume.trim(),perl);
			return volumeObject;
		}
		return null;
	}

	private ElementData getIssue(String issue,Perl5Util perl)
	{
		if(issue != null)
		{
			Issue issueObject = new Issue(issue.trim(),perl);
			return issueObject;
		}
		return null;
	}

	private EIDoc newEIDoc(DocID did, String dataFormat,ElementDataMap ht)
	{
		EIDoc eiDoc = null;
		if(dataFormat.equals(Citation.CITATION_FORMAT))
		{
			eiDoc = new EIDoc(did, ht, Citation.CITATION_FORMAT);
			eiDoc.exportLabels(false);
			eiDoc.setOutputKeys(CITATION_KEYS);
		}
		else if(dataFormat.equals(Abstract.ABSTRACT_FORMAT))
		{
			 eiDoc = new EIDoc(did,ht, Abstract.ABSTRACT_FORMAT);
			 eiDoc.exportLabels(true);
			 eiDoc.setOutputKeys(ABSTRACT_KEYS);
		}
		else if(dataFormat.equals(FullDoc.FULLDOC_FORMAT))
		{
			eiDoc = new EIDoc(did, ht, Detail.FULLDOC_FORMAT);
			eiDoc.exportLabels(true);
			eiDoc.setOutputKeys(DETAILED_KEYS);
		}
		else if(dataFormat.equalsIgnoreCase("RIS"))
		{
			eiDoc = new EIDoc(did, ht,RIS.RIS_FORMAT);
			eiDoc.exportLabels(false);
			eiDoc.setOutputKeys(RIS_KEYS);
		}
		else if(dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT))
		{
			eiDoc = new EIDoc(did, ht, Citation.XMLCITATION_FORMAT);
			eiDoc.exportLabels(false);
			eiDoc.setOutputKeys(XML_KEYS);
		}
		return eiDoc;
	}

	private ElementDataMap buildField(Key key,ElementData data, ElementDataMap ht) throws Exception
	{
		if(data !=null)
		{
			ht.put(key,data);
		}

		return ht;
	}

	private ElementDataMap buildField(Key key,String data, ElementDataMap ht) throws Exception
	{
			if(data !=null && (data.trim()).length()>0)
			{
				ht.put(key,new XMLWrapper(key,data.trim()));
			}

			return ht;
	}

	private ElementDataMap buildField(Key key,String[] data, ElementDataMap ht) throws Exception
	{
		if(data !=null && data.length>0)
		{
			ht.put(key,new XMLMultiWrapper(key,data));
		}

		return ht;

	}

	private String getPublisher(String name,String address) throws Exception
	{
		String outputString = null;
		StringBuffer addressBuffer = new StringBuffer();
		if(address != null)
		{
			if(address.indexOf(BdParser.IDDELIMITER)>-1)
			{
				BdAffiliations aff = new BdAffiliations(address);
				List aList = aff.getAffiliations();
				for(int i=0;i<aList.size();i++)
				{
					BdAffiliation bdaff = (BdAffiliation)aList.get(i);
					addressBuffer.append(bdaff.getDisplayValue());
				}
	        	address = addressBuffer.toString();
			}
			else if(address.indexOf(BdParser.AUDELIMITER)>-1)
			{
				address.replaceAll(BdParser.AUDELIMITER,", ");
			}
		}

		if(name != null && name.length()>0 && address != null && address.length()>0)
		{
			outputString = name + ", " +address;
		}
		else if(name != null && name.length()>0)
		{
			outputString = name;
		}
		else if(address != null && address.length()>0)
		{
			outputString = address;
		}

		return outputString;
	}

	private String getCitationTitle(String titleString) throws Exception
	{
		String  title = null;

		if(titleString!=null && titleString.length()>0)
		{
			BdCitationTitle bdTitle = new BdCitationTitle(titleString);
			List bdTitleList = bdTitle.getCitationTitle();
			if(bdTitleList != null)
			{
				for(int i=0;i<bdTitleList.size();i++)
				{
					BdCitationTitle titleObject = (BdCitationTitle)bdTitleList.get(i);
					if(titleObject.getTitle()!=null)
					{
						title = titleObject.getTitle();
						break;
					}
				}
			}
		}
		return title;
	}

	private String getDocumentType(String ct,String confCode) throws Exception
	{
		boolean confCodeFlag = false;
		if(confCode != null)
		{
			confCodeFlag = true;
		}
		if(ct != null)
		{
			return replaceDTNullWithEmptyString(BdDocumentType.getDocType(ct,confCodeFlag));
		}
		else
		{
			return null;
		}
	}

	private String  getStartPage(String page)
	{
		String strPage=StringUtil.EMPTY_STRING;

		if(page != null)
		{
			String[] pages = setElementData(page);
			if(pages[0] != null && pages[0].length()>0)
			{
				strPage = pages[0];
				if(strPage.indexOf("-")>0)
				{
					strPage = strPage.substring(0,strPage.indexOf("-")-1);
				}
			}
			else
			{
				if(pages[1] != null)
				{
					strPage = pages[1];
				}
			}
		}
		return strPage.trim();
	}

	private String  getEndPage(String page)
	{
		String strPage=StringUtil.EMPTY_STRING;

		if(page != null)
		{
			String[] pages = setElementData(page);

			if(pages[0] != null && pages[0].length()>0)
			{
				strPage = pages[0];
				if(strPage.indexOf("-")>-1)
				{
					strPage = strPage.substring(strPage.indexOf("-")+1);
				}
			}
			else
			{
				if(pages[2] != null)
				{
					strPage = pages[2];
				}
			}

		}
		return strPage.trim();
	}

	private PageRange  getPageRange(String page,String pageCount,String articleNumber,String issn,String eissn,Perl5Util perl) throws Exception
	{
		String strPage=null;

		if(page != null)
		{
			String[] pages = setElementData(page);

			if(pages[0] != null && pages[0].length()>0)
			{
				strPage = pages[0];
			}
			else
			{
				if(pages[1] != null && pages[2] != null)
				{
					strPage = ("p "+pages[1] +" - "+ pages[2]);
				}
				else if(pages[1] != null)
				{
					strPage = "p "+pages[1];
				}
				else if(pages[2] != null)
				{
					strPage = "p "+pages[2];
				}
			}

		}
		else if(pageCount != null)
		{
			strPage = getPageCount(pageCount);
		}
		else if(articleNumber != null) // Records with AR field Fix
		{
			if(issn==null)
			{
				issn = eissn;
			}

			if(issn != null && hasARFix(issn)) // Check ISSN for AR problem
			{
				strPage="p "+articleNumber;
			}
			else if(strPage != null)
			{
				strPage="p "+articleNumber;
			}
		}

		if(strPage != null )
		{
			 PageRange pageRange = new PageRange(strPage,perl);
			 return pageRange;
		 }

		 return null;
	}

	private String getPageCount(String pageCountString)
	{
		if(pageCountString != null)
		{
			BdPageCount pageCount = new BdPageCount(pageCountString);
			return pageCount.getDisplayValue();
		}
		return null;
	}

	private ElementData  getIssn(String issn,String eissn) throws Exception
	{
		if(issn !=null)
		{
			return new ISSN(issn);
		}
		else if(eissn !=null)
		{
			return new ISSN(eissn);
		}
		return null;
	}


	private String  getIsbn(String isbn, int type) throws Exception
	{
		BdIsbn bdisbn = new BdIsbn(isbn);
		String isbnString = null;
		if(type==10)
		{
			isbnString = bdisbn.getISBN10();
		}
		else if(type==13)
		{
			isbnString = bdisbn.getISBN13();
		}

		return isbnString;
	}


	private String[] getTranslatedCitationTitle(String titleString) throws Exception
	{
		List titleList = new ArrayList();

		if(titleString!=null && titleString.length()>0)
		{
			BdCitationTitle bdTitle = new BdCitationTitle(titleString);
			List bdTitleList = bdTitle.getTranslatedCitationTitle();
			if(bdTitleList != null)
			{
				for(int i=0;i<bdTitleList.size();i++)
				{
					BdCitationTitle titleObject = (BdCitationTitle)bdTitleList.get(i);
					if(titleObject.getTitle()!=null)
					{
						titleList.add(titleObject.getTitle());
					}
				}
			}

		}

		return (String[])titleList.toArray(new String[titleList.size()]);
	}


	private String getConferenceLocation(String confLocation) throws Exception
	{
		if(confLocation != null)
		{
			BdConfLocations bdc = new BdConfLocations(confLocation);
			StringBuffer result = new StringBuffer();
			String[] cfl = bdc.getDisplayValue().split(",");
			for(int i = 0; i < cfl.length ; i++)
			{
				String cf = cfl[i].trim();
				result.append(cf.substring(0,1).toUpperCase());
				if(cf.length() > 0)
				{
					result.append(cf.substring(1));
					if(i < cfl.length-1)
					{
						result.append(", ");
					}
				}
			}
			return result.toString();
		}
		return null;
	}

	private String getCorrespondingAuAff(String coAuAff) throws Exception
	{
		if(coAuAff != null)
		{
			BdCorrespAffiliations aff = new BdCorrespAffiliations(coAuAff);
			ArrayList l =(ArrayList) aff.getAffiliations();

			if (l == null || l.size() == 0)
			{
			   	         return null;
			}
			else
			{
				StringBuffer buf = new StringBuffer();
				for (int i = 0; i < l.size(); i++)
				{
					BdCorrespAffiliation a  =(BdCorrespAffiliation) l.get(i);
					buf.append(a.getDisplayValue()).append(" ");
				}
				return buf.toString();
			}
		}

		return null;
	}

	private Contributors getAuthors(Key key,
		        					String authorString,
		        					String authorString1, String dataFormat)

	throws Exception
	{
		StringBuffer auString = new StringBuffer();

		if(authorString != null)
		{
			auString.append(authorString);
		}
		if(authorString1 != null)
		{
			auString.append(authorString1);
		}

		if(auString.length()> 0)
		{
		    List authorNames = new ArrayList();
			BdAuthors authors = new BdAuthors(auString.toString());
			List authorList = authors.getAuthors();
			for(int i= 0;i<authorList.size();i++)
			{

				BdAuthor author = (BdAuthor)authorList.get(i);
				if(dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT) ||
						dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT))
				{
					authorNames.add(new Contributor(key,
									author.getDisplayName()));
				}
				else
				{
					authorNames.add(new Contributor(key,
									author.getDisplayName(),
									author.getAffIdList()));
				}
			}
		    return (new Contributors(Keys.AUTHORS,authorNames));
		}
	    return null;
	}

	private ElementData getEditors(Key key,String authorString,String editorsString, String confEditor) throws Exception
	{
		BdEditors editors = new BdEditors(editorsString);

		List editorList = editors.getEditors();
		List editorNames = new ArrayList();
		if(authorString == null && editorsString != null)
		{
			for(int i= 0;i<editorList.size();i++)
			{
				BdAuthor author = (BdAuthor)editorList.get(i);
				editorNames.add(new Contributor(key,author.getDisplayName()));
			}
			return new Contributors(Keys.EDITORS, editorNames);
		}
		else if(authorString == null && editorsString == null && confEditor != null)
		{
			editors = new BdEditors(confEditor);
			editorList = editors.getEditors();

			for(int i= 0;i<editorList.size();i++)
			{
				BdAuthor author = (BdAuthor)editorList.get(i);
				editorNames.add(new Contributor(key,author.getDisplayName()));
			}
			return new Contributors(key, editorNames);

		}
		else
		{
			return null;
		}
	}

	private ElementData getCorAuthors(Key key,String authorString,String emailString) throws Exception
	{
		BdEditors editors = new BdEditors(authorString);
		List editorList = editors.getEditors();
		List editorNames = new ArrayList();

		for(int i= 0;i<editorList.size();i++)
		{
			BdAuthor author = (BdAuthor)editorList.get(i);
			Contributor persons = new Contributor(key,author.getDisplayName());

			if(emailString!= null)
			{
				if(emailString.indexOf("email"+BdParser.IDDELIMITER)>-1 )
				{
					emailString = emailString.substring(emailString.indexOf(BdParser.IDDELIMITER)+1);
				}

				persons.setEmail(emailString);
            }
			editorNames.add(persons);
		}
		return new Contributors(key, editorNames);

	}

	private ElementData getClassification(Key key,String classCode, Database database) throws Exception
	{
		if(classCode != null)
		{
			return new Classifications(key, setElementData(classCode), database);
		}
		else
		{
			return null;
		}
	}

	private Affiliations getAuthorsAffiliation(Key key,
			   								   String affString,
			   								   String affString1,
			   								   String dataFormat) throws Exception
    {
	    StringBuffer affBuffer = new StringBuffer();

	    if(affString != null)
	    {
	        affBuffer.append(affString);
	    }
	    if(affString1 != null)
	    {
	        affBuffer.append(affString1);
	    }
	    if(affBuffer.length()>0)
	    {
	        List affList = new ArrayList();
	        BdAffiliations aff = new BdAffiliations(affBuffer.toString());
	        List aList = aff.getAffiliations();
	        for(int i=0;i<aList.size();i++)
	        {
	            BdAffiliation bdaff = (BdAffiliation)aList.get(i);

	            if(i == 0 && dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT))
	            {
	            	affList.add(new Affiliation(key,
	            			bdaff.getDisplayValue()));
	            	return (new Affiliations(Keys.RIS_AD,affList));
				}
	            else if(dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT)
	            		&& i==0)
	            {
	            	affList.add(new Affiliation(key,bdaff.getDisplayValue()));
	            	return (new Affiliations(Keys.AUTHOR_AFFS,affList));
	            }
				else
				{
	            	affList.add(new Affiliation(key,
	                    bdaff.getDisplayValue(),
	                    bdaff.getIdDislpayValue()));

				}

	        }
	        return (new Affiliations(Keys.AUTHOR_AFFS,affList));

	    }
	    return null;
    }



     private String hasAbstract(ResultSet rs) throws Exception
	 {
		String abs = null;
		Clob clob = rs.getClob("AB");
		if(clob != null)
		{
			abs = StringUtil.getStringFromClob(clob);
		}

		 if(abs == null ||
			abs.length() < 100)
		 {
			 return null;
		 }
		 else
		 {
			return abs;
		}
    }

   /* This method builds the IN String
    * from list of docId objects.
    * The select query will get the result set in a reverse way
    * So in order to get in correct order we are doing a reverse
    * example of return String--(23,22,1,12...so on);
    * @param listOfDocIDs
    * @return String
    */
    private String buildINString(List listOfDocIDs) throws Exception
    {
        StringBuffer sQuery=new StringBuffer();
        sQuery.append("(");
        for(int k=listOfDocIDs.size();k>0;k--)
        {
            DocID doc = (DocID)listOfDocIDs.get(k-1);
            String docID=doc.getDocID();
                if((k-1)==0)
                {
                    sQuery.append("'"+docID+"'");
                }
                else
                {
                    sQuery.append("'"+docID+"'").append(",");
                }
         }//end of for
         sQuery.append(")");
         return sQuery.toString();
    }

    /*TS if volume  is null and str is not null print n  */
    private String  replaceVolumeNullWithEmptyString (String str) throws Exception
    {
        if(str==null || str.equals("QQ"))
        {
            str="";
        }

        if( !str.equals("") && ((str.indexOf("v",0) < 0 ) && (str.indexOf("V",0) < 0 )))
        {
            str = "v ".concat(str);
        }
        return str;
    }
    /*TS if number  is null and str is not null print n   */
    private String  replaceIssueNullWithEmptyString (String str)
    {
        if(str==null || str.equals("QQ"))
        {
            str="";
        }

        if( !str.equals("") && ((str.indexOf("n") < 0 ) && (str.indexOf("N")< 0 )))
        {
            str = "n ".concat(str);
        }
        return str;
    }

    private String getAbstract(ResultSet rs) throws Exception
    {
		String abs = null;
		Clob clob = rs.getClob("ABSTRACTDATA");
		if(clob != null)
		{
			abs = StringUtil.getStringFromClob(clob);
		}

        return abs;

    }

    private static boolean hasARFix(String strISSN) //Checks ISSNs with AR field problems
    {
		 if (issnARFix.containsKey(strISSN.replaceAll("-","")))
		 {
			 return true;
		 }
		 else
		 {
			return false;
		 }
	}


    // jam XML document mapping, conversion to TY values
    // for RIS format - only called from loadRIS
    private String  replaceTYwithRIScode(String str)
    {
        if(str==null || str.equals("QQ"))
        {
            str=StringUtil.EMPTY_STRING;
        }

        if(!str.equals(StringUtil.EMPTY_STRING))
        {

            if (str.equals("Journal article (JA)")){str = "JOUR";}
            else if (str.equals("Conference article (CA)")){str = "CONF";}
            else if (str.equals("Conference proceeding (CP)")){str = "CONF";}
            else if (str.equals("Monograph chapter (MC)")){str = "CHAP";}
            else if (str.equals("MMonograph review (MR)")){str = "BOOK";}
            else if (str.equals("Report chapter (RC)")){str = "RPRT";}
            else if (str.equals("Report review (RR)")){str = "RPRT";}
            else if (str.equals("Dissertation (DS)")){str = "THES";}
            else if (str.equals("Unpublished paper (UP)")){str = "UNPB";}
        }
        else
        {
            str="JOUR";
        }
        return str;
    }

    // TS XML document mapping, conversion to dt values 02/10/03

    private String  replaceDTNullWithEmptyString(String str)
    {
        if(str==null || str.equals("QQ"))
        {
            str=StringUtil.EMPTY_STRING;
        }

        if( !str.equals(StringUtil.EMPTY_STRING))
        {
            if (str.equals("JA")){str = "Journal article (JA)";}
            else if (str.equals("CA")){str = "Conference article (CA)";}
            else if (str.equals("CP")){str = "Conference proceeding (CP)";}
            else if (str.equals("MC")){str = "Monograph chapter (MC)";}
            else if (str.equals("MR")){str = "Monograph review (MR)";}
            else if (str.equals("RC")){str = "Report chapter (RC)";}
            else if (str.equals("RR")){str = "Report review (RR)";}
            else if (str.equals("DS")){str = "Dissertation (DS)";}
            else if (str.equals("UP")){str = "Unpublished paper (UP)";}
        }
        return str;
    }

    private Hashtable getDocIDTable(List listOfDocIDs)
    {
        Hashtable h = new Hashtable();

        for(int i=0; i<listOfDocIDs.size(); ++i)
        {
            DocID d = (DocID)listOfDocIDs.get(i);
            h.put(d.getDocID(), d);
        }


        return h;
    }


    public String setSponsorData(String elementVal)
	{
		StringBuffer buf = new StringBuffer();
		String [] array = null;
		String result = null;
		if(elementVal!=null && elementVal.trim().length()>0)
		{
			if(elementVal.indexOf(BdParser.AUDELIMITER)>-1)
			{
				array = elementVal.split(BdParser.AUDELIMITER,-1);
			}
			else
			{
				array = new String[1];
				array[0]=elementVal;
			}
		}
		if(array != null)
		{
			for (int i=0; i <array.length; i++)
			{
				buf.append(array[i]).append("; ");
			}
			result = buf.toString();
			if(result.length() > 2)
			{
				result = result.substring(0,result.length() - 2);
			}
		}
		return result;
	}

	public String[] setElementData(String elementVal)
	{
		String[] array = null;
		if(elementVal!=null && elementVal.trim().length()>0)
		{
			if(elementVal.indexOf(BdParser.AUDELIMITER)>-1)
			{
				array = elementVal.split(BdParser.AUDELIMITER,-1);
			}
			else
			{
				array = new String[1];
				array[0]=elementVal;
			}
		}
		return array;
	}

	public ElementData getTreatments(String treatments, Database database)
	{
	    ArrayList result = new ArrayList();
	    int len = 0;
	    int trlen = 0;
	    String ch = "";
	    if(treatments!=null)
	    {
			trlen = treatments.length();
			if (trlen >= 0 && trlen < 10 && trlen > 0)
			{
				for (int i = 0; i< trlen ; i++)
				{
					if (len < trlen)
					{
						ch = treatments.substring(len,len+1);
					}
					else
					{
						ch = treatments.substring(len);
					}

					if (ch != null && !ch.equals("") &&
						!ch.equals(BdParser.AUDELIMITER) &&
						!ch.equals("QQ")&&
						!ch.equals(";"))
					{
					  result.add(ch);
					}
					len ++;
					ch = "";
				}
			}
			return new Treatments((String[])result.toArray(new String[result.size()]),database);
		}

        return null;
	}

	public void formatRIS(ElementDataMap map, String dataFormat, Key ORIGINAL_KEY, Key NEW_KEY)
	{
		if(dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT))
		{
			if(map!= null)
			{
				ElementData ed = map.get(ORIGINAL_KEY);
				if(ed != null)
				{
					ed.setKey(NEW_KEY);
					map.put(NEW_KEY, ed);
				}
			}
		}
	}

	public void formatRISDocType(ElementDataMap map, String dataFormat, Key ORIGINAL_KEY, Key NEW_KEY)
	{
		if(dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT))
		{
			ElementData ed = map.get(ORIGINAL_KEY);
			if(ed != null)
			{
				String[] elementDataArray = ed.getElementData();

				for(int i = 0; i < elementDataArray.length; i++)
				{
					String strDocType = StringUtil.replaceNullWithEmptyString(elementDataArray[i]);
					String risDocType = replaceTYwithRIScode(strDocType);
					elementDataArray[i] = risDocType;
				}

				ed.setKey(NEW_KEY);
				ed.setElementData(elementDataArray);
				map.put(NEW_KEY, ed);
			}
		}
	}

	public void formatRISSerialTitle(ElementDataMap map, String dataFormat, Key ORIGINAL_KEY, String docType)
	{
		if(dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT) && map != null)
		{
			ElementData ed = map.get(ORIGINAL_KEY);

			if(ed != null)
			{
				if(docType.equals("Journal article (JA)"))
				{
					ed.setKey(Keys.RIS_JO);
					map.put(Keys.RIS_JO, ed);
				}
				else
				{
					ed.setKey(Keys.RIS_T3);
					map.put(Keys.RIS_T3, ed);
				}
			}

		}
	}

}





