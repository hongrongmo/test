package org.ei.common.ntis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.domain.EIDoc;

public class NTISData
{
    public static Map mapCountryCodes = new Hashtable();
    static
    {
        mapCountryCodes.put("AC","Antigua and Barbuda");
        mapCountryCodes.put("AF","Afghanistan");
        mapCountryCodes.put("AG","Algeria");
        mapCountryCodes.put("AJ","Azerbaijan");
        mapCountryCodes.put("AL","Albania");
        mapCountryCodes.put("AM","Armenia");
        mapCountryCodes.put("AN","Andorra");
        mapCountryCodes.put("AO","Angola");
        mapCountryCodes.put("AQ","American Samoa");
        mapCountryCodes.put("AR","Argentina");
        mapCountryCodes.put("AS","Australia");
        mapCountryCodes.put("AU","Austria");
        mapCountryCodes.put("AV","Anguilla");
        mapCountryCodes.put("AY","Antarctica");
        mapCountryCodes.put("BA","Bahrain");
        mapCountryCodes.put("BB","Barbados");
        mapCountryCodes.put("BC","Botswana");
        mapCountryCodes.put("BD","Bermuda");
        mapCountryCodes.put("BE","Belgium");
        mapCountryCodes.put("BF","Bahamas");
        mapCountryCodes.put("BG","Bangladesh");
        mapCountryCodes.put("BH","Belize");
        mapCountryCodes.put("BK","Bosnia and Herzegovina");
        mapCountryCodes.put("BL","Bolivia");
        mapCountryCodes.put("BM","Burma");
        mapCountryCodes.put("BN","Benin");
        mapCountryCodes.put("BO","Belarus");
        mapCountryCodes.put("BP","Solomon Islands");
        mapCountryCodes.put("BR","Brazil");
        mapCountryCodes.put("BT","Bhutan");
        mapCountryCodes.put("BU","Bulgaria");
        mapCountryCodes.put("BX","Brunei Darussalamy");
        mapCountryCodes.put("BY","Burundi");
        mapCountryCodes.put("CA","Canada");
        mapCountryCodes.put("CB","Cambodia");
        mapCountryCodes.put("CD","Chad");
        mapCountryCodes.put("CE","Sri Lanka");
        mapCountryCodes.put("CF","Congo");
        mapCountryCodes.put("CG","Zaire");
        mapCountryCodes.put("CH","China");
        mapCountryCodes.put("CI","Chile");
        mapCountryCodes.put("CK","Cocos (Keeling) Islands");
        mapCountryCodes.put("CM","Cameroon");
        mapCountryCodes.put("CO","Colombia");
        mapCountryCodes.put("CS","Costa Rica");
        mapCountryCodes.put("CT","Central African Republic");
        mapCountryCodes.put("CU","Cuba");
        mapCountryCodes.put("CV","Cape Verde");
        mapCountryCodes.put("CY","Cyprus");
        mapCountryCodes.put("CZ","Czechoslovakia");
        mapCountryCodes.put("DA","Denmark");
        mapCountryCodes.put("DJ","Djibouti");
        mapCountryCodes.put("DO","Dominica");
        mapCountryCodes.put("DR","Dominican Republic");
        mapCountryCodes.put("EC","Ecuador");
        mapCountryCodes.put("EG","Egypt");
        mapCountryCodes.put("EI","Ireland");
        mapCountryCodes.put("EK","Equatorial Guinea");
        mapCountryCodes.put("EN","Estonia");
        mapCountryCodes.put("ES","El Salvador");
        mapCountryCodes.put("ET","Ethiopia");
        mapCountryCodes.put("EZ","Czech Republic");
        mapCountryCodes.put("FA","Falkland Islands (Malvinas)");
        mapCountryCodes.put("FG","French Guiana");
        mapCountryCodes.put("FI","Finland");
        mapCountryCodes.put("FJ","Fiji");
        mapCountryCodes.put("FO","Faroe Islands");
        mapCountryCodes.put("FR","France");
        mapCountryCodes.put("GA","Gambia");
        mapCountryCodes.put("GB","Gabon");
        mapCountryCodes.put("GC","German Democratic Republic");
        mapCountryCodes.put("GE","Germany Federal Republic");
        mapCountryCodes.put("GG","Georgia");
        mapCountryCodes.put("GH","Ghana");
        mapCountryCodes.put("GI","Gilbraltar");
        mapCountryCodes.put("GJ","Grenada");
        mapCountryCodes.put("GL","Greenland");
        mapCountryCodes.put("GM","Germany");
        mapCountryCodes.put("GP","Guadeloupe");
        mapCountryCodes.put("GQ","Guam");
        mapCountryCodes.put("GR","Greece");
        mapCountryCodes.put("GT","Guatemala");
        mapCountryCodes.put("GV","Guinea");
        mapCountryCodes.put("GY","Guyana");
        mapCountryCodes.put("HA","Haiti");
        mapCountryCodes.put("HK","Hong Kong");
        mapCountryCodes.put("HO","Honduras");
        mapCountryCodes.put("HR","Croatia");
        mapCountryCodes.put("HU","Hungary");
        mapCountryCodes.put("IC","Iceland");
        mapCountryCodes.put("ID","Indonesia");
        mapCountryCodes.put("IN","India");
        mapCountryCodes.put("IO","British Indian Ocean Territory");
        mapCountryCodes.put("IQ","United States Minor Outer Isl.");
        mapCountryCodes.put("IR","Iran (Islamic Republic of)");
        mapCountryCodes.put("IS","Israel");
        mapCountryCodes.put("IT","Italy");
        mapCountryCodes.put("IV","Ivory Coast");
        mapCountryCodes.put("IZ","Iraq");
        mapCountryCodes.put("JA","Japan");
        mapCountryCodes.put("JM","Jamaica");
        mapCountryCodes.put("JO","Jordan");
        mapCountryCodes.put("KE","Kenya");
        mapCountryCodes.put("KG","Kyrgyzstan");
        mapCountryCodes.put("KN","Korea, People's Democratic Rep");
        mapCountryCodes.put("KR","Kiribati");
        mapCountryCodes.put("KS","Korea, Republic of");
        mapCountryCodes.put("KU","Kuwait");
        mapCountryCodes.put("KZ","Kazakhstan");
        mapCountryCodes.put("LA","Laos, People's Democratic Rep.");
        mapCountryCodes.put("LE","Lebanon");
        mapCountryCodes.put("LG","Latvia");
        mapCountryCodes.put("LH","Lithuania");
        mapCountryCodes.put("LI","Liberia");
        mapCountryCodes.put("LO","Slovakia");
        mapCountryCodes.put("LS","Liechtenstein");
        mapCountryCodes.put("LT","Lesotho");
        mapCountryCodes.put("LU","Luxembourg");
        mapCountryCodes.put("LY","Libyan Arab Jamahiriya");
        mapCountryCodes.put("MA","Madagascar");
        mapCountryCodes.put("MB","Martinique");
        mapCountryCodes.put("MC","Macua");
        mapCountryCodes.put("MD","Moldova");
        mapCountryCodes.put("MG","Mogolia");
        mapCountryCodes.put("MH","Montserrat");
        mapCountryCodes.put("MI","Malawi");
        mapCountryCodes.put("MK","Macedonia");
        mapCountryCodes.put("ML","Mali");
        mapCountryCodes.put("MN","Monaco");
        mapCountryCodes.put("MO","Morocco");
        mapCountryCodes.put("MP","Mauritius");
        mapCountryCodes.put("MR","Mauritania");
        mapCountryCodes.put("MT","Malta");
        mapCountryCodes.put("MU","Oman");
        mapCountryCodes.put("MV","Maldives");
        mapCountryCodes.put("MW","Montenegro");
        mapCountryCodes.put("MX","Mexico");
        mapCountryCodes.put("MY","Malaysia");
        mapCountryCodes.put("MZ","Mozambique");
        mapCountryCodes.put("NA","Netherlands Antilles");
        mapCountryCodes.put("NC","New Caledonia");
        mapCountryCodes.put("NE","Niue");
        mapCountryCodes.put("NF","Norfolk Island");
        mapCountryCodes.put("NG","Niger");
        mapCountryCodes.put("NI","Nigeria");
        mapCountryCodes.put("NL","Netherlands");
        mapCountryCodes.put("NO","Norway");
        mapCountryCodes.put("NP","Nepal");
        mapCountryCodes.put("NR","Nauru");
        mapCountryCodes.put("NS","Suriname");
        mapCountryCodes.put("NU","Nicaragua");
        mapCountryCodes.put("NZ","New Zealand");
        mapCountryCodes.put("PA","Paraguay");
        mapCountryCodes.put("PC","Pitcairn");
        mapCountryCodes.put("PE","Peru");
        mapCountryCodes.put("PK","Pakistan");
        mapCountryCodes.put("PL","Poland");
        mapCountryCodes.put("PN","Panama");
        mapCountryCodes.put("PO","Portugal");
        mapCountryCodes.put("PP","Papua New Guinea");
        mapCountryCodes.put("QA","Qutar");
        mapCountryCodes.put("RE","Reunion");
        mapCountryCodes.put("RH","rhodesia");
        mapCountryCodes.put("RO","Romania");
        mapCountryCodes.put("RP","Philippines");
        mapCountryCodes.put("RQ","Puerto Rico");
        mapCountryCodes.put("RS","Russia");
        mapCountryCodes.put("RW","Rwanda");
        mapCountryCodes.put("SA","Saudi Arabia");
        mapCountryCodes.put("SE","Seychelles");
        mapCountryCodes.put("SF","South Africa");
        mapCountryCodes.put("SG","Senegal");
        mapCountryCodes.put("SH","St. Helena");
        mapCountryCodes.put("SI","Slovenia");
        mapCountryCodes.put("SL","Sierra Leone");
        mapCountryCodes.put("SM","San Marino");
        mapCountryCodes.put("SN","Singapore");
        mapCountryCodes.put("SO","Somalia");
        mapCountryCodes.put("SP","Spain");
        mapCountryCodes.put("SR","Serbia");
        mapCountryCodes.put("SU","Sudan");
        mapCountryCodes.put("SV","Svalbard and Jan Mayen Islands");
        mapCountryCodes.put("SW","Sweden");
        mapCountryCodes.put("SY","Syrian Arab Republic");
        mapCountryCodes.put("SZ","Switzerland");
        mapCountryCodes.put("TC","United Arab Emirates");
        mapCountryCodes.put("TD","Trinidad and Tobago");
        mapCountryCodes.put("TH","Thailand");
        mapCountryCodes.put("TI","Tajikistan");
        mapCountryCodes.put("TK","Turks and Caicos Islands");
        mapCountryCodes.put("TL","Tokelau");
        mapCountryCodes.put("TN","Tonga");
        mapCountryCodes.put("TO","Togo");
        mapCountryCodes.put("TP","Sao Tome and Principe");
        mapCountryCodes.put("TQ","Other");
        mapCountryCodes.put("TS","Tunisia");
        mapCountryCodes.put("TU","Turkey");
        mapCountryCodes.put("TV","Tuvalu");
        mapCountryCodes.put("TW","Taiwan, Province of China");
        mapCountryCodes.put("TX","Turkmenistan");
        mapCountryCodes.put("TZ","Tanzania, United Republic of");
        mapCountryCodes.put("UG","Uganda");
        mapCountryCodes.put("UK","United Kingdom");
        mapCountryCodes.put("UP","Ukraine");
        mapCountryCodes.put("UR","USSR");
        mapCountryCodes.put("US","United States");
        mapCountryCodes.put("UV","Burkina FASO");
        mapCountryCodes.put("UY","Uruguay");
        mapCountryCodes.put("UZ","Uzbekistan");
        mapCountryCodes.put("VC","Saint Vicent & the Grenadines");
        mapCountryCodes.put("VE","Venezuela");
        mapCountryCodes.put("VI","Virgin Islands (British)");
        mapCountryCodes.put("VM","Viet Nam");
        mapCountryCodes.put("VQ","Virgin Islands (United States)");
        mapCountryCodes.put("VT","Vatican City State (Holy See)");
        mapCountryCodes.put("WA","Namibia");
        mapCountryCodes.put("WF","Wallis and Futuna Islands");
        mapCountryCodes.put("WI","Western Sahara");
        mapCountryCodes.put("WS","Samoa");
        mapCountryCodes.put("WZ","Swaziland");
        mapCountryCodes.put("YD","Yemen, Democratic Republic of");
        mapCountryCodes.put("YE","Yemen, Republic of");
        mapCountryCodes.put("YO","Yugoslavia");
        mapCountryCodes.put("ZA","Zambia");
        mapCountryCodes.put("ZI","Zimbabwe");
        mapCountryCodes.put("ZZ","International Organization");
    }

    static Perl5Util perl = new Perl5Util();

    // strip out terminal period "."
    public static String formatTitle(String ti)
    {
        String title = ti;
        if (perl.match("#.$#",ti)) {
            title = perl.substitute("s#.$##",ti);
        }
        return title;
    }

  public static Map authorAffiliationAndSponsor(String inSource) {

    String source = null;
    String performers = null;
    String sponsors = null;
    Map mapResult = new Hashtable();

    if(inSource != null) {

        // if there are performers
        if(perl.match("#\\*\\*#", inSource)) {
            source = perl.substitute("s#\\*\\*#;#g", inSource);
        }
        else {
            source = inSource;
        }
        // if there are sponsors
        if(perl.match("#\\*#",source)) {
          int intStarIndex = source.indexOf("*");
            performers = source.substring(0, intStarIndex);
            sponsors = perl.substitute("s#\\*#;#g", source.substring(intStarIndex+1));
        }
        else {
            performers = source;
        }
        if(performers != null) {
        mapResult.put(EIDoc.PERFORMER, performers);
      }
        if(sponsors != null) {
            mapResult.put(EIDoc.RSRCH_SPONSOR, sponsors);
      }
    }
    return mapResult;
  }

    //strip out the date so it contains
    // only Month and Year
    // removed any terminal comma(s) and "C"
    public static String formatDate(String date)
    {
        String pubMonthYear = date;
        if(perl.match("#,$#",pubMonthYear))
        {
            pubMonthYear = perl.substitute("s#,$##",pubMonthYear);
        }
        if(perl.match("#^c#i",pubMonthYear))
        {
            pubMonthYear = perl.substitute("s#^c##i",pubMonthYear);
        }
        if(perl.match("#(filed|reissue|patented)#i",pubMonthYear))
        {
            pubMonthYear = perl.substitute("s#(filed|reissue|patented)##i",pubMonthYear);
        }
        if(perl.match("#(\\d+)\\W+(\\w+)\\W+(\\d+)#", pubMonthYear))
        {
            String strYear = perl.group(3);
            if (strYear != null)
            {
                // jam 11/17 - Make NTIS date Year 4 chars if only 2
                if(strYear.length() == 2)
                {
                    try
                    {
                        SimpleDateFormat formatter1 = new SimpleDateFormat("yy");
                        java.util.Date dteYear = formatter1.parse(strYear);
                        formatter1 = new SimpleDateFormat("yyyy");
                        strYear = formatter1.format(dteYear);
                    }
                    catch (ParseException pe)
                    {

                    }
                }
                pubMonthYear = (perl.group(2)).concat(" ").concat(strYear);
            }
        }

        return pubMonthYear;
    }

    //strip out "*"
    public static String formatPage(String page)
    {
        String pages = page;
        if(perl.match("#\\*#",page)){
            pages = perl.substitute("s#\\*##g",page).trim();
        }
        return pages;
    }

    //strip out "{", replace with ";"
    // also need to remove commas
    // Format appears to be
    // multiple values appear as  "{UCRL-101574, {CONF-900285-1"
  // single values appear to be "{UCRL-101574{"
  // empty values appear as "{{"
    public static String formatRN(String rn)
    {
        String reportNum = rn;
        // handle multiple
        if(perl.match("#, #", reportNum)) {
            reportNum = perl.substitute("s#, #; #g",reportNum);
        }
    // handle empty and singles
        if(perl.match("#{#", reportNum)) {
      reportNum = perl.substitute("s#{##g",reportNum);
    }


        return reportNum;
    }

    //strip out "/XAB","-","/" and empty spaces
    public static String formatAN(String an)
    {
        String accNum = an;
        if(perl.match("#\\/XAB#",accNum)) {
            accNum = perl.substitute("s#\\/XAB##i",accNum);
        }
        if(perl.match("#\\W+#",accNum)) {
            accNum = perl.substitute("s#\\W+##g",accNum);
        }
        return accNum;
    }

    public static String formatLA(String la)
    {
        String lang = la;
        if(perl.match("#\\;(\\d+)#",la)) {
            lang = perl.group(1).toString();
            if(lang.endsWith("0")) {
                lang = "Unknown";
            }
            else if(lang.endsWith("1")) {
                lang = "English";
            }
            else if(lang.endsWith("2")) {
                lang = "Translation";
            }
            else if(lang.endsWith("3")) {
                if(perl.match("#\\/lnafr#",la)) {
                    lang = "Afrikaans";
                }
                else if(perl.match("#\\/lnara#",la)){
                    lang = "Arabic";
                }
                else if(perl.match("#\\/lnbel#",la)){
                    lang = "Belorussian";
                }
                else if(perl.match("#\\/lnbul#",la)){
                    lang = "Bulgarian";
                }
                else if(perl.match("#\\/lnchi#",la)){
                    lang = "Chinese";
                }
                else if(perl.match("#\\/lncro#",la)){
                    lang = "Croatian";
                }
                else if(perl.match("#\\/lncze#",la)){
                    lang = "Czech";
                }
                else if(perl.match("#\\/lndan#",la)){
                    lang = "Danish";
                }
                else if(perl.match("#\\/lndut#",la)){
                    lang = "Dutch";
                }
                else if(perl.match("#\\/lnest#",la)){
                    lang = "Estonian";
                }
                else if(perl.match("#\\/lnfin#",la)){
                    lang = "Finnish";
                }
                else if(perl.match("#\\/lnfle#",la)){
                    lang = "Flemish";
                }
                else if(perl.match("#\\/lnfre#",la)){
                    lang = "French";
                }
                else if(perl.match("#\\/lnger#",la)){
                    lang = "German";
                }
                else if(perl.match("#\\/lngre#",la)){
                    lang = "Greek";
                }
                else if(perl.match("#\\/lnheb#",la)){
                    lang = "Hebrew";
                }
                else if(perl.match("#\\/lnhun#",la)){
                    lang = "Hungarian";
                }
                else if(perl.match("#\\/lnind#",la)){
                    lang = "Indonesian";
                }
                else if(perl.match("#\\/lnira#",la)){
                    lang = "Iranian";
                }
                else if(perl.match("#\\/lnita#",la)){
                    lang = "Italian";
                }
                else if(perl.match("#\\/lnjap#",la)){
                    lang = "Japanese";
                }
                else if(perl.match("#\\/lnkor#",la)){
                    lang = "Korean";
                }
                else if(perl.match("#\\/lnlit#",la)){
                    lang = "Lithuanian";
                }
                else if(perl.match("#\\/lnmal#",la)){
                    lang = "Malay";
                }
                else if(perl.match("#\\/lnnor#",la)){
                    lang = "Norwegian";
                }
                else if(perl.match("#\\/lnoth#",la)){
                    lang = "Other Languages";
                }
                else if(perl.match("#\\/lnpol#",la)){
                    lang = "Polish";
                }
                else if(perl.match("#\\/lnpor#",la)){
                    lang = "Portuguese";
                }
                else if(perl.match("#\\/lnrom#",la)){
                    lang = "Romanian";
                }
                else if(perl.match("#\\/lnrus#",la)){
                    lang = "Russian";
                }
                else if(perl.match("#\\/lnser#",la)){
                    lang = "Serbo Croatian";
                }
                else if(perl.match("#\\/lnsev#",la)){
                    lang = "Several Languages";
                }
                else if(perl.match("#\\/lnslo#",la)){
                    lang = "Slovak";
                }
                else if(perl.match("#\\/lnspa#",la)){
                    lang = "Spanish";
                }
                else if(perl.match("#\\/lnswe#",la)){
                    lang = "Swedish";
                }
                else if(perl.match("#\\/lntha#",la)){
                    lang = "Thai";
                }
                else if(perl.match("#\\/lntur#",la)){
                    lang = "Turkish";
                }
                else if(perl.match("#\\/lnukr#",la)){
                    lang = "Ukrainian";
                }
                else if(perl.match("#\\/lnvie#",la)){
                    lang = "Vietnamese";
                }
                else {
                    lang = "Foreign";
                }
            }
        }
        return lang;
    }

    public static String formatVI(String vi)
    {
        String volume = vi;
        if(perl.match("#\\/#",volume)){
            volume = perl.substitute("s#^\\/##",volume).trim();
            volume = perl.substitute("s#\\/#; #",volume).trim();
        }
        return volume;
    }


    //strip out "Descriptors:", "*", ",", terminal period ".", seperate with ";"
    public static String formatDES(String des)
    {
        String descriptors = des;
        if(perl.match("#Descriptors:#",descriptors)){
            descriptors = perl.substitute("s#Descriptors:##i",descriptors);
        }
        if(perl.match("#,#",descriptors)){
            descriptors = perl.substitute("s#,#;#g",descriptors);
        }
        if(perl.match("#\\*#",descriptors)){
            descriptors = perl.substitute("s#\\*##g",descriptors).trim();
        }
        if(perl.match("#.$#",descriptors)){
            descriptors = perl.substitute("s#.$##",descriptors);
        }
        return descriptors;
    }

    public static String formatCountry(String so, String ic)
    {
        Iterator itrCountries = mapCountryCodes.values().iterator();

        StringBuffer buf = new StringBuffer();
        while(itrCountries.hasNext())
        {
            String c = (String) itrCountries.next();
            if(so.toLowerCase().indexOf(c.toLowerCase()) > -1)
            {
                if(buf.length() > 0)
                {
                    buf.append(";");
                }
                buf.append(c);
            }
        }

        if(buf.length() == 0)
        {
            if((ic != null) && (perl.match("#\\/fn([a-z]{2})#i",ic)))
            {
                String strCountryCode = perl.group(1).toUpperCase();
                if(mapCountryCodes.containsKey(strCountryCode))
                {
                    return (String) mapCountryCodes.get(strCountryCode);
                }
            }
            return "United States";
        }
        return buf.toString();
    }

    public static String formatIDE(String ide)
    {
        String unconTerms = ide;
        if(perl.match("#Identifiers:#i",unconTerms)){
            unconTerms = perl.substitute("s#Identifiers:##i",unconTerms);
        }
        if(perl.match("#\\*#",unconTerms)){
            unconTerms = perl.substitute("s#\\*##g",unconTerms);
        }
        if(perl.match("#.$#",unconTerms)){
            unconTerms = perl.substitute("s#.$##",unconTerms);
        }
        if(perl.match("#,#",unconTerms)){
            unconTerms = perl.substitute("s#,#;#g",unconTerms);
        }
        return unconTerms;
    }

    public static String formatCAT(String cat)
    {
        String cls = cat;
        if(perl.match("#Field#i",cls)){
            cls = perl.substitute("s#Field##i",cls);
        }
        if(perl.match("#\\*#",cls)){
            cls = perl.substitute("s#\\*##g",cls);
        }
        if(perl.match("#,#",cls)){
            cls = perl.substitute("s#,#;#g",cls);
        }
        return cls;
    }

    public static String stripOutBracket(String in)
    {
        String out = in;
        if(perl.match("#\\{#",out)){
            out = perl.substitute("s#\\{##g",out);
        }
        return out;
    }


    public static void main (String[] args)
    {
    //  NTISSource ntis = new NTISSource();

        System.out.println("title: "+formatTitle("This. is a title test."));
    //  System.out.println("so: "+formatSource("National Energy Technology Lab.,Pittsburg, PA.**Department of Energy, Washington, DC.*USDOE*ABCDE"));
    //  System.out.println("so: "+formatSource("A*d c *e*f"));
    //  System.out.println("so: "+formatSource("A"));
    //  System.out.println("so: "+formatSource("*d f*e"));

        System.out.println("date: "+formatDate("2 Jan 2003"));
        System.out.println("date: "+formatDate("Feb 2003"));
        System.out.println("date: "+formatDate("cDec 2003,"));
        System.out.println("date: "+formatDate("Filed 3 Apr 2003,"));
        System.out.println("date: "+formatDate("c17 Oct 2000"));

        System.out.println("page: "+formatPage("139 p"));
        System.out.println("page: "+formatPage("one CD-ROM discs *"));

        System.out.println("rn: "+formatRN("{ABCD EF GES/GABFA-AEGA {"));

        System.out.println("an: "+formatAN("ADA393927/XAB"));
        System.out.println("an: "+formatAN("ERS-AIB-772/XAB"));
        System.out.println("an: "+formatAN("AD-B149 933/XAB"));
        System.out.println("an: "+formatAN("NUREG/CR-6706/XAB"));
        System.out.println("an: "+formatAN("ADA333333"));

        System.out.println("la: "+formatLA("U/tfnpo/fnl/lnafr;12113,1101<R"));
        System.out.println("la: "+formatLA("U/dodxa/doda/lnchi;12113,1101"));
        System.out.println("la: "+formatLA("u/dagd/dfjqowe;12111,1102"));
        System.out.println("des: "+formatDES("Descriptors: *Biological agents, *Millitary medicine, Virginia, Symposia, Medicine, *Department of Defense."));
        System.out.println("vi: "+formatVI("/u2321/s3334"));
        System.out.println("vi: "+formatVI("/u3432"));
    }


}


