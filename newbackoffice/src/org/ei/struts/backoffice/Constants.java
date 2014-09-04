/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/Constants.java-arc   1.2   Mar 17 2009 15:33:24   johna  $
 * $Revision:   1.2  $
 * $Date:   Mar 17 2009 15:33:24  $
 *
 */
package org.ei.struts.backoffice;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.struts.util.LabelValueBean;
import org.ei.struts.backoffice.contract.accessmethod.SiteLicenseDatabase;
import org.ei.struts.backoffice.contract.status.StatusDatabase;

public final class Constants {

	private static Log log = LogFactory.getLog("Constants");

	public static final String NEWLINE = System.getProperty("line.separator");
	public static final String EMPTY_STRING = "";

	// Default Time periods - in Days
	public static final int TRIAL_LENGTH = 30;
	public static final int UNLIMITED_LENGTH = 730;
	public static final int CONTRACT_LENGTH = 365;
	public static final int GRACE_PERIOD = 30;
	public static final int ARCHIVE_PERIOD = 60;

	public static final int LIST_COUNT = 10;

	public static final String NO = "no";
	public static final String YES = "yes";

	public static final String ON = "on";
	public static final String OFF = "off";

	public static final String N = "N";
	public static final String Y = "Y";

	public static final int ENABLED = 1;
	public static final int DISABLED = 0;

	public static final String USER_KEY = "user";

	public static final String DEFAULT_CONTEXT = "java:comp/env";
	public static final String U_ACCESS_DBCP_POOL = "jdbc/U_ACCESS_Pool";
	public static final String OFFICE_DBCP_POOL = "jdbc/OfficePool";
	public static final String TOKEN_DBCP_POOL = "jdbc/PPD_DBCP_POOL";

	public static final String ACTION_ADD = "Add";
	public static final String ACTION_CONVERT = "Convert";
	public static final String ACTION_RENEW = "Renew";
	public static final String ACTION_REMOVE = "Remove";
	public static final String ACTION_CREATE = "Create";
	public static final String ACTION_EDIT = "Edit";
	public static final String ACTION_LIST = "List";
	public static final String ACTION_DELETE = "Delete";
	public static final String ACTION_ENABLE = "Enable";
	public static final String ACTION_DISABLE = "Disable";

	public static final String pageSize = "100";

	public static final String START_FROM_DATE = "TO_DATE('1/1/1998','dd/mm/yyyy')";
	public static final String START_TO_DATE = "TO_DATE('1/1/2013','dd/mm/yyyy')";
	public static final String END_FROM_DATE ="TO_DATE('1/1/1998','dd/mm/yyyy')";
	public static final String END_TO_DATE = "TO_DATE('1/1/2013','dd/mm/yyyy')";

	public static final String ROLL_OVER = "roll over";

	public static final String NERAC_ID = "9998";
	public static final String NERAC_NAME = "nerac";

	public static final String USAGE_ID = "9999";
	public static final String USAGE_NAME = "Usage";
	public static final String EV2 = "4000";
	public static final String EV2_NAME = "EngVillage2";
	public static final String EV2_LONGNAME = "ENGINEERINGVILLAGE2";
	public static final String DDS = "9004";


	public static final String PV2 = "4100";
	public static final String PV2_NAME = "PaperVillage2";

	public static final String CV = "4400";
	public static final String CV_NAME = "ChemVillage";

	public static final String ENC2 = "4600";
	public static final String ENC2_NAME = "EnCompassWEB";

	public static final String ENCOMP_LIT = "4200";
	public static final String ENCOMP_PAT = "4300";

	public static final String SERVLET_STARTTIME_KEY="starttime";

	public static Collection getActiveProducts() {

		Collection allprods  = new ArrayList();

		// All products must still exist in EI_PRODUCT Table.
		// These are the IDs of active products that will
		// be available up when adding items to a contract
		allprods.add(EV2);
		//allprods.add(ENC2);
		//allprods.add(PV2);
		//allprods.add(ENCOMP_LIT);
		//allprods.add(ENCOMP_PAT);
		//allprods.add(CV);
		allprods.add(DDS);
		allprods.add(USAGE_ID);
		//allprods.add(NERAC_ID);

		return allprods;
	}

	public static Collection getProductsWithLocalHoldings() {

		Collection allprods  = new ArrayList();

		allprods.add(EV2);
		//allprods.add(ENC2);
		//allprods.add(PV2);
		//allprods.add(CV);

		return allprods;

	}
	public static Collection getProductsWithOptions() {

		Collection allprods  = new ArrayList();

		allprods.add(EV2);
		//allprods.add(ENC2);
		//allprods.add(PV2);
		//allprods.add(CV);
		allprods.add(DDS);

		return allprods;
	}

	public static final String GATEWAY_URL_LINK = "http://www.engineeringvillage.com/controller/servlet/Controller?SYSTEM_NEWSESSION=true";

	public static final String IP = "IP";
	public static final String GATEWAY = "GATEWAY";
	public static final String USERNAME  = "USERNAME";


/*	public static Collection getAccessTypes() {

		Collection accesstypes = new ArrayList();

		accesstypes.add(new LabelValueBean(IP, "IP"));
		accesstypes.add(new LabelValueBean(GATEWAY, "Gateway"));
		accesstypes.add(new LabelValueBean(USERNAME, "Username/Password"));

		return accesstypes;
	}
*/
	public static final String STATUS_NEW = "New";
	public static final String STATUS_UPGRADE = "Upgrade";
	public static final String STATUS_CONVERTED  = "Converted";
	public static final String STATUS_RENEWAL = "Renewal";

	public static Collection getAllStatus() {

		Collection colAllStatus = new ArrayList();

		colAllStatus.add(new LabelValueBean(EMPTY_STRING, EMPTY_STRING));
		colAllStatus.add(new LabelValueBean(STATUS_NEW, STATUS_NEW));
		colAllStatus.add(new LabelValueBean(STATUS_UPGRADE, STATUS_UPGRADE));
		colAllStatus.add(new LabelValueBean(STATUS_CONVERTED, STATUS_CONVERTED));
		colAllStatus.add(new LabelValueBean(STATUS_RENEWAL, STATUS_RENEWAL));

		return (colAllStatus);
	}

	public static Collection getAllAccessType() {

		Collection colAccessType = new ArrayList();

		colAccessType.add(new LabelValueBean(EMPTY_STRING, EMPTY_STRING));
		colAccessType.addAll(new StatusDatabase().getStatus());

		return (colAccessType);
	}
	public final static String TYPE_TRIAL = "Trial";
	public final static String TYPE_AGENT = "Agent";
	public final static String TYPE_CONSORTIUM = "Consortium";
	public final static String TYPE_DIRECT = "Direct";

	public static Collection getAllContractType() {

		Collection colContractType = new ArrayList();

		colContractType.add(new LabelValueBean(EMPTY_STRING, EMPTY_STRING));
		colContractType.add(new LabelValueBean(TYPE_AGENT, TYPE_AGENT));
		colContractType.add(new LabelValueBean(TYPE_CONSORTIUM, TYPE_CONSORTIUM));
		colContractType.add(new LabelValueBean(TYPE_DIRECT, TYPE_DIRECT));
		colContractType.add(new LabelValueBean(TYPE_TRIAL, TYPE_TRIAL));

		return (colContractType);
	}

	public static Collection getSiteLicenses() {

		Collection licenses = new ArrayList();

		licenses.add(new LabelValueBean(EMPTY_STRING, "0"));
		licenses.addAll((new SiteLicenseDatabase()).getSiteLicenses());

		return licenses;
	}

	public static Collection getCountries() {

		Collection countries = new ArrayList();

		countries.add(new LabelValueBean(EMPTY_STRING, EMPTY_STRING));
		countries.add(new LabelValueBean("AFGHANISTAN", "AFGHANISTAN"));
		countries.add(new LabelValueBean("ALBANIA", "ALBANIA"));
		countries.add(new LabelValueBean("ALGERIA", "ALGERIA"));
		countries.add(new LabelValueBean("AMERICAN SAMOA", "AMERICAN SAMOA"));
		countries.add(new LabelValueBean("ANDORRA", "ANDORRA"));
		countries.add(new LabelValueBean("ANGOLA", "ANGOLA"));
		countries.add(new LabelValueBean("ANGUILLA", "ANGUILLA"));
		countries.add(new LabelValueBean("ANTARCTICA", "ANTARCTICA"));
		countries.add(
			new LabelValueBean("ANTIGUA AND BARBUDA", "ANTIGUA AND BARBUDA"));
		countries.add(
			new LabelValueBean("ANTILLES (FRENCH)", "ANTILLES (FRENCH)"));
		countries.add(new LabelValueBean("ARGENTINA", "ARGENTINA"));
		countries.add(new LabelValueBean("ARMENIA", "ARMENIA"));
		countries.add(new LabelValueBean("ARUBA", "ARUBA"));
		countries.add(new LabelValueBean("AUSTRALIA", "AUSTRALIA"));
		countries.add(new LabelValueBean("AUSTRIA", "AUSTRIA"));
		countries.add(new LabelValueBean("AZERBAIJAN", "AZERBAIJAN"));
		countries.add(new LabelValueBean("BAHAMAS", "BAHAMAS"));
		countries.add(new LabelValueBean("BAHRAIN", "BAHRAIN"));
		countries.add(new LabelValueBean("BANGLADESH", "BANGLADESH"));
		countries.add(new LabelValueBean("BARBADOS", "BARBADOS"));
		countries.add(new LabelValueBean("BELGIUM", "BELGIUM"));
		countries.add(new LabelValueBean("BELIZE", "BELIZE"));
		countries.add(new LabelValueBean("BENIN", "BENIN"));
		countries.add(new LabelValueBean("BERMUDA", "BERMUDA"));
		countries.add(new LabelValueBean("BHUTAN", "BHUTAN"));
		countries.add(new LabelValueBean("BOLIVIA", "BOLIVIA"));
		countries.add(
			new LabelValueBean(
				"BOSNIA AND HERCEGOVINA",
				"BOSNIA AND HERCEGOVINA"));
		countries.add(new LabelValueBean("BOTSWANA", "BOTSWANA"));
		countries.add(new LabelValueBean("BOUVET ISLAND", "BOUVET ISLAND"));
		countries.add(new LabelValueBean("BRAZIL", "BRAZIL"));
		countries.add(
			new LabelValueBean(
				"BRITISH INDIAN OCEAN TERRITORY",
				"BRITISH INDIAN OCEAN TERRITORY"));
		countries.add(
			new LabelValueBean(
				"BRITISH VIRGIN ISLANDS",
				"BRITISH VIRGIN ISLANDS"));
		countries.add(new LabelValueBean("BRUNEI", "BRUNEI"));
		countries.add(new LabelValueBean("BULGARIA", "BULGARIA"));
		countries.add(new LabelValueBean("BURKINA FASO", "BURKINA FASO"));
		countries.add(new LabelValueBean("BURUNDI", "BURUNDI"));
		countries.add(new LabelValueBean("BYELORUSSIA", "BYELORUSSIA"));
		countries.add(new LabelValueBean("CAMBODIA", "CAMBODIA"));
		countries.add(new LabelValueBean("CAMEROON", "CAMEROON"));
		countries.add(new LabelValueBean("CANADA", "CANADA"));
		countries.add(new LabelValueBean("CAPE VERDE", "CAPE VERDE"));
		countries.add(new LabelValueBean("CAYMAN ISLANDS", "CAYMAN ISLANDS"));
		countries.add(
			new LabelValueBean(
				"CENTRAL AFRICAN REPUBLIC",
				"CENTRAL AFRICAN REPUBLIC"));
		countries.add(new LabelValueBean("CHAD", "CHAD"));
		countries.add(new LabelValueBean("CHILE", "CHILE"));
		countries.add(new LabelValueBean("CHINA", "CHINA"));
		countries.add(
			new LabelValueBean("CHRISTMAS ISLAND", "CHRISTMAS ISLAND"));
		countries.add(
			new LabelValueBean(
				"COCOS (KEELING) ISLANDS",
				"COCOS (KEELING) ISLANDS"));
		countries.add(new LabelValueBean("COLOMBIA", "COLOMBIA"));
		countries.add(new LabelValueBean("COMOROS", "COMOROS"));
		countries.add(new LabelValueBean("CONGO", "CONGO"));
		countries.add(new LabelValueBean("COOK ISLANDS", "COOK ISLANDS"));
		countries.add(new LabelValueBean("COSTA RICA", "COSTA RICA"));
		countries.add(new LabelValueBean("CROATIA", "CROATIA"));
		countries.add(new LabelValueBean("CUBA", "CUBA"));
		countries.add(new LabelValueBean("CYPRUS", "CYPRUS"));
		countries.add(new LabelValueBean("CZECH REPUBLIC", "CZECH REPUBLIC"));
		countries.add(
			new LabelValueBean(
				"DEMOCRATIC REPUBLIC CONGO",
				"DEMOCRATIC REPUBLIC CONGO"));
		countries.add(new LabelValueBean("DENMARK", "DENMARK"));
		countries.add(new LabelValueBean("DJIBOUTI", "DJIBOUTI"));
		countries.add(new LabelValueBean("DOMINICA", "DOMINICA"));
		countries.add(
			new LabelValueBean("DOMINICAN REPUBLIC", "DOMINICAN REPUBLIC"));
		countries.add(new LabelValueBean("EAST TIMOR", "EAST TIMOR"));
		countries.add(new LabelValueBean("ECUADOR", "ECUADOR"));
		countries.add(new LabelValueBean("EGYPT", "EGYPT"));
		countries.add(new LabelValueBean("EL SALVADOR", "EL SALVADOR"));
		countries.add(
			new LabelValueBean("EQUATORIAL GUINEA", "EQUATORIAL GUINEA"));
		countries.add(new LabelValueBean("ERITREA", "ERITREA"));
		countries.add(new LabelValueBean("ESTONIA", "ESTONIA"));
		countries.add(new LabelValueBean("ETHIOPIA", "ETHIOPIA"));
		countries.add(
			new LabelValueBean(
				"FEDERATED STATES OF MICRONESIA",
				"FEDERATED STATES OF MICRONESIA"));
		countries.add(new LabelValueBean("FIJI", "FIJI"));
		countries.add(new LabelValueBean("FINLAND", "FINLAND"));
		countries.add(new LabelValueBean("FRANCE", "FRANCE"));
		countries.add(new LabelValueBean("FRENCH GUIANA", "FRENCH GUIANA"));
		countries.add(
			new LabelValueBean("FRENCH POLYNESIA", "FRENCH POLYNESIA"));
		countries.add(
			new LabelValueBean(
				"FRENCH SOUTHERN TERRITORIES",
				"FRENCH SOUTHERN TERRITORIES"));
		countries.add(new LabelValueBean("GABON", "GABON"));
		countries.add(new LabelValueBean("GAMBIA", "GAMBIA"));
		countries.add(new LabelValueBean("GEORGIA", "GEORGIA"));
		countries.add(new LabelValueBean("GERMANY", "GERMANY"));
		countries.add(new LabelValueBean("GHANA", "GHANA"));
		countries.add(new LabelValueBean("GIBRALTAR", "GIBRALTAR"));
		countries.add(new LabelValueBean("GREECE", "GREECE"));
		countries.add(new LabelValueBean("GREENLAND", "GREENLAND"));
		countries.add(new LabelValueBean("GRENADA", "GRENADA"));
		countries.add(new LabelValueBean("GUADELOUPE", "GUADELOUPE"));
		countries.add(new LabelValueBean("GUAM", "GUAM"));
		countries.add(new LabelValueBean("GUATEMALA", "GUATEMALA"));
		countries.add(new LabelValueBean("GUINEA", "GUINEA"));
		countries.add(new LabelValueBean("GUINEA-BISSAU", "GUINEA-BISSAU"));
		countries.add(new LabelValueBean("GUYANA", "GUYANA"));
		countries.add(new LabelValueBean("HAITI", "HAITI"));
		countries.add(
			new LabelValueBean(
				"HEARD AND MC DONALD ISLANDS",
				"HEARD AND MC DONALD ISLANDS"));
		countries.add(new LabelValueBean("HONDURAS", "HONDURAS"));
		countries.add(new LabelValueBean("HONG KONG", "HONG KONG"));
		countries.add(new LabelValueBean("HUNGARY", "HUNGARY"));
		countries.add(new LabelValueBean("ICELAND", "ICELAND"));
		countries.add(new LabelValueBean("INDIA", "INDIA"));
		countries.add(new LabelValueBean("INDONESIA", "INDONESIA"));
		countries.add(new LabelValueBean("IRAN", "IRAN"));
		countries.add(new LabelValueBean("IRAQ", "IRAQ"));
		countries.add(new LabelValueBean("IRELAND", "IRELAND"));
		countries.add(new LabelValueBean("ISRAEL", "ISRAEL"));
		countries.add(new LabelValueBean("ITALY", "ITALY"));
		countries.add(new LabelValueBean("IVORY COAST", "IVORY COAST"));
		countries.add(new LabelValueBean("JAMAICA", "JAMAICA"));
		countries.add(new LabelValueBean("JAPAN", "JAPAN"));
		countries.add(new LabelValueBean("JORDAN", "JORDAN"));
		countries.add(new LabelValueBean("KAZAKHSTAN", "KAZAKHSTAN"));
		countries.add(new LabelValueBean("KENYA", "KENYA"));
		countries.add(new LabelValueBean("KIRGIZIYA", "KIRGIZIYA"));
		countries.add(new LabelValueBean("KIRIBATI", "KIRIBATI"));
		countries.add(new LabelValueBean("KUWAIT", "KUWAIT"));
		countries.add(new LabelValueBean("LAOS", "LAOS"));
		countries.add(new LabelValueBean("LATVIA", "LATVIA"));
		countries.add(new LabelValueBean("LEBANON", "LEBANON"));
		countries.add(new LabelValueBean("LESOTHO", "LESOTHO"));
		countries.add(new LabelValueBean("LIBERIA", "LIBERIA"));
		countries.add(
			new LabelValueBean(
				"LIBYAN ARAB JAMAHIRIYA",
				"LIBYAN ARAB JAMAHIRIYA"));
		countries.add(new LabelValueBean("LIECHTENSTEIN", "LIECHTENSTEIN"));
		countries.add(new LabelValueBean("LITHUANIA", "LITHUANIA"));
		countries.add(new LabelValueBean("LUXEMBOURG", "LUXEMBOURG"));
		countries.add(new LabelValueBean("MACAU", "MACAU"));
		countries.add(new LabelValueBean("MACEDONIA", "MACEDONIA"));
		countries.add(new LabelValueBean("MADAGASCAR", "MADAGASCAR"));
		countries.add(new LabelValueBean("MALAWI", "MALAWI"));
		countries.add(new LabelValueBean("MALAYSIA", "MALAYSIA"));
		countries.add(new LabelValueBean("MALDIVES", "MALDIVES"));
		countries.add(new LabelValueBean("MALI", "MALI"));
		countries.add(new LabelValueBean("MALTA", "MALTA"));
		countries.add(new LabelValueBean("MALVINAS", "MALVINAS"));
		countries.add(
			new LabelValueBean("MARSHALL ISLANDS", "MARSHALL ISLANDS"));
		countries.add(new LabelValueBean("MARTINIQUE", "MARTINIQUE"));
		countries.add(new LabelValueBean("MAURITANIA", "MAURITANIA"));
		countries.add(new LabelValueBean("MAURITIUS", "MAURITIUS"));
		countries.add(new LabelValueBean("MEXICO", "MEXICO"));
		countries.add(
			new LabelValueBean("MINOR YUGOSLAVIA", "MINOR YUGOSLAVIA"));
		countries.add(new LabelValueBean("MOLDAVIA", "MOLDAVIA"));
		countries.add(new LabelValueBean("MONACO", "MONACO"));
		countries.add(new LabelValueBean("MONGOLIA", "MONGOLIA"));
		countries.add(new LabelValueBean("MONTENEGRO", "MONTENEGRO"));
		countries.add(new LabelValueBean("MONTSERRAT", "MONTSERRAT"));
		countries.add(new LabelValueBean("MOROCCO", "MOROCCO"));
		countries.add(new LabelValueBean("MOZAMBIQUE", "MOZAMBIQUE"));
		countries.add(new LabelValueBean("MULTINATIONAL", "MULTINATIONAL"));
		countries.add(new LabelValueBean("MYANMAR", "MYANMAR"));
		countries.add(new LabelValueBean("NAMIBIA", "NAMIBIA"));
		countries.add(new LabelValueBean("NAURU", "NAURU"));
		countries.add(new LabelValueBean("NEPAL", "NEPAL"));
		countries.add(new LabelValueBean("NETHERLANDS", "NETHERLANDS"));
		countries.add(
			new LabelValueBean("NETHERLANDS ANTILLES", "NETHERLANDS ANTILLES"));
		countries.add(new LabelValueBean("NEW CALEDONIA", "NEW CALEDONIA"));
		countries.add(new LabelValueBean("NEW ZEALAND", "NEW ZEALAND"));
		countries.add(new LabelValueBean("NICARAGUA", "NICARAGUA"));
		countries.add(new LabelValueBean("NIGER", "NIGER"));
		countries.add(new LabelValueBean("NIGERIA", "NIGERIA"));
		countries.add(new LabelValueBean("NIUE", "NIUE"));
		countries.add(new LabelValueBean("NORFOLK ISLAND", "NORFOLK ISLAND"));
		countries.add(new LabelValueBean("NORTH KOREA", "NORTH KOREA"));
		countries.add(
			new LabelValueBean(
				"NORTHERN MARIANA ISLANDS",
				"NORTHERN MARIANA ISLANDS"));
		countries.add(new LabelValueBean("NORWAY", "NORWAY"));
		countries.add(new LabelValueBean("OCEANIA", "OCEANIA"));
		countries.add(new LabelValueBean("OMAN", "OMAN"));
		countries.add(new LabelValueBean("PAKISTAN", "PAKISTAN"));
		countries.add(new LabelValueBean("PALAU", "PALAU"));
		countries.add(new LabelValueBean("PANAMA", "PANAMA"));
		countries.add(
			new LabelValueBean("PANAMA CANAL ZONE", "PANAMA CANAL ZONE"));
		countries.add(
			new LabelValueBean("PAPUA NEW GUINEA", "PAPUA NEW GUINEA"));
		countries.add(new LabelValueBean("PARAGUAY", "PARAGUAY"));
		countries.add(new LabelValueBean("PERU", "PERU"));
		countries.add(new LabelValueBean("PHILIPPINES", "PHILIPPINES"));
		countries.add(new LabelValueBean("PITCAIRN", "PITCAIRN"));
		countries.add(new LabelValueBean("POLAND", "POLAND"));
		countries.add(new LabelValueBean("PORTUGAL", "PORTUGAL"));
		countries.add(new LabelValueBean("PUERTO RICO", "PUERTO RICO"));
		countries.add(new LabelValueBean("QATAR", "QATAR"));
		countries.add(new LabelValueBean("REUNION", "REUNION"));
		countries.add(new LabelValueBean("ROMANIA", "ROMANIA"));
		countries.add(new LabelValueBean("RUSSIA", "RUSSIA"));
		countries.add(new LabelValueBean("RWANDA", "RWANDA"));
		countries.add(
			new LabelValueBean(
				"SAINT KITTS AND NEVIS",
				"SAINT KITTS AND NEVIS"));
		countries.add(new LabelValueBean("SAINT LUCIA", "SAINT LUCIA"));
		countries.add(new LabelValueBean("SAMOA", "SAMOA"));
		countries.add(new LabelValueBean("SAN MARINO", "SAN MARINO"));
		countries.add(
			new LabelValueBean(
				"SAO TOME AND PRINCIPE",
				"SAO TOME AND PRINCIPE"));
		countries.add(new LabelValueBean("SAUDI ARABIA", "SAUDI ARABIA"));
		countries.add(new LabelValueBean("SENEGAL", "SENEGAL"));
		countries.add(new LabelValueBean("SEYCHELLES", "SEYCHELLES"));
		countries.add(new LabelValueBean("SIERRA LEONE", "SIERRA LEONE"));
		countries.add(new LabelValueBean("SINGAPORE", "SINGAPORE"));
		countries.add(new LabelValueBean("SLOVAK REPUBLIC", "SLOVAK REPUBLIC"));
		countries.add(new LabelValueBean("SLOVENIA", "SLOVENIA"));
		countries.add(new LabelValueBean("SOLOMON ISLANDS", "SOLOMON ISLANDS"));
		countries.add(new LabelValueBean("SOMALIA", "SOMALIA"));
		countries.add(new LabelValueBean("SOUTH AFRICA", "SOUTH AFRICA"));
		countries.add(new LabelValueBean("SOUTH KOREA", "SOUTH KOREA"));
		countries.add(new LabelValueBean("SPAIN", "SPAIN"));
		countries.add(new LabelValueBean("SRI LANKA", "SRI LANKA"));
		countries.add(new LabelValueBean("ST. HELENA", "ST. HELENA"));
		countries.add(
			new LabelValueBean(
				"ST. PIERRE AND MIQUELON",
				"ST. PIERRE AND MIQUELON"));
		countries.add(
			new LabelValueBean(
				"ST. VINCENT AND THE GRENADINES",
				"ST. VINCENT AND THE GRENADINES"));
		countries.add(new LabelValueBean("SUDAN", "SUDAN"));
		countries.add(new LabelValueBean("SURINAME", "SURINAME"));
		countries.add(new LabelValueBean("SWAZILAND", "SWAZILAND"));
		countries.add(new LabelValueBean("SWEDEN", "SWEDEN"));
		countries.add(new LabelValueBean("SWITZERLAND", "SWITZERLAND"));
		countries.add(new LabelValueBean("SYRIA", "SYRIA"));
		countries.add(new LabelValueBean("TADZHIKISTAN", "TADZHIKISTAN"));
		countries.add(new LabelValueBean("TAIWAN", "TAIWAN"));
		countries.add(new LabelValueBean("TANZANIA", "TANZANIA"));
		countries.add(new LabelValueBean("THAILAND", "THAILAND"));
		countries.add(new LabelValueBean("TOGO", "TOGO"));
		countries.add(new LabelValueBean("TOKELAU", "TOKELAU"));
		countries.add(new LabelValueBean("TONGA", "TONGA"));
		countries.add(new LabelValueBean("TRANSKEI", "TRANSKEI"));
		countries.add(
			new LabelValueBean("TRINIDAD AND TOBAGO", "TRINIDAD AND TOBAGO"));
		countries.add(new LabelValueBean("TUNISIA", "TUNISIA"));
		countries.add(new LabelValueBean("TURKEY", "TURKEY"));
		countries.add(new LabelValueBean("TURKMENISTAN", "TURKMENISTAN"));
		countries.add(
			new LabelValueBean(
				"TURKS AND CAICOS ISLANDS",
				"TURKS AND CAICOS ISLANDS"));
		countries.add(new LabelValueBean("TUVALU", "TUVALU"));
		countries.add(new LabelValueBean("UGANDA", "UGANDA"));
		countries.add(new LabelValueBean("UK", "UK"));
		countries.add(new LabelValueBean("UKRAINE", "UKRAINE"));
		countries.add(
			new LabelValueBean("UNITED ARAB EMIRATES", "UNITED ARAB EMIRATES"));
		countries.add(new LabelValueBean("UNKNOWN", "UNKNOWN"));
		countries.add(new LabelValueBean("URUGUAY", "URUGUAY"));
		countries.add(
			new LabelValueBean(
				"US MINOR OUTLYING ISLANDS",
				"US MINOR OUTLYING ISLANDS"));
		countries.add(
			new LabelValueBean("US VIRGIN ISLANDS", "US VIRGIN ISLANDS"));
		countries.add(new LabelValueBean("USA", "USA"));
		countries.add(new LabelValueBean("UZBEKISTAN", "UZBEKISTAN"));
		countries.add(new LabelValueBean("VANUATU", "VANUATU"));
		countries.add(
			new LabelValueBean("VATICAN CITY STATE", "VATICAN CITY STATE"));
		countries.add(new LabelValueBean("VENEZUELA", "VENEZUELA"));
		countries.add(new LabelValueBean("VIETNAM", "VIETNAM"));
		countries.add(
			new LabelValueBean(
				"WALLIS AND FUTUNA ISLANDS",
				"WALLIS AND FUTUNA ISLANDS"));
		countries.add(new LabelValueBean("WEST INDIES", "WEST INDIES"));
		countries.add(new LabelValueBean("WESTERN SAHARA", "WESTERN SAHARA"));
		countries.add(new LabelValueBean("YEMEN", "YEMEN"));
		countries.add(new LabelValueBean("YEMEN SOUTH", "YEMEN SOUTH"));
		countries.add(new LabelValueBean("YUGOSLAVIA", "YUGOSLAVIA"));
		countries.add(new LabelValueBean("ZAMBIA", "ZAMBIA"));
		countries.add(new LabelValueBean("ZIMBABWE", "ZIMBABWE"));
		return countries;
	}

	public Collection getIndustries() {

		Collection industries = new ArrayList();

		industries.add(new LabelValueBean(EMPTY_STRING, EMPTY_STRING));
        industries.add(new LabelValueBean("Aerospace / Defense","1"));
        industries.add(new LabelValueBean("Agricultural / Forestry","2"));
        industries.add(new LabelValueBean("Automotive","3"));
        industries.add(new LabelValueBean("Banking / Finance","4"));
        industries.add(new LabelValueBean("Biotechnology","5"));
        industries.add(new LabelValueBean("Chemical","6"));
        industries.add(new LabelValueBean("Chemical/Pharma Div.","7"));
        industries.add(new LabelValueBean("Corporate Sales Leads","8"));
        industries.add(new LabelValueBean("Electronics","9"));
        industries.add(new LabelValueBean("Eng. & Const. Firms","10"));
        industries.add(new LabelValueBean("Food / Home / Personal Care","11"));
        industries.add(new LabelValueBean("IT – Computers/Software","12"));
        industries.add(new LabelValueBean("Insurance","13"));
        industries.add(new LabelValueBean("Life Science Misc.","14"));
        industries.add(new LabelValueBean("Manufacturing","15"));
        industries.add(new LabelValueBean("Metals","16"));
        industries.add(new LabelValueBean("Mining","17"));
        industries.add(new LabelValueBean("Oil & Gas","18"));
        industries.add(new LabelValueBean("Pharmaceutical (Large Accounts)","19"));
        industries.add(new LabelValueBean("Pharmaceutical (Small Accounts)","20"));
        industries.add(new LabelValueBean("Power/Utilities","21"));
        industries.add(new LabelValueBean("Pulp & Paper","22"));
        industries.add(new LabelValueBean("Research Life Sciences","23"));
        industries.add(new LabelValueBean("Telecom","24"));
        industries.add(new LabelValueBean("Transportation","25"));

		return industries;
	}

	public static Collection getTitles() {

		Collection titles = new ArrayList();

		titles.add(new LabelValueBean(EMPTY_STRING, EMPTY_STRING));
		titles.add(new LabelValueBean("Mr.", "Mr."));
		titles.add(new LabelValueBean("Miss", "Miss"));
		titles.add(new LabelValueBean("Ms.", "Ms."));
		titles.add(new LabelValueBean("Mrs.", "Mrs."));
		titles.add(new LabelValueBean("Dr.", "Dr."));
		titles.add(new LabelValueBean("Prof.", "Prof."));

		return titles;
	}

	public static Collection getMonths() {

		Collection months = new ArrayList();
		months.add(
			new LabelValueBean(
				EMPTY_STRING,
					EMPTY_STRING));

		int[] aryMonths =
			new int[] {
				Calendar.JANUARY,
				Calendar.FEBRUARY,
				Calendar.MARCH,
				Calendar.APRIL,
				Calendar.MAY,
				Calendar.JUNE,
				Calendar.JULY,
				Calendar.AUGUST,
				Calendar.SEPTEMBER,
				Calendar.OCTOBER,
				Calendar.NOVEMBER,
				Calendar.DECEMBER };

		SimpleDateFormat formatter = new SimpleDateFormat("MMMM");
		Calendar calendar = Calendar.getInstance();


		for (int idx = 0; idx < aryMonths.length; idx++) {
			calendar.set(Calendar.MONTH, aryMonths[idx]);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			months.add(
				new LabelValueBean(
					formatter.format(calendar.getTime()),
					String.valueOf(aryMonths[idx])));
		}

		return months;
	}

	public static Collection CustOptYears(int beg) {

		Calendar calendar = Calendar.getInstance();
		return getRange(beg, (calendar.get(Calendar.YEAR)));
	}

	public static Collection getYears() {

		Collection years = new ArrayList();
		Calendar calendar = Calendar.getInstance();
		years =
			getRange(
				(calendar.get(Calendar.YEAR) - 10),
				(calendar.get(Calendar.YEAR) + 10));
		return years;
	}

	public static Collection getRange(int beg, int end) {

		Collection range = new ArrayList();
		range.add(
			new LabelValueBean(
				EMPTY_STRING,
					EMPTY_STRING));

		if (beg > end) {
			for (int idx = beg; idx >= end; idx--) {
				range.add(
					new LabelValueBean(
						String.valueOf(idx),
						String.valueOf(idx)));
			}
		}
		if (beg < end) {
			for (int idx = beg; idx <= end; idx++) {
				range.add(
					new LabelValueBean(
						String.valueOf(idx),
						String.valueOf(idx)));
			}
		}
		return range;
	}

	public static Collection getRange(int beg, int end, int increment) {

		Collection range = new ArrayList();
		if (increment > 0) {
			if (beg > end) {
				for (int idx = end; idx <= beg; idx = idx + increment) {
					range.add(
						new LabelValueBean(
							String.valueOf(idx),
							String.valueOf(idx)));
				}
			}
			if (beg < end) {
				for (int idx = beg; idx <= end; idx = idx + increment) {
					range.add(
						new LabelValueBean(
							String.valueOf(idx),
							String.valueOf(idx)));
				}
			}
		} else {
			if (beg > end) {
				for (int idx = beg; idx >= end; idx = idx + increment) {
					range.add(
						new LabelValueBean(
							String.valueOf(idx),
							String.valueOf(idx)));
				}
			}
			if (beg < end) {
				for (int idx = end; idx >= beg; idx = idx + increment) {
					range.add(
						new LabelValueBean(
							String.valueOf(idx),
							String.valueOf(idx)));
				}
			}

		}
		return range;
	}

	public static Collection getDays() {

		Collection days = new ArrayList();
		days = getRange(1, 31);
		return days;
	}

	public static String formatDate(long lngDate) {
		java.util.Date dateTime = new java.util.Date(lngDate);
		String strFormattedDate = dateTime.toString();

		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
		strFormattedDate = formatter.format(dateTime);
		return strFormattedDate;
	}

	public static String formatDate(String strDate) {
		String strFormattedDate = strDate;

		try {
			SimpleDateFormat formatter =
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			java.util.Date dateTime = formatter.parse((String) strDate);
			formatter = new SimpleDateFormat("MM/dd/yy");
			strFormattedDate = formatter.format(dateTime);
		} catch (java.text.ParseException pe) {
			log.error("ParseException ", pe);
		}
		return strFormattedDate;
	}

	public static java.util.Date createDate(String strDate) {

		java.util.Date dateTime = Calendar.getInstance().getTime();

		try {
			//SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			dateTime = formatter.parse((String) strDate);

		} catch (java.text.ParseException pe) {
			log.error("ParseException ", pe);
		}
		return dateTime;
	}

	public static String formatIpAddress(String ipaddr) {

		Perl5Util perl = new Perl5Util();
		String formatted = EMPTY_STRING;
		if (perl.match("/^[0-9]*$/", ipaddr)) {
			formatted =
				perl.substitute(
					"s/(\\d+)(\\d{3})(\\d{3})(\\d{3})/$1\\.$2\\.$3\\.$4/",
					ipaddr);
		} else {
			formatted = ipaddr;
		}
		return formatted;
	}

	public static String cleanIpAddress(String ipaddr) {

		Perl5Util perl = new Perl5Util();
		String cleaned = EMPTY_STRING;

		if (perl.match("/^\\d+\\./", ipaddr)) {

			if (perl.match("/(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)/", ipaddr)) {
				try {
					NumberFormat nf = NumberFormat.getNumberInstance();
					DecimalFormat df = (DecimalFormat) nf;
					// format bytes to three digits for storage
					df.applyPattern("000");

					for (int x = 1; x <= 4; x++) {
						long aByte = Long.parseLong(perl.group(x));
						cleaned = cleaned.concat(df.format(aByte));
					}

				} catch (NumberFormatException nfe) {
					cleaned = ipaddr;
					log.error("cleanIpAddress", nfe);
				}
			} else {
				cleaned = ipaddr;
			}

		} else {
			cleaned = ipaddr;
		}

		return cleaned;
	}
}
