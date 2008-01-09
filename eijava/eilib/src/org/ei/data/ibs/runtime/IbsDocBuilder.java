package org.ei.data.ibs.runtime;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.io.Writer;

import org.ei.connectionpool.*;
import org.ei.domain.*;
import org.ei.domain.ElementDataMap;
import org.ei.domain.XMLWrapper;
import org.ei.domain.XMLMultiWrapper;
import org.ei.util.StringUtil;
import org.ei.data.*;
import org.ei.data.insback.runtime.*;
import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;

import java.text.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.IOException;
import org.ei.config.*;
import org.ei.util.StringUtil;


public class IbsDocBuilder
    extends InsBackDocBuilder
{
    private Database database;

    public DocumentBuilder newInstance(Database database)
    {
        return new IbsDocBuilder(database);
    }

    public IbsDocBuilder()
    {
    }

    public IbsDocBuilder(Database database)
    {
        this.database = database;
    }

    protected String buildINString(List listOfDocIDs)
    {
        StringBuffer sQuery=new StringBuffer();
        sQuery.append("(");
        for(int k=listOfDocIDs.size();k>0;k--)
        {
            DocID doc = (DocID)listOfDocIDs.get(k-1);
            String docID = "ibf_".concat((doc.getDocID().substring(4)));
			if((k-1)==0)
			{
				sQuery.append("'"+docID+"'");
			}
			else
			{
				sQuery.append("'"+docID+"'").append(",");
			}
         }
         sQuery.append(")");
         return sQuery.toString();
    }

    protected Hashtable getDocIDTable(List listOfDocIDs)
    {
        Hashtable h = new Hashtable();

        for(int i=0; i<listOfDocIDs.size(); ++i)
        {
            DocID d = (DocID)listOfDocIDs.get(i);
            String docID = "ibf_".concat((d.getDocID().substring(4)));
            h.put(docID, d);
        }

        return h;
	}
}





