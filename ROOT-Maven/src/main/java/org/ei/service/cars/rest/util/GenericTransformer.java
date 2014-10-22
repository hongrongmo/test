package org.ei.service.cars.rest.util;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.elsevier.edit.common.client.util.DtdCatalog;

/**
 * Generic implementation of ITransformer interface.
 */
public final class GenericTransformer implements ITransformer {
    private final static Logger log4j = Logger.getLogger(GenericTransformer.class);

    /** XMLReader pool holds XMLReader objects for reuse. */
    private static XMLReaderObjectPool c_readerPool = XMLReaderObjectPool.getInstance();

    /**
     * The default entity resolver to use. This implementation will cache the results of DTD requests.
     */
    private static final DtdCatalog DEFAULT_ENTITY_RESOLVER = new DtdCatalog();

    /** Private Constructor */
    private GenericTransformer() {
    }

    /**
     * Factory method for the construction of the transformer
     * 
     * @return ITransformer
     */
    public static ITransformer newInstance() {
        return new GenericTransformer();
    }

    /**
     * 
     * This method performs an XSLT transformation and streams the output to the provided writer.
     * 
     * @throws XSLTTransformationException
     * @throws TransformerConfigurationException
     * @see com.elsevier.els.app.biz.transformer.ITransformer#transform(java.io.Reader, java.lang.String, java.io.Writer, java.util.Map)
     */
    public void transform(Reader xmlReader, String uri, Writer writer, Map<String, Object> params) throws XSLTTransformationException {
        // construct a file object
        File stylesheetFile = new File(uri);

        // we're going to make sure we got a file.
        if (stylesheetFile == null || !stylesheetFile.exists() || !stylesheetFile.isFile()) {
            log4j.warn("Cannot locate stylesheet file [" + uri + "]");
        } else {
            Properties properties = System.getProperties();
            // if there is already a transformer factory system property,
            // save it to restore when this process is done
            String storeTransFactoryImpl = null;
            String newTransFactoryImpl = GenericTransformerConstants.XSLTC_COMPILED_TRANSFORMER_FACTORY_IMPL;
            if (properties.containsKey(GenericTransformerConstants.TRANSFORMER_FACTORY)) {
                storeTransFactoryImpl = properties.getProperty(GenericTransformerConstants.TRANSFORMER_FACTORY);
            }

            // set the transformer factory system property
            properties.put(GenericTransformerConstants.TRANSFORMER_FACTORY, newTransFactoryImpl);
            System.setProperties(properties);

            // create a new transformer factory instance
            TransformerFactory tFactory = TransformerFactory.newInstance();

            // set the transformer factory attributes
            tFactory.setAttribute(GenericTransformerConstants.FACTORY_ATTR_GENERATE_TRANSLET, Boolean.FALSE);
            tFactory.setAttribute(GenericTransformerConstants.FACTORY_ATTR_ENABLE_INLINING, Boolean.FALSE);

            // generate a new translet object
            Templates translet = null;

            try {
                translet = tFactory.newTemplates(new StreamSource(stylesheetFile));
            } catch (TransformerConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // call the transform method
            transform(xmlReader, translet, writer, params);

            // restore the transformer factory system property
            if (!GenericValidator.isBlankOrNull(storeTransFactoryImpl)) {
                properties.put(GenericTransformerConstants.TRANSFORMER_FACTORY, storeTransFactoryImpl);
            }
        }
    }

    /**
     * 
     * This method performs an XSLT transformation and streams the output to the provided writer.
     * 
     * @see com.elsevier.els.app.biz.transformer.ITransformer#transform(java.io.Reader, javax.xml.transform.Templates, java.io.Writer)
     */
    public void transform(Reader xmlReader, Templates templates, Writer writer) throws XSLTTransformationException {
        transform(xmlReader, templates, writer, null);
    }

    /**
     * This method performs an XSLT transformation and streams the output to the provided writer.
     * 
     * @see com.elsevier.els.app.biz.transformer.ITransformer#transform(java.io.Reader, javax.xml.transform.Templates, java.io.Writer, java.util.Map)
     */
    public void transform(Reader xmlReader, Templates templates, Writer writer, Map<String, Object> map) throws XSLTTransformationException {
        Transformer transformer = null;
        XMLReader pooledXMLreader = null;

        try {

            pooledXMLreader = c_readerPool.borrowXMLReader();
            pooledXMLreader.setEntityResolver(DEFAULT_ENTITY_RESOLVER);

            Source source = null;
            if (pooledXMLreader == null) {
                source = new StreamSource(xmlReader);
            } else {
                source = new SAXSource(pooledXMLreader, new InputSource(xmlReader));
            }
            // Constuct a Result Steam using a Response Print Writer
            StreamResult result = new StreamResult(writer);

            // Construct a new instance of a TransformerFactory
            transformer = templates.newTransformer();

            // populate the parameters
            if (map != null && !map.isEmpty()) {
                Iterator<String> iterator = map.keySet().iterator();

                while (iterator.hasNext()) {
                    String key = (String) iterator.next();

                    Object value = map.get(key);

                    if (value == null) {
                        value = "";
                    }

                    transformer.setParameter(key, value);
                }
            }

            // with the transformer transform the xml
            transformer.transform(source, result);
        } catch (TransformerConfigurationException ex) {
            throw new XSLTTransformationException(ex.getMessage(), ex);
        } catch (TransformerException ex) {
            throw new XSLTTransformationException(ex.getMessage(), ex);
        } catch (Throwable t) {
            throw new XSLTTransformationException(t.getMessage(), t);
        } finally {
            if (transformer != null) {
                transformer.clearParameters();
            }
            try {
                c_readerPool.returnXMLReader(pooledXMLreader);
            } catch (Exception e) {
                throw new XSLTTransformationException(e.getMessage(), e);
            }
        }
    }
}
