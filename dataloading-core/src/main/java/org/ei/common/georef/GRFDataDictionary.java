package org.ei.common.georef;

import java.util.Hashtable;
import java.util.Map;

import org.ei.domain.DataDictionary;


public class GRFDataDictionary implements DataDictionary {
    private static GRFDataDictionary instance           = null;
    private Map<String, String>      categories         = null;
    private Map<String, String>      doctypes           = null;
    private Map<String, String>      bibliographiccodes = null;
    private Map<String, String>      languages          = null;
    private Map<String, String>      countries          = null;
    private Map<String, String>      termtypes          = null;
    private Map<String, String>      translatetermtypes = null;

    private GRFDataDictionary() {
        categories = new Hashtable<String, String>();
        categories.put("01", "Mineralogy");
        categories.put("01A", "General mineralogy (Includes mineralogical methods, regional studies, mineral collecting)");
        categories.put("01B", "Mineralogy of silicates");
        categories.put("01C", "Mineralogy of non-silicates");
        categories.put("02", "Geochemistry");
        categories.put("02A", "General geochemistry (Includes geochemical methods)");
        categories.put("02B", "Geochemistry of water");
        categories.put("02C", "Geochemistry of rocks, soils, and sediments");
        categories.put("02D", "Isotope geochemistry");
        categories.put("03", "Geochronology (Includes absolute age and relative age)");
        categories.put("04", "Extraterrestrial geology");
        categories.put("05", "Igneous and metamorphic petrology");
        categories
            .put(
                "05A",
                "Igneous and metamorphic petrology (Includes igneous rocks, metamorphic rocks, inclusions, intrusions, lava, magmas, metamorphism, metasomatism, meteor craters, phase equilibria, pre-Quaternary volcanism)");
        categories.put("05B", "Petrology of meteorites and tektites");
        categories.put("06", "Sedimentary petrology");
        categories
            .put(
                "06A",
                "Sedimentary petrology (Includes clay mineralogy, diagenesis, heavy minerals, reefs, sedimentary rocks, sedimentary structures, sedimentation, sediments, weathering)");
        categories.put("06B", "Petrology of coal");
        categories.put("07", "Oceanography (Includes modern marine sedimentation)");
        categories
            .put(
                "08",
                "General paleontology (Includes life origin, ichnofossils (if not related to a specific fossil group), problematic fossils, and studies which fall under more than one paleontologic category)");
        categories.put("09", "Paleobotany");
        categories.put("10", "Invertebrate paleontology");
        categories.put("11", "Vertebrate paleontology");
        categories
            .put(
                "12",
                "Stratigraphy (Includes pre-Quaternary stratigraphy, biostratigraphy, lithostratigraphy, magnetostratigraphy, paleogeography, archaeology, changes of level, paleoclimatology, lithofacies, paleoecology, biogeography)");
        categories.put("13", "Areal geology (Includes regional studies, guidebooks, and studies which fall under 3 or more categories)");
        categories.put("14", "Geologic maps (Note ,Other specific maps are found under the relevant category)");
        categories
            .put(
                "15",
                "Miscellaneous (Includes mathematical geology, general geoscience education, annual reports of geologic surveys and associations, history, geology as a profession, forensic geology)");
        categories
            .put(
                "16",
                "Structural geology (Includes deformation, structural analysis, tectonics, neotectonics, salt tectonics, epeirogeny, faults, folds, foliation, fractures, geosynclines, isostasy, lineation, orogeny)");
        categories.put("17", "General geophysics");
        categories.put("17A", "General geophysics (Includes physical properties of rocks and minerals)");
        categories
            .put(
                "17B",
                "Geophysics of minerals and rocks, Includes phase transitions, high pressure-temperature studies of rocks and minerals (applied to core and mantle composition)");
        categories
            .put(
                "18",
                "Solid-earth geophysics (Includes tectonophysics, crust, mantle, core, application of seismicity, plate tectonics, paleomagnetism, heat flow, isostasy, sea-floor spreading, magnetic field, gravity field, Earth's orbit and rotation)");
        categories.put("19", "Seismology (Includes earthquakes, seismicity, explosions, elastic waves, seismic sources)");
        categories
            .put(
                "20",
                "Applied geophysics (Includes acoustical surveys, Earth-current surveys, electrical surveys, electromagnetic surveys, gravity surveys, infrared surveys, magnetic surveys, magnetotelluric surveys, seismic surveys, geodesy, heat flow, remote sensing, well-logging)");
        categories.put("21", "Hydrogeology (Includes water resources)");
        categories
            .put(
                "22",
                "Environmental geology (Includes conservation, ecology, geologic hazards, impact statements, land use, pollution (including water pollution and soil pollution), reclamation, waste disposal)");
        categories
            .put(
                "23",
                "Geomorphology (Includes erosion, mass movements, meteor craters, cryptoexplosion features, eolian features, erosion features, fluvial features, frost action, lacustrine features, shore features, solution features, volcanic features)");
        categories
            .put(
                "24",
                "Quaternary geology (Includes Quaternary geomorphology, Quaternary glacial geology and glacial features, Quaternary stratigraphy, Quaternary archaeology, Quaternary volcanoes, Quaternary climate, Quaternary sediments, Quaternary changes of level)");
        categories.put("25", "Soils");
        categories.put("26", "Economic geology, general");
        categories.put("26A", "Economic geology, general, deposits (Includes mining geology)");
        categories.put("26B", "Economic geology, general, economics");
        categories.put("27", "Economic geology of ore deposits");
        categories.put("27A", "Economic geology, geology of ore deposits (Includes uranium ores)");
        categories.put("27B", "Economic geology, economics of ore deposits (Includes uranium ores)");
        categories.put("28", "Economic geology of nonmetal deposits");
        categories.put("28A", "Economic geology, geology of nonmetal deposits");
        categories.put("28B", "Economic geology, economics of nonmetal deposits");
        categories.put("29", "Economic geology of energy sources");
        categories.put("29A", "Economic geology, geology of energy sources (Includes petroleum (oil and gas), coal, and other energy sources)");
        categories.put("29B", "Economic geology, economics of energy sources (Includes petroleum (oil and gas), coal, and other energy sources)");
        categories
            .put(
                "30",
                "Engineering geology (Includes rock mechanics, soil mechanics, waste disposal, reclamation, dams, earthquakes, explosions, foundations, geologic hazards, highways, land subsidence, marine installations, nuclear facilities, permafrost, reservoirs, shorelines, slope stability, soil mechanics, tunnels, underground installations, waterways)");

        doctypes = new Hashtable<String, String>();
        doctypes.put("S", "Serial");
        doctypes.put("B", "Book");
        doctypes.put("R", "Report");
        doctypes.put("C", "Conference document");
        doctypes.put("M", "Map");
        doctypes.put("T", "Thesis or dissertation");
        doctypes.put("G", "In Process");

        bibliographiccodes = new Hashtable<String, String>();
        bibliographiccodes.put("A", "Analytic level");
        bibliographiccodes.put("M", "Monographic level");
        bibliographiccodes.put("C", "Collective level");
        bibliographiccodes.put("S", "Serial level");

        languages = new Hashtable<String, String>();
        languages.put("AF", "Afrikaans");
        languages.put("AL", "Albanian");
        languages.put("AH", "Amharic");
        languages.put("AR", "Arabic");
        languages.put("AM", "Armenian");
        languages.put("AZ", "Azerbaijani");
        languages.put("BK", "Bashkir");
        languages.put("BA", "Basque");
        languages.put("BE", "Belorussian");
        languages.put("BN", "Bengali");
        languages.put("BB", "Berber");
        languages.put("BT", "Breton");
        languages.put("BU", "Bulgarian");
        languages.put("BR", "Burmese");
        languages.put("CM", "Cambodian");
        languages.put("CA", "Catalan");
        languages.put("CH", "Chinese");
        languages.put("CR", "Croatian");
        languages.put("CZ", "Czech");
        languages.put("DA", "Danish");
        languages.put("DU", "Dutch");
        languages.put("EL", "English");
        languages.put("EK", "Eskimo");
        languages.put("ES", "Esperanto");
        languages.put("EN", "Estonian");
        languages.put("FA", "Faroese");
        languages.put("FI", "Finnish");
        languages.put("FL", "Flemish");
        languages.put("FR", "French");
        languages.put("FS", "Frisian");
        languages.put("GL", "Gaelic");
        languages.put("GA", "Galician");
        languages.put("GG", "Georgian");
        languages.put("GE", "German");
        languages.put("GR", "Greek");
        languages.put("GI", "Guarani");
        languages.put("GU", "Gujerati");
        languages.put("HA", "Hausa");
        languages.put("HE", "Hebrew");
        languages.put("HI", "Hindi");
        languages.put("HU", "Hungarian");
        languages.put("IC", "Icelandic");
        languages.put("IG", "Igbo");
        languages.put("IN", "Indonesian");
        languages.put("IR", "Irish");
        languages.put("IT", "Italian");
        languages.put("JA", "Japanese");
        languages.put("KZ", "Kazakhstan");
        languages.put("KI", "Kirghiz");
        languages.put("KN", "Kongo");
        languages.put("KO", "Korean");
        languages.put("KU", "Kurdish");
        languages.put("LO", "Laotian");
        languages.put("LP", "Lapp");
        languages.put("LA", "Latin");
        languages.put("LV", "Latvian");
        languages.put("LI", "Lithuanian");
        languages.put("LU", "Luba");
        languages.put("MC", "Macedonian");
        languages.put("ML", "Malagasy");
        languages.put("MA", "Malaysian");
        languages.put("MD", "Moldavian");
        languages.put("MG", "Mongol");
        languages.put("NO", "Norwegian");
        languages.put("PJ", "Panjabi");
        languages.put("PE", "Persian");
        languages.put("PO", "Polish");
        languages.put("PR", "Portuguese");
        languages.put("PV", "Provencal");
        languages.put("PU", "Pushto");
        languages.put("QU", "Quechua");
        languages.put("RO", "Romanian");
        languages.put("RH", "Romansh");
        languages.put("RU", "Russian");
        languages.put("SE", "Serbian");
        languages.put("SO", "Shona");
        languages.put("SI", "Sinhalese");
        languages.put("SL", "Slovakian");
        languages.put("SV", "Slovenian");
        languages.put("SP", "Spanish");
        languages.put("SH", "Swahili");
        languages.put("SW", "Swedish");
        languages.put("TA", "Tadzhikistan");
        languages.put("TG", "Tagalog");
        languages.put("TJ", "Tajik");
        languages.put("TM", "Tamil");
        languages.put("TH", "Thai");
        languages.put("TU", "Turkish");
        languages.put("TR", "Turkmenistan");
        languages.put("UK", "Ukrainian");
        languages.put("UR", "Urdu");
        languages.put("UZ", "Uzbekistan");
        languages.put("VN", "Vietnamese");
        languages.put("WL", "Welsh");
        languages.put("WO", "Wolof");
        languages.put("YO", "Yoruba");

        countries = new Hashtable<String, String>();
        countries.put("???", "???");
        countries.put("AFG", "Afghanistan");
        countries.put("AGO", "Angola");
        countries.put("ALB", "Albania");
        countries.put("AND", "Andorra");
        countries.put("ANT", "Netherlands Antilles");
        countries.put("ARE", "United Arab Emirates");
        countries.put("ARG", "Argentina");
        countries.put("ARM", "Armenia");
        countries.put("ASM", "American Samoa");
        countries.put("ATA", "Antarctica");
        countries.put("ATG", "Antigua");
        countries.put("ATN", "Dronning Maud Land");
        countries.put("AUS", "Australia");
        countries.put("AUT", "Austria");
        countries.put("AZE", "Azerbaijan");
        countries.put("BDI", "Burundi");
        countries.put("BEL", "Belgium");
        countries.put("BEN", "Benin");
        countries.put("BFA", "Burkina Faso");
        countries.put("BGD", "Bangladesh");
        countries.put("BGR", "Bulgaria");
        countries.put("BHS", "Bahamas");
        countries.put("BIH", "Bosnia and Herzegowina");
        countries.put("BLR", "Belarus");
        countries.put("BLZ", "Belize");
        countries.put("BMU", "Bermuda");
        countries.put("BOL", "Bolivia");
        countries.put("BRA", "Brazil");
        countries.put("BRB", "Barbados");
        countries.put("BRN", "Brunei");
        countries.put("BTN", "Bhutan");
        countries.put("BUR", "Burma");
        countries.put("BVT", "Bouvet Island");
        countries.put("BWA", "Botswana");
        countries.put("BYS", "Byelorussian SSR");
        countries.put("CAF", "Central African Republic");
        countries.put("CAN", "Canada");
        countries.put("CCK", "Cocos (Keeling) Islands");
        countries.put("CHE", "Switzerland");
        countries.put("CHL", "Chile");
        countries.put("CHN", "China");
        countries.put("CIV", "Ivory Coast");
        countries.put("CMR", "Cameroon");
        countries.put("COG", "Congo");
        countries.put("COK", "Cook Islands");
        countries.put("COL", "Colombia");
        countries.put("COM", "Comoros");
        countries.put("CPV", "Cape Verde Islands");
        countries.put("CRI", "Costa Rica");
        countries.put("CSK", "Czechoslovakia");
        countries.put("CTE", "Canton and Enderbury Islands");
        countries.put("CUB", "Cuba");
        countries.put("CXR", "Christmas Island");
        countries.put("CYM", "Cayman Islands");
        countries.put("CYP", "Cyprus");
        countries.put("CZE", "Czech Republic");
        countries.put("DDR", "German Democratic Republic");
        countries.put("DEU", "Federal Republic of Germany");
        countries.put("DJI", "Djibouti");
        countries.put("DMA", "Dominica");
        countries.put("DNK", "Denmark");
        countries.put("DOM", "Dominican Republic");
        countries.put("DZA", "Algeria");
        countries.put("ECU", "Ecuador");
        countries.put("EGY", "Egypt");
        countries.put("ERI", "Eritrea");
        countries.put("ESH", "Western Sahara");
        countries.put("ESP", "Spain");
        countries.put("EST", "Estonia");
        countries.put("ETH", "Ethiopia");
        countries.put("FIN", "Finland");
        countries.put("FJI", "Fiji");
        countries.put("FLK", "Falkland Islands (Malvinas)");
        countries.put("FRA", "France");
        countries.put("FRO", "Faeroe Islands");
        countries.put("GAB", "Gabon");
        countries.put("GBR", "United Kingdom");
        countries.put("GEO", "Georgian Republic");
        countries.put("GHA", "Ghana");
        countries.put("GIB", "Gibraltar");
        countries.put("GIN", "Guinea");
        countries.put("GLP", "Guadeloupe");
        countries.put("GMB", "Gambia");
        countries.put("GNB", "Guinea-Bissau");
        countries.put("GNQ", "Equatorial Guinea");
        countries.put("GRC", "Greece");
        countries.put("GRD", "Grenada");
        countries.put("GRL", "Greenland");
        countries.put("GTM", "Guatemala");
        countries.put("GUF", "French Guiana");
        countries.put("GUM", "Guam");
        countries.put("GUY", "Guyana");
        countries.put("HKG", "Hong-Kong");
        countries.put("HMD", "Heard and McDonald Islands");
        countries.put("HND", "Honduras");
        countries.put("HRV", "Croatia");
        countries.put("HTI", "Haiti");
        countries.put("HUN", "Hungary");
        countries.put("HVO", "Upper Volta");
        countries.put("IDN", "Indonesia");
        countries.put("III", "International");
        countries.put("IND", "India");
        countries.put("IOT", "British Indian Ocean Territory");
        countries.put("IRL", "Ireland");
        countries.put("IRN", "Iran");
        countries.put("IRQ", "Iraq");
        countries.put("ISL", "Iceland");
        countries.put("ISR", "Israel");
        countries.put("ITA", "Italy");
        countries.put("JAM", "Jamaica");
        countries.put("JOR", "Jordan");
        countries.put("JPN", "Japan");
        countries.put("JTN", "Johnston Island");
        countries.put("KAZ", "Kazakhstan");
        countries.put("KEN", "Kenya");
        countries.put("KGZ", "Kyrgyzstan");
        countries.put("KHM", "Kampuchea");
        countries.put("KIR", "Kiribati");
        countries.put("KNA", "St. Kitts-Nevis-Anguilla");
        countries.put("KOR", "South Korea");
        countries.put("KWT", "Kuwait");
        countries.put("LAO", "Laos");
        countries.put("LBN", "Lebanon");
        countries.put("LBR", "Liberia");
        countries.put("LBY", "Libya");
        countries.put("LCA", "Saint Lucia");
        countries.put("LIE", "Liechtenstein");
        countries.put("LKA", "Sri Lanka");
        countries.put("LSO", "Lesotho");
        countries.put("LTU", "Lithuania");
        countries.put("LUX", "Luxembourg");
        countries.put("LVA", "Latvia");
        countries.put("MAC", "Macau");
        countries.put("MAR", "Morocco");
        countries.put("MCO", "Monaco");
        countries.put("MDA", "Moldova, Republic of");
        countries.put("MDG", "Madagascar");
        countries.put("MDV", "Maldives");
        countries.put("MEX", "Mexico");
        countries.put("MID", "Midway Islands");
        countries.put("MLI", "Mali");
        countries.put("MLT", "Malta");
        countries.put("MNG", "Mongolia");
        countries.put("MOZ", "Mozambique");
        countries.put("MRT", "Mauritania");
        countries.put("MSR", "Montserrat");
        countries.put("MTQ", "Martinique");
        countries.put("MUS", "Mauritius");
        countries.put("MWI", "Malawi");
        countries.put("MYS", "Malaysia");
        countries.put("NAM", "Namibia");
        countries.put("NCL", "New Caledonia");
        countries.put("NER", "Niger");
        countries.put("NFK", "Norfolk Island");
        countries.put("NGA", "Nigeria");
        countries.put("NIC", "Nicaragua");
        countries.put("NIU", "Niue");
        countries.put("NLD", "Nethlerlands");
        countries.put("NOR", "Norway");
        countries.put("NPL", "Nepal");
        countries.put("NRU", "Nauru");
        countries.put("NTZ", "Neutral Zone");
        countries.put("NZL", "New Zealand");
        countries.put("OMN", "Oman");
        countries.put("PAK", "Pakistan");
        countries.put("PAN", "Panama ");
        countries.put("PCI", "Pacific Islands (trust territory)");
        countries.put("PCN", "Pitcairn Island");
        countries.put("PER", "Peru");
        countries.put("PHL", "Philippines");
        countries.put("PNG", "Papua New Guinea");
        countries.put("POL", "Poland");
        countries.put("PRI", "Puerto Rico");
        countries.put("PRK", "North Korea");
        countries.put("PRT", "Portugal");
        countries.put("PRY", "Paraguay");
        countries.put("PUS", "United States Miscellaneous Pacific Islands");
        countries.put("PYF", "French Polynesia");
        countries.put("QAT", "Qatar");
        countries.put("REU", "Reunion");
        countries.put("ROM", "Romania");
        countries.put("RUS", "Russian Federation");
        countries.put("RWA", "Rwanda");
        countries.put("SAU", "Saudi Arabia");
        countries.put("SDN", "Sudan");
        countries.put("SEN", "Senegal");
        countries.put("SGP", "Singapore");
        countries.put("SHN", "St. Helena");
        countries.put("SJM", "Svalbard and Jan Mayen Islands");
        countries.put("SLB", "Solomon Islands");
        countries.put("SLE", "Sierra Leone");
        countries.put("SLV", "El Salvador");
        countries.put("SMR", "San Marino");
        countries.put("SOM", "Somalia");
        countries.put("SPM", "St. Pierre and Miquelon");
        countries.put("STP", "Sao Tome and Principe");
        countries.put("SUN", "USSR");
        countries.put("SUR", "Surinam");
        countries.put("SVK", "Slovak Republic");
        countries.put("SVN", "Slovenia");
        countries.put("SWE", "Sweden");
        countries.put("SWZ", "Swaziland");
        countries.put("SYC", "Seychelles");
        countries.put("SYR", "Syria");
        countries.put("TCA", "Turks and Caicos Islands");
        countries.put("TCD", "Chad");
        countries.put("TGO", "Togo");
        countries.put("THA", "Thailand");
        countries.put("TJK", "Tajikistan");
        countries.put("TKL", "Tokelau");
        countries.put("TKM", "Turkmenistan");
        countries.put("TMP", "East Timor");
        countries.put("TON", "Tonga");
        countries.put("TTO", "Trinidad and Tobago");
        countries.put("TUN", "Tunisia");
        countries.put("TUR", "Turkey");
        countries.put("TUV", "Tuvalu");
        countries.put("TWN", "Taiwan");
        countries.put("TZA", "Tanzania");
        countries.put("UGA", "Uganda");
        countries.put("UKR", "Ukraine");
        countries.put("URY", "Uruguay");
        countries.put("USA", "United States");
        countries.put("UZB", "Uzbekistan");
        countries.put("VAT", "Vatican City State (Holy See)");
        countries.put("VCT", "Saint Vincent and the Grenadines");
        countries.put("VEN", "Venezuela");
        countries.put("VGB", "British Virgin Islands");
        countries.put("VIR", "United States Virgin Islands");
        countries.put("VNM", "Vietnam");
        countries.put("VUT", "Vanuatu");
        countries.put("VVV", "International");
        countries.put("WAK", "Wake Island");
        countries.put("WLF", "Wallis and Futuna Islands");
        countries.put("WSM", "Samoa");
        countries.put("YEM", "Yemen");
        countries.put("YMD", "South Yemen");
        countries.put("YUG", "Yugoslavia");
        countries.put("ZAF", "South Africa");
        countries.put("ZAR", "Zaire");
        countries.put("ZMB", "Zambia");
        countries.put("ZWE", "Zimbabwe");

        termtypes = new Hashtable<String, String>();
        termtypes.put("A", ""); // Primary terms (access points for print indexes)
        termtypes.put("B", "geochronology methods");
        termtypes.put("C", "commodities");
        termtypes.put("D", "elements, isotopes");
        termtypes.put("E", "geologic age");
        termtypes.put("F", "fossils");
        termtypes.put("G", "meteorites");
        termtypes.put("H", "igneous rocks");
        termtypes.put("I", "sedimentary rocks");
        termtypes.put("J", "metamorphic rocks");
        termtypes.put("K", "sedimentary structures");
        termtypes.put("L", "minerals");
        termtypes.put("M", "soils");
        termtypes.put("N", "sediments");
        termtypes.put("O", "geography"); // all geography including DSDP/ODP Sites and Legs
        termtypes.put("R", "rock formations");

        translatetermtypes = new Hashtable<String, String>();
        translatetermtypes.put("A", "Primary terms (A)");
        translatetermtypes.put("B", "geochronology methods (B)");
        translatetermtypes.put("C", "commodities (C)");
        translatetermtypes.put("D", "elements, isotopes (D)");
        translatetermtypes.put("E", "geologic age (E)");
        translatetermtypes.put("F", "fossils (F)");
        translatetermtypes.put("G", "meteorites (G)");
        translatetermtypes.put("H", "igneous rocks (H)");
        translatetermtypes.put("I", "sedimentary rocks (I)");
        translatetermtypes.put("J", "metamorphic rocks (J)");
        translatetermtypes.put("K", "sedimentary structures (K) ");
        translatetermtypes.put("L", "minerals (L)");
        translatetermtypes.put("M", "soils (M)");
        translatetermtypes.put("N", "sediments (N)");
        translatetermtypes.put("O", "all geography including DSDP/ODP Sites and Legs (O)");
        translatetermtypes.put("R", "rock formations (R)");
    }

    /* There was a bug in the singleton getInstance() method */
    /* synchronization was not preventing two calls to constructor */
    public static synchronized GRFDataDictionary getInstance() {
        if (instance == null) {
            instance = new GRFDataDictionary();
        }
        return instance;
    }

    public Hashtable<String, String> getClassCodes() {
        return new Hashtable<String, String>();
    }

    public Hashtable<String, String> getTreatments() {
        return new Hashtable<String, String>();
    }

    public String getClassCodeTitle(String classCode) {
        return "";
    }

    public Hashtable<String, String> getAuthorityCodes() {
        return new Hashtable<String, String>();
    }

    public Map<String, String> getCategories() {
        return categories;
    }


    public Map<String, String> getDocumenttypes() {
        return doctypes;
    }


    public Map<String, String> getBibliographiccodes() {
        return bibliographiccodes;
    }

    public Map<String, String> getLanguages() {
        return languages;
    }

    public Map<String, String> getCountries() {
        return countries;
    }

    public Map<String, String> getThesaurusTermType() {
        return termtypes;
    }

    public Map<String, String> getThesaurusTranslatedTermType() {
        return translatetermtypes;
    }

    public String translateValue(String strcode, Map<String, String> transtable) {
        String strtranslated = null;
        if ((transtable != null) && (strcode != null)) {
            if (transtable.containsKey(strcode)) {
                strtranslated = (String) transtable.get(strcode);
            }
        }
        return strtranslated;
    }

    public String getTreatmentTitle(String mTreatmentCode) {
        return null;
    }
}
