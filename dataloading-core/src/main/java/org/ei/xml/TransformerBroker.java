package org.ei.xml;

/**
 * HH commented out on Fri 10/04/2019 due to compilation error depends on adding maven dependency for saxon which cause other issue with dup import
for classes as java.xml.sax, checked and found this class not in use by any of fataloading-core classes
*/


import java.util.Hashtable;
/*
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;


public final class TransformerBroker
{
    private static TransformerBroker broker;
    private Hashtable<String,Templates> cache;
    private long beginTime;
    private long refreshInterval = 20000000;

    private TransformerBroker()
    {
        this.cache = new Hashtable<String,Templates>();
        beginTime = System.currentTimeMillis();
    }


    public static TransformerBroker getInstance()
    {

        if(broker == null)
        {
            broker = new TransformerBroker();
        }


        return broker;
    }

    // 
    // TMH - added caching control var for development...
    //
    private boolean docaching = true;
    public void setCache(boolean docaching) {
    	this.docaching = docaching;
    }

    public Transformer getTransformer(String displayURL) throws TransformerConfigurationException
        
    {
        Templates templates = null;
        long cur = System.currentTimeMillis();

        synchronized(this)
        {
            if((cur - beginTime) > refreshInterval)
            {
              cache.clear();
              this.beginTime = cur;
            }

            if (docaching) {
	            if(!cache.containsKey(displayURL))
	            {
	                TransformerFactory tFactory = new com.icl.saxon.TransformerFactoryImpl();
	                templates = tFactory.newTemplates(new StreamSource(displayURL));
	                cache.put(displayURL, templates);
	            }
	            else
	            {
	                templates = (Templates)cache.get(displayURL);
	            }
            } else {
            	// TMH just return the stylesheet when caching off...
                TransformerFactory tFactory = new com.icl.saxon.TransformerFactoryImpl();
                templates = tFactory.newTemplates(new StreamSource(displayURL));
            }
        }

        return templates.newTransformer();
    }
}

*/

