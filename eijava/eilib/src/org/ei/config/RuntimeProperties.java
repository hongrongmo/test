// All rights reserved.
//
// This software is the confidential and proprietary information

///////////////////////////////////////////////////////////////////////////////
// Revision History Information for this file.
// $Header:   P:/VM/ei-dev/archives/baja/eijava/eilib/src/org/ei/config/RuntimeProperties.java-arc   1.0   May 09 2007 15:33:12   johna  $
// $Project: EI$
// $Revision:   1.0  $
// $Locker:  $
// $Log:
//
///////////////////////////////////////////////////////////////////////////////

package org.ei.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

/**
 * This class encapsulates the properties class.
 * @author Laxman
 **/
public final class RuntimeProperties extends Properties
{



    	

    	public RuntimeProperties(String propsFile)
    		throws IOException
	{
		loadProperties(new BufferedInputStream(new FileInputStream(propsFile)));
	}

	
    /**
     * loads the properties file from the passed inputStream.
     * @exception IOException If an error occurred when reading from the input stream.
     **/
    public void loadProperties(InputStream inStream) throws IOException {
        try {
           load(new BufferedInputStream(inStream));
        } catch(IOException e) {
            throw new IOException("<"+this.getClass().getName()+".loadProperties> "+
                                  "IOException "+e.getMessage()
                                  +"\nError Opening properties from "+inStream);
        }
    }

    /**
     * Prints this property list out to the specified output stream.
     * This method is useful for debugging.
     *
     * @param   out   an output stream.
     */
    public void list(PrintStream out) {
        for (Enumeration e = propertyNames() ; e.hasMoreElements() ;) {
            String key = (String)e.nextElement();
            String val = getProperty(key);
            out.println(key + "=" + val);
            out.println("");
        }
    }

    /**
     * Prints this property list out to the specified output stream.
     * This method is useful for debugging.
     *
     * @param   out   an output stream.
     */
    public void list(PrintWriter out) {
        for (Enumeration e = propertyNames() ; e.hasMoreElements() ;) {
            String key = (String)e.nextElement();
            String val = getProperty(key);
            out.println(key + "=" + val);
            out.println("");
        }
    }

   /**
     * Calls the hashtable method <code>put</code>. Provided for
     * parallelism with the getProperties method. Enforces use of
     * strings for property keys and values.
     * This is a duplicate of the jdk1.2.2 implementaion. It is put in for
     * compatibility with jdk1.x
     */
    public synchronized Object setProperty(String key, String value) {
        return put(key, value);
    }

}


// END OF FILE : EIProperties.java

