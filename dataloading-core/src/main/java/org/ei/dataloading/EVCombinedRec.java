package org.ei.dataloading;

import java.util.Hashtable;

public class EVCombinedRec {
	public static final String AUTHOR = "1";
	public static final String AUTHOR_AFFILIATION = "2";
	public static final String TITLE = "3";
	public static final String TRANSLATED_TITLE = "4";
	public static final String VOLUME_TITLE = "5";
	public static final String ABSTRACT = "6";
	public static final String EDITOR = "7";
	public static final String EDITOR_AFFILIATION = "8";
	public static final String TRANSLATOR = "9";
	public static final String CONTROLLED_TERMS = "10";
	public static final String UNCONTROLLED_TERMS = "11";
	public static final String ISSN = "12";
	public static final String ISSN_OF_TRANSLATION = "13";
	public static final String CODEN = "14";
	public static final String CODEN_OF_TRANSLATION = "15";
	public static final String ISBN = "16";
	public static final String SERIAL_TITLE = "17";
	public static final String SERIAL_TITLE_TRANSLATION = "18";
	public static final String MAIN_HEADING = "19";
	public static final String SUB_HEADING = "20";
	public static final String PUBLISHER_NAME = "21";
	public static final String TREATMENT_CODE = "22";
	public static final String LANGUAGE = "23";
	public static final String DOCTYPE = "24";
	public static final String CLASSIFICATION_CODE = "25";
	public static final String CONFERENCE_CODE = "26";
	public static final String CONFERENCE_NAME = "27";
	public static final String CONFERENCE_LOCATION = "28";
	public static final String MEETING_DATE = "29";
	public static final String SPONSOR_NAME = "30";
	public static final String MONOGRAPH_TITLE = "31";
	public static final String DISCIPLINE = "32";
	public static final String MATERIAL_NUMBER = "33";
	public static final String NUMERICAL_INDEXING = "34";
	public static final String CHEMICAL_INDEXING = "35";
	public static final String ASTRONOMICAL_INDEXING = "36";
	public static final String DOCID = "37";
	public static final String DEDUPKEY = "38";
	public static final String DATABASE = "39";
	public static final String LOAD_NUMBER = "40";
	public static final String PUB_YEAR = "41";
	public static final String ACCESSION_NUMBER = "42";
	public static final String PUB_SORT = "43";
	public static final String REPORTNUMBER = "44";
	public static final String ORDERNUMBER = "45";
	public static final String COUNTRY = "46";
	public static final String VOLUME = "47";
	public static final String ISSUE = "48";
	public static final String STARTPAGE = "49";
	public static final String AVAILABILITY = "50";
	public static final String NOTES = "51";
	public static final String PATENTAPPDATE = "52";
	public static final String PATENTISSUEDATE = "53";
	public static final String COMPANIES = "54";
	public static final String CASREGISTRYNUMBER = "55";
	public static final String BUSINESSTERMS = "56";
	public static final String CHEMICALTERMS = "57";
	public static final String CHEMICALACRONYMS = "58";
	public static final String SIC = "59";
	public static final String INDUSTRIALCODES = "60";
	public static final String INDUSTRIALSECTORS = "61";
	public static final String SCOPE = "62";
	public static final String AGENCY = "63";
	public static final String PATENT_NUMBER = "64";
	public static final String REGIONS = "65";
	public static final String PAGE = "66";
	public static final String DERWENT_ACCESSION_NUMBER = "67";
	public static final String APPLICATION_NUMBER = "68";
	public static final String APPLICATION_DATE = "69";
	public static final String APPLICATION_COUNTRY = "70";
	public static final String PATENT_DATE = "71";
	public static final String PATENT_COUNTRY = "72";
	public static final String INT_PATENT_CLASSIFICATION = "73";
	public static final String LINKED_TERMS = "74";
	public static final String ENTRY_YEAR = "75";
	public static final String PRIORITY_NUMBER = "76";
	public static final String PRIORITY_DATE = "77";
	public static final String PRIORITY_COUNTRY = "78";
	public static final String SOURCE = "79";
	public static final String SECONDARY_SRC_TITLE = "80";
	public static final String OTHER_ABSTRACT = "81";
	public static final String MAIN_TERM = "82";
	public static final String ABBRV_SRC_TITLE = "83";
	public static final String NOROLE_TERMS = "85";
	public static final String REAGENT_TERMS = "86";
	public static final String PRODUCT_TERMS = "87";
	public static final String MAJORNOROLE_TERMS = "88";
	public static final String MAJORREAGENT_TERMS = "89";
	public static final String MAJORPRODUCT_TERMS = "90";
	public static final String CONFERENCEEDITORS = "91";
	public static final String CONFERENCEAFFILIATIONS = "92";
	public static final String CONFERENCESTARTDATE = "93";
	public static final String CONFERENCEENDDATE = "94";
	public static final String CONFERENCEVENUESITE = "95";
	public static final String CONFERENCECITY = "96";
	public static final String CONFERENCECOUNTRYCODE = "97";
	public static final String CONFERENCEPAGERANGE = "98";
	public static final String CONFERENCENUMBERPAGES = "99";
	public static final String CONFERENCEPARTNUMBER = "100";
	public static final String DESIGNATED_STATES = "101";
	public static final String STN_CONFERENCE = "102";
	public static final String STN_SECONDARY_CONFERENCE = "103";
	public static final String PATENT_FILING_DATE = "104";
	public static final String PATENT_KIND = "105";
	public static final String PRIORITY_KIND = "106";
	public static final String KIND_DESCR = "107";
	public static final String ECLA_CODES = "108";
	public static final String ECLA_CLASSES = "110";
	public static final String ECLA_SUB_CLASSES = "111";
	public static final String INT_PATENT_CLASSES = "113";
	public static final String INT_PATENT_SUB_CLASSES = "114";
	public static final String ATTORNEY_NAME = "115";
	public static final String PRIMARY_EXAMINER = "116";
	public static final String ASSISTANT_EXAMINER = "117";
	public static final String AUTHORITY_CODE = "118";
	public static final String DMASK = "119";
	public static final String PCITED = "120";
	public static final String DOI = "121";
	public static final String AFFILIATION_LOCATION = "122";
	public static final String PCITEDINDEX = "123";
	public static final String USPTOCODE = "124";
	public static final String USPTOCLASS = "125";
	public static final String USPTOSUBCLASS = "126";
	public static final String PREFINDEX = "127";
	public static final String COPYRIGHT = "128";
	public static final String PII = "129";
	public static final String PUI = "130";
	public static final String LAT_NW = "131";
	public static final String LNG_NW = "132";
	public static final String LAT_NE = "133";
	public static final String LNG_NE = "134";
	public static final String LAT_SW = "135";
	public static final String LNG_SW = "136";
	public static final String LAT_SE = "137";
	public static final String LNG_SE = "138";
	public static final String SCOPUSID = "139";
	public static final String AUTHORID = "140";
	public static final String AFFILIATIONID = "141";
	public static final String DATESORT = "142";
	public static final String PARENT_ID = "143";


	private Hashtable h = new Hashtable();

	public boolean containsKey(String key)
	{
		return h.containsKey(key);
	}

	public String getString(String key)
	{
		String[] s = (String[]) h.get(key);
		if(s == null)
		{
			return null;
		}

		return s[0];
	}

	public String get(String key)
	{
		String[] s = (String[]) h.get(key);
		if(s == null)
		{
			return null;
		}

		return s[0];
	}

	public String[] getStrings(String key)
	{
		return (String[])h.get(key);
	}


	public void put(String key, String[] value)
	{
		h.put(key, value);
	}

	public void putIfNotNull(String key, String value)
	{
	  if(value != null)
	  {
	    this.put(key, value);
    }
	}

	public void putIfNotNull(String key, String[] value)
	{
	  if(value != null)
	  {
	    this.put(key, value);
    }
	}

	public void put(String key, String value)
	{
		String[] s = new String[1];
		s[0] = value;
		h.put(key, s);
	}

	public void remove(String key)
	{
		if(h.containsKey(key))
		{
			h.remove(key);
		}
	}

	public boolean clear (){
	    if (h != null){
	        h.clear();
	    }
	    return true;
	}
}
