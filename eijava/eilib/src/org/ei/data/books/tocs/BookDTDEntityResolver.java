package org.ei.data.books.tocs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class BookDTDEntityResolver implements EntityResolver {
    protected static Log log = LogFactory.getLog(BookDTDEntityResolver.class);

    private String dtdCatalogPath = System.getProperty("user.home") + "\\Desktop\\Referex EW\\DTDs\\";
    
    public BookDTDEntityResolver() {
        // TODO Auto-generated constructor stub
    }
    public InputSource resolveEntity(String publicId, String systemId) {
        InputStreamReader is = null;
        try {
            String dtdfile = new File(systemId).getName();
            // log.info("<!--" + dtdfile + " == " + systemId + "-->");
            is = new InputStreamReader(new FileInputStream(dtdCatalogPath + dtdfile));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new InputSource(is);
    }

}