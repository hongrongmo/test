package org.ei.stripes.adapter;

import java.io.InputStream;

import net.sourceforge.stripes.action.ActionBean;

import org.apache.log4j.Logger;
import org.ei.exception.EVBaseException;
import org.ei.exception.InfrastructureException;

public abstract class BizXmlAdapter implements IBizXmlAdapter {
	private final static Logger log4j = Logger.getLogger(BizXmlAdapter.class);
	
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
