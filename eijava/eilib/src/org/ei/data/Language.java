package org.ei.data;
import java.util.*;

public class Language
{
	public static Hashtable iso639_language = new Hashtable();

	static
	{
		iso639_language.put("AA","Afar");
		iso639_language.put("AB","Abkhazian");
		iso639_language.put("AF","Afrikaans");
		iso639_language.put("AM","Amharic");
		iso639_language.put("AR","Arabic");
		iso639_language.put("AS","Assamese");
		iso639_language.put("AY","Aymara");
		iso639_language.put("AZ","Azerbaijani");
		iso639_language.put("BA","Bashkir");
		iso639_language.put("BE","Byelorussian");
		iso639_language.put("BG","Bulgarian");
		iso639_language.put("BH","Bihari");
		iso639_language.put("BI","Bislama");
		iso639_language.put("BN","Bengali");
		iso639_language.put("BO","Tibetan");
		iso639_language.put("BR","Breton");
		iso639_language.put("CA","Catalan");
		iso639_language.put("CO","Corsican");
		iso639_language.put("CS","Czech");
		iso639_language.put("CY","Welsh");
		iso639_language.put("DA","Danish");
		iso639_language.put("DE","German");
		iso639_language.put("DZ","Bhutani");
		iso639_language.put("EL","Greek");
		iso639_language.put("EN","English");
		iso639_language.put("EO","Esperanto");
		iso639_language.put("ES","Spanish");
		iso639_language.put("ET","Estonian");
		iso639_language.put("EU","Basque");
		iso639_language.put("FA","Persian");
		iso639_language.put("FI","Finnish");
		iso639_language.put("FJ","Fiji");
		iso639_language.put("FO","Faeroese");
		iso639_language.put("FR","French");
		iso639_language.put("FY","Frisian");
		iso639_language.put("GA","Irish");
		iso639_language.put("GD","Gaelic");
		iso639_language.put("GL","Galician");
		iso639_language.put("GN","Guarani");
		iso639_language.put("GU","Gujarati");
		iso639_language.put("HA","Hausa");
		iso639_language.put("HI","Hindi");
		iso639_language.put("HR","Croatian");
		iso639_language.put("HU","Hungarian");
		iso639_language.put("HY","Armenian");
		iso639_language.put("IA","Interlingua");
		iso639_language.put("IE","Interlingue");
		iso639_language.put("IK","Inupiak");
		iso639_language.put("IN","Indonesian");
		iso639_language.put("IS","Icelandic");
		iso639_language.put("IT","Italian");
		iso639_language.put("IW","Hebrew");
		iso639_language.put("JA","Japanese");
		iso639_language.put("JI","Yiddish");
		iso639_language.put("JW","Javanese");
		iso639_language.put("KA","Georgian");
		iso639_language.put("KK","Kazakh");
		iso639_language.put("KL","Greenlandic");
		iso639_language.put("KM","Cambodian");
		iso639_language.put("KN","Kannada");
		iso639_language.put("KO","Korean");
		iso639_language.put("KS","Kashmiri");
		iso639_language.put("KU","Kurdish");
		iso639_language.put("KY","Kirghiz");
		iso639_language.put("LA","Latin");
		iso639_language.put("LN","Lingala");
		iso639_language.put("LO","Laothian");
		iso639_language.put("LT","Lithuanian");
		iso639_language.put("LV","Latvian");
		iso639_language.put("MG","Malagasy");
		iso639_language.put("MI","Maori");
		iso639_language.put("MK","Macedonian");
		iso639_language.put("ML","Malayalam");
		iso639_language.put("MN","Mongolian");
		iso639_language.put("MO","Moldavian");
		iso639_language.put("MR","Marathi");
		iso639_language.put("MS","Malay");
		iso639_language.put("MT","Maltese");
		iso639_language.put("MY","Burmese");
		iso639_language.put("NA","Nauru");
		iso639_language.put("NE","Nepali");
		iso639_language.put("NL","Dutch");
		iso639_language.put("NN","Norwegian (Nynorsk)");
		iso639_language.put("NO","Norwegian");
		iso639_language.put("OC","Occitan");
		iso639_language.put("OM","Oromo");
		iso639_language.put("OR","Oriya");
		iso639_language.put("PA","Punjabi");
		iso639_language.put("PL","Polish");
		iso639_language.put("PS","Pashto");
		iso639_language.put("PT","Portuguese");
		iso639_language.put("QU","Quechua");
		iso639_language.put("RM","Rhaeto-Romance");
		iso639_language.put("RN","Kirundi");
		iso639_language.put("RO","Romanian");
		iso639_language.put("RU","Russian");
		iso639_language.put("RW","Kinyarwanda");
		iso639_language.put("SA","Sanskrit");
		iso639_language.put("SD","Sindhi");
		iso639_language.put("SG","Sangro");
		iso639_language.put("SH","Serbo-Croatian");
		iso639_language.put("SI","Singhalese");
		iso639_language.put("SK","Slovak");
		iso639_language.put("SL","Slovenian");
		iso639_language.put("SM","Samoan");
		iso639_language.put("SN","Shona");
		iso639_language.put("SO","Somali");
		iso639_language.put("SQ","Albanian");
		iso639_language.put("SR","Serbian");
		iso639_language.put("SS","Siswati");
		iso639_language.put("ST","Sesotho");
		iso639_language.put("SU","Sudanese");
		iso639_language.put("SV","Swedish");
		iso639_language.put("SW","Swahili");
		iso639_language.put("TA","Tamil");
		iso639_language.put("TE","Tegulu");
		iso639_language.put("TG","Tajik");
		iso639_language.put("TH","Thai");
		iso639_language.put("TI","Tigrinya");
		iso639_language.put("TK","Turkmen");
		iso639_language.put("TL","Tagalog");
		iso639_language.put("TN","Setswana");
		iso639_language.put("TO","Tonga");
		iso639_language.put("TR","Turkish");
		iso639_language.put("TS","Tsonga");
		iso639_language.put("TT","Tatar");
		iso639_language.put("TW","Twi");
		iso639_language.put("UK","Ukrainian");
		iso639_language.put("UR","Urdu");
		iso639_language.put("UZ","Uzbek");
		iso639_language.put("VI","Vietnamese");
		iso639_language.put("VO","Volapuk");
		iso639_language.put("WO","Wolof");
		iso639_language.put("XH","Xhosa");
		iso639_language.put("YO","Yoruba");
		iso639_language.put("ZH","Chinese");
		iso639_language.put("ZU","Zulu");
	}

	public static String getIso639Language(String code)
	{
		String lName = null;
		lName = (String)iso639_language.get(code.toUpperCase());
		if(lName==null)
		{
			lName = code;
		}
		return lName.toString();
	}

}
