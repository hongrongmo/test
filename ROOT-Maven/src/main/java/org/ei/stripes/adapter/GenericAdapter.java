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
import org.ei.config.EVProperties;
import org.ei.config.RuntimeProperties;
import org.ei.exception.EVBaseException;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SessionException;
import org.ei.exception.SystemErrorCodes;
import org.ei.session.UserSession;
import org.ei.stripes.EVActionBeanContext;
import org.ei.xml.TransformerBroker;

public class GenericAdapter extends BizXmlAdapter {
    private final static Logger log4j = Logger.getLogger(GenericAdapter.class);

    /**
     * Process XML from the model layer
     *
     * @throws InfrastructureException
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
        if(actionbean != null && actionbean.getContext() != null ){
        	EVActionBeanContext actionBeanContext = (EVActionBeanContext)actionbean.getContext();
        	String csrfSyncToken = "";
        	boolean isCSRFPrevEnabled = Boolean.parseBoolean((EVProperties.getRuntimeProperty(RuntimeProperties.PREVENT_CSRF_ATTACK)));
        	
        	if(isCSRFPrevEnabled && actionBeanContext != null && actionBeanContext.getUserSession() != null){
        		UserSession usersession = actionBeanContext.getUserSession();
        		boolean isSessionUpdateNeeded = false;
    			if(usersession.getFifoQueue().isEmpty())isSessionUpdateNeeded = true;
    			csrfSyncToken = usersession.getFifoQueue().getLastElement();
    			if(isSessionUpdateNeeded) {
        			try {
						actionBeanContext.updateUserSession(usersession);
					} catch (SessionException e) {
						log4j.warn("Could not create or get the latest csrf token due to session exception!, exception = "+e.getMessage());
					}
        		}
        	}
        	transformer.setParameter("csrfSyncToken", csrfSyncToken);
        }
        
        try {
            transformer.transform(new StreamSource(instream), new StreamResult(transformout));
        } catch (TransformerException e) {
            try { instream.close(); } catch (IOException e1) {}
            try { transformout.close(); } catch (IOException e1) {}
            throw new InfrastructureException(SystemErrorCodes.PARSE_ERROR, e);
        }

    }
}
