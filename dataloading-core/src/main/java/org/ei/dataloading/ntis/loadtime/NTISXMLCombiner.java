package org.ei.dataloading.ntis.loadtime;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.ei.dataloading.*;
import org.ei.dataloading.lookup.LookupEntry;
import org.ei.common.ntis.*;
import org.ei.common.*;
/*
import org.ei.data.AuthorStream;
import org.ei.data.CombinedWriter;
import org.ei.data.CombinedXMLWriter;
import org.ei.data.Combiner;
import org.ei.data.Country;
import org.ei.data.XMLWriterCommon;
import org.ei.data.EVCombinedRec;
*/
import org.ei.util.GUID;

public class NTISXMLCombiner
	extends Combiner
{

    private static Perl5Util perl = new Perl5Util();

    private Perl5Compiler compiler = new Perl5Compiler();

    private Perl5Matcher matcher = new Perl5Matcher();

    private String[] countries =
    { "France", "Germany", "Netherlands", "Japan", "China", "Russia", "Italy", "Spain", "Canada", "Sweden", "Finland", "United Kingdom", "Australia", "Denmark", "International Organizations", "Antigua and Barbuda", "Afghanistan", "Algeria", "Azerbaijan", "Albania", "Armenia", "Andorra", "Angola", "American Samoa", "Argentina", "Austria", "Anguilla", "Antarctica", "Bahrain", "Barbados", "Botswana", "Bermuda", "Belgium", "Bahamas", "Bangladesh", "Belize", "Bosnia and Herzegovina", "Bolivia",
            "Burma", "Benin", "Belarus", "Solomon Islands", "Brazil", "Bhutan", "Bulgaria", "Brunei Darussalamy", "Burundi", "Cambodia", "Chad", "Sri Lanka", "Congo", "Zaire", "Chile", "Cocos (Keeling) Islands", "Cameroon", "Colombia", "Costa Rica", "Central African Republic", "Cuba", "Cape Verde", "Cyprus", "Czechoslovakia", "Djibouti", "Dominica", "Dominican Republic", "Ecuador", "Egypt", "Ireland", "Equatorial Guinea", "Estonia", "El Salvador", "Ethiopia", "Czech Republic",
            "Falkland Islands (Malvinas)", "French Guiana ", "Fiji", "Faroe Islands", "Gambia", "Gabon", "	German Democratic Republic ", "Germany Federal Republic", "Ghana", "Gilbraltar ", "Grenada", "Greenland", "Guadeloupe", "Guam", "Greece", "Guatemala", "Guinea", "Guyana", "Haiti ", "Hong Kong", "Honduras", "Croatia", "Hungary", "Iceland", "Indonesia", "India", "British Indian Ocean Territory", "United States Minor Outer Isl.", "Iran (Islamic Republic of)", "Israel", "Ivory Coast", "Iraq",
            "Jamaica", "Jordan", "Kenya ", "Kyrgyzstan ", "Korea, People's Democratic Rep", "Kiribati", "Korea, Republic of", "Kuwait", "Kazakhstan", "Laos, People's Democratic Rep.", "Lebanon", "Latvia", "Lithuania", "Liberia", "Slovakia", "Liechtenstein", "Lesotho", "Luxembourg", "Libyan Arab Jamahiriya", "Madagascar", "Martinique", "Macua", "Moldova", "Mogolia", "Montserrat", "Malawi", "Macedonia", "Mali", "Monaco", "Morocco", "Mauritius", "Mauritania", "Malta", "Oman", "Maldives",
            "Montenegro", "Mexico", "Malaysia", "Mozambique", "Netherlands Antilles", "New Caledonia", "Niue", "Norfolk Island", "Niger", "Nigeria", "Norway", "Nepal", "Nauru", "Suriname", "Nicaragua", "New Zealand", "Paraguay", "Pitcairn", "Peru", "Pakistan", "Poland", "Panama", "Portugal", "Papua New Guinea", "Qutar", "Reunion", "rhodesia", "Romania", "Philippines", "Puerto Rico", "Rwanda", "Saudi Arabia", "Seychelles", "South Africa", "Senegal", "St. Helena", "Slovenia", "Sierra Leone",
            "San Marino", "Singapore", "Somalia", "Serbia", "Sudan", "Svalbard and Jan Mayen Islands", "Syrian Arab Republic", "	Switzerland ", "United Arab Emirates", "Trinidad and Tobago", "Thailand", "Tajikistan", "Turks and Caicos Islands", "Tokelau", "Tonga", "Togo", "Sao Tome and Principe", "Tunisia", "Turkey", "Tuvalu", "Taiwan, Province of China", "Turkmenistan", "Tanzania, United Republic of", "Uganda", "Ukraine", "USSR", "United States", "Burkina FASO", "Uruguay", "Uzbekistan",
            "Saint Vicent & the Grenadines", "Venezuela", "Virgin Islands (British)", "Viet Nam", "Virgin Islands (United States)", "Vatican City State (Holy See)", "Namibia", "Wallis and Futuna Islands", "Western Sahara", "Samoa", "Swaziland", "Yemen, Democratic Republic of", "Yemen, Republic of", "Yugoslavia", "Zambia", "Zimbabwe", "International Organization" };

    private int exitNumber;

    private static String tablename;

    /*HT added 09/21/2020 for lookup extraction to ES*/
    private String action = null;
    private LookupEntry lookupObj = null;

    
    public static void main(String args[])
    	throws Exception
    {
        String url = args[0];
        String driver = args[1];
        String username = args[2];
        String password = args[3];
        int loadNumber = Integer.parseInt(args[4]);
        int recsPerbatch = Integer.parseInt(args[5]);
        String operation = args[6];
        tablename = args[7];
        String environment = args[8].toLowerCase();

        Combiner.TABLENAME = tablename;
        System.out.println(Combiner.TABLENAME);

        CombinedWriter writer = new CombinedXMLWriter(recsPerbatch,
                									  loadNumber,
                									  "ntis");
		writer.setOperation(operation);

        NTISCombiner c = new NTISCombiner(writer);

        /*TH added 09/21/2020 for ES lookup generation*/
        for(String str: args)
        {
        	if(str.equalsIgnoreCase("lookup"))
        		c.setAction("lookup");
        	System.out.println("Action: lookup");
        }
        
        /*HT added 09/21/2020 if condition on action to consider lookup extraction for ES, otherwise consider it reg. extraction and so embed all looadnumber check inside this global if stmt*/
        if(c.getAction() == null || c.getAction().isEmpty())
        {
        	if(loadNumber > 100001)
    		{
                c.writeCombinedByWeekNumber(url,
                        					driver,
                        					username,
                        					password,
                        					loadNumber);
    		}
        	// extract the whole thing
        	else if(loadNumber == 0)
        	{
          		for(int yearIndex = 1964; yearIndex <= 2012; yearIndex++)
          		{
        			System.out.println("Processing year " + yearIndex + "...");
          	  		// create  a new writer so we can see the loadNumber/yearNumber in the filename
           			c = new NTISCombiner(new CombinedXMLWriter(recsPerbatch, yearIndex,"ntis", environment));
            		c.writeCombinedByYear(url,
                                		driver,
                                		username,
                                		password,
                                		yearIndex);
          		}
        	}
        	else
        	{
          		c.writeCombinedByYear(url,
                                	driver,
                                	username,
                                	password,
                                	loadNumber);
        	}


            System.out.println("++end of loadnumber " + loadNumber);
        }
        else
        {
        	System.out.println("Extracting Lookups");
        	c.writeLookupByWeekNumber(loadNumber);
        }
    }

    public void setAction(String str)
    {
    	action = str;
    }
    public String getAction() {
    	return action;
    }
    
    public NTISXMLCombiner(CombinedWriter writer)
    {
        super(writer);
    }

    public void writeCombinedByTableHook(Connection con)
    		throws Exception
		{
			Statement stmt = null;
			ResultSet rs = null;
			try
			{
			
				stmt = con.createStatement();
				System.out.println("Running the query...");
				String sqlQuery = "select * from " + Combiner.TABLENAME +" where database='" + Combiner.CURRENTDB + "'";
				System.out.println(sqlQuery);
				rs = stmt.executeQuery(sqlQuery);
				
				System.out.println("Got records ...from table::"+Combiner.TABLENAME);
				writeRecs(rs);
				System.out.println("Wrote records.");
				this.writer.end();
				this.writer.flush();
			
			}
			finally
			{
			
				if (rs != null)
				{
					try
					{
						rs.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				
				if (stmt != null)
				{
					try
					{
						stmt.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}


    public void writeCombinedByYearHook(Connection con,
            							int year)
    	throws Exception
    {

        Statement stmt = null;
        ResultSet rs = null;
        try
        {

            stmt = con.createStatement();
            System.out.println("Running the query...");
            String q = "select LOAD_NUMBER,M_ID,AN,TI,TN,PN,AB,IC,SU,DES,IDE,SO,PA1,PA2,PA2,PA3,PA4,PA5,RD,RN,CAT,VI,XP,AV,MAA1,MAA2,CT,PR,HN,seq_num from " + Combiner.TABLENAME + " where substr(load_number,1,4) ='" + year + "'";
            rs = stmt.executeQuery(q);
            System.out.println("Got records ...");
            writeRecs(rs);
            System.out.println("Wrote records.");
            this.writer.end();
            this.writer.flush();

        }
        finally
        {

            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            if (stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeRecs(ResultSet rs)
    	throws Exception
    {
        int i = 0;

        while (rs.next())
        {

            EVCombinedRec rec = new EVCombinedRec();
            ++i;

            String aut = NTISAuthor.formatAuthors(rs.getString("PA1"),
                    								 rs.getString("PA2"),
                    								 rs.getString("PA3"),
                    								 rs.getString("PA4"),
                    								 rs.getString("PA5"),
                    								 rs.getString("HN"));

            if (aut != null)
            {
                rec.put(EVCombinedRec.AUTHOR, prepareAuthor(aut));
            }

            String affil = formatAffil(rs.getString("SO"));
            if (affil != null )
            {
                rec.put(EVCombinedRec.AUTHOR_AFFILIATION, prepareMulti(affil));
            }

            rec.put(EVCombinedRec.TITLE, formatTitle(rs.getString("TI")));
            rec.put(EVCombinedRec.ABSTRACT, formatAbstract(rs.getString("AB")));

            String cv = formatDelimiter(formatCV(rs.getString("DES")));
            if (cv != null)
            {
                rec.put(EVCombinedRec.CONTROLLED_TERMS,
                        prepareMulti(cv));
            }

            String uncontrolled = formatDelimiter(formatFL(rs.getString("IDE")));
            if (uncontrolled != null)
            {
                rec.put(EVCombinedRec.UNCONTROLLED_TERMS,
                        prepareMulti(uncontrolled));
            }

            String report = formatDelimiter(formatReportNumbers(rs.getString("RN")));
            if (report != null)
            {
                rec.put(EVCombinedRec.REPORTNUMBER, prepareMulti(report));
            }

            String order = formatDelimiter(formatOrderNumbers(rs.getString("CT"),
                    											 rs.getString("PN")));
            if (order != null)
            {
                rec.put(EVCombinedRec.ORDERNUMBER, prepareMulti(order));
            }

            String country = getCountry(rs.getString("SO"), rs.getString("IC"));
            if (country != null)
            {
                rec.put(EVCombinedRec.COUNTRY, prepareMulti(country));
                rec.put(EVCombinedRec.AFFILIATION_LOCATION,prepareMulti(country));
            }

            String classcodes = formatDelimiter(formatClassCodes(rs.getString("CAT")));
            if (classcodes != null)
            {
                rec.put(EVCombinedRec.CLASSIFICATION_CODE,
                        prepareMulti(XMLWriterCommon.formatClassCodes(classcodes)));
            }

            rec.put(EVCombinedRec.VOLUME, getVolume(rs.getString("VI")));
            rec.put(EVCombinedRec.ISSUE, getIssue(rs.getString("VI")));
            rec.put(EVCombinedRec.STARTPAGE, getStartPage(rs.getString("XP")));
            //rec.put(EVCombinedRec.DOCTYPE, rs.getString("TN"));
            rec.put(EVCombinedRec.AVAILABILITY, getAvailability(rs.getString("AV"),
                    											rs.getString("PR")));

            rec.put(EVCombinedRec.NOTES, formatNotes(rs.getString("SU")));
            rec.put(EVCombinedRec.PATENTAPPDATE, getPatentAppDate(rs.getString("RD")));
            rec.put(EVCombinedRec.PATENTISSUEDATE, getPatentIssueDate(rs.getString("RD")));
            rec.put(EVCombinedRec.DEDUPKEY, new GUID().toString());
            rec.put(EVCombinedRec.LANGUAGE,
                    prepareMulti(getLanguage(rs.getString("IC"),
                            				 rs.getString("SU"))));
            rec.put(EVCombinedRec.DOCID, rs.getString("M_ID"));
            rec.put(EVCombinedRec.DATABASE, "ntis");
            rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("LOAD_NUMBER"));
            rec.put(EVCombinedRec.PUB_YEAR, getPubYear(rs.getString("RD"),
                    								   rs.getString("LOAD_NUMBER")));
            String agency = getAgency(rs.getString("MAA1"),
									  rs.getString("MAA2"));
            if (agency != null)
            {
                rec.put(EVCombinedRec.AGENCY, prepareMulti(agency));
            }
            rec.put(EVCombinedRec.ACCESSION_NUMBER,
                    formatAccessionNumber(rs.getString("AN")));

			if(rs.getString("seq_num") != null)
			{
				rec.put(EVCombinedRec.PARENT_ID, rs.getString("seq_num"));
			}
			
			if(getAction() != null && !(getAction().equalsIgnoreCase("lookup")))
			{
				this.writer.writeRec(rec);
			}
			 /*HT added 09/21/2020 for ES lookup*/
            else if (getAction() != null && getAction().equalsIgnoreCase("lookup"))
            {
            	this.lookupObj.writeLookupRec(rec);
            }
            
        }
    }

    private String formatOrderNumbers(String num1,
            						  String num2)
    {

        if (num1 == null && num2 == null)
        {
            return null;
        }
        else
        {
            if (num1 == null)
            {
                num1 = "";
            }

            if (num2 == null)
            {
                num2 = "";
            }
            num1 = num1.trim();
            num2 = num2.trim();
        }

        if (num1.length() == 0 &&
            num2.length() == 0)
        {
            return null;
        }

        num1 = num1.replace('{', ' ');
        num2 = num2.replace('{', ' ');
        return num1 + ";" + num2;
    }

    private String  formatReportNumbers (String rn)
    {
        if (rn == null)
        {
            return null;
        }

        StringBuffer buf = new StringBuffer();
        StringTokenizer tokens = new StringTokenizer(rn, "{");
        while (tokens.hasMoreTokens())
        {
            String group1 = tokens.nextToken().trim();
            if (group1.length() > 0)
            {
                StringTokenizer tokens2 = new StringTokenizer(group1, ",");
                while (tokens2.hasMoreTokens())
                {
                    String reportNumber = tokens2.nextToken().trim();
                    if (buf.length() > 0)
                    {
                        buf.append(";");
                    }

                    buf.append(reportNumber).append(";");

                    reportNumber = perl.substitute("s# ##g", reportNumber);
                    reportNumber = perl.substitute("s#-##g", reportNumber);
                    reportNumber = perl.substitute("s#/##g", reportNumber);
                    reportNumber = perl.substitute("s#\\.##g", reportNumber);

                    buf.append(reportNumber);
                }
            }
        }

        if (buf.length() == 0)
        {
            return null;
        }

        return buf.toString();
    }

    private String formatCommaDelim(String s)
    {
        if (s == null)
        {
            return null;
        }

        return s.replace(',', ';');
    }

    public String formatCV(String cv)
    {
        if (cv == null)
        {
            return null;
        }

        cv = formatCommaDelim(cv);
        cv = cv.trim();
        cv = perl.substitute("s#\\*##g", cv);
        cv = perl.substitute("s#\\.$##g", cv);
        cv = perl.substitute("s#^Descriptors:##i", cv);
        return cv;
    }

    private String formatFL(String fl)
    {
        if (fl == null)
        {
            return null;
        }

        fl = formatCommaDelim(fl);
        fl = fl.trim();
        fl = perl.substitute("s#\\*##g", fl);
        fl = perl.substitute("s#\\.$##g", fl);
        fl = perl.substitute("s#^Identifiers:##i", fl);
        return fl;
    }

    private String formatAccessionNumber(String an)
    {
        if (an == null)
        {
            return null;
        } //		an = perl.substitute("s#/XAB##g", an);
        //		an = perl.substitute("s# ##g", an);
        //		an = perl.substitute("s#-##g", an);
        //		an = perl.substitute("s#/##g", an);
        an = perl.substitute("s#\\/XAB##i", an);
        an = perl.substitute("s#\\W+##g", an);
        return an;
    }

    private String formatTitle(String ti)
    {
        if (ti == null)
        {
            return null;
        }
        return ti;
    }

    private String formatAbstract(String ab)
    {
        if (ab == null)
        {
            return null;
        }
        return ab;
    }

    public String getPubYear(String rd,
            				  String load_number)
    {
        String mResult = null;
        String years19 = "19";
        String years20 = "20";

        if (load_number != null && load_number.length()>3)
        {
            load_number = load_number.substring(0,3);
        }

        if (rd == null)
        {
            return load_number;
        }

        if (perl.match("/(\\d{2,4})\\D?$/", rd))
        {
            mResult = (perl.group(1).toString());
            if (mResult.length() == 4)
            {
                return mResult;
            }
            else if (mResult.length() < 4)
            {
                int year = Integer.parseInt(mResult);
                if (year >= 20)
                {
                    return years19.concat(mResult);
                }
                else
                {
                    return years20.concat(mResult);
                }
            }
            else
            {
                return mResult;
            }
        }
        else
        {

            return load_number;
        }
    }

    public String formatDelimiter(String fielddata)
    {
        if (fielddata == null)
        {
            return null;
        }

        fielddata = fielddata.replace('{', ';');
        return fielddata;
    }

    private String getAgency(String maa1,
            				 String maa2)
    {

        StringBuffer buf = new StringBuffer();
        if (maa1 != null)
        {
            maa1 = maa1.trim();
        }
        else
        {
            maa1 = "";
        }

        if (maa2 != null)
        {
            maa2 = maa2.trim();
        }
        else
        {
            maa2 = "";
        }

        if (maa1.length() > 0)
        {
            buf.append(maa1);
        }

        if (maa2.length() > 0)
        {
            if (buf.length() > 0)
            {
                buf.append(";");
            }

            buf.append(maa2);
        }

        if (buf.length() == 0)
        {
            return null;
        }

        return buf.toString();
    }

    private String getPatentIssueDate(String rd)
    {

        if (rd == null)
        {
            return null;
        }

        rd = rd.trim();
        if (rd.length() == 0)
        {
            return null;
        }

        String pubYear = null;
        StringTokenizer toks = new StringTokenizer(rd, ",");
        while (toks.hasMoreElements())
        {
            String token = toks.nextToken().trim();
            if (token.toLowerCase().indexOf("patented") > -1)
            {
                String yr = getPubYear(token, null);
                if (perl.match("/(\\d{2,4})\\D?$/", token))
                {
                    String mResult = (perl.group(1).toString());
                    String s = perl.substitute("s#" + mResult + "#" + yr + "#g", token);
                    if (pubYear != null)
                    {
                        pubYear = pubYear + ";" + s;
                    }
                    else
                    {
                        pubYear = s;
                    }
                }
            }
            else if (token.toLowerCase().indexOf("reissue") > -1)
            {
                String yr = getPubYear(token, null);
                if (perl.match("/(\\d{2,4})\\D?$/", token))
                {
                    String mResult = (perl.group(1).toString());
                    String s = perl.substitute("s#" + mResult + "#" + yr + "#g", token);
                    if (pubYear != null)
                    {
                        pubYear = pubYear + ";" + s;
                    }
                    else
                    {
                        pubYear = s;
                    }
                }
            }
        }

        return pubYear;
    }

    private String getPatentAppDate(String rd)
    {

        if (rd == null)
        {
            return null;
        }

        rd = rd.trim();
        if (rd.length() == 0)
        {
            return null;
        }

        String pubYear = null;
        StringTokenizer toks = new StringTokenizer(rd, ",");
        while (toks.hasMoreElements())
        {
            String token = toks.nextToken().trim();
            if (token.toLowerCase().indexOf("filed") > -1)
            {
                String yr = getPubYear(token, null);
                if (perl.match("/(\\d{2,4})\\D?$/", token))
                {
                    String mResult = (perl.group(1).toString());
                    pubYear = perl.substitute("s#" + mResult + "#" + yr + "#g", token);
                }
            }
        }

        return pubYear;
    }

    private String getLanguage(String fielddataIC,
            				   String fielddataSU)
    {
        String mResult;
        String language = "English";
        if (fielddataIC != null &&
            fielddataIC.length() > 0)
        {
            if (perl.match("/;(\\d+)/", fielddataIC))
            {
                mResult = (perl.group(1).toString());
                if (mResult.endsWith("0"))
                {
                    language = "Unknown Language";
                }
                else if (mResult.endsWith("1"))
                {
                    language = "English";
                }
                else if (mResult.endsWith("2"))
                {
                    language = "Translation";
                }
                else if (mResult.endsWith("3"))
                {
                    language = getForeignLang(fielddataIC);
                }
            }

            return language;
        }
        else
        {
            return fielddataSU;
        }

    }

    private String getForeignLang(String fielddataIC)
    {
        String lang = "";
        if (perl.match("#/lnafr#i", fielddataIC))
        {
            lang = "Afrikaans";
        }
        else if (perl.match("#/lnara#i", fielddataIC))
        {
            lang = "Arabic";
        }
        else if (perl.match("#/lnbel#i", fielddataIC))
        {
            lang = "Belorussian";
        }
        else if (perl.match("#/lnbul#i", fielddataIC))
        {
            lang = "Bulgarian";
        }
        else if (perl.match("#/lnchi#i", fielddataIC))
        {
            lang = "Chinese";
        }
        else if (perl.match("#/lncro#i", fielddataIC))
        {
            lang = "Croatian";
        }
        else if (perl.match("#/lncze#i", fielddataIC))
        {
            lang = "Czech";
        }
        else if (perl.match("#/lndan#i", fielddataIC))
        {
            lang = "Danish";
        }
        else if (perl.match("#/lndut#i", fielddataIC))
        {
            lang = "Dutch";
        }
        else if (perl.match("#/lnest#i", fielddataIC))
        {
            lang = "Estonian";
        }
        else if (perl.match("#/lnfin#i", fielddataIC))
        {
            lang = "Finnish";
        }
        else if (perl.match("#/lnfle#i", fielddataIC))
        {
            lang = "Flemish";
        }
        else if (perl.match("#/lnfre#i", fielddataIC))
        {
            lang = "French";
        }
        else if (perl.match("#/lnger#i", fielddataIC))
        {
            lang = "German";
        }
        else if (perl.match("#/lngre#i", fielddataIC))
        {
            lang = "Greek";
        }
        else if (perl.match("#/lnheb#i", fielddataIC))
        {
            lang = "Hebrew";
        }
        else if (perl.match("#/lnhun#i", fielddataIC))
        {
            lang = "Hungarian";
        }
        else if (perl.match("#/lnind#i", fielddataIC))
        {
            lang = "Indonesian";
        }
        else if (perl.match("#/lnira#i", fielddataIC))
        {
            lang = "Iranian";
        }
        else if (perl.match("#/lnita#i", fielddataIC))
        {
            lang = "Italian";
        }
        else if (perl.match("#/lnjap#i", fielddataIC))
        {
            lang = "Japanese";
        }
        else if (perl.match("#/lnkor#i", fielddataIC))
        {
            lang = "Korean";
        }
        else if (perl.match("#/lnlit#i", fielddataIC))
        {
            lang = "Lithuanian";
        }
        else if (perl.match("#/lnmal#i", fielddataIC))
        {
            lang = "Malay";
        }
        else if (perl.match("#/lnnor#i", fielddataIC))
        {
            lang = "Norwegian";
        }
        else if (perl.match("#/lnoth#i", fielddataIC))
        {
            lang = "Other languages";
        }
        else if (perl.match("#/lnpol#i", fielddataIC))
        {
            lang = "Polish";
        }
        else if (perl.match("#/lnpor#i", fielddataIC))
        {
            lang = "Portuguese";
        }
        else if (perl.match("#/lnrom#i", fielddataIC))
        {
            lang = "Romanian";
        }
        else if (perl.match("#/lnrus#i", fielddataIC))
        {
            lang = "Russian";
        }
        else if (perl.match("#/lnser#i", fielddataIC))
        {
            lang = "Serbo Croatian";
        }
        else if (perl.match("#/lnsev#i", fielddataIC))
        {
            lang = "Several Languages";
        }
        else if (perl.match("#/lnslo#i", fielddataIC))
        {
            lang = "Slovak";
        }
        else if (perl.match("#/lnspa#i", fielddataIC))
        {
            lang = "Spanish";
        }
        else if (perl.match("#/lnswe#i", fielddataIC))
        {
            lang = "Swedish";
        }
        else if (perl.match("#/lntha#i", fielddataIC))
        {
            lang = "Thai";
        }
        else if (perl.match("#/lntur#i", fielddataIC))
        {
            lang = "Turkish";
        }
        else if (perl.match("#/lnukr#i", fielddataIC))
        {
            lang = "Ukrainian";
        }
        else if (perl.match("#/lnvie#i", fielddataIC))
        {
            lang = "Vietnamese";
        }

        return lang;
    }

    public String formatNotes(String notes)
    {
        if (notes == null)
        {
            return null;
        }

        notes = notes.trim();
        if (notes.length() == 0)
        {
            return null;
        }

        return notes;
    }

    public String formatAffil(String af)
    {
        if (af == null)
        {
            return null;
        }

        if (af.length() == 0)
        {
            return null;
        }

        return af;
    }

    private String getVolume(String fielddata)
    {

        if (fielddata == null)
        {
            return null;
        }

        fielddata = fielddata.trim();
        if (fielddata.length() == 0)
        {
            return null;
        }

        PatternMatcherInput input;
        Pattern pattern = null;
        String regularExpression;
        StringBuffer volume = new StringBuffer();
        regularExpression = "/\\w(\\d{2})(\\d{2})";
        try
        {
            pattern = compiler.compile(regularExpression);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        input = new PatternMatcherInput(fielddata);
        while (matcher.contains(input, pattern))
        {
            MatchResult result = matcher.getMatch();
            if (volume.length() > 0)
            {
                volume = volume.append(";");
            }
            volume = volume.append(result.group(1));
        }
        return volume.toString();
    }

    private String formatClassCodes(String cl)
    {
        if (cl == null)
        {
            return null;
        }

        cl.trim();
        if (cl.length() == 0)
        {
            return null;
        }

        cl = perl.substitute("s#^Field##i", cl);
        cl = perl.substitute("s#\\*##g", cl);
        cl.trim();
        if (cl.length() == 0)
        {
            return null;
        }

        return formatCommaDelim(cl);
    }

    private String getIssue(String fielddata)
    {

        if (fielddata == null)
        {
            return null;
        }

        fielddata = fielddata.trim();
        if (fielddata.length() == 0)
        {
            return null;
        }

        PatternMatcherInput input;
        Pattern pattern = null;
        String regularExpression;
        StringBuffer issue = new StringBuffer();
        regularExpression = "/\\w(\\d{2})(\\d{2})";
        try
        {
            pattern = compiler.compile(regularExpression);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        input = new PatternMatcherInput(fielddata);
        while (matcher.contains(input, pattern))
        {
            MatchResult result = matcher.getMatch();
            if (issue.length() > 0)
            {
                issue = issue.append(";");
            }
            issue = issue.append(result.group(2));
        }
        return issue.toString();
    }

    private String getAvailability(String vn,
            					   String pr)
    {

        if (vn == null)
        {
            vn = "";
        }

        if (pr == null)
        {
            pr = "";
        }

        vn = vn.trim();
        pr = pr.trim();
        String av = vn;
        if (pr != null &&
                pr.length() > 0)
        {
            if (av != null)
            {
                av = av + " " + pr;
            }
            else
            {
                av = " " + pr;
            }
        }

        return av;
    }

    private String getStartPage(String p)
    {
        if (p == null)
        {
            return null;
        }

        p = p.trim();
        if (p.length() == 0)
        {
            return null;
        }

        return getFirstNumber(p);
    }

    private String getFirstNumber(String v)
    {

        MatchResult mResult = null;
        if (v == null)
        {
            return null;
        }

        if (perl.match("/[1-9][0-9]*/", v))
        {
            mResult = perl.getMatch();
        }
        else
        {
            return null;
        }

        return mResult.toString();
    }


    private String getCountry(String fielddataAF, String ic)
    {
        StringBuffer buf = new StringBuffer();
        if (fielddataAF != null)
        {
            for (int i = 0; i < countries.length; ++i)
            {
                String c = countries[i].toLowerCase();
                if (perl.match("/[^a-zA-Z]" + c + "[^a-zA-Z]/i", fielddataAF))
                {
                    if (buf.length() > 0)
                    {
                        buf.append(";");
                    }
                    buf.append(Country.formatCountry(countries[i]));
                }
            }
        }

        if(buf.length() == 0)
        {
            if((ic != null) && (perl.match("#\\/fn([a-z]{2})#i",ic)))
            {
                String strCountryCode = perl.group(1).toUpperCase();
                if(NTISData.mapCountryCodes.containsKey(strCountryCode))
                {
                    String co =  (String) NTISData.mapCountryCodes.get(strCountryCode);
                    return Country.formatCountry(co);
                }
            }
        }

        if (buf.length() == 0)
        {
            return "united states";
        }

        return buf.toString();
    }

    public void writeCombinedByWeekHook(Connection con,
            							int weekNumber)
    	throws Exception
    {
        Statement stmt = null;
        ResultSet rs = null;
        System.out.println("Running query...");
        try
        {

            stmt = con.createStatement();
            rs = stmt.executeQuery("select LOAD_NUMBER,M_ID,AN,TI,TN,PN,AB,IC,SU,DES,IDE,SO,PA1,PA2,PA2,PA3,PA4,PA5,RD,RN,CAT,VI,XP,AV,MAA1,MAA2,CT,PR,HN,seq_num from " + tablename + " where load_number =" + weekNumber);
            writeRecs(rs);
            this.writer.end();
            this.writer.flush();
        }
        finally
        {

            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            if (stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    public String[] prepareAuthor(String aString)
    	throws Exception
    {
        StringBuffer buf = new StringBuffer();
        AuthorStream astream = new AuthorStream(new ByteArrayInputStream(aString.getBytes()));
        String s = null;
        ArrayList list = new ArrayList();
        while ((s = astream.readAuthor()) != null)
        {
            s = s.trim();
            if (s.length() > 0)
            {
                s = stripAnon(s);
                s = s.trim();
                s = s.replaceAll("-", " qqdashqq ");
                list.add(s);
            }
        }

        return (String[]) list.toArray(new String[1]);
    }

    private String stripAnon(String line)
    {
        line = perl.substitute("s/\\banon\\b/ /gi", line);
        line = perl.substitute("s/\\(ed\\.\\)/ /gi", line);
        return line;
    }

    public String replaceNull(String sVal)
    {

        if (sVal == null) sVal = "";
        return sVal;
    }

    public String[] prepareMulti(String multiString)
    	throws Exception
    {

        AuthorStream astream = new AuthorStream(new ByteArrayInputStream(multiString.getBytes()));
        String s = null;
        ArrayList list = new ArrayList();
        while ((s = astream.readAuthor()) != null)
        {
            s = s.trim();
            if (s.length() > 0)
            {
                list.add(s);
            }
        }

        return (String[]) list.toArray(new String[1]);
    }

    @Override
   	public void writeLookupByWeekHook(int weekNumber) throws Exception {
   		System.out.println("Extract Lookup");
   		String database =  Combiner.CURRENTDB;
   		lookupObj = new LookupEntry(database, weekNumber);
       	lookupObj.init();
   		
   	}

}
