package org.ei.stripes.adapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sourceforge.stripes.action.ActionBean;

import org.apache.log4j.Logger;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;
import org.ei.xml.TransformerBroker;

public class GenericAdapter extends BizXmlAdapter {
    private final static Logger log4j = Logger.getLogger(GenericAdapter.class);

    /**
     * Process XML from the model layer
     *
     * @throws InfrastructureException
     * @throws
     * @throws EVBaseException
     */

    public void processXml(ActionBean actionbean, InputStream instream, String stylesheet) throws InfrastructureException {
        //
        // Setup for the transformation
        //
        Transformer transformer = null;
        // try {

        //
        // TRANSFORM! Use stylesheet to transform XML to bean (ThesaurusSearchAction)
        //
        TransformerBroker broker = TransformerBroker.getInstance();
        // For testing, grab the XML (viewable) when debug or info mode
        // and show output from transform
        // Also set stylesheet caching to OFF
        OutputStream transformout = new NullOutputStream();
        if (log4j.isDebugEnabled() || log4j.isInfoEnabled()) {
            log4j.info("Transforming in debug/info");
            Scanner scanner = new Scanner(instream);
            String modelxml = scanner.useDelimiter("\\A").next();
            System.out.println("old xml----------->"+ modelxml );
            scanner.close();
            // log4j.info("modelxml : "+modelxml);
            instream = new ByteArrayInputStream(modelxml.getBytes());
            broker.setCache(false);
        }
        try {
            transformer = broker.getTransformer(stylesheet);
        } catch (TransformerConfigurationException e) {
            try { instream.close(); } catch (IOException e1) {}
            try { transformout.close(); } catch (IOException e1) {}
            throw new InfrastructureException(SystemErrorCodes.PARSE_ERROR, e);
        }
        transformer.setParameter("actionbean", actionbean);
        
        try {
            transformer.transform(new StreamSource(sanitizeInputStream(instream)), new StreamResult(transformout));
        } catch (TransformerException e) {
            try { instream.close(); } catch (IOException e1) {}
            try { transformout.close(); } catch (IOException e1) {}
            throw new InfrastructureException(SystemErrorCodes.PARSE_ERROR, e);
        }

    }
}
