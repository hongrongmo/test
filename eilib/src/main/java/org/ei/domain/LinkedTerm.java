package org.ei.domain;
import java.io.IOException;
import java.io.Writer;
import java.util.StringTokenizer;

public class LinkedTerm {

    String terms;
    Key key;

    /**
     * @return
     */
    public String getTerm() {
        return terms;
    }
    public void setKey(Key key) {
        this.key = key;
    }
    /**
     * @param string
     */
    public void setTerm(String string) {
        terms = string;
    }
    public void toXML(Writer out) throws IOException {

        StringTokenizer ltSubTerms = new StringTokenizer(terms, ";", false);

        StringBuffer strXML = new StringBuffer();

        while (ltSubTerms.hasMoreTokens()) {
            String termToken = ltSubTerms.nextToken();
            strXML.append("<").append(Keys.LINKED_SUB_TERM.getKey());
            strXML.append(">");
            strXML.append("<![CDATA[").append(termToken.trim()).append("]]>");
            strXML.append("</").append(Keys.LINKED_SUB_TERM.getKey()).append(">");

        }

        out.write(strXML.toString());
    }
}
