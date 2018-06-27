package org.ei.data.upt.runtime;

import java.util.*;
import java.io.*;
import org.ei.domain.*;

public class RefPage
{
	private List builtDocs;
	private int offset;

	private String sessionID;

	public RefPage(List builtDocs, int offset)
	{
		this.builtDocs = builtDocs;
		this.offset = offset;
	}

	public void toXML(Writer out)
		throws IOException
	{
		int o = offset;
		out.write("<REF-DOCS OFFSET=\"" + offset + "\">");
		for(int i=0; i<builtDocs.size(); i++)
		{
			PageEntry doc = (PageEntry)builtDocs.get(i);
			(doc.getDoc()).getDocID().setHitIndex(++o);
			doc.toXML(out);
		}
		out.write("</REF-DOCS>");
	}
}