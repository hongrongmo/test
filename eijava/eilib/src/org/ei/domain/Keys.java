package org.ei.domain;



public interface Keys
{
    /*

        Below are a list of the standard keys in the systems sorted alpha
    */
    public static Key BOOK_TITLE = new Key("BTI", "Book title");
	public static Key BOOK_PAGE = new Key("BPP", "Page number");
	public static Key BOOK_PII = new Key("PII", "PII");
	public static Key BOOK_PAGE_COUNT = new Key("BPC", "Page count");
	public static Key BOOK_YEAR = new Key("BYR", "Year");
	public static Key BOOK_PUBLISHER = new Key("BPN", "Publisher");
	public static Key BOOK_PAGE_KEYWORDS = new Key("BKYS", "Keywords");
	public static Key BOOK_PAGE_KEYWORD = new Key("BKY", "Keyword");
	public static Key BOOK_DESCRIPTION = new Key("BAB", "Book description");
	public static Key BOOK_CHAP_START = new Key("BSPG", "Chapter start");

    public static final Key ABBRV_MON_MONOGRAPH_TITLE  = new Key("ME", "");
    public static final Key ABBRV_SERIAL_TITLE = new Key("SE", "Abbreviated serial title");
    public static final Key ABSTRACT = new Key("AB", "Abstract");
    public static final Key ABSTRACT2 = new Key("AB2","Abstract");
    public static final Key ABSTRACT_TYPE = new Key("AT","Abstract type");
    public static final Key ACCESSION_NUMBER  = new Key("AN", "Accession number");
    public static final Key AGENCY = new Key("AG","Agency");
    public static final Key ARTICLE_NUMBER = new Key("ARTICLE_NUMBER","Article number");
    public static final Key ASOURCES = new Key ("ASO", "Additional sources");
    public static final Key ASSISTANT_EXAMINER = new Key("AE", "Assistant examiner");
    public static final Key ASTRONOMICAL_OBJECT_INDEX = new Key("AI");
    public static final Key ASTRONOMICAL_OBJECT_INDEXING = new Key("AOI","Astronomical object indexing");
    public static final Key ACRONYM_DEFINITION = new Key("AO","Acronym definition");
    public static final Key ATTERNEY = new Key("ATY");
    public static final Key ATTERNEY_COUNTRY = new Key("ATY_CTRY");
    public static final Key AUTHORS = new Key("AUS", "Authors");
    public static final Key AUTHOR_AFFS = new Key("AFS", "Author affiliation");
    public static final Key AUTH_CODE = new Key("AUTHCD","Patent authority"); // pat num
    public static final Key AVAILABILITY = new Key("AV","Availability");
    public static final Key CAS_REGISTRY_CODE = new Key("CR");
    public static final Key CAS_REGISTRY_CODES = new Key("CRS", "CAS registry number(s)");
    public static final Key CONF_EDITORS = new Key("CFE", "Conference editor(s)");
    public static final Key CONF_EDITORS_AF = new Key("CFA", "Conference editor affiliations");
    public static final Key CORRESPONDING_AUTHORS = new Key("CAUS", "Corr. author");
    public static final Key CORRESPONDING_AUTHORS_AFF = new Key("CAF", "Corr. author affiliation");
    public static final Key CORRESPONDING_EMAIL = new Key("CEML", "Corr. author email");
    public static final Key CHEMICAL_DATA_INDEX = new Key("CI");
    public static final Key CHEMICAL_DATA_INDEXING = new Key("CHI","Chemical indexing");
	public static final Key COMPANIES = new Key("CPO","Companies");
    public static final Key CITCNT = new Key("CCT");
    public static final Key CLASS_CODE = new Key("CL");
    public static final Key ELT_CLASS_CODE = new Key("ECL");
    public static final Key CLASS_CODES = new Key("CLS");
    public static final Key CODEN = new Key("CN","CODEN");
    public static final Key CONFERENCE_NAME  = new Key("CF","Conference name");
    public static final Key CONF_CODE = new Key("CC", "Conference code");
    public static final Key CONF_DATE = new Key("MD", "Conference date");;
    public static final Key CONTRACTIN_STATES = new Key("ASG_CST");
    public static final Key CONTRACT_NUMBERS = new Key("CTS","Contract numbers");
    public static final Key CONTROLLED_TERMS = new Key("CVS");
    public static final Key CONTROLLED_TERM = new Key("CV");
    public static final Key COPYRIGHT = new Key("CPR", "Copyright");
    public static final Key COPYRIGHT_TEXT = new Key("CPRT");
    public static final Key CORPORATE_SOURCE_CODES = new Key("CAC","Corporate source codes");
    public static final Key COUNTRY = new Key("CO","Country");
    public static final Key COUNTRY_OF_PUB = new Key("CPUB", "Country of publication");
	public static final Key COUNTRY_CODES = new Key("SCC", "Geographical indexing");
	public static final Key CHEMICALS = new Key("CMS", "Chemicals");
	public static final Key CHEMICAL_ACRONS = new Key("CES", "Chemical Acronyms");
    public static final Key DERWENT_NO = new Key("DERW", "DERWENT accession no.");
    public static final Key DESIGNATED_STATES = new Key("DSM", "Designated states");
    public static final Key DISCIPLINE = new Key("DISP");
    public static final Key DISCIPLINES = new Key("DISPS","Discipline");
    public static final Key DOCID = new Key("DOC-ID");
    public static final Key DOC_TYPE = new Key("DT", "Document type");
    public static final Key DOC_URL = new Key("URL");
    public static final Key DOI = new Key("DO", "DOI");
    public static final Key DOMESTIC_APPNUM = new Key("DAN");
    public static final Key EDITORS = new Key("EDS", "Editors");
    public static final Key EDITOR_AFFS = new Key("EFS", "First editor affiliation");
    public static final Key END_PAGE= new Key("EPG");
    public static final Key EUROPCL_CODE = new Key("PECM","ECLA Code" );
    public static final Key EUR_PATCLASSES = new Key("ECC", "European patent classes");
    public static final Key FIELD_OF_SEARCH = new Key("FSM", "Field of search");
    public static final Key FILING_DATE = new Key("FD");
    public static final Key FURTHER_EUR_PATCLASS = new Key("FEC","Further ECLA Code");
    public static final Key FURTHER_INT_PATCLASS = new Key("FIC", "Further IPC Code");
    public static final Key GLOBAL_TAGS = new Key("GTAGS", "Public tags");
    public static final Key INDEXING_TEMPLATE = new Key("ATM", "Indexing template");
    public static final Key IMAGES = new Key("IMG", "Figures");
    public static final Key INTERNATCL_CODE = new Key("PIDM", "IPC Code");
    public static final Key INTERNATCL_CODE8 = new Key("PIDM8", "IPC-8 Code");
    public static final Key INT_PATCLASSES = new Key("ICC", "Int. patent classes");
    public static final Key INVENTORS = new Key("IVS", "Inventors");
    public static final Key INVENTOR_COUNTRY = new Key("INV_CTRY", "Inventor country");
    public static final Key ISBN  = new Key("BN","ISBN");
    public static final Key ISSN  = new Key("SN","ISSN");
    public static final Key E_ISSN  = new Key("E_ISSN","Electronic ISSN");
    public static final Key ISSUE = new Key("IS", "Issue");
    public static final Key ISSUE_DATE = new Key("SD","Issue date");
    public static final Key ISSUE_DATE_PAPER = new Key("PSD", "Issue date");
    public static final Key ISSUING_ORG = new Key("IORG","Issuing organization");
    public static final Key I_PUBLISHER = new Key("I_PN", "Publisher");
    public static final Key JOURNAL_ANNOUNCEMENT = new Key("VI","Journal announcement");
    public static final Key KIND_CODE = new Key("KC", "Kind");
    public static final Key KIND_DESCRIPTION = new Key("KD");
    public static final Key LANGUAGE = new Key("LA", "Language");
    public static final Key LINKED_SUB_TERM = new Key("LST");
    public static final Key LINKED_TERMS_HOLDER = new Key("LH", "Linked Terms");
    public static final Key LINKED_TERM_GROUP = new Key("LG");
    public static final Key LINKED_TERMS = new Key("LT", "Linked Terms");
    public static final Key LINKING_YEAR = new Key("LYR");
    public static final Key MAIN_HEADING = new Key("MH", "Main heading");
    public static final Key MAJOR_TERMS = new Key("MJS");
    public static final Key MAJOR_TERM = new Key("CVM");
    public static final Key MAJOR_REAGENT_TERM = new Key("CVMA");
    public static final Key MAJOR_PRODUCT_TERM = new Key("CVMP");
    public static final Key MAJOR_NOROLE_TERM = new Key("CVMN");
    public static final Key REAGENT_TERM = new Key("CVA");
    public static final Key PRODUCT_TERM = new Key("CVP");
    public static final Key NUMBER_OF_CLAIMS = new Key("CLAIM", "Number of claims");
    public static final Key NUMBER_OF_TABLES = new Key("NOTAB","Number of tables");
    public static final Key NOROLE_TERM = new Key("CVN");
    public static final Key MANUAL_LINKED_TERMS = new Key("MLT", "Manually linked terms");
    public static final Key MATERIAL_ID = new Key("MI","Material Identity Number");
    public static final Key MEETING_INFO = new Key("MD", "Conference date");
    public static final Key MEETING_LOCATION = new Key("ML", "Conference location");
    public static final Key MONITORING_AGENCIES = new Key("AGS", "Monitoring agencies");
    public static final Key MONOGRAPH_TITLE = new Key("MT", "Monograph title");
    public static final Key NOCONTROLLED_TERMS = new Key("NO_CVS");
    public static final Key NPREFCNT = new Key("NPRCT");
    public static final Key NOTES = new Key("SU","Notes");
    public static final Key NO_SO = new Key("NO_SO");
    public static final Key NUMBER_OF_FIGURES = new Key("NF","Number of figures");
    public static final Key NUMBER_OF_REFERENCES  = new Key("NR","Number of references");
    public static final Key NUMERICAL_DATA_INDEX = new Key("ND");
    public static final Key NUMERICAL_DATA_INDEXING = new Key("NDI","Numerical data indexing");
    public static final Key NUMVOL = new Key("NV");
    public static final Key ORIGINAL_CLASS_CODE = new Key("OCL");
    public static final Key ORIGINAL_CLASS_CODES = new Key("OCLS");
    public static final Key ORIGINAL_CONTROLLED_TERM = new Key("OCV");
    public static final Key ORIGINAL_CONTROLLED_TERMS = new Key("OCVS");
    public static final Key OTHER_INFO = new Key("OINF","Additional information");
    public static final Key PAGE_COUNT= new Key("PC");
    public static final Key PAGE_RANGE= new Key("PP","Pages");
    public static final Key PAGE_RANGE_pp = new Key("PP_pp");
    public static final Key PAPER_NUMBER = new Key("PA","Paper number");
    public static final Key PAP_NUMBER = new Key("PR","Part number");
    public static final Key PAT_ATTORNEY = new Key("ATT", "Attorney, Agent or Firm");
    public static final Key PATAPPNUM = new Key("PAN", "Application number");
    public static final Key PATAPPDATE = new Key("PAPD", "Application date");
    public static final Key PATAPPDATEX = new Key("PAPX", "Application date");
    public static final Key PATAPP_INFO = new Key("PAPIS", "Application information");
    public static final Key PATAPPNUMS = new Key("PANS", "Application  number");
    public static final Key PATAPPNUMUS = new Key("PANUS", "Unstandardized application number");
    public static final Key PATAPPCOUNTRY= new Key("PAPCO", "Application country");
    public static final Key PATASSIGN = new Key("PASM", "Assignee");
    public static final Key PATCOUNTRY = new Key("COPA","Country of application");
    public static final Key PATENT_ISSUE_DATE = new Key("PIDD","Patent issue date");
    public static final Key PATFILDATE = new Key("PFD","Filing date");
    public static final Key PATNUM = new Key("PAP", "Patent number");
    public static final Key PATENT_NUMBER = new Key("PM", "Publication number");
    public static final Key PATENT_NUMBER1 = new Key("PM1", "Publication number"); // for pat refs pages
    public static final Key PATPUBDATE = new Key("PPD");
    public static final Key PERFORMER = new Key("PF", "Author affiliation");
    public static final Key PRICES = new Key("PS");
    public static final Key PRIMARY_EXAMINER = new Key("PEXM", "Primary examiner");
    public static final Key PRIORITY_INFORMATION = new Key("PIM", "Priority information");
    public static final Key PRIVATE_TAGS = new Key("PTAGS", "Private tags");
    public static final Key PROJECT_NUMBER = new Key("PNUM","Project number");
    public static final Key PROVIDER = new Key("PVD", "Provider");
    public static final Key PUBLICATION = new Key("DD");
    public static final Key PUBLICATION_DATE = new Key("PD_YR","Publication date");
    public static final Key PUBLICATION_DATE_PAPER = new Key("PPD_YR", "Publication date");
    public static final Key PUBLICATION_ORDER = new Key("PO");
    public static final Key PUBLICATION_YEAR = new Key("YR", "Publication year");
    public static final Key PUBLICATION_YEAR_PAPER = new Key("PYR", "Publication year");
    public static final Key PUBLICATION_NUMBER = new Key("PUN","Publication number"); // not needed
    public static final Key PUBLISHER = new Key("PN", "Publisher");
    public static final Key PUB_LOCATION = new Key("PL","Country of publication");
    public static final Key PUB_PLACE = new Key("PLA","Place of publication");
    public static final Key REFCNT = new Key("RCT");
	public static final Key SCOPE = new Key("SC","Scope");
	public static final Key SIC_CODES = new Key("ICS","SIC Codes");
	public static final Key INDUSTRIAL_SEC_CODES = new Key("GCI","Industrial Sector Codes");
	public static final Key INDUSTRIAL_SECTORS = new Key("GDI","Industrial Sectors");
    public static final Key REPORT_NUMBER = new Key("RN", "Report number");
    public static final Key REPORT_NUMBER_PAPER = new Key("PRN", "Report number");
    public static final Key RIS_A1 = new Key("A1");
    public static final Key RIS_A2 = new Key("A2");
    public static final Key RIS_AB2 = new Key("AB2");
    public static final Key RIS_AD = new Key("AD");
    public static final Key RIS_AN = new Key("AN");
    public static final Key RIS_AUS = new Key("AUS");
    public static final Key RIS_AV = new Key("AV");
    public static final Key RIS_BT = new Key("BT");
    public static final Key RIS_BN = new Key("BN");
    public static final Key RIS_CY = new Key("CY");
    public static final Key RIS_CVS = new Key("CVS");
    public static final Key RIS_DO = new Key("DO");
    public static final Key RIS_EP = new Key("EP");
    public static final Key RIS_EDS = new Key("EDS");
    public static final Key RIS_FLS = new Key("FLS");
    public static final Key RIS_IS = new Key("IS");
    public static final Key RIS_JO = new Key("JO");
    public static final Key RIS_KW = new Key("KW");
    public static final Key RIS_LA = new Key("LA");
    public static final Key RIS_MD = new Key("MD");
    public static final Key RIS_MT = new Key("BT");
    public static final Key RIS_M1 = new Key("M1");
    public static final Key RIS_M2 = new Key("M2");
    public static final Key RIS_M3 = new Key("M3");
    public static final Key RIS_N1 = new Key("N1");
    public static final Key RIS_N2 = new Key("N2");
    public static final Key RIS_PAT = new Key("PAT");
    public static final Key RIS_PB = new Key("PB");
    public static final Key RIS_PE = new Key("PE");
    public static final Key RIS_PY = new Key("PY");
    public static final Key RIS_SE = new Key("SE");
    public static final Key RIS_SN = new Key("SN");
    public static final Key RIS_SP = new Key("SP");
    public static final Key RIS_S1 = new Key("S1");
    public static final Key RIS_T1 = new Key("T1");
    public static final Key RIS_T2 = new Key("T2");
    public static final Key RIS_T3 = new Key("T3");
    public static final Key RIS_TI = new Key("TI");
    public static final Key RIS_TY = new Key("TY");
    public static final Key RIS_VL = new Key("VL");
    public static final Key RIS_Y1 = new Key("Y1");
    public static final Key RIS_Y2 = new Key("Y2");
    public static final Key RIS_UR = new Key("UR");
    public static final Key RIS_U1 = new Key("U1");
    public static final Key RIS_U2 = new Key("U2");
    public static final Key RIS_U3 = new Key("U3");
    public static final Key RIS_U4 = new Key("U4");
    public static final Key RIS_U5 = new Key("U5");
    public static final Key BIB_TY = new Key("BY");
    public static final Key RN_LABEL = new Key("RN_LABEL","Report number");
    public static final Key RSRCH_SPONSOR = new Key("RSP","Sponsor");
    public static final Key SALUTATION = new Key("TL", "Salutation");
    public static final Key SERIAL_TITLE = new Key("ST","Serial title");
    public static final Key SECONDARY_SOURCE = new Key("SEC", "Secondary source");
    public static final Key SESSION_NAME_NUMBER = new Key("CSESS","Session name number");
    public static final Key SPECIFIC_NAMES = new Key("SPECN","Specific Names");
    public static final Key SOURCE = new Key("SO","Source");
    public static final Key SPONSOR = new Key("SP","Sponsor");
    public static final Key START_PAGE= new Key("SPG");
    public static final Key STT = new Key("STT");
    public static final Key TASK_NUMBER = new Key("TNUM", "Task number");
    public static final Key TITLE = new Key("TI", "Title");
    public static final Key TITLE_ANNOTATION = new Key("TA","Title annotation");
    public static final Key TITLE_TRANSLATION = new Key("TT", "Title of translation");
    public static final Key TRANSLATION_ABBREVIATED_SERIAL_TITLE = new Key("TTJ","Translation abbreviated serial title");
    public static final Key TRANSLATION_CODEN = new Key("CNT","Translation CODEN");
    public static final Key TRANSLATION_COUNTRY_OF_PUB = new Key("CPUBT","Translation country of publication");
    public static final Key TRANSLATION_ISSN = new Key("SNT","Translation ISSN");
    public static final Key TRANSLATION_ISSUE = new Key("ISST","Translation issue");
    public static final Key TRANSLATION_PAGES = new Key("IPNT","Translation pages");
    public static final Key TRANSLATION_PUBLICATION_DATE = new Key("TDATE","Translation publication date");
    public static final Key TRANSLATION_SERIAL_TITLE = new Key("FTTJ","Translation serial title");
    public static final Key TRANSLATION_VOLUME = new Key("VOLT","Translation volume");
    public static final Key TRANSLATION_VOLUME_ISSUE = new Key("VOLISST");
    public static final Key TREATMENT = new Key("TR");
    public static final Key TREATMENTS = new Key("TRS", "Treatment");
    public static final Key TSOURCES = new Key("TSO","Additional translated sources");
    public static final Key UNCONTROLLED_TERMS = new Key("FLS","Uncontrolled terms");
    public static final Key UPAT_PUBDATE = new Key("UPD", "Publication date");
    public static final Key VOLISSUE = new Key("VO");
    public static final Key USCL_CODE = new Key("PUCM","US Classification");
    public static final Key VOLUME = new Key("VOM", "Volume");
    public static final Key VOLUME_TITLE = new Key("VT", "Volume title");
    public static final Key p_PAGE_RANGE = new Key("p_PP");
   	public static final Key SOURCE_TYPE = new Key("SOURCE_TYPE","Source type");
   	public static final Key SOURCE_COUNTRY = new Key("SOURCE_COUNTRY","Source country");
    public static final Key PATENT_PRI  = new Key("PATPRI");
    public static final Key REGION_CONTROLLED_TERMS = new Key("RGIS","Regional terms");
    public static final Key CLASSIFICATION_SUBJECT = new Key("C_SUBJECT","Classification subject");
    public static final Key INDEX_TERM = new Key("CVS","Index terms");
    public static final Key SPECIES_TERMS = new Key("FLS","Species terms");
    public static final Key CORRESPONDENCE_PERSON = new Key("CAUS","Correspondence person");
    public static final Key CORRESPONDENCE_AFFILIATION = new Key("CAFS","Correspondence affiliation");

    public static final Key[] ALL_HIGHLIGHT_KEYS = {Keys.BOOK_PAGE_KEYWORDS, Keys.INDEX_TERM,Keys.SPECIES_TERMS,Keys.CLASSIFICATION_SUBJECT,Keys.REGION_CONTROLLED_TERMS,Keys.TITLE, TITLE_TRANSLATION, Keys.ABSTRACT,Keys.ABSTRACT2,Keys.MAIN_HEADING,Keys.CONTROLLED_TERMS,Keys.ORIGINAL_CONTROLLED_TERMS,Keys.UNCONTROLLED_TERMS,Keys.SERIAL_TITLE,Keys.ASOURCES,Keys.PUBLISHER,Keys.I_PUBLISHER,Keys.MONOGRAPH_TITLE,Keys.SOURCE,Keys.SPONSOR,Keys.CONFERENCE_NAME,Keys.MEETING_LOCATION,Keys.CONF_DATE,Keys.SERIAL_TITLE,Keys.AUTHOR_AFFS,Keys.NOTES,Keys.AVAILABILITY,Keys.COUNTRY,Keys.PERFORMER,Keys.RSRCH_SPONSOR,Keys.INTERNATCL_CODE,Keys.INTERNATCL_CODE8,Keys.USCL_CODE,Keys.EUROPCL_CODE};
    public static final Key[] KY_HIGHLIGHT_KEYS = {Keys.BOOK_PAGE_KEYWORDS, Keys.TITLE,TITLE_TRANSLATION,Keys.ABSTRACT,Keys.ABSTRACT2, Keys.CONTROLLED_TERMS, Keys.ORIGINAL_CONTROLLED_TERMS, Keys.MAIN_HEADING, Keys.UNCONTROLLED_TERMS};
    public static final Key[] AF_HIGHLIGHT_KEYS = {Keys.AUTHOR_AFFS,Keys.PERFORMER,Keys.RSRCH_SPONSOR};
    public static final Key[] CF_HIGHLIGHT_KEYS = {Keys.TITLE,Keys.SPONSOR,Keys.CONFERENCE_NAME,Keys.MEETING_LOCATION,Keys.CONF_DATE,Keys.SERIAL_TITLE};
    public static final Key[] FL_HIGHLIGHT_KEYS = {Keys.UNCONTROLLED_TERMS};
    public static final Key[] ST_HIGHLIGHT_KEYS = {Keys.SERIAL_TITLE,Keys.SOURCE,Keys.ASOURCES};
    public static final Key[] NT_HIGHLIGHT_KEYS = {Keys.NOTES};
    public static final Key[] PN_HIGHLIGHT_KEYS = {Keys.PUBLISHER,Keys.I_PUBLISHER};
    public static final Key[] CV_HIGHLIGHT_KEYS = {Keys.CONTROLLED_TERMS,Keys.ORIGINAL_CONTROLLED_TERMS,Keys.MAIN_HEADING};
    public static final Key[] MH_HIGHLIGHT_KEYS = {Keys.MAIN_HEADING};
    public static final Key[] AV_HIGHLIGHT_KEYS = {Keys.AVAILABILITY};
    public static final Key[] CO_HIGHLIGHT_KEYS = {Keys.COUNTRY};
    public static final Key[] TI_HIGHLIGHT_KEYS = {Keys.TITLE, TITLE_TRANSLATION};
    public static final Key[] AB_HIGHLIGHT_KEYS = {Keys.ABSTRACT,Keys.ABSTRACT2};
    public static final Key[] RGI_HIGHLIGHT_KEYS = {Keys.REGION_CONTROLLED_TERMS};

    public static final Key COLLECTION = new Key("COL","Collection name");
    public static final Key COVER_IMAGE = new Key("CVR","Cover Image");
    public static final Key PAGE_THUMBNAIL = new Key("THMB","Page Thumbnail");
    public static final Key PAGE_THUMBS = new Key("THMBS","Thumbnail Images");

}