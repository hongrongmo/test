package org.ei.stripes.action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.config.EVProperties;
import org.ei.config.JSPPathProperties;
import org.ei.exception.ErrorXml;
import org.ei.exception.InfrastructureException;
import org.ei.stripes.adapter.IBizBean;
import org.ei.xml.TransformerBroker;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

@UrlBinding("/ScopusServlet/ScopusEV")
public class ScopusOutwardLinkAction extends EVActionBean implements IBizBean {
    private final static Logger log4j = Logger.getLogger(ScopusOutwardLinkAction.class);

    private String accessionNumber;

    @Override
    public void processModelXml(InputStream instream)  throws InfrastructureException {
        // Get the XML from the openXML JSP
        modelXml = new Scanner(instream).useDelimiter("\\A").next();
    }

    @Override
    public String getXSLPath() {
        return "/transform/search/openXML.xsl";
    }

    @Override
    public String getXMLPath() {

        String qs = "?DATABASE=1&XQUERYX=%3Cquery%3E%3Cword+path%3D%22an%22%3E" + accessionNumber
            + "%3C%2Fword%3E%3C%2Fquery%3E&AUTOSTEM=on&STARTYEAR=1900&ENDYEAR=&SORT=re&xmlsearch=Submit+Query";
        return EVProperties.getJSPPath(JSPPathProperties.OPEN_XML_PATH) + qs;
    }

    @DefaultHandler
    @DontValidate
    public Resolution handle() {
        // Make sure XML was recieved from engvillage layer
        if (GenericValidator.isBlankOrNull(modelXml)) {
            log4j.error("XML was not generated!");
            return SystemMessage.SYSTEM_ERROR_RESOLUTION;
        }

        // Transform to real Open XML
        //
        // Setup for the transformation
        //
        Transformer transformer = null;
        InputStream instream = null;
        String stylesheet = null;
        try {

            //
            // TRANSFORM! Use stylesheet to transform XML to bean (ThesaurusSearchAction)
            //
            stylesheet = this.getClass().getResource(getXSLPath()).toExternalForm();
            TransformerBroker broker = TransformerBroker.getInstance();
            // For testing, grab the XML (viewable) when debug or info mode
            // and show output from transform
            // Also set stylesheet caching to OFF
            OutputStream transformout = new ByteArrayOutputStream();
            instream = new ByteArrayInputStream(modelXml.getBytes());
            transformer = broker.getTransformer(stylesheet);
            transformer.transform(
                new StreamSource(instream),
                new StreamResult(transformout));

            // Parse resulting OpenXML and construct abstract URL
            EVSaxParser parser = new EVSaxParser();
            String transformxml = transformout.toString();
            parser.parseDocument(new ByteArrayInputStream(transformxml.getBytes()));
            String abstractLink = parser.getAbstractLink();
            String resultCount = parser.getResultCount();

            log4j.info("Result count for " + accessionNumber + " is " + resultCount);

            if ("1".equals(resultCount)) {
                return new RedirectResolution(abstractLink);
            } else if (!GenericValidator.isBlankOrNull(parser.getException())) {
                log4j.warn("Exception occurred from openXML.jsp: '" + parser.getException() + "'");
                return new ForwardResolution(SystemMessage.SYSTEM_ERROR_URL + "?message=" + URLEncoder.encode(parser.getException(), "UTF-8"));
            } else {
                return SystemMessage.SYSTEM_ERROR_RESOLUTION;
            }

        } catch (Exception e) {
            log4j.error("Unable to resolve outward link: " + e.getMessage());
            return SystemMessage.SYSTEM_ERROR_RESOLUTION;
        }
    }

    /**
     * Inner class to handle parsing OpenXML response
     *
     * @author harovetm
     */
    public static class EVSaxParser extends org.xml.sax.helpers.DefaultHandler {
        private String tempVal;
        private String abstractLink;
        private String resultCount;
        private String exception;

        private String qName;

        public EVSaxParser() {
            super();
        }

        public void parseDocument(InputStream stream) {

            SAXParserFactory spf = SAXParserFactory.newInstance();

            try {
                SAXParser sp = spf.newSAXParser();
                sp.parse(stream, this);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            tempVal = new String(ch, start, length);
            if (qName.equalsIgnoreCase("ABSTRACT-LINK")) {
                abstractLink = tempVal;
            }
            if (qName.equalsIgnoreCase("RESULTCOUNT")) {
                resultCount = tempVal;
            }
            if (qName.equalsIgnoreCase("EXCEPTION")) {
                exception = tempVal;
            }
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

            this.qName = qName;

        }

        public String getAbstractLink() {
            return abstractLink;
        }

        public String getResultCount() {
            return resultCount;
        }

        public String getException() {
            return exception;
        }

    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    /**
     * Default handling for exceptions from data service layer
     * @param errorXml
     * @return
     */
	public Resolution handleException(ErrorXml errorXml) {
		context.getRequest().setAttribute("errorXml", errorXml);
		return new ForwardResolution("/system/error.url");
	}

}
