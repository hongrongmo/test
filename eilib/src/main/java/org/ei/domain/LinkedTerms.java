package org.ei.domain;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class LinkedTerms implements ElementData {

    protected List<?> linkedTerms = new ArrayList<Object>();
    protected Key key;
    protected boolean labels = false;

    public void setKey(Key akey) {
        this.key = akey;
    }

    public Key getKey() {
        return this.key;
    }

    public void exportLabels(boolean labels) {
        this.labels = labels;
    }

    public String[] getElementData() {
        String[] edata = new String[linkedTerms.size()];

        for (int i = 0; i < linkedTerms.size(); i++) {
            LinkedTerm c = (LinkedTerm) linkedTerms.get(i);
            edata[i] = c.getTerm();
        }

        return edata;
    }

    public void setElementData(String[] elementData) {
        for (int i = 0; i < elementData.length; i++) {
            LinkedTerm lt = (LinkedTerm) linkedTerms.get(i);
            lt.setTerm(elementData[i]);
        }
    }

    public LinkedTerms(Key key, List<?> list) {
        this.key = key;
        this.linkedTerms = list;
    }

    public List<?> getChildren() {
        return linkedTerms;
    }

    public void toXML(Writer out) throws IOException {

        out.write("<");
        out.write(key.getKey());
        if (labels && (this.key.getLabel() != null)) {
            out.write(" label=\"");
            out.write(this.key.getLabel());
            out.write("\"");
        }
        out.write(">");

        for (int j = 0; j < linkedTerms.size(); j++) {
            out.write("<");
            out.write(Keys.LINKED_TERM_GROUP.getKey());
            if (labels && (this.key.getLabel() != null)) {
                out.write(" label=\"");
                out.write(this.key.getLabel());
                out.write("\"");
            }
            out.write(">");
            LinkedTerm linkedTerm = (LinkedTerm) linkedTerms.get(j);
            linkedTerm.toXML(out);

            out.write("</");
            out.write(Keys.LINKED_TERM_GROUP.getKey());
            out.write(">");

        }
        out.write("</");
        out.write(key.getKey());
        out.write(">");
    }
}
