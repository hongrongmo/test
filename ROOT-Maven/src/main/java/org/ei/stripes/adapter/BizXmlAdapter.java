package org.ei.stripes.adapter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import net.sourceforge.stripes.action.ActionBean;

import org.apache.log4j.Logger;
import org.ei.exception.EVBaseException;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;

public abstract class BizXmlAdapter implements IBizXmlAdapter {
	private final static Logger log4j = Logger.getLogger(BizXmlAdapter.class);

    public final static String xml10_illegal_xml_pattern = "[^"
        + "\u0009\r\n"
        + "\u0020-\uD7FF"
        + "\uE000-\uFFFD"
        + "\ud800\udc00-\udbff\udfff"
        + "]";

    /**
     * Convert incoming XML input stream to String.  This will remove any illegal XML characters from
     * input stream before returning.
     *
     * @param instream
     * @return
     * @throws InfrastructureException
     */
    protected InputStream sanitizeInputStream(InputStream instream) throws InfrastructureException {

        InputStream valid= null;

        Scanner scanner = new Scanner(instream, "UTF-8");
        String modelxml = scanner.useDelimiter("\\A").next();
        modelxml = modelxml.replaceAll(xml10_illegal_xml_pattern, "");
        scanner.close();

        try {
            valid = new ByteArrayInputStream(modelxml.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new InfrastructureException(SystemErrorCodes.SEARCH_QUERY_EXECUTION_FAILED, "Unable to validate InputStream!");
        }

        return valid;
    }

	/**
	 * NOOP implementation of the processXml method.  Child classes should
	 * do this work, not this base class!
	 * @throws InfrastructureException
	 * @throws EVBaseException
	 */
	public void processXml(ActionBean actionbean, InputStream instream, String stylesheet) throws InfrastructureException  {
		// No operation here!  This is just the base class, this should
		// only occur in child classes!
		return;
	}

}
