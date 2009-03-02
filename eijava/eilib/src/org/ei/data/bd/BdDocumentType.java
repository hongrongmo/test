/*
 * Created on Jun 16, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.data.bd;
import java.util.*;
import org.ei.data.bd.loadtime.*;
/**
 * @author solovyevat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BdDocumentType
{
    private static final Hashtable bdDocType = new Hashtable();

    static
    {  	bdDocType.put("ab","JA");
		bdDocType.put("ar","JA");
		bdDocType.put("bk","MC");
		bdDocType.put("br","MR");
		bdDocType.put("bz","JA");
		bdDocType.put("ch","MC");
		//bdDocType.put("cp","CA");
		bdDocType.put("cr","CP");
		bdDocType.put("di","DS");
		bdDocType.put("ed","JA");
		bdDocType.put("er","JA");
		bdDocType.put("ip","JA");
		bdDocType.put("le","JA");
		bdDocType.put("no","JA");
		bdDocType.put("pa","JA");
		bdDocType.put("pr","JA");
		bdDocType.put("re","JA");
		bdDocType.put("rp","RR");
		bdDocType.put("sh","JA");
    	bdDocType.put("wp","JA");
    	bdDocType.put("ja","JA");
    	bdDocType.put("pa","PA");
	}

    public static String getDocType(String doctype,
    								boolean confCode)
    {
        String dt = doctype.toLowerCase().trim();
        if(dt.equals("cp"))
        {
			if(!confCode)
			{
				return "JA";
			}
			else
			{
				return "CA";
			}
		}
		else
		{
			return (String) bdDocType.get(dt);
		}
    }
}
