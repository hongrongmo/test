package org.ei.util.test;

import org.ei.connectionpool.*;

import java.io.StringWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ei.domain.DatabaseConfig;
import org.ei.util.*;

public class TestSession
{
    public TestSession()
    {
    }
    
    public void run()
        throws Exception
    {
        SessionProcedure sessproc = new SessionProcedure();
        
        sessproc.createSession();
        sessproc.touchSession();
        sessproc.updateSession();
      /*  
        SessionBroker_updateSession1
        */
    }        
   
}
