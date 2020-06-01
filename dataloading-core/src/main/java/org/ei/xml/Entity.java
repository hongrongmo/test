package org.ei.xml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.commons.text.StringEscapeUtils;

public class Entity {
    private static Properties etable = new Properties();
    private static Properties extraTable = new Properties();
    private static Properties isotable = new Properties();
    private static Properties utftable = new Properties();
    private static Perl5Util perl = new Perl5Util();
    private static final int MIN_ESCAPE = 2;
    private static final int MAX_ESCAPE = 7;
    
    public static void main(String args[]) {
        try {
            String test = "?? b c d E ?";
            // System.out.println(Entity.replaceLatinChars(test));
            String line = null;
            String filename = "c:/tmp/entity.txt";
            FileWriter outputFile = new FileWriter(filename, true);
            String inputFileName = args[0];
            // String inputFileName = "c:/tmp/entity.dat";
            String[] fileNameArray = null;
            BufferedReader in = null;
            if (inputFileName.indexOf(";") > 0) {
                fileNameArray = inputFileName.split(";");
            } else {
                fileNameArray = new String[1];
                fileNameArray[0] = inputFileName;
            }
            for (int i = 0; i < fileNameArray.length; i++) {
                in = new BufferedReader(new FileReader(fileNameArray[i]));
                int j = 1;
                while ((line = in.readLine()) != null) {
                    findEntity(line, outputFile);                   
                }

                outputFile.flush();
            }
        } catch (Exception e) {
            System.out.println("exception= " + e);
        }

    }
    
    public static String unescapeHtml(final String input) 
    {
        StringWriter writer = null;
        int len = input.length();
        int i = 1;
        int st = 0;
        while (true) 
        {
            // look for '&'
            while (i < len && input.charAt(i-1) != '&')
                i++;
            if (i >= len)
                break;

            // found '&', look for ';'
            int j = i;
            while (j < len && j < i + MAX_ESCAPE + 1 && input.charAt(j) != ';')
                j++;
            if (j == len || j < i + MIN_ESCAPE || j == i + MAX_ESCAPE + 1) {
                i++;
                continue;
            }

            // found entity number escape 
            if (input.charAt(i) == '#') 
            {
                // numeric escape
                int k = i + 1;
                int radix = 10;

                final char firstChar = input.charAt(k);
                if (firstChar == 'x' || firstChar == 'X') 
                {
                    k++;
                    radix = 16;
                }

                try {
	                    int entityValue = Integer.parseInt(input.substring(k, j), radix);
	                    
	                    /*
	                    if(entityValue==8239) //change NARROW NO-BREAK SPACE to space
	                    {
	                    	entityValue=0;
	                    }
	                    */
	                    
	                    if (writer == null) 
	                        writer = new StringWriter(input.length());
	                   	writer.append(input.substring(st, i - 1));
	
	                    if (entityValue > 0xFFFF) {                   
	                        final char[] chrs = Character.toChars(entityValue);
	                        writer.write(chrs[0]);
	                        writer.write(chrs[1]);
	                    } else {                   
	                        writer.write(entityValue);
	                    }
	
	                } catch (NumberFormatException ex) { 
	                    i++;
	                    continue;
	                }
	            }
	            else 
	            {
	                // entity named escape
	            	//CharSequence value = StringEscapeUtils.unescapeHtml4("&"+input.substring(i, j)+";"); 
	            	String originalString = "&"+input.substring(i, j)+";";
	            	String value = StringEscapeUtils.unescapeHtml4(originalString);
	            	if(originalString.equals(value))
	            	{
	            		value = extraTable.getProperty(originalString);
	            	}
	            	
	                if (value == null) 
	                {                	
	                    i++;
	                    continue;	                	
	                }
	               
	                	
	
	                if (writer == null) 
	                    writer = new StringWriter(input.length());
	                writer.append(input.substring(st, i - 1));
	
	                writer.append(value);
	            }

            // skip escape
            st = j + 1;
            i = st;
        }

        if (writer != null) 
        {
            writer.append(input.substring(st, len));
            return writer.toString();
        }
        return input;
    }

    public static void findEntity(String inputString, FileWriter outputFile) {
        try {
            Perl5Util matcher = new Perl5Util();
            PatternMatcherInput input = new PatternMatcherInput(inputString);
            Hashtable<?, ?> matches = new Hashtable<Object, Object>();

            while (matcher.match("/[a-zA-Z]&#{0,1}[a-z0-9]*;/i", input)) {
                MatchResult result = matcher.getMatch();
                outputFile.write(result.toString() + "\n");
            }

        } catch (Exception e) {
            System.out.println("Exception in findEntity= " + e);
        }
    }

    public static String replaceLatinChars(String s) {

        if (s == null)
            return s;

        StringBuffer buffer = new StringBuffer();
        StringBuffer schar = new StringBuffer();

        char[] arrChars = s.toCharArray();

        int i = 0;

        for (int j = 0; j < arrChars.length; j++) {
            i = (int) arrChars[j];

            if (i >= 1 && i <= 126) {

                buffer.append(arrChars[j]);

            } else {

                schar.append(arrChars[j]);

                String sval = (String) isotable.get(schar.substring(0, schar.length()));

                if (sval != null)
                    buffer.append(sval);
                else
                    buffer.append(" ");

                schar.setLength(0);
            }
        }

        return buffer.toString();

    }

    public static String replaceUTFString(String s) {

        if (s == null) {
            return null;
        }
        
        
        Perl5Util matcher = new Perl5Util();
        PatternMatcherInput input = new PatternMatcherInput(s);

        Hashtable<String, String> matches = new Hashtable<String, String>();

        while (matcher.match("/{acute over \\([a-zA-Z]\\)}/i", input)) {
            MatchResult result = matcher.getMatch();
            matches.put(result.toString(), "");

        }

        input = new PatternMatcherInput(s);

        while (matcher.match("/{umlaut over \\([a-zA-Z]\\)}/i", input)) {
            MatchResult result = matcher.getMatch();
            matches.put(result.toString(), "");

        }

        Enumeration<String> keys = matches.keys();
        while (keys.hasMoreElements()) {
            StringBuffer pbuf = new StringBuffer();
            String match = (String) keys.nextElement();
            String newValue = utftable.getProperty(match);

            if (newValue == null) {
                newValue = "";
            }

            pbuf.append("s/");
            match = matcher.substitute("s/\\(/\\\\(/g", match);
            match = matcher.substitute("s/\\)/\\\\)/g", match);

            pbuf.append(match);
            pbuf.append("/");
            pbuf.append(newValue);
            pbuf.append("/g");

            s = matcher.substitute(pbuf.toString(), s);

        }
        

        return s;

    }

    private static String convertDecimalToHex(String s) {
        String outString = null;
        try {
            int intValue = Integer.parseInt(s.substring(2, s.length() - 1));
            outString = "&#x" + Integer.toHexString(intValue) + ";";
        } catch (Exception e) {
            outString = s;
        }

        return outString;
    }

    public static String prepareString(String s) {

        if (s == null) {
            return null;
        }

        Perl5Util matcher = new Perl5Util();
        PatternMatcherInput input = new PatternMatcherInput(s);

        Hashtable<String, String> matches = new Hashtable<String, String>();

        while (matcher.match("/&#{0,1}[a-z0-9]*;/i", input)) {
            MatchResult result = matcher.getMatch();
            matches.put(result.toString(), "");
        }

        Enumeration<String> keys = matches.keys();
        while (keys.hasMoreElements()) {

            StringBuffer pbuf = new StringBuffer();
            String match = (String) keys.nextElement();
            String match1 = null;
            if (match.indexOf("&#") == 0 && match.indexOf("&#x") < 0) {
                match1 = convertDecimalToHex(match);
            } else if (match.indexOf("&#x") == 0) {
                match1 = match.toLowerCase();
            } else {
                match1 = match;
            }          
            
            //block this line for Elastic Search
            String newValue = match1; //use this line for elastic search
            //String newValue = etable.getProperty(match1); //use this line for FAST

            if (newValue == null) {
                newValue = "";
                //System.out.println("MATCH1"+match1);
            }

            pbuf.append("s/");
            pbuf.append(match);
            pbuf.append("/");
            pbuf.append(newValue);
            pbuf.append("/g");
            s = matcher.substitute(pbuf.toString(), s);
        }            

        return s;
    }
    
    static
    {
    	
    		extraTable.setProperty("&dstrok;","đ");
    		extraTable.setProperty("&hstrok;","ħ");
    		extraTable.setProperty("&lstrok;","ł");
    		extraTable.setProperty("&tstrok;","ŧ");
    		extraTable.setProperty("&Dstrok;","Đ");
    		extraTable.setProperty("&Hstrok;","Ħ");
    		extraTable.setProperty("&Lstrok;","Ł");
    		extraTable.setProperty("&Tstrok;","Ŧ");
    		extraTable.setProperty("&Gbreve;","Ğ");
    		extraTable.setProperty("&Ustrok;","Ŭ");
    		extraTable.setProperty("&gstrok;","ğ");
    		extraTable.setProperty("&ustrok;","ŭ");
    		
    }

    static {
        etable.setProperty("&Aacute;", "A");
        etable.setProperty("&aacute;", "a");
        etable.setProperty("&Acirc;", "A");
        etable.setProperty("&acirc;", "a");
        etable.setProperty("&acute;", "");
        etable.setProperty("&AElig;", "AE");
        etable.setProperty("&aelig;", "ae");
        etable.setProperty("&Afr;", "A");
        etable.setProperty("&Agrave;", "A");
        etable.setProperty("&agrave;", "a");
        etable.setProperty("&aleph;", "");
        etable.setProperty("&alpha;", "");
        etable.setProperty("&amp;", "");
        etable.setProperty("&and;", "");
        etable.setProperty("&ang;", "");
        etable.setProperty("&ang90;", "");
        etable.setProperty("&ap;", "");
        etable.setProperty("&Aring;", "A");
        etable.setProperty("&aring;", "a");
        etable.setProperty("&Ascr;", "A");
        etable.setProperty("&Atilde;", "A");
        etable.setProperty("&atilde;", "a");
        etable.setProperty("&Auml;", "A");
        etable.setProperty("&auml;", "a");
        etable.setProperty("&becaus;", "");
        etable.setProperty("&bepsi;", "");
        etable.setProperty("&beta;", "");
        etable.setProperty("&Bfr;", "B");
        etable.setProperty("&breve;", "");
        etable.setProperty("&Bscr;", "B");
        etable.setProperty("&bumpe;", "");
        etable.setProperty("&cap;", "");
        etable.setProperty("&capped;", "");
        etable.setProperty("&caron;", "");
        etable.setProperty("&Ccedil;", "C");
        etable.setProperty("&ccedil;", "c");
        etable.setProperty("&cedil;", "");
        etable.setProperty("&Cfr;", "C");
        etable.setProperty("&chi;", "x");
        etable.setProperty("&circ;", "");
        etable.setProperty("&cong;", "");
        etable.setProperty("&conint;", "");
        etable.setProperty("&Conint;", "");
        etable.setProperty("&Copf;", "");
        etable.setProperty("&copy;", "");
        etable.setProperty("&Cscr;", "C");
        etable.setProperty("&cup;", "U");
        etable.setProperty("&dagger;", "");
        etable.setProperty("&Dagger;", "");
        etable.setProperty("&darr;", "");
        etable.setProperty("&Dashv;", "");
        etable.setProperty("&dashv;", "");
        etable.setProperty("&dasia;", "");
        etable.setProperty("&deg;", "");
        etable.setProperty("&delta;", "");
        etable.setProperty("&Delta;", "");
        etable.setProperty("&Dfr;", "D");
        etable.setProperty("&diam;", "");
        etable.setProperty("&diams;", "");
        etable.setProperty("&die;", "");
        etable.setProperty("&divide;", "");
        etable.setProperty("&dot;", "");
        etable.setProperty("&Dscr;", "D");
        etable.setProperty("&dstrok;", "d");
        etable.setProperty("&Dstrok;", "D");
        etable.setProperty("&duntilde;", "");
        etable.setProperty("&Eacute;", "E");
        etable.setProperty("&eacute;", "e");
        etable.setProperty("&Ecirc;", "E");
        etable.setProperty("&ecirc;", "e");
        etable.setProperty("&eDot;", "");
        etable.setProperty("&Efr;", "E");
        etable.setProperty("&Egrave;", "E");
        etable.setProperty("&egrave;", "e");
        etable.setProperty("&epsi;", "");
        etable.setProperty("&epsiv;", "");
        etable.setProperty("&equiv;", "");
        etable.setProperty("&Escr;", "E");
        etable.setProperty("&esdot;", "");
        etable.setProperty("&eta;", "n");
        etable.setProperty("&Euml;", "E");
        etable.setProperty("&euml;", "e");
        etable.setProperty("&euro;", "E");
        etable.setProperty("&exist;", "");
        etable.setProperty("&female;", "");
        etable.setProperty("&Ffr;", "F");
        etable.setProperty("&forall;", "");
        etable.setProperty("&frac1116;", "");
        etable.setProperty("&frac116;", "");
        etable.setProperty("&frac12;", "");
        etable.setProperty("&frac1316;", "");
        etable.setProperty("&frac14;", "");
        etable.setProperty("&frac1516;", "");
        etable.setProperty("&frac18;", "");
        etable.setProperty("&frac316;", "");
        etable.setProperty("&frac34;", "");
        etable.setProperty("&frac38;", "");
        etable.setProperty("&frac516;", "");
        etable.setProperty("&frac58;", "");
        etable.setProperty("&frac716;", "");
        etable.setProperty("&frac78;", "");
        etable.setProperty("&frac916;", "");
        etable.setProperty("&Fscr;", "F");
        etable.setProperty("&gamma;", "");
        etable.setProperty("&Gamma;", "");
        etable.setProperty("&ges;", "");
        etable.setProperty("&Gfr;", "G");
        etable.setProperty("&grave;", "");
        etable.setProperty("&#096;", "");
        etable.setProperty("&Gscr;", "G");
        etable.setProperty("&gsim;", "");
//        etable.setProperty("&gt;", ">");
//        etable.setProperty("&Gt;", ">");
        etable.setProperty("&gt;", "");    //HH 01/18/2014 from eijava
        etable.setProperty("&Gt;", "");    //HH 01/18/2014 from eijava

        etable.setProperty("&harr;", "");
        etable.setProperty("&hArr;", "");
        etable.setProperty("&hellip;", "");
        etable.setProperty("&herma;", "");
        etable.setProperty("&Hfr;", "H");
        etable.setProperty("&Hscr;", "H");
        etable.setProperty("&Iacute;", "I");
        etable.setProperty("&iacute;", "i");
        etable.setProperty("&Icirc;", "I");
        etable.setProperty("&icirc;", "i");
        etable.setProperty("&iexcl;", "");
        etable.setProperty("&Ifr;", "I");
        etable.setProperty("&Igrave;", "I");
        etable.setProperty("&igrave;", "i");
        etable.setProperty("&imped;", "z");
        etable.setProperty("&infin;", "");
        etable.setProperty("&inodot;", "i");
        etable.setProperty("&int;", "");
        etable.setProperty("&iota;", "");
        etable.setProperty("&iquest;", "");
        etable.setProperty("&Iscr;", "I");
        etable.setProperty("&isin;", "");
        etable.setProperty("&Iuml;", "I");
        etable.setProperty("&iuml;", "i");
        etable.setProperty("&Jfr;", "J");
        etable.setProperty("&Jscr;", "J");
        etable.setProperty("&kappa;", "k");
        etable.setProperty("&Kfr;", "K");
        etable.setProperty("&Kscr;", "K");
        etable.setProperty("&lambda;", "");
        etable.setProperty("&Lambda;", "");
        etable.setProperty("&lang;", "");
        etable.setProperty("&larr;", "");
        etable.setProperty("&lArr;", "");
        etable.setProperty("&lceil;", "");
        etable.setProperty("&ldquo;", "");
        etable.setProperty("&ldr;", "");
        etable.setProperty("&les;", "");
        etable.setProperty("&lfloor;", "");
        etable.setProperty("&Lfr;", "L");
        etable.setProperty("&lowbar;", "_");
        etable.setProperty("&lrarr2;", "");
        etable.setProperty("&lrhar2;", "");
        etable.setProperty("&lscr;", "l");
        etable.setProperty("&Lscr;", "L");
        etable.setProperty("&lsim;", "");
        etable.setProperty("&lstrok;", "l");
        etable.setProperty("&Lstrok;", "L");
//        etable.setProperty("&lt;", "<");
//        etable.setProperty("&Lt;", "<");
        etable.setProperty("&lt;", "");   //HH 01/18/2015 from eijava
        etable.setProperty("&Lt;", "");   //HH 01/18/2015 from eijava

        etable.setProperty("&macr;", "");
        etable.setProperty("&male;", "");
        etable.setProperty("&mdash;", "");
        etable.setProperty("&Mfr;", "M");
        etable.setProperty("&middot;", "");
        etable.setProperty("&minus;", "");
        etable.setProperty("&mnplus;", "");
        etable.setProperty("&Mscr;", "M");
        etable.setProperty("&mu;", "");
        etable.setProperty("&nabla;", "");
        etable.setProperty("&ndash;", "");
        etable.setProperty("&ndashv;", "");
        etable.setProperty("&ne;", "");
        etable.setProperty("&nearr;", "");
        etable.setProperty("&nexist;", "");
        etable.setProperty("&Nfr;", "N");
        etable.setProperty("&ngt;", "");
        etable.setProperty("&nland;", "");
        etable.setProperty("&nlt;", "");
        etable.setProperty("&Nopf;", "N");
        etable.setProperty("&not;", "");
        etable.setProperty("&notin;", "");
        etable.setProperty("&npar;", "");
        etable.setProperty("&Nscr;", "N");
        etable.setProperty("&nsub;", "");
        etable.setProperty("&nsube;", "");
        etable.setProperty("&nsup;", "");
        etable.setProperty("&nsupe;", "");
        etable.setProperty("&nthroot;", "");
        etable.setProperty("&Ntilde;", "N");
        etable.setProperty("&ntilde;", "n");
        etable.setProperty("&nu;", "N");
        etable.setProperty("&nvdash;", "");
        etable.setProperty("&nwarr;", "");
        etable.setProperty("&Oacute;", "O");
        etable.setProperty("&oacute;", "o");
        etable.setProperty("&oarr;", "");
        etable.setProperty("&Ocirc;", "O");
        etable.setProperty("&ocirc;", "o");
        etable.setProperty("&odot;", "o");
        etable.setProperty("&oelig;", "oe");
        etable.setProperty("&OElig;", "OE");
        etable.setProperty("&Ofr;", "O");
		etable.setProperty("&Ogr;", "O");
        etable.setProperty("&ogr;", "o");
        etable.setProperty("&omicron;", "O");
        etable.setProperty("&Ograve;", "O");
        etable.setProperty("&ograve;", "o");
        etable.setProperty("&omega;", "");
        etable.setProperty("&Omega;", "");
        etable.setProperty("&ominus;", "");
        etable.setProperty("&oplus;", "");
        etable.setProperty("&or;", "");
        etable.setProperty("&Oscr;", "O");
        etable.setProperty("&oslash;", "o");
        etable.setProperty("&Oslash;", "O");
        etable.setProperty("&Otilde;", "O");
        etable.setProperty("&otilde;", "o");
        etable.setProperty("&otimes;", "");
        etable.setProperty("&Ouml;", "O");
        etable.setProperty("&ouml;", "o");
        etable.setProperty("&par;", "");
        etable.setProperty("&para;", "");
        etable.setProperty("&part;", "");
        etable.setProperty("&perp;", "");
        etable.setProperty("&Pfr;", "P");
        etable.setProperty("&phi;", "");
        etable.setProperty("&Phi;", "");
        etable.setProperty("&pi;", "");
        etable.setProperty("&Pi;", "");
        etable.setProperty("&planck;", "h");
        etable.setProperty("&plusmn;", "");
        etable.setProperty("&pound;", "");
        etable.setProperty("&prop;", "");
        etable.setProperty("&Pscr;", "P");
        etable.setProperty("&psi;", "");
        etable.setProperty("&Psi;", "");
        etable.setProperty("&Qfr;", "Q");
        etable.setProperty("&Qopf;", "Q");
        etable.setProperty("&Qscr;", "Q");
        etable.setProperty("&radic;", "");
        etable.setProperty("&rang;", "");
        etable.setProperty("&rarr;", "");
        etable.setProperty("&rArr;", "");
        etable.setProperty("&rceil;", "");
        etable.setProperty("&rdquo;", "");
        etable.setProperty("&reg;", "");
        etable.setProperty("&rfloor;", "");
        etable.setProperty("&Rfr;", "R");
        etable.setProperty("&rho;", "");
        etable.setProperty("&ring;", "");
        etable.setProperty("&rlarr2;", "");
        etable.setProperty("&rlhar2;", "");
        etable.setProperty("&Ropf;", "R");
        etable.setProperty("&Rscr;", "R");
        etable.setProperty("&Sfr;", "S");
        etable.setProperty("&sigma;", "");
        etable.setProperty("&Sigma;", "");
        etable.setProperty("&sigmav;", "");
        etable.setProperty("&sime;", "");
        etable.setProperty("&slashz;", "z");
        etable.setProperty("&sqsub;", "");
        etable.setProperty("&sqsup;", "");
        etable.setProperty("&square;", "");
        etable.setProperty("&Sscr;", "S");
        etable.setProperty("&sub;", "");
        etable.setProperty("&subdot;", "");
        etable.setProperty("&sube;", "");
        etable.setProperty("&subne;", "");
        etable.setProperty("&sup;", "");
        etable.setProperty("&supdot;", "");
        etable.setProperty("&supe;", "");
        etable.setProperty("&supne;", "");
        etable.setProperty("&szlig;", "c");
        etable.setProperty("&tau;", "");
        etable.setProperty("&Tfr;", "T");
        etable.setProperty("&there4;", "");
        etable.setProperty("&Theta;", "");
        etable.setProperty("&thetas;", "");
        etable.setProperty("&thetav;", "");
        etable.setProperty("&thorn;", "");
        etable.setProperty("&THORN;", "");
        etable.setProperty("&tilde;", "");
        etable.setProperty("&times;", "");
        etable.setProperty("&trade;", "");
        etable.setProperty("&Tscr;", "T");
        etable.setProperty("&Uacute;", "U");
        etable.setProperty("&uacute;", "u");
        etable.setProperty("&uarr;", "");
        etable.setProperty("&Ucirc;", "U");
        etable.setProperty("&ucirc;", "u");
        etable.setProperty("&udarr;", "");
        etable.setProperty("&udot;", "");
        etable.setProperty("&Ufr;", "U");
        etable.setProperty("&Ugrave;", "U");
        etable.setProperty("&ugrave;", "u");
        etable.setProperty("&uml;", "");
        etable.setProperty("&UnderBar;", "");
        etable.setProperty("&upsi;", "y");
        etable.setProperty("&Upsi;", "Y");
        etable.setProperty("&Uscr;", "U");
        etable.setProperty("&utilde;", "");
        etable.setProperty("&utri;", "");
        etable.setProperty("&Uuml;", "U");
        etable.setProperty("&uuml;", "u");
        etable.setProperty("&vDash;", "");
        etable.setProperty("&vdash;", "");
        etable.setProperty("&Vfr;", "F");
        etable.setProperty("&Vscr;", "F");
        etable.setProperty("&Wfr;", "W");
        etable.setProperty("&Wscr;", "W");
        etable.setProperty("&Xfr;", "X");
        etable.setProperty("&xi;", "");
        etable.setProperty("&Xi;", "");
        etable.setProperty("&Xscr;", "X");
        etable.setProperty("&xutri;", "");
        etable.setProperty("&Yacute;", "Y");
        etable.setProperty("&yacute;", "y");
        etable.setProperty("&Yfr;", "Y");
        etable.setProperty("&Yscr;", "Y");
        etable.setProperty("&Yuml;", "Y");
        etable.setProperty("&yuml;", "y");
        etable.setProperty("&zeta;", "z");
        etable.setProperty("&Zfr;", "Z");
        etable.setProperty("&Zopf;", "");
        etable.setProperty("&Zscr;", "Z");

        /**
         * Begin hex mappings
         **/

        etable.setProperty("&#x26", ""); // &
        etable.setProperty("&#xco;", "A"); // latin capital letter A with grave
        etable.setProperty("&#xc1;", "A"); // latin capital letter A with acute
        etable.setProperty("&#xc2;", "A"); // latin capital letter A with circumflex
        etable.setProperty("&#xc3;", "A"); // latin capital letter A with tilde
        etable.setProperty("&#xc4;", "A"); // latin capital letter A with diaeresis
        etable.setProperty("&#xc5;", "A"); // latin capital letter A with ring above
        etable.setProperty("&#xc6;", "AE"); // latin capital letter AE
        etable.setProperty("&#xc7;", "C"); // latin capital letter C with cedilla
        etable.setProperty("&#xc8;", "E"); // latin capital letter E with grave
        etable.setProperty("&#xc9;", "E"); // latin capital letter E with acute
        etable.setProperty("&#xca;", "E"); // latin capital letter E with circumflex
        etable.setProperty("&#xcb;", "E"); // latin capital letter E with diaeresis
        etable.setProperty("&#xcc;", "I"); // latin capital letter I with grave
        etable.setProperty("&#xcd;", "I"); // latin capital letter I with acute
        etable.setProperty("&#xce;", "I"); // latin capital letter I with circumflex
        etable.setProperty("&#xcf;", "I"); // latin capital letter I with diaeresis
        etable.setProperty("&#xd0;", "ETH"); // latin capital letter ETH
        etable.setProperty("&#xd1;", "N"); // latin capital letter N with tilde
        etable.setProperty("&#xd2;", "O"); // latin capital letter O with grave
        etable.setProperty("&#xd3;", "O"); // latin capital letter O with acute
        etable.setProperty("&#xd4;", "O"); // latin capital letter O with circumflex
        etable.setProperty("&#xd5;", "O"); // latin capital letter O with tilde
        etable.setProperty("&#xd6;", "O"); // latin capital letter O with diaeresis
        etable.setProperty("&#xd8;", "O"); // latin capital letter O with slash
        etable.setProperty("&#xd9;", "U"); // latin capital letter U with grave
        etable.setProperty("&#xda;", "U"); // latin capital letter U with acute
        etable.setProperty("&#xdb;", "U"); // latin capital letter U with circumflex
        etable.setProperty("&#xdc;", "U"); // latin capital letter U with diaeresis
        etable.setProperty("&#xdd;", "Y"); // latin capital letter Y with acute
        etable.setProperty("&#xde;", ""); // latin capital letter THORN
        etable.setProperty("&#xdf;", "ss"); // latin small letter sharp s - ess-zed
        etable.setProperty("&#xe0;", "a"); // latin small letter a with grave
        etable.setProperty("&#xe1;", "a"); // latin small letter a with acute
        etable.setProperty("&#xe2;", "a"); // latin small letter a with circumflex
        etable.setProperty("&#xe3;", "a"); // latin small letter a with tilde
        etable.setProperty("&#xe4;", "a"); // latin small letter a with diaeresis
        etable.setProperty("&#xe5;", "a"); // latin small letter a with ring above
        etable.setProperty("&#xe6;", "ae"); // latin small letter ae
        etable.setProperty("&#xe7;", "c"); // latin small letter c with cedilla
        etable.setProperty("&#xe8;", "e"); // latin small letter e with grave
        etable.setProperty("&#xe9;", "e"); // latin small letter e with acute
        etable.setProperty("&#xea;", "e"); // latin small letter e with circumflex
        etable.setProperty("&#xeb;", "e"); // latin small letter e with diaeresis
        etable.setProperty("&#xec;", "i"); // latin small letter i with grave
        etable.setProperty("&#xed;", "i"); // latin small letter i with acute
        etable.setProperty("&#xee;", "i"); // latin small letter i with circumflex
        etable.setProperty("&#xef;", "i"); // latin small letter i with diaeresis
        etable.setProperty("&#xf0;", "eth"); // latin small letter eth
        etable.setProperty("&#xf1;", "n"); // latin small letter n with tilde
        etable.setProperty("&#xf2;", "o"); // latin small letter o with grave
        etable.setProperty("&#xf3;", "o"); // latin small letter o with acute
        etable.setProperty("&#xf4;", "o"); // latin small letter o with circumflex
        etable.setProperty("&#xf5;", "o"); // latin small letter o with tilde
        etable.setProperty("&#xf6;", "o"); // latin small letter o with diaeresis
        etable.setProperty("&#xf7;", ""); // division sign
        etable.setProperty("&#xf8;", "o"); // latin small letter o with slash
        etable.setProperty("&#xf9;", "u"); // latin small letter u with grave
        etable.setProperty("&#xfa;", "u"); // latin small letter u with acute
        etable.setProperty("&#xfb;", "u"); // latin small letter u with circumflex
        etable.setProperty("&#xfc;", "u"); // latin small letter u with diaeresis
        etable.setProperty("&#xfd;", "y"); // latin small letter y with acute
        etable.setProperty("&#xfe;", ""); // latin small letter thorn
        etable.setProperty("&#xff;", "y"); // latin small letter y with diaeresis

        // added by hongrong
        etable.setProperty("&#x300;", "");// combining grave accent
        etable.setProperty("&#x301;", "");// combining acute accent
        etable.setProperty("&#x302;", "");// combining circumflex accent
        etable.setProperty("&#x303;", "");// combining tilde
        etable.setProperty("&#x304;", "");// combining macron
        etable.setProperty("&#x307;", "");// combining dot above
        etable.setProperty("&#x308;", "");// combining dieresis
        etable.setProperty("&#x30a;", "");// combining ring above
        etable.setProperty("&#x30c;", "");// combining caron
        etable.setProperty("&#x327;", "");// combining cedilla
        etable.setProperty("&#x2034;", "");// triple prime
        etable.setProperty("&#x2102;", "C");// double-struck C (&Cdbl;)
        etable.setProperty("&#x2115;", "N");// double-struck N (&Ndbl;)
        etable.setProperty("&#x2119;", "P");// double-struck P (&Pdbl;)
        etable.setProperty("&#x211a;", "Q");// double-struck Q (&Qdbl;)
        etable.setProperty("&#x211d;", "R");// double-struck R (&Rdbl;)
        etable.setProperty("&#x2124;", "Z");// double-struck Z (&Zdbl;)
        etable.setProperty("&#x212c;", "B");// script B (&Bscr;)
        etable.setProperty("&#x2130;", "E");// script E (&Escr;)
        etable.setProperty("&#x2131;", "F");// script F (&Fscr;)
        etable.setProperty("&#x210b;", "H");// script H (&Hscr;)
        etable.setProperty("&#x2110;", "I");// script I (capital i)(&Iscr;)
        etable.setProperty("&#x2112;", "L");// script L (&Lscr;)
        etable.setProperty("&#x2133;", "M");// script M (&Mscr;)
        etable.setProperty("&#x2118;", "P");// script P (&Pscr;)
        etable.setProperty("&#x211b;", "R");// script R (&Rscr;)
        etable.setProperty("&#x211c;", "R");// R-fraktur (&Rfr;)
        etable.setProperty("&#x3b1;", "");// Greek alpha (&alpha;)
        etable.setProperty("&#x3b2;", "");// Greek beta(&beta;)
        etable.setProperty("&#x3b3;", "");// Greek gamma(&gamma;)
        etable.setProperty("&#x3b4;", "");// Greek delta(&delta;)
        etable.setProperty("&#x3b5;", "");// Greek epsilon(&epsi;)
        etable.setProperty("&#x3b6;", "");// Greek zeta(&zeta;)
        etable.setProperty("&#x3b7;", "");// Greek eta(&eta;)
        etable.setProperty("&#x3b8;", "");// Greek theta(&theta;)
        etable.setProperty("&#x3b9;", "");// Greek iota(&iota;)
        etable.setProperty("&#x3ba;", "");// Greek kappa (&kappa;)
        etable.setProperty("&#x3bb;", "");// Greek lambda(&lambda;)
        etable.setProperty("&#x3bc;", "");// Greek mu(&mu;)
        etable.setProperty("&#x3bd;", "");// Greek nu(&nu;)
        etable.setProperty("&#x3be;", "");// Greek xi(&xi;)
        etable.setProperty("&#x3bf;", "");// Greek omicron(&omicron;)
        etable.setProperty("&#x3c0;", "");// Greek pi(&pi;)
        etable.setProperty("&#x3c1;", "");// Greek rho(&rho;)
        etable.setProperty("&#x3c2;", "");// Greek sigma (final)(&sigmav;)
        etable.setProperty("&#x3c3;", "");// Greek sigma (&sigma;)
        etable.setProperty("&#x3c4;", "");// Greek tau(&tau;)
        etable.setProperty("&#x3c5;", "");// Greek upsilon(&upsilon;)
        etable.setProperty("&#x3c6;", "");// Greek phi(&phi;)
        etable.setProperty("&#x3c7;", "");// Greek chi(&chi;)
        etable.setProperty("&#x3c8;", "");// Greek psi(&psi;)
        etable.setProperty("&#x3c9;", "");// Greek omega(&omega;)
        etable.setProperty("&#x391;", "");// Greek ALPHA(&Alpha;)
        etable.setProperty("&#x392;", "");// Greek BETA(&ABeta;)
        etable.setProperty("&#x393;", "");// Greek GAMMA(&Gamma;)
        etable.setProperty("&#x394;", "");// Greek DELTA(&Delta;)
        etable.setProperty("&#x395;", "");// Greek EPSILON(&Epsi;)
        etable.setProperty("&#x396;", "");// Greek ZETA(&Zeta;)
        etable.setProperty("&#x397;", "");// Greek ETA(&Eta;)
        etable.setProperty("&#x398;", "");// Greek THETA(&Theta;)
        etable.setProperty("&#x399;", "");// Greek IOTA(&Iota;)
        etable.setProperty("&#x39a;", "");// Greek KAPPA(&Kappa;)
        etable.setProperty("&#x39b;", "");// Greek LAMBDA(&ABeta;)
        etable.setProperty("&#x39c;", "");// Greek MU(&Mu;)
        etable.setProperty("&#x39d;", "");// Greek NU(&Nu;)
        etable.setProperty("&#x39e;", "");// Greek XI(&Xi;)
        etable.setProperty("&#x39f;", "");// Greek OMICRON(&Omicron;)
        etable.setProperty("&#x3a0;", "");// Greek PI(&Pi;)
        etable.setProperty("&#x3a1;", "");// Greek RHO(&Rho;)
        etable.setProperty("&#x3a2;", "");// Greek SIGMA(&Sigma;)
        etable.setProperty("&#x3a3;", "");// Greek SIGMA(&Sigma;)
        etable.setProperty("&#x3a4;", "");// Greek TAU(&Tau;)
        etable.setProperty("&#x3a5;", "");// Greek UPSILON(&Upsi;)
        etable.setProperty("&#x3a6;", "");// Greek PHI(&Phi;)
        etable.setProperty("&#x3a7;", "");// Greek RCHI(&Chi;)
        etable.setProperty("&#x3a8;", "");// Greek PSI(&Psi;)
        etable.setProperty("&#x3a9;", "");// Greek OMEGA(&Omega;)
        etable.setProperty("&#xa1;", "");// inverted exclamation mark(&iexcl;)
        etable.setProperty("&#xa2;", "");// cent sign(&cent;)
        etable.setProperty("&#xa3;", "");// pound sign(&pound;)
        etable.setProperty("&#xa5;", "");// yen sign(&yen;)
        etable.setProperty("&#xa7;", "");// section sign(&sect;)
        etable.setProperty("&#xa9;", "");// copyright sign(&Ocopy;)
        etable.setProperty("&#xab;", "");// left angle quote(&laquo;)
        etable.setProperty("&#xae;", "");// registered trademark(&reg;)
        etable.setProperty("&#xb0;", "");// degree sign(&deg;)
        etable.setProperty("&#xb1;", "");// plus-or-minus sign(&plusmn;)
        etable.setProperty("&#xb6;", "");// paragraph sign(&para;)
        etable.setProperty("&#xb7;", "");// middle dot(&middot;)
        etable.setProperty("&#xbb;", "");// right angle quote(&raquo;)
        etable.setProperty("&#xbf;", "");// inverted question mark(&iquest;)
        etable.setProperty("&#xd7;", "");// multiplication sign(&times;)
        etable.setProperty("&#x3008;", "");// left angle bracket(&lang;)
        etable.setProperty("&#x3009;", "");// right angle bracket(&rang;)
        etable.setProperty("&#x2019;", "'");
        etable.setProperty("&#x2190;", "");// left arrow(&larr;)
        etable.setProperty("&#x2191;", "");// up arrow(&uarr;)
        etable.setProperty("&#x2192;", "");// right arrow(&rarr;)
        etable.setProperty("&#x2193;", "");// down arrow(&darr;)
        etable.setProperty("&#x2194;", "");// left-right arrow(&harr;)
        etable.setProperty("&#x2196;", "");// north west arrow(&nwarr;)
        etable.setProperty("&#x2197;", "");// north east arrow(&nearr;)
        etable.setProperty("&#x2198;", "");// south east arrow(&searr;)
        etable.setProperty("&#x2199;", "");// south west arrow(&swarr;)
        etable.setProperty("&#x21c4;", "");// right arrow over left arrow(&rlarr2;)
        etable.setProperty("&#x21c6;", "");// left arrow over right arrow(&lrarr2;)
        etable.setProperty("&#x21cb;", "");// left harpoon over right harpoon(&lrhar2;)
        etable.setProperty("&#x21cc;", "");// right harpoon over left harpoon(&rlhar2;)
        etable.setProperty("&#x21d0;", "");// left double arrow(&lArr;)
        etable.setProperty("&#x21d2;", "");// right double arrow(&rArr;)
        etable.setProperty("&#x21d4;", "");// left right double arrow(&iff;)
        etable.setProperty("&#x2213;", "");// minus-or-plus sign(&mnplus;)
        etable.setProperty("&#x211e;", "");// infinity(&infin;)
        etable.setProperty("&#x223c;", "");// similar to(&sim;)
        etable.setProperty("&#x2241;", "");// not similar to(&nsim;)
        etable.setProperty("&#x2243;", "");// asymptotically equal to(&sime;)
        etable.setProperty("&#x2244;", "");// not asymptotically equal to(&sime;)
        etable.setProperty("&#x2245;", "");// approximately equal to(&cong;)
        etable.setProperty("&#x2247;", "");// neither approximately nor actually equal to(&ncong;)
        etable.setProperty("&#x2248;", "");// almost equal to(&ap;)
        etable.setProperty("&#x2249;", "");// not almost equal to(&nap;)
        etable.setProperty("&#x224a;", "");// almost equal or equal to(&ape;)
        etable.setProperty("&#x2252;", "");// approximately equal to or image of(&efDot;)
        etable.setProperty("&#x2253;", "");// image of or approximately equal to (&erDot;)
        etable.setProperty("&#x2260;", "");// not equal to(&ne;)
        etable.setProperty("&#x2261;", "");// identical to(&equiv;)
        etable.setProperty("&#x2262;", "");// not identical to(&nequiv;)
        etable.setProperty("&#x2264;", "");// less-than or equal to(&le;)
        etable.setProperty("&#x2265;", "");// greater-than or equal to(&ge;)
        etable.setProperty("&#x226a;", "");// much less-than(&Lt;)
        etable.setProperty("&#x226b;", "");// much greater-than(&Gt;)
        etable.setProperty("&#x226e;", "");// neither less-than nor equal to(&nlt;)
        etable.setProperty("&#x226f;", "");// not greater-than(&ngt;)
        etable.setProperty("&#x2270;", "");// neither less-than nor equal to(&nle;)
        etable.setProperty("&#x2271;", "");// neither greater-than nor equal to(&nge;)
        etable.setProperty("&#x2272;", "");// less-than or equivalent to(&lsim;)
        etable.setProperty("&#x2273;", "");// greater-than or equivalent to(&gsim;)
        etable.setProperty("&#x222b;", "");// integral(&int;)
        etable.setProperty("&#x222e;", "");// contour integral(&conint;)
        etable.setProperty("&#x2205;", "");// empty set(&emptyv;)
        etable.setProperty("&#x221a;", "");// root(&radic;)
        etable.setProperty("&#x2295;", "");// circled plus(circled plus)
        etable.setProperty("&#x2296;", "");// circled minus(&ominus;)
        etable.setProperty("&#x2297;", "");// circled times(&otimes;)
        etable.setProperty("&#x2299;", "");// middle dot in circle(&odot;)
        etable.setProperty("&#x2200;", "");// for all(&forall;)
        etable.setProperty("&#x2202;", "");// partial differential(&part;)
        etable.setProperty("&#x2203;", "");// there exists(&exist;)
        etable.setProperty("&#x2204;", "");// there does not exist(&nexist;)
        etable.setProperty("&#x2207;", "");// Nabla(&nabla;)
        etable.setProperty("&#x2208;", "");// element of(&isin;)
        etable.setProperty("&#x2209;", "");// not an element of(&notin;)
        etable.setProperty("&#x220b;", "");// contains as member(&ni;)
        etable.setProperty("&#x220c;", "");// there does not exist(&nexist;)
        etable.setProperty("&#x220f;", "");// there exists(&exist;)
        etable.setProperty("&#x2210;", "");// coproduct sign(&coprod;)
        etable.setProperty("&#x2211;", "");// summation sign(&sum;)
        etable.setProperty("&#x221d;", "");// proportional to(&prop;)
        etable.setProperty("&#x2220;", "");// angle(&ang;)
        etable.setProperty("&#x2225;", "");// parallel to(&par;)
        etable.setProperty("&#x2226;", "");// not parallel to(&npar;)
        etable.setProperty("&#x2227;", "");// logical AND(&and;)
        etable.setProperty("&#x2228;", "");// logical OR(&or;)
        etable.setProperty("&#x2229;", "");// intersection(&cap;)
        etable.setProperty("&#x222a;", "");// union(&cup;)
        etable.setProperty("&#x2282;", "");// subset of(&sub;)
        etable.setProperty("&#x2283;", "");// superset of(&sup;)
        etable.setProperty("&#x2284;", "");// not a subset of(&nsub;)
        etable.setProperty("&#x2285;", "");// not a superset of(&nsup;)
        etable.setProperty("&#x2286;", "");// subset of or equal to(&sube;)
        etable.setProperty("&#x2287;", "");// superset of or equal to(&supe;)
        etable.setProperty("&#x2288;", "");// neither a subset of nor equal to(&nsube;)
        etable.setProperty("&#x2289;", "");// neither a superset of nor equal to(&nsupe;)
        etable.setProperty("&#x2234;", "");// therefore(&there4;)
        etable.setProperty("&#x2235;", "");// because(&becaus;)
        etable.setProperty("&#x22a2;", "");// right tack(&vdash;)
        etable.setProperty("&#x22a3;", "");// left tack(&dashv;)
        etable.setProperty("&#x22a4;", "");// inverted perpendicular(&top;)
        etable.setProperty("&#x22a5;", "");// perpendicular(&perp;)
        etable.setProperty("&#x25a0;", "");// black square(&squ;)
        etable.setProperty("&#x25a1;", "");// white square(&squ;)
        etable.setProperty("&#x25c6;", "");// black diamond(&diams;)
        etable.setProperty("&#x25c7;", "");// white diamond(&diam;)
        etable.setProperty("&#x25b2;", "");// black up-triangle(&utrif;)
        etable.setProperty("&#x25b3;", "");// white up-triangle(&utri;)
        etable.setProperty("&#x25bc;", "");// black down-triangle(&dtrif;)
        etable.setProperty("&#x25bd;", "");// white down-triangle(&dtri;)
        etable.setProperty("&#x25b6;", "");// black right-triangle(&rtrif;)
        etable.setProperty("&#x25b7;", "");// white right-triangle(&rtri;)
        etable.setProperty("&#x25c0;", "");// black left-triangle(&ltrif;)
        etable.setProperty("&#x25c1;", "");// white left-triangle(&ltri;)
        etable.setProperty("&#x25cb;", "");// white circle(&cir;)
        etable.setProperty("&#x2642;", "");// male sign(&male;)
        etable.setProperty("&#x2640;", "");// female(&female;)
        etable.setProperty("&#x5d0;", "");// aleph(&aleph;)
        etable.setProperty("&#x141;", "L");// L stroke(&Lstrok;)
        etable.setProperty("&#x142;", "l");// l stroke(&lstrok;)
        etable.setProperty("&#x152;", "");// OE ligature(&OElig;)
        etable.setProperty("&#x153;", "");// oe ligature(&oelig;)
        etable.setProperty("&#x20ac;", "");// euro sign(&euro;)
        etable.setProperty("&#x2122;", "");// trademark sign(&trade;)
        etable.setProperty("&#x210f;", "");// Planck constant over two pi(&plankv;)
        etable.setProperty("&#x2111;", "");// J-fraktur(&Jfr;)
        etable.setProperty("&#x2113;", "");// script small l(&ell;)
        etable.setProperty("&#xac;", "");// logical not sign(&not;)
        etable.setProperty("&#x21c0;", "");// right harpoon, up(&rharu;)
        etable.setProperty("&#x21c1;", "");// right harpoon, down(&rhard;)
        etable.setProperty("&#x21bc;", "");// left harpoon, up(&lharu;)
        etable.setProperty("&#x21bd;", "");// left harpoon, down(&lhard;)
        etable.setProperty("&#x21a0;", "");// right double-headed arrow(&Rarr;)
        etable.setProperty("&#x219e;", "");// left double-headed arrow(&Larr;)
        etable.setProperty("&#x2195;", "");// up-down arrow(&varr;)
        etable.setProperty("&#x2266;", "");// less-than over equal to(&lE;)
        etable.setProperty("&#x22ee;", "");// rvertical ellipsis(&vellip;)
        etable.setProperty("&#x22ef;", "");// midline ellipsis(&mellip;)
        etable.setProperty("&#x22d9;", "");// very much greater-than(&Gg;)
        etable.setProperty("&#x22d8;", "");// very much less-than(&Ll;)
        etable.setProperty("&#x227a;", "");// lprecedes(&pr;)
        etable.setProperty("&#x227b;", "");// succeeds(&sc;)
        etable.setProperty("&#x2259;", "");// estimates(&estimates;)
        etable.setProperty("&#x22b4;", "");// normal subgroup of or equal to(&ltrie;)
        etable.setProperty("&#x22b5;", "");// contains as normal subgroup or equal to(&rtrie;)
        etable.setProperty("&#x230a;", "");// left floor(&lfloor;)
        etable.setProperty("&#x230b;", "");// right floor(&rfloor;)
        etable.setProperty("&#x2308;", "");// left ceiling(&lceil;)
        etable.setProperty("&#x2309;", "");// right ceiling(&rceil;)
        etable.setProperty("&#x22ca;", "");// right-closed times(&rtimes;)
        etable.setProperty("&#x22c9;", "");// left-closed times(&ltimes;)
        etable.setProperty("&#x2127;", "");// mho(&mho;)

        etable.setProperty("&#x15e;", "S");// Cryllic 'S' (see isbn: 9780444522375)
        etable.setProperty("&#x131;", "i");// i with no dot (see isbn: 9780444522375)

        etable.setProperty("&#x73;", "s"); // Converted 's' from SafeHtmlUtil code TMH
        etable.setProperty("&#x17d", "Z"); // LATIN CAPITAL LETTER Z WITH CARON
        
        etable.setProperty("&#x10d", "c"); // LATIN SMALL LETTER C WITH CARON
        etable.setProperty("&#x103", "a"); // LATIN SMALL LETTER A WITH BREVE
        etable.setProperty("&#x105", "a"); // LATIN SMALL LETTER A WITH OGONEK
        etable.setProperty("&#x107", "c"); // LATIN SMALL LETTER C WITH ACUTE
        etable.setProperty("&#x10c", "C"); // LATIN CAPITAL LETTER C WITH CARON
        etable.setProperty("&#x10e", "D"); // LATIN CAPITAL LETTER D WITH CARON
        etable.setProperty("&#x111", "d"); // LATIN SMALL LETTER D WITH STROKE
        etable.setProperty("&#x117", "e"); // LATIN SMALL LETTER E WITH DOT ABOVE
        etable.setProperty("&#x118", "E"); // LATIN CAPITAL LETTER E WITH OGONEK
        etable.setProperty("&#x119", "e"); // LATIN SMALL LETTER E WITH OGONEK
        etable.setProperty("&#x11b", "e"); // LATIN SMALL LETTER E WITH CARON
        etable.setProperty("&#x11f", "g"); // LATIN SMALL LETTER G WITH BREVE
        etable.setProperty("&#x121", "g"); // LATIN SMALL LETTER G WITH DOT ABOVE
        etable.setProperty("&#x130", "I"); // LATIN CAPITAL LETTER I WITH DOT ABOVE
        
        
        
        /**
         * ISO Latin to ascii mappings
         **/
        isotable.setProperty("Â¡", "!"); //  Inverted exclamation
        //isotable.setProperty("Â¢", ""); //  Cent sign
        //isotable.setProperty("Â£", ""); //  Pound sterling
        //isotable.setProperty("Â¤", ""); //  General currency sign
        //isotable.setProperty("Â¥", ""); //  Yen sign
        isotable.setProperty("Â¦", "|"); //  Broken vertical bar
        //isotable.setProperty("Â§", ""); //  Section sign
        //isotable.setProperty("Â¨", ""); //  Umlaut (dieresis)
        //isotable.setProperty("Â©", "&copy;"); //  Copyright
        //isotable.setProperty("Âª", ""); //  Feminine ordinal
        isotable.setProperty("Â«", "<<"); //  Left angle quote, guillemotleft
        //isotable.setProperty("Â¬", ""); //  Not sign
        isotable.setProperty("Â­", "-"); //  Soft hyphen
        //isotable.setProperty("Â®", "&reg;"); //  Registered trademark
        //isotable.setProperty("Â¯", ""); //  Macron accent
        //isotable.setProperty("Â°", ""); //  Degree sign
        isotable.setProperty("Â±", "+/-"); //  Plus or minus
        isotable.setProperty("Â²", "^2"); //  Superscript two
        isotable.setProperty("Â³", "^3"); //  Superscript three
        isotable.setProperty("Â´", "'"); //  Acute accent
        //isotable.setProperty("Âµ", ""); //  Micro sign
        //isotable.setProperty("Â¶", ""); //  Paragraph sign
        //isotable.setProperty("Â·", ""); //  Middle dot
        //isotable.setProperty("Â¸", ""); //  Cedilla
        isotable.setProperty("Â¹", "^1"); //  Superscript one
        //isotable.setProperty("Âº", ""); //  Masculine ordinal
        isotable.setProperty("Â»", ">>"); //  Right angle quote, guillemotright
        isotable.setProperty("Â¼", "1/4"); //  Fraction one-fourth
        isotable.setProperty("Â½", "1/2"); //  Fraction one-half
        isotable.setProperty("Â¾", "3/4"); //  Fraction three-fourths
        isotable.setProperty("Â¿", "?"); //  Inverted question mark
        isotable.setProperty("Ã€", "A"); //  Capital A, grave accent
        isotable.setProperty("Ã�", "A"); //  Capital A, acute accent
        isotable.setProperty("Ã‚", "A"); //  Capital A, circumflex accent
        isotable.setProperty("Ãƒ", "A"); //  Capital A, tilde
        isotable.setProperty("Ã„", "A"); //  Capital A, dieresis or umlaut mark
        isotable.setProperty("Ã…", "A"); //  Capital A, ring
        isotable.setProperty("Ã†", "AE"); //  Capital AE dipthong (ligature)
        isotable.setProperty("Ã‡", "C"); //  Capital C, cedilla
        isotable.setProperty("Ãˆ", "E"); //  Capital E, grave accent
        isotable.setProperty("Ã‰", "E"); //  Capital E, acute accent
        isotable.setProperty("ÃŠ", "E"); //  Capital E, circumflex accent
        isotable.setProperty("Ã‹", "E"); //  Capital E, dieresis or umlaut mark
        isotable.setProperty("ÃŒ", "I"); //  Capital I, grave accent
        isotable.setProperty("Ã�", "I"); //  Capital I, acute accent
        isotable.setProperty("ÃŽ", "I"); //  Capital I, circumflex accent
        isotable.setProperty("Ã�", "I"); //  Capital I, dieresis or umlaut mark
        isotable.setProperty("Ã�", "D"); //  Capital Eth, Icelandic
        isotable.setProperty("Ã‘", "N"); //  Capital N, tilde
        isotable.setProperty("Ã’", "O"); //  Capital O, grave accent
        isotable.setProperty("Ã“", "O"); //  Capital O, acute accent
        isotable.setProperty("Ã”", "O"); //  Capital O, circumflex accent
        isotable.setProperty("Ã•", "O"); //  Capital O, tilde
        isotable.setProperty("Ã–", "O"); //  Capital O, dieresis or umlaut mark
        isotable.setProperty("Ã—", "x"); //  Multiply sign
        isotable.setProperty("Ã˜", "O"); //  Capital O, slash
        isotable.setProperty("Ã™", "U"); //  Capital U, grave accent
        isotable.setProperty("Ãš", "U"); //  Capital U, acute accent
        isotable.setProperty("Ã›", "U"); //  Capital U, circumflex accent
        isotable.setProperty("Ãœ", "U"); //  Capital U, dieresis or umlaut mark
        isotable.setProperty("Ã�", "Y"); //  Capital Y, acute accent
        isotable.setProperty("Ãž", "THORN"); //  Capital THORN, Icelandic
        isotable.setProperty("ÃŸ", "B"); //  Small sharp s, German (sz ligature)
        isotable.setProperty("Ã ", "a"); //  Small a, grave accent
        isotable.setProperty("Ã¡", "a"); //  Small a, acute accent
        isotable.setProperty("Ã¢", "a"); //  Small a, circumflex accent
        isotable.setProperty("Ã£", "a"); //  Small a, tilde
        isotable.setProperty("Ã¤", "a"); //  Small a, dieresis or umlaut mark
        isotable.setProperty("Ã¥", "a"); //  Small a, ring
        isotable.setProperty("Ã¦", "ae"); //  Small ae dipthong (ligature)
        isotable.setProperty("Ã§", "c"); //  Small c, cedilla
        isotable.setProperty("Ã¨", "d"); //  Small e, grave accent
        isotable.setProperty("Ã©", "d"); //  Small e, acute accent
        isotable.setProperty("Ãª", "d"); //  Small e, circumflex accent
        isotable.setProperty("Ã«", "d"); //  Small e, dieresis or umlaut mark
        isotable.setProperty("Ã¬", "i"); //  Small i, grave accent
        isotable.setProperty("Ã­", "i"); //  Small i, acute accent
        isotable.setProperty("Ã®", "i"); //  Small i, circumflex accent
        isotable.setProperty("Ã¯", "i"); //  Small i, dieresis or umlaut mark
        isotable.setProperty("Ã°", "o"); //  Small eth, Icelandic
        isotable.setProperty("Ã±", "n"); //  Small n, tilde
        isotable.setProperty("Ã²", "o"); //  Small o, grave accent
        isotable.setProperty("Ã³", "o"); //  Small o, acute accent
        isotable.setProperty("Ã´", "o"); //  Small o, circumflex accent
        isotable.setProperty("Ãµ", "o"); //  Small o, tilde
        isotable.setProperty("Ã¶", "o"); //  Small o, dieresis or umlaut mark
        isotable.setProperty("Ã·", "/"); //  Division sign
        isotable.setProperty("Ã¸", "o"); //  Small o, slash
        isotable.setProperty("Ã¹", "u"); //  Small u, grave accent
        isotable.setProperty("Ãº", "u"); //  Small u, acute accent
        isotable.setProperty("Ã»", "u"); //  Small u, circumflex accent
        isotable.setProperty("Ã¼", "u"); //  Small u, dieresis or umlaut mark
        isotable.setProperty("Ã½", "y"); //  Small y, acute accent
        isotable.setProperty("Ã¾", "thorn"); //  Small thorn, Icelandic
        isotable.setProperty("Ã¿", "y"); //  Small y, dieresis or umlaut mark
        //HH: 08/18/2016  added for Cafe srctitle normalization
        isotable.setProperty("Ä„", "A");		//Capital A with Ogonek
        isotable.setProperty("Ä‚", "A");		//Capital A with Breve
        isotable.setProperty("Ä…", "a");		//Small a with Ogonek
        isotable.setProperty("Äƒ", "a");		//Small a with Breve
        isotable.setProperty("Ä‡", "c");		//Small c with Acute
        isotable.setProperty("Ä‰", "c");		//Small c with Circumflex
        isotable.setProperty("Ä�", "c");		//Small c with Caron
        isotable.setProperty("ÄŒ","C");		//Capital C with Caron
        isotable.setProperty("Ä†","C");		//Capital C with Acute
        isotable.setProperty("Äˆ","C");		// Capital C with Circumflex
        isotable.setProperty("Ä�", "d");		//Small d with Caron
        isotable.setProperty("ÄŽ", "D");		//Capital D with Caron
        isotable.setProperty("Ä‘","d");		// Small d
        isotable.setProperty("Ã°", "d");		//Small d
        isotable.setProperty("Äš", "E");		//Capital E with Caron
        isotable.setProperty("Ä˜","E");		//Capital E with Ogonek
        isotable.setProperty("Ä›", "e");		//Small e with Caron
        isotable.setProperty("Ä™","e");		//Small e with Ogonek
        isotable.setProperty("Äº", "l");		//Small l with Acute
        isotable.setProperty("Å‚", "l");		//Small l with 
        isotable.setProperty("Å�","L");		//Capital L with
        isotable.setProperty("Ä½","L");		//Capital L with
        isotable.setProperty("Ä¹","L");		//Capital L with
        isotable.setProperty("Åˆ", "n");		//Small n with Caron
        isotable.setProperty("Å„", "n");		//Small n with
        isotable.setProperty("Å‡","N");		//Capital N with Caron
        isotable.setProperty("Åƒ", "N");		//Capital N with
        isotable.setProperty("Å‘" ,"o");		//Small o with double Acute
        isotable.setProperty("Å�", "O");		//Capital O with double Acute
        isotable.setProperty("Å™", "r");		//Small r with Caron
        isotable.setProperty("Å•","r");		//Small r with Acute
        isotable.setProperty("Å˜","R");		//Cappital R with Caron
        isotable.setProperty("Å”","R");		//Capital R with Acute
        isotable.setProperty("ÅŸ","s");		//Small s with Comma Below
        isotable.setProperty("Å›","s");		//Small s with Acute
        isotable.setProperty("Å¡","s");		//Small s with Caron
        isotable.setProperty("Åž","S");		//Capital S with Comma Below
        isotable.setProperty("Åš", "S");		//Capital S with Acute
        isotable.setProperty("Å ", "S");		//Capital S with Caron
        isotable.setProperty("Å¥","t");		//Small t with Acute
        isotable.setProperty("Å£","t");		//Small t with Comma Below
        isotable.setProperty("Å¤","T");		//Capital T with Caron
        isotable.setProperty("Èš", "T");		//Capital T with Comma Below
        isotable.setProperty("Å¯", "u");		//Small u with Ring ABove
        isotable.setProperty("Å±","u");		//Small u with Double Acute
        isotable.setProperty("Å®", "U");		//Capital U with Ring Above
        isotable.setProperty("Å°","U");		//Capital U with Doube Acute
        isotable.setProperty("Å¼","z");		//Small z with Dot Above
        isotable.setProperty("Å¾", "z");		//Small z with Acron
        isotable.setProperty("Åº", "z");		//Small z with Acute
        isotable.setProperty("Å»", "Z");		//Capital Z with Dot Above
        isotable.setProperty("Å½", "Z");		//Capital Z with Acron
        isotable.setProperty("Å¹", "Z");		//Capital Z with Acute
        
        
        
        
        
        // Patent UTF String
        utftable.setProperty("{acute over (a)}", "a");
        utftable.setProperty("{acute over (b)}", "b");
        utftable.setProperty("{acute over (c)}", "c");
        utftable.setProperty("{acute over (d)}", "d");
        utftable.setProperty("{acute over (e)}", "e");
        utftable.setProperty("{acute over (f)}", "f");
        utftable.setProperty("{acute over (g)}", "g");
        utftable.setProperty("{acute over (h)}", "h");
        utftable.setProperty("{acute over (i)}", "i");
        utftable.setProperty("{acute over (j)}", "j");
        utftable.setProperty("{acute over (k)}", "k");
        utftable.setProperty("{acute over (l)}", "l");
        utftable.setProperty("{acute over (m)}", "m");
        utftable.setProperty("{acute over (n)}", "n");
        utftable.setProperty("{acute over (o)}", "o");
        utftable.setProperty("{acute over (p)}", "p");
        utftable.setProperty("{acute over (q)}", "q");
        utftable.setProperty("{acute over (r)}", "r");
        utftable.setProperty("{acute over (s)}", "s");
        utftable.setProperty("{acute over (t)}", "t");
        utftable.setProperty("{acute over (u)}", "u");
        utftable.setProperty("{acute over (v)}", "v");
        utftable.setProperty("{acute over (w)}", "w");
        utftable.setProperty("{acute over (x)}", "x");
        utftable.setProperty("{acute over (y)}", "y");
        utftable.setProperty("{acute over (z)}", "z");

        utftable.setProperty("{umlaut over (a)}", "a");
        utftable.setProperty("{umlaut over (b)}", "b");
        utftable.setProperty("{umlaut over (c)}", "c");
        utftable.setProperty("{umlaut over (d)}", "d");
        utftable.setProperty("{umlaut over (e)}", "e");
        utftable.setProperty("{umlaut over (f)}", "f");
        utftable.setProperty("{umlaut over (g)}", "g");
        utftable.setProperty("{umlaut over (h)}", "h");
        utftable.setProperty("{umlaut over (i)}", "i");
        utftable.setProperty("{umlaut over (j)}", "j");
        utftable.setProperty("{umlaut over (k)}", "k");
        utftable.setProperty("{umlaut over (l)}", "l");
        utftable.setProperty("{umlaut over (m)}", "m");
        utftable.setProperty("{umlaut over (n)}", "n");
        utftable.setProperty("{umlaut over (o)}", "o");
        utftable.setProperty("{umlaut over (p)}", "p");
        utftable.setProperty("{umlaut over (q)}", "q");
        utftable.setProperty("{umlaut over (r)}", "r");
        utftable.setProperty("{umlaut over (s)}", "s");
        utftable.setProperty("{umlaut over (t)}", "t");
        utftable.setProperty("{umlaut over (u)}", "u");
        utftable.setProperty("{umlaut over (v)}", "v");
        utftable.setProperty("{umlaut over (w)}", "w");
        utftable.setProperty("{umlaut over (x)}", "x");
        utftable.setProperty("{umlaut over (y)}", "y");
        utftable.setProperty("{umlaut over (z)}", "z");
    }
}
