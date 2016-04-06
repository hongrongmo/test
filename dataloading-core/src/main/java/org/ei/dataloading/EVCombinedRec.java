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
	//public static final String AMOUNT_OF_SUBSTANCE_MINIMUM = "146"; 
	public static final String AMOUNT_OF_SUBSTANCE_RANGES = "147";
	public static final String AMOUNT_OF_SUBSTANCE_TEXT = "148";
	//amount of substance
	//public static final String ELECTRIC_CURRENT_MINIMUM = "149"; 
	public static final String ELECTRIC_CURRENT_RANGES = "150";
	public static final String ELECTRIC_CURRENT_TEXT = "151";
	//electric current
	//public static final String LUMINOUS_INTENSITY_MINIMUM = "152"; 
	public static final String LUMINOUS_INTENSITY_RANGES = "153";
	public static final String LUMINOUS_INTENSITY_TEXT = "154";
	//luminous intensity
	//public static final String MASS_MINIMUM = "155"; 
	public static final String MASS_RANGES = "156";
	public static final String MASS_TEXT = "157";
	//mass
	//public static final String TEMPERATURE_MINIMUM = "158"; 
	public static final String TEMPERATURE_RANGES = "159";
	public static final String TEMPERATURE_TEXT = "160";
	//temperature
	//public static final String TIME_MINIMUM = "161"; 
	public static final String TIME_RANGES = "162";
	public static final String TIME_TEXT = "163";
	//time
	//public static final String SIZE_MINIMUM = "164"; 
	public static final String SIZE_RANGES = "165";
	public static final String SIZE_TEXT = "166";
	//size
	//public static final String ELECTRICAL_CONDUCTANCE_MINIMUM = "167"; 
	public static final String ELECTRICAL_CONDUCTANCE_RANGES = "168";
	public static final String ELECTRICAL_CONDUCTANCE_TEXT = "169";
	//electrical conductance
	//public static final String ELECTRICAL_CONDUCTIVITY_MINIMUM = "170"; 
	public static final String ELECTRICAL_CONDUCTIVITY_RANGES = "171";
	public static final String ELECTRICAL_CONDUCTIVITY_TEXT = "172";
	//electrical conductivity
	//public static final String THERMAL_CONDUCTIVITY_MINIMUM = "173"; 
	public static final String THERMAL_CONDUCTIVITY_RANGES = "174";
	public static final String THERMAL_CONDUCTIVITY_TEXT = "175";
	//thermal conductivity
	//public static final String VOLTAGE_MINIMUM = "176"; 
	public static final String VOLTAGE_RANGES = "177";
	public static final String VOLTAGE_TEXT = "178"; 
	//voltage
	//public static final String ELECTRIC_FIELD_STRENGTH_MINIMUM = "179"; 
	public static final String ELECTRIC_FIELD_STRENGTH_RANGES = "180";
	public static final String ELECTRIC_FIELD_STRENGTH_TEXT = "181"; 
	//electric field strength
	//public static final String CURRENT_DENSITY_MINIMUM = "182"; 
	public static final String CURRENT_DENSITY_RANGES = "183";
	public static final String CURRENT_DENSITY_TEXT = "184"; 
	//current density
	//public static final String ENERGY_MINIMUM = "185"; 
	public static final String ENERGY_RANGES = "186";
	public static final String ENERGY_TEXT = "187"; 
	//energy
	//public static final String ELECTRICAL_RESISTANCE_MINIMUM = "188"; 
	public static final String ELECTRICAL_RESISTANCE_RANGES = "189";
	public static final String ELECTRICAL_RESISTANCE_TEXT = "190"; 
	//electrical resistance
	//public static final String ELECTRICAL_RESISTIVITY_MINIMUM = "191"; 
	public static final String ELECTRICAL_RESISTIVITY_RANGES = "192";
	public static final String ELECTRICAL_RESISTIVITY_TEXT = "193"; 
	//electrical resistivity
	//public static final String ELECTRON_VOLT_ENERGY_MINIMUM = "194"; 
	public static final String ELECTRON_VOLT_ENERGY_RANGES = "195";
	public static final String ELECTRON_VOLT_ENERGY_TEXT = "196"; 
	//electron volt energy
	//public static final String CAPACITANCE_MINIMUM = "197"; 
	public static final String CAPACITANCE_RANGES = "198";
	public static final String CAPACITANCE_TEXT = "199"; 
	//capacitance
	//public static final String PERMITTIVITY_MINIMUM = "200"; 
	public static final String PERMITTIVITY_RANGES = "201";
	public static final String PERMITTIVITY_TEXT = "202"; 
	//permittivity
	//public static final String FREQUENCY_MINIMUM = "203"; 
	public static final String FREQUENCY_RANGES = "204";
	public static final String FREQUENCY_TEXT = "205"; 
	//frequency
	//public static final String POWER_MINIMUM = "206"; 
	public static final String POWER_RANGES = "207";
	public static final String POWER_TEXT = "208";  
	//power
	//public static final String APPARENT_POWER_MINIMUM = "209"; 
	public static final String APPARENT_POWER_RANGES = "210";
	public static final String APPARENT_POWER_TEXT = "211"; 
	//apparent power 
	//public static final String REACTIVE_POWER_MINIMUM = "212"; 
	public static final String REACTIVE_POWER_RANGES = "213";
	public static final String REACTIVE_POWER_TEXT = "214"; 
	//reactive power
	//public static final String HEAT_FLUX_DENSITY_MINIMUM = "215"; 
	public static final String HEAT_FLUX_DENSITY_RANGES = "216";
	public static final String HEAT_FLUX_DENSITY_TEXT = "217";  
	//heat flux density
	//public static final String PERCENTAGE_MINIMUM = "218"; 
	public static final String PERCENTAGE_RANGES = "219";
	public static final String PERCENTAGE_TEXT = "220"; 
	//percentage
	//public static final String MAGNETIC_FLUX_DENSITY_MINIMUM = "221"; 
	public static final String MAGNETIC_FLUX_DENSITY_RANGES = "222";
	public static final String MAGNETIC_FLUX_DENSITY_TEXT = "223";
	//magnetic flux density
	//public static final String MAGNETIC_FLUX_MINIMUM = "224"; 
	public static final String MAGNETIC_FLUX_RANGES = "225";
	public static final String MAGNETIC_FLUX_TEXT = "226";
	//magnetic flux
	//public static final String INDUCTANCE_MINIMUM = "227"; 
	public static final String INDUCTANCE_RANGES = "228";
	public static final String INDUCTANCE_TEXT = "229";
	//inductance
	//public static final String PERMEABILITY_MINIMUM = "230"; 
	public static final String PERMEABILITY_RANGES = "231";
	public static final String PERMEABILITY_TEXT = "232";
	//permeability
	//public static final String ELECTRIC_CHARGE_MINIMUM = "233"; 
	public static final String ELECTRIC_CHARGE_RANGES = "234";
	public static final String ELECTRIC_CHARGE_TEXT = "235";
	//electric charge
	//public static final String VOLUME_CHARGE_DENSITY_MINIMUM = "236"; 
	public static final String VOLUME_CHARGE_DENSITY_RANGES = "237";
	public static final String VOLUME_CHARGE_DENSITY_TEXT = "238";
	//volume charge density
	//public static final String SURFACE_CHARGE_DENSITY_MINIMUM = "239"; 
	public static final String SURFACE_CHARGE_DENSITY_RANGES = "240";
	public static final String SURFACE_CHARGE_DENSITY_TEXT = "241";
	//surface charge density
	//public static final String LINEAR_CHARGE_DENSITY_MINIMUM = "242"; 
	public static final String LINEAR_CHARGE_DENSITY_RANGES = "243";
	public static final String LINEAR_CHARGE_DENSITY_TEXT = "244";
	//linear charge density
	//public static final String DECIBEL_MINIMUM = "245"; 
	public static final String DECIBEL_RANGES = "246";
	public static final String DECIBEL_TEXT = "247";
	//decibel 
	//public static final String LUMINOUS_FLUX_MINIMUM = "248"; 
	public static final String LUMINOUS_FLUX_RANGES = "249";
	public static final String LUMINOUS_FLUX_TEXT = "250";
	//luminous flux 
	//public static final String ILLUMINANCE_MINIMUM = "251"; 
	public static final String ILLUMINANCE_RANGES = "252";
	public static final String ILLUMINANCE_TEXT = "253";
	//illuminance
	//public static final String BIT_RATE_MINIMUM = "254"; 
	public static final String BIT_RATE_RANGES = "255";
	public static final String BIT_RATE_TEXT = "256";
	//bit rate
	//public static final String PICTURE_ELEMENT_MINIMUM = "257"; 
	public static final String PICTURE_ELEMENT_RANGES = "258";
	public static final String PICTURE_ELEMENT_TEXT = "259";
	//picture element
	//public static final String MASS_DENSITY_MINIMUM = "260"; 
	public static final String MASS_DENSITY_RANGES = "261";
	public static final String MASS_DENSITY_TEXT = "262";
	//mass density
	//public static final String MASS_FLOW_RATE_MINIMUM = "263"; 
	public static final String MASS_FLOW_RATE_RANGES = "264";
	public static final String MASS_FLOW_RATE_TEXT = "265";
	//mass flow rate
	//public static final String VOLUMETRIC_FLOW_RATE_MINIMUM = "266"; 
	public static final String VOLUMETRIC_FLOW_RATE_RANGES = "267";
	public static final String VOLUMETRIC_FLOW_RATE_TEXT = "268"; 
	//volumetric flow rate
	//public static final String UNIT_OF_INFORMATION_MINIMUM = "269"; 
	public static final String UNIT_OF_INFORMATION_RANGES = "270";
	public static final String UNIT_OF_INFORMATION_TEXT = "271";  
	//unit of information 
	//public static final String ANGLE_MINIMUM = "272"; 
	public static final String ANGLE_RANGES = "273";
	public static final String ANGLE_TEXT = "274";  
	//angle
	//public static final String SOLID_ANGLE_MINIMUM = "275"; 
	public static final String SOLID_ANGLE_RANGES = "276";
	public static final String SOLID_ANGLE_TEXT = "277";  
	//solid angle
	//public static final String PRESSURE_MINIMUM = "278"; 
	public static final String PRESSURE_RANGES = "279";
	public static final String PRESSURE_TEXT = "280"; 
	//pressure
	//public static final String DYNAMIC_VISCOSITY_MINIMUM = "281"; 
	public static final String DYNAMIC_VISCOSITY_RANGES = "282";
	public static final String DYNAMIC_VISCOSITY_TEXT = "283"; 
	//dynamic viscosity
	//public static final String FORCE_MINIMUM = "284"; 
	public static final String FORCE_RANGES = "285";
	public static final String FORCE_TEXT = "286";
	//force
	//public static final String TORQUE_MINIMUM = "287"; 
	public static final String TORQUE_RANGES = "288";
	public static final String TORQUE_TEXT = "289";
	//torque
	
	//public static final String AREA_MINIMUM = "290"; 
	public static final String AREA_RANGES = "291";
	public static final String AREA_TEXT = "292";
	//area
	
	//public static final String VOLUME_MINIMUM = "293"; 
	public static final String VOLUME_RANGES = "294";
	public static final String VOLUME_TEXT = "295";
	//volume 
	
	//public static final String VELOCITY_MINIMUM = "296"; 
	public static final String VELOCITY_RANGES = "297";
	public static final String VELOCITY_TEXT = "298";       
	//velocity
	
	//public static final String ACCELERATION_MINIMUM = "299"; 
	public static final String ACCELERATION_RANGES = "300";
	public static final String ACCELERATION_TEXT = "301";   
	//acceleration
	
	//public static final String ANGULAR_VELOCITY_MINIMUM = "302"; 
	public static final String ANGULAR_VELOCITY_RANGES = "303";
	public static final String ANGULAR_VELOCITY_TEXT = "304";    
	//angular velocity
	
	//public static final String ROTATIONAL_SPEED_MINIMUM = "305"; 
	public static final String ROTATIONAL_SPEED_RANGES = "306";
	public static final String ROTATIONAL_SPEED_TEXT = "307";   
	//rotational speed 
	
	//public static final String AGE_MINIMUM = "308"; 
	public static final String AGE_RANGES = "309";
	public static final String AGE_TEXT = "310"; 
	//age
	
	//public static final String MOLAR_MASS_MINIMUM = "311"; 
	public static final String MOLAR_MASS_RANGES = "312";
	public static final String MOLAR_MASS_TEXT = "313"; 
	//molar mass
	
	//public static final String MOLALITY_OF_SUBSTANCE_MINIMUM = "314"; 
	public static final String MOLALITY_OF_SUBSTANCE_RANGES = "315";
	public static final String MOLALITY_OF_SUBSTANCE_TEXT = "316"; 
	//molality of substance
	
	//public static final String PH_VALUE_MINIMUM = "317"; 
	public static final String PH_VALUE_RANGES = "318";
	public static final String PH_VALUE_TEXT = "319"; 
	//pH value
	
	//public static final String RADIOACTIVITY_MINIMUM = "320"; 
	public static final String RADIOACTIVITY_RANGES = "321";
	public static final String RADIOACTIVITY_TEXT = "322"; 
	//radioactivity 
	
	//public static final String ABSORBED_DOSE_MINIMUM = "323"; 
	public static final String ABSORBED_DOSE_RANGES = "324";
	public static final String ABSORBED_DOSE_TEXT = "325"; 
	//absorbed dose 
	
	//public static final String DOSE_EQUIVALENT_MINIMUM = "326"; 
	public static final String DOSE_EQUIVALENT_RANGES = "327";
	public static final String DOSE_EQUIVALENT_TEXT = "328";
	//dose equivalent
	
	//public static final String RADIATION_EXPOSURE_MINIMUM = "329"; 
	public static final String RADIATION_EXPOSURE_RANGES = "330";
	public static final String RADIATION_EXPOSURE_TEXT = "331";
	//radiation exposure
	
	//public static final String CATALYTIC_ACTIVITY_MINIMUM = "332"; 
	public static final String CATALYTIC_ACTIVITY_RANGES = "333";
	public static final String CATALYTIC_ACTIVITY_TEXT = "334";
	//catalytic activity
	
	//public static final String ELECTRON_VOLT_MINIMUM = "335"; 
	public static final String ELECTRON_VOLT_RANGES = "336";
	public static final String ELECTRON_VOLT_TEXT = "337";
	//electron_volt
	
	//public static final String LUMINANCE_MINIMUM = "338"; 
	public static final String LUMINANCE_RANGES = "339";
	public static final String LUMINANCE_TEXT = "340";
	//Luminance
	
	//public static final String LUMINANCE_EFFICACY_MINIMUM = "341"; 
	public static final String LUMINANCE_EFFICACY_RANGES = "342";
	public static final String LUMINANCE_EFFICACY_TEXT = "343";
	//Luminance_Efficacy
	
	//public static final String LUMINANCE_EFFICIENCY_MINIMUM = "344"; 
	public static final String LUMINANCE_EFFICIENCY_RANGES = "345";
	public static final String LUMINANCE_EFFICIENCY_TEXT = "346";
	//Luminance_Efficiency
	
	//public static final String MAGNETIC_FIELD_STRENGTH_MINIMUM = "347"; 
	public static final String MAGNETIC_FIELD_STRENGTH_RANGES = "348";
	public static final String MAGNETIC_FIELD_STRENGTH_TEXT = "349";
	//Magnetic_Field_Strength
	
	//public static final String SPECTRAL_EFFICIENCY_MINIMUM = "350"; 
	public static final String SPECTRAL_EFFICIENCY_RANGES = "351";
	public static final String SPECTRAL_EFFICIENCY_TEXT = "352";
	//Spectral_Efficiency
	
	//public static final String SURFACE_POWER_DENSITY_MINIMUM = "353"; 
	public static final String SURFACE_POWER_DENSITY_RANGES = "354";
	public static final String SURFACE_POWER_DENSITY_TEXT = "355";
	//Surface_Power_Density
	
	public static final String NUMERICALUNITS = "356";
	public static final String EID = "357";
	public static final String DEPARTMENTID = "358";
	//eid
	
	//New georef fields 2016-03-16
	
	public static final String TITLE_OF_COLLECTION = "359";
	public static final String UNIVERSITY = "360";
	public static final String TYPE_OF_DEGREE = "361";
	public static final String ANNOTATION = "362";
	public static final String MAP_SCALE = "363";
	public static final String MAP_TYPE = "364";
	public static final String SOURCE_NOTE = "365";
	
	// add Grant info 2016-03-18
	
	public static final String GRANTID = "366";
	public static final String GRANTAGENCY = "367";

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
