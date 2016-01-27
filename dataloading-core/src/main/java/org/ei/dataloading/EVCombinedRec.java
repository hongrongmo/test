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
	public static final String TABLE_OF_CONTENT = "144";
	public static final String CPCCLASS = "145";
	
	public static final String NUMERICALINDEXHIGH = "146";
	public static final String NUMERICALINDEXLOW = "147";
	public static final String NUMERICALINDEXUNIT = "148";
	//*  
	public static final String AMOUNT_OF_SUBSTANCE_MINIMUM = "146"; 
	public static final String AMOUNT_OF_SUBSTANCE_MAXIMUM = "147";
	public static final String AMOUNT_OF_SUBSTANCE_TEXT = "148";
	//amount of substance
	public static final String ELECTRIC_CURRENT_MINIMUM = "149"; 
	public static final String ELECTRIC_CURRENT_MAXIMUM = "150";
	public static final String ELECTRIC_CURRENT_TEXT = "151";
	//electric current
	public static final String LUMINOUS_INTENSITY_MINIMUM = "152"; 
	public static final String LUMINOUS_INTENSITY_MAXIMUM = "153";
	public static final String LUMINOUS_INTENSITY_TEXT = "154";
	//luminous intensity
	public static final String MASS_MINIMUM = "155"; 
	public static final String MASS_MAXIMUM = "156";
	public static final String MASS_TEXT = "157";
	//mass
	public static final String TEMPERATURE_MINIMUM = "158"; 
	public static final String TEMPERATURE_MAXIMUM = "159";
	public static final String TEMPERATURE_TEXT = "160";
	//temperature
	public static final String TIME_MINIMUM = "161"; 
	public static final String TIME_MAXIMUM = "162";
	public static final String TIME_TEXT = "163";
	//time
	public static final String SIZE_MINIMUM = "164"; 
	public static final String SIZE_MAXIMUM = "165";
	public static final String SIZE_TEXT = "166";
	//size
	public static final String ELECTRICAL_CONDUCTANCE_MINIMUM = "167"; 
	public static final String ELECTRICAL_CONDUCTANCE_MAXIMUM = "168";
	public static final String ELECTRICAL_CONDUCTANCE_TEXT = "169";
	//electrical conductance
	public static final String ELECTRICAL_CONDUCTIVITY_MINIMUM = "170"; 
	public static final String ELECTRICAL_CONDUCTIVITY_MAXIMUM = "171";
	public static final String ELECTRICAL_CONDUCTIVITYE_TEXT = "172";
	//electrical conductivity
	public static final String THERMAL_CONDUCTIVITY_MINIMUM = "173"; 
	public static final String THERMAL_CONDUCTIVITY_MAXIMUM = "174";
	public static final String THERMAL_CONDUCTIVITY_TEXT = "175";
	//thermal conductivity
	public static final String VOLTAGE_MINIMUM = "176"; 
	public static final String VOLTAGE_MAXIMUM = "177";
	public static final String VOLTAGE_TEXT = "178"; 
	//voltage
	public static final String ELECTRIC_FIELD_STRENGTH_MINIMUM = "179"; 
	public static final String ELECTRIC_FIELD_STRENGTH_MAXIMUM = "180";
	public static final String ELECTRIC_FIELD_STRENGTH_TEXT = "181"; 
	//electric field strength
	public static final String CURRENT_DENSITY_MINIMUM = "182"; 
	public static final String CURRENT_DENSITY_MAXIMUM = "183";
	public static final String CURRENT_DENSITY_TEXT = "184"; 
	//current density
	public static final String ENERGY_MINIMUM = "185"; 
	public static final String ENERGY_MAXIMUM = "186";
	public static final String ENERGY_TEXT = "187"; 
	//energy
	public static final String ELECTRICAL_RESISTANCE_MINIMUM = "188"; 
	public static final String ELECTRICAL_RESISTANCE_MAXIMUM = "189";
	public static final String ELECTRICAL_RESISTANCE_TEXT = "190"; 
	//electrical resistance
	public static final String ELECTRICAL_RESISTIVITY_MINIMUM = "191"; 
	public static final String ELECTRICAL_RESISTIVITY_MAXIMUM = "192";
	public static final String ELECTRICAL_RESISTIVITY_TEXT = "193"; 
	//electrical resistivity
	public static final String ELECTRON_VOLT_ENERGY_MINIMUM = "194"; 
	public static final String ELECTRON_VOLT_ENERGY_MAXIMUM = "195";
	public static final String ELECTRON_VOLT_ENERGY_TEXT = "196"; 
	//electron volt energy
	public static final String CAPACITANCE_MINIMUM = "197"; 
	public static final String CAPACITANCE_MAXIMUM = "198";
	public static final String CAPACITANCE_TEXT = "199"; 
	//capacitance
	public static final String PERMITTIVITY_MINIMUM = "200"; 
	public static final String PERMITTIVITY_MAXIMUM = "201";
	public static final String PERMITTIVITY_TEXT = "202"; 
	//permittivity
	public static final String FREQUENCY_MINIMUM = "203"; 
	public static final String FREQUENCY_MAXIMUM = "204";
	public static final String FREQUENCY_TEXT = "205"; 
	//frequency
	public static final String POWER_MINIMUM = "206"; 
	public static final String POWER_MAXIMUM = "207";
	public static final String POWER_TEXT = "208";  
	//power
	public static final String APPARENT_POWER_MINIMUM = "209"; 
	public static final String APPARENT_POWER_MAXIMUM = "210";
	public static final String APPARENT_POWER_TEXT = "211"; 
	//apparent power 
	public static final String REACTIVE_POWER_MINIMUM = "212"; 
	public static final String REACTIVE_POWER_MAXIMUM = "213";
	public static final String REACTIVE_POWER_TEXT = "214"; 
	//reactive power
	public static final String HEAT_FLUX_DENSITY_MINIMUM = "215"; 
	public static final String HEAT_FLUX_DENSITY_MAXIMUM = "216";
	public static final String HEAT_FLUX_DENSITY_TEXT = "217";  
	//heat flux density
	public static final String PERCENTAGE_MINIMUM = "218"; 
	public static final String PERCENTAGE_MAXIMUM = "219";
	public static final String PERCENTAGE_TEXT = "220"; 
	//percentage
	public static final String MAGNETIC_FLUX_DENSITY_MINIMUM = "221"; 
	public static final String MAGNETIC_FLUX_DENSITY_MAXIMUM = "222";
	public static final String MAGNETIC_FLUX_DENSITY_TEXT = "223";
	//magnetic flux density
	public static final String MAGNETIC_FLUX_MINIMUM = "224"; 
	public static final String MAGNETIC_FLUX_MAXIMUM = "225";
	public static final String MAGNETIC_FLUX_TEXT = "226";
	//magnetic flux
	public static final String INDUCTANCE_MINIMUM = "227"; 
	public static final String INDUCTANCE_MAXIMUM = "228";
	public static final String INDUCTANCE_TEXT = "229";
	//inductance
	public static final String PERMEABILITY_MINIMUM = "230"; 
	public static final String PERMEABILITY_MAXIMUM = "231";
	public static final String PERMEABILITY_TEXT = "232";
	//permeability
	public static final String ELECTRIC_CHARGE_MINIMUM = "233"; 
	public static final String ELECTRIC_CHARGE_MAXIMUM = "234";
	public static final String ELECTRIC_CHARGE_TEXT = "235";
	//electric charge
	public static final String VOLUME_CHARGE_DENSITY_MINIMUM = "236"; 
	public static final String VOLUME_CHARGE_DENSITY_MAXIMUM = "237";
	public static final String VOLUME_CHARGE_DENSITY_TEXT = "238";
	//volume charge density
	public static final String SURFACE_CHARGE_DENSITY_MINIMUM = "239"; 
	public static final String SURFACE_CHARGE_DENSITY_MAXIMUM = "240";
	public static final String SURFACE_CHARGE_DENSITY_TEXT = "241";
	//surface charge density
	public static final String LINEAR_CHARGE_DENSITY_MINIMUM = "242"; 
	public static final String LINEAR_CHARGE_DENSITY_MAXIMUM = "243";
	public static final String LINEAR_CHARGE_DENSITY_TEXT = "244";
	//linear charge density
	public static final String DECIBEL_MINIMUM = "245"; 
	public static final String DECIBEL_MAXIMUM = "246";
	public static final String DECIBEL_TEXT = "247";
	//decibel 
	public static final String LUMINOUS_FLUX_MINIMUM = "248"; 
	public static final String LUMINOUS_FLUX_MAXIMUM = "249";
	public static final String LUMINOUS_FLUX_TEXT = "250";
	//luminous flux 
	public static final String ILLUMINANCE_MINIMUM = "251"; 
	public static final String ILLUMINANCE_MAXIMUM = "252";
	public static final String ILLUMINANCE_TEXT = "253";
	//illuminance
	public static final String BIT_RATE_MINIMUM = "254"; 
	public static final String BIT_RATE_MAXIMUM = "255";
	public static final String BIT_RATE_TEXT = "256";
	//bit rate
	public static final String PICTURE_ELEMENT_MINIMUM = "257"; 
	public static final String PICTURE_ELEMENT_MAXIMUM = "258";
	public static final String PICTURE_ELEMENT_TEXT = "259";
	//picture element
	public static final String MASS_DENSITY_MINIMUM = "260"; 
	public static final String MASS_DENSITY_MAXIMUM = "261";
	public static final String MASS_DENSITY_TEXT = "262";
	//mass density
	public static final String MASS_FLOW_RATE_MINIMUM = "263"; 
	public static final String MASS_FLOW_RATE_MAXIMUM = "264";
	public static final String MASS_FLOW_RATE_TEXT = "265";
	//mass flow rate
	public static final String VOLUMETRIC_FLOW_RATE_MINIMUM = "266"; 
	public static final String VOLUMETRIC_FLOW_RATE_MAXIMUM = "267";
	public static final String VOLUMETRIC_FLOW_RATE_TEXT = "268"; 
	//volumetric flow rate
	public static final String UNIT_OF_INFORMATION_MINIMUM = "269"; 
	public static final String UNIT_OF_INFORMATION_MAXIMUM = "270";
	public static final String UNIT_OF_INFORMATION_TEXT = "271";  
	//unit of information 
	public static final String ANGLE_MINIMUM = "272"; 
	public static final String ANGLE_MAXIMUM = "273";
	public static final String ANGLE_TEXT = "274";  
	//angle
	public static final String SOLID_ANGLE_MINIMUM = "275"; 
	public static final String SOLID_ANGLE_MAXIMUM = "276";
	public static final String SOLID_ANGLE_TEXT = "277";  
	//solid angle
	public static final String PRESSURE_MINIMUM = "278"; 
	public static final String PRESSURE_MAXIMUM = "279";
	public static final String PRESSURE_TEXT = "280"; 
	//pressure
	public static final String DYNAMIC_VISCOSITY_MINIMUM = "281"; 
	public static final String DYNAMIC_VISCOSITY_MAXIMUM = "282";
	public static final String DYNAMIC_VISCOSITY_TEXT = "283"; 
	//dynamic viscosity
	public static final String FORCE_MINIMUM = "284"; 
	public static final String FORCE_MAXIMUM = "285";
	public static final String FORCE_TEXT = "286";
	//force
	public static final String TORQUE_MINIMUM = "287"; 
	public static final String TORQUE_MAXIMUM = "288";
	public static final String TORQUE_TEXT = "289";
	//torque
	//public static final String PRESSURE_MINIMUM = "290"; 
	//public static final String PRESSURE_MAXIMUM = "291";
	//public static final String PRESSURE_TEXT = "292";
	//pressure
	public static final String AREA_MINIMUM = "290"; 
	public static final String AREA_MAXIMUM = "291";
	public static final String AREA_TEXT = "292";
	//area
	public static final String VOLUME_MINIMUM = "293"; 
	public static final String VOLUME_MAXIMUM = "294";
	public static final String VOLUME_TEXT = "295";
	//volume 
	public static final String VELOCITY_MINIMUM = "296"; 
	public static final String VELOCITY_MAXIMUM = "297";
	public static final String VELOCITY_TEXT = "298";       
	//velocity
	public static final String ACCELERATION_MINIMUM = "299"; 
	public static final String ACCELERATION_MAXIMUM = "300";
	public static final String ACCELERATION_TEXT = "301";   
	//acceleration
	public static final String ANGULAR_VELOCITY_MINIMUM = "302"; 
	public static final String ANGULAR_VELOCITY_MAXIMUM = "303";
	public static final String ANGULAR_VELOCITY_TEXT = "304";    
	//angular velocity
	public static final String ROTATIONAL_SPEED_MINIMUM = "305"; 
	public static final String ROTATIONAL_SPEED_MAXIMUM = "306";
	public static final String ROTATIONAL_SPEED_TEXT = "307";   
	//rotational speed 
	public static final String AGE_MINIMUM = "308"; 
	public static final String AGE_MAXIMUM = "309";
	public static final String AGE_TEXT = "310"; 
	//age
	public static final String MOLAR_MASS_MINIMUM = "311"; 
	public static final String MOLAR_MASS_MAXIMUM = "312";
	public static final String MOLAR_MASS_TEXT = "313"; 
	//molar mass
	public static final String MOLALITY_OF_SUBSTANCE_MINIMUM = "314"; 
	public static final String MOLALITY_OF_SUBSTANCE_MAXIMUM = "315";
	public static final String MOLALITY_OF_SUBSTANCE_TEXT = "316"; 
	//molality of substance
	public static final String PH_VALUE_MINIMUM = "317"; 
	public static final String PH_VALUE_MAXIMUM = "318";
	public static final String PH_VALUE_TEXT = "319"; 
	//pH value
	public static final String RADIOACTIVITY_MINIMUM = "320"; 
	public static final String RADIOACTIVITY_MAXIMUM = "321";
	public static final String RADIOACTIVITY_TEXT = "322"; 
	//radioactivity 
	public static final String ABSORBED_DOSE_MINIMUM = "323"; 
	public static final String ABSORBED_DOSE_MAXIMUM = "324";
	public static final String ABSORBED_DOSE_TEXT = "325"; 
	//absorbed dose 
	public static final String DOSE_EQUIVALENT_MINIMUM = "326"; 
	public static final String DOSE_EQUIVALENT_MAXIMUM = "327";
	public static final String DOSE_EQUIVALENT_TEXT = "328";
	//dose equivalent
	public static final String RADIATION_EXPOSURE_MINIMUM = "329"; 
	public static final String RADIATION_EXPOSURE_MAXIMUM = "330";
	public static final String RADIATION_EXPOSURE_TEXT = "331";
	//radiation exposure
	public static final String CATALYTIC_ACTIVITY_MINIMUM = "332"; 
	public static final String CATALYTIC_ACTIVITY_MAXIMUM = "333";
	public static final String CATALYTIC_ACTIVITY_TEXT = "334";
	//catalytic activity

	 //*/

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
