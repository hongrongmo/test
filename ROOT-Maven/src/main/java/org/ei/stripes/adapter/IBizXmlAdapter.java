package org.ei.stripes.adapter;

import java.io.InputStream;

import net.sourceforge.stripes.action.ActionBean;

import org.ei.exception.InfrastructureException;

/**
 * This interface is used to mark Adapter classes that translate the model layer XML into view objects
 * 
 * @author harovetm
 * 
 */
public interface IBizXmlAdapter {
    public void processXml(ActionBean actionbean, InputStream instream, String stylesheet) throws InfrastructureException;
}
