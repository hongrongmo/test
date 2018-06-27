package org.ei.data.insback.runtime;

import org.ei.domain.*;
import java.util.*;
import java.io.*;


public class ASources implements ElementData {
    
    
    private List<Source> elementData = new ArrayList<Source>();
    private Key          key;
    private boolean      labels      = false;
    
    public ASources(Key key, String raw) {
        this.key = key;
        parse(raw);
    }
    
    public void setKey(Key akey) {
        this.key = akey;
    }
    
    public Key getKey() {
        return this.key;
    }
    
    public void setElementData(String[] edata) {
        for (int i = 0; i < edata.length; i++) {
            Source so = elementData.get(i);
            so.str = edata[i];
        }
    }
    
    public void exportLabels(boolean labels) {
        this.labels = labels;
    }
    
    public String[] getElementData() {
        String[] edata = new String[elementData.size()];
        for (int i = 0; i < elementData.size(); i++) {
            Source so = elementData.get(i);
            edata[i] = so.str;
        }
        return edata;
    }
    
    public void toXML(Writer out) throws IOException {
        out.write("<");
        out.write(this.key.getKey());
        if (this.labels && (this.key.getLabel() != null)) {
            out.write(" label=\"");
            out.write(this.key.getLabel());
            out.write("\"");
        }
        out.write(">");
        for (int i = 0; i < elementData.size(); i++) {
            out.write("<SOURCE NO=\"");
            out.write(Integer.toString(i + 1));
            out.write("\"");
            Source so = elementData.get(i);
            if (so.DOI != null) {
                out.write(" DOI=\"");
                out.write(so.DOI);
                out.write("\"");
            }
            out.write("><![CDATA[");
            out.write(so.str);
            out.write("]]></SOURCE>");
            
        }
        out.write("</");
        out.write(this.key.getKey());
        out.write(">");
    }
    
    
    class Source {
        public String DOI;
        public String str;
    }
    
    
    private void parse(String strSource) {
        String[] strToken = strSource.split("~ ");
        for (int i = 0; i < strToken.length; i++) {
            Source so = new Source();
            StringBuffer strResult = new StringBuffer();
            
            String[] source = strToken[i].split("\\|");
            
            if (source.length == 12) {
                so.DOI = source[11];
            }
            
            
            if (source.length >= 1) {
                if (!source[0].equals("")) {
                    strResult.append(source[0]);
                } else if (source.length >= 3) {
                    if (!source[2].equals("")) strResult.append(source[2]);
                }
            }
            
            if (source.length >= 4) {
                if (!source[3].equals("")) strResult.append(", v ").append(source[3]);
            }
            if (source.length >= 5) {
                if (!source[4].equals("")) strResult.append(", n ").append(source[4]);
            }
            if (source.length >= 8) {
                if (!source[7].equals("")) strResult.append(", ").append(source[7]);
            }
            if (source.length >= 7) {
                if (!source[6].equals("")) strResult.append(", p ").append(source[6]);
            }
            so.str = strResult.toString();
            elementData.add(so);
        }
    }
}