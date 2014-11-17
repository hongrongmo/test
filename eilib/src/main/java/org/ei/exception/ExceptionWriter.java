package org.ei.exception;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;

import com.amazonaws.util.StringInputStream;

public class ExceptionWriter {
	private static final Logger log4j = Logger.getLogger(ExceptionWriter.class);

	/**
	 * Unmarshall string to ErrorXml object
	 * @param errorXml
	 * @return
	 */
	public static ErrorXml toObject(String errorXml) {
		ErrorXml fail = new ErrorXml();
		fail.setErrorCode(SystemErrorCodes.ERROR_XML_CONVERSION_FAILED);
		fail.setErrorMessage("Error XML string is empty!");

		if (GenericValidator.isBlankOrNull(errorXml)) {
			return fail;
		}
		try {
			return ExceptionWriter.toObject(new StringInputStream(errorXml));
		} catch (UnsupportedEncodingException e) {
			fail.setErrorMessage("Unable to convert String to errorXml!");
			return fail;
		}
	}

	/**
	 * Unmarshall input stream to ErrorXml object
	 * @param errorXml
	 * @return
	 */
	public static ErrorXml toObject(InputStream errorXml) {
		// Create new ErrorXml object with defaults
		ErrorXml errorXmlObject = new ErrorXml();

		// Now try to convert incoming stream to ErrorXml
		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(ErrorXml.class);
			Unmarshaller jaxbUnMarshaller = jaxbContext.createUnmarshaller();
			errorXmlObject = (ErrorXml) jaxbUnMarshaller.unmarshal(errorXml);

			return errorXmlObject;

		} catch (JAXBException e) {
			log4j.error("Unable to Unmarshall to ErrorXml object!", e);
			errorXmlObject.setErrorCode(SystemErrorCodes.ERROR_XML_CONVERSION_FAILED);
			errorXmlObject.setErrorMessage(e.getMessage());
		}

		return errorXmlObject;

	}

	public static String toXml(EVBaseException e) {
		// Output exception in xml format
		ErrorXml errorxml = new ErrorXml(e);
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(ErrorXml.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			Writer out = new StringWriter();
			jaxbMarshaller.marshal(errorxml, out);
			return out.toString();
		} catch (JAXBException jaxbexc) {
			log4j.error("Unable to marshal XML from ErrorXml object!",jaxbexc);
			return "";
		}
	}

    public static String toXml(ErrorXml errorxml) {
        // Output exception in xml format
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(ErrorXml.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            Writer out = new StringWriter();
            jaxbMarshaller.marshal(errorxml, out);
            return out.toString();
        } catch (JAXBException jaxbexc) {
            log4j.error("Unable to marshal XML from ErrorXml object!",jaxbexc);
            return "";
        }
    }
}
