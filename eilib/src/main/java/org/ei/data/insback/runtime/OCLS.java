package org.ei.data.insback.runtime;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.ei.domain.ElementData;
import org.ei.domain.Key;


public class OCLS implements ElementData {
    
    
    private List<List<String>> elementData = new ArrayList<List<String>>();
    private Key                key;
    private boolean            labels      = false;
    
    public OCLS(Key key, String raw) {
        this.key = key;
        parse(raw);
    }
    
    public void setKey(Key akey) {
        this.key = akey;
    }
    
    public Key getKey() {
        return this.key;
    }
    
    public void setElementData(String[] edata)
    
    {
        for (int i = 0; i < edata.length; i++) {
            List<String> l = new ArrayList<String>();
            String[] parts = edata[i].split(" QDelimQ ");
            for (int z = 0; z < parts.length; z++) {
                l.add(parts[z]);
            }
            
            elementData.set(i, l);
        }
    }
    
    public void exportLabels(boolean labels) {
        this.labels = labels;
    }
    
    public String[] getElementData() {
        String[] edata = new String[elementData.size()];
        for (int i = 0; i < elementData.size(); i++) {
            List<?> l = (List<?>) elementData.get(i);
            StringBuffer buf = new StringBuffer();
            for (int z = 0; z < l.size(); z++) {
                String s = (String) l.get(z);
                if (z > 0) {
                    buf.append(" QDelimQ ");
                }
                buf.append(s);
            }
            edata[i] = buf.toString();
        }
        return edata;
    }
    
    public void toXML(Writer out) throws IOException {
        if (elementData.size() > 0) {
            out.write("<");
            out.write(this.key.getKey());
            if (this.labels && (this.key.getLabel() != null)) {
                out.write(" label=\"");
                out.write(this.key.getLabel());
                out.write("\"");
            }
            out.write(">");
            for (int i = 0; i < elementData.size(); i++) {
                out.write("<ORGC>");
                List<?> l = (List<?>) elementData.get(i);
                for (int z = 0; z < l.size(); z++) {
                    String s = (String) l.get(z);
                    out.write("<OC><![CDATA[");
                    out.write(s);
                    out.write("]]></OC>");
                }
                out.write("</ORGC>");
            }
            out.write("</");
            out.write(this.key.getKey());
            out.write(">");
        }
    }
    
    
    private void parse(String raw) {
        String[] cls = raw.split("~ ");
        for (int i = 0; i < cls.length; i++) {
            if (!cls[i].replaceFirst("prime\\|", "\\|").startsWith("|||")) {
                List<String> l = new ArrayList<String>();
                String[] term = cls[i].split("\\|");
                for (int j = 1; j < term.length; j++) {
                    if (!term[j].equals("")) {
                        l.add(term[j]);
                    }
                }
                elementData.add(l);
            }
            
        }
    }
}