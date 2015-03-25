package org.ei.data.compendex.runtime;

import org.ei.domain.*;
import java.util.*;
import java.io.*;
import org.ei.common.Constants;
import org.ei.common.AuthorStream;
import org.ei.util.StringUtil;
import java.net.*;

import org.ei.query.base.*;

import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;

/**
 * This class is basically responsible for Constructing the basic EIDocs.The same class presently serves for all formats.
 *
 */
public class CPXDocument {

    private String[] numberPatterns = { "/[1-9][0-9]*/" };

    private StringUtil sUtil = new StringUtil();

    private String[] regExPatterns = { "s/#/\\#/g" };

    private String authors;
    private String authorAffiCity;
    private String authorAssoState;
    private String authorAffiCountry;
    private String authorAffiProvidence;
    private String authorAffi;
    private String authorAffiCorpDiv;

    private String articleTitle;
    private String titleOfTranslation;
    private String accessionNumber;

    private String editors;
    private String editorAffiCity;
    private String editorAssoState;
    private String editorAffiCorpDiv;
    private String editorAffiCountry;
    private String editorAffiProvidence;
    private String editorAffi;

    private String serialTitle;
    private String abbreviatedSerialTitle;
    private String volume;
    private String volumeTitle;
    private String issue;
    private String issueDate;
    private String monographTitle;
    private String abbreviatedMonographTitle;

    private String publicationDate;
    private String numberOfVolumes;
    private String numberOfPages;
    private String paperNumber;
    private String pages;
    private String language;
    private String ISSN;
    private String coden;
    private String ISBN;

    private String treatment;
    private String documentType;
    private String abstractType;
    private String abstractText;

    private String numberOfReferences;
    private String database;
    private DocID docid;
    private String handle;
    private String eiMainHeading;

    private String pageRange;
    private String primaryCalCode;

    private String publisherCity;// dept
    private String publisherState;
    private String publisherName;
    private String publisherProvince;
    private String publisherCountry;
    private String publisherOrderNum;

    private String conferenceState;
    private String conferenceDate;
    private String conferenceName;
    private String conferenceCountry;
    private String conferenceCode;
    private String conferenceCity;
    private String conferenceProvince;

    private String sponsorState;
    private String sponsorDepartment;
    private String sponsorName;
    private String sponsorCountry;
    private String sponsorCity;
    private String sponsorProvince;

    private String controlledTerms;
    private String controlledTerm;

    private String classificationCode;
    private String classificationDesc;

    private String unControlledTerms;
    private String unControlledTerm;

    private static final String eiRootDocumentTag = "EI-DOCUMENT";

    private static final String eiHitIndexTag = "HIT-INDEX";

    private static final String eiAuthorsTag = "AUTHORS";
    private static final String eiAuthorTag = "AUTHOR";// just a tag

    private static final String eiFirstAuthorAffiliationTag = "FIRST-AUTHOR-AFFILIATION";// just a tag
    private static final String eiAuthorAssoStateTag = "AUTHOR-ASSO-STATE";
    private static final String eiAuthorAffiCountryTag = "AUTHOR-AFFI-COUNTRY";
    private static final String eiAuthorAffiProvidenceTag = "AUTHOR-AFFI-PROVINCE";
    private static final String eiAuthorAffiTag = "AUTHOR-AFFI";
    private static final String eiAuthorAffiCorpDivTag = "AUTHOR-AFFI-CORP-DIV";
    private static final String eiAuthorAffiCityTag = "AUTHOR-AFFI-CITY";

    private static final String eiArticleTitleTag = "ARTICLE-TITLE";
    private static final String eiTitleOfTranslationTag = "TITLE-OF-TRANSLATION";
    private static final String eiAccessionNumberTag = "ACCESSION-NUMBER";

    private static final String eiEditorsTag = "EDITORS";
    private static final String eiEditorTag = "EDITOR";// just a tag

    private static final String eiFirstEditorAffiliationTag = "FIRST-EDITOR-AFFILIATION";// just a tag
    private static final String eiEditorAffiCountryTag = "EDITOR-AFFI-COUNTRY";
    private static final String eiEditorAffiProvidenceTag = "EDITOR-AFFI-PROVINCE";
    private static final String eiEditorAffiTag = "EDITOR-AFFI";
    private static final String eiEditorAffiCityTag = "EDITOR-AFFI-CITY";
    private static final String eiEditorAffiCorpDivTag = "EDITOR-AFFI-CORP-DIV";
    private static final String eiEditorAssoStateTag = "EDITOR-ASSO-STATE";

    private static final String eiSerialTitleTag = "SERIAL-TITLE";
    private static final String eiAbbreviatedSerialTitleTag = "ABBREVIATED-SERIAL-TITLE";

    private static final String eiVolumeTag = "VOLUME";
    private static final String eiVolumeTitleTag = "VOLUME-TITLE";

    private static final String eiIssueTag = "ISSUE";
    private static final String eiIssueDateTag = "ISSUE-DATE";

    private static final String eiMonographTitleTag = "MONOGRAPH-TITLE";
    private static final String eiAbbreviatedMonographTitleTag = "ABBREVIATED-MONOGRAPH-TITLE";

    private static final String eiPublicationDateTag = "PUBLICATION-DATE";
    private static final String eiNumberOfVolumesTag = "NUMBER-OF-VOLUMES";

    private static final String eiPaperNumberTag = "PAPER-NUMBER";
    private static final String eiPagesTag = "PAGES";

    private static final String eiLanguageTag = "LANGUAGE";
    private static final String eiISSNTag = "ISSN";

    private static final String eiCodenTag = "CODEN";
    private static final String eiISBNTag = "ISBN";

    private static final String eiTreatmentTag = "TREATMENT";
    private static final String eiDocumentTypeTag = "DOCUMENT-TYPE";

    private static final String eiAbstractTextTag = "ABSTRACT";
    private static final String eiAbstractTypeTag = "ABSTRACT-TYPE";

    private static final String eiNumberOfReferencesTag = "NUMBER-OF-REFERENCES";
    private static final String eiDocidTag = "DOC-ID";
    private static final String eiHandleTag = "HANDLE";
    private static final String eiDatabaseTag = "DATABASE";

    private static final String eiPublisherLocationTag = "PUBLISHER-LOCATION";// JUST A TAG
    private static final String eiPublisherTag = "PUBLISHER";// JUST A TAG

    private static final String eiPublisherNameTag = "PUBLISHER-NAME";
    private static final String eiPublisherCityTag = "PUBLISHER-CITY";// dept
    private static final String eiPublisherStateTag = "PUBLISHER-STATE";
    private static final String eiPublisherCountryTag = "PUBLISHER-COUNTRY";
    private static final String eiPublisherProvinceTag = "PUBLISHER-PROVINCE";
    private static final String eiPublisherOrderNumTag = "PUBLISHER-ORDER-NUM";

    private static final String eiConferenceTag = "CONFERENCE";// just a tag
    private static final String eiConferenceLocationTag = "CONFERENCE-LOCATION";// just a tag

    private static final String eiConferenceNameTag = "CONFERENCE-NAME";
    private static final String eiConferenceDateTag = "CONFERENCE-DATE";
    private static final String eiConferenceCodeTag = "CONFERENCE-CODE";
    private static final String eiConferenceCityTag = "CONFERENCE-CITY";
    private static final String eiConferenceStateTag = "CONFERENCE-STATE";
    private static final String eiConferenceCountryTag = "CONFERENCE-COUNTRY";
    private static final String eiConferenceProvinceTag = "CONFERENCE-PROVINCE";

    private static final String eiSponsorTag = "SPONSOR";// just a tag
    private static final String eiSponsorLocationTag = "SPONSOR-LOCATION";// just a tag

    private static final String eiSponsorNameTag = "SPONSOR-NAME";
    private static final String eiSponsorDepartmentTag = "SPONSOR-DEPARTMENT";
    private static final String eiSponsorCityTag = "SPONSOR-CITY";
    private static final String eiSponsorStateTag = "SPONSOR-STATE";
    private static final String eiSponsorCountryTag = "SPONSOR-COUNTRY";
    private static final String eiSponsorProvinceTag = "SPONSOR-PROVINCE";

    private static final String eiClassificationCodesTag = "CLASSIFICATION-CODES";// just a tag
    private static final String eiClassificationCodeTag = "CLASSIFICATION-CODE";// just a tag
    private static final String eiDescTag = "C-DESC";
    private static final String eiCodeTag = "C-CODE";

    private static final String eiControlledTermsTag = "CONTROLLED-TERMS";
    private static final String eiControlledTermTag = "CONTROLLED-TERM";

    private static final String eiUnControlledTermsTag = "UNCONTROLLED-TERMS";
    private static final String eiUnControlledTermTag = "UNCONTROLLED-TERM";

    private static final String eiEiMainHeadingTag = "EI-MAIN-HEADING";

    private static final String eiNumberOfPagesTag = "NUMBER-OF-PAGES";

    private static final String eiPageRangeTag = "PAGE-RANGE";
    private static final String eiPrimaryCalCodeTag = "PRIMARY-CAL-CODE";

    private HitHighlightFinisher fi = new HitHighlightFinisher();
    private Perl5Util perl = new Perl5Util();

    public CPXDocument newInstance(DocID aDocid) {
        return new CPXDocument(aDocid);
    }

    public CPXDocument() {

    }

    public CPXDocument(DocID aDocid) {
        this.docid = aDocid;
    }

    public Hashtable<String, String> getHighlightData(String field) {

        if (field.equalsIgnoreCase("all")) {
            Hashtable<String, String> h = new Hashtable<String, String>();
            h.put("ARTICLE-TITLE", this.articleTitle);
            if (this.abstractText != null) {
                h.put("ABSTRACT", this.abstractText);
            }

            if (this.authorAffi != null) {
                h.put("AUTHOR-AFFI", this.authorAffi);
            }

            if (this.editorAffi != null) {
                h.put("EDITOR-AFFI", this.editorAffi);
            }

            if (this.controlledTerms != null) {
                String tempCT = perl.substitute("s/;/ QQ /g", this.controlledTerms);
                h.put("CONTROLLED-TERMS", tempCT);
            }
            if (this.unControlledTerms != null) {
                String tempUCT = perl.substitute("s/;/ QQ /g", this.unControlledTerms);
                h.put("UNCONTROLLED-TERMS", tempUCT);
            }

            if (this.serialTitle != null) {
                h.put("SERIAL-TITLE", this.serialTitle);
            }

            if (this.eiMainHeading != null) {
                h.put("MAIN-HEADING", this.eiMainHeading);
            }

            if (this.publisherName != null) {
                h.put("PUB-NAME", this.publisherName);
            }

            if (this.conferenceName != null) {
                h.put("CONF-NAME", this.conferenceName);
            }

            if (this.conferenceDate != null) {
                h.put("CONF-DATE", this.conferenceDate);
            }

            if (this.conferenceCode != null) {
                h.put("CONF-CODE", this.conferenceCode);
            }

            if (this.conferenceCity != null) {
                h.put("CONF-CITY", this.conferenceCity);
            }

            if (this.conferenceState != null) {
                h.put("CONF-STATE", this.conferenceState);
            }

            if (this.conferenceProvince != null) {
                h.put("CONF-PROVINCE", this.conferenceProvince);
            }

            if (this.conferenceCountry != null) {
                h.put("CONF-COUNTRY", this.conferenceCountry);
            }

            if (this.monographTitle != null) {
                h.put("MONO-TITLE", this.monographTitle);
            }

            if (this.sponsorName != null) {
                String tempSN = perl.substitute("s/;/ QQ /g", this.sponsorName);
                h.put("SPON-NAME", tempSN);
            }

            return h;
        } else if (field.equalsIgnoreCase("st")) {
            Hashtable<String, String> h = new Hashtable<String, String>();
            if (this.serialTitle != null) {
                h.put("SERIAL-TITLE", this.serialTitle);
            }

            return h;
        } else if (field.equalsIgnoreCase("cf")) {
            Hashtable<String, String> h = new Hashtable<String, String>();
            if (this.conferenceName != null) {
                h.put("CONF-NAME", this.conferenceName);
            }

            if (this.conferenceCode != null) {
                h.put("CONF-CODE", this.conferenceCode);
            }

            if (this.conferenceDate != null) {
                h.put("CONF-DATE", this.conferenceDate);
            }

            if (this.conferenceCity != null) {
                h.put("CONF-CITY", this.conferenceCity);
            }

            if (this.conferenceState != null) {
                h.put("CONF-STATE", this.conferenceState);
            }

            if (this.conferenceProvince != null) {
                h.put("CONF-PROVINCE", this.conferenceProvince);
            }

            if (this.conferenceCountry != null) {
                h.put("CONF-COUNTRY", this.conferenceCountry);
            }

            if (this.sponsorName != null) {
                String tempSN = perl.substitute("s/;/ QQ /g", this.sponsorName);
                h.put("SPON-NAME", tempSN);
            }

            return h;
        } else if (field.equalsIgnoreCase("mh")) {
            Hashtable<String, String> h = new Hashtable<String, String>();
            if (this.eiMainHeading != null) {
                h.put("MAIN-HEADING", this.eiMainHeading);
            }

            return h;
        } else if (field.equalsIgnoreCase("af")) {
            Hashtable<String, String> h = new Hashtable<String, String>();
            if (this.authorAffi != null) {
                h.put("AUTHOR-AFFI", this.authorAffi);
            }

            if (this.editorAffi != null) {
                h.put("EDITOR-AFFI", this.editorAffi);
            }

            return h;
        } else if (field.equalsIgnoreCase("ky")) {
            Hashtable<String, String> h = new Hashtable<String, String>();
            h.put("ARTICLE-TITLE", this.articleTitle);
            if (this.abstractText != null) {
                h.put("ABSTRACT", this.abstractText);
            }
            if (this.controlledTerms != null) {
                String tempCT = perl.substitute("s/;/ QQ /g", this.controlledTerms);
                h.put("CONTROLLED-TERMS", tempCT);
            }
            if (this.unControlledTerms != null) {
                String tempUCT = perl.substitute("s/;/ QQ /g", this.unControlledTerms);
                h.put("UNCONTROLLED-TERMS", tempUCT);
            }

            if (this.eiMainHeading != null) {
                h.put("MAIN-HEADING", this.eiMainHeading);
            }

            return h;
        } else if (field.equalsIgnoreCase("ti")) {
            Hashtable<String, String> h = new Hashtable<String, String>();
            h.put("ARTICLE-TITLE", this.articleTitle);
            return h;
        } else if (field.equalsIgnoreCase("cv")) {
            Hashtable<String, String> h = new Hashtable<String, String>();

            if (this.controlledTerms != null) {
                String tempCT = perl.substitute("s/;/ QQ /g", this.controlledTerms);
                h.put("CONTROLLED-TERMS", tempCT);
            }

            if (this.eiMainHeading != null) {
                h.put("MAIN-HEADING", this.eiMainHeading);
            }

            return h;
        } else if (field.equalsIgnoreCase("pn")) {
            Hashtable<String, String> h = new Hashtable<String, String>();

            if (this.publisherName != null) {
                h.put("PUB-NAME", this.publisherName);
            }

            return h;
        } else if (field.equalsIgnoreCase("fl")) {
            Hashtable<String, String> h = new Hashtable<String, String>();

            if (this.unControlledTerms != null) {
                String tempCT = perl.substitute("s/;/ QQ /g", this.unControlledTerms);
                h.put("UNCONTROLLED-TERMS", tempCT);
            }

            return h;
        } else if (field.equalsIgnoreCase("ab")) {
            Hashtable<String, String> h = new Hashtable<String, String>();
            if (this.abstractText != null) {
                h.put("ABSTRACT", this.abstractText);
            }

            return h;
        }

        return null;
    }

    public void setHighlightData(Hashtable<?, ?> h) {

        if (h.containsKey("ARTICLE-TITLE")) {
            this.articleTitle = validate(h.get("ARTICLE-TITLE"));
        }

        if (h.containsKey("ABSTRACT")) {
            this.abstractText = validate(h.get("ABSTRACT"));
        }

        if (h.containsKey("CONTROLLED-TERMS")) {
            this.controlledTerms = validate(perl.substitute("s/ QQ /;/g", (String) h.get("CONTROLLED-TERMS")));
        }

        if (h.containsKey("UNCONTROLLED-TERMS")) {
            this.unControlledTerms = validate(perl.substitute("s/ QQ /;/g", (String) h.get("UNCONTROLLED-TERMS")));
        }

        if (h.containsKey("SERIAL-TITLE")) {
            this.serialTitle = validate(h.get("SERIAL-TITLE"));
        }

        if (h.containsKey("MAIN-HEADING")) {
            this.eiMainHeading = validate(h.get("MAIN-HEADING"));
        }

        if (h.containsKey("PUB-NAME")) {
            this.publisherName = validate(h.get("PUB-NAME"));
        }

        if (h.containsKey("CONF-NAME")) {
            this.conferenceName = validate(h.get("CONF-NAME"));
        }

        if (h.containsKey("CONF-CODE")) {
            this.conferenceCode = validate(h.get("CONF-CODE"));
        }

        if (h.containsKey("CONF-DATE")) {
            this.conferenceDate = validate(h.get("CONF-DATE"));
        }

        if (h.containsKey("CONF-CITY")) {
            this.conferenceCity = validate(h.get("CONF-CITY"));
        }

        if (h.containsKey("CONF-STATE")) {
            this.conferenceState = validate(h.get("CONF-STATE"));
        }

        if (h.containsKey("CONF-PROVINCE")) {
            this.conferenceProvince = validate(h.get("CONF-PROVINCE"));
        }

        if (h.containsKey("CONF-COUNTRY")) {
            this.conferenceCountry = validate(h.get("CONF-COUNTRY"));
        }

        if (h.containsKey("SPON-NAME")) {
            this.sponsorName = validate(perl.substitute("s/ QQ /;/g", (String) h.get("SPON-NAME")));
        }

        if (h.containsKey("MONO-TITLE")) {
            this.monographTitle = validate(h.get("MONO-TITLE"));
        }

        if (h.containsKey("AUTHOR-AFFI")) {
            this.authorAffi = validate(h.get("AUTHOR-AFFI"));
        }

        if (h.containsKey("EDITOR-AFFI")) {
            this.editorAffi = validate(h.get("EDITOR-AFFI"));
        }
    }

    /**
     * This method is basically used for initialization. Hashtable is iterated and corresponding set methods are called.
     *
     * @param Hashtable
     */
    public void load(Hashtable<?, ?> ht) {
        this.authors = validate(ht.get("AUTHORS"));
        this.authorAffiCity = validate(ht.get("AUTHOR-AFFI-CITY"));
        this.authorAssoState = validate(ht.get("AUTHOR-ASSO-STATE"));
        this.authorAffiCountry = validate(ht.get("AUTHOR-AFFI-COUNTRY"));
        this.authorAffiProvidence = validate(ht.get("AUTHOR-AFFI-PROVIDENCE"));
        this.authorAffi = validate(ht.get("AUTHOR-AFFI"));
        this.authorAffiCorpDiv = validate(ht.get("AUTHOR-AFFI-CORP-DIV"));
        this.articleTitle = validate(ht.get("ARTICLE-TITLE"));
        this.titleOfTranslation = validate(ht.get("TITLE-OF-TRANSLATION"));
        this.accessionNumber = validate(ht.get("ACCESSION-NUMBER"));
        this.editors = validate(ht.get("EDITORS"));
        this.editorAffiCity = validate(ht.get("EDITOR-AFFI-CITY"));
        this.editorAssoState = validate(ht.get("EDITOR-ASSO-STATE"));
        this.editorAffiCorpDiv = validate(ht.get("EDITOR-AFFI-CORP-DIV"));
        this.editorAffiCountry = validate(ht.get("EDITOR-AFFI-COUNTRY"));
        this.editorAffiProvidence = validate(ht.get("EDITOR-AFFI-PROVIDENCE"));
        this.editorAffi = validate(ht.get("EDITOR-AFFI"));
        this.serialTitle = validate(ht.get("SERIAL-TITLE"));
        this.abbreviatedSerialTitle = validate(ht.get("ABBREVIATED-SERIAL-TITLE"));
        this.volume = validate(ht.get("VOLUME"));
        this.volumeTitle = validate(ht.get("VOLUME-TITLE"));
        this.issue = validate(ht.get("ISSUE"));
        this.issueDate = validate(ht.get("ISSUE-DATE"));
        this.monographTitle = validate(ht.get("MONOGRAPH-TITLE"));
        this.abbreviatedMonographTitle = validate(ht.get("ABBREVIATED-MONOGRAPH-TITLE"));
        this.publicationDate = validate(ht.get("PUBLICATION-DATE"));
        this.numberOfVolumes = validate(ht.get("NUMBER-OF-VOLUMES"));
        this.numberOfPages = validate(ht.get("NUMBER-OF-PAGES"));
        this.paperNumber = validate(ht.get("PAPER-NUMBER"));
        this.pages = validate(ht.get("PAGES"));
        this.pageRange = validate(ht.get("PAGE-RANGE"));
        this.language = validate(ht.get("LANGUAGE"));
        this.ISSN = validate(ht.get("ISSN"));
        this.coden = validate(ht.get("CODEN"));
        this.ISBN = validate(ht.get("ISBN"));
        this.treatment = validate(ht.get("TREATMENT"));
        this.documentType = validate(ht.get("DOCUMENT-TYPE"));
        this.abstractType = validate(ht.get("ABSTRACT-TYPE"));
        this.abstractText = validate(ht.get("ABSTRACT"));
        this.numberOfReferences = validate(ht.get("NUMBER-OF-REFERENCES"));
        this.database = validate(ht.get("DATABASE"));
        this.docid = ((DocID) ht.get("DOC-ID"));
        this.handle = validate(ht.get("HANDLE"));
        this.publisherCity = validate(ht.get("PUBLISHER-DEPARTMENT"));// dept
        this.publisherState = validate(ht.get("PUBLISHER-STATE"));
        this.publisherName = validate(ht.get("PUBLISHER-NAME"));
        this.publisherProvince = validate(ht.get("PUBLISHER-PROVINCE"));
        this.publisherCountry = validate(ht.get("PUBLISHER-COUNTRY"));
        this.publisherOrderNum = validate(ht.get("PUBLISHER-ORDER-NUM"));
        this.conferenceState = validate(ht.get("CONFERENCE-STATE"));
        this.conferenceDate = validate(ht.get("CONFERENCE-DATE"));
        this.conferenceName = validate(ht.get("CONFERENCE-NAME"));
        this.conferenceCountry = validate(ht.get("CONFERENCE-COUNTRY"));
        this.conferenceCode = validate(ht.get("CONFERENCE-CODE"));
        this.conferenceCity = validate(ht.get("CONFERENCE-CITY"));
        this.conferenceProvince = validate(ht.get("CONFERENCE-PROVINCE"));
        this.sponsorState = validate(ht.get("SPONSOR-STATE"));
        this.sponsorProvince = validate(ht.get("SPONSOR-PROVINCE"));
        this.sponsorName = validate(ht.get("SPONSOR-NAME"));
        this.sponsorCountry = validate(ht.get("SPONSOR-COUNTRY"));
        this.sponsorCity = validate(ht.get("SPONSOR-CITY"));
        this.controlledTerms = validate(ht.get("CONTROLLED-TERMS"));

        this.classificationCode = ((String) ht.get("C-CODE"));
        this.classificationDesc = ((String) ht.get("C-DESC"));

        this.eiMainHeading = validate(ht.get("EI-MAIN-HEADING"));
        this.pageRange = validate(ht.get("PAGE-RANGE"));
        this.primaryCalCode = validate(ht.get("PRIMARY-CAL-CODE"));
        this.unControlledTerms = validate(ht.get("UNCONTROLLED-TERMS"));

    }// end of load

    /*
     * This method basically for checks for null If the object is null then returns ""
     *
     * @ param Object
     *
     * @ return String
     */
    private String validate(Object obj) {
        String str = (String) obj;
        if (str == null) {
            str = "";
        }
        return str;
    }

    // Accessor and Mutators methods

    public String getYear() {
        StringBuffer retStr = new StringBuffer();
        if (perl.match("/\\d\\d\\d\\d/", getPublicationDate())) {
            MatchResult mResult = perl.getMatch();
            retStr.append(mResult.toString());
        }
        return retStr.toString();
    }

    private String getEndPage() {
        StringBuffer retStr = new StringBuffer();
        String lastPage = null;
        if (getPageRange() != null) {
            StringTokenizer tmpPage = new StringTokenizer(getPageRange(), "-");
            if (tmpPage.countTokens() > 1) {
                lastPage = tmpPage.nextToken();
                lastPage = tmpPage.nextToken();
            } else {
                lastPage = getPageRange();
            }

            for (int x = 0; x < numberPatterns.length; ++x) {
                String pattern = numberPatterns[x];
                if (perl.match(pattern, lastPage)) {
                    MatchResult mResult = perl.getMatch();
                    retStr.append(mResult.toString());
                    break;
                }
            }
            return retStr.toString();
        }

        return null;
    }

    private String getStartPage() {
        StringBuffer retStr = new StringBuffer();
        String firstPage = null;
        if (getPageRange() != null) {
            StringTokenizer tmpPage = new StringTokenizer(getPageRange(), "-");
            if (tmpPage.countTokens() > 0) {
                firstPage = tmpPage.nextToken();
            } else {
                firstPage = getPageRange();
            }

            for (int x = 0; x < numberPatterns.length; ++x) {
                String pattern = numberPatterns[x];
                if (perl.match(pattern, firstPage)) {
                    MatchResult mResult = perl.getMatch();
                    retStr.append(mResult.toString());
                    break;
                }
            }
            return retStr.toString();
        }

        return null;

    }

    public String getPrimaryCalCode() {
        return primaryCalCode;
    }

    public String getPageRange() {
        return pageRange;
    }

    public String getFirstAuthorAffiliation() {
        StringBuffer sb = new StringBuffer();
        sb.append("<" + eiAuthorAffiCountryTag + "><![CDATA[" + getAuthorAffiCountry() + "]]></" + eiAuthorAffiCountryTag + ">");
        sb.append("<" + eiAuthorAssoStateTag + "><![CDATA[" + getAuthorAssoState() + "]]></" + eiAuthorAssoStateTag + ">");
        sb.append("<" + eiAuthorAffiTag + "><![CDATA[" + getAuthorAffi() + "]]></" + eiAuthorAffiTag + ">");
        sb.append("<" + eiAuthorAffiCityTag + "><![CDATA[" + getAuthorAffiCity() + "]]></" + eiAuthorAffiCityTag + ">");
        sb.append("<" + eiAuthorAffiProvidenceTag + "><![CDATA[" + getAuthorAffiProvidence() + "]]></" + eiAuthorAffiProvidenceTag + ">");
        sb.append("<" + eiAuthorAffiCorpDivTag + "><![CDATA[" + getAuthorAffiCorpDiv() + "]]></" + eiAuthorAffiCorpDivTag + ">");
        return sb.toString();
    }

    public String getAuthorAffiCountry() {
        return authorAffiCountry;
    }

    public String getAuthorAssoState() {
        return authorAssoState;
    }

    public String getAuthorAffi() {
        return authorAffi;
    }

    public String getAuthorAffiCity() {
        return authorAffiCity;
    }

    public String getAuthorAffiProvidence() {
        return authorAffiProvidence;
    }

    public String getAuthorAffiCorpDiv() {
        return authorAffiCorpDiv;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getTitleOfTranslation() {
        return titleOfTranslation;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public String getAuthors() throws IOException {
        return getAuthors(authors);
    }

    public String getFirstAuthor() {
        if (authors != null) {
            StringTokenizer st = new StringTokenizer(authors, ";", false);
            if (st.countTokens() > 0) {
                return st.nextToken();
            }
        }

        return null;
    }

    public String getFirstAuthorLN() {
        if (getFirstAuthor() != null) {
            StringTokenizer st = new StringTokenizer(getFirstAuthor(), ",", false);
            if (st.countTokens() > 0) {
                return st.nextToken();
            }
        }

        return null;
    }

    public String getFirstAuthorFN() {
        if (getFirstAuthor() != null) {

            StringTokenizer st = new StringTokenizer(getFirstAuthor(), ",", false);
            if (st.countTokens() > 1) {
                String tmpStr = st.nextToken();
                return st.nextToken();
            }
        }

        return null;
    }

    public String getEditors() throws IOException {
        return getEditors(editors);
    }

    public String getEditorAffiCountry() {
        return editorAffiCountry;
    }

    public String getEditorAssoState() {
        return editorAssoState;
    }

    public String getEditorAffi() {
        return editorAffi;
    }

    public String getEditorAffiCity() {
        return editorAffiCity;
    }

    public String getEditorAffiProvidence() {
        return editorAffiProvidence;
    }

    public String getEditorAffiCorpDiv() {
        return editorAffiCorpDiv;
    }

    public String getFirstEditorAffiliation() {
        StringBuffer sb = new StringBuffer();
        if (editorAffiCountry != null && editorAffiCountry.length() > 0) {
            sb.append("<" + eiEditorAffiCountryTag + "><![CDATA[" + editorAffiCountry + "]]></" + eiEditorAffiCountryTag + ">");
        }

        if (editorAssoState != null && editorAssoState.length() > 0) {
            sb.append("<" + eiEditorAssoStateTag + "><![CDATA[" + editorAssoState + "]]></" + eiEditorAssoStateTag + ">");
        }

        if (editorAffi != null && editorAffi.length() > 0) {
            sb.append("<" + eiEditorAffiTag + "><![CDATA[" + editorAffi + "]]></" + eiEditorAffiTag + ">");
        }

        if (editorAffiCity != null && editorAffiCity.length() > 0) {
            sb.append("<" + eiEditorAffiCityTag + "><![CDATA[" + editorAffiCity + "]]></" + eiEditorAffiCityTag + ">");
        }

        if (editorAffiProvidence != null && editorAffiProvidence.length() > 0) {
            sb.append("<" + eiEditorAffiProvidenceTag + "><![CDATA[" + editorAffiProvidence + "]]></" + eiEditorAffiProvidenceTag + ">");
        }

        if (editorAffiCorpDiv != null && editorAffiCorpDiv.length() > 0) {
            sb.append("<" + eiEditorAffiCorpDivTag + "><![CDATA[" + editorAffiCorpDiv + "]]></" + eiEditorAffiCorpDivTag + ">");
        }

        return sb.toString();
    }

    public String getSerialTitle() {
        return serialTitle;
    }

    public String getAbbreviatedSerialTitle() {
        return abbreviatedSerialTitle;
    }

    public String getVolume() {
        return volume;
    }

    public String getVolumeNo() {
        StringBuffer retStr = new StringBuffer();
        String tmpNum = getVolume();
        if (tmpNum != null) {

            for (int x = 0; x < numberPatterns.length; ++x) {
                String pattern = numberPatterns[x];
                if (perl.match(pattern, tmpNum)) {
                    MatchResult mResult = perl.getMatch();
                    retStr.append(mResult.toString());
                    break;
                }
            }

            return retStr.toString();
        }

        return null;
    }

    public String getVolumeTitle() {
        return volumeTitle;
    }

    public String getIssue() {
        return issue;
    }

    public String getIssueNo() {
        StringBuffer retStr = new StringBuffer();
        String tmpNum = getIssue();
        if (tmpNum != null) {
            for (int x = 0; x < numberPatterns.length; ++x) {
                String pattern = numberPatterns[x];
                if (perl.match(pattern, tmpNum)) {
                    MatchResult mResult = perl.getMatch();
                    retStr.append(mResult.toString());
                    break;
                }
            }
            return retStr.toString();
        }

        return null;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getMonographTitle() {
        return monographTitle;
    }

    public String getAbbreviatedMonographTitle() {
        return abbreviatedMonographTitle;
    }

    public String getISSN() {
        if (ISSN != null) {
            if (ISSN.length() == 9) {
                return ISSN;
            } else if (ISSN.length() == 8) {
                return ISSN.substring(0, 4) + "-" + ISSN.substring(4, 8);
            }
        }

        return null;
    }

    public String getISSN2() {
        if (ISSN != null) {
            if (ISSN.length() == 9) {
                return ISSN.substring(0, 4) + ISSN.substring(5, 9);
            } else if (ISSN.length() == 8) {
                return ISSN;
            }
        }

        return null;
    }

    public String getISBN() {
        return ISBN;
    }

    public String getPages() {
        return pages;
    }

    public String getNumberOfVolumes() {
        return numberOfVolumes;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getLanguage() {
        return language;
    }

    public String getPaperNumber() {
        return paperNumber;
    }

    public String getCoden() {
        return coden;
    }

    public String getTreatment() {
        return treatment;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public String getAbstractType() {
        return abstractType;
    }

    public String getNumberOfReferences() {
        return getNumberOfReferences(numberOfReferences);
    }

    public String getDatabase() {
        return database;
    }

    public DocID getDocID() {
        return docid;
    }

    public String getHandle() {
        return handle;
    }

    public String getPublisherCity() {
        return publisherCity;
    }

    public String getPublisherProvince() {
        return publisherProvince;
    }

    public String getPublisherState() {
        return publisherState;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public String getPublisherCountry() {
        return publisherCountry;
    }

    public String getPublisherOrderNum() {
        return publisherOrderNum;
    }

    public String getPublisher() {
        StringBuffer sb = new StringBuffer();
        if (publisherName != null && publisherName.length() > 0) {
            sb.append("<" + eiPublisherNameTag + "><![CDATA[" + publisherName + "]]></" + eiPublisherNameTag + ">");
        }

        if (publisherOrderNum != null && publisherOrderNum.length() > 0) {
            sb.append("<" + eiPublisherOrderNumTag + "><![CDATA[" + publisherOrderNum + "]]></" + eiPublisherOrderNumTag + ">");
        }

        if ((publisherCity != null && publisherCity.length() > 0) || (publisherState != null && publisherState.length() > 0)
            || (publisherProvince != null && publisherProvince.length() > 0) || (publisherCountry != null && publisherCountry.length() > 0)) {
            sb.append("<" + eiPublisherLocationTag + ">");
            sb.append("<" + eiPublisherCityTag + "><![CDATA[" + getPublisherCity() + "]]></" + eiPublisherCityTag + ">");
            sb.append("<" + eiPublisherStateTag + "><![CDATA[" + getPublisherState() + "]]></" + eiPublisherStateTag + ">");
            sb.append("<" + eiPublisherProvinceTag + "><![CDATA[" + getPublisherProvince() + "]]></" + eiPublisherProvinceTag + ">");
            sb.append("<" + eiPublisherCountryTag + "><![CDATA[" + getPublisherCountry() + "]]></" + eiPublisherCountryTag + ">");
            sb.append("</" + eiPublisherLocationTag + ">");
        }

        return sb.toString();
    }

    public String getConferenceState() {
        return conferenceState;
    }

    public String getConferenceDate() {
        return conferenceDate;
    }

    public String getConferenceName() {
        return conferenceName;
    }

    public String getConferenceCountry() {
        return conferenceCountry;
    }

    public String getConferenceCode() {
        return conferenceCode;
    }

    public String getConferenceCity() {
        return conferenceCity;
    }

    public String getConferenceProvince() {
        return conferenceProvince;
    }

    public String getConference() {
        StringBuffer sb = new StringBuffer();
        if (conferenceName != null && conferenceName.length() > 0) {
            sb.append("<" + eiConferenceNameTag + "><![CDATA[" + conferenceName + "]]></" + eiConferenceNameTag + ">");
        }

        if (conferenceDate != null && conferenceDate.length() > 0) {
            sb.append("<" + eiConferenceDateTag + "><![CDATA[" + conferenceDate + "]]></" + eiConferenceDateTag + ">");
        }

        if (conferenceCode != null && conferenceCode.length() > 0) {
            sb.append("<" + eiConferenceCodeTag + "><![CDATA[" + conferenceCode + "]]></" + eiConferenceCodeTag + ">");
        }

        if ((conferenceCity != null && conferenceCity.length() > 0) || (conferenceState != null && conferenceState.length() > 0)
            || (conferenceProvince != null && conferenceProvince.length() > 0) || (conferenceCountry != null && conferenceCountry.length() > 0)) {
            sb.append("<" + eiConferenceLocationTag + ">");
            sb.append("<" + eiConferenceCityTag + "><![CDATA[" + conferenceCity + "]]></" + eiConferenceCityTag + ">");
            sb.append("<" + eiConferenceStateTag + "><![CDATA[" + conferenceState + "]]></" + eiConferenceStateTag + ">");
            sb.append("<" + eiConferenceProvinceTag + "><![CDATA[" + conferenceProvince + "]]></" + eiConferenceProvinceTag + ">");
            sb.append("<" + eiConferenceCountryTag + "><![CDATA[" + conferenceCountry + "]]></" + eiConferenceCountryTag + ">");
            sb.append("</" + eiConferenceLocationTag + ">");
        }

        return sb.toString();
    }

    public String getSponsorState() {
        return sponsorState;
    }

    public String getSponsorDepartment() {
        return sponsorDepartment;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public String getSponsorCountry() {
        return sponsorCountry;
    }

    public String getSponsorCity() {
        return sponsorCity;
    }

    public String getSponsorProvince() {
        return sponsorProvince;
    }

    public String getSponsor() {
        StringBuffer sb = new StringBuffer();
        if (sponsorName != null && sponsorName.length() > 0) {
            sb.append("<" + eiSponsorNameTag + "><![CDATA[" + sponsorName + "]]></" + eiSponsorNameTag + ">");
        }

        if ((sponsorCity != null && sponsorCity.length() > 0) || (sponsorState != null && sponsorState.length() > 0)
            || (sponsorProvince != null && sponsorProvince.length() > 0) || (sponsorCountry != null && sponsorCountry.length() > 0)) {
            // sb.append("<"+eiSponsorDateTag+">"+ getSponsorDepartment() +"</"+eiSponsorDateTag+">");
            sb.append("<" + eiSponsorLocationTag + ">");
            sb.append("<" + eiSponsorCityTag + "><![CDATA[" + sponsorCity + "]]></" + eiSponsorCityTag + ">");
            sb.append("<" + eiSponsorStateTag + "><![CDATA[" + sponsorState + "]]></" + eiSponsorStateTag + ">");
            sb.append("<" + eiSponsorProvinceTag + "><![CDATA[" + sponsorProvince + "]]></" + eiSponsorProvinceTag + ">");
            sb.append("<" + eiSponsorCountryTag + "><![CDATA[" + sponsorCountry + "]]></" + eiSponsorCountryTag + ">");
            sb.append("</" + eiSponsorLocationTag + ">");
        }

        return sb.toString();

    }

    public String getControlledTerms() {
        return getControlledTerms(controlledTerms);
    }

    public String getClassificationCodes() {
        return getClassificationCodes(classificationCode);
    }

    public String getUnControlledTerms() {
        return getUnControlledTerms(unControlledTerms);
    }

    public String getEiMainHeading() {
        return eiMainHeading;
    }

    public String getAuthorForLocalHolding() {
        return getAuthorForLocalHolding(authors);
    }

    public String getDocIDXml() {
        StringBuffer sb = new StringBuffer();
        sb.append("<" + eiHitIndexTag + "><![CDATA[" + docid.getHitIndex() + "]]></" + eiHitIndexTag + ">");
        sb.append("<" + eiDocidTag + "><![CDATA[" + docid.getDocID() + "]]></" + eiDocidTag + ">");
        sb.append("<" + eiDatabaseTag + "><![CDATA[" + docid.getDatabase().getID() + "]]></" + eiDatabaseTag + ">");
        return sb.toString();
    }

    /**
     * this method compares the two objects based on the doc id @ return : true , if the two objects are equal : false , if the two objects are not equal.
     */
    public boolean equals(Object object) throws ClassCastException {
        if (object == null) {
            return false;
        } else {
            CPXDocument docObj = (CPXDocument) object;
            if (((DocID) this.getDocID()).equals(((DocID) docObj.getDocID())))
                return true;
            else
                return false;
        }
    }

    /**
     * Compares the Object with the specified object for order
     *
     * @exception ClassCastException
     * @return 0 if the objects match
     */
    public int compareTo(Object object) throws ClassCastException {
        if (object == null) {
            return -1;
        } else {
            CPXDocument docObj = (CPXDocument) object;
            String sObjString = docObj.getDocID().toString();
            String sThisString = this.getDocID().toString();
            return sThisString.compareTo(sObjString);
        }
    }

    /**
     * This method returns the xml formatted string of the Document
     *
     * @return : String
     */
    public void toXML(Writer out) throws IOException {
        out.write("<" + eiRootDocumentTag + ">");

        out.write(includeXML(getAuthors(), eiAuthorsTag));
        out.write(includeXML(getFirstAuthorAffiliation(), eiFirstAuthorAffiliationTag));
        out.write(includeCDataXML(getArticleTitle(), eiArticleTitleTag));
        out.write(includeCDataXML(getTitleOfTranslation(), eiTitleOfTranslationTag));
        out.write(includeXML(getEditors(), eiEditorsTag));
        out.write(includeXML(getFirstEditorAffiliation(), eiFirstEditorAffiliationTag));

        out.write(includeCDataXML(getSerialTitle(), eiSerialTitleTag));
        out.write(includeCDataXML(getAbbreviatedSerialTitle(), eiAbbreviatedSerialTitleTag));
        out.write(includeCDataXML(getVolume(), eiVolumeTag));
        out.write(includeCDataXML(getVolumeTitle(), eiVolumeTitleTag));
        out.write(includeCDataXML(getIssue(), eiIssueTag));
        out.write(includeCDataXML(getIssueDate(), eiIssueDateTag));
        out.write(includeCDataXML(getMonographTitle(), eiMonographTitleTag));
        out.write(includeCDataXML(getAbbreviatedMonographTitle(), eiAbbreviatedMonographTitleTag));

        out.write(includeCDataXML(getISSN(), eiISSNTag));
        out.write(includeCDataXML(getNumberOfVolumes(), eiNumberOfVolumesTag));
        out.write(includeCDataXML(getPublicationDate(), eiPublicationDateTag));
        out.write(includeCDataXML(getISBN(), eiISBNTag));
        out.write(includeCDataXML(getPages(), eiPagesTag));
        out.write(includeCDataXML(getPaperNumber(), eiPaperNumberTag));
        out.write(includeCDataXML(getCoden(), eiCodenTag));
        out.write(includeCDataXML(getLanguage(), eiLanguageTag));
        out.write(includeCDataXML(getTreatment(), eiTreatmentTag));
        out.write(includeCDataXML(getDocumentType(), eiDocumentTypeTag));

        out.write(includeCDataXML(getAbstractType(), eiAbstractTypeTag));
        out.write(includeCDataXML(getAbstractText(), eiAbstractTextTag));
        out.write(includeCDataXML(getNumberOfReferences(), eiNumberOfReferencesTag));
        out.write(includeCDataXML(getDatabase(), eiDatabaseTag));
        out.write(includeXML(getDocIDXml(), eiDocidTag));
        out.write(includeCDataXML(getHandle(), eiHandleTag));
        out.write(includeXML(getConference(), eiConferenceTag));

        out.write(includeXML(getPublisher(), eiPublisherTag));
        out.write(includeXML(getSponsor(), eiSponsorTag));
        out.write(includeXML(getClassificationCodes(), eiClassificationCodesTag));
        out.write(includeXML(getUnControlledTerms(), eiUnControlledTermsTag));
        out.write(includeXML(getControlledTerms(), eiControlledTermsTag));
        out.write(includeCDataXML(getEiMainHeading(), eiEiMainHeadingTag));

        out.write(includeCDataXML(getPageRange(), eiPageRangeTag));
        out.write(includeCDataXML(getPrimaryCalCode(), eiPrimaryCalCodeTag));
        out.write(includeCDataXML(getAccessionNumber(), eiAccessionNumberTag));
        out.write(getAuthorForLocalHolding());

        out.write(buildIVIP());
        // System.out.println(buildIVIP());

        out.write("</" + eiRootDocumentTag + ">");

    }

    /**
     * This method provides the String format of EIDocument.
     *
     * @return String
     */
    public String toString() {
        StringBuffer journalString = new StringBuffer();
        try {
            journalString.append("<" + eiRootDocumentTag + ">");

            journalString.append(includeXML(getAuthors(), eiAuthorsTag));
            journalString.append(includeXML(getFirstAuthorAffiliation(), eiFirstAuthorAffiliationTag));
            journalString.append(includeCDataXML(getArticleTitle(), eiArticleTitleTag));
            journalString.append(includeCDataXML(getTitleOfTranslation(), eiTitleOfTranslationTag));
            journalString.append(includeXML(getEditors(), eiEditorsTag));
            journalString.append(includeXML(getFirstEditorAffiliation(), eiFirstEditorAffiliationTag));

            journalString.append(includeCDataXML(getSerialTitle(), eiSerialTitleTag));
            journalString.append(includeCDataXML(getAbbreviatedSerialTitle(), eiAbbreviatedSerialTitleTag));
            journalString.append(includeCDataXML(getVolume(), eiVolumeTag));
            journalString.append(includeCDataXML(getVolumeTitle(), eiVolumeTitleTag));
            journalString.append(includeCDataXML(getIssue(), eiIssueTag));
            journalString.append(includeCDataXML(getIssueDate(), eiIssueDateTag));
            journalString.append(includeCDataXML(getMonographTitle(), eiMonographTitleTag));
            journalString.append(includeCDataXML(getAbbreviatedMonographTitle(), eiAbbreviatedMonographTitleTag));

            journalString.append(includeCDataXML(getISSN(), eiISSNTag));
            journalString.append(includeCDataXML(getNumberOfVolumes(), eiNumberOfVolumesTag));
            journalString.append(includeCDataXML(getPublicationDate(), eiPublicationDateTag));
            journalString.append(includeCDataXML(getISBN(), eiISBNTag));
            journalString.append(includeCDataXML(getPages(), eiPagesTag));
            journalString.append(includeCDataXML(getPaperNumber(), eiPaperNumberTag));
            journalString.append(includeCDataXML(getCoden(), eiCodenTag));
            journalString.append(includeCDataXML(getLanguage(), eiLanguageTag));
            journalString.append(includeCDataXML(getTreatment(), eiTreatmentTag));
            journalString.append(includeCDataXML(getDocumentType(), eiDocumentTypeTag));

            journalString.append(includeCDataXML(getAbstractType(), eiAbstractTypeTag));
            journalString.append(includeCDataXML(getAbstractText(), eiAbstractTextTag));
            journalString.append(includeCDataXML(getNumberOfReferences(), eiNumberOfReferencesTag));
            journalString.append(includeCDataXML(getDatabase(), eiDatabaseTag));
            journalString.append(includeXML(getDocIDXml(), eiDocidTag));
            journalString.append(includeCDataXML(getHandle(), eiHandleTag));
            journalString.append(includeXML(getConference(), eiConferenceTag));

            journalString.append(includeXML(getPublisher(), eiPublisherTag));
            journalString.append(includeXML(getSponsor(), eiSponsorTag));
            journalString.append(includeXML(getClassificationCodes(), eiClassificationCodesTag));
            journalString.append(includeXML(getUnControlledTerms(), eiUnControlledTermsTag));
            journalString.append(includeXML(getControlledTerms(), eiControlledTermsTag));
            journalString.append(includeCDataXML(getEiMainHeading(), eiEiMainHeadingTag));

            journalString.append(includeCDataXML(getPageRange(), eiPageRangeTag));
            journalString.append(includeCDataXML(getPrimaryCalCode(), eiPrimaryCalCodeTag));
            journalString.append(includeCDataXML(getAccessionNumber(), eiAccessionNumberTag));
            journalString.append(getAuthorForLocalHolding());

            journalString.append("</" + eiRootDocumentTag + ">");
        } catch (Exception e) {

        }

        return journalString.toString();
    }

    /**
     * This method returns the authors in xml format.
     *
     * @return Authors
     */

    /**
     * This method returns the authors in xml format.
     *
     * @return Authors
     */

    private String getAuthors(String authors) throws IOException {
        StringBuffer xmlAuthStr = new StringBuffer();
        String retStr = "";

        if (authors != null && authors.length() > 0) {
            authors = new String(perl.substitute("s/\\(Ed\\. \\)/ /ig", authors));
            authors = new String(perl.substitute("s/\\(Ed\\.\\)/ /ig", authors));
            AuthorStream aStream = new AuthorStream(new ByteArrayInputStream(authors.getBytes()));
            String author = null;
            while ((author = aStream.readAuthor()) != null) {
                xmlAuthStr.append("<").append(eiAuthorTag).append(">").append("<![CDATA[").append(author).append("]]>").append("</").append(eiAuthorTag)
                    .append(">");
            }
        }
        return xmlAuthStr.toString();
    }

    private String getAuthorForLocalHolding(String Authors) {
        StringBuffer xmlAuthStr = new StringBuffer();
        String fullName = "";
        String firstName = "";
        String lastName = "";
        if (Authors != null) {
            StringTokenizer st = new StringTokenizer(Authors, ";");
            if (st.hasMoreTokens()) {
                fullName = st.nextToken();
            } else {
                fullName = Authors;
            }
            xmlAuthStr.append("<AUTHOR-FULLNAME>");
            xmlAuthStr.append("<![CDATA[");
            xmlAuthStr.append(fullName);
            xmlAuthStr.append("]]>");
            xmlAuthStr.append("</AUTHOR-FULLNAME>");
            StringTokenizer st1 = new StringTokenizer(fullName, ",");
            if (st1.countTokens() >= 1) {
                firstName = st1.nextToken();
                xmlAuthStr.append("<AUTHOR-FIRSTNAME>");
                xmlAuthStr.append("<![CDATA[");
                xmlAuthStr.append(firstName);
                xmlAuthStr.append("]]>");
                xmlAuthStr.append("</AUTHOR-FIRSTNAME>");
                if (st1.countTokens() == 1) {
                    lastName = st1.nextToken();
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

    private String getEditors(String editors) throws IOException {

        StringBuffer xmlEditStr = new StringBuffer();

        if (editors != null && editors.length() > 0) {
            editors = new String(perl.substitute("s/\\(Ed\\. \\)/ /ig", editors));
            editors = new String(perl.substitute("s/\\(Ed\\.\\)/ /ig", editors));
            AuthorStream aStream = new AuthorStream(new ByteArrayInputStream(editors.getBytes()));
            String editor = null;
            while ((editor = aStream.readAuthor()) != null) {
                xmlEditStr.append("<").append(eiEditorTag).append(">").append("<![CDATA[").append(editor).append("]]>").append("</").append(eiEditorTag)
                    .append(">");
            }
        }

        return xmlEditStr.toString();
    }

    /**
     * This method returns Numeric Classification Codes
     *
     * @return Numeric Classification Codes
     */
    private String getClassificationCodes(String classCodes) {
        StringBuffer tempStr = new StringBuffer();
        if (classCodes != null && classCodes.length() > 0) {
            StringTokenizer stoken = new StringTokenizer(classCodes, ";");
            while (stoken.hasMoreTokens()) {
                String str = stoken.nextToken();
                tempStr.append("<").append(eiClassificationCodeTag).append(">").append("<![CDATA[").append(str).append("]]>").append("</")
                    .append(eiClassificationCodeTag).append(">");
            }
        }

        return tempStr.toString();
    }

    /**
     * This method returns Controlled Terms
     *
     * @return Controlled Terms
     **/

    private String getControlledTerms(String conTerms) {
        StringBuffer tempStr = new StringBuffer();
        if (conTerms != null) {
            StringTokenizer stoken = new StringTokenizer(conTerms, ";");
            while (stoken.hasMoreTokens()) {
                String str = stoken.nextToken();
                tempStr.append("<").append(eiControlledTermTag).append(">").append("<![CDATA[").append(str).append("]]>").append("</")
                    .append(eiControlledTermTag).append(">");
            }
        } else {
            tempStr.append("<").append(eiControlledTermTag).append("></").append(eiControlledTermTag).append(">");
        }
        return tempStr.toString();
    }

    /**
     * This method returns Uncontrolled Terms.
     *
     * @return Uncontrolled Terms.
     */
    private String getUnControlledTerms(String unConTerms) {
        StringBuffer tempStr = new StringBuffer();
        if (unConTerms != null && unConTerms.length() > 0) {
            StringTokenizer stoken = new StringTokenizer(unConTerms, ";");
            while (stoken.hasMoreTokens()) {
                String str = stoken.nextToken();
                tempStr.append("<").append(eiUnControlledTermTag).append(">").append("<![CDATA[").append(str).append("]]>").append("</")
                    .append(eiUnControlledTermTag).append(">");
            }
        }

        return tempStr.toString();
    }

    private String getNumberOfReferences(String refs) {
        String str = "";
        if (!(refs == null || refs.trim().equals("")) == true) {
            StringTokenizer st = new StringTokenizer(refs, " ");
            str = st.nextToken();
        }
        return str;
    }

    private String includeXML(String methodResult, String methodTag) {

        if ((methodResult == null) || (methodResult.trim().equals(""))) {
            return "";
        } else {
            String str = "<" + methodTag + ">" + methodResult + "</" + methodTag + ">";
            return str;
        }

    }

    private String includeCDataXML(String methodResult, String methodTag) {

        if ((methodResult == null) || (methodResult.trim().equals(""))) {
            return "";
        } else {
            String str = "<" + methodTag + "><![CDATA[" + methodResult + "]]></" + methodTag + ">";
            return str;
        }

    }

    /**
     * This method returns IVIP
     *
     * @return IVIP format eg:<IVIP ISSN="abc" firstPage="11" firstVolume="11" />
     */
    private String buildIVIP() {
        StringBuffer tempStr = new StringBuffer();
        tempStr.append("<IVIP");
        tempStr.append(buildISSN());
        tempStr.append(buildFirstVolume());
        tempStr.append(buildFirstIssue());
        tempStr.append(buildFirstPage());
        tempStr.append(">");
        tempStr.append("</IVIP>");
        return tempStr.toString();
    }

    /**
     * This method returns ISSN part of IVIP ISSN format is abcd-xyz which is processed by this method Removes "-" and returns abcdxyz.
     *
     * @return tempStr
     */
    private String buildISSN() {

        StringBuffer tempISSN = new StringBuffer();

        tempISSN.append(" ISSN=").append("\"").append(getISSN()).append("\"");
        return tempISSN.toString();
    }

    /**
     * This method returns First Volume part of IVIP VIP format is volume/issue (firstpage-lastpage) returns the volume part of the above mentioned format.
     *
     * @return retStr
     */
    private String buildFirstVolume() {
        StringBuffer retStr = new StringBuffer();
        retStr.append(" firstVolume=\"");
        String tmpNum = getVolume();

        for (int x = 0; x < numberPatterns.length; ++x) {
            String pattern = numberPatterns[x];
            if (perl.match(pattern, tmpNum)) {
                MatchResult mResult = perl.getMatch();
                retStr.append(mResult.toString());
                break;
            }
        }
        retStr.append("\"");
        return retStr.toString();
    }

    /**
     * This method returns First Isuue part of IVIP VIP format is volume/issue (firstpage-lastpage) returns the volume part of the above mentioned format.
     *
     * @return retStr
     */
    private String buildFirstIssue() {
        StringBuffer retStr = new StringBuffer();
        retStr.append(" firstIssue=\"");
        String tmpNum = getIssue();
        for (int x = 0; x < numberPatterns.length; ++x) {
            String pattern = numberPatterns[x];
            if (perl.match(pattern, tmpNum)) {
                MatchResult mResult = perl.getMatch();
                retStr.append(mResult.toString());
                break;
            }
        }
        retStr.append("\"");
        return retStr.toString();
    }

    /**
     * This method returns First Page part of IVIP VIP format is volume/issue (firstpage-lastpage) returns the firstpage part of the above mentioned format.
     *
     * @return retStr
     */
    private String buildFirstPage() {
        StringBuffer retStr = new StringBuffer();
        retStr.append(" firstPage=\"");
        String firstPage = null;

        StringTokenizer tmpPage = new StringTokenizer(getPageRange(), "-");
        if (tmpPage.countTokens() > 0) {
            firstPage = tmpPage.nextToken();
        } else {
            firstPage = getPageRange();
        }

        for (int x = 0; x < numberPatterns.length; ++x) {
            String pattern = numberPatterns[x];
            if (perl.match(pattern, firstPage)) {
                MatchResult mResult = perl.getMatch();
                retStr.append(mResult.toString());
                break;
            }
        }
        retStr.append("\"");
        return retStr.toString();

    }

    public String getLocalHoldingLink(String URL) {

        if (URL == null) {
            return null;
        }

        for (int i = 0; i < LocalHoldingLinker.localHoldingFields.length; ++i) {
            URL = sUtil.replace(URL, LocalHoldingLinker.localHoldingFields[i], getDataForLocalHolding(LocalHoldingLinker.localHoldingFields[i]),
                StringUtil.REPLACE_GLOBAL, StringUtil.MATCH_CASE_SENSITIVE);
        }

        return URL;
    }

    private String getDataForLocalHolding(String field) {
        String value = null;

        if (field.equals(LocalHoldingLinker.AULAST)) {
            value = notNull(getFirstAuthorLN());
        } else if (field.equals(LocalHoldingLinker.AUFIRST)) {
            value = notNull(getFirstAuthorFN());
        } else if (field.equals(LocalHoldingLinker.AUFULL)) {
            value = notNull(getFirstAuthor());
        } else if (field.equals(LocalHoldingLinker.ISSN)) {
            value = notNull(getISSN2());
        } else if (field.equals(LocalHoldingLinker.ISSN9)) {
            value = notNull(getISSN());
        } else if (field.equals(LocalHoldingLinker.CODEN)) {
            value = notNull(getCoden());
        } else if (field.equals(LocalHoldingLinker.TITLE)) {
            value = notNull(getSerialTitle());
        } else if (field.equals(LocalHoldingLinker.STITLE)) {
            value = notNull(getAbbreviatedSerialTitle());
        } else if (field.equals(LocalHoldingLinker.ATITLE)) {
            value = notNull(getArticleTitle());
        } else if (field.equals(LocalHoldingLinker.VOLUME) || field.equals(LocalHoldingLinker.VOLUME2)) {
            value = notNull(getVolumeNo());
        } else if (field.equals(LocalHoldingLinker.ISSUE) || field.equals(LocalHoldingLinker.ISSUE2)) {
            value = notNull(getIssueNo());
        } else if (field.equals(LocalHoldingLinker.SPAGE)) {
            value = notNull(getStartPage());
        } else if (field.equals(LocalHoldingLinker.EPAGE)) {
            value = notNull(getEndPage());
        } else if (field.equals(LocalHoldingLinker.PAGES)) {
            value = notNull(getPageRange());
        } else if (field.equals(LocalHoldingLinker.YEAR)) {
            value = notNull(getYear());
        }

        if (value != null) {
            value = URLEncoder.encode(value);
        } else {
            return "";
        }

        return value;
    }

    public String notNull(String s) {
        if (s == null) {
            return "";
        }

        return s;
    }
}
