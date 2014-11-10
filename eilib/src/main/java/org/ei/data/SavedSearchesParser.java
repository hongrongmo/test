package org.ei.data;

//import org.ei.xml.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

public class SavedSearchesParser extends DefaultHandler {
    Map dataMap = null;
    String name = null;

    SavedSearchesParser(Map dataMap) {
        this.dataMap = dataMap;
    }

    public void parseQueryString(String queryString) throws Exception {
        SavedSearchesParser handler = new SavedSearchesParser(dataMap);
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser parser = saxParserFactory.newSAXParser();
        parser.parse(new InputSource((InputStream) new ByteArrayInputStream(queryString.getBytes())), handler);
    }

    public void startElement(String uri, String name, String qName, Attributes atts) {
        this.name = name;
    }

    public void characters(char[] text, int start, int length) {
        if (length > 0) {
            String content = new String(text, start, length);
            content = content.replaceAll("\n", "");
            content = content.replaceAll("\r", "");
            content = content.replaceAll("|", "");
            dataMap.put(name, content);
        }
    }

}
