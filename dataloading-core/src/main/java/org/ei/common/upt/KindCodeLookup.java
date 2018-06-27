/*
 * Created on Jan 31, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.common.upt;

import java.util.Hashtable;

/**
 * @author KFokuo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class KindCodeLookup
{

    private static Hashtable htEpCodes = new Hashtable();
    private static Hashtable htUsCodes = new Hashtable();


    static
    {

        htEpCodes.put("A1", "Application with search report");
        htEpCodes.put("A2", "Application without search report");
        htEpCodes.put("A3", "Publ. of search report");
        htEpCodes.put("A4", "Supplementary search report");
        htEpCodes.put("A8", "Corrected title page of an EP-A Document");
        htEpCodes.put("A9", "Complete reprint of an EP-A Document");
        htEpCodes.put("B1", "Patent");
        htEpCodes.put("B2", "Patent after modification");
        htEpCodes.put("B8", "Corrected front page of an EP-B Document");
        htEpCodes.put("B9", "Complete reprint of an EP-B Document");

        htUsCodes.put("A", "Utility Patent Grant");
        htUsCodes.put("A1", "Utility Patent Application");
        htUsCodes.put("A2", "Second or subsequent publication of a Utility Patent Application");
        htUsCodes.put("A9", "Correction published Utility Patent Application");
        htUsCodes.put("B1", "Utility Patent Grant (no pre-grant publication");
        htUsCodes.put("B2", "Utility Patent Grant (with pre-grant publication");
        htUsCodes.put("S", "Design Patent");
        htUsCodes.put("S1", "Design Patent");
        htUsCodes.put("H", "Statutory Invention Registration (S.I.R.)");
        htUsCodes.put("H1", "Statutory Invention Registration (S.I.R.)");
        htUsCodes.put("P", "Plant Patent Grant");
		htUsCodes.put("P1", "Plant Patent Application");
        htUsCodes.put("P2", "Plant Patent (no previous Pre-Grant Publication)");
        htUsCodes.put("P3", "Plant Patent (previous Pre-Grant Publication)");
        htUsCodes.put("E", "Reissue Patent");
        htUsCodes.put("E1", "Reissue (pre-grant)");

    }


    public static String getTitle(String authCode, String kind)
    {
        String title = null;

        if (authCode != null &&
        	kind != null)
        {
            if (authCode.equalsIgnoreCase("us"))
            {
                title = (String) htUsCodes.get(kind);
			}
            else if (authCode.equalsIgnoreCase("ep"))
            {
                title = (String) htEpCodes.get(kind);
		 	}
        }

        return title;
    }
}
