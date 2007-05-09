package org.ei.xml;

import java.util.Hashtable;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;


public final class TransformerBroker
{
    private static TransformerBroker broker;
    private Hashtable cache;
    private long beginTime;
    private long refreshInterval = 20000000;

    private TransformerBroker()
    {
        this.cache = new Hashtable();
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




    public Transformer getTransformer(String displayURL)
        throws Exception
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
        }

        return templates.newTransformer();
    }
}
