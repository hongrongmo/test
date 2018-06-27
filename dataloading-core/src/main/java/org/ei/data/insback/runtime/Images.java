package org.ei.data.insback.runtime;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.ei.domain.ElementData;
import org.ei.domain.Key;

public class Images implements ElementData {
    
    private Key     key;
    private List<IMG>    elementData = new ArrayList<IMG>();
    private boolean labels;
    
    public Images(Key key, String raw) {
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
        // Do nothing
    }
    
    public void exportLabels(boolean labels) {
        this.labels = labels;
    }
    
    public String[] getElementData() {
        return null;
    }
    
    public void parse(String strImage) {
        
        if (strImage != null) {
            String[] temp;
            temp = strImage.split("~ ");
            for (int i = 0; i < temp.length; i++) {
                IMG image = new IMG();
                
                String tmpStr = temp[i].trim();
                if (tmpStr.indexOf('[') > 0) {
                    image.file = tmpStr.substring(0, tmpStr.indexOf('['));
                    image.caption = tmpStr.substring(tmpStr.indexOf('[') + 1, tmpStr.indexOf(']'));
                } else {
                    image.file = tmpStr;
                }
                
                System.out.println("Image File:" + image.file);
                elementData.add(image);
            }
        }
    }
    
    class IMG {
        public String file;
        public String caption;
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
        
        out.write("<IMGGRP><IROW>");
        for (int i = 0; i < elementData.size(); i++) {
            IMG image = (IMG) elementData.get(i);
            out.write("<IMAGE img=\"");
            out.write(image.file);
            out.write("\">");
            if (image.caption != null) {
                out.write("<![CDATA[");
                out.write(image.caption);
                out.write("]]>");
            }
            
            out.write("</IMAGE>");
            if (i == 5 || i == 11 || i == 17 || i == 23) {
                out.write("</IROW><IROW>");
            }
        }
        out.write("</IROW></IMGGRP>");
        out.write("</");
        out.write(this.key.getKey());
        out.write(">");
    }
}