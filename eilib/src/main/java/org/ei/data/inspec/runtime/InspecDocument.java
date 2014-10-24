package org.ei.data.inspec.runtime;

import java.io.IOException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.domain.DocID;
import org.ei.domain.LocalHoldingLinker;
import org.ei.util.StringUtil;

/**  This class is basically responsible for
  *  Constructing the basic EIDocs.The same
  *  class presently serves for all formats.
  *
  */
public class InspecDocument
{

	private StringUtil sUtil = new StringUtil();
	private String[] numberPatterns = {"/[1-9][0-9]*/"};
	private String[] urlPatterns = {"s/\\s+/ /g",
									"s/\\s/\\+/g"};
	private String[] regExPatterns = {"s/#/\\#/g" };

	private String database;
	private DocID docid;
	private String handle;
	private String documentType;
	private String accessionNumber;
	private String articleTitle;
	private String authors;
	private String editors;
	private String firstEditorAffiliation;
	private String firstAuthorAffiliation;
    private String serialTitle;
    private String abbreviatedSerialTitle;
    private String voliss;
    private String publicationDate;

    private String pages;
    private String pages1;
    private String pages2;
    private String language;
    private String ISSN;
    private String coden;

	private String conferenceTitle;
	private String conferenceDate;
	private String conferenceLocation;
	private String sponsorOrg;

	private String publisherName;
	private String publisherCountry;
	private String placeOfPublisher;

	private String translationSerialTitle;
	private String translationAbbrSerialTitle;
	private String translationPublicationDate;
	private String translationPages;
	private String translationISSN;
	private String translationCODEN;
	private String translationCountryOfPub;
	private String translationVolumeIssue;

	private String materialIdentityNum;
	private String partNumber;
	private String reportNumber;

	private String ISBN;
	private String issuingOrg;
	private String abstractText;
	private String filingDate;
	private String url;
	private String um;

	private String assignee;
	private String applicationNumber;
	private String patentNumber;
	private String numOfReferences;

	private String controlledTerms;
	private String controlledTerm;

	private String classificationCodes;

	private String unControlledTerms;
	private String unControlledTerm;

	private String highLevelPubTitle;
	private String numericalDataIndexing;
	private String chemicalIndexing;
	private String astronomicalObjectIndexing;
	private String treatment;
	private String copyright;
	private String countryOfAppl;

    private static final String eiRootDocumentTag="EI-DOCUMENT";

    private static final String eiHitIndexTag="HIT-INDEX";

    private static final String eiDocidTag="DOC-ID";
	private static final String eiHandleTag="HANDLE";
	private static final String eiDatabaseTag="DATABASE";

	private static final String eiAuthorsTag="AUTHORS";
	private static final String eiAuthorTag="AUTHOR";//just a tag

	private static final String eiEditorsTag="EDITORS";
	private static final String eiEditorTag="EDITOR";//just a tag

	private static final String eiFirstAuthorAffiliationTag="FIRST-AUTHOR-AFFILIATION";//just a tag
	private static final String eiArticleTitleTag="ARTICLE-TITLE";
	private static final String eiAccessionNumberTag="ACCESSION-NUMBER";
	private static final String eiFirstEditorAffiliationTag= "FIRST-EDITOR-AFFILIATION" ;//just a tag

    private static final String eiSerialTitleTag="SERIAL-TITLE";
	private static final String eiAbbreviatedSerialTitleTag="ABBREVIATED-SERIAL-TITLE";

	private static final String eiVolissTag="VOLISS";

	private static final String eiPublicationDateTag="PUBLICATION-DATE";
    private static final String eiPagesTag="PAGES";
    private static final String eiPages1Tag="PAGES1";
    private static final String eiPages2Tag="PAGES2";
	private static final String eiLanguageTag="LANGUAGE";
	private static final String eiISSNTag="ISSN";
	private static final String eiCodenTag="CODEN";
	private static final String eiISBNTag="ISBN";

	private static final String eiConferenceNameTag="CONFERENCE-TITLE";
	private static final String eiConferenceDateTag="CONFERENCE-DATE";
	private static final String eiConferenceLocationTag="CONFERENCE-LOCATION";
	private static final String eiSponsorOrgTag="SPONSOR-ORG";//just a tag
	private static final String eiPublisherNameTag="PUBLISHER-NAME";
	private static final String eiPlaceOfPublisher="PLACE-OF-PUBLISHER";
	private static final String eiPublisherCountryTag="PUBLISHER-COUNTRY";
	private static final String eiNumberOfReferencesTag="NUMBER-OF-REFERENCES";

	private static final String eiTranslationSerialTitleTag="TRANSLATION-SERIAL-TITLE";
	private static final String eiTranslationAbbrSerialTitleTag="TRANSLATION-ABBREVIATED-SERIAL-TITLE";
	private static final String eiTranslationVolumeIssueTag="TRANSLATION-VOLUME-ISSUE";

	private static final String eiTranslationPublicationDateTag="TRANSLATION-PUBLICATION-DATE";
	private static final String eiTranslationPagesTag="TRANSLATION-PAGES";
	private static final String eiTranslationISSNTag="TRANSLATION-ISSN";
	private static final String eiTranslationCODENTag="TRANSLATION-CODEN";
	private static final String eiTranslationCountryOfPubTag="TRANSLATION-COUNTRY-OF-PUB";
	private static final String eiMaterialIdentityNumTag="MATERIAL-IDENTITY-NUM";

	private static final String eiPartNumberTag="PART-NUMBER";
	private static final String eiReportNumberTag="REPORT-NUMBER";
	private static final String eiIssuingOrgTag="ISSUING-ORG";
	private static final String eiAbstractTextTag="ABSTRACT";
	private static final String eiFilingDateTag="FILING-DATE";
	private static final String eiAssigneeTag="ASSIGNEE";

	private static final String eiApplicationNumberTag="APPLICATION-NUMBER";
	private static final String eiPatentNumberTag="PATENT-NUMBER";
	private static final String eiHighLevelPubTitleTag="HIGH-LEVEL-PUB-TITLE";
	private static final String eiNumericalDataIndexingTag="NUMERICAL-DATA-INDEXING";
	private static final String eiChemicalIndexingTag="CHEMICAL-INDEXING";
	private static final String eiAstronomicalObjectIndexingTag="ASTRONOMICAL-OBJECT-INDEXING";
	private static final String eiCopyrightTag="COPY-RIGHT";

	private static final String eiTreatmentTag="TREATMENT";
	private static final String eiDocumentTypeTag="DOCUMENT-TYPE";

	private static final String eiClassificationCodesTag="CLASSIFICATION-CODES";//just a tag
	private static final String eiClassificationCodeTag="CLASSIFICATION-CODE";//just a tag

	private static final String eiControlledTermsTag="CONTROLLED-TERMS";
	private static final String eiControlledTermTag="CONTROLLED-TERM";

	private static final String eiUnControlledTermsTag="UNCONTROLLED-TERMS";
	private static final String eiUnControlledTermTag="UNCONTROLLED-TERM";

	private static final String eiDisciplinesTag="DISCIPLINES";
	private static final String eiDisciplineTag="DISCIPLINE";

	private static final String eiTranslationIssueTag="TRANSLATION-ISSUE";
	private static final String eiTranslationVolumeTag="TRANSLATION-VOLUME";

	private static final String eiVolumeTag="VOLUME";
	private static final String eiIssueTag="ISSUE";
	private static final String eiCountryOfApplTag="COUNTRY-OF-APPLICATION";

	private static final String eiURLTag="URL";
	private static final String eiUMTag="UM";

	private Perl5Util perl = new Perl5Util();



    public InspecDocument newInstance(DocID aDocid)
	{
		return new InspecDocument(aDocid);
	}

	public InspecDocument()
	{

	}

	public InspecDocument(DocID aDocid)
	{
		this.docid = aDocid;
	}



   /** This method is basically used for initialization.
    *  Hashtable is iterated and corresponding set methods
    *  are called.
    *  @param Hashtable
	*/
	public void load(Hashtable ht)
	{
	    this.authors=validate(ht.get("AUTHORS"));
		this.articleTitle=validate(ht.get("ARTICLE-TITLE"));
		this.accessionNumber=validate(ht.get("ACCESSION-NUMBER"));
		this.editors=validate(ht.get("EDITORS"));
		this.serialTitle=validate(ht.get("SERIAL-TITLE"));
		this.abbreviatedSerialTitle=validate(ht.get("ABBREVIATED-SERIAL-TITLE"));
		this.voliss=validate(ht.get("VOLISS"));
		this.publicationDate=validate(ht.get("PUBLICATION-DATE"));
		this.pages=validate(ht.get("PAGES"));
		this.pages1=validate(ht.get("PAGES1"));
		this.pages2=validate(ht.get("PAGES2"));
		this.language=validate(ht.get("LANGUAGE"));
		this.ISSN=validate(ht.get("ISSN"));
		this.coden=validate(ht.get("CODEN"));
		this.ISBN=validate(ht.get("ISBN"));
		this.treatment=validate(ht.get("TREATMENT"));
		this.documentType=validate(ht.get("DOCUMENT-TYPE"));
		this.abstractText=validate(ht.get("ABSTRACT"));
		this.numOfReferences=validate(ht.get("NUMBER-OF-REFERENCES"));
		this.database=validate(ht.get("DATABASE"));
		this.docid=((DocID)ht.get("DOC-ID"));
		this.handle=validate(ht.get("HANDLE"));
		this.publisherName=validate(ht.get("PUBLISHER-NAME"));
		this.publisherCountry=validate(ht.get("PUBLISHER-COUNTRY"));
		this.placeOfPublisher=validate(ht.get("PLACE-OF-PUBLISHER"));
		this.conferenceTitle=validate(ht.get("CONFERENCE-TITLE"));
		this.conferenceDate=validate(ht.get("CONFERENCE-DATE"));
		this.conferenceLocation=validate(ht.get("CONFERENCE-LOCATION"));
		this.translationSerialTitle=validate(ht.get("TRANSLATION-SERIAL-TITLE"));
		this.translationAbbrSerialTitle=validate(ht.get("TRANSLATION-ABBREVIATED-SERIAL-TITLE"));
		this.translationVolumeIssue=validate(ht.get("TRANSLATION-VOLUME-ISSUE"));
		this.translationPublicationDate=validate(ht.get("TRANSLATION-PUBLICATION-DATE"));
		this.translationPages=validate(ht.get("TRANSLATION-PAGES"));
		this.translationISSN=validate(ht.get("TRANSLATION-ISSN"));
		this.translationCODEN=validate(ht.get("TRANSLATION-CODEN"));
		this.translationCountryOfPub=validate(ht.get("TRANSLATION-COUNTRY-OF-PUB"));
		this.materialIdentityNum=validate(ht.get("MATERIAL-IDENTITY-NUM"));
		this.partNumber=validate(ht.get("PART-NUMBER"));
		this.reportNumber=validate(ht.get("REPORT-NUMBER"));
		this.ISBN=validate(ht.get("ISBN"));
		this.issuingOrg=validate(ht.get("ISSUING-ORG"));
		this.filingDate=validate(ht.get("FILING-DATE"));
		this.assignee=validate(ht.get("ASSIGNEE"));
		this.applicationNumber=validate(ht.get("APPLICATION-NUMBER"));
		this.patentNumber=validate(ht.get("PATENT-NUMBER"));
		this.highLevelPubTitle=validate(ht.get("HIGH-LEVEL-PUB-TITLE"));
		this.numericalDataIndexing=validate(ht.get("NUMERICAL-DATA-INDEXING"));
		this.chemicalIndexing=validate(ht.get("CHEMICAL-INDEXING"));
		this.astronomicalObjectIndexing=validate(ht.get("ASTRONOMICAL-OBJECT-INDEXING"));
		this.copyright=validate(ht.get("COPY-RIGHT"));
		this.controlledTerms=validate(ht.get("CONTROLLED-TERMS"));
		this.classificationCodes=validate(ht.get("CLASSIFICATION-CODES"));
	    this.unControlledTerms=validate(ht.get("UNCONTROLLED-TERMS"));
	    this.firstEditorAffiliation=validate(ht.get("FIRST-EDITOR-AFFILIATION"));
	    this.firstAuthorAffiliation=validate(ht.get("FIRST-AUTHOR-AFFILIATION"));
	    this.sponsorOrg=validate(ht.get("SPONSOR-ORG"));
	    this.countryOfAppl=validate(ht.get("COUNTRY-OF-APPLICATION"));
	    this.url=validate(ht.get("URL"));
	    this.um=validate(ht.get("UM"));

	}//end of load

    /*  This method basically for checks for null
    *   If the object is null then returns ""
    *   @ param Object
    *   @ return String
    */
	private String validate(Object obj)
	{
		String str=(String)obj;
		 if(str==null)
		 {
			 str="";
		 }
		 return str;
	}

//   Accessor and Mutators methods

	public String getFirstAuthor()
	{
		if(authors != null)
		{
			StringTokenizer st = new StringTokenizer(authors,";",false);
			if (st.countTokens() > 0)
			{
				return st.nextToken();
			}
		}

		return null;
	}

	public String getFirstAuthorLN()
	{
		String f = getFirstAuthor();
		if(f != null)
		{
			StringTokenizer st = new StringTokenizer(f,",",false);
			if (st.countTokens() > 0)
			{
				return st.nextToken();
			}
		}
		return null;
	}

	public String getFirstAuthorFN()
	{
		String f = getFirstAuthor();
		if(f != null)
		{
			StringTokenizer st = new StringTokenizer(f,",",false);
			if (st.countTokens() > 1)
			{
				String tmpStr = st.nextToken();
				return st.nextToken();
			}
		}
		return null;
	}

	public String getVolumeNo()
	{
	    StringBuffer retStr=new StringBuffer();
		String tmpNum = getVolume();

		if(tmpNum != null)
		{
			for(int x=0; x<numberPatterns.length; ++x)
			{
				String pattern = numberPatterns[x];
				if(perl.match(pattern, tmpNum))
				{
					MatchResult mResult = perl.getMatch();
					retStr.append(mResult.toString());
					break;
				}
			}
		}

	    return retStr.toString();
	}

	public String getIssueNo()
	{
		StringBuffer retStr=new StringBuffer();
		String tmpNum = getIssue();
		if(tmpNum != null)
		{
			for(int x=0; x<numberPatterns.length; ++x)
			{
				String pattern = numberPatterns[x];
				if(perl.match(pattern, tmpNum))
				{
					MatchResult mResult = perl.getMatch();
					retStr.append(mResult.toString());
					break;
				}
			}
		}

		return retStr.toString();

	}

	public String getYear()
	{
		StringBuffer retStr=new StringBuffer();
		if(getPublicationDate() != null)
		{
			if(perl.match("/\\d\\d\\d\\d/", getPublicationDate()))
			{
				MatchResult mResult = perl.getMatch();
				retStr.append(mResult.toString());
			}
		}
		return retStr.toString();
	}

	public String getCountryOfAppl()
	{
		return countryOfAppl;
	}

	public String getFirstEditorAffiliation()
	{
		return firstEditorAffiliation;
	}

	public String getFirstAuthorAffiliation()
	{
		return firstAuthorAffiliation;
	}

     public String getArticleTitle()
	 {
	 	return articleTitle;
	 }

	 public String getAccessionNumber()
	 {
		return accessionNumber;
	 }

	 public String getAuthors()
	 {
	 	return getAuthors(authors);
	 }

	 public String getAuthorForLocalHolding()
	 {
		return getAuthorForLocalHolding(authors);
	 }

	 public String getEditors()
	 {
		 return getEditors(editors);
	 }

	public String getSerialTitle()
	{
		return serialTitle;
	}

	public String getAbbreviatedSerialTitle()
	{
		return abbreviatedSerialTitle;
	}

	public String getVoliss()
	{
		return getVolumeIssue(voliss);
	}

    public String getISSN()
	{
		if(ISSN != null)
		{
			if (ISSN.length() == 9)
			{
				return ISSN;
			}
			else if(ISSN.length() == 8)
			{
				return ISSN.substring(0,4)+"-"+ISSN.substring(4,8);
			}
		}

		return null;
	}

	public String getISSN2()
	{
		if(ISSN != null)
		{
			if (ISSN.length() == 9)
			{
				return ISSN.substring(0,4)+ISSN.substring(5,9);
			}
			else if(ISSN.length() == 8)
			{
				return ISSN;
			}
		}

		return null;
	}

	public String getISBN()
	{
		return ISBN;
	}

	public String getPages()
	{
		return pages;
	}

	public String getPages1()
	{
		return pages1;
	}

	public String getPages2()
	{
		return pages2;
	}

	public String getPublicationDate()
	{
		return publicationDate;
	}

	public String getLanguage()
	{
		return language;
	}

	public String getCoden()
	{
		return coden;
    }

    public String getTreatment()
	{
		return treatment;
	}

	public String getDocumentType()
	{
		return documentType;
	}

	public String getAbstractText()
	{
		return abstractText;
	}

	public String getNumberOfReferences()
	{
		return numOfReferences;
	}

	public String getDatabase()
	{
		return database;
	}

	public DocID getDocID()
	{
		return docid;
	}

	public String getHandle()
	{
		return handle;
	}


	public String getPublisherName()
	{
		return publisherName;
	}

	public String getPublisherCountry()
	{
		return publisherCountry;
	}

	public String getPlaceOfPublisher()
	{
		return placeOfPublisher;
	}

	public String getConferenceDate()
	{
		return conferenceDate;
	}

	public String getConferenceTitle()
	{
		return conferenceTitle;
	}

	public String getConferenceLocation()
	{
		return conferenceLocation;
	}

	public String getControlledTerms()
	{
		return getControlledTerms(controlledTerms);
	}

	public String getClassificationCodes()
	{
		return getClassificationCodes(classificationCodes);
	}

	public String getUnControlledTerms()
	{
		return getUnControlledTerms(unControlledTerms);
	}

	public String getMaterialIdentityNum()
	{
		return materialIdentityNum;
	}

	public String getIssuingOrg()
	{
		return issuingOrg;
	}

	public String getCopyright()
	{
		return copyright;
	}

	public String getTranslationCODEN()
	{
		return translationCODEN;
	}

	public String getSponsorOrg()
	{
		return sponsorOrg;
	}

	public String getAstronomicalObjectIndexing()
	{
		return astronomicalObjectIndexing;
	}

	public String getTranslationCountryOfPub()
	{
		return translationCountryOfPub;
	}

	public String getHighLevelPubTitle()
	{
		return highLevelPubTitle;
	}

	public String getTranslationPages()
	{
		return translationPages;
	}

	public String getFilingDate()
	{
		return filingDate;
	}

	public String getNumericalDataIndexing()
	{
		return numericalDataIndexing;
	}

	public String getPatentNumber()
	{
		return patentNumber;
	}

	public String getPartNumber()
	{
		return getPartNumber(partNumber);
	}

	public String getTranslationISSN()
	{
		return translationISSN;
	}

	public String getReportNumber()
	{
		return reportNumber;
	}

	public String getChemicalIndexing()
	{
		return chemicalIndexing;
	}

	public String getTranslationSerialTitle()
	{
		return translationSerialTitle;
	}

	public String getTranslationVolumeIssue()
	{
		return getTranslationVolumeIssue(translationVolumeIssue);
	}

	public String getTranslationAbbrSerialTitle()
	{
		return translationAbbrSerialTitle;
	}

	public String getAssignee()
	{
		return assignee;
	}

	public String getTranslationPublicationDate()
	{
		return translationPublicationDate;
	}

	public String getApplicationNumber()
	{
		return applicationNumber;
	}

	public String getDisciplines(){
		return getDiciplines(classificationCodes);
	}

	public String getURL(){
		return url;
	}

	public String getUM(){
		return um;
	}


	public String getDocIDXml(){
		StringBuffer sb=new StringBuffer();
		sb.append("<"+eiHitIndexTag+"><![CDATA["+docid.getHitIndex()+"]]></"+eiHitIndexTag+">");
		sb.append("<"+eiDocidTag+"><![CDATA["+docid.getDocID()+"]]></"+eiDocidTag+">");
		sb.append("<"+eiDatabaseTag+"><![CDATA["+docid.getDatabase().getID()+"]]></"+eiDatabaseTag+">");
		return sb.toString();
	}

	 /**
	   * this method compares the two objects based on the doc id
	   * @ return : true , if the two objects are equal
	   *          : false , if the two objects are not equal.
	   */
	public boolean equals(Object object) throws ClassCastException
	{
	    if(object == null)
	    {
		    return false;
	    }
	    else
	    {
		    InspecDocument docObj= (InspecDocument)object;
	        if(((DocID)this.getDocID()) .equals( ((DocID)docObj.getDocID())))
		  	    return true;
		    else
		        return false;
		}
	}

	  /**
		* Compares the Object with the specified object for order
		* @exception ClassCastException
		* @return 0 if the objects match
		*/
	public int compareTo(Object object) throws ClassCastException{
		if(object == null)
		{
			return -1;
		}
		else
		{
			InspecDocument docObj=(InspecDocument)object;
			String sObjString= docObj.getDocID().toString();
			String sThisString= this.getDocID().toString();
			return sThisString.compareTo(sObjString);
		}
	}

  /** This method returns the xml formatted string of the Document
	* @return : String
	*/
	public void toXML(Writer out)throws IOException
	{
		out.write("<"+eiRootDocumentTag+">");

		out.write(includeXML(getAuthors(),eiAuthorsTag));

		out.write(includeCDataXML(getArticleTitle(),eiArticleTitleTag));
		out.write(includeXML(getEditors(),eiEditorsTag));

		out.write(includeCDataXML(getSerialTitle(),eiSerialTitleTag));
		out.write(includeCDataXML(getAbbreviatedSerialTitle(),eiAbbreviatedSerialTitleTag));
		out.write(includeXML(getVoliss(),eiVolissTag));
		out.write(includeCDataXML(getISSN(),eiISSNTag));
		out.write(includeCDataXML(getPublicationDate(),eiPublicationDateTag));

		out.write(includeCDataXML(getISBN(),eiISBNTag));

		out.write(includeXML(getPages(),eiPagesTag));
		out.write(includeXML(getPages1(),eiPages1Tag));
		out.write(includeXML(getPages2(),eiPages2Tag));
		out.write(includeCDataXML(getCoden(),eiCodenTag));
		out.write(includeCDataXML(getLanguage(),eiLanguageTag));

		out.write(includeCDataXML(getTreatment(),eiTreatmentTag));
		out.write(includeXML(getDocumentType(),eiDocumentTypeTag));
		out.write(includeCDataXML(getAbstractText(),eiAbstractTextTag));

		out.write(includeCDataXML(getNumberOfReferences(),eiNumberOfReferencesTag));
		out.write(includeCDataXML(getDatabase(),eiDatabaseTag));
		out.write(includeXML(getDocIDXml(),eiDocidTag));
		out.write(includeCDataXML(getHandle(),eiHandleTag));

		out.write(includeXML(getClassificationCodes(),eiClassificationCodesTag));
		out.write(includeXML(getUnControlledTerms(),eiUnControlledTermsTag));
		out.write(includeXML(getControlledTerms(),eiControlledTermsTag));
		out.write(includeCDataXML(getAccessionNumber(),eiAccessionNumberTag));

		out.write(includeXML(getMaterialIdentityNum(),eiMaterialIdentityNumTag));
		out.write(includeCDataXML(getIssuingOrg(),eiIssuingOrgTag));
		out.write(includeCDataXML(getCopyright(),eiCopyrightTag));
		out.write(includeCDataXML(getTranslationCODEN(),eiTranslationCODENTag));

		out.write(includeCDataXML(getSponsorOrg(),eiSponsorOrgTag));
		out.write(includeCDataXML(getAstronomicalObjectIndexing(),eiAstronomicalObjectIndexingTag));
		out.write(includeCDataXML(getTranslationCountryOfPub(),eiTranslationCountryOfPubTag));
		out.write(includeCDataXML(getHighLevelPubTitle(),eiHighLevelPubTitleTag));
		out.write(includeCDataXML(getTranslationPages(),eiTranslationPagesTag));

		out.write(includeCDataXML(getFilingDate(),eiFilingDateTag));
		out.write(includeCDataXML(getNumericalDataIndexing(),eiNumericalDataIndexingTag));
		out.write(includeCDataXML(getFirstAuthorAffiliation(),eiFirstAuthorAffiliationTag));
		out.write(includeCDataXML(getPatentNumber(),eiPatentNumberTag));
		out.write(includeXML(getPartNumber(),eiPartNumberTag));

		out.write(includeCDataXML(getTranslationISSN(),eiTranslationISSNTag));
		out.write(includeCDataXML(getFirstEditorAffiliation(),eiFirstEditorAffiliationTag));
		out.write(includeCDataXML(getReportNumber(),eiReportNumberTag));
		out.write(includeCDataXML(getChemicalIndexing(),eiChemicalIndexingTag));
		out.write(includeCDataXML(getTranslationSerialTitle(),eiTranslationSerialTitleTag));
		out.write(includeXML(getTranslationVolumeIssue(),eiTranslationVolumeIssueTag));
		out.write(includeCDataXML(getAssignee(),eiAssigneeTag));
		out.write(includeCDataXML(getTranslationPublicationDate(),eiTranslationPublicationDateTag));
		out.write(includeCDataXML(getApplicationNumber(),eiApplicationNumberTag));
		out.write(includeCDataXML(getTranslationAbbrSerialTitle(),eiTranslationAbbrSerialTitleTag));
		out.write(includeXML(getDisciplines(),eiDisciplinesTag));

		out.write(includeCDataXML(getPublisherName(),eiPublisherNameTag));
		out.write(includeCDataXML(getPlaceOfPublisher(),eiPlaceOfPublisher));

		out.write(includeCDataXML(getPublisherCountry(),eiPublisherCountryTag));
		out.write(includeCDataXML(getConferenceDate(),eiConferenceDateTag));
		out.write(includeCDataXML(getConferenceTitle(),eiConferenceNameTag));
		out.write(includeCDataXML(getConferenceLocation(),eiConferenceLocationTag));
		out.write(includeCDataXML(getCountryOfAppl(),eiCountryOfApplTag));
		out.write(includeCDataXML(getURL(),eiURLTag));
		out.write(includeCDataXML(getUM(),eiUMTag));
		out.write(getAuthorForLocalHolding());

		out.write(buildIVIP());
		//System.out.println(buildIVIP());

        out.write("</"+eiRootDocumentTag+">");

	}

	public String includeXML(String methodResult,String methodTag){

		if((methodResult==null)||(methodResult.trim().equals(""))){
			return "";
		}else{
			String str="<"+methodTag+">"+methodResult+"</"+methodTag+">";
			return str;
		}

	}


	public String includeCDataXML(String methodResult,String methodTag){

		if((methodResult==null)||(methodResult.trim().equals(""))){
			return "";
		}else{
			String str="<"+methodTag+"><![CDATA["+methodResult+"]]></"+methodTag+">";
			return str;
		}

	}


   /** This method provides the String format of EIDocument.
	 *  @return String
	 */
	public String toString()
	{
		StringBuffer journalString = new StringBuffer();
		journalString.append("<"+eiRootDocumentTag+">");
		journalString.append(includeXML(getAuthors(),eiAuthorsTag));
		journalString.append(includeCDataXML(getArticleTitle(),eiArticleTitleTag));
		journalString.append(includeXML(getEditors(),eiEditorsTag));
		journalString.append(includeCDataXML(getSerialTitle(),eiSerialTitleTag));
		journalString.append(includeCDataXML(getAbbreviatedSerialTitle(),eiAbbreviatedSerialTitleTag));
		journalString.append(includeXML(getVoliss(),eiVolissTag));
		journalString.append(includeCDataXML(getISSN(),eiISSNTag));
		journalString.append(includeCDataXML(getPublicationDate(),eiPublicationDateTag));
		journalString.append(includeCDataXML(getISBN(),eiISBNTag));
		journalString.append(includeXML(getPages(),eiPagesTag));
		journalString.append(includeXML(getPages1(),eiPages1Tag));
		journalString.append(includeXML(getPages2(),eiPages2Tag));
		journalString.append(includeCDataXML(getCoden(),eiCodenTag));
		journalString.append(includeCDataXML(getLanguage(),eiLanguageTag));
		journalString.append(includeCDataXML(getTreatment(),eiTreatmentTag));
		journalString.append(includeXML(getDocumentType(),eiDocumentTypeTag));
		journalString.append(includeCDataXML(getAbstractText(),eiAbstractTextTag));
		journalString.append(includeCDataXML(getNumberOfReferences(),eiNumberOfReferencesTag));
		journalString.append(includeCDataXML(getDatabase(),eiDatabaseTag));
		journalString.append(includeXML(getDocIDXml(),eiDocidTag));
		journalString.append(includeCDataXML(getHandle(),eiHandleTag));
		journalString.append(includeXML(getClassificationCodes(),eiClassificationCodesTag));
		journalString.append(includeXML(getUnControlledTerms(),eiUnControlledTermsTag));
		journalString.append(includeXML(getControlledTerms(),eiControlledTermsTag));
		journalString.append(includeCDataXML(getAccessionNumber(),eiAccessionNumberTag));
		journalString.append(includeXML(getMaterialIdentityNum(),eiMaterialIdentityNumTag));
		journalString.append(includeCDataXML(getIssuingOrg(),eiIssuingOrgTag));
		journalString.append(includeCDataXML(getCopyright(),eiCopyrightTag));
		journalString.append(includeCDataXML(getTranslationCODEN(),eiTranslationCODENTag));
		journalString.append(includeCDataXML(getSponsorOrg(),eiSponsorOrgTag));
		journalString.append(includeCDataXML(getAstronomicalObjectIndexing(),eiAstronomicalObjectIndexingTag));
		journalString.append(includeCDataXML(getTranslationCountryOfPub(),eiTranslationCountryOfPubTag));
		journalString.append(includeCDataXML(getHighLevelPubTitle(),eiHighLevelPubTitleTag));
		journalString.append(includeCDataXML(getTranslationPages(),eiTranslationPagesTag));
		journalString.append(includeCDataXML(getFilingDate(),eiFilingDateTag));
		journalString.append(includeCDataXML(getNumericalDataIndexing(),eiNumericalDataIndexingTag));
		journalString.append(includeCDataXML(getFirstAuthorAffiliation(),eiFirstAuthorAffiliationTag));
		journalString.append(includeCDataXML(getPatentNumber(),eiPatentNumberTag));
		journalString.append(includeXML(getPartNumber(),eiPartNumberTag));
		journalString.append(includeCDataXML(getTranslationISSN(),eiTranslationISSNTag));
		journalString.append(includeCDataXML(getFirstEditorAffiliation(),eiFirstEditorAffiliationTag));
		journalString.append(includeCDataXML(getReportNumber(),eiReportNumberTag));
		journalString.append(includeCDataXML(getChemicalIndexing(),eiChemicalIndexingTag));
		journalString.append(includeCDataXML(getTranslationSerialTitle(),eiTranslationSerialTitleTag));
		journalString.append(includeXML(getTranslationVolumeIssue(),eiTranslationVolumeIssueTag));
		journalString.append(includeCDataXML(getAssignee(),eiAssigneeTag));
		journalString.append(includeCDataXML(getTranslationPublicationDate(),eiTranslationPublicationDateTag));
		journalString.append(includeCDataXML(getApplicationNumber(),eiApplicationNumberTag));
		journalString.append(includeCDataXML(getTranslationAbbrSerialTitle(),eiTranslationAbbrSerialTitleTag));
		journalString.append(includeXML(getDisciplines(),eiDisciplinesTag));
		journalString.append(includeCDataXML(getPublisherName(),eiPublisherNameTag));
		journalString.append(includeCDataXML(getPlaceOfPublisher(),eiPlaceOfPublisher));
		journalString.append(includeCDataXML(getPublisherCountry(),eiPublisherCountryTag));
		journalString.append(includeCDataXML(getConferenceDate(),eiConferenceDateTag));
		journalString.append(includeCDataXML(getConferenceTitle(),eiConferenceNameTag));
		journalString.append(includeCDataXML(getConferenceLocation(),eiConferenceLocationTag));
		journalString.append(includeCDataXML(getCountryOfAppl(),eiCountryOfApplTag));
		journalString.append(includeCDataXML(getURL(),eiURLTag));
		journalString.append(includeCDataXML(getUM(),eiUMTag));
		journalString.append(getAuthorForLocalHolding());

        journalString.append("</"+eiRootDocumentTag+">");
		return journalString.toString();
	}



/** This method returns the authors in xml format.
  * @return Authors
  */


      private String getAuthors(String Authors){
		 StringBuffer xmlAuthStr=new StringBuffer();
		 StringTokenizer st=new StringTokenizer(Authors,";");
		 if(Authors!=null){
			 while(st.hasMoreTokens()){
				 xmlAuthStr.append("<").append(eiAuthorTag).append(">").append("<![CDATA[").append(st.nextToken()).append("]]>").append("</").append(eiAuthorTag).append(">");
			 }
		 }
		 else
		 {
			 xmlAuthStr.append("<").append(eiAuthorTag).append("></").append(eiAuthorTag).append(">");
		 }

		return xmlAuthStr.toString();
 	  }

	    private String getEditors(String editors){
	 		 StringBuffer xmlAuthStr=new StringBuffer();
	 		 StringTokenizer st=new StringTokenizer(editors,";");
	 		 if(editors!=null)
	 		 {
	 			 while(st.hasMoreTokens()){
	 				 xmlAuthStr.append("<").append(eiEditorTag).append(">").append("<![CDATA[").append(st.nextToken()).append("]]>").append("</").append(eiEditorTag).append(">");
	 			 }
	 		 }
	 		 else
			 {
			 	 	 xmlAuthStr.append("<").append(eiEditorTag).append("></").append(eiEditorTag).append(">");
	 		 }

	 		return xmlAuthStr.toString();
 		}



	 /** This method returns Numeric Classification Codes
	 	  * @return Numeric Classification Codes
	 	  */
	 	 private String getClassificationCodes(String classCodes)
	 	 {
	 		 StringBuffer tempStr=new StringBuffer();
	 		 if(classCodes!=null)
	 		 {
	 		 StringTokenizer stoken=new StringTokenizer(classCodes,";");
	 		 while(stoken.hasMoreTokens())
	 		 {
	 			String str=stoken.nextToken();
	 			tempStr.append("<").append(eiClassificationCodeTag).append(">").append("<![CDATA[").append(str).append("]]>").append("</").append(eiClassificationCodeTag).append(">");
	 			}
	 		 }
	 		 else
	 		 {
	 		    tempStr.append("<").append(eiClassificationCodeTag).append("></").append(eiClassificationCodeTag).append(">");
	 		 }
	 		 return tempStr.toString();
	 	 }

      	/**
	*	This method returns Controlled Terms
	* 	@return Controlled Terms
	**/

	 private String getControlledTerms(String conTerms)
	 {
		 StringBuffer tempStr=new StringBuffer();
		 if(conTerms!=null)
		 {
		    StringTokenizer stoken=new StringTokenizer(conTerms,";");
		    while(stoken.hasMoreTokens())
		    {
			    String str=stoken.nextToken();
			    tempStr.append("<").append(eiControlledTermTag).append(">").append("<![CDATA[").append(str).append("]]>").append("</").append(eiControlledTermTag).append(">");
			    }
		    }
		    else
		    {
		        tempStr.append("<").append(eiControlledTermTag).append("></").append(eiControlledTermTag).append(">");
		    }
		 return tempStr.toString();
	 }

	/** This method returns Uncontrolled Terms.
	  * @return Uncontrolled Terms.
	  */
	 private String getUnControlledTerms(String unConTerms)
	 {
		 StringBuffer tempStr=new StringBuffer();
		 if(unConTerms!=null)
		 {
		 StringTokenizer stoken=new StringTokenizer(unConTerms,";");
		 while(stoken.hasMoreTokens())
		 {
			String str=stoken.nextToken();
			tempStr.append("<").append(eiUnControlledTermTag).append(">").append("<![CDATA[").append(str).append("]]>").append("</").append(eiUnControlledTermTag).append(">");
			}
		 }
		 else
		 {
		    tempStr.append("<").append(eiUnControlledTermTag).append("></").append(eiUnControlledTermTag).append(">");
		 }
		 return tempStr.toString();
	     }

		/** Return the volumn only
		*/
		private String getVolume() {
			StringBuffer tmpStr = new StringBuffer();
			if (voliss != null) {
				StringTokenizer stoken = new StringTokenizer(voliss,",");
				while(stoken.hasMoreTokens()){
					String str = stoken.nextToken();
					StringTokenizer st = new StringTokenizer(str,".");
					String tmp = st.nextToken();
					if(tmp.trim().equals("vol")){
						String numStr = st.nextToken();

						for(int x=0; x<numberPatterns.length; ++x)
						{
							String pattern = numberPatterns[x];
							if(perl.match(pattern, numStr))
							{
								MatchResult mResult = perl.getMatch();
								tmpStr.append(mResult.toString());
								break;
							}
						}
					}
				}
			}
			return tmpStr.toString();
		}


		/** Return the issue only
		*/
		private String getIssue() {
			StringBuffer tmpStr = new StringBuffer();
			if (voliss != null) {
				StringTokenizer stoken = new StringTokenizer(voliss,",");
				while(stoken.hasMoreTokens()){
					String str = stoken.nextToken();
					StringTokenizer st = new StringTokenizer(str,".");
					String tmp = st.nextToken();
					if(tmp.trim().equals("no")){
						String numStr = st.nextToken();

						for(int x=0; x<numberPatterns.length; ++x)
						{
							String pattern = numberPatterns[x];
							if(perl.match(pattern, numStr))
							{
								MatchResult mResult = perl.getMatch();
								tmpStr.append(mResult.toString());
								break;
							}
						}
					}
				}
			}
			return tmpStr.toString();
		}


		/** This method returns Uncontrolled Terms.
		  * @return voliss.
		  */
		 private String getVolumeIssue(String voliss)
		 {
			 StringBuffer tempStr=new StringBuffer();
			 if(voliss!=null){
				StringTokenizer stoken=new StringTokenizer(voliss,",");
				String str=null;
				while(stoken.hasMoreTokens()){
					tempStr.append(stripVolIss(stoken.nextToken()));
				}

			}
			 return tempStr.toString();
	      }

		private String stripVolIss(String str)
		{
			StringBuffer tempStr=new StringBuffer();
			StringTokenizer stoken=new StringTokenizer(str,".");
			String tmp=stoken.nextToken();
			if(tmp.trim().equals("vol")){
				tempStr.append("<").append(eiVolumeTag).append(">").append("<![CDATA[").append(stoken.nextToken()).append("]]>").append("</").append(eiVolumeTag).append(">");
			}else if(tmp.trim().equals("no")){
				if(stoken.hasMoreTokens()){
					tempStr.append("<").append(eiIssueTag).append(">").append("<![CDATA[").append(stoken.nextToken()).append("]]>").append("</").append(eiIssueTag).append(">");
				}
			}
			return tempStr.toString();
	    }


		/** This method returns Uncontrolled Terms.
		  * @return voliss.
		  */
		 private String getTranslationVolumeIssue(String volisstrans)
		 {
			 StringBuffer tempStr=new StringBuffer();
			 if(voliss!=null){
				StringTokenizer stoken=new StringTokenizer(volisstrans,",");
				String str=null;
				while(stoken.hasMoreTokens()){
					tempStr.append(stripVolIssTrans(stoken.nextToken()));
				}

			}
			 return tempStr.toString();
		  }

		private String stripVolIssTrans(String str)
		{
			StringBuffer tempStr=new StringBuffer();
			StringTokenizer stoken=new StringTokenizer(str,".");
			String tmp=stoken.nextToken();
			if(tmp.trim().equals("vol")){
				tempStr.append("<").append(eiTranslationVolumeTag).append(">").append("<![CDATA[").append(stoken.nextToken()).append("]]>").append("</").append(eiTranslationVolumeTag).append(">");
			}else if(tmp.trim().equals("no")){
				tempStr.append("<").append(eiTranslationIssueTag).append(">").append("<![CDATA[").append(stoken.nextToken()).append("]]>").append("</").append(eiTranslationIssueTag).append(">");
			}
			return tempStr.toString();
		}

		private String getPartNumber(String partNumber)
		{
			String str=null;
			StringTokenizer stoken=new StringTokenizer(partNumber,".");
			if(stoken.hasMoreTokens()){
				String tmp=stoken.nextToken();
				if(tmp.trim().equals("vol")){
					str= stoken.nextToken();
				}
			}
			return str;
		}


		 private String getDiciplines(String disp)
		 {
			 StringBuffer tempStr=new StringBuffer();
			 HashSet hs=new HashSet();
			 if(disp!=null){
				StringTokenizer stoken=new StringTokenizer(disp,";");
				while(stoken.hasMoreTokens()){
					String temp=stoken.nextToken();
					String tmp=temp.substring(0,1);
					hs.add(tmp);
				}
			}
			Iterator iter=hs.iterator();
			while(iter.hasNext()){
				String out="<"+eiDisciplineTag+">"+iter.next()+"</"+eiDisciplineTag+">";
				tempStr.append(out);
			}
			 return tempStr.toString();
      }





	private String getAuthorForLocalHolding(String Authors){
		 StringBuffer xmlAuthStr=new StringBuffer();
		 String fullName="";
		 String firstName="";
		 String lastName="";
		 if(Authors!=null){
			 StringTokenizer st=new StringTokenizer(Authors,";");
			 if(st.hasMoreTokens())
			 {
			 	fullName=st.nextToken();
			 }
			 else
			 {
			 	fullName=Authors;
			 }
			 xmlAuthStr.append("<AUTHOR-FULLNAME>");
			 xmlAuthStr.append("<![CDATA[");
			 xmlAuthStr.append(fullName);
			 xmlAuthStr.append("]]>");
			 xmlAuthStr.append("</AUTHOR-FULLNAME>");
			 StringTokenizer st1=new StringTokenizer(fullName,",");
			 if(st1.countTokens()>=1){
				 firstName=st1.nextToken();
				 xmlAuthStr.append("<AUTHOR-FIRSTNAME>");
				 xmlAuthStr.append("<![CDATA[");
				 xmlAuthStr.append(firstName);
				 xmlAuthStr.append("]]>");
				 xmlAuthStr.append("</AUTHOR-FIRSTNAME>");
				 if(st1.countTokens()==1){
					 lastName=st1.nextToken();
					 xmlAuthStr.append("<AUTHOR-LASTNAME>");
					 xmlAuthStr.append("<![CDATA[");
					 xmlAuthStr.append(lastName.trim());
					 xmlAuthStr.append("]]>");
					 xmlAuthStr.append("</AUTHOR-LASTNAME>");
			      }
		     }
		 }
	   return xmlAuthStr.toString();
	}


	/** This method returns IVIP
	  * @return IVIP format  eg:<IVIP ISSN="abc" firstPage="11" firstVolume="11" />
	  */
	private String buildIVIP(){
	    StringBuffer tempStr=new StringBuffer();
	    tempStr.append("<IVIP");
	    tempStr.append(buildISSN());
	    tempStr.append(buildFirstVolume());
	    tempStr.append(buildFirstIssue());
	    tempStr.append(buildFirstPage());
	    tempStr.append(">");
	    tempStr.append("</IVIP>");
	    return tempStr.toString();
	}

	/** This method returns ISSN part of IVIP
	  * ISSN format is abcd-xyz which is processed
	  * by this method Removes "-" and returns abcdxyz.
	  * @return tempStr
	  */
	private String buildISSN()
	{
	    ;
	    StringBuffer tempISSN=new StringBuffer();

	    tempISSN.append(" ISSN=").append("\"").append(getISSN()).append("\"");
	    return tempISSN.toString();
	}

	/** This method returns First Volume part of IVIP
	  * VIP format is volume/issue (firstpage-lastpage)
	  * returns the volume part of the above mentioned format.
	  * @return retStr
	  */
	private String buildFirstVolume()
	{
	    StringBuffer retStr=new StringBuffer();
	    retStr.append(" firstVolume=").append("\"").append(getVolume()).append("\"");
	    return retStr.toString();
	}

	/** This method returns First Isuue part of IVIP
	  * VIP format is volume/issue (firstpage-lastpage)
	  * returns the volume part of the above mentioned format.
	  * @return retStr
	  */
	private String buildFirstIssue()
	{
	    StringBuffer retStr=new StringBuffer();
	    retStr.append(" firstIssue=").append("\"").append(getIssue()).append("\"");
	    return retStr.toString();
	}

	/** This method returns First Page part of IVIP
	  * VIP format is volume/issue (firstpage-lastpage)
	  * returns the firstpage part of the above mentioned format.
	  * @return retStr
	  */
	private String buildFirstPage()
	{
		StringBuffer retStr=new StringBuffer();
		retStr.append(" firstPage=\"");
		String firstPage = null;

		StringTokenizer tmpPage = new StringTokenizer(getPages(),"-");
		if (tmpPage.countTokens()>0) {
			firstPage = tmpPage.nextToken();
		} else {
			firstPage = getPages();
		}


		for(int x=0; x<numberPatterns.length; ++x)
		{
			String pattern = numberPatterns[x];
			if(perl.match(pattern, firstPage))
			{
				MatchResult mResult = perl.getMatch();
				retStr.append(mResult.toString());
				break;
			}
		}
		retStr.append("\"");
	    return retStr.toString();

	}


	public String getLocalHoldingLink(String URL)
	{

		if (URL == null)
		{
			return null;
		}

		for(int i=0; i<LocalHoldingLinker.localHoldingFields.length; ++i)
		{
			URL = sUtil.replace(URL,
								LocalHoldingLinker.localHoldingFields[i],
								getDataForLocalHolding(LocalHoldingLinker.localHoldingFields[i]),
								StringUtil.REPLACE_GLOBAL,
								StringUtil.MATCH_CASE_SENSITIVE);
		}

		return URL;
	}

	private String getDataForLocalHolding(String field)
	{
		String value = null;

		if(field.equals(LocalHoldingLinker.AULAST))
		{
			value = notNull(getFirstAuthorLN());
		}
		else if(field.equals(LocalHoldingLinker.AUFIRST))
		{
			value = notNull(getFirstAuthorFN());
		}
		else if(field.equals(LocalHoldingLinker.AUFULL))
		{
			value = notNull(getFirstAuthor());
		}
		else if(field.equals(LocalHoldingLinker.ISSN))
		{
			value = notNull(getISSN2());
		}
		else if(field.equals(LocalHoldingLinker.ISSN9))
		{
			value = notNull(getISSN());
		}
		else if(field.equals(LocalHoldingLinker.CODEN))
		{
			value = notNull(getCoden());
		}
		else if(field.equals(LocalHoldingLinker.TITLE))
		{
			value = notNull(getSerialTitle());
		}
		else if(field.equals(LocalHoldingLinker.STITLE))
		{
			value = notNull(getAbbreviatedSerialTitle());
		}
		else if(field.equals(LocalHoldingLinker.ATITLE))
		{
			value = notNull(getArticleTitle());
		}
		else if(field.equals(LocalHoldingLinker.VOLUME))
		{
			value = notNull(getVolumeNo());
		}
		else if(field.equals(LocalHoldingLinker.ISSUE))
		{
			value = notNull(getIssueNo());
		}
		else if(field.equals(LocalHoldingLinker.SPAGE))
		{
			value = notNull(getPages1());
		}
		else if(field.equals(LocalHoldingLinker.EPAGE))
		{
			value = notNull(getPages2());
		}
		else if(field.equals(LocalHoldingLinker.PAGES))
		{
			value = notNull(getPages1())+"-"+notNull(getPages2());
		}
		else if(field.equals(LocalHoldingLinker.YEAR))
		{
			value = notNull(getYear());
		}

		if(value != null)
		{
			value = URLEncoder.encode(value);
		}
		else
		{
			return "";
		}

		return value;
	}

	public String notNull(String s)
	{
		if(s == null)
		{
			return "";
		}

		return s;
	}

}//end of class