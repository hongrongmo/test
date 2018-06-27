package org.ei.dataloading.inspec.loadtime;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.ei.common.AuthorStream;
import org.ei.util.StringUtil;
import org.ei.xml.Entity;

public class ExtractAuthorsIbf {

    public void extract(int load_number_begin, int load_number_end, Connection con) throws Exception {
        PrintWriter writerAuthor = null;
        Hashtable<String, String> ausHash = new Hashtable<String, String>();

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        long begin = System.currentTimeMillis();
        try {

            writerAuthor = new PrintWriter(new FileWriter("ibf_aus.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select aus from ibf_master where (aus is not null) and load_number = " + load_number_begin);
                System.out.println("\n\nQuery: " + " select aus from ins_master where (aus is not null) and load_number = " + load_number_begin);
            } else {
                pstmt1 = con.prepareStatement(" select aus from ibf_master where (aus is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
                System.out.println("\n\nQuery: " + " select aus from ibf_master where (aus is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();
            // FileWriter out1 = new FileWriter("/temp/testXML.txt");
            while (rs1.next()) {
                String aus = rs1.getString("aus");
                AuthorStream aStream = new AuthorStream(new ByteArrayInputStream(aus.getBytes()));
                String author = "";
                while ((author = aStream.readAuthor()) != null) {
                    // out1.write(author.trim()+"\n");
                    author = Entity.prepareString(author).trim().toUpperCase();
                    if (!ausHash.containsKey(author)) {
                        ausHash.put(author, author);
                    }
                }
            }
            // out1.flush();
            Iterator<String> itrTest = ausHash.keySet().iterator();
            String display_name = null;
            for (int i = 0; itrTest.hasNext(); i++) {
                display_name = (String) itrTest.next();
                String au_index_name = getIndexedAuthor(display_name);
                writerAuthor.println(display_name + "\t" + au_index_name + "\t" + "ibf\t");
            }
            ausHash.clear();

        } catch (Exception e) {
            e.printStackTrace();
            ;
        } finally {
            if (rs1 != null) {
                try {
                    rs1.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (pstmt1 != null) {
                try {
                    pstmt1.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (writerAuthor != null) {
                try {
                    writerAuthor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private String getIndexedAuthor(String author) {
        String Iauthor = new String();
        StringUtil stringUtil = new StringUtil();

        // ` ~ ! @ # $ % ^ & * ( ) - _ = + [ { ] } \ | ; : ' " , < . > / ?
        // ~ ! @ # % ^ + = ` : | , . < > [ ] - '
        Iauthor = author.replace('~', ' ');
        Iauthor = Iauthor.replace('!', ' ');
        Iauthor = Iauthor.replace('@', ' ');
        Iauthor = Iauthor.replace('#', ' ');
        Iauthor = Iauthor.replace('$', ' ');
        Iauthor = Iauthor.replace('%', ' ');
        Iauthor = Iauthor.replace('^', ' ');
        Iauthor = Iauthor.replace('&', ' ');
        Iauthor = Iauthor.replace('*', ' ');
        Iauthor = Iauthor.replace('+', ' ');
        Iauthor = Iauthor.replaceAll("`", " ");
        Iauthor = Iauthor.replaceAll("'", " ");
        Iauthor = Iauthor.replace(':', ' ');
        Iauthor = Iauthor.replace('|', ' ');
        Iauthor = Iauthor.replace('<', ' ');
        Iauthor = Iauthor.replace('>', ' ');
        Iauthor = Iauthor.replace('[', ' ');
        Iauthor = Iauthor.replace(']', ' ');
        Iauthor = Iauthor.replace('\'', ' ');

        Iauthor = stringUtil.replace(Iauthor, ",", " ", 1, 4);

        Iauthor = Iauthor.replaceAll(".", " ");
        Iauthor = Iauthor.replaceAll("-", " ");

        Iauthor = Iauthor.trim();

        while (Iauthor.indexOf("  ") > -1) {
            Iauthor = stringUtil.replace(Iauthor, "  ", " ", 1, 4);
        }

        Iauthor = Iauthor.replace(' ', '9');

        return Iauthor;

    }

    private String replacXMLElement(String element) {
        String outString = null;
        outString = element.replaceAll("&AELIG;", "&AElig;");
        outString = outString.replaceAll("&AACUTE;", "&Aacute;");
        outString = outString.replaceAll("&ACIRC;", "&Acirc;");
        outString = outString.replaceAll("&ACUTE;", "&acute;");
        outString = outString.replaceAll("&AGRAVE;", "&Agrave;");
        outString = outString.replaceAll("&AMP;", "&amp;");
        outString = outString.replaceAll("&ARING;", "&Aring;");
        outString = outString.replaceAll("&ATILDE;", "&Atilde;");
        outString = outString.replaceAll("&AUML;", "&Auml;");
        outString = outString.replaceAll("&BREVE;", "&breve;");
        outString = outString.replaceAll("&BULL;", "&bull;");
        outString = outString.replaceAll("&BULL;", "&bull;");
        outString = outString.replaceAll("&CARON;", "&caron;");
        outString = outString.replaceAll("&CCEDIL;", "&Ccedil;");
        outString = outString.replaceAll("&CEDIL;", "&cedil;");
        outString = outString.replaceAll("&CENT;", "&cent;");
        outString = outString.replaceAll("&CIRC;", "&circ;");
        outString = outString.replaceAll("&COPY;", "&copy;");
        outString = outString.replaceAll("&DAGGER;", "&dagger;");
        outString = outString.replaceAll("&DEG;", "&deg;");
        outString = outString.replaceAll("&DELTA;", "&Delta;");
        outString = outString.replaceAll("&DIVIDE;", "&divide;");
        outString = outString.replaceAll("&DOT;", "&dot;");
        outString = outString.replaceAll("&EACUTE;", "&Eacute;");
        outString = outString.replaceAll("&ECIRC;", "&Ecirc;");
        outString = outString.replaceAll("&EGRAVE;", "&Egrave;");
        outString = outString.replaceAll("&EUML;", "&Euml;");
        outString = outString.replaceAll("&FNOF;", "&fnof;");
        outString = outString.replaceAll("&GE;", "&ge;");
        outString = outString.replaceAll("&GRAVE;", "&grave;");
        outString = outString.replaceAll("&GT;", "&gt;");
        outString = outString.replaceAll("&HELLIP;", "&hellip;");
        outString = outString.replaceAll("&IACUTE;", "&Iacute;");
        outString = outString.replaceAll("&ICIRC;", "&Icirc;");
        outString = outString.replaceAll("&IEXCL;", "&iexcl;");
        outString = outString.replaceAll("&IGRAVE;", "&Igrave;");
        outString = outString.replaceAll("&INFIN;", "&infin;");
        outString = outString.replaceAll("&INT;", "&int;");
        outString = outString.replaceAll("&IQUEST;", "&iquest;");
        outString = outString.replaceAll("&IUML;", "&iuml;");
        outString = outString.replaceAll("&LAQUO;", "&laquo;");
        outString = outString.replaceAll("&LDQUO;", "&ldquo;");
        outString = outString.replaceAll("&LE;", "&le;");
        outString = outString.replaceAll("&LSAQUO;", "&lsaquo;");
        outString = outString.replaceAll("&LSQUO;", "&lsquo;");
        outString = outString.replaceAll("&LSTROK;", "&lstrok;");
        outString = outString.replaceAll("&LT;", "&lt;");
        outString = outString.replaceAll("&MACR;", "&macr;");
        outString = outString.replaceAll("&MDASH;", "&mdash;");
        outString = outString.replaceAll("&MICRO;", "&micro;");
        outString = outString.replaceAll("&MIDDOT;", "&middot;");
        outString = outString.replaceAll("&MU;", "&mu;");
        outString = outString.replaceAll("&NBSP;", "&nbsp;");
        outString = outString.replaceAll("&NDASH;", "&ndash;");
        outString = outString.replaceAll("&NE;", "&ne;");
        outString = outString.replaceAll("&NOT;", "&not;");
        outString = outString.replaceAll("&NTILDE;", "&Ntilde;");
        outString = outString.replaceAll("&NTILDE;", "&ntilde;");
        outString = outString.replaceAll("&NU;", "&nu;");
        outString = outString.replaceAll("&OACUTE;", "&Oacute;");
        outString = outString.replaceAll("&OCIRC;", "&Ocirc;");
        outString = outString.replaceAll("&OELIG;", "&OElig;");
        outString = outString.replaceAll("&OELIG;", "&oelig;");
        outString = outString.replaceAll("&OGRAVE;", "&Ograve;");
        outString = outString.replaceAll("&OMEGA;", "&Omega;");
        outString = outString.replaceAll("&ORDF;", "&ordf;");
        outString = outString.replaceAll("&ORDM;", "&ordm;");
        outString = outString.replaceAll("&OSLASH;", "&Oslash;");
        outString = outString.replaceAll("&OTILDE;", "&Otilde;");
        outString = outString.replaceAll("&OUML;", "&Ouml;");
        outString = outString.replaceAll("&PARA;", "&para;");
        outString = outString.replaceAll("&PART;", "&part;");
        outString = outString.replaceAll("&PERMIL;", "&permil;");
        outString = outString.replaceAll("&PI;", "&pi;");
        outString = outString.replaceAll("&PLANCK;", "&planck;");
        outString = outString.replaceAll("&PLUSMN;", "&plusmn;");
        outString = outString.replaceAll("&POUND;", "&pound;");
        outString = outString.replaceAll("&QUOT;", "&quot;");
        outString = outString.replaceAll("&RADIC;", "&radic;");
        outString = outString.replaceAll("&RAQUO;", "&raquo;");
        outString = outString.replaceAll("&RDQUO;", "&rdquo;");
        outString = outString.replaceAll("&REG;", "&reg;");
        outString = outString.replaceAll("&RSAQUO;", "&rsaquo;");
        outString = outString.replaceAll("&RSQUO;", "&rsquo;");
        outString = outString.replaceAll("&SECT;", "&sect;");
        outString = outString.replaceAll("&SUM;", "&sum;");
        outString = outString.replaceAll("&SZLIG;", "&szlig;");
        outString = outString.replaceAll("&TILDE;", "&tilde;");
        outString = outString.replaceAll("&TRADE;", "&trade;");
        outString = outString.replaceAll("&UACUTE;", "&Uacute;");
        outString = outString.replaceAll("&UCIRC;", "&Ucirc;");
        outString = outString.replaceAll("&UCIRC;", "&ucirc;");
        outString = outString.replaceAll("&UGRAVE;", "&Ugrave;");
        outString = outString.replaceAll("&UML;", "&uml;");
        outString = outString.replaceAll("&UUML;", "&Uuml;");
        outString = outString.replaceAll("&YACUTE;", "&yacute;");
        outString = outString.replaceAll("&YEN;", "&yen;");
        outString = outString.replaceAll("&YUML;", "&yuml;");

        return outString;
    }

}
