package org.ei.stripes.adapter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sourceforge.stripes.action.ActionBean;

import org.apache.log4j.Logger;
import org.ei.stripes.action.search.SearchDisplayAction;
import org.ei.xml.TransformerBroker;

public class QuickSearchAdapter extends BizXmlAdapter {
	private final static Logger log4j = Logger.getLogger(QuickSearchAdapter.class);

	/**
	 * Process XML from the model layer
	 */
	public void processXml(ActionBean actionbean, InputStream instream, String stylesheet) {
		SearchDisplayAction sdactionbean = (SearchDisplayAction)actionbean;
		
        Transformer transformer = null;
		try {
			//
		    // TRANSFORM!  Use stylesheet to transform XML to bean (ThesaurusSearchAction)
		    //
			TransformerBroker broker = TransformerBroker.getInstance();
			// For testing, grab the XML (viewable) when debug or info mode
			// and show output from transform
			// Also set stylesheet caching to OFF
			OutputStream transformout = new NullOutputStream();
			if (log4j.isDebugEnabled() || log4j.isInfoEnabled()) {
				log4j.info("Transforming in debug/info");
				String modelxml = new Scanner(instream).useDelimiter("\\A").next();
				instream = new ByteArrayInputStream(modelxml.getBytes());
				broker.setCache(false); 
			}
			transformer = broker.getTransformer(stylesheet);
			transformer.setParameter("actionbean", sdactionbean);
			transformer.transform(
					new StreamSource(instream), 
					new StreamResult(transformout));

		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
