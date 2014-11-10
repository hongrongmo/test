package org.ei.stripes.adapter;

import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;

import net.sourceforge.stripes.action.Resolution;

import org.ei.exception.EVBaseException;
import org.ei.exception.ErrorXml;
import org.ei.exception.InfrastructureException;

/**
 * This interface marks an ActionBean as requiring XML from the model (biz) layer.
 * 
 * @author harovetm
 *
 */
public interface IBizBean {

	/**
	 * CIDs have been removed from incoming URLs but the engvillage layer
	 * may still need them.  IBizBean implementors should supply!
	 * @return
	 */
	public String getCID();
	
	/**
	 * Process the model-layer XML
	 * @return
	 * @throws ServletException 
	 * @throws EVBaseException 
	 */
	public void processModelXml(InputStream instream) throws InfrastructureException;
	
	/**
	 * Returns the path the the stylesheet for the transform.
	 * @return
	 */
	public String getXSLPath();
	
	/**
	 * Returns the path for retrieving the XML to be transformed
	 * @return
	 */
	public String getXMLPath();
	
	/**
	 * Get/Set comments from biz layer
	 */
    public List<String> getComments();
    public void setComments(List<String> comments);

	public Resolution handleException(ErrorXml errorXml);

}
