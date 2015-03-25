package org.ei.dataloading.xmlDataLoading;

import java.util.Hashtable;
import java.util.StringTokenizer;

public class CountryLanguageConverter extends BaseElement {
    public static Hashtable<String, String> iso3166_country = null;
    public static Hashtable<String, String> iso639_language = null;
    public String delimited = ";";
    public String iddelimited = String.valueOf(DataParser.delimited29);

    public CountryLanguageConverter(String delimited) {
        if (delimited != null) {
            this.delimited = delimited;
        }

        if (iso3166_country == null) {
            iso3166_country = new Hashtable<String, String>();
            loadCountry();
        }

        if (iso639_language == null) {
            iso639_language = new Hashtable<String, String>();
            loadLanguage();
        }

    }

    private void loadLanguage() {
        iso639_language.put("AA", "Afar");
        iso639_language.put("AB", "Abkhazian");
        iso639_language.put("AF", "Afrikaans");
        iso639_language.put("AM", "Amharic");
        iso639_language.put("AR", "Arabic");
        iso639_language.put("AS", "Assamese");
        iso639_language.put("AY", "Aymara");
        iso639_language.put("AZ", "Azerbaijani");
        iso639_language.put("BA", "Bashkir");
        iso639_language.put("BE", "Byelorussian");
        iso639_language.put("BG", "Bulgarian");
        iso639_language.put("BH", "Bihari");
        iso639_language.put("BI", "Bislama");
        iso639_language.put("BN", "Bengali");
        iso639_language.put("BO", "Tibetan");
        iso639_language.put("BR", "Breton");
        iso639_language.put("CA", "Catalan");
        iso639_language.put("CO", "Corsican");
        iso639_language.put("CS", "Czech");
        iso639_language.put("CY", "Welsh");
        iso639_language.put("DA", "Danish");
        iso639_language.put("DE", "German");
        iso639_language.put("DZ", "Bhutani");
        iso639_language.put("EL", "Greek");
        iso639_language.put("EN", "English");
        iso639_language.put("EO", "Esperanto");
        iso639_language.put("ES", "Spanish");
        iso639_language.put("ET", "Estonian");
        iso639_language.put("EU", "Basque");
        iso639_language.put("FA", "Persian");
        iso639_language.put("FI", "Finnish");
        iso639_language.put("FJ", "Fiji");
        iso639_language.put("FO", "Faeroese");
        iso639_language.put("FR", "French");
        iso639_language.put("FY", "Frisian");
        iso639_language.put("GA", "Irish");
        iso639_language.put("GD", "Gaelic");
        iso639_language.put("GL", "Galician");
        iso639_language.put("GN", "Guarani");
        iso639_language.put("GU", "Gujarati");
        iso639_language.put("HA", "Hausa");
        iso639_language.put("HI", "Hindi");
        iso639_language.put("HR", "Croatian");
        iso639_language.put("HU", "Hungarian");
        iso639_language.put("HY", "Armenian");
        iso639_language.put("IA", "Interlingua");
        iso639_language.put("IE", "Interlingue");
        iso639_language.put("IK", "Inupiak");
        iso639_language.put("IN", "Indonesian");
        iso639_language.put("IS", "Icelandic");
        iso639_language.put("IT", "Italian");
        iso639_language.put("IW", "Hebrew");
        iso639_language.put("JA", "Japanese");
        iso639_language.put("JI", "Yiddish");
        iso639_language.put("JW", "Javanese");
        iso639_language.put("KA", "Georgian");
        iso639_language.put("KK", "Kazakh");
        iso639_language.put("KL", "Greenlandic");
        iso639_language.put("KM", "Cambodian");
        iso639_language.put("KN", "Kannada");
        iso639_language.put("KO", "Korean");
        iso639_language.put("KS", "Kashmiri");
        iso639_language.put("KU", "Kurdish");
        iso639_language.put("KY", "Kirghiz");
        iso639_language.put("LA", "Latin");
        iso639_language.put("LN", "Lingala");
        iso639_language.put("LO", "Laothian");
        iso639_language.put("LT", "Lithuanian");
        iso639_language.put("LV", "Latvian");
        iso639_language.put("MG", "Malagasy");
        iso639_language.put("MI", "Maori");
        iso639_language.put("MK", "Macedonian");
        iso639_language.put("ML", "Malayalam");
        iso639_language.put("MN", "Mongolian");
        iso639_language.put("MO", "Moldavian");
        iso639_language.put("MR", "Marathi");
        iso639_language.put("MS", "Malay");
        iso639_language.put("MT", "Maltese");
        iso639_language.put("MY", "Burmese");
        iso639_language.put("NA", "Nauru");
        iso639_language.put("NE", "Nepali");
        iso639_language.put("NL", "Dutch");
        iso639_language.put("NN", "Norwegian (Nynorsk)");
        iso639_language.put("NO", "Norwegian");
        iso639_language.put("OC", "Occitan");
        iso639_language.put("OM", "Oromo");
        iso639_language.put("OR", "Oriya");
        iso639_language.put("PA", "Punjabi");
        iso639_language.put("PL", "Polish");
        iso639_language.put("PS", "Pashto");
        iso639_language.put("PT", "Portuguese");
        iso639_language.put("QU", "Quechua");
        iso639_language.put("RM", "Rhaeto-Romance");
        iso639_language.put("RN", "Kirundi");
        iso639_language.put("RO", "Romanian");
        iso639_language.put("RU", "Russian");
        iso639_language.put("RW", "Kinyarwanda");
        iso639_language.put("SA", "Sanskrit");
        iso639_language.put("SD", "Sindhi");
        iso639_language.put("SG", "Sangro");
        iso639_language.put("SH", "Serbo-Croatian");
        iso639_language.put("SI", "Singhalese");
        iso639_language.put("SK", "Slovak");
        iso639_language.put("SL", "Slovenian");
        iso639_language.put("SM", "Samoan");
        iso639_language.put("SN", "Shona");
        iso639_language.put("SO", "Somali");
        iso639_language.put("SQ", "Albanian");
        iso639_language.put("SR", "Serbian");
        iso639_language.put("SS", "Siswati");
        iso639_language.put("ST", "Sesotho");
        iso639_language.put("SU", "Sudanese");
        iso639_language.put("SV", "Swedish");
        iso639_language.put("SW", "Swahili");
        iso639_language.put("TA", "Tamil");
        iso639_language.put("TE", "Tegulu");
        iso639_language.put("TG", "Tajik");
        iso639_language.put("TH", "Thai");
        iso639_language.put("TI", "Tigrinya");
        iso639_language.put("TK", "Turkmen");
        iso639_language.put("TL", "Tagalog");
        iso639_language.put("TN", "Setswana");
        iso639_language.put("TO", "Tonga");
        iso639_language.put("TR", "Turkish");
        iso639_language.put("TS", "Tsonga");
        iso639_language.put("TT", "Tatar");
        iso639_language.put("TW", "Twi");
        iso639_language.put("UK", "Ukrainian");
        iso639_language.put("UR", "Urdu");
        iso639_language.put("UZ", "Uzbek");
        iso639_language.put("VI", "Vietnamese");
        iso639_language.put("VO", "Volapuk");
        iso639_language.put("WO", "Wolof");
        iso639_language.put("XH", "Xhosa");
        iso639_language.put("YO", "Yoruba");
        iso639_language.put("ZH", "Chinese");
        iso639_language.put("ZU", "Zulu");
    }

    private void loadCountry() {
        iso3166_country.put("AD", "Andorra");
        iso3166_country.put("AE", "United Arab Emirates");
        iso3166_country.put("AF", "Afghanistan");
        iso3166_country.put("AG", "Antigua & Barbuda");
        iso3166_country.put("AI", "Anguilla");
        iso3166_country.put("AL", "Albania");
        iso3166_country.put("AM", "Armenia");
        iso3166_country.put("AN", "Netherlands Antilles");
        iso3166_country.put("AO", "Angola");
        iso3166_country.put("AQ", "Antarctica");
        iso3166_country.put("AR", "Argentina");
        iso3166_country.put("AS", "American Samoa");
        iso3166_country.put("AT", "Austria");
        iso3166_country.put("AU", "Australia");
        iso3166_country.put("AW", "Aruba");
        iso3166_country.put("AZ", "Azerbaijan");
        iso3166_country.put("BA", "Bosnia and Herzegovina");
        iso3166_country.put("BB", "Barbados");
        iso3166_country.put("BD", "Bangladesh");
        iso3166_country.put("BE", "Belgium");
        iso3166_country.put("BF", "Burkina Faso");
        iso3166_country.put("BG", "Bulgaria");
        iso3166_country.put("BH", "Bahrain");
        iso3166_country.put("BI", "Burundi");
        iso3166_country.put("BJ", "Benin");
        iso3166_country.put("BM", "Bermuda");
        iso3166_country.put("BN", "Brunei Darussalam");
        iso3166_country.put("BO", "Bolivia");
        iso3166_country.put("BR", "Brazil");
        iso3166_country.put("BS", "Bahama");
        iso3166_country.put("BT", "Bhutan");
        iso3166_country.put("BU", "Burma (no longer exists)");
        iso3166_country.put("BV", "Bouvet Island");
        iso3166_country.put("BW", "Botswana");
        iso3166_country.put("BY", "Belarus");
        iso3166_country.put("BZ", "Belize");
        iso3166_country.put("CA", "Canada");
        iso3166_country.put("CC", "Cocos (Keeling) Islands");
        iso3166_country.put("CD", "Democratic Republic Congo");
        iso3166_country.put("CF", "Central African Republic");
        iso3166_country.put("CG", "Congo");
        iso3166_country.put("CH", "Switzerland");
        iso3166_country.put("CI", "C&ocirc;te D'ivoire (Ivory Coast)");
        iso3166_country.put("CK", "Cook Iislands");
        iso3166_country.put("CL", "Chile");
        iso3166_country.put("CM", "Cameroon");
        iso3166_country.put("CN", "China");
        iso3166_country.put("CO", "Colombia");
        iso3166_country.put("CR", "Costa Rica");
        iso3166_country.put("CS", "Czechoslovakia (no longer exists)");
        iso3166_country.put("CU", "Cuba");
        iso3166_country.put("CV", "Cape Verde");
        iso3166_country.put("CX", "Christmas Island");
        iso3166_country.put("CY", "Cyprus");
        iso3166_country.put("CZ", "Czech Republic");
        iso3166_country.put("DD", "German Democratic Republic (no longer exists)");
        iso3166_country.put("DE", "Germany");
        iso3166_country.put("DJ", "Djibouti");
        iso3166_country.put("DK", "Denmark");
        iso3166_country.put("DM", "Dominica");
        iso3166_country.put("DO", "Dominican Republic");
        iso3166_country.put("DZ", "Algeria");
        iso3166_country.put("EC", "Ecuador");
        iso3166_country.put("EE", "Estonia");
        iso3166_country.put("EG", "Egypt");
        iso3166_country.put("EH", "Western Sahara");
        iso3166_country.put("ER", "Eritrea");
        iso3166_country.put("ES", "Spain");
        iso3166_country.put("ET", "Ethiopia");
        iso3166_country.put("FI", "Finland");
        iso3166_country.put("FJ", "Fiji");
        iso3166_country.put("FK", "Falkland Islands (Malvinas)");
        iso3166_country.put("FM", "Micronesia");
        iso3166_country.put("FO", "Faroe Islands");
        iso3166_country.put("FR", "France");
        iso3166_country.put("FX", "France, Metropolitan");
        iso3166_country.put("GA", "Gabon");
        iso3166_country.put("GB", "United Kingdom (Great Britain)");
        iso3166_country.put("GD", "Grenada");
        iso3166_country.put("GE", "Georgia");
        iso3166_country.put("GF", "French Guiana");
        iso3166_country.put("GH", "Ghana");
        iso3166_country.put("GI", "Gibraltar");
        iso3166_country.put("GL", "Greenland");
        iso3166_country.put("GM", "Gambia");
        iso3166_country.put("GN", "Guinea");
        iso3166_country.put("GP", "Guadeloupe");
        iso3166_country.put("GQ", "Equatorial Guinea");
        iso3166_country.put("GR", "Greece");
        iso3166_country.put("GS", "South Georgia and the South Sandwich Islands");
        iso3166_country.put("GT", "Guatemala");
        iso3166_country.put("GU", "Guam");
        iso3166_country.put("GW", "Guinea-Bissau");
        iso3166_country.put("GY", "Guyana");
        iso3166_country.put("HK", "Hong Kong");
        iso3166_country.put("HM", "Heard & McDonald Islands");
        iso3166_country.put("HN", "Honduras");
        iso3166_country.put("HR", "Croatia");
        iso3166_country.put("HT", "Haiti");
        iso3166_country.put("HU", "Hungary");
        iso3166_country.put("ID", "Indonesia");
        iso3166_country.put("IE", "Ireland");
        iso3166_country.put("IL", "Israel");
        iso3166_country.put("IN", "India");
        iso3166_country.put("IO", "British Indian Ocean Territory");
        iso3166_country.put("IQ", "Iraq");
        iso3166_country.put("IR", "Islamic Republic of Iran");
        iso3166_country.put("IS", "Iceland");
        iso3166_country.put("IT", "Italy");
        iso3166_country.put("JM", "Jamaica");
        iso3166_country.put("JO", "Jordan");
        iso3166_country.put("JP", "Japan");
        iso3166_country.put("KE", "Kenya");
        iso3166_country.put("KG", "Kyrgyzstan");
        iso3166_country.put("KH", "Cambodia");
        iso3166_country.put("KI", "Kiribati");
        iso3166_country.put("KM", "Comoros");
        iso3166_country.put("KN", "St. Kitts and Nevis");
        iso3166_country.put("KP", "Korea, Democratic People's Republic of");
        iso3166_country.put("KR", "Korea, Republic of");
        iso3166_country.put("KW", "Kuwait");
        iso3166_country.put("KY", "Cayman Islands");
        iso3166_country.put("KZ", "Kazakhstan");
        iso3166_country.put("LA", "Lao People's Democratic Republic");
        iso3166_country.put("LB", "Lebanon");
        iso3166_country.put("LC", "Saint Lucia");
        iso3166_country.put("LI", "Liechtenstein");
        iso3166_country.put("LK", "Sri Lanka");
        iso3166_country.put("LR", "Liberia");
        iso3166_country.put("LS", "Lesotho");
        iso3166_country.put("LT", "Lithuania");
        iso3166_country.put("LU", "Luxembourg");
        iso3166_country.put("LV", "Latvia");
        iso3166_country.put("LY", "Libyan Arab Jamahiriya");
        iso3166_country.put("MA", "Morocco");
        iso3166_country.put("MC", "Monaco");
        iso3166_country.put("MD", "Moldova, Republic of");
        iso3166_country.put("MG", "Madagascar");
        iso3166_country.put("MH", "Marshall Islands");
        iso3166_country.put("ML", "Mali");
        iso3166_country.put("MN", "Mongolia");
        iso3166_country.put("MM", "Myanmar");
        iso3166_country.put("MO", "Macau");
        iso3166_country.put("MP", "Northern Mariana Islands");
        iso3166_country.put("MQ", "Martinique");
        iso3166_country.put("MR", "Mauritania");
        iso3166_country.put("MS", "Monserrat");
        iso3166_country.put("MT", "Malta");
        iso3166_country.put("MU", "Mauritius");
        iso3166_country.put("MV", "Maldives");
        iso3166_country.put("MW", "Malawi");
        iso3166_country.put("MX", "Mexico");
        iso3166_country.put("MY", "Malaysia");
        iso3166_country.put("MZ", "Mozambique");
        iso3166_country.put("NA", "Namibia");
        iso3166_country.put("NC", "New Caledonia");
        iso3166_country.put("NE", "Niger");
        iso3166_country.put("NF", "Norfolk Island");
        iso3166_country.put("NG", "Nigeria");
        iso3166_country.put("NI", "Nicaragua");
        iso3166_country.put("NL", "Netherlands");
        iso3166_country.put("NO", "Norway");
        iso3166_country.put("NP", "Nepal");
        iso3166_country.put("NR", "Nauru");
        iso3166_country.put("NT", "Neutral Zone ");
        iso3166_country.put("NU", "Niue");
        iso3166_country.put("NZ", "New Zealand");
        iso3166_country.put("OM", "Oman");
        iso3166_country.put("PA", "Panama");
        iso3166_country.put("PE", "Peru");
        iso3166_country.put("PF", "French Polynesia");
        iso3166_country.put("PG", "Papua New Guinea");
        iso3166_country.put("PH", "Philippines");
        iso3166_country.put("PK", "Pakistan");
        iso3166_country.put("PL", "Poland");
        iso3166_country.put("PM", "St. Pierre & Miquelon");
        iso3166_country.put("PN", "Pitcairn");
        iso3166_country.put("PR", "Puerto Rico");
        iso3166_country.put("PT", "Portugal");
        iso3166_country.put("PW", "Palau");
        iso3166_country.put("PY", "Paraguay");
        iso3166_country.put("QA", "Qatar");
        iso3166_country.put("RE", "R&eacute;union");
        iso3166_country.put("RO", "Romania");
        iso3166_country.put("RU", "Russian Federation");
        iso3166_country.put("RW", "Rwanda");
        iso3166_country.put("SA", "Saudi Arabia");
        iso3166_country.put("SB", "Solomon Islands");
        iso3166_country.put("SC", "Seychelles");
        iso3166_country.put("SD", "Sudan");
        iso3166_country.put("SE", "Sweden");
        iso3166_country.put("SG", "Singapore");
        iso3166_country.put("SH", "St. Helena");
        iso3166_country.put("SI", "Slovenia");
        iso3166_country.put("SJ", "Svalbard & Jan Mayen Islands");
        iso3166_country.put("SK", "Slovakia");
        iso3166_country.put("SL", "Sierra Leone");
        iso3166_country.put("SM", "San Marino");
        iso3166_country.put("SN", "Senegal");
        iso3166_country.put("SO", "Somalia");
        iso3166_country.put("SR", "Suriname");
        iso3166_country.put("ST", "Sao Tome & Principe");
        iso3166_country.put("SU", "Union of Soviet Socialist Republics");
        iso3166_country.put("SV", "El Salvador");
        iso3166_country.put("SY", "Syrian Arab Republic");
        iso3166_country.put("SZ", "Swaziland");
        iso3166_country.put("TC", "Turks & Caicos Islands");
        iso3166_country.put("TD", "Chad");
        iso3166_country.put("TF", "French Southern Territories");
        iso3166_country.put("TG", "Togo");
        iso3166_country.put("TH", "Thailand");
        iso3166_country.put("TJ", "Tajikistan");
        iso3166_country.put("TK", "Tokelau");
        iso3166_country.put("TM", "Turkmenistan");
        iso3166_country.put("TN", "Tunisia");
        iso3166_country.put("TO", "Tonga");
        iso3166_country.put("TP", "East Timor");
        iso3166_country.put("TR", "Turkey");
        iso3166_country.put("TT", "Trinidad & Tobago");
        iso3166_country.put("TV", "Tuvalu");
        iso3166_country.put("TW", "Taiwan, Province of China");
        iso3166_country.put("TZ", "Tanzania, United Republic of");
        iso3166_country.put("UA", "Ukraine");
        iso3166_country.put("UG", "Uganda");
        iso3166_country.put("UM", "United States Minor Outlying Islands");
        iso3166_country.put("US", "United States of America");
        iso3166_country.put("UY", "Uruguay");
        iso3166_country.put("UZ", "Uzbekistan");
        iso3166_country.put("VA", "Vatican City State (Holy See)");
        iso3166_country.put("VC", "St. Vincent & the Grenadines");
        iso3166_country.put("VE", "Venezuela");
        iso3166_country.put("VG", "British Virgin Islands");
        iso3166_country.put("VI", "United States Virgin Islands");
        iso3166_country.put("VN", "Viet Nam");
        iso3166_country.put("VU", "Vanuatu");
        iso3166_country.put("WF", "Wallis & Futuna Islands");
        iso3166_country.put("WS", "Samoa");
        iso3166_country.put("YD", "Democratic Yemen");
        iso3166_country.put("YE", "Yemen");
        iso3166_country.put("YT", "Mayotte");
        iso3166_country.put("YU", "Yugoslavia");
        iso3166_country.put("ZA", "South Africa");
        iso3166_country.put("ZM", "Zambia");
        iso3166_country.put("ZR", "Zaire");
        iso3166_country.put("ZW", "Zimbabwe");
        iso3166_country.put("ZZ", "Unknown or unspecified country");
    }

    public String getIso3166Country(String code) {
        StringBuffer countryName = new StringBuffer();
        String cName = null;
        String[] countryArray = null;
        if (code.indexOf(delimited) >= 0) {
            countryArray = code.split(delimited);
        } else {
            countryArray = new String[1];
            countryArray[0] = code;
        }

        for (int i = 0; i < countryArray.length; i++) {
            String sCode = countryArray[i].trim();
            String id = null;
            if (sCode.indexOf(iddelimited) > -1) {
                id = sCode.substring(0, sCode.indexOf(iddelimited));
                sCode = sCode.substring(sCode.indexOf(iddelimited) + 1);
            }

            cName = (String) iso3166_country.get(sCode.toUpperCase());
            if (cName == null) {
                cName = (String) iso639_language.get(sCode.toUpperCase());
            }

            if (id != null) {
                countryName.append(id + iddelimited);
            }

            if (cName != null) {
                countryName.append(cName);
            } else {
                countryName.append(sCode);
            }

            if (i < countryArray.length - 1) {
                countryName.append(delimited);
            }
        }

        return countryName.toString();
    }

    public String getIso639Language(String code) {
        // System.out.println("delimited= "+delimited);

        StringBuffer languageName = new StringBuffer();
        String lName = null;
        if (code.indexOf(delimited) >= 0) {
            StringTokenizer tokens = new StringTokenizer(code, delimited);
            while (tokens.hasMoreTokens()) {
                String sCode = tokens.nextToken().trim();
                lName = (String) iso639_language.get(sCode.toUpperCase());
                if (lName != null)
                    languageName.append(lName + ",");
                else
                    languageName.append(sCode + ",");
            }
        } else {
            lName = (String) iso639_language.get(code.toUpperCase());
            if (lName != null)
                languageName.append((String) iso639_language.get(code.toUpperCase()));
            else
                languageName.append(code);
        }
        return languageName.toString();
    }

}
